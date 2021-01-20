package dev.webfx.website.application;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
    private boolean showMenu = true;

    @Override
    public void start(Stage stage) {
        webFxText.setOnMouseClicked(e -> toggleTextColorAnimation());
        webFxText.setCursor(Cursor.HAND);
        double MARGIN = 10;
        Pane rootPane = new Pane(webFxText, subtitle, cards[0], cards[1], cards[2]) {
            @Override
            protected void layoutChildren() {
                double w = getWidth() - 4 * MARGIN, h = getHeight() - MARGIN, fontSize, vh = 0, cx = MARGIN;
                webFxText.setVisible(showMenu);
                subtitle.setVisible(showMenu);
                if (showMenu) {
                    webFxText.setFont(Font.font("Arial", FontWeight.BOLD, fontSize = h * 0.12));
                    webFxText.setStrokeWidth(fontSize >= 70 ? 2 : 1);
                    subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, fontSize = h * 0.03));
                    subtitle.setStrokeWidth(fontSize >= 70 ? 2 : 1);
                    vh = webFxText.prefHeight(w);
                    layoutInArea(webFxText, cx, 0, w, vh, 0, null, HPos.CENTER, VPos.TOP);
                    double sh = subtitle.prefHeight(w);
                    vh = 0.9 * vh;
                    layoutInArea(subtitle, cx, vh, w, sh, 0, null, HPos.CENTER, VPos.TOP);
                    vh += sh * 1.2;
                }
                double cy = vh, cw = w / 3, ch = h - vh;
                layoutInArea(cards[0], cx, cy, cw, ch, 0, HPos.CENTER, VPos.BOTTOM);
                cx += cw + MARGIN;
                layoutInArea(cards[1], cx, cy, cw, ch, 0, HPos.CENTER, VPos.BOTTOM);
                cx += cw + MARGIN;
                layoutInArea(cards[2], cx, cy, cw, ch, 0, HPos.CENTER, VPos.BOTTOM);
            }
        };
        rootPane.setBackground(null);
        rootPane.setOnSwipeUp(e -> {
            showMenu = false;
            rootPane.requestLayout();
        });
        rootPane.setOnSwipeDown(e -> {
            showMenu = true;
            rootPane.requestLayout();
        });

        Scene scene = new Scene(rootPane, 800, 600, backgroundGradient);
        stage.setTitle(webFxText.getText() + " - " + subtitle.getText());
        stage.setScene(scene);
        stage.show();
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
