package com.oneplus.settings.network;

import android.content.ActivityNotFoundException;
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

public class OPDualChannelDownloadAccelerationPreferenceController extends BasePreferenceController implements LifecycleObserver, SwitchWidgetController.OnSwitchChangeListener, OnResume {
    private static final String DUAL_CHANNEL_DOWNLOAD_ACCELERATION = "download_smart_link_aggregation";
    private static final String KEY_DUAL_CHANNEL_DOWNLOAD_ACCELERATION = "dual_channel_download_acceleration";
    private MasterSwitchPreference mSwitch;
    private MasterSwitchController mSwitchController;

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

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY_DUAL_CHANNEL_DOWNLOAD_ACCELERATION;
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

    public OPDualChannelDownloadAccelerationPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, KEY_DUAL_CHANNEL_DOWNLOAD_ACCELERATION);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitch = (MasterSwitchPreference) preferenceScreen.findPreference(KEY_DUAL_CHANNEL_DOWNLOAD_ACCELERATION);
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        Settings.System.putInt(this.mContext.getContentResolver(), DUAL_CHANNEL_DOWNLOAD_ACCELERATION, z ? 1 : 0);
        OPUtils.sendAnalytics("dl_speedup", "state", z ? "on" : "off");
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_DUAL_CHANNEL_DOWNLOAD_ACCELERATION.equals(preference.getKey())) {
            return false;
        }
        try {
            this.mContext.startActivity(new Intent("oneplus.intent.action.ONEPLUS_DUAL_CHANNEL_DOWNLOAD_ACCELERATION_ACTION"));
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable() && this.mSwitch != null) {
            boolean z = false;
            int i = Settings.System.getInt(this.mContext.getContentResolver(), DUAL_CHANNEL_DOWNLOAD_ACCELERATION, 0);
            MasterSwitchPreference masterSwitchPreference = this.mSwitch;
            if (i == 1) {
                z = true;
            }
            masterSwitchPreference.setChecked(z);
            MasterSwitchController masterSwitchController = new MasterSwitchController(this.mSwitch);
            this.mSwitchController = masterSwitchController;
            masterSwitchController.setListener(this);
            this.mSwitchController.startListening();
        }
    }
}
