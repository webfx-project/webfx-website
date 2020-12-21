package dev.webfx.website.application;

import dev.webfx.extras.webtext.controls.SvgText;
import dev.webfx.website.application.cards.Card;
import dev.webfx.website.application.cards.FullyCrossPlatformCard;
import dev.webfx.website.application.cards.FullyJavaCard;
import dev.webfx.website.application.cards.FullySustainabilityCard;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class WebSiteShared {

    static final LinearGradient backgroundGradient = LinearGradient.valueOf("from 0% 0% to 100% 100%, #c33764, #1d2671");
    public static final LinearGradient circleGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.gray(0.80, 0.7)),
            new Stop(1, Color.gray(0.43, 0.7))
    );
    static final LinearGradient githubGradient = LinearGradient.valueOf("to left, #FFE580, #FF7571, #EA5DAD, #C2A0FD, #3BF0E4, #B2F4B6");
    private static final List<Stop> githubStops = githubGradient.getStops();

    public static final DropShadow dropShadow  = new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 10, 0, 8, 8);
    public static final Color javaColor        = Color.rgb(244, 175, 103);
    public static final Color fxColor          = Color.rgb(63,  142, 217);
    public static final Color raspberryPiColor = Color.rgb(197,  32, 73);
    public static final Color html5Color       = Color.rgb(226,  57, 38);
    public static final Color windowsColor     = Color.rgb(47,  116, 212);
    public static final Color androidColor     = Color.rgb(165, 199, 54);
    public static final Color appleColor       = Color.grayRgb(30);

    // Ease out interpolator closer to the web standard than the one proposed in JavaFx (ie Interpolator.EASE_OUT)
    //public final static Interpolator EASE_OUT_INTERPOLATOR = Interpolator.SPLINE(0, .75, .25, 1);

    public static SvgText createWebFxSvgText(double fontSize) {
        return setUpText(new SvgText("WebFX"), fontSize, true, true, true);
    }

    public static SvgText createTranspilerSvgText(double fontSize) {
        SvgText text = setUpText(new SvgText("The JavaFX \u2192 Web transpiler"), fontSize, false, false, false);
        text.setStrokeWidth(2);
        text.setStrokeLineCap(StrokeLineCap.BUTT);
        text.getStrokeDashArray().setAll(5d, 20d);
        text.setFill(githubGradient);
        return text;
    }

    public static <T extends Text> T setUpText(T text, double fontSize, boolean bold, boolean stroke, boolean shadow) {
        text.setFont(Font.font("Arial", bold ? FontWeight.BOLD : FontWeight.NORMAL, fontSize));
        if (stroke) {
            text.setStroke(Color.WHITE);
            text.setStrokeWidth(fontSize >= 70 ? 2 : 1);
        }
        if (shadow)
            text.setEffect(dropShadow);
        return text;
    }

    public static final Card[] cards = {
            new FullyJavaCard(),
            new FullyCrossPlatformCard(),
            new FullySustainabilityCard(),
    };

    public static LinearGradient createGithubGradient(double angle) {
        double length = 200;
        return new LinearGradient(0, 0, length * Math.cos(angle), length * Math.sin(angle), false, CycleMethod.REPEAT, githubStops);
    }
}
