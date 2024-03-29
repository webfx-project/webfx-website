package dev.webfx.website.application.cards;

import dev.webfx.extras.panes.ScalePane;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;

import static dev.webfx.website.application.shared.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
public class BlogCard extends Card {

    private final SVGPath blogLogo = createBlogLogo();

    public BlogCard() {
        super("Blog");
    }

    @Override
    Node createIllustrationNode() {
        setShapeHoverAnimationColor(blogLogo, MIDDLE_GITHUB_GRADIENT_COLOR);
        ScalePane scalePane = new ScalePane(blogLogo);
        scalePane.setScaleX(0.75);
        scalePane.setScaleY(0.75);
        return scalePane;
    }

    @Override
    String caption(int step) {
        switch (step) {
            case 1: return "Get the latest news";
            default : return null;
        }
    }

    @Override
    public void transitionToNextStep() {
        if (currentAnimationStep == 1)
            openUrl("https://blog.webfx.dev");
        else
            super.transitionToNextStep();
    }

}
