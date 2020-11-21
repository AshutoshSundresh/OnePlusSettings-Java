package com.android.settings.network.telephony.gsm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.telephony.CarrierConfigManager;
import android.telephony.TelephonyManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.network.PreferredNetworkModeContentObserver;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.NetworkSelectSettings;
import com.android.settings.network.telephony.TelephonyTogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutoSelectPreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver {
    private static final long MINIMUM_DIALOG_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1);
    private List<OnNetworkSelectModeListener> mListeners = new ArrayList();
    private boolean mOnlyAutoSelectInHome;
    private PreferenceScreen mPreferenceScreen;
    private PreferredNetworkModeContentObserver mPreferredNetworkModeObserver;
    ProgressDialog mProgressDialog;
    SwitchPreference mSwitchPreference;
    private TelephonyManager mTelephonyManager;
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());

    public interface OnNetworkSelectModeListener {
        void onNetworkSelectModeChanged();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AutoSelectPreferenceController(Context context, String str) {
        super(context, str);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mSubId = -1;
        PreferredNetworkModeContentObserver preferredNetworkModeContentObserver = new PreferredNetworkModeContentObserver(this.mUiHandler);
        this.mPreferredNetworkModeObserver = preferredNetworkModeContentObserver;
        preferredNetworkModeContentObserver.setPreferredNetworkModeChangedListener(new PreferredNetworkModeContentObserver.OnPreferredNetworkModeChangedListener() {
            /* class com.android.settings.network.telephony.gsm.$$Lambda$AutoSelectPreferenceController$HPYPaZ5Go4jb40eDiUgse5U0IU */

            @Override // com.android.settings.network.PreferredNetworkModeContentObserver.OnPreferredNetworkModeChangedListener
            public final void onPreferredNetworkModeChanged() {
                AutoSelectPreferenceController.this.lambda$new$0$AutoSelectPreferenceController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: updatePreference */
    public void lambda$new$0() {
        PreferenceScreen preferenceScreen = this.mPreferenceScreen;
        if (preferenceScreen != null) {
            displayPreference(preferenceScreen);
        }
        SwitchPreference switchPreference = this.mSwitchPreference;
        if (switchPreference != null) {
            updateState(switchPreference);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mPreferredNetworkModeObserver.register(this.mContext, this.mSubId);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mPreferredNetworkModeObserver.unregister(this.mContext);
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback, com.android.settings.network.telephony.TelephonyTogglePreferenceController
    public int getAvailabilityStatus(int i) {
        return MobileNetworkUtils.shouldDisplayNetworkSelectOptions(this.mContext, i) ? 0 : 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        this.mSwitchPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mTelephonyManager.getNetworkSelectionMode() == 1;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.TogglePreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setSummary((CharSequence) null);
        if (this.mTelephonyManager.getServiceState().getRoaming()) {
            preference.setEnabled(true);
            return;
        }
        preference.setEnabled(!this.mOnlyAutoSelectInHome);
        if (this.mOnlyAutoSelectInHome) {
            preference.setSummary(this.mContext.getString(C0017R$string.manual_mode_disallowed_summary, this.mTelephonyManager.getSimOperatorName()));
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (z) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            showAutoSelectProgressBar();
            this.mSwitchPreference.setEnabled(false);
            ThreadUtils.postOnBackgroundThread(new Runnable(elapsedRealtime) {
                /* class com.android.settings.network.telephony.gsm.$$Lambda$AutoSelectPreferenceController$F10XVncref23bmw69zLaWiI2Ug */
                public final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AutoSelectPreferenceController.this.lambda$setChecked$2$AutoSelectPreferenceController(this.f$1);
                }
            });
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("android.provider.extra.SUB_ID", this.mSubId);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(NetworkSelectSettings.class.getName());
        subSettingLauncher.setSourceMetricsCategory(1581);
        subSettingLauncher.setTitleRes(C0017R$string.choose_network_title);
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.launch();
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setChecked$2 */
    public /* synthetic */ void lambda$setChecked$2$AutoSelectPreferenceController(long j) {
        this.mTelephonyManager.setNetworkSelectionModeAutomatic();
        this.mUiHandler.postDelayed(new Runnable(this.mTelephonyManager.getNetworkSelectionMode()) {
            /* class com.android.settings.network.telephony.gsm.$$Lambda$AutoSelectPreferenceController$lx6pxiscZgdC70BpNNKuQLu7tE */
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                AutoSelectPreferenceController.this.lambda$setChecked$1$AutoSelectPreferenceController(this.f$1);
            }
        }, Math.max(MINIMUM_DIALOG_TIME_MILLIS - (SystemClock.elapsedRealtime() - j), 0L));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setChecked$1 */
    public /* synthetic */ void lambda$setChecked$1$AutoSelectPreferenceController(int i) {
        boolean z = true;
        this.mSwitchPreference.setEnabled(true);
        SwitchPreference switchPreference = this.mSwitchPreference;
        if (i != 1) {
            z = false;
        }
        switchPreference.setChecked(z);
        for (OnNetworkSelectModeListener onNetworkSelectModeListener : this.mListeners) {
            onNetworkSelectModeListener.onNetworkSelectModeChanged();
        }
        dismissProgressBar();
    }

    public AutoSelectPreferenceController init(Lifecycle lifecycle, int i) {
        this.mSubId = i;
        this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        PersistableBundle configForSubId = ((CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class)).getConfigForSubId(this.mSubId);
        this.mOnlyAutoSelectInHome = configForSubId != null ? configForSubId.getBoolean("only_auto_select_in_home_network") : false;
        lifecycle.addObserver(this);
        return this;
    }

    public AutoSelectPreferenceController addListener(OnNetworkSelectModeListener onNetworkSelectModeListener) {
        this.mListeners.add(onNetworkSelectModeListener);
        return this;
    }

    private void showAutoSelectProgressBar() {
        if (this.mProgressDialog == null) {
            ProgressDialog progressDialog = new ProgressDialog(this.mContext);
            this.mProgressDialog = progressDialog;
            progressDialog.setMessage(this.mContext.getResources().getString(C0017R$string.register_automatically));
            this.mProgressDialog.setCanceledOnTouchOutside(false);
            this.mProgressDialog.setCancelable(false);
            this.mProgressDialog.setIndeterminate(true);
        }
        this.mProgressDialog.show();
    }

    private void dismissProgressBar() {
        ProgressDialog progressDialog = this.mProgressDialog;
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                this.mProgressDialog.dismiss();
            } catch (IllegalArgumentException unused) {
            }
        }
    }
}
