package dev.webfx.website.application.cards;

import dev.webfx.platform.client.services.uischeduler.UiScheduler;
import dev.webfx.website.application.WebSiteShared;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyValue;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

import static dev.webfx.website.application.WebSiteShared.createCloud;

/**
 * @author Bruno Salmon
 */
final class WebFxCloudAnimationPane extends Pane {

    private final static long MILLIS_IN_NANO = 1_000_000;
    private final static double SPEED_FACTOR = 0.05;

    private final ScalePane cloudPane = new ScalePane(createCloud());
    private final FxWreathPane fxWreathPane = new FxWreathPane();
    private final SVGPath wreath = fxWreathPane.getWreathSVGPath();
    private final ScalePane jsLogoPane = new ScalePane(WebSiteShared.createJSLogo());
    private final SVGPath gwtLogo = WebSiteShared.createGwtLogo();
    private final SVGPath gwtText = WebSiteShared.createGwtText();
    private final ScalePane gwtLogoPane = new ScalePane(gwtLogo);
    private final ScalePane gwtTextPane = new ScalePane(gwtText);
    private final SVGPath arrowUp = WebSiteShared.createArrowUp();
    private final ScalePane arrowUpScalePane = new ScalePane(arrowUp);
    private final Canvas canvas = new Canvas();
    private final AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            long animationTimeMillis = animationTimeMillis(now);
            paintCanvas(animationTimeMillis);
            updateGithubGradients(animationTimeMillis);
        }
    };
    private final DoubleProperty expansionProperty = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            requestLayout();
        }
    };
    private boolean playing;

    public WebFxCloudAnimationPane() {
        getChildren().setAll(canvas, cloudPane, jsLogoPane, arrowUpScalePane, gwtLogoPane, gwtTextPane, fxWreathPane);
        fxWreathPane.setScaleMode(ScalePane.ScaleMode.MIN_WIDTH_HEIGHT);
        gwtLogo.setEffect(WebSiteShared.dropShadow);
        gwtText.setEffect(WebSiteShared.dropShadow);
        updateVisibilities();
    }

    private void updateGithubGradients(long animationTimeMillis) {
        double wreathHeight = wreath.getLayoutBounds().getHeight();
        double length = 2 * wreathHeight;
        wreath.setFill(   createVerticalGithubGradiant(length,       0, animationTimeMillis));
        arrowUp.setStroke(createVerticalGithubGradiant(length, wreathHeight, animationTimeMillis));
    }

    private static LinearGradient createVerticalGithubGradiant(double length, double shift, long animationTimeMillis) {
        return WebSiteShared.createVerticalGithubGradiant(length, shift + SPEED_FACTOR * animationTimeMillis);
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth(), h = getHeight(), hd2 = h / 2, hd4 = h / 4, expansion = expansionProperty.get();
        double sc = (0.5 - 0.1 * expansion) * h, scd2 = sc / 2, yc = hd2 - expansion * hd4 * 1.2;
        layoutInArea(cloudPane,   0,         yc - scd2, w,  sc, 0, HPos.CENTER, VPos.CENTER);
        double sa = playing ? 1.3 * hd4 : 1.6 * hd4, ya = playing ? hd4 : yc + sc * 0.3;
        layoutInArea(arrowUpScalePane, 0,                      ya, w,  sa, 0, HPos.CENTER, VPos.CENTER);
        double sjs = sc * 0.24, yjs = ya - sjs * 1.5;
        layoutInArea(jsLogoPane, 0, yjs, w, sjs, 0, HPos.CENTER, VPos.CENTER);
        double sg = sa * 0.3, yg = ya + sa * 0.6 - sg / 2;
        layoutInArea(gwtLogoPane,0, yg, w, sg, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(gwtTextPane,0, yg + sg * 1.1, w, sg / 2, 0, HPos.CENTER, VPos.CENTER);
        double sfx = (playing ? 0.875 : 1.7 - 0.2 * expansion) * sc, sfxd2 = sfx / 2, yfx = playing ? hd2 + hd4 : hd2 * 1.1 + expansion * hd4 * 1.1;
        layoutInArea(fxWreathPane,     0,       yfx - sfxd2, w, sfx, 0, HPos.CENTER, VPos.CENTER);
        if (playing) {
            canvas.setWidth(w);
            canvas.setHeight(h);
            long animationTimeMillis = animationTimeMillis(UiScheduler.nanoTime());
            paintCanvas(animationTimeMillis);
            updateGithubGradients(animationTimeMillis);
        }
    }

    void playContractionAnimation(CardTransition cardTransition) {
        cardTransition.addKeyValue(
                new KeyValue(expansionProperty, 0),
                new KeyValue(arrowUp.opacityProperty(), 0),
                new KeyValue(jsLogoPane.opacityProperty(), 0),
                new KeyValue(gwtLogoPane.opacityProperty(), 0),
                new KeyValue(gwtTextPane.opacityProperty(), 0)
        );
    }

    void playExpansionAnimation(CardTransition cardTransition, boolean showJsGwtLogos) {
        cardTransition.addKeyValue(new KeyValue(expansionProperty, 1));
        if (!showJsGwtLogos) {
            jsLogoPane.setOpacity(0);
            gwtLogoPane.setOpacity(0);
            gwtTextPane.setOpacity(0);
            arrowUp.setOpacity(1);
        } else cardTransition.addOnFinished(() -> {
            cardTransition.addKeyValue(
                    new KeyValue(arrowUp.opacityProperty(), 1),
                    new KeyValue(jsLogoPane.opacityProperty(), 1),
                    new KeyValue(gwtLogoPane.opacityProperty(), 1),
                    new KeyValue(gwtTextPane.opacityProperty(), 1)
            );
            cardTransition.run(true);
        });
    }

    void flipThumbUp() {
        fxWreathPane.flipThumbUp();
    }

    void flipFx() {
        fxWreathPane.flipFx();
    }

    void playBubblesAnimation() {
        bubbles.clear();
        lastSpawnBubble = null;
        for (int i = 0; i < 5; i++)
            bubbles.add(new Bubble());
        animationTimer.start();
        playing = true;
        expansionProperty.set(1);
        updateVisibilities();
    }

    void stopAnimation() {
        animationTimer.stop();
        playing = false;
        startMillis = 0;
        updateVisibilities();
    }

    private void updateVisibilities() {
        canvas.setVisible(playing);
        fxWreathPane.getWreathSVGPath().setVisible(playing);
        jsLogoPane.setVisible(!playing);
        gwtLogoPane.setVisible(!playing);
        gwtTextPane.setVisible(!playing);
    }

    private long animationTimeMillis(long nowNanos) {
        long nowMillis = nowNanos / MILLIS_IN_NANO;
        if (playing && startMillis == 0)
            startMillis = nowMillis;
        return playing ? nowMillis - startMillis : 0;
    }

    private final List<Bubble> bubbles = new ArrayList<>();
    private long startMillis;

    private void paintCanvas(long animationTimeMillis) {
        double w = getWidth(), h = getHeight(), hd2 = snapPositionY(h / 2), hd4 = snapPositionY(h / 4);
        if (w == 0)
            return;
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        ctx.clearRect(0, 0, w, h);

        for (Bubble bubble : bubbles)
            bubble.draw(w, hd2, animationTimeMillis, ctx);

        ctx.setFill(Color.WHITE);
        ctx.setFont(Font.font(14));
        ctx.setTextAlign(TextAlignment.CENTER);
        double x = 50;
        ctx.fillText("Web", x, hd4 - 10);
        ctx.fillText("ecosystem", x, hd4 + 10);
        ctx.fillText("Java Desktop", x, hd2 + hd4 - 10);
        ctx.fillText("ecosystem", x, hd2 + hd4 + 10);

        x = w - x;
        ctx.fillText("Volatile", x, hd4 - 10);
        ctx.fillText("technologies", x, hd4 + 10);
        ctx.fillText("Sustainable", x, hd2 + hd4 - 10);
        ctx.fillText("technology", x, hd2 + hd4 + 10);

        ctx.save();
        ctx.setStroke(Color.grayRgb(255, 0.5));
        ctx.setLineWidth(5);
        ctx.setLineDashes(10, 20);
        double dashOffset = (SPEED_FACTOR * animationTimeMillis) % 30;
        ctx.setLineDashOffset(dashOffset);
        ctx.setFont(Font.font(8));
        ctx.beginPath();
        ctx.moveTo(0, hd2);
        ctx.lineTo(w, hd2);
        ctx.stroke();
        ctx.restore();

        ctx.closePath();
    }

    private Bubble lastSpawnBubble;

    private class Bubble {
        // constant fields for the time of a bubble cycle
        double xRight, radiusHPercent, yHPercent, decayingWPercent, dyingWPercent;
        // constant fields for the time of a draw
        double w, h;
        long animationTimeMillis;

        void spawn() {
            radiusHPercent = 0.08 + 0.08 * Math.random();
            yHPercent = Math.random();
            decayingWPercent = 0.3 + 0.6 * Math.random();
            dyingWPercent = 0.6 * decayingWPercent;
            if (w == 0)
                xRight = 0;
            else {
                if (lastSpawnBubble == null)
                    xRight = (animationTimeMillis + 2000) * SPEED_FACTOR; // Will enter from the right 2s after animation started
                else
                    xRight = lastSpawnBubble.xRight + 3 * lastSpawnBubble.radiusHPercent * h + 3000 * SPEED_FACTOR * Math.random();
                lastSpawnBubble = this;
            }
        }

        double getX() {
            double x = w + xRight - animationTimeMillis * SPEED_FACTOR;
            if (radiusHPercent == 0 || x < - 2 * radiusHPercent * h) {
                spawn();
                return getX();
            }
            return x;
        }

        double getY() {
            double r = radiusHPercent * h * 1.1;
            return r + yHPercent * (h - 3 * r);
        }

        void draw(double w, double h, long animationTimeMillis, GraphicsContext ctx) {
            this.w = w;
            this.h = h;
            this.animationTimeMillis = animationTimeMillis;
            ctx.setLineWidth(0.01 * h);
            double x = getX();
            double wPercent = x / w;
            boolean decaying = wPercent < decayingWPercent;
            boolean dying = wPercent < dyingWPercent;
            double dyingOpacity = Math.max(0, 1 + 2 * (wPercent - dyingWPercent));
            ctx.setStroke(
                    dying ? Color.color(1, 0.2, 0.2, dyingOpacity) :
                    decaying ? Color.ORANGE :
                    Color.color(0.4, 0.8, 0.4)
            );
            double radius = radiusHPercent * h;
            double r = radius * (dying ? 1 + 2 * (dyingWPercent- wPercent) : 1);
            ctx.strokeArc(x + radius - r, getY() + radius - r, 2 * r, 2 * r, 0, 360, ArcType.OPEN);
        }
    }
}
