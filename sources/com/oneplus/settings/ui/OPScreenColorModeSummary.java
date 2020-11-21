package com.oneplus.settings.ui;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settingslib.RestrictedPreference;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.better.OPReadingModeTurnOnPreferenceController;

public class OPScreenColorModeSummary extends RestrictedPreference {
    private Context mContext;
    private TextView mSummary;

    public OPScreenColorModeSummary(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mContext = context;
        setLayoutResource(C0012R$layout.op_screen_color_mode_summary);
    }

    public OPScreenColorModeSummary(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public OPScreenColorModeSummary(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OPScreenColorModeSummary(Context context) {
        this(context, null);
    }

    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mSummary = (TextView) preferenceViewHolder.findViewById(C0010R$id.summary);
        boolean z = false;
        boolean z2 = Settings.Secure.getInt(this.mContext.getContentResolver(), "night_display_activated", 0) != 1;
        if (Settings.System.getInt(this.mContext.getContentResolver(), OPReadingModeTurnOnPreferenceController.READING_MODE_STATUS_MANUAL, 0) != 1) {
            z = true;
        }
        if (!z2) {
            this.mSummary.setText(SettingsBaseApplication.mApplication.getText(C0017R$string.oneplus_screen_color_mode_title_summary));
        }
        if (!z) {
            this.mSummary.setText(SettingsBaseApplication.mApplication.getText(C0017R$string.oneplus_screen_color_mode_reading_mode_on_summary));
        }
    }

    public void setTextSummary(String str) {
        TextView textView = this.mSummary;
        if (textView != null) {
            textView.setText(str);
        }
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public void setSummary(CharSequence charSequence) {
        setTextSummary(charSequence.toString());
    }
}
