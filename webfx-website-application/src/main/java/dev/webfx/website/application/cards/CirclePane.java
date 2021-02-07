package dev.webfx.website.application.cards;

import dev.webfx.website.application.WebSiteShared;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Arrays;

/**
 * @author Bruno Salmon
 */
final class CirclePane extends Pane {

    private final double angle, lettersAngle;
    private double radius;
    private final Text[] circleLetters;
    private final Paint fill;
    private Paint stroke = Color.WHITE;
    private final ScalePane topScalePane, bottomScalePane;

    CirclePane(String circleText, double angle, Paint fill, Node topNode, Node bottomNode) {
        this.angle = angle;
        this.lettersAngle = angle;
        this.fill = fill;
        topScalePane = createScalePane(topNode);
        bottomScalePane = createScalePane(bottomNode);
        setBlendMode(BlendMode.SCREEN); // Note: HTML & JavaFX implementation differs, so the rendering is different
        getChildren().setAll(circleLetters = createLetters(circleText));
        getChildren().add(topScalePane);
        if (bottomScalePane != null)
            getChildren().add(bottomScalePane);
    }

    private ScalePane createScalePane(Node node) {
        ScalePane scalePane = null;
        if (node != null) {
            scalePane = new ScalePane(node);
            scalePane.setAlwaysTry(true);
        }
        return scalePane;
    }

    public void setStroke(Paint stroke) {
        this.stroke = stroke;
    }

    void setRadius(double radius) {
        if (radius != this.radius) {
            this.radius = radius;
            double d = 2 * radius;
            setMinSize(d, d);
            setMaxSize(d, d);
            CornerRadii radii = new CornerRadii(radius);
            WebSiteShared.setRegionBackground(this, fill, radii);
            setBorder(new Border(new BorderStroke(stroke, BorderStrokeStyle.SOLID, radii, new BorderWidths(radius * 0.05))));
        }
    }

    double getAngle() {
        return angle;
    }

    private Text[] createLetters(String text) {
        return text.chars().mapToObj(c -> createLetter("" + (char) c)).toArray(Text[]::new);
    }

    private Text createLetter(String text) {
        Text letter = new Text(text);
        letter.setFill(Color.WHITE);
        letter.setMouseTransparent(true);
        letter.setTextAlignment(TextAlignment.CENTER);
        letter.setManaged(false); // Workaround to avoid an async infinite loop in layoutChildren() (Web version only)
        return letter;
    }

    @Override
    protected void layoutChildren() {
        double width  = getWidth();
        double height = getHeight();
        double size = Math.min(width, height);
        double r = size / 2;
        double length = Arrays.stream(circleLetters).mapToDouble(l -> getLetterBounds(l).getWidth()).sum();
        boolean top = lettersAngle < 0;
        double middleAngle = (top ? -1 : +1) * Math.PI / 2;  //lettersAngle * Math.PI / 180;
        double anglePerLength = 0.9 * Math.PI / 2 / length * (top ? 1 : -1);
        double angle = middleAngle - anglePerLength * length / 2;
        Font font = Font.font(r * 0.2);
        for (Text letter : circleLetters) {
            letter.setFont(font);
            Bounds letterBounds = getLetterBounds(letter);
            double lw = letterBounds.getWidth();
            double lh = letterBounds.getHeight();
            angle += anglePerLength * lw / 2;
            if (letter.getRotate() != angle) {
                letter.setRotate(angle / Math.PI * 180 + (top ? 90 : -90));
                layoutInArea(letter, r + 0.75 * r * Math.cos(angle) - lw / 2, r + 0.75 * r * Math.sin(angle) - lh / 2, lw, lh, 0, HPos.CENTER, VPos.CENTER);
            }
            angle += anglePerLength * lw / 2;
        }
        double h = 0.3 * height;
        if (bottomScalePane == null) {
            double dy = -0.04 * height;
            layoutInArea(topScalePane, 0, height / 2 - h / 2 + dy, width, h, 0, HPos.CENTER, VPos.CENTER);
        } else {
            double dy = 0.06 * height, gap = 0.03 * height;
            layoutInArea(topScalePane,    0, height / 2 - h + dy - gap / 2, width, h, 0, HPos.CENTER, VPos.CENTER);
            layoutInArea(bottomScalePane, 0, height / 2 + dy + gap / 2, width, h, 0, HPos.CENTER, VPos.CENTER);
        }
    }

    private Bounds getLetterBounds(Text letter) {
        Bounds bounds = (Bounds) letter.getProperties().get("letterBounds");
        if (bounds == null)
            letter.getProperties().put("letterBounds", bounds = letter.getBoundsInLocal());
        return bounds;
    }
}
