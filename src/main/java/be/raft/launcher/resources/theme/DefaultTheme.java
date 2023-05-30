package be.raft.launcher.resources.theme;

import javafx.scene.image.Image;

import java.util.Random;

public class DefaultTheme extends Theme{
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
}
