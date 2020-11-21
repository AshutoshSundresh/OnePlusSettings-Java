package com.android.settings.applications;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceCategory;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.widget.RadioButtonPreference;
import com.android.settingslib.widget.FooterPreference;
import java.util.Iterator;

public class OpenSupportedLinks extends AppInfoWithHeader implements RadioButtonPreference.OnClickListener {
    RadioButtonPreference mAllowOpening;
    RadioButtonPreference mAskEveryTime;
    private int mCurrentIndex;
    RadioButtonPreference mNotAllowed;
    PackageManager mPackageManager;
    PreferenceCategory mPreferenceCategory;
    private String[] mRadioKeys = {"app_link_open_always", "app_link_open_ask", "app_link_open_never"};

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

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1824;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public boolean refreshUi() {
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.applications.AppInfoBase, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mPackageManager = getPackageManager();
        addPreferencesFromResource(C0019R$xml.open_supported_links);
        initRadioPreferencesGroup();
        updateFooterPreference();
    }

    /* access modifiers changed from: package-private */
    public void initRadioPreferencesGroup() {
        this.mPreferenceCategory = (PreferenceCategory) findPreference("supported_links_radio_group");
        this.mAllowOpening = makeRadioPreference("app_link_open_always", C0017R$string.app_link_open_always);
        int entriesNo = getEntriesNo();
        this.mAllowOpening.setAppendixVisibility(8);
        this.mAllowOpening.setSummary(getResources().getQuantityString(C0015R$plurals.app_link_open_always_summary, entriesNo, Integer.valueOf(entriesNo)));
        this.mAskEveryTime = makeRadioPreference("app_link_open_ask", C0017R$string.app_link_open_ask);
        this.mNotAllowed = makeRadioPreference("app_link_open_never", C0017R$string.app_link_open_never);
        int linkStateToIndex = linkStateToIndex(this.mPackageManager.getIntentVerificationStatusAsUser(this.mPackageName, this.mUserId));
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

    /* access modifiers changed from: package-private */
    public int getEntriesNo() {
        return Utils.getHandledDomains(this.mPackageManager, this.mPackageName).size();
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

    private void updateAppLinkState(int i) {
        if (this.mPackageManager.getIntentVerificationStatusAsUser(this.mPackageName, this.mUserId) != i) {
            if (this.mPackageManager.updateIntentVerificationStatusAsUser(this.mPackageName, i, this.mUserId)) {
                this.mPackageManager.getIntentVerificationStatusAsUser(this.mPackageName, this.mUserId);
            } else {
                Log.e("OpenSupportedLinks", "Couldn't update intent verification status!");
            }
        }
    }

    private void updateFooterPreference() {
        FooterPreference footerPreference = (FooterPreference) findPreference("supported_links_footer");
        if (footerPreference == null) {
            Log.w("OpenSupportedLinks", "Can't find the footer preference.");
        } else {
            addLinksToFooter(footerPreference);
        }
    }

    /* access modifiers changed from: package-private */
    public void addLinksToFooter(FooterPreference footerPreference) {
        ArraySet<String> handledDomains = Utils.getHandledDomains(this.mPackageManager, this.mPackageName);
        if (handledDomains.isEmpty()) {
            Log.w("OpenSupportedLinks", "Can't find any app links.");
            return;
        }
        String str = ((Object) footerPreference.getTitle()) + System.lineSeparator();
        Iterator<String> it = handledDomains.iterator();
        while (it.hasNext()) {
            str = ((Object) str) + System.lineSeparator() + it.next();
        }
        footerPreference.setTitle(str);
    }
}
