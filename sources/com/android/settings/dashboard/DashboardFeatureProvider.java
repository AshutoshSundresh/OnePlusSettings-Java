package com.android.settings.dashboard;

import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.Tile;
import java.util.List;

public interface DashboardFeatureProvider {
    List<DynamicDataObserver> bindPreferenceToTileAndGetObservers(FragmentActivity fragmentActivity, boolean z, int i, Preference preference, Tile tile, String str, int i2);

    List<DashboardCategory> getAllCategories();

    String getDashboardKeyForTile(Tile tile);

    DashboardCategory getTilesForCategory(String str);
}
