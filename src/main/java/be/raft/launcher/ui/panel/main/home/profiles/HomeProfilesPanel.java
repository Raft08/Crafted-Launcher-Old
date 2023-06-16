package be.raft.launcher.ui.panel.main.home.profiles;

import be.raft.launcher.game.profiles.Profile;
import be.raft.launcher.resources.Text;
import be.raft.launcher.ui.Placing;
import be.raft.launcher.ui.panel.main.home.HomeSideMenuEntry;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeProfilesPanel extends HomeSideMenuEntry {

    private List<Runnable> onHide = new ArrayList<>();

    @Override
    public void init() {
        //FOR TESTING PURPOSES
        Profile dummyProfile = new Profile("Dummy", new File("DUMMY"),
                "1.0.0", "DUMMIES", "FOR TESTING PURPOSES", "1.20.1");

        dummyProfile.setPlayable(true);

        this.uiManager.getLauncher().getAvailableProfiles().add(dummyProfile);

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

        for (Profile profile : this.uiManager.getLauncher().getAvailableProfiles()) {
            GridPane profilePanel = new GridPane();
            profilePanel.setId("home-profiles-profile");

            //Icon
            ImageView icon = new ImageView(this.uiManager.getTheme().getImage("icon/profile_icon.png"));
            icon.setId("home-profiles-profile-icon");
            icon.setFitHeight(200);
            icon.setFitWidth(200);

            Placing.setCanTakeAllSize(icon);
            Placing.setTop(icon);
            Placing.setCenterH(icon);

            profilePanel.getChildren().add(icon);

            //Name
            Label name = new Label();
            name.setId("home-profiles-profile-name");

            Placing.setCanTakeAllSize(name);
            Placing.setBottom(name);
            Placing.setCenterH(name);

            profilePanel.getChildren().add(name);

            if (profile.isPlayable()) {
                name.setText(profile.getName());
            } else {
                //Edit name
                name.setId("home-profiles-profile-download");
                name.setWrapText(true);

                //Progress
                ProgressBar progressBar = new ProgressBar();
                progressBar.setId("home-profiles-profile-progress");

                Placing.setCanTakeAllSize(progressBar);
                Placing.setBottom(progressBar);
                Placing.setCenterH(progressBar);

                profilePanel.getChildren().add(progressBar);

                //Data
                profile.addProgressListener((step, progress) -> {
                    Platform.runLater(() -> {
                        if (progress > -1) {
                            if (!profile.isPlayable()) {
                                name.setText(Text.translated(step));
                                progressBar.setProgress(progress);
                            } else {
                                //Reload
                                this.uiManager.setMainPane(new HomeProfilesPanel());
                            }
                        } else {
                            name.setText(Text.translated("label.failed"));
                            profilePanel.getChildren().remove(progressBar);
                        }
                    });
                });

                this.onHide.add(profile::clearProgressListeners);
            }

            profilesHolder.getChildren().add(profilePanel);
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
    public void onHide() {
        this.onHide.forEach(Runnable::run);
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
