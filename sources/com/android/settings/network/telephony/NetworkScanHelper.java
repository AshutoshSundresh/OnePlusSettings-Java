package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.CellInfo;
import android.telephony.NetworkScan;
import android.telephony.NetworkScanRequest;
import android.telephony.RadioAccessSpecifier;
import android.telephony.TelephonyManager;
import android.telephony.TelephonyScanManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import org.codeaurora.internal.IExtTelephony;

public class NetworkScanHelper {
    private IntentFilter filter = new IntentFilter("qualcomm.intent.action.ACTION_INCREMENTAL_NW_SCAN_IND");
    private Context mContext;
    private final Executor mExecutor;
    private IExtTelephony mExtTelephony;
    private final TelephonyScanManager.NetworkScanCallback mInternalNetworkScanCallback;
    private final LegacyIncrementalScanBroadcastReceiver mLegacyIncrScanReceiver;
    private final NetworkScanCallback mNetworkScanCallback;
    private NetworkScan mNetworkScanRequester;
    private final TelephonyManager mTelephonyManager;

    public interface NetworkScanCallback {
        void onComplete();

        void onError(int i);

        void onResults(List<CellInfo> list);
    }

    public NetworkScanHelper(Context context, TelephonyManager telephonyManager, NetworkScanCallback networkScanCallback, Executor executor) {
        this.mContext = context;
        this.mTelephonyManager = telephonyManager;
        this.mNetworkScanCallback = networkScanCallback;
        this.mInternalNetworkScanCallback = new NetworkScanCallbackImpl();
        this.mExecutor = executor;
        this.mLegacyIncrScanReceiver = new LegacyIncrementalScanBroadcastReceiver(this.mContext, this.mInternalNetworkScanCallback);
    }

    private NetworkScanRequest createNetworkScanForPreferredAccessNetworks() {
        long preferredNetworkTypeBitmask = this.mTelephonyManager.getPreferredNetworkTypeBitmask() & 906119;
        ArrayList arrayList = new ArrayList();
        int i = (preferredNetworkTypeBitmask > 0 ? 1 : (preferredNetworkTypeBitmask == 0 ? 0 : -1));
        if (i == 0 || (32843 & preferredNetworkTypeBitmask) != 0) {
            arrayList.add(new RadioAccessSpecifier(1, null, null));
        }
        if (i == 0 || (93108 & preferredNetworkTypeBitmask) != 0) {
            arrayList.add(new RadioAccessSpecifier(2, null, null));
        }
        if (i == 0 || (397312 & preferredNetworkTypeBitmask) != 0) {
            arrayList.add(new RadioAccessSpecifier(3, null, null));
        }
        if (i == 0 || (preferredNetworkTypeBitmask & 524288) != 0) {
            arrayList.add(new RadioAccessSpecifier(6, null, null));
        }
        return new NetworkScanRequest(0, (RadioAccessSpecifier[]) arrayList.toArray(new RadioAccessSpecifier[arrayList.size()]), 5, 254, true, 3, null);
    }

    public void startNetworkScan(int i) {
        Log.d("NetworkScanHelper", "startNetworkScan: " + i);
        if (i == 1) {
            if (this.mNetworkScanRequester == null) {
                NetworkScan requestNetworkScan = this.mTelephonyManager.requestNetworkScan(createNetworkScanForPreferredAccessNetworks(), this.mExecutor, this.mInternalNetworkScanCallback);
                this.mNetworkScanRequester = requestNetworkScan;
                if (requestNetworkScan == null) {
                    onError(10000);
                }
            }
        } else if (i == 2) {
            this.mContext.registerReceiver(this.mLegacyIncrScanReceiver, this.filter);
            boolean z = false;
            IExtTelephony asInterface = IExtTelephony.Stub.asInterface(ServiceManager.getService("qti.radio.extphone"));
            this.mExtTelephony = asInterface;
            try {
                z = asInterface.performIncrementalScan(this.mTelephonyManager.getSlotIndex());
            } catch (RemoteException | NullPointerException e) {
                Log.e("NetworkScanHelper", "performIncrementalScan Exception: ", e);
            }
            Log.d("NetworkScanHelper", "success: " + z);
            if (!z) {
                onError(10000);
            }
        }
    }

    public void stopNetworkQuery() {
        NetworkScan networkScan = this.mNetworkScanRequester;
        if (networkScan != null) {
            networkScan.stopScan();
            this.mNetworkScanRequester = null;
        }
        try {
            if (this.mExtTelephony != null) {
                int slotIndex = this.mTelephonyManager.getSlotIndex();
                if (slotIndex < 0 || slotIndex >= this.mTelephonyManager.getActiveModemCount()) {
                    Log.d("NetworkScanHelper", "slotIndex is invalid, skipping abort");
                } else {
                    this.mExtTelephony.abortIncrementalScan(slotIndex);
                }
                this.mExtTelephony = null;
                this.mContext.unregisterReceiver(this.mLegacyIncrScanReceiver);
            }
        } catch (RemoteException | NullPointerException e) {
            Log.e("NetworkScanHelper", "abortIncrementalScan Exception: ", e);
        } catch (IllegalArgumentException unused) {
            Log.e("NetworkScanHelper", "IllegalArgumentException");
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onResults(List<CellInfo> list) {
        this.mNetworkScanCallback.onResults(list);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onComplete() {
        this.mNetworkScanCallback.onComplete();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onError(int i) {
        this.mNetworkScanCallback.onError(i);
    }

    private final class NetworkScanCallbackImpl extends TelephonyScanManager.NetworkScanCallback {
        private NetworkScanCallbackImpl() {
        }

        @Override // android.telephony.TelephonyScanManager.NetworkScanCallback
        public void onResults(List<CellInfo> list) {
            Log.d("NetworkScanHelper", "Async scan onResults() results = " + CellInfoUtil.cellInfoListToString(list));
            NetworkScanHelper.this.onResults(list);
        }

        public void onComplete() {
            Log.d("NetworkScanHelper", "async scan onComplete()");
            NetworkScanHelper.this.onComplete();
        }

        public void onError(int i) {
            Log.d("NetworkScanHelper", "async scan onError() errorCode = " + i);
            NetworkScanHelper.this.onError(i);
        }
    }
}
