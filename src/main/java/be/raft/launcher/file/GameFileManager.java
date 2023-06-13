package be.raft.launcher.file;

import be.raft.launcher.CraftedLauncher;

import java.io.File;
import java.io.IOException;

public class GameFileManager {
    private static final String LAUNCHER_FOLDER = "crafted";

    public static File getWorkingDirectory() {
        return new File(System.getProperty("user.dir"));
    }

    public static File getGameDirectory() {
        if (CraftedLauncher.devEnv) {
            return new File(getWorkingDirectory(), "/appdata/" + LAUNCHER_FOLDER);
        }

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win"))
            return new File(System.getProperty("user.home") + "/AppData/Roaming/." + LAUNCHER_FOLDER);
        else if (os.contains("mac"))
            return new File(System.getProperty("user.home") + "/Library/Application Support/" + LAUNCHER_FOLDER);
        else
            return new File(System.getProperty("user.home") + "/." + LAUNCHER_FOLDER);
    }

    public static File getFileInGameDirectory(String file) {
        return new File(getGameDirectory(), file);
    }

    public static File getInstallationDirectory() {
        if (CraftedLauncher.devEnv) {
            return new File(getWorkingDirectory(), "/program");
        }
        return getWorkingDirectory();
    }

    public static File getThemeDirectory() {
        return new File(getGameDirectory(), "themes");
    }
    public static boolean validDirectoryName(String filename) {
        File f = new File(filename);
        try {
            f.getCanonicalPath();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
