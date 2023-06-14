package be.raft.launcher.game.profiles.loader;

import java.io.File;

public class VanillaLoader {
    public boolean isModded() {
        return false;
    }

    public void createFolderStructure(File directory) {
        File resourcePacksFolder = new File(directory, "resourcepacks");
        File saveFolder = new File(directory, "saves");

        resourcePacksFolder.mkdirs();
        saveFolder.mkdirs();
    }

    public void installMinecraftVersion(String version, File directory) {

    }

    public void installLoader(String mcVersion, String loaderVersion, File directory) {
        //For Child loaders
    }
}
