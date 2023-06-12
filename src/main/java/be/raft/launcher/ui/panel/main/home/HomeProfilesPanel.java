package be.raft.launcher.ui.panel.main.home;

import be.raft.launcher.ui.Placing;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class HomeProfilesPanel extends HomeSideMenuEntry {
    @Override
    public void init() {
        //Top bar
        GridPane topBar = new GridPane();
        topBar.setId("home-profiles-top-bar");

        Placing.setCanTakeAllSize(topBar);

        GridPane contentPane = new GridPane();

        Placing.setCanTakeAllSize(contentPane);

        FlowPane profilesHolder = new FlowPane();
        profilesHolder.setId("home-profiles-holder");

        Placing.setCanTakeAllSize(profilesHolder);
        Placing.setCenterH(profilesHolder);

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
