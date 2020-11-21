package androidx.constraintlayout.motion.widget;

import android.util.Pair;
import java.util.HashMap;

public class DesignTool {
    static final HashMap<Pair<Integer, Integer>, String> allAttributes = new HashMap<>();
    static final HashMap<String, String> allMargins = new HashMap<>();

    public DesignTool(MotionLayout motionLayout) {
    }

    static {
        allAttributes.put(Pair.create(4, 4), "layout_constraintBottom_toBottomOf");
        allAttributes.put(Pair.create(4, 3), "layout_constraintBottom_toTopOf");
        allAttributes.put(Pair.create(3, 4), "layout_constraintTop_toBottomOf");
        allAttributes.put(Pair.create(3, 3), "layout_constraintTop_toTopOf");
        allAttributes.put(Pair.create(6, 6), "layout_constraintStart_toStartOf");
        allAttributes.put(Pair.create(6, 7), "layout_constraintStart_toEndOf");
        allAttributes.put(Pair.create(7, 6), "layout_constraintEnd_toStartOf");
        allAttributes.put(Pair.create(7, 7), "layout_constraintEnd_toEndOf");
        allAttributes.put(Pair.create(1, 1), "layout_constraintLeft_toLeftOf");
        allAttributes.put(Pair.create(1, 2), "layout_constraintLeft_toRightOf");
        allAttributes.put(Pair.create(2, 2), "layout_constraintRight_toRightOf");
        allAttributes.put(Pair.create(2, 1), "layout_constraintRight_toLeftOf");
        allAttributes.put(Pair.create(5, 5), "layout_constraintBaseline_toBaselineOf");
        allMargins.put("layout_constraintBottom_toBottomOf", "layout_marginBottom");
        allMargins.put("layout_constraintBottom_toTopOf", "layout_marginBottom");
        allMargins.put("layout_constraintTop_toBottomOf", "layout_marginTop");
        allMargins.put("layout_constraintTop_toTopOf", "layout_marginTop");
        allMargins.put("layout_constraintStart_toStartOf", "layout_marginStart");
        allMargins.put("layout_constraintStart_toEndOf", "layout_marginStart");
        allMargins.put("layout_constraintEnd_toStartOf", "layout_marginEnd");
        allMargins.put("layout_constraintEnd_toEndOf", "layout_marginEnd");
        allMargins.put("layout_constraintLeft_toLeftOf", "layout_marginLeft");
        allMargins.put("layout_constraintLeft_toRightOf", "layout_marginLeft");
        allMargins.put("layout_constraintRight_toRightOf", "layout_marginRight");
        allMargins.put("layout_constraintRight_toLeftOf", "layout_marginRight");
    }
}
