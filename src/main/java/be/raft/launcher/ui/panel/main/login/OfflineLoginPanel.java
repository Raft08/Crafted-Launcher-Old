package be.raft.launcher.ui.panel.main.login;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.file.GameFileManager;
import be.raft.launcher.file.loader.JsonFileLoader;
import be.raft.launcher.game.account.Account;
import be.raft.launcher.game.account.AccountManager;
import be.raft.launcher.game.account.OfflineAccount;
import be.raft.launcher.resources.Text;
import be.raft.launcher.ui.Placing;
import be.raft.launcher.ui.panel.Panel;
import be.raft.launcher.ui.panel.side.HomeSidePanel;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class OfflineLoginPanel extends Panel {
    @Override
    public void init() {
        Label panelTitle = new Label(Text.translated("label.login.offline.title"));
        panelTitle.setId("login-offline-title");

        Placing.setCanTakeAllSize(panelTitle);
        Placing.setCenterV(panelTitle);
        Placing.setCenterH(panelTitle);

        Label usernameErrorField = new Label();
        usernameErrorField.setId("login-offline-username-error");
        usernameErrorField.setVisible(false);

        Placing.setCanTakeAllSize(usernameErrorField);
        Placing.setCenterV(usernameErrorField);
        Placing.setCenterH(usernameErrorField);

        Label usernameFieldLabel = new Label(Text.translated("label.username"));
        usernameFieldLabel.setId("login-offline-username-label");

        Placing.setCanTakeAllSize(usernameFieldLabel);
        Placing.setCenterH(usernameFieldLabel);
        Placing.setCenterV(usernameFieldLabel);

        TextField usernameField = new TextField();
        usernameField.setPromptText(Text.translated("label.username"));
        usernameField.setId("login-offline-username-field");

        Placing.setCanTakeAllSize(usernameField);
        Placing.setCenterH(usernameField);
        Placing.setCenterV(usernameField);

        Button login = new Button(Text.translated("btn.login.offline.login"));
        login.setId("login-offline-login-btn");
        login.setDisable(true);

        Placing.setCanTakeAllSize(login);
        Placing.setCenterV(login);
        Placing.setCenterH(login);

        this.layout.getChildren().addAll(panelTitle, usernameErrorField, usernameFieldLabel, usernameField, login);

        //Username error field animation
        Translate errorFieldTranslate = new Translate();
        usernameErrorField.getTransforms().add(errorFieldTranslate);

        Timeline errorFieldAnim = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(errorFieldTranslate.xProperty(), 0)),
                new KeyFrame(Duration.millis(100), new KeyValue(errorFieldTranslate.xProperty(), -10)),
                new KeyFrame(Duration.millis(200), new KeyValue(errorFieldTranslate.xProperty(), 10)),
                new KeyFrame(Duration.millis(300), new KeyValue(errorFieldTranslate.xProperty(), -10)),
                new KeyFrame(Duration.millis(400), new KeyValue(errorFieldTranslate.xProperty(), 10)),
                new KeyFrame(Duration.millis(500), new KeyValue(errorFieldTranslate.xProperty(), 0))
        );

        //Dynamic UI code
        usernameField.textProperty().addListener(observable -> {
            if (usernameField.getText().isEmpty()) {
                usernameErrorField.setText("*" + Text.translated("label.required"));
                usernameErrorField.setVisible(true);
                errorFieldAnim.play();
                login.setDisable(true);
                return;
            }

            if (!isValidUsername(usernameField.getText())) {
                usernameErrorField.setText("*" + Text.translated("label.login.offline.username_error.invalid"));
                usernameErrorField.setVisible(true);
                errorFieldAnim.play();
                login.setDisable(true);
                return;
            }

            usernameErrorField.setVisible(false);
            login.setDisable(false);
        });

        login.setOnMouseClicked(event -> {
            UUID uuid = generateOfflineUuid(usernameField.getText());

            Account account = new OfflineAccount(usernameField.getText(), uuid);

            CraftedLauncher.logger.info("Logged as: {}({})", account.getUsername(), account.getUniqueId());

            File accountDir = GameFileManager.getFileInGameDirectory(AccountManager.ACCOUNTS_DIR);

            if (!accountDir.isDirectory()) {
                accountDir.mkdirs();
            }

            JsonFileLoader accountLoader = new JsonFileLoader(new File(accountDir,
                    account.getUniqueId() + ".json"));

            if (accountLoader.fileExists()) {
                CraftedLauncher.logger.warn("Account '{}' already exists, deleting old account file..", account.getUsername());
                accountLoader.getFile().delete();
            }

            accountLoader.save(account.toJson());
            this.uiManager.getLauncher().setSelectedAccount(account);
            this.uiManager.getLauncher().getSettingsManager().setString("selectedAccount", account.getUniqueId().toString());
            this.uiManager.getLauncher().getSettingsManager().save();

            //Go to the home panel
            Platform.runLater(() -> this.uiManager.setSideBar(new HomeSidePanel()));
        });

    }

    private boolean isValidUsername(String username) {
        String regex = "^[a-zA-Z0-9_]{3,16}$";
        return username.matches(regex) && !username.startsWith("_") && !username.endsWith("_");
    }

    private UUID generateOfflineUuid(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public @NotNull String toString() {
        return "login-offline-panel";
    }
}
