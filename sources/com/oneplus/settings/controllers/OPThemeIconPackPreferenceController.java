package com.oneplus.settings.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.utils.OPDeviceHelper;
import java.util.ArrayList;
import java.util.HashMap;

public class OPThemeIconPackPreferenceController extends BasePreferenceController {
    private static final String ICONPACK_METADATA_KEY_EMPTY = "empty";
    private static final String ICONPACK_METADATA_KEY_OS = "os";
    private static final String IGNORE_ICONPACK_OS = "h2";
    private static final String SETTING_PROVIDER_ICONPACK_KEY = "launcher_iconpack";
    private static CharSequence[][] mIconPackList;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OPThemeIconPackPreferenceController(Context context, String str) {
        super(context, str);
        mIconPackList = getIconPackList(this.mContext);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        String string = Settings.System.getString(this.mContext.getContentResolver(), SETTING_PROVIDER_ICONPACK_KEY);
        int i = 0;
        while (true) {
            CharSequence[][] charSequenceArr = mIconPackList;
            if (i >= charSequenceArr[1].length) {
                return charSequenceArr[0][0];
            }
            if (TextUtils.equals(string, charSequenceArr[1][i])) {
                return mIconPackList[0][i];
            }
            i++;
        }
    }

    private CharSequence[][] getIconPackList(Context context) {
        CharSequence[] charSequenceArr;
        boolean z;
        ArrayList<ResolveInfo> arrayList = new ArrayList();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent();
        IntentCategory[] intentCategoryArr = {new IntentCategory(C0003R$array.oneplus_theme_icon_packs_action1, "android.intent.category.DEFAULT"), new IntentCategory(C0003R$array.oneplus_theme_icon_packs_action2, "com.anddoes.launcher.THEME"), new IntentCategory(C0003R$array.oneplus_theme_icon_packs_action2, "com.teslacoilsw.launcher.THEME")};
        for (int i = 0; i < 3; i++) {
            IntentCategory intentCategory = intentCategoryArr[i];
            intent.addCategory(intentCategory.categoryString);
            for (String str : context.getResources().getStringArray(intentCategory.actionId)) {
                intent.setAction(str);
                arrayList.addAll(packageManager.queryIntentActivities(intent, 192));
            }
        }
        HashMap hashMap = new HashMap();
        for (ResolveInfo resolveInfo : arrayList) {
            Bundle bundle = resolveInfo.activityInfo.metaData;
            if (bundle != null) {
                z = IGNORE_ICONPACK_OS.equals(bundle.get(ICONPACK_METADATA_KEY_OS));
                if (resolveInfo.activityInfo.metaData.get(ICONPACK_METADATA_KEY_EMPTY) != null) {
                    z = true;
                }
            } else {
                z = false;
            }
            if (!z) {
                hashMap.put(resolveInfo.activityInfo.packageName, resolveInfo.loadLabel(packageManager));
            }
        }
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        if (OPDeviceHelper.isAtLeastOP8DeviceVersion()) {
            charSequenceArr = OPDeviceHelper.DEFAULT_AT_LEAST_OP8_ICON_PACK_CANDIDATE_LIST;
        } else {
            charSequenceArr = OPDeviceHelper.DEFAULT_ICON_PACK_CANDIDATE_LIST;
        }
        for (CharSequence charSequence : charSequenceArr) {
            if (hashMap.containsKey(charSequence)) {
                arrayList3.add(charSequence);
                arrayList2.add((CharSequence) hashMap.get(charSequence));
                hashMap.remove(charSequence);
            }
        }
        if (arrayList3.size() == 0) {
            arrayList2.add(context.getResources().getString(C0017R$string.oneplus_system_default));
            arrayList3.add("none");
        }
        CharSequence[] charSequenceArr2 = OPDeviceHelper.NOT_DEFAULT_ICON_PACK_ORDER_LIST;
        for (CharSequence charSequence2 : charSequenceArr2) {
            if (hashMap.containsKey(charSequence2)) {
                arrayList3.add(charSequence2);
                arrayList2.add((CharSequence) hashMap.get(charSequence2));
                hashMap.remove(charSequence2);
            }
        }
        for (CharSequence charSequence3 : hashMap.keySet()) {
            arrayList3.add(charSequence3);
            arrayList2.add((CharSequence) hashMap.get(charSequence3));
        }
        return new CharSequence[][]{(CharSequence[]) arrayList2.toArray(new CharSequence[arrayList2.size()]), (CharSequence[]) arrayList3.toArray(new CharSequence[arrayList3.size()])};
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return super.handlePreferenceTreeClick(preference);
        }
        Intent intent = new Intent();
        intent.setClassName("net.oneplus.launcher", "net.oneplus.launcher.IconPackSelectorActivity");
        this.mContext.startActivity(intent);
        return true;
    }

    /* access modifiers changed from: private */
    public static class IntentCategory {
        int actionId;
        String categoryString;

        public IntentCategory(int i, String str) {
            this.actionId = i;
            this.categoryString = str;
        }
    }
}
