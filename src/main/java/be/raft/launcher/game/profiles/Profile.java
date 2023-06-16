package be.raft.launcher.game.profiles;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.file.GameFileManager;
import be.raft.launcher.file.loader.JsonFileLoader;
import be.raft.launcher.game.api.mojang.entities.VersionManifest;
import be.raft.launcher.game.api.mojang.entities.VersionSchema;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.TestOnly;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class Profile {
    private final String name;
    private final File dir;
    private final String author;
    private final String description;
    private final String version;
    private final List<BiConsumer<String, Integer>> progressListeners;
    private final String mcVersion;
    private boolean playable;

    public Profile(String name, File dir, String version, String author, String description, String mcVersion) {
        this.name = name;
        this.dir = dir;
        this.author = author;
        this.description = description;
        this.version = version;
        this.progressListeners = new ArrayList<>();
        this.mcVersion = mcVersion;

        File profileJson = new File(dir, "profile.json");
        if (!profileJson.isFile()) {
            this.playable = false;
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }

            JsonFileLoader loader = new JsonFileLoader(profileJson);

            JsonObject data = new JsonObject();

            data.addProperty("comment", "DO NOT MODIFY THIS FILE, THIS IS USED AS A BLUEPRINT FOR THIS PROFILE. IF YOU EDIT/CHANGE DATA FROM THIS JSON IT COULD HAVE VERY BAD SIDE EFFECTS!!!!");
            data.addProperty("name", name);
            data.addProperty("author", author);
            data.addProperty("description", description);
            data.addProperty("version", version);

            JsonObject minecraftData = new JsonObject();

            minecraftData.addProperty("version", mcVersion);

            data.add("minecraft", minecraftData);

            loader.createFile();
            loader.save(data);
        }
    }

    public void delete() {
        this.playable = false;
        GameFileManager.deleteDirectory(dir);
    }

    public boolean install(VersionManifest manifest) {
        this.playable = false;

        File assetsDir = new File(dir, "assets");
        File libraryDir = new File(dir, "libraries");
        File clientJar = new File(dir, "client.jar");

        if (assetsDir.isDirectory()) {
            assetsDir.delete();
        }

        if (libraryDir.isDirectory()) {
            libraryDir.delete();
        }

        if (clientJar.isFile()) {
            clientJar.delete();
        }

        this.updateProgressListeners("download.version", 0);

        VersionSchema versionSchema = manifest.getVersion(this.mcVersion)
                .exceptionally(throwable -> {
                    CraftedLauncher.logger.error("Cannot install {}", this.name, throwable);
                    return null;
                }).join();

        if (versionSchema == null) {
            this.updateProgressListeners("download.version", -1);
            return false;
        }

        this.updateProgressListeners("download.version", 100);

        //Download Jar
        this.updateProgressListeners("download.jar", 0);
        File jar = versionSchema.downloadClientJar(clientJar, progress -> this.updateProgressListeners("download.jar", progress))
                .exceptionally(throwable -> {
                    CraftedLauncher.logger.error("Cannot install {}", this.name, throwable);
                    return null;
                }).join();

        if (jar == null) {
            this.updateProgressListeners("download.jar", -1);
            return false;
        }

        //Download Libraries
        this.updateProgressListeners("download.libraries", 0);
        File libs = versionSchema.downloadLibraries(libraryDir, progress -> this.updateProgressListeners("download.libraries", progress))
                .exceptionally(throwable -> {
                    CraftedLauncher.logger.error("Cannot install {}", this.name, throwable);
                    return null;
                }).join();

        if (libs == null) {
            this.updateProgressListeners("download.libraries", -1);
            return false;
        }

        //Download Assets
        this.updateProgressListeners("download.assets", 0);
        File assets = versionSchema.downloadAssets(assetsDir, progress -> this.updateProgressListeners("download.assets", progress))
                .exceptionally(throwable -> {
                    CraftedLauncher.logger.error("Cannot install {}", this.name, throwable);
                    return null;
                }).join();

        if (assets == null) {
            this.updateProgressListeners("download.assets", -1);
            return false;
        }

        this.playable = true;

        //Call a last time to update the listeners for them to accept the new playable value
        this.updateProgressListeners("download.assets", 100);

        return true;
    }

    public boolean isPlayable() {
        return playable;
    }

    @TestOnly
    public void setPlayable(boolean playable) {
        this.playable = playable;
    }

    private void updateProgressListeners(String step, int progress) {
        this.progressListeners.forEach(listener -> listener.accept(step, progress));
    }

    public void addProgressListener(BiConsumer<String, Integer> progressCallback) {
        this.progressListeners.add(progressCallback);
    }

    public void clearProgressListeners() {
        this.progressListeners.clear();
    }

    public String getName() {
        return name;
    }

    public File getDir() {
        return dir;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public String getMcVersion() {
        return mcVersion;
    }
}
