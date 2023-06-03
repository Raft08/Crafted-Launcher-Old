package be.raft.launcher.game.login;

import be.raft.launcher.ui.Placing;
import be.raft.launcher.ui.panel.Panel;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;

public class MicrosoftLoginOption extends Panel implements LoginOption {
    @Override
    public @NotNull String getTranslationKey() {
        return "login_option.microsoft";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "microsoft";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public @NotNull Panel getPanel() {
        return this;
    }

    //Panel
    @Override
    public void init() {
        Label feedBackLabel = new Label();
        feedBackLabel.setId("login-microsoft-feedback");

        Placing.setCanTakeAllSize(feedBackLabel);
        Placing.setCenterV(feedBackLabel);
        Placing.setCenterH(feedBackLabel);

        this.layout.getChildren().addAll(feedBackLabel);
    }

    @Override
    public @NotNull String toString() {
        return "login-microsoft-panel";
    }
}
