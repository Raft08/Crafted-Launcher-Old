package be.raft.launcher.game.login;

import be.raft.launcher.ui.panel.Panel;
import org.jetbrains.annotations.NotNull;

public interface LoginOption {
    @NotNull
    String getTranslationKey();
    @NotNull
    String getIdentifier();

    boolean isAvailable();

    @NotNull
    Panel getPanel();
}
