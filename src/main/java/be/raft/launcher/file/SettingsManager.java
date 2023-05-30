package be.raft.launcher.file;

import be.raft.launcher.file.loader.JsonFileLoader;
import com.google.gson.JsonObject;

import java.io.File;

public class SettingsManager {
    public static final File SETTINGS_FILE = GameFileManager.getFileInGameDirectory("settings.json");

    private final JsonFileLoader loader;
    private JsonObject settings;

    public SettingsManager() {
        this.loader = new JsonFileLoader(SETTINGS_FILE);
    }

    public void load() {
        this.settings = this.loader.load().getAsJsonObject();
    }

    public void save() {
        this.loader.save(this.settings);
    }

    public boolean settingsFileExists() {
        return this.loader.fileExists();
    }

    public void createSettings() {
        this.loader.createFile();
    }

    public JsonObject getSettings() {
        return settings;
    }
}
