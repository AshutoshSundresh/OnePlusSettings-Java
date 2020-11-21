package com.android.settings.applications.specialaccess.vrlistener;

import android.content.ComponentName;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.utils.ManagedServiceSettings;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class VrListenerSettings extends ManagedServiceSettings {
    private static final ManagedServiceSettings.Config CONFIG;
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.vr_listeners_settings);
    private static final String TAG = VrListenerSettings.class.getSimpleName();

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 334;
    }

    static {
        ManagedServiceSettings.Config.Builder builder = new ManagedServiceSettings.Config.Builder();
        builder.setTag(TAG);
        builder.setSetting("enabled_vr_listeners");
        builder.setIntentAction("android.service.vr.VrListenerService");
        builder.setPermission("android.permission.BIND_VR_LISTENER_SERVICE");
        builder.setNoun("vr listener");
        builder.setWarningDialogTitle(C0017R$string.vr_listener_security_warning_title);
        builder.setWarningDialogSummary(C0017R$string.vr_listener_security_warning_summary);
        builder.setEmptyText(C0017R$string.no_vr_listeners);
        CONFIG = builder.build();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.utils.ManagedServiceSettings
    public ManagedServiceSettings.Config getConfig() {
        return CONFIG;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.utils.ManagedServiceSettings
    public boolean setEnabled(ComponentName componentName, String str, boolean z) {
        logSpecialPermissionChange(z, componentName.getPackageName());
        return super.setEnabled(componentName, str, z);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.vr_listeners_settings;
    }

    /* access modifiers changed from: package-private */
    public void logSpecialPermissionChange(boolean z, String str) {
        int i = z ? 772 : 773;
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(getActivity()), i, getMetricsCategory(), str, 0);
    }
}
