package it.gromov.zpayments.util;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionUtil {

    private static final Pattern VERSION_PATTERN = Pattern.compile("\\(MC:\\s*(\\d+)\\.(\\d+)(?:\\.(\\d+))?\\)");
    private static final int MAJOR;
    private static final int MINOR;
    private static final int PATCH;

    static {
        Matcher matcher = VERSION_PATTERN.matcher(Bukkit.getVersion());
        if (matcher.find()) {
            MAJOR = Integer.parseInt(matcher.group(1));
            MINOR = Integer.parseInt(matcher.group(2));
            PATCH = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
        } else {
            MAJOR = 1;
            MINOR = 12;
            PATCH = 2;
        }
    }

    private VersionUtil() {
    }

    public static int major() {
        return MAJOR;
    }

    public static int minor() {
        return MINOR;
    }

    public static int patch() {
        return PATCH;
    }

    public static boolean isAtLeast(int requiredMinor) {
        return MINOR >= requiredMinor;
    }
}
