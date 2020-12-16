package dev.webfx.website.application;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
public class WebsiteApplication extends Application {

    @Override
    public void start(Stage stage) {
        Text webFxText = new Text("WebFx");
        webFxText.setFont(new Font("Arial", 100));
        webFxText.setFill(LinearGradient.valueOf("to right, #753a88, #cc2b5e"));
        Text subtitle = new Text("A JavaFx application transpiler");
        subtitle.setFill(LinearGradient.valueOf("to left, #FFE580 -21.36%, #FF7571 -2.45%, #EA5DAD 26.84%, #C2A0FD 64.15%, #3BF0E4 108.29%, #B2F4B6 159.03%"));
        subtitle.setFont(new Font("Arial", 25));
        VBox vBox = new VBox(webFxText, subtitle);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, 800, 600, Color.web("#0D1117"));
        stage.setTitle("WebFx");
        stage.setScene(scene);
        stage.show();
    }
}
