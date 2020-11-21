package com.android.settings.datausage;

import android.content.Context;
import android.net.NetworkTemplate;
import android.util.AttributeSet;
import androidx.preference.Preference;
import com.android.settings.datausage.TemplatePreference;

public class NetworkRestrictionsPreference extends Preference implements TemplatePreference {
    @Override // com.android.settings.datausage.TemplatePreference
    public void setTemplate(NetworkTemplate networkTemplate, int i, TemplatePreference.NetworkServices networkServices) {
    }

    public NetworkRestrictionsPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}
