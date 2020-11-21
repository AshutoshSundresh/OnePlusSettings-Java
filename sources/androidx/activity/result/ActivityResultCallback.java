package androidx.activity.result;

import android.annotation.SuppressLint;

public interface ActivityResultCallback<O> {
    void onActivityResult(@SuppressLint({"UnknownNullness"}) O o);
}
