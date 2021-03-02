package dev.webfx.website.application.cards;

import dev.webfx.extras.webtext.controls.HtmlText;
import dev.webfx.website.application.images.ImageLoader;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.WebSiteShared;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * @author Bruno Salmon
 */
final class PerformanceAnimationPane extends LayoutPane {

    private static final Color backgroundCircleColor = Color.web("#E9FAF0"); // Light green
    private static final Color strokeCircleColor = Color.web("#54C583"); // Stronger green

    private final Text performanceText = new Text("Performance");
    private final HtmlText noteText = WebSiteShared.setHtmlText(new HtmlText(), "Score obtained on a MacBook Pro 2019 with Chrome.<br/>You can check the <a style='color:inherit;' href='#'>screenshots</a>.");
    private final Canvas canvas = new Canvas();
    private Font scoreFont = Font.font("Monospace");
    private final DoubleProperty scoreProperty = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            forceLayoutChildren();
        }
    };

    public PerformanceAnimationPane() {
        getChildren().setAll(canvas, performanceText, noteText);
        performanceText.setFill(strokeCircleColor);
        noteText.setFill(backgroundCircleColor);
        noteText.setMouseTransparent(false);
        WebSiteShared.runOnMouseClick(noteText, () -> WebSiteShared.openUrl(ImageLoader.toImageUrl("Performance.png")));
    }

    @Override
    protected void layoutChildren(double width, double height) {
        WebSiteShared.updateTextFontSize(noteText, Math.min(16, 0.03 * Math.min(width, height)), false);
        double h = noteText.prefHeight(width);
        bottomInArea(noteText,0, height - h, width, h);
        height -= h;
        WebSiteShared.updateTextFontSize(performanceText, 0.15 * Math.min(width, height), false);
        bottomInArea(performanceText,0, 0, width, height);
        height -= performanceText.getLayoutBounds().getHeight();
        canvas.setWidth(width);
        canvas.setHeight(height);
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        double radius = 0.8 * Math.min(width, height) / 2;
        ctx.clearRect(0, 0, width, height);
        double lineWidth = radius / 5;
        ctx.setLineWidth(lineWidth);
        ctx.setLineCap(StrokeLineCap.ROUND);
        ctx.setFill(backgroundCircleColor);
        radius += lineWidth / 2;
        ctx.fillArc(width / 2 - radius, height / 2 - radius, 2 * radius, 2 * radius, 90, 360, ArcType.OPEN);
        double score = scoreProperty.get();
        if (score > 0) {
            radius -= lineWidth / 2;
            ctx.setStroke(strokeCircleColor);
            ctx.strokeArc(width / 2 - radius, height / 2 - radius, 2 * radius, 2 * radius, 90, -360 * score, ArcType.OPEN);
            ctx.stroke();
            ctx.setTextAlign(TextAlignment.CENTER);
            ctx.setTextBaseline(VPos.CENTER);
            ctx.setFill(strokeCircleColor);
            ctx.setFont(scoreFont = WebSiteShared.updateFontSize(scoreFont,0.8 * radius, false));
            ctx.fillText("" + (int) (score * 100), width / 2, height / 2);
        }
    }

    void play(CardTransition cardTransition) {
        cardTransition.addOnFinished(() -> {
            cardTransition.setDurationMillis(1500);
            cardTransition.addKeyValue(new KeyValue(scoreProperty, 1, new Interpolator() {
                @Override
                protected double curve(double t) {
                    double t0 = 0.66, f = 10, ft = f * t; // Speed factor 10 until reaching 2/3
                    if (ft < t0)
                        return ft;
                    // Now we are in the last 1/3 where we spend 90% of the time
                    t = (ft - t0) / (f - t0); // => normalization (between 0 & 1) of the time within these 90%
                    t = Math.sin(t * Math.PI / 2); // applying a sinusoidal ease out function
                    return t0 + (1 - t0) * t; // => de-normalization
                }
            }));
            cardTransition.run(true);
        });
    }
}
