package be.raft.launcher.resources.theme;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.file.GameFileManager;
import be.raft.launcher.file.loader.JsonFileLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ThemeManager {
    public static CompletableFuture<List<Theme>> loadThemes() {
        return CompletableFuture.supplyAsync(() -> {
            CraftedLauncher.logger.info("Discovering themes...");

            File themeDirectory = GameFileManager.getThemeDirectory();

            if (!themeDirectory.isDirectory()) {
                themeDirectory.mkdirs();
                CraftedLauncher.logger.info("Loaded 1 theme");
                return List.of(DefaultTheme.theme);
            }

            File[] themesFiles = themeDirectory.listFiles(File::isDirectory);
            if (themesFiles == null) {
                CraftedLauncher.logger.info("Loaded 1 theme");
                return List.of(DefaultTheme.theme);
            }

            List<Theme> themes = new ArrayList<>(List.of(DefaultTheme.theme));

            for (File file : themesFiles) {
                //Load the theme file
                try {
                    JsonObject themeJson = new JsonFileLoader(new File(file, "theme.json")).load().getAsJsonObject();

                    Theme theme = validateTheme(themeJson, file);
                    if (theme == null) {
                        CraftedLauncher.logger.error("Theme validation failed for '{}'", file);
                        continue;
                    }

                    themes.add(theme);
                } catch (Exception e) {
                    CraftedLauncher.logger.error("Unable to read theme properties file for '{}'", file, e);
                }
            }

            CraftedLauncher.logger.info("Loaded {} theme", themes.size());

            return themes;
        });
    }

    private static Theme validateTheme(JsonObject json, File file) {
        try {
            String name = json.get("name").getAsString();
            String description = json.get("description").getAsString();
            String version = json.get("version").getAsString();
            String[] authors = json.get("authors").getAsJsonArray().asList().stream().map(JsonElement::getAsString)
                    .toArray(String[]::new);
            String credit = json.get("credit").getAsString();

            return new Theme(name, file.getName(), description, version, authors, credit);
        } catch (Exception e) {
            CraftedLauncher.logger.error("Unable to validate theme!", e);
            return null;
        }
    }
}
