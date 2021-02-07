package dev.webfx.website.application;

import dev.webfx.extras.webtext.controls.SvgText;
import dev.webfx.website.application.cards.Card;
import dev.webfx.website.application.cards.ScalePane;
import dev.webfx.website.application.demos.DemoThumbnailsPane;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.stream.Stream;

import static dev.webfx.website.application.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
public final class WebsiteApplication extends Application {

    private final Text webFxText = createWebFxSvgText();
    private final Text demosText = setUpText(new SvgText("Demos"), 50, true, false, true, true);
    private final SVGPath githubLogo = Card.createGithubLogo();
    private final ScalePane githubLogoPane =  new ScalePane(githubLogo);
    private final DemoThumbnailsPane demoThumbnailsPane = new DemoThumbnailsPane();
    private AnimationTimer webFxFillAnimationTimer;
    private Pane cardsPane;
    private int focusedCardIndex = -1;
    private boolean showMenu = true, verticalCards;
    private Timeline scrollTimeline;
    private double gap, scrollTimelineEndValue;

    @Override
    public void start(Stage stage) {
        cardsPane = new Pane(cards) { { getChildren().addAll(demosText, webFxText, githubLogoPane, demoThumbnailsPane); }
            @Override
            protected void layoutChildren() {
                double w = getWidth(), h = getHeight(), vh = 0;
                webFxText.setVisible(showMenu);
                demosText.setVisible(showMenu);
                if (showMenu) {
                    setFontSize(webFxText, h * 0.12, true);
                    setFontSize(demosText, h * 0.12, true);
                    vh = webFxText.prefHeight(w);
                    layoutInArea(webFxText, 0, 0, w, vh, 0, null, HPos.CENTER, VPos.TOP);
                    layoutInArea(demosText, 0, 0, w/3, vh, 0, null, HPos.CENTER, VPos.TOP);
                    layoutInArea(githubLogoPane, w - w/3, vh * 0.1, w/3, vh * 0.7, 0, null, HPos.CENTER, VPos.TOP);
                }
                layoutInArea(demoThumbnailsPane, 0, vh, w, h - vh, 0, HPos.CENTER, VPos.BOTTOM);
                gap = Math.max(5, w * 0.01);
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
        cardsPane.setBackground(null);
        for (Card card : cards)
            card.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> scrollHorizontallyToCard(Arrays.asList(cards).indexOf(card)));
        cardsPane.setOnMouseClicked(e -> scrollHorizontallyToCard(clickedCardIndex(e.getX())));
        cardsPane.setOnSwipeUp(     e -> scrollVerticallyToCard(focusedCardIndex + 1));
        cardsPane.setOnSwipeDown(   e -> scrollVerticallyToCard(focusedCardIndex - 1));
        cardsPane.setOnSwipeLeft(   e -> cards[Math.max(0, focusedCardIndex)].transitionToNextStep());
        cardsPane.setOnSwipeRight(  e -> cards[Math.max(0, focusedCardIndex)].transitionToPreviousStep());

        runOnMouseClick(demosText,  () -> showDemos(true));
        runOnMouseClick(webFxText,  () -> { if (demoThumbnailsPane.isVisible()) showDemos(false); });
        runOnMouseClick(githubLogo, () -> openUrl("https://github.com/webfx-project/webfx"));
        WebSiteShared.setHostServices(getHostServices()); // To make openUrl() work

        webFxText.setOnMouseEntered(e -> startWebFxFillAnimation());
        webFxText.setOnMouseExited( e -> stopWebFxFillAnimation());

        setShapeHoverAnimationColor(githubLogo, lastGithubGradientColor.darker());
        setShapeHoverAnimationColor(demosText, firstGithubGradientColor.darker());

        demoThumbnailsPane.setVisible(false);
        demoThumbnailsPane.setOpacity(0);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        Scene scene = new Scene(cardsPane, screenBounds.getWidth(), screenBounds.getHeight(), backgroundGradient);
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

    private void showDemos(boolean show) {
        demoThumbnailsPane.setVisible(true);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500),
                Stream.concat(Stream.of(demoThumbnailsPane), Arrays.stream(cards)).map(node -> new KeyValue(node.opacityProperty(), node == demoThumbnailsPane ? (show ? 1 : 0) : (show ? 0 : 1))).toArray(KeyValue[]::new))
        );
        timeline.setOnFinished(e -> demoThumbnailsPane.setVisible(show));
        timeline.play();
    }

    private int clickedCardIndex(double clickX) {
        int cardIndex = (int) ((clickX - cards[0].getTranslateX()) / (cards[0].getWidth() + gap));
        if (cardIndex > focusedCardIndex)
            cardIndex = Math.min(focusedCardIndex <= 0 ? 2 : focusedCardIndex + 1, cardIndex);
        return cardIndex;
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
        if (showMenu != (!verticalCards || cardsPane.getHeight() > cardsPane.getWidth() || cardIndex < 0)) {
            showMenu = !showMenu;
            cardsPane.requestLayout();
        } else if (verticalCards && cardIndex == 0)
            cardIndex = focusedCardIndex == -1 ? 1 : -1;
        focusedCardIndex = cardIndex;
        scrollToFocusedCard();
    }

    private void scrollToFocusedCard() {
        if (!verticalCards) {
            cardsPane.setTranslateY(0);
            int leftCardIndex = Math.max(0, Math.min(cards.length - 3, focusedCardIndex - 1));
            double translateX = -leftCardIndex * (cardsPane.getWidth() - gap) / 3;
            if (cards[0].getTranslateX() != translateX && translateX != scrollTimelineEndValue) {
                stopScrollTimeline();
                scrollTimelineEndValue = translateX;
                scrollTimeline = new Timeline(new KeyFrame(Duration.millis(500),
                        Arrays.stream(cards).map(c -> new KeyValue(c.translateXProperty(), scrollTimelineEndValue, EASE_OUT_INTERPOLATOR)).toArray(KeyValue[]::new))
                );
                scrollTimeline.play();
            }
        } else {
            cardsPane.setTranslateX(0);
            double translateY = focusedCardIndex <= 0 ? 0 : - focusedCardIndex * cardsPane.getHeight();
            if (cardsPane.getTranslateY() != translateY && translateY != scrollTimelineEndValue) {
                stopScrollTimeline();
                scrollTimelineEndValue = translateY;
                scrollTimeline = new Timeline(new KeyFrame(Duration.millis(500), new KeyValue(cardsPane.translateYProperty(), scrollTimelineEndValue, EASE_OUT_INTERPOLATOR)));
                scrollTimeline.play();
            }
        }
    }

    private void stopScrollTimeline() {
        if (scrollTimeline != null)
            scrollTimeline.stop();
        scrollTimelineEndValue = -1;
    }

    private void startWebFxFillAnimation() {
        stopWebFxFillAnimation();
        webFxFillAnimationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                webFxText.setFill(createAngleGithubGradient(now / (2 * Math.PI * 100_000_000)));
            }
        };
        webFxFillAnimationTimer.start();
    }

    private void stopWebFxFillAnimation() {
        if (webFxFillAnimationTimer != null) {
            webFxFillAnimationTimer.stop();
            webFxFillAnimationTimer = null;
        }
        webFxText.setFill(githubGradient);
    }
}