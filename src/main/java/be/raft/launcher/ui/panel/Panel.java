package be.raft.launcher.ui.panel;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.ui.Placing;
import be.raft.launcher.ui.UIManager;
import javafx.animation.FadeTransition;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

public abstract class Panel {
    protected final GridPane layout;
    protected UIManager uiManager;

    public Panel() {
        this.layout = new GridPane();
        Placing.setCanTakeAllSize(this.layout);
        this.uiManager = CraftedLauncher.instance.getUIManager();
    }

    public abstract void init();

    public void onShow() {
        FadeTransition transition = new FadeTransition(Duration.seconds(1), this.layout);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.setAutoReverse(true);
        transition.play();
        CraftedLauncher.logger.debug("Showing panel '{}'", this);
    }

    public void onHide() {
    }

    public GridPane getLayout() {
        return layout;
    }

    public UIManager getUiManager() {
        return uiManager;
    }

    @NotNull
    @Override
    public abstract String toString();
}
