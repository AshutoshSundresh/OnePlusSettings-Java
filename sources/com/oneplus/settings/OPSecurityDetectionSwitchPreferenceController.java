package com.oneplus.settings;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.MasterSwitchController;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.oneplus.settings.utils.OPUtils;

public class OPSecurityDetectionSwitchPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, SwitchWidgetController.OnSwitchChangeListener {
    private static final String EVENT_TRACKER = "sec_recommend";
    private static final String KEY_OP_APP_SECURITY_RECOMMEND = "op_app_security_recommend_setting";
    private static final String KEY_SECURITY_DETECTION = "security_detection";
    public static final String PREF_KEY_OP_APP_SECURITY_RECOMMEND = "op_app_security_recommend";
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
        return KEY_SECURITY_DETECTION;
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

    public OPSecurityDetectionSwitchPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_SECURITY_DETECTION);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitch = (MasterSwitchPreference) preferenceScreen.findPreference(KEY_SECURITY_DETECTION);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return OPUtils.isO2() || !OPUtils.isSupportAppSecureRecommd() ? 4 : 0;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable() && this.mSwitch != null) {
            boolean z = true;
            int intForUser = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), KEY_OP_APP_SECURITY_RECOMMEND, 1, -2);
            MasterSwitchPreference masterSwitchPreference = this.mSwitch;
            if (intForUser != 1) {
                z = false;
            }
            masterSwitchPreference.setChecked(z);
            MasterSwitchController masterSwitchController = new MasterSwitchController(this.mSwitch);
            this.mSwitchController = masterSwitchController;
            masterSwitchController.setListener(this);
            this.mSwitchController.startListening();
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: boolean */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), KEY_OP_APP_SECURITY_RECOMMEND, z ? 1 : 0, -2);
        OPUtils.sendAppTracker(EVENT_TRACKER, (int) z);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_SECURITY_DETECTION.equals(preference.getKey())) {
            return false;
        }
        Intent intent = new Intent();
        intent.setAction("com.oneplus.action.APP_SECURITY_RECOMMEND_SETTINGS");
        this.mContext.startActivity(intent);
        return true;
    }
}
