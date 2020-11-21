package com.android.settings.biometrics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.preference.Preference;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.OPMemberController;

public abstract class BiometricStatusPreferenceController extends BasePreferenceController {
    protected final LockPatternUtils mLockPatternUtils;
    protected final int mProfileChallengeUserId;
    protected final UserManager mUm;
    private final int mUserId = UserHandle.myUserId();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    /* access modifiers changed from: protected */
    public abstract String getEnrollClassName();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    /* access modifiers changed from: protected */
    public abstract String getSettingsClassName();

    /* access modifiers changed from: protected */
    public abstract String getSummaryTextEnrolled();

    /* access modifiers changed from: protected */
    public abstract String getSummaryTextNoneEnrolled();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    /* access modifiers changed from: protected */
    public abstract boolean hasEnrolledBiometrics();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    /* access modifiers changed from: protected */
    public abstract boolean isDeviceSupported();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    /* access modifiers changed from: protected */
    public boolean isUserSupported() {
        return true;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BiometricStatusPreferenceController(Context context, String str) {
        super(context, str);
        this.mUm = (UserManager) context.getSystemService("user");
        this.mLockPatternUtils = FeatureFactory.getFactory(context).getSecurityFeatureProvider().getLockPatternUtils(context);
        this.mProfileChallengeUserId = Utils.getManagedProfileId(this.mUm, this.mUserId);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!isDeviceSupported()) {
            return 3;
        }
        return isUserSupported() ? 0 : 4;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        String str;
        if (isAvailable()) {
            preference.setVisible(true);
            if (hasEnrolledBiometrics()) {
                str = getSummaryTextEnrolled();
            } else {
                str = getSummaryTextNoneEnrolled();
            }
            preference.setSummary(str);
        } else if (preference != null) {
            preference.setVisible(false);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        String str;
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return super.handlePreferenceTreeClick(preference);
        }
        Context context = preference.getContext();
        if (((Activity) this.mContext).isInMultiWindowMode()) {
            Toast.makeText(context, C0017R$string.feature_not_support_split_screen, 0).show();
            return false;
        }
        UserManager userManager = UserManager.get(context);
        int userId = getUserId();
        if (Utils.startQuietModeDialogIfNecessary(context, userManager, userId)) {
            return false;
        }
        Intent intent = new Intent();
        if (hasEnrolledBiometrics()) {
            str = getSettingsClassName();
        } else {
            str = getEnrollClassName();
        }
        intent.setClassName(OPMemberController.PACKAGE_NAME, str);
        intent.putExtra("android.intent.extra.USER_ID", userId);
        intent.putExtra("from_settings_summary", true);
        context.startActivity(intent);
        return true;
    }

    /* access modifiers changed from: protected */
    public int getUserId() {
        return this.mUserId;
    }
}
