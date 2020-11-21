package com.oneplus.security.utils;

import android.content.Context;
import android.content.res.Resources;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;

public class OPSNSUtils {
    public static String getSimName(Context context, int i, boolean z) {
        return getSimName(context, i, z ? context.getResources().getString(C0017R$string.carrier_info_default_summary) : null);
    }

    public static String getSimName(Context context, int i, String str) {
        String str2 = null;
        if (context == null) {
            return null;
        }
        SubscriptionInfo activeSubscriptionInfoForSimSlotIndex = SubscriptionManager.from(context).getActiveSubscriptionInfoForSimSlotIndex(i);
        if (activeSubscriptionInfoForSimSlotIndex != null) {
            int nameSource = activeSubscriptionInfoForSimSlotIndex.getNameSource();
            if (nameSource == 0) {
                str2 = getDisplayName(context, activeSubscriptionInfoForSimSlotIndex);
                if (TextUtils.isEmpty(str2)) {
                    str2 = getOpeName(context, activeSubscriptionInfoForSimSlotIndex, i);
                }
            } else if (activeSubscriptionInfoForSimSlotIndex.getDisplayName() != null) {
                str2 = getDisplayName(context, activeSubscriptionInfoForSimSlotIndex);
            }
            logd("nameSource = " + nameSource + "slotName = " + str2);
            return TextUtils.isEmpty(str2) ? str : str2;
        }
        String simOperatorNameForPhone = TelephonyManager.getDefault().getSimOperatorNameForPhone(i);
        logd("getSimName subinfo is null slotName = " + simOperatorNameForPhone);
        return simOperatorNameForPhone;
    }

    private static String getOpeName(Context context, SubscriptionInfo subscriptionInfo, int i) {
        if (context == null) {
            return null;
        }
        if (subscriptionInfo == null) {
            String simOperatorNameForPhone = TelephonyManager.getDefault().getSimOperatorNameForPhone(i);
            logd("getOpeName subinfo is null slotName = " + simOperatorNameForPhone);
            return simOperatorNameForPhone;
        }
        String simOperator = TelephonyManager.getDefault().getSimOperator(subscriptionInfo.getSubscriptionId());
        logd("simOpe = " + simOperator);
        if (simOperator == null) {
            return null;
        }
        if (simOperator.startsWith("46000") || simOperator.startsWith("46002") || simOperator.startsWith("46007")) {
            return context.getResources().getString(C0017R$string.operator_cm);
        }
        if (simOperator.startsWith("46001") || simOperator.startsWith("46009")) {
            return context.getResources().getString(C0017R$string.operator_cu);
        }
        if (simOperator.startsWith("46003") || simOperator.startsWith("46006") || simOperator.startsWith("46011")) {
            return context.getResources().getString(C0017R$string.operator_ct);
        }
        return getDisplayName(context, subscriptionInfo);
    }

    private static String getDisplayName(Context context, SubscriptionInfo subscriptionInfo) {
        CharSequence displayName = subscriptionInfo.getDisplayName();
        String localString = !TextUtils.isEmpty(displayName) ? getLocalString(context, displayName.toString()) : null;
        logd("getDisplayName name = " + localString);
        return localString;
    }

    private static String getLocalString(Context context, String str) {
        Resources resources = context.getResources();
        String[] stringArray = resources.getStringArray(C0003R$array.datausage_original_operator_names);
        String[] stringArray2 = resources.getStringArray(C0003R$array.datausage_locale_operator_names);
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i].equalsIgnoreCase(str)) {
                return resources.getString(resources.getIdentifier(stringArray2[i], "string", context.getPackageName()));
            }
        }
        return str;
    }

    public static int findSlotIdBySubId(int i) {
        int phoneCount = TelephonyManager.getDefault().getPhoneCount();
        for (int i2 = 0; i2 < phoneCount; i2++) {
            int[] subId = SubscriptionManager.getSubId(i2);
            if (subId != null && subId.length > 0 && i == subId[0]) {
                return i2;
            }
        }
        return 0;
    }

    public static int findSubIdBySlotId(int i) {
        Log.d("OPSNSUtils", "slotId: " + i);
        int[] subId = SubscriptionManager.getSubId(i);
        if (subId == null) {
            return -1;
        }
        Log.d("OPSNSUtils", "return : " + subId[0]);
        return subId[0];
    }

    private static void logd(String str) {
        LogUtils.d("OPSNSUtils", str);
    }
}
