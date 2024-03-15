package dev.webfx.website.application.cards;

import dev.webfx.extras.panes.ScalePane;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;

import static dev.webfx.website.application.shared.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
public class GitHubCard extends Card {

    private final SVGPath githubLogo = createGithubLogo();

    public GitHubCard() {
        super("GitHub");
    }

    @Override
    Node createIllustrationNode() {
        setShapeHoverAnimationColor(githubLogo, LAST_GITHUB_GRADIENT_COLOR);
        ScalePane scalePane = new ScalePane(githubLogo);
        scalePane.setScaleX(0.65);
        scalePane.setScaleY(0.65);
        return scalePane;
    }

    @Override
    String caption(int step) {
        switch (step) {
            case 1: return "Get involved in the project";
            default : return null;
        }
    }

    @Override
    protected void layoutChildren(double width, double height) {
        githubLogo.setTranslateX(-0.05 * width);
        super.layoutChildren(width, height);
    }

    @Override
    public void transitionToNextStep() {
        if (currentAnimationStep == 1)
            openUrl("https://github.com/webfx-project/webfx");
        else
            super.transitionToNextStep();
    }
}
