package com.android.settings.notification;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.notification.NotificationAssistantPicker;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.widget.CandidateInfo;

public class NotificationAssistantPreferenceController extends BasePreferenceController {
    protected NotificationBackend mNotificationBackend = new NotificationBackend();
    private PackageManager mPackageManager = this.mContext.getPackageManager();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public NotificationAssistantPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        CandidateInfo candidateNone = new NotificationAssistantPicker.CandidateNone(this.mContext);
        ComponentName allowedNotificationAssistant = this.mNotificationBackend.getAllowedNotificationAssistant();
        if (allowedNotificationAssistant != null) {
            candidateNone = createCandidateInfo(allowedNotificationAssistant);
        }
        return candidateNone.loadLabel();
    }

    /* access modifiers changed from: protected */
    public CandidateInfo createCandidateInfo(ComponentName componentName) {
        return new DefaultAppInfo(this.mContext, this.mPackageManager, UserHandle.myUserId(), componentName);
    }
}
