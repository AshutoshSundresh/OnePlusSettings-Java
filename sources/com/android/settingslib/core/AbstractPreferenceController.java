package com.android.settingslib.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

public abstract class AbstractPreferenceController {
    private static final String TAG = "AbstractPrefController";
    protected final Context mContext;

    public abstract String getPreferenceKey();

    public CharSequence getSummary() {
        return null;
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        return false;
    }

    public abstract boolean isAvailable();

    public AbstractPreferenceController(Context context) {
        this.mContext = context;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        String preferenceKey = getPreferenceKey();
        if (TextUtils.isEmpty(preferenceKey)) {
            Log.w(TAG, "Skipping displayPreference because key is empty:" + getClass().getName());
        } else if (isAvailable()) {
            setVisible(preferenceScreen, preferenceKey, true);
            if (this instanceof Preference.OnPreferenceChangeListener) {
                preferenceScreen.findPreference(preferenceKey).setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) this);
            }
        } else {
            setVisible(preferenceScreen, preferenceKey, false);
        }
    }

    public void updateState(Preference preference) {
        refreshSummary(preference);
    }

    /* access modifiers changed from: protected */
    public void refreshSummary(Preference preference) {
        CharSequence summary;
        if (preference != null && (summary = getSummary()) != null) {
            preference.setSummary(summary);
        }
    }

    /* access modifiers changed from: protected */
    public final void setVisible(PreferenceGroup preferenceGroup, String str, boolean z) {
        Preference findPreference = preferenceGroup.findPreference(str);
        if (findPreference != null) {
            findPreference.setVisible(z);
        }
    }
}
