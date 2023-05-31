package be.raft.launcher.ui.panel;

import be.raft.launcher.resources.Text;
import be.raft.launcher.ui.Placing;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;

public class WelcomePanel extends Panel{
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
    }

    @Override
    public @NotNull String toString() {
        return "welcome-panel";
    }
}
