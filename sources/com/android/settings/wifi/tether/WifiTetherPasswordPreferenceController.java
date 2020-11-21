package com.android.settings.wifi.tether;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.SoftApConfiguration;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import android.util.Pair;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.ValidatedEditTextPreference;
import com.android.settings.wifi.WifiUtils;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import java.util.UUID;

public class WifiTetherPasswordPreferenceController extends WifiTetherBasePreferenceController implements ValidatedEditTextPreference.Validator {
    private static final String PREF_KEY = "wifi_tether_network_password";
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private String mPassword;

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    WifiTetherPasswordPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener, MetricsFeatureProvider metricsFeatureProvider) {
        super(context, onTetherConfigUpdateListener, PREF_KEY);
        this.mMetricsFeatureProvider = metricsFeatureProvider;
    }

    public WifiTetherPasswordPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener, PREF_KEY);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return FeatureFlagUtils.isEnabled(this.mContext, "settings_tether_all_in_one") ? "wifi_tether_network_password_2" : PREF_KEY;
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        if (softApConfiguration == null || ((softApConfiguration.getSecurityType() == 1 || softApConfiguration.getSecurityType() == 2) && TextUtils.isEmpty(softApConfiguration.getPassphrase()))) {
            this.mPassword = generateRandomPassword();
        } else {
            this.mPassword = softApConfiguration.getPassphrase();
        }
        ((ValidatedEditTextPreference) this.mPreference).setValidator(this);
        ((ValidatedEditTextPreference) this.mPreference).setIsPassword(true);
        ((ValidatedEditTextPreference) this.mPreference).setIsSummaryPassword(true);
        updatePasswordDisplay((EditTextPreference) this.mPreference);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener, com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String str = (String) obj;
        if (!TextUtils.equals(this.mPassword, str)) {
            this.mMetricsFeatureProvider.action(this.mContext, 1737, new Pair[0]);
        }
        this.mPassword = str;
        updatePasswordDisplay((EditTextPreference) this.mPreference);
        this.mListener.onTetherConfigUpdated(this);
        return true;
    }

    public String getPasswordValidated(int i) {
        if (i == 0 || i == 4) {
            return "";
        }
        if (!isTextValid(this.mPassword)) {
            this.mPassword = generateRandomPassword();
            updatePasswordDisplay((EditTextPreference) this.mPreference);
        }
        return this.mPassword;
    }

    public void updateVisibility(int i) {
        this.mPreference.setVisible((i == 0 || i == 4) ? false : true);
    }

    @Override // com.android.settings.widget.ValidatedEditTextPreference.Validator
    public boolean isTextValid(String str) {
        return WifiUtils.isHotspotPasswordValid(str);
    }

    private static String generateRandomPassword() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8) + uuid.substring(9, 13);
    }

    private void updatePasswordDisplay(EditTextPreference editTextPreference) {
        ValidatedEditTextPreference validatedEditTextPreference = (ValidatedEditTextPreference) editTextPreference;
        validatedEditTextPreference.setText(this.mPassword);
        if (!TextUtils.isEmpty(this.mPassword)) {
            validatedEditTextPreference.setIsSummaryPassword(true);
            validatedEditTextPreference.setSummary(this.mPassword);
            validatedEditTextPreference.setVisible(true);
            return;
        }
        validatedEditTextPreference.setIsSummaryPassword(false);
        validatedEditTextPreference.setSummary(C0017R$string.wifi_hotspot_no_password_subtext);
        validatedEditTextPreference.setVisible(false);
    }
}
