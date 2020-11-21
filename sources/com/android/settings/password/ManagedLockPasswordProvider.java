package com.android.settings.password;

import android.content.Context;
import android.content.Intent;
import com.android.internal.widget.LockscreenCredential;

public class ManagedLockPasswordProvider {
    /* access modifiers changed from: package-private */
    public Intent createIntent(boolean z, LockscreenCredential lockscreenCredential) {
        return null;
    }

    /* access modifiers changed from: package-private */
    public CharSequence getPickerOptionTitle(boolean z) {
        return "";
    }

    /* access modifiers changed from: package-private */
    public boolean isManagedPasswordChoosable() {
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isSettingManagedPasswordSupported() {
        return false;
    }

    public static ManagedLockPasswordProvider get(Context context, int i) {
        return new ManagedLockPasswordProvider();
    }

    protected ManagedLockPasswordProvider() {
    }
}
