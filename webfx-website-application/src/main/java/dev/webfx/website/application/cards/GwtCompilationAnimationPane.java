package dev.webfx.website.application.cards;

import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.extras.panes.ScalePane;
import dev.webfx.website.application.shared.WebSiteShared;
import javafx.animation.KeyValue;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

import java.util.stream.Stream;

import static dev.webfx.website.application.shared.WebSiteShared.jsYellowColor;

/**
 * @author Bruno Salmon
 */
final class GwtCompilationAnimationPane extends LayoutPane {

    private final Region[] javaFxClasses = Stream.generate(() -> WebSiteShared.setRegionBackground(new Region(), WebSiteShared.raspberryPiColor)).limit(16 * 8).toArray(Region[]::new);
    private final int[] nonDeadIndexes = {21, 25, 39, 42, 52, 60, 72, 82, 85, 90, 93, 104, 116, 124};
    private boolean showGrid;
    private final LayoutPane javaFxClassesPane = new LayoutPane(javaFxClasses) {
        private final Circle circleClip = new Circle(); { setClip(circleClip); }
        @Override
        protected void layoutChildren(double width, double height) {
            double xc = width / 2, yc = height;
            double radius = Math.min(xc, yc);
            circleClip.setCenterX(xc);
            circleClip.setCenterY(yc);
            circleClip.setRadius(radius);
            double jfxSize = radius / 8;
            for (int i = 0; i < 16 * 8; i++) {
                int row = i / 16, col = i % 16;
                layoutInArea(javaFxClasses[i], xc - radius + col * jfxSize, yc - radius + row * jfxSize, jfxSize - (showGrid ? 2 : 0), jfxSize - (showGrid ? 2 : 0));
            }
        }
    };
    private final CirclePane javaFxCirclePane = new CirclePane("JavaFX",90, WebSiteShared.raspberryPiColor, null, null);
    private final CirclePane appCirclePane = new CirclePane("Your App",90, Color.rgb(89, 36, 189), WebSiteShared.createGwtLogo(), null);
    private final Pane appClipPane = new StackPane();
    private final SVGPath gwtLogo = WebSiteShared.createGwtLogo();
    private final ScalePane gwtLogoPane = new ScalePane(gwtLogo);
    private final Pane jsLogo = WebSiteShared.createJSLogo();
    private final ScalePane jsLogoPane = new ScalePane(jsLogo);
    private final Region jsFlow = new Region();
    private final DoubleProperty jsFlowFillPercent = new SimpleDoubleProperty(1) {
        @Override
        protected void invalidated() {
            boolean flowStart = get() >= 0; // negative value for flow end
            fillRegion(jsFlow, flowStart ? jsYellowColor : Color.TRANSPARENT, flowStart ? Color.TRANSPARENT : jsYellowColor, Math.abs(get()), false);
        }
    };
    private final DoubleProperty jsLogoFillPercent = new SimpleDoubleProperty(1) {
        @Override
        protected void invalidated() {
            fillRegion(jsLogo, jsYellowColor, Color.TRANSPARENT, get(), true);
        }
    };
    private final Region redCross1 = new Region(), redCross2 = new Region();
    private final DoubleProperty redCross1FillPercent = new SimpleDoubleProperty(1) {
        @Override
        protected void invalidated() {
            fillRegion(redCross1, Color.RED, Color.TRANSPARENT, get(), false);
        }
    };
    private final DoubleProperty redCross2FillPercent = new SimpleDoubleProperty(1) {
        @Override
        protected void invalidated() {
            fillRegion(redCross2, Color.RED, Color.TRANSPARENT, get(), false);
        }
    };
    private final ObjectProperty<Paint> nonDeadCodeFillProperty = new SimpleObjectProperty<Paint>(WebSiteShared.raspberryPiColor) {
        @Override
        protected void invalidated() {
            for (int i : nonDeadIndexes)
                WebSiteShared.setRegionBackground(javaFxClasses[i], get());
        }
    };
    private final ObjectProperty<Paint> deadCodeFillProperty = new SimpleObjectProperty<Paint>(WebSiteShared.raspberryPiColor) {
        @Override
        protected void invalidated() {
            for (int i = 0, j = 0; i < javaFxClasses.length; i++)
                if (j < nonDeadIndexes.length && i == nonDeadIndexes[j])
                    j++;
                else
                    WebSiteShared.setRegionBackground(javaFxClasses[i], get());
        }
    };
    private final Rectangle mainClip = new Rectangle();
    private final Rectangle appClip = new Rectangle();

    GwtCompilationAnimationPane() {
        initAnim(0, null);
    }

    static void fillRegion(Region region, Color color1, Color color2, double percent, boolean up) {
        Paint fill = percent <= 0 ? color2 : percent >= 1 ? color1 : new LinearGradient(0, up ? 1 : 0, 0, up ? 0 : 1, true, CycleMethod.NO_CYCLE, new Stop(0, color1), new Stop(percent, color1), new Stop(percent, color2), new Stop(1, color2));
        WebSiteShared.setRegionBackground(region, fill);
    }

    @Override
    protected void layoutChildren(double width, double height) {
        double xc = width / 2, yc = height / 2;
        double radius = Math.min(xc, yc);
        appCirclePane.setRadius(0.75 * radius);
        if (!appClipPane.isVisible()) {
            javaFxCirclePane.setRadius(radius);
            centerInArea(appCirclePane,     0, 0, width, height);
            centerInArea(javaFxCirclePane,  0, 0, width, height);
            centerInArea(javaFxClassesPane, 0, 0, width, yc);
            double rw = 0.1 * radius, rh = 0.2 * height, rx = xc - rw / 2, ry = yc + 0.3 * radius - rh / 2;
            centerInArea(redCross1, rx, ry, rw, rh);
            centerInArea(redCross2, rx, ry, rw, rh);
        } else {
            // App layout
            double ax = 0, ay = 0, aw = width, ah = yc;
            appClip.setWidth(aw);
            appClip.setHeight(ah);
            appClipPane.setMinSize(aw, ah); // Necessary
            // Gwt layout
            double gw = width, gh = ah, gx = 0, gy = yc - gh / 2;
            // JS logo pane layout
            double lx = 0, ly = gy + gh + height / 20, lw = width, lh = height - ly;
            // JS flow layout
            double fw = Math.min(lw, lh) * 0.25, fx = width / 2 - fw / 2, fy = gy + gh / 2, fh = height - fy;
            layoutInArea(appClipPane, ax, ay, aw, ah);
            layoutInArea(gwtLogoPane, gx, gy, gw, gh);
            layoutInArea(jsLogoPane,  lx, ly, lw, lh);
            layoutInArea(jsFlow,      fx, fy, fw, fh);
        }
        mainClip.setWidth(width);
        mainClip.setHeight(height);
    }

    private void showGrid(boolean showGrid) {
        if (this.showGrid != showGrid) {
            this.showGrid = showGrid;
            javaFxClassesPane.forceLayoutChildren();
        }
    }

    void playJavaFxRenderingLayerRemoval(CardTransition cardTransition) {
        initAnim(0, cardTransition);
    }

    void playDeadCodeRemoval(CardTransition cardTransition) {
        initAnim(10, cardTransition);
    }

    void playNonDeadCodeIntegration(CardTransition cardTransition) {
        initAnim(20, cardTransition);
    }

    private void initAnim(int animationStep, CardTransition cardTransition) {
        getChildren().setAll(appCirclePane, javaFxClassesPane, javaFxCirclePane, redCross1, redCross2);
        redCross1.setRotate(-45);
        redCross2.setRotate(+45);
        setClip(mainClip);
        appClipPane.setClip(appClip);

        this.animationStep = animationStep;
        redCross1FillPercent.set(0);
        redCross2FillPercent.set(0);
        redCross1.setOpacity(1);
        redCross2.setOpacity(1);
        javaFxCirclePane.setOpacity(1);
        if (animationStep == 0)
            javaFxCirclePane.setFill(WebSiteShared.raspberryPiColor);

        if (animationStep < 20) {
            nonDeadCodeFillProperty.set(WebSiteShared.raspberryPiColor);
            deadCodeFillProperty.set(WebSiteShared.raspberryPiColor);
            showGrid(false);
        }
        javaFxClassesPane.setScaleX(1);
        javaFxClassesPane.setScaleY(1);
        appCirclePane.setVisible(false);
        appClipPane.setVisible(false);
        jsLogoFillPercent.set(0);
        jsFlowFillPercent.set(0);
        if (cardTransition != null)
            cardTransition.addOnFinished(() -> runNextAnimationStep(cardTransition));
    }

    private int animationStep; // 1 = , 2 = fill red cross 1, 3 = fill red cross 2, 4 = fade renderer layer, 4 = fade red crosses
    private int scanIndex, scanNonDeadIndex;

    private void runNextAnimationStep(CardTransition cardTransition) {
        switch (++animationStep) {
            case 1: // Half fading JavaFX renderer layer
                cardTransition.addKeyValue(new KeyValue(javaFxCirclePane.fillProperty(), WebSiteShared.raspberryPiColor.deriveColor(0, 1, 1, 0.5)));
                break;
            case 2: // Filling red cross 1
                cardTransition.addKeyValue(new KeyValue(redCross1FillPercent, 1));
                break;
            case 3: // Filling red cross 1
                cardTransition.addKeyValue(new KeyValue(redCross2FillPercent, 1));
                break;
            case 4: // Fully fading JavaFX renderer layer
                cardTransition.addKeyValue(new KeyValue(javaFxCirclePane.fillProperty(), WebSiteShared.raspberryPiColor.deriveColor(0, 1, 1, 0.01)));
                break;
            case 11: // Fading red crosses
                cardTransition.addKeyValue(
                        new KeyValue(redCross1.opacityProperty(), 0),
                        new KeyValue(redCross2.opacityProperty(), 0)
                );
                break;
            case 12: // Showing the JavaFX classes grid
                showGrid(true);
                cardTransition.setDurationMillis(60);
                scanIndex = scanNonDeadIndex = 0;
                break;
            case 13: // Coloring next JavaFX class (with dead or non-dead color)
                if (scanIndex < javaFxClasses.length) {
                    Paint fill = Color.gray(0.4, 0.4);
                    if (scanNonDeadIndex < nonDeadIndexes.length && scanIndex == nonDeadIndexes[scanNonDeadIndex]) {
                        scanNonDeadIndex++;
                        fill = Color.SANDYBROWN;
                    }
                    WebSiteShared.setRegionBackground(javaFxClasses[scanIndex++], fill);
                    animationStep--;
                }
                break;
            case 14: // Fading dead classes
                nonDeadCodeFillProperty.setValue(Color.SANDYBROWN);
                cardTransition.setDurationMillis(1000);
                cardTransition.addKeyValue(new KeyValue(deadCodeFillProperty, Color.TRANSPARENT));
                break;
            case 21: // Fading JavaFX circle pane
                cardTransition.addKeyValue(new KeyValue(javaFxCirclePane.opacityProperty(), 0));
                break;
            case 22: // Moving App from top to JavaFX classes
                appCirclePane.setVisible(true);
                double radius = Math.min(getWidth(), getHeight()) / 2;
                appCirclePane.setTranslateY(-getHeight() / 2 - radius);
                cardTransition.addKeyValue(new KeyValue(appCirclePane.translateYProperty(), - radius / 2));
                break;
            case 23: // Dissolving JavaFX classes into the App
                cardTransition.addKeyValue(
                        new KeyValue(javaFxClassesPane.scaleXProperty(), 0),
                        new KeyValue(javaFxClassesPane.scaleYProperty(), 0)
                );
                break;
            case 24: // Moving App to the bottom
                radius = Math.min(getWidth(), getHeight()) / 2;
                cardTransition.addKeyValue(new KeyValue(appCirclePane.translateYProperty(), getHeight() / 2 + radius));
                break;
            case 25: // App descends from the top
                // Changing children set
                getChildren().clear(); // Required for the web version on subsequent pass TODO: investigate why
                getChildren().setAll(appClipPane, jsFlow, gwtLogoPane, jsLogoPane);
                appClipPane.getChildren().clear();  // Required for the web version on subsequent pass TODO: investigate why
                appClipPane.getChildren().setAll(appCirclePane);
                appClipPane.setVisible(true);
                cardTransition.setDurationMillis(1000);
                radius = 0.75 * Math.min(getWidth(), getHeight()) / 2;
                appCirclePane.setTranslateY(0 - getHeight() / 4 - radius);
                appCirclePane.setVisible(true);
                cardTransition.addKeyValue(
                        new KeyValue(appCirclePane.translateYProperty(), 0.25 * getHeight() - radius)
                );
                break;
            case 26: // JS flow starts
                radius = 0.75 * Math.min(getWidth(), getHeight()) / 2;
                cardTransition.setDurationMillis(500);
                cardTransition.addKeyValue(
                        new KeyValue(appCirclePane.translateYProperty(), 0.25 * getHeight() - 0.8 * radius),
                        new KeyValue(jsFlowFillPercent, 1)
                );
                break;
            case 27: // JS logo fills
                radius = 0.75 * Math.min(getWidth(), getHeight()) / 2;
                cardTransition.setDurationMillis(5000);
                cardTransition.addKeyValue(
                        new KeyValue(appCirclePane.translateYProperty(), 0.25 * getHeight() + 1.1 * radius),
                        new KeyValue(jsLogoFillPercent, 0.9)
                );
                break;
            case 28: // JS flow stops
                jsFlowFillPercent.set(0);
                cardTransition.setDurationMillis(400);
                cardTransition.addKeyValue(
                        new KeyValue(jsLogoFillPercent, 1),
                        new KeyValue(jsFlowFillPercent, -1.01 * ((jsLogoPane.getLayoutY() - jsFlow.getLayoutY()) / jsFlow.getHeight()))
                );
                break;
            default: return;
        }
        cardTransition.addOnFinished(() -> runNextAnimationStep(cardTransition));
        cardTransition.run(true);
    }
}
