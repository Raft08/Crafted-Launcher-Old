package be.raft.launcher.ui.panel.main;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.resources.Text;
import be.raft.launcher.ui.Placing;
import be.raft.launcher.ui.panel.EmptyPanel;
import be.raft.launcher.ui.panel.Panel;
import be.raft.launcher.ui.panel.side.LoginSidePanel;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;

public class WelcomePanel extends Panel {
    @Override
    public void init() {
        Label welcomeTitle = new Label(Text.translated("label.welcome.title"));
        welcomeTitle.setId("welcome-title");

        Placing.setCanTakeAllSize(welcomeTitle);
        Placing.setCenterV(welcomeTitle);
        Placing.setCenterH(welcomeTitle);

        Label welcomeMessage = new Label(Text.translated("label.welcome.message"));
        welcomeMessage.setId("welcome-message");
        welcomeMessage.setWrapText(true);

        Placing.setCanTakeAllSize(welcomeMessage);
        Placing.setCenterV(welcomeMessage);
        Placing.setCenterH(welcomeMessage);

        Label acknowledgeButton = new Label(Text.translated("btn.continue"));
        acknowledgeButton.setId("welcome-continue");
        acknowledgeButton.getStyleClass().add("clickable-label");

        Placing.setCanTakeAllSize(acknowledgeButton);
        Placing.setCenterV(acknowledgeButton);
        Placing.setCenterH(acknowledgeButton);

        this.layout.getChildren().addAll(welcomeTitle, welcomeMessage, acknowledgeButton);

        //Events
        acknowledgeButton.setOnMouseClicked(event -> {
            if (!CraftedLauncher.devEnv) {
                this.uiManager.getLauncher().getSettingsManager().setBoolean("firstLaunch", false);
                this.uiManager.getLauncher().getSettingsManager().save();
            }

            this.uiManager.setSideBar(new LoginSidePanel());
            this.uiManager.setMainPane(new EmptyPanel("login-empty-panel"));
        });
    }

    @Override
    public @NotNull String toString() {
        return "welcome-panel";
    }
}
