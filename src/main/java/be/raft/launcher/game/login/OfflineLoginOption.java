package be.raft.launcher.game.login;

import be.raft.launcher.ui.panel.Panel;
import org.jetbrains.annotations.NotNull;

public class OfflineLoginOption extends Panel implements LoginOption {
    @Override
    public @NotNull String getTranslationKey() {
        return "login_option.offline";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "offline";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public @NotNull Panel getPanel() {
        return this;
    }

    @Override
    public void init() {

    }

    @Override
    public @NotNull String toString() {
        return "login-offline-panel";
    }
}
