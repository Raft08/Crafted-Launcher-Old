package be.raft.launcher.file.loader;

import java.io.File;
import java.io.IOException;

public abstract class FileLoader<T> {
    protected final File file;

    public FileLoader(File file) {
        this.file = file;
    }

    public abstract T load();
    public abstract void save(T value);
    public boolean fileExists() {
        return file.isFile();
    }

    public void createFile() {
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File getFile() {
        return file;
    }
}
