package com.android.settings.notification;

import android.content.Context;
import android.text.TextUtils;
import com.android.settings.C0005R$bool;
import com.android.settings.C0008R$drawable;
import com.android.settings.Utils;
import com.android.settings.slices.SliceBackgroundWorker;

public class NotificationVolumePreferenceController extends RingVolumePreferenceController {
    private static final String KEY_NOTIFICATION_VOLUME = "notification_volume";

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.notification.RingVolumePreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.notification.RingVolumePreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController
    public int getAudioStream() {
        return 5;
    }

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.notification.RingVolumePreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController, com.android.settings.notification.RingVolumePreferenceController
    public String getPreferenceKey() {
        return KEY_NOTIFICATION_VOLUME;
    }

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.notification.RingVolumePreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.notification.RingVolumePreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.notification.RingVolumePreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    public NotificationVolumePreferenceController(Context context) {
        super(context, KEY_NOTIFICATION_VOLUME);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settings.notification.RingVolumePreferenceController
    public int getAvailabilityStatus() {
        return (!this.mContext.getResources().getBoolean(C0005R$bool.config_show_notification_volume) || Utils.isVoiceCapable(this.mContext) || ((VolumeSeekBarPreferenceController) this).mHelper.isSingleVolume()) ? 3 : 0;
    }

    @Override // com.android.settings.notification.AdjustVolumeRestrictedPreferenceController, com.android.settings.notification.RingVolumePreferenceController, com.android.settings.core.SliderPreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), KEY_NOTIFICATION_VOLUME);
    }

    @Override // com.android.settings.notification.RingVolumePreferenceController, com.android.settings.notification.VolumeSeekBarPreferenceController
    public int getMuteIcon() {
        return C0008R$drawable.ic_notifications_off_24dp;
    }
}
