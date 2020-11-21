package com.oneplus.settings.defaultapp.apptype;

import android.content.Intent;
import android.content.IntentFilter;
import java.util.ArrayList;
import java.util.List;

public class DefaultAppTypeCamera extends DefaultAppTypeInfo {
    @Override // com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo
    public List<Intent> getAppIntent() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new Intent("android.media.action.IMAGE_CAPTURE"));
        arrayList.add(new Intent("android.media.action.VIDEO_CAPTURE"));
        arrayList.add(new Intent("android.media.action.VIDEO_CAMERA"));
        arrayList.add(new Intent("com.oppo.action.CAMERA"));
        return arrayList;
    }

    @Override // com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo
    public List<IntentFilter> getAppFilter() {
        ArrayList arrayList = new ArrayList();
        IntentFilter intentFilter = new IntentFilter("android.media.action.IMAGE_CAPTURE");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        arrayList.add(intentFilter);
        IntentFilter intentFilter2 = new IntentFilter("android.media.action.VIDEO_CAPTURE");
        intentFilter2.addCategory("android.intent.category.DEFAULT");
        arrayList.add(intentFilter2);
        IntentFilter intentFilter3 = new IntentFilter("android.media.action.VIDEO_CAMERA");
        intentFilter3.addCategory("android.intent.category.DEFAULT");
        arrayList.add(intentFilter3);
        intentFilter3.addAction("com.oppo.action.CAMERA");
        intentFilter3.addCategory("android.intent.category.DEFAULT");
        arrayList.add(intentFilter3);
        return arrayList;
    }

    @Override // com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo
    public List<Integer> getAppMatchParam() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(1048576);
        arrayList.add(1048576);
        arrayList.add(1048576);
        arrayList.add(1048576);
        return arrayList;
    }
}
