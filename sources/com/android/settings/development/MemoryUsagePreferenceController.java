package com.android.settings.development;

import android.content.Context;
import android.text.format.Formatter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.applications.ProcStatsData;
import com.android.settings.applications.ProcessStatsBase;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.utils.ThreadUtils;

public class MemoryUsagePreferenceController extends DeveloperOptionsPreferenceController implements PreferenceControllerMixin {
    private ProcStatsData mProcStatsData;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "memory";
    }

    public MemoryUsagePreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mProcStatsData = getProcStatsData();
        setDuration();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ThreadUtils.postOnBackgroundThread(new Runnable() {
            /* class com.android.settings.development.$$Lambda$MemoryUsagePreferenceController$2UovDioLDVLRpJrL4IsFsRdoZts */

            public final void run() {
                MemoryUsagePreferenceController.this.lambda$updateState$1$MemoryUsagePreferenceController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateState$1 */
    public /* synthetic */ void lambda$updateState$1$MemoryUsagePreferenceController() {
        this.mProcStatsData.refreshStats(true);
        ProcStatsData.MemInfo memInfo = this.mProcStatsData.getMemInfo();
        ThreadUtils.postOnMainThread(new Runnable(Formatter.formatShortFileSize(this.mContext, (long) memInfo.realUsedRam), Formatter.formatShortFileSize(this.mContext, (long) memInfo.realTotalRam)) {
            /* class com.android.settings.development.$$Lambda$MemoryUsagePreferenceController$jVfwyLcntt7OQNk4ZzyeXShgglc */
            public final /* synthetic */ String f$1;
            public final /* synthetic */ String f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                MemoryUsagePreferenceController.this.lambda$updateState$0$MemoryUsagePreferenceController(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateState$0 */
    public /* synthetic */ void lambda$updateState$0$MemoryUsagePreferenceController(String str, String str2) {
        this.mPreference.setSummary(this.mContext.getString(C0017R$string.memory_summary, str, str2));
    }

    /* access modifiers changed from: package-private */
    public void setDuration() {
        this.mProcStatsData.setDuration(ProcessStatsBase.sDurations[0]);
    }

    /* access modifiers changed from: package-private */
    public ProcStatsData getProcStatsData() {
        return new ProcStatsData(this.mContext, false);
    }
}
