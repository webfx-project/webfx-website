package dev.webfx.website.application;

import dev.webfx.extras.webtext.SvgText;
import dev.webfx.website.application.cards.Card;
import dev.webfx.website.application.demos.DemoThumbnailsPane;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.WebSiteShared;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.stream.Stream;

import static dev.webfx.website.application.shared.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
public final class WebsiteApplication extends Application {

    private final Text webFxText = createWebFxSvgText();
    private final Text demosText = setUpText(new SvgText("Demos"), 50, true, false, true, true);
    private final Text startText = setUpText(new SvgText("Start"), 50, true, false, true, true);
    //private final SVGPath githubLogo = createGithubLogo();
    //private final ScalePane githubLogoPane =  new ScalePane(githubLogo);
    private DemoThumbnailsPane demoThumbnailsPane;
    private AnimationTimer webFxFillAnimationTimer;
    private LayoutPane cardsPane;
    private int visibleCardsCount, focusedCardIndex = -1;
    private boolean sizeChangedDuringScroll;
    private Timeline scrollTimeline;
    private double gap, scrollTimelineEndValue;

    @Override
    public void start(Stage stage) {
        cardsPane = new LayoutPane(Card.cards) { { getChildren().addAll(demosText, webFxText, startText); }
            @Override
            protected void layoutChildren(double width, double height) {
                if (scrollTimeline != null) {
                    sizeChangedDuringScroll = true;
                    return;
                }
                double w = width, h = height, vh;
                double fontSize = Math.min(0.08 * w, 0.12 * h);
                updateTextFontSize(webFxText, fontSize);
                updateTextFontSize(demosText, fontSize);
                updateTextFontSize(startText, fontSize);
                vh = webFxText.prefHeight(w);
                centerInArea(webFxText, 0, 0, w, vh);
                centerInArea(demosText, 0, 0, w/3, vh);
                centerInArea(startText, w - w/3, 0, w/3, vh);
                if (demoThumbnailsPane != null)
                    layoutInArea(demoThumbnailsPane, 0, vh, w, h - vh);
                visibleCardsCount = w * h < 640 * 360 ? 1 : Math.max(1, Math.min(Card.cards.length, (int) (1.8 * w / h)));
                int leftCardIndex = Math.max(0, Math.min(Card.cards.length - visibleCardsCount, focusedCardIndex - 1));
                for (int i = leftCardIndex; i < Math.min(Card.cards.length, leftCardIndex + visibleCardsCount); i++)
                    Card.cards[i].checkInitialized();
                gap = Math.max(5, w * 0.01);
                w -= (visibleCardsCount + 1) * gap; h -= gap;
                double cx = gap, cy = vh, cw = w / visibleCardsCount, ch = h - vh;
                for (Card card : Card.cards) {
                    layoutInArea(card, cx, cy, cw, ch);
                    cx += cw + gap;
                }
                scrollToFocusedCard(false);
                sizeChangedDuringScroll = false;
            }
        };
        cardsPane.setBackground(null);
        for (Card card : Card.cards)
            WebSiteShared.runOnMouseClick(card, () -> scrollToCard(Arrays.asList(Card.cards).indexOf(card), true));
        cardsPane.setOnMouseClicked(e -> scrollToCard(clickedCardIndex(e.getX()), false));
        cardsPane.setOnSwipeLeft(   e -> scrollToCard(focusedCardIndex - 1, false));
        cardsPane.setOnSwipeRight(  e -> scrollToCard(focusedCardIndex + 1, false));

        runOnMouseClick(demosText,  () -> showDemos(true));
        runOnMouseClick(webFxText,  () -> { if (demoThumbnailsPane.isVisible()) showDemos(false); });
        runOnMouseClick(startText, () -> openUrl("https://github.com/webfx-project/webfx"));
        setHostServices(getHostServices()); // Necessary to make openUrl() work

        webFxText.setOnMouseEntered(e -> startWebFxFillAnimation());
        webFxText.setOnMouseExited( e -> stopWebFxFillAnimation());

        setShapeHoverAnimationColor(startText, LAST_GITHUB_GRADIENT_COLOR.darker());
        setShapeHoverAnimationColor(demosText, FIRST_GITHUB_GRADIENT_COLOR.darker());

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        Scene scene = new Scene(cardsPane, screenBounds.getWidth(), screenBounds.getHeight(), BACKGROUND_GRADIENT);
        stage.setTitle("WebFX - JavaFX \u2192 JS transpiler");
        stage.setScene(scene);
        stage.show();
    }

    public static void updateTextFontSize(Text text, double fontSize) {
        WebSiteShared.updateTextFontSize(text, fontSize, true);
        text.setStrokeWidth(fontSize >= 70 ? 2 : 1);
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

    private void scrollToCard(int cardIndex, boolean playCard) {
        scrollHorizontallyToCard(cardIndex, playCard);
    }

    private void scrollHorizontallyToCard(int cardIndex, boolean playCard) {
        cardIndex = Math.min(cardIndex, Card.cards.length - 1);
        focusedCardIndex = cardIndex;
        scrollToFocusedCard(playCard);
    }

    private void scrollToFocusedCard(boolean playCard) {
        Timeline timeline = scrollTimeline;
        cardsPane.setTranslateY(0);
        int leftCardIndex = Math.max(0, Math.min(Card.cards.length - visibleCardsCount, focusedCardIndex - 1));
        if (!Card.cards[Math.min(Card.cards.length - 1, leftCardIndex + visibleCardsCount - 1)].checkInitialized())
            cardsPane.forceLayoutChildren();
        double translateX = -leftCardIndex * (cardsPane.getWidth() - gap) / visibleCardsCount;
        if (Card.cards[0].getTranslateX() == translateX)
            playCard(playCard);
        else if (scrollTimeline == null || translateX != scrollTimelineEndValue) {
            stopScrollTimeline();
            scrollTimelineEndValue = translateX;
            scrollTimeline = new Timeline(new KeyFrame(Duration.millis(500),
                    Arrays.stream(Card.cards).map(c -> new KeyValue(c.translateXProperty(), scrollTimelineEndValue, EASE_OUT_INTERPOLATOR)).toArray(KeyValue[]::new))
            );
        }
        if (timeline != scrollTimeline) {
            scrollTimeline.setOnFinished(e -> {
                scrollTimeline = null;
                if (sizeChangedDuringScroll)
                    cardsPane.forceLayoutChildren();
                playCard(playCard);
            });
            scrollTimeline.play();
        }
    }

    private void stopScrollTimeline() {
        if (scrollTimeline != null)
            scrollTimeline.stop();
        scrollTimelineEndValue = -1;
    }

    private void playCard(boolean play) {
        if (play)
            Card.cards[focusedCardIndex].transitionToNextStep();
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