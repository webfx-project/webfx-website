package dev.webfx.website.application.cards;

import dev.webfx.extras.scalepane.ScaleMode;
import dev.webfx.website.application.shared.WebSiteShared;
import dev.webfx.website.application.images.SvgLogoPaths;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.extras.scalepane.ScalePane;
import dev.webfx.extras.flippane.FlipPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.SVGPath;

/**
 * @author Bruno Salmon
 */
public final class FXWreathPane extends LayoutPane {

    private final SVGPath wreathSVGPath;
    private final SVGPath fxLogo = WebSiteShared.createFxLogo();
    private final SVGPath thumbUp = WebSiteShared.createThumbUp();
    private final ScalePane wreathPane;
    private final FlipPane flipPane = new FlipPane();

    public FXWreathPane() {
        this(WebSiteShared.createLogoSVGPath(SvgLogoPaths.getWreathPath(), LinearGradient.valueOf("to right, brown, orange")));
    }

    public FXWreathPane(SVGPath wreathSVGPath) {
        this.wreathSVGPath = wreathSVGPath;
        wreathPane = new ScalePane(ScaleMode.FIT_HEIGHT, wreathSVGPath);
        getChildren().setAll(wreathPane, flipPane);
        flipPane.setFront(new ScalePane(fxLogo));
        flipPane.setBack(thumbUp);
    }

    SVGPath getWreathSVGPath() {
        return wreathSVGPath;
    }

    public ScalePane getWreathPane() {
        return wreathPane;
    }

    void setScaleMode(ScaleMode scaleMode) {
        wreathPane.setScaleMode(scaleMode);
    }

    @Override
    protected void layoutChildren(double width, double height) {
        double w = width, h = height, s = Math.min(w, h);
        centerInArea(wreathPane,(w - s) / 2, (h - s) / 2, s, s);
        s *= 0.4;
        centerInArea(flipPane,    (w - s) / 2, (h - s) / 2, s, s);
        thumbUp.setScaleX(fxLogo.getScaleX() * 1.25);
        thumbUp.setScaleY(fxLogo.getScaleY() * 1.25);
    }

    void flipThumbUp() {
        fxLogo.setFill(Color.TRANSPARENT);
        flipPane.flipToBack();
    }

    void flipFx() {
        fxLogo.setFill(WebSiteShared.fxColor);
        flipPane.flipToFront();
    }
}
