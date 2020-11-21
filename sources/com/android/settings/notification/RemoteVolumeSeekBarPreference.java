package com.android.settings.notification;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class RemoteVolumeSeekBarPreference extends VolumeSeekBarPreference {
    @Override // com.android.settings.notification.VolumeSeekBarPreference
    public void setStream(int i) {
    }

    public RemoteVolumeSeekBarPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public RemoteVolumeSeekBarPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public RemoteVolumeSeekBarPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public RemoteVolumeSeekBarPreference(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.notification.VolumeSeekBarPreference
    public void init() {
        if (((VolumeSeekBarPreference) this).mSeekBar != null) {
            setContinuousUpdates(true);
            updateIconView();
            updateSuppressionText();
            notifyHierarchyChanged();
        }
    }

    @Override // com.android.settings.widget.SeekBarPreference
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        super.onProgressChanged(seekBar, i, z);
        if (z) {
            notifyChanged();
        }
    }
}
