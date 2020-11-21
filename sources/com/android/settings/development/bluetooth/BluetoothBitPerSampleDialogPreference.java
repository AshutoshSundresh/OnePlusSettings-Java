package com.android.settings.development.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;
import com.android.settings.C0003R$array;
import com.android.settings.C0010R$id;

public class BluetoothBitPerSampleDialogPreference extends BaseBluetoothDialogPreference implements RadioGroup.OnCheckedChangeListener {
    public BluetoothBitPerSampleDialogPreference(Context context) {
        super(context);
        initialize(context);
    }

    public BluetoothBitPerSampleDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
    }

    public BluetoothBitPerSampleDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context);
    }

    public BluetoothBitPerSampleDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initialize(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference
    public int getRadioButtonGroupId() {
        return C0010R$id.bluetooth_audio_bit_per_sample_radio_group;
    }

    private void initialize(Context context) {
        String[] stringArray;
        String[] stringArray2;
        this.mRadioButtonIds.add(Integer.valueOf(C0010R$id.bluetooth_audio_bit_per_sample_default));
        this.mRadioButtonIds.add(Integer.valueOf(C0010R$id.bluetooth_audio_bit_per_sample_16));
        this.mRadioButtonIds.add(Integer.valueOf(C0010R$id.bluetooth_audio_bit_per_sample_24));
        this.mRadioButtonIds.add(Integer.valueOf(C0010R$id.bluetooth_audio_bit_per_sample_32));
        for (String str : context.getResources().getStringArray(C0003R$array.bluetooth_a2dp_codec_bits_per_sample_titles)) {
            this.mRadioButtonStrings.add(str);
        }
        for (String str2 : context.getResources().getStringArray(C0003R$array.bluetooth_a2dp_codec_bits_per_sample_summaries)) {
            this.mSummaryStrings.add(str2);
        }
    }
}
