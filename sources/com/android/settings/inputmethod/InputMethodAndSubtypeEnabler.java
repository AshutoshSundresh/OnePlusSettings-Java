package com.android.settings.inputmethod;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;

public class InputMethodAndSubtypeEnabler extends DashboardFragment {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "InputMethodAndSubtypeEnabler";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 60;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.input_methods_subtype;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((InputMethodAndSubtypePreferenceController) use(InputMethodAndSubtypePreferenceController.class)).initialize(this, getStringExtraFromIntentOrArguments("input_method_id"));
    }

    private String getStringExtraFromIntentOrArguments(String str) {
        String stringExtra = getActivity().getIntent().getStringExtra(str);
        if (stringExtra != null) {
            return stringExtra;
        }
        Bundle arguments = getArguments();
        if (arguments == null) {
            return null;
        }
        return arguments.getString(str);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        String stringExtraFromIntentOrArguments = getStringExtraFromIntentOrArguments("android.intent.extra.TITLE");
        if (!TextUtils.isEmpty(stringExtraFromIntentOrArguments)) {
            getActivity().setTitle(stringExtraFromIntentOrArguments);
        }
    }
}
