package dev.webfx.website.application;

import dev.webfx.extras.webtext.controls.SvgText;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.value.WritableValue;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

/**
 * @author Bruno Salmon
 */
public class WebsiteApplication extends Application {

    private final LinearGradient githubGradient = LinearGradient.valueOf("to left, #FFE580, #FF7571, #EA5DAD, #C2A0FD, #3BF0E4, #B2F4B6");
    private final DropShadow dropShadow = new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 10, 0, 0, 15);
    private final Text webFxText = new SvgText("WebFX");
    private final Text subtitle = new SvgText("A JavaFX application transpiler");

    private AnimationTimer gradientAnimationTimer;
    private Timeline dashOffsetTimeline;

    @Override
    public void start(Stage stage) {
        webFxText.setFont(Font.font("Arial", FontWeight.BOLD, 100));
        webFxText.setStroke(Color.WHITE);
        webFxText.setStrokeWidth(2);
        webFxText.setEffect(dropShadow);

        subtitle.setFont(Font.font("Arial", 25));
        subtitle.setStrokeWidth(2);
        subtitle.setStrokeLineCap(StrokeLineCap.BUTT);
        subtitle.getStrokeDashArray().setAll(5d, 20d);
        //subtitle.setEffect(dropShadow);
        subtitle.setFill(githubGradient);
        subtitle.setTranslateY(-10);
        
        Text box1Text = new Text("Fullstack Java");
        box1Text.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        box1Text.setFill(Color.WHITE);
        box1Text.setEffect(dropShadow);
        BorderPane box1 = new BorderPane(box1Text);

        Text box2Text = new Text("The power of desktop");
        box2Text.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        box2Text.setFill(Color.WHITE);
        box2Text.setEffect(dropShadow);
        BorderPane box2 = new BorderPane(box2Text);

        Text box3Text = new Text("Truly cross-platform");
        box3Text.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        box3Text.setFill(Color.WHITE);
        box3Text.setEffect(dropShadow);
        BorderPane box3 = new BorderPane(box3Text);

        VBox textVBox = new VBox();
        textVBox.getChildren().setAll(webFxText, subtitle);

        textVBox.setCursor(Cursor.HAND);
        textVBox.setOnMouseClicked(e -> toggleTextColorAnimation());
        textVBox.setAlignment(Pos.TOP_CENTER);

        Pane pane = new Pane(textVBox, box1, box2, box3) {
            @Override
            protected void layoutChildren() {
                double w = getWidth(), h = getHeight();
                double vh = textVBox.prefHeight(w);
                layoutInArea(textVBox, 0, 0, w, vh, 0, null, HPos.CENTER, VPos.TOP);
                layoutInArea(box1, 0, vh, w / 3, h - vh, 0, null, HPos.CENTER, VPos.BOTTOM);
                layoutInArea(box2, w/3, vh, w / 3, h - vh, 0, null, HPos.CENTER, VPos.BOTTOM);
                layoutInArea(box3, 2*w/3, vh, w / 3, h - vh, 0, null, HPos.CENTER, VPos.BOTTOM);
            }
        };

        Scene scene = new Scene(pane, 800, 600, Color.web("#753a88"));
        stage.setTitle("WebFX");
        stage.setScene(scene);

        double outOfScreen = scene.getHeight();
        pane.setTranslateY(outOfScreen);
        box1.setTranslateY(outOfScreen);
        box2.setTranslateY(outOfScreen);
        box3.setTranslateY(outOfScreen);
        Interpolator easeOut = Interpolator.SPLINE(0, .75, .25, 1);
        animateProperty(4000, scene.fillProperty(), Color.web("#0D1117"));
        animateProperty(1500, pane.translateYProperty(), 0, easeOut)
                .setOnFinished(e1 -> animateProperty(700, box1.translateYProperty(), 0, easeOut)
                .setOnFinished(e2 -> animateProperty(700, box2.translateYProperty(), 0, easeOut)
                .setOnFinished(e3 -> animateProperty(700, box3.translateYProperty(), 0, easeOut))));
        startTextColorAnimation();

        stage.show();
    }


    private void startTextColorAnimation() {
        stopTextColorAnimation();

        List<Stop> githubStops = githubGradient.getStops();
        gradientAnimationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double angle = now / (2 * Math.PI * 1_000_000_00);
                double length = 200;
                webFxText.setFill(new LinearGradient(0, 0, length * Math.cos(angle), length * Math.sin(angle), false, CycleMethod.REPEAT, githubStops));
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

    private static <T> Timeline animateProperty(int durationMillis, WritableValue<T> target, T endValue) {
        return animateProperty(durationMillis, target, endValue, Interpolator.EASE_BOTH);
    }

    private static <T> Timeline animateProperty(int durationMillis, WritableValue<T> target, T endValue, Interpolator interpolator) {
        Timeline timeline = new Timeline(new KeyFrame(new Duration(durationMillis), new KeyValue(target, endValue, interpolator)));
        timeline.play();
        return timeline;
    }
}
