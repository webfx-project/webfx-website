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
import javafx.application.Platform;
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
    private final SVGPath githubLogo = createGithubLogo();
    private final ScalePane githubLogoPane =  new ScalePane(githubLogo);
    private DemoThumbnailsPane demoThumbnailsPane;
    private AnimationTimer webFxFillAnimationTimer;
    private Pane cardsPane;
    private int focusedCardIndex = -1;
    private boolean showMenu = true, verticalCards;
    private Timeline scrollTimeline;
    private double gap, scrollTimelineEndValue;

    @Override
    public void start(Stage stage) {
        cardsPane = new Pane(Card.cards) { { getChildren().addAll(demosText, webFxText, githubLogoPane); }
            @Override
            protected void layoutChildren() {
                double w = getWidth(), h = getHeight(), vh = 0;
                demosText.setVisible(showMenu);
                webFxText.setVisible(showMenu);
                githubLogoPane.setVisible(showMenu);
                if (showMenu) {
                    double fontSize = Math.min(0.08 * w, 0.12 * h);
                    setFontSize(webFxText, fontSize, true);
                    setFontSize(demosText, fontSize, true);
                    vh = webFxText.prefHeight(w);
                    layoutInArea(webFxText, 0, 0, w, vh, 0, null, HPos.CENTER, VPos.TOP);
                    layoutInArea(demosText, 0, 0, w/3, vh, 0, null, HPos.CENTER, VPos.TOP);
                    layoutInArea(githubLogoPane, w - w/3, vh * 0.1, w/3, vh * 0.7, 0, null, HPos.CENTER, VPos.TOP);
                }
                if (demoThumbnailsPane != null)
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
                    for (Card card : Card.cards) {
                        layoutInArea(card, cx, cy, cw, ch, 0, HPos.CENTER, VPos.BOTTOM);
                        cx += cw + gap;
                    }
                } else {
                    w += 2 * gap;
                    for (Card card : Card.cards) {
                        layoutInArea(card, cx, cy, w, ch, 0, HPos.CENTER, VPos.BOTTOM);
                        if (card == Card.cards[0]) {
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
        for (Card card : Card.cards)
            card.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> scrollHorizontallyToCard(Arrays.asList(Card.cards).indexOf(card)));
        cardsPane.setOnMouseClicked(e -> scrollHorizontallyToCard(clickedCardIndex(e.getX())));
        cardsPane.setOnSwipeUp(     e -> scrollVerticallyToCard(focusedCardIndex + 1));
        cardsPane.setOnSwipeDown(   e -> scrollVerticallyToCard(focusedCardIndex - 1));
        cardsPane.setOnSwipeLeft(   e -> Card.cards[Math.max(0, focusedCardIndex)].transitionToNextStep());
        cardsPane.setOnSwipeRight(  e -> Card.cards[Math.max(0, focusedCardIndex)].transitionToPreviousStep());

        runOnMouseClick(demosText,  () -> showDemos(true));
        runOnMouseClick(webFxText,  () -> { if (demoThumbnailsPane.isVisible()) showDemos(false); });
        runOnMouseClick(githubLogo, () -> openUrl("https://github.com/webfx-project/webfx"));
        WebSiteShared.setHostServices(getHostServices()); // To make openUrl() work

        webFxText.setOnMouseEntered(e -> startWebFxFillAnimation());
        webFxText.setOnMouseExited( e -> stopWebFxFillAnimation());

        setShapeHoverAnimationColor(githubLogo, lastGithubGradientColor.darker());
        setShapeHoverAnimationColor(demosText, FIRST_GITHUB_GRADIENT_COLOR.darker());

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        Scene scene = new Scene(cardsPane, screenBounds.getWidth(), screenBounds.getHeight(), BACKGROUND_GRADIENT);
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
        if (demoThumbnailsPane == null) {
            // Creating the demo pane and adding it to the cards pane
            cardsPane.getChildren().add(demoThumbnailsPane = new DemoThumbnailsPane());
            demoThumbnailsPane.setOpacity(0);
            // Postponing the fade effect after the next layout pass (which may take time to consider the demo pane addition)
            Platform.runLater(() -> showDemos(show));
            return;
        }
        stopWebFxFillAnimation();
        demoThumbnailsPane.setVisible(true);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500),
                Stream.concat(Stream.of(demoThumbnailsPane), Arrays.stream(Card.cards)).map(node -> new KeyValue(node.opacityProperty(), node == demoThumbnailsPane ? (show ? 1 : 0) : (show ? 0 : 1))).toArray(KeyValue[]::new))
        );
        timeline.setOnFinished(e -> demoThumbnailsPane.setVisible(show));
        timeline.play();
    }

    private int clickedCardIndex(double clickX) {
        int cardIndex = (int) ((clickX - Card.cards[0].getTranslateX()) / (Card.cards[0].getWidth() + gap));
        if (cardIndex > focusedCardIndex)
            cardIndex = Math.min(focusedCardIndex <= 0 ? 2 : focusedCardIndex + 1, cardIndex);
        return cardIndex;
    }

    private void scrollHorizontallyToCard(int cardIndex) {
        if (!verticalCards) {
            cardIndex = Math.min(cardIndex, Card.cards.length - 1);
            focusedCardIndex = cardIndex;
            scrollToFocusedCard();
        }
    }

    private void scrollVerticallyToCard(int cardIndex) {
        cardIndex = Math.min(cardIndex, Card.cards.length - 1);
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
            int leftCardIndex = Math.max(0, Math.min(Card.cards.length - 3, focusedCardIndex - 1));
            double translateX = -leftCardIndex * (cardsPane.getWidth() - gap) / 3;
            if (Card.cards[0].getTranslateX() != translateX && translateX != scrollTimelineEndValue) {
                stopScrollTimeline();
                scrollTimelineEndValue = translateX;
                scrollTimeline = new Timeline(new KeyFrame(Duration.millis(500),
                        Arrays.stream(Card.cards).map(c -> new KeyValue(c.translateXProperty(), scrollTimelineEndValue, EASE_OUT_INTERPOLATOR)).toArray(KeyValue[]::new))
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
        webFxText.setFill(GITHUB_GRADIENT);
    }
}