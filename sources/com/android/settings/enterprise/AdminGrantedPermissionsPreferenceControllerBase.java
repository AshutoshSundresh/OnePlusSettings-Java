package com.android.settings.enterprise;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C0015R$plurals;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;

public abstract class AdminGrantedPermissionsPreferenceControllerBase extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final boolean mAsync;
    private final ApplicationFeatureProvider mFeatureProvider;
    private boolean mHasApps = false;
    private final String[] mPermissions;

    public AdminGrantedPermissionsPreferenceControllerBase(Context context, boolean z, String[] strArr) {
        super(context);
        this.mPermissions = strArr;
        this.mFeatureProvider = FeatureFactory.getFactory(context).getApplicationFeatureProvider(context);
        this.mAsync = z;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mFeatureProvider.calculateNumberOfAppsWithAdminGrantedPermissions(this.mPermissions, true, new ApplicationFeatureProvider.NumberOfAppsCallback(preference) {
            /* class com.android.settings.enterprise.$$Lambda$AdminGrantedPermissionsPreferenceControllerBase$HagCjfF7PxnpVV5KlxE2WbuQthA */
            public final /* synthetic */ Preference f$1;

            {
                this.f$1 = r2;
            }

            @Override // com.android.settings.applications.ApplicationFeatureProvider.NumberOfAppsCallback
            public final void onNumberOfAppsResult(int i) {
                AdminGrantedPermissionsPreferenceControllerBase.this.lambda$updateState$0$AdminGrantedPermissionsPreferenceControllerBase(this.f$1, i);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateState$0 */
    public /* synthetic */ void lambda$updateState$0$AdminGrantedPermissionsPreferenceControllerBase(Preference preference, int i) {
        if (i == 0) {
            this.mHasApps = false;
        } else {
            preference.setSummary(this.mContext.getResources().getQuantityString(C0015R$plurals.enterprise_privacy_number_packages_lower_bound, i, Integer.valueOf(i)));
            this.mHasApps = true;
        }
        preference.setVisible(this.mHasApps);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (this.mAsync) {
            return true;
        }
        Boolean[] boolArr = {null};
        this.mFeatureProvider.calculateNumberOfAppsWithAdminGrantedPermissions(this.mPermissions, false, new ApplicationFeatureProvider.NumberOfAppsCallback(boolArr) {
            /* class com.android.settings.enterprise.$$Lambda$AdminGrantedPermissionsPreferenceControllerBase$4ZAcP8cSJJvD_RXkeJP9Rdjuu0k */
            public final /* synthetic */ Boolean[] f$0;

            {
                this.f$0 = r1;
            }

            @Override // com.android.settings.applications.ApplicationFeatureProvider.NumberOfAppsCallback
            public final void onNumberOfAppsResult(int i) {
                AdminGrantedPermissionsPreferenceControllerBase.lambda$isAvailable$1(this.f$0, i);
            }
        });
        boolean booleanValue = boolArr[0].booleanValue();
        this.mHasApps = booleanValue;
        return booleanValue;
    }

    static /* synthetic */ void lambda$isAvailable$1(Boolean[] boolArr, int i) {
        boolArr[0] = Boolean.valueOf(i > 0);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (getPreferenceKey().equals(preference.getKey()) && this.mHasApps) {
            return super.handlePreferenceTreeClick(preference);
        }
        return false;
    }
}
