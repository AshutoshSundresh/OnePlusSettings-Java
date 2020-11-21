package com.android.settings.password;

import android.app.admin.DevicePolicyManager;
import android.app.admin.PasswordMetrics;
import android.content.Context;
import android.os.UserHandle;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import java.util.ArrayList;
import java.util.List;

public class ChooseLockGenericController {
    private final Context mContext;
    private DevicePolicyManager mDpm;
    private final LockPatternUtils mLockPatternUtils;
    private ManagedLockPasswordProvider mManagedPasswordProvider;
    private final int mRequestedMinComplexity;
    private final int mUserId;

    public ChooseLockGenericController(Context context, int i) {
        this(context, i, 0, new LockPatternUtils(context));
    }

    public ChooseLockGenericController(Context context, int i, int i2, LockPatternUtils lockPatternUtils) {
        this(context, i, i2, (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class), ManagedLockPasswordProvider.get(context, i), lockPatternUtils);
    }

    ChooseLockGenericController(Context context, int i, int i2, DevicePolicyManager devicePolicyManager, ManagedLockPasswordProvider managedLockPasswordProvider, LockPatternUtils lockPatternUtils) {
        this.mContext = context;
        this.mUserId = i;
        this.mRequestedMinComplexity = i2;
        this.mManagedPasswordProvider = managedLockPasswordProvider;
        this.mDpm = devicePolicyManager;
        this.mLockPatternUtils = lockPatternUtils;
    }

    public int upgradeQuality(int i) {
        return Math.max(Math.max(i, this.mDpm.getPasswordQuality(null, this.mUserId)), PasswordMetrics.complexityLevelToMinQuality(this.mRequestedMinComplexity));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.android.settings.password.ChooseLockGenericController$1  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$password$ScreenLockType;

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|14) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.android.settings.password.ScreenLockType[] r0 = com.android.settings.password.ScreenLockType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                com.android.settings.password.ChooseLockGenericController.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType = r0
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.NONE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = com.android.settings.password.ChooseLockGenericController.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType     // Catch:{ NoSuchFieldError -> 0x001d }
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.SWIPE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = com.android.settings.password.ChooseLockGenericController.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.MANAGED     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = com.android.settings.password.ChooseLockGenericController.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.PIN     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = com.android.settings.password.ChooseLockGenericController.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType     // Catch:{ NoSuchFieldError -> 0x003e }
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.PATTERN     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = com.android.settings.password.ChooseLockGenericController.AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.android.settings.password.ScreenLockType r1 = com.android.settings.password.ScreenLockType.PASSWORD     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.password.ChooseLockGenericController.AnonymousClass1.<clinit>():void");
        }
    }

    public boolean isScreenLockVisible(ScreenLockType screenLockType) {
        boolean z = this.mUserId != UserHandle.myUserId();
        switch (AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType[screenLockType.ordinal()]) {
            case 1:
                return !this.mContext.getResources().getBoolean(C0005R$bool.config_hide_none_security_option) && !z;
            case 2:
                return !this.mContext.getResources().getBoolean(C0005R$bool.config_hide_swipe_security_option) && !z;
            case 3:
                return this.mManagedPasswordProvider.isManagedPasswordChoosable();
            case 4:
            case 5:
            case 6:
                return this.mLockPatternUtils.hasSecureLockScreen();
            default:
                return true;
        }
    }

    public boolean isScreenLockEnabled(ScreenLockType screenLockType, int i) {
        return screenLockType.maxQuality >= i;
    }

    public boolean isScreenLockDisabledByAdmin(ScreenLockType screenLockType, int i) {
        boolean z = true;
        boolean z2 = screenLockType.maxQuality < i;
        if (screenLockType != ScreenLockType.MANAGED) {
            return z2;
        }
        if (!z2 && this.mManagedPasswordProvider.isManagedPasswordChoosable()) {
            z = false;
        }
        return z;
    }

    public CharSequence getTitle(ScreenLockType screenLockType) {
        switch (AnonymousClass1.$SwitchMap$com$android$settings$password$ScreenLockType[screenLockType.ordinal()]) {
            case 1:
                return this.mContext.getText(C0017R$string.unlock_set_unlock_off_title);
            case 2:
                return this.mContext.getText(C0017R$string.unlock_set_unlock_none_title);
            case 3:
                return this.mManagedPasswordProvider.getPickerOptionTitle(false);
            case 4:
                return this.mContext.getText(C0017R$string.unlock_set_unlock_pin_title);
            case 5:
                return this.mContext.getText(C0017R$string.unlock_set_unlock_pattern_title);
            case 6:
                return this.mContext.getText(C0017R$string.unlock_set_unlock_password_title);
            default:
                return null;
        }
    }

    public List<ScreenLockType> getVisibleScreenLockTypes(int i, boolean z) {
        int upgradeQuality = upgradeQuality(i);
        ArrayList arrayList = new ArrayList();
        ScreenLockType[] values = ScreenLockType.values();
        for (ScreenLockType screenLockType : values) {
            if (isScreenLockVisible(screenLockType) && (z || isScreenLockEnabled(screenLockType, upgradeQuality))) {
                arrayList.add(screenLockType);
            }
        }
        return arrayList;
    }
}
