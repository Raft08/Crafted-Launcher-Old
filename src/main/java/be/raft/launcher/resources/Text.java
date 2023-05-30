package be.raft.launcher.resources;

import com.google.gson.JsonObject;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Text {
    private static JsonObject parsedLocale;
    private static Locale activeLocale;
    public static String translated(String key, String... data) {
        if (parsedLocale == null || parsedLocale.get(key) == null) {
            return key;
        }

        String translated = parsedLocale.get(key).getAsString();

        Matcher matcher = Pattern.compile("%s(\\d+)").matcher(translated);

        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            int index = Integer.parseInt(matcher.group(1));
            if (index >= 0 && index < data.length) {
                String replacement = data[index];
                matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
            }
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }

    public static JsonObject getParsedLocale() {
        return parsedLocale;
    }

    public static Locale getActiveLocale() {
        return activeLocale;
    }

    public static void setLocale(JsonObject localeJson, Locale locale) {
        parsedLocale = localeJson;
        activeLocale = locale;
    }
}

