package com.oneplus.settings;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.ui.RadioButtonPreference;
import com.oneplus.settings.ui.OPPhoneControlWayCategory;
import com.oneplus.settings.utils.OPUtils;

public class OPPhoneControlWaySettings extends SettingsPreferenceFragment implements RadioButtonPreference.OnClickListener, Preference.OnPreferenceChangeListener {
    private RadioButtonPreference mAlwaysShowNavigationBar;
    private Context mContext;
    private RadioButtonPreference mGestureNavigationBar;
    private Handler mHandler = new Handler();
    private RadioButtonPreference mLeftButtonNavigationBar;
    private OPPhoneControlWayCategory mOPPhoneControlWayCategory;

    private void showNavbar() {
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_phone_control_way_settings);
        this.mContext = SettingsBaseApplication.mApplication;
        getActivity().getWindow();
        initPref();
    }

    private void initPref() {
        this.mAlwaysShowNavigationBar = (RadioButtonPreference) findPreference("always_show_navigation_bar");
        this.mLeftButtonNavigationBar = (RadioButtonPreference) findPreference("hide_navigation_bar");
        this.mGestureNavigationBar = (RadioButtonPreference) findPreference("gesture_navigation_bar");
        this.mAlwaysShowNavigationBar.setOnClickListener(this);
        this.mLeftButtonNavigationBar.setOnClickListener(this);
        this.mGestureNavigationBar.setOnClickListener(this);
        this.mOPPhoneControlWayCategory = (OPPhoneControlWayCategory) findPreference("phone_control_way");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        updateUI();
        super.onResume();
        OPPhoneControlWayCategory oPPhoneControlWayCategory = this.mOPPhoneControlWayCategory;
        if (oPPhoneControlWayCategory != null) {
            oPPhoneControlWayCategory.startAnim();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        OPPhoneControlWayCategory oPPhoneControlWayCategory = this.mOPPhoneControlWayCategory;
        if (oPPhoneControlWayCategory != null) {
            oPPhoneControlWayCategory.stopAnim();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        OPPhoneControlWayCategory oPPhoneControlWayCategory = this.mOPPhoneControlWayCategory;
        if (oPPhoneControlWayCategory != null) {
            oPPhoneControlWayCategory.releaseAnim();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateUI() {
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "op_navigation_bar_type", 1);
        boolean z = false;
        this.mAlwaysShowNavigationBar.setChecked(i == 1);
        this.mLeftButtonNavigationBar.setChecked(i == 2);
        RadioButtonPreference radioButtonPreference = this.mGestureNavigationBar;
        if (i == 3) {
            z = true;
        }
        radioButtonPreference.setChecked(z);
        this.mAlwaysShowNavigationBar.setEnabled(true);
        this.mLeftButtonNavigationBar.setEnabled(true);
        this.mGestureNavigationBar.setEnabled(true);
    }

    @Override // com.android.settings.ui.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        RadioButtonPreference radioButtonPreference2 = this.mAlwaysShowNavigationBar;
        if (radioButtonPreference == radioButtonPreference2) {
            radioButtonPreference2.setChecked(true);
            this.mLeftButtonNavigationBar.setChecked(false);
            this.mGestureNavigationBar.setChecked(false);
            showNavbar();
            delayHideNavkey();
            setNavigationType(1);
        } else if (radioButtonPreference == this.mLeftButtonNavigationBar) {
            radioButtonPreference2.setChecked(false);
            this.mLeftButtonNavigationBar.setChecked(true);
            this.mGestureNavigationBar.setChecked(false);
            showNavbar();
            delayHideNavkey();
            setNavigationType(2);
        } else if (radioButtonPreference == this.mGestureNavigationBar) {
            radioButtonPreference2.setChecked(false);
            this.mLeftButtonNavigationBar.setChecked(false);
            this.mGestureNavigationBar.setChecked(true);
            Settings.System.putInt(this.mContext.getContentResolver(), "lock_to_app_enabled", 0);
            delayHideNavkey();
            setNavigationType(3);
        }
    }

    private void delayHideNavkey() {
        this.mAlwaysShowNavigationBar.setEnabled(false);
        this.mLeftButtonNavigationBar.setEnabled(false);
        this.mGestureNavigationBar.setEnabled(false);
        this.mHandler.postDelayed(new Runnable() {
            /* class com.oneplus.settings.OPPhoneControlWaySettings.AnonymousClass1 */

            public void run() {
                OPPhoneControlWaySettings.this.mAlwaysShowNavigationBar.setEnabled(true);
                OPPhoneControlWaySettings.this.mLeftButtonNavigationBar.setEnabled(true);
                OPPhoneControlWaySettings.this.mGestureNavigationBar.setEnabled(true);
                OPPhoneControlWaySettings.this.updateUI();
            }
        }, 1000);
    }

    private void setNavigationType(int i) {
        Settings.System.putInt(this.mContext.getContentResolver(), "op_navigation_bar_type", i);
        this.mOPPhoneControlWayCategory.setViewType(i);
        OPUtils.sendAppTracker("op_fullscreen_gesture_enabled", i);
    }
}
