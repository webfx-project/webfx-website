package dev.webfx.website.application.cards;

import dev.webfx.extras.webtext.controls.HtmlText;
import dev.webfx.website.application.WebSiteShared;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
public abstract class Card extends Pane {

    private final static Border CARD_BORDER = new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(10), BorderStroke.THIN));
    private static double cardWidth, cardHeight, maxTitleHeight, maxHtmlHeight;

    public static final Card[] cards = {
            new WebFxCard(),
            new JavaFullStackCard(),
            new CrossPlatformCard(),
            new SustainableCard(),
            new ResponsiveCard(),
            new MagicalCard(),
    };

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
            for (Card card : cards) {
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

}
