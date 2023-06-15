package be.raft.launcher.game.profiles;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.file.GameFileManager;
import be.raft.launcher.game.api.mojang.entities.VersionManifest;
import javafx.util.Pair;

import java.io.File;
import java.util.function.Consumer;

public class ProfileManager {
    public static final String PROFILE_LOCATION = "profiles";

    public static Profile createVanillaProfile(String name, File profileDir, String version, VersionManifest manifest, Consumer<Pair<String, Integer>> creationCallBack) {
        //Make sure location is available else append _duplicate after it
        profileDir = updateLocation(profileDir);

        CraftedLauncher.logger.info("Profile creation requested: name: '{}' location: '{}' version: '{}'", name,
                profileDir, version);

        profileDir.mkdirs();

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
