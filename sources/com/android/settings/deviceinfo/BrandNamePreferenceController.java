package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.utils.OPUtils;

public class BrandNamePreferenceController extends BasePreferenceController {
    public static final String KEY_BRAND_NAME = "brand_name";
    private boolean isNeedEnable = false;
    private Context mContext;

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

    public BrandNamePreferenceController(Context context) {
        super(context, KEY_BRAND_NAME);
        this.mContext = context;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return OPUtils.isSupportUss() ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(KEY_BRAND_NAME);
        if (findPreference != null) {
            findPreference.setSummary(getBrandName());
            findPreference.setEnabled(this.isNeedEnable);
        }
    }

    private String getBrandName() {
        Context context = this.mContext;
        if (context != null) {
            String simOperatorName = ((TelephonyManager) context.getSystemService("phone")).getSimOperatorName();
            if (TextUtils.isEmpty(simOperatorName)) {
                String string = this.mContext.getString(C0017R$string.device_info_not_available);
                this.isNeedEnable = false;
                return string;
            }
            this.isNeedEnable = true;
            return simOperatorName;
        }
        this.isNeedEnable = false;
        return "";
    }
}
