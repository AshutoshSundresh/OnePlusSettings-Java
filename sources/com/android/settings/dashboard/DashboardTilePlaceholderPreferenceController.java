package com.android.settings.dashboard;

import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

/* access modifiers changed from: package-private */
public class DashboardTilePlaceholderPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private int mOrder = Integer.MAX_VALUE;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "dashboard_tile_placeholder";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return false;
    }

    public DashboardTilePlaceholderPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        if (findPreference != null) {
            this.mOrder = findPreference.getOrder();
            preferenceScreen.removePreference(findPreference);
        }
    }

    public int getOrder() {
        return this.mOrder;
    }
}
