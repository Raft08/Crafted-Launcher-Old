package be.raft.launcher.game.profiles;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.file.GameFileManager;
import be.raft.launcher.game.api.mojang.entities.VersionManifest;
import be.raft.launcher.game.api.mojang.entities.VersionSchema;
import javafx.util.Pair;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ProfileManager {
    public static final String PROFILE_LOCATION = "profiles";

    public static Profile createVanillaProfile(String name, File profileDir, String version, VersionManifest manifest, Consumer<Pair<String, Integer>> creationCallBack) {
        CompletableFuture<VersionSchema> versionFuture = manifest.getVersion(version);

        //Make sure location is available else append _duplicate after it
        profileDir = updateLocation(profileDir);

        CraftedLauncher.logger.info("Profile creation requested: name: '{}' location: '{}' version: '{}'", name,
                profileDir, version);

        profileDir.mkdirs();

        VersionSchema schema = versionFuture.join();

        //Download Jar
        schema.downloadClientJar(new File(profileDir, "client.jar"), progress -> {
            if (progress > -1) {
                CraftedLauncher.logger.info("Downloading {} client jar: {}%", version, progress);
            } else {
                CraftedLauncher.logger.error("Failed to download the client jar!");
            }
        }).exceptionally(throwable -> {
            CraftedLauncher.logger.error("Download failed: ", throwable);
            return null;
        }).join();

        //Download Libraries
        schema.downloadLibraries(new File(profileDir, "libraries"), progress -> {
            if (progress > -1) {
                CraftedLauncher.logger.info("Downloading libraries for {}: {}%", version, progress);
            } else {
                CraftedLauncher.logger.error("Failed to download the libraries!");
            }
        }).exceptionally(throwable -> {
            CraftedLauncher.logger.error("Download failed: ", throwable);
            return null;
        }).join();

        //Download Assets
        schema.downloadAssets(new File(profileDir, "assets"), progress -> {
            if (progress > -1) {
                CraftedLauncher.logger.info("Downloading assets for {}: {}%", version, progress);
            } else {
                CraftedLauncher.logger.error("Failed to download the libraries!");
            }
        }).exceptionally(throwable -> {
            CraftedLauncher.logger.error("Download failed: ", throwable);
            return null;
        }).join();

        return new Profile();
    }

    private static File updateLocation(File file) {
        if (file.isDirectory()) {
            File updatedFile = new File(GameFileManager.getFileInGameDirectory(PROFILE_LOCATION), file.getName() + "_duplicate");
            return updateLocation(updatedFile);
        }
        return file;
    }
}
