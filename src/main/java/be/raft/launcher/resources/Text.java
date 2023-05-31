package be.raft.launcher.resources;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.file.SettingsManager;
import be.raft.launcher.resources.theme.Theme;
import com.google.gson.JsonObject;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
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

    public static CompletableFuture<Void> loadLocales(SettingsManager settings, Theme theme) {
        return CompletableFuture.runAsync(() -> {
            if (settings.has("language")) {
                String[] language = settings.getString("language").split("_");

                Locale locale = new Locale(language[0], language[1]);
                if (theme.isLocaleAvailable(locale)) {
                    Text.setLocale(theme.getLocaleJson(locale), locale);
                    return;
                }

                CraftedLauncher.logger.warn("Unable to load language '{}' trying to load system language..", locale);

                Locale systemLocale = Locale.getDefault();
                if (theme.isLocaleAvailable(systemLocale)) {
                    Text.setLocale(theme.getLocaleJson(systemLocale), systemLocale);
                    settings.setString("language", systemLocale.toString().toLowerCase());
                    settings.save();
                    return;
                }

                CraftedLauncher.logger.warn("Unable to load system language '{}' loading en_us..", systemLocale);

                Locale enUSLocale = new Locale("en", "us");
                Text.setLocale(theme.getLocaleJson(enUSLocale), enUSLocale);
                settings.setString("language", enUSLocale.toString().toLowerCase());
                settings.save();
            } else {
                Locale locale = Locale.getDefault();
                if (theme.isLocaleAvailable(locale)) {
                    Text.setLocale(theme.getLocaleJson(locale), locale);
                    settings.setString("language", locale.toString().toLowerCase());
                    settings.save();
                    return;
                }

                CraftedLauncher.logger.warn("Unable to load system language '{}' loading en_us..", locale);

                Locale enUSLocale = new Locale("en", "us");
                Text.setLocale(theme.getLocaleJson(enUSLocale), enUSLocale);
                settings.setString("language", enUSLocale.toString().toLowerCase());
                settings.save();
            }
        });
    }
}

