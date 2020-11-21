package com.android.settings.notification;

import android.content.Context;
import android.media.RingtoneManager;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.ThreadUtils;

public abstract class RingtonePreferenceControllerBase extends AbstractPreferenceController implements PreferenceControllerMixin {
    public abstract int getRingtoneType();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public RingtonePreferenceControllerBase(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ThreadUtils.postOnBackgroundThread(new Runnable(preference) {
            /* class com.android.settings.notification.$$Lambda$RingtonePreferenceControllerBase$Y95tp89vC8fk0DlynP4A12MKPAU */
            public final /* synthetic */ Preference f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RingtonePreferenceControllerBase.this.lambda$updateState$0$RingtonePreferenceControllerBase(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: updateSummary */
    public void lambda$updateState$0(Preference preference) {
        try {
            String locatRingtoneTitle = getLocatRingtoneTitle(this.mContext, RingtoneManager.getActualDefaultRingtoneUri(this.mContext, getRingtoneType()), getRingtoneType(), 0);
            if (locatRingtoneTitle != null) {
                ThreadUtils.postOnMainThread(new Runnable(locatRingtoneTitle) {
                    /* class com.android.settings.notification.$$Lambda$RingtonePreferenceControllerBase$DGmnmcDCcHzdJN6wPwVZkSTCfk0 */
                    public final /* synthetic */ CharSequence f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        Preference.this.setSummary((Preference) this.f$1);
                    }
                });
            } else {
                ThreadUtils.postOnMainThread(new Runnable() {
                    /* class com.android.settings.notification.$$Lambda$RingtonePreferenceControllerBase$gFsZQNi0jHi_HOuyL6S9nWKFtM */

                    public final void run() {
                        Preference.this.setSummary((Preference) 17041178);
                    }
                });
            }
        } catch (IllegalArgumentException e) {
            Log.w("PrefControllerMixin", "Error getting ringtone summary.", e);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0069, code lost:
        r12 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x006a, code lost:
        r1 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x006c, code lost:
        r12 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x006f, code lost:
        r12 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0080, code lost:
        if (r1 == null) goto L_0x0089;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0084, code lost:
        if (r1 == null) goto L_0x0089;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0086, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0089, code lost:
        r1 = r12;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0069 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:11:0x0043] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x007b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String getLocatRingtoneTitle(android.content.Context r12, android.net.Uri r13, int r14, int r15) {
        /*
        // Method dump skipped, instructions count: 144
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.notification.RingtonePreferenceControllerBase.getLocatRingtoneTitle(android.content.Context, android.net.Uri, int, int):java.lang.String");
    }
}
