package androidx.slice;

import android.app.slice.SliceManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.os.UserHandle;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

class SliceManagerWrapper extends SliceManager {
    private final SliceManager mManager;

    SliceManagerWrapper(Context context) {
        this((SliceManager) context.getSystemService(SliceManager.class));
    }

    SliceManagerWrapper(SliceManager sliceManager) {
        this.mManager = sliceManager;
    }

    @Override // androidx.slice.SliceManager
    public Set<SliceSpec> getPinnedSpecs(Uri uri) {
        if (Build.VERSION.SDK_INT == 28) {
            uri = maybeAddCurrentUserId(uri);
        }
        return SliceConvert.wrap(this.mManager.getPinnedSpecs(uri));
    }

    @Override // androidx.slice.SliceManager
    public List<Uri> getPinnedSlices() {
        return this.mManager.getPinnedSlices();
    }

    private Uri maybeAddCurrentUserId(Uri uri) {
        if (uri == null || uri.getAuthority().contains("@")) {
            return uri;
        }
        String authority = uri.getAuthority();
        Uri.Builder buildUpon = uri.buildUpon();
        return buildUpon.encodedAuthority(getCurrentUserId() + "@" + authority).build();
    }

    private int getCurrentUserId() {
        UserHandle myUserHandle = Process.myUserHandle();
        try {
            return ((Integer) myUserHandle.getClass().getDeclaredMethod("getIdentifier", new Class[0]).invoke(myUserHandle, new Object[0])).intValue();
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException unused) {
            return 0;
        }
    }
}
