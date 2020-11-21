package com.android.settings.applications;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.RadioButtonPreference;

public class AppOpenSupportedLinksPreferenceController extends BasePreferenceController implements RadioButtonPreference.OnClickListener {
    private static final String KEY_LINK_OPEN_ALWAYS = "app_link_open_always";
    private static final String KEY_LINK_OPEN_ASK = "app_link_open_ask";
    private static final String KEY_LINK_OPEN_NEVER = "app_link_open_never";
    private static final String TAG = "OpenLinksPrefCtrl";
    RadioButtonPreference mAllowOpening;
    RadioButtonPreference mAskEveryTime;
    private Context mContext;
    private int mCurrentIndex;
    RadioButtonPreference mNotAllowed;
    private PackageManager mPackageManager;
    private String mPackageName;
    private PreferenceCategory mPreferenceCategory;
    private String[] mRadioKeys = {KEY_LINK_OPEN_ALWAYS, KEY_LINK_OPEN_ASK, KEY_LINK_OPEN_NEVER};

    private int indexToLinkState(int i) {
        if (i != 0) {
            return i != 2 ? 1 : 3;
        }
        return 2;
    }

    private int linkStateToIndex(int i) {
        if (i != 2) {
            return i != 3 ? 1 : 2;
        }
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public AppOpenSupportedLinksPreferenceController(Context context, String str) {
        super(context, str);
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
    }

    public AppOpenSupportedLinksPreferenceController setInit(String str) {
        this.mPackageName = str;
        return this;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mAllowOpening = makeRadioPreference(KEY_LINK_OPEN_ALWAYS, C0017R$string.app_link_open_always);
        int entriesNo = getEntriesNo();
        this.mAllowOpening.setAppendixVisibility(8);
        this.mAllowOpening.setSummary(this.mContext.getResources().getQuantityString(C0015R$plurals.app_link_open_always_summary, entriesNo, Integer.valueOf(entriesNo)));
        this.mAskEveryTime = makeRadioPreference(KEY_LINK_OPEN_ASK, C0017R$string.app_link_open_ask);
        this.mNotAllowed = makeRadioPreference(KEY_LINK_OPEN_NEVER, C0017R$string.app_link_open_never);
        int linkStateToIndex = linkStateToIndex(this.mPackageManager.getIntentVerificationStatusAsUser(this.mPackageName, UserHandle.myUserId()));
        this.mCurrentIndex = linkStateToIndex;
        setRadioStatus(linkStateToIndex);
    }

    @Override // com.android.settings.widget.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        int preferenceKeyToIndex = preferenceKeyToIndex(radioButtonPreference.getKey());
        if (this.mCurrentIndex != preferenceKeyToIndex) {
            this.mCurrentIndex = preferenceKeyToIndex;
            setRadioStatus(preferenceKeyToIndex);
            updateAppLinkState(indexToLinkState(this.mCurrentIndex));
        }
    }

    private RadioButtonPreference makeRadioPreference(String str, int i) {
        RadioButtonPreference radioButtonPreference = new RadioButtonPreference(this.mPreferenceCategory.getContext());
        radioButtonPreference.setKey(str);
        radioButtonPreference.setTitle(i);
        radioButtonPreference.setOnClickListener(this);
        this.mPreferenceCategory.addPreference(radioButtonPreference);
        return radioButtonPreference;
    }

    private int preferenceKeyToIndex(String str) {
        int i = 0;
        while (true) {
            String[] strArr = this.mRadioKeys;
            if (i >= strArr.length) {
                return 1;
            }
            if (TextUtils.equals(str, strArr[i])) {
                return i;
            }
            i++;
        }
    }

    private void setRadioStatus(int i) {
        boolean z = false;
        this.mAllowOpening.setChecked(i == 0);
        this.mAskEveryTime.setChecked(i == 1);
        RadioButtonPreference radioButtonPreference = this.mNotAllowed;
        if (i == 2) {
            z = true;
        }
        radioButtonPreference.setChecked(z);
    }

    private boolean updateAppLinkState(int i) {
        int myUserId = UserHandle.myUserId();
        boolean z = false;
        if (this.mPackageManager.getIntentVerificationStatusAsUser(this.mPackageName, myUserId) == i) {
            return false;
        }
        boolean updateIntentVerificationStatusAsUser = this.mPackageManager.updateIntentVerificationStatusAsUser(this.mPackageName, i, myUserId);
        if (updateIntentVerificationStatusAsUser) {
            if (i == this.mPackageManager.getIntentVerificationStatusAsUser(this.mPackageName, myUserId)) {
                z = true;
            }
            return z;
        }
        Log.e(TAG, "Couldn't update intent verification status!");
        return updateIntentVerificationStatusAsUser;
    }

    /* access modifiers changed from: package-private */
    public int getEntriesNo() {
        return Utils.getHandledDomains(this.mPackageManager, this.mPackageName).size();
    }
}
