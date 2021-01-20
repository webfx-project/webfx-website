package dev.webfx.website.application.cards;

import dev.webfx.website.application.SvgLogoPaths;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.SVGPath;

/**
 * @author Bruno Salmon
 */
final class FxWreathPane extends Pane {

    private final SVGPath wreathSVGPath = Card.createLogoSVGPath(SvgLogoPaths.getWreathPath(), LinearGradient.valueOf("to right, brown, orange"));
    private final ScalePane wreathPane = new ScalePane(wreathSVGPath);
    private final ScalePane fxPane = new ScalePane(Card.createFxLogo());

    public FxWreathPane() {
        getChildren().setAll(wreathPane, fxPane);
    }

    SVGPath getWreathSVGPath() {
        return wreathSVGPath;
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth(), h = getHeight(), s;
        //layoutInArea(wreathNode,(w - (s = w      )) / 2, (h - s) / 2, s, s, 0, HPos.CENTER, VPos.CENTER);
        //layoutInArea(fxNode,    (w - (s = w * 0.4)) / 2, (h - s) / 2, s, s, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(wreathPane,(w - (s = h      )) / 2, (h - s) / 2, s, s, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(fxPane,    (w - (s = h * 0.4)) / 2, (h - s) / 2, s, s, 0, HPos.CENTER, VPos.CENTER);
    }

}