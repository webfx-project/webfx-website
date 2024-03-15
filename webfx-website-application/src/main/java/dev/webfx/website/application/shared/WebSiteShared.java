package dev.webfx.website.application.shared;

import dev.webfx.extras.panes.ScalePane;
import dev.webfx.extras.webtext.HtmlText;
import dev.webfx.extras.webtext.SvgText;
import dev.webfx.platform.windowlocation.WindowLocation;
import dev.webfx.website.application.cards.FXWreathPane;
import dev.webfx.website.application.images.ImageLoader;
import dev.webfx.website.application.images.SvgLogoPaths;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class WebSiteShared {

    public static final LinearGradient BACKGROUND_GRADIENT = LinearGradient.valueOf("to bottom, #4C2459, #6F295A");
    //public static final LinearGradient CARD_TRANSLUCENT_BACKGROUND = LinearGradient.valueOf("rgba(0, 0, 0, 0.25), rgba(0, 0, 0, 0.54)");
    public static final LinearGradient CARD_TRANSLUCENT_BACKGROUND = LinearGradient.valueOf("to bottom, #3C1A43, #34132B");
    public static final LinearGradient CIRCLE_GRADIENT = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.gray(0.90)),
            new Stop(1, Color.gray(0.65))
    );
    public static final LinearGradient GITHUB_GRADIENT = LinearGradient.valueOf("to right, #B2F4B6, #3BF0E4, #C2A0FD, #EA5DAD, #FF7571, #FFE580");
    private static final List<Stop> GRADIENT_STOPS = GITHUB_GRADIENT.getStops();
    public static final Color FIRST_GITHUB_GRADIENT_COLOR = GRADIENT_STOPS.get(0).getColor(),
            MIDDLE_GITHUB_GRADIENT_COLOR = GRADIENT_STOPS.get(GRADIENT_STOPS.size() / 2).getColor(),
            LAST_GITHUB_GRADIENT_COLOR = GRADIENT_STOPS.get(GRADIENT_STOPS.size() - 1).getColor();

    public static final DropShadow dropShadow  = new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 10, 0, 8, 8);
    public static final Color javaColor        = Color.rgb(244, 175, 103);
    public static final Color fxColor          = Color.rgb(63,  142, 217);
    public static final Color raspberryPiColor = Color.rgb(197,  32, 73);
    public static final Color html5Color       = Color.rgb(226,  57, 38);
    public static final Color windowsColor     = Color.rgb(47,  116, 212);
    public static final Color androidColor     = Color.rgb(116, 140,  38); //Official color Color.rgb(165, 199, 54) is too bright for the circle background;
    public static final Color appleColor       = Color.grayRgb(30);
    public static final Color gwtColor         = Color.rgb(249,  53, 53);
    public static final Color jsYellowColor    = Color.web("#F0DB4F");

    // Ease out interpolator closer to the web standard than the one proposed in JavaFX (ie Interpolator.EASE_OUT)
    public final static Interpolator EASE_OUT_INTERPOLATOR = Interpolator.SPLINE(0, .75, .25, 1);

    public static SvgText createWebFxSvgText() {
        return setUpText(new SvgText("WebFX"), 50, true, false, true, true);
    }

    public static <T extends Text> T setUpText(T text, double fontSize, boolean bold, boolean white, boolean stroke, boolean shadow) {
        text.setFont(Font.font("Arial", bold ? FontWeight.BOLD : FontWeight.NORMAL, fontSize));
        text.setFill(white ? Color.WHITE : GITHUB_GRADIENT);
        if (stroke) {
            text.setStroke(Color.WHITE);
            text.setStrokeWidth(fontSize >= 70 ? 2 : 1);
        }
        if (shadow)
            text.setEffect(dropShadow);
        return text;
    }

    public static Font htmlTextFont = Font.font("Arial", FontWeight.NORMAL, 22);

    public static HtmlText setHtmlText(HtmlText htmlText, String text) {
        htmlText.setFont(htmlTextFont);
        htmlText.setText(text == null ? null : "<center style='font-style: oblique; line-height: 1.5em;'>" + text + "</center>");
        htmlText.setFill(Color.WHITE);
        htmlText.setMouseTransparent(true);
        return htmlText;
    }

    public static Font updateFontSize(Font font, double fontSize, boolean bold) {
        if (font == null || font.getSize() != fontSize)
            font = Font.font(font == null ?"Arial" : font.getFamily(), bold ? FontWeight.BOLD : FontWeight.NORMAL, fontSize);
        return font;
    }

    public static void updateTextFontSize(Text text, double fontSize, boolean bold) {
        text.setFont(updateFontSize(text.getFont(), fontSize, bold));
    }

    public static void updateTextFontSize(HtmlText text, double fontSize, boolean bold) {
        text.setFont(updateFontSize(text.getFont(), fontSize, bold));
    }

    public static LinearGradient createAngleGithubGradient(double angle) {
        return createAngleGithubGradient(angle, 200, 0);
    }

    public static LinearGradient createAngleGithubGradient(double angle, double length, double shift) {
        shift = shift % length;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new LinearGradient(shift * cos, shift * sin, (shift + length) * cos, (shift + length) * sin, false, CycleMethod.REPEAT, GRADIENT_STOPS);
    }

    public static LinearGradient createVerticalGithubGradiant(double length, double shift) {
        return WebSiteShared.createAngleGithubGradient(-Math.PI / 2, length, shift);
    }

    public static Region setRegionBackground(Region region, Paint fill) {
        return setRegionBackground(region, fill, null);
    }

    public static Region setRegionBackground(Region region, Paint fill, CornerRadii radii) {
        region.setBackground(fill == null || fill == Color.TRANSPARENT ? null : new Background(new BackgroundFill(fill, radii, null)));
        return region;
    }

    public static <T extends Region> T setFixedSize(T region, double wh) {
        return setFixedSize(region, wh, wh);
    }

    public static <T extends Region> T setFixedSize(T region, double w, double h) {
        region.setMaxSize(w, h);
        region.setMinSize(w, h);
        region.setPrefSize(w, h);
        return region;
    }

    public static <N extends Node> N rotate(N node, double angle) {
        node.setRotate(angle);
        return node;
    }

    public static void openUrl(String url) {
        WindowLocation.assignHref(url);
        //hostServices.showDocument(url);
    }

/*
    private static HostServices hostServices;
    public static void setHostServices(HostServices hostServices) {
        WebSiteShared.hostServices = hostServices;
    }
*/

    public static void runOnMouseClick(Node node, Runnable runnable) {
        runOnMouseClick(node, e -> runnable.run());
    }

    public static void runOnMouseClick(Node node, EventHandler<? super MouseEvent> eventHandler) {
        node.setCursor(Cursor.HAND);
        node.setOnMouseClicked(e -> {
            eventHandler.handle(e);
            e.consume();
        });
    }

    public static void setShapeHoverAnimationColor(Shape shape, Color color) {
        Timeline[] timelineHolder = { new Timeline() };
        shape.setOnMouseEntered(e -> {
            timelineHolder[0].stop();
            timelineHolder[0] = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(shape.fillProperty(), color)));
            timelineHolder[0].play();
        });
        Color darker = color.darker();
        shape.setOnMouseExited(e -> {
            timelineHolder[0].stop();
            timelineHolder[0] = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(shape.fillProperty(), darker)));
            timelineHolder[0].play();
        });
        shape.setFill(darker);
    }

    public static Pane createNoLogo() {
        return setLogoId(new StackPane(), "no");
    }

    private static ImageView createImageViewLogo(String file) {
        return setLogoId(ImageLoader.loadImage(file), file);
    }

    public static ImageView createFlutterLogo() {
        return createImageViewLogo("Flutter.png");
    }

    public static ImageView createVueLogo() {
        return createImageViewLogo("Vue.png");
    }

    public static ImageView createReactLogo() {
        return createImageViewLogo("React.png");
    }

    public static ImageView createAngularLogo() {
        return createImageViewLogo("Angular.png");
    }

    public static FXWreathPane createWebFxLogo() {
        SVGPath cloud = createCloud();
        cloud.setTranslateY(-5);
        return setLogoId(new FXWreathPane(cloud), "WebFX");
    }

    public static Pane createJSLogo() {
        SVGPath jsPath = createLogoSVGPath(SvgLogoPaths.getJSLogoPath(), Color.web("#323330"), null);
        Pane jsPane = WebSiteShared.setFixedSize(new StackPane(jsPath), 64);
        setRegionBackground(jsPane, jsYellowColor);
        return setLogoId(jsPane, "JS");
    }

    public static SVGPath createLogoSVGPath(String content, Paint fill) {
        return createLogoSVGPath(content, fill, null);
    }

    static SVGPath createLogoSVGPath(String content, Paint fill, String logoId) {
        SVGPath javaPath = new SVGPath();
        javaPath.setContent(content);
        javaPath.setFill(fill);
        return setLogoId(javaPath, logoId);
    }

    static <N extends Node> N setLogoId(N node, String logoId) {
        node.getProperties().put("logoId", logoId);
        return node;
    }

    public static String getLogoId(Node node) {
        return (String) node.getProperties().get("logoId");
    }

    public static SVGPath createJavaLogo() {
        return createLogoSVGPath(SvgLogoPaths.getJavaLogoPath(), Color.WHITE, "Java");
    }

    public static SVGPath createFxLogo() {
        return createLogoSVGPath(SvgLogoPaths.getFxWordPath(), fxColor, "FX");
    }

    public static SVGPath createGwtLogo() {
        return createLogoSVGPath(SvgLogoPaths.getGwtLogoPath(), gwtColor, "GWT");
    }

    public static SVGPath createGwtText() {
        return createLogoSVGPath(SvgLogoPaths.getGwtTextPath(), gwtColor, "GWT");
    }

    public static ScalePane createFlashLogo() {
        ScalePane pane = new ScalePane(createLogoSVGPath(SvgLogoPaths.getFlashLetterPath(), Color.WHITE));
        pane.setPrefSize(64, 64);
        setRegionBackground(pane, LinearGradient.valueOf("to bottom, #D21921, #4C060A"));
        return pane;
    }

    public static SVGPath createGithubLogo() {
        SVGPath githubSVGPath = createLogoSVGPath(SvgLogoPaths.getGithubLogoPath(), Color.gray(0.2));
        githubSVGPath.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 5, 0, 3, 3));
        githubSVGPath.setStroke(Color.WHITE);
        githubSVGPath.setStrokeWidth(1);
        return githubSVGPath;
    }

    public static SVGPath createBlogLogo() {
        SVGPath githubSVGPath = createLogoSVGPath(SvgLogoPaths.getBlogPath(), Color.gray(0.2));
        githubSVGPath.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 5, 0, 3, 3));
        githubSVGPath.setStroke(Color.WHITE);
        githubSVGPath.setStrokeWidth(1);
        return githubSVGPath;
    }

    public static SVGPath createDocLogo() {
        SVGPath githubSVGPath = createLogoSVGPath(SvgLogoPaths.getDocPath(), Color.gray(0.2));
        githubSVGPath.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 5, 0, 3, 3));
        githubSVGPath.setStroke(Color.WHITE);
        githubSVGPath.setStrokeWidth(1);
        return githubSVGPath;
    }

    public static SVGPath createCloud() {
        SVGPath cloudSVGPath = createLogoSVGPath(SvgLogoPaths.getCloudPath(), Color.gray(1, 0.8));
        cloudSVGPath.setStroke(GITHUB_GRADIENT);
        cloudSVGPath.setStrokeWidth(4);
        return cloudSVGPath;
    }

    public static SVGPath createArrowUp() {
        SVGPath arrowUpSVGPath = createLogoSVGPath(SvgLogoPaths.getArrowUpPath(), null);
        arrowUpSVGPath.setStroke(createAngleGithubGradient(0));
        arrowUpSVGPath.setStrokeWidth(4);
        arrowUpSVGPath.setFill(Color.WHITE);
        return arrowUpSVGPath;
    }

    public static SVGPath createArrowDown() {
        SVGPath arrowUpSVGPath = createLogoSVGPath(SvgLogoPaths.getArrowDownPath(), null);
        arrowUpSVGPath.setStroke(createAngleGithubGradient(0));
        arrowUpSVGPath.setStrokeWidth(4);
        arrowUpSVGPath.setFill(Color.WHITE);
        return arrowUpSVGPath;
    }

    public static SVGPath createMedal() {
        return createLogoSVGPath(SvgLogoPaths.getMedalPath(), Color.GOLD);
    }

    public static SVGPath createThumbUp() {
        return createLogoSVGPath(SvgLogoPaths.getThumbUpPath(), fxColor);
    }

    public static ImageView createJQueryLogo() {
        return createImageViewLogo("JQuery.png");
    }

    public static ImageView createSilverlightLogo() {
        return createImageViewLogo("Silverlight.png");
    }

    public static ImageView createBackboneLogo() {
        return createImageViewLogo("Backbone.png");
    }

    public static ImageView createEmberLogo() {
        return createImageViewLogo("Ember.png");
    }

    public static ImageView createMeteorLogo() {
        return createImageViewLogo("Meteor.png");
    }

    public static HBox createJavaFxLogo() {
        SVGPath javaPath = new SVGPath();
        javaPath.setContent(SvgLogoPaths.getJavaWordPath());
        javaPath.setFill(javaColor);
        javaPath.setTranslateY(12);
        //javaPath.setEffect(WebSiteShared.dropShadow);
        SVGPath fxPath = new SVGPath();
        fxPath.setContent(SvgLogoPaths.getFxWordPath());
        fxPath.setFill(fxColor);
        //fxPath.setEffect(WebSiteShared.dropShadow);
        HBox javaFxHBox = new HBox(5, javaPath, fxPath);
        javaFxHBox.setAlignment(Pos.CENTER);
        return javaFxHBox;
    }
}
