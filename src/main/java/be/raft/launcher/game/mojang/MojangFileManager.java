package be.raft.launcher.game.mojang;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.file.GameFileManager;
import be.raft.launcher.file.loader.JsonFileLoader;
import be.raft.launcher.web.HttpClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MojangFileManager {
    public static final String VERSION_MANIFEST = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
    private static final HttpClient client = new HttpClient(new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool())
            .build());

    public static CompletableFuture<List<String>> getVersion(boolean includeSnapshots) {
        return CompletableFuture.supplyAsync(() -> {
            //Check if version are cached
            File versionManifest = new File(GameFileManager.getCacheDirectory(), "versions.json");
            JsonFileLoader versionLoader = new JsonFileLoader(versionManifest);
            if (!versionManifest.isFile()) {
                if (!GameFileManager.getCacheDirectory().isDirectory()) {
                    GameFileManager.getCacheDirectory().mkdirs();
                }

                JsonElement rawJson = client.jsonRequest(VERSION_MANIFEST).exceptionally(throwable -> {
                    CraftedLauncher.logger.error("Unable to download versions!", throwable);
                    return null;
                }).join();

                if (rawJson == null) {
                    return null;
                }

                versionLoader.createFile();
                versionLoader.save(rawJson);
            }

            List<JsonObject> jsonVersions = versionLoader.load().getAsJsonObject().get("versions").getAsJsonArray().asList()
                    .stream().map(JsonElement::getAsJsonObject).toList();

            List<String> versions = new ArrayList<>();

            jsonVersions.forEach(jsonVersion -> {
                if (includeSnapshots) {
                    versions.add(jsonVersion.get("id").getAsString());
                    return;
                }

                if (jsonVersion.get("type").getAsString().equals("release")) {
                    versions.add(jsonVersion.get("id").getAsString());
                }
            });

            //Update cache async
            CompletableFuture.runAsync(() -> {
                JsonElement rawJson = client.jsonRequest(VERSION_MANIFEST).exceptionally(throwable -> {
                    CraftedLauncher.logger.error("Unable to update version cache!", throwable);
                    return null;
                }).join();

                if (rawJson == null) {
                    return;
                }

                versionLoader.save(rawJson);
            });

            return versions;
        });
    }

    //Simply to initialize the class
    public static void init() {
    }
}
