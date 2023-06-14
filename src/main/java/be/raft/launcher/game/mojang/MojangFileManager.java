package be.raft.launcher.game.mojang;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.file.GameFileManager;
import be.raft.launcher.file.loader.JsonFileLoader;
import be.raft.launcher.web.HttpClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.util.Pair;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MojangFileManager {
    public static final String VERSION_MANIFEST = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
    private static final HttpClient client = new HttpClient(new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool())
            .build());

    private static JsonFileLoader loadVersionManifest() {
        File versionManifest = new File(GameFileManager.getCacheDirectory(), "versions.json");
        JsonFileLoader versionLoader = new JsonFileLoader(versionManifest);
        //Check if the versions are cached
        if (!versionManifest.isFile()) {
            CraftedLauncher.logger.info("No local cache of the versions manifest, downloading it.");
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

        return versionLoader;
    }

    public static CompletableFuture<List<String>> getVersion(boolean includeSnapshots) {
        return CompletableFuture.supplyAsync(() -> {
            //Check if the versions are cached
            JsonFileLoader versionLoader = loadVersionManifest();

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

            return versions;
        });
    }

    public static void installVersion(String version, File dest, Consumer<Pair<String, Integer>> downloadCallback) {
        File versionDest = new File(GameFileManager.getCacheDirectory(), version);
        File libraryDest = new File(GameFileManager.getCacheDirectory(), "libraries");

        //Version manifest
        JsonFileLoader versionManifestLoader = loadVersionManifest();

        List<JsonObject> parsedJsonVersionManifest = versionManifestLoader.load().getAsJsonObject().get("versions").getAsJsonArray().asList().stream()
                .map(JsonElement::getAsJsonObject).toList();

        JsonObject versionBlock = parsedJsonVersionManifest.stream().filter(jsonVersion ->
                jsonVersion.get("id").getAsString().equals(version)).findFirst().orElse(null);

        if (versionBlock == null) {
            CraftedLauncher.logger.error("Could not find version '{}'!", version);
            return;
        }

        //Get & Load the specific version manifest
        JsonFileLoader versionLoader = loadVersion(version, versionBlock);
    }

    public static JsonFileLoader loadVersion(String version, JsonObject versionBlock) {
        File versionDest = new File(GameFileManager.getCacheDirectory(), version);
        JsonFileLoader versionLoader = new JsonFileLoader(new File(versionDest, version + ".json"));

        if (!versionLoader.fileExists()) {
            CraftedLauncher.logger.info("No local cache of the version manifest of {}, downloading it.", version);

            if (!versionDest.isDirectory()) {
                versionDest.mkdirs();
            }

            JsonElement rawJson = client.jsonRequest(versionBlock.get("url").getAsString()).exceptionally(throwable -> {
                CraftedLauncher.logger.error("Unable to download version '{}'!", version,  throwable);
                return null;
            }).join();

            if (rawJson == null) {
                return null;
            }

            versionLoader.createFile();
            versionLoader.save(rawJson);
        }

        //Validate checksum
        if (!GameFileManager.validateChecksum("sha1", versionBlock.get("sha1").getAsString(), versionLoader.getFile())) {
            CraftedLauncher.logger.error("Checksum validation failed, downloading the manifest again..");
            return loadVersion(version, versionBlock);
        }

        return versionLoader;
    }

    //Simply to initialize the class
    public static void init() {
    }
}
