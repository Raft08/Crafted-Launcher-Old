package be.raft.launcher.game.login;

import be.raft.launcher.game.account.Account;
import be.raft.launcher.game.account.MicrosoftAccount;
import be.raft.launcher.ui.panel.Panel;
import be.raft.launcher.ui.panel.main.login.MicrosoftLoginPanel;
import be.raft.launcher.web.WebUtils;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MicrosoftLoginOption implements LoginOption {
    public static final String IDENTIFIER = "microsoft";

    @Override
    public @NotNull String getTranslationKey() {
        return "login_option.microsoft";
    }

    @Override
    public @NotNull String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public @NotNull Account parseAccount(JsonObject json) {
        String username = json.get("username").getAsString();
        UUID uniqueId = UUID.fromString(json.get("uuid").getAsString());
        String refreshToken = json.get("refreshToken").getAsString();

        return new MicrosoftAccount(username, uniqueId, refreshToken);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> isAvailable() {
        return WebUtils.ping(3000, "login.microsoftonline.com", "login.live.com", "user.auth.xboxlive.com",
                "api.minecraftservices.com");
    }

    @Override
    public @NotNull Panel getPanel() {
        return new MicrosoftLoginPanel();
    }
}
