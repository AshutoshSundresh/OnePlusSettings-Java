package androidx.slice;

import android.annotation.SuppressLint;
import android.app.slice.SliceManager;
import android.app.slice.SliceSpec;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import androidx.collection.ArrayMap;
import androidx.slice.widget.SliceLiveData;
import java.util.Collection;
import java.util.Set;

class SliceViewManagerWrapper extends SliceViewManagerBase {
    private final ArrayMap<String, String> mCachedAuthorities;
    private final ArrayMap<String, Boolean> mCachedSuspendFlags;
    private final SliceManager mManager;
    private final Set<SliceSpec> mSpecs;

    SliceViewManagerWrapper(Context context) {
        this(context, (SliceManager) context.getSystemService(SliceManager.class));
    }

    SliceViewManagerWrapper(Context context, SliceManager sliceManager) {
        super(context);
        this.mCachedSuspendFlags = new ArrayMap<>();
        this.mCachedAuthorities = new ArrayMap<>();
        this.mManager = sliceManager;
        this.mSpecs = SliceConvert.unwrap(SliceLiveData.SUPPORTED_SPECS);
    }

    @Override // androidx.slice.SliceViewManager
    public void pinSlice(Uri uri) {
        try {
            this.mManager.pinSlice(uri, this.mSpecs);
        } catch (RuntimeException e) {
            ContentProviderClient acquireContentProviderClient = this.mContext.getContentResolver().acquireContentProviderClient(uri);
            if (acquireContentProviderClient == null) {
                throw new IllegalArgumentException("No provider found for " + uri);
            }
            acquireContentProviderClient.release();
            throw e;
        }
    }

    @Override // androidx.slice.SliceViewManager
    public void unpinSlice(Uri uri) {
        try {
            this.mManager.unpinSlice(uri);
        } catch (IllegalStateException unused) {
        }
    }

    @Override // androidx.slice.SliceViewManager
    public Slice bindSlice(Uri uri) {
        if (isAuthoritySuspended(uri.getAuthority())) {
            return null;
        }
        return SliceConvert.wrap(this.mManager.bindSlice(uri, this.mSpecs), this.mContext);
    }

    @Override // androidx.slice.SliceViewManager
    public Slice bindSlice(Intent intent) {
        if (isPackageSuspended(intent)) {
            return null;
        }
        return SliceConvert.wrap(this.mManager.bindSlice(intent, this.mSpecs), this.mContext);
    }

    private boolean isPackageSuspended(Intent intent) {
        if (intent.getComponent() != null) {
            return isPackageSuspended(intent.getComponent().getPackageName());
        }
        if (intent.getPackage() != null) {
            return isPackageSuspended(intent.getPackage());
        }
        if (intent.getData() != null) {
            return isAuthoritySuspended(intent.getData().getAuthority());
        }
        return false;
    }

    private boolean isAuthoritySuspended(String str) {
        String str2 = this.mCachedAuthorities.get(str);
        if (str2 == null) {
            ProviderInfo resolveContentProvider = this.mContext.getPackageManager().resolveContentProvider(str, 0);
            if (resolveContentProvider == null) {
                return false;
            }
            str2 = resolveContentProvider.packageName;
            this.mCachedAuthorities.put(str, str2);
        }
        return isPackageSuspended(str2);
    }

    private boolean isPackageSuspended(String str) {
        Boolean bool = this.mCachedSuspendFlags.get(str);
        if (bool == null) {
            try {
                Boolean valueOf = Boolean.valueOf((this.mContext.getPackageManager().getApplicationInfo(str, 0).flags & 1073741824) != 0);
                this.mCachedSuspendFlags.put(str, valueOf);
                bool = valueOf;
            } catch (PackageManager.NameNotFoundException unused) {
                return false;
            }
        }
        return bool.booleanValue();
    }

    @Override // androidx.slice.SliceViewManager
    @SuppressLint({"WrongThread"})
    public Collection<Uri> getSliceDescendants(Uri uri) {
        try {
            return this.mManager.getSliceDescendants(uri);
        } catch (RuntimeException e) {
            ContentProviderClient acquireContentProviderClient = this.mContext.getContentResolver().acquireContentProviderClient(uri);
            if (acquireContentProviderClient == null) {
                throw new IllegalArgumentException("No provider found for " + uri);
            }
            acquireContentProviderClient.release();
            throw e;
        }
    }
}
