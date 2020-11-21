package com.oneplus.settings.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.preference.Preference;
import com.android.settings.C0003R$array;
import com.android.settings.C0006R$color;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.OPColorPickerActivity;
import com.oneplus.settings.utils.OPUtils;
import java.util.HashMap;

public class OPThemeAccentColorPreferenceController extends BasePreferenceController {
    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public OPThemeAccentColorPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i = C0003R$array.op_custom_accent_color_entries;
        int i2 = C0003R$array.op_custom_accent_color_values;
        String[] stringArray = this.mContext.getResources().getStringArray(i);
        TypedArray obtainTypedArray = this.mContext.getResources().obtainTypedArray(i2);
        int length = obtainTypedArray.length();
        HashMap hashMap = new HashMap();
        for (int i3 = 0; i3 < length; i3++) {
            hashMap.put(this.mContext.getString(obtainTypedArray.getResourceId(i3, -1)), stringArray[i3]);
        }
        obtainTypedArray.recycle();
        String string = Settings.System.getString(this.mContext.getContentResolver(), "oneplus_accent_color");
        if (TextUtils.isEmpty(string)) {
            string = this.mContext.getString(C0006R$color.op_control_accent_color_red_default);
        }
        if (TextUtils.isEmpty((CharSequence) hashMap.get(string.toLowerCase()))) {
            return this.mContext.getString(C0017R$string.customization_settings_title);
        }
        return (CharSequence) hashMap.get(string.toLowerCase());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return super.handlePreferenceTreeClick(preference);
        }
        if (OPUtils.isAndroidModeOn(this.mContext.getContentResolver())) {
            Toast.makeText(this.mContext, C0017R$string.oneplus_colorful_mode_cannot_change_color_accent, 0).show();
            return super.handlePreferenceTreeClick(preference);
        }
        Intent intent = new Intent();
        intent.setClass(this.mContext, OPColorPickerActivity.class);
        this.mContext.startActivity(intent);
        return true;
    }
}
