package com.oneplus.settings.ui;

import android.content.Context;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.service.vr.IVrManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.display.BrightnessUtils;

public class OPBrightnessSeekbarPreferenceCategory extends RestrictedPreference implements SeekBar.OnSeekBarChangeListener {
    private boolean isManuallyTouchingSeekbar;
    private int mBrightness;
    private OPCallbackBrightness mCallback;
    private Context mContext;
    private float mDefaultBacklight;
    private float mDefaultBacklightForVr;
    private float mMaximumBacklight;
    private float mMaximumBacklightForVr;
    private float mMinimumBacklight;
    private float mMinimumBacklightForVr;
    private SeekBar mSeekBar;
    private float max;
    private float min;

    public interface OPCallbackBrightness {
        void onOPBrightValueChanged(int i, int i2);

        void onOPBrightValueStartTrackingTouch(int i);

        void saveBrightnessDataBase(int i);
    }

    public OPBrightnessSeekbarPreferenceCategory(Context context) {
        super(context);
        initView(context);
    }

    public OPBrightnessSeekbarPreferenceCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    public OPBrightnessSeekbarPreferenceCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context);
    }

    /* access modifiers changed from: package-private */
    public IVrManager safeGetVrManager() {
        return IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
    }

    /* access modifiers changed from: package-private */
    public boolean isInVrMode() {
        IVrManager safeGetVrManager = safeGetVrManager();
        if (safeGetVrManager == null) {
            return false;
        }
        try {
            return safeGetVrManager.getVrModeState();
        } catch (RemoteException e) {
            Log.e("OPBrightnessSeekbarPreferenceCategory", "Failed to check vr mode!", e);
            return false;
        }
    }

    private void initView(Context context) {
        setLayoutResource(C0012R$layout.op_brightness_seekbar_preference);
        this.mContext = context;
        PowerManager powerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mMinimumBacklight = powerManager.getBrightnessConstraint(0);
        this.mMaximumBacklight = powerManager.getBrightnessConstraint(1);
        this.mDefaultBacklight = powerManager.getBrightnessConstraint(2);
        this.mMinimumBacklightForVr = powerManager.getBrightnessConstraint(5);
        this.mMaximumBacklightForVr = powerManager.getBrightnessConstraint(6);
        this.mDefaultBacklightForVr = powerManager.getBrightnessConstraint(7);
        boolean isInVrMode = isInVrMode();
        if (isInVrMode) {
            Settings.System.getFloatForUser(this.mContext.getContentResolver(), "screen_brightness_for_vr_float", this.mDefaultBacklightForVr, -2);
        } else {
            Settings.System.getFloatForUser(this.mContext.getContentResolver(), "screen_brightness_float", this.mDefaultBacklight, -2);
        }
        if (isInVrMode) {
            this.min = this.mMinimumBacklightForVr;
            this.max = this.mMaximumBacklightForVr;
        } else {
            this.min = this.mMinimumBacklight;
            this.max = this.mMaximumBacklight;
        }
        this.mBrightness = BrightnessUtils.convertLinearToGammaFloat(Settings.System.getFloat(this.mContext.getContentResolver(), "screen_brightness_float", this.min), this.min, this.max);
    }

    public void setCallback(OPCallbackBrightness oPCallbackBrightness) {
        this.mCallback = oPCallbackBrightness;
    }

    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        SeekBar seekBar = (SeekBar) preferenceViewHolder.findViewById(C0010R$id.opseekbar);
        this.mSeekBar = seekBar;
        seekBar.setMax(65535);
        this.mSeekBar.setProgress(this.mBrightness);
        this.mSeekBar.setOnSeekBarChangeListener(this);
        preferenceViewHolder.setDividerAllowedAbove(false);
    }

    public void setBrightness(int i) {
        try {
            this.mBrightness = i;
            Log.d("OPBrightnessSeekbarPreferenceCategory", "seekbar brightness after set : " + this.mBrightness);
            if (this.mSeekBar != null) {
                this.mSeekBar.post(new Runnable() {
                    /* class com.oneplus.settings.ui.$$Lambda$OPBrightnessSeekbarPreferenceCategory$TLKzjtXAQpNWkURZY9YtOXk */

                    public final void run() {
                        OPBrightnessSeekbarPreferenceCategory.this.notifyChanged();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getBrightness() {
        return this.mBrightness;
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        Log.d("OPBrightnessSeekbarPreferenceCategory", "seekbar progress arg1 : " + i + " mSeekBar.getProgress : " + this.mSeekBar.getProgress());
        if (this.isManuallyTouchingSeekbar) {
            this.mCallback.onOPBrightValueChanged(0, this.mSeekBar.getProgress());
            this.mBrightness = this.mSeekBar.getProgress();
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        SeekBar seekBar2;
        Log.d("OPBrightnessSeekbarPreferenceCategory", "start tracking seekbar");
        this.isManuallyTouchingSeekbar = true;
        OPCallbackBrightness oPCallbackBrightness = this.mCallback;
        if (oPCallbackBrightness != null && (seekBar2 = this.mSeekBar) != null) {
            oPCallbackBrightness.onOPBrightValueStartTrackingTouch(seekBar2.getProgress());
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        SeekBar seekBar2;
        Log.d("OPBrightnessSeekbarPreferenceCategory", "stop tracking seekbar " + this.mSeekBar.getProgress());
        this.isManuallyTouchingSeekbar = false;
        OPCallbackBrightness oPCallbackBrightness = this.mCallback;
        if (oPCallbackBrightness != null && (seekBar2 = this.mSeekBar) != null) {
            oPCallbackBrightness.saveBrightnessDataBase(seekBar2.getProgress());
        }
    }
}
