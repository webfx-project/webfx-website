package dev.webfx.website.application.cards;

import dev.webfx.website.application.images.SvgLogoPaths;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.ScalePane;
import dev.webfx.website.application.shared.WebSiteShared;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.ArcType;

import static dev.webfx.website.application.shared.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
public final class MagicalCard extends FlipCard {

    private final static Color[] RAINBOW_COLORS = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE };

    private GwtCompilationAnimationPane gwtCompilationAnimationPane;
    private GearsAnimationPane gearsAnimationPane;

    public MagicalCard() {
        super("Magical");
    }

    @Override
    String caption(int step) {
        switch (step) {
            case 1: return "WebFX can generate fast and quite thin web apps despite the big JavaFX size. How possible?";
            case 2: return "The JS code only needs to create a DOM rendered by the browser, so it doesn't include the big JavaFX rendering layer, as opposed to the desktop version.";
            case 3: return "And GWT removes all dead code! Making your JS code much thinner, with only the JavaFX classes required by your application (you won't use them all!).";
            case 4: return "GWT is known to produce a compact and optimized JS code, that is likely faster than a hand-written JS code.";
            case 5: return "And JavaFX itself is fast, because it's not a framework but &ldquo;just&rdquo; a UI toolkit, with no processing running in the background, so giving you full speed.";
            case 6: return "Here is the Lighthouse performance score for this website. Isn't WebFX magical?";
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
                flipToNewContent(new LayoutPane(rainbowCanvas, rainbowStar1, rainbowStar2, webFxLogo, whiteWand, blackWand, star1, star2, star3, star4) {
                    @Override
                    protected void layoutChildren(double width, double height) {
                        rainbowCanvas.setWidth(width);
                        rainbowCanvas.setHeight(height);
                        GraphicsContext ctx = rainbowCanvas.getGraphicsContext2D();
                        ctx.clearRect(0, 0, width, height);
                        double minwh = Math.min(width, height), wd2 = width / 2, hd2 = height / 2;
                        double strokeWidth = 0.03 * minwh, bigRadius = minwh / 2 - strokeWidth / 2, radius = bigRadius;
                        ctx.setLineWidth(strokeWidth);
                        for (Color color : RAINBOW_COLORS) {
                            if (radius <= 0) // Stopping the loop if radius is negative (may happen if card is very small)
                                break;
                            ctx.setStroke(color.deriveColor(0, 0.7, 1, 1));
                            ctx.strokeArc(wd2 - radius, hd2 - radius, 2 * radius, 2 * radius, 0, 180, ArcType.OPEN);
                            ctx.stroke();
                            radius -= strokeWidth - 1;
                        }
                        radius = Math.max(0, radius);
                        double wfw = wd2, wfh = hd2, wfx = wd2 - wfw / 2, wfy = hd2 - wfh / 2 ;
                        layoutInArea(webFxLogo, wfx, wfy, wfw, wfh);
                        double sw = Math.min(bigRadius * 0.75, (bigRadius - radius) * 1.7), sh = sw, sx = wd2 - Math.max(bigRadius + strokeWidth, (bigRadius + strokeWidth + radius) / 2 + sw / 2), sy = hd2 - sh / 2;
                        layoutInArea(rainbowStar1, sx, sy, sw, sh);
                        sx = width - sx - sw;
                        layoutInArea(rainbowStar2, sx, sy, sw, sh);
                        centerInArea(whiteWand, -0.84 * minwh + wd2, 0, minwh, 2 * strokeWidth);
                        centerInArea(blackWand, -0.84 * minwh + wd2 + 0.2 * minwh, 0, 0.6 * minwh, 2 * strokeWidth);
                        sw = 0.1 * minwh; sh = sw; sx = wd2 - 0.6 * bigRadius; sy = hd2 + 0.3 * bigRadius;
                        layoutInArea(star1, sx, sy, sw, sh);
                        sx = wd2 - 0.4 * bigRadius; sy = hd2 - 0.5 * bigRadius;
                        layoutInArea(star2, sx, sy, sw, sh);
                        sx = wd2 + 0.3 * bigRadius; sy = hd2 - 0.4 * bigRadius;
                        layoutInArea(star3, sx, sy, sw, sh);
                        sx = wd2 + 0.1 * bigRadius; sy = hd2 + 0.5 * bigRadius;
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
                if (gearsAnimationPane != null)
                    cardTransition.addOnFinished(gearsAnimationPane::stop);
                break;
            case 5:
                if (gearsAnimationPane == null)
                    gearsAnimationPane = new GearsAnimationPane();
                flipToNewContent(gearsAnimationPane);
                gearsAnimationPane.play(cardTransition);
                break;
            case 6:
                cardTransition.addOnFinished(gearsAnimationPane::stop);
                PerformanceAnimationPane performanceAnimationPane = new PerformanceAnimationPane();
                flipToNewContent(performanceAnimationPane);
                performanceAnimationPane.play(cardTransition);
                break;
        }
    }
}
