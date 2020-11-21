package com.android.settings.wifi;

import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0012R$layout;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.settingslib.wifi.LongPressWifiEntryPreference;
import com.android.wifitrackerlib.WifiEntry;

public class ConnectedWifiEntryPreference extends LongPressWifiEntryPreference implements View.OnClickListener {
    private OnGearClickListener mOnGearClickListener;

    public interface OnGearClickListener {
        void onGearClick(ConnectedWifiEntryPreference connectedWifiEntryPreference);
    }

    public ConnectedWifiEntryPreference(Context context, WifiEntry wifiEntry, Fragment fragment) {
        super(context, wifiEntry, fragment);
        setLayoutResource(C0012R$layout.op_preference_access_point);
        setWidgetLayoutResource(R$layout.preference_widget_gear_optional_background);
    }

    public void setOnGearClickListener(OnGearClickListener onGearClickListener) {
        this.mOnGearClickListener = onGearClickListener;
        notifyChanged();
    }

    @Override // com.android.settingslib.wifi.WifiEntryPreference, com.android.settingslib.wifi.LongPressWifiEntryPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(R$id.settings_button);
        findViewById.setOnClickListener(this);
        boolean canSignIn = getWifiEntry().canSignIn();
        int i = 4;
        preferenceViewHolder.findViewById(R$id.settings_button_no_background).setVisibility(canSignIn ? 4 : 0);
        findViewById.setVisibility(canSignIn ? 0 : 4);
        View findViewById2 = preferenceViewHolder.findViewById(R$id.two_target_divider);
        if (canSignIn) {
            i = 0;
        }
        findViewById2.setVisibility(i);
    }

    @Override // com.android.settingslib.wifi.WifiEntryPreference
    public void onClick(View view) {
        OnGearClickListener onGearClickListener;
        if (view.getId() == R$id.settings_button && (onGearClickListener = this.mOnGearClickListener) != null) {
            onGearClickListener.onGearClick(this);
        }
    }
}
