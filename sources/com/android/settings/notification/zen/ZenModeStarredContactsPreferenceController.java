package com.android.settings.notification.zen;

import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

public class ZenModeStarredContactsPreferenceController extends AbstractZenModePreferenceController implements Preference.OnPreferenceClickListener {
    private Intent mFallbackIntent;
    private final PackageManager mPackageManager;
    private Preference mPreference;
    private final int mPriorityCategory;
    private Intent mStarredContactsIntent;

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(this.KEY);
        this.mPreference = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return this.KEY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        int i = this.mPriorityCategory;
        if (i == 8) {
            return this.mBackend.isPriorityCategoryEnabled(8) && this.mBackend.getPriorityCallSenders() == 2 && isIntentValid();
        }
        if (i == 4) {
            return this.mBackend.isPriorityCategoryEnabled(4) && this.mBackend.getPriorityMessageSenders() == 2 && isIntentValid();
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mBackend.getStarredContactsSummary(this.mContext);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, preference.getExtras().getInt("category"));
        if (this.mStarredContactsIntent.resolveActivity(this.mPackageManager) != null) {
            this.mContext.startActivity(this.mStarredContactsIntent);
            return true;
        }
        this.mContext.startActivity(this.mFallbackIntent);
        return true;
    }

    private boolean isIntentValid() {
        return (this.mStarredContactsIntent.resolveActivity(this.mPackageManager) == null && this.mFallbackIntent.resolveActivity(this.mPackageManager) == null) ? false : true;
    }
}
