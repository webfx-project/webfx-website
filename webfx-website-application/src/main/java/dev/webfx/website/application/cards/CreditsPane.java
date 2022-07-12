package dev.webfx.website.application.cards;

import dev.webfx.platform.uischeduler.UiScheduler;
import dev.webfx.website.application.images.ImageLoader;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.WebSiteShared;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Arrays;

import static dev.webfx.website.application.shared.WebSiteShared.setUpText;

/**
 * @author Bruno Salmon
 */
final class CreditsPane extends LayoutPane {

    private final static String[] credits = {
            "Free",
            "Open source",
            "Well documented",
            "Long Term Support",
            "Cross-platform & native",
            "6 months release cadence",
            "A strong & active community",
            "Including many Java champions",
            "Almas Baimagambetov",
            "Alessio Vinerbi",
            "Alexander Casall",
            "Andres Almiray",
            "Andreas Billmann",
            "Arnaud Nouard",
            "Bertrand Goetzmann",
            "Bernard Traversat",
            "Bruno Borges",
            "Carl Dea",
            "Christian Campo",
            "Danno Ferrin ",
            "David Grieve",
            "Dean Iverson",
            "Delorme Loïc",
            "Dirk Lemmermann ",
            "Felix",
            "Gerrit Grunwald",
            "Hendrik Ebbers",
            "James Weaver",
            "Jasper Potts",
            "Jeanette Winzenburg",
            "Jens Deters",
            "Johan Vos",
            "Jonathan Giles",
            "José Pereda",
            "Mark Heckler",
            "Michael Heinrichs",
            "Michael Hoffer",
            "Mohamed Taman",
            "Pedro Duque Vieira",
            "Peter Pilgrim",
            "Peter Rogge",
            "Sean Phillips",
            "Stephen Chin",
            "Thierry Wasylczenko",
            "Tobias Bley",
            "Tom Schindl",
            "Weiqi Gao",
            "William Antônio",
            "& many others..."
    };

    private final Card card;
    private final int cardStep;
    private final HBox javaFxLogo = WebSiteShared.createJavaFxLogo();
    private final ImageView ltsImageView = ImageLoader.loadImage("LTS.png");
    private final VBox creditsBox = new VBox(10, Arrays.stream(credits).map(CreditsPane::createText).toArray(Node[]::new));
    private final DoubleProperty creditsBottomDistanceProperty = new SimpleDoubleProperty() {
        @Override
        protected void invalidated() {
            if (card.currentAnimationStep == cardStep)
                forceLayoutChildren();
        }
    };

    private static Text createText(String text) {
        return setUpText(new Text(text), 20, false, true, false, false);
    }


    public CreditsPane(Card card) {
        this.card = card;
        cardStep = card.currentAnimationStep;
        ltsImageView.setEffect(WebSiteShared.dropShadow);
        creditsBox.getChildren().add(ltsImageView);
        creditsBox.setAlignment(Pos.TOP_CENTER);
        getChildren().setAll(creditsBox, javaFxLogo);
        UiScheduler.scheduleInAnimationFrame(() -> {
            double h = creditsBox.prefHeight(getWidth());
            new Timeline(new KeyFrame(Duration.minutes(0.8), new KeyValue(creditsBottomDistanceProperty, h + 50))).play();
        }, 10);
    }

    private double lastW, lastH1, lastH2, lastH3;

    @Override
    protected void layoutChildren(double width, double height) {
        double w = width, h = height, h1 = lastH1, h2 = lastH2, h3 = lastH3;
        if (w != lastW) {
            h1 = javaFxLogo.prefHeight(w);
            h2 = creditsBox.prefHeight(w);
            h3 = ltsImageView.prefHeight(w);
            if (h1 != lastH1 || h2 != lastH2 || h3 != lastH3) {
                lastH1 = h1;
                lastH2 = h2;
                lastH3 = h3;
            } else
                lastW = w;
            layoutInArea(javaFxLogo, 0, 0, w, h1);
        }
        double y = Math.max(h1 - h2 + h3 + 9, h - creditsBottomDistanceProperty.get());
        layoutInArea(creditsBox, 0, y, w, h2);
        creditsBox.setClip(new Rectangle(0, h1 - y, w , h - h1));
    }
}
