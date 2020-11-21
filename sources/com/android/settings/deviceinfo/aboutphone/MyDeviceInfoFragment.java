package com.android.settings.deviceinfo.aboutphone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.Process;
import android.os.UserManager;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C0005R$bool;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.deviceinfo.BluetoothAddressPreferenceController;
import com.android.settings.deviceinfo.BrandNamePreferenceController;
import com.android.settings.deviceinfo.BuildNumberPreferenceController;
import com.android.settings.deviceinfo.DeviceNamePreferenceController;
import com.android.settings.deviceinfo.FccEquipmentIdPreferenceController;
import com.android.settings.deviceinfo.FeedbackPreferenceController;
import com.android.settings.deviceinfo.IpAddressPreferenceController;
import com.android.settings.deviceinfo.ManualPreferenceController;
import com.android.settings.deviceinfo.RegulatoryInfoPreferenceController;
import com.android.settings.deviceinfo.SafetyInfoPreferenceController;
import com.android.settings.deviceinfo.SoftwareVersionPreferenceController;
import com.android.settings.deviceinfo.StorageSizePreferenceController;
import com.android.settings.deviceinfo.UptimePreferenceController;
import com.android.settings.deviceinfo.WarrantCodePreferenceController;
import com.android.settings.deviceinfo.WifiMacAddressPreferenceController;
import com.android.settings.deviceinfo.imei.ImeiInfoPreferenceController;
import com.android.settings.deviceinfo.simstatus.SimStatusPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.LayoutPreference;
import com.oneplus.settings.OPCarrierConfigVersionPreferenceController;
import com.oneplus.settings.aboutphone.OPBaseBandPreferenceController;
import com.oneplus.settings.aboutphone.OPHardwareVersionInfoPreferenceController;
import com.oneplus.settings.aboutphone.OPKernelVersionPreferenceController;
import com.oneplus.settings.aboutphone.OPSerialNumberPreferenceController;
import com.oneplus.settings.aboutphone.OPUptimePreferenceController;
import com.oneplus.settings.product.OPAuthenticationInformationPreferenceController;
import com.oneplus.settings.product.OPDDRInfoController;
import com.oneplus.settings.product.OPMemoryInfoController;
import com.oneplus.settings.product.OPProductInfoPreferenceController;
import com.oneplus.settings.product.OPVersionInfoController;
import java.util.ArrayList;
import java.util.List;

public class MyDeviceInfoFragment extends DashboardFragment implements DeviceNamePreferenceController.DeviceNamePreferenceHost {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.my_device_info) {
        /* class com.android.settings.deviceinfo.aboutphone.MyDeviceInfoFragment.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return MyDeviceInfoFragment.buildPreferenceControllers(context, null, null);
        }
    };
    private BuildNumberPreferenceController mBuildNumberPreferenceController;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "MyDeviceInfoFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 40;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_uri_about;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((ImeiInfoPreferenceController) use(ImeiInfoPreferenceController.class)).setHost(this);
        ((DeviceNamePreferenceController) use(DeviceNamePreferenceController.class)).setHost(this);
        BuildNumberPreferenceController buildNumberPreferenceController = (BuildNumberPreferenceController) use(BuildNumberPreferenceController.class);
        this.mBuildNumberPreferenceController = buildNumberPreferenceController;
        buildNumberPreferenceController.setHost(this);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        initHeader();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.my_device_info;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this, getSettingsLifecycle());
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, MyDeviceInfoFragment myDeviceInfoFragment, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new SimStatusPreferenceController(context, myDeviceInfoFragment));
        arrayList.add(new IpAddressPreferenceController(context, lifecycle));
        arrayList.add(new WifiMacAddressPreferenceController(context, lifecycle));
        arrayList.add(new BluetoothAddressPreferenceController(context, lifecycle));
        arrayList.add(new RegulatoryInfoPreferenceController(context));
        arrayList.add(new SafetyInfoPreferenceController(context));
        arrayList.add(new ManualPreferenceController(context));
        arrayList.add(new FeedbackPreferenceController(myDeviceInfoFragment, context));
        arrayList.add(new FccEquipmentIdPreferenceController(context));
        arrayList.add(new UptimePreferenceController(context, lifecycle));
        arrayList.add(new SoftwareVersionPreferenceController(context));
        arrayList.add(new StorageSizePreferenceController(context));
        arrayList.add(new BrandNamePreferenceController(context));
        arrayList.add(new OPCarrierConfigVersionPreferenceController(context));
        arrayList.add(new OPProductInfoPreferenceController(context));
        arrayList.add(new OPVersionInfoController(context));
        arrayList.add(new OPDDRInfoController(context));
        arrayList.add(new OPMemoryInfoController(context));
        arrayList.add(new OPSerialNumberPreferenceController(context));
        arrayList.add(new OPHardwareVersionInfoPreferenceController(context));
        arrayList.add(new OPBaseBandPreferenceController(context));
        arrayList.add(new OPKernelVersionPreferenceController(context));
        arrayList.add(new OPUptimePreferenceController(context, lifecycle));
        arrayList.add(new OPAuthenticationInformationPreferenceController(context));
        arrayList.add(new OPLastFactoryResetPreferenceController(context));
        arrayList.add(new WarrantCodePreferenceController(context));
        return arrayList;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (!this.mBuildNumberPreferenceController.onActivityResult(i, i2, intent)) {
            super.onActivityResult(i, i2, intent);
        }
    }

    private void initHeader() {
        LayoutPreference layoutPreference = (LayoutPreference) getPreferenceScreen().findPreference("my_device_info_header");
        boolean z = getContext().getResources().getBoolean(C0005R$bool.config_show_device_header_in_device_info);
        layoutPreference.setVisible(z);
        if (z) {
            View findViewById = layoutPreference.findViewById(C0010R$id.entity_header);
            FragmentActivity activity = getActivity();
            Bundle arguments = getArguments();
            EntityHeaderController newInstance = EntityHeaderController.newInstance(activity, this, findViewById);
            newInstance.setRecyclerView(getListView(), getSettingsLifecycle());
            newInstance.setButtonActions(0, 0);
            if (arguments.getInt("icon_id", 0) == 0) {
                UserManager userManager = (UserManager) getActivity().getSystemService("user");
                UserInfo existingUser = Utils.getExistingUser(userManager, Process.myUserHandle());
                newInstance.setLabel(existingUser.name);
                newInstance.setIcon(com.android.settingslib.Utils.getUserIcon(getActivity(), userManager, existingUser));
            }
            newInstance.done((Activity) activity, true);
            layoutPreference.setVisible(false);
        }
    }

    @Override // com.android.settings.deviceinfo.DeviceNamePreferenceController.DeviceNamePreferenceHost
    public void showDeviceNameWarningDialog(String str) {
        DeviceNameWarningDialog.show(this);
    }

    public void onSetDeviceNameConfirm(boolean z) {
        ((DeviceNamePreferenceController) use(DeviceNamePreferenceController.class)).updateDeviceName(z);
    }
}
