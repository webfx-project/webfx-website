package dev.webfx.website.application.cards;

import dev.webfx.extras.flippane.FlipPane;
import javafx.animation.KeyValue;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * @author Bruno Salmon
 */
abstract class FlipCard extends Card {

    protected FlipPane flipPane;
    private boolean flipShowingFront;

    FlipCard(String title) {
        super(title);
    }

    @Override
    Node createIllustrationNode() {
        flipShowingFront = true;
        return flipPane = new FlipPane();
    }

    private ObjectProperty<Node> getFlipNodeProperty(boolean front) {
        return front ? flipPane.frontProperty() : flipPane.backProperty();
    }

    void performFadingTransition(Node enteringNode, CardTransition cardTransition, boolean parallel) {
        ObjectProperty<Node> flipNodeProperty = getFlipNodeProperty(flipShowingFront);
        Node leavingNode = flipNodeProperty.get();
        enteringNode.setOpacity(0);
        cardTransition.addKeyValue(
                new KeyValue(leavingNode .opacityProperty(), 0)
        );
        if (parallel) {
            flipNodeProperty.set(new StackPane(leavingNode, enteringNode));
            cardTransition.addKeyValue(
                    new KeyValue(enteringNode.opacityProperty(), 1)
            );
        }
        cardTransition.addOnFinished(() -> {
            flipNodeProperty.set(enteringNode);
            if (!parallel) {
                cardTransition.addKeyValue(
                        new KeyValue(enteringNode.opacityProperty(), 1)
                );
                cardTransition.run(true);
            }
        });
    }

    void flipToNewContent(Node newContent) {
        ObjectProperty<Node> flipNodeProperty = getFlipNodeProperty(flipShowingFront);
        boolean changed = flipNodeProperty.get() != newContent;
        if (changed) {
            flipShowingFront = !flipShowingFront;
            changeFlipContent(newContent);
            flipPane.setFlipDuration(Duration.millis(flipNodeProperty.get() == null ? 0 : 700));
            if (flipShowingFront)
                flipPane.flipToFront();
            else
                flipPane.flipToBack();
        }
    }

    void changeFlipContent(Node newContent) {
        getFlipNodeProperty(flipShowingFront).set(newContent);
    }
}
