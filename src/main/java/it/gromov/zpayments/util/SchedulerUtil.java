package it.gromov.zpayments.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public final class SchedulerUtil {

    private SchedulerUtil() {
    }

    public static void runOnMain(Plugin plugin, Runnable task) {
        if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void runAsync(Plugin plugin, Runnable task) {
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        } else {
            task.run();
        }
    }
}
