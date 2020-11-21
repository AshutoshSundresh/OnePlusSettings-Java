package com.oneplus.settings.defaultapp.apptype;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

public class DefaultAppTypeMusic extends DefaultAppTypeInfo {
    @Override // com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo
    public List<Intent> getAppIntent() {
        ArrayList arrayList = new ArrayList();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.parse("content://media"), "audio/mpeg");
        arrayList.add(intent);
        Intent intent2 = new Intent("android.intent.action.VIEW");
        intent2.addCategory("android.intent.category.DEFAULT");
        intent2.setDataAndType(Uri.parse("content://media"), "audio/aac");
        arrayList.add(intent2);
        Intent intent3 = new Intent("android.intent.action.VIEW");
        intent3.addCategory("android.intent.category.DEFAULT");
        intent3.setDataAndType(Uri.parse("content://media"), "audio/amr");
        arrayList.add(intent3);
        Intent intent4 = new Intent("android.intent.action.VIEW");
        intent4.addCategory("android.intent.category.DEFAULT");
        intent4.setDataAndType(Uri.parse("content://media"), "audio/x-wav");
        arrayList.add(intent4);
        Intent intent5 = new Intent("android.intent.action.VIEW");
        intent5.addCategory("android.intent.category.DEFAULT");
        intent5.setDataAndType(Uri.parse("content://media"), "audio/x-ms-wma");
        arrayList.add(intent5);
        Intent intent6 = new Intent("android.intent.action.VIEW");
        intent6.addCategory("android.intent.category.DEFAULT");
        intent6.setDataAndType(Uri.parse("content://media"), "audio/ogg");
        arrayList.add(intent6);
        Intent intent7 = new Intent("android.intent.action.VIEW");
        intent7.addCategory("android.intent.category.DEFAULT");
        intent7.setDataAndType(Uri.parse("content://"), "audio/mpeg");
        arrayList.add(intent7);
        Intent intent8 = new Intent("android.intent.action.VIEW");
        intent8.addCategory("android.intent.category.DEFAULT");
        intent8.setDataAndType(Uri.parse("file://"), "audio/mpeg");
        arrayList.add(intent8);
        Intent intent9 = new Intent("android.intent.action.VIEW");
        intent9.addCategory("android.intent.category.DEFAULT");
        intent9.setDataAndType(Uri.parse("content://"), "audio/aac");
        arrayList.add(intent9);
        Intent intent10 = new Intent("android.intent.action.VIEW");
        intent10.addCategory("android.intent.category.DEFAULT");
        intent10.setDataAndType(Uri.parse("file://"), "audio/aac");
        arrayList.add(intent10);
        Intent intent11 = new Intent("android.intent.action.VIEW");
        intent11.addCategory("android.intent.category.DEFAULT");
        intent11.setDataAndType(Uri.parse("content://"), "audio/amr");
        arrayList.add(intent11);
        Intent intent12 = new Intent("android.intent.action.VIEW");
        intent12.addCategory("android.intent.category.DEFAULT");
        intent12.setDataAndType(Uri.parse("file://"), "audio/amr");
        arrayList.add(intent12);
        Intent intent13 = new Intent("android.intent.action.VIEW");
        intent13.addCategory("android.intent.category.DEFAULT");
        intent13.setDataAndType(Uri.parse("content://"), "audio/x-wav");
        arrayList.add(intent13);
        Intent intent14 = new Intent("android.intent.action.VIEW");
        intent14.addCategory("android.intent.category.DEFAULT");
        intent14.setDataAndType(Uri.parse("file://"), "audio/x-wav");
        arrayList.add(intent14);
        Intent intent15 = new Intent("android.intent.action.VIEW");
        intent15.addCategory("android.intent.category.DEFAULT");
        intent15.setDataAndType(Uri.parse("content://"), "audio/x-ms-wma");
        arrayList.add(intent15);
        Intent intent16 = new Intent("android.intent.action.VIEW");
        intent16.addCategory("android.intent.category.DEFAULT");
        intent16.setDataAndType(Uri.parse("file://"), "audio/x-ms-wma");
        arrayList.add(intent16);
        Intent intent17 = new Intent("android.intent.action.VIEW");
        intent17.addCategory("android.intent.category.DEFAULT");
        intent17.setDataAndType(Uri.parse("content://"), "audio/ogg");
        arrayList.add(intent17);
        Intent intent18 = new Intent("android.intent.action.VIEW");
        intent18.addCategory("android.intent.category.DEFAULT");
        intent18.setDataAndType(Uri.parse("file://"), "audio/ogg");
        arrayList.add(intent18);
        return arrayList;
    }

    @Override // com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo
    public List<IntentFilter> getAppFilter() {
        ArrayList arrayList = new ArrayList();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.VIEW");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        intentFilter.addDataScheme("content");
        intentFilter.addDataAuthority("media", null);
        try {
            intentFilter.addDataType("audio/mpeg");
        } catch (Exception e) {
            e.printStackTrace();
        }
        arrayList.add(intentFilter);
        IntentFilter intentFilter2 = new IntentFilter("android.intent.action.VIEW");
        intentFilter2.addCategory("android.intent.category.DEFAULT");
        intentFilter2.addDataScheme("content");
        intentFilter2.addDataAuthority("media", null);
        try {
            intentFilter2.addDataType("audio/aac");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        arrayList.add(intentFilter2);
        IntentFilter intentFilter3 = new IntentFilter("android.intent.action.VIEW");
        intentFilter3.addCategory("android.intent.category.DEFAULT");
        intentFilter3.addDataScheme("content");
        intentFilter3.addDataAuthority("media", null);
        try {
            intentFilter3.addDataType("audio/amr");
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        arrayList.add(intentFilter3);
        IntentFilter intentFilter4 = new IntentFilter("android.intent.action.VIEW");
        intentFilter4.addCategory("android.intent.category.DEFAULT");
        intentFilter4.addDataScheme("content");
        intentFilter4.addDataAuthority("media", null);
        try {
            intentFilter4.addDataType("audio/x-wav");
        } catch (Exception e4) {
            e4.printStackTrace();
        }
        arrayList.add(intentFilter4);
        IntentFilter intentFilter5 = new IntentFilter("android.intent.action.VIEW");
        intentFilter5.addCategory("android.intent.category.DEFAULT");
        intentFilter5.addDataScheme("content");
        intentFilter5.addDataAuthority("media", null);
        try {
            intentFilter5.addDataType("audio/x-ms-wma");
        } catch (Exception e5) {
            e5.printStackTrace();
        }
        arrayList.add(intentFilter5);
        IntentFilter intentFilter6 = new IntentFilter("android.intent.action.VIEW");
        intentFilter6.addCategory("android.intent.category.DEFAULT");
        intentFilter6.addDataScheme("content");
        intentFilter6.addDataAuthority("media", null);
        try {
            intentFilter6.addDataType("audio/ogg");
        } catch (Exception e6) {
            e6.printStackTrace();
        }
        arrayList.add(intentFilter6);
        IntentFilter intentFilter7 = new IntentFilter("android.intent.action.VIEW");
        intentFilter7.addCategory("android.intent.category.DEFAULT");
        intentFilter7.addDataScheme("content");
        try {
            intentFilter7.addDataType("audio/mpeg");
        } catch (Exception e7) {
            e7.printStackTrace();
        }
        arrayList.add(intentFilter7);
        IntentFilter intentFilter8 = new IntentFilter("android.intent.action.VIEW");
        intentFilter8.addCategory("android.intent.category.DEFAULT");
        intentFilter8.addDataScheme("file");
        try {
            intentFilter8.addDataType("audio/mpeg");
        } catch (Exception e8) {
            e8.printStackTrace();
        }
        arrayList.add(intentFilter8);
        IntentFilter intentFilter9 = new IntentFilter("android.intent.action.VIEW");
        intentFilter9.addCategory("android.intent.category.DEFAULT");
        intentFilter9.addDataScheme("content");
        try {
            intentFilter9.addDataType("audio/aac");
        } catch (Exception e9) {
            e9.printStackTrace();
        }
        arrayList.add(intentFilter9);
        IntentFilter intentFilter10 = new IntentFilter("android.intent.action.VIEW");
        intentFilter10.addCategory("android.intent.category.DEFAULT");
        intentFilter10.addDataScheme("file");
        try {
            intentFilter10.addDataType("audio/aac");
        } catch (Exception e10) {
            e10.printStackTrace();
        }
        arrayList.add(intentFilter10);
        IntentFilter intentFilter11 = new IntentFilter("android.intent.action.VIEW");
        intentFilter11.addCategory("android.intent.category.DEFAULT");
        intentFilter11.addDataScheme("content");
        try {
            intentFilter11.addDataType("audio/amr");
        } catch (Exception e11) {
            e11.printStackTrace();
        }
        arrayList.add(intentFilter11);
        IntentFilter intentFilter12 = new IntentFilter("android.intent.action.VIEW");
        intentFilter12.addCategory("android.intent.category.DEFAULT");
        intentFilter12.addDataScheme("file");
        try {
            intentFilter12.addDataType("audio/amr");
        } catch (Exception e12) {
            e12.printStackTrace();
        }
        arrayList.add(intentFilter12);
        IntentFilter intentFilter13 = new IntentFilter("android.intent.action.VIEW");
        intentFilter13.addCategory("android.intent.category.DEFAULT");
        intentFilter13.addDataScheme("content");
        try {
            intentFilter13.addDataType("audio/x-wav");
        } catch (Exception e13) {
            e13.printStackTrace();
        }
        arrayList.add(intentFilter13);
        IntentFilter intentFilter14 = new IntentFilter("android.intent.action.VIEW");
        intentFilter14.addCategory("android.intent.category.DEFAULT");
        intentFilter14.addDataScheme("file");
        try {
            intentFilter14.addDataType("audio/x-wav");
        } catch (Exception e14) {
            e14.printStackTrace();
        }
        arrayList.add(intentFilter14);
        IntentFilter intentFilter15 = new IntentFilter("android.intent.action.VIEW");
        intentFilter15.addCategory("android.intent.category.DEFAULT");
        intentFilter15.addDataScheme("file");
        try {
            intentFilter15.addDataType("audio/x-ms-wma");
        } catch (Exception e15) {
            e15.printStackTrace();
        }
        arrayList.add(intentFilter15);
        IntentFilter intentFilter16 = new IntentFilter("android.intent.action.VIEW");
        intentFilter16.addCategory("android.intent.category.DEFAULT");
        intentFilter16.addDataScheme("content");
        try {
            intentFilter16.addDataType("audio/x-ms-wma");
        } catch (Exception e16) {
            e16.printStackTrace();
        }
        arrayList.add(intentFilter16);
        IntentFilter intentFilter17 = new IntentFilter("android.intent.action.VIEW");
        intentFilter17.addCategory("android.intent.category.DEFAULT");
        intentFilter17.addDataScheme("file");
        try {
            intentFilter17.addDataType("audio/ogg");
        } catch (Exception e17) {
            e17.printStackTrace();
        }
        arrayList.add(intentFilter17);
        IntentFilter intentFilter18 = new IntentFilter("android.intent.action.VIEW");
        intentFilter18.addCategory("android.intent.category.DEFAULT");
        intentFilter18.addDataScheme("content");
        try {
            intentFilter18.addDataType("audio/ogg");
        } catch (Exception e18) {
            e18.printStackTrace();
        }
        arrayList.add(intentFilter18);
        return arrayList;
    }

    @Override // com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo
    public List<Integer> getAppMatchParam() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        arrayList.add(6291456);
        return arrayList;
    }
}
