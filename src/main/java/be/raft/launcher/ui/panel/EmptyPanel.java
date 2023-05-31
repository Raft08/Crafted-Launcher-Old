package be.raft.launcher.ui.panel;

import org.jetbrains.annotations.NotNull;

public class EmptyPanel extends Panel{
    @Override
    public void init() {
        //Is Empty
    }

    @Override
    public @NotNull String toString() {
        return "empty-panel";
    }
}
