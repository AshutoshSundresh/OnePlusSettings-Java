package com.android.settings.accessibility;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.List;

public class RTTSettingPreferenceController extends BasePreferenceController {
    private static final String DIALER_RTT_CONFIGURATION = "dialer_rtt_configuration";
    private final Context mContext;
    private final String mDialerPackage = this.mContext.getString(C0017R$string.config_rtt_setting_package_name);
    private final CharSequence[] mModes;
    private final PackageManager mPackageManager;
    Intent mRTTIntent;
    private final TelecomManager mTelecomManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public RTTSettingPreferenceController(Context context, String str) {
        super(context, str);
        this.mContext = context;
        this.mModes = context.getResources().getTextArray(C0003R$array.rtt_setting_mode);
        this.mPackageManager = context.getPackageManager();
        this.mTelecomManager = (TelecomManager) context.getSystemService(TelecomManager.class);
        this.mRTTIntent = new Intent(context.getString(C0017R$string.config_rtt_setting_intent_action));
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        List<ResolveInfo> queryIntentActivities = this.mPackageManager.queryIntentActivities(this.mRTTIntent, 0);
        if (queryIntentActivities == null || queryIntentActivities.isEmpty() || !isDialerSupportRTTSetting()) {
            return 3;
        }
        return 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        preferenceScreen.findPreference(getPreferenceKey()).setIntent(this.mRTTIntent);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mModes[Settings.Secure.getInt(this.mContext.getContentResolver(), DIALER_RTT_CONFIGURATION, 1)];
    }

    /* access modifiers changed from: package-private */
    public boolean isDialerSupportRTTSetting() {
        return TextUtils.equals(this.mTelecomManager.getDefaultDialerPackage(), this.mDialerPackage);
    }
}
