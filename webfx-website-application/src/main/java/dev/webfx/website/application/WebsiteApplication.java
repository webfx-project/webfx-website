package dev.webfx.website.application;

import dev.webfx.extras.webtext.controls.SvgText;
import dev.webfx.platform.client.services.windowlocation.WindowLocation;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.value.WritableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.SVGPath;
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

    private final Text webFxText = new SvgText("WebFX");
    private final Text subtitle = new SvgText("A JavaFX application transpiler");

    private final LinearGradient githubGradient = LinearGradient.valueOf("to left, #FFE580, #FF7571, #EA5DAD, #C2A0FD, #3BF0E4, #B2F4B6");
    private final LinearGradient purpleGradient = LinearGradient.valueOf("to right, #753a88, #cc2b5e");
    private final DropShadow dropShadow = new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 10, 0, 0, 15);

    private AnimationTimer gradientAnimationTimer;
    private Timeline dashOffsetTimeline;

    @Override
    public void start(Stage stage) {
        webFxText.setFont(Font.font("Arial", FontWeight.BOLD, 120));
        webFxText.setStroke(Color.WHITE);
        webFxText.setStrokeWidth(2);
        webFxText.setEffect(dropShadow);
        webFxText.setCursor(Cursor.HAND);
        webFxText.setOnMouseClicked(e -> startTextColorAnimation());

        subtitle.setFont(Font.font("Arial", 25));
        subtitle.setStrokeWidth(2);
        subtitle.setStrokeLineCap(StrokeLineCap.BUTT);
        subtitle.getStrokeDashArray().setAll(5d, 20d);
        subtitle.setEffect(dropShadow);
        subtitle.setFill(githubGradient);
        subtitle.setTranslateY(-10);
        subtitle.setOnMouseClicked(e -> startTextColorAnimation());

        Pane githubButton = createSVGButton(SvgButtonPaths.getGithubPath(), purpleGradient);
        //VBox.setMargin(githubButton, new Insets(50));
        githubButton.setOnMouseClicked(e -> WindowLocation.assignHref("https://github.com/webfx-project/webfx"));

        VBox vBox = new VBox(0, webFxText, subtitle);
        vBox.setAlignment(Pos.TOP_CENTER);
        Scene scene = new Scene(new StackPane(githubButton, vBox), 800, 600, Color.web("#0D1117"));
        stage.setTitle("WebFX");
        stage.setScene(scene);
        vBox.setTranslateY(scene.getHeight());

        ScaleTransition scaleTransition = new ScaleTransition(new Duration(2000), githubButton);
        scaleTransition.setFromX(0);
        scaleTransition.setFromY(0);
        scaleTransition.setToX(150);
        scaleTransition.setToY(150);
        scaleTransition.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                return t * t * t * t;
            }
        });
        scaleTransition.setOnFinished(e -> startTextColorAnimation());

        FadeTransition fadeTransition = new FadeTransition(new Duration(2000), githubButton);
        fadeTransition.setToValue(0);

        TranslateTransition translateTransition = new TranslateTransition(new Duration(2000), vBox);
        translateTransition.setToY(0);
        translateTransition.setInterpolator(Interpolator.SPLINE(0, .75, .25, 1));

        PauseTransition pauseTransition = new PauseTransition(new Duration(10000));
        pauseTransition.setOnFinished(e -> stopTextColorAnimation());

        new SequentialTransition(
                scaleTransition,
                new ParallelTransition(fadeTransition, translateTransition),
                pauseTransition)
                .play();

        stage.show();
    }

    private Pane createSVGButton(String svgPath, Paint fill) {
        SVGPath path = new SVGPath();
        path.setContent(svgPath);
        path.setFill(fill);
        // We now embed the svg path in a pane. The reason is for a better click experience. Because in JavaFx (not in
        // the browser), the clicking area is only the filled shape, not the empty space in that shape. So when clicking
        // on a gear icon on a mobile for example, even if globally our finger covers the icon, the final click point
        // may be in this empty space, making the button not reacting, leading to a frustrating experience.
        Pane pane = new Pane(path); // Will act as the mouse click area covering the entire surface
        // The pane needs to be reduced to the svg path size (which we can get using the layout bounds).
        path.sceneProperty().addListener((observableValue, scene, t1) -> { // This postpone is necessary only when running in the browser, not in standard JavaFx
            Bounds b = path.getLayoutBounds(); // Bounds computation should be correct now even in the browser
            pane.setMaxSize(b.getWidth(), b.getHeight());
        });
        pane.setCursor(Cursor.HAND);
        return pane;
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
        dashOffsetTimeline = new Timeline();
        dashOffsetTimeline.getKeyFrames().setAll(new KeyFrame(new Duration(2000), new KeyValue(subtitle.strokeDashOffsetProperty(), 25)));
        dashOffsetTimeline.setCycleCount(Animation.INDEFINITE);
        dashOffsetTimeline.play();
    }

    private void stopTextColorAnimation() {
        if (gradientAnimationTimer != null)
            gradientAnimationTimer.stop();
        if (dashOffsetTimeline != null)
            dashOffsetTimeline.stop();
        subtitle.setStroke(null);
        webFxText.setFill(Color.BLACK);
    }

    private static <T> Timeline animateProperty(int durationMillis, WritableValue<T> target, T endValue) {
        Timeline timeline = new Timeline(new KeyFrame(new Duration(durationMillis), new KeyValue(target, endValue, Interpolator.EASE_BOTH)));
        timeline.play();
        return timeline;
    }

}
