package it.gromov.zpayments.menu;

import it.gromov.zpayments.config.MenuConfig;
import it.gromov.zpayments.storage.entity.CartEntry;
import it.gromov.zpayments.util.ColorUtil;
import it.gromov.zpayments.util.MaterialUtil;
import it.gromov.zpayments.util.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class CartMenu {

    private CartMenu() {
    }

    public static MenuHolder build(MenuConfig config, List<CartEntry> entries) {
        MenuHolder holder = new MenuHolder();
        Inventory inventory = Bukkit.createInventory(holder, config.getSafeSize(), ColorUtil.colorize(config.getTitle()));
        holder.setInventory(inventory);

        if (config.isFillerEnabled()) {
            ItemStack filler = buildItem(MaterialUtil.resolve(config.getFillerMaterial(), Material.STONE), config.getFillerName(), null, 1);
            for (int slot = 0; slot < inventory.getSize(); slot++) {
                inventory.setItem(slot, filler);
            }
        }

        int claimAllSlot = config.getClaimAllSlot();
        boolean claimAllVisible = config.isClaimAllEnabled() && claimAllSlot >= 0 && claimAllSlot < inventory.getSize();

        if (entries.isEmpty()) {
            ItemStack empty = buildItem(MaterialUtil.resolve(config.getEmptyItemMaterial(), Material.BARRIER), config.getEmptyItemName(), null, 1);
            inventory.setItem(inventory.getSize() / 2, empty);
        } else {
            int slot = 0;
            for (CartEntry entry : entries) {
                if (claimAllVisible && slot == claimAllSlot) {
                    slot++;
                }
                if (slot >= inventory.getSize()) {
                    break;
                }

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("title", entry.getItemTitle());
                placeholders.put("count", String.valueOf(entry.getItemCount()));
                placeholders.put("order-id", entry.getOrderId());
                placeholders.put("price", String.valueOf(entry.getItemPrice()));

                ItemStack item = buildItem(
                        MaterialUtil.resolve(config.getItemMaterial(), Material.CHEST),
                        PlaceholderUtil.apply(config.getItemNameFormat(), placeholders),
                        PlaceholderUtil.apply(config.getItemLoreFormat(), placeholders),
                        Math.max(1, Math.min(64, entry.getItemCount()))
                );

                inventory.setItem(slot, item);
                holder.putEntry(slot, entry);
                slot++;
            }
        }

        if (claimAllVisible) {
            ItemStack claimAll = buildItem(MaterialUtil.resolve(config.getClaimAllMaterial(), Material.EMERALD_BLOCK), config.getClaimAllName(), null, 1);
            inventory.setItem(claimAllSlot, claimAll);
            holder.setClaimAllSlot(claimAllSlot);
        }

        return holder;
    }

    private static ItemStack buildItem(Material material, String name, List<String> lore, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtil.colorize(name));
            if (lore != null) {
                meta.setLore(lore.stream().map(ColorUtil::colorize).collect(Collectors.toList()));
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
