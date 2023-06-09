package be.raft.launcher.ui.panel.main.home;

import be.raft.launcher.ui.panel.Panel;
import org.jetbrains.annotations.NotNull;

public abstract class HomeSideMenuEntry extends Panel {
    @NotNull
    public abstract String getTranslationKey();
}
