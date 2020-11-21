package com.oneplus.settings.system;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0012R$layout;

public class OPSystemUpdatePreference extends Preference {
    private Context mContext;

    public OPSystemUpdatePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    public OPSystemUpdatePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPSystemUpdatePreference(Context context) {
        super(context);
        initViews(context);
    }

    private void initViews(Context context) {
        this.mContext = context;
        setWidgetLayoutResource(C0012R$layout.op_layout_sys_update_icon);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        int i = 0;
        boolean z = true;
        if (Settings.System.getInt(this.mContext.getContentResolver(), "has_new_version_to_update", 0) != 1) {
            z = false;
        }
        View findViewById = preferenceViewHolder.findViewById(16908312);
        findViewById.setOnClickListener(null);
        if (!z) {
            i = 8;
        }
        findViewById.setVisibility(i);
    }

    public void updateView() {
        notifyChanged();
    }
}
