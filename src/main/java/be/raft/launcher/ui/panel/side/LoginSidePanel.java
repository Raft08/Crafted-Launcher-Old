package be.raft.launcher.ui.panel.side;

import be.raft.launcher.game.login.LoginOption;
import be.raft.launcher.game.login.MicrosoftLoginOption;
import be.raft.launcher.game.login.OfflineLoginOption;
import be.raft.launcher.resources.Text;
import be.raft.launcher.ui.Placing;
import be.raft.launcher.ui.panel.Panel;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class LoginSidePanel extends Panel {
    @Override
    public void init() {
        //Title Box
        GridPane titleBox = new GridPane();

        Placing.setCanTakeAllSize(titleBox);
        Placing.setTop(titleBox);
        Placing.setCenterH(titleBox);

        Label title = new Label(Text.translated("label.login.title"));
        title.setId("login-title");

        Placing.setCanTakeAllSize(title);
        Placing.setCenterV(title);
        Placing.setCenterH(title);

        titleBox.getChildren().add(title);

        //Side Menu
        VBox sideMenu = new VBox();

        ToggleGroup sideMenuToggle = new ToggleGroup();

        for (LoginOption loginOption : loginOptions) {
            ToggleButton option = new ToggleButton(Text.translated(loginOption.getTranslationKey()));
            option.getStyleClass().add("side-menu-element");
            option.setToggleGroup(sideMenuToggle);

            option.setDisable(true);

            loginOption.isAvailable().thenAccept(available -> Platform.runLater(() -> option.setDisable(!available)));

            Placing.setCanTakeAllSize(option);

            option.setOnMouseClicked(event -> this.uiManager.setMainPane(loginOption.getPanel()));

            sideMenu.getChildren().add(option);
        }

        ImageView selector = new ImageView(this.uiManager.getLauncher().getTheme().getImage("icon/selector.png"));
        selector.setFitWidth(15);
        selector.setFitHeight(45);

        //Toggle group
        sideMenuToggle.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ToggleButton newOption = (ToggleButton) newValue;
                newOption.setGraphic(selector);
            }

            if (oldValue != null) {
                ToggleButton oldOption = (ToggleButton) oldValue;
                oldOption.setGraphic(null);
            }

            if (oldValue != null && newValue == null) {
                // If the user attempts to unselect the current option, revert the selection
                sideMenuToggle.selectToggle(oldValue);
            }
        });

        //Placing
        RowConstraints titleBoxConstraints = new RowConstraints();
        titleBoxConstraints.setMinHeight(250);
        titleBoxConstraints.setMaxHeight(250);

        this.layout.getRowConstraints().addAll(titleBoxConstraints, new RowConstraints());
        this.layout.add(titleBox, 0, 0);
        this.layout.add(sideMenu, 0, 1);
    }

    private final List<LoginOption> loginOptions = Arrays.asList(new MicrosoftLoginOption(), new OfflineLoginOption());

    @Override
    public @NotNull String toString() {
        return "login-side-panel";
    }
}
