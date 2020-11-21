package com.android.settings.notification.zen;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;

public abstract class ZenModeSettingsBase extends RestrictedDashboardFragment {
    protected static final boolean DEBUG = Log.isLoggable("ZenModeSettings", 3);
    protected ZenModeBackend mBackend;
    protected Context mContext;
    private final Handler mHandler = new Handler();
    private final SettingsObserver mSettingsObserver = new SettingsObserver();
    protected int mZenMode;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ZenModeSettings";
    }

    /* access modifiers changed from: protected */
    public void onZenModeConfigChanged() {
    }

    public ZenModeSettingsBase() {
        super("no_adjust_volume");
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mBackend = ZenModeBackend.getInstance(context);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.dashboard.RestrictedDashboardFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        updateZenMode(false);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateZenMode(true);
        this.mSettingsObserver.register();
        if (!isUiRestricted()) {
            return;
        }
        if (isUiRestrictedByOnlyAdmin()) {
            getPreferenceScreen().removeAll();
        } else {
            finish();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mSettingsObserver.unregister();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateZenMode(boolean z) {
        int i = Settings.Global.getInt(getContentResolver(), "zen_mode", this.mZenMode);
        if (i != this.mZenMode) {
            this.mZenMode = i;
            if (DEBUG) {
                Log.d("ZenModeSettings", "updateZenMode mZenMode=" + this.mZenMode + " " + z);
            }
        }
    }

    /* access modifiers changed from: private */
    public final class SettingsObserver extends ContentObserver {
        private final Uri ZEN_MODE_CONFIG_ETAG_URI;
        private final Uri ZEN_MODE_URI;

        private SettingsObserver() {
            super(ZenModeSettingsBase.this.mHandler);
            this.ZEN_MODE_URI = Settings.Global.getUriFor("zen_mode");
            this.ZEN_MODE_CONFIG_ETAG_URI = Settings.Global.getUriFor("zen_mode_config_etag");
        }

        public void register() {
            ZenModeSettingsBase.this.getContentResolver().registerContentObserver(this.ZEN_MODE_URI, false, this);
            ZenModeSettingsBase.this.getContentResolver().registerContentObserver(this.ZEN_MODE_CONFIG_ETAG_URI, false, this);
        }

        public void unregister() {
            ZenModeSettingsBase.this.getContentResolver().unregisterContentObserver(this);
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.ZEN_MODE_URI.equals(uri)) {
                ZenModeSettingsBase.this.updateZenMode(true);
            }
            if (this.ZEN_MODE_CONFIG_ETAG_URI.equals(uri)) {
                ZenModeSettingsBase.this.mBackend.updatePolicy();
                ZenModeSettingsBase.this.onZenModeConfigChanged();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updatePreference(AbstractPreferenceController abstractPreferenceController) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (abstractPreferenceController.isAvailable()) {
            String preferenceKey = abstractPreferenceController.getPreferenceKey();
            Preference findPreference = preferenceScreen.findPreference(preferenceKey);
            if (findPreference == null) {
                Log.d("ZenModeSettings", String.format("Cannot find preference with key %s in Controller %s", preferenceKey, abstractPreferenceController.getClass().getSimpleName()));
                return;
            }
            abstractPreferenceController.updateState(findPreference);
        }
    }
}
