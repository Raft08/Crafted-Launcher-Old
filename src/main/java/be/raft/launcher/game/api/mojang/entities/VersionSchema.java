package be.raft.launcher.game.api.mojang.entities;

import be.raft.launcher.game.api.mojang.MojangPistonMeta;
import com.google.gson.JsonObject;

public class VersionSchema {
    private final JsonObject version;
    private final MojangPistonMeta mojang;

    public VersionSchema(JsonObject version, MojangPistonMeta mojang) {
        this.version = version;
        this.mojang = mojang;
    }
}
