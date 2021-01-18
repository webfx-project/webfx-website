package dev.webfx.website.application.cards;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Bruno Salmon
 */
final class RefactoringAnimationPane extends Pane {

    private final static Color BRICK_COLOR1 = Color.rgb(185, 128, 70);
    private final static Color BRICK_COLOR2 = Color.rgb(207, 156, 96);
    private final static Color BRICK_COLOR3 = Color.rgb(242, 178, 93);
    private final static Color BRICK_COLOR4 = Color.rgb(240, 190, 118);

    private int MAX_BRICK_WIDTH, STARTING_ROW, ENDING_ROW;
    private double BRICK_WIDTH_UNIT_PIXELS;
    private double BRICK_HEIGHT_PIXELS;
    private double BRICK_STROKE_WIDTH;

    private final Region card;
    private Timeline brickAnimationTimeline;

    RefactoringAnimationPane(Region card) {
        this.card = card;
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    void startBadRefactoringBrickAnimation(double wallHeight, Runnable onFinished) {
        MAX_BRICK_WIDTH = 3;
        STARTING_ROW = 0;
        ENDING_ROW = Integer.MAX_VALUE;
        BRICK_HEIGHT_PIXELS = wallHeight / 15;
        BRICK_WIDTH_UNIT_PIXELS = BRICK_HEIGHT_PIXELS * 25 / 20;
        BRICK_STROKE_WIDTH = 1;
        startBrickAnimation(onFinished,
                // Building and falling bricks
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 3, new BrickMove( 0,  0, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.circle,    BRICK_COLOR2, 1, new BrickMove( 1,  1, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove( 2,  1, 1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR4, 3, new BrickMove( 3,  2, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove( 4,  3, 1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove( 5,  3, 2, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 3, new BrickMove( 6,  4, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.circle,    BRICK_COLOR2, 1, new BrickMove( 7,  5, 1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 3, new BrickMove( 8,  6, 0, MoveType.TRANSLATION_BOTTOM), new BrickMove(24, 5, 1, MoveType.ROTATION)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove( 9,  7, 0, MoveType.TRANSLATION_BOTTOM), new BrickMove(24, 5, 1, MoveType.ROTATION), new BrickMove(25, 0, 1, MoveType.ROTATION),   new BrickMove(26, -40, 10, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove(10,  7, 2, MoveType.TRANSLATION_BOTTOM), new BrickMove(24, 5, 1, MoveType.ROTATION), new BrickMove(25, 0, 1.1, MoveType.ROTATION), new BrickMove(26, -40, 10, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 3, new BrickMove(11,  8, 0, MoveType.TRANSLATION_BOTTOM), new BrickMove(24, 5, 1, MoveType.ROTATION), new BrickMove(25, 0, 1.3, MoveType.ROTATION), new BrickMove(26, -40, 10, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(12,  9, 0, MoveType.TRANSLATION_BOTTOM), new BrickMove(24, 5, 1, MoveType.ROTATION), new BrickMove(25, 0, 1.5, MoveType.ROTATION), new BrickMove(26, -40, 10, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.circle,    BRICK_COLOR1, 1, new BrickMove(13,  9, 1, MoveType.TRANSLATION_BOTTOM), new BrickMove(24, 5, 1, MoveType.ROTATION), new BrickMove(25, 0, 1.6, MoveType.ROTATION), new BrickMove(26, -40, 10, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR4, 3, new BrickMove(14, 10, 0, MoveType.TRANSLATION_BOTTOM), new BrickMove(22, 9, 1, MoveType.ROTATION), new BrickMove(24,-4, 3, MoveType.ROTATION),   new BrickMove(25,  -4,  3, MoveType.ROTATION), new BrickMove(26, -40, 10, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove(15, 11, 1, MoveType.TRANSLATION_BOTTOM), new BrickMove(22, 9, 1, MoveType.ROTATION), new BrickMove(23, 0, 1, MoveType.ROTATION),   new BrickMove(24, -50, 10, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove(16, 11, 2, MoveType.TRANSLATION_BOTTOM), new BrickMove(22, 9, 1, MoveType.ROTATION), new BrickMove(23, 0, 1.2, MoveType.ROTATION), new BrickMove(24, -50, 10, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 3, new BrickMove(17, 12, 0, MoveType.TRANSLATION_BOTTOM), new BrickMove(22, 9, 1, MoveType.ROTATION), new BrickMove(23, 0, 1.4, MoveType.ROTATION), new BrickMove(24, -50, 10, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove(18, 13, 1, MoveType.TRANSLATION_BOTTOM), new BrickMove(22, 9, 1, MoveType.ROTATION), new BrickMove(23, 0, 1.6, MoveType.ROTATION), new BrickMove(24, -50, 10, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove(19, 13, 2, MoveType.TRANSLATION_BOTTOM), new BrickMove(22, 9, 1, MoveType.ROTATION), new BrickMove(23, 0, 1.8, MoveType.ROTATION), new BrickMove(24, -50, 10, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 2, new BrickMove(20, 15, 1, MoveType.TRANSLATION_BOTTOM), // The too much brick -> first move to row 15, then slow move to row 14
                                                                               new BrickMove(21, 14, 1, MoveType.TRANSLATION_BOTTOM), new BrickMove(22, 9, 1, MoveType.ROTATION), new BrickMove(23, 0, 2, MoveType.ROTATION),   new BrickMove(24, -50, 10, MoveType.TRANSLATION_BOTTOM))
        );
    }

    void startGoodRefactoringBrickAnimation(double wallHeight, Runnable onFinished) {
        MAX_BRICK_WIDTH = 3;
        STARTING_ROW = 0;
        ENDING_ROW = Integer.MAX_VALUE;
        BRICK_HEIGHT_PIXELS = wallHeight / 15;
        BRICK_WIDTH_UNIT_PIXELS = BRICK_HEIGHT_PIXELS * 25 / 20;
        BRICK_STROKE_WIDTH = 1;
        startBrickAnimation(onFinished,
                // Building bricks
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 3, new BrickMove( 0,  0,  0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.circle,    BRICK_COLOR2, 1, new BrickMove( 1,  1,  0, MoveType.TRANSLATION_BOTTOM), new BrickMove(12,  1, -1, MoveType.TRANSLATION_LEFT),  new BrickMove(13, -10, -1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove( 2,  1,  1, MoveType.TRANSLATION_BOTTOM), new BrickMove(12,  1,  0, MoveType.TRANSLATION_LEFT)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR4, 3, new BrickMove( 3,  2,  0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove( 4,  3,  1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove( 5,  3,  2, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 3, new BrickMove( 6,  4,  0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.circle,    BRICK_COLOR2, 1, new BrickMove( 7,  5,  1, MoveType.TRANSLATION_BOTTOM), new BrickMove(16,  5, -1, MoveType.TRANSLATION_LEFT),  new BrickMove(17, -10, -1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 3, new BrickMove( 8,  6,  0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove( 9,  7,  0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove(10,  7,  2, MoveType.TRANSLATION_BOTTOM), new BrickMove(19,  7,  1, MoveType.TRANSLATION_LEFT)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 3, new BrickMove(20,  8,  0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(21,  9,  0, MoveType.TRANSLATION_BOTTOM), new BrickMove(31,  9,  2, MoveType.TRANSLATION_RIGHT)),
                new AnimatedBrick(BrickShape.circle,    BRICK_COLOR1, 1, new BrickMove(22,  9,  1, MoveType.TRANSLATION_BOTTOM), new BrickMove(31,  9,  3, MoveType.TRANSLATION_RIGHT), new BrickMove(32, -10,  3, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR4, 3, new BrickMove(23, 10,  0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove(24, 11,  1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove(25, 11,  2, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 3, new BrickMove(26, 12,  0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove(27, 13,  1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove(28, 13,  2, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 2, new BrickMove(29, 14,  1, MoveType.TRANSLATION_BOTTOM), new BrickMove(36, 14,  0, MoveType.TRANSLATION_LEFT)),
                // Refactoring bricks
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 2, new BrickMove(11,  1,  2, MoveType.TRANSLATION_LEFT),   new BrickMove(12,  1,  1, MoveType.TRANSLATION_LEFT)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(14,  3,  0, MoveType.TRANSLATION_RIGHT)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 3, new BrickMove(15,  5,  2, MoveType.TRANSLATION_LEFT),   new BrickMove(16,  5,  0, MoveType.TRANSLATION_LEFT),  new BrickMove(17,   5,  0, MoveType.TRANSLATION_LEFT)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(18,  7,  3, MoveType.TRANSLATION_LEFT),   new BrickMove(19,  7,  2, MoveType.TRANSLATION_LEFT)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 2, new BrickMove(30,  9, -2, MoveType.TRANSLATION_RIGHT),  new BrickMove(31,  9,  0, MoveType.TRANSLATION_RIGHT)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(33, 11,  0, MoveType.TRANSLATION_RIGHT)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(34, 13,  0, MoveType.TRANSLATION_RIGHT)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(35, 14,  3, MoveType.TRANSLATION_LEFT),   new BrickMove(36, 14,  2, MoveType.TRANSLATION_LEFT))
        );
    }

    void startGoodShortRefactoringBrickAnimation(int startingRow, int endingRow, double wallHeight, Runnable onFinished) {
        MAX_BRICK_WIDTH = 2;
        STARTING_ROW = startingRow;
        ENDING_ROW = endingRow;
        BRICK_WIDTH_UNIT_PIXELS = 40;
        BRICK_HEIGHT_PIXELS = wallHeight / endingRow;
        BRICK_STROKE_WIDTH = 2;
        startBrickAnimation(onFinished,
                // Building and falling bricks
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 2, new BrickMove( 0,  0, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove( 1,  1, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove( 2,  1, 1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR4, 1, new BrickMove( 3,  2, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove( 4,  2, 1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove( 5,  3, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove( 6,  3, 1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 2, new BrickMove( 7,  4, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove( 8,  5, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR4, 1, new BrickMove( 9,  5, 1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 2, new BrickMove(10,  6, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove(11,  7, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove(12,  7, 1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 2, new BrickMove(13,  8, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(14,  9, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 1, new BrickMove(15,  9, 1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR4, 2, new BrickMove(16, 10, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove(17, 11, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove(18, 11, 1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 2, new BrickMove(19, 12, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove(20, 13, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR2, 1, new BrickMove(21, 13, 1, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR1, 2, new BrickMove(22, 14, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR4, 1, new BrickMove(23, 15, 0, MoveType.TRANSLATION_BOTTOM)),
                new AnimatedBrick(BrickShape.rectangle, BRICK_COLOR3, 1, new BrickMove(24, 15, 1, MoveType.TRANSLATION_BOTTOM))
        );
    }

    private void startBrickAnimation(Runnable onFinished, AnimatedBrick... animatedBricks) {
        setMaxWidth(MAX_BRICK_WIDTH * BRICK_WIDTH_UNIT_PIXELS);
        setMinHeight(14 * BRICK_HEIGHT_PIXELS + 100);
        card.requestLayout();
        card.layout();
        getChildren().addAll(Arrays.stream(animatedBricks).map(ab -> { ab.shape.setVisible(false); return ab.shape;}).collect(Collectors.toList()));
        playBrickAnimationSequence(animatedBricks, 0, onFinished);
    }

    private void playBrickAnimationSequence(AnimatedBrick[] animatedBricks, int sequence, Runnable onFinished) {
        brickAnimationTimeline = new Timeline();
        boolean initialSequence = sequence == 0;
        boolean reachedStartingRow = !initialSequence;
        while (true) {
            for (AnimatedBrick animatedBrick : animatedBricks)
                reachedStartingRow |= animatedBrick.programAnimationSequence(sequence, brickAnimationTimeline);
            if (reachedStartingRow) {
                if (initialSequence && STARTING_ROW > 0) {
                    brickAnimationTimeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(500), new KeyValue(translateXProperty(), getTranslateX() + 183, Interpolator.EASE_BOTH)));
                    sequence--;
                }
                break;
            }
            sequence++;
        }
        if (!brickAnimationTimeline.getKeyFrames().isEmpty()) {
            int nextSequence = sequence + 1;
            brickAnimationTimeline.setOnFinished(e -> playBrickAnimationSequence(animatedBricks, nextSequence, onFinished));
            brickAnimationTimeline.play();
        } else if (onFinished != null)
            onFinished.run();
    }

    void stopBrickAnimation() {
        if (brickAnimationTimeline != null)
            brickAnimationTimeline.stop();
    }

    private class AnimatedBrick {
        final int width;
        final Shape shape;
        final BrickMove[] brickMoves;
        double column;

        AnimatedBrick(BrickShape brickShape, Color color, int width, BrickMove... brickMoves) {
            this.width = width;
            Color brighter = color.brighter();
            Color darker = color.darker();
            LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, brighter), new Stop(1, darker));
            if (brickShape == BrickShape.rectangle)
                shape = new Rectangle(width * BRICK_WIDTH_UNIT_PIXELS, BRICK_HEIGHT_PIXELS - 2 * BRICK_STROKE_WIDTH, gradient);
            else {
                double radius = (BRICK_HEIGHT_PIXELS - BRICK_STROKE_WIDTH) / 2;
                shape = new Circle(radius, gradient);
                shape.setLayoutX(radius);
                shape.setLayoutY(radius);
            }
            shape.setStrokeType(StrokeType.OUTSIDE);
            shape.setStroke(brighter);
            shape.setStrokeWidth(BRICK_STROKE_WIDTH);
            this.brickMoves = brickMoves;
        }

        boolean programAnimationSequence(int sequence, Timeline timeline) {
            BrickMove brickMove = Arrays.stream(brickMoves).filter(bt -> sequence == bt.sequence).findFirst().orElse(null);
            if (brickMove != null && brickMove.row < ENDING_ROW) {
                shape.setVisible(true);
                double h = getHeight(), w = getWidth(); // Of RefactoringAnimatedPane
                double endX = width < -MAX_BRICK_WIDTH ? 0 : width > MAX_BRICK_WIDTH ? w : brickMove.column * BRICK_WIDTH_UNIT_PIXELS;
                double endY = brickMove.row < -5 ? shape.getTranslateY() + card.getHeight() : h - BRICK_HEIGHT_PIXELS * (brickMove.row + 1);
                boolean vertical = brickMove.moveType == MoveType.TRANSLATION_BOTTOM;
                boolean rotation = brickMove.moveType == MoveType.ROTATION;
                boolean initial = brickMove == brickMoves[0];
                if (initial) {
                    shape.getTransforms().clear();
                    if (brickMove.row < STARTING_ROW) {
                        shape.setTranslateX(endX);
                        shape.setTranslateY(endY);
                        return false;
                    }
                    if (vertical) {
                        shape.setTranslateX(endX);
                        shape.setTranslateY(-h / 2);
                    } else {
                        shape.setTranslateX((brickMove.moveType == MoveType.TRANSLATION_LEFT ? 1 : -1) * card.getWidth() / 2);
                        shape.setTranslateY(endY);
                    }
                }
                Rotate rotate = rotation ? new Rotate(0, (brickMove.column + 0.5 - column) * BRICK_WIDTH_UNIT_PIXELS, endY + 0.5 * BRICK_HEIGHT_PIXELS - shape.getTranslateY()) : null;
                if (rotate != null)
                    shape.getTransforms().add(rotate);
                boolean secondRotate = rotation && shape.getTransforms().size() == 2;
                boolean thirdRotate = rotation && shape.getTransforms().size() == 3;
                boolean lastRotate = secondRotate && sequence != 24 || thirdRotate;
                boolean theTooMuchBrick = !initial && brickMove.row == 14;
                KeyFrame keyFrame =
                        rotation ?
                                new KeyFrame(Duration.millis(sequence == 25 ? 400 : 1000), new KeyValue(rotate.angleProperty(), secondRotate && lastRotate ? 60 : 30, sequence < 24 ? Interpolator.SPLINE(1, 0, 1, 1) : sequence == 24 ? Interpolator.SPLINE(0.5, 0, 1, 0.5) : Interpolator.SPLINE(0.5, 0.5, 1, 1))) :
                        vertical ?
                                new KeyFrame(Duration.millis(initial ? 150 : theTooMuchBrick ? 1500 : 400), new KeyValue(shape.translateYProperty(), endY, theTooMuchBrick ? Interpolator.SPLINE(0, 1, 1, 1) : Interpolator.EASE_IN)) :
                                new KeyFrame(Duration.millis(500), new KeyValue(shape.translateXProperty(), endX));
                timeline.getKeyFrames().add(keyFrame);
                if (!rotation)
                    column = brickMove.column;
            }
            return brickMove != null;
        }
    }

    private static class BrickMove {
        MoveType moveType;
        int sequence;
        double row, column;

        BrickMove(int sequence, double row, double column, MoveType moveType) {
            this.sequence = sequence;
            this.row = row;
            this.column = column;
            this.moveType = moveType;
        }
    }

    enum BrickShape { rectangle, circle }

    enum MoveType { TRANSLATION_LEFT, TRANSLATION_RIGHT, TRANSLATION_BOTTOM, ROTATION }
}
