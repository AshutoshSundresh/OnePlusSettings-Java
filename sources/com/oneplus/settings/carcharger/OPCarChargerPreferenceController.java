package com.oneplus.settings.carcharger;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.utils.OPUtils;

public class OPCarChargerPreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    private static final String KEY_AUTO_TURN_ON_CAR_CHARGER = "car_charger_auto_turn_on";
    private static final String KEY_AUTO_TURN_ON_DND = "car_charger_auto_turn_on_dnd";
    private String KEY;

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

    public OPCarChargerPreferenceController(Context context, String str) {
        super(context, str);
        this.KEY = str;
    }

    /* JADX WARN: Type inference failed for: r4v1, types: [int, boolean] */
    /* JADX WARNING: Unknown variable types count: 1 */
    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean onPreferenceChange(androidx.preference.Preference r4, java.lang.Object r5) {
        /*
            r3 = this;
            java.lang.Boolean r5 = (java.lang.Boolean) r5
            boolean r4 = r5.booleanValue()
            java.lang.String r5 = r3.KEY
            java.lang.String r0 = "car_charger_auto_turn_on_dnd"
            boolean r5 = r5.equals(r0)
            java.lang.String r1 = "on"
            java.lang.String r2 = "off"
            if (r5 == 0) goto L_0x0027
            android.content.Context r3 = r3.mContext
            android.content.ContentResolver r3 = r3.getContentResolver()
            android.provider.Settings.System.putInt(r3, r0, r4)
            if (r4 == 0) goto L_0x0020
            goto L_0x0021
        L_0x0020:
            r1 = r2
        L_0x0021:
            java.lang.String r3 = "charge_dnd"
            com.oneplus.settings.utils.OPUtils.sendAppTracker(r3, r1)
            goto L_0x0043
        L_0x0027:
            java.lang.String r5 = r3.KEY
            java.lang.String r0 = "car_charger_auto_turn_on"
            boolean r5 = r5.equals(r0)
            if (r5 == 0) goto L_0x0043
            android.content.Context r3 = r3.mContext
            android.content.ContentResolver r3 = r3.getContentResolver()
            android.provider.Settings.System.putInt(r3, r0, r4)
            if (r4 == 0) goto L_0x003d
            goto L_0x003e
        L_0x003d:
            r1 = r2
        L_0x003e:
            java.lang.String r3 = "charge_carmode"
            com.oneplus.settings.utils.OPUtils.sendAppTracker(r3, r1)
        L_0x0043:
            r3 = 1
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.carcharger.OPCarChargerPreferenceController.onPreferenceChange(androidx.preference.Preference, java.lang.Object):boolean");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        boolean z = true;
        if (this.KEY.equals(KEY_AUTO_TURN_ON_DND)) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            if (Settings.System.getInt(this.mContext.getContentResolver(), KEY_AUTO_TURN_ON_DND, 0) != 1) {
                z = false;
            }
            switchPreference.setChecked(z);
        } else if (this.KEY.equals(KEY_AUTO_TURN_ON_CAR_CHARGER)) {
            SwitchPreference switchPreference2 = (SwitchPreference) preference;
            if (Settings.System.getInt(this.mContext.getContentResolver(), KEY_AUTO_TURN_ON_CAR_CHARGER, 0) != 1) {
                z = false;
            }
            switchPreference2.setChecked(z);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (OPUtils.isO2() && this.KEY.equals(KEY_AUTO_TURN_ON_DND)) {
            return 0;
        }
        if (OPUtils.isO2() || !this.KEY.equals(KEY_AUTO_TURN_ON_CAR_CHARGER)) {
            return 2;
        }
        return 0;
    }
}
