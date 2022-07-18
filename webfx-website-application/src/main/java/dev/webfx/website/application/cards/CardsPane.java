package dev.webfx.website.application.cards;

import dev.webfx.platform.uischeduler.UiScheduler;
import dev.webfx.website.application.shared.LayoutPane;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Arrays;

import static dev.webfx.website.application.shared.WebSiteShared.EASE_OUT_INTERPOLATOR;

/**
 * @author Bruno Salmon
 */
public class CardsPane extends LayoutPane {

    public final Card[] cards;
    private int leftCardIndex, visibleCardsCount;
    private boolean sizeChangedDuringScroll;
    private Timeline scrollTimeline;
    private double gap, scrollTimelineEndValue;
    private final Circle[] dots = {new Circle(), new Circle(), new Circle(), new Circle(), new Circle(), new Circle()};

    public CardsPane(Card... cards) {
        super(cards);
        this.cards = cards;
        for (Card card : cards)
            card.setCardsPane(this);
        setOnMouseClicked(this::onPaneClicked);
        setBackground(null);
        getChildren().addAll(dots);
        for (Circle dot : dots)
            dot.setFill(Color.WHITE);
        updateDotsVisibility(false);
    }

    private void onPaneClicked(MouseEvent e) {
        double x = e.getX(); // Note x can go crazy with cards having SVG TODO: fix bug in WebFX InputEventUtils.recomputeCoordinates() and probably in call to Node.sceneToLocal()
        // That's why we rely more on target than x to figure out what card was clicked
        //int cardIndex = clickedCardIndex(x);
        int cardIndex = -1;
        EventTarget target = e.getTarget();
        if (target instanceof Node) {
            Node node = (Node) target;
            while (node != null && !(node instanceof Card))
                node = node.getParent();
            if (node != null)
                cardIndex = Arrays.asList(cards).indexOf((Card) node);
        }
        if (cardIndex != -1)
             playCard(cardIndex);
        else if (x <= gap)
            scrollOneCardLeft();
        else if (x >= getWidth() - gap)
            scrollOneCardRight();
    }

    public void onPaneSwipe(boolean left) { // Called by the WebFXWebsiteApplication
        if (left)
            scrollOneCardLeft();
        else
            scrollOneCardRight();
    }

    @Override
    protected void layoutChildren(double width, double height) {
        if (scrollTimeline != null) {
            sizeChangedDuringScroll = true;
            return;
        }
        double w = width, h = height, vh = 0;
        visibleCardsCount = w * h < 640 * 360 ? 1 : Math.max(1, Math.min(cards.length, (int) (1.5 * w / h)));
        int leftCardIndex = getLeftCardIndex();
        int rightCardIndex = getRightCardIndex(leftCardIndex);
        for (int i = leftCardIndex; i <= rightCardIndex; i++)
            cards[i].checkInitialized();
        gap = Math.max(15, w * 0.01);
        w -= (visibleCardsCount + 1) * gap; h -= gap;
        double cx = gap, cy = vh, cw = w / visibleCardsCount, ch = h - vh;
        for (Card card : cards) {
            layoutInArea(card, cx, cy, cw, ch);
            cx += cw + gap;
        }
        scrollToCard(leftCardIndex);
        sizeChangedDuringScroll = false;
        // Positioning the navigation dots
        for (int i = 0; i < 6; i++) {
            Circle dot = dots[i];
            dot.setRadius(gap / 6);
            boolean left = i < 3;
            int farX = left ? i :  (5 - i);
            dot.setCenterX(left ? gap / 2 : width - gap / 2);
            dot.setCenterY(height / 2 -  height / 30 + farX * height / 30);
        }
    }

    private int boundedCardIndex(int cardIndex) {
        return boundedCardIndex(cardIndex, cards.length - 1);
    }

    private int boundedCardIndex(int cardIndex, int maxValue) {
        return Math.max(0, Math.min(cardIndex, maxValue));
    }

    private int getLeftCardIndex() {
        return leftCardIndex;
    }

    private int getRightCardIndex() {
        return getRightCardIndex(getLeftCardIndex());
    }

    private int getRightCardIndex(int leftCardIndex) {
        return boundedCardIndex(leftCardIndex + visibleCardsCount - 1);
    }

    private void scrollOneCardLeft() {
        scrollToCard(getLeftCardIndex() - 1);
    }

    private void scrollOneCardRight() {
        scrollToCard(getLeftCardIndex() + 1);
    }

    private void scrollToCard(int leftCardIndex) {
        this.leftCardIndex = leftCardIndex = boundedCardIndex(leftCardIndex, cards.length - visibleCardsCount);
        Timeline timeline = scrollTimeline;
        int rightCardIndex = getRightCardIndex(leftCardIndex);
        checkCardInitialized(rightCardIndex);
        double translateX = -leftCardIndex * (getWidth() - gap) / visibleCardsCount;
        if (cards[0].getTranslateX() != translateX && (scrollTimeline == null || translateX != scrollTimelineEndValue)) {
            stopScrollTimeline();
            scrollTimelineEndValue = translateX;
            scrollTimeline = new Timeline(new KeyFrame(Duration.millis(500),
                    Arrays.stream(cards).map(c -> new KeyValue(c.translateXProperty(), scrollTimelineEndValue, EASE_OUT_INTERPOLATOR)).toArray(KeyValue[]::new))
            );
        }
        if (timeline != scrollTimeline) {
            scrollTimeline.setOnFinished(e -> {
                scrollTimeline = null;
                if (sizeChangedDuringScroll)
                    forceLayoutChildren();
                // Updating navigation dots visibility
                updateDotsVisibility(false);
            });
            scrollTimeline.play();
            // Hiding navigation dots during the cards scroll animation
            updateDotsVisibility(true);
        }
    }

    private void updateDotsVisibility(boolean forceInvisible) {
        int leftCardIndex = getLeftCardIndex();
        for (int i = 0; i < 6; i++) {
            Circle dot = dots[i];
            boolean left = i < 3;
            dot.setVisible(!forceInvisible && (left ? leftCardIndex > 0 : leftCardIndex + visibleCardsCount < cards.length));
        }
    }

    private void checkCardInitialized(int cardIndex) {
        if (!cards[cardIndex].checkInitialized())
            forceLayoutChildren();
    }

    private void stopScrollTimeline() {
        if (scrollTimeline != null)
            scrollTimeline.stop();
        scrollTimelineEndValue = -1;
    }

    private void playCard(int cardIndex) {
        checkCardInitialized(cardIndex);
        Card card = cards[cardIndex];
        card.transitionToNextStep();
        if (!card.hasSingleStep() && card.currentAnimationStep == 1) { // Card animation finished and went back to first step
            int rightCardIndex = getRightCardIndex();
            // If this was the last card on the right of the screen, we transit to the next card (if any)
            if (cardIndex == rightCardIndex)
                UiScheduler.scheduleDelay(1000, () -> { // We wait 1s before transiting
                    if (card.currentAnimationStep == 1) // If the user played again on that card, we finally don't transit to the next
                        scrollOneCardRight();
                });
        }
    }
    
}
