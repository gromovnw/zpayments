package it.gromov.zpayments.service.impl;

import it.gromov.zpayments.menu.CartMenu;
import it.gromov.zpayments.menu.MenuHolder;
import it.gromov.zpayments.service.CartService;
import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.MenuService;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class MenuServiceImpl implements MenuService {

    private final ConfigService configService;
    private final CartService cartService;
    private final Map<UUID, Long> lastOpened = new ConcurrentHashMap<>();

    public MenuServiceImpl(ConfigService configService, CartService cartService) {
        this.configService = configService;
        this.cartService = cartService;
    }

    @Override
    public void enable() {
    }

    @Override
    public void reload() {
        lastOpened.clear();
    }

    @Override
    public void disable() {
        lastOpened.clear();
    }

    @Override
    public boolean openCart(Player player) {
        if (isOnCooldown(player)) {
            return false;
        }
        MenuHolder holder = CartMenu.build(configService.getMenu(), cartService.getCart(player.getName()));
        player.openInventory(holder.getInventory());
        if (!player.hasPermission("zpayments.bypass.cooldown")) {
            lastOpened.put(player.getUniqueId(), System.currentTimeMillis());
        }
        return true;
    }

    @Override
    public boolean isOnCooldown(Player player) {
        return getRemainingCooldownSeconds(player) > 0;
    }

    @Override
    public long getRemainingCooldownSeconds(Player player) {
        if (player.hasPermission("zpayments.bypass.cooldown")) {
            return 0;
        }
        Long last = lastOpened.get(player.getUniqueId());
        if (last == null) {
            return 0;
        }
        long elapsedSeconds = (System.currentTimeMillis() - last) / 1000;
        long remaining = configService.getConfig().getCart().getOpenCooldownSeconds() - elapsedSeconds;
        return Math.max(0, remaining);
    }
}
