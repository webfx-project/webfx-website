package dev.webfx.website.application.cards;

import dev.webfx.extras.webtext.controls.HtmlText;
import dev.webfx.platform.shared.services.resource.ResourceService;
import dev.webfx.website.application.SvgLogoPaths;
import dev.webfx.website.application.WebSiteShared;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

/**
 * @author Bruno Salmon
 */
public abstract class Card extends Pane {

    private final static Border cardBorder = new Border(new BorderStroke(Color.grayRgb(255, 0.5), BorderStrokeStyle.SOLID, new CornerRadii(10), BorderStroke.THIN));
    private double cardWidth, maxTitleHeight, maxHtmlHeight;

    protected final Node illustrationNode;
    protected final Text titleText;
    protected final HtmlText htmlText1 = new HtmlText(), htmlText2 = new HtmlText();
    protected int currentAnimationStep = 0;
    protected boolean useHiddenTitleSpaceForIllustration = true, smoothIllustrationMove = true;
    private CardTransition cardTransition;

    Card(String title) {
        setBorder(cardBorder);
        titleText = WebSiteShared.setUpText(new Text(title + " \u2192"), 30, true, true, false, true);
        illustrationNode = createIllustrationNode();
        htmlText1.setMouseTransparent(true);
        htmlText2.setMouseTransparent(true);
        getChildren().setAll(illustrationNode, titleText, htmlText1, htmlText2);
        setCursor(Cursor.HAND);
        transitionToNextStep();
        setOnMouseClicked(e -> transitionToNextStep());
        setUpCardClip();
        titleText.opacityProperty().addListener((observableValue, number, t1) -> onTitleOpacityChanged());
    }

    void setUpCardClip() {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);
    }

    abstract Node createIllustrationNode();

    void transitionToNextStep() {
        boolean animateTransition = currentAnimationStep > 0;
        if (caption(++currentAnimationStep) == null)
            currentAnimationStep = 1;
        if (cardTransition != null)
            cardTransition.stop();
        cardTransition = new CardTransition();
        prepareCardTransition(currentAnimationStep, cardTransition);
        cardTransition.run(animateTransition);
    }

    void prepareCardTransition(int step, CardTransition cardTransition) {
        cardTransition.addKeyValue(new KeyValue(titleText.opacityProperty(), step == 1 ? 1 : 0));
        boolean enteringTextIs1 = htmlText2.getTranslateX() == 0;
        HtmlText enteringText = enteringTextIs1 ? htmlText1 : htmlText2;
        HtmlText leavingText  = enteringTextIs1 ? htmlText2 : htmlText1;
        String caption = caption(step);
        WebSiteShared.setHtmlText(enteringText, caption == null ? null : caption + (step > 1 && caption(step + 1) != null ? " \u2192" : ""));
        double width = getWidth();
        enteringText.setTranslateX(width);
        cardTransition.addKeyValue(
                new KeyValue(enteringText.translateXProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(leavingText.translateXProperty(), -width, Interpolator.EASE_OUT)
        );
        enteringText.setVisible(true);
        cardTransition.addOnFinished(() -> leavingText.setVisible(false));
    }

    abstract String caption(int step);

    @Override
    protected void layoutChildren() {
        double w = getWidth() - 20, h = getHeight();
        if (w != cardWidth) {
            maxTitleHeight = maxHtmlHeight = 0;
            for (Card card : WebSiteShared.cards) {
                maxTitleHeight = Math.max(card.titleText.prefHeight(w), maxTitleHeight);
                String longestCaption = "";
                for (int step = 1; card.caption(step) != null; step++) {
                    String caption = card.caption(step);
                    if (caption.length() > longestCaption.length())
                        longestCaption = caption;
                }
                HtmlText htmlText = card.htmlText1.getTranslateX() == 0 ? card.htmlText2 : card.htmlText1;
                String displayedText = htmlText.getText();
                WebSiteShared.setHtmlText(htmlText, longestCaption);
                maxHtmlHeight = Math.max(htmlText.prefHeight(w), maxHtmlHeight);
                htmlText.setText(displayedText);
            }
            cardWidth = w;
        }
        double ny = h;
        double nh = maxHtmlHeight;
        ny -= nh + 10;
        htmlText1.setMaxHeight(htmlText1.prefHeight(w));
        layoutInArea(htmlText1, 10, ny, w, nh, 0, HPos.CENTER, VPos.CENTER);
        htmlText2.setMaxHeight(htmlText2.prefHeight(w));
        layoutInArea(htmlText2, 10, ny, w, nh, 0, HPos.CENTER, VPos.CENTER);
        nh = maxTitleHeight;
        ny -= nh + 10;
        layoutInArea(titleText, 10, ny, w, nh, 0, HPos.CENTER, VPos.TOP);
        double titleOpacity = titleText.getOpacity();
        if (useHiddenTitleSpaceForIllustration && titleOpacity < 1)
            ny += (nh + 10) * (smoothIllustrationMove ? 1 - titleOpacity : 1); // Smoothing the transition with opacity
        nh = ny - 30;
        ny = 10;
        layoutInArea(illustrationNode, 10, ny, w, nh, 0, HPos.CENTER, VPos.CENTER);
    }

    private void onTitleOpacityChanged() {
        if (useHiddenTitleSpaceForIllustration && smoothIllustrationMove)
            layoutChildren();
    }

    // Static utility methods

    static Pane createNoLogo() {
        return setLogoId(new StackPane(), "no");
    }

    private static ImageView createImageViewLogo(String file) {
        return setLogoId(new ImageView(ResourceService.toUrl(file, Card.class)), file);
    }

    static ImageView createQtLogo() {
        return createImageViewLogo("Qt.png");
    }

    static ImageView createFlutterLogo() {
        return createImageViewLogo("Flutter.png");
    }

    static ImageView createVueLogo() {
        return createImageViewLogo("Vue.png");
    }

    static ImageView createReactLogo() {
        return createImageViewLogo("React.png");
    }

    static ImageView createAngularLogo() {
        return createImageViewLogo("Angular.png");
    }

    static Pane createJSLogo() {
        SVGPath jsPath = new SVGPath();
        jsPath.setContent(SvgLogoPaths.getJSLogoPath());
        jsPath.setFill(Color.BLACK);
        Pane jsPane = new StackPane(jsPath);
        jsPane.setMaxSize(64, 64);
        WebSiteShared.setBackground(jsPane, Color.web("#f7df1e"));
        return setLogoId(jsPane, "JS");
    }

    static SVGPath createLogoSVGPath(String content, Paint fill) {
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

    static String getLogoId(Node node) {
        return (String) node.getProperties().get("logoId");
    }

    static SVGPath createJavaLogo() {
        return createLogoSVGPath(SvgLogoPaths.getJavaLogoPath(), Color.WHITE, "Java");
    }

    static SVGPath createFxLogo() {
        return createLogoSVGPath(SvgLogoPaths.getFxWordPath(), WebSiteShared.fxColor, "FX");
    }

    static ScalePane createFlashLogo() {
        ScalePane pane = new ScalePane(createLogoSVGPath(SvgLogoPaths.getFlashLetterPath(), Color.WHITE));
        pane.setPrefSize(64, 64);
        WebSiteShared.setBackground(pane, LinearGradient.valueOf("to bottom, #D21921, #4C060A"));
        return pane;
    }

    static SVGPath createCloud() {
        SVGPath cloudSVGPath = createLogoSVGPath(SvgLogoPaths.getCloudPath(), null);
        cloudSVGPath.setStroke(WebSiteShared.githubGradient);
        cloudSVGPath.setStrokeWidth(4);
        cloudSVGPath.setFill(Color.gray(1, 0.9));
        return cloudSVGPath;
    }

    static SVGPath createArrowUp() {
        SVGPath arrowUpSVGPath = createLogoSVGPath(SvgLogoPaths.getArrowUpPath(), null);
        arrowUpSVGPath.setStroke(WebSiteShared.createAngleGithubGradient(0));
        arrowUpSVGPath.setStrokeWidth(4);
        arrowUpSVGPath.setFill(Color.WHITE);
        return arrowUpSVGPath;
    }

    static SVGPath createThumbUp() {
        return createLogoSVGPath(SvgLogoPaths.getThumbUpPath(), Color.WHITE);
    }

    static SVGPath createThumbDown() {
        SVGPath thumb = createThumbUp();
        Rotate rotate = new Rotate(180, Rotate.X_AXIS);
        rotate.setPivotY(thumb.getLayoutBounds().getHeight() / 2);
        thumb.getTransforms().add(rotate);
        return thumb;
    }

    static ImageView createJQueryLogo() {
        return createImageViewLogo("JQuery.png");
    }

    static ImageView createSilverlightLogo() {
        return createImageViewLogo("Silverlight.png");
    }

    static ImageView createBackboneLogo() {
        return createImageViewLogo("Backbone.png");
    }

    static ImageView createEmberLogo() {
        return createImageViewLogo("Ember.png");
    }

    static ImageView createMeteorLogo() {
        return createImageViewLogo("Meteor.png");
    }

    static ImageView createDartLogo() {
        return createImageViewLogo("Dart.png");
    }

    static ImageView createCppLogo() {
        return createImageViewLogo("Cpp.png");
    }

    static ImageView createPythonLogo() {
        return createImageViewLogo("Python.png");
    }

    static HBox createJavaFxLogo() {
        SVGPath javaPath = new SVGPath();
        javaPath.setContent(SvgLogoPaths.getJavaWordPath());
        javaPath.setFill(WebSiteShared.javaColor);
        javaPath.setTranslateY(12);
        //javaPath.setEffect(WebSiteShared.dropShadow);
        SVGPath fxPath = new SVGPath();
        fxPath.setContent(SvgLogoPaths.getFxWordPath());
        fxPath.setFill(WebSiteShared.fxColor);
        //fxPath.setEffect(WebSiteShared.dropShadow);
        HBox javaFxHBox = new HBox(5, javaPath, fxPath);
        javaFxHBox.setAlignment(Pos.CENTER);
        return javaFxHBox;
    }
}
