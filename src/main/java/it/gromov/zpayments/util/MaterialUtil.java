package it.gromov.zpayments.util;

import org.bukkit.Material;

public final class MaterialUtil {

    private MaterialUtil() {
    }

    public static Material resolve(String name, Material fallback) {
        if (name == null || name.trim().isEmpty()) {
            return fallback;
        }
        Material material = Material.matchMaterial(name.trim());
        return material != null ? material : fallback;
    }

    public static Material resolve(String name) {
        return resolve(name, Material.STONE);
    }
}
