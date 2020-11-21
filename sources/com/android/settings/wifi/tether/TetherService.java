package com.android.settings.wifi.tether;

import android.app.Service;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.net.TetheringManager;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TetherService extends Service {
    private static final boolean DEBUG = Log.isLoggable("TetherService", 3);
    public static final String EXTRA_RESULT = "EntitlementResult";
    public static final String EXTRA_TETHER_PROVISIONING_RESPONSE = "android.net.extra.TETHER_PROVISIONING_RESPONSE";
    public static final String EXTRA_TETHER_SILENT_PROVISIONING_ACTION = "android.net.extra.TETHER_SILENT_PROVISIONING_ACTION";
    public static final String EXTRA_TETHER_SUBID = "android.net.extra.TETHER_SUBID";
    private ArrayList<Integer> mCurrentTethers;
    private int mCurrentTypeIndex;
    private String mExpectedProvisionResponseAction = null;
    private boolean mInProvisionCheck;
    private ArrayMap<Integer, List<ResultReceiver>> mPendingCallbacks;
    private String mProvisionAction;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.wifi.tether.TetherService.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            if (TetherService.DEBUG) {
                Log.d("TetherService", "Got provision result " + intent);
            }
            if (!intent.getAction().equals(TetherService.this.mExpectedProvisionResponseAction)) {
                Log.e("TetherService", "Received provisioning response for unexpected action=" + intent.getAction() + ", expected=" + TetherService.this.mExpectedProvisionResponseAction);
            } else if (!TetherService.this.mInProvisionCheck) {
                Log.e("TetherService", "Unexpected provisioning response when not in provisioning check" + intent);
            } else {
                int intValue = ((Integer) TetherService.this.mCurrentTethers.get(TetherService.this.mCurrentTypeIndex)).intValue();
                TetherService.this.mInProvisionCheck = TetherService.DEBUG;
                int intExtra = intent.getIntExtra(TetherService.EXTRA_RESULT, 0);
                if (intExtra != -1) {
                    TetherService.this.disableTethering(intValue);
                }
                TetherService.this.fireCallbacksForType(intValue, intExtra);
                if (TetherService.access$304(TetherService.this) >= TetherService.this.mCurrentTethers.size()) {
                    TetherService.this.stopSelf();
                    return;
                }
                TetherService tetherService = TetherService.this;
                tetherService.startProvisioning(tetherService.mCurrentTypeIndex);
            }
        }
    };
    private int mSubId = -1;
    private TetherServiceWrapper mWrapper;

    public IBinder onBind(Intent intent) {
        return null;
    }

    static /* synthetic */ int access$304(TetherService tetherService) {
        int i = tetherService.mCurrentTypeIndex + 1;
        tetherService.mCurrentTypeIndex = i;
        return i;
    }

    public void onCreate() {
        super.onCreate();
        if (DEBUG) {
            Log.d("TetherService", "Creating TetherService");
        }
        this.mCurrentTethers = stringToTethers(getSharedPreferences("tetherPrefs", 0).getString("currentTethers", ""));
        this.mCurrentTypeIndex = 0;
        ArrayMap<Integer, List<ResultReceiver>> arrayMap = new ArrayMap<>(3);
        this.mPendingCallbacks = arrayMap;
        arrayMap.put(0, new ArrayList());
        this.mPendingCallbacks.put(1, new ArrayList());
        this.mPendingCallbacks.put(2, new ArrayList());
        this.mPendingCallbacks.put(5, new ArrayList());
    }

    private void maybeRegisterReceiver(String str) {
        if (!Objects.equals(str, this.mExpectedProvisionResponseAction)) {
            if (this.mExpectedProvisionResponseAction != null) {
                unregisterReceiver(this.mReceiver);
            }
            registerReceiver(this.mReceiver, new IntentFilter(str), "android.permission.TETHER_PRIVILEGED", null);
            this.mExpectedProvisionResponseAction = str;
            if (DEBUG) {
                Log.d("TetherService", "registerReceiver " + str);
            }
        }
    }

    private int stopSelfAndStartNotSticky() {
        stopSelf();
        return 2;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent.hasExtra(EXTRA_TETHER_SUBID)) {
            int intExtra = intent.getIntExtra(EXTRA_TETHER_SUBID, -1);
            int activeDataSubscriptionId = getTetherServiceWrapper().getActiveDataSubscriptionId();
            if (intExtra != activeDataSubscriptionId) {
                Log.e("TetherService", "This Provisioning request is outdated, current subId: " + activeDataSubscriptionId);
                if (this.mInProvisionCheck) {
                    return 2;
                }
                stopSelf();
                return 2;
            }
            this.mSubId = activeDataSubscriptionId;
        }
        if (intent.hasExtra("extraAddTetherType")) {
            int intExtra2 = intent.getIntExtra("extraAddTetherType", -1);
            ResultReceiver resultReceiver = (ResultReceiver) intent.getParcelableExtra("extraProvisionCallback");
            if (resultReceiver != null) {
                List<ResultReceiver> list = this.mPendingCallbacks.get(Integer.valueOf(intExtra2));
                if (list != null) {
                    list.add(resultReceiver);
                } else {
                    Log.e("TetherService", "Invalid tethering type " + intExtra2 + ", stopping");
                    resultReceiver.send(1, null);
                    return stopSelfAndStartNotSticky();
                }
            }
            if (!this.mCurrentTethers.contains(Integer.valueOf(intExtra2))) {
                if (DEBUG) {
                    Log.d("TetherService", "Adding tether " + intExtra2);
                }
                this.mCurrentTethers.add(Integer.valueOf(intExtra2));
            }
        }
        String stringExtra = intent.getStringExtra(EXTRA_TETHER_SILENT_PROVISIONING_ACTION);
        this.mProvisionAction = stringExtra;
        if (stringExtra == null) {
            Log.e("TetherService", "null provisioning action, stop ");
            return stopSelfAndStartNotSticky();
        }
        String stringExtra2 = intent.getStringExtra(EXTRA_TETHER_PROVISIONING_RESPONSE);
        if (stringExtra2 == null) {
            Log.e("TetherService", "null provisioning response, stop ");
            return stopSelfAndStartNotSticky();
        }
        maybeRegisterReceiver(stringExtra2);
        if (intent.hasExtra("extraRemTetherType")) {
            if (!this.mInProvisionCheck) {
                int intExtra3 = intent.getIntExtra("extraRemTetherType", -1);
                int indexOf = this.mCurrentTethers.indexOf(Integer.valueOf(intExtra3));
                if (DEBUG) {
                    Log.d("TetherService", "Removing tether " + intExtra3 + ", index " + indexOf);
                }
                if (indexOf >= 0) {
                    removeTypeAtIndex(indexOf);
                }
            } else if (DEBUG) {
                Log.d("TetherService", "Don't remove tether type during provisioning");
            }
        }
        if (intent.getBooleanExtra("extraRunProvision", DEBUG)) {
            startProvisioning(this.mCurrentTypeIndex);
            return 3;
        } else if (this.mInProvisionCheck) {
            return 3;
        } else {
            if (DEBUG) {
                Log.d("TetherService", "Stopping self.  startid: " + i2);
            }
            return stopSelfAndStartNotSticky();
        }
    }

    public void onDestroy() {
        if (this.mInProvisionCheck) {
            Log.e("TetherService", "TetherService getting destroyed while mid-provisioning" + this.mCurrentTethers.get(this.mCurrentTypeIndex));
        }
        getSharedPreferences("tetherPrefs", 0).edit().putString("currentTethers", tethersToString(this.mCurrentTethers)).commit();
        if (this.mExpectedProvisionResponseAction != null) {
            unregisterReceiver(this.mReceiver);
            this.mExpectedProvisionResponseAction = null;
        }
        if (DEBUG) {
            Log.d("TetherService", "Destroying TetherService");
        }
        super.onDestroy();
    }

    private void removeTypeAtIndex(int i) {
        this.mCurrentTethers.remove(i);
        if (DEBUG) {
            Log.d("TetherService", "mCurrentTypeIndex: " + this.mCurrentTypeIndex);
        }
        int i2 = this.mCurrentTypeIndex;
        if (i <= i2 && i2 > 0) {
            this.mCurrentTypeIndex = i2 - 1;
        }
    }

    private ArrayList<Integer> stringToTethers(String str) {
        String[] split;
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (TextUtils.isEmpty(str)) {
            return arrayList;
        }
        for (String str2 : str.split(",")) {
            arrayList.add(Integer.valueOf(Integer.parseInt(str2)));
        }
        return arrayList;
    }

    private String tethersToString(ArrayList<Integer> arrayList) {
        StringBuffer stringBuffer = new StringBuffer();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                stringBuffer.append(',');
            }
            stringBuffer.append(arrayList.get(i));
        }
        return stringBuffer.toString();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void disableTethering(int i) {
        ((TetheringManager) getSystemService("tethering")).stopTethering(i);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void startProvisioning(int i) {
        if (i < this.mCurrentTethers.size()) {
            Intent provisionBroadcastIntent = getProvisionBroadcastIntent(i);
            setEntitlementAppActive(i);
            if (DEBUG) {
                Log.d("TetherService", "Sending provisioning broadcast: " + provisionBroadcastIntent.getAction() + " type: " + this.mCurrentTethers.get(i));
            }
            sendBroadcast(provisionBroadcastIntent);
            this.mInProvisionCheck = true;
        }
    }

    private Intent getProvisionBroadcastIntent(int i) {
        if (this.mProvisionAction == null) {
            Log.wtf("TetherService", "null provisioning action");
        }
        Intent intent = new Intent(this.mProvisionAction);
        intent.putExtra("TETHER_TYPE", this.mCurrentTethers.get(i).intValue());
        intent.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", this.mSubId);
        intent.setFlags(285212672);
        return intent;
    }

    private void setEntitlementAppActive(int i) {
        List<ResolveInfo> queryBroadcastReceivers = getPackageManager().queryBroadcastReceivers(getProvisionBroadcastIntent(i), 131072);
        if (queryBroadcastReceivers.isEmpty()) {
            Log.e("TetherService", "No found BroadcastReceivers for provision intent.");
            return;
        }
        for (ResolveInfo resolveInfo : queryBroadcastReceivers) {
            if (resolveInfo.activityInfo.applicationInfo.isSystemApp()) {
                getTetherServiceWrapper().setAppInactive(resolveInfo.activityInfo.packageName, DEBUG);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void fireCallbacksForType(int i, int i2) {
        List<ResultReceiver> list = this.mPendingCallbacks.get(Integer.valueOf(i));
        if (list != null) {
            int i3 = i2 == -1 ? 0 : 11;
            for (ResultReceiver resultReceiver : list) {
                if (DEBUG) {
                    Log.d("TetherService", "Firing result: " + i3 + " to callback");
                }
                resultReceiver.send(i3, null);
            }
            list.clear();
        }
    }

    /* access modifiers changed from: package-private */
    public void setTetherServiceWrapper(TetherServiceWrapper tetherServiceWrapper) {
        this.mWrapper = tetherServiceWrapper;
    }

    private TetherServiceWrapper getTetherServiceWrapper() {
        if (this.mWrapper == null) {
            this.mWrapper = new TetherServiceWrapper(this);
        }
        return this.mWrapper;
    }

    public static class TetherServiceWrapper {
        private final UsageStatsManager mUsageStatsManager;

        TetherServiceWrapper(Context context) {
            this.mUsageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        }

        /* access modifiers changed from: package-private */
        public void setAppInactive(String str, boolean z) {
            this.mUsageStatsManager.setAppInactive(str, z);
        }

        /* access modifiers changed from: package-private */
        public int getActiveDataSubscriptionId() {
            return SubscriptionManager.getActiveDataSubscriptionId();
        }
    }
}
