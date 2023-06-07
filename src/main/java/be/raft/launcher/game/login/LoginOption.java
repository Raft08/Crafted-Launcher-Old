package be.raft.launcher.game.login;

import be.raft.launcher.game.account.Account;
import be.raft.launcher.ui.panel.Panel;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface LoginOption {
    @NotNull
    String getTranslationKey();

    @NotNull
    String getIdentifier();

    @NotNull
    Account parseAccount(JsonObject json);

    @NotNull
    CompletableFuture<Boolean> isAvailable();

    @NotNull
    Panel getPanel();
}
