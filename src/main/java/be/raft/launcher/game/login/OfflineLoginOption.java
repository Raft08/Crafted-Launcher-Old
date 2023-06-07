package be.raft.launcher.game.login;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.game.account.Account;
import be.raft.launcher.ui.panel.Panel;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class OfflineLoginOption extends Panel implements LoginOption {
    public static final String IDENTIFIER = "microsoft";

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
        return null; //Not Implemented
    }

    @Override
    public @NotNull CompletableFuture<Boolean> isAvailable() {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public @NotNull Panel getPanel() {
        return this;
    }

    @Override
    public void init() {
        //Update the UI manager, side effect of preloading the accounts
        this.uiManager = CraftedLauncher.instance.getUIManager();
    }

    @Override
    public @NotNull String toString() {
        return "login-offline-panel";
    }
}
