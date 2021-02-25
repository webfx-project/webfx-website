package dev.webfx.website.application.cards;

import dev.webfx.website.application.shared.WebSiteShared;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.ScalePane;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
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
final class CirclePane extends LayoutPane {

    private final double angle, lettersAngle, lettersAngleLength;
    private final boolean showBorder;
    private double radius;
    private final Text[] circleLetters;
    private Paint stroke = Color.WHITE;
    private final ScalePane topScalePane, bottomScalePane;
    private final ObjectProperty<Paint> fillProperty = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            updateBackground(false);
        }
    };

    CirclePane(String circleText, double angle, Paint fill, Node topNode, Node bottomNode) {
        this(circleText, angle, -1 , fill, topNode, bottomNode, fill != Color.TRANSPARENT);
    }

    CirclePane(String circleText, double angle, double lettersAngleLength, Paint fill, Node topNode, Node bottomNode, boolean showBorder) {
        this.angle = angle;
        this.lettersAngle = angle;
        this.lettersAngleLength = lettersAngleLength > 0 ? lettersAngleLength : 0.9 * 90 * (circleText.length() <= 9 ? 1 : circleText.length() / 6.);
        this.showBorder = showBorder;
        topScalePane = createScalePane(topNode);
        bottomScalePane = createScalePane(bottomNode);
        circleLetters = circleText == null ? null : createLetters(circleText);
        if (circleLetters != null)
            getChildren().setAll(circleLetters);
        if (topScalePane != null)
            getChildren().add(topScalePane);
        if (bottomScalePane != null)
            getChildren().add(bottomScalePane);
        setFill(fill);
    }

    private ScalePane createScalePane(Node node) {
        ScalePane scalePane = null;
        if (node != null) {
            scalePane = new ScalePane(node);
            scalePane.setAlwaysTry(true);
        }
        return scalePane;
    }

    Paint getFill() {
        return fillProperty.get();
    }

    void setFill(Paint fill) {
        fillProperty.setValue(fill);
    }

    ObjectProperty<Paint> fillProperty() {
        return fillProperty;
    }

    public void setStroke(Paint stroke) {
        this.stroke = stroke;
    }

    void setRadius(double radius) {
        if (radius != this.radius) {
            this.radius = radius;
            double d = 2 * radius;
            setMinSize(d, d);
            setPrefSize(d, d);
            setMaxSize(d, d);
            updateBackground(true);
        }
    }

    private void updateBackground(boolean updateBorder) {
        CornerRadii radii = new CornerRadii(radius);
        WebSiteShared.setRegionBackground(this, getFill(), radii);
        if (updateBorder && showBorder)
            setBorder(new Border(new BorderStroke(stroke, BorderStrokeStyle.SOLID, radii, new BorderWidths(radius * 0.05))));
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
    protected void layoutChildren(double width, double height) {
        double size = Math.min(width, height);
        if (circleLetters != null) {
            double r = size / 2;
            double length = Arrays.stream(circleLetters).mapToDouble(l -> getLetterBounds(l).getWidth()).sum();
            boolean top = lettersAngle < 0;
            double middleAngle = (top ? -1 : +1) * Math.PI / 2;  //lettersAngle * Math.PI / 180;
            double anglePerLength = lettersAngleLength * Math.PI / 180 / length * (top ? 1 : -1);
            double angle = middleAngle - anglePerLength * length / 2;
            Font font = Font.font(r * 0.2);
            for (Text letter : circleLetters) {
                letter.setFont(font);
                Bounds letterBounds = getLetterBounds(letter);
                double lw = letterBounds.getWidth();
                double lh = letterBounds.getHeight();
                if (lw == 0 && lh == 0) { // this happens only in the browser (not OpenJFX) for the space character
                    lw = 4; lh = 16; // Correcting the values to make the expected space between the letters
                }
                angle += anglePerLength * lw / 2;
                if (letter.getRotate() != angle) {
                    letter.setRotate(angle / Math.PI * 180 + (top ? 90 : -90));
                    layoutInArea(letter, r + 0.75 * r * Math.cos(angle) - lw / 2, r + 0.75 * r * Math.sin(angle) - lh / 2, lw, lh);
                }
                angle += anglePerLength * lw / 2;
            }
        }
        if (topScalePane != null || bottomScalePane != null) {
            double h = 0.3 * height;
            if (bottomScalePane == null) {
                double dy = -0.04 * height;
                layoutInArea(topScalePane, 0, height / 2 - h / 2 + dy, width, h);
            } else {
                double dy = 0.06 * height, gap = 0.03 * height;
                if (topScalePane != null)
                    layoutInArea(topScalePane, 0, height / 2 - h + dy - gap / 2, width, h);
                layoutInArea(bottomScalePane, 0, height / 2 + dy + gap / 2, width, h);
            }
        }
    }

    private Bounds getLetterBounds(Text letter) {
        Bounds bounds = (Bounds) letter.getProperties().get("letterBounds");
        if (bounds == null)
            letter.getProperties().put("letterBounds", bounds = letter.getBoundsInLocal());
        return bounds;
    }
}
