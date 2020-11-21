package com.oneplus.settings.system;

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
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.oneplus.settings.utils.OPUtils;

public class OPSystemUpdatePreferenceController extends BasePreferenceController implements LifecycleObserver, OnCreate, OnDestroy {
    private static final String KEY_OP_SYSTEM_UPDATE_SETTINGS = "oneplus_system_update_settings";
    private static final String TAG = "OPSysUpdatePrefContr";
    private Context mContext;
    private SystemUpdateObserver mSystemUpdateObserver;
    private final UserManager mUm;
    OPSystemUpdatePreference mUpdatePreference;

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
        return KEY_OP_SYSTEM_UPDATE_SETTINGS;
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

    public OPSystemUpdatePreferenceController(Context context, String str) {
        super(context, KEY_OP_SYSTEM_UPDATE_SETTINGS);
        this.mUm = UserManager.get(context);
        this.mContext = context;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isNeedAvailable() ? 0 : 3;
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
        OPSystemUpdatePreference oPSystemUpdatePreference = this.mUpdatePreference;
        if (oPSystemUpdatePreference == null) {
            return;
        }
        if (oPSystemUpdatePreference.getIntent() == null || this.mUpdatePreference.getIntent().resolveActivity(this.mContext.getPackageManager()) == null) {
            this.mUpdatePreference.setEnabled(false);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        OPSystemUpdatePreference oPSystemUpdatePreference;
        if (isAvailable() && preference != null && (oPSystemUpdatePreference = (OPSystemUpdatePreference) preference) != null) {
            oPSystemUpdatePreference.updateView();
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
            OPSystemUpdatePreferenceController oPSystemUpdatePreferenceController = OPSystemUpdatePreferenceController.this;
            oPSystemUpdatePreferenceController.updateState(oPSystemUpdatePreferenceController.mUpdatePreference);
        }

        public void onChange(boolean z) {
            OPSystemUpdatePreferenceController oPSystemUpdatePreferenceController = OPSystemUpdatePreferenceController.this;
            oPSystemUpdatePreferenceController.updateState(oPSystemUpdatePreferenceController.mUpdatePreference);
        }

        public void startObserving() {
            ContentResolver contentResolver = OPSystemUpdatePreferenceController.this.mContext.getContentResolver();
            contentResolver.unregisterContentObserver(this);
            contentResolver.registerContentObserver(this.SYSTEM_UPDATE_URI, false, this, -1);
        }

        public void stopObserving() {
            OPSystemUpdatePreferenceController.this.mContext.getContentResolver().unregisterContentObserver(this);
        }
    }
}
