package dev.webfx.website.application.cards;

import dev.webfx.extras.webtext.controls.HtmlText;
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
import javafx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
final class SustainableCard extends FlipCard {

    private FrameworksPane javaFrameworksPane, webFrameworksPane;
    private WebFxCloudAnimationPane webFxCloudAnimationPane;
    private RefactoringAnimationPane refactoringAnimationPane;
    private LongevityAnimationPane longevityAnimationPane;
    private LayoutPane longevityAnimationEnclosingPane;
    private double wallHeight;

    SustainableCard() {
        super("Sustainable");
    }

    @Override
    String caption(int step) {
        switch (step) {
            case 1: return "WebFX will give your web applications a longer lifespan potential.";
            case 2: return "The lifespan potential of your web application firstly depends on the lifespan of its UI toolkit, which makes JavaFX a very interesting choice.";
            case 3: return "As the Java desktop ecosystem has become hyper stable with no more competitor to come, JavaFX can safely enjoy a long life with many good days ahead.";
            case 4: return "While in the divided and highly competitive Web ecosystem, the lifespan of a web framework is very uncertain.";
            case 5: return "Your WebFX application will therefore inherit from JavaFX a lifespan potential that is likely longer than any web framework.";
            case 6: return "Another requirement for your web application to grow successfully is a good refactoring support.";
            case 7: return "And one big advantage of a strict language like Java is to excel in refactoring.";
            case 8: return "A lasting UI toolkit and a good refactoring support are the two fundamental requirements for your application longevity, and Java & JavaFX fulfil them perfectly!";
            case 9: return "This is what has motivated us to build WebFX: make these sustainable technologies available for the web as an alternative to the short-lived web technologies.";
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
                //setUpCardClip();
                break;
            case 2:
                HBox javaFxLogo = WebSiteShared.createJavaFxLogo();
                SVGPath years12 = WebSiteShared.createLogoSVGPath(SvgLogoPaths.get12YearsPath(), WebSiteShared.createVerticalGithubGradiant(200, 0));
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
                        webFxCloudAnimationPane = new WebFxCloudAnimationPane();
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
                    performFadingTransition(longevityAnimationEnclosingPane, cardTransition, false);
                longevityAnimationPane.startJavaFxAnimation(cardTransition);
                cardTransition.addOnFinished(refactoringAnimationPane::stopBrickAnimation);
                break;
            case 9:
                longevityAnimationPane.startHtmlFrameAnimation(cardTransition);
                break;
        }
    }

    static Text createText(String text) {
        return WebSiteShared.setUpText(new Text(text), 20, false, true, false, false);
    }
}
