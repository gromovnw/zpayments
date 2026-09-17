package it.gromov.zpayments.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class PlaceholderUtil {

    private PlaceholderUtil() {
    }

    public static String apply(String text, Map<String, String> placeholders) {
        if (text == null) {
            return "";
        }
        if (placeholders == null || placeholders.isEmpty()) {
            return text;
        }
        String result = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String value = entry.getValue() == null ? "" : entry.getValue();
            result = result.replace("{" + entry.getKey() + "}", value);
        }
        return result;
    }

    public static List<String> apply(List<String> lines, Map<String, String> placeholders) {
        if (lines == null) {
            return java.util.Collections.emptyList();
        }
        return lines.stream()
                .map(line -> apply(line, placeholders))
                .collect(Collectors.toList());
    }
}
