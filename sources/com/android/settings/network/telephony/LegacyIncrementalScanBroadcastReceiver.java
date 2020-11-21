package com.android.settings.network.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.telephony.TelephonyScanManager;
import android.util.Log;
import com.android.internal.telephony.OperatorInfo;
import java.util.ArrayList;
import java.util.List;

public class LegacyIncrementalScanBroadcastReceiver extends BroadcastReceiver {
    private static int sPhoneCount;
    private Context mContext;
    private final TelephonyScanManager.NetworkScanCallback mNetworkScanCallback;
    private QueryDetails[] mQueryDetails;

    /* access modifiers changed from: package-private */
    public class QueryDetails {
        String[] storedScanInfo = null;

        QueryDetails(LegacyIncrementalScanBroadcastReceiver legacyIncrementalScanBroadcastReceiver) {
        }

        /* access modifiers changed from: package-private */
        public void concatScanInfo(String[] strArr) {
            String[] strArr2 = this.storedScanInfo;
            String[] strArr3 = new String[(strArr2.length + strArr.length)];
            System.arraycopy(strArr2, 0, strArr3, 0, strArr2.length);
            System.arraycopy(strArr, 0, strArr3, this.storedScanInfo.length, strArr.length);
            this.storedScanInfo = strArr3;
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.storedScanInfo = null;
        }
    }

    public LegacyIncrementalScanBroadcastReceiver(Context context, TelephonyScanManager.NetworkScanCallback networkScanCallback) {
        this.mContext = context;
        int activeModemCount = ((TelephonyManager) context.getSystemService("phone")).getActiveModemCount();
        sPhoneCount = activeModemCount;
        this.mQueryDetails = new QueryDetails[activeModemCount];
        for (int i = 0; i < sPhoneCount; i++) {
            this.mQueryDetails[i] = new QueryDetails(this);
        }
        this.mNetworkScanCallback = networkScanCallback;
    }

    public void onReceive(Context context, Intent intent) {
        Log.d("LegacyIncrementalScanBroadcastReceiver", "onReceive " + intent);
        if ("qualcomm.intent.action.ACTION_INCREMENTAL_NW_SCAN_IND".equals(intent.getAction())) {
            broadcastIncrementalQueryResults(intent);
        }
    }

    private void onResults(List<CellInfo> list) {
        this.mNetworkScanCallback.onResults(list);
    }

    private void onComplete() {
        this.mNetworkScanCallback.onComplete();
    }

    private void onError(int i) {
        this.mNetworkScanCallback.onError(i);
    }

    private void broadcastIncrementalQueryResults(Intent intent) {
        int i;
        int intExtra = intent.getIntExtra("scan_result", -1);
        int intExtra2 = intent.getIntExtra("sub_id", -1);
        Log.d("LegacyIncrementalScanBroadcastReceiver", "broadcastIncrementalQueryResults: phoneid: " + intExtra2 + ", result: " + intExtra);
        if (intExtra2 < 0 || intExtra2 >= sPhoneCount) {
            onError(2);
        } else if (intExtra == 3) {
            onError(10000);
        } else if (intExtra == 2) {
            onError(1);
        } else if (intExtra == 0 || intExtra == 1) {
            String[] stringArrayExtra = intent.getStringArrayExtra("incr_nw_scan_data");
            QueryDetails queryDetails = this.mQueryDetails[intExtra2];
            StringBuilder sb = new StringBuilder();
            sb.append("broadcastIncrementalQueryResults, scanInfo.length: ");
            if (stringArrayExtra == null) {
                i = 0;
            } else {
                i = stringArrayExtra.length;
            }
            sb.append(i);
            Log.d("LegacyIncrementalScanBroadcastReceiver", sb.toString());
            if (queryDetails.storedScanInfo == null || stringArrayExtra == null) {
                queryDetails.storedScanInfo = stringArrayExtra;
            } else {
                queryDetails.concatScanInfo(stringArrayExtra);
            }
            String[] strArr = queryDetails.storedScanInfo;
            if (strArr != null) {
                onResults(getCellInfosFromScanResult(strArr));
            }
            if (intExtra == 0) {
                queryDetails.reset();
                onComplete();
            }
        }
    }

    private List<CellInfo> getCellInfosFromScanResult(String[] strArr) {
        Log.d("LegacyIncrementalScanBroadcastReceiver", "Number of operators: " + (strArr.length / 4));
        ArrayList arrayList = new ArrayList();
        if (strArr.length >= 4 && strArr.length % 4 == 0) {
            for (int i = 0; i < strArr.length / 4; i++) {
                int i2 = i * 4;
                OperatorInfo operatorInfo = new OperatorInfo(strArr[i2 + 0], strArr[i2 + 1], strArr[i2 + 2], strArr[i2 + 3]);
                CellInfo convertLegacyIncrScanOperatorInfoToCellInfo = CellInfoUtil.convertLegacyIncrScanOperatorInfoToCellInfo(operatorInfo);
                Log.d("LegacyIncrementalScanBroadcastReceiver", "OperatorInfo: " + operatorInfo.toString() + " CellInfo: " + CellInfoUtil.cellInfoToString(convertLegacyIncrScanOperatorInfoToCellInfo));
                arrayList.add(convertLegacyIncrScanOperatorInfoToCellInfo);
            }
        }
        return arrayList;
    }
}
