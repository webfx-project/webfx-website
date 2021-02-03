package dev.webfx.website.application;

import dev.webfx.extras.webtext.controls.HtmlText;
import dev.webfx.extras.webtext.controls.SvgText;
import dev.webfx.website.application.cards.*;
import javafx.animation.Interpolator;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class WebSiteShared {

    static final LinearGradient backgroundGradient = LinearGradient.valueOf("from 0% 0% to 100% 100%, #4C2459, #6F295A");
    public static final LinearGradient circleGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.gray(0.90)),
            new Stop(1, Color.gray(0.65))
    );
    public static final LinearGradient githubGradient = LinearGradient.valueOf("to left, #FFE580, #FF7571, #EA5DAD, #C2A0FD, #3BF0E4, #B2F4B6");
    private static final List<Stop> githubStops = githubGradient.getStops();

    public static final DropShadow dropShadow  = new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 10, 0, 8, 8);
    public static final Color javaColor        = Color.rgb(244, 175, 103);
    public static final Color fxColor          = Color.rgb(63,  142, 217);
    public static final Color raspberryPiColor = Color.rgb(197,  32, 73);
    public static final Color html5Color       = Color.rgb(226,  57, 38);
    public static final Color windowsColor     = Color.rgb(47,  116, 212);
    public static final Color androidColor     = Color.rgb(116, 140,  38); //Official color Color.rgb(165, 199, 54) is too bright for the circle background;
    public static final Color appleColor       = Color.grayRgb(30);
    public static final Color gwtColor         = Color.rgb(249,  53, 53);

    // Ease out interpolator closer to the web standard than the one proposed in JavaFx (ie Interpolator.EASE_OUT)
    public final static Interpolator EASE_OUT_INTERPOLATOR = Interpolator.SPLINE(0, .75, .25, 1);

    public static final Card[] cards = {
            new WebFxCard(),
            new FullyCrossPlatformCard(),
            new FullySustainableCard(),
            new FullStackJavaCard(),
    };

    public static SvgText createWebFxSvgText(double fontSize) {
        return setUpText(new SvgText("WebFX"), fontSize, true, false, true, true);
    }

    public static <T extends Text> T setUpText(T text, double fontSize, boolean bold, boolean white, boolean stroke, boolean shadow) {
        text.setFont(Font.font("Arial", bold ? FontWeight.BOLD : FontWeight.NORMAL, fontSize));
        text.setFill(white ? Color.WHITE : githubGradient);
        if (stroke) {
            text.setStroke(Color.WHITE);
            text.setStrokeWidth(fontSize >= 70 ? 2 : 1);
        }
        if (shadow)
            text.setEffect(dropShadow);
        return text;
    }

    public static Font htmlTextFont = Font.font("Arial", FontWeight.NORMAL, 22);

    public static HtmlText setHtmlText(HtmlText htmlText, String text) {
        htmlText.setFont(htmlTextFont);
        htmlText.setText(text == null ? null : "<center style='font-style: oblique; line-height: 1.5em; color:white'>" + text + "</center>");
        htmlText.setMouseTransparent(true);
        return htmlText;
    }

    public static LinearGradient createAngleGithubGradient(double angle) {
        return createAngleGithubGradient(angle, 200, 0);
    }

    public static LinearGradient createAngleGithubGradient(double angle, double length, double shift) {
        shift = shift % length;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new LinearGradient(shift * cos, shift * sin, (shift + length) * cos, (shift + length) * sin, false, CycleMethod.REPEAT, githubStops);
    }

    public static LinearGradient createVerticalGithubGradiant(double length, double shift) {
        return WebSiteShared.createAngleGithubGradient(Math.PI / 2, length, -shift);
    }

    public static void setBackground(Region region, Paint fill) {
        setBackground(region, fill, null);
    }

    public static void setBackground(Region region, Paint fill, CornerRadii radii) {
        region.setBackground(fill == null || fill == Color.TRANSPARENT ? null : new Background(new BackgroundFill(fill, radii, null)));
    }

    public static <T extends Region> T setFixedSize(T region, double wh) {
        return setFixedSize(region, wh, wh);
    }

    public static <T extends Region> T setFixedSize(T region, double w, double h) {
        region.setMaxSize(w, h);
        region.setMinSize(w, h);
        region.setPrefSize(w, h);
        return region;
    }
}
