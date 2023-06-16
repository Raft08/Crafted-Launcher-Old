package be.raft.launcher.game.api.mojang.entities;

import be.raft.launcher.game.api.mojang.MojangPistonMeta;
import be.raft.launcher.utilities.SystemUtils;
import be.raft.launcher.web.Request;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class VersionSchema {
    public static final String RESOURCE_URL = "https://resources.download.minecraft.net/";

    private final JsonObject version;
    private final MojangPistonMeta mojang;

    public VersionSchema(JsonObject version, MojangPistonMeta mojang) {
        this.version = version;
        this.mojang = mojang;
    }

    public String getVersion() {
        return this.version.get("id").getAsString();
    }

    public int getJavaVersion() {
        return this.version.get("javaVersion").getAsJsonObject().get("majorVersion").getAsInt();
    }

    public String getMainClass() {
        return this.version.get("mainClass").getAsString();
    }

    public String getType() {
        return this.version.get("type").getAsString();
    }

    public CompletableFuture<File> downloadClientJar(File dest, Consumer<Integer> callback) {
        return new Request<>(this.mojang.getClient())
                .url(this.version.get("downloads").getAsJsonObject().get("client").getAsJsonObject().get("url").getAsString())
                .cache(new File(this.mojang.getCacheDirectory(), this.getVersion() + "/" + this.getVersion() + ".jar"))
                .callBack(callback)
                .download(dest);
    }

    public String getClientJarChecksum() {
        return this.version.get("downloads").getAsJsonObject().get("client").getAsJsonObject().get("sha1").getAsString();
    }

    public CompletableFuture<File> downloadServerJar(File dest, Consumer<Integer> callback) {
        return new Request<>(this.mojang.getClient())
                .url(this.version.get("downloads").getAsJsonObject().get("server").getAsJsonObject().get("url").getAsString())
                .cache(new File(this.mojang.getCacheDirectory(), this.getVersion() + "/" + this.getVersion() + "-server.jar"))
                .callBack(callback)
                .download(dest);
    }

    public String getServerJarChecksum() {
        return this.version.get("downloads").getAsJsonObject().get("server").getAsJsonObject().get("sha1").getAsString();
    }

    public CompletableFuture<File> downloadLibraries(File destRoot, Consumer<Integer> callback) {
        return CompletableFuture.supplyAsync(() -> {
            List<JsonObject> libraries = this.version.get("libraries").getAsJsonArray().asList().stream()
                    .map(JsonElement::getAsJsonObject).toList();

            //Filter downloads with the rules
            libraries = libraries.stream().filter(this::validateLibrariesRules).toList();

            int fileProgress = 0;
            for (JsonObject json : libraries) {

                String fileName = "";
                String url = "";
                JsonObject downloadBlock = json.get("downloads").getAsJsonObject();

                //Support for old Mojang manifest rules
                if (downloadBlock.has("classifiers")) {
                    JsonObject classifiers = downloadBlock.get("classifiers").getAsJsonObject();
                    String nativesOs = "natives-" + SystemUtils.getOS().getMojangValue();

                    if (classifiers.has(nativesOs)) {
                        fileName = classifiers.get(nativesOs).getAsJsonObject().get("path").getAsString();
                        url = classifiers.get(nativesOs).getAsJsonObject().get("url").getAsString();
                    }

                    String windowsNativesOsArch = "natives-" + SystemUtils.getOS().getMojangValue() + "-" + SystemUtils.getArchitecture();
                    if (classifiers.has(windowsNativesOsArch)) {
                        fileName = classifiers.get(windowsNativesOsArch).getAsJsonObject().get("path").getAsString();
                        url = classifiers.get(windowsNativesOsArch).getAsJsonObject().get("url").getAsString();
                    }
                }

                if (downloadBlock.has("artifact")) {
                    fileName = downloadBlock.get("artifact").getAsJsonObject().get("path").getAsString();
                    url = downloadBlock.get("artifact").getAsJsonObject().get("url").getAsString();
                }

                new Request<>(this.mojang.getClient())
                        .cache(new File(this.mojang.getCacheDirectory(), "libraries/" + fileName))
                        .url(url)
                        .download(new File(destRoot, fileName)).join();

                fileProgress++;
                callback.accept(fileProgress * 100 / libraries.size());
            }

            return destRoot;
        });
    }

    private boolean validateLibrariesRules(JsonObject lib) {
        if (!lib.has("rules")) {
            return true;
        }

        List<JsonObject> libRules = lib.get("rules").getAsJsonArray().asList().stream().map(JsonElement::getAsJsonObject)
                .toList();

        boolean rulesRespected = true;

        for (JsonObject rule : libRules) {
            String action = rule.get("action").getAsString();

            //Put every rule here
            if (rule.has("os")) {
                boolean ruleBool = rule.get("os").getAsJsonObject().get("name").getAsString().equals(SystemUtils.getOS().getMojangValue());
                if (action.equals("allow")) {
                    if (!ruleBool) {
                        rulesRespected = false;
                    }
                } else {
                    if (ruleBool) {
                        rulesRespected = true;
                    }
                }
            }


        }

        return rulesRespected;
    }

    public CompletableFuture<File> downloadAssets(File destRoot, Consumer<Integer> callback) {
        return CompletableFuture.supplyAsync(() -> {
            String assetIndexUrl = this.version.get("assetIndex").getAsJsonObject()
                    .get("url").getAsString();

            JsonObject assetIndex = new Request<JsonObject>(this.mojang.getClient())
                    .cache(new File(this.mojang.getCacheDirectory(), this.getVersion() + "/assetIndex.json"))
                    .url(assetIndexUrl)
                    .execute(data -> data.getAsJsonObject().get("objects").getAsJsonObject()).join();

            int processedAssets = 0;
            for (Map.Entry<String, JsonElement> entry : assetIndex.entrySet()) {
                String hash = entry.getValue().getAsJsonObject().get("hash").getAsString();

                String url = RESOURCE_URL + hash.substring(0, 2) + "/" + hash;

                new Request<>(this.mojang.getClient())
                        .cache(new File(this.mojang.getCacheDirectory(), this.getVersion() + "/assets/" + entry.getKey()))
                        .url(url)
                        .download(new File(destRoot, entry.getKey())).join();


                processedAssets++;
                callback.accept(processedAssets * 100 / assetIndex.size());
            }

            return destRoot;
        });
    }
}
