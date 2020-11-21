package com.android.settings.development.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioGroup;
import com.android.settings.C0003R$array;
import com.android.settings.C0010R$id;

public class BluetoothCodecDialogPreference extends BaseBluetoothDialogPreference implements RadioGroup.OnCheckedChangeListener {
    public BluetoothCodecDialogPreference(Context context) {
        super(context);
        initialize(context);
    }

    public BluetoothCodecDialogPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
    }

    public BluetoothCodecDialogPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context);
    }

    public BluetoothCodecDialogPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initialize(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.development.bluetooth.BaseBluetoothDialogPreference
    public int getRadioButtonGroupId() {
        return C0010R$id.bluetooth_audio_codec_radio_group;
    }

    private void initialize(Context context) {
        this.mRadioButtonIds.add(Integer.valueOf(C0010R$id.bluetooth_audio_codec_default));
        this.mRadioButtonIds.add(Integer.valueOf(C0010R$id.bluetooth_audio_codec_sbc));
        this.mRadioButtonIds.add(Integer.valueOf(C0010R$id.bluetooth_audio_codec_aac));
        this.mRadioButtonIds.add(Integer.valueOf(C0010R$id.bluetooth_audio_codec_aptx));
        this.mRadioButtonIds.add(Integer.valueOf(C0010R$id.bluetooth_audio_codec_aptx_hd));
        this.mRadioButtonIds.add(Integer.valueOf(C0010R$id.bluetooth_audio_codec_ldac));
        String[] stringArray = context.getResources().getStringArray(C0003R$array.bluetooth_a2dp_codec_titles);
        Log.e("BtCodecDlgPref", "a2dp_codec_titles array length: " + stringArray.length);
        for (int i = 0; i < stringArray.length; i++) {
            this.mRadioButtonStrings.add(stringArray[i]);
        }
        String[] stringArray2 = context.getResources().getStringArray(C0003R$array.bluetooth_a2dp_codec_summaries);
        Log.e("BtCodecDlgPref", "a2dp_codec_summaries array length: " + stringArray2.length);
        for (String str : stringArray2) {
            this.mSummaryStrings.add(str);
        }
    }
}
