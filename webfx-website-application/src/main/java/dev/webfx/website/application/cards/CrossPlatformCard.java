package dev.webfx.website.application.cards;

import dev.webfx.extras.panes.ScaleMode;
import dev.webfx.extras.panes.ScalePane;
import dev.webfx.extras.webtext.SvgText;
import dev.webfx.website.application.images.ImageLoader;
import dev.webfx.website.application.images.SvgLogoPaths;
import dev.webfx.website.application.shared.*;
import javafx.animation.KeyValue;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

import static dev.webfx.website.application.shared.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
public final class CrossPlatformCard extends Card {

    private Pane[] platformsNodes;
    private Pane platformsPane, html5Circle, androidCircle, macOSCircle, raspberryPiCircle, linuxCircle, iOSCircle, windowsCircle;
    private ImageView jdkImageView, gluonImageView;
    private SvgText webFxText;
    private FlipPane flipPane;
    private boolean flipFrontShowing;

    public CrossPlatformCard() {
        super("Cross-platform");
    }

    @Override
    Node createIllustrationNode() {
        FXWreathPane fxPane = createWebFxLogo();
        fxPane.setScaleMode(ScaleMode.BEST_FIT);
        platformsNodes = new Pane[] {
                html5Circle       = createSVGCircle(SvgLogoPaths.getHtml5LogoPath(), html5Color, 0, 2),
                raspberryPiCircle = createSVGCircle(SvgLogoPaths.getRaspberryPiLogoPath(), raspberryPiColor, 0, 2),
                iOSCircle         = createSVGCircle(SvgLogoPaths.getIOSLogoPath(), appleColor, 1, -3),
                androidCircle     = createSVGCircle(SvgLogoPaths.getAndroidLogoPath(), androidColor, 1, 0),
                windowsCircle     = createSVGCircle(SvgLogoPaths.getWindowsLogoPath(), windowsColor, -4, 0),
                macOSCircle       = createSVGCircle(SvgLogoPaths.getApplePath(), appleColor, 0, -3),
                linuxCircle       = createSVGCircle(SvgLogoPaths.getLinuxLogoPath(), Color.BLACK, 2, -1),
                fxPane
        };
        double cr = 64 * 1.4 / 2, osr = 2.5 * cr;
        platformsPane = new Pane(platformsNodes);
        int n = platformsNodes.length - 1;
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n - Math.PI / 2;
            platformsNodes[i].relocate(osr * (1 + Math.cos(angle)), osr * ( 1 + Math.sin(angle)));
        }
        fxPane.relocate(osr - 0.25 * cr, osr - 0.25 * cr);
        fxPane.setPrefSize(2.5 * cr, 2.5 * cr);
        jdkImageView   = createImageView("JDK.png");
        gluonImageView = createImageView("Gluon.png");
        webFxText = createWebFxSvgText();
        flipPane = new FlipPane();
        ScalePane platformScalePane = new ScalePane(setFixedSize(new StackPane(platformsPane), 2 * (osr + cr))); // The StackPane is to isolate scale and rotate transforms, because mixing them doesn't work in the web version due to a transform-origin problem
        ScalePane flipPanelScalePane = new ScalePane(flipPane);
        LayoutPane pane = new LayoutPane(platformScalePane, flipPanelScalePane) {
            @Override
            protected void layoutChildren(double width, double height) {
                double w = width, h = height - getTransitionalTitleSpace();
                layoutInArea(platformScalePane, 0, 0, w, h);
                double sh = platformScalePane.getHeight(), fh = Math.min(90, 0.3 * sh);
                layoutInArea(flipPanelScalePane, 0, h / 2 + 0.3 * sh - fh / 2, w, fh);
            }
        };
        titleText.opacityProperty().addListener(e -> pane.forceLayoutChildren());
        return pane;
    }

    private ImageView createImageView(String resourcePath) {
        ImageView imageView = ImageLoader.loadImage(resourcePath);
        imageView.setEffect(dropShadow);
        // Setting the size of the image (both images are 90x90) to avoid a problem on HTML version (bad position image once loaded because of LayoutPane that doesn't redo layout)
        imageView.setFitWidth(90);
        imageView.setFitHeight(90);
        return imageView;
    }

    private static Pane createSVGCircle(String svgPath, Paint fill, double dx, double dy) {
        SVGPath path = new SVGPath();
        path.setContent(svgPath);
        path.setFill(fill);
        path.setTranslateX(dx);
        path.setTranslateY(dy);
        Pane pane = new StackPane(path);
        double h = 64 * 1.4; // 64 is the height of the SVGPath
        pane.setMinSize(h, h);
        pane.setMaxSize(h, h);
        CornerRadii radii = new CornerRadii(h / 2);
        WebSiteShared.setRegionBackground(pane, CIRCLE_GRADIENT, radii);
        pane.setBorder(new Border(new BorderStroke(Color.GOLD, BorderStrokeStyle.SOLID, radii, BorderStroke.THICK)));
        return pane;
    }

    @Override
    String caption(int step) {
        switch (step) {
            case 1: return "Your WebFX applications will also run natively on desktops, mobiles & embeds.";
            case 2: return "As with any JavaFX application, the JDK toolchain will generate your WebFX application desktop executables (powered by an optimized JVM).";
            case 3: return "In addition, the Gluon toolchain will generate native executables for the desktop (no JVM - your application compiled into native by GraalVM),";
            case 4: return "and also for Android & iOS. Ideal for your front-end development.";
            case 5: return "Gluon can even build your application for Raspberry Pi, with its JavaFX runtime for embeds (more devices to come).";
            case 6: return "And finally, WebFX compiles your application for the Web platform. Perfect for your front-end development";
            case 7: return "7 platforms from a single code base! (check-out the demos for a Github workflow example)";
            default: return null;
        }
    }

    @Override
    void prepareCardTransition(int step, CardTransition cardTransition) {
        super.prepareCardTransition(step, cardTransition);
        double angle = (step == 1 ? 0 : step <= 3 ? 2 : step == 4 ? 4.5 : step == 5 ? 6 : 7) * 360 / 7;
        cardTransition.addKeyValue(new KeyValue(platformsPane.rotateProperty(), angle));
        for (Node platformNode : platformsNodes)
            cardTransition.addKeyValue(new KeyValue(platformNode.rotateProperty(), -angle));
        if (step == 7)
            cardTransition.addOnFinished(() -> {
                platformsPane.setRotate(-360);
                for (Node platformNode : platformsNodes)
                    platformNode.setRotate(360);
                cardTransition.addKeyValue(new KeyValue(platformsPane.rotateProperty(), 0));
                for (Node platformNode : platformsNodes)
                    cardTransition.addKeyValue(new KeyValue(platformNode.rotateProperty(), 0));
                cardTransition.setDurationMillis(6000);
                cardTransition.run(true);
            });
        cardTransition.addKeyValue(
                new KeyValue(html5Circle.opacityProperty(), step == 1 || step >= 6 ? 1 : 0),
                new KeyValue(windowsCircle.opacityProperty(), step <= 3 || step >= 7 ? 1 : 0),
                new KeyValue(macOSCircle.opacityProperty(), step <= 3 || step >= 7 ? 1 : 0),
                new KeyValue(linuxCircle.opacityProperty(), step <= 3 || step >= 7 ? 1 : 0),
                new KeyValue(androidCircle.opacityProperty(), step == 1 || step == 4 || step == 7 ? 1 : 0),
                new KeyValue(iOSCircle.opacityProperty(), step == 1 || step == 4 || step == 7 ? 1 : 0),
                new KeyValue(raspberryPiCircle.opacityProperty(), step == 1 || step == 5 || step == 7 ? 1 : 0),
                new KeyValue(flipPane.opacityProperty(), step >= 2 && step <= 6 ? 1 : 0)
        );
        flipPane.setFront(step <= 3 ? jdkImageView : webFxText);
        flipPane.setBack(step == 2 || step == 7 ? new ImageView() : gluonImageView);
        boolean showFlipFront = step == 2 || step == 6;
        if (showFlipFront != flipFrontShowing) {
            if (showFlipFront)
                flipPane.flipToFront();
            else
                flipPane.flipToBack();
            flipFrontShowing = showFlipFront;
        }
    }
}
