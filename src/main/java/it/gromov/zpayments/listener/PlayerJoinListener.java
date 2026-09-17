package it.gromov.zpayments.listener;

import it.gromov.zpayments.service.CartService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public final class PlayerJoinListener implements Listener {

    private final Plugin plugin;
    private final CartService cartService;

    public PlayerJoinListener(Plugin plugin, CartService cartService) {
        this.plugin = plugin;
        this.cartService = cartService;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> cartService.claimOnJoin(event.getPlayer()), 20L);
    }
}
