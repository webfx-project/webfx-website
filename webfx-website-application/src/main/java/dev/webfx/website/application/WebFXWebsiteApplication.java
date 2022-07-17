package dev.webfx.website.application;

import dev.webfx.extras.webtext.SvgText;
import dev.webfx.platform.uischeduler.UiScheduler;
import dev.webfx.website.application.cards.*;
import dev.webfx.website.application.demos.DemosThumbnailsPane;
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

import java.util.Objects;
import java.util.stream.Stream;

import static dev.webfx.website.application.shared.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
public final class WebFXWebsiteApplication extends Application {

    private final Text webFxText = createWebFxSvgText();
    private final Text demosText = setUpText(new SvgText("Demos"), 50, true, false, true, true);
    private final Text startText = setUpText(new SvgText("Start"), 50, true, false, true, true);
    private DemosThumbnailsPane demosThumbnailsPane; // lazy initialisation
    private final CardsPane webfxCardsPane = new CardsPane(
            new WebFXCard(),
            new CrossPlatformCard(),
            new JavaFullStackCard(),
            new SustainableCard(),
            new ResponsiveCard(),
            new MagicalCard());
    private CardsPane startCardsPane; // lazy initialisation
    private boolean showDemos, showWebFxCards = true, showStartCards;
    private AnimationTimer webFxFillAnimationTimer;
    private final LayoutPane containerPane = new LayoutPane(demosText, webFxText, startText, webfxCardsPane) {
        @Override
        protected void layoutChildren(double width, double height) {
            double w = width, h = height, vh;
            double fontSize = Math.min(0.08 * w, 0.12 * h);
            updateTextFontSize(webFxText, fontSize);
            updateTextFontSize(demosText, fontSize);
            updateTextFontSize(startText, fontSize);
            vh = webFxText.prefHeight(w);
            centerInArea(webFxText, 0, 0, w, vh);
            centerInArea(demosText, 0, 0, w/3, vh);
            centerInArea(startText, w - w/3, 0, w/3, vh);
            if (showDemos && demosThumbnailsPane != null)
                layoutInArea(demosThumbnailsPane, 0, vh, w, h - vh);
            if (showWebFxCards)
                layoutInArea(webfxCardsPane, 0, vh, w, h - vh);
            if (showStartCards && startCardsPane != null)
                layoutInArea(startCardsPane, 0, vh, w, h - vh);
        }
    };

    @Override
    public void start(Stage stage) {
        containerPane.setBackground(null);
        containerPane.setOnSwipeLeft( e -> onSwipe(true));
        containerPane.setOnSwipeRight(e -> onSwipe(false));

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        Scene scene = new Scene(containerPane, screenBounds.getWidth(), screenBounds.getHeight(), BACKGROUND_GRADIENT);
        stage.setTitle("WebFX - JavaFX \u2192 JS transpiler");
        stage.setScene(scene);
        stage.show();

        setShapeHoverAnimationColor(demosText, FIRST_GITHUB_GRADIENT_COLOR.darker());
        setShapeHoverAnimationColor(startText, LAST_GITHUB_GRADIENT_COLOR.darker());

        runOnMouseClick(demosText, () -> switchShow(true, false, false));
        runOnMouseClick(webFxText, () -> switchShow(false, true, false));
        runOnMouseClick(startText, () -> switchShow(false, false, true));

        webFxText.setOnMouseEntered(e -> startWebFxFillAnimation());
        webFxText.setOnMouseExited( e -> stopWebFxFillAnimation());

        setHostServices(getHostServices()); // Necessary to make openUrl() work
    }

    private void onSwipe(boolean left) {
        CardsPane cardsPane = showWebFxCards ? webfxCardsPane : showStartCards ? startCardsPane : null;
        if (cardsPane != null)
            cardsPane.onSwipe(left);
    }

    public static void updateTextFontSize(Text text, double fontSize) {
        WebSiteShared.updateTextFontSize(text, fontSize, true);
        text.setStrokeWidth(fontSize >= 70 ? 2 : 1);
    }

    private void switchShow(boolean showDemos, boolean showWebFxCards, boolean showStartCards) {
        if (this.showDemos == showDemos && this.showWebFxCards == showWebFxCards && this.showStartCards == showStartCards)
            return;
        if (showDemos && demosThumbnailsPane == null) {
            // Creating the demo pane and adding it to the cards pane
            containerPane.getChildren().add(demosThumbnailsPane = new DemosThumbnailsPane());
            demosThumbnailsPane.setOpacity(0);
            // Postponing the fade effect after the next layout pass (which may take time to consider the demo pane addition)
            Platform.runLater(() -> switchShow(showDemos, showWebFxCards, showStartCards));
            return;
        }
        if (showStartCards && startCardsPane == null) {
            // Creating the demo pane and adding it to the cards pane
            containerPane.getChildren().add(startCardsPane = new CardsPane(
                    new DocumentationCard(),
                    new BlogCard(),
                    new GitHubCard()
            ));
            startCardsPane.setOpacity(0);
            // Postponing the fade effect after the next layout pass (which may take time to consider the demo pane addition)
            Platform.runLater(() -> switchShow(showDemos, showWebFxCards, showStartCards));
            long index = 0;
            for (Card card : startCardsPane.cards) {
                card.setTranslateY(containerPane.getHeight());
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(800), new KeyValue(card.translateYProperty(), 0, EASE_OUT_INTERPOLATOR)));
                UiScheduler.scheduleDelay(200 * index++, timeline::play);
            }
            return;
        }
        this.showDemos = showDemos;
        this.showWebFxCards = showWebFxCards;
        this.showStartCards = showStartCards;
        stopWebFxFillAnimation();
        if (showDemos)
            demosThumbnailsPane.setVisible(true);
        if (showWebFxCards)
            webfxCardsPane.setVisible(true);
        if (showStartCards)
            startCardsPane.setVisible(true);
        containerPane.forceLayoutChildren();
        Timeline fadeTimeline = new Timeline(new KeyFrame(Duration.millis(500),
                Stream.of(demosThumbnailsPane, webfxCardsPane, startCardsPane)
                        .filter(Objects::nonNull)
                        .map(node -> new KeyValue(node.opacityProperty(),
                                node == demosThumbnailsPane ? (showDemos ? 1 : 0) :
                                        node == webfxCardsPane ? (showWebFxCards ? 1 : 0) :
                                                showStartCards ? 1 : 0))
                        .toArray(KeyValue[]::new))
        );
        fadeTimeline.setOnFinished(e -> {
            if (demosThumbnailsPane != null)
                demosThumbnailsPane.setVisible(showDemos);
            webfxCardsPane.setVisible(showWebFxCards);
            if (startCardsPane != null)
                startCardsPane.setVisible(showStartCards);
        });
        fadeTimeline.play();
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