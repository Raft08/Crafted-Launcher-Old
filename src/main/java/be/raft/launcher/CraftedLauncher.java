package be.raft.launcher;

import be.raft.launcher.file.GameFileManager;
import be.raft.launcher.file.SettingsManager;
import be.raft.launcher.resources.theme.DefaultTheme;
import be.raft.launcher.resources.theme.Theme;
import be.raft.launcher.resources.theme.ThemeManager;
import be.raft.launcher.ui.UIManager;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CraftedLauncher {
    private static final long startTime = System.currentTimeMillis();
    public static boolean devEnv;
    public static CraftedLauncher instance;
    public static final Logger logger = LoggerFactory.getLogger("Crafted-Launcher");

    public static void main(String[] args) {
        devEnv = Arrays.stream(args).toList().contains("-launcher:dev");
        try {
            Class.forName("javafx.application.Application");
            CraftedLauncher.logger.info("JavaFX validation successful!");
            new CraftedLauncher();
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error:\n" + e.getMessage() + "\nnot found!" +
                    "\nUnable to start the launcher!", "error", JOptionPane.ERROR_MESSAGE);
            CraftedLauncher.logger.error("JavaFX validation failed!", e);
        }
    }

    private final SettingsManager settingsManager;
    private UIManager uiManager;
    private Theme theme;
    private List<Theme> loadedThemes;

    public CraftedLauncher() {
        instance = this;

        CraftedLauncher.logger.info("Initializing Crafted Launcher...");

        if (!GameFileManager.getGameDirectory().isDirectory()) {
            GameFileManager.getGameDirectory().mkdirs();
        }

        //Load Themes
        long themeLoadingStartTime = System.currentTimeMillis();
        CompletableFuture<Void> themeLoadingFuture = ThemeManager.loadThemes().thenAccept(themes ->  {
            loadedThemes = themes;
            CraftedLauncher.logger.info("Themes took {}ms to load!", (System.currentTimeMillis() - themeLoadingStartTime));
        });

        //Load Settings
        settingsManager = new SettingsManager();
        if (!settingsManager.settingsFileExists()) {
            settingsManager.createSettings();
        }
        settingsManager.load();

        //Block main thread until themes are loaded
        if (!themeLoadingFuture.isDone()) {
            CraftedLauncher.logger.warn("Theme are still loading, blocking main thread until finished.");
            themeLoadingFuture.join();
        }

        if(settingsManager.getSettings().has("theme")) {
            String themeId = settingsManager.getSettings().get("theme").getAsString();
            Optional<Theme> possibleTheme = this.loadedThemes.stream().filter(loadedTheme -> loadedTheme.getId().equals(themeId)).findFirst();

            if (possibleTheme.isEmpty()) {
                CraftedLauncher.logger.warn("Unable to find theme '{}' setting default theme.", themeId);
                settingsManager.getSettings().addProperty("theme", "default");
                this.theme = DefaultTheme.theme;
                return;
            }

            this.theme = possibleTheme.get();
        } else {
            settingsManager.getSettings().addProperty("theme", "default");
            this.theme = DefaultTheme.theme;
        }

        //Run the JavaFX on another thread
        CompletableFuture.runAsync(() -> Application.launch(UIManager.class));
    }

    public Theme getTheme() {
        return theme;
    }

    public List<Theme> getLoadedThemes() {
        return loadedThemes;
    }

    public UIManager getUIManager() {
        return uiManager;
    }

    public void setUiManager(UIManager uiManager) {
        this.uiManager = uiManager;
    }
}
