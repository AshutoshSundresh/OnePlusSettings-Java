package com.android.settings;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.android.settings.AppWidgetLoader.LabelledItem;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppWidgetLoader<Item extends LabelledItem> {
    private AppWidgetManager mAppWidgetManager;
    private Context mContext;
    ItemConstructor<Item> mItemConstructor;

    public interface ItemConstructor<Item> {
        Item createItem(Context context, AppWidgetProviderInfo appWidgetProviderInfo, Bundle bundle);
    }

    /* access modifiers changed from: package-private */
    public interface LabelledItem {
        CharSequence getLabel();
    }

    public AppWidgetLoader(Context context, AppWidgetManager appWidgetManager, ItemConstructor<Item> itemConstructor) {
        this.mContext = context;
        this.mAppWidgetManager = appWidgetManager;
        this.mItemConstructor = itemConstructor;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003f, code lost:
        r4 = null;
        r5 = null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void putCustomAppWidgets(java.util.List<Item> r10, android.content.Intent r11) {
        /*
        // Method dump skipped, instructions count: 175
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.AppWidgetLoader.putCustomAppWidgets(java.util.List, android.content.Intent):void");
    }

    /* access modifiers changed from: package-private */
    public void putAppWidgetItems(List<AppWidgetProviderInfo> list, List<Bundle> list2, List<Item> list3, int i, boolean z) {
        if (list != null) {
            int size = list.size();
            for (int i2 = 0; i2 < size; i2++) {
                AppWidgetProviderInfo appWidgetProviderInfo = list.get(i2);
                if (z || (appWidgetProviderInfo.widgetCategory & i) != 0) {
                    list3.add(this.mItemConstructor.createItem(this.mContext, appWidgetProviderInfo, list2 != null ? list2.get(i2) : null));
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public List<Item> getItems(Intent intent) {
        boolean booleanExtra = intent.getBooleanExtra("customSort", true);
        ArrayList arrayList = new ArrayList();
        putInstalledAppWidgets(arrayList, intent.getIntExtra("categoryFilter", 1));
        if (booleanExtra) {
            putCustomAppWidgets(arrayList, intent);
        }
        Collections.sort(arrayList, new Comparator<Item>(this) {
            /* class com.android.settings.AppWidgetLoader.AnonymousClass1 */
            Collator mCollator = Collator.getInstance();

            public int compare(Item item, Item item2) {
                return this.mCollator.compare(item.getLabel(), item2.getLabel());
            }
        });
        if (!booleanExtra) {
            ArrayList arrayList2 = new ArrayList();
            putCustomAppWidgets(arrayList2, intent);
            arrayList.addAll(arrayList2);
        }
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public void putInstalledAppWidgets(List<Item> list, int i) {
        putAppWidgetItems(this.mAppWidgetManager.getInstalledProviders(i), null, list, i, false);
    }
}
