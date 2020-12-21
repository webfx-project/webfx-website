package dev.webfx.website.application.cards;

import dev.webfx.extras.webtext.controls.SvgText;
import dev.webfx.platform.shared.services.resource.ResourceService;
import dev.webfx.website.application.SvgLogoPaths;
import eu.hansolo.enzo.flippanel.FlipPanel;
import javafx.animation.KeyValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

import static dev.webfx.website.application.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
public final class FullyCrossPlatformCard extends Card {

    private Pane[] platformsNodes;
    private Pane platformsPane, html5Circle, androidCircle, macOSCircle, raspberryPiCircle, linuxCircle, iOSCircle, windowsCircle, javaPane;
    private ImageView jdkImageView, gluonImageView;
    private SvgText webFxText;
    private FlipPanel flipPanel;

    public FullyCrossPlatformCard() {
        super("Fully cross-platform");
    }

    @Override
    protected Node createIllustrationNode() {
        platformsNodes = new Pane[] {
                html5Circle       = createSVGCircle(SvgLogoPaths.getHtml5LogoPath(), html5Color, 0, 0),
                raspberryPiCircle = createSVGCircle(SvgLogoPaths.getRaspberryPiLogoPath(), raspberryPiColor, 0, 0),
                iOSCircle         = createSVGCircle(SvgLogoPaths.getIOSLogoPath(), appleColor, 1, -3),
                androidCircle     = createSVGCircle(SvgLogoPaths.getAndroidLogoPath(), androidColor, 1, 0),
                windowsCircle     = createSVGCircle(SvgLogoPaths.getWindowsLogoPath(), windowsColor, -4, 0),
                macOSCircle       = createSVGCircle(SvgLogoPaths.getApplePath(), appleColor, 0, -3),
                linuxCircle       = createSVGCircle(SvgLogoPaths.getLinuxLogoPath(), Color.BLACK, 2, -1),
                javaPane          = createSVGCircle(SvgLogoPaths.getJavaLogoPath(), Color.WHITE, 0, 0)
        };
        platformsPane = new Pane(platformsNodes);
        double cr = 64 * 1.4 / 2, osr = 2.5 * cr;
        int n = platformsNodes.length - 1;
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n - Math.PI / 2;
            platformsNodes[i].relocate(osr + osr * Math.cos(angle), osr + osr * Math.sin(angle));
        }
        javaPane.relocate(osr, osr);
        platformsPane.setMaxSize(2 * (osr + cr), 2 * (osr + cr));
        jdkImageView   = new ImageView(ResourceService.toUrl("JDK.png", getClass()));
        jdkImageView.setEffect(dropShadow);
        gluonImageView = new ImageView(ResourceService.toUrl("Gluon.png", getClass()));
        gluonImageView.setEffect(dropShadow);
        webFxText = createWebFxSvgText(50);
        webFxText.setFill(createGithubGradient(Math.PI));
        flipPanel = new FlipPanel();
        flipPanel.flipToBack();
        flipPanel.setTranslateY(osr);
        return new StackPane(platformsPane, flipPanel);
    }

    private static Pane createSVGCircle(String svgPath, Paint fill, double dx, double dy) {
        SVGPath path = new SVGPath();
        path.setContent(svgPath);
        if (fill != null)
            path.setFill(fill);
        path.setTranslateX(dx);
        path.setTranslateY(dy);
        Pane pane = new BorderPane(path);
        // The pane needs to be reduced to the svg path size (which we can get using the layout bounds).
        path.sceneProperty().addListener((observableValue, scene, t1) -> { // This postpone is necessary only when running in the browser, not in standard JavaFx
            Bounds b = path.getLayoutBounds(); // Bounds computation should be correct now even in the browser
            double h = b.getHeight() * 1.4;
            pane.setMinSize(h, h);
            pane.setMaxSize(h, h);
            if (fill != null && fill != Color.WHITE)
                pane.setBackground(new Background(new BackgroundFill(circleGradient, new CornerRadii(h / 2), null)));
        });
        if (fill != null && fill != Color.WHITE)
            pane.setEffect(dropShadow);
        return pane;
    }

    @Override
    protected String subTitle(int step) {
        switch (step) {
            case 1: return "Your back-end and front-end will also run on desktops, mobiles & embed.";
            case 2: return "The standard JDK toolchain will generate desktop executables of your applications with an optimized JVM and the JavaFX runtime (OpenJFX).";
            case 3: return "The Gluon toolchain will use GraalVM to generate native executables of your applications";
            case 4: return "including for mobiles (Android & iOS).";
            case 5: return "Gluon also provides a JavaFX runtime for embed (Raspberry Pi)";
            case 6: return "And WebFX is here to add the Web platform to this collection.";
            case 7: return "7 platforms from a single source code base! (check-out the demos for a Github workflow example)";
            default: return null;
        }
    }

    @Override
    protected void prepareCardTransition(int step, CardTransition cardTransition) {
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
                new KeyValue(flipPanel.opacityProperty(), step >= 2 && step <= 6 ? 1 : 0)
        );
        flipPanel.getFront().getChildren().setAll(step <= 3 ? jdkImageView : webFxText);
        flipPanel.getBack().getChildren().setAll(step <= 2 || step == 7 ? new ImageView() : gluonImageView);
        if (step == 2 || step == 6)
            flipPanel.flipToFront();
        if (step == 3 || step == 7)
            flipPanel.flipToBack();
    }
}
