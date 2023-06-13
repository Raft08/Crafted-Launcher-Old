package be.raft.launcher.ui.panel.side;

import be.raft.launcher.resources.Text;
import be.raft.launcher.ui.Placing;
import be.raft.launcher.ui.panel.Panel;
import be.raft.launcher.ui.panel.main.home.profiles.HomeProfilesPanel;
import be.raft.launcher.ui.panel.main.home.HomeSideMenuEntry;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HomeSidePanel extends Panel {
    private static final List<HomeSideMenuEntry> SIDE_MENU_ENTRIES = List.of(new HomeProfilesPanel());

    @Override
    public void init() {
        //Title Box
        GridPane titleBox = new GridPane();

        Placing.setCanTakeAllSize(titleBox);
        Placing.setTop(titleBox);
        Placing.setCenterH(titleBox);

        Label title = new Label(Text.translated("label.home.title"));
        title.setId("home-title");

        Placing.setCanTakeAllSize(title);
        Placing.setCenterV(title);
        Placing.setCenterH(title);

        titleBox.getChildren().add(title);

        //Side Menu
        VBox sideMenu = new VBox();

        //Selector
        ImageView selector = new ImageView(this.uiManager.getLauncher().getTheme().getImage("icon/selector.png"));
        selector.setFitWidth(15);
        selector.setFitHeight(45);

        //Toggle group
        ToggleGroup sideMenuToggle = new ToggleGroup();
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

        for (HomeSideMenuEntry entry : SIDE_MENU_ENTRIES) {
            ToggleButton option = new ToggleButton(Text.translated(entry.getTranslationKey()));
            option.getStyleClass().add("side-menu-element");
            option.setToggleGroup(sideMenuToggle);

            Placing.setCanTakeAllSize(option);

            option.setOnMouseClicked(event -> {
                entry.getLayout().getChildren().clear();
                entry.getLayout().getRowConstraints().clear();
                entry.getLayout().getColumnConstraints().clear();
                this.uiManager.setMainPane(entry);
            });

            sideMenu.getChildren().add(option);

            if (SIDE_MENU_ENTRIES.get(0).equals(entry)) {
                this.uiManager.setMainPane(entry);
                sideMenuToggle.selectToggle(option);
            }
        }

        //Placing
        RowConstraints titleBoxConstraints = new RowConstraints();
        titleBoxConstraints.setMinHeight(250);
        titleBoxConstraints.setMaxHeight(250);

        this.layout.getRowConstraints().addAll(titleBoxConstraints, new RowConstraints());
        this.layout.add(titleBox, 0, 0);
        this.layout.add(sideMenu, 0, 1);
    }

    @Override
    public @NotNull String toString() {
        return "home-side-panel";
    }
}
