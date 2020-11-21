package com.android.settings.enterprise;

import android.content.Context;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.PreferenceCategoryController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class EnterprisePrivacySettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.enterprise_privacy_settings) {
        /* class com.android.settings.enterprise.EnterprisePrivacySettings.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return EnterprisePrivacySettings.isPageEnabled(context);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return EnterprisePrivacySettings.buildPreferenceControllers(context, false);
        }
    };

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "EnterprisePrivacySettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 628;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.enterprise_privacy_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, true);
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, boolean z) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new NetworkLogsPreferenceController(context));
        arrayList.add(new BugReportsPreferenceController(context));
        arrayList.add(new SecurityLogsPreferenceController(context));
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(new EnterpriseInstalledPackagesPreferenceController(context, z));
        arrayList2.add(new AdminGrantedLocationPermissionsPreferenceController(context, z));
        arrayList2.add(new AdminGrantedMicrophonePermissionPreferenceController(context, z));
        arrayList2.add(new AdminGrantedCameraPermissionPreferenceController(context, z));
        arrayList2.add(new EnterpriseSetDefaultAppsPreferenceController(context));
        arrayList2.add(new AlwaysOnVpnCurrentUserPreferenceController(context));
        arrayList2.add(new AlwaysOnVpnManagedProfilePreferenceController(context));
        arrayList2.add(new ImePreferenceController(context));
        arrayList2.add(new GlobalHttpProxyPreferenceController(context));
        arrayList2.add(new CaCertsCurrentUserPreferenceController(context));
        arrayList2.add(new CaCertsManagedProfilePreferenceController(context));
        arrayList.addAll(arrayList2);
        arrayList.add(new PreferenceCategoryController(context, "exposure_changes_category").setChildren(arrayList2));
        arrayList.add(new FailedPasswordWipeCurrentUserPreferenceController(context));
        arrayList.add(new FailedPasswordWipeManagedProfilePreferenceController(context));
        return arrayList;
    }

    public static boolean isPageEnabled(Context context) {
        return FeatureFactory.getFactory(context).getEnterprisePrivacyFeatureProvider(context).hasDeviceOwner();
    }
}
