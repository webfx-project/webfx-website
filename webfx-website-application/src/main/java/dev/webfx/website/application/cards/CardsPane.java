package dev.webfx.website.application.cards;

import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.WebSiteShared;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
        for (Card card : cards) {
            card.setCardsPane(this);
            WebSiteShared.runOnMouseClick(card, () -> onCardClicked(card));
        }
        setOnMouseClicked(e -> scrollToCard(clickedCardIndex(e.getX()), false));
        setBackground(null);
        getChildren().addAll(dots);
        for (Circle dot : dots)
            dot.setFill(Color.WHITE);
    }

    private void onCardClicked(Card card) {
        if (visibleCardsCount > 1)
            scrollToCard(Arrays.asList(cards).indexOf(card), true);
        else
            playCard(true);
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
        int leftCardIndex = Math.max(0, Math.min(cards.length - visibleCardsCount, focusedCardIndex - 1));
        for (int i = leftCardIndex; i < Math.min(cards.length, leftCardIndex + visibleCardsCount); i++)
            cards[i].checkInitialized();
        gap = Math.max(5, w * 0.01);
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

    private int clickedCardIndex(double clickX) {
        int cardIndex = (int) ((clickX - cards[0].getTranslateX()) / (cards[0].getWidth() + gap));
        if (cardIndex > focusedCardIndex)
            cardIndex = Math.min(focusedCardIndex <= 0 ? 2 : focusedCardIndex + 1, cardIndex);
        return cardIndex;
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
        int leftCardIndex = Math.max(0, Math.min(cards.length - visibleCardsCount, focusedCardIndex - 1));
        if (!cards[Math.min(cards.length - 1, leftCardIndex + visibleCardsCount - 1)].checkInitialized())
            forceLayoutChildren();
        double translateX = -leftCardIndex * (getWidth() - gap) / visibleCardsCount;
        if (cards[0].getTranslateX() == translateX)
            playCard(playCard);
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
                playCard(playCard);
            });
            scrollTimeline.play();
            for (Circle dot : dots)
                dot.setVisible(false);
        }
    }

    private void stopScrollTimeline() {
        if (scrollTimeline != null)
            scrollTimeline.stop();
        scrollTimelineEndValue = -1;
    }

    private void playCard(boolean play) {
        if (play)
            cards[focusedCardIndex].transitionToNextStep();
        int leftCardIndex = Math.max(0, Math.min(cards.length - visibleCardsCount, focusedCardIndex - 1));
        for (int i = 0; i < 6; i++) {
            Circle dot = dots[i];
            boolean left = i < 3;
            dot.setVisible(left ? leftCardIndex > 0 : leftCardIndex + visibleCardsCount < cards.length);
        }
    }
    
}
