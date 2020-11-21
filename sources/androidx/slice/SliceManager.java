package androidx.slice;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import java.util.List;
import java.util.Set;

public abstract class SliceManager {
    public abstract List<Uri> getPinnedSlices();

    public abstract Set<SliceSpec> getPinnedSpecs(Uri uri);

    public static SliceManager getInstance(Context context) {
        if (Build.VERSION.SDK_INT >= 28) {
            return new SliceManagerWrapper(context);
        }
        return new SliceManagerCompat(context);
    }

    SliceManager() {
    }
}
