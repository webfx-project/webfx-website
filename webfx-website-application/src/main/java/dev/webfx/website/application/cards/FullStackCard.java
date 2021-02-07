package dev.webfx.website.application.cards;

import dev.webfx.website.application.SvgLogoPaths;
import dev.webfx.website.application.WebSiteShared;
import eu.hansolo.enzo.flippanel.FlipPanel;
import javafx.animation.KeyValue;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * @author Bruno Salmon
 */
public final class FullStackCard extends Card {

    private Pane pane;
    private Node[] stepToolkitLogos, stepLanguageLogos;
    private FlipPanel[] toolkitFlipPanels, languageFlipPanels;
    private Node[] puzzles;
    private DoubleProperty expansionProperty;

    public FullStackCard() {
        super("Full-stack");
        alwaysUseTitleSpaceForIllustration = true;
        //useTitleSpaceForIllustrationWhenHidden = false;
    }

    @Override
    Node createIllustrationNode() {
        stepToolkitLogos = new Node[] {
                // Step 1 - Frontend, backend
                createNoLogo(), createNoLogo(),
                // Step 2 - Frontend, backend
                createAngularLogo(), createQtLogo(),
                // Step 3 - Frontend, backend
                createVueLogo(), createVueLogo(),
                // Step 4 - Frontend, backend
                createReactLogo(), createReactLogo(),
                // Step 5 - Frontend, backend
                createWebFxLogo(), createWebFxLogo(),
                // Step 6 - Frontend, backend
                createWebFxLogo(), createWebFxLogo(),
                // Step 7 - Frontend, backend
                createNoLogo(), createNoLogo()
        };
        stepLanguageLogos = new Node[] {
                // Step 1 - Frontend, backend, server
                createNoLogo(), createNoLogo(), createNoLogo(),
                // Step 2 - Frontend, backend, server
                createJSLogo(), createCppLogo(), createJavaLogo(),
                // Step 3 - Frontend, backend, server
                createJSLogo(), createJSLogo(), createPythonLogo(),
                // Step 4 - Frontend, backend, server
                createJSLogo(), createJSLogo(), createJSLogo(),
                // Step 5 - Frontend, backend, server
                createJavaLogo(), createJavaLogo(), createJavaLogo(),
                // Step 6 - Frontend, backend, server
                createJavaLogo(), createJavaLogo(), createJavaLogo(),
                // Step 7 - Frontend, backend, server
                createNoLogo(), createNoLogo(), createNoLogo()
        };
        toolkitFlipPanels = new FlipPanel[] {
                new FlipPanel(), // For frontend toolkit
                new FlipPanel()  // For backend toolkit
        };
        languageFlipPanels = new FlipPanel[] {
                new FlipPanel(), // For frontend language
                new FlipPanel(), // For backend language
                new FlipPanel()  // For server language
        };
        puzzles = new Node[] {
                createPuzzle(Color.RED),
                createPuzzle(Color.YELLOW),
                createPuzzle(Color.BLUE),
                createPuzzle(Color.GREEN)
        };
        CirclePane frontendCirclePane = new CirclePane("Front-end", -150, Color.rgb(98, 0, 173), toolkitFlipPanels[0], languageFlipPanels[0]);
        CirclePane backendCirclePane  = new CirclePane("Back-end",   -30, WebSiteShared.raspberryPiColor, toolkitFlipPanels[1], languageFlipPanels[1]);
        CirclePane serverCirclePane   = new CirclePane("Server",      90, WebSiteShared.fxColor, languageFlipPanels[2], null);
        pane = new Pane(frontendCirclePane, backendCirclePane, serverCirclePane) { { getChildren().addAll(puzzles); }
            @Override
            protected void layoutChildren() {
                double w = getWidth(), h = getHeight(), s = Math.min(w, h), wd2 = w / 2, hd2 = h / 2, sd2 = s / 2, r = sd2 * 0.49, d = 2 * r;
                double R = (s - d) / (2 * Math.cos(Math.PI/6)) * (0.5 + 0.5 * expansionProperty.get());
                double yc = 0.8 * hd2, yClients = yc - R * Math.sin(Math.PI/6) - r;
                double Rcos = R * Math.cos(Math.PI/6);
                frontendCirclePane.setRadius(r);
                frontendCirclePane.setRotate((frontendCirclePane.getAngle() + 90) * (1 - expansionProperty.get()));
                layoutInArea(frontendCirclePane, wd2 - Rcos - r, yClients,   d, d, 0, HPos.CENTER, VPos.CENTER);
                backendCirclePane.setRadius(r);
                backendCirclePane.setRotate((backendCirclePane.getAngle() + 90) * (1 - expansionProperty.get()));
                layoutInArea(backendCirclePane,  wd2 + Rcos - r, yClients,   d, d, 0, HPos.CENTER, VPos.CENTER);
                serverCirclePane.setRadius(r);
                layoutInArea(serverCirclePane,   wd2 - r, yc + R - r,  d, d, 0, HPos.CENTER, VPos.CENTER);
                if (currentAnimationStep == 7) {
                    double pd = 0.3 * r, pr = pd / 2;
                    layoutInArea(puzzles[0], wd2 - pr, yc - r * 0.0 - pr, pd, pd, 0, HPos.CENTER, VPos.CENTER);
                    layoutInArea(puzzles[1], wd2 - pr, yc - r * 0.7 - pr, pd, pd, 0, HPos.CENTER, VPos.CENTER);
                    layoutInArea(puzzles[2], wd2 - 0.6 * r - pr, yc + r * 0.3 - pr, pd, pd, 0, HPos.CENTER, VPos.CENTER);
                    layoutInArea(puzzles[3], wd2 + 0.6 * r - pr, yc + r * 0.3 - pr, pd, pd, 0, HPos.CENTER, VPos.CENTER);
                }
            }
        };
        expansionProperty = new SimpleDoubleProperty() {
            @Override
            protected void invalidated() {
                pane.requestLayout();
            }
        };
        preloadImages(stepToolkitLogos);
        preloadImages(stepLanguageLogos);
        return pane;
    }

    private static Node createPuzzle(Paint fill) {
        return new ScalePane(createLogoSVGPath(SvgLogoPaths.getPuzzlePath(), fill));
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
            case 1: return "WebFX will simplify your development stack.";
            case 2: return "Mixing different UI toolkits and languages makes your stack complex, and prevents you to share any common code.";
            case 3: return "Most of full-stack frameworks actually don't have a full-stack language, so your stack is still not fully homogeneous.";
            case 4: return "Very few languages can be used both on client and server, especially when targeting the Web. JavaScript is one of them, but perhaps you would prefer Java?";
            case 5: return "WebFX opens the door to a 100% full-stack Java development, with JavaFX as common UI toolkit for your back-end and front-end.";
            case 6: return "Same language for all your stack. Same UI toolkit for your back-end & front-end. Same IDE for all your code.";
            case 7: return "And share the code between your back-end, front-end and server in a professional manner with the Java module system.";
            default: return null;
        }
    }

    @Override
    void prepareCardTransition(int step, CardTransition cardTransition) {
        super.prepareCardTransition(step, cardTransition);
        updateFlipPanels(toolkitFlipPanels,  stepToolkitLogos,  step, cardTransition);
        updateFlipPanels(languageFlipPanels, stepLanguageLogos, step, cardTransition);
        cardTransition.addKeyValue(new KeyValue(expansionProperty, step == 1 || step >= 6 ? 0 : 1));
        for (Node puzzle : puzzles)
            cardTransition.addKeyValue(new KeyValue(puzzle.opacityProperty(), step == 7 ? 1 : 0));
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
