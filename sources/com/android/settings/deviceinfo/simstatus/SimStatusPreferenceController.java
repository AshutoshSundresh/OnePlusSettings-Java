package com.android.settings.deviceinfo.simstatus;

import android.content.Context;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.deviceinfo.AbstractSimStatusImeiInfoPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class SimStatusPreferenceController extends AbstractSimStatusImeiInfoPreferenceController implements PreferenceControllerMixin {
    private final Fragment mFragment;
    private final List<Preference> mPreferenceList = new ArrayList();
    private final SubscriptionManager mSubscriptionManager;
    private final TelephonyManager mTelephonyManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "sim_status";
    }

    public SimStatusPreferenceController(Context context, Fragment fragment) {
        super(context);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService("telephony_subscription_service");
        this.mFragment = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        if (isAvailable() && findPreference != null && findPreference.isVisible()) {
            this.mPreferenceList.add(findPreference);
            int order = findPreference.getOrder();
            for (int i = 1; i < this.mTelephonyManager.getPhoneCount(); i++) {
                Preference createNewPreference = createNewPreference(preferenceScreen.getContext());
                createNewPreference.setOrder(order + i);
                createNewPreference.setKey("sim_status" + i);
                preferenceScreen.addPreference(createNewPreference);
                this.mPreferenceList.add(createNewPreference);
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        for (int i = 0; i < this.mPreferenceList.size(); i++) {
            Preference preference2 = this.mPreferenceList.get(i);
            preference2.setTitle(getPreferenceTitle(i));
            preference2.setSummary(getCarrierName(i));
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        int indexOf = this.mPreferenceList.indexOf(preference);
        if (indexOf == -1) {
            return false;
        }
        SimStatusDialogFragment.show(this.mFragment, indexOf, getPreferenceTitle(indexOf));
        return true;
    }

    private String getPreferenceTitle(int i) {
        if (this.mTelephonyManager.getPhoneCount() <= 1) {
            return this.mContext.getString(C0017R$string.sim_status_title);
        }
        return this.mContext.getString(C0017R$string.sim_status_title_sim_slot, Integer.valueOf(i + 1));
    }

    private CharSequence getCarrierName(int i) {
        List<SubscriptionInfo> activeSubscriptionInfoList = this.mSubscriptionManager.getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList != null) {
            for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                if (subscriptionInfo.getSimSlotIndex() == i) {
                    return subscriptionInfo.getCarrierName();
                }
            }
        }
        return this.mContext.getText(C0017R$string.device_info_not_available);
    }

    /* access modifiers changed from: package-private */
    public Preference createNewPreference(Context context) {
        return new Preference(context);
    }
}
