package dev.webfx.website.application.cards;

import dev.webfx.website.application.images.SvgLogoPaths;
import dev.webfx.website.application.shared.FlipPane;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.extras.panes.ScalePane;
import javafx.animation.KeyValue;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import static dev.webfx.website.application.shared.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
public final class JavaFullStackCard extends Card {

    private LayoutPane pane;
    private Node[] stepToolkitLogos, stepLanguageLogos;
    private FlipPane[] toolkitFlipPanes, languageFlipPanes;
    private Node[] puzzles;
    private DoubleProperty expansionProperty;

    public JavaFullStackCard() {
        super("Java full-stack");
    }

    @Override
    Node createIllustrationNode() {
        stepToolkitLogos = new Node[] {
                // Step 1 - Front-office, back-office
                createNoLogo(), createNoLogo(),
                // Step 2 - Front-office, back-office
                createAngularLogo(), createFxLogo(),
                // Step 3 - Front-office, back-office
                createVueLogo(), createVueLogo(),
                // Step 4 - Front-office, back-office
                createReactLogo(), createReactLogo(),
                // Step 5 - Front-office, back-office
                createWebFxLogo(), createWebFxLogo(),
                // Step 6 - Front-office, back-office
                createWebFxLogo(), createWebFxLogo(),
                // Step 7 - Front-office, back-office
                createNoLogo(), createNoLogo()
        };
        stepLanguageLogos = new Node[] {
                // Step 1 - Front-office, back-office, server
                createNoLogo(), createNoLogo(), createNoLogo(),
                // Step 2 - Front-office, back-office, server
                createJSLogo(), createJavaLogo(), createJavaLogo(),
                // Step 3 - Front-office, back-office, server
                createJSLogo(), createJSLogo(), createJavaLogo(),
                // Step 4 - Front-office, back-office, server
                createJSLogo(), createJSLogo(), createJSLogo(),
                // Step 5 - Front-office, back-office, server
                createJavaLogo(), createJavaLogo(), createJavaLogo(),
                // Step 6 - Front-office, back-office, server
                createJavaLogo(), createJavaLogo(), createJavaLogo(),
                // Step 7 - Front-office, back-office, server
                createNoLogo(), createNoLogo(), createNoLogo()
        };
        toolkitFlipPanes = new FlipPane[] {
                new FlipPane(), // For front-office toolkit
                new FlipPane()  // For back-office toolkit
        };
        languageFlipPanes = new FlipPane[] {
                new FlipPane(), // For front-office language
                new FlipPane(), // For back-office language
                new FlipPane()  // For server language
        };
        CirclePane frontOfficeCirclePane = new CirclePane("Front-office", -150, Color.rgb(98, 0, 173), toolkitFlipPanes[0], languageFlipPanes[0]);
        CirclePane backOfficeCirclePane  = new CirclePane("Back-office",   -30, raspberryPiColor, toolkitFlipPanes[1], languageFlipPanes[1]);
        CirclePane serverCirclePane   = new CirclePane("Server",      90, fxColor, languageFlipPanes[2], null);
        // Note: HTML & JavaFX implementation differs, so the rendering is different
        frontOfficeCirclePane.setBlendMode(BlendMode.SCREEN);
        backOfficeCirclePane .setBlendMode(BlendMode.SCREEN);
        serverCirclePane     .setBlendMode(BlendMode.SCREEN);
        pane = new LayoutPane(frontOfficeCirclePane, backOfficeCirclePane, serverCirclePane) {
            @Override
            protected void layoutChildren(double width, double height) {
                double w = width, h = height, s = Math.min(w, h), wd2 = w / 2, hd2 = h / 2, sd2 = s / 2, r = sd2 * 0.49, d = 2 * r;
                double R = (s - d) / (2 * Math.cos(Math.PI/6)) * (0.5 + 0.5 * expansionProperty.get());
                double yc = 0.8 * hd2, yClients = yc - R * Math.sin(Math.PI/6) - r;
                double Rcos = R * Math.cos(Math.PI/6);
                frontOfficeCirclePane.setRadius(r);
                frontOfficeCirclePane.setRotate((frontOfficeCirclePane.getAngle() + 90) * (1 - expansionProperty.get()));
                layoutInArea(frontOfficeCirclePane, wd2 - Rcos - r, yClients,   d, d);
                backOfficeCirclePane.setRadius(r);
                backOfficeCirclePane.setRotate((backOfficeCirclePane.getAngle() + 90) * (1 - expansionProperty.get()));
                layoutInArea(backOfficeCirclePane,  wd2 + Rcos - r, yClients,   d, d);
                serverCirclePane.setRadius(r);
                layoutInArea(serverCirclePane,   wd2 - r, yc + R - r,  d, d);
                if (puzzles != null) {
                    double pd = 0.3 * r, pr = pd / 2;
                    layoutInArea(puzzles[0], wd2 - pr, yc - r * 0.0 - pr, pd, pd);
                    layoutInArea(puzzles[1], wd2 - pr, yc - r * 0.7 - pr, pd, pd);
                    layoutInArea(puzzles[2], wd2 - 0.6 * r - pr, yc + r * 0.3 - pr, pd, pd);
                    layoutInArea(puzzles[3], wd2 + 0.6 * r - pr, yc + r * 0.3 - pr, pd, pd);
                }
            }
        };
        expansionProperty = new SimpleDoubleProperty() {
            @Override
            protected void invalidated() {
                pane.forceLayoutChildren();
            }
        };
        preloadImages(stepToolkitLogos);
        preloadImages(stepLanguageLogos);
        return pane;
    }

    private static Node createPuzzle(Paint fill) {
        ScalePane puzzle = new ScalePane(createLogoSVGPath(SvgLogoPaths.getPuzzlePath(), fill));
        puzzle.setOpacity(0);
        return puzzle;
    }

    private void preloadImages(Node... nodes) {
        // We insert the nodes containing images in the scene graph (=> DOM) to trigger image loading
        // First making them invisible to the user
        for (Node node : nodes)
            node.setVisible(false);
        // Then inserting them (as children of this card)
        getChildren().addAll(nodes);
    }

    @Override
    String caption(int step) {
        switch (step) {
            case 1: return "WebFX makes 100% Java full-stack development possible, with JavaFX as the client-side technology.";
            case 2: return "Web development is transitioning to client-side technologies, and this often introduces heterogeneity in Java-based environments.";
            case 3: return "Standardizing your client stack with a JS front & back-office may be an option for sharing common client code, but your stack is still heterogeneous.";
            case 4: return "Switching to a pure JS stack may be a solution, but you would prefer to stay with Java, right?";
            case 5: return "With WebFX you can use JavaFX for your client-side web technology, and stick with Java throughout your stack.";
            case 6: return "The same language for all your stack. The same UI toolkit for your front & back-office. The same IDE for all your code. The ideal solution for Java-based environments.";
            case 7: return "Simply share common code between your front-office, back-office and server using the Java module system.";
            default: return null;
        }
    }

    @Override
    void prepareCardTransition(int step, CardTransition cardTransition) {
        super.prepareCardTransition(step, cardTransition);
        if (step > 1 || puzzles != null) {
            updateFlipPanes(toolkitFlipPanes,  stepToolkitLogos,  step, cardTransition);
            updateFlipPanes(languageFlipPanes, stepLanguageLogos, step, cardTransition);
            cardTransition.addKeyValue(new KeyValue(expansionProperty, step == 1 || step >= 6 ? 0 : 1));
            if (puzzles == null)
                pane.getChildren().addAll(puzzles = new Node[] {
                        createPuzzle(Color.RED),
                        createPuzzle(Color.YELLOW),
                        createPuzzle(Color.BLUE),
                        createPuzzle(Color.GREEN)
                });
            if (puzzles != null)
                for (Node puzzle : puzzles)
                    cardTransition.addKeyValue(new KeyValue(puzzle.opacityProperty(), step == 7 ? 1 : 0));
        }
    }

    private void updateFlipPanes(FlipPane[] flipPanes, Node[] logos, int step, CardTransition cardTransition) {
        int n = flipPanes.length;
        for (int i = 0; i < n; i++) {
            FlipPane flipPanel = flipPanes[i];
            cardTransition.addKeyValue(new KeyValue(flipPanel.opacityProperty(), step >= 2 && step <= 5 ? 1 : 0));
            Node stepLogo = logos[(step - 1) * n + i];
            getChildren().remove(stepLogo); // Removing them from the card if inserted by preloadImages()
            stepLogo.setVisible(true);
            Node previousStepLogo = logos[((step - 1 + (forwardingStep ? -1 : 1) + 7) % 7) * n + i];
            boolean logoChanged = !getLogoId(stepLogo).equals(getLogoId(previousStepLogo));
            boolean mustBeFront = step == 1 || step == 6;
            if (logoChanged || mustBeFront) {
                boolean backVisible = flipPanel.isShowingBack();
                if (backVisible || mustBeFront)
                    flipPanel.setFront(stepLogo);
                else
                    flipPanel.setBack(stepLogo);
                flipPanel.setFlipDuration(Duration.millis(mustBeFront ? 0 : 700));
                if (mustBeFront)
                    flipPanel.flipToFront();
                else if (step == 2 && forwardingStep)
                    cardTransition.addOnFinished(() -> flip(flipPanel));
                else if (step >= 2 && step <= 5)
                    flip(flipPanel);
            }
        }
    }

    private void flip(FlipPane flipPane) {
        flipPane.getParent().requestLayout(); // Hack for the web version, otherwise the flipPane may be incorrectly sized after a windows resize
        flipPane.flip();
    }
}
