package it.gromov.zpayments.util;

import org.bukkit.ChatColor;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Method HEX_OF_METHOD = resolveHexMethod();

    private ColorUtil() {
    }

    public static String colorize(String text) {
        if (text == null || text.isEmpty()) {
            return text == null ? "" : text;
        }
        return ChatColor.translateAlternateColorCodes('&', applyHex(text));
    }

    public static String strip(String text) {
        if (text == null) {
            return "";
        }
        return ChatColor.stripColor(colorize(text));
    }

    private static String applyHex(String text) {
        if (!text.contains("&#")) {
            return text;
        }
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String replacement = resolveHex(matcher.group(1));
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String resolveHex(String hex) {
        if (HEX_OF_METHOD == null) {
            return "";
        }
        try {
            Object color = HEX_OF_METHOD.invoke(null, "#" + hex);
            return color.toString();
        } catch (ReflectiveOperationException exception) {
            return "";
        }
    }

    private static Method resolveHexMethod() {
        try {
            Class<?> bungeeChatColor = Class.forName("net.md_5.bungee.api.ChatColor");
            return bungeeChatColor.getMethod("of", String.class);
        } catch (ReflectiveOperationException exception) {
            return null;
        }
    }
}
