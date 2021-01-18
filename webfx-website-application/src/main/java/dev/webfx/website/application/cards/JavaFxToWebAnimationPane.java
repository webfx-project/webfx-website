package dev.webfx.website.application.cards;

import dev.webfx.platform.client.services.uischeduler.UiScheduler;
import dev.webfx.website.application.WebSiteShared;
import javafx.animation.AnimationTimer;
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

import static dev.webfx.website.application.cards.Card.createCloud;

/**
 * @author Bruno Salmon
 */
final class JavaFxToWebAnimationPane extends Pane {

    private final static long MILLIS_IN_NANO = 1_000_000;
    private final static double SPEED_FACTOR = 0.1;

    private final Pane cloudScalePane = new ScalePane(createCloud());
    private final FxWreathPane fxWreathPane = new FxWreathPane();
    private final SVGPath wreath = fxWreathPane.getWreathSVGPath();
    private final SVGPath arrowUp = Card.createArrowUp();
    private final Pane arrowUpScalePane = new ScalePane(arrowUp);
    private final Canvas canvas = new Canvas();
    private final AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            now = normalizeNowMillis(now / MILLIS_IN_NANO);
            paintCanvas(now);
            updateGithubGradients(now);
        }
    };
    private boolean playing;

    public JavaFxToWebAnimationPane() {
        getChildren().setAll(canvas, cloudScalePane, arrowUpScalePane, fxWreathPane);
    }

    private void updateGithubGradients(long nowMillis) {
        wreath.setFill(createVerticalGithubGradiant(nowMillis, 0));
        arrowUp.setStroke(createVerticalGithubGradiant(nowMillis, fxWreathPane.getHeight()));
    }

    private static LinearGradient createVerticalGithubGradiant(long nowMillis, double shift) {
        return WebSiteShared.createVerticalGithubGradiant(shift + SPEED_FACTOR * nowMillis);
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth(), h = getHeight(), hd2 = h / 2, hd4 = h / 4;
        double sc = 0.4 * h, scd2 = sc / 2, sfx = 0.35 * h, sfxd2 = sfx / 2;
        layoutInArea(cloudScalePane, 0, hd4 - scd2, w, sc, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(fxWreathPane, 0, hd2 + hd4 - sfxd2, w, sfx, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(arrowUpScalePane, 0, hd4, w, 1.3 * hd4, 0, HPos.CENTER, VPos.CENTER);
        canvas.setWidth(w);
        canvas.setHeight(h);
        long nowMillis = normalizeNowMillis(nowMillis());
        paintCanvas(nowMillis);
        updateGithubGradients(nowMillis);
    }

    void playAnimation() {
        bubbles.clear();
        lastSpawnBubble = null;
        for (int i = 0; i < 5; i++)
            bubbles.add(new Bubble());
        animationTimer.start();
        playing = true;
    }

    void stopAnimation() {
        animationTimer.stop();
        playing = false;
        startMillis = 0;
    }

    private long normalizeNowMillis(long nowMillis) {
        if (playing && startMillis == 0)
            startMillis = nowMillis;
        return playing ? nowMillis - startMillis : 0;
    }

    private final List<Bubble> bubbles = new ArrayList<>();
    private long startMillis;

    private void paintCanvas(long nowMillis) {
        double w = getWidth(), h = getHeight(), hd2 = h / 2, hd4 = h / 4;
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        ctx.clearRect(0, 0, w, h);

        for (Bubble bubble : bubbles)
            bubble.draw(w, hd2, nowMillis, ctx);

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
        double dashOffset = (SPEED_FACTOR * nowMillis) % 30;
        ctx.setLineDashOffset(dashOffset);
        ctx.setFont(Font.font(8));
        ctx.beginPath();
        ctx.moveTo(0, hd2);
        ctx.lineTo(w, hd2);
        ctx.stroke();
        ctx.restore();

        ctx.closePath();
    }

    private static long nowMillis() {
        return UiScheduler.nanoTime() / MILLIS_IN_NANO;
    }

    private Bubble lastSpawnBubble;

    private class Bubble {
        // constant fields for the time of a bubble cycle
        double xRight, radius, hPercent, decayingWPercent, dyingWPercent;
        // constant fields for the time of a draw
        double w, h;
        long nowMillis;

        void spawn() {
            radius = 20 + 20 * Math.random();
            hPercent = Math.random();
            decayingWPercent = 0.3 + 0.6 * Math.random();
            dyingWPercent = 0.6 * decayingWPercent;
            if (w == 0)
                xRight = 0;
            else {
                if (lastSpawnBubble == null)
                    xRight = (nowMillis + 1000) * SPEED_FACTOR; // Will enter from the right 1s after animation started
                else
                    xRight = lastSpawnBubble.xRight + 3 * lastSpawnBubble.radius + 3000 * SPEED_FACTOR * Math.random();
                lastSpawnBubble = this;
            }
        }

        double getX() {
            double x = w + xRight - nowMillis * SPEED_FACTOR;
            if (radius == 0 || x < - 2 * radius) {
                spawn();
                return getX();
            }
            return x;
        }

        double getY() {
            double r = radius * 1.1;
            return r + hPercent * (h - 3 * r);
        }

        void draw(double w, double h, long nowMillis, GraphicsContext ctx) {
            this.w = w;
            this.h = h;
            this. nowMillis = nowMillis;
            ctx.setLineWidth(5);
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
            double r = radius * (dying ? 1 + 2 * (dyingWPercent- wPercent) : 1);
            ctx.strokeArc(x + radius - r, getY() + radius - r, 2 * r, 2 * r, 0, 360, ArcType.OPEN);
        }
    }
}
