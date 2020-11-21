package com.oneplus.settings.controllers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserManager;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.oneplus.settings.system.OPSystemUpdatePreference;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;

public class OPTopLevelSystemPreferenceController extends BasePreferenceController implements LifecycleObserver, OnCreate, OnDestroy {
    private static final String KEY_TOP_LEVEL_SYSTEM = "top_level_system";
    private Context mContext;
    private SystemUpdateObserver mSystemUpdateObserver;
    private final UserManager mUm;
    OPSystemUpdatePreference mUpdatePreference;

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
        return KEY_TOP_LEVEL_SYSTEM;
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

    public OPTopLevelSystemPreferenceController(Context context, String str) {
        super(context, KEY_TOP_LEVEL_SYSTEM);
        this.mUm = UserManager.get(context);
        this.mContext = context;
    }

    public boolean isNeedAvailable() {
        return !this.mContext.getResources().getBoolean(C0005R$bool.config_use_gota) && !OPUtils.isSupportUss() && this.mUm.isAdminUser();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            this.mUpdatePreference = (OPSystemUpdatePreference) preferenceScreen.findPreference(getPreferenceKey());
        }
        if (ProductUtils.isUsvMode()) {
            this.mUpdatePreference.setSummary(C0017R$string.oneplus_top_level_system_summary_verizon);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (isAvailable()) {
            ((OPSystemUpdatePreference) preference).updateView();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnCreate
    public void onCreate(Bundle bundle) {
        if (isAvailable()) {
            SystemUpdateObserver systemUpdateObserver = new SystemUpdateObserver(new Handler());
            this.mSystemUpdateObserver = systemUpdateObserver;
            systemUpdateObserver.startObserving();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        SystemUpdateObserver systemUpdateObserver = this.mSystemUpdateObserver;
        if (systemUpdateObserver != null) {
            systemUpdateObserver.stopObserving();
            this.mSystemUpdateObserver = null;
        }
    }

    private class SystemUpdateObserver extends ContentObserver {
        private final Uri SYSTEM_UPDATE_URI = Settings.System.getUriFor("has_new_version_to_update");

        public SystemUpdateObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean z, Uri uri) {
            OPTopLevelSystemPreferenceController oPTopLevelSystemPreferenceController = OPTopLevelSystemPreferenceController.this;
            oPTopLevelSystemPreferenceController.updateState(oPTopLevelSystemPreferenceController.mUpdatePreference);
        }

        public void onChange(boolean z) {
            OPTopLevelSystemPreferenceController oPTopLevelSystemPreferenceController = OPTopLevelSystemPreferenceController.this;
            oPTopLevelSystemPreferenceController.updateState(oPTopLevelSystemPreferenceController.mUpdatePreference);
        }

        public void startObserving() {
            ContentResolver contentResolver = OPTopLevelSystemPreferenceController.this.mContext.getContentResolver();
            contentResolver.unregisterContentObserver(this);
            contentResolver.registerContentObserver(this.SYSTEM_UPDATE_URI, false, this, -1);
        }

        public void stopObserving() {
            OPTopLevelSystemPreferenceController.this.mContext.getContentResolver().unregisterContentObserver(this);
        }
    }
}
