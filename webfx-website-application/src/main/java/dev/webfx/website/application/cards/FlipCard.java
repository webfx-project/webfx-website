package dev.webfx.website.application.cards;

import eu.hansolo.enzo.flippanel.FlipPanel;
import javafx.animation.KeyValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
abstract class FlipCard extends Card {

    protected FlipPanel flipPanel;
    private boolean flipShowingFront;

    FlipCard(String title) {
        super(title);
    }

    @Override
    Node createIllustrationNode() {
        flipShowingFront = true;
        return flipPanel = new FlipPanel();
    }

    void performFadingTransition(Node enteringNode, CardTransition cardTransition, boolean parallel) {
        ObservableList<Node> containerChildren = getFlipChildren(flipShowingFront);
        Node leavingNode = containerChildren.get(0);
        enteringNode.setOpacity(0);
        cardTransition.addKeyValue(
                new KeyValue(leavingNode .opacityProperty(), 0)
        );
        if (parallel)
            cardTransition.addKeyValue(
                    new KeyValue(enteringNode.opacityProperty(), 1)
            );
        cardTransition.addOnFinished(() -> {
            containerChildren.remove(leavingNode);
            if (!parallel) {
                cardTransition.addKeyValue(
                        new KeyValue(enteringNode.opacityProperty(), 1)
                );
                cardTransition.run(true);
            }
        });
        containerChildren.add(enteringNode);
    }

    ObservableList<Node> getFlipChildren(boolean front) {
        return (front ? flipPanel.getFront() : flipPanel.getBack()).getChildren();
    }

    void flipToNewContent(Node newContent) {
        ObservableList<Node> children = getFlipChildren(flipShowingFront);
        boolean changed = children.size() != 1 || children.get(0) != newContent;
        if (changed) {
            flipShowingFront = !flipShowingFront;
            changeFlipContent(newContent);
            if (flipShowingFront)
                flipPanel.flipToFront();
            else
                flipPanel.flipToBack();
        }
    }

    void changeFlipContent(Node newContent) {
        getFlipChildren(flipShowingFront).setAll(newContent);
    }
}
