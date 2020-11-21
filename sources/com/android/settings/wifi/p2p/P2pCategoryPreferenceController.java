package com.android.settings.wifi.p2p;

import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public abstract class P2pCategoryPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    protected PreferenceGroup mCategory;

    public P2pCategoryPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mCategory.getPreferenceCount() > 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mCategory = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
        super.displayPreference(preferenceScreen);
    }

    public void removeAllChildren() {
        PreferenceGroup preferenceGroup = this.mCategory;
        if (preferenceGroup != null) {
            preferenceGroup.removeAll();
            this.mCategory.setVisible(false);
        }
    }

    public void addChild(Preference preference) {
        PreferenceGroup preferenceGroup = this.mCategory;
        if (preferenceGroup != null) {
            preferenceGroup.addPreference(preference);
            this.mCategory.setVisible(true);
        }
    }

    public void setEnabled(boolean z) {
        PreferenceGroup preferenceGroup = this.mCategory;
        if (preferenceGroup != null) {
            preferenceGroup.setEnabled(z);
        }
    }
}
