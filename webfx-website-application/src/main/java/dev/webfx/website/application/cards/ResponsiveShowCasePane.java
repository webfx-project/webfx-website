package dev.webfx.website.application.cards;

import dev.webfx.website.application.shared.WebSiteShared;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * @author Bruno Salmon
 */
final class ResponsiveShowCasePane extends Pane {

    private final Region sandRegion   = createColorRegion(Color.web("#f08a5d"));
    private final Region pinkRegion   = createColorRegion(Color.web("#b83b5e"));
    private final Region greenRegion  = createColorRegion(Color.web("#158467"));
    private final Region orangeRegion = createColorRegion(Color.web("#ff5722"));
    private final Region yellowRegion = createColorRegion(Color.ORANGE);

    public ResponsiveShowCasePane() {
        getChildren().setAll(sandRegion, pinkRegion, greenRegion, yellowRegion, orangeRegion);
    }

    @Override
    protected void layoutChildren() {
        // Getting the width and height of this container
        double width = getWidth(), height = getHeight();
        // Sand region x, y, w, h => by default: left part of top
        double sx = 0, sy = 0, sw = width / 2, sh = 50; // 50px height
        // Pink region x, y, w, h => by default: right part of top
        double px = sw, py = sy, pw = width - sw, ph = sh;
        // But if the sand region becomes too small, we stack the 2 regions vertically
        if (sw < 50) { // Let's say 50px is the minimal sand width
            sw = pw = width; // Finally allocating whole width for both regions
            px = 0; py = sy + sh; // And moving the pink region below the sand region
        }
        double rh = height - py - ph; // Remaining height (under the pink region)
        // Green region x, y, w, h => by default: upper part of remaining space
        double gx = 0, gy = py + ph, gw = width, gh = rh / 2;
        // Orange region x, y, w, h => by default: central part of remaining space (1/4 of surface)
        double ow = Math.sqrt(rh * width) / 2, oh = ow, ox = width / 2 - ow / 2, oy = gy + rh / 2 - oh / 2;
        // Yellow region x, y, w, h => by default: lower part of remaining space
        double yx = gx, yy = gy + gh, yw = gw, yh = gh;
        if (width / height >= 1) {
            gw = ow = yw = width / 3;
            gh = oh = yh = rh;
            ox = gx + gw;
            yx = ox + ow;
            oy = yy = gy;
        } else if (width / height < 0.72 ) {
            gw = ow = yw = width;
            oh = 0.72 / 4 * rh * rh / ow;
            gx = ox = yx = 0;
            oy = gy + gh; yy = oy + oh;
        }
        // Finally applying the layout with all the above calculations
        layoutInArea(sandRegion,   sx, sy, sw, sh, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(pinkRegion,   px, py, pw, ph, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(greenRegion,  gx, gy, gw, gh, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(orangeRegion, ox, oy, ow, oh, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(yellowRegion, yx, yy, yw, yh, 0, HPos.CENTER, VPos.CENTER);
    }

    private static Region createColorRegion(Color color) {
        return WebSiteShared.setRegionBackground(new Region(), color);
    }

    static SplitPane createSplitPanes() {
        SplitPane horizontalSplitPane = new SplitPane(new ResponsiveShowCasePane(), new ResponsiveShowCasePane());
        horizontalSplitPane.setBackground(null);
        SplitPane verticalSplitPane = new SplitPane(new ResponsiveShowCasePane(), horizontalSplitPane);
        verticalSplitPane.setOrientation(Orientation.VERTICAL);
        verticalSplitPane.setBackground(null);
        return verticalSplitPane;
    }
}
