package androidx.lifecycle;

import android.view.View;
import androidx.lifecycle.runtime.R$id;

public class ViewTreeLifecycleOwner {
    public static void set(View view, LifecycleOwner lifecycleOwner) {
        view.setTag(R$id.view_tree_lifecycle_owner, lifecycleOwner);
    }
}
