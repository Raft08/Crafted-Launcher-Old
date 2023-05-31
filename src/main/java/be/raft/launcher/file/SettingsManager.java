package be.raft.launcher.file;

import be.raft.launcher.file.loader.JsonFileLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

    public boolean has(String settingKey) {
        return this.settings.has(settingKey);
    }

    public JsonElement getJsonElement(String settingKey) {
        return this.settings.get(settingKey);
    }

    public JsonObject getJsonObject(String settingKey) {
        return this.settings.get(settingKey).getAsJsonObject();
    }

    public JsonArray getJsonArray(String settingKey) {
        return this.settings.get(settingKey).getAsJsonArray();
    }

    public String getString(String settingKey) {
        return this.settings.get(settingKey).getAsString();
    }

    public boolean getBoolean(String settingKey) {
        return this.settings.get(settingKey).getAsBoolean();
    }

    public int getInt(String settingKey) {
        return this.settings.get(settingKey).getAsInt();
    }

    public long getLong(String settingKey) {
        return this.settings.get(settingKey).getAsInt();
    }

    public float getFloat(String settingKey) {
        return this.settings.get(settingKey).getAsFloat();
    }

    public short getShort(String settingKey) {
        return this.settings.get(settingKey).getAsShort();
    }

    public void setJsonElement(String settingKey, JsonElement value) {
        this.settings.add(settingKey, value);
    }

    public void setJsonObject(String settingKey, JsonObject value) {
        this.settings.add(settingKey, value);
    }

    public void setJsonArray(String settingKey, JsonArray value) {
        this.settings.add(settingKey, value);
    }

    public void setString(String settingKey, String value) {
        this.settings.addProperty(settingKey, value);
    }

    public void setBoolean(String settingKey, boolean value) {
        this.settings.addProperty(settingKey, value);
    }

    public void setInt(String settingKey, int value) {
        this.settings.addProperty(settingKey, value);
    }

    public void setLong(String settingKey, long value) {
        this.settings.addProperty(settingKey, value);
    }

    public void setFloat(String settingKey, float value) {
        this.settings.addProperty(settingKey, value);
    }

    public void setShort(String settingKey, short value) {
        this.settings.addProperty(settingKey, value);
    }
}
