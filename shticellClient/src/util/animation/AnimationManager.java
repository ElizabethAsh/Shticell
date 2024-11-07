package util.animation;

import javafx.animation.*;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class AnimationManager {

    public static void animateLabelHover(Label label) {
        TranslateTransition translateTransition = new TranslateTransition();

        translateTransition.setDuration(Duration.seconds(2));
        translateTransition.setByY(-15);

        translateTransition.setAutoReverse(true);
        translateTransition.setCycleCount(2);

        translateTransition.setNode(label);

        translateTransition.play();
    }

    public static void animateLabelShake(Label label) {
        Timeline timeline = new Timeline();

        KeyValue moveLeft = new KeyValue(label.translateXProperty(), -10);
        KeyFrame frameLeft = new KeyFrame(Duration.millis(100), moveLeft);

        KeyValue moveRight = new KeyValue(label.translateXProperty(), 10);
        KeyFrame frameRight = new KeyFrame(Duration.millis(200), moveRight);

        KeyValue moveBack = new KeyValue(label.translateXProperty(), 0);
        KeyFrame frameBack = new KeyFrame(Duration.millis(300), moveBack);

        timeline.getKeyFrames().addAll(frameLeft, frameRight, frameBack);
        timeline.setCycleCount(2);
        timeline.play();
    }

    public static void animateLabelRotate(Label label) {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), label);

        rotateTransition.setToAngle(360);
        rotateTransition.setCycleCount(1);
        rotateTransition.play();
    }


    public static void animateLabelFade(Label label) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), label);

        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.3);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(2);
        fadeTransition.play();
    }

    public static void animateSelectedCellHighlight(Label selectedCell) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), selectedCell);
        scaleTransition.setByX(1.5);
        scaleTransition.setByY(1.5);

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), selectedCell);
        rotateTransition.setByAngle(360);

        ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, rotateTransition);
        parallelTransition.setCycleCount(1);
        parallelTransition.play();
    }



}
