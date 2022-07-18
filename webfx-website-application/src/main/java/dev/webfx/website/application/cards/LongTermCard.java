package dev.webfx.website.application.cards;

import dev.webfx.extras.webtext.HtmlText;
import dev.webfx.website.application.images.SvgLogoPaths;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.WebSiteShared;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import static dev.webfx.website.application.shared.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
public final class LongTermCard extends FlipCard {

    private FrameworksPane javaFrameworksPane, webFrameworksPane;
    private WebFXCloudAnimationPane webFxCloudAnimationPane;
    private RefactoringAnimationPane refactoringAnimationPane;
    private LongevityAnimationPane longevityAnimationPane;
    private LayoutPane longevityAnimationEnclosingPane;
    private double wallHeight;

    public LongTermCard() {
        super("Long-term");
    }

    @Override
    String caption(int step) {
        switch (step) {
            case 1: return "WebFX provides your applications with longevity.";
            case 2: return "The lifespan of your web application is affected by the lifespan of its UI toolkit.";
            case 3: return "As the Java desktop ecosystem is hyper stable, we can safely assume that JavaFX is here to stay.";
            case 4: return "In the divided and highly competitive Web ecosystem, the lifespan of a web framework is very uncertain.";
            case 5: return "WebFX injects a measure of certainty by inheriting the long lifespan characteristics of JavaFX.";
            case 6: return "Another requirement for your web application to flourish is a good refactoring support.";
            case 7: return "And Java, being a strictly-typed language, excels in refactoring.";
            case 8: return "A lasting UI toolkit and good refactoring support are important requirements for application longevity, and are comprehensively fulfilled by Java & JavaFX.";
            case 9: return "This is also what has motivated us to build WebFX: to make these sustainable, long-term technologies available for the Web.";
            default: return null;
        }
    }

    @Override
    void prepareCardTransition(int step, CardTransition cardTransition) {
        super.prepareCardTransition(step, cardTransition);
        cardTransition.setApplyFinalValuesOnStop(true);
        switch (step) {
            case 1:
                if (longevityAnimationPane == null) {
                    longevityAnimationPane = new LongevityAnimationPane();
                    longevityAnimationEnclosingPane = new LayoutPane(longevityAnimationPane) {
                        @Override
                        protected void layoutChildren(double width, double height) {
                            layoutInArea(longevityAnimationPane, 0, 0, width, height - getTransitionalTitleSpace());
                        }
                    };
                    titleText.opacityProperty().addListener(e -> longevityAnimationEnclosingPane.forceLayoutChildren());
                }
                longevityAnimationPane.startBackToOriginalAnimation(cardTransition);
                changeFlipContent(longevityAnimationEnclosingPane);
                break;
            case 2:
                HBox javaFxLogo = createJavaFxLogo();
                SVGPath years12 = createLogoSVGPath(SvgLogoPaths.get12YearsPath(), WebSiteShared.createVerticalGithubGradiant(200, 0));
                VBox.setMargin(years12, new Insets(50));
                HtmlText htmlText = createHtmlText("JavaFX is a long-term, high-quality client platform that is still relevant today!");
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
                }
                if (forwardingStep)
                    changeFlipContent(vBox);
                else
                    flipToNewContent(vBox);
                break;
            case 3:
                //setUpCardClip();
                if (javaFrameworksPane == null)
                    javaFrameworksPane = new FrameworksPane(false);
                flipToNewContent(javaFrameworksPane);
                break;
            case 4:
                if (webFrameworksPane == null)
                    webFrameworksPane = new FrameworksPane(true);
                flipToNewContent(webFrameworksPane);
                break;
            case 5:
                if (forwardingStep) {
                    if (webFxCloudAnimationPane == null)
                        webFxCloudAnimationPane = new WebFXCloudAnimationPane();
                    webFxCloudAnimationPane.setOpacity(1);
                    flipToNewContent(webFxCloudAnimationPane);
                } else {
                    if (longevityAnimationPane == null)
                        longevityAnimationPane = new LongevityAnimationPane();
                    performFadingTransition(webFxCloudAnimationPane, cardTransition, true);
                    cardTransition.addOnFinished(refactoringAnimationPane::stopBrickAnimation);
                }
                webFxCloudAnimationPane.playBubblesAnimation();
                break;
            case 6:
                htmlText = createHtmlText("Refactoring is essential to avoid the pitfall of regular rewrites in your development cycle, a common point of failure in applications longevity.");
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
                htmlText = createHtmlText("Java has a prime refactoring support, a big force to clean, correct and make your code grow successfully.");
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
                    performFadingTransition(longevityAnimationEnclosingPane, cardTransition, false);
                longevityAnimationPane.startJavaFxAnimation(cardTransition);
                cardTransition.addOnFinished(refactoringAnimationPane::stopBrickAnimation);
                break;
            case 9:
                longevityAnimationPane.startHtmlFrameAnimation(cardTransition);
                break;
        }
    }

    private HtmlText createHtmlText(String text) {
        HtmlText htmlText = setHtmlText(new HtmlText(), text);
        updateTextFontSize(htmlText, getWidth() * 0.04, false);
        return htmlText;
    }
}
