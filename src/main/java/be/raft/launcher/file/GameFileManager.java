package be.raft.launcher.file;

import be.raft.launcher.CraftedLauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

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
    public static File getCacheDirectory() {
        return new File(getGameDirectory(), "cache");
    }

    public static boolean validateChecksum(String algorithm, String checksum, File file) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);

            try (DigestInputStream dis = new DigestInputStream(new FileInputStream(file), md)) {
                while (dis.read() != -1) ; //empty loop to clear the data
                md = dis.getMessageDigest();
            }

            // bytes to hex
            StringBuilder result = new StringBuilder();
            for (byte b : md.digest()) {
                result.append(String.format("%02x", b));
            }
            return result.toString().equals(checksum);
        } catch (IOException | NoSuchAlgorithmException e) {
            CraftedLauncher.logger.error("Unable to validate checksum of '{}'", file, e);
            return false;
        }
    }

    public static File updateLocation(File file) {
        if (file.isDirectory()) {
            File updatedFile = new File(file.getParent(), file.getName() + "_duplicate");
            return updateLocation(updatedFile);
        }
        return file;
    }

    public static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    public static List<File> getAllChildFiles(File rootDirectory) {
        List<File> childFiles = new ArrayList<>();
        exploreDirectory(rootDirectory, childFiles);
        return childFiles;
    }

    private static void exploreDirectory(File directory, List<File> childFiles) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    exploreDirectory(file, childFiles);
                } else {
                    childFiles.add(file);
                }
            }
        }
    }
}
