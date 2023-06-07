package be.raft.launcher.game.login;

import be.raft.launcher.game.account.Account;
import be.raft.launcher.game.account.OfflineAccount;
import be.raft.launcher.ui.panel.Panel;
import be.raft.launcher.ui.panel.main.login.OfflineLoginPanel;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class OfflineLoginOption implements LoginOption {
    public static final String IDENTIFIER = "offline";

    @Override
    public @NotNull String getTranslationKey() {
        return "login_option.offline";
    }

    @Override
    public @NotNull String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public @NotNull Account parseAccount(JsonObject json) {
        String username = json.get("username").getAsString();
        UUID uniqueId = UUID.fromString(json.get("uuid").getAsString());

        return new OfflineAccount(username, uniqueId);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> isAvailable() {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public @NotNull Panel getPanel() {
        return new OfflineLoginPanel();
    }
}
