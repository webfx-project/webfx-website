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
public final class FullySustainableCard extends Card {

    private FlipPanel flipPanel;
    private FrameworksPane javaFrameworksPane, webFrameworksPane;
    private WebFxCloudAnimationPane webFxCloudAnimationPane;
    private RefactoringAnimationPane refactoringAnimationPane;
    private LongevityAnimationPane longevityAnimationPane;
    private boolean flipShowingFront;
    private double wallHeight;

    public FullySustainableCard() {
        super("Fully sustainable");
    }

    @Override
    Node createIllustrationNode() {
        bindTitleSpaceWithOpacity();
        javaFrameworksPane = new FrameworksPane(false);
        webFrameworksPane = new FrameworksPane(true);
        webFxCloudAnimationPane = new WebFxCloudAnimationPane();
        longevityAnimationPane = new LongevityAnimationPane();
        flipPanel = new FlipPanel();
        flipPanel.getBack().getChildren().setAll(webFrameworksPane);
        flipShowingFront = true;
        return flipPanel;
    }

    @Override
    String caption(int step) {
        switch (step) {
            case 1: return "WebFX will give your web applications a much longer lifespan potential.";
            case 2: return "The lifespan potential of your web application firstly depends on the lifespan of its UI toolkit, which makes JavaFX a very interesting choice.";
            case 3: return "As the Java desktop ecosystem has become hyper stable with no more competitor to come, JavaFX can safely enjoy a long life with many good days ahead.";
            case 4: return "While in the divided and highly competitive Web ecosystem, the lifespan of a web toolkit is very uncertain.";
            case 0: return "Now with WebFX, JavaFX has gained this unique position of a major desktop toolkit that can be transpiled to the web with an acceptable size and startup time.";
            case 5: return "Therefore your WebFX application will inherit from JavaFX a lifespan potential that is likely much longer than any toolkit from the standard Web ecosystem.";
            case 6: return "Another requirement to make your web application grow successfully is a good refactoring support.";
            case 7: return "And one big advantage of a strict language like Java is to excel in refactoring.";
            case 8: return "A lasting UI toolkit and a good refactoring support are the two fundamental requirements for your application longevity, and Java & JavaFX fulfil them perfectly!";
            case 9: return "This is what have motivated us to build WebFX, and port this sustainable development model to the web.";
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
                setUpCardClip();
                break;
            case 2:
                smoothIllustrationMove = false;
                HBox javaFxLogo = createJavaFxLogo();
                SVGPath years12 = createLogoSVGPath(SvgLogoPaths.get12YearsPath(), WebSiteShared.createVerticalGithubGradiant(200, 0));
                VBox.setMargin(years12, new Insets(50));
                HtmlText htmlText = WebSiteShared.setHtmlText(new HtmlText(), "JavaFX has a great quality: it lasts!<br/>And is still relevant today!");
                VBox vBox = new VBox(10,
                        javaFxLogo,
                        years12,
                        htmlText
                );
                vBox.setAlignment(Pos.CENTER);
                htmlText.setMaxHeight(0); // Hack for the web version, otherwise takes too much height and vBox is not initially centered
                if (forwardingStep) {
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
                }
                if (forwardingStep)
                    changeFlipContent(vBox);
                else
                    flipToNewContent(vBox);
                break;
            case 3:
                setUpCardClip();
                flipToNewContent(javaFrameworksPane);
                break;
            case 4:
                flipToNewContent(webFrameworksPane);
                break;
            case 0:
                webFxCloudAnimationPane.stopAnimation(); // In case of a backward navigation
                webFxCloudAnimationPane.setOpacity(1);
                flipToNewContent(webFxCloudAnimationPane);
                break;
            case 5:
                if (forwardingStep) {
                    webFxCloudAnimationPane.setOpacity(1);
                    flipToNewContent(webFxCloudAnimationPane);
                } else {
                    performFadingTransition(webFxCloudAnimationPane, cardTransition, true);
                    cardTransition.addOnFinished(refactoringAnimationPane::stopBrickAnimation);
                }
                webFxCloudAnimationPane.playBubblesAnimation();
                break;
            case 6:
                htmlText = WebSiteShared.setHtmlText(new HtmlText(), "Refactoring is essential to avoid the pitfall of regular rewrites in your development cycle, a common point of failure in applications longevity.");
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
                cardTransition.addOnFinished(webFxCloudAnimationPane::stopAnimation);
                break;
            case 7:
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
            case 8:
                if (forwardingStep)
                    performFadingTransition(longevityAnimationPane, cardTransition, false);
                longevityAnimationPane.startJavaFxAnimation(cardTransition);
                cardTransition.addOnFinished(refactoringAnimationPane::stopBrickAnimation);
                break;
            case 9:
                longevityAnimationPane.startHtmlFrameAnimation(cardTransition);
                smoothIllustrationMove = true;
                break;
        }
    }

    private void performFadingTransition(Node enteringNode, CardTransition cardTransition, boolean parallel) {
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

    static Text createText(String text) {
        return WebSiteShared.setUpText(new Text(text), 20, false, true, false, false);
    }

    ObservableList<Node> getFlipChildren(boolean front) {
        return (front ? flipPanel.getFront() : flipPanel.getBack()).getChildren();
    }

    private void flipToNewContent(Node newContent) {
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

    private void changeFlipContent(Node newContent) {
        getFlipChildren(flipShowingFront).setAll(newContent);
    }
}
