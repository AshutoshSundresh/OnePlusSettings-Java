package com.android.settings.development;

import android.content.Context;
import android.debug.PairDevice;
import android.os.Bundle;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class AdbDeviceDetailsFragment extends DashboardFragment {
    private PairDevice mPairedDevice;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AdbDeviceDetailsFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1836;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        Bundle arguments = getArguments();
        if (arguments.containsKey("paired_device")) {
            this.mPairedDevice = arguments.getParcelable("paired_device");
        }
        super.onAttach(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.adb_device_details_fragment;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new AdbDeviceDetailsHeaderController(this.mPairedDevice, context, this));
        arrayList.add(new AdbDeviceDetailsActionController(this.mPairedDevice, context, this));
        arrayList.add(new AdbDeviceDetailsFingerprintController(this.mPairedDevice, context, this));
        return arrayList;
    }
}
