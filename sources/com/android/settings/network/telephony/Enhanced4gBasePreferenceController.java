package com.android.settings.network.telephony;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Looper;
import android.os.PersistableBundle;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsMmTelManager;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.network.ims.VolteQueryImsState;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.ArrayList;
import java.util.List;

public class Enhanced4gBasePreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    protected static final int MODE_4G_CALLING = 2;
    protected static final int MODE_ADVANCED_CALL = 1;
    protected static final int MODE_NONE = -1;
    protected static final int MODE_VOLTE = 0;
    private static final String TAG = "Enhanced4g";
    private int m4gCurrentMode = MODE_NONE;
    private final List<On4gLteUpdateListener> m4gLteListeners = new ArrayList();
    Integer mCallState;
    private boolean mHas5gCapability;
    boolean mIsNrEnabledFromCarrierConfig;
    private PhoneCallStateListener mPhoneStateListener;
    Preference mPreference;
    private boolean mShow5gLimitedDialog;

    public interface On4gLteUpdateListener {
        void on4gLteUpdated();
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

    /* access modifiers changed from: protected */
    public int getMode() {
        return MODE_NONE;
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

    public Enhanced4gBasePreferenceController(Context context, String str) {
        super(context, str);
    }

    public Enhanced4gBasePreferenceController init(int i) {
        if (this.mPhoneStateListener == null) {
            this.mPhoneStateListener = new PhoneCallStateListener();
        }
        if (this.mSubId == i) {
            return this;
        }
        this.mSubId = i;
        PersistableBundle carrierConfigForSubId = getCarrierConfigForSubId(i);
        if (carrierConfigForSubId == null) {
            return this;
        }
        boolean z = carrierConfigForSubId.getBoolean("show_4g_for_lte_data_icon_bool");
        int i2 = carrierConfigForSubId.getInt("enhanced_4g_lte_title_variant_int");
        this.m4gCurrentMode = i2;
        if (i2 != 1) {
            this.m4gCurrentMode = z ? 2 : 0;
        }
        this.mShow5gLimitedDialog = carrierConfigForSubId.getBoolean("volte_5g_limited_alert_dialog_bool");
        this.mIsNrEnabledFromCarrierConfig = carrierConfigForSubId.getBoolean("nr_enabled_bool");
        return this;
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback, com.android.settings.network.telephony.TelephonyTogglePreferenceController
    public int getAvailabilityStatus(int i) {
        PersistableBundle carrierConfigForSubId;
        init(i);
        if (!isModeMatched() || (carrierConfigForSubId = getCarrierConfigForSubId(i)) == null || carrierConfigForSubId.getBoolean("hide_enhanced_4g_lte_bool")) {
            return 2;
        }
        VolteQueryImsState queryImsState = queryImsState(i);
        if (!queryImsState.isReadyToVoLte()) {
            return 2;
        }
        return (!isUserControlAllowed(carrierConfigForSubId) || !queryImsState.isAllowUserControl()) ? 1 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        PhoneCallStateListener phoneCallStateListener = this.mPhoneStateListener;
        if (phoneCallStateListener != null) {
            phoneCallStateListener.register(this.mContext, this.mSubId);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        PhoneCallStateListener phoneCallStateListener = this.mPhoneStateListener;
        if (phoneCallStateListener != null) {
            phoneCallStateListener.unregister();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.TogglePreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference != null) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            VolteQueryImsState queryImsState = queryImsState(this.mSubId);
            boolean z = true;
            switchPreference.setEnabled(isUserControlAllowed(getCarrierConfigForSubId(this.mSubId)) && queryImsState.isAllowUserControl());
            if (!queryImsState.isEnabledByUser() || !queryImsState.isAllowUserControl()) {
                z = false;
            }
            switchPreference.setChecked(z);
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        ImsMmTelManager createForSubscriptionId;
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId) || (createForSubscriptionId = ImsMmTelManager.createForSubscriptionId(this.mSubId)) == null) {
            return false;
        }
        if (!isDialogNeeded() || z) {
            return setAdvancedCallingSettingEnabled(createForSubscriptionId, z);
        }
        show5gLimitedDialog(createForSubscriptionId);
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return queryImsState(this.mSubId).isEnabledByUser();
    }

    public Enhanced4gBasePreferenceController addListener(On4gLteUpdateListener on4gLteUpdateListener) {
        this.m4gLteListeners.add(on4gLteUpdateListener);
        return this;
    }

    private boolean isModeMatched() {
        return this.m4gCurrentMode == getMode();
    }

    /* access modifiers changed from: package-private */
    public VolteQueryImsState queryImsState(int i) {
        return new VolteQueryImsState(this.mContext, i);
    }

    private boolean isUserControlAllowed(PersistableBundle persistableBundle) {
        Integer num = this.mCallState;
        return num != null && num.intValue() == 0 && persistableBundle != null && persistableBundle.getBoolean("editable_enhanced_4g_lte_bool");
    }

    /* access modifiers changed from: private */
    public class PhoneCallStateListener extends PhoneStateListener {
        private TelephonyManager mTelephonyManager;

        PhoneCallStateListener() {
            super(Looper.getMainLooper());
        }

        public void onCallStateChanged(int i, String str) {
            Enhanced4gBasePreferenceController.this.mCallState = Integer.valueOf(i);
            Enhanced4gBasePreferenceController enhanced4gBasePreferenceController = Enhanced4gBasePreferenceController.this;
            enhanced4gBasePreferenceController.updateState(enhanced4gBasePreferenceController.mPreference);
        }

        public void register(Context context, int i) {
            this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            if (SubscriptionManager.isValidSubscriptionId(i)) {
                this.mTelephonyManager = this.mTelephonyManager.createForSubscriptionId(i);
            }
            this.mTelephonyManager.listen(this, 32);
            long supportedRadioAccessFamily = this.mTelephonyManager.getSupportedRadioAccessFamily();
            Enhanced4gBasePreferenceController.this.mHas5gCapability = (supportedRadioAccessFamily & 524288) > 0;
        }

        public void unregister() {
            Enhanced4gBasePreferenceController.this.mCallState = null;
            this.mTelephonyManager.listen(this, 0);
        }
    }

    private boolean isDialogNeeded() {
        Log.d(TAG, "Has5gCapability:" + this.mHas5gCapability);
        return this.mShow5gLimitedDialog && this.mHas5gCapability && this.mIsNrEnabledFromCarrierConfig;
    }

    private void show5gLimitedDialog(final ImsMmTelManager imsMmTelManager) {
        Log.d(TAG, "show5gLimitedDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        AnonymousClass1 r1 = new DialogInterface.OnClickListener() {
            /* class com.android.settings.network.telephony.Enhanced4gBasePreferenceController.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(Enhanced4gBasePreferenceController.TAG, "onClick,isChecked:false");
                Enhanced4gBasePreferenceController.this.setAdvancedCallingSettingEnabled(imsMmTelManager, false);
                Enhanced4gBasePreferenceController enhanced4gBasePreferenceController = Enhanced4gBasePreferenceController.this;
                enhanced4gBasePreferenceController.updateState(enhanced4gBasePreferenceController.mPreference);
            }
        };
        builder.setTitle(C0017R$string.volte_5G_limited_title);
        builder.setMessage(C0017R$string.volte_5G_limited_text);
        builder.setNeutralButton(this.mContext.getResources().getString(C0017R$string.cancel), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(this.mContext.getResources().getString(C0017R$string.condition_turn_off), r1);
        builder.create().show();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean setAdvancedCallingSettingEnabled(ImsMmTelManager imsMmTelManager, boolean z) {
        try {
            imsMmTelManager.setAdvancedCallingSettingEnabled(z);
            for (On4gLteUpdateListener on4gLteUpdateListener : this.m4gLteListeners) {
                on4gLteUpdateListener.on4gLteUpdated();
            }
            return true;
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "fail to set VoLTE=" + z + ". subId=" + this.mSubId, e);
            return false;
        }
    }
}
