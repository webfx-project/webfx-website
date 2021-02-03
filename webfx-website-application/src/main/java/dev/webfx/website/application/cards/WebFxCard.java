package dev.webfx.website.application.cards;

import eu.hansolo.enzo.flippanel.FlipPanel;
import javafx.animation.KeyValue;
import javafx.scene.Node;

/**
 * @author Bruno Salmon
 */
public final class WebFxCard extends Card {

    private FlipPanel flipPanel;
    private WebFxCloudAnimationPane webFxCloudAnimationPane;
    private WebFxDevAnimationPane webFxDevAnimationPane;

    public WebFxCard() {
        super("WebFX?");
    }

    @Override
    Node createIllustrationNode() {
        flipPanel = new FlipPanel();
        flipPanel.getFront().getChildren().setAll(webFxCloudAnimationPane = new WebFxCloudAnimationPane());
        flipPanel.getBack() .getChildren().setAll(webFxDevAnimationPane = new WebFxDevAnimationPane());
        return flipPanel;
    }

    @Override
    String caption(int step) {
        switch (step) {
            case 1: return "A new alternative to develop modern web applications in Java & JavaFX!";
            case 2: return "WebFX is a JavaFX \u2192 JS transpiler powered by GWT. You write your application in JavaFX and GWT will transpile it in pure JS (no plugin, no server).";
            case 3: return "WebFX patches the higher layer of OpenJFX to make it GWT compatible, and replaces the lower layer with a JavaFX scene graph \u2192 browser DOM mapper.";
            case 4: return "You don't need to transpile each code change, you can use the OpenJFX runtime to develop, test and debug your application as usual in your Java IDE,";
            case 5: return "and transpile it only from time to time to check your web version is working as expected.";
            case 6: return "WebFX is a just starting and doesn't cover all JavaFX features yet, but it has a big potential and may quickly receive support from the JavaFX & GWT communities.";
            case 7: return "WebFX will soon be ready for beta testing. We hope you will love it.";
            default : return null;
        }
    }

    @Override
    void prepareCardTransition(int step, CardTransition cardTransition) {
        super.prepareCardTransition(step, cardTransition);
        switch (step) {
            case 1:
                cardTransition.addKeyValue(new KeyValue(titleSpacePercent, 1));
                webFxCloudAnimationPane.playContractionAnimation(cardTransition);
                webFxCloudAnimationPane.flipFx();
                break;
            case 2:
                cardTransition.addKeyValue(new KeyValue(titleSpacePercent, 0));
                webFxCloudAnimationPane.playExpansionAnimation(cardTransition);
                flipPanel.flipToFront(); // In case of reward
                break;
            case 3:
                flipPanel.flipToBack();
                webFxDevAnimationPane.hideArcs();
                webFxDevAnimationPane.showRightArrow(cardTransition);
                webFxDevAnimationPane.playMoveAppCenter(cardTransition); // In case of forward
                break;
            case 4:
                webFxDevAnimationPane.hideRightArrow(cardTransition);
                webFxDevAnimationPane.playMoveAppLeft(cardTransition);
                // Preparing the front for step 7
                webFxCloudAnimationPane.playContractionAnimation(cardTransition);
                webFxCloudAnimationPane.flipFx(); // In case of reward
                break;
            case 5:
                webFxDevAnimationPane.playMoveAppRight(cardTransition);
                webFxDevAnimationPane.hideArcs(); // In case of reward
                break;
            case 6:
                webFxDevAnimationPane.playMoveAppCenter(cardTransition); // In case of forward
                webFxDevAnimationPane.playArcs(cardTransition);
                flipPanel.flipToBack(); // In case of reward
                cardTransition.addKeyValue(new KeyValue(titleSpacePercent, 0)); // In case of reward
                break;
            case 7:
                cardTransition.addKeyValue(new KeyValue(titleSpacePercent, 1));
                flipPanel.flipToFront();
                webFxCloudAnimationPane.flipThumbUp();
                break;
        }
    }
}
