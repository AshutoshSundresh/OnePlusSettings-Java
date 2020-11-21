package com.oneplus.security.receiver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.oneplus.security.firewall.NetworkRestrictService;
import com.oneplus.security.utils.LogUtils;

public class NetworkStateUtils {
    private static int netWorkState;

    public static int getNetWorkState(Context context) {
        if (netWorkState == 0) {
            netWorkState = reGetNetworkState(context);
        }
        LogUtils.d("NetworkStateUtils", "netWorkState=" + netWorkState);
        return netWorkState;
    }

    public static boolean currentNetWorkIsMobileData(Context context) {
        int netWorkState2 = getNetWorkState(context);
        return (netWorkState2 == 0 || netWorkState2 == 1) ? false : true;
    }

    public static boolean currentNetWorkIsWlan(Context context) {
        if (netWorkState == 0) {
            netWorkState = reGetNetworkState(context);
        }
        return netWorkState == 1;
    }

    public static boolean isNetWorkAvailable(Context context) {
        if (netWorkState == 0) {
            netWorkState = reGetNetworkState(context);
        }
        return netWorkState != 0;
    }

    public static void onReceiveNetWorkStateChanged(Context context) {
        netWorkState = reGetNetworkState(context);
        LogUtils.d("NetworkStateUtils", "------netWorkState--------" + netWorkState);
        NetworkRestrictService.applyRules(context);
    }

    private static int reGetNetworkState(Context context) {
        NetworkInfo activeNetworkInfo;
        NetworkInfo.State state;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (!(connectivityManager == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null || !activeNetworkInfo.isAvailable())) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(1);
            if (networkInfo != null && (state = networkInfo.getState()) != null && (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING)) {
                return 1;
            }
            NetworkInfo networkInfo2 = connectivityManager.getNetworkInfo(0);
            if (networkInfo2 != null) {
                NetworkInfo.State state2 = networkInfo2.getState();
                String subtypeName = networkInfo2.getSubtypeName();
                if (state2 != null && (state2 == NetworkInfo.State.CONNECTED || state2 == NetworkInfo.State.CONNECTING)) {
                    switch (activeNetworkInfo.getSubtype()) {
                        case 1:
                        case 2:
                        case 4:
                        case 7:
                        case 11:
                            return 2;
                        case 3:
                        case 5:
                        case 6:
                        case 8:
                        case 9:
                        case 10:
                        case 12:
                        case 14:
                        case 15:
                            return 3;
                        case 13:
                            return 4;
                        default:
                            return (subtypeName.equalsIgnoreCase("TD-SCDMA") || subtypeName.equalsIgnoreCase("WCDMA") || subtypeName.equalsIgnoreCase("CDMA2000")) ? 3 : 5;
                    }
                }
            }
        }
        return 0;
    }
}
