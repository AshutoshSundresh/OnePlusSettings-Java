package com.android.settings.display;

import android.content.Context;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.NightDisplayListener;
import android.util.AttributeSet;
import androidx.preference.SwitchPreference;
import java.time.LocalTime;

public class NightDisplayPreference extends SwitchPreference implements NightDisplayListener.Callback {
    private ColorDisplayManager mColorDisplayManager;
    private NightDisplayListener mNightDisplayListener;
    private NightDisplayTimeFormatter mTimeFormatter;

    public NightDisplayPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mColorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
        this.mNightDisplayListener = new NightDisplayListener(context);
        this.mTimeFormatter = new NightDisplayTimeFormatter(context);
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mNightDisplayListener.setCallback(this);
        updateSummary();
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        super.onDetached();
        this.mNightDisplayListener.setCallback((NightDisplayListener.Callback) null);
    }

    public void onActivated(boolean z) {
        updateSummary();
    }

    public void onAutoModeChanged(int i) {
        updateSummary();
    }

    public void onCustomStartTimeChanged(LocalTime localTime) {
        updateSummary();
    }

    public void onCustomEndTimeChanged(LocalTime localTime) {
        updateSummary();
    }

    private void updateSummary() {
        setSummary(this.mTimeFormatter.getAutoModeTimeSummary(getContext(), this.mColorDisplayManager));
    }
}
