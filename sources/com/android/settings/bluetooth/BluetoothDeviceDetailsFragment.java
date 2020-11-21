package com.android.settings.bluetooth;

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
import android.os.Message;
import android.os.Messenger;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.internal.app.AssistUtils;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.BlockingSlicePrefController;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.oneplus.settings.utils.OPUtils;
import com.oos.onepluspods.service.aidl.IOnePlusPodDevice;
import com.oos.onepluspods.service.aidl.IOnePlusUpdate;
import java.util.ArrayList;
import java.util.List;

public class BluetoothDeviceDetailsFragment extends RestrictedDashboardFragment {
    static int EDIT_DEVICE_NAME_ITEM_ID = 1;
    static TestDataFactory sTestDataFactory;
    private ServiceConnection mBreenoConnection = new ServiceConnection() {
        /* class com.android.settings.bluetooth.BluetoothDeviceDetailsFragment.AnonymousClass1 */

        public void onServiceDisconnected(ComponentName componentName) {
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Messenger messenger = new Messenger(iBinder);
            Message obtain = Message.obtain((Handler) null, 1001);
            obtain.replyTo = BluetoothDeviceDetailsFragment.this.mBreenoMessenger;
            try {
                messenger.send(obtain);
            } catch (Exception e) {
                Log.d("BTDeviceDetailsFrg", "send e = " + e);
            }
        }
    };
    private Messenger mBreenoMessenger;
    CachedBluetoothDevice mCachedDevice;
    private ServiceConnection mConnecttion = new ServiceConnection() {
        /* class com.android.settings.bluetooth.BluetoothDeviceDetailsFragment.AnonymousClass3 */

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("BTDeviceDetailsFrg", "onServiceConnected componentName = " + componentName + " iBinder = " + iBinder);
            BluetoothDeviceDetailsFragment.this.mPodsService = IOnePlusPodDevice.Stub.asInterface(iBinder);
            String string = BluetoothDeviceDetailsFragment.this.getArguments().getString("device_address");
            try {
                if (BluetoothDeviceDetailsFragment.this.mPodsService.isOnePlusPods(string)) {
                    BluetoothDeviceDetailsFragment.this.mPodsService.setIOnePlusUpdate(BluetoothDeviceDetailsFragment.this.mStub);
                    BluetoothDeviceDetailsFragment.this.updateOnePlusPodsPreference(string);
                    BluetoothDeviceDetailsFragment.this.mIntroductionPreference.setVisible(true);
                    if (BluetoothDeviceDetailsFragment.this.mController != null) {
                        BluetoothDeviceDetailsFragment.this.mController.setTwsAddress(true);
                        return;
                    }
                    return;
                }
                BluetoothDeviceDetailsFragment.this.mDoubleClickLeft.setVisible(false);
                BluetoothDeviceDetailsFragment.this.mDoubleClickRight.setVisible(false);
                BluetoothDeviceDetailsFragment.this.mOtaSwitchPreference.setVisible(false);
                BluetoothDeviceDetailsFragment.this.mFindEarPreference.setVisible(false);
                BluetoothDeviceDetailsFragment.this.mOtaWifiDataSwitchPreference.setVisible(false);
                BluetoothDeviceDetailsFragment.this.mIntroductionPreference.setVisible(false);
                BluetoothDeviceDetailsFragment.this.mContext.unbindService(BluetoothDeviceDetailsFragment.this.mConnecttion);
                BluetoothDeviceDetailsFragment.this.mPodsService = null;
                if (BluetoothDeviceDetailsFragment.this.mController != null) {
                    BluetoothDeviceDetailsFragment.this.mController.setTwsAddress(false);
                }
            } catch (Exception e) {
                Log.d("BTDeviceDetailsFrg", "onServiceConnected e = " + e);
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("BTDeviceDetailsFrg", "onServiceDisconnected componentName = " + componentName);
        }
    };
    private Context mContext;
    private BluetoothDetailsHeaderController mController;
    String mDeviceAddress;
    private ListPreference mDoubleClickLeft;
    private ListPreference mDoubleClickRight;
    private Preference mFindEarPreference;
    private Handler mHandler;
    private Preference mIntroductionPreference;
    private int mLeftFunc;
    private Preference.OnPreferenceChangeListener mListener = new Preference.OnPreferenceChangeListener() {
        /* class com.android.settings.bluetooth.BluetoothDeviceDetailsFragment.AnonymousClass2 */

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            String key = preference.getKey();
            Log.d("BTDeviceDetailsFrg", "onPreferenceChange key = " + key);
            if (key.equals("function_click_left_key_preference")) {
                String str = (String) obj;
                BluetoothDeviceDetailsFragment.this.mDoubleClickLeft.setSummary(str);
                BluetoothDeviceDetailsFragment bluetoothDeviceDetailsFragment = BluetoothDeviceDetailsFragment.this;
                bluetoothDeviceDetailsFragment.mLeftFunc = bluetoothDeviceDetailsFragment.getKeyFunctionFromString(str);
                try {
                    BluetoothDeviceDetailsFragment.this.mPodsService.setKeyFunction(BluetoothDeviceDetailsFragment.this.mLeftFunc, BluetoothDeviceDetailsFragment.this.mRightFunc, BluetoothDeviceDetailsFragment.this.mDeviceAddress);
                } catch (Exception e) {
                    Log.d("BTDeviceDetailsFrg", "setKeyFunction e = " + e);
                }
                if (str.equals(BluetoothDeviceDetailsFragment.this.mContext.getString(C0017R$string.earphone_function_listening_breeno))) {
                    BluetoothDeviceDetailsFragment.this.startBreenoService();
                }
                OPUtils.sendAnalytics("RBS8PPYT2W", "leftearbud", "select", String.valueOf(BluetoothDeviceDetailsFragment.this.getValueFunctionFromString(str)));
            } else if (key.equals("function_click_right_key_preference")) {
                String str2 = (String) obj;
                BluetoothDeviceDetailsFragment.this.mDoubleClickRight.setSummary(str2);
                BluetoothDeviceDetailsFragment bluetoothDeviceDetailsFragment2 = BluetoothDeviceDetailsFragment.this;
                bluetoothDeviceDetailsFragment2.mRightFunc = bluetoothDeviceDetailsFragment2.getKeyFunctionFromString(str2);
                try {
                    BluetoothDeviceDetailsFragment.this.mPodsService.setKeyFunction(BluetoothDeviceDetailsFragment.this.mLeftFunc, BluetoothDeviceDetailsFragment.this.mRightFunc, BluetoothDeviceDetailsFragment.this.mDeviceAddress);
                } catch (Exception e2) {
                    Log.d("BTDeviceDetailsFrg", "setKeyFunction e = " + e2);
                }
                if (str2.equals(BluetoothDeviceDetailsFragment.this.mContext.getString(C0017R$string.earphone_function_listening_breeno))) {
                    BluetoothDeviceDetailsFragment.this.startBreenoService();
                }
                OPUtils.sendAnalytics("RBS8PPYT2W", "rightearbud", "select", String.valueOf(BluetoothDeviceDetailsFragment.this.getValueFunctionFromString(str2)));
            } else if (key.equals("ota_device_support_enable_preference")) {
                if (((Boolean) obj).booleanValue()) {
                    Settings.System.putInt(BluetoothDeviceDetailsFragment.this.mContext.getContentResolver(), "onepluspods_auto_ota_version", 1);
                    BluetoothDeviceDetailsFragment.this.mOtaSwitchPreference.setChecked(true);
                    BluetoothDeviceDetailsFragment.this.mOtaWifiDataSwitchPreference.setEnabled(true);
                    OPUtils.sendAnalytics("RBS8PPYT2W", "autoupdate", "autoupdateopen", "1");
                } else {
                    Settings.System.putInt(BluetoothDeviceDetailsFragment.this.mContext.getContentResolver(), "onepluspods_auto_ota_version", 0);
                    BluetoothDeviceDetailsFragment.this.mOtaSwitchPreference.setChecked(false);
                    Settings.System.putInt(BluetoothDeviceDetailsFragment.this.mContext.getContentResolver(), "onepluspods_ota_data_download", 0);
                    BluetoothDeviceDetailsFragment.this.mOtaWifiDataSwitchPreference.setChecked(false);
                    BluetoothDeviceDetailsFragment.this.mOtaWifiDataSwitchPreference.setEnabled(false);
                    OPUtils.sendAnalytics("RBS8PPYT2W", "autoupdate", "autoupdateoff", "0");
                }
            } else if (key.equals("ota_device_wifi_data_enable_preference")) {
                if (((Boolean) obj).booleanValue()) {
                    Settings.System.putInt(BluetoothDeviceDetailsFragment.this.mContext.getContentResolver(), "onepluspods_ota_data_download", 1);
                    BluetoothDeviceDetailsFragment.this.mOtaWifiDataSwitchPreference.setChecked(true);
                    OPUtils.sendAnalytics("RBS8PPYT2W", "mobiledate", "mobiledateopen", "1");
                } else {
                    Settings.System.putInt(BluetoothDeviceDetailsFragment.this.mContext.getContentResolver(), "onepluspods_ota_data_download", 0);
                    BluetoothDeviceDetailsFragment.this.mOtaWifiDataSwitchPreference.setChecked(false);
                    OPUtils.sendAnalytics("RBS8PPYT2W", "mobiledate", "mobiledateoff", "0");
                }
            }
            return true;
        }
    };
    LocalBluetoothManager mManager;
    private SwitchPreference mOtaSwitchPreference;
    private SwitchPreference mOtaWifiDataSwitchPreference;
    private IOnePlusPodDevice mPodsService;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.bluetooth.BluetoothDeviceDetailsFragment.AnonymousClass5 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int i = 1;
            if ("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED".equals(action)) {
                int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.CONNECTION_STATE", Integer.MIN_VALUE);
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (intExtra == 2) {
                    try {
                        if (BluetoothDeviceDetailsFragment.this.mPodsService == null) {
                            Intent intent2 = new Intent();
                            intent2.setClassName("com.oneplus.twspods", "com.oos.onepluspods.service.MultiDeviceCoreService");
                            intent2.putExtra("address", bluetoothDevice.getAddress());
                            intent2.putExtra("device", bluetoothDevice);
                            BluetoothDeviceDetailsFragment.this.mContext.bindService(intent2, BluetoothDeviceDetailsFragment.this.mConnecttion, 1);
                        } else if (BluetoothDeviceDetailsFragment.this.mPodsService.isOnePlusPods(bluetoothDevice.getAddress())) {
                            BluetoothDeviceDetailsFragment.this.updateOnePlusPodsPreference(bluetoothDevice.getAddress());
                            BluetoothDeviceDetailsFragment.this.mIntroductionPreference.setVisible(true);
                        }
                    } catch (Exception e) {
                        Log.d("BTDeviceDetailsFrg", "onReceive e = " + e);
                    }
                } else if (BluetoothDeviceDetailsFragment.this.mDeviceAddress.equals(bluetoothDevice.getAddress())) {
                    BluetoothDeviceDetailsFragment.this.mDoubleClickLeft.setVisible(false);
                    BluetoothDeviceDetailsFragment.this.mDoubleClickRight.setVisible(false);
                    BluetoothDeviceDetailsFragment.this.mOtaSwitchPreference.setVisible(false);
                    BluetoothDeviceDetailsFragment.this.mOtaWifiDataSwitchPreference.setVisible(false);
                    BluetoothDeviceDetailsFragment bluetoothDeviceDetailsFragment = BluetoothDeviceDetailsFragment.this;
                    bluetoothDeviceDetailsFragment.mCachedDevice = bluetoothDeviceDetailsFragment.getCachedDevice(bluetoothDeviceDetailsFragment.mDeviceAddress);
                }
            } else if ("android.bluetooth.headset.action.VENDOR_SPECIFIC_HEADSET_EVENT".equals(action)) {
                try {
                    if (BluetoothDeviceDetailsFragment.this.mPodsService != null) {
                        BluetoothDevice bluetoothDevice2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                        if ((bluetoothDevice2 == null || BluetoothDeviceDetailsFragment.this.mDeviceAddress.equals(bluetoothDevice2.getAddress())) && bluetoothDevice2 != null && BluetoothDeviceDetailsFragment.this.mPodsService.isOnePlusPods(bluetoothDevice2.getAddress())) {
                            String stringExtra = intent.getStringExtra("android.bluetooth.headset.extra.VENDOR_SPECIFIC_HEADSET_EVENT_CMD");
                            Object[] objArr = (Object[]) intent.getExtras().get("android.bluetooth.headset.extra.VENDOR_SPECIFIC_HEADSET_EVENT_ARGS");
                            if (stringExtra != null && objArr != null && objArr.length == 7) {
                                int intValue = (((Integer) objArr[2]).intValue() + 1) * 10;
                                int intValue2 = (((Integer) objArr[4]).intValue() + 1) * 10;
                                if (!(objArr[6] instanceof String)) {
                                    i = (((Integer) objArr[6]).intValue() + 1) * 10;
                                }
                                Log.d("BTDeviceDetailsFrg", "ACTION_VENDOR_SPECIFIC_HEADSET_EVENT address = " + bluetoothDevice2.getAddress() + ", leftLevel: " + intValue + ", rightLevel: " + intValue2 + ", boxLevel: " + i);
                                BluetoothDeviceDetailsFragment.this.mController.updateSumary(bluetoothDevice2.getAddress(), BluetoothDeviceDetailsFragment.this.getBatteryString(intValue, intValue2, i));
                            }
                        }
                    }
                } catch (Exception e2) {
                    Log.d("BTDeviceDetailsFrg", "onReceive e2 = " + e2);
                }
            } else if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(action) && intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE) != 12) {
                BluetoothDeviceDetailsFragment.this.mDoubleClickLeft.setVisible(false);
                BluetoothDeviceDetailsFragment.this.mDoubleClickRight.setVisible(false);
                BluetoothDeviceDetailsFragment.this.mOtaSwitchPreference.setVisible(false);
                BluetoothDeviceDetailsFragment.this.mOtaWifiDataSwitchPreference.setVisible(false);
            }
        }
    };
    private int mRightFunc;
    private Handler mServiceHandler;
    private IOnePlusUpdate.Stub mStub = new IOnePlusUpdate.Stub() {
        /* class com.android.settings.bluetooth.BluetoothDeviceDetailsFragment.AnonymousClass4 */

        @Override // com.oos.onepluspods.service.aidl.IOnePlusUpdate
        public void updateView(final String str, final int i) {
            BluetoothDeviceDetailsFragment.this.mHandler.post(new Runnable() {
                /* class com.android.settings.bluetooth.BluetoothDeviceDetailsFragment.AnonymousClass4.AnonymousClass1 */

                public void run() {
                    int battaryInfo;
                    try {
                        if (str.equals(BluetoothDeviceDetailsFragment.this.mDeviceAddress)) {
                            Log.d("BTDeviceDetailsFrg", "updateView address = " + str + " content = " + i);
                            if (i == 0) {
                                int keyFunction = BluetoothDeviceDetailsFragment.this.mPodsService.getKeyFunction(str);
                                BluetoothDeviceDetailsFragment.this.mLeftFunc = keyFunction / 10;
                                BluetoothDeviceDetailsFragment.this.mRightFunc = keyFunction - (BluetoothDeviceDetailsFragment.this.mLeftFunc * 10);
                                BluetoothDeviceDetailsFragment.this.mDoubleClickLeft.setSummary(BluetoothDeviceDetailsFragment.this.getStringFromKeyFunction(BluetoothDeviceDetailsFragment.this.mLeftFunc));
                                BluetoothDeviceDetailsFragment.this.mDoubleClickLeft.setValue(BluetoothDeviceDetailsFragment.this.getStringFromKeyFunction(BluetoothDeviceDetailsFragment.this.mLeftFunc));
                                BluetoothDeviceDetailsFragment.this.mDoubleClickRight.setSummary(BluetoothDeviceDetailsFragment.this.getStringFromKeyFunction(BluetoothDeviceDetailsFragment.this.mRightFunc));
                                BluetoothDeviceDetailsFragment.this.mDoubleClickRight.setValue(BluetoothDeviceDetailsFragment.this.getStringFromKeyFunction(BluetoothDeviceDetailsFragment.this.mRightFunc));
                            } else if (i == 1) {
                                SwitchPreference switchPreference = BluetoothDeviceDetailsFragment.this.mOtaSwitchPreference;
                                switchPreference.setSummary(BluetoothDeviceDetailsFragment.this.mContext.getString(C0017R$string.earphone_support_current_version) + BluetoothDeviceDetailsFragment.this.mPodsService.getVersion(BluetoothDeviceDetailsFragment.this.getArguments().getString("device_address")));
                            } else if (i == 2 && (battaryInfo = BluetoothDeviceDetailsFragment.this.mPodsService.getBattaryInfo(BluetoothDeviceDetailsFragment.this.getArguments().getString("device_address"))) > 0) {
                                int i = battaryInfo % 1000;
                                int i2 = battaryInfo / 1000;
                                int i3 = i2 % 1000;
                                int i4 = i2 / 1000;
                                int i5 = i4 % 1000;
                                Log.d("BTDeviceDetailsFrg", "updateView detail battery = " + i4 + " left = " + i + " right=" + i3 + " box = " + i5);
                                BluetoothDeviceDetailsFragment.this.mController.updateSumary(str, BluetoothDeviceDetailsFragment.this.getBatteryString(i, i3, i5));
                            }
                        }
                    } catch (Exception e) {
                        Log.d("BTDeviceDetailsFrg", "updateView e = " + e);
                    }
                }
            });
        }
    };

    /* access modifiers changed from: package-private */
    public interface TestDataFactory {
        CachedBluetoothDevice getDevice(String str);

        LocalBluetoothManager getManager(Context context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BTDeviceDetailsFrg";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1009;
    }

    public BluetoothDeviceDetailsFragment() {
        super("no_config_bluetooth");
    }

    /* access modifiers changed from: package-private */
    public LocalBluetoothManager getLocalBluetoothManager(Context context) {
        TestDataFactory testDataFactory = sTestDataFactory;
        if (testDataFactory != null) {
            return testDataFactory.getManager(context);
        }
        return Utils.getLocalBtManager(context);
    }

    /* access modifiers changed from: package-private */
    public CachedBluetoothDevice getCachedDevice(String str) {
        TestDataFactory testDataFactory = sTestDataFactory;
        if (testDataFactory != null) {
            return testDataFactory.getDevice(str);
        }
        return this.mManager.getCachedDeviceManager().findDevice(this.mManager.getBluetoothAdapter().getRemoteDevice(str));
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        this.mDeviceAddress = getArguments().getString("device_address");
        this.mManager = getLocalBluetoothManager(context);
        this.mCachedDevice = getCachedDevice(this.mDeviceAddress);
        super.onAttach(context);
        if (this.mCachedDevice == null) {
            Log.w("BTDeviceDetailsFrg", "onAttach() CachedDevice is null!");
            finish();
            return;
        }
        ((AdvancedBluetoothDetailsHeaderController) use(AdvancedBluetoothDetailsHeaderController.class)).init(this.mCachedDevice);
        BluetoothFeatureProvider bluetoothFeatureProvider = FeatureFactory.getFactory(context).getBluetoothFeatureProvider(context);
        ((BlockingSlicePrefController) use(BlockingSlicePrefController.class)).setSliceUri(DeviceConfig.getBoolean("settings_ui", "bt_slice_settings_enabled", true) ? bluetoothFeatureProvider.getBluetoothDeviceSettingsUri(this.mCachedDevice.getDevice()) : null);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        finishFragmentIfNecessary();
    }

    /* access modifiers changed from: package-private */
    public void finishFragmentIfNecessary() {
        if (this.mCachedDevice.getBondState() == 10) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.bluetooth_device_details_fragment;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, EDIT_DEVICE_NAME_ITEM_ID, 0, C0017R$string.bluetooth_rename_button);
        add.setIcon(17302751);
        add.setShowAsAction(2);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != EDIT_DEVICE_NAME_ITEM_ID) {
            return super.onOptionsItemSelected(menuItem);
        }
        RemoteDeviceNameDialogFragment.newInstance(this.mCachedDevice).show(getFragmentManager(), "RemoteDeviceName");
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        if (this.mCachedDevice != null) {
            Lifecycle settingsLifecycle = getSettingsLifecycle();
            BluetoothDetailsHeaderController bluetoothDetailsHeaderController = new BluetoothDetailsHeaderController(context, this, this.mCachedDevice, settingsLifecycle, this.mManager);
            this.mController = bluetoothDetailsHeaderController;
            arrayList.add(bluetoothDetailsHeaderController);
            arrayList.add(new BluetoothDetailsButtonsController(context, this, this.mCachedDevice, settingsLifecycle));
            arrayList.add(new BluetoothDetailsProfilesController(context, this, this.mManager, this.mCachedDevice, settingsLifecycle));
            arrayList.add(new BluetoothDetailsMacAddressController(context, this, this.mCachedDevice, settingsLifecycle));
        }
        return arrayList;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener, com.android.settings.dashboard.DashboardFragment
    public boolean onPreferenceTreeClick(Preference preference) {
        Log.d("hanksettings", "onPreferenceTreeClick  key = " + preference.getKey());
        if ("find_my_bluetooth_earphone_preference".equals(preference.getKey())) {
            Intent intent = new Intent();
            intent.addFlags(268435456);
            intent.setClassName("com.oneplus.twspods", "com.oos.onepluspods.map.OPAMapLocationActivity");
            Bundle bundle = new Bundle();
            bundle.putParcelable("device", this.mCachedDevice.getDevice());
            intent.putExtras(bundle);
            intent.putExtra("address", this.mDeviceAddress);
            intent.putExtra("connected", this.mCachedDevice.isConnected());
            getActivity().startActivity(intent);
        } else if ("function_introduction_earphone_preference".equals(preference.getKey())) {
            Intent intent2 = new Intent();
            intent2.addFlags(268435456);
            intent2.setClassName("com.oneplus.twspods", "com.oos.onepluspods.settings.functionlist.introduction.EarphoneUsageGuideActivity");
            Bundle bundle2 = new Bundle();
            bundle2.putParcelable("device", this.mCachedDevice.getDevice());
            intent2.putExtras(bundle2);
            intent2.putExtra("address", this.mDeviceAddress);
            getActivity().startActivity(intent2);
            OPUtils.sendAnalytics("RBS8PPYT2W", "function", "functiondescription", "1");
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.dashboard.RestrictedDashboardFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
        String string = getArguments().getString("device_address");
        this.mDeviceAddress = string;
        CachedBluetoothDevice cachedDevice = getCachedDevice(string);
        this.mCachedDevice = cachedDevice;
        if (cachedDevice != null) {
            Intent intent = new Intent();
            intent.setClassName("com.oneplus.twspods", "com.oos.onepluspods.service.MultiDeviceCoreService");
            intent.putExtra("address", this.mDeviceAddress);
            intent.putExtra("device", this.mCachedDevice.getDevice());
            this.mContext.bindService(intent, this.mConnecttion, 1);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED");
            intentFilter.addAction("android.bluetooth.headset.action.VENDOR_SPECIFIC_HEADSET_EVENT");
            intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
            this.mContext.registerReceiver(this.mReceiver, intentFilter);
            Log.d("BTDeviceDetailsFrg", "onCreate binderservice address = " + this.mDeviceAddress);
        }
        this.mDoubleClickLeft = (ListPreference) findPreference("function_click_left_key_preference");
        this.mDoubleClickRight = (ListPreference) findPreference("function_click_right_key_preference");
        this.mOtaSwitchPreference = (SwitchPreference) findPreference("ota_device_support_enable_preference");
        this.mFindEarPreference = findPreference("find_my_bluetooth_earphone_preference");
        this.mOtaWifiDataSwitchPreference = (SwitchPreference) findPreference("ota_device_wifi_data_enable_preference");
        this.mIntroductionPreference = findPreference("function_introduction_earphone_preference");
        this.mDoubleClickLeft.setVisible(false);
        this.mDoubleClickRight.setVisible(false);
        this.mOtaSwitchPreference.setVisible(false);
        this.mFindEarPreference.setVisible(false);
        this.mOtaWifiDataSwitchPreference.setVisible(false);
        this.mIntroductionPreference.setVisible(false);
        this.mHandler = new Handler(this.mContext.getMainLooper());
        this.mServiceHandler = new ServiceHanlder(this.mContext.getApplicationContext());
        this.mBreenoMessenger = new Messenger(this.mServiceHandler);
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        try {
            this.mPodsService.setIOnePlusUpdate(null);
            this.mContext.unbindService(this.mConnecttion);
        } catch (Exception e) {
            Log.d("BTDeviceDetailsFrg", "unbindService e = " + e);
        }
        try {
            this.mContext.unregisterReceiver(this.mReceiver);
        } catch (Exception e2) {
            Log.d("BTDeviceDetailsFrg", "unregisterReceiver e = " + e2);
        }
        try {
            this.mBreenoMessenger = null;
            this.mServiceHandler = null;
            this.mContext.unbindService(this.mBreenoConnection);
        } catch (Exception e3) {
            Log.d("BTDeviceDetailsFrg", "unbindService mBreenoConnection e = " + e3);
        }
        super.onDestroy();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int getKeyFunctionFromString(String str) {
        if (str.equals(this.mContext.getString(C0017R$string.earphone_function_control_play_pause))) {
            return 1;
        }
        if (str.equals(this.mContext.getString(C0017R$string.earphone_function_listening_breeno))) {
            return 3;
        }
        if (str.equals(this.mContext.getString(C0017R$string.earphone_function_control_last_song))) {
            return 4;
        }
        return str.equals(this.mContext.getString(C0017R$string.earphone_function_control_next_song)) ? 5 : -1;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int getValueFunctionFromString(String str) {
        if (str.equals(this.mContext.getString(C0017R$string.earphone_function_control_play_pause))) {
            return 1;
        }
        if (str.equals(this.mContext.getString(C0017R$string.earphone_function_listening_breeno))) {
            return 2;
        }
        if (str.equals(this.mContext.getString(C0017R$string.earphone_function_control_last_song))) {
            return 3;
        }
        return str.equals(this.mContext.getString(C0017R$string.earphone_function_control_next_song)) ? 4 : -1;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private String getStringFromKeyFunction(int i) {
        if (i == 1) {
            return this.mContext.getString(C0017R$string.earphone_function_control_play_pause);
        }
        if (i == 3) {
            return this.mContext.getString(C0017R$string.earphone_function_listening_breeno);
        }
        if (i == 4) {
            return this.mContext.getString(C0017R$string.earphone_function_control_last_song);
        }
        return i == 5 ? this.mContext.getString(C0017R$string.earphone_function_control_next_song) : "";
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void startBreenoService() {
        ComponentName assistComponentForUser = new AssistUtils(this.mContext).getAssistComponentForUser(UserHandle.myUserId());
        if (assistComponentForUser == null || !assistComponentForUser.getPackageName().equals("com.heytap.speechassist")) {
            Log.d("BTDeviceDetailsFrg", "startBreenoService return");
            return;
        }
        Log.d("BTDeviceDetailsFrg", "startBreenoService");
        Intent intent = new Intent("heytap.speech.intent.action.TEXT_DIRECTIVE");
        intent.setClassName("com.heytap.speechassist", "com.heytap.speechassist.agent.MessengerService");
        this.mContext.bindService(intent, this.mBreenoConnection, 1);
    }

    private static class ServiceHanlder extends Handler {
        private Context mContext;

        public ServiceHanlder(Context context) {
            super(context.getMainLooper());
            this.mContext = context;
        }

        public void handleMessage(Message message) {
            if (message.what == 1001) {
                Log.d("BTDeviceDetailsFrg", "handleMessage msg.arg1 = " + message.arg1);
                if (message.arg1 == -1) {
                    try {
                        this.mContext.startActivity(this.mContext.getPackageManager().getLaunchIntentForPackage("com.heytap.speechassist"));
                    } catch (Exception e) {
                        Log.d("BTDeviceDetailsFrg", "startActivity e = " + e);
                    }
                }
            }
            super.handleMessage(message);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateOnePlusPodsPreference(String str) {
        CachedBluetoothDevice cachedBluetoothDevice = this.mCachedDevice;
        boolean z = true;
        if (cachedBluetoothDevice != null && cachedBluetoothDevice.isConnected()) {
            try {
                this.mDoubleClickLeft.setVisible(true);
                this.mDoubleClickRight.setVisible(true);
                this.mOtaSwitchPreference.setVisible(true);
                if (!OPUtils.isO2()) {
                    this.mFindEarPreference.setVisible(true);
                }
                this.mOtaWifiDataSwitchPreference.setVisible(true);
                int keyFunction = this.mPodsService.getKeyFunction(str);
                int i = keyFunction / 10;
                this.mLeftFunc = i;
                this.mRightFunc = keyFunction - (i * 10);
                this.mDoubleClickLeft.setSummary(getStringFromKeyFunction(i));
                this.mDoubleClickLeft.setValue(getStringFromKeyFunction(this.mLeftFunc));
                this.mDoubleClickRight.setSummary(getStringFromKeyFunction(this.mRightFunc));
                this.mDoubleClickRight.setValue(getStringFromKeyFunction(this.mRightFunc));
                this.mDoubleClickLeft.setOnPreferenceChangeListener(this.mListener);
                this.mDoubleClickRight.setOnPreferenceChangeListener(this.mListener);
                int i2 = Settings.System.getInt(this.mContext.getContentResolver(), "onepluspods_auto_ota_version", -1);
                if (i2 == -1) {
                    i2 = !OPUtils.isO2() ? 1 : 0;
                    Settings.System.putInt(this.mContext.getContentResolver(), "onepluspods_auto_ota_version", i2);
                }
                this.mOtaSwitchPreference.setOnPreferenceChangeListener(this.mListener);
                this.mOtaSwitchPreference.setSummary(this.mContext.getString(C0017R$string.earphone_support_current_version) + this.mPodsService.getVersion(getArguments().getString("device_address")));
                this.mOtaSwitchPreference.setChecked(i2 == 1);
                int i3 = Settings.System.getInt(this.mContext.getContentResolver(), "onepluspods_ota_data_download", 0);
                this.mOtaWifiDataSwitchPreference.setOnPreferenceChangeListener(this.mListener);
                SwitchPreference switchPreference = this.mOtaWifiDataSwitchPreference;
                if (i3 != 1) {
                    z = false;
                }
                switchPreference.setChecked(z);
                int battaryInfo = this.mPodsService.getBattaryInfo(getArguments().getString("device_address"));
                if (battaryInfo > 0) {
                    int i4 = battaryInfo % 1000;
                    int i5 = battaryInfo / 1000;
                    int i6 = i5 % 1000;
                    int i7 = i5 / 1000;
                    int i8 = i7 % 1000;
                    Log.d("BTDeviceDetailsFrg", "updateView detail battery = " + i7 + " left = " + i4 + " right=" + i6 + " box = " + i8);
                    this.mController.updateSumary(str, getBatteryString(i4, i6, i8));
                }
            } catch (Exception e) {
                Log.d("BTDeviceDetailsFrg", "updateOnePlusPodsPreference e = " + e);
            }
        } else if (!OPUtils.isO2()) {
            this.mFindEarPreference.setVisible(true);
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
}
