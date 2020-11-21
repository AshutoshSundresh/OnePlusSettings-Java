package com.android.settings.nfc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public class PaymentSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.nfc_payment_settings) {
        /* class com.android.settings.nfc.PaymentSettings.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            if (((UserManager) context.getSystemService(UserManager.class)).getUserInfo(UserHandle.myUserId()).isGuest()) {
                return false;
            }
            return context.getPackageManager().hasSystemFeature("android.hardware.nfc");
        }
    };
    private PaymentBackend mPaymentBackend;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "PaymentSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 70;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.nfc_payment_settings;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mPaymentBackend = new PaymentBackend(getActivity());
        setHasOptionsMenu(true);
        ((NfcPaymentPreferenceController) use(NfcPaymentPreferenceController.class)).setPaymentBackend(this.mPaymentBackend);
        ((NfcForegroundPreferenceController) use(NfcForegroundPreferenceController.class)).setPaymentBackend(this.mPaymentBackend);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (isShowEmptyImage(getPreferenceScreen())) {
            ((ViewGroup) view.findViewById(16908351)).addView(getActivity().getLayoutInflater().inflate(C0012R$layout.nfc_payment_empty, (ViewGroup) null, false));
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        this.mPaymentBackend.onResume();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mPaymentBackend.onPause();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        MenuItem add = menu.add(C0017R$string.nfc_payment_how_it_works);
        add.setIntent(new Intent(getActivity(), HowItWorks.class));
        add.setShowAsActionFlags(0);
    }

    /* access modifiers changed from: package-private */
    public boolean isShowEmptyImage(PreferenceScreen preferenceScreen) {
        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
            if (preferenceScreen.getPreference(i).isVisible()) {
                return false;
            }
        }
        return true;
    }
}
