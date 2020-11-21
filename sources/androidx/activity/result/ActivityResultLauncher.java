package androidx.activity.result;

import android.annotation.SuppressLint;
import androidx.core.app.ActivityOptionsCompat;

public abstract class ActivityResultLauncher<I> {
    public abstract void launch(@SuppressLint({"UnknownNullness"}) I i, ActivityOptionsCompat activityOptionsCompat);

    public abstract void unregister();

    public void launch(@SuppressLint({"UnknownNullness"}) I i) {
        launch(i, null);
    }
}
