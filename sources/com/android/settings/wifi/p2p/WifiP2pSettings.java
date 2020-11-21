package com.android.settings.wifi.p2p;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pGroupList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.sysprop.TelephonyProperties;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.R$styleable;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class WifiP2pSettings extends DashboardFragment implements WifiP2pManager.PersistentGroupInfoListener, WifiP2pManager.PeerListListener, WifiP2pManager.DeviceInfoListener {
    private static final boolean DBG = Log.isLoggable("WifiP2pSettings", 3);
    static final int DIALOG_CANCEL_CONNECT = 2;
    static final int DIALOG_DELETE_GROUP = 4;
    static final int DIALOG_DISCONNECT = 1;
    static final int DIALOG_RENAME = 3;
    static final int MENU_ID_RENAME = 2;
    static final int MENU_ID_SEARCH = 1;
    static final String SAVE_DEVICE_NAME = "DEV_NAME";
    static final String SAVE_DIALOG_PEER = "PEER_STATE";
    static final String SAVE_SELECTED_GROUP = "GROUP_NAME";
    DialogInterface.OnClickListener mCancelConnectListener;
    private WifiP2pManager.Channel mChannel;
    int mConnectedDevices;
    DialogInterface.OnClickListener mDeleteGroupListener;
    private EditText mDeviceNameText;
    DialogInterface.OnClickListener mDisconnectListener;
    private final IntentFilter mIntentFilter = new IntentFilter();
    private boolean mIsIgnoreInitConnectionInfoCallback = DBG;
    boolean mLastGroupFormed = DBG;
    P2pPeerCategoryPreferenceController mPeerCategoryController;
    private WifiP2pDeviceList mPeers = new WifiP2pDeviceList();
    P2pPersistentCategoryPreferenceController mPersistentCategoryController;
    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.wifi.p2p.WifiP2pSettings.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            WifiP2pSettings wifiP2pSettings;
            WifiP2pManager wifiP2pManager;
            String action = intent.getAction();
            boolean equals = "android.net.wifi.p2p.STATE_CHANGED".equals(action);
            boolean z = WifiP2pSettings.DBG;
            if (equals) {
                WifiP2pSettings wifiP2pSettings2 = WifiP2pSettings.this;
                if (intent.getIntExtra("wifi_p2p_state", 1) == 2) {
                    z = true;
                }
                wifiP2pSettings2.mWifiP2pEnabled = z;
                WifiP2pSettings.this.handleP2pStateChanged();
            } else if ("android.net.wifi.p2p.PEERS_CHANGED".equals(action)) {
                WifiP2pSettings.this.mPeers = (WifiP2pDeviceList) intent.getParcelableExtra("wifiP2pDeviceList");
                WifiP2pSettings.this.handlePeersChanged();
            } else if ("android.net.wifi.p2p.CONNECTION_STATE_CHANGE".equals(action)) {
                if (WifiP2pSettings.this.mWifiP2pManager != null) {
                    WifiP2pInfo wifiP2pInfo = (WifiP2pInfo) intent.getParcelableExtra("wifiP2pInfo");
                    if (!((NetworkInfo) intent.getParcelableExtra("networkInfo")).isConnected()) {
                        WifiP2pSettings wifiP2pSettings3 = WifiP2pSettings.this;
                        if (!wifiP2pSettings3.mLastGroupFormed) {
                            wifiP2pSettings3.startSearch();
                        }
                    } else if (WifiP2pSettings.DBG) {
                        Log.d("WifiP2pSettings", "Connected");
                    }
                    WifiP2pSettings wifiP2pSettings4 = WifiP2pSettings.this;
                    wifiP2pSettings4.mLastGroupFormed = wifiP2pInfo.groupFormed;
                    wifiP2pSettings4.mIsIgnoreInitConnectionInfoCallback = true;
                }
            } else if ("android.net.wifi.p2p.THIS_DEVICE_CHANGED".equals(action)) {
                if (WifiP2pSettings.DBG) {
                    Log.d("WifiP2pSettings", "This device changed. Requesting device info.");
                }
                WifiP2pSettings wifiP2pSettings5 = WifiP2pSettings.this;
                wifiP2pSettings5.mWifiP2pManager.requestDeviceInfo(wifiP2pSettings5.mChannel, WifiP2pSettings.this);
            } else if ("android.net.wifi.p2p.DISCOVERY_STATE_CHANGE".equals(action)) {
                int intExtra = intent.getIntExtra("discoveryState", 1);
                if (WifiP2pSettings.DBG) {
                    Log.d("WifiP2pSettings", "Discovery state changed: " + intExtra);
                }
                if (intExtra == 2) {
                    WifiP2pSettings.this.updateSearchMenu(true);
                } else {
                    WifiP2pSettings.this.updateSearchMenu(WifiP2pSettings.DBG);
                }
            } else if ("android.net.wifi.p2p.action.WIFI_P2P_PERSISTENT_GROUPS_CHANGED".equals(action) && (wifiP2pManager = (wifiP2pSettings = WifiP2pSettings.this).mWifiP2pManager) != null) {
                wifiP2pManager.requestPersistentGroupInfo(wifiP2pSettings.mChannel, WifiP2pSettings.this);
            }
        }
    };
    DialogInterface.OnClickListener mRenameListener;
    String mSavedDeviceName;
    WifiP2pPersistentGroup mSelectedGroup;
    String mSelectedGroupName;
    WifiP2pPeer mSelectedWifiPeer;
    private WifiP2pDevice mThisDevice;
    P2pThisDevicePreferenceController mThisDevicePreferenceController;
    private boolean mWifiP2pEnabled;
    WifiP2pManager mWifiP2pManager;
    boolean mWifiP2pSearching;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i == 1) {
            return 575;
        }
        if (i == 2) {
            return 576;
        }
        if (i != 3) {
            return i != 4 ? 0 : 578;
        }
        return 577;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "WifiP2pSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return R$styleable.Constraint_transitionPathRotate;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.wifi_p2p_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_wifi_p2p;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        this.mPersistentCategoryController = new P2pPersistentCategoryPreferenceController(context);
        this.mPeerCategoryController = new P2pPeerCategoryPreferenceController(context);
        this.mThisDevicePreferenceController = new P2pThisDevicePreferenceController(context);
        arrayList.add(this.mPersistentCategoryController);
        arrayList.add(this.mPeerCategoryController);
        arrayList.add(this.mThisDevicePreferenceController);
        return arrayList;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        FragmentActivity activity = getActivity();
        if (this.mWifiP2pManager == null) {
            this.mWifiP2pManager = (WifiP2pManager) getSystemService("wifip2p");
        }
        WifiP2pManager wifiP2pManager = this.mWifiP2pManager;
        if (wifiP2pManager != null) {
            WifiP2pManager.Channel initialize = wifiP2pManager.initialize(activity.getApplicationContext(), getActivity().getMainLooper(), null);
            this.mChannel = initialize;
            if (initialize == null) {
                Log.e("WifiP2pSettings", "Failed to set up connection with wifi p2p service");
                this.mWifiP2pManager = null;
            }
        } else {
            Log.e("WifiP2pSettings", "mWifiP2pManager is null !");
        }
        if (bundle != null && bundle.containsKey(SAVE_DIALOG_PEER)) {
            this.mSelectedWifiPeer = new WifiP2pPeer(getPrefContext(), (WifiP2pDevice) bundle.getParcelable(SAVE_DIALOG_PEER));
        }
        if (bundle != null && bundle.containsKey(SAVE_DEVICE_NAME)) {
            this.mSavedDeviceName = bundle.getString(SAVE_DEVICE_NAME);
        }
        if (bundle != null && bundle.containsKey(SAVE_SELECTED_GROUP)) {
            this.mSelectedGroupName = bundle.getString(SAVE_SELECTED_GROUP);
        }
        this.mRenameListener = new DialogInterface.OnClickListener() {
            /* class com.android.settings.wifi.p2p.WifiP2pSettings.AnonymousClass2 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    WifiP2pSettings wifiP2pSettings = WifiP2pSettings.this;
                    if (wifiP2pSettings.mWifiP2pManager != null) {
                        String obj = wifiP2pSettings.mDeviceNameText.getText().toString();
                        if (obj != null) {
                            for (int i2 = 0; i2 < obj.length(); i2++) {
                                char charAt = obj.charAt(i2);
                                if (!Character.isDigit(charAt) && !Character.isLetter(charAt) && charAt != '-' && charAt != '_' && charAt != ' ') {
                                    Toast.makeText(WifiP2pSettings.this.getActivity(), C0017R$string.wifi_p2p_failed_rename_message, 1).show();
                                    return;
                                }
                            }
                        }
                        WifiP2pSettings wifiP2pSettings2 = WifiP2pSettings.this;
                        wifiP2pSettings2.mWifiP2pManager.setDeviceName(wifiP2pSettings2.mChannel, WifiP2pSettings.this.mDeviceNameText.getText().toString(), new WifiP2pManager.ActionListener() {
                            /* class com.android.settings.wifi.p2p.WifiP2pSettings.AnonymousClass2.AnonymousClass1 */

                            public void onSuccess() {
                                if (WifiP2pSettings.DBG) {
                                    Log.d("WifiP2pSettings", " device rename success");
                                }
                            }

                            public void onFailure(int i) {
                                Toast.makeText(WifiP2pSettings.this.getActivity(), C0017R$string.wifi_p2p_failed_rename_message, 1).show();
                            }
                        });
                    }
                }
            }
        };
        this.mDisconnectListener = new DialogInterface.OnClickListener() {
            /* class com.android.settings.wifi.p2p.WifiP2pSettings.AnonymousClass3 */

            public void onClick(DialogInterface dialogInterface, int i) {
                WifiP2pSettings wifiP2pSettings;
                WifiP2pManager wifiP2pManager;
                if (i == -1 && (wifiP2pManager = (wifiP2pSettings = WifiP2pSettings.this).mWifiP2pManager) != null) {
                    wifiP2pManager.removeGroup(wifiP2pSettings.mChannel, new WifiP2pManager.ActionListener(this) {
                        /* class com.android.settings.wifi.p2p.WifiP2pSettings.AnonymousClass3.AnonymousClass1 */

                        public void onSuccess() {
                            if (WifiP2pSettings.DBG) {
                                Log.d("WifiP2pSettings", " remove group success");
                            }
                        }

                        public void onFailure(int i) {
                            if (WifiP2pSettings.DBG) {
                                Log.d("WifiP2pSettings", " remove group fail " + i);
                            }
                        }
                    });
                }
            }
        };
        this.mCancelConnectListener = new DialogInterface.OnClickListener() {
            /* class com.android.settings.wifi.p2p.WifiP2pSettings.AnonymousClass4 */

            public void onClick(DialogInterface dialogInterface, int i) {
                WifiP2pSettings wifiP2pSettings;
                WifiP2pManager wifiP2pManager;
                if (i == -1 && (wifiP2pManager = (wifiP2pSettings = WifiP2pSettings.this).mWifiP2pManager) != null) {
                    wifiP2pManager.cancelConnect(wifiP2pSettings.mChannel, new WifiP2pManager.ActionListener(this) {
                        /* class com.android.settings.wifi.p2p.WifiP2pSettings.AnonymousClass4.AnonymousClass1 */

                        public void onSuccess() {
                            if (WifiP2pSettings.DBG) {
                                Log.d("WifiP2pSettings", " cancel connect success");
                            }
                        }

                        public void onFailure(int i) {
                            if (WifiP2pSettings.DBG) {
                                Log.d("WifiP2pSettings", " cancel connect fail " + i);
                            }
                        }
                    });
                }
            }
        };
        this.mDeleteGroupListener = new DialogInterface.OnClickListener() {
            /* class com.android.settings.wifi.p2p.WifiP2pSettings.AnonymousClass5 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    WifiP2pSettings wifiP2pSettings = WifiP2pSettings.this;
                    if (wifiP2pSettings.mWifiP2pManager == null) {
                        return;
                    }
                    if (wifiP2pSettings.mSelectedGroup != null) {
                        if (WifiP2pSettings.DBG) {
                            Log.d("WifiP2pSettings", " deleting group " + WifiP2pSettings.this.mSelectedGroup.getGroupName());
                        }
                        WifiP2pSettings wifiP2pSettings2 = WifiP2pSettings.this;
                        wifiP2pSettings2.mWifiP2pManager.deletePersistentGroup(wifiP2pSettings2.mChannel, WifiP2pSettings.this.mSelectedGroup.getNetworkId(), new WifiP2pManager.ActionListener(this) {
                            /* class com.android.settings.wifi.p2p.WifiP2pSettings.AnonymousClass5.AnonymousClass1 */

                            public void onSuccess() {
                                if (WifiP2pSettings.DBG) {
                                    Log.d("WifiP2pSettings", " delete group success");
                                }
                            }

                            public void onFailure(int i) {
                                if (WifiP2pSettings.DBG) {
                                    Log.d("WifiP2pSettings", " delete group fail " + i);
                                }
                            }
                        });
                        WifiP2pSettings.this.mSelectedGroup = null;
                    } else if (WifiP2pSettings.DBG) {
                        Log.w("WifiP2pSettings", " No selected group to delete!");
                    }
                } else if (i == -2) {
                    if (WifiP2pSettings.DBG) {
                        Log.d("WifiP2pSettings", " forgetting selected group " + WifiP2pSettings.this.mSelectedGroup.getGroupName());
                    }
                    WifiP2pSettings.this.mSelectedGroup = null;
                }
            }
        };
        super.onActivityCreated(bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        this.mIntentFilter.addAction("android.net.wifi.p2p.STATE_CHANGED");
        this.mIntentFilter.addAction("android.net.wifi.p2p.PEERS_CHANGED");
        this.mIntentFilter.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
        this.mIntentFilter.addAction("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
        this.mIntentFilter.addAction("android.net.wifi.p2p.DISCOVERY_STATE_CHANGE");
        this.mIntentFilter.addAction("android.net.wifi.p2p.action.WIFI_P2P_PERSISTENT_GROUPS_CHANGED");
        getPreferenceScreen();
        getActivity().registerReceiver(this.mReceiver, this.mIntentFilter);
        WifiP2pManager wifiP2pManager = this.mWifiP2pManager;
        if (wifiP2pManager != null) {
            wifiP2pManager.requestPeers(this.mChannel, this);
            this.mWifiP2pManager.requestDeviceInfo(this.mChannel, this);
            this.mIsIgnoreInitConnectionInfoCallback = DBG;
            this.mWifiP2pManager.requestNetworkInfo(this.mChannel, new WifiP2pManager.NetworkInfoListener() {
                /* class com.android.settings.wifi.p2p.$$Lambda$WifiP2pSettings$2b5R7FzwgOqx1vdLhUfqMEQt0cw */

                public final void onNetworkInfoAvailable(NetworkInfo networkInfo) {
                    WifiP2pSettings.this.lambda$onResume$1$WifiP2pSettings(networkInfo);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onResume$1 */
    public /* synthetic */ void lambda$onResume$1$WifiP2pSettings(NetworkInfo networkInfo) {
        this.mWifiP2pManager.requestConnectionInfo(this.mChannel, new WifiP2pManager.ConnectionInfoListener(networkInfo) {
            /* class com.android.settings.wifi.p2p.$$Lambda$WifiP2pSettings$HqTMhVVQgyo2s25QDYpssZSHqw */
            public final /* synthetic */ NetworkInfo f$1;

            {
                this.f$1 = r2;
            }

            public final void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                WifiP2pSettings.this.lambda$onResume$0$WifiP2pSettings(this.f$1, wifiP2pInfo);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onResume$0 */
    public /* synthetic */ void lambda$onResume$0$WifiP2pSettings(NetworkInfo networkInfo, WifiP2pInfo wifiP2pInfo) {
        if (!this.mIsIgnoreInitConnectionInfoCallback) {
            if (networkInfo.isConnected()) {
                if (DBG) {
                    Log.d("WifiP2pSettings", "Connected");
                }
            } else if (!this.mLastGroupFormed) {
                startSearch();
            }
            this.mLastGroupFormed = wifiP2pInfo.groupFormed;
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        WifiP2pManager wifiP2pManager = this.mWifiP2pManager;
        if (wifiP2pManager != null) {
            wifiP2pManager.stopPeerDiscovery(this.mChannel, null);
        }
        getActivity().unregisterReceiver(this.mReceiver);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        int i;
        if (this.mWifiP2pSearching) {
            i = C0017R$string.wifi_p2p_menu_searching;
        } else {
            i = C0017R$string.wifi_p2p_menu_search;
        }
        menu.add(0, 1, 0, i).setEnabled(this.mWifiP2pEnabled);
        menu.add(0, 2, 0, C0017R$string.wifi_p2p_menu_rename).setEnabled(this.mWifiP2pEnabled);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem findItem = menu.findItem(1);
        MenuItem findItem2 = menu.findItem(2);
        if (this.mWifiP2pEnabled) {
            findItem.setEnabled(true);
            findItem2.setEnabled(true);
        } else {
            findItem.setEnabled(DBG);
            findItem2.setEnabled(DBG);
        }
        if (this.mWifiP2pSearching) {
            findItem.setTitle(C0017R$string.wifi_p2p_menu_searching);
        } else {
            findItem.setTitle(C0017R$string.wifi_p2p_menu_search);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            startSearch();
            return true;
        } else if (itemId != 2) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            startActivity(new Intent("com.oneplus.intent.OPDeviceNameActivity"));
            return true;
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof WifiP2pPeer) {
            WifiP2pPeer wifiP2pPeer = (WifiP2pPeer) preference;
            this.mSelectedWifiPeer = wifiP2pPeer;
            int i = wifiP2pPeer.device.status;
            if (i == 0) {
                showDialog(1);
            } else if (i == 1) {
                showDialog(2);
            } else {
                WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
                wifiP2pConfig.deviceAddress = this.mSelectedWifiPeer.device.deviceAddress;
                int intValue = ((Integer) TelephonyProperties.wps_info().orElse(-1)).intValue();
                if (intValue != -1) {
                    wifiP2pConfig.wps.setup = intValue;
                } else if (this.mSelectedWifiPeer.device.wpsPbcSupported()) {
                    wifiP2pConfig.wps.setup = 0;
                } else if (this.mSelectedWifiPeer.device.wpsKeypadSupported()) {
                    wifiP2pConfig.wps.setup = 2;
                } else {
                    wifiP2pConfig.wps.setup = 1;
                }
                this.mWifiP2pManager.connect(this.mChannel, wifiP2pConfig, new WifiP2pManager.ActionListener() {
                    /* class com.android.settings.wifi.p2p.WifiP2pSettings.AnonymousClass6 */

                    public void onSuccess() {
                        if (WifiP2pSettings.DBG) {
                            Log.d("WifiP2pSettings", " connect success");
                        }
                    }

                    public void onFailure(int i) {
                        Log.e("WifiP2pSettings", " connect fail " + i);
                        Toast.makeText(WifiP2pSettings.this.getActivity(), C0017R$string.wifi_p2p_failed_connect_message, 0).show();
                    }
                });
            }
        } else if (preference instanceof WifiP2pPersistentGroup) {
            this.mSelectedGroup = (WifiP2pPersistentGroup) preference;
            showDialog(4);
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        String str;
        String str2;
        String str3;
        if (i == 1) {
            if (TextUtils.isEmpty(this.mSelectedWifiPeer.device.deviceName)) {
                str2 = this.mSelectedWifiPeer.device.deviceAddress;
            } else {
                str2 = this.mSelectedWifiPeer.device.deviceName;
            }
            if (this.mConnectedDevices > 1) {
                str3 = getActivity().getString(C0017R$string.wifi_p2p_disconnect_multiple_message, new Object[]{str2, Integer.valueOf(this.mConnectedDevices - 1)});
            } else {
                str3 = getActivity().getString(C0017R$string.wifi_p2p_disconnect_message, new Object[]{str2});
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(C0017R$string.wifi_p2p_disconnect_title);
            builder.setMessage(str3);
            builder.setPositiveButton(getActivity().getString(C0017R$string.dlg_ok), this.mDisconnectListener);
            builder.setNegativeButton(getActivity().getString(C0017R$string.dlg_cancel), (DialogInterface.OnClickListener) null);
            return builder.create();
        } else if (i == 2) {
            int i2 = C0017R$string.wifi_p2p_cancel_connect_message;
            if (TextUtils.isEmpty(this.mSelectedWifiPeer.device.deviceName)) {
                str = this.mSelectedWifiPeer.device.deviceAddress;
            } else {
                str = this.mSelectedWifiPeer.device.deviceName;
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
            builder2.setTitle(C0017R$string.wifi_p2p_cancel_connect_title);
            builder2.setMessage(getActivity().getString(i2, new Object[]{str}));
            builder2.setPositiveButton(getActivity().getString(C0017R$string.dlg_ok), this.mCancelConnectListener);
            builder2.setNegativeButton(getActivity().getString(C0017R$string.dlg_cancel), (DialogInterface.OnClickListener) null);
            return builder2.create();
        } else if (i == 3) {
            View inflate = LayoutInflater.from(getPrefContext()).inflate(C0012R$layout.dialog_edittext, (ViewGroup) null);
            EditText editText = (EditText) inflate.findViewById(C0010R$id.edittext);
            this.mDeviceNameText = editText;
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
            String str4 = this.mSavedDeviceName;
            if (str4 != null) {
                this.mDeviceNameText.setText(str4);
                this.mDeviceNameText.setSelection(this.mSavedDeviceName.length());
            } else {
                WifiP2pDevice wifiP2pDevice = this.mThisDevice;
                if (wifiP2pDevice != null && !TextUtils.isEmpty(wifiP2pDevice.deviceName)) {
                    this.mDeviceNameText.setText(this.mThisDevice.deviceName);
                    this.mDeviceNameText.setSelection(0, this.mThisDevice.deviceName.length());
                }
            }
            this.mSavedDeviceName = null;
            AlertDialog.Builder builder3 = new AlertDialog.Builder(getActivity());
            builder3.setTitle(C0017R$string.wifi_p2p_menu_rename);
            builder3.setView(inflate);
            builder3.setPositiveButton(getActivity().getString(C0017R$string.dlg_ok), this.mRenameListener);
            builder3.setNegativeButton(getActivity().getString(C0017R$string.dlg_cancel), (DialogInterface.OnClickListener) null);
            return builder3.create();
        } else if (i != 4) {
            return null;
        } else {
            int i3 = C0017R$string.wifi_p2p_delete_group_message;
            AlertDialog.Builder builder4 = new AlertDialog.Builder(getActivity());
            builder4.setMessage(getActivity().getString(i3));
            builder4.setPositiveButton(getActivity().getString(C0017R$string.dlg_ok), this.mDeleteGroupListener);
            builder4.setNegativeButton(getActivity().getString(C0017R$string.dlg_cancel), this.mDeleteGroupListener);
            return builder4.create();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        WifiP2pPeer wifiP2pPeer = this.mSelectedWifiPeer;
        if (wifiP2pPeer != null) {
            bundle.putParcelable(SAVE_DIALOG_PEER, wifiP2pPeer.device);
        }
        EditText editText = this.mDeviceNameText;
        if (editText != null) {
            bundle.putString(SAVE_DEVICE_NAME, editText.getText().toString());
        }
        WifiP2pPersistentGroup wifiP2pPersistentGroup = this.mSelectedGroup;
        if (wifiP2pPersistentGroup != null) {
            bundle.putString(SAVE_SELECTED_GROUP, wifiP2pPersistentGroup.getGroupName());
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handlePeersChanged() {
        this.mPeerCategoryController.removeAllChildren();
        this.mConnectedDevices = 0;
        if (DBG) {
            Log.d("WifiP2pSettings", "List of available peers");
        }
        for (WifiP2pDevice wifiP2pDevice : this.mPeers.getDeviceList()) {
            if (DBG) {
                Log.d("WifiP2pSettings", "-> " + wifiP2pDevice);
            }
            this.mPeerCategoryController.addChild(new WifiP2pPeer(getPrefContext(), wifiP2pDevice));
            if (wifiP2pDevice.status == 0) {
                this.mConnectedDevices++;
            }
        }
        if (DBG) {
            Log.d("WifiP2pSettings", " mConnectedDevices " + this.mConnectedDevices);
        }
    }

    public void onPersistentGroupInfoAvailable(WifiP2pGroupList wifiP2pGroupList) {
        this.mPersistentCategoryController.removeAllChildren();
        for (WifiP2pGroup wifiP2pGroup : wifiP2pGroupList.getGroupList()) {
            if (DBG) {
                Log.d("WifiP2pSettings", " group " + wifiP2pGroup);
            }
            WifiP2pPersistentGroup wifiP2pPersistentGroup = new WifiP2pPersistentGroup(getPrefContext(), wifiP2pGroup);
            this.mPersistentCategoryController.addChild(wifiP2pPersistentGroup);
            if (wifiP2pPersistentGroup.getGroupName().equals(this.mSelectedGroupName)) {
                if (DBG) {
                    Log.d("WifiP2pSettings", "Selecting group " + wifiP2pPersistentGroup.getGroupName());
                }
                this.mSelectedGroup = wifiP2pPersistentGroup;
                this.mSelectedGroupName = null;
            }
        }
        if (this.mSelectedGroupName != null) {
            Log.w("WifiP2pSettings", " Selected group " + this.mSelectedGroupName + " disappered on next query ");
        }
    }

    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        if (DBG) {
            Log.d("WifiP2pSettings", "Requested peers are available");
        }
        this.mPeers = wifiP2pDeviceList;
        handlePeersChanged();
    }

    public void onDeviceInfoAvailable(WifiP2pDevice wifiP2pDevice) {
        this.mThisDevice = wifiP2pDevice;
        if (DBG) {
            Log.d("WifiP2pSettings", "Update device info: " + this.mThisDevice);
        }
        this.mThisDevicePreferenceController.updateDeviceName(this.mThisDevice);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleP2pStateChanged() {
        updateSearchMenu(DBG);
        this.mThisDevicePreferenceController.setEnabled(this.mWifiP2pEnabled);
        this.mPersistentCategoryController.setEnabled(this.mWifiP2pEnabled);
        this.mPeerCategoryController.setEnabled(this.mWifiP2pEnabled);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateSearchMenu(boolean z) {
        this.mWifiP2pSearching = z;
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void startSearch() {
        WifiP2pManager wifiP2pManager = this.mWifiP2pManager;
        if (wifiP2pManager != null && !this.mWifiP2pSearching) {
            wifiP2pManager.discoverPeers(this.mChannel, new WifiP2pManager.ActionListener(this) {
                /* class com.android.settings.wifi.p2p.WifiP2pSettings.AnonymousClass7 */

                public void onSuccess() {
                }

                public void onFailure(int i) {
                    if (WifiP2pSettings.DBG) {
                        Log.d("WifiP2pSettings", " discover fail " + i);
                    }
                }
            });
        }
    }
}
