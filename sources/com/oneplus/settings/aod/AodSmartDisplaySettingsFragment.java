package com.oneplus.settings.aod;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Switch;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.widget.SwitchBar;

public class AodSmartDisplaySettingsFragment extends InstrumentedPreferenceFragment implements SwitchBar.OnSwitchChangeListener, Preference.OnPreferenceChangeListener {
    private boolean mAodSmartDisplayCurState;
    private boolean mCalendarEnabled;
    private SwitchPreference mCalendarPreference;
    private ContentResolver mContentResolver;
    private Context mContext;
    private int mCurrentUser;
    private boolean mMusicInfoEnabled;
    private SwitchPreference mMusicPreference;
    private SwitchBar mSwitchBar;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    public static AodSmartDisplaySettingsFragment newInstance() {
        return new AodSmartDisplaySettingsFragment();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_aod_smart_display_settings);
        Context baseContext = getActivity().getBaseContext();
        this.mContext = baseContext;
        this.mContentResolver = baseContext.getContentResolver();
        this.mCurrentUser = ActivityManager.getCurrentUser();
        loadSettings();
        SwitchBar switchBar = ((AodSmartDisplaySettingsActivity) getActivity()).getSwitchBar();
        this.mSwitchBar = switchBar;
        if (switchBar != null) {
            int i = C0017R$string.oneplus_aod_smart_display_title;
            switchBar.setSwitchBarText(i, i);
            this.mSwitchBar.setChecked(this.mAodSmartDisplayCurState);
            this.mSwitchBar.addOnSwitchChangeListener(this);
        }
        getPreferenceScreen();
        SwitchPreference switchPreference = (SwitchPreference) findPreference("aod_smart_display_music_info");
        this.mMusicPreference = switchPreference;
        switchPreference.setChecked(this.mMusicInfoEnabled);
        this.mMusicPreference.setOnPreferenceChangeListener(this);
        SwitchPreference switchPreference2 = (SwitchPreference) findPreference("aod_smart_display_calender");
        this.mCalendarPreference = switchPreference2;
        switchPreference2.setChecked(this.mCalendarEnabled);
        this.mCalendarPreference.setOnPreferenceChangeListener(this);
        boolean z = this.mAodSmartDisplayCurState;
        if (!z) {
            this.mMusicPreference.setEnabled(z);
            this.mCalendarPreference.setEnabled(this.mAodSmartDisplayCurState);
        }
    }

    @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
    public void onSwitchChanged(Switch r2, boolean z) {
        Log.d("AodSmartDisplaySettingsFragment", "onSwitchChanged(" + z + ")");
        this.mAodSmartDisplayCurState = z;
        this.mMusicPreference.setEnabled(z);
        this.mCalendarPreference.setEnabled(z);
        updateSettings();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Log.d("AodSmartDisplaySettingsFragment", "onPreferenceChange preference changed key = " + preference.getKey() + ", value = " + obj);
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (preference.getKey().equals("aod_smart_display_music_info")) {
            this.mMusicInfoEnabled = booleanValue;
        } else if (preference.getKey().equals("aod_smart_display_calender")) {
            this.mCalendarEnabled = booleanValue;
        }
        updateSettings();
        return true;
    }

    private void loadSettings() {
        boolean z = true;
        this.mAodSmartDisplayCurState = 1 == Settings.System.getIntForUser(this.mContentResolver, "aod_smart_display_cur_state", 1, this.mCurrentUser);
        this.mMusicInfoEnabled = 1 == Settings.System.getIntForUser(this.mContentResolver, "aod_smart_display_music_info_enabled", 1, this.mCurrentUser);
        if (1 != Settings.System.getIntForUser(this.mContentResolver, "aod_smart_display_calendar_enabled", 1, this.mCurrentUser)) {
            z = false;
        }
        this.mCalendarEnabled = z;
    }

    private void updateSettings() {
        ContentResolver contentResolver = this.mContentResolver;
        boolean z = this.mAodSmartDisplayCurState;
        Settings.System.putIntForUser(contentResolver, "aod_smart_display_cur_state", z ? 1 : 0, this.mCurrentUser);
        ContentResolver contentResolver2 = this.mContentResolver;
        boolean z2 = this.mMusicInfoEnabled;
        Settings.System.putIntForUser(contentResolver2, "aod_smart_display_music_info_enabled", z2 ? 1 : 0, this.mCurrentUser);
        ContentResolver contentResolver3 = this.mContentResolver;
        boolean z3 = this.mCalendarEnabled;
        Settings.System.putIntForUser(contentResolver3, "aod_smart_display_calendar_enabled", z3 ? 1 : 0, this.mCurrentUser);
    }
}
