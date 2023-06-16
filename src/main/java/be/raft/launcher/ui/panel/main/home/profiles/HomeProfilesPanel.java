package be.raft.launcher.ui.panel.main.home.profiles;

import be.raft.launcher.game.profiles.Profile;
import be.raft.launcher.resources.Text;
import be.raft.launcher.ui.Placing;
import be.raft.launcher.ui.panel.main.home.HomeSideMenuEntry;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeProfilesPanel extends HomeSideMenuEntry {

    private List<Runnable> onHide = new ArrayList<>();

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

        for (Profile profile : this.uiManager.getLauncher().getAvailableProfiles()) {
            GridPane profilePanel = new GridPane();
            profilePanel.setId("home-profiles-profile");

            //Icon

            ImageView icon = new ImageView(this.uiManager.getTheme().getImage("icon/profile_icon.png"));
            icon.setId("home-profiles-profile-icon");
            icon.setFitHeight(175);
            icon.setFitWidth(175);

            // Round Icon
            Rectangle clip = new Rectangle(icon.getFitWidth(), icon.getFitHeight());
            clip.setArcWidth(10);
            clip.setArcHeight(10);
            icon.setClip(clip);

            Placing.setCanTakeAllSize(icon);
            Placing.setTop(icon);
            Placing.setCenterH(icon);

            profilePanel.getChildren().add(icon);

            //Name
            Label name = new Label(profile.getName());
            name.setId("home-profiles-profile-name");

            Placing.setCanTakeAllSize(name);
            Placing.setBottom(name);

            profilePanel.getChildren().add(name);

            //Author
            Label author = new Label(Text.translated("label.home.profiles", profile.getAuthor()));
            author.setId("home-profiles-profile-author");

            Placing.setCanTakeAllSize(author);
            Placing.setBottom(author);

            profilePanel.getChildren().add(author);

            //Progress Bar
            ProgressBar installProgress = new ProgressBar();
            installProgress.setId("home-profiles-profile-progress");

            Placing.setCanTakeAllSize(installProgress);
            Placing.setBottom(installProgress);

            profilePanel.getChildren().add(installProgress);

            //Progress Bar: Percentage
            Label progressPercentage = new Label("0%");
            progressPercentage.setId("home-profiles-profile-progress-percentage");

            Placing.setCanTakeAllSize(progressPercentage);
            Placing.setBottom(progressPercentage);
            Placing.setRight(progressPercentage);

            profilePanel.getChildren().add(progressPercentage);

            profilesHolder.getChildren().add(profilePanel);

            //Dynamic code
            if (profile.isPlayable()) {
                progressPercentage.setVisible(false);
                installProgress.setVisible(false);
            } else {
                Task<Void> progressUpdaterTask = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        profile.addProgressListener((step, progress) -> {
                            if (progress < 0) {
                                this.updateMessage(Text.translated("label.failed"));
                                return;
                            }

                            if (profile.isPlayable()) {
                                progressPercentage.setVisible(false);
                                installProgress.setVisible(false);

                                this.updateMessage(Text.translated("label.home.profiles", profile.getAuthor()));
                                return;
                            }

                            this.updateMessage(Text.translated(step));
                            this.updateProgress(progress, 100);
                        });
                        return null;
                    }
                };

                installProgress.progressProperty().bind(progressUpdaterTask.progressProperty());
                progressPercentage.textProperty().bind(progressUpdaterTask.progressProperty().multiply(100).asString("%.0f%%"));
                author.textProperty().bind(progressUpdaterTask.messageProperty());

                Thread progressUpdaterThread = new Thread(progressUpdaterTask, "progressUpdater-" + profile.getName());
                progressUpdaterThread.setDaemon(true);
                progressUpdaterThread.start();
            }
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
