package dev.webfx.website.application.cards;

import dev.webfx.extras.webtext.HtmlText;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.WebSiteShared;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static dev.webfx.website.application.shared.WebSiteShared.CARD_TRANSLUCENT_BACKGROUND;
import static dev.webfx.website.application.shared.WebSiteShared.updateFontSize;

/**
 * @author Bruno Salmon
 */
public abstract class Card extends LayoutPane {

    private final static Border CARD_BORDER = new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, new CornerRadii(10), BorderStroke.THIN));
    private static double cardWidth, cardHeight, maxTitleHeight, maxCaptionHeight;

    public static final Card[] cards = {
            new WebFXCard(),
            new CrossPlatformCard(),
            new JavaFullStackCard(),
            new SustainableCard(),
            new ResponsiveCard(),
            new MagicalCard(),
    };

    private final Rectangle clip = new Rectangle();
    private final String title;
    private boolean initialized;
    protected Node illustrationNode;
    protected Text titleText;
    protected final HtmlText captionText1 = new HtmlText(), captionText2 = new HtmlText();
    protected int currentAnimationStep = 0;
    protected boolean forwardingStep;
    private CardTransition cardTransition;
    private String longestCaption;

    Card(String title) {
        this.title = title;
        setBorder(CARD_BORDER);
        WebSiteShared.setRegionBackground(this, CARD_TRANSLUCENT_BACKGROUND, CARD_BORDER.getStrokes().get(0).getRadii());
    }

    public boolean checkInitialized() {
        if (initialized)
            return true;
        init();
        return false;
    }

    protected void init() {
        longestCaption = "";
        for (int step = 1; caption(step) != null; step++) {
            String caption = caption(step);
            if (caption.length() > longestCaption.length())
                longestCaption = caption;
        }
        titleText = WebSiteShared.setUpText(new Text(title + " \u2192"), 30, true, true, false, true);
        illustrationNode = createIllustrationNode();
        titleText.setMouseTransparent(true);
        captionText1.setMouseTransparent(true);
        captionText2.setMouseTransparent(true);
        getChildren().setAll(illustrationNode, titleText, captionText1, captionText2);
        transitionToNextStep();
        setClip(clip);
        initialized = true;
        cardWidth = 0;
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
        forceLayoutChildren(); // Since we have changed the content of the caption, it is necessary to update the layout
    }

    abstract String caption(int step);

    private static void computeMaxTextHeights(double width, double height) {
        if (cardWidth == width && cardHeight == height)
            return;
        cardWidth = width; cardHeight = height;
        maxTitleHeight = maxCaptionHeight = 0;
        double titleFontSize   = Math.max(16, width * 0.07);
        double captionFontSize = Math.max(16, Math.sqrt(width * height) * 0.035);
        Font titleFont = null, captionFont = null;
        for (Card card : cards) {
            if (!card.initialized)
                continue;
            if (titleFont == null)
                titleFont = updateFontSize(card.titleText.getFont(), titleFontSize, true);
            card.titleText.setFont(titleFont);
            maxTitleHeight = Math.max(card.titleText.prefHeight(width), maxTitleHeight);
            if (captionFont == null)
                captionFont = WebSiteShared.htmlTextFont = updateFontSize(card.captionText1.getFont(), captionFontSize, false);
            card.captionText1.setFont(captionFont);
            card.captionText2.setFont(captionFont);
            HtmlText htmlText = card.captionText1.getTranslateX() == 0 ? card.captionText2 : card.captionText1;
            String savedText = htmlText.getText();
            WebSiteShared.setHtmlText(htmlText, card.longestCaption);
            maxCaptionHeight = Math.max(htmlText.prefHeight(width), maxCaptionHeight);
            htmlText.setText(savedText);
        }
    }

    @Override
    protected void layoutChildren(double width, double height) {
        if (!initialized)
            return;
        clip.setWidth(width);
        clip.setHeight(height);
        double w = width, h = height, hgap = w * 0.02;
        w -= 2 * hgap;
        computeMaxTextHeights(w, h);
        double ny = h;
        double nh = maxCaptionHeight;
        double vGap = h * 0.02;
        ny -= nh + vGap;
        captionText1.setMaxHeight(captionText1.prefHeight(w));
        centerInArea(captionText1, hgap, ny, w, nh);
        captionText2.setMaxHeight(captionText2.prefHeight(w));
        centerInArea(captionText2, hgap, ny, w, nh);
        nh = maxTitleHeight;
        ny -= nh + vGap;
        centerInArea(titleText, hgap, ny, w, nh);
        ny += nh + vGap;
        nh = ny - 2 * vGap;
        ny = vGap;
        layoutInArea(illustrationNode, hgap, ny, w, nh);
    }

    double getTitleSpace() {
        return maxTitleHeight + 0.02 * getHeight();
    }

    double getTransitionalTitleSpace() {
        return getTitleSpace() * titleText.getOpacity();
    }
}
