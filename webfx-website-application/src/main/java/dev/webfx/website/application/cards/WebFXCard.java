package dev.webfx.website.application.cards;

/**
 * @author Bruno Salmon
 */
final class WebFXCard extends FlipCard {

    private WebFXCloudAnimationPane webFxCloudAnimationPane;
    private WebFXDevAnimationPane webFxDevAnimationPane;

    WebFXCard() {
        super("WebFX?");
    }

    @Override
    String caption(int step) {
        switch (step) {
            case 1: return "A new way to develop modern web applications... in Java & JavaFX!";
            case 2: return "WebFX is a JavaFX \u2192 JS transpiler powered by GWT. You write your web application in JavaFX, and GWT will transpile it in pure JS (no plugin, no server).";
            case 3: return "WebFX patches the higher layer of OpenJFX to make it GWT compatible, and replaces the lower layer with a JavaFX scene graph \u2192 browser DOM mapper.";
            case 4: return "You don't need to transpile each code change, you can use the OpenJFX runtime to develop, test and debug your application as usual in your Java IDE,";
            case 5: return "and transpile it only from time to time to check your web version is working as expected.";
            case 6: return "WebFX is just starting and doesn't cover all JavaFX features yet, but it has a big potential and shall quickly receive support from the JavaFX & GWT communities.";
            // Removed this step as it may be controversial (advise from Dukke and Ivan)
            //case 7: return "With WebFX, JavaFX shall become the first major desktop toolkit that can be transpiled to the web!";
            case 7: return "WebFX will soon be ready for testing.<br/> We hope you will love it.";
            default : return null;
        }
    }

    @Override
    void prepareCardTransition(int step, CardTransition cardTransition) {
        super.prepareCardTransition(step, cardTransition);
        switch (step) {
            case 1:
                if (webFxCloudAnimationPane == null)
                    webFxCloudAnimationPane = new WebFXCloudAnimationPane();
                flipToNewContent(webFxCloudAnimationPane);
                webFxCloudAnimationPane.playContractionAnimation(cardTransition);
                webFxCloudAnimationPane.flipFx();
                break;
            case 2:
                webFxCloudAnimationPane.playExpansionAnimation(cardTransition, true);
                flipToNewContent(webFxCloudAnimationPane); // In case of reward
                break;
            case 3:
                if (webFxDevAnimationPane == null)
                    webFxDevAnimationPane = new WebFXDevAnimationPane();
                flipToNewContent(webFxDevAnimationPane);
                webFxDevAnimationPane.hideArcs();
                webFxDevAnimationPane.showRightArrow(cardTransition);
                webFxDevAnimationPane.playRotateAppCenter(cardTransition); // In case of forward
                break;
            case 4:
                webFxDevAnimationPane.hideRightArrow(cardTransition);
                webFxDevAnimationPane.playRotateAppLeft(cardTransition);
                break;
            case 5:
                webFxDevAnimationPane.playRotateAppRight(cardTransition);
                webFxDevAnimationPane.hideArcs(); // In case of reward
                break;
            case 6:
                webFxDevAnimationPane.playRotateAppCenter(cardTransition); // In case of forward
                webFxDevAnimationPane.playArcs(cardTransition);
                flipToNewContent(webFxDevAnimationPane); // In case of reward
                break;
/*
            case 7:
                flipToNewContent(webFxCloudAnimationPane);
                webFxCloudAnimationPane.playExpansionAnimation(cardTransition, false);  // In case of reward
                webFxCloudAnimationPane.flipFx(); // In case of reward
                break;
*/
            case 7:
                flipToNewContent(webFxCloudAnimationPane);
                webFxCloudAnimationPane.playContractionAnimation(cardTransition);
                webFxCloudAnimationPane.flipThumbUp();
                break;
        }
    }
}
