package it.gromov.zpayments.service.impl;

import it.gromov.zpayments.http.ShopApiClient;
import it.gromov.zpayments.http.ShopApiException;
import it.gromov.zpayments.http.dto.PendingItemDto;
import it.gromov.zpayments.http.dto.PendingOrderDto;
import it.gromov.zpayments.http.dto.PendingPurchasesResponse;
import it.gromov.zpayments.http.dto.PendingTaskDto;
import it.gromov.zpayments.model.PurchaseItem;
import it.gromov.zpayments.model.PurchaseTask;
import it.gromov.zpayments.service.CartService;
import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.ShopApiService;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public final class ShopApiServiceImpl implements ShopApiService {

    private final Plugin plugin;
    private final ConfigService configService;
    private final CartService cartService;
    private final ShopApiClient client;

    private BukkitTask task;

    private final AtomicLong lastPollTimestamp = new AtomicLong(0);
    private final AtomicBoolean lastPollSuccessful = new AtomicBoolean(false);
    private final AtomicReference<String> lastErrorMessage = new AtomicReference<>("");
    // Последняя ошибка, которую реально напечатали в консоль — чтобы не
    // спамить одним и тем же WARNING каждый цикл опроса (напр. неверный
    // ключ/незаполненный shopId: ошибка не меняется, значит и повторный
    // лог не несёт новой информации). Печатаем заново только когда текст
    // ошибки меняется или когда опрос восстанавливается после сбоя.
    private final AtomicReference<String> lastLoggedError = new AtomicReference<>(null);

    public ShopApiServiceImpl(Plugin plugin, ConfigService configService, CartService cartService, ShopApiClient client) {
        this.plugin = plugin;
        this.configService = configService;
        this.cartService = cartService;
        this.client = client;
    }

    @Override
    public void enable() {
        int delaySeconds = configService.getConfig().getPolling().getSafeRequestDelaySeconds();
        long delayTicks = delaySeconds * 20L;
        task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::pollNow, delayTicks, delayTicks);
    }

    @Override
    public void reload() {
        disable();
        enable();
    }

    @Override
    public void disable() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void pollNow() {
        try {
            PendingPurchasesResponse response = client.fetchPending();
            if (response == null) {
                markResult(true, "");
                return;
            }
            for (PendingOrderDto order : response.getOrders()) {
                List<PurchaseItem> items = new ArrayList<>();
                for (PendingItemDto itemDto : order.getItems()) {
                    items.add(new PurchaseItem(itemDto.getTitle(), itemDto.getCount(), itemDto.getPrice()));
                }
                for (PendingTaskDto taskDto : order.getTasks()) {
                    PurchaseTask purchaseTask = new PurchaseTask(order.getOrderId(), order.getNickname(), order.getTotal(), items, taskDto.getKey(), taskDto.getCommand());
                    cartService.process(purchaseTask);
                }
            }
            markResult(true, "");
            logRecoveryIfNeeded();
        } catch (ShopApiException exception) {
            markResult(false, exception.getMessage());
            logErrorOnce("Ошибка ответа zDonate при опросе покупок: " + exception.getMessage(), null);
        } catch (Exception exception) {
            markResult(false, exception.getMessage());
            logErrorOnce("Не удалось опросить zDonate: " + exception.getMessage(), exception);
        }
    }

    // Печатает WARNING только если текст ошибки отличается от последнего
    // залогированного — иначе один и тот же сбой (напр. незаполненный
    // shopId/ключ) заливал бы консоль каждые несколько секунд бесконечно.
    private void logErrorOnce(String message, Exception exception) {
        if (message.equals(lastLoggedError.get())) {
            return;
        }
        lastLoggedError.set(message);
        if (exception != null) {
            plugin.getLogger().log(Level.WARNING, message, exception);
        } else {
            plugin.getLogger().log(Level.WARNING, message);
        }
    }

    private void logRecoveryIfNeeded() {
        if (lastLoggedError.getAndSet(null) != null) {
            plugin.getLogger().log(Level.INFO, "Связь с zDonate восстановлена.");
        }
    }

    private void markResult(boolean success, String error) {
        lastPollTimestamp.set(System.currentTimeMillis());
        lastPollSuccessful.set(success);
        lastErrorMessage.set(error == null ? "" : error);
    }

    @Override
    public long getLastPollTimestamp() {
        return lastPollTimestamp.get();
    }

    @Override
    public boolean isLastPollSuccessful() {
        return lastPollSuccessful.get();
    }

    @Override
    public String getLastErrorMessage() {
        return lastErrorMessage.get();
    }

    @Override
    public boolean testConnection() {
        try {
            client.fetchPending();
            return true;
        } catch (Exception exception) {
            lastErrorMessage.set(exception.getMessage());
            return false;
        }
    }
}
