package dev.webfx.website.application.cards;

import dev.webfx.extras.webtext.controls.HtmlText;
import dev.webfx.website.application.SvgLogoPaths;
import dev.webfx.website.application.WebSiteShared;
import eu.hansolo.enzo.flippanel.FlipPanel;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
public final class FullySustainabilityCard extends Card {

    private FlipPanel flipPanel;
    private JavaFxToWebAnimationPane javaFxToWebAnimationPane;
    private RefactoringAnimationPane refactoringAnimationPane;
    private LongevityAnimationPane longevityAnimationPane;
    private boolean flipShowingFront;
    private double wallHeight;

    public FullySustainabilityCard() {
        super("Fully sustainable");
    }

    @Override
    Node createIllustrationNode() {
        useHiddenTitleSpaceForIllustration = true;
        longevityAnimationPane = new LongevityAnimationPane();
        flipShowingFront = true;
        return flipPanel = new FlipPanel();
    }

    @Override
    String caption(int step) {
        switch (step) {
            case 1: return "Build modern and sustainable applications.";
            case 2: return "To build sustainable applications, the first requirement you need is a lasting UI toolkit.";
            case 3: return "As Java desktop has gained less attraction but still remains necessary, the Java desktop ecosystem has become immutable with no more competitor to come.";
            case 4: return "In the divided and highly competitive Web ecosystem however, the lifespan of a web toolkit is still very uncertain.";
            case 5: return "Today, JavaFX has this unique position of a major and sustainable desktop toolkit that can be transpiled to the web with an acceptable size and startup time.";
            case 6: return "By building your application on the stable JavaFX ground, you will ensure it a much longer lifespan than if building it with other Web technologies.";
            case 7: return "A second requirement for your application to grow successfully is a good refactoring support.";
            case 8: return "One of the big advantages of a strict language is to excel in refactoring.";
            case 9: return "A lasting UI toolkit and a good refactoring support are the two fundamental requirements for your application longevity, and Java & JavaFX fulfil them perfectly!";
            case 10: return "This is what have motivated us to build WebFX, and use this sustainable technology for the web development.";
            default: return null;
        }
    }

    @Override
    void prepareCardTransition(int step, CardTransition cardTransition) {
        super.prepareCardTransition(step, cardTransition);
        cardTransition.setApplyFinalValuesOnStop(true);
        switch (step) {
            case 1:
                longevityAnimationPane.startBackToOriginalAnimation(cardTransition);
                changeFlipContent(longevityAnimationPane);
                break;
            case 2:
                smoothIllustrationMove = false;
                HBox javaFxLogo = createJavaFxLogo();
                SVGPath years12 = createLogoSVGPath(SvgLogoPaths.get12YearsPath(), WebSiteShared.createVerticalGithubGradiant(0));
                VBox.setMargin(years12, new Insets(50));
                HtmlText htmlText = WebSiteShared.setHtmlText(new HtmlText(), "JavaFX has a great quality: it lasts!<br/>And is still relevant today!");
                VBox vBox = new VBox(10,
                        javaFxLogo,
                        years12,
                        htmlText
                );
                vBox.setAlignment(Pos.CENTER);
                htmlText.setMaxHeight(0); // Hack for the web version, otherwise takes too much height and vBox is not initially centered
                years12.setOpacity(0);
                years12.scaleYProperty().bind(years12.scaleXProperty());
                years12.setScaleX(15);
                cardTransition.setDurationMillis(1500);
                cardTransition.addKeyValue(
                        new KeyValue(years12.opacityProperty(), 1),
                        new KeyValue(years12.scaleXProperty(), 2, Interpolator.SPLINE(1, 0.5, 1, 1))
                );
                setClip(null);
                cardTransition.addOnFinished(this::setUpCardClip);
                changeFlipContent(vBox);
                //performFadingTransition(vBox, cardTransition, true);
                break;
            case 4:
                flipToNewContent(new FrameworksPane(true));
                break;
            case 3:
                setUpCardClip();
                flipToNewContent(new FrameworksPane(false));
                break;
            case 5:
                flipToNewContent(javaFxToWebAnimationPane = new JavaFxToWebAnimationPane());
                break;
            case 6:
                javaFxToWebAnimationPane.playAnimation();
                break;
            case 7:
                htmlText = WebSiteShared.setHtmlText(new HtmlText(), "Refactoring is essential to avoid the pitfall of regular rewrites in your development cycle, a very common point of failure for applications longevity.");
                StackPane.setAlignment(htmlText, Pos.TOP_CENTER);
                StackPane.setMargin(htmlText, new Insets(50, 0, 0, 0));
                htmlText.setMaxHeight(0);
                htmlText.setOpacity(0);
                refactoringAnimationPane = new RefactoringAnimationPane(this);
                performFadingTransition(new StackPane(refactoringAnimationPane, htmlText), cardTransition, true);
                refactoringAnimationPane.startBadRefactoringBrickAnimation(wallHeight = (flipPanel.getHeight() - htmlText.getLayoutY() - htmlText.getHeight()) * 0.7 , () -> {
                    cardTransition.addKeyValue(new KeyValue(htmlText.opacityProperty(), 1));
                    cardTransition.run(true);
                });
                cardTransition.addOnFinished(() -> {
                    javaFxToWebAnimationPane.stopAnimation();
                });
                break;
            case 8:
                htmlText = WebSiteShared.setHtmlText(new HtmlText(), "Java has a prime refactoring support, a big force to clean, correct and make your code grow successfully.");
                StackPane.setAlignment(htmlText, Pos.TOP_CENTER);
                StackPane.setMargin(htmlText, new Insets(50, 0, 0, 0));
                htmlText.setOpacity(0);
                RefactoringAnimationPane previousAnimation = refactoringAnimationPane;
                refactoringAnimationPane = new RefactoringAnimationPane(this);
                performFadingTransition(new StackPane(refactoringAnimationPane, htmlText), cardTransition, false);
                refactoringAnimationPane.startGoodRefactoringBrickAnimation(wallHeight , () -> {
                    cardTransition.addKeyValue(new KeyValue(htmlText.opacityProperty(), 1));
                    cardTransition.run(true);
                });
                cardTransition.addOnFinished(previousAnimation::stopBrickAnimation);
                break;
            case 9:
                performFadingTransition(longevityAnimationPane, cardTransition, false);
                longevityAnimationPane.startJavaFxAnimation(cardTransition);
                cardTransition.addOnFinished(refactoringAnimationPane::stopBrickAnimation);
                break;
            case 10:
                longevityAnimationPane.startHtmlFrameAnimation(cardTransition);
                smoothIllustrationMove = true;
                break;
        }
    }

    private void performFadingTransition(Node enteringNode, CardTransition cardTransition, boolean parallel) {
        ObservableList<Node> containerChildren = (flipShowingFront ? flipPanel.getFront() : flipPanel.getBack()).getChildren();
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

    static Text createText(String text) {
        return WebSiteShared.setUpText(new Text(text), 20, false, true, false, false);
    }

    private void flipToNewContent(Node newContent) {
        flipShowingFront = !flipShowingFront;
        changeFlipContent(newContent);
        if (flipShowingFront)
            flipPanel.flipToFront();
        else
            flipPanel.flipToBack();
    }

    private void changeFlipContent(Node newContent) {
        if (flipShowingFront)
            flipPanel.getFront().getChildren().setAll(newContent);
        else
            flipPanel.getBack().getChildren().setAll(newContent);
    }
}