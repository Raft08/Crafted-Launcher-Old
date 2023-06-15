package be.raft.launcher.game.api.mojang.entities;

import be.raft.launcher.game.api.mojang.MojangPistonMeta;
import be.raft.launcher.web.Request;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VersionManifest {
    private final JsonObject manifest;
    private final MojangPistonMeta mojang;

    public VersionManifest(JsonObject manifest, MojangPistonMeta mojang) {
        this.manifest = manifest;
        this.mojang = mojang;
    }

    public List<String> getVersions(boolean includeSnapshots)  {
        List<JsonElement> versions = this.manifest.get("versions").getAsJsonArray().asList();

        if (includeSnapshots) {
            return versions.stream().map(element -> element.getAsJsonObject().get("id").getAsString()).toList();
        }

        return versions.stream().map(JsonElement::getAsJsonObject).filter(element ->
                element.get("type").getAsString().equals("release")).map(element -> element.get("id").getAsString()).toList();
    }

    public CompletableFuture<VersionSchema> getVersion(String version) {
        JsonObject versionBlock = this.manifest.get("versions").getAsJsonArray().asList()
                .stream().map(JsonElement::getAsJsonObject).filter(element ->
                        element.get("id").getAsString().equals(version)).findFirst().orElse(null);

        if (versionBlock == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Unknown version " + version));
        }

        String url = versionBlock.get("url").getAsString();

        return new Request<VersionSchema>(this.mojang.getClient())
                .cache(new File(this.mojang.getCacheDirectory(), version + "/" + version + ".json"))
                .url(url)
                .execute(data -> new VersionSchema(data.getAsJsonObject(), this.mojang));
    }
}
