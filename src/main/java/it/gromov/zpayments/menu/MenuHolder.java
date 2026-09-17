package it.gromov.zpayments.menu;

import it.gromov.zpayments.storage.entity.CartEntry;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public final class MenuHolder implements InventoryHolder {

    private final Map<Integer, CartEntry> slotEntries = new HashMap<>();
    private Inventory inventory;
    private int claimAllSlot = -1;

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void putEntry(int slot, CartEntry entry) {
        slotEntries.put(slot, entry);
    }

    public CartEntry getEntry(int slot) {
        return slotEntries.get(slot);
    }

    public void setClaimAllSlot(int slot) {
        this.claimAllSlot = slot;
    }

    public boolean isClaimAllSlot(int slot) {
        return claimAllSlot >= 0 && claimAllSlot == slot;
    }
}
