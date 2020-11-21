package com.android.settings.security;

import android.content.Context;
import android.os.UserManager;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.PreferenceCategoryController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EncryptionAndCredential extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.encryption_and_credential) {
        /* class com.android.settings.security.EncryptionAndCredential.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return EncryptionAndCredential.buildPreferenceControllers(context, null);
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return ((UserManager) context.getSystemService("user")).isAdminUser();
        }
    };

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "EncryptionAndCredential";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 846;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.encryption_and_credential;
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        EncryptionStatusPreferenceController encryptionStatusPreferenceController = new EncryptionStatusPreferenceController(context, "encryption_and_credentials_encryption_status");
        arrayList.add(encryptionStatusPreferenceController);
        arrayList.add(new PreferenceCategoryController(context, "encryption_and_credentials_status_category").setChildren(Arrays.asList(encryptionStatusPreferenceController)));
        arrayList.add(new CredentialStoragePreferenceController(context));
        arrayList.add(new UserCredentialsPreferenceController(context));
        arrayList.add(new ResetCredentialsPreferenceController(context, lifecycle));
        arrayList.add(new InstallCertificatePreferenceController(context));
        return arrayList;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_encryption;
    }
}
