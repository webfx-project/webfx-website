package dev.webfx.website.application.cards;

import dev.webfx.website.application.images.SvgLogoPaths;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.ScalePane;
import dev.webfx.website.application.shared.WebSiteShared;
import javafx.animation.KeyValue;
import javafx.scene.layout.Pane;
import javafx.scene.paint.LinearGradient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
final class LongevityAnimationPane extends ScalePane {

    private final ScalePane longevityScalePane = new ScalePane(WebSiteShared.createLogoSVGPath(SvgLogoPaths.getLongevityPath(), LinearGradient.valueOf("to right, red, brown, orange, green")));
    private final ScalePane htmlFrameScalePane = new ScalePane(WebSiteShared.createLogoSVGPath(SvgLogoPaths.getHtmlFramePath(), WebSiteShared.html5Color));
    private final Pane fxWreathPane = new FXWreathPane();
    private final LayoutPane fixedSizePane;
    private final List<Pane> refactoringAnimationPanes = new ArrayList<>();

    public LongevityAnimationPane() {
        setNode(fixedSizePane = new LayoutPane(htmlFrameScalePane, longevityScalePane, fxWreathPane) {
            @Override
            protected void layoutChildren(double width, double height) {
                layoutInArea(htmlFrameScalePane, 0, 0, width, height);
                layoutInArea(longevityScalePane, 0, 0, width, height);
                layoutInArea(fxWreathPane, 270, 200, 300, 300);
                for (Pane p : refactoringAnimationPanes)
                    layoutInArea(p, 0, 0, width, height);
            }
        });
        WebSiteShared.setFixedSize(fixedSizePane, 1000);
        htmlFrameScalePane.setScaleX(2);
        htmlFrameScalePane.setScaleY(2);
        fxWreathPane.setRotate(-42);
        scaleYProperty().bind(scaleXProperty());
    }

    private boolean backToOriginal;

    void startBackToOriginalAnimation(CardTransition cardTransition) {
        backToOriginal = true;
        cardTransition.addKeyValue(
                new KeyValue(fxWreathPane.opacityProperty(), 0),
                new KeyValue(htmlFrameScalePane.opacityProperty(), 0),
                new KeyValue(scaleXProperty(), 1),
                new KeyValue(longevityScalePane.translateYProperty(), 0)
        );
        for (Pane p : refactoringAnimationPanes)
            cardTransition.addKeyValue(new KeyValue(p.opacityProperty(), 0));
        cardTransition.addOnFinished(() -> {
            fixedSizePane.getChildren().removeAll(refactoringAnimationPanes);
            refactoringAnimationPanes.clear();
        });
    }

    void startJavaFxAnimation(CardTransition cardTransition) {
        backToOriginal = false;
        cardTransition.addKeyValue(
                new KeyValue(fxWreathPane.opacityProperty(), 1),
                new KeyValue(htmlFrameScalePane.opacityProperty(), 0),
                new KeyValue(scaleXProperty(), 1)
        );
        cardTransition.addOnFinished(this::startAnotherShortRefactoringAnimation);
    }

    private final static int[] WALL_BRICK_ROWS = { 2, 3, 6, 9, 16 };
    private final static double[] WALL_HEIGHT = { 70, 120, 205, 320, 555 };

    private void startAnotherShortRefactoringAnimation() {
        int n = refactoringAnimationPanes.size();
        if (n < 5 && !backToOriginal) {
            RefactoringAnimationPane refactoringAnimationPane = new RefactoringAnimationPane(fixedSizePane);
            refactoringAnimationPane.setTranslateX(Math.max(0, n - 1) * 183 + 100);
            refactoringAnimationPane.setTranslateY(-98);
            refactoringAnimationPanes.add(refactoringAnimationPane);
            fixedSizePane.getChildren().add(/*n,*/ refactoringAnimationPane);
            refactoringAnimationPane.startGoodShortRefactoringBrickAnimation(n == 0 ? 0 : WALL_BRICK_ROWS[n - 1], WALL_BRICK_ROWS[n], WALL_HEIGHT[n], this::startAnotherShortRefactoringAnimation);
        }
    }

    void startHtmlFrameAnimation(CardTransition cardTransition) {
        cardTransition.addKeyValue(
                new KeyValue(scaleXProperty(), 0.5),
                new KeyValue(htmlFrameScalePane.opacityProperty(), 1)
        );
    }
}
