package com.android.settings.accounts;

import android.content.Context;
import android.os.UserManager;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;

public class ChooseAccountFragment extends DashboardFragment {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ChooseAccountFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 10;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((ChooseAccountPreferenceController) use(ChooseAccountPreferenceController.class)).initialize(getIntent().getStringArrayExtra("authorities"), getIntent().getStringArrayExtra("account_types"), Utils.getSecureTargetUser(getActivity().getActivityToken(), UserManager.get(getContext()), null, getIntent().getExtras()), getActivity());
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.add_account_settings;
    }
}
