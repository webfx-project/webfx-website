package dev.webfx.website.application.cards;

import dev.webfx.website.application.WebSiteShared;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
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
final class CirclePane extends StackPane {

    private final double angle, lettersAngle;
    private final Text[] circleLetters;

    CirclePane(String circleText, double radius, double angle, Paint fill) {
        this.angle = angle;
        this.lettersAngle = angle;
        double d = 2 * radius;
        setMinSize(d, d);
        setMaxSize(d, d);
        setBlendMode(BlendMode.SCREEN);
        CornerRadii radii = new CornerRadii(radius);
        setBackground(new Background(new BackgroundFill(fill, radii, Insets.EMPTY)));
        setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, radii, BorderStroke.THICK)));
        setEffect(WebSiteShared.dropShadow);
        getChildren().setAll(circleLetters = createLetters(circleText, Color.WHITE));
    }

    double getAngle() {
        return angle;
    }

    private Text[] createLetters(String text, Paint fill) {
        return text.chars().mapToObj(c -> createLetter("" + (char) c, fill)).toArray(Text[]::new);
    }

    private Text createLetter(String text, Paint fill) {
        Text letter = new Text(text);
        letter.setFill(fill);
        letter.setMouseTransparent(true);
        letter.setTextAlignment(TextAlignment.CENTER);
        letter.setManaged(false); // Workaround to avoid an async infinite loop in layoutChildren() (Web version only)
        return letter;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double width  = getWidth();
        double height = getHeight();
        double size = Math.min(width, height);
        double length = Arrays.stream(circleLetters).mapToDouble(l -> getLetterBounds(l).getWidth()).sum();
        double r = size / 2;
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
    }

    private Bounds getLetterBounds(Text letter) {
        Bounds bounds = (Bounds) letter.getProperties().get("letterBounds");
        if (bounds == null)
            letter.getProperties().put("letterBounds", bounds = letter.getBoundsInLocal());
        return bounds;
    }
}
