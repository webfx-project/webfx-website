package dev.webfx.website.application;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import static dev.webfx.website.application.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
public class WebsiteApplication extends Application {

    private final Text webFxText = createWebFxSvgText(100);
    private final Text subtitle = createTranspilerSvgText(25);

    private AnimationTimer gradientAnimationTimer;
    private Timeline dashOffsetTimeline;

    @Override
    public void start(Stage stage) {
        VBox textVBox = new VBox();
        textVBox.getChildren().setAll(webFxText, subtitle);
        textVBox.setCursor(Cursor.HAND);
        textVBox.setOnMouseClicked(e -> toggleTextColorAnimation());
        textVBox.setAlignment(Pos.TOP_CENTER);
        subtitle.setTranslateY(-10);

        double MARGIN = 10;
        Pane rootPane = new Pane(textVBox, cards[0], cards[1], cards[2]) {
            @Override
            protected void layoutChildren() {
                double w = getWidth() - 4 * MARGIN, h = getHeight() - MARGIN;
                double vh = textVBox.prefHeight(w), cx = MARGIN, cy = vh, cw = w / 3, ch = h - vh;
                layoutInArea(textVBox, cx, 0, w, vh, 0, null, HPos.CENTER, VPos.TOP);
                layoutInArea(cards[0], cx, cy, cw, ch, 0, HPos.CENTER, VPos.BOTTOM);
                cx += cw + MARGIN;
                layoutInArea(cards[1], cx, cy, cw, ch, 0, HPos.CENTER, VPos.BOTTOM);
                cx += cw + MARGIN;
                layoutInArea(cards[2], cx, cy, cw, ch, 0, HPos.CENTER, VPos.BOTTOM);
            }
        };
        rootPane.setBackground(null);

        Scene scene = new Scene(rootPane, 800, 600, backgroundGradient);
        stage.setTitle("WebFX");
        stage.setScene(scene);
        stage.show();
    }

    private void startTextColorAnimation() {
        stopTextColorAnimation();

        gradientAnimationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                webFxText.setFill(createGithubGradient(now / (2 * Math.PI * 100_000_000)));
            }
        };
        gradientAnimationTimer.start();

        subtitle.setStroke(new Color(1, 1, 1, 0.5));
        subtitle.setStrokeDashOffset(0);
        dashOffsetTimeline = new Timeline();
        dashOffsetTimeline.getKeyFrames().setAll(new KeyFrame(new Duration(2000), new KeyValue(subtitle.strokeDashOffsetProperty(), 25)));
        dashOffsetTimeline.setCycleCount(Animation.INDEFINITE);
        dashOffsetTimeline.play();
    }

    private void stopTextColorAnimation() {
        if (gradientAnimationTimer != null) {
            gradientAnimationTimer.stop();
            dashOffsetTimeline.stop();
            gradientAnimationTimer = null;
            dashOffsetTimeline = null;
        }
        subtitle.setStroke(null);
        webFxText.setFill(Color.BLACK);
    }

    private void toggleTextColorAnimation() {
        if (gradientAnimationTimer != null)
            stopTextColorAnimation();
        else
            startTextColorAnimation();
    }
}
