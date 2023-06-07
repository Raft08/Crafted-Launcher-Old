package be.raft.launcher.ui.panel.main.login;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.file.GameFileManager;
import be.raft.launcher.file.loader.JsonFileLoader;
import be.raft.launcher.game.account.Account;
import be.raft.launcher.game.account.AccountManager;
import be.raft.launcher.game.account.MicrosoftAccount;
import be.raft.launcher.game.login.MicrosoftLoginOption;
import be.raft.launcher.resources.Text;
import be.raft.launcher.ui.Placing;
import be.raft.launcher.ui.panel.Panel;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import javafx.application.Platform;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;

public class MicrosoftLoginPanel extends Panel {
    private Label loginStatus;
    private Label retryBtn;
    @Override
    public void init() {
        this.loginStatus = new Label(Text.translated("label.login.microsoft.status.waiting"));
        this.loginStatus.setId("login-microsoft-status");

        Placing.setCanTakeAllSize(this.loginStatus);
        Placing.setCenterH(this.loginStatus);
        Placing.setCenterV(this.loginStatus);

        this.retryBtn = new Label(Text.translated("btn.retry"));
        this.retryBtn.getStyleClass().add("clickable-label");
        this.retryBtn.setId("login-microsoft-retry");
        this.retryBtn.setVisible(false);

        Placing.setCanTakeAllSize(this.retryBtn);
        Placing.setCenterH(this.retryBtn);
        Placing.setCenterV(this.retryBtn);

        this.layout.getChildren().addAll(this.loginStatus, this.retryBtn);

        //Dynamic code
        this.retryBtn.setOnMouseClicked(event -> this.uiManager.setMainPane(new MicrosoftLoginPanel()));
    }

    @Override
    public void onShow() {
        super.onShow(); //Animation

        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();

        authenticator.loginWithAsyncWebview().whenComplete((result, throwable) -> {
            if (result == null) {
                Platform.runLater(() -> {
                    if (throwable != null) {
                        CraftedLauncher.logger.error("Unable to login:", throwable);
                        this.loginStatus.setText(Text.translated("label.login.microsoft.status.failed", throwable.getMessage()));
                    } else {
                        CraftedLauncher.logger.error("Unable to login: User closed the window");
                        this.loginStatus.setText(Text.translated("label.login.microsoft.status.failed",
                                Text.translated("label.login.microsoft.status.failed.window_closed")));
                    }
                    this.retryBtn.setVisible(true);
                });
                return;
            }

            Platform.runLater(() -> this.loginStatus.setText(Text.translated("label.login.microsoft.status.success")));

            UUID accountUniqueId = UUID.fromString(result.getProfile().getId().replaceAll("(.{8})(.{4})(.{4})(.{4})(.{12})", "$1-$2-$3-$4-$5"));

            Account account = new MicrosoftAccount(result.getProfile().getName(), accountUniqueId,
                    result.getRefreshToken(), result.getAccessToken());

            CraftedLauncher.logger.info("Logged as {}({})", account.getUsername(), account.getUniqueId());

            File accountDir = GameFileManager.getFileInGameDirectory(AccountManager.ACCOUNTS_DIR);

            if (!accountDir.isDirectory()) {
                accountDir.mkdirs();
            }

            //Saving the account
            JsonFileLoader accountFileLoader = new JsonFileLoader(new File(accountDir,
                    account.getUniqueId().toString() + ".json"));



            if (accountFileLoader.fileExists()) {
                CraftedLauncher.logger.warn("Account '{}' already exists, deleting old account file..", account.getUsername());
                accountFileLoader.getFile().delete();
            }

            accountFileLoader.createFile();
            accountFileLoader.save(account.toJson());

            this.uiManager.getLauncher().setSelectedAccount(account);
            this.uiManager.getLauncher().getSettingsManager().setString("selectedAccount", account.getUniqueId().toString());
            this.uiManager.getLauncher().getSettingsManager().save();
        });
    }

    @Override
    public @NotNull String toString() {
        return "login-microsoft-panel";
    }
}
