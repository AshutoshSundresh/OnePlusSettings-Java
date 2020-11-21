package androidx.leanback.app;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;

class FragmentUtil {
    static Context getContext(Fragment fragment) {
        if (Build.VERSION.SDK_INT >= 23) {
            return fragment.getContext();
        }
        return fragment.getActivity();
    }
}
