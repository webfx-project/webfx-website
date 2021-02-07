package dev.webfx.website.application.cards;

import dev.webfx.extras.webtext.controls.HtmlText;
import dev.webfx.platform.shared.services.resource.ResourceService;
import dev.webfx.website.application.SvgLogoPaths;
import dev.webfx.website.application.WebSiteShared;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
public abstract class Card extends Pane {

    private final static Border CARD_BORDER = new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(10), BorderStroke.THIN));
    private static double cardWidth, cardHeight, maxTitleHeight, maxHtmlHeight;

    protected final Node illustrationNode;
    protected final Text titleText;
    protected final HtmlText captionText1 = new HtmlText(), captionText2 = new HtmlText();
    protected int currentAnimationStep = 0;
    protected boolean forwardingStep;
    protected boolean alwaysUseTitleSpaceForIllustration = false, smoothIllustrationMove = true;
    private CardTransition cardTransition;
    private final String longestCaption;
    protected final DoubleProperty titleSpacePercent = new SimpleDoubleProperty(1) {
        @Override
        protected void invalidated() {
            requestLayout();
        }
    };

    Card(String title) {
        setBorder(CARD_BORDER);
        WebSiteShared.setRegionBackground(this, Color.grayRgb(0, 0.25), CARD_BORDER.getStrokes().get(0).getRadii());
        String longestCaption = "";
        for (int step = 1; caption(step) != null; step++) {
            String caption = caption(step);
            if (caption.length() > longestCaption.length())
                longestCaption = caption;
        }
        this.longestCaption = longestCaption;
        titleText = WebSiteShared.setUpText(new Text(title + " \u2192"), 30, true, true, false, true);
        illustrationNode = createIllustrationNode();
        captionText1.setMouseTransparent(true);
        captionText2.setMouseTransparent(true);
        getChildren().setAll(illustrationNode, titleText, captionText1, captionText2);
        transitionToNextStep();
        WebSiteShared.runOnMouseClick(this, e -> doStepTransition(!e.isControlDown()));
        setUpCardClip();
    }

    void setUpCardClip() {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);
    }

    abstract Node createIllustrationNode();

    public void transitionToNextStep() {
        doStepTransition(true);
    }

    public void transitionToPreviousStep() {
        doStepTransition(false);
    }

    private void doStepTransition(boolean forwardStep) {
        if (currentAnimationStep == 1 && !forwardStep)
            return;
        this.forwardingStep = forwardStep;
        boolean animateTransition = !forwardStep || currentAnimationStep > 0;
        currentAnimationStep += forwardStep ? 1 : -1;
        if (forwardStep && caption(currentAnimationStep) == null)
            currentAnimationStep = 1;
        if (cardTransition != null)
            cardTransition.stop();
        cardTransition = new CardTransition();
        prepareCardTransition(currentAnimationStep, cardTransition);
        cardTransition.run(animateTransition);
    }

    void prepareCardTransition(int step, CardTransition cardTransition) {
        cardTransition.addKeyValue(new KeyValue(titleText.opacityProperty(), step == 1 ? 1 : 0));
        boolean enteringTextIs1 = captionText2.getTranslateX() == 0;
        HtmlText enteringCaptionText = enteringTextIs1 ? captionText1 : captionText2;
        HtmlText leavingCaptionText  = enteringTextIs1 ? captionText2 : captionText1;
        String caption = caption(step);
        WebSiteShared.setHtmlText(enteringCaptionText, caption == null ? null : caption + (step > 1 && caption(step + 1) != null ? " \u2192" : ""));
        double width = getWidth();
        double initialEnteringX = forwardingStep ? width : -width;
        enteringCaptionText.setTranslateX(initialEnteringX);
        cardTransition.addKeyValue(
                new KeyValue(enteringCaptionText.translateXProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(leavingCaptionText.translateXProperty(), -initialEnteringX, Interpolator.EASE_OUT)
        );
        enteringCaptionText.setVisible(true);
        cardTransition.addOnFinished(() -> leavingCaptionText.setVisible(false));
        requestLayout(); // Since we have changed the content of the caption, it is necessary to request a layout
    }

    abstract String caption(int step);

    @Override
    protected void layoutChildren() {
        double w = getWidth(), h = getHeight(), hgap = w * 0.02;
        w -= 2 * hgap;
        if (w != cardWidth || h != cardHeight) {
            maxTitleHeight = maxHtmlHeight = 0;
            Font titleFont   = Font.font("Arial", FontWeight.BOLD,   Math.max(16, w * 0.07));
            Font captionFont = Font.font("Arial", FontWeight.NORMAL, Math.max(16, Math.sqrt(w * h) * 0.035));
            WebSiteShared.htmlTextFont = captionFont;
            for (Card card : WebSiteShared.cards) {
                card.titleText.setFont(titleFont);
                maxTitleHeight = Math.max(card.titleText.prefHeight(w), maxTitleHeight);
                card.captionText1.setFont(captionFont);
                card.captionText2.setFont(captionFont);
                HtmlText htmlText = card.captionText1.getTranslateX() == 0 ? card.captionText2 : card.captionText1;
                String savedText = htmlText.getText();
                WebSiteShared.setHtmlText(htmlText, card.longestCaption);
                maxHtmlHeight = Math.max(htmlText.prefHeight(w), maxHtmlHeight);
                htmlText.setText(savedText);
            }
            cardWidth = w;
            cardHeight = h;
        }
        double ny = h;
        double nh = maxHtmlHeight;
        double vGap = h * 0.02;
        ny -= nh + vGap;
        captionText1.setMaxHeight(captionText1.prefHeight(w));
        layoutInArea(captionText1, hgap, ny, w, nh, 0, HPos.CENTER, VPos.CENTER);
        captionText2.setMaxHeight(captionText2.prefHeight(w));
        layoutInArea(captionText2, hgap, ny, w, nh, 0, HPos.CENTER, VPos.CENTER);
        nh = maxTitleHeight;
        ny -= nh + vGap;
        layoutInArea(titleText, hgap, ny, w, nh, 0, HPos.CENTER, VPos.TOP);
        if (alwaysUseTitleSpaceForIllustration) {
            nh = h - maxHtmlHeight - 2 * vGap;
        } else {
            double titlePercent = titleSpacePercent.get();
            //if (/*useTitleSpaceForIllustrationWhenHidden && */titlePercent < 1)
                ny += (nh + vGap) * (smoothIllustrationMove ? 1 - titlePercent : 1); // Smoothing the transition with opacity
            nh = ny - 3 * vGap;
        }
        ny = vGap;
        layoutInArea(illustrationNode, hgap, ny, w, nh, 0, HPos.CENTER, VPos.CENTER);
    }

    protected void bindTitleSpaceWithOpacity(boolean bind) {
        if (!bind) {
            titleSpacePercent.unbind();
            titleSpacePercent.set(1);
        } else if (!titleSpacePercent.isBound())
            bindTitleSpaceWithOpacity();
    }

    protected void bindTitleSpaceWithOpacity() {
        titleSpacePercent.bind(titleText.opacityProperty());
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

    static FxWreathPane createWebFxLogo() {
        SVGPath cloud = Card.createCloud();
        cloud.setTranslateY(-5);
        return setLogoId(new FxWreathPane(cloud), "WebFX");
    }

    static Pane createJSLogo() {
        SVGPath jsPath = new SVGPath();
        jsPath.setContent(SvgLogoPaths.getJSLogoPath());
        jsPath.setFill(Color.web("#323330"));
        Pane jsPane = new StackPane(jsPath);
        jsPane.setMaxSize(64, 64);
        WebSiteShared.setRegionBackground(jsPane, Color.web("#F0DB4F")); // JS yellow background color
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

    static SVGPath createGwtLogo() {
        return createLogoSVGPath(SvgLogoPaths.getGwtLogoPath(), WebSiteShared.gwtColor, "GWT");
    }

    static SVGPath createGwtText() {
        return createLogoSVGPath(SvgLogoPaths.getGwtTextPath(), WebSiteShared.gwtColor, "GWT");
    }

    static ScalePane createFlashLogo() {
        ScalePane pane = new ScalePane(createLogoSVGPath(SvgLogoPaths.getFlashLetterPath(), Color.WHITE));
        pane.setPrefSize(64, 64);
        WebSiteShared.setRegionBackground(pane, LinearGradient.valueOf("to bottom, #D21921, #4C060A"));
        return pane;
    }

    public static SVGPath createGithubLogo() {
        SVGPath githubSVGPath = createLogoSVGPath(SvgLogoPaths.getGithubLogoPath(), Color.gray(0.2));
        githubSVGPath.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 5, 0, 3, 3));
        githubSVGPath.setStroke(Color.WHITE);
        githubSVGPath.setStrokeWidth(1);
        return githubSVGPath;
    }

    static SVGPath createCloud() {
        SVGPath cloudSVGPath = createLogoSVGPath(SvgLogoPaths.getCloudPath(), Color.gray(1, 0.8));
        cloudSVGPath.setStroke(WebSiteShared.githubGradient);
        cloudSVGPath.setStrokeWidth(4);
        return cloudSVGPath;
    }

    static SVGPath createArrowUp() {
        SVGPath arrowUpSVGPath = createLogoSVGPath(SvgLogoPaths.getArrowUpPath(), null);
        arrowUpSVGPath.setStroke(WebSiteShared.createAngleGithubGradient(0));
        arrowUpSVGPath.setStrokeWidth(4);
        arrowUpSVGPath.setFill(Color.WHITE);
        return arrowUpSVGPath;
    }

    static SVGPath createArrowDown() {
        SVGPath arrowUpSVGPath = createLogoSVGPath(SvgLogoPaths.getArrowDownPath(), null);
        arrowUpSVGPath.setStroke(WebSiteShared.createAngleGithubGradient(0));
        arrowUpSVGPath.setStrokeWidth(4);
        arrowUpSVGPath.setFill(Color.WHITE);
        return arrowUpSVGPath;
    }

    static SVGPath createThumbUp() {
        return createLogoSVGPath(SvgLogoPaths.getThumbUpPath(), WebSiteShared.fxColor);
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
