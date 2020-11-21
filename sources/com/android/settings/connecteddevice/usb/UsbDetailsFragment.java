package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class UsbDetailsFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.usb_details_fragment) {
        /* class com.android.settings.connecteddevice.usb.UsbDetailsFragment.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return new ArrayList(UsbDetailsFragment.createControllerList(context, new UsbBackend(context), null));
        }
    };
    private static final String TAG = UsbDetailsFragment.class.getSimpleName();
    private List<UsbDetailsController> mControllers;
    private UsbBackend mUsbBackend;
    private UsbConnectionBroadcastReceiver.UsbConnectionListener mUsbConnectionListener = new UsbConnectionBroadcastReceiver.UsbConnectionListener() {
        /* class com.android.settings.connecteddevice.usb.$$Lambda$UsbDetailsFragment$0qs6NXPaSCNUBBPVeTrwViGe6pk */

        @Override // com.android.settings.connecteddevice.usb.UsbConnectionBroadcastReceiver.UsbConnectionListener
        public final void onUsbConnectionChanged(boolean z, long j, int i, int i2) {
            UsbDetailsFragment.this.lambda$new$0$UsbDetailsFragment(z, j, i, i2);
        }
    };
    UsbConnectionBroadcastReceiver mUsbReceiver;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1291;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$UsbDetailsFragment(boolean z, long j, int i, int i2) {
        for (UsbDetailsController usbDetailsController : this.mControllers) {
            usbDetailsController.refresh(z, j, i, i2);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Utils.setActionBarShadowAnimation(getActivity(), getSettingsLifecycle(), getListView());
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return TAG;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.usb_details_fragment;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        UsbBackend usbBackend = new UsbBackend(context);
        this.mUsbBackend = usbBackend;
        this.mControllers = createControllerList(context, usbBackend, this);
        this.mUsbReceiver = new UsbConnectionBroadcastReceiver(context, this.mUsbConnectionListener, this.mUsbBackend);
        getSettingsLifecycle().addObserver(this.mUsbReceiver);
        return new ArrayList(this.mControllers);
    }

    /* access modifiers changed from: private */
    public static List<UsbDetailsController> createControllerList(Context context, UsbBackend usbBackend, UsbDetailsFragment usbDetailsFragment) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new UsbDetailsFunctionsController(context, usbDetailsFragment, usbBackend));
        arrayList.add(new UsbDetailsPowerRoleController(context, usbDetailsFragment, usbBackend));
        return arrayList;
    }
}
