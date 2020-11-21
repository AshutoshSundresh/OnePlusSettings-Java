package androidx.slice;

import android.content.Context;
import android.net.Uri;
import androidx.slice.compat.SliceProviderCompat;
import java.util.List;
import java.util.Set;

class SliceManagerCompat extends SliceManager {
    private final Context mContext;

    SliceManagerCompat(Context context) {
        this.mContext = context;
    }

    @Override // androidx.slice.SliceManager
    public Set<SliceSpec> getPinnedSpecs(Uri uri) {
        return SliceProviderCompat.getPinnedSpecs(this.mContext, uri);
    }

    @Override // androidx.slice.SliceManager
    public List<Uri> getPinnedSlices() {
        return SliceProviderCompat.getPinnedSlices(this.mContext);
    }
}
