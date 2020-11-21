package com.android.settings.development.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;
import com.android.settings.C0003R$array;
import com.android.settings.C0010R$id;

public class BluetoothSampleRateDialogPreference extends BaseBluetoothDialogPreference implements RadioGroup.OnCheckedChangeListener {
    public BluetoothSampleRateDialogPreference(Context context) {
        super(context);
        initialize(context);
    }

    public BluetoothSampleRateDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
    }

    public BluetoothSampleRateDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context);
    }

    public BluetoothSampleRateDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initialize(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference
    public int getRadioButtonGroupId() {
        return C0010R$id.bluetooth_audio_sample_rate_radio_group;
    }

    private void initialize(Context context) {
        String[] stringArray;
        String[] stringArray2;
        this.mRadioButtonIds.add(Integer.valueOf(C0010R$id.bluetooth_audio_sample_rate_default));
        this.mRadioButtonIds.add(Integer.valueOf(C0010R$id.bluetooth_audio_sample_rate_441));
        this.mRadioButtonIds.add(Integer.valueOf(C0010R$id.bluetooth_audio_sample_rate_480));
        this.mRadioButtonIds.add(Integer.valueOf(C0010R$id.bluetooth_audio_sample_rate_882));
        this.mRadioButtonIds.add(Integer.valueOf(C0010R$id.bluetooth_audio_sample_rate_960));
        for (String str : context.getResources().getStringArray(C0003R$array.bluetooth_a2dp_codec_sample_rate_titles)) {
            this.mRadioButtonStrings.add(str);
        }
        for (String str2 : context.getResources().getStringArray(C0003R$array.bluetooth_a2dp_codec_sample_rate_summaries)) {
            this.mSummaryStrings.add(str2);
        }
    }
}
