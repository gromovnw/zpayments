package it.gromov.zpayments.util;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

public final class CommandMapUtil {

    private static CommandMap commandMap;
    private static boolean resolutionAttempted;

    private CommandMapUtil() {
    }

    public static boolean applyAliases(Command command, List<String> aliases, String fallbackPrefix, Logger logger) {
        CommandMap map = commandMap(logger);
        if (map == null || command == null) {
            return false;
        }
        command.setAliases(aliases == null ? java.util.Collections.emptyList() : aliases);
        map.register(fallbackPrefix, command);
        return true;
    }

    private static CommandMap commandMap(Logger logger) {
        if (commandMap != null || resolutionAttempted) {
            return commandMap;
        }
        resolutionAttempted = true;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                commandMap = (CommandMap) field.get(Bukkit.getPluginManager());
            }
        } catch (ReflectiveOperationException exception) {
            if (logger != null) {
                logger.warning("Не удалось получить доступ к CommandMap: " + exception.getMessage());
            }
            commandMap = null;
        }
        return commandMap;
    }
}
