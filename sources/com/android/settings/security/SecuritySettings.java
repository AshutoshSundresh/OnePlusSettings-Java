package com.android.settings.security;

import android.content.Context;
import android.content.Intent;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.biometrics.face.FaceProfileStatusPreferenceController;
import com.android.settings.biometrics.face.FaceStatusPreferenceController;
import com.android.settings.biometrics.fingerprint.FingerprintProfileStatusPreferenceController;
import com.android.settings.biometrics.fingerprint.FingerprintStatusPreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.enterprise.EnterprisePrivacyPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.security.trustagent.ManageTrustAgentsPreferenceController;
import com.android.settings.security.trustagent.TrustAgentListPreferenceController;
import com.android.settings.system.OPCollectDiagnosticsPreferenceController;
import com.android.settings.widget.PreferenceCategoryController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.oneplus.settings.OPSecurityDetectionSwitchPreferenceController;
import com.oneplus.settings.controllers.OPDashboardTilePlaceholderPreferenceCategoryController;
import com.oneplus.settings.controllers.OPFaceUnlockPreferenceController;
import com.oneplus.settings.others.OPEmergencyRescueSettingsPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class SecuritySettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.security_dashboard_settings) {
        /* class com.android.settings.security.SecuritySettings.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return SecuritySettings.buildPreferenceControllers(context, null, null, SecuritySettings.class.getName());
        }
    };

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SecuritySettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 87;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.security_dashboard_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_security;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this, SecuritySettings.class.getName());
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (!((TrustAgentListPreferenceController) use(TrustAgentListPreferenceController.class)).handleActivityResult(i, i2) && !((LockUnificationPreferenceController) use(LockUnificationPreferenceController.class)).handleActivityResult(i, i2, intent)) {
            super.onActivityResult(i, i2, intent);
        }
    }

    /* access modifiers changed from: package-private */
    public void startUnification() {
        ((LockUnificationPreferenceController) use(LockUnificationPreferenceController.class)).startUnification();
    }

    /* access modifiers changed from: package-private */
    public void updateUnificationPreference() {
        ((LockUnificationPreferenceController) use(LockUnificationPreferenceController.class)).updateState(null);
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle, SecuritySettings securitySettings, String str) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new OPDashboardTilePlaceholderPreferenceCategoryController(context, "security_status", str));
        arrayList.add(new EnterprisePrivacyPreferenceController(context));
        arrayList.add(new ManageTrustAgentsPreferenceController(context));
        arrayList.add(new ScreenPinningPreferenceController(context));
        arrayList.add(new OPCollectDiagnosticsPreferenceController(context));
        arrayList.add(new OPEmergencyRescueSettingsPreferenceController(context));
        arrayList.add(new OPSecurityDetectionSwitchPreferenceController(context, lifecycle));
        arrayList.add(new SimLockPreferenceController(context));
        arrayList.add(new EncryptionStatusPreferenceController(context, "encryption_and_credential"));
        arrayList.add(new TrustAgentListPreferenceController(context, securitySettings, lifecycle));
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(new FaceStatusPreferenceController(context));
        arrayList2.add(new FingerprintStatusPreferenceController(context));
        arrayList2.add(new OPFaceUnlockPreferenceController(context));
        arrayList2.add(new ChangeScreenLockPreferenceController(context, securitySettings));
        arrayList.add(new PreferenceCategoryController(context, "security_category").setChildren(arrayList2));
        arrayList.addAll(arrayList2);
        ArrayList arrayList3 = new ArrayList();
        arrayList3.add(new ChangeProfileScreenLockPreferenceController(context, securitySettings));
        arrayList3.add(new LockUnificationPreferenceController(context, securitySettings));
        arrayList3.add(new VisiblePatternProfilePreferenceController(context, lifecycle));
        arrayList3.add(new FaceProfileStatusPreferenceController(context));
        arrayList3.add(new FingerprintProfileStatusPreferenceController(context));
        arrayList.add(new PreferenceCategoryController(context, "security_category_profile").setChildren(arrayList3));
        arrayList.addAll(arrayList3);
        return arrayList;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener, com.android.settings.dashboard.DashboardFragment
    public boolean onPreferenceTreeClick(Preference preference) {
        if (!"reset_collect_diagnostics".equals(preference.getKey())) {
            return super.onPreferenceTreeClick(preference);
        }
        try {
            Intent intent = new Intent("com.metrics.tmobile.SUMMARY");
            intent.setClassName("com.tmobile.pr.mytmobile", "com.tmobile.pr.mytmobile.iqtoggle.ui.OptInSummary");
            getActivity().startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}
