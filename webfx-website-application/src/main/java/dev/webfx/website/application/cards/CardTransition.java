package dev.webfx.website.application.cards;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;

/**
 * @author Bruno Salmon
 */
public final class CardTransition {

    private long durationMillis = 1000;
    private final List<KeyValue> keyValues = new ArrayList<>();
    private final List<Runnable> onFinisheds = new ArrayList<>();
    private Timeline timeline;

    public void setDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
    }

    void addKeyValue(KeyValue... keyValues) {
        this.keyValues.addAll(Arrays.asList(keyValues));
    }

    void addOnFinished(Runnable... onFinisheds) {
        this.onFinisheds.addAll(Arrays.asList(onFinisheds));
    }

    void run(boolean animate) {
        if (!animate) {
            for (KeyValue kv : keyValues)
                ((WritableValue<Object>) kv.getTarget()).setValue(kv.getEndValue());
            runOnFinisheds();
        } else {
            timeline = new Timeline(new KeyFrame(new Duration(durationMillis), toArray(keyValues, KeyValue[]::new)));
            timeline.setOnFinished(e -> runOnFinisheds());
            timeline.play();
        }
    }

    void stop() {
        if (timeline != null)
            timeline.stop();
    }

    private void runOnFinisheds() {
        keyValues.clear();
        for (Runnable onFinished : onFinisheds)
            onFinished.run();
    }

    // for GWT
    private static <T> T[] toArray(Collection<T> collection, IntFunction<T[]> arrayGenerator) {
        return collection.toArray(arrayGenerator.apply(collection.size()));
    }

}
