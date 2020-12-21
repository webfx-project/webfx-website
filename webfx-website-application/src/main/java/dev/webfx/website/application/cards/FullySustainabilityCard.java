package dev.webfx.website.application.cards;

import dev.webfx.website.application.WebSiteShared;
import eu.hansolo.enzo.flippanel.FlipPanel;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Bruno Salmon
 */
public final class FullySustainabilityCard extends Card {

    private final static Color BRICK_COLOR1 = Color.rgb(185, 128, 70);
    private final static Color BRICK_COLOR2 = Color.rgb(207, 156, 96);
    private final static Color BRICK_COLOR3 = Color.rgb(242, 178, 93);
    private final static Color BRICK_COLOR4 = Color.rgb(240, 190, 118);

    private final AnimatedBrick[] refactoringAnimatedBricks = {
            // Building bricks
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 3, new BrickMove( 0,  0,  0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.circle,    BRICK_COLOR2, 1, new BrickMove( 1,  1,  0, Direction.BOTTOM), new BrickMove(22,  1, -1, Direction.LEFT),  new BrickMove(23, -10, -1, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove( 2,  1,  1, Direction.BOTTOM), new BrickMove(22,  1,  0, Direction.LEFT)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR4, 3, new BrickMove( 3,  2,  0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove( 4,  3,  1, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove( 5,  3,  2, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 3, new BrickMove( 6,  4,  0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.circle,    BRICK_COLOR2, 1, new BrickMove( 7,  5,  1, Direction.BOTTOM), new BrickMove(26,  5, -1, Direction.LEFT),  new BrickMove(27, -10, -1, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 3, new BrickMove( 8,  6,  0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove( 9,  7,  0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove(10,  7,  2, Direction.BOTTOM), new BrickMove(29,  7,  1, Direction.LEFT)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 3, new BrickMove(11,  8,  0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(12,  9,  0, Direction.BOTTOM), new BrickMove(31,  9,  2, Direction.RIGHT)),
            new AnimatedBrick(BrickShape.circle,    BRICK_COLOR1, 1, new BrickMove(13,  9,  1, Direction.BOTTOM), new BrickMove(31,  9,  3, Direction.RIGHT), new BrickMove(32, -10,  3, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR4, 3, new BrickMove(14, 10,  0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove(15, 11,  1, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove(16, 11,  2, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 3, new BrickMove(17, 12,  0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove(18, 13,  1, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove(19, 13,  2, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 2, new BrickMove(20, 14,  1, Direction.BOTTOM), new BrickMove(36, 14,  0, Direction.LEFT)),
            // Refactoring bricks
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 2, new BrickMove(21,  1,  2, Direction.LEFT),   new BrickMove(22,  1,  1, Direction.LEFT)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(24,  3,  0, Direction.RIGHT)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 3, new BrickMove(25,  5,  2, Direction.LEFT),   new BrickMove(26,  5,  0, Direction.LEFT),  new BrickMove(27,   5,  0, Direction.LEFT)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(28,  7,  3, Direction.LEFT),   new BrickMove(29,  7,  2, Direction.LEFT)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 2, new BrickMove(30,  9, -2, Direction.RIGHT),  new BrickMove(31,  9,  0, Direction.RIGHT)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(33, 11,  0, Direction.RIGHT)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(34, 13,  0, Direction.RIGHT)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(35, 14,  3, Direction.LEFT),   new BrickMove(36, 14,  2, Direction.LEFT))
    };
    private final AnimatedBrick[] noRefactoringAnimatedBricks = {
            // Building bricks
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 3, new BrickMove( 0,  0, 0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.circle,    BRICK_COLOR2, 1, new BrickMove( 1,  1, 0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove( 2,  1, 1, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR4, 3, new BrickMove( 3,  2, 0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove( 4,  3, 1, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove( 5,  3, 2, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 3, new BrickMove( 6,  4, 0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.circle,    BRICK_COLOR2, 1, new BrickMove( 7,  5, 1, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 3, new BrickMove( 8,  6, 0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove( 9,  7, 0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove(10,  7, 2, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 3, new BrickMove(11,  8, 0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(12,  9, 0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.circle,    BRICK_COLOR1, 1, new BrickMove(13,  9, 1, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR4, 3, new BrickMove(14, 10, 0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove(15, 11, 1, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove(16, 11, 2, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 3, new BrickMove(17, 12, 0, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove(18, 13, 1, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove(19, 13, 2, Direction.BOTTOM)),
            new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 2, new BrickMove(20, 14, 1, Direction.BOTTOM))
    };

    private BorderPane borderPane;
    private StackPane animationPane;
    private Timeline brickAnimationTimeline;
    private HBox refactoringHBox;
    private FlipPanel languageFlipPanel, thumbFlipPanel;

    public FullySustainabilityCard() {
        super("Fully sustainable");
    }

    @Override
    protected Node createIllustrationNode() {
/*
        SVGPath javaPath = new SVGPath();
        javaPath.setContent(SvgLogoPaths.getJavaWordPath());
        javaPath.setFill(WebSiteShared.javaColor);
        javaPath.setTranslateY(12);
        javaPath.setEffect(WebSiteShared.dropShadow);
        SVGPath fxPath = new SVGPath();
        fxPath.setContent(SvgLogoPaths.getFxWordPath());
        fxPath.setFill(WebSiteShared.fxColor);
        fxPath.setEffect(WebSiteShared.dropShadow);
        HBox javaFxHBox = new HBox(5, javaPath, fxPath);
        javaFxHBox.setAlignment(Pos.CENTER);
        return javaFxHBox;
*/
        languageFlipPanel = new FlipPanel();
        SVGPath javaLogo = createJavaLogo();
        javaLogo.setTranslateY(-5);
        languageFlipPanel.getFront().getChildren().setAll(javaLogo);
        languageFlipPanel.getBack().getChildren().setAll(createJSLogo());
        thumbFlipPanel = new FlipPanel(Orientation.VERTICAL);
        thumbFlipPanel.setFlipTime(1500);
        SVGPath thumbUp = createThumbUp();
        thumbUp.setTranslateY(-6);
        thumbFlipPanel.getFront().getChildren().setAll(thumbUp);
        SVGPath thumbDown = createThumbDown();
        thumbDown.setTranslateY(8);
        thumbFlipPanel.getBack().getChildren().setAll(thumbDown);
        animationPane = new StackPane();
        animationPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        //animationPane.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
        borderPane = new BorderPane(animationPane);
        Text refactoring = WebSiteShared.setUpText(new Text("Refactoring"), 25, true, false, false);
        refactoring.setFill(Color.WHITE);
        borderPane.setBottom(refactoringHBox = new HBox(10, languageFlipPanel, refactoring, thumbFlipPanel) );
        refactoringHBox.setAlignment(Pos.CENTER);
        BorderPane.setMargin(refactoringHBox, new Insets(10));
        return borderPane;
    }

    @Override
    protected String subTitle(int step) {
        switch (step) {
            case 1: return "Invest in the right technology to build sustainable applications.";
            case 2: return "Only a robust refactoring support can give you the ability to keep your code working over the time.";
            case 3: return "This is where Java wins over JavaScript.";
            case 4: return "To last, your application also needs a UI toolkit with a long life and a stable API.";
            case 5: return "This is where JavaFX wins over all other web toolkits.";
            case 6: return "Therefore Java & JavaFX is an excellent choice for building sustainable applications.";
            case 7: return "This is what have motivated us to build WebFX and port this sustainable model to the Web.";
            default: return null;
        }
    }

    @Override
    protected void prepareCardTransition(int step, CardTransition cardTransition) {
        super.prepareCardTransition(step, cardTransition);
        cardTransition.addKeyValue(
                new KeyValue(refactoringHBox.opacityProperty(), step == 2 || step == 3 ? 1 : 0)
        );
        refactoringHBox.setVisible(step <= 3);
        stopBrickAnimation();
        switch (step) {
            case 1:
                languageFlipPanel.flipToFront();
                thumbFlipPanel.flipToFront();
                break;
            case 2:
                animationPane.setMinHeight(14 * BRICK_HEIGHT_PIXELS + 100);
                borderPane.requestLayout();
                borderPane.layout();
                startBrickAnimation(refactoringAnimatedBricks);
                break;
            case 3:
                languageFlipPanel.flipToBack();
                thumbFlipPanel.flipToBack();
                startBrickAnimation(noRefactoringAnimatedBricks);
                break;
            case 4:
                animationPane.setMinHeight(-1);
                break;
        }
    }

    private void startBrickAnimation(AnimatedBrick[] animatedBricks) {
        animationPane.getChildren().addAll(Arrays.stream(animatedBricks).map(ab -> { ab.shape.setVisible(false); return ab.shape;}).collect(Collectors.toList()));
        playBrickAnimationSequence(animatedBricks, 0);
    }

    private void playBrickAnimationSequence(AnimatedBrick[] animatedBricks, int sequence) {
        brickAnimationTimeline = new Timeline();
        for (AnimatedBrick animatedBrick : animatedBricks)
            animatedBrick.programAnimationSequence(sequence, brickAnimationTimeline);
        if (!brickAnimationTimeline.getKeyFrames().isEmpty()) {
            brickAnimationTimeline.setOnFinished(e -> playBrickAnimationSequence(animatedBricks, sequence + 1));
            brickAnimationTimeline.play();
        }
    }

    private void stopBrickAnimation() {
        if (brickAnimationTimeline != null)
            brickAnimationTimeline.stop();
        animationPane.getChildren().clear();
    }

    private static final double BRICK_WIDTH_UNIT_PIXELS = 25;
    private static final double BRICK_HEIGHT_PIXELS = 20;
    private static final int MAX_BRICK_WIDTH = 3;

    private class AnimatedBrick {
        final int width;
        final Shape shape;
        final BrickMove[] brickMoves;

        AnimatedBrick(BrickShape brickShape, Color color, int width, BrickMove... brickMoves) {
            this.width = width;
            Color brighter = color.brighter();
            Color darker = color.darker();
            LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, brighter), new Stop(1, darker));
            if (brickShape == BrickShape.rectangle)
                shape = new Rectangle(width * BRICK_WIDTH_UNIT_PIXELS, BRICK_HEIGHT_PIXELS - 1, gradient);
            else
                shape = new Circle((BRICK_HEIGHT_PIXELS - 1) / 2, gradient);
            shape.setStroke(brighter);
            this.brickMoves = brickMoves;
        }

        void programAnimationSequence(int sequence, Timeline timeline) {
            BrickMove brickMove = Arrays.stream(brickMoves).filter(bt -> sequence == bt.sequence).findFirst().orElse(null);
            if (brickMove != null)
                shape.setVisible(true);
            if (brickMove != null) {
                double h = animationPane.getHeight(), w = animationPane.getWidth();
                double endX = width < -MAX_BRICK_WIDTH ? -w/2 : width > MAX_BRICK_WIDTH ? w/2 : (brickMove.horizontalShift - 0.5 * (MAX_BRICK_WIDTH - width)) * BRICK_WIDTH_UNIT_PIXELS;
                double endY = brickMove.row < 0 ? 0.75 * getHeight() : h / 2 - BRICK_HEIGHT_PIXELS * (brickMove.row + 0.5);
                boolean vertical = brickMove.direction == Direction.BOTTOM;
                boolean initial = brickMove == brickMoves[0];
                if (initial) {
                    if (vertical) {
                        shape.setTranslateX(endX);
                        shape.setTranslateY(-h / 2);
                    } else {
                        shape.setTranslateX((brickMove.direction == Direction.LEFT ? 1 : -1) * w / 2);
                        shape.setTranslateY(endY);
                    }
                }
                KeyFrame keyFrame = vertical ?
                        new KeyFrame(Duration.millis(150), new KeyValue(shape.translateYProperty(), endY, Interpolator.EASE_IN)) :
                        new KeyFrame(Duration.millis(500), new KeyValue(shape.translateXProperty(), endX));
                timeline.getKeyFrames().add(keyFrame);
            }
        }
    }

    private static class BrickMove {
        Direction direction;
        int sequence, row, horizontalShift;

        BrickMove(int sequence, int row, int column, Direction direction) {
            this.sequence = sequence;
            this.row = row;
            this.horizontalShift = column;
            this.direction = direction;
        }
    }

    enum BrickShape {
        rectangle, circle
    }

    enum Direction {
        LEFT, RIGHT, BOTTOM
    }
}
