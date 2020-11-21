package com.android.settings.notification;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.C0003R$array;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class SwipeDirectionPreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
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

    public SwipeDirectionPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((ListPreference) preference).setValue(String.valueOf(Settings.Secure.getInt(this.mContext.getContentResolver(), "notification_dismiss_rtl", 1)));
        super.updateState(preference);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "notification_dismiss_rtl", Integer.valueOf((String) obj).intValue());
        refreshSummary(preference);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i = Settings.Secure.getInt(this.mContext.getContentResolver(), "notification_dismiss_rtl", 1);
        String[] stringArray = this.mContext.getResources().getStringArray(C0003R$array.swipe_direction_values);
        String[] stringArray2 = this.mContext.getResources().getStringArray(C0003R$array.swipe_direction_titles);
        if (stringArray == null) {
            return null;
        }
        for (int i2 = 0; i2 < stringArray.length; i2++) {
            if (i == Integer.parseInt(stringArray[i2])) {
                return stringArray2[i2];
            }
        }
        return null;
    }
}
