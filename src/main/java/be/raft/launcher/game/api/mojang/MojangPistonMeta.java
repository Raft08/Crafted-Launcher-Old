package be.raft.launcher.game.api.mojang;

import be.raft.launcher.game.api.mojang.entities.VersionManifest;
import be.raft.launcher.web.Request;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class MojangPistonMeta {
    public static final String VERSION_MANIFEST = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";

    private final OkHttpClient client;
    private final File cacheDirectory;

    public MojangPistonMeta(OkHttpClient client, File cacheDirectory) {
        this.client = client;
        this.cacheDirectory = cacheDirectory;
    }

    public CompletableFuture<VersionManifest> getVersionManifest() {
        return new Request<VersionManifest>(this.client)
                .url(VERSION_MANIFEST)
                .execute(data -> new VersionManifest(data.getAsJsonObject(), this));
    }

    public OkHttpClient getClient() {
        return client;
    }

    public File getCacheDirectory() {
        return cacheDirectory;
    }
}
