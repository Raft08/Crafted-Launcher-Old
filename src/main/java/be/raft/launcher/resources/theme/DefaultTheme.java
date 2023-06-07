package be.raft.launcher.resources.theme;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.file.loader.JsonStreamLoader;
import com.google.gson.JsonObject;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class DefaultTheme extends Theme {
    public static final DefaultTheme theme = new DefaultTheme();
    private static final int BACKGROUND_COUNT = 6;

    public DefaultTheme() {
        super("Default", "default", "Default Theme of the launcher", "1.0.0",
                new String[]{"RaftDev"}, "All Right reserved on the background images!");
    }

    @Override
    public String getStyleSheet() {
        return "default/css/" + Theme.DEFAULT_CSS;
    }

    @Override
    public Image getImage(String image) {
        return new Image("default/images/" + image);
    }

    @Override
    public Image getBackground() {
        return new Image("default/images/background/" + new Random().nextInt(BACKGROUND_COUNT - 1) + ".jpg");
    }

    @Override
    public List<Locale> getAvailableLocales() {
        //Manually add every language in the default theme
        return Arrays.asList(new Locale("en", "us"));
    }

    @Override
    public JsonObject getLocaleJson(Locale locale) {
        if (!super.isLocaleAvailable(locale)) {
            CraftedLauncher.logger.error("Cannot load locale '{}'!", locale);
            return null;
        }

        String file = "default/lang/" + locale.getLanguage().toLowerCase() + "_" + locale.getCountry().toLowerCase() + ".json";

        try (InputStream stream = Theme.class.getClassLoader().getResourceAsStream(file)) {
            return new JsonStreamLoader(stream).load().getAsJsonObject();
        } catch (IOException e) {
            CraftedLauncher.logger.error("Unable to load locale '{}'!", locale, e);
            return null;
        }
    }
}
