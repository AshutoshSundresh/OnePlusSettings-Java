package com.android.settings.wifi.calling;

import android.content.Context;
import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@VisibleForTesting
public final class DisclaimerItemFactory {
    public static List<DisclaimerItem> create(Context context, int i) {
        List<DisclaimerItem> disclaimerItemList = getDisclaimerItemList(context, i);
        Iterator<DisclaimerItem> it = disclaimerItemList.iterator();
        while (it.hasNext()) {
            if (!it.next().shouldShow()) {
                it.remove();
            }
        }
        return disclaimerItemList;
    }

    private static List<DisclaimerItem> getDisclaimerItemList(Context context, int i) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new LocationPolicyDisclaimer(context, i));
        arrayList.add(new EmergencyCallLimitationDisclaimer(context, i));
        return arrayList;
    }
}
