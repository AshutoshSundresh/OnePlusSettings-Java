package com.android.settings.notification;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0016R$raw;
import com.android.settings.widget.SeekBarPreference;
import com.oneplus.settings.notification.OPSeekBarVolumizer;
import java.util.Objects;

public class VolumeSeekBarPreference extends SeekBarPreference {
    AudioManager mAudioManager;
    private Callback mCallback;
    private int mIconResId;
    private ImageView mIconView;
    private int mMuteIconResId;
    private boolean mMuted;
    protected SeekBar mSeekBar;
    private boolean mStopped;
    private int mStream;
    private String mSuppressionText;
    private TextView mSuppressionTextView;
    private OPSeekBarVolumizer mVolumizer;
    private boolean mZenMuted;

    public interface Callback {
        void onSampleStarting(OPSeekBarVolumizer oPSeekBarVolumizer);

        void onStreamValueChanged(int i, int i2);
    }

    public VolumeSeekBarPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setLayoutResource(C0012R$layout.preference_volume_slider);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    public VolumeSeekBarPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutResource(C0012R$layout.preference_volume_slider);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    public VolumeSeekBarPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(C0012R$layout.preference_volume_slider);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    public VolumeSeekBarPreference(Context context) {
        super(context);
        setLayoutResource(C0012R$layout.preference_volume_slider);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
    }

    public void setStream(int i) {
        this.mStream = i;
        setMax(this.mAudioManager.getStreamMaxVolume(i));
        setMin(this.mAudioManager.getStreamMinVolumeInt(this.mStream));
        setProgress(this.mAudioManager.getStreamVolume(this.mStream));
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void onActivityResume() {
        if (this.mStopped) {
            this.mStopped = false;
            init();
        }
    }

    public void onActivityPause() {
        this.mStopped = true;
        OPSeekBarVolumizer oPSeekBarVolumizer = this.mVolumizer;
        if (oPSeekBarVolumizer != null) {
            oPSeekBarVolumizer.stop();
            this.mVolumizer = null;
        }
    }

    @Override // com.android.settingslib.TwoTargetPreference, com.android.settings.widget.SeekBarPreference, com.android.settingslib.RestrictedPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mSeekBar = (SeekBar) preferenceViewHolder.findViewById(16909402);
        this.mIconView = (ImageView) preferenceViewHolder.findViewById(16908294);
        this.mSuppressionTextView = (TextView) preferenceViewHolder.findViewById(C0010R$id.suppression_text);
        init();
    }

    /* access modifiers changed from: protected */
    public void init() {
        if (this.mSeekBar != null) {
            AnonymousClass1 r0 = new OPSeekBarVolumizer.Callback() {
                /* class com.android.settings.notification.VolumeSeekBarPreference.AnonymousClass1 */

                @Override // com.oneplus.settings.notification.OPSeekBarVolumizer.Callback
                public void onSampleStarting(OPSeekBarVolumizer oPSeekBarVolumizer) {
                    if (VolumeSeekBarPreference.this.mCallback != null) {
                        VolumeSeekBarPreference.this.mCallback.onSampleStarting(oPSeekBarVolumizer);
                    }
                }

                @Override // com.oneplus.settings.notification.OPSeekBarVolumizer.Callback
                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    if (VolumeSeekBarPreference.this.mCallback != null) {
                        VolumeSeekBarPreference.this.mCallback.onStreamValueChanged(VolumeSeekBarPreference.this.mStream, i);
                        VolumeSeekBarPreference.this.updateIconView();
                    }
                }

                @Override // com.oneplus.settings.notification.OPSeekBarVolumizer.Callback
                public void onMuted(boolean z, boolean z2) {
                    VolumeSeekBarPreference.this.mMuted = z;
                    VolumeSeekBarPreference.this.mZenMuted = z2;
                    VolumeSeekBarPreference.this.updateIconView();
                }
            };
            Uri mediaVolumeUri = this.mStream == 3 ? getMediaVolumeUri() : null;
            if (this.mVolumizer == null) {
                this.mVolumizer = new OPSeekBarVolumizer(getContext(), this.mStream, mediaVolumeUri, r0);
            }
            this.mVolumizer.start();
            this.mVolumizer.setSeekBar(this.mSeekBar);
            updateIconView();
            updateSuppressionText();
            if (!isEnabled()) {
                this.mSeekBar.setEnabled(false);
                this.mVolumizer.stop();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void updateIconView() {
        int i;
        ImageView imageView = this.mIconView;
        if (imageView != null && !this.mStopped) {
            int i2 = this.mIconResId;
            if (i2 != 0) {
                imageView.setImageResource(i2);
            } else if (this.mSeekBar.getProgress() != 0 || (i = this.mMuteIconResId) == 0) {
                this.mIconView.setImageDrawable(getIcon());
            } else {
                this.mIconView.setImageResource(i);
            }
        }
    }

    public void showIcon(int i) {
        if (this.mIconResId != i) {
            this.mIconResId = i;
            updateIconView();
        }
    }

    public void setMuteIcon(int i) {
        if (this.mMuteIconResId != i) {
            this.mMuteIconResId = i;
            updateIconView();
        }
    }

    private Uri getMediaVolumeUri() {
        return Uri.parse("android.resource://" + getContext().getPackageName() + "/" + C0016R$raw.media_volume);
    }

    public void setSuppressionText(String str) {
        if (!Objects.equals(str, this.mSuppressionText)) {
            this.mSuppressionText = str;
            updateSuppressionText();
        }
    }

    /* access modifiers changed from: protected */
    public void updateSuppressionText() {
        TextView textView = this.mSuppressionTextView;
        if (textView != null && this.mSeekBar != null) {
            textView.setText(this.mSuppressionText);
            this.mSuppressionTextView.setVisibility(TextUtils.isEmpty(this.mSuppressionText) ^ true ? 0 : 8);
        }
    }
}
