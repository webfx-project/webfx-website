package dev.webfx.website.application.cards;

import dev.webfx.platform.uischeduler.UiScheduler;
import dev.webfx.website.application.shared.LayoutPane;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
    private int visibleCardsCount, focusedCardIndex = -1;
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
    }

    private void onPaneClicked(MouseEvent e) {
        double x = e.getX();
        if (x <= gap)
            scrollToCard(focusedCardIndex - 1, false);
        else {
            int cardIndex = clickedCardIndex(x); // Index of the clicked card
            if (x >= getWidth() - gap)
                scrollToCard(cardIndex, false);
            else {
                if (visibleCardsCount == 1) // Single card displayed => no scroll, just play
                    playCard(cardIndex);
                else // Several cards displayed => eventually scroll the cards before playing
                    scrollToCard(cardIndex, true);
            }
        }
    }

    public void onPaneSwipe(boolean left) { // Called by the WebFXWebsiteApplication
        scrollToCard(focusedCardIndex + (left ? +1 : -1), false);
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
        scrollToFocusedCard(false);
        sizeChangedDuringScroll = false;
        for (int i = 0; i < 6; i++) {
            Circle dot = dots[i];
            dot.setRadius(gap / 6);
            boolean left = i < 3;
            int farX = left ? i :  (5 - i);
            //dot.setVisible(left ? leftCardIndex > 0 : leftCardIndex + visibleCardsCount < cards.length);
            dot.setCenterX(left ? gap / 2 : width - gap / 2);
            dot.setCenterY(height / 2 -  height / 30 + farX * height / 30);
        }
    }

    private int boundedCardIndex(int cardIndex) {
        return Math.max(0, Math.min(cardIndex, cards.length - 1));
    }

    private int getLeftCardIndex() {
        return boundedCardIndex(Math.min(cards.length - visibleCardsCount, focusedCardIndex + (visibleCardsCount == 1 ? 0 : -1)));
    }

    private int getRightCardIndex() {
        return getRightCardIndex(getLeftCardIndex());
    }

    private int getRightCardIndex(int leftCardIndex) {
        return boundedCardIndex(leftCardIndex + visibleCardsCount - 1);
    }

    private int clickedCardIndex(double clickX) {
        int cardIndex = (int) ((clickX - cards[0].getTranslateX()) / (cards[0].getWidth() + gap));
        if (cardIndex > focusedCardIndex)
            cardIndex = Math.min(focusedCardIndex <= 0 ? 2 : focusedCardIndex + 1, cardIndex);
        return boundedCardIndex(cardIndex);
    }

    private void scrollToCard(int cardIndex, boolean playCard) {
        scrollHorizontallyToCard(cardIndex, playCard);
    }

    private void scrollHorizontallyToCard(int cardIndex, boolean playCard) {
        cardIndex = Math.min(cardIndex, cards.length - 1);
        focusedCardIndex = cardIndex;
        scrollToFocusedCard(playCard);
    }

    private void scrollToFocusedCard(boolean playCard) {
        Timeline timeline = scrollTimeline;
        int leftCardIndex = getLeftCardIndex();
        int rightCardIndex = getRightCardIndex(leftCardIndex);
        checkCardInitialized(rightCardIndex);
        double translateX = -leftCardIndex * (getWidth() - gap) / visibleCardsCount;
        if (cards[0].getTranslateX() == translateX)
            playFocusedCard(playCard);
        else if (scrollTimeline == null || translateX != scrollTimelineEndValue) {
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
                playFocusedCard(playCard);
            });
            scrollTimeline.play();
            for (Circle dot : dots)
                dot.setVisible(false);
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
        focusedCardIndex = cardIndex;
        playFocusedCard(true);
    }

    private void playFocusedCard(boolean play) {
        if (play) {
            Card card = cards[focusedCardIndex];
            card.transitionToNextStep();
            if (!card.hasSingleStep() && card.currentAnimationStep == 1) { // Card animation finished and went back to first step
                int rightCardIndex = getRightCardIndex();
                // If this was the last card on the right of the screen, we transit to the next card (if any)
                if (focusedCardIndex == rightCardIndex)
                    UiScheduler.scheduleDelay(2000, () -> { // We wait 2s before transiting
                        if (card.currentAnimationStep == 1) // If the user played again on that card, we finally don't transit to the next
                            scrollToCard(rightCardIndex + 1, false);
                    });
            }
        }
        int leftCardIndex = getLeftCardIndex();
        for (int i = 0; i < 6; i++) {
            Circle dot = dots[i];
            boolean left = i < 3;
            dot.setVisible(left ? leftCardIndex > 0 : leftCardIndex + visibleCardsCount < cards.length);
        }
    }
    
}
