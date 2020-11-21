package com.android.settings.slices;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.provider.SearchIndexableResource;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import com.android.settings.C0003R$array;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.accessibility.AccessibilitySettings;
import com.android.settings.accessibility.AccessibilitySlicePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceData;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.android.settingslib.search.SearchIndexableData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

class SliceDataConverter {
    private Context mContext;
    private final MetricsFeatureProvider mMetricsFeatureProvider;

    public SliceDataConverter(Context context) {
        this.mContext = context;
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    public List<SliceData> getSliceData() {
        ArrayList arrayList = new ArrayList();
        for (SearchIndexableData searchIndexableData : FeatureFactory.getFactory(this.mContext).getSearchFeatureProvider().getSearchIndexableResources().getProviderValues()) {
            String name = searchIndexableData.getTargetClass().getName();
            Indexable$SearchIndexProvider searchIndexProvider = searchIndexableData.getSearchIndexProvider();
            if (searchIndexProvider == null) {
                Log.e("SliceDataConverter", name + " dose not implement Search Index Provider");
            } else {
                arrayList.addAll(getSliceDataFromProvider(searchIndexProvider, name));
            }
        }
        arrayList.addAll(getAccessibilitySliceData());
        return arrayList;
    }

    private List<SliceData> getSliceDataFromProvider(Indexable$SearchIndexProvider indexable$SearchIndexProvider, String str) {
        ArrayList arrayList = new ArrayList();
        List<SearchIndexableResource> xmlResourcesToIndex = indexable$SearchIndexProvider.getXmlResourcesToIndex(this.mContext, true);
        if (xmlResourcesToIndex == null) {
            return arrayList;
        }
        for (SearchIndexableResource searchIndexableResource : xmlResourcesToIndex) {
            int i = searchIndexableResource.xmlResId;
            if (i == 0) {
                Log.e("SliceDataConverter", str + " provides invalid XML (0) in search provider.");
            } else {
                arrayList.addAll(getSliceDataFromXML(i, str));
            }
        }
        return arrayList;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x011f, code lost:
        if (0 == 0) goto L_0x0163;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x015e, code lost:
        if (0 == 0) goto L_0x0163;
     */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0166  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.List<com.android.settings.slices.SliceData> getSliceDataFromXML(int r17, java.lang.String r18) {
        /*
        // Method dump skipped, instructions count: 362
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.slices.SliceDataConverter.getSliceDataFromXML(int, java.lang.String):java.util.List");
    }

    private List<SliceData> getAccessibilitySliceData() {
        ArrayList arrayList = new ArrayList();
        String name = AccessibilitySlicePreferenceController.class.getName();
        String name2 = AccessibilitySettings.class.getName();
        CharSequence text = this.mContext.getText(C0017R$string.accessibility_settings);
        SliceData.Builder builder = new SliceData.Builder();
        builder.setFragmentName(name2);
        builder.setScreenTitle(text);
        builder.setPreferenceControllerClassName(name);
        HashSet hashSet = new HashSet();
        Collections.addAll(hashSet, this.mContext.getResources().getStringArray(C0003R$array.config_settings_slices_accessibility_components));
        List<AccessibilityServiceInfo> accessibilityServiceInfoList = getAccessibilityServiceInfoList();
        PackageManager packageManager = this.mContext.getPackageManager();
        for (AccessibilityServiceInfo accessibilityServiceInfo : accessibilityServiceInfoList) {
            ResolveInfo resolveInfo = accessibilityServiceInfo.getResolveInfo();
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            String flattenToString = new ComponentName(serviceInfo.packageName, serviceInfo.name).flattenToString();
            if (hashSet.contains(flattenToString)) {
                String charSequence = resolveInfo.loadLabel(packageManager).toString();
                int iconResource = resolveInfo.getIconResource();
                if (iconResource == 0) {
                    iconResource = C0008R$drawable.ic_accessibility_generic;
                }
                builder.setKey(flattenToString);
                builder.setTitle(charSequence);
                builder.setUri(new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath(flattenToString).build());
                builder.setIcon(iconResource);
                builder.setSliceType(1);
                try {
                    arrayList.add(builder.build());
                } catch (SliceData.InvalidSliceDataException e) {
                    Log.w("SliceDataConverter", "Invalid data when building a11y SliceData for " + flattenToString, e);
                }
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public List<AccessibilityServiceInfo> getAccessibilityServiceInfoList() {
        return AccessibilityManager.getInstance(this.mContext).getInstalledAccessibilityServiceList();
    }
}
