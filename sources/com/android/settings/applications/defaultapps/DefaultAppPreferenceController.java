package com.android.settings.applications.defaultapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.GearPreference;
import com.android.settingslib.TwoTargetPreference;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.core.AbstractPreferenceController;

public abstract class DefaultAppPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    protected final PackageManager mPackageManager;
    protected int mUserId = UserHandle.myUserId();
    protected final UserManager mUserManager;

    /* access modifiers changed from: protected */
    public abstract DefaultAppInfo getDefaultAppInfo();

    /* access modifiers changed from: protected */
    public Intent getSettingIntent(DefaultAppInfo defaultAppInfo) {
        return null;
    }

    public DefaultAppPreferenceController(Context context) {
        super(context);
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = (UserManager) context.getSystemService("user");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        DefaultAppInfo defaultAppInfo = getDefaultAppInfo();
        CharSequence defaultAppLabel = getDefaultAppLabel();
        if (preference instanceof TwoTargetPreference) {
            ((TwoTargetPreference) preference).setIconSize(1);
        }
        if (!TextUtils.isEmpty(defaultAppLabel)) {
            preference.setSummary(defaultAppLabel);
            Utils.setSafeIcon(preference, getDefaultAppIcon());
        } else {
            Log.d("DefaultAppPrefControl", "No default app");
            preference.setSummary(C0017R$string.app_list_preference_none);
            preference.setIcon((Drawable) null);
        }
        mayUpdateGearIcon(defaultAppInfo, preference);
    }

    private void mayUpdateGearIcon(DefaultAppInfo defaultAppInfo, Preference preference) {
        if (preference instanceof GearPreference) {
            Intent settingIntent = getSettingIntent(defaultAppInfo);
            if (settingIntent != null) {
                ((GearPreference) preference).setOnGearClickListener(new GearPreference.OnGearClickListener(settingIntent) {
                    /* class com.android.settings.applications.defaultapps.$$Lambda$DefaultAppPreferenceController$P93yGe3NhKzPqeqQwHkMaXpVB1M */
                    public final /* synthetic */ Intent f$1;

                    {
                        this.f$1 = r2;
                    }

                    @Override // com.android.settings.widget.GearPreference.OnGearClickListener
                    public final void onGearClick(GearPreference gearPreference) {
                        DefaultAppPreferenceController.this.lambda$mayUpdateGearIcon$0$DefaultAppPreferenceController(this.f$1, gearPreference);
                    }
                });
            } else {
                ((GearPreference) preference).setOnGearClickListener(null);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$mayUpdateGearIcon$0 */
    public /* synthetic */ void lambda$mayUpdateGearIcon$0$DefaultAppPreferenceController(Intent intent, GearPreference gearPreference) {
        startActivity(intent);
    }

    /* access modifiers changed from: protected */
    public void startActivity(Intent intent) {
        this.mContext.startActivity(intent);
    }

    public Drawable getDefaultAppIcon() {
        DefaultAppInfo defaultAppInfo;
        if (isAvailable() && (defaultAppInfo = getDefaultAppInfo()) != null) {
            return defaultAppInfo.loadIcon();
        }
        return null;
    }

    public CharSequence getDefaultAppLabel() {
        DefaultAppInfo defaultAppInfo;
        if (isAvailable() && (defaultAppInfo = getDefaultAppInfo()) != null) {
            return defaultAppInfo.loadLabel();
        }
        return null;
    }
}
