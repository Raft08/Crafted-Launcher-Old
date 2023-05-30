package be.raft.launcher.resources.theme;

import be.raft.launcher.file.GameFileManager;
import javafx.scene.image.Image;

import java.io.File;
import java.util.Random;

public class Theme {
    public static final String DEFAULT_CSS = "launcher.css";

    private final String name;
    private final String id;
    private final String description;
    private final String version;
    private final String[] authors;
    private final String credit;

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
        File cssFile = new File(GameFileManager.getThemeDirectory(),this.id + "/css/" + DEFAULT_CSS);
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
        File[] backgrounds = new File(GameFileManager.getThemeDirectory(), this.id + "/images/background").listFiles();
        if (backgrounds == null) {
            return DefaultTheme.theme.getBackground();
        }

        return new Image(backgrounds[new Random().nextInt(backgrounds.length)].getAbsolutePath());
    }
}
