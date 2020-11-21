package com.oneplus.settings.others;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.OpFeatures;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.MasterSwitchController;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;

public class OPServiceAppUpdatePreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, SwitchWidgetController.OnSwitchChangeListener {
    private static final String APPUPGRADER_SWITCH_ACTION = "com.oneplus.appupgrader.action.SWITCH_CHANGED";
    private static final String APPUPGRADER_SWITCH_ACTION_PERMISSION = "com.android.settings.RECEIVE_PERMISSION";
    private static final String KEY_BUILT_IN_APP_UPDATES = "built_in_app_updates";
    private static final int KEY_BUILT_IN_APP_UPDATES_TYPE = 15;
    private static final String KEY_FROM_SETTINGS = "key_from_settings";
    private static final String KEY_NOTICES_TYPE = "op_legal_notices_type";
    private static final String OPLEGAL_NOTICES_ACTION = "android.oem.intent.action.OP_LEGAL";
    private MasterSwitchPreference mSwitch;
    private MasterSwitchController mSwitchController;

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

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY_BUILT_IN_APP_UPDATES;
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

    public OPServiceAppUpdatePreferenceController(Context context) {
        super(context, KEY_BUILT_IN_APP_UPDATES);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitch = (MasterSwitchPreference) preferenceScreen.findPreference(KEY_BUILT_IN_APP_UPDATES);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (OpFeatures.isSupport(new int[]{0})) {
            return 2;
        }
        if (!ProductUtils.isUsvMode()) {
            return OPUtils.isAppExist(this.mContext, "com.oneplus.appupgrader") ? 0 : 2;
        }
        return 4;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable()) {
            try {
                MasterSwitchPreference masterSwitchPreference = this.mSwitch;
                boolean z = true;
                if (Settings.System.getInt(this.mContext.getContentResolver(), "oem_app_updates_enable") != 1) {
                    z = false;
                }
                masterSwitchPreference.setChecked(z);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            MasterSwitchPreference masterSwitchPreference2 = this.mSwitch;
            if (masterSwitchPreference2 != null) {
                MasterSwitchController masterSwitchController = new MasterSwitchController(masterSwitchPreference2);
                this.mSwitchController = masterSwitchController;
                masterSwitchController.setListener(this);
                this.mSwitchController.startListening();
            }
        }
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        Settings.System.putInt(this.mContext.getContentResolver(), "oem_app_updates_enable", z ? 1 : 0);
        OPUtils.sendAnalytics("opush_switch", "click", z ? "1" : "0");
        Intent intent = new Intent(APPUPGRADER_SWITCH_ACTION);
        intent.setPackage("com.oneplus.appupgrader");
        intent.putExtra("oem_app_updates_enable", z);
        intent.addFlags(268435456);
        this.mContext.sendBroadcast(intent, APPUPGRADER_SWITCH_ACTION_PERMISSION);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_BUILT_IN_APP_UPDATES.equals(preference.getKey())) {
            return false;
        }
        Intent intent = new Intent(OPLEGAL_NOTICES_ACTION);
        intent.putExtra(KEY_NOTICES_TYPE, 15);
        intent.putExtra(KEY_FROM_SETTINGS, true);
        this.mContext.startActivity(intent);
        return true;
    }
}
