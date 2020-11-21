package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.Telephony;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.ims.ImsManager;
import android.util.Log;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.slices.SliceBackgroundWorker;

public class ContactDiscoveryPreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver {
    private static final String TAG = "ContactDiscoveryPref";
    private static final Uri UCE_URI = Uri.withAppendedPath(Telephony.SimInfo.CONTENT_URI, "ims_rcs_uce_enabled");
    private CarrierConfigManager mCarrierConfigManager = ((CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class));
    private FragmentManager mFragmentManager;
    private ImsManager mImsManager = ((ImsManager) this.mContext.getSystemService(ImsManager.class));
    private ContentObserver mUceSettingObserver;
    public Preference preference;

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

    public ContactDiscoveryPreferenceController(Context context, String str) {
        super(context, str);
    }

    public ContactDiscoveryPreferenceController init(FragmentManager fragmentManager, int i, Lifecycle lifecycle) {
        this.mFragmentManager = fragmentManager;
        this.mSubId = i;
        lifecycle.addObserver(this);
        return this;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return MobileNetworkUtils.isContactDiscoveryEnabled(this.mImsManager, this.mSubId);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        registerUceObserver();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        unregisterUceObserver();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (z) {
            showContentDiscoveryDialog();
            return false;
        }
        MobileNetworkUtils.setContactDiscoveryEnabled(this.mImsManager, this.mSubId, false);
        return true;
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback, com.android.settings.network.telephony.TelephonyTogglePreferenceController
    public int getAvailabilityStatus(int i) {
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(i);
        if (configForSubId != null && configForSubId.getBoolean("use_rcs_presence_bool", false)) {
            return 0;
        }
        return 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.preference = preferenceScreen.findPreference(getPreferenceKey());
    }

    private void registerUceObserver() {
        this.mUceSettingObserver = new ContentObserver(this.mContext.getMainThreadHandler()) {
            /* class com.android.settings.network.telephony.ContactDiscoveryPreferenceController.AnonymousClass1 */

            public void onChange(boolean z) {
                onChange(z, null);
            }

            public void onChange(boolean z, Uri uri) {
                Log.d(ContactDiscoveryPreferenceController.TAG, "UCE setting changed, re-evaluating.");
                ContactDiscoveryPreferenceController contactDiscoveryPreferenceController = ContactDiscoveryPreferenceController.this;
                ((SwitchPreference) contactDiscoveryPreferenceController.preference).setChecked(contactDiscoveryPreferenceController.isChecked());
            }
        };
        this.mContext.getContentResolver().registerContentObserver(UCE_URI, true, this.mUceSettingObserver);
    }

    private void unregisterUceObserver() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mUceSettingObserver);
    }

    private void showContentDiscoveryDialog() {
        ContactDiscoveryDialogFragment.newInstance(this.mSubId, getCarrierDisplayName(this.preference.getContext())).show(this.mFragmentManager, ContactDiscoveryDialogFragment.getFragmentTag(this.mSubId));
    }

    private CharSequence getCarrierDisplayName(Context context) {
        for (SubscriptionInfo subscriptionInfo : SubscriptionUtil.getAvailableSubscriptions(context)) {
            if (this.mSubId == subscriptionInfo.getSubscriptionId()) {
                return subscriptionInfo.getDisplayName();
            }
        }
        return "";
    }
}
