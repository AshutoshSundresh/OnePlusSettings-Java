package com.oneplus.settings.ui;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.widget.SeekBar;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.oneplus.settings.better.OPSuwScreenColorMode;

public class OPSuwSeekBarPreference extends Preference {
    private Context mContext;
    OPColorModeSeekBarChangeListener mOPColorModeSeekBarChangeListener;
    private SeekBar mSeekBar;

    public interface OPColorModeSeekBarChangeListener {
        void onProgressChanged(SeekBar seekBar, int i, boolean z);

        void onStartTrackingTouch(SeekBar seekBar);

        void onStopTrackingTouch(SeekBar seekBar);
    }

    public OPSuwSeekBarPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mContext = context;
        setLayoutResource(C0012R$layout.op_suw_seekpreference);
    }

    public OPSuwSeekBarPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public OPSuwSeekBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OPSuwSeekBarPreference(Context context) {
        this(context, null);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedBelow(false);
        preferenceViewHolder.setDividerAllowedAbove(false);
        initSeekBar(preferenceViewHolder);
    }

    private void initSeekBar(PreferenceViewHolder preferenceViewHolder) {
        SeekBar seekBar = (SeekBar) preferenceViewHolder.findViewById(C0010R$id.screen_color_mode_seekbar);
        this.mSeekBar = seekBar;
        seekBar.setMax(100);
        this.mSeekBar.setProgress(Settings.System.getInt(this.mContext.getContentResolver(), "oem_screen_better_value", OPSuwScreenColorMode.DEFAULT_COLOR_PROGRESS));
        this.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /* class com.oneplus.settings.ui.OPSuwSeekBarPreference.AnonymousClass1 */

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                OPColorModeSeekBarChangeListener oPColorModeSeekBarChangeListener = OPSuwSeekBarPreference.this.mOPColorModeSeekBarChangeListener;
                if (oPColorModeSeekBarChangeListener != null) {
                    oPColorModeSeekBarChangeListener.onProgressChanged(seekBar, i, z);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                OPColorModeSeekBarChangeListener oPColorModeSeekBarChangeListener = OPSuwSeekBarPreference.this.mOPColorModeSeekBarChangeListener;
                if (oPColorModeSeekBarChangeListener != null) {
                    oPColorModeSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                OPColorModeSeekBarChangeListener oPColorModeSeekBarChangeListener = OPSuwSeekBarPreference.this.mOPColorModeSeekBarChangeListener;
                if (oPColorModeSeekBarChangeListener != null) {
                    oPColorModeSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }
        });
    }

    public void setOPColorModeSeekBarChangeListener(OPColorModeSeekBarChangeListener oPColorModeSeekBarChangeListener) {
        this.mOPColorModeSeekBarChangeListener = oPColorModeSeekBarChangeListener;
    }
}
