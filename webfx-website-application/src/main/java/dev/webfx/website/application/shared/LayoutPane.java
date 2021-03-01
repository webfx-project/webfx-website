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
    private int layoutCount;

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
        if (force || lastWidth != width || lastHeight != height) {
            layoutCount++;
            //  if (layoutCount > 1) dev.webfx.platform.shared.services.log.Logger.log("Layout count = " + layoutCount + " for " + getClass() + (lastWidth == width ? "" : ", width: " + lastWidth + " -> " + width) + (lastHeight == height ? "" : ", height: " + lastHeight + " -> " + height) + (!force ? "" : ", force: true"));
            layoutChildren(width, height);
            lastWidth = width;
            lastHeight = height;
            force = false;
        }
    }

    protected abstract void layoutChildren(double width, double height);

    public void forceLayoutChildren() {
        force = true;
        if (layoutCount >= 1) // Startup optimisation: no need to schedule another layout if the first one is still not yet done
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
