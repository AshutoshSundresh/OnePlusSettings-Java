package com.android.settings.display;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.display.ColorDisplayManager;
import android.location.LocationManager;
import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class NightDisplayAutoModePreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    private ColorDisplayManager mColorDisplayManager;
    private final LocationManager mLocationManager;
    private DropDownPreference mPreference;

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

    public NightDisplayAutoModePreferenceController(Context context, String str) {
        super(context, str);
        this.mColorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
        this.mLocationManager = (LocationManager) context.getSystemService(LocationManager.class);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return ColorDisplayManager.isNightDisplayAvailable(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        DropDownPreference dropDownPreference = (DropDownPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = dropDownPreference;
        dropDownPreference.setEntries(new CharSequence[]{this.mContext.getString(C0017R$string.night_display_auto_mode_never), this.mContext.getString(C0017R$string.night_display_auto_mode_custom), this.mContext.getString(C0017R$string.night_display_auto_mode_twilight)});
        this.mPreference.setEntryValues(new CharSequence[]{String.valueOf(0), String.valueOf(1), String.valueOf(2)});
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public final void updateState(Preference preference) {
        this.mPreference.setValue(String.valueOf(this.mColorDisplayManager.getNightDisplayAutoMode()));
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public final boolean onPreferenceChange(Preference preference, Object obj) {
        if (!String.valueOf(2).equals(obj) || this.mLocationManager.isLocationEnabled()) {
            return this.mColorDisplayManager.setNightDisplayAutoMode(Integer.parseInt((String) obj));
        }
        TwilightLocationDialog.show(this.mContext);
        return true;
    }
}
