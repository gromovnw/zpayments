package it.gromov.zpayments.service.impl;

import it.gromov.zpayments.api.event.PurchaseFailedEvent;
import it.gromov.zpayments.api.event.PurchaseFulfilledEvent;
import it.gromov.zpayments.api.event.PurchaseQueuedEvent;
import it.gromov.zpayments.http.ShopApiClient;
import it.gromov.zpayments.model.PurchaseItem;
import it.gromov.zpayments.model.PurchaseTask;
import it.gromov.zpayments.service.CartService;
import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.MessageService;
import it.gromov.zpayments.service.StorageService;
import it.gromov.zpayments.storage.entity.CartEntry;
import it.gromov.zpayments.util.ConsoleCommandExecutor;
import it.gromov.zpayments.util.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class CartServiceImpl implements CartService {

    private final Plugin plugin;
    private final ConfigService configService;
    private final StorageService storageService;
    private final MessageService messageService;
    private final ShopApiClient shopApiClient;

    private final Set<String> inFlight = ConcurrentHashMap.newKeySet();

    public CartServiceImpl(Plugin plugin, ConfigService configService, StorageService storageService,
                            MessageService messageService, ShopApiClient shopApiClient) {
        this.plugin = plugin;
        this.configService = configService;
        this.storageService = storageService;
        this.messageService = messageService;
        this.shopApiClient = shopApiClient;
    }

    @Override
    public void enable() {
    }

    @Override
    public void reload() {
    }

    @Override
    public void disable() {
        inFlight.clear();
    }

    @Override
    public void process(PurchaseTask task) {
        if (storageService.hasEntry(task.getOrderId(), task.getKey())) {
            return;
        }
        String flightKey = flightKey(task.getOrderId(), task.getKey());
        if (inFlight.contains(flightKey)) {
            return;
        }

        boolean online = Bukkit.getPlayerExact(task.getNickname()) != null;
        boolean cartEnabled = configService.getConfig().getCart().isEnabled();
        boolean onlyForOffline = configService.getConfig().getCart().isOnlyForOffline();
        boolean shouldQueue = cartEnabled && (!onlyForOffline || !online);

        if (shouldQueue) {
            // process() вызывается из pollNow() — тот крутится на async-таске
            // (см. ShopApiServiceImpl.enable): callEvent() Paper разрешает
            // строго из основного потока, иначе IllegalStateException и
            // покупка вообще не сохраняется/не ставится в очередь. Запись в
            // storageService — блокирующий I/O (MySQL/файл), поэтому её
            // оставляем как есть на async, а на main переносим только сам
            // ивент.
            storageService.addEntry(toEntity(task));
            plugin.getLogger().info("Заказ " + task.getOrderId() + " поставлен в очередь для " + task.getNickname() + " — выполнится при заходе на сервер");
            SchedulerUtil.runOnMain(plugin, () ->
                    Bukkit.getPluginManager().callEvent(new PurchaseQueuedEvent(task.getOrderId(), task.getNickname(), task.getKey())));
            return;
        }

        executeAndAcknowledge(task);
    }

    private void executeAndAcknowledge(PurchaseTask task) {
        String flightKey = flightKey(task.getOrderId(), task.getKey());
        if (!inFlight.add(flightKey)) {
            return;
        }
        SchedulerUtil.runOnMain(plugin, () -> {
            String output = ConsoleCommandExecutor.execute(task.getCommand());
            Bukkit.getPluginManager().callEvent(new PurchaseFulfilledEvent(task.getOrderId(), task.getNickname(), task.getKey(), task.getCommand(), output));
            SchedulerUtil.runAsync(plugin, () -> {
                try {
                    shopApiClient.acknowledge(task.getOrderId(), task.getKey(), true, output);
                } catch (Exception exception) {
                    plugin.getLogger().warning("Не удалось подтвердить выполнение покупки " + task.getOrderId() + ": " + exception.getMessage());
                    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager()
                            .callEvent(new PurchaseFailedEvent(task.getOrderId(), task.getNickname(), task.getKey(), exception.getMessage())));
                } finally {
                    inFlight.remove(flightKey);
                }
            });
        });
    }

    @Override
    public List<CartEntry> getCart(String nickname) {
        return storageService.findByNickname(nickname);
    }

    @Override
    public boolean isCartEmpty(String nickname) {
        return getCart(nickname).isEmpty();
    }

    @Override
    public void claimOnJoin(Player player) {
        if (!configService.getConfig().getCart().isClaimOnJoin()) {
            return;
        }
        List<CartEntry> entries = getCart(player.getName());
        if (entries.isEmpty()) {
            return;
        }
        int claimed = claimEntries(player, entries);
        if (claimed > 0) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("count", String.valueOf(claimed));
            messageService.send(player, configService.getMessages().getJoinCartClaimed(), placeholders);
        }
    }

    @Override
    public int claimAll(Player player) {
        return claimEntries(player, getCart(player.getName()));
    }

    @Override
    public boolean claimOne(Player player, CartEntry entry) {
        return claimEntries(player, Collections.singletonList(entry)) > 0;
    }

    private int claimEntries(Player player, List<CartEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return 0;
        }
        List<CartEntry> snapshot = new ArrayList<>(entries);
        SchedulerUtil.runOnMain(plugin, () -> {
            for (CartEntry entry : snapshot) {
                String output = ConsoleCommandExecutor.execute(entry.getCommand());
                storageService.removeEntry(entry);
                Bukkit.getPluginManager().callEvent(new PurchaseFulfilledEvent(entry.getOrderId(), entry.getNickname(), entry.getTaskKey(), entry.getCommand(), output));
                SchedulerUtil.runAsync(plugin, () -> {
                    try {
                        shopApiClient.acknowledge(entry.getOrderId(), entry.getTaskKey(), true, output);
                    } catch (Exception exception) {
                        plugin.getLogger().warning("Не удалось подтвердить выдачу из корзины (" + entry.getOrderId() + "): " + exception.getMessage());
                    }
                });
            }
        });
        return snapshot.size();
    }

    private CartEntry toEntity(PurchaseTask task) {
        String title = task.getItems().isEmpty() ? task.getCommand() : task.getItems().get(0).getTitle();
        int count = task.getItems().isEmpty() ? 1 : task.getItems().get(0).getCount();
        double price = task.getItems().isEmpty() ? task.getTotal() : task.getItems().get(0).getPrice();
        return new CartEntry(0, task.getNickname(), task.getOrderId(), task.getKey(), task.getCommand(),
                title, count, price, task.getTotal(), System.currentTimeMillis());
    }

    private String flightKey(String orderId, String taskKey) {
        return orderId + ":" + taskKey;
    }
}
