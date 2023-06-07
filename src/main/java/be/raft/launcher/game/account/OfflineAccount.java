package be.raft.launcher.game.account;

import be.raft.launcher.game.login.OfflineLoginOption;
import com.google.gson.JsonObject;

import java.util.UUID;

public class OfflineAccount extends Account{
    public OfflineAccount(String username, UUID uniqueId) {
        super(username, uniqueId);
    }

    @Override
    public AuthInfo auth() {
        return new AuthInfo(this.username, this.uniqueId, "");
    }

    @Override
    public String toString() {
        return OfflineLoginOption.IDENTIFIER;
    }

    @Override
    public JsonObject toJson() {
        JsonObject data = new JsonObject();

        data.addProperty("username", this.username);
        data.addProperty("uuid", this.uniqueId.toString());
        data.addProperty("type", this.toString());

        return data;
    }
}
