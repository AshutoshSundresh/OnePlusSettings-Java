package com.android.settings.vpn2;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import androidx.preference.Preference;
import com.android.internal.net.VpnConfig;

public class AppPreference extends ManageablePreference {
    public static final int STATE_DISCONNECTED = ManageablePreference.STATE_NONE;
    private final String mName;
    private final String mPackageName;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public AppPreference(Context context, int i, String str) {
        super(context, null);
        Drawable drawable = null;
        super.setUserId(i);
        this.mPackageName = str;
        try {
            Context userContext = getUserContext();
            PackageManager packageManager = userContext.getPackageManager();
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(this.mPackageName, 0);
                if (packageInfo != null) {
                    drawable = packageInfo.applicationInfo.loadIcon(packageManager);
                    str = VpnConfig.getVpnLabel(userContext, this.mPackageName).toString();
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
            if (drawable == null) {
                drawable = packageManager.getDefaultActivityIcon();
            }
        } catch (PackageManager.NameNotFoundException unused2) {
        }
        this.mName = str;
        setTitle(str);
        setIcon(drawable);
    }

    public PackageInfo getPackageInfo() {
        try {
            return getUserContext().getPackageManager().getPackageInfo(this.mPackageName, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    public String getLabel() {
        return this.mName;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    private Context getUserContext() throws PackageManager.NameNotFoundException {
        return getContext().createPackageContextAsUser(getContext().getPackageName(), 0, UserHandle.of(this.mUserId));
    }

    @Override // androidx.preference.Preference
    public int compareTo(Preference preference) {
        if (preference instanceof AppPreference) {
            AppPreference appPreference = (AppPreference) preference;
            int i = appPreference.mState - this.mState;
            if (i != 0) {
                return i;
            }
            int compareToIgnoreCase = this.mName.compareToIgnoreCase(appPreference.mName);
            if (compareToIgnoreCase != 0) {
                return compareToIgnoreCase;
            }
            int compareTo = this.mPackageName.compareTo(appPreference.mPackageName);
            return compareTo == 0 ? this.mUserId - appPreference.mUserId : compareTo;
        } else if (preference instanceof LegacyVpnPreference) {
            return -((LegacyVpnPreference) preference).compareTo((Preference) this);
        } else {
            return super.compareTo(preference);
        }
    }
}
