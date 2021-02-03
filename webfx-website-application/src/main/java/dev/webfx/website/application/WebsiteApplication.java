package dev.webfx.website.application;

import dev.webfx.website.application.cards.Card;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;

import static dev.webfx.website.application.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
public class WebsiteApplication extends Application {

    private final Text webFxText = createWebFxSvgText(100);
    private AnimationTimer gradientAnimationTimer;
    private Pane rootPane;
    private int focusedCardIndex = -1;
    private boolean showMenu = true, verticalCards;
    private Timeline scrollTimeline;
    private double scrollTimelineEndValue;

    @Override
    public void start(Stage stage) {
        webFxText.setOnMouseClicked(e -> toggleTextColorAnimation());
        webFxText.setCursor(Cursor.HAND);
        rootPane = new Pane(cards) { { getChildren().add(webFxText); }
            @Override
            protected void layoutChildren() {
                double w = getWidth(), h = getHeight(), vh = 0;
                webFxText.setVisible(showMenu);
                if (showMenu) {
                    setFontSize(webFxText, h * 0.12, true);
                    vh = webFxText.prefHeight(w);
                    layoutInArea(webFxText, 0, 0, w, vh, 0, null, HPos.CENTER, VPos.TOP);
                    //vh += sh * 1.2;
                }
                double gap = Math.max(5, w * 0.01);
                w -= 4 * gap; h -= gap;
                double cx = gap, cy = vh, cw = w / 3, ch = h - vh;
                if (!showMenu) {
                    cy += gap;
                    ch -= gap;
                }
                verticalCards = cw < 230;
                if (!verticalCards) {
                    for (Card card : cards) {
                        layoutInArea(card, cx, cy, cw, ch, 0, HPos.CENTER, VPos.BOTTOM);
                        cx += cw + gap;
                    }
                } else {
                    w += 2 * gap;
                    for (Card card : cards) {
                        layoutInArea(card, cx, cy, w, ch, 0, HPos.CENTER, VPos.BOTTOM);
                        if (card == cards[0]) {
                            ch = h - gap;
                            cy = h + 2 * gap;
                        } else
                            cy += h + gap;
                    }
                }
                scrollToFocusedCard();
            }
        };
        rootPane.setBackground(null);
        rootPane.setOnSwipeUp(    e -> scrollVerticallyToCard(focusedCardIndex + 1));
        rootPane.setOnSwipeDown(  e -> scrollVerticallyToCard(focusedCardIndex - 1));
        rootPane.setOnSwipeLeft(  e -> cards[Math.max(0, focusedCardIndex)].transitionToNextStep());
        rootPane.setOnSwipeRight( e -> cards[Math.max(0, focusedCardIndex)].transitionToPreviousStep());
        rootPane.setOnMouseClicked(e -> scrollHorizontallyToCard((int) (3 * (e.getX() - cards[0].getTranslateX()) / rootPane.getWidth())));

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        Scene scene = new Scene(rootPane, screenBounds.getWidth(), screenBounds.getHeight(), backgroundGradient);
        stage.setTitle("WebFX - JavaFX \u2192 JS transpiler");
        stage.setScene(scene);
        stage.show();
    }

    private static void setFontSize(Text text, double fontSize, boolean bold) {
        if (text.getFont().getSize() != fontSize) {
            text.setFont(Font.font("Arial", bold ? FontWeight.BOLD : FontWeight.NORMAL, fontSize));
            text.setStrokeWidth(fontSize >= 70 ? 2 : 1);
        }
    }

    private void scrollHorizontallyToCard(int cardIndex) {
        if (!verticalCards) {
            cardIndex = Math.min(cardIndex, cards.length - 1);
            focusedCardIndex = cardIndex;
            scrollToFocusedCard();
        }
    }

    private void scrollVerticallyToCard(int cardIndex) {
        cardIndex = Math.min(cardIndex, cards.length - 1);
        if (showMenu != (!verticalCards || rootPane.getHeight() > rootPane.getWidth() || cardIndex < 0)) {
            showMenu = !showMenu;
            rootPane.requestLayout();
        } else if (verticalCards && cardIndex == 0)
            cardIndex = focusedCardIndex == -1 ? 1 : -1;
        focusedCardIndex = cardIndex;
        scrollToFocusedCard();
    }

    private void scrollToFocusedCard() {
        if (!verticalCards) {
            rootPane.setTranslateY(0);
            int leftCardIndex = Math.max(0, Math.min(cards.length - 3, focusedCardIndex - 1));
            double translateX = -leftCardIndex * rootPane.getWidth() / 3;
            if (cards[0].getTranslateX() != translateX && translateX != scrollTimelineEndValue) {
                stopScrollTimeline();
                scrollTimelineEndValue = translateX;
                scrollTimeline = new Timeline(new KeyFrame(Duration.millis(500),
                        Arrays.stream(cards).map(c -> new KeyValue(c.translateXProperty(), scrollTimelineEndValue, EASE_OUT_INTERPOLATOR)).toArray(KeyValue[]::new))
                );
                scrollTimeline.play();
            }
        } else {
            rootPane.setTranslateX(0);
            double translateY = focusedCardIndex <= 0 ? 0 : - focusedCardIndex * rootPane.getHeight();
            if (rootPane.getTranslateY() != translateY && translateY != scrollTimelineEndValue) {
                stopScrollTimeline();
                scrollTimelineEndValue = translateY;
                scrollTimeline = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(rootPane.translateYProperty(), scrollTimelineEndValue, EASE_OUT_INTERPOLATOR)));
                scrollTimeline.play();
            }
        }
    }

    private void stopScrollTimeline() {
        if (scrollTimeline != null)
            scrollTimeline.stop();
        scrollTimelineEndValue = -1;
    }

    private void startTextColorAnimation() {
        stopTextColorAnimation();

        gradientAnimationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                webFxText.setFill(createAngleGithubGradient(now / (2 * Math.PI * 100_000_000)));
            }
        };
        gradientAnimationTimer.start();
    }

    private void stopTextColorAnimation() {
        if (gradientAnimationTimer != null) {
            gradientAnimationTimer.stop();
            gradientAnimationTimer = null;
        }
    }

    private void toggleTextColorAnimation() {
        if (gradientAnimationTimer != null)
            stopTextColorAnimation();
        else
            startTextColorAnimation();
    }
}
