package dev.webfx.website.application.cards;

import dev.webfx.extras.webtext.controls.HtmlText;
import dev.webfx.platform.shared.services.resource.ResourceService;
import dev.webfx.website.application.SvgLogoPaths;
import dev.webfx.website.application.WebSiteShared;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

/**
 * @author Bruno Salmon
 */
public abstract class Card extends Pane {

    private final static Border cardBorder = new Border(new BorderStroke(Color.grayRgb(255, 0.5), BorderStrokeStyle.SOLID, new CornerRadii(10), BorderStroke.THIN));
    private double cardWidth, maxIllustrationHeight, maxTitleHeight, maxHtmlHeight;

    protected final Node illustrationNode;
    protected final Text titleText;
    protected final HtmlText htmlText1 = new HtmlText(), htmlText2 = new HtmlText();
    protected int currentAnimationStep = 0;
    private CardTransition cardTransition;

    Card(String title) {
        setBorder(cardBorder);
        titleText = new Text(title + " \u2192");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        titleText.setFill(Color.WHITE);
        titleText.setEffect(WebSiteShared.dropShadow);
        illustrationNode = createIllustrationNode();
        htmlText1.setMouseTransparent(true);
        htmlText2.setMouseTransparent(true);
        getChildren().setAll(illustrationNode, titleText, htmlText1, htmlText2);
        setCursor(Cursor.HAND);
        transitionToNextStep();
        setOnMouseClicked(e -> transitionToNextStep());
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);
    }

    protected abstract Node createIllustrationNode();

    protected void transitionToNextStep() {
        boolean animateTransition = currentAnimationStep > 0;
        if (subTitle(++currentAnimationStep) == null)
            currentAnimationStep = 1;
        if (cardTransition != null)
            cardTransition.stop();
        cardTransition = new CardTransition();
        prepareCardTransition(currentAnimationStep, cardTransition);
        cardTransition.run(animateTransition);
    }

    protected void prepareCardTransition(int step, CardTransition cardTransition) {
        if (step <= 2)
            cardTransition.addKeyValue(new KeyValue(titleText.opacityProperty(), step == 1 ? 1 : 0));
        boolean enteringTextIs1 = htmlText2.getTranslateX() == 0;
        HtmlText enteringText = enteringTextIs1 ? htmlText1 : htmlText2;
        HtmlText leavingText  = enteringTextIs1 ? htmlText2 : htmlText1;
        String subTitle = subTitle(step);
        enteringText.setText(subTitle == null ? null : "<center style='font-size: 22px; font-style: oblique; color:white'>" + subTitle + (step > 1 && subTitle(step + 1) != null ? " \u2192" : "") + "</center>");
        double width = getWidth();
        enteringText.setTranslateX(width);
        cardTransition.addKeyValue(
                new KeyValue(enteringText.translateXProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(leavingText.translateXProperty(), -width, Interpolator.EASE_OUT)
        );
    }

    protected abstract String subTitle(int step);

    @Override
    protected void layoutChildren() {
        double w = getWidth() - 20, h = getHeight();
        if (w != cardWidth) {
            cardWidth = w;
            maxIllustrationHeight = maxTitleHeight = maxHtmlHeight = 0;
            for (Card card : WebSiteShared.cards) {
                maxIllustrationHeight = Math.max(card.illustrationNode.prefHeight(w), maxIllustrationHeight);
                maxTitleHeight = Math.max(card.titleText.prefHeight(w), maxTitleHeight);
                maxHtmlHeight = Math.max(card.htmlText1.prefHeight(w), maxHtmlHeight);
            }
        }
        double extraSpace = h - maxIllustrationHeight - maxTitleHeight - maxHtmlHeight;
        double gap = extraSpace / 4;
        double ny = gap, nh = maxIllustrationHeight;
        layoutInArea(illustrationNode, 10, ny, w, nh, 0, HPos.CENTER, VPos.CENTER);
        ny += nh + gap;
        nh = maxTitleHeight;
        layoutInArea(titleText, 10, ny, w, nh, 0, HPos.CENTER, VPos.TOP);
        ny += nh + gap;
        nh = maxHtmlHeight;
        layoutInArea(htmlText1, 10, ny, w, nh, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(htmlText2, 10, ny, w, nh, 0, HPos.CENTER, VPos.CENTER);
    }

    // Static utility methods

    static Pane createNoLogo() {
        StackPane stackPane = new StackPane();
        stackPane.getProperties().put("logo", "no");
        return stackPane;
    }

    private static ImageView createImageViewLogo(String file) {
        ImageView imageView = new ImageView(ResourceService.toUrl(file, Card.class));
        imageView.getProperties().put("logo", file);
        return imageView;
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

    static Pane createJSLogo() {
        SVGPath jsPath = new SVGPath();
        jsPath.setContent(SvgLogoPaths.getJSLogoPath());
        jsPath.setFill(Color.BLACK);
        Pane jsPane = new StackPane(jsPath);
        jsPane.setMaxSize(64, 64);
        jsPane.setBackground(new Background(new BackgroundFill(Color.web("#f7df1e"), null, null)));
        jsPane.getProperties().put("logo", "JS");
        return jsPane;
    }

    static SVGPath createLogoSVGPath(String content, Paint fill, String logo) {
        SVGPath javaPath = new SVGPath();
        javaPath.setContent(content);
        javaPath.setFill(fill);
        javaPath.getProperties().put("logo", logo);
        return javaPath;
    }

    static SVGPath createJavaLogo() {
        return createLogoSVGPath(SvgLogoPaths.getJavaLogoPath(), Color.WHITE, "Java");
    }

    static SVGPath createFxLogo() {
        return createLogoSVGPath(SvgLogoPaths.getFxWordPath(), WebSiteShared.fxColor, "FX");
    }

    static SVGPath createThumbUp() {
        return createLogoSVGPath(SvgLogoPaths.getThumbUpPath(), Color.WHITE, null);
    }

    static SVGPath createThumbDown() {
        SVGPath thumb = createThumbUp();
        Rotate rotate = new Rotate(180, Rotate.X_AXIS);
        rotate.setPivotY(thumb.getLayoutBounds().getHeight() / 2);
        thumb.getTransforms().add(rotate);
        return thumb;
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

}
