package com.android.settingslib.wifi;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceViewHolder;
import com.android.wifitrackerlib.WifiEntry;

public class LongPressWifiEntryPreference extends WifiEntryPreference {
    private final Fragment mFragment;

    public LongPressWifiEntryPreference(Context context, WifiEntry wifiEntry, Fragment fragment) {
        super(context, wifiEntry);
        this.mFragment = fragment;
    }

    @Override // com.android.settingslib.wifi.WifiEntryPreference, androidx.preference.Preference
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
