package dev.webfx.website.application.cards;

import dev.webfx.extras.webtext.controls.HtmlText;
import dev.webfx.website.application.images.SvgLogoPaths;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.ScalePane;
import dev.webfx.website.application.shared.WebSiteShared;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import static dev.webfx.website.application.shared.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
final class MagicalCard extends FlipCard {

    private final static Color[] RAINBOW_COLORS = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE };

    private GwtCompilationAnimationPane gwtCompilationAnimationPane;

    MagicalCard() {
        super("Magical");
    }

    @Override
    String caption(int step) {
        switch (step) {
            case 1: return "WebFX can generate fast and quite thin web apps despite the big JavaFX size. How possible?";
            case 2: return "The JS code only needs to create a DOM rendered by the browser, so it doesn't include the big JavaFX rendering layer, as opposed to the desktop version.";
            case 3: return "And GWT removes all dead code! Making your JS code much thinner, with only the JavaFX classes required by your application (you won't use them all!).";
            case 4: return "GWT is known to generate a compact and optimized JS code, that is likely faster than a hand-written JS code.";
            case 5: return "And JavaFX itself is fast, because it's \"just\" a UI toolkit and not a framework (no DOM queries, syntax parsing or binding processing in the background)";
            //case 5: return "Also JavaFX is a UI toolkit, not a framework (no DOM queries, syntax parsing or binding processing in the background), so just a UI engine at full speed.";
            //case 6: return "But JavaFX is loved for having all its graphical properties bindable (a two-way binding API that WebFX also uses to efficiently synchronize the DOM).";
            case 6: return "Here is the Lighthouse performance score for this website. Knowing that JavaFX is under the hood, isn't that magical?";
            default: return null;
        }
    }

    private static Region createWand(boolean white) {
        return setRegionBackground(rotate(new Region(), 45), LinearGradient.valueOf(white ? "white, #333" : "gray, black"));
    }

    private static ScalePane createStar(Color color, double angle) {
        return new ScalePane(rotate(createLogoSVGPath(SvgLogoPaths.getStarPath(), color), angle));
    }

    @Override
    void prepareCardTransition(int step, CardTransition cardTransition) {
        super.prepareCardTransition(step, cardTransition);
        switch (step) {
            case 1:
                Canvas rainbowCanvas = new Canvas();
                FxWreathPane webFxLogo = WebSiteShared.createWebFxLogo();
                webFxLogo.setScaleMode(ScalePane.ScaleMode.MIN_WIDTH_HEIGHT);
                Region whiteWand = createWand(true), blackWand = createWand(false);
                Node rainbowStar1 = createStar(Color.BLUE, 0), rainbowStar2 = createStar(Color.PURPLE, 0), star1 = createStar(Color.GREEN,-30), star2 = createStar(Color.RED, -20), star3 = createStar(Color.ORANGE, 20), star4 = createStar(Color.YELLOW, 30);
                flipToNewContent(new LayoutPane(rainbowCanvas, webFxLogo, rainbowStar1, rainbowStar2, whiteWand, blackWand, star1, star2, star3, star4) {
                    @Override
                    protected void layoutChildren(double width, double height) {
                        rainbowCanvas.setWidth(width);
                        rainbowCanvas.setHeight(height);
                        GraphicsContext ctx = rainbowCanvas.getGraphicsContext2D();
                        ctx.clearRect(0, 0, width, height);
                        double strokeWidth = 0.03 * width, bigRadius = Math.min(width, height) / 2 - strokeWidth / 2, radius = bigRadius;
                        for (Color color : RAINBOW_COLORS) {
                            if (radius <= 0) // Stopping the loop if radius is negative (may happen if card is very small)
                                break;
                            ctx.setStroke(color.deriveColor(0, 0.7, 1, 1));
                            ctx.setLineWidth(strokeWidth);
                            ctx.strokeArc(width / 2 - radius, height / 2 - radius, 2 * radius, 2 * radius, 0, 180, ArcType.OPEN);
                            ctx.stroke();
                            radius -= strokeWidth - 1;
                        }
                        double wfw = 0.5 * width, wfh = 0.5 * height, wfx = width / 2 - wfw / 2, wfy = height / 2 - wfh / 2 ;
                        layoutInArea(webFxLogo, wfx, wfy, wfw, wfh);
                        double sw = (bigRadius - radius) * 1.7, sh = sw, sx = width / 2 - (bigRadius + radius) / 2 - sw / 2, sy = height / 2 - sh / 2;
                        layoutInArea(rainbowStar1, sx, sy, sw, sh);
                        sx = width - sx - sw;
                        layoutInArea(rainbowStar2, sx, sy, sw, sh);
                        layoutInArea(whiteWand, -width / 3, 0, width, 2 * strokeWidth, 0, HPos.CENTER, VPos.CENTER);
                        layoutInArea(blackWand, -width / 3 + 0.2 * width, 0, 0.6 * width, 2 * strokeWidth, 0, HPos.CENTER, VPos.CENTER);
                        sw = 0.1 * width; sh = sw; sx = 0.25 * width; sy = 0.65 * height;
                        layoutInArea(star1, sx, sy, sw, sh);
                        sx = 0.33 * width; sy = 0.3 * height;
                        layoutInArea(star2, sx, sy, sw, sh);
                        sx = 0.66 * width; sy = 0.35 * height;
                        layoutInArea(star3, sx, sy, sw, sh);
                        sx = 0.55 * width; sy = 0.75 * height;
                        layoutInArea(star4, sx, sy, sw, sh);
                    }
                });
                break;
            case 2:
                if (gwtCompilationAnimationPane == null)
                    gwtCompilationAnimationPane = new GwtCompilationAnimationPane();
                gwtCompilationAnimationPane.setOpacity(1);
                flipToNewContent(gwtCompilationAnimationPane);
                gwtCompilationAnimationPane.playJavaFxRenderingLayerRemoval(cardTransition);
                break;
            case 3:
                flipToNewContent(gwtCompilationAnimationPane);
                gwtCompilationAnimationPane.playDeadCodeRemoval(cardTransition);
                break;
            case 4:
                gwtCompilationAnimationPane.playNonDeadCodeIntegration(cardTransition);
                flipToNewContent(gwtCompilationAnimationPane); // In case of reward
                break;
            case 5:
                flipToNewContent(WebSiteShared.createJavaFxLogo());
                break;
            case 6:
                Color backgroundCircleColor = Color.web("#E9FAF0"); // Light green
                Color strokeCircleColor = Color.web("#54C583"); // Stronger green
                Text performanceText = new Text("Performance");
                performanceText.setFill(strokeCircleColor);
                HtmlText noteText = WebSiteShared.setHtmlText(new HtmlText(), "Score obtained on a MacBook Pro 2019 with Chrome.<br/>You can check the <a style='color:inherit;' href='#'>screenshots</a>.");
                noteText.setFill(backgroundCircleColor);
                noteText.setMouseTransparent(false);
                LayoutPane[] canvasPaneHolder = new LayoutPane[1]; // to allow forward reference in scoreProperty
                DoubleProperty scoreProperty = new SimpleDoubleProperty() {
                    @Override
                    protected void invalidated() {
                        canvasPaneHolder[0].forceLayoutChildren();
                    }
                };
                Canvas canvas = new Canvas();
                flipToNewContent(canvasPaneHolder[0] = new LayoutPane(canvas, performanceText, noteText) {
                    private Font scoreFont = Font.font("Monospace");
                    @Override
                    protected void layoutChildren(double width, double height) {
                        WebSiteShared.updateTextFontSize(noteText, 0.03 * width, false);
                        double h = noteText.prefHeight(width);
                        bottomInArea(noteText,0, height - h, width, h);
                        height -= h;
                        WebSiteShared.updateTextFontSize(performanceText, 0.15 * width, false);
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
                });
                cardTransition.addOnFinished(() -> {
                    cardTransition.setDurationMillis(1500);
                    cardTransition.addKeyValue(new KeyValue(scoreProperty, 1, new Interpolator() {
                        @Override
                        protected double curve(double t) {
                            double t0 = 0.66, f = 10;
                            if (t < t0 / f)
                                return f * t;
                            t = (t - t0 / f) / (1 - t0 / f); // => between 0 & 1
                            t = Math.sin((t * Math.PI) / 2);
                            t = t0 + (1 - t0) * t;
                            return t;
                        }
                    }));
                    cardTransition.run(true);
                });
                break;
        }
    }
}
