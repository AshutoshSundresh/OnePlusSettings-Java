package com.android.settings.enterprise;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C0015R$plurals;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;

public class EnterpriseInstalledPackagesPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final boolean mAsync;
    private final ApplicationFeatureProvider mFeatureProvider;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "number_enterprise_installed_packages";
    }

    public EnterpriseInstalledPackagesPreferenceController(Context context, boolean z) {
        super(context);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getApplicationFeatureProvider(context);
        this.mAsync = z;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mFeatureProvider.calculateNumberOfPolicyInstalledApps(true, new ApplicationFeatureProvider.NumberOfAppsCallback(preference) {
            /* class com.android.settings.enterprise.$$Lambda$EnterpriseInstalledPackagesPreferenceController$Q9tBW_UqmtZkNfVwyJDUmlYiHc */
            public final /* synthetic */ Preference f$1;

            {
                this.f$1 = r2;
            }

            @Override // com.android.settings.applications.ApplicationFeatureProvider.NumberOfAppsCallback
            public final void onNumberOfAppsResult(int i) {
                EnterpriseInstalledPackagesPreferenceController.this.lambda$updateState$0$EnterpriseInstalledPackagesPreferenceController(this.f$1, i);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateState$0 */
    public /* synthetic */ void lambda$updateState$0$EnterpriseInstalledPackagesPreferenceController(Preference preference, int i) {
        boolean z = false;
        if (i != 0) {
            preference.setSummary(this.mContext.getResources().getQuantityString(C0015R$plurals.enterprise_privacy_number_packages_lower_bound, i, Integer.valueOf(i)));
            z = true;
        }
        preference.setVisible(z);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (this.mAsync) {
            return true;
        }
        Boolean[] boolArr = {null};
        this.mFeatureProvider.calculateNumberOfPolicyInstalledApps(false, new ApplicationFeatureProvider.NumberOfAppsCallback(boolArr) {
            /* class com.android.settings.enterprise.$$Lambda$EnterpriseInstalledPackagesPreferenceController$cz4TBR7YJ9IEY1tdj7V5o_Yuo */
            public final /* synthetic */ Boolean[] f$0;

            {
                this.f$0 = r1;
            }

            @Override // com.android.settings.applications.ApplicationFeatureProvider.NumberOfAppsCallback
            public final void onNumberOfAppsResult(int i) {
                EnterpriseInstalledPackagesPreferenceController.lambda$isAvailable$1(this.f$0, i);
            }
        });
        return boolArr[0].booleanValue();
    }

    static /* synthetic */ void lambda$isAvailable$1(Boolean[] boolArr, int i) {
        boolArr[0] = Boolean.valueOf(i > 0);
    }
}
