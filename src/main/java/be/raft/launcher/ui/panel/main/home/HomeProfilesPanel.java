package be.raft.launcher.ui.panel.main.home;

import be.raft.launcher.ui.Placing;
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

        //Profiles holder
        FlowPane profilesHolder = new FlowPane();
        profilesHolder.setId("home-profiles-holder");

        Placing.setCanTakeAllSize(profilesHolder);

        ScrollPane scrollPane = new ScrollPane(profilesHolder);

        Placing.setCanTakeAllSize(scrollPane);

        for (int i = 0; i < 200; i++) {
            GridPane profile = new GridPane();
            profile.setId("home-profiles-profile");

            Placing.setCanTakeAllSize(profile);
            Placing.setTop(profile);
            Placing.setLeft(profile);

            profilesHolder.getChildren().add(profile);
        }

        //Row Constrains
        RowConstraints topBarConstraints = new RowConstraints();
        topBarConstraints.setMaxHeight(50);
        topBarConstraints.setMinHeight(50);

        this.layout.getRowConstraints().addAll(topBarConstraints, new RowConstraints());
        this.layout.add(topBar, 0, 0);
        this.layout.add(scrollPane, 0, 1);
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
