package com.android.settings.connecteddevice;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.bluetooth.BluetoothDevicePreference;
import com.android.settings.bluetooth.BluetoothDeviceRenamePreferenceController;
import com.android.settings.bluetooth.BluetoothFilesPreferenceController;
import com.android.settings.bluetooth.BluetoothSwitchPreferenceController;
import com.android.settings.bluetooth.OPBluetoothDiscoverablePreferenceController;
import com.android.settings.bluetooth.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBarController;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.widget.OPFooterPreference;
import com.oos.onepluspods.service.aidl.IOnePlusPodDevice;
import com.oos.onepluspods.service.aidl.IOnePlusUpdate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BluetoothDashboardFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.bluetooth_screen);
    private static OPBluetoothDiscoverablePreferenceController mOPBluetoothDiscoverablePreferenceController;
    private ServiceConnection mConnecttion = new ServiceConnection() {
        /* class com.android.settings.connecteddevice.BluetoothDashboardFragment.AnonymousClass1 */

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("BluetoothDashboardFrag", "onServiceConnected componentName = " + componentName + " iBinder = " + iBinder);
            BluetoothDashboardFragment.this.mPodsService = IOnePlusPodDevice.Stub.asInterface(iBinder);
            Set<String> keySet = BluetoothDashboardFragment.this.mTwsPreferences.keySet();
            ArrayList arrayList = new ArrayList();
            try {
                for (String str : keySet) {
                    if (BluetoothDashboardFragment.this.mPodsService.isOnePlusPods(str)) {
                        BluetoothDashboardFragment.this.mPodsService.setIOnePlusUpdate(BluetoothDashboardFragment.this.mStub);
                        BluetoothDashboardFragment.this.updatePreferecenSummary(str);
                    } else {
                        arrayList.add(str);
                    }
                }
            } catch (Exception e) {
                Log.d("BluetoothDashboardFrag", "onServiceConnected e = " + e);
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                BluetoothDashboardFragment.this.mTwsPreferences.remove((String) it.next());
            }
            arrayList.clear();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("BluetoothDashboardFrag", "onServiceDisconnected componentName = " + componentName);
        }
    };
    private Context mContext = SettingsBaseApplication.getContext();
    private BluetoothSwitchPreferenceController mController;
    private OPFooterPreference mFooterPreference;
    private IOnePlusPodDevice mPodsService;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.connecteddevice.BluetoothDashboardFragment.AnonymousClass3 */

        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.headset.action.VENDOR_SPECIFIC_HEADSET_EVENT".equals(intent.getAction())) {
                try {
                    if (BluetoothDashboardFragment.this.mPodsService != null) {
                        BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                        String address = bluetoothDevice.getAddress();
                        if (bluetoothDevice != null && BluetoothDashboardFragment.this.mPodsService.isOnePlusPods(address)) {
                            String stringExtra = intent.getStringExtra("android.bluetooth.headset.extra.VENDOR_SPECIFIC_HEADSET_EVENT_CMD");
                            Object[] objArr = (Object[]) intent.getExtras().get("android.bluetooth.headset.extra.VENDOR_SPECIFIC_HEADSET_EVENT_ARGS");
                            if (stringExtra != null && objArr != null && objArr.length == 7) {
                                int i = 1;
                                int intValue = (((Integer) objArr[2]).intValue() + 1) * 10;
                                int intValue2 = (((Integer) objArr[4]).intValue() + 1) * 10;
                                if (!(objArr[6] instanceof String)) {
                                    i = (((Integer) objArr[6]).intValue() + 1) * 10;
                                }
                                Log.d("BluetoothDashboardFrag", "ACTION_VENDOR_SPECIFIC_HEADSET_EVENT address = " + address + ", leftLevel: " + intValue + ", rightLevel: " + intValue2 + ", boxLevel: " + i);
                                String batteryString = BluetoothDashboardFragment.this.getBatteryString(intValue, intValue2, i);
                                BluetoothDevicePreference bluetoothDevicePreference = (BluetoothDevicePreference) BluetoothDashboardFragment.this.mTwsPreferences.get(address);
                                if (bluetoothDevicePreference == null) {
                                    bluetoothDevicePreference = BluetoothDashboardFragment.this.findPreferenceFromAddress(address, "available_device_list");
                                }
                                if (bluetoothDevicePreference == null) {
                                    bluetoothDevicePreference = BluetoothDashboardFragment.this.findPreferenceFromAddress(address, "connected_device_list");
                                }
                                if (bluetoothDevicePreference != null) {
                                    bluetoothDevicePreference.setSummary(batteryString);
                                    bluetoothDevicePreference.setTwsBattery(batteryString);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d("BluetoothDashboardFrag", "onReceive e2 = " + e);
                }
            }
        }
    };
    private IOnePlusUpdate.Stub mStub = new IOnePlusUpdate.Stub() {
        /* class com.android.settings.connecteddevice.BluetoothDashboardFragment.AnonymousClass2 */

        @Override // com.oos.onepluspods.service.aidl.IOnePlusUpdate
        public void updateView(final String str, final int i) {
            new Handler(BluetoothDashboardFragment.this.mContext.getMainLooper()).post(new Runnable() {
                /* class com.android.settings.connecteddevice.BluetoothDashboardFragment.AnonymousClass2.AnonymousClass1 */

                public void run() {
                    try {
                        Log.d("BluetoothDashboardFrag", "updateView address = " + str + " content = " + i);
                        if (i == 2) {
                            BluetoothDashboardFragment.this.updatePreferecenSummary(str);
                        }
                    } catch (Exception e) {
                        Log.d("BluetoothDashboardFrag", "updateView e = " + e);
                    }
                }
            });
        }
    };
    private SwitchBar mSwitchBar;
    private HashMap<String, BluetoothDevicePreference> mTwsPreferences = new HashMap<>();

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BluetoothDashboardFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1390;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_uri_bluetooth_screen;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.bluetooth_screen;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildControllers(context, getSettingsLifecycle());
    }

    private static List<AbstractPreferenceController> buildControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        OPBluetoothDiscoverablePreferenceController oPBluetoothDiscoverablePreferenceController = new OPBluetoothDiscoverablePreferenceController(context, lifecycle);
        mOPBluetoothDiscoverablePreferenceController = oPBluetoothDiscoverablePreferenceController;
        arrayList.add(oPBluetoothDiscoverablePreferenceController);
        arrayList.add(new BluetoothFilesPreferenceController(context));
        return arrayList;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mFooterPreference = (OPFooterPreference) findPreference("bluetooth_screen_footer");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.headset.action.VENDOR_SPECIFIC_HEADSET_EVENT");
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        OPBluetoothDiscoverablePreferenceController oPBluetoothDiscoverablePreferenceController = mOPBluetoothDiscoverablePreferenceController;
        if (oPBluetoothDiscoverablePreferenceController != null) {
            oPBluetoothDiscoverablePreferenceController.setVisible();
        }
        IOnePlusPodDevice iOnePlusPodDevice = this.mPodsService;
        if (iOnePlusPodDevice != null) {
            try {
                iOnePlusPodDevice.setIOnePlusUpdate(this.mStub);
                if (this.mTwsPreferences.size() > 0) {
                    updateAllPreferenceSummary();
                }
            } catch (Exception e) {
                Log.d("BluetoothDashboardFrag", "onResume e = " + e);
            }
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((BluetoothDeviceRenamePreferenceController) use(BluetoothDeviceRenamePreferenceController.class)).setFragment(this);
        ((AvailableMediaDeviceGroupController) use(AvailableMediaDeviceGroupController.class)).init(this);
        ((ConnectedDeviceGroupController) use(ConnectedDeviceGroupController.class)).init(this);
        ((PreviouslyConnectedDevicePreferenceController) use(PreviouslyConnectedDevicePreferenceController.class)).init(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        SwitchBar switchBar = settingsActivity.getSwitchBar();
        this.mSwitchBar = switchBar;
        this.mController = new BluetoothSwitchPreferenceController(settingsActivity, new SwitchBarController(switchBar), this.mFooterPreference);
        Lifecycle settingsLifecycle = getSettingsLifecycle();
        if (settingsLifecycle != null) {
            settingsLifecycle.addObserver(this.mController);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        try {
            this.mPodsService.setIOnePlusUpdate(null);
            this.mContext.unbindService(this.mConnecttion);
            this.mPodsService = null;
        } catch (Exception e) {
            Log.d("BluetoothDashboardFrag", "unbindService e = " + e);
        }
        try {
            this.mContext.unregisterReceiver(this.mReceiver);
        } catch (Exception e2) {
            Log.d("BluetoothDashboardFrag", "unregisterReceiver e = " + e2);
        }
        super.onDestroy();
    }

    public void checkOnePlusPods(BluetoothDevicePreference bluetoothDevicePreference) {
        CachedBluetoothDevice cachedDevice = getCachedDevice(bluetoothDevicePreference.getBluetoothDevice().getAddress());
        Log.d("BluetoothDashboardFrag", "onActivityCreated device.getname = " + cachedDevice.getName() + " isConnectedHfpDevice = " + cachedDevice.isConnectedHfpDevice() + " isConnectedA2dpDevice = " + cachedDevice.isConnectedA2dpDevice());
        if (cachedDevice.isConnectedHfpDevice() || cachedDevice.isConnectedA2dpDevice()) {
            IOnePlusPodDevice iOnePlusPodDevice = this.mPodsService;
            if (iOnePlusPodDevice != null) {
                try {
                    if (iOnePlusPodDevice.isOnePlusPods(cachedDevice.getAddress())) {
                        this.mPodsService.setIOnePlusUpdate(this.mStub);
                        this.mTwsPreferences.put(cachedDevice.getAddress(), bluetoothDevicePreference);
                        updatePreferecenSummary(cachedDevice.getAddress());
                        bluetoothDevicePreference.setTwsAddress(true);
                        return;
                    }
                    bluetoothDevicePreference.setTwsAddress(false);
                    bluetoothDevicePreference.setTwsBattery("");
                } catch (Exception e) {
                    Log.d("BluetoothDashboardFrag", "checkOnePlusPods isOnePlusPods e = " + e);
                }
            } else {
                this.mTwsPreferences.put(cachedDevice.getAddress(), bluetoothDevicePreference);
                bindOnePlusPodsService(cachedDevice);
            }
        }
    }

    private void bindOnePlusPodsService(CachedBluetoothDevice cachedBluetoothDevice) {
        Intent intent = new Intent();
        intent.setClassName("com.oneplus.twspods", "com.oos.onepluspods.service.MultiDeviceCoreService");
        intent.putExtra("address", cachedBluetoothDevice.getAddress());
        intent.putExtra("device", cachedBluetoothDevice.getDevice());
        this.mContext.bindService(intent, this.mConnecttion, 1);
    }

    /* access modifiers changed from: package-private */
    public CachedBluetoothDevice getCachedDevice(String str) {
        LocalBluetoothManager localBluetoothManager = getLocalBluetoothManager(this.mContext);
        return localBluetoothManager.getCachedDeviceManager().findDevice(localBluetoothManager.getBluetoothAdapter().getRemoteDevice(str));
    }

    /* access modifiers changed from: package-private */
    public LocalBluetoothManager getLocalBluetoothManager(Context context) {
        return Utils.getLocalBtManager(context);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private BluetoothDevicePreference findPreferenceFromAddress(String str, String str2) {
        BluetoothDevicePreference bluetoothDevicePreference;
        CachedBluetoothDevice bluetoothDevice;
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference(str2);
        if (preferenceCategory == null) {
            return null;
        }
        int preferenceCount = preferenceCategory.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceCategory.getPreference(i);
            if ((preference instanceof BluetoothDevicePreference) && (bluetoothDevice = (bluetoothDevicePreference = (BluetoothDevicePreference) preference).getBluetoothDevice()) != null && str.equals(bluetoothDevice.getAddress())) {
                this.mTwsPreferences.put(str, bluetoothDevicePreference);
                return bluetoothDevicePreference;
            }
        }
        return null;
    }

    private void updateAllPreferenceSummary() {
        for (String str : this.mTwsPreferences.keySet()) {
            CachedBluetoothDevice cachedDevice = getCachedDevice(this.mTwsPreferences.get(str).getBluetoothDevice().getAddress());
            if (cachedDevice.isConnectedHfpDevice() || cachedDevice.isConnectedA2dpDevice()) {
                try {
                    updatePreferecenSummary(cachedDevice.getAddress());
                } catch (Exception e) {
                    Log.d("BluetoothDashboardFrag", "updateBluetoothDevicePreferenceSummary = " + e);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updatePreferecenSummary(String str) throws Exception {
        int battaryInfo = this.mPodsService.getBattaryInfo(str);
        if (battaryInfo > 0) {
            int i = battaryInfo % 1000;
            int i2 = battaryInfo / 1000;
            int i3 = i2 % 1000;
            int i4 = i2 / 1000;
            int i5 = i4 % 1000;
            Log.d("BluetoothDashboardFrag", "updateView battery = " + i4 + " left = " + i + " right=" + i3 + " box = " + i5);
            String batteryString = getBatteryString(i, i3, i5);
            BluetoothDevicePreference bluetoothDevicePreference = this.mTwsPreferences.get(str);
            if (bluetoothDevicePreference != null) {
                bluetoothDevicePreference.setSummary(batteryString);
                bluetoothDevicePreference.setTwsBattery(batteryString);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private String getBatteryString(int i, int i2, int i3) {
        StringBuilder sb = new StringBuilder();
        if (i > 0) {
            Context context = this.mContext;
            int i4 = C0017R$string.earphone_support_battery_info_left;
            sb.append(context.getString(i4, i + "%"));
        }
        if (i2 > 0) {
            if (i > 0) {
                sb.append(this.mContext.getString(C0017R$string.earphone_support_battery_dot));
            }
            Context context2 = this.mContext;
            int i5 = C0017R$string.earphone_support_battery_info_right;
            sb.append(context2.getString(i5, i2 + "%"));
        }
        if (i3 > 0) {
            if (i > 0 || i2 > 0) {
                sb.append(this.mContext.getString(C0017R$string.earphone_support_battery_dot));
            }
            Context context3 = this.mContext;
            int i6 = C0017R$string.earphone_support_battery_info_box;
            sb.append(context3.getString(i6, i3 + "%"));
        }
        return sb.toString();
    }

    public boolean isOnePlusPods(String str) {
        IOnePlusPodDevice iOnePlusPodDevice = this.mPodsService;
        if (iOnePlusPodDevice == null) {
            return false;
        }
        try {
            return iOnePlusPodDevice.isOnePlusPods(str);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }
}
