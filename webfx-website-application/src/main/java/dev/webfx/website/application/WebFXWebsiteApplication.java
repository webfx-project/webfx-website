package dev.webfx.website.application;

import dev.webfx.extras.webtext.SvgText;
import dev.webfx.platform.uischeduler.UiScheduler;
import dev.webfx.platform.windowlocation.WindowLocation;
import dev.webfx.website.application.cards.*;
import dev.webfx.website.application.demos.DemosPage;
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
    private DemosPage demosPage; // lazy initialisation
    private final CardsPane webFXPage = new CardsPane(
            new WebFXCard(),
            new CrossPlatformCard(),
            new JavaFullStackCard(),
            new LongTermCard(),
            new ResponsiveCard(),
            new MagicalCard());
    private CardsPane startPage; // lazy initialisation
    private boolean showDemosPage, showWebFXPage = true, showStartPage;
    private AnimationTimer webFxFillAnimationTimer;
    private final LayoutPane containerPane = new LayoutPane(demosText, webFxText, startText, webFXPage) {
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
            if (showDemosPage && demosPage != null)
                layoutInArea(demosPage, 0, vh, w, h - vh);
            if (showWebFXPage)
                layoutInArea(webFXPage, 0, vh, w, h - vh);
            if (showStartPage && startPage != null)
                layoutInArea(startPage, 0, vh, w, h - vh);
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

        runOnMouseClick(demosText, () -> showPage(true, false, false));
        runOnMouseClick(webFxText, () -> showPage(false, true, false));
        runOnMouseClick(startText, () -> showPage(false, false, true));

        webFxText.setOnMouseEntered(e -> startWebFXFillAnimation());
        webFxText.setOnMouseExited( e -> stopWebFXFillAnimation());

        // Initial UI rooting in case the window location ends with #/demos or #/start
        String href = WindowLocation.getHref();
        if (href.endsWith("#/demos"))
            showPage(true, false, false);
        else if (href.endsWith("#/start"))
            showPage(false, false, true);
    }

    private void onSwipe(boolean left) {
        CardsPane page = showWebFXPage ? webFXPage : showStartPage ? startPage : null;
        if (page != null)
            page.onPaneSwipe(left);
    }

    public static void updateTextFontSize(Text text, double fontSize) {
        WebSiteShared.updateTextFontSize(text, fontSize, true);
        text.setStrokeWidth(fontSize >= 70 ? 2 : 1);
    }

    private void showPage(boolean showDemosPage, boolean showWebFXPage, boolean showStartPage) {
        if (this.showDemosPage == showDemosPage && this.showWebFXPage == showWebFXPage && this.showStartPage == showStartPage)
            return;
        if (showDemosPage && demosPage == null) {
            // Creating the demo pane and adding it to the cards pane
            containerPane.getChildren().add(demosPage = new DemosPage());
            demosPage.setOpacity(0);
            // Postponing the fade effect after the next layout pass (which may take time to consider the demo pane addition)
            Platform.runLater(() -> showPage(true, showWebFXPage, showStartPage));
            return;
        }
        if (showStartPage && startPage == null) {
            // Creating the demo pane and adding it to the cards pane
            containerPane.getChildren().add(startPage = new CardsPane(
                    new DocumentationCard(),
                    new BlogCard(),
                    new GitHubCard()
            ));
            startPage.setOpacity(0);
            // Postponing the fade effect after the next layout pass (which may take time to consider the demo pane addition)
            Platform.runLater(() -> showPage(showDemosPage, showWebFXPage, true));
            long index = 0;
            for (Card card : startPage.cards) {
                card.setTranslateY(containerPane.getHeight());
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(800), new KeyValue(card.translateYProperty(), 0, EASE_OUT_INTERPOLATOR)));
                UiScheduler.scheduleDelay(200 * index++, timeline::play);
            }
            return;
        }
        this.showDemosPage = showDemosPage;
        this.showWebFXPage = showWebFXPage;
        this.showStartPage = showStartPage;
        stopWebFXFillAnimation();
        // Replacing the window location with the hashtag matching the request page, and make this page visible
        String href = WindowLocation.getHref();
        if (href.contains("#")) // Removing the existing hashtag
            href = href.substring(0, href.indexOf('#'));
        if (showDemosPage) {
            href += "#/demos";
            demosPage.setVisible(true);
        } else if (showWebFXPage) {
            href += "#";
            webFXPage.setVisible(true);
        } else if (showStartPage) {
            href += "#/start";
            startPage.setVisible(true);
        }
        // Actual replacement of the window location
        WindowLocation.replaceHref(href);
        containerPane.forceLayoutChildren();
        Timeline fadeTimeline = new Timeline(new KeyFrame(Duration.millis(500),
                Stream.of(demosPage, webFXPage, startPage)
                        .filter(Objects::nonNull)
                        .map(node -> new KeyValue(node.opacityProperty(),
                                node == demosPage ? (showDemosPage ? 1 : 0) :
                                        node == webFXPage ? (showWebFXPage ? 1 : 0) :
                                                showStartPage ? 1 : 0))
                        .toArray(KeyValue[]::new))
        );
        fadeTimeline.setOnFinished(e -> {
            if (demosPage != null)
                demosPage.setVisible(showDemosPage);
            webFXPage.setVisible(showWebFXPage);
            if (startPage != null)
                startPage.setVisible(showStartPage);
        });
        fadeTimeline.play();
    }

    private void startWebFXFillAnimation() {
        stopWebFXFillAnimation();
        webFxFillAnimationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                webFxText.setFill(createAngleGithubGradient(now / (2 * Math.PI * 100_000_000)));
            }
        };
        webFxFillAnimationTimer.start();
    }

    private void stopWebFXFillAnimation() {
        if (webFxFillAnimationTimer != null) {
            webFxFillAnimationTimer.stop();
            webFxFillAnimationTimer = null;
        }
        webFxText.setFill(GITHUB_GRADIENT);
    }
}