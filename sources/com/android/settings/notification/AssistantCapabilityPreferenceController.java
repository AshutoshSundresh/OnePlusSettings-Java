package com.android.settings.notification;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.List;

public class AssistantCapabilityPreferenceController extends TogglePreferenceController {
    static final String PRIORITIZER_KEY = "asst_capability_prioritizer";
    static final String RANKING_KEY = "asst_capability_ranking";
    static final String SMART_KEY = "asst_capabilities_actions_replies";
    private NotificationBackend mBackend = new NotificationBackend();

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AssistantCapabilityPreferenceController(Context context, String str) {
        super(context, str);
    }

    /* access modifiers changed from: package-private */
    public void setBackend(NotificationBackend notificationBackend) {
        this.mBackend = notificationBackend;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        List<String> assistantAdjustments = this.mBackend.getAssistantAdjustments(this.mContext.getPackageName());
        if (PRIORITIZER_KEY.equals(getPreferenceKey())) {
            return assistantAdjustments.contains("key_importance");
        }
        if (RANKING_KEY.equals(getPreferenceKey())) {
            return assistantAdjustments.contains("key_ranking_score");
        }
        if (!SMART_KEY.equals(getPreferenceKey()) || !assistantAdjustments.contains("key_contextual_actions") || !assistantAdjustments.contains("key_text_replies")) {
            return false;
        }
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (PRIORITIZER_KEY.equals(getPreferenceKey())) {
            this.mBackend.allowAssistantAdjustment("key_importance", z);
            return true;
        } else if (RANKING_KEY.equals(getPreferenceKey())) {
            this.mBackend.allowAssistantAdjustment("key_ranking_score", z);
            return true;
        } else if (!SMART_KEY.equals(getPreferenceKey())) {
            return true;
        } else {
            this.mBackend.allowAssistantAdjustment("key_contextual_actions", z);
            this.mBackend.allowAssistantAdjustment("key_text_replies", z);
            return true;
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mBackend.getAllowedNotificationAssistant() != null ? 0 : 5;
    }
}
