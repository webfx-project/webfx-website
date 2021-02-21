package dev.webfx.website.application.cards;

import dev.webfx.website.application.shared.WebSiteShared;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.ScalePane;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * @author Bruno Salmon
 */
final class FrameworksPane extends LayoutPane {

    private final static double year1 = 2021.1;
    private final static Framework[] webFrameworks = {             /*1996.8*/
            new Framework("Flash",       WebSiteShared::createFlashLogo,      2006.0, 2010.1, 2020.9),
            new Framework("JQuery",      WebSiteShared::createJQueryLogo,     2006.7, 2013.8),
            new Framework("Silverlight", WebSiteShared::createSilverlightLogo,2007.3, 2011.5, 2017),
            new Framework("JavaFX",      WebSiteShared::createFxLogo,         2008.7, year1),
            new Framework("AngularJS",   WebSiteShared::createAngularLogo,    2009.0, 2016.0),
            new Framework("Backbone",    WebSiteShared::createBackboneLogo,   2010.7, 2013.0),
            new Framework("Ember",       WebSiteShared::createEmberLogo,      2010.9, 2013.6),
            new Framework("Meteor",      WebSiteShared::createMeteorLogo,     2012.1, 2015.6),
            new Framework("React",       WebSiteShared::createReactLogo,      2013.5, year1),
            new Framework("Vue",         WebSiteShared::createVueLogo,        2014.1, 2020.5),
            new Framework("Angular",     WebSiteShared::createAngularLogo,    2016.7, 2018.5),
            new Framework("Flutter",     WebSiteShared::createFlutterLogo,    2018.9, year1),
    };
    private final static Framework[] javaFrameworks = {
            new Framework("AWT",  null,       1995, 1998, 2018.0),
            new Framework("Swing",null,       1997, 2013, 2030),
            new Framework("SWT",  null,       2003.3, 2009, 2030),
            new Framework("Pivot",null,       2008.5, 2017, 2030),
            new Framework("JavaFX", WebSiteShared::createFxLogo, 2008.7, year1),
    };

    private final Framework[] frameworks;
    private final Canvas yearsCanvas;
    private final double year0;

    FrameworksPane(boolean web) {
        super();
        frameworks = web ? webFrameworks : javaFrameworks;
        year0 = Arrays.stream(frameworks).mapToDouble(f -> f.startYear).min().getAsDouble();
        getChildren().add(yearsCanvas = new Canvas());
        for (Framework framework : frameworks)
            getChildren().addAll(framework.liveBar, framework.logo, framework.nameText);
    }

    @Override
    protected void layoutChildren(double width, double height) {
        double w = width, h = height;
        double rowHeight = h / (frameworks.length + 0.5); // Adding half a row to make space for the years legend
        double vgap = 0.03 * h;
        double fy = 0, fh = rowHeight - vgap;
        for (Framework fw : frameworks) {
            double thisBarX = getYearX(fw.startYear, w), thisBarW = w - thisBarX;
            leftInArea(fw.liveBar, thisBarX, fy, thisBarW, fh);
            leftInArea(fw.logo, thisBarX + 10, fy, fh, fh);
            leftInArea(fw.nameText, thisBarX + 20 + fh, fy, fh, fh);
            fy += rowHeight;
        }
        yearsCanvas.setWidth(w);
        yearsCanvas.setHeight(h);
        GraphicsContext ctx = yearsCanvas.getGraphicsContext2D();
        ctx.clearRect(0, 0, w, h);
        ctx.setFill(Color.WHITE);
        ctx.setStroke(Color.GRAY);
        ctx.setLineWidth(0.5);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setFont(Font.font(8));
        ctx.beginPath();
        for (double year = year0; year <= year1; year++) {
            double x = getYearX(year, w);
            ctx.moveTo(x, 0);
            ctx.lineTo(x, h);
            ctx.stroke();
            ctx.fillText("" + (int) year, getYearX(year + 0.5, w), h);
        }
        ctx.closePath();
    }

    private double getYearX(double year, double width) {
        return snapPositionX((year - year0) / (year1 - year0) * width) + 0.5;
    }

    static class Framework {
        final Text nameText;
        final Node logo;
        final Region liveBar = new Region();
        final double startYear;

        Framework(String name, Supplier<Node> logoFactory, double startYear, double apogeeYear) {
            this(name, logoFactory, startYear, apogeeYear, apogeeYear + 10);
        }

        Framework(String name, Supplier<Node> logoFactory, double startYear, double apogeeYear, double deathYear) {
            nameText = WebSiteShared.setUpText(new Text(name), 20, false, true, false, false);
            logo = logoFactory == null ? new Text("") : new ScalePane(logoFactory.get());
            this.startYear = startYear;
            double clumpDeathYear = Math.min(deathYear, year1);
            double d = year1 - startYear;
            Color increasingColor = Color.color(0.1, 0.5, 0.1), decayingColor = Color.ORANGE, dyingColor = Color.color(1, 0.2, 0.2);
            WebSiteShared.setRegionBackground(liveBar, new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                    new Stop(0,                                increasingColor),
                    new Stop((apogeeYear     - startYear) / d, increasingColor),
                    new Stop((apogeeYear     - startYear) / d, decayingColor),
                    new Stop((apogeeYear     - startYear) / d * 1.3, decayingColor),
                    new Stop((apogeeYear     - startYear) / d * 1.3, dyingColor),
                    new Stop(Math.max((clumpDeathYear - startYear) / d, (apogeeYear     - startYear) / d * 1.3), Color.rgb(255, 0, 0, deathYear < year1 ? 0 : 1 - (year1 - apogeeYear) / (deathYear - apogeeYear)))
                    ));
        }
    }
}
