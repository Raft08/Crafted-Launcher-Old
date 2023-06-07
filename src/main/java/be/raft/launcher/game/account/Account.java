package be.raft.launcher.game.account;

import java.util.UUID;

public abstract class Account {
    protected String username;
    protected UUID uniqueId;

    public Account(String username, UUID uniqueId) {
        this.username = username;
        this.uniqueId = uniqueId;
    }

    public abstract AuthInfo auth();

    public String getUsername() {
        return username;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    record AuthInfo(String username, UUID uniqueId, String accessToken) {
    }
}
