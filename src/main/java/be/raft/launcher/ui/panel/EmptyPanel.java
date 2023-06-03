package be.raft.launcher.ui.panel;

import org.jetbrains.annotations.NotNull;

public class EmptyPanel extends Panel{
    private final String cssId;

    public EmptyPanel(String cssId) {
        this.cssId = cssId;
    }

    @Override
    public void init() {
        //Is Empty
    }

    @Override
    public @NotNull String toString() {
        return cssId;
    }
}
