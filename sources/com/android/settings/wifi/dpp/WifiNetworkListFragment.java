package com.android.settings.wifi.dpp;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.wifi.AddNetworkFragment;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.AccessPointPreference;
import com.android.settingslib.wifi.WifiSavedConfigUtils;
import com.android.settingslib.wifi.WifiTracker;
import com.android.settingslib.wifi.WifiTrackerFactory;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WifiNetworkListFragment extends SettingsPreferenceFragment implements WifiTracker.WifiListener, AccessPoint.AccessPointListener {
    private PreferenceCategory mAccessPointsPreferenceCategory;
    private Preference mAddPreference;
    private Preference mFakeNetworkPreference;
    private boolean mIsTest;
    private OnChooseNetworkListener mOnChooseNetworkListener;
    private WifiManager.ActionListener mSaveListener;
    private AccessPointPreference.UserBadgeCache mUserBadgeCache;
    private WifiManager mWifiManager;
    private WifiTracker mWifiTracker;

    public interface OnChooseNetworkListener {
        void onChooseNetwork(WifiNetworkConfig wifiNetworkConfig);
    }

    private String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1595;
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onConnectedChanged() {
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChooseNetworkListener) {
            this.mOnChooseNetworkListener = (OnChooseNetworkListener) context;
            return;
        }
        throw new IllegalArgumentException("Invalid context type");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        this.mOnChooseNetworkListener = null;
        super.onDetach();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        WifiTracker create = WifiTrackerFactory.create(getActivity(), this, getSettingsLifecycle(), true, true);
        this.mWifiTracker = create;
        this.mWifiManager = create.getManager();
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mIsTest = arguments.getBoolean("test", false);
        }
        this.mSaveListener = new WifiManager.ActionListener() {
            /* class com.android.settings.wifi.dpp.WifiNetworkListFragment.AnonymousClass1 */

            public void onSuccess() {
            }

            public void onFailure(int i) {
                FragmentActivity activity = WifiNetworkListFragment.this.getActivity();
                if (activity != null) {
                    Toast.makeText(activity, C0017R$string.wifi_failed_save_message, 0).show();
                }
            }
        };
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1) {
            if (i2 == -1) {
                handleAddNetworkSubmitEvent(intent);
            }
            this.mWifiTracker.resumeScanning();
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment
    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(C0019R$xml.wifi_dpp_network_list);
        this.mAccessPointsPreferenceCategory = (PreferenceCategory) findPreference("access_points");
        Preference preference = new Preference(getPrefContext());
        this.mFakeNetworkPreference = preference;
        preference.setIcon(C0008R$drawable.ic_wifi_signal_0);
        this.mFakeNetworkPreference.setKey("fake_key");
        this.mFakeNetworkPreference.setTitle("fake network");
        Preference preference2 = new Preference(getPrefContext());
        this.mAddPreference = preference2;
        preference2.setIcon(C0008R$drawable.ic_add_24dp);
        this.mAddPreference.setTitle(C0017R$string.wifi_add_network);
        this.mUserBadgeCache = new AccessPointPreference.UserBadgeCache(getPackageManager());
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onWifiStateChanged(int i) {
        int wifiState = this.mWifiManager.getWifiState();
        if (wifiState == 0 || wifiState == 2) {
            removeAccessPointPreferences();
        } else if (wifiState == 3) {
            updateAccessPointPreferences();
        }
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onAccessPointsChanged() {
        updateAccessPointPreferences();
    }

    @Override // com.android.settingslib.wifi.AccessPoint.AccessPointListener
    public void onAccessPointChanged(AccessPoint accessPoint) {
        Log.d("WifiNetworkListFragment", "onAccessPointChanged (singular) callback initiated");
        View view = getView();
        if (view != null) {
            view.post(new Runnable() {
                /* class com.android.settings.wifi.dpp.$$Lambda$WifiNetworkListFragment$0MXyYoxpcuvpYu82f1MtTJJVwJA */

                public final void run() {
                    WifiNetworkListFragment.lambda$onAccessPointChanged$0(AccessPoint.this);
                }
            });
        }
    }

    static /* synthetic */ void lambda$onAccessPointChanged$0(AccessPoint accessPoint) {
        Object tag = accessPoint.getTag();
        if (tag != null) {
            ((AccessPointPreference) tag).refresh();
        }
    }

    @Override // com.android.settingslib.wifi.AccessPoint.AccessPointListener
    public void onLevelChanged(AccessPoint accessPoint) {
        ((AccessPointPreference) accessPoint.getTag()).onLevelChanged();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof AccessPointPreference) {
            AccessPoint accessPoint = ((AccessPointPreference) preference).getAccessPoint();
            if (accessPoint == null) {
                return false;
            }
            WifiConfiguration config = accessPoint.getConfig();
            if (config != null) {
                WifiNetworkConfig validConfigOrNull = WifiNetworkConfig.getValidConfigOrNull(accessPoint.getSecurityString(true), config.getPrintableSsid(), config.preSharedKey, config.hiddenSSID, config.networkId, false);
                OnChooseNetworkListener onChooseNetworkListener = this.mOnChooseNetworkListener;
                if (onChooseNetworkListener != null) {
                    onChooseNetworkListener.onChooseNetwork(validConfigOrNull);
                }
            } else {
                throw new IllegalArgumentException("Invalid access point");
            }
        } else if (preference == this.mAddPreference) {
            launchAddNetworkFragment();
        } else if (preference != this.mFakeNetworkPreference) {
            return super.onPreferenceTreeClick(preference);
        } else {
            OnChooseNetworkListener onChooseNetworkListener2 = this.mOnChooseNetworkListener;
            if (onChooseNetworkListener2 != null) {
                onChooseNetworkListener2.onChooseNetwork(new WifiNetworkConfig("WPA", "fake network", "password", true, -1, false));
            }
        }
        return true;
    }

    private void handleAddNetworkSubmitEvent(Intent intent) {
        WifiConfiguration wifiConfiguration = (WifiConfiguration) intent.getParcelableExtra("wifi_config_key");
        if (wifiConfiguration != null) {
            this.mWifiManager.save(wifiConfiguration, this.mSaveListener);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: isValidForDppConfiguration */
    public boolean lambda$updateAccessPointPreferences$1(AccessPoint accessPoint) {
        int security = accessPoint.getSecurity();
        return security == 2 || security == 5;
    }

    private void launchAddNetworkFragment() {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setTitleRes(C0017R$string.wifi_add_network);
        subSettingLauncher.setDestination(AddNetworkFragment.class.getName());
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.setResultListener(this, 1);
        subSettingLauncher.launch();
    }

    private void removeAccessPointPreferences() {
        this.mAccessPointsPreferenceCategory.removeAll();
        this.mAccessPointsPreferenceCategory.setVisible(false);
    }

    private void updateAccessPointPreferences() {
        if (this.mWifiManager.isWifiEnabled()) {
            int i = 0;
            this.mAccessPointsPreferenceCategory.removeAll();
            for (AccessPoint accessPoint : (List) WifiSavedConfigUtils.getAllConfigs(getContext(), this.mWifiManager).stream().filter(new Predicate() {
                /* class com.android.settings.wifi.dpp.$$Lambda$WifiNetworkListFragment$tHnl1HEzbhrgoOfuT9H8v_fns */

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return WifiNetworkListFragment.this.lambda$updateAccessPointPreferences$1$WifiNetworkListFragment((AccessPoint) obj);
                }
            }).map(new Function() {
                /* class com.android.settings.wifi.dpp.$$Lambda$WifiNetworkListFragment$im98oMVseKI8S1PfQ90XTsRVeE */

                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return WifiNetworkListFragment.this.lambda$updateAccessPointPreferences$2$WifiNetworkListFragment((AccessPoint) obj);
                }
            }).sorted(new Comparator() {
                /* class com.android.settings.wifi.dpp.$$Lambda$WifiNetworkListFragment$MDDOx8wAL2tgC__Fhp1GKaOuM */

                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return WifiNetworkListFragment.this.lambda$updateAccessPointPreferences$3$WifiNetworkListFragment((AccessPoint) obj, (AccessPoint) obj2);
                }
            }).collect(Collectors.toList())) {
                AccessPointPreference createAccessPointPreference = createAccessPointPreference(accessPoint);
                createAccessPointPreference.setOrder(i);
                createAccessPointPreference.setEnabled(accessPoint.isReachable());
                accessPoint.setListener(this);
                createAccessPointPreference.refresh();
                this.mAccessPointsPreferenceCategory.addPreference(createAccessPointPreference);
                i++;
            }
            this.mAddPreference.setOrder(i);
            this.mAccessPointsPreferenceCategory.addPreference(this.mAddPreference);
            if (this.mIsTest) {
                this.mAccessPointsPreferenceCategory.addPreference(this.mFakeNetworkPreference);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateAccessPointPreferences$3 */
    public /* synthetic */ int lambda$updateAccessPointPreferences$3$WifiNetworkListFragment(AccessPoint accessPoint, AccessPoint accessPoint2) {
        if (accessPoint.isReachable() && !accessPoint2.isReachable()) {
            return -1;
        }
        if (accessPoint.isReachable() || !accessPoint2.isReachable()) {
            return nullToEmpty(accessPoint.getTitle()).compareToIgnoreCase(nullToEmpty(accessPoint2.getTitle()));
        }
        return 1;
    }

    /* access modifiers changed from: private */
    /* renamed from: getScannedAccessPointIfAvailable */
    public AccessPoint lambda$updateAccessPointPreferences$2(AccessPoint accessPoint) {
        List<AccessPoint> accessPoints = this.mWifiTracker.getAccessPoints();
        WifiConfiguration config = accessPoint.getConfig();
        for (AccessPoint accessPoint2 : accessPoints) {
            if (accessPoint2.matches(config)) {
                return accessPoint2;
            }
        }
        return accessPoint;
    }

    private AccessPointPreference createAccessPointPreference(AccessPoint accessPoint) {
        return new AccessPointPreference(accessPoint, getPrefContext(), this.mUserBadgeCache, C0008R$drawable.ic_wifi_signal_0, false);
    }
}
