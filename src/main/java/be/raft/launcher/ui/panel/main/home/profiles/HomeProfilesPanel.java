package be.raft.launcher.ui.panel.main.home.profiles;

import be.raft.launcher.resources.Text;
import be.raft.launcher.ui.Placing;
import be.raft.launcher.ui.panel.main.home.HomeSideMenuEntry;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.jetbrains.annotations.NotNull;

public class HomeProfilesPanel extends HomeSideMenuEntry {

    @Override
    public void init() {
        //Top bar
        GridPane topBar = new GridPane();
        topBar.setId("home-profiles-top-bar");

        Placing.setCanTakeAllSize(topBar);

        Label createNewProfileBtn = new Label(Text.translated("btn.home.profiles.create"));
        createNewProfileBtn.getStyleClass().add("clickable-label");
        createNewProfileBtn.setId("home-profiles-create-profile");

        Placing.setCanTakeAllSize(createNewProfileBtn);
        Placing.setRight(createNewProfileBtn);
        Placing.setCenterV(createNewProfileBtn);

        topBar.getChildren().add(createNewProfileBtn);

        //Content Pane
        GridPane contentPane = new GridPane();

        Placing.setCanTakeAllSize(contentPane);

        FlowPane profilesHolder = new FlowPane();
        profilesHolder.setId("home-profiles-holder");

        Placing.setCanTakeAllSize(profilesHolder);

        for (int i = 0; i < 48; i++) {
            GridPane gridPane = new GridPane();
            gridPane.setId("home-profiles-profile");

            Label name = new Label("Profile #" + i);

            gridPane.getChildren().add(name);

            profilesHolder.getChildren().add(gridPane);
        }

        ScrollPane scrollPane = new ScrollPane(profilesHolder);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Placing.setCanTakeAllSize(scrollPane);

        contentPane.getChildren().add(scrollPane);

        //Row Constrains
        RowConstraints topBarConstraints = new RowConstraints();
        topBarConstraints.setMaxHeight(50);
        topBarConstraints.setMinHeight(50);

        this.layout.getRowConstraints().addAll(topBarConstraints, new RowConstraints());
        this.layout.add(topBar, 0, 0);
        this.layout.add(contentPane, 0, 1);

        //Dynamic Code
        createNewProfileBtn.setOnMouseClicked(event -> {
            this.uiManager.setMainPane(new HomeProfileCreationPanel());
        });
    }


    @Override
    public @NotNull String toString() {
        return "home-profiles-panel";
    }

    @Override
    public @NotNull String getTranslationKey() {
        return "sidebar_entry.home.profiles";
    }
}
