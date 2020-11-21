package com.android.settings.wifi;

import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.AccessPointPreference;

public class ConnectedAccessPointPreference extends LongPressAccessPointPreference implements View.OnClickListener {
    private boolean mIsCaptivePortal;
    private OnGearClickListener mOnGearClickListener;

    public interface OnGearClickListener {
        void onGearClick(ConnectedAccessPointPreference connectedAccessPointPreference);
    }

    public ConnectedAccessPointPreference(AccessPoint accessPoint, Context context, AccessPointPreference.UserBadgeCache userBadgeCache, int i, boolean z, Fragment fragment) {
        super(accessPoint, context, userBadgeCache, z, i, fragment);
    }

    @Override // androidx.preference.Preference
    public void setLayoutResource(int i) {
        super.setLayoutResource(C0012R$layout.op_preference_access_point);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.wifi.AccessPointPreference
    public int getWidgetLayoutResourceId() {
        return C0012R$layout.preference_widget_gear_optional_background;
    }

    @Override // com.android.settingslib.wifi.AccessPointPreference
    public void refresh() {
        super.refresh();
        setShowDivider(this.mIsCaptivePortal);
        if (this.mIsCaptivePortal) {
            setSummary(C0017R$string.wifi_tap_to_sign_in);
        }
    }

    public void setOnGearClickListener(OnGearClickListener onGearClickListener) {
        this.mOnGearClickListener = onGearClickListener;
        notifyChanged();
    }

    @Override // com.android.settings.wifi.LongPressAccessPointPreference, com.android.settingslib.wifi.AccessPointPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(C0010R$id.settings_button);
        findViewById.setOnClickListener(this);
        int i = 4;
        preferenceViewHolder.findViewById(C0010R$id.settings_button_no_background).setVisibility(this.mIsCaptivePortal ? 4 : 0);
        if (this.mIsCaptivePortal) {
            i = 0;
        }
        findViewById.setVisibility(i);
    }

    public void onClick(View view) {
        OnGearClickListener onGearClickListener;
        if (view.getId() == C0010R$id.settings_button && (onGearClickListener = this.mOnGearClickListener) != null) {
            onGearClickListener.onGearClick(this);
        }
    }

    public void setCaptivePortal(boolean z) {
        if (this.mIsCaptivePortal != z) {
            this.mIsCaptivePortal = z;
            refresh();
        }
    }
}
