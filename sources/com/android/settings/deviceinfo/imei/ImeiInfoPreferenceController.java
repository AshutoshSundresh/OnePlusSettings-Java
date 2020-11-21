package com.android.settings.deviceinfo.imei;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.slices.Sliceable;
import com.oneplus.settings.utils.ProductUtils;
import java.util.ArrayList;
import java.util.List;

public class ImeiInfoPreferenceController extends BasePreferenceController {
    private Fragment mFragment;
    private final boolean mIsMultiSim;
    private final List<Preference> mPreferenceList = new ArrayList();
    private final TelephonyManager mTelephonyManager;

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
    public boolean useDynamicSliceSummary() {
        return true;
    }

    public ImeiInfoPreferenceController(Context context, String str) {
        super(context, str);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        this.mTelephonyManager = telephonyManager;
        boolean z = true;
        this.mIsMultiSim = (telephonyManager.getPhoneCount() <= 1 || !this.mTelephonyManager.isMultiSimEnabled()) ? false : z;
    }

    public void setHost(Fragment fragment) {
        this.mFragment = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceList.add(findPreference);
        updatePreference(findPreference, 0);
        int order = findPreference.getOrder();
        int phoneCount = this.mTelephonyManager.isMultiSimEnabled() ? this.mTelephonyManager.getPhoneCount() : 1;
        for (int i = 1; i < phoneCount; i++) {
            Preference createNewPreference = createNewPreference(preferenceScreen.getContext());
            createNewPreference.setOrder(order + i);
            createNewPreference.setKey(getPreferenceKey() + i);
            preferenceScreen.addPreference(createNewPreference);
            this.mPreferenceList.add(createNewPreference);
            updatePreference(createNewPreference, i);
        }
        if (Utils.isSupportCTPA(this.mContext) && phoneCount >= 2) {
            int currentPhoneTypeForSlot = this.mTelephonyManager.getCurrentPhoneTypeForSlot(0);
            int currentPhoneTypeForSlot2 = this.mTelephonyManager.getCurrentPhoneTypeForSlot(1);
            if (2 != currentPhoneTypeForSlot && 2 != currentPhoneTypeForSlot2) {
                addPreferenceNotInList(preferenceScreen, 0, order + phoneCount, getPreferenceKey() + phoneCount, true);
            } else if (2 == currentPhoneTypeForSlot) {
                addPreferenceNotInList(preferenceScreen, 0, order + phoneCount, getPreferenceKey() + phoneCount, false);
            } else if (2 == currentPhoneTypeForSlot2) {
                addPreferenceNotInList(preferenceScreen, 1, order + phoneCount, getPreferenceKey() + phoneCount, false);
            }
        }
    }

    private void addPreferenceNotInList(PreferenceScreen preferenceScreen, int i, int i2, String str, boolean z) {
        Preference createNewPreference = createNewPreference(preferenceScreen.getContext());
        createNewPreference.setOrder(i2);
        createNewPreference.setKey(str);
        preferenceScreen.addPreference(createNewPreference);
        if (z) {
            createNewPreference.setTitle(getTitleForCdmaPhone(i));
            createNewPreference.setSummary(this.mTelephonyManager.getMeid(i));
            return;
        }
        createNewPreference.setTitle(getTitleForGsmPhone(i));
        if (ProductUtils.isUsvMode()) {
            createNewPreference.setSummary(String.join(" ", ProductUtils.splitTextToNChar(this.mTelephonyManager.getImei(i), 4)));
        } else {
            createNewPreference.setSummary(this.mTelephonyManager.getImei(i));
        }
    }

    private void addPreference(PreferenceScreen preferenceScreen, int i, int i2, String str, boolean z) {
        Preference createNewPreference = createNewPreference(preferenceScreen.getContext());
        createNewPreference.setOrder(i2);
        createNewPreference.setKey(str);
        preferenceScreen.addPreference(createNewPreference);
        this.mPreferenceList.add(createNewPreference);
        if (z) {
            createNewPreference.setTitle(getTitleForCdmaPhone(i));
            createNewPreference.setSummary(this.mTelephonyManager.getMeid(i));
            return;
        }
        createNewPreference.setTitle(getTitleForGsmPhone(i));
        createNewPreference.setSummary(this.mTelephonyManager.getImei(i));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference != null) {
            int size = this.mPreferenceList.size();
            for (int i = 0; i < size; i++) {
                updatePreference(this.mPreferenceList.get(i), i);
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return getSummary(0);
    }

    private CharSequence getSummary(int i) {
        int phoneType = getPhoneType(i);
        if (Utils.isSupportCTPA(this.mContext) && 2 == phoneType) {
            i = 0;
        }
        if (phoneType == 2) {
            return this.mTelephonyManager.getMeid(i);
        }
        return this.mTelephonyManager.getImei(i);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        int indexOf = this.mPreferenceList.indexOf(preference);
        if (indexOf == -1) {
            return false;
        }
        if (Utils.isSupportCTPA(this.mContext)) {
            return true;
        }
        ImeiInfoDialogFragment.show(this.mFragment, indexOf, preference.getTitle().toString());
        return true;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!((UserManager) this.mContext.getSystemService(UserManager.class)).isAdminUser() || com.android.settingslib.Utils.isWifiOnly(this.mContext)) ? 3 : 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public void copy() {
        Sliceable.setCopyContent(this.mContext, getSummary(0), getTitle(0));
    }

    private void updatePreference(Preference preference, int i) {
        preference.setTitle(getTitle(i));
        preference.setSummary(getSummary(i));
    }

    private CharSequence getTitleForGsmPhone(int i) {
        if (!this.mIsMultiSim) {
            return this.mContext.getString(C0017R$string.status_imei);
        }
        return this.mContext.getString(C0017R$string.imei_multi_sim, Integer.valueOf(i + 1));
    }

    private CharSequence getTitleForCdmaPhone(int i) {
        if (!this.mIsMultiSim) {
            return this.mContext.getString(C0017R$string.status_meid_number);
        }
        return this.mContext.getString(C0017R$string.meid_multi_sim, Integer.valueOf(i + 1));
    }

    private CharSequence getTitle(int i) {
        if (getPhoneType(i) == 2) {
            return getTitleForCdmaPhone(i);
        }
        return getTitleForGsmPhone(i);
    }

    private int getPhoneType(int i) {
        if (Utils.isSupportCTPA(this.mContext)) {
            return this.mTelephonyManager.getCurrentPhoneTypeForSlot(i);
        }
        SubscriptionInfo activeSubscriptionInfoForSimSlotIndex = SubscriptionManager.from(this.mContext).getActiveSubscriptionInfoForSimSlotIndex(i);
        return this.mTelephonyManager.getCurrentPhoneType(activeSubscriptionInfoForSimSlotIndex != null ? activeSubscriptionInfoForSimSlotIndex.getSubscriptionId() : Integer.MAX_VALUE);
    }

    /* access modifiers changed from: package-private */
    public Preference createNewPreference(Context context) {
        return new Preference(context);
    }
}
