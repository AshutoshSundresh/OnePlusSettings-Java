package com.oneplus.settings.faceunlock;

import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.ui.RadioButtonPreference;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.OPFaceUnlockModeLottieViewCategory;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPFaceUnlockModeSettings extends SettingsPreferenceFragment implements RadioButtonPreference.OnClickListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.faceunlock.OPFaceUnlockModeSettings.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_face_unlock_mode_settings;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            return new ArrayList();
        }
    };
    private Context mContext;
    private OPFaceUnlockModeLottieViewCategory mRetainModeView;
    private RadioButtonPreference mSwipeUpMode;
    private RadioButtonPreference mUsePowerButton;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_face_unlock_mode_settings);
        this.mContext = SettingsBaseApplication.mApplication;
        this.mSwipeUpMode = (RadioButtonPreference) findPreference("key_faceunlock_swipe_up_mode");
        this.mUsePowerButton = (RadioButtonPreference) findPreference("key_faceunlock_use_power_button_mode");
        this.mSwipeUpMode.setOnClickListener(this);
        this.mUsePowerButton.setOnClickListener(this);
        this.mRetainModeView = (OPFaceUnlockModeLottieViewCategory) findPreference("key_faceunlock_mode_retain_view");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        if (!(this.mSwipeUpMode == null || this.mUsePowerButton == null)) {
            boolean z = false;
            int i = Settings.System.getInt(this.mContext.getContentResolver(), "oneplus_face_unlock_powerkey_recognize_enable", 0);
            this.mSwipeUpMode.setChecked(i == 0);
            RadioButtonPreference radioButtonPreference = this.mUsePowerButton;
            if (i == 1) {
                z = true;
            }
            radioButtonPreference.setChecked(z);
        }
        super.onResume();
    }

    @Override // com.android.settings.ui.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        RadioButtonPreference radioButtonPreference2 = this.mSwipeUpMode;
        if (radioButtonPreference == radioButtonPreference2) {
            radioButtonPreference2.setChecked(true);
            this.mUsePowerButton.setChecked(false);
            Settings.System.putInt(this.mContext.getContentResolver(), "oneplus_face_unlock_powerkey_recognize_enable", 0);
            OPUtils.sendAppTracker("pop_up_face_unlock", 0);
            setRetainViewMode(0);
        } else if (radioButtonPreference == this.mUsePowerButton) {
            radioButtonPreference2.setChecked(false);
            this.mUsePowerButton.setChecked(true);
            Settings.System.putInt(this.mContext.getContentResolver(), "oneplus_face_unlock_powerkey_recognize_enable", 1);
            OPUtils.sendAppTracker("pop_up_face_unlock", 1);
            setRetainViewMode(1);
        }
    }

    private void setRetainViewMode(int i) {
        OPFaceUnlockModeLottieViewCategory oPFaceUnlockModeLottieViewCategory = this.mRetainModeView;
        if (oPFaceUnlockModeLottieViewCategory != null) {
            oPFaceUnlockModeLottieViewCategory.setViewType(getUnlockMode());
        }
    }

    public int getUnlockMode() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "oneplus_face_unlock_powerkey_recognize_enable", 0);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        OPFaceUnlockModeLottieViewCategory oPFaceUnlockModeLottieViewCategory = this.mRetainModeView;
        if (oPFaceUnlockModeLottieViewCategory != null) {
            oPFaceUnlockModeLottieViewCategory.stopAnim();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        OPFaceUnlockModeLottieViewCategory oPFaceUnlockModeLottieViewCategory = this.mRetainModeView;
        if (oPFaceUnlockModeLottieViewCategory != null) {
            oPFaceUnlockModeLottieViewCategory.releaseAnim();
        }
    }
}
