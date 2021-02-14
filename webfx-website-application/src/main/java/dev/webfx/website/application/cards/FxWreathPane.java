package dev.webfx.website.application.cards;

import dev.webfx.website.application.SvgLogoPaths;
import dev.webfx.website.application.WebSiteShared;
import eu.hansolo.enzo.flippanel.FlipPanel;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.SVGPath;

/**
 * @author Bruno Salmon
 */
public final class FxWreathPane extends Pane {

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
    protected void layoutChildren() {
        double w = getWidth(), h = getHeight(), s = Math.min(w, h);
        layoutInArea(wreathPane,(w - s) / 2, (h - s) / 2, s, s, 0, HPos.CENTER, VPos.CENTER);
        s *= 0.4;
        layoutInArea(flipPanel,    (w - s) / 2, (h - s) / 2, s, s, 0, HPos.CENTER, VPos.CENTER);
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
