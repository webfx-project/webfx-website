package dev.webfx.website.application.cards;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * @author Bruno Salmon
 */
class ScalePane extends Pane {

    enum ScaleMode { WIDTH, HEIGHT, WIDTH_OR_HEIGHT}

    private final ScaleMode scaleMode;
    private Node node;
    private boolean canGrow = true, canShrink = true;
    private boolean canScaleX = true, canScaleY = true;
    private boolean alwaysTry = false;

    ScalePane() {
        this((Node) null);
    }

    ScalePane(ScaleMode scaleMode) {
        this(scaleMode, null);
    }

    ScalePane(Node node) {
        this(ScaleMode.WIDTH_OR_HEIGHT, node);
    }

    ScalePane(ScaleMode scaleMode, Node node) {
        this.scaleMode = scaleMode;
        setNode(node);
    }

    void setNode(Node node) {
        this.node = node;
        if (node != null)
            getChildren().setAll(node);
    }

    public void setCanGrow(boolean canGrow) {
        this.canGrow = canGrow;
    }

    public void setCanShrink(boolean canShrink) {
        this.canShrink = canShrink;
    }

    public void setCanScaleX(boolean canScaleX) {
        this.canScaleX = canScaleX;
    }

    public void setCanScaleY(boolean canScaleY) {
        this.canScaleY = canScaleY;
    }

    public void setAlwaysTry(boolean alwaysTry) {
        this.alwaysTry = alwaysTry;
    }

    @Override
    protected void layoutChildren() {
        double width = getWidth();
        double height = getHeight();
        boolean tryRescale = !node.isResizable() || alwaysTry;
        if (!tryRescale && node instanceof Region) {
            Region region = (Region) this.node;
            tryRescale = region.minWidth(height) > width || region.maxWidth(height) < width || region.minHeight(width) > height || region.maxHeight(width) < height;
        }
        if (tryRescale) {
            double w = node.prefWidth(height), h = node.prefHeight(w);
            double scale = scaleMode == ScaleMode.HEIGHT ? height / h : scaleMode == ScaleMode.WIDTH ? width / h : Math.min(height / h, width / h);
            if (scale == 1 || scale < 1 && canShrink || scale > 1 && canGrow) {
                if (canScaleX)
                    node.setScaleX(scale);
                if (canScaleY)
                    node.setScaleY(scale);
            }
        }
        layoutInArea(node, 0, 0, width, height, 0, HPos.CENTER, VPos.CENTER);
    }
}