package com.android.settings.wifi;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.AccessPointPreference;

public class LongPressAccessPointPreference extends AccessPointPreference {
    private final Fragment mFragment;

    public LongPressAccessPointPreference(AccessPoint accessPoint, Context context, AccessPointPreference.UserBadgeCache userBadgeCache, boolean z, int i, Fragment fragment) {
        super(accessPoint, context, userBadgeCache, i, z);
        this.mFragment = fragment;
    }

    @Override // com.android.settingslib.wifi.AccessPointPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        Fragment fragment = this.mFragment;
        if (fragment != null) {
            preferenceViewHolder.itemView.setOnCreateContextMenuListener(fragment);
            preferenceViewHolder.itemView.setTag(this);
            preferenceViewHolder.itemView.setLongClickable(true);
        }
    }
}
