package dev.webfx.website.application.cards;

import dev.webfx.website.application.shared.ScalePane;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;

import static dev.webfx.website.application.shared.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
public class DocumentationCard extends Card {

    private final SVGPath documentationLogo = createDocLogo();

    public DocumentationCard() {
        super("Documentation");
    }

    @Override
    Node createIllustrationNode() {
        setShapeHoverAnimationColor(documentationLogo, FIRST_GITHUB_GRADIENT_COLOR);
        ScalePane scalePane = new ScalePane(documentationLogo);
        scalePane.setScaleX(0.75);
        scalePane.setScaleY(0.75);
        return scalePane;
    }

    @Override
    String caption(int step) {
        switch (step) {
            case 1: return "Follow the guide to get started";
            default : return null;
        }
    }

    @Override
    protected String computeLongestCaption() {
        return "WebFX is just starting and doesn't cover all JavaFX features yet, but it has a big potential and shall quickly receive support from the JavaFX & GWT communities.";
    }

    @Override
    protected void layoutChildren(double width, double height) {
        documentationLogo.setTranslateX(0.075 * width);
        super.layoutChildren(width, height);
    }

    @Override
    public void transitionToNextStep() {
        if (currentAnimationStep == 1)
            openUrl("https://docs.webfx.dev");
        else
            super.transitionToNextStep();
    }

}