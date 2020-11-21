package com.android.settingslib.drawer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Comparator;

public abstract class Tile implements Parcelable {
    public static final Parcelable.Creator<Tile> CREATOR = new Parcelable.Creator<Tile>() {
        /* class com.android.settingslib.drawer.Tile.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public Tile createFromParcel(Parcel parcel) {
            boolean readBoolean = parcel.readBoolean();
            parcel.setDataPosition(0);
            return readBoolean ? new ProviderTile(parcel) : new ActivityTile(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public Tile[] newArray(int i) {
            return new Tile[i];
        }
    };
    public static final Comparator<Tile> TILE_COMPARATOR = $$Lambda$Tile$5_ETnVHzVG6DF0RKPoy76eRIQM.INSTANCE;
    private String mCategory;
    protected ComponentInfo mComponentInfo;
    private final String mComponentName;
    private final String mComponentPackage;
    private final Intent mIntent;
    long mLastUpdateTime;
    private Bundle mMetaData;
    private CharSequence mSummaryOverride;
    public ArrayList<UserHandle> userHandle = new ArrayList<>();

    public int describeContents() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public abstract int getComponentIcon(ComponentInfo componentInfo);

    /* access modifiers changed from: protected */
    public abstract ComponentInfo getComponentInfo(Context context);

    /* access modifiers changed from: protected */
    public abstract CharSequence getComponentLabel(Context context);

    public abstract String getDescription();

    public Tile(ComponentInfo componentInfo, String str) {
        this.mComponentInfo = componentInfo;
        this.mComponentPackage = componentInfo.packageName;
        this.mComponentName = componentInfo.name;
        this.mCategory = str;
        this.mIntent = new Intent().setClassName(this.mComponentPackage, this.mComponentName);
    }

    Tile(Parcel parcel) {
        parcel.readBoolean();
        this.mComponentPackage = parcel.readString();
        this.mComponentName = parcel.readString();
        this.mIntent = new Intent().setClassName(this.mComponentPackage, this.mComponentName);
        int readInt = parcel.readInt();
        for (int i = 0; i < readInt; i++) {
            this.userHandle.add((UserHandle) UserHandle.CREATOR.createFromParcel(parcel));
        }
        this.mCategory = parcel.readString();
        this.mMetaData = parcel.readBundle();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBoolean(this instanceof ProviderTile);
        parcel.writeString(this.mComponentPackage);
        parcel.writeString(this.mComponentName);
        int size = this.userHandle.size();
        parcel.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            this.userHandle.get(i2).writeToParcel(parcel, i);
        }
        parcel.writeString(this.mCategory);
        parcel.writeBundle(this.mMetaData);
    }

    public String getPackageName() {
        return this.mComponentPackage;
    }

    public String getComponentName() {
        return this.mComponentName;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public String getCategory() {
        return this.mCategory;
    }

    public void setCategory(String str) {
        this.mCategory = str;
    }

    public int getOrder() {
        if (hasOrder()) {
            return this.mMetaData.getInt("com.android.settings.order");
        }
        return 0;
    }

    public boolean hasOrder() {
        return this.mMetaData.containsKey("com.android.settings.order") && (this.mMetaData.get("com.android.settings.order") instanceof Integer);
    }

    public boolean hasSwitch() {
        Bundle bundle = this.mMetaData;
        return bundle != null && bundle.containsKey("com.android.settings.switch_uri");
    }

    public CharSequence getTitle(Context context) {
        ensureMetadataNotStale(context);
        PackageManager packageManager = context.getPackageManager();
        String str = null;
        if (this.mMetaData.containsKey("com.android.settings.title")) {
            if (this.mMetaData.containsKey("com.android.settings.title_uri")) {
                return null;
            }
            if (this.mMetaData.get("com.android.settings.title") instanceof Integer) {
                try {
                    str = packageManager.getResourcesForApplication(this.mComponentPackage).getString(this.mMetaData.getInt("com.android.settings.title"));
                } catch (PackageManager.NameNotFoundException | Resources.NotFoundException e) {
                    Log.w("Tile", "Couldn't find info", e);
                }
            } else {
                str = this.mMetaData.getString("com.android.settings.title");
            }
        }
        return str == null ? getComponentLabel(context) : str;
    }

    public CharSequence getSummary(Context context) {
        CharSequence charSequence = this.mSummaryOverride;
        if (charSequence != null) {
            return charSequence;
        }
        ensureMetadataNotStale(context);
        PackageManager packageManager = context.getPackageManager();
        Bundle bundle = this.mMetaData;
        if (bundle == null || bundle.containsKey("com.android.settings.summary_uri") || !this.mMetaData.containsKey("com.android.settings.summary")) {
            return null;
        }
        if (!(this.mMetaData.get("com.android.settings.summary") instanceof Integer)) {
            return this.mMetaData.getString("com.android.settings.summary");
        }
        try {
            return packageManager.getResourcesForApplication(this.mComponentPackage).getString(this.mMetaData.getInt("com.android.settings.summary"));
        } catch (PackageManager.NameNotFoundException | Resources.NotFoundException e) {
            Log.d("Tile", "Couldn't find info", e);
            return null;
        }
    }

    public void setMetaData(Bundle bundle) {
        this.mMetaData = bundle;
    }

    public Bundle getMetaData() {
        return this.mMetaData;
    }

    public String getKey(Context context) {
        if (!hasKey()) {
            return null;
        }
        ensureMetadataNotStale(context);
        if (this.mMetaData.get("com.android.settings.keyhint") instanceof Integer) {
            return context.getResources().getString(this.mMetaData.getInt("com.android.settings.keyhint"));
        }
        return this.mMetaData.getString("com.android.settings.keyhint");
    }

    public boolean hasKey() {
        Bundle bundle = this.mMetaData;
        return bundle != null && bundle.containsKey("com.android.settings.keyhint");
    }

    public Icon getIcon(Context context) {
        Icon icon = null;
        if (!(context == null || this.mMetaData == null)) {
            ensureMetadataNotStale(context);
            ComponentInfo componentInfo = getComponentInfo(context);
            if (componentInfo == null) {
                Log.w("Tile", "Cannot find ComponentInfo for " + getDescription());
                return null;
            }
            int i = this.mMetaData.getInt("com.android.settings.icon");
            if (i == 0 && !this.mMetaData.containsKey("com.android.settings.icon_uri")) {
                i = getComponentIcon(componentInfo);
            }
            if (i != 0) {
                icon = Icon.createWithResource(componentInfo.packageName, i);
                if (isIconTintable(context)) {
                    TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{16843817});
                    int color = obtainStyledAttributes.getColor(0, 0);
                    obtainStyledAttributes.recycle();
                    icon.setTint(color);
                }
            }
        }
        return icon;
    }

    public boolean isIconTintable(Context context) {
        ensureMetadataNotStale(context);
        Bundle bundle = this.mMetaData;
        if (bundle == null || !bundle.containsKey("com.android.settings.icon_tintable")) {
            return false;
        }
        return this.mMetaData.getBoolean("com.android.settings.icon_tintable");
    }

    private void ensureMetadataNotStale(Context context) {
        try {
            long j = context.getApplicationContext().getPackageManager().getPackageInfo(this.mComponentPackage, 128).lastUpdateTime;
            if (j != this.mLastUpdateTime) {
                this.mComponentInfo = null;
                getComponentInfo(context);
                this.mLastUpdateTime = j;
            }
        } catch (PackageManager.NameNotFoundException unused) {
            Log.d("Tile", "Can't find package, probably uninstalled.");
        }
    }

    public boolean isPrimaryProfileOnly() {
        Bundle bundle = this.mMetaData;
        String str = "all_profiles";
        String string = bundle != null ? bundle.getString("com.android.settings.profile") : str;
        if (string != null) {
            str = string;
        }
        return TextUtils.equals(str, "primary_profile_only");
    }

    static /* synthetic */ int lambda$static$0(Tile tile, Tile tile2) {
        return tile2.getOrder() - tile.getOrder();
    }
}
