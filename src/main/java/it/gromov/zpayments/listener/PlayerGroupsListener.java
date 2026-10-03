package it.gromov.zpayments.listener;

import it.gromov.zpayments.api.event.PurchaseFulfilledEvent;
import it.gromov.zpayments.http.ShopApiClient;
import it.gromov.zpayments.util.LuckPermsGroups;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.logging.Level;

// Шлёт на zDonate группы LuckPerms игрока: при входе и после выдачи покупки
// (LuckPerms уже сменил группу). Нужно только для доплаты «Умная».
// Ошибки не мешают ни входу, ни выдаче — только лог на уровне FINE.
public final class PlayerGroupsListener implements Listener {

    private static final long JOIN_DELAY_TICKS = 40L;
    private static final long AFTER_PURCHASE_DELAY_TICKS = 20L;

    private final Plugin plugin;
    private final ShopApiClient shopApiClient;
    private final LuckPermsGroups luckPerms;

    public PlayerGroupsListener(Plugin plugin, ShopApiClient shopApiClient) {
        this.plugin = plugin;
        this.shopApiClient = shopApiClient;
        this.luckPerms = new LuckPermsGroups(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> report(player.getName(), player.getUniqueId()), JOIN_DELAY_TICKS);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFulfilled(PurchaseFulfilledEvent event) {
        String nickname = event.getNickname();
        Player online = Bukkit.getPlayerExact(nickname);
        UUID uuid = online != null ? online.getUniqueId() : Bukkit.getOfflinePlayer(nickname).getUniqueId();
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> report(nickname, uuid), AFTER_PURCHASE_DELAY_TICKS);
    }

    private void report(String nickname, UUID uuid) {
        try {
            if (!luckPerms.isAvailable()) {
                return;
            }
            shopApiClient.reportPlayerGroups(nickname, uuid.toString(), luckPerms.groupsOf(uuid));
        } catch (Exception exception) {
            plugin.getLogger().log(Level.FINE, "Не удалось отправить группы игрока " + nickname, exception);
        }
    }
}
