package com.android.settings.security;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Pair;
import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class ConfirmSimDeletionPreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    public static final String KEY_CONFIRM_SIM_DELETION = "confirm_sim_deletion";
    private boolean mConfirmationDefaultOn;
    private MetricsFeatureProvider mMetricsFeatureProvider;

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

    public ConfirmSimDeletionPreferenceController(Context context, String str) {
        super(context, str);
        this.mConfirmationDefaultOn = context.getResources().getBoolean(C0005R$bool.config_sim_deletion_confirmation_default_on);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return MobileNetworkUtils.showEuiccSettings(this.mContext) ? 0 : 3;
    }

    private boolean getGlobalState() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), KEY_CONFIRM_SIM_DELETION, this.mConfirmationDefaultOn ? 1 : 0) == 1;
    }

    public boolean isChecked() {
        return getGlobalState();
    }

    public boolean setChecked(boolean z) {
        Settings.Global.putInt(this.mContext.getContentResolver(), KEY_CONFIRM_SIM_DELETION, z ? 1 : 0);
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!preference.getKey().equals(getPreferenceKey())) {
            return false;
        }
        if (!isChecked()) {
            this.mMetricsFeatureProvider.action(this.mContext, 1738, new Pair[0]);
            setChecked(true);
            return true;
        }
        WifiDppUtils.showLockScreen(this.mContext, new Runnable(preference) {
            /* class com.android.settings.security.$$Lambda$ConfirmSimDeletionPreferenceController$WAH6ftBGqZdHr4LtRG31b8Ku9_A */
            public final /* synthetic */ Preference f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ConfirmSimDeletionPreferenceController.this.lambda$onPreferenceChange$0$ConfirmSimDeletionPreferenceController(this.f$1);
            }
        });
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onPreferenceChange$0 */
    public /* synthetic */ void lambda$onPreferenceChange$0$ConfirmSimDeletionPreferenceController(Preference preference) {
        this.mMetricsFeatureProvider.action(this.mContext, 1739, new Pair[0]);
        setChecked(false);
        ((TwoStatePreference) preference).setChecked(false);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (!((KeyguardManager) this.mContext.getSystemService(KeyguardManager.class)).isKeyguardSecure()) {
            preference.setEnabled(false);
            if (preference instanceof TwoStatePreference) {
                ((TwoStatePreference) preference).setChecked(false);
            }
            preference.setSummary(C0017R$string.disabled_because_no_backup_security);
            return;
        }
        preference.setEnabled(true);
        if (preference instanceof TwoStatePreference) {
            ((TwoStatePreference) preference).setChecked(getGlobalState());
        }
        preference.setSummary(C0017R$string.confirm_sim_deletion_description);
    }
}
