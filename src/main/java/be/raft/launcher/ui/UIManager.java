package be.raft.launcher.ui;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.resources.theme.Theme;
import be.raft.launcher.ui.panel.EmptyPanel;
import be.raft.launcher.ui.panel.Panel;
import be.raft.launcher.ui.panel.main.WelcomePanel;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class UIManager extends Application {
    private final CraftedLauncher launcher;
    private final GridPane layout;
    private final GridPane sideBar;
    private final GridPane mainPane;
    private Stage stage;

    public UIManager() {
        this.launcher = CraftedLauncher.instance;
        this.layout = new GridPane();
        this.sideBar = new GridPane();
        this.mainPane = new GridPane();

        this.launcher.setUiManager(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        CraftedLauncher.logger.info("Initializing main window..");
        this.stage = stage;

        //Initialize the window
        this.initWindow();
        this.stage.show();

        if (!this.launcher.getSettingsManager().getSettings().has("firstLaunch")) {
            this.launcher.getSettingsManager().getSettings().addProperty("firstLaunch", true);
            this.launcher.getSettingsManager().save();
        }

        if (this.launcher.getSettingsManager().getSettings().get("firstLaunch").getAsBoolean()) {
            this.setMainPane(new WelcomePanel());
            this.setSideBar(new EmptyPanel("welcome-side-panel"));
        } else {
            //TODO: Create login page
        }
    }

    public void initWindow() {
        this.stage.setTitle("Crafted Launcher");
        this.stage.setMinWidth(1280);
        this.stage.setMinHeight(720);
        this.stage.setWidth(1280);
        this.stage.setHeight(720);
        this.stage.centerOnScreen();

        Scene scene = new Scene(this.layout);
        scene.getStylesheets().add(this.launcher.getTheme().getStyleSheet());

        this.stage.setScene(scene);

        //Setup UI Main layout
        Placing.setCanTakeAllSize(this.layout);
        this.layout.setId("launcher-layout");
        this.layout.setBackground(new Background(new BackgroundImage(this.launcher.getTheme().getBackground(),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(1.0, 1.0, true, true, false, true))));

        this.layout.getStylesheets().add(this.launcher.getTheme().getStyleSheet());

        //Column Constraints for the sidebar
        ColumnConstraints sidebarConstraints = new ColumnConstraints();
        sidebarConstraints.setMinWidth(350);
        sidebarConstraints.setMaxWidth(350);

        this.layout.getColumnConstraints().addAll(sidebarConstraints, new ColumnConstraints());

        this.layout.add(this.sideBar, 0, 0);
        Placing.setCanTakeAllSize(this.sideBar);

        this.layout.add(this.mainPane, 1, 0);
        Placing.setCanTakeAllSize(this.mainPane);
    }

    public void setMainPane(Panel panel) {
        this.mainPane.getChildren().clear();
        this.mainPane.getChildren().add(panel.getLayout());

        panel.getLayout().setId(panel.toString());
        panel.init();
        panel.onShow();
    }

    public void setSideBar(Panel panel) {
        this.sideBar.getChildren().clear();
        this.sideBar.getChildren().add(panel.getLayout());

        panel.getLayout().setId(panel.toString());
        panel.init();
        panel.onShow();
    }

    public CraftedLauncher getLauncher() {
        return launcher;
    }
    public Theme getTheme() {
        return launcher.getTheme();
    }

    public Stage getStage() {
        return stage;
    }
}
