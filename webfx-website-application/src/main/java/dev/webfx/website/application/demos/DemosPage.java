package dev.webfx.website.application.demos;

import dev.webfx.extras.scalepane.ScaleMode;
import dev.webfx.website.application.shared.WebSiteShared;
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
public final class DemosPage extends Pane {

    private final static DemoThumbnail
            colorfulCircles = new DemoThumbnail("Colorful Circles", BASIC, ScaleMode.BEST_ZOOM), // "ColorfulCircles.png"), // "https://colorfulcircles.webfx.dev"), "https://github.com/webfx-demos/webfx-demo-colorfulcircles/blob/main/webfx-demo-colorfulcircles-application/src/main/java/dev/webfx/demo/colorfulcircles/ColorfulCircles.java"),
            tallyCounter    = new DemoThumbnail("Tally Counter", CUSTOM_CONTROL, ScaleMode.BEST_FIT), // "TallyCounter.png"), // "https://tallycounter.webfx.dev"), "https://github.com/webfx-demos/webfx-demo-tallycounter/blob/main/webfx-demo-tallycounter-application/src/main/java/dev/webfx/demo/tallycounter/TallyCounterApplication.java"),
            modernGauge     = new DemoThumbnail("Modern Gauge", CUSTOM_CONTROL, ScaleMode.BEST_FIT), // "ModernGauge.png"), // "https://moderngauge.webfx.dev"), "https://github.com/webfx-demos/webfx-demo-moderngauge/blob/main/webfx-demo-moderngauge-application/src/main/java/dev/webfx/demo/moderngauge/ModernGaugeApplication.java"),
            enzoClocks      = new DemoThumbnail("Enzo Clocks", CUSTOM_CONTROL, ScaleMode.BEST_FIT), // "EnzoClocks.png"),
            fx2048          = new DemoThumbnail("FX2048", GAME, ScaleMode.BEST_FIT), // "FX2048.png"),
            spaceFX         = new DemoThumbnail("SpaceFX", GAME, ScaleMode.FIT_HEIGHT), // "SpaceFX.png"),
            demoFX          = new DemoThumbnail("DemoFX", ANIMATION, ScaleMode.BEST_ZOOM), // "DemoFX.png"), // "https://demofx.webfx.dev", "https://github.com/webfx-demos/webfx-demo-demofx/blob/main/webfx-demo-demofx-application/src/main/java/dev/webfx/demo/demofx/DemoFXApplication.java"),
            rayTracer       = new DemoThumbnail("Ray Tracer", WEB_WORKER, ScaleMode.BEST_ZOOM), // "RayTracer.png"),
            mandelbrot      = new DemoThumbnail("Mandelbrot", WEBASSEMBLY, ScaleMode.BEST_ZOOM); // "Mandelbrot.png")


    public DemosPage() {
        super(colorfulCircles, tallyCounter, spaceFX, enzoClocks, modernGauge, fx2048, demoFX, rayTracer, mandelbrot); // 9 featured demos
        for (Node child : getChildren())
            WebSiteShared.runOnMouseClick(child, () -> WebSiteShared.openUrl(((DemoThumbnail) child).getDemoLink()));
    }

    @Override
    protected void layoutChildren() {
        double width = getWidth(), height = getHeight();
        ObservableList<Node> children = getChildren();
        int n = children.size(); // 9
        int p = 3; // number of columns
        int q = 3; // number of rows
        double gap = 0.01 * width;
        Insets margin = new Insets(0, 0, gap, gap);
        double wp = (width - margin.getLeft() - margin.getRight()) / p;
        double hp = (height) / q;
        for (int i = 0; i < n; i++) {
            Node demoThumbnail = children.get(i);
            // General rule: the demo thumbnail is on a 3 x 3 grid
            int col = i % p, row = i / p;
            double x = col * wp, y = row * hp, w = wp, h = hp;
            // Managing exceptions:
            if (demoThumbnail == spaceFX) // Making SpaceFX twice taller
                h = 2 * hp;
            else if (w > h) { // When width is bigger than height
                if (demoThumbnail == modernGauge) // Making Modern Gauge twice smaller in width
                    w = wp / 2;
                else if (demoThumbnail == fx2048) { // Making FX2048 twice smaller in width
                    w = wp / 2;
                    x = 1.5 * wp; // also correcting position so it just after Modern Gauge
                }
            } else { // when height is bigger than width
                if (demoThumbnail == tallyCounter) // Making Tally Counter twice shorter
                    h = hp / 2;
                else if (demoThumbnail == enzoClocks) { // Making Enzo Clocks twice shorter
                    y = h = hp / 2;
                    x = wp; // also correcting position so it is just under Tally Counter
                } else if (demoThumbnail == modernGauge) // Moving Moder Gauge to the left
                    x = 0;
                else if (demoThumbnail == fx2048) // Moving FX2048 to the middle
                    x = wp;
            }
            layoutInArea(demoThumbnail, x, y, w, h, 0, margin, HPos.LEFT, VPos.TOP);
            demoThumbnail.setClip(new Rectangle(0, 0, ((Region) demoThumbnail).getWidth(), ((Region) demoThumbnail).getHeight()));
        }
    }
}
