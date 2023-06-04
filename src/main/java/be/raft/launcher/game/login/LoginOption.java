package be.raft.launcher.game.login;

import be.raft.launcher.ui.panel.Panel;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface LoginOption {
    @NotNull
    String getTranslationKey();
    @NotNull
    String getIdentifier();

    CompletableFuture<Boolean> isAvailable();

    @NotNull
    Panel getPanel();
}
