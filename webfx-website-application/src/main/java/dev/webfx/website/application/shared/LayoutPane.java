package dev.webfx.website.application.shared;

import dev.webfx.platform.client.services.uischeduler.AnimationFramePass;
import dev.webfx.platform.client.services.uischeduler.UiScheduler;
import javafx.collections.ListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * @author Bruno Salmon
 */
public abstract class LayoutPane extends Pane {

    private double lastWidth, lastHeight;
    private boolean force;

    public LayoutPane() {
    }

    public LayoutPane(Node... children) {
        super(children);
    }

    {
        getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> change) {
                force = true;
            }
        });
    }

    @Override
    protected void layoutChildren() {
        double width = getWidth(), height = getHeight();
        if (force || lastWidth != width || lastHeight != height)
            layoutChildren(width, height);
        lastWidth = width;
        lastHeight = height;
    }

    protected abstract void layoutChildren(double width, double height);

    public void forceLayoutChildren() {
        force = true;
        UiScheduler.scheduleInAnimationFrame(this::layoutChildren, AnimationFramePass.SCENE_PULSE_LAYOUT_PASS);
    }

    protected void layoutInArea(Node child, double areaX, double areaY, double areaWidth, double areaHeight) {
        layoutInArea(child, areaX, areaY, areaWidth, areaHeight, 0, Insets.EMPTY, HPos.LEFT, VPos.TOP);
    }

    protected void leftInArea(Node child, double areaX, double areaY, double areaWidth, double areaHeight) {
        layoutInArea(child, areaX, areaY, areaWidth, areaHeight, 0, Insets.EMPTY, HPos.LEFT, VPos.CENTER);
    }

    protected void centerInArea(Node child, double areaX, double areaY, double areaWidth, double areaHeight) {
        layoutInArea(child, areaX, areaY, areaWidth, areaHeight, 0, Insets.EMPTY, HPos.CENTER, VPos.CENTER);
    }

    protected void bottomInArea(Node child, double areaX, double areaY, double areaWidth, double areaHeight) {
        layoutInArea(child, areaX, areaY, areaWidth, areaHeight, 0, Insets.EMPTY, HPos.CENTER, VPos.BOTTOM);
    }
}
