package dev.webfx.website.application.demos;

import dev.webfx.website.application.WebSiteShared;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

import static dev.webfx.website.application.demos.DemoThumbnail.DemoCategory.*;

/**
 * @author Bruno Salmon
 */
public final class DemoThumbnailsPane extends Pane {


    public DemoThumbnailsPane() {
        super(
                new DemoThumbnail("Colorful circles", BASIC,          "ColorfulCircles.png", true,  "https://colorfulcircles.webfx.dev", "https://github.com/webfx-project/webfx-demo-colorfulcircles"),
                new DemoThumbnail("Particles",        BASIC,          "Particles.png",       true,  "https://particles.webfx.dev",       "https://github.com/webfx-project/webfx-demo-particles"),
                new DemoThumbnail("Tally counter",    CUSTOM_CONTROL, "TallyCounter.png",    false, "https://tallycounter.webfx.dev",    "https://github.com/webfx-project/webfx-demo-tallycounter"),
                new DemoThumbnail("Modern gauge",     CUSTOM_CONTROL, "ModernGauge.png",     false, "https://moderngauge.webfx.dev",     "https://github.com/webfx-project/webfx-demo-moderngauge"),
                new DemoThumbnail("Enzo clocks",      CUSTOM_CONTROL, "EnzoClocks.png",      true,  "https://enzoclocks.webfx.dev",      "https://github.com/webfx-project/webfx-demo-enzoclocks"),
                new DemoThumbnail("FX2048",           GAME,           "FX2048.png",          false, "https://fx2048.webfx.dev",          "https://github.com/webfx-project/webfx-demo-fx2048"),
                new DemoThumbnail("SpaceFX",          GAME,           "SpaceFX.png",         false, "https://spacefx.webfx.dev",         "https://github.com/webfx-project/webfx-demo-spacefx"),
                new DemoThumbnail("Ray tracer",       WEB_WORKER,     "RayTracer.png",       true,  "https://raytracer.webfx.dev",       "https://github.com/webfx-project/webfx-demo-raytracer"),
                new DemoThumbnail("Mandelbrot",       WEBASSEMBLY,    "Mandelbrot.png",      true,  "https://mandelbrot.webfx.dev",      "https://github.com/webfx-project/webfx-demo-mandelbrot")
        );
        for (Node child : getChildren())
            WebSiteShared.runOnMouseClick(child, () -> WebSiteShared.openUrl(((DemoThumbnail) child).getDemoLink()));
    }

    @Override
    protected void layoutChildren() {
        double width = getWidth(), height = getHeight();
        int n = getChildren().size();
        int p = (int) Math.sqrt(n);
        int q = n / p;
        if (p * q < n) {
            if (width > height)
                p++;
            else
                q++;
        }
        double gap = 0.01 * width;
        Insets margin = new Insets(0, 0, gap, gap);
        double wp = (width - margin.getLeft() - margin.getRight()) / p;
        double hp = (height ) / q;
        ObservableList<Node> children = getChildren();
        for (int i = 0; i < n; i++) {
            Node child = children.get(i);
            int col = i % p, row = i / p;
            layoutInArea(child, col * wp, row * hp, wp, hp, 0, margin, HPos.LEFT, VPos.TOP);
            child.setClip(new Rectangle(0, 0, ((Region) child).getWidth(), ((Region) child).getHeight()));
        }
    }
}
