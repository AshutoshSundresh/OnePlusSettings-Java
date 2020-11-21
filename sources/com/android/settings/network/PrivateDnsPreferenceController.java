package com.android.settings.network;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.util.ArrayUtils;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.List;

public class PrivateDnsPreferenceController extends BasePreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnStart, OnStop {
    private static final String KEY_PRIVATE_DNS_SETTINGS = "private_dns_settings";
    private static final Uri[] SETTINGS_URIS = {Settings.Global.getUriFor("private_dns_mode"), Settings.Global.getUriFor("private_dns_default_mode"), Settings.Global.getUriFor("private_dns_specifier")};
    private final ConnectivityManager mConnectivityManager;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private LinkProperties mLatestLinkProperties;
    private final ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {
        /* class com.android.settings.network.PrivateDnsPreferenceController.AnonymousClass1 */

        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            PrivateDnsPreferenceController.this.mLatestLinkProperties = linkProperties;
            if (PrivateDnsPreferenceController.this.mPreference != null) {
                PrivateDnsPreferenceController privateDnsPreferenceController = PrivateDnsPreferenceController.this;
                privateDnsPreferenceController.updateState(privateDnsPreferenceController.mPreference);
            }
        }

        public void onLost(Network network) {
            PrivateDnsPreferenceController.this.mLatestLinkProperties = null;
            if (PrivateDnsPreferenceController.this.mPreference != null) {
                PrivateDnsPreferenceController privateDnsPreferenceController = PrivateDnsPreferenceController.this;
                privateDnsPreferenceController.updateState(privateDnsPreferenceController.mPreference);
            }
        }
    };
    private Preference mPreference;
    private final ContentObserver mSettingsObserver = new PrivateDnsSettingsObserver(this.mHandler);

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
        return KEY_PRIVATE_DNS_SETTINGS;
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

    public PrivateDnsPreferenceController(Context context) {
        super(context, KEY_PRIVATE_DNS_SETTINGS);
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_show_private_dns_settings) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        for (Uri uri : SETTINGS_URIS) {
            this.mContext.getContentResolver().registerContentObserver(uri, false, this.mSettingsObserver);
        }
        Network activeNetwork = this.mConnectivityManager.getActiveNetwork();
        if (activeNetwork != null) {
            this.mLatestLinkProperties = this.mConnectivityManager.getLinkProperties(activeNetwork);
        }
        this.mConnectivityManager.registerDefaultNetworkCallback(this.mNetworkCallback, this.mHandler);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mSettingsObserver);
        this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        List list;
        Resources resources = this.mContext.getResources();
        ContentResolver contentResolver = this.mContext.getContentResolver();
        String modeFromSettings = PrivateDnsModeDialogPreference.getModeFromSettings(contentResolver);
        LinkProperties linkProperties = this.mLatestLinkProperties;
        if (linkProperties == null) {
            list = null;
        } else {
            list = linkProperties.getValidatedPrivateDnsServers();
        }
        boolean z = !ArrayUtils.isEmpty(list);
        char c = 65535;
        int hashCode = modeFromSettings.hashCode();
        if (hashCode != -539229175) {
            if (hashCode != -299803597) {
                if (hashCode == 109935 && modeFromSettings.equals("off")) {
                    c = 0;
                }
            } else if (modeFromSettings.equals("hostname")) {
                c = 2;
            }
        } else if (modeFromSettings.equals("opportunistic")) {
            c = 1;
        }
        if (c == 0) {
            return resources.getString(C0017R$string.private_dns_mode_off);
        }
        if (c != 1) {
            if (c != 2) {
                return "";
            }
            if (z) {
                return PrivateDnsModeDialogPreference.getHostnameFromSettings(contentResolver);
            }
            return resources.getString(C0017R$string.private_dns_mode_provider_failure);
        } else if (z) {
            return resources.getString(C0017R$string.private_dns_mode_on);
        } else {
            return resources.getString(C0017R$string.private_dns_mode_opportunistic);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setEnabled(!isManagedByAdmin());
    }

    private boolean isManagedByAdmin() {
        return RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "disallow_config_private_dns", UserHandle.myUserId()) != null;
    }

    private class PrivateDnsSettingsObserver extends ContentObserver {
        public PrivateDnsSettingsObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean z) {
            if (PrivateDnsPreferenceController.this.mPreference != null) {
                PrivateDnsPreferenceController privateDnsPreferenceController = PrivateDnsPreferenceController.this;
                privateDnsPreferenceController.updateState(privateDnsPreferenceController.mPreference);
            }
        }
    }
}
