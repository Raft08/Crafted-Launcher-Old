package be.raft.launcher.ui;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class Placing {
    public static void setCanTakeAllSize(Node node) {
        GridPane.setHgrow(node, Priority.ALWAYS);
        GridPane.setVgrow(node, Priority.ALWAYS);
    }

    public static void setCanTakeAllWidth(Node node) {
        GridPane.setHgrow(node, Priority.ALWAYS);
    }

    public static void setCanTakeAllHeight(Node node) {
        GridPane.setVgrow(node, Priority.ALWAYS);
    }

    public static void setLeft(Node node) {
        GridPane.setHalignment(node, HPos.LEFT);
    }

    public static void setRight(Node node) {
        GridPane.setHalignment(node, HPos.RIGHT);
    }

    public static void setTop(Node node) {
        GridPane.setValignment(node, VPos.TOP);
    }

    public static void setBottom(Node node) {
        GridPane.setValignment(node, VPos.BOTTOM);
    }

    public static void setBaseLine(Node node) {
        GridPane.setValignment(node, VPos.BASELINE);
    }

    public static void setCenterH(Node node) {
        GridPane.setHalignment(node, HPos.CENTER);
    }

    public static void setCenterV(Node node) {
        GridPane.setValignment(node, VPos.CENTER);
    }
}
