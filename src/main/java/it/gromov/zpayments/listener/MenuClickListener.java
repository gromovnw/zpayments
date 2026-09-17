package it.gromov.zpayments.listener;

import it.gromov.zpayments.menu.MenuHolder;
import it.gromov.zpayments.service.CartService;
import it.gromov.zpayments.service.ConfigService;
import it.gromov.zpayments.service.MessageService;
import it.gromov.zpayments.storage.entity.CartEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public final class MenuClickListener implements Listener {

    private final ConfigService configService;
    private final MessageService messageService;
    private final CartService cartService;

    public MenuClickListener(ConfigService configService, MessageService messageService, CartService cartService) {
        this.configService = configService;
        this.messageService = messageService;
        this.cartService = cartService;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof MenuHolder)) {
            return;
        }
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        MenuHolder menuHolder = (MenuHolder) holder;
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getInventory().getSize()) {
            return;
        }

        if (menuHolder.isClaimAllSlot(slot)) {
            int claimed = cartService.claimAll(player);
            player.closeInventory();
            if (claimed > 0) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("count", String.valueOf(claimed));
                messageService.send(player, configService.getMessages().getCartAllClaimed(), placeholders);
            }
            return;
        }

        CartEntry entry = menuHolder.getEntry(slot);
        if (entry == null) {
            return;
        }

        boolean claimed = cartService.claimOne(player, entry);
        player.closeInventory();
        if (claimed) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("title", entry.getItemTitle());
            placeholders.put("count", String.valueOf(entry.getItemCount()));
            messageService.send(player, configService.getMessages().getCartItemClaimed(), placeholders);
        } else {
            messageService.send(player, configService.getMessages().getCartClaimError());
        }
    }
}
