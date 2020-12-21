package dev.webfx.website.application.cards;

import dev.webfx.website.application.SvgLogoPaths;
import dev.webfx.website.application.WebSiteShared;
import eu.hansolo.enzo.flippanel.FlipPanel;
import javafx.animation.KeyValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

/**
 * @author Bruno Salmon
 */
public final class FullyJavaCard extends Card {

    private final static double r = 115;
    private CirclePane[] circlePanes;
    private Node[] stepToolkitLogos, stepLanguageLogos;
    private FlipPanel[] toolkitFlipPanels, languageFlipPanels;
    private Node[] puzzles;

    public FullyJavaCard() {
        super("Fully Java");
    }

    @Override
    protected Node createIllustrationNode() {
        circlePanes = new CirclePane[] {
                new CirclePane("Front-end", r, -150, WebSiteShared.raspberryPiColor),
                new CirclePane("Back-end",  r,  -30, Color.rgb(179, 181, 44)),
                new CirclePane("Server",    r,   90, WebSiteShared.fxColor)
        };
        toolkitFlipPanels = new FlipPanel[] {
                new FlipPanel(), // For frontend toolkit
                new FlipPanel()  // For backend toolkit
        };
        for (int i = 0; i < toolkitFlipPanels.length; i++) {
            circlePanes[i].getChildren().add(toolkitFlipPanels[i]);
            toolkitFlipPanels[i].setTranslateY(-20);
        }
        languageFlipPanels = new FlipPanel[] {
                new FlipPanel(), // For frontend language
                new FlipPanel(), // For backend language
                new FlipPanel()  // For server language
        };
        for (int i = 0; i < languageFlipPanels.length; i++) {
            circlePanes[i].getChildren().add(languageFlipPanels[i]);
            languageFlipPanels[i].setTranslateY(i == 2 ? -10 : 50);
        }
        puzzles = new Node[] {
                createPuzzle(Color.RED, r - 16, r * 0.25 - 16),
                createPuzzle(Color.GREEN, r - 16, r - 16),
                createPuzzle(Color.YELLOW, 0.33 * r - 16, r * 1.33 - 16),
                createPuzzle(Color.BLUE, 1.66 * r - 16, r * 1.33 - 16)
        };
        Group group = new Group(circlePanes);
        group.getChildren().addAll(puzzles);
        return group;
    }

    private static SVGPath createPuzzle(Paint fill, double x, double y) {
        SVGPath puzzle = createLogoSVGPath(SvgLogoPaths.getPuzzlePath(), fill, null);
        puzzle.relocate(x, y);
        return puzzle;
    }

    protected String subTitle(int step) {
        switch (step) {
            case 1: return "Simplify your development by using the same language and IDE for all your stack.";
            case 2: return "Modern applications require client-side UI toolkits to provide a reactive experience, but this can lead to heterogeneous environments.";
            case 3: return "Many frameworks claim to provide full-stack solutions but they don't have a full-stack language so your environment is still not homogeneous.";
            case 4: return "Very few languages can be used both on client and server, especially when targeting the Web. JavaScript is one of them, but do you like it?";
            case 5: return "Fruit of the marriage between GWT & JavaFX, WebFX opens the door to a 100% full-stack Java Web development with one of the most amazing UI toolkit!";
            case 6: return "Same language for all your stack. Same UI toolkit for your back-end & front-end. All in your preferred IDE. Your dream as Java Web developer becoming real.";
            case 7: return "And share the code between your back-end, front-end and server in a professional manner with the Java module system.";
            default: return null;
        }
    }

    @Override
    protected void prepareCardTransition(int step, CardTransition cardTransition) {
        super.prepareCardTransition(step, cardTransition);
        if (stepToolkitLogos == null) {
            stepToolkitLogos = new Node[] {
                    // Step 1 - Frontend, backend
                    createNoLogo(), createNoLogo(),
                    // Step 2 - Frontend, backend
                    createFlutterLogo(), createQtLogo(),
                    // Step 3 - Frontend, backend
                    createReactLogo(), createReactLogo(),
                    // Step 4 - Frontend, backend
                    createVueLogo(), createVueLogo(),
                    // Step 5 - Frontend, backend
                    createFxLogo(), createFxLogo(),
                    // Step 6 - Frontend, backend
                    createFxLogo(), createFxLogo(),
                    // Step 7 - Frontend, backend
                    createNoLogo(), createNoLogo()
            };
            stepLanguageLogos = new Node[] {
                    // Step 1 - Frontend, backend, server
                    createNoLogo(), createNoLogo(), createNoLogo(),
                    // Step 2 - Frontend, backend, server
                    createDartLogo(), createCppLogo(), createJavaLogo(),
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
        }
        updateFlipPanels(toolkitFlipPanels,  stepToolkitLogos,  step, cardTransition);
        updateFlipPanels(languageFlipPanels, stepLanguageLogos, step, cardTransition);
        if (step <= 2 || step >= 6) {
            boolean compact = step == 1 || step >= 6;
            double distance = (compact ? 0.5 : 1.2) * r;
            for (CirclePane circlePane : circlePanes) {
                double angle = circlePane.getAngle();
                double cx = distance * Math.cos(angle * Math.PI / 180), cy = distance * Math.sin(angle * Math.PI / 180);
                cardTransition.addKeyValue(
                        new KeyValue(circlePane.layoutXProperty(), cx),
                        new KeyValue(circlePane.layoutYProperty(), cy),
                        new KeyValue(circlePane.rotateProperty(), compact && angle != 90 ? circlePane.getAngle() + 90 : 0)
                );
            }
        }
        for (Node puzzle : puzzles)
            cardTransition.addKeyValue(new KeyValue(puzzle.opacityProperty(), step == 7 ? 1 : 0));
    }

    private void updateFlipPanels(FlipPanel[] flipPanels, Node[] logos, int step, CardTransition cardTransition) {
        int n = flipPanels.length;
        for (int i = 0; i < n; i++) {
            FlipPanel flipPanel = flipPanels[i];
            cardTransition.addKeyValue(new KeyValue(flipPanel.opacityProperty(), step >= 2 && step <= 5 ? 1 : 0));
            Node stepLogo = logos[(step - 1) * n + i];
            Node previousStepLogo = logos[(step == 1 ? 6 : step - 2) * n + i];
            boolean logoChanged = !stepLogo.getProperties().get("logo").equals(previousStepLogo.getProperties().get("logo"));
            boolean mustBeFront = step == 1 || step == 6;
            if (logoChanged || mustBeFront) {
                boolean backVisible = flipPanel.isBackVisible();
                (backVisible || mustBeFront ? flipPanel.getFront() : flipPanel.getBack()).getChildren().setAll(stepLogo);
                flipPanel.setFlipTime(mustBeFront ? 0 : 700);
                if (mustBeFront)
                    flipPanel.flipToFront();
                else if (step == 2)
                    cardTransition.addOnFinished(() -> flip(flipPanel));
                else if (step >= 3 && step <= 5)
                    flip(flipPanel);
            }
        }
    }

    private void flip(FlipPanel flipPanel) {
        if (flipPanel.isBackVisible())
            flipPanel.flipToFront();
        else
            flipPanel.flipToBack();
    }
}
