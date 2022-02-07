package dev.webfx.website.application.cards;

import dev.webfx.website.application.images.SvgLogoPaths;
import dev.webfx.website.application.shared.LayoutPane;
import dev.webfx.website.application.shared.ScalePane;
import eu.hansolo.enzo.flippanel.FlipPanel;
import javafx.animation.KeyValue;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import static dev.webfx.website.application.shared.WebSiteShared.*;

/**
 * @author Bruno Salmon
 */
final class JavaFullStackCard extends Card {

    private LayoutPane pane;
    private Node[] stepToolkitLogos, stepLanguageLogos;
    private FlipPanel[] toolkitFlipPanels, languageFlipPanels;
    private Node[] puzzles;
    private DoubleProperty expansionProperty;

    JavaFullStackCard() {
        super("Java full-stack");
    }

    @Override
    Node createIllustrationNode() {
        stepToolkitLogos = new Node[] {
                // Step 1 - Front office, back office
                createNoLogo(), createNoLogo(),
                // Step 2 - Front office, back office
                createAngularLogo(), createFxLogo(),
                // Step 3 - Front office, back office
                createVueLogo(), createVueLogo(),
                // Step 4 - Front office, back office
                createReactLogo(), createReactLogo(),
                // Step 5 - Front office, back office
                createWebFxLogo(), createWebFxLogo(),
                // Step 6 - Front office, back office
                createWebFxLogo(), createWebFxLogo(),
                // Step 7 - Front office, back office
                createNoLogo(), createNoLogo()
        };
        stepLanguageLogos = new Node[] {
                // Step 1 - Front office, back office, server
                createNoLogo(), createNoLogo(), createNoLogo(),
                // Step 2 - Front office, back office, server
                createJSLogo(), createJavaLogo(), createJavaLogo(),
                // Step 3 - Front office, back office, server
                createJSLogo(), createJSLogo(), createJavaLogo(),
                // Step 4 - Front office, back office, server
                createJSLogo(), createJSLogo(), createJSLogo(),
                // Step 5 - Front office, back office, server
                createJavaLogo(), createJavaLogo(), createJavaLogo(),
                // Step 6 - Front office, back office, server
                createJavaLogo(), createJavaLogo(), createJavaLogo(),
                // Step 7 - Front office, back office, server
                createNoLogo(), createNoLogo(), createNoLogo()
        };
        toolkitFlipPanels = new FlipPanel[] {
                new FlipPanel(), // For front office toolkit
                new FlipPanel()  // For back office toolkit
        };
        languageFlipPanels = new FlipPanel[] {
                new FlipPanel(), // For front office language
                new FlipPanel(), // For back office language
                new FlipPanel()  // For server language
        };
        CirclePane frontOfficeCirclePane = new CirclePane("Front office", -150, Color.rgb(98, 0, 173), toolkitFlipPanels[0], languageFlipPanels[0]);
        CirclePane backOfficeCirclePane  = new CirclePane("Back office",   -30, raspberryPiColor, toolkitFlipPanels[1], languageFlipPanels[1]);
        CirclePane serverCirclePane   = new CirclePane("Server",      90, fxColor, languageFlipPanels[2], null);
        // Note: HTML & JavaFX implementation differs, so the rendering is different
        frontOfficeCirclePane.setBlendMode(BlendMode.SCREEN);
        backOfficeCirclePane .setBlendMode(BlendMode.SCREEN);
        serverCirclePane  .setBlendMode(BlendMode.SCREEN);
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
            case 1: return "WebFX opens the door to a 100% java full-stack web development with JavaFX as client-side technology.";
            case 2: return "Web development is transitioning to client-side technologies, and this often introduces heterogeneity in java-based environments.";
            case 3: return "Standardizing your client stack with a JS back office may be an option to share the common client code, but your stack is still heterogeneous.";
            case 4: return "Switching to a pure JS stack may be a solution, but you would prefer to stay with Java?";
            case 5: return "WebFX offers you JavaFX for your client-side web technology, and let you stay with Java all along your stack.";
            case 6: return "Same language for all your stack. Same UI toolkit for your front & back office. Same IDE for all your code. The ideal solution for java-based environments.";
            case 7: return "And simply share the common code between your front office, back office and server with the Java module system.";
            default: return null;
        }
    }

    @Override
    void prepareCardTransition(int step, CardTransition cardTransition) {
        super.prepareCardTransition(step, cardTransition);
        if (step > 1 || puzzles != null) {
            updateFlipPanels(toolkitFlipPanels,  stepToolkitLogos,  step, cardTransition);
            updateFlipPanels(languageFlipPanels, stepLanguageLogos, step, cardTransition);
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

    private void updateFlipPanels(FlipPanel[] flipPanels, Node[] logos, int step, CardTransition cardTransition) {
        int n = flipPanels.length;
        for (int i = 0; i < n; i++) {
            FlipPanel flipPanel = flipPanels[i];
            cardTransition.addKeyValue(new KeyValue(flipPanel.opacityProperty(), step >= 2 && step <= 5 ? 1 : 0));
            Node stepLogo = logos[(step - 1) * n + i];
            getChildren().remove(stepLogo); // Removing them from the card if inserted by preloadImages()
            stepLogo.setVisible(true);
            Node previousStepLogo = logos[((step - 1 + (forwardingStep ? -1 : 1) + 7) % 7) * n + i];
            boolean logoChanged = !getLogoId(stepLogo).equals(getLogoId(previousStepLogo));
            boolean mustBeFront = step == 1 || step == 6;
            if (logoChanged || mustBeFront) {
                boolean backVisible = flipPanel.isBackVisible();
                (backVisible || mustBeFront ? flipPanel.getFront() : flipPanel.getBack()).getChildren().setAll(stepLogo);
                flipPanel.setFlipTime(mustBeFront ? 0 : 700);
                if (mustBeFront)
                    flipPanel.flipToFront();
                else if (step == 2 && forwardingStep)
                    cardTransition.addOnFinished(() -> flip(flipPanel));
                else if (step >= 2 && step <= 5)
                    flip(flipPanel);
            }
        }
    }

    private void flip(FlipPanel flipPanel) {
        flipPanel.getParent().requestLayout(); // Hack for the web version, otherwise the flipPanel may be incorrectly sized after a windows resize
        if (flipPanel.isBackVisible())
            flipPanel.flipToFront();
        else
            flipPanel.flipToBack();
    }
}
