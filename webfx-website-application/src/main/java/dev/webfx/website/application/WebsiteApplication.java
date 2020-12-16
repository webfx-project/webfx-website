package dev.webfx.website.application;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import dev.webfx.platform.client.services.windowlocation.WindowLocation;

/**
 * @author Bruno Salmon
 */
public class WebsiteApplication extends Application {

    @Override
    public void start(Stage stage) {
        Text webFxText = new Text("WebFx");
        webFxText.setFont(Font.font("Arial", 120));
        LinearGradient purpleGradient = LinearGradient.valueOf("to right, #753a88, #cc2b5e");
        webFxText.setFill(purpleGradient);
        Text subtitle = new Text("A JavaFx application transpiler");
        LinearGradient githubGradient = LinearGradient.valueOf("to left, #FFE580, #FF7571, #EA5DAD, #C2A0FD, #3BF0E4, #B2F4B6");
        subtitle.setFill(githubGradient);
        subtitle.setFont(Font.font("Arial", 25));
        Text footer = new Text("Site under construction");
        footer.setFill(purpleGradient);
        footer.setFont(Font.font("Arial", FontWeight.NORMAL, FontPosture.ITALIC, 14));
        Pane githubButton = createSVGButton(SvgButtonPaths.getGithubPath(), purpleGradient);
        VBox.setMargin(githubButton, new Insets(50));
        githubButton.setCursor(Cursor.HAND);
        githubButton.setOnMouseClicked(e -> WindowLocation.assignHref("https://github.com/webfx-project/webfx"));
        VBox vBox = new VBox(webFxText, subtitle, githubButton, footer);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, 800, 600, Color.web("#0D1117"));
        stage.setTitle("WebFx");
        stage.setScene(scene);
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

}
