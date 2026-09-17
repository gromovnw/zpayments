package it.gromov.zpayments.service;

import org.bukkit.entity.Player;

public interface MenuService extends Service {

    boolean openCart(Player player);

    boolean isOnCooldown(Player player);

    long getRemainingCooldownSeconds(Player player);
}
