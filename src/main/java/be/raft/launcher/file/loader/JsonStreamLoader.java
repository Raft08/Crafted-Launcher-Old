package be.raft.launcher.file.loader;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class JsonStreamLoader extends FileLoader<JsonElement> {
    private final InputStream stream;

    public JsonStreamLoader(InputStream stream) {
        super(null);
        this.stream = stream;
    }

    @Override
    public void createFile() {
        throw new IllegalStateException("Cannot create file for a StreamLoader!");
    }

    @Override
    public boolean fileExists() {
        return this.stream != null;
    }

    @Override
    public JsonElement load() {
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(JsonElement value) {
        throw new IllegalStateException("Cannot save file for a StreamLoader!");
    }

    public InputStream getStream() {
        return stream;
    }
}
