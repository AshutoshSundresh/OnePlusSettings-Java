package com.android.settings.network;

import android.content.Context;
import android.os.ParcelUuid;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.UiccSlotInfo;
import com.android.internal.util.CollectionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SubscriptionUtil {
    private static List<SubscriptionInfo> sActiveResultsForTesting;
    private static List<SubscriptionInfo> sAvailableResultsForTesting;

    public static void setAvailableSubscriptionsForTesting(List<SubscriptionInfo> list) {
        sAvailableResultsForTesting = list;
    }

    public static void setActiveSubscriptionsForTesting(List<SubscriptionInfo> list) {
        sActiveResultsForTesting = list;
    }

    public static List<SubscriptionInfo> getActiveSubscriptions(SubscriptionManager subscriptionManager) {
        List<SubscriptionInfo> list = sActiveResultsForTesting;
        if (list != null) {
            return list;
        }
        List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        return activeSubscriptionInfoList == null ? new ArrayList() : activeSubscriptionInfoList;
    }

    static boolean isInactiveInsertedPSim(UiccSlotInfo uiccSlotInfo) {
        if (uiccSlotInfo != null && !uiccSlotInfo.getIsEuicc() && !uiccSlotInfo.getIsActive() && uiccSlotInfo.getCardStateInfo() == 2) {
            return true;
        }
        return false;
    }

    public static List<SubscriptionInfo> getAvailableSubscriptions(Context context) {
        List<SubscriptionInfo> list = sAvailableResultsForTesting;
        if (list != null) {
            return list;
        }
        return new ArrayList(CollectionUtils.emptyIfNull(getSelectableSubscriptionInfoList(context)));
    }

    public static SubscriptionInfo getAvailableSubscription(Context context, ProxySubscriptionManager proxySubscriptionManager, int i) {
        SubscriptionInfo accessibleSubscriptionInfo = proxySubscriptionManager.getAccessibleSubscriptionInfo(i);
        if (accessibleSubscriptionInfo == null) {
            return null;
        }
        ParcelUuid groupUuid = accessibleSubscriptionInfo.getGroupUuid();
        if (groupUuid == null || isPrimarySubscriptionWithinSameUuid(getUiccSlotsInfo(context), groupUuid, proxySubscriptionManager.getAccessibleSubscriptionsInfo(), i)) {
            return accessibleSubscriptionInfo;
        }
        return null;
    }

    private static UiccSlotInfo[] getUiccSlotsInfo(Context context) {
        return ((TelephonyManager) context.getSystemService(TelephonyManager.class)).getUiccSlotsInfo();
    }

    private static boolean isPrimarySubscriptionWithinSameUuid(UiccSlotInfo[] uiccSlotInfoArr, ParcelUuid parcelUuid, List<SubscriptionInfo> list, int i) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        for (SubscriptionInfo subscriptionInfo : list) {
            if (parcelUuid.equals(subscriptionInfo.getGroupUuid())) {
                if (!subscriptionInfo.isEmbedded()) {
                    arrayList.add(subscriptionInfo);
                } else {
                    if (!subscriptionInfo.isOpportunistic()) {
                        arrayList2.add(subscriptionInfo);
                    }
                    if (subscriptionInfo.getSimSlotIndex() != -1) {
                        arrayList3.add(subscriptionInfo);
                    } else {
                        arrayList4.add(subscriptionInfo);
                    }
                }
            }
        }
        if (uiccSlotInfoArr != null && arrayList.size() > 0) {
            SubscriptionInfo searchForSubscriptionId = searchForSubscriptionId(arrayList, i);
            if (searchForSubscriptionId == null) {
                return false;
            }
            for (UiccSlotInfo uiccSlotInfo : uiccSlotInfoArr) {
                if (!(uiccSlotInfo == null || uiccSlotInfo.getIsEuicc() || uiccSlotInfo.getLogicalSlotIdx() != searchForSubscriptionId.getSimSlotIndex())) {
                    return true;
                }
            }
            return false;
        } else if (arrayList2.size() > 0) {
            Iterator it = arrayList2.iterator();
            int i2 = 0;
            boolean z = false;
            while (it.hasNext()) {
                SubscriptionInfo subscriptionInfo2 = (SubscriptionInfo) it.next();
                boolean z2 = subscriptionInfo2.getSubscriptionId() == i;
                if (subscriptionInfo2.getSimSlotIndex() == -1) {
                    z |= z2;
                } else if (z2) {
                    return true;
                } else {
                    i2++;
                }
            }
            if (i2 > 0) {
                return false;
            }
            return z;
        } else if (arrayList.size() > 0) {
            return false;
        } else {
            return arrayList3.size() > 0 ? ((SubscriptionInfo) arrayList3.get(0)).getSubscriptionId() == i : ((SubscriptionInfo) arrayList4.get(0)).getSubscriptionId() == i;
        }
    }

    private static SubscriptionInfo searchForSubscriptionId(List<SubscriptionInfo> list, int i) {
        for (SubscriptionInfo subscriptionInfo : list) {
            if (subscriptionInfo.getSubscriptionId() == i) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    public static String getDisplayName(SubscriptionInfo subscriptionInfo) {
        CharSequence displayName = subscriptionInfo.getDisplayName();
        return displayName != null ? displayName.toString() : "";
    }

    public static boolean showToggleForPhysicalSim(SubscriptionManager subscriptionManager) {
        return subscriptionManager.canDisablePhysicalSubscription();
    }

    public static int getPhoneId(Context context, int i) {
        SubscriptionInfo activeSubscriptionInfo;
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        if (subscriptionManager == null || (activeSubscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(i)) == null) {
            return -1;
        }
        return activeSubscriptionInfo.getSimSlotIndex();
    }

    public static List<SubscriptionInfo> getSelectableSubscriptionInfoList(Context context) {
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        List<SubscriptionInfo> availableSubscriptionInfoList = subscriptionManager.getAvailableSubscriptionInfoList();
        if (availableSubscriptionInfoList == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        HashMap hashMap = new HashMap();
        for (SubscriptionInfo subscriptionInfo : availableSubscriptionInfoList) {
            if (isSubscriptionVisible(subscriptionManager, context, subscriptionInfo)) {
                ParcelUuid groupUuid = subscriptionInfo.getGroupUuid();
                if (groupUuid == null) {
                    arrayList.add(subscriptionInfo);
                } else if (!hashMap.containsKey(groupUuid) || (((SubscriptionInfo) hashMap.get(groupUuid)).getSimSlotIndex() == -1 && subscriptionInfo.getSimSlotIndex() != -1)) {
                    arrayList.remove(hashMap.get(groupUuid));
                    arrayList.add(subscriptionInfo);
                    hashMap.put(groupUuid, subscriptionInfo);
                }
            }
        }
        return arrayList;
    }

    private static boolean isSubscriptionVisible(SubscriptionManager subscriptionManager, Context context, SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo == null) {
            return false;
        }
        if (subscriptionInfo.getGroupUuid() == null || !subscriptionInfo.isOpportunistic()) {
            return true;
        }
        return ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(subscriptionInfo.getSubscriptionId()).hasCarrierPrivileges() || subscriptionManager.canManageSubscription(subscriptionInfo);
    }
}
