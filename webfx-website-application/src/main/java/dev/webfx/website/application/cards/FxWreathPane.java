package dev.webfx.website.application.cards;

import dev.webfx.website.application.shared.WebSiteShared;
import dev.webfx.website.application.images.SvgLogoPaths;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.ScalePane;
import eu.hansolo.enzo.flippanel.FlipPanel;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.SVGPath;

/**
 * @author Bruno Salmon
 */
public final class FxWreathPane extends LayoutPane {

    private final SVGPath wreathSVGPath;
    private final SVGPath fxLogo = WebSiteShared.createFxLogo();
    private final SVGPath thumbUp = WebSiteShared.createThumbUp();
    private final ScalePane wreathPane;
    private final FlipPanel flipPanel = new FlipPanel();

    public FxWreathPane() {
        this(WebSiteShared.createLogoSVGPath(SvgLogoPaths.getWreathPath(), LinearGradient.valueOf("to right, brown, orange")));
    }

    public FxWreathPane(SVGPath wreathSVGPath) {
        this.wreathSVGPath = wreathSVGPath;
        wreathPane = new ScalePane(ScalePane.ScaleMode.HEIGHT, wreathSVGPath);
        getChildren().setAll(wreathPane, flipPanel);
        flipPanel.getFront().getChildren().setAll(new ScalePane(fxLogo));
        flipPanel.getBack().getChildren().setAll(thumbUp);
    }

    SVGPath getWreathSVGPath() {
        return wreathSVGPath;
    }

    public ScalePane getWreathPane() {
        return wreathPane;
    }

    void setScaleMode(ScalePane.ScaleMode scaleMode) {
        wreathPane.setScaleMode(scaleMode);
    }

    @Override
    protected void layoutChildren(double width, double height) {
        double w = width, h = height, s = Math.min(w, h);
        centerInArea(wreathPane,(w - s) / 2, (h - s) / 2, s, s);
        s *= 0.4;
        centerInArea(flipPanel,    (w - s) / 2, (h - s) / 2, s, s);
        thumbUp.setScaleX(fxLogo.getScaleX() * 1.25);
        thumbUp.setScaleY(fxLogo.getScaleY() * 1.25);
    }

    void flipThumbUp() {
        fxLogo.setFill(Color.TRANSPARENT);
        flipPanel.flipToBack();
    }

    void flipFx() {
        fxLogo.setFill(WebSiteShared.fxColor);
        flipPanel.flipToFront();
    }
}
