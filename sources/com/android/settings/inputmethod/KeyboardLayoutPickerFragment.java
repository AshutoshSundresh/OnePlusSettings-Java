package com.android.settings.inputmethod;

import android.content.Context;
import android.hardware.input.InputDeviceIdentifier;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;

public class KeyboardLayoutPickerFragment extends DashboardFragment {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "KeyboardLayoutPicker";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 58;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        InputDeviceIdentifier parcelableExtra = getActivity().getIntent().getParcelableExtra("input_device_identifier");
        if (parcelableExtra == null) {
            getActivity().finish();
        }
        ((KeyboardLayoutPickerController) use(KeyboardLayoutPickerController.class)).initialize(this, parcelableExtra);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.keyboard_layout_picker_fragment;
    }
}
