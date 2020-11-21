package com.oneplus.settings.defaultapp.apptype;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

public class DefaultAppTypeEmail extends DefaultAppTypeInfo {
    @Override // com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo
    public List<Intent> getAppIntent() {
        ArrayList arrayList = new ArrayList();
        Intent intent = new Intent("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.parse("mailto://"), null);
        arrayList.add(intent);
        Intent intent2 = new Intent("android.intent.action.SENDTO");
        intent2.addCategory("android.intent.category.DEFAULT");
        intent2.setDataAndType(Uri.parse("mailto://"), null);
        arrayList.add(intent2);
        Intent intent3 = new Intent("android.intent.action.VIEW");
        intent3.addCategory("android.intent.category.DEFAULT");
        intent3.addCategory("android.intent.category.BROWSABLE");
        intent3.setDataAndType(Uri.parse("mailto://"), null);
        arrayList.add(intent3);
        return arrayList;
    }

    @Override // com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo
    public List<IntentFilter> getAppFilter() {
        ArrayList arrayList = new ArrayList();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SEND");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        intentFilter.addDataScheme("mailto");
        arrayList.add(intentFilter);
        intentFilter.addAction("android.intent.action.SENDTO");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        intentFilter.addDataScheme("mailto");
        arrayList.add(intentFilter);
        intentFilter.addAction("android.intent.action.VIEW");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        intentFilter.addCategory("android.intent.category.BROWSABLE");
        intentFilter.addDataScheme("mailto");
        arrayList.add(intentFilter);
        return arrayList;
    }

    @Override // com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo
    public List<Integer> getAppMatchParam() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(6291456);
        arrayList.add(2097152);
        arrayList.add(2097152);
        return arrayList;
    }
}
