package com.android.settings.notification.zen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenRuleStarredContactsPreferenceController extends AbstractZenCustomRulePreferenceController implements Preference.OnPreferenceClickListener {
    private Intent mFallbackIntent;
    private final PackageManager mPackageManager = this.mContext.getPackageManager();
    private Preference mPreference;
    private final int mPriorityCategory;
    private Intent mStarredContactsIntent = new Intent("com.android.contacts.action.LIST_STARRED");

    public ZenRuleStarredContactsPreferenceController(Context context, Lifecycle lifecycle, int i, String str) {
        super(context, str, lifecycle);
        this.mPriorityCategory = i;
        Intent intent = new Intent("android.intent.action.MAIN");
        this.mFallbackIntent = intent;
        intent.addCategory("android.intent.category.APP_CONTACTS");
    }

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

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController
    public boolean isAvailable() {
        if (!super.isAvailable() || this.mRule.getZenPolicy() == null || !isIntentValid()) {
            return false;
        }
        int i = this.mPriorityCategory;
        if (i == 3) {
            if (this.mRule.getZenPolicy().getPriorityCallSenders() == 3) {
                return true;
            }
            return false;
        } else if (i == 2 && this.mRule.getZenPolicy().getPriorityMessageSenders() == 3) {
            return true;
        } else {
            return false;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mBackend.getStarredContactsSummary(this.mContext);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
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
