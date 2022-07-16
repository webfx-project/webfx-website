package dev.webfx.website.application.cards;

import dev.webfx.website.application.shared.WebSiteShared;
import dev.webfx.website.application.images.SvgLogoPaths;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.ScalePane;
import javafx.animation.KeyValue;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
final class WebFXDevAnimationPane extends LayoutPane {

    private final SVGPath downArrow = WebSiteShared.createArrowDown(), rightArrow = WebSiteShared.createArrowUp(); // Will be rotated in constructor
    private final ScalePane rightArrowPane = new ScalePane(rightArrow);
    private final CirclePane openJFXCirclePane = new CirclePane("OpenJFX",-90, WebSiteShared.raspberryPiColor, sandyText("API"), sandyText("{...}"));
    private final CirclePane webFXCirclePane   = new CirclePane("WebFX",  -90, Color.PURPLE, WebSiteShared.createGwtLogo(), WebSiteShared.createLogoSVGPath(SvgLogoPaths.getHtmlFramePath(), WebSiteShared.html5Color));
    private final CirclePane appCirclePane     = new CirclePane("Your App",90, Color.rgb(89, 36, 189), WebSiteShared.createGwtLogo(), downArrow);
    private final Arc openJFXArc = createArc(), webFXArc = createArc();
    private double fullAngle = 30;
    private final DoubleProperty anglePercentProperty = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            updateAppAngle();
        }
    };

    private void updateAppAngle() {
        appCirclePane.setRotate(fullAngle * anglePercentProperty.get());
    }

    public WebFXDevAnimationPane() {
        getChildren().setAll(openJFXCirclePane, openJFXArc, webFXCirclePane, webFXArc, rightArrowPane, appCirclePane);
        downArrow.setOpacity(0);
        rightArrow.setRotate(90);
        webFXArc.visibleProperty().bind(openJFXArc.visibleProperty());
        webFXArc.lengthProperty().bind(openJFXArc.lengthProperty());
    }

    private static Text sandyText(String s) {
        Text text = new Text(s);
        text.setFill(Color.SANDYBROWN);
        return text;
    }

    private static Arc createArc() {
        Arc arc = new Arc();
        arc.setStartAngle(90);
        arc.setFill(Color.grayRgb(255, 0.5));
        arc.setType(ArcType.ROUND);
        arc.setVisible(false);
        return arc;
    }

    private static void setArcRadius(Arc arc, double radius) {
        arc.setRadiusX(radius);
        arc.setRadiusY(radius);
        arc.setCenterX(radius);
        arc.setCenterY(radius);
    }

    @Override
    protected void layoutChildren(double width, double height) {
        double w = width, h = height, wd2 = w / 2, hd2 = h / 2;
        double d = 0.98 * Math.min(wd2, hd2), r = d / 2;
        appCirclePane.setRadius(r);
        downArrow.setTranslateY(0.8 * r);
        layoutInArea(appCirclePane, wd2 - r, 0, d, hd2);
        openJFXCirclePane.setRadius(r);
        centerInArea(openJFXCirclePane, 0, hd2, wd2, hd2);
        setArcRadius(openJFXArc, r);
        Bounds ba = openJFXArc.getLayoutBounds();
        double ya = 1.5 * hd2 - r, xac = -Math.min(ba.getWidth(), r), ha = ba.getHeight();
        centerInArea(openJFXArc, 0.5 * wd2 + xac, ya, ba.getWidth(), ha);
        webFXCirclePane.setRadius(r);
        centerInArea(webFXCirclePane, wd2, hd2, wd2, hd2);
        setArcRadius(webFXArc, r);
        centerInArea(webFXArc, 1.5 * wd2 + xac, ya, ba.getWidth(), ha);
        double sa = 0.7 * r;
        centerInArea(rightArrowPane, wd2 - sa / 2, 1.5 * hd2 - sa / 2, sa, sa);
        fullAngle = Math.atan(0.5 * wd2 / hd2) / Math.PI * 180;
        updateAppAngle();
    }

    void showRightArrow(CardTransition cardTransition) {
        cardTransition.addKeyValue(new KeyValue(rightArrow.opacityProperty(), 1));
    }

    void hideRightArrow(CardTransition cardTransition) {
        cardTransition.addKeyValue(new KeyValue(rightArrow.opacityProperty(), 0));
    }

    void playRotateAppLeft(CardTransition cardTransition) {
        cardTransition.addKeyValue(
                new KeyValue(downArrow.opacityProperty(), 1),
                new KeyValue(anglePercentProperty, 1)
        );
    }

    void playRotateAppRight(CardTransition cardTransition) {
        cardTransition.addKeyValue(
                new KeyValue(downArrow.opacityProperty(), 1),
                new KeyValue(anglePercentProperty, -1)
        );
    }

    void playRotateAppCenter(CardTransition cardTransition) {
        cardTransition.addKeyValue(
                new KeyValue(downArrow.opacityProperty(), 0),
                new KeyValue(anglePercentProperty, 0)
        );
    }

    void playArcs(CardTransition cardTransition) {
        cardTransition.addOnFinished(() -> {
            openJFXArc.setVisible(true);
            openJFXArc.setLength(360);
            cardTransition.setDurationMillis(10_000);
            cardTransition.addKeyValue(new KeyValue(openJFXArc.lengthProperty(), 0));
            cardTransition.run(true);
        });
    }

    void hideArcs() {
        openJFXArc.setVisible(false);
    }
}
