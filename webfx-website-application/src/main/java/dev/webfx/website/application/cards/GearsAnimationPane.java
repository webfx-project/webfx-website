package dev.webfx.website.application.cards;

import dev.webfx.extras.webtext.HtmlText;
import dev.webfx.website.application.images.SvgLogoPaths;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.ScalePane;
import dev.webfx.website.application.shared.WebSiteShared;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyValue;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.*;

import static dev.webfx.website.application.shared.WebSiteShared.createJavaFxLogo;
import static dev.webfx.website.application.shared.WebSiteShared.createLogoSVGPath;

/**
 * @author Bruno Salmon
 */
final class GearsAnimationPane extends LayoutPane {

    private final HBox javaFxLogo = createJavaFxLogo();
    private final HtmlText noteText = WebSiteShared.setHtmlText(new HtmlText(), "JavaFX doesn't bind your model but is loved for having all its graphical properties bindable with a two-way binding API");
    private final ScalePane gear1 = createGear(Color.web("#b08968"));
    private final ScalePane gear2 = createGear(Color.web("#9c6644"));
    private final ScalePane gear3 = createGear(Color.web("#7f5539"));
    private final CirclePane circle1 = new CirclePane("Syntax parsing",   -90, Color.TRANSPARENT, null, null);
    private final CirclePane circle2 = new CirclePane("DOM queries",-90, Color.TRANSPARENT, null, null);
    private final CirclePane circle3 = new CirclePane("Model binding", -90, Color.TRANSPARENT, null, null);
    private final Region redCross1 = new Region(), redCross2 = new Region();
    private final DoubleProperty redCross1FillPercent = new SimpleDoubleProperty(1) {
        @Override
        protected void invalidated() {
            fillRegion(redCross1, get());
        }
    };
    private final DoubleProperty redCross2FillPercent = new SimpleDoubleProperty(1) {
        @Override
        protected void invalidated() {
            fillRegion(redCross2, get());
        }
    };
    private final AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            double angle = 360. * now / 1e9 * 0.25 % 360;
            gear1.setRotate(angle);
            gear2.setRotate(-angle);
            gear3.setRotate(angle);
            circle1.setRotate(angle);
            circle2.setRotate(-angle);
            circle3.setRotate(angle + 45);
        }
    };

    private static void fillRegion(Region region, double percent) {
        Color color1 = Color.rgb(255, 0, 0, 0.75), color2 = Color.TRANSPARENT;
        Paint fill = percent <= 0 ? color2 : percent >= 1 ? color1 : new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, color1), new Stop(percent, color1), new Stop(percent, color2), new Stop(1, color2));
        WebSiteShared.setRegionBackground(region, fill);
    }

    private static ScalePane createGear(Color color) {
        return new ScalePane(createLogoSVGPath(SvgLogoPaths.getGearPath(), color));
    }

    public GearsAnimationPane() {
        getChildren().setAll(gear1, gear2, gear3, circle1, circle2, circle3, javaFxLogo, noteText, redCross1, redCross2);
        redCross1.setRotate(-45);
        redCross2.setRotate(+45);
    }

    @Override
    protected void layoutChildren(double width, double height) {
        double javaFxHeight = javaFxLogo.prefHeight(width);
        centerInArea(javaFxLogo, 0, 0, width, javaFxHeight);
        WebSiteShared.updateTextFontSize(noteText, Math.min(0.035 * width, 16), false);
        double noteHeight = noteText.prefHeight(width), gap = 10;
        centerInArea(noteText, 0, javaFxHeight + gap, width, noteHeight);
        double y0 = javaFxHeight + noteHeight + gap;
        height -= y0;
        // Thinking computation in landscape layout (will be corrected at the end if portrait)
        double w = Math.max(width, height), h = Math.min(width, height), s = h, wd2 = w / 2, hd2 = h / 2, sd2 = s / 2, r = sd2 * 0.49, d = 2 * r;
        double R = (s - d) / (2 * Math.cos(Math.PI / 6));
        double yc = 0.8 * hd2, yClients = yc - R * Math.sin(Math.PI/6) - r;
        double Rcos = R * Math.cos(Math.PI/6);
        // Initializing position variables in landscape layout
        double y1 = yc + R - r, x1 = wd2 - Rcos - r, y3 = y1, x3 = wd2 + Rcos - r, y2 = yClients, x2 = wd2 - r;
        // Correcting variables if portrait layout
        if (width < height) {
            double t;
            t = x1; x1 = y1; y1 = t; t = x2; x2 = y2; y2 = t; t = x3; x3 = y3; y3 = t;
        }
        // Adjusting vertical positions to equalize top and bottom margins
        double maxY = Math.max(Math.max(y1, y2), y3);
        double minY = Math.min(Math.min(y1, y2), y3);
        double dy = (height - (maxY + d - minY)) / 2 - minY + y0; // Also shifting everything under JavaFX & note
        layoutInArea(gear1, x1, dy + y1, d, d);
        layoutInArea(gear2, x2, dy + y2, d, d);
        layoutInArea(gear3, x3, dy + y3, d, d);
        double cr = 0.8 * r, cd = 2 * cr, dr = r - cr;
        circle1.setRadius(cr);
        circle2.setRadius(cr);
        circle3.setRadius(cr);
        layoutInArea(circle1, x1 + dr, dy + y1 + dr, cd, cd);
        layoutInArea(circle2, x2 + dr, dy + y2 + dr, cd, cd);
        layoutInArea(circle3, x3 + dr, dy + y3 + dr, cd, cd);
        double rw = 0.1 * h, rh = 0.5 * height, rx = width / 2 - rw / 2, ry = height / 2 - rh / 2 + y0;
        centerInArea(redCross1, rx, ry, rw, rh);
        centerInArea(redCross2, rx, ry, rw, rh);
    }

    void play(CardTransition cardTransition) {
        animationTimer.start();
        redCross1FillPercent.set(0);
        redCross2FillPercent.set(0);
        cardTransition.addOnFinished(() -> {
            cardTransition.addKeyValue(new KeyValue(redCross1FillPercent, 1));
            cardTransition.addOnFinished(() -> {
                cardTransition.addKeyValue(new KeyValue(redCross2FillPercent, 1));
                cardTransition.run(true);
            });
            cardTransition.run(true);
        });
    }

    void stop() {
        animationTimer.stop();
    }
}
