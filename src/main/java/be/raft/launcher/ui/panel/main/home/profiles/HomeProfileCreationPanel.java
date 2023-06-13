package be.raft.launcher.ui.panel.main.home.profiles;

import be.raft.launcher.file.GameFileManager;
import be.raft.launcher.game.mojang.MojangFileManager;
import be.raft.launcher.game.profiles.ProfileManager;
import be.raft.launcher.resources.Text;
import be.raft.launcher.ui.Placing;
import be.raft.launcher.ui.panel.Panel;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HomeProfileCreationPanel extends Panel {
    @Override
    public void init() {
        CompletableFuture<List<String>> versionsFuture = MojangFileManager.getVersion(false);

        //Base Pane and scroll pane
        VBox basePane = new VBox();
        basePane.setId("home-profiles-creation-base-pane");

        Placing.setCanTakeAllSize(basePane);

        ScrollPane scrollPane = new ScrollPane(basePane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Placing.setCanTakeAllSize(scrollPane);

        this.layout.getChildren().add(scrollPane);

        //Title
        Label title = new Label(Text.translated("label.home.profiles.creation.title"));
        title.setId("home-profiles-creation-title");

        Placing.setCanTakeAllSize(title);

        basePane.getChildren().add(title);

        //Configuration Blocks
        //Core Configuration
        GridPane basicConfigurationBlock = createConfigurationBlock("label.home.profiles.creation.config_block.core_configuration.title");
        basicConfigurationBlock.setId("home-profiles-creation-core-block");

        //Core Configuration: Name Field Title
        Label nameFieldTitle = new Label(Text.translated("label.home.profiles.creation.config_block.core_configuration.profile_name"));
        nameFieldTitle.setId("home-profiles-creation-core-block-name-field-title");

        Placing.setCanTakeAllSize(nameFieldTitle);
        Placing.setTop(nameFieldTitle);

        basicConfigurationBlock.getChildren().add(nameFieldTitle);

        //Core Configuration: Name Field Error
        Label nameFieldError = new Label();
        nameFieldError.setVisible(false);
        nameFieldError.setId("home-profiles-creation-core-block-name-field-error");

        Placing.setCanTakeAllSize(nameFieldError);
        Placing.setTop(nameFieldError);
        Placing.setRight(nameFieldError);

        basicConfigurationBlock.getChildren().add(nameFieldError);

        //Core Configuration: Name Field
        TextField nameField = new TextField();
        nameField.setPromptText(Text.translated("label.home.profiles.creation.config_block.core_configuration.profile_name"));
        nameField.setId("home-profiles-creation-core-block-name-field");

        Placing.setCanTakeAllSize(nameField);
        Placing.setTop(nameField);

        basicConfigurationBlock.getChildren().add(nameField);

        //Core Configuration: Save Location
        Label profileSaveLocationLabel = new Label();
        profileSaveLocationLabel.setVisible(false);
        profileSaveLocationLabel.setId("home-profiles-creation-core-block-save-location");

        Placing.setCanTakeAllSize(profileSaveLocationLabel);
        Placing.setLeft(profileSaveLocationLabel);

        basicConfigurationBlock.getChildren().add(profileSaveLocationLabel);

        //Core Configuration: Version Selector Title
        Label versionSelectorTitle = new Label(Text.translated("label.home.profiles.creation.config_block.core_configuration.version_selector"));
        versionSelectorTitle.setId("home-profiles-creation-core-block-version-selector-title");

        Placing.setCanTakeAllSize(versionSelectorTitle);

        basicConfigurationBlock.getChildren().add(versionSelectorTitle);

        //Core Configuration: Show Snapshots
        CheckBox showSnapshots = new CheckBox(Text.translated("label.home.profiles.creation.config_block.core_configuration.show_snapshots"));
        showSnapshots.setId("home-profiles-creation-core-block-version-selector-show-snapshot");

        Placing.setCanTakeAllSize(showSnapshots);
        Placing.setRight(showSnapshots);

        basicConfigurationBlock.getChildren().add(showSnapshots);

        //Core Configuration: Version Selector
        ComboBox<String> versionSelector = new ComboBox<>();
        versionSelector.setId("home-profiles-creation-core-block-version-selector");

        Placing.setCanTakeAllSize(versionSelector);

        List<String> versions = versionsFuture.join(); //Join the future, if finished the value will be directly given, else thread will wait
        if (versions == null) {
            versionSelector.setItems(FXCollections.observableList(List.of(Text.translated("label.failed"))));
            versionSelector.setDisable(true);
        } else {
            versionSelector.setItems(FXCollections.observableList(versions));
            versionSelector.setValue(versions.get(0));
        }

        basicConfigurationBlock.getChildren().add(versionSelector);


        basePane.getChildren().add(basicConfigurationBlock);

        //Dynamic Code
        Translate errorFieldTranslate = new Translate();
        nameFieldError.getTransforms().add(errorFieldTranslate);

        Timeline errorFieldAnim = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(errorFieldTranslate.xProperty(), 0)),
                new KeyFrame(Duration.millis(100), new KeyValue(errorFieldTranslate.xProperty(), -10)),
                new KeyFrame(Duration.millis(200), new KeyValue(errorFieldTranslate.xProperty(), 10)),
                new KeyFrame(Duration.millis(300), new KeyValue(errorFieldTranslate.xProperty(), -10)),
                new KeyFrame(Duration.millis(400), new KeyValue(errorFieldTranslate.xProperty(), 10)),
                new KeyFrame(Duration.millis(500), new KeyValue(errorFieldTranslate.xProperty(), 0))
        );

        //Basic Configuration
        nameField.textProperty().addListener(observable -> {
            String profileLocation = GameFileManager.getFileInGameDirectory(ProfileManager.PROFILE_LOCATION).getAbsolutePath() + "\\";

            profileSaveLocationLabel.setText(Text.translated("label.home.profiles.creation.config_block.core_configuration.save_location",
                    profileLocation + nameField.getText()));
            profileSaveLocationLabel.setVisible(true);

            //Error
            if (nameField.getText().isEmpty()) {
                nameFieldError.setText("*" + Text.translated("label.required"));
                nameFieldError.setVisible(true);
                errorFieldAnim.play();
                return;
            }

            if (nameField.getText().contains("\\") || nameField.getText().contains("/")) {
                nameFieldError.setText("*" + Text.translated("label.invalid"));
                nameFieldError.setVisible(true);
                errorFieldAnim.play();
                return;
            }

            nameFieldError.setVisible(false);
        });

        showSnapshots.selectedProperty().addListener(observable -> {
            List<String> updatedVersion = MojangFileManager.getVersion(showSnapshots.isSelected()).join();

            if(updatedVersion == null) {
                versionSelector.setItems(FXCollections.observableList(List.of(Text.translated("label.failed"))));
                versionSelector.setDisable(true);
                return;
            }

            versionSelector.setItems(FXCollections.observableList(updatedVersion));
            versionSelector.setValue(updatedVersion.get(0));
        });
    }

    private GridPane createConfigurationBlock(String name) {
        //Base Pane
        GridPane configBlock = new GridPane();
        configBlock.getStyleClass().add("home-profiles-creation-config-block");

        Placing.setCanTakeAllSize(configBlock);

        //Title
        Label title = new Label(Text.translated(name));
        title.setId("home-profiles-creation-config-block-title");

        Placing.setCanTakeAllSize(title);
        Placing.setTop(title);

        configBlock.getChildren().add(title);

        return configBlock;
    }

    @Override
    public @NotNull String toString() {
        return "home-profiles-creation-panel";
    }
}
