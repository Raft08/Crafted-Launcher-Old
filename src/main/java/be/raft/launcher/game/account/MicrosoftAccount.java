package be.raft.launcher.game.account;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.game.login.MicrosoftLoginOption;
import com.google.gson.JsonObject;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;

import java.util.UUID;

public class MicrosoftAccount extends Account {
    private final String refreshToken;
    private String accessToken;

    public MicrosoftAccount(String username, UUID uniqueId, String refreshToken) {
        super(username, uniqueId);
        this.refreshToken = refreshToken;
    }

    public MicrosoftAccount(String username, UUID uniqueId, String refreshToken, String accessToken) {
        super(username, uniqueId);
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

    @Override
    public AuthInfo auth() {
        if (accessToken == null) {
            try {
                MicrosoftAuthResult result = new MicrosoftAuthenticator().loginWithRefreshToken(this.refreshToken);

                this.username = result.getProfile().getName();
                this.uniqueId = UUID.fromString(result.getProfile().getId());
                this.accessToken = result.getAccessToken();
            } catch (MicrosoftAuthenticationException e) {
                CraftedLauncher.logger.error("Cannot authenticate '{}'", this.getUsername(), e);
                return null;
            }
        }

        return new AuthInfo(this.username, this.uniqueId, this.accessToken);
    }

    @Override
    public String toString() {
        return MicrosoftLoginOption.IDENTIFIER;
    }

    @Override
    public JsonObject toJson() {
        JsonObject data = new JsonObject();

        data.addProperty("username", this.username);
        data.addProperty("uuid", this.uniqueId.toString());
        data.addProperty("type", this.toString());
        data.addProperty("refreshToken", this.refreshToken);

        return data;
    }
}
