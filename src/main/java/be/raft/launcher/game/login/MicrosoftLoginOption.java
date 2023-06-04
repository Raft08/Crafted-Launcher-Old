package be.raft.launcher.game.login;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.ui.Placing;
import be.raft.launcher.ui.panel.Panel;
import be.raft.launcher.web.WebUtils;
import javafx.scene.control.Label;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<Boolean> isAvailable() {
        return WebUtils.ping(3000, "login.microsoftonline.com", "login.live.com", "user.auth.xboxlive.com",
                "api.minecraftservices.com");
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
