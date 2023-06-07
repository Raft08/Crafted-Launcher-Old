package be.raft.launcher.resources.theme;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.file.GameFileManager;
import be.raft.launcher.file.loader.JsonFileLoader;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import javafx.scene.image.Image;

import java.io.File;
import java.util.*;

public class Theme {
    public static final String DEFAULT_CSS = "launcher.css";

    private final String name;
    private final String id;
    private final String description;
    private final String version;
    private final String[] authors;
    private final String credit;

    private List<Locale> availableLocales;

    public Theme(String name, String id, String description, String version, String[] authors, String credit) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.version = version;
        this.authors = authors;
        this.credit = credit;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public String[] getAuthors() {
        return authors;
    }

    public String getCredit() {
        return credit;
    }

    public String getStyleSheet() {
        File cssFile = new File(GameFileManager.getThemeDirectory(), this.id + "/css/" + DEFAULT_CSS);
        if (!cssFile.isFile()) {
            return DefaultTheme.theme.getStyleSheet();
        }

        return GameFileManager.getThemeDirectory() + "/" + this.id + "/css/" + DEFAULT_CSS;
    }

    public Image getImage(String image) {
        File imageFile = new File(GameFileManager.getThemeDirectory() + "/" + this.id + "/images/" + image);
        if (!imageFile.isFile()) {
            return DefaultTheme.theme.getImage(image);
        }

        return new Image(GameFileManager.getThemeDirectory() + "/" + this.id + "/images/" + image);
    }

    public Image getBackground() {
        File[] backgrounds = new File(GameFileManager.getThemeDirectory(), this.id + "/images/background").listFiles(file ->
                file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".png")));

        if (backgrounds == null) {
            return DefaultTheme.theme.getBackground();
        }

        return new Image(backgrounds[new Random().nextInt(backgrounds.length)].getAbsolutePath());
    }

    public List<Locale> getAvailableLocales() {
        if (this.availableLocales == null) {
            Set<Locale> uniqueLocales = new HashSet<>();

            uniqueLocales.addAll(DefaultTheme.theme.getAvailableLocales());

            File[] langFiles = new File(GameFileManager.getThemeDirectory(), this.id + "/lang/")
                    .listFiles(file -> file.isFile() && file.getName().endsWith(".json"));

            if (langFiles != null) {
                for (File langFile : langFiles) {
                    String[] splitLangFile = Files.getNameWithoutExtension(langFile.getName()).split("_");

                    if (splitLangFile.length != 2) {
                        CraftedLauncher.logger.error("Unable to load language file '{}'!", langFile);
                        continue;
                    }

                    uniqueLocales.add(new Locale(splitLangFile[0], splitLangFile[1]));
                }
            }

            this.availableLocales = new ArrayList<>(uniqueLocales);
        }

        return this.availableLocales;
    }

    public boolean isLocaleAvailable(Locale locale) {
        if (this.availableLocales == null) {
            this.availableLocales = getAvailableLocales();
        }

        return this.availableLocales.stream().anyMatch(availableLocale -> availableLocale.equals(locale));
    }

    public JsonObject getLocaleJson(Locale locale) {
        if (!isLocaleAvailable(locale)) {
            CraftedLauncher.logger.error("Cannot load locale '{}'!", locale);
            return null;
        }

        File langFile = new File(GameFileManager.getThemeDirectory(),
                this.id + "/lang/" + locale.getLanguage().toLowerCase() + "_" + locale.getCountry().toLowerCase() + ".json");

        if (!langFile.isFile()) {
            return DefaultTheme.theme.getLocaleJson(locale);
        }

        JsonFileLoader loader = new JsonFileLoader(langFile);
        return loader.load().getAsJsonObject();
    }
}
