package com.android.settings;

import android.content.Context;
import android.content.Intent;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.OPLegalSettingsControlPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class LegalSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.about_legal);

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "LegalSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 225;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.about_legal;
    }

    public static void startLegalActivity(Context context, int i) {
        Intent intent = new Intent("android.oem.intent.action.OP_LEGAL");
        intent.putExtra("op_legal_notices_type", i);
        intent.putExtra("key_from_settings", true);
        context.startActivity(intent);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        OPLegalSettingsControlPreferenceController oPLegalSettingsControlPreferenceController = new OPLegalSettingsControlPreferenceController(context, "op_user_agreements", 2);
        OPLegalSettingsControlPreferenceController oPLegalSettingsControlPreferenceController2 = new OPLegalSettingsControlPreferenceController(context, "op_privacy_policy", 3);
        OPLegalSettingsControlPreferenceController oPLegalSettingsControlPreferenceController3 = new OPLegalSettingsControlPreferenceController(context, "op_permission_agreement", 4);
        OPLegalSettingsControlPreferenceController oPLegalSettingsControlPreferenceController4 = new OPLegalSettingsControlPreferenceController(context, "health_safety_information", 12);
        arrayList.add(oPLegalSettingsControlPreferenceController);
        arrayList.add(oPLegalSettingsControlPreferenceController2);
        arrayList.add(oPLegalSettingsControlPreferenceController3);
        arrayList.add(oPLegalSettingsControlPreferenceController4);
        return arrayList;
    }
}
