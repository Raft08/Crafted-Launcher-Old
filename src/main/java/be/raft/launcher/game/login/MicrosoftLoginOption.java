package be.raft.launcher.game.login;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.file.GameFileManager;
import be.raft.launcher.file.loader.JsonFileLoader;
import be.raft.launcher.game.account.Account;
import be.raft.launcher.game.account.AccountManager;
import be.raft.launcher.game.account.MicrosoftAccount;
import be.raft.launcher.resources.Text;
import be.raft.launcher.ui.Placing;
import be.raft.launcher.ui.panel.Panel;
import be.raft.launcher.web.WebUtils;
import com.google.gson.JsonObject;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import javafx.application.Platform;
import javafx.scene.control.Label;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MicrosoftLoginOption extends Panel implements LoginOption {
    public static final String IDENTIFIER = "microsoft";

    private Label loginStatus;
    private Label retryBtn;

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
        return this;
    }

    //Panel
    @Override
    public void init() {
        //Update the UI manager, side effect of preloading the accounts
        this.uiManager = CraftedLauncher.instance.getUIManager();

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
        this.retryBtn.setOnMouseClicked(event -> this.uiManager.setMainPane(new MicrosoftLoginOption()));
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
