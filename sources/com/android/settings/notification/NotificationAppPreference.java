package com.android.settings.notification;

import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settingslib.RestrictedLockUtils;
import com.oneplus.common.VibratorSceneUtils;
import com.oneplus.settings.utils.OPUtils;

public class NotificationAppPreference extends MasterSwitchPreference {
    private boolean mChecked;
    private boolean mEnableSwitch = true;
    private SwitchCompat mSwitch;
    protected long[] mVibratePattern;
    protected Vibrator mVibrator;

    public NotificationAppPreference(Context context) {
        super(context);
        if (OPUtils.isSupportXVibrate()) {
            this.mVibrator = (Vibrator) context.getSystemService("vibrator");
        }
    }

    public NotificationAppPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (OPUtils.isSupportXVibrate()) {
            this.mVibrator = (Vibrator) context.getSystemService("vibrator");
        }
    }

    public NotificationAppPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        if (OPUtils.isSupportXVibrate()) {
            this.mVibrator = (Vibrator) context.getSystemService("vibrator");
        }
    }

    public NotificationAppPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        if (OPUtils.isSupportXVibrate()) {
            this.mVibrator = (Vibrator) context.getSystemService("vibrator");
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference, com.android.settings.widget.MasterSwitchPreference
    public int getSecondTargetResId() {
        return C0012R$layout.preference_widget_master_switch;
    }

    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference, androidx.preference.Preference, com.android.settings.widget.MasterSwitchPreference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(C0010R$id.switchWidget);
        if (findViewById != null) {
            findViewById.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.notification.NotificationAppPreference.AnonymousClass1 */

                public void onClick(View view) {
                    if (NotificationAppPreference.this.mSwitch == null || NotificationAppPreference.this.mSwitch.isEnabled()) {
                        if (VibratorSceneUtils.systemVibrateEnabled(NotificationAppPreference.this.getContext())) {
                            NotificationAppPreference notificationAppPreference = NotificationAppPreference.this;
                            notificationAppPreference.mVibratePattern = VibratorSceneUtils.getVibratorScenePattern(notificationAppPreference.getContext(), NotificationAppPreference.this.mVibrator, 1003);
                            NotificationAppPreference notificationAppPreference2 = NotificationAppPreference.this;
                            VibratorSceneUtils.vibrateIfNeeded(notificationAppPreference2.mVibratePattern, notificationAppPreference2.mVibrator);
                        }
                        NotificationAppPreference notificationAppPreference3 = NotificationAppPreference.this;
                        notificationAppPreference3.setChecked(!notificationAppPreference3.mChecked);
                        NotificationAppPreference notificationAppPreference4 = NotificationAppPreference.this;
                        if (!notificationAppPreference4.callChangeListener(Boolean.valueOf(notificationAppPreference4.mChecked))) {
                            NotificationAppPreference notificationAppPreference5 = NotificationAppPreference.this;
                            notificationAppPreference5.setChecked(!notificationAppPreference5.mChecked);
                            return;
                        }
                        NotificationAppPreference notificationAppPreference6 = NotificationAppPreference.this;
                        notificationAppPreference6.persistBoolean(notificationAppPreference6.mChecked);
                    }
                }
            });
        }
        SwitchCompat switchCompat = (SwitchCompat) preferenceViewHolder.findViewById(C0010R$id.switchWidget);
        this.mSwitch = switchCompat;
        if (switchCompat != null) {
            switchCompat.setContentDescription(getTitle());
            this.mSwitch.setChecked(this.mChecked);
            this.mSwitch.setEnabled(this.mEnableSwitch);
        }
    }

    @Override // com.android.settings.widget.MasterSwitchPreference
    public boolean isChecked() {
        return this.mSwitch != null && this.mChecked;
    }

    @Override // com.android.settings.widget.MasterSwitchPreference
    public void setChecked(boolean z) {
        this.mChecked = z;
        SwitchCompat switchCompat = this.mSwitch;
        if (switchCompat != null) {
            switchCompat.setChecked(z);
        }
    }

    @Override // com.android.settings.widget.MasterSwitchPreference
    public void setSwitchEnabled(boolean z) {
        this.mEnableSwitch = z;
        SwitchCompat switchCompat = this.mSwitch;
        if (switchCompat != null) {
            switchCompat.setEnabled(z);
        }
    }

    @Override // com.android.settingslib.RestrictedPreference, com.android.settings.widget.MasterSwitchPreference
    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        setSwitchEnabled(enforcedAdmin == null);
    }
}
