package com.android.settings.development;

import android.bluetooth.BluetoothCodecConfig;

public class BluetoothA2dpConfigStore {
    private int mBitsPerSample = 0;
    private int mChannelMode = 0;
    private int mCodecPriority = 0;
    private long mCodecSpecific1Value;
    private long mCodecSpecific2Value;
    private long mCodecSpecific3Value;
    private long mCodecSpecific4Value;
    private int mCodecType = 1000000;
    private int mSampleRate = 0;

    public void setCodecType(int i) {
        this.mCodecType = i;
    }

    public void setCodecPriority(int i) {
        this.mCodecPriority = i;
    }

    public void setSampleRate(int i) {
        this.mSampleRate = i;
    }

    public void setBitsPerSample(int i) {
        this.mBitsPerSample = i;
    }

    public void setChannelMode(int i) {
        this.mChannelMode = i;
    }

    public void setCodecSpecific1Value(long j) {
        this.mCodecSpecific1Value = j;
    }

    public BluetoothCodecConfig createCodecConfig() {
        return new BluetoothCodecConfig(this.mCodecType, this.mCodecPriority, this.mSampleRate, this.mBitsPerSample, this.mChannelMode, this.mCodecSpecific1Value, this.mCodecSpecific2Value, this.mCodecSpecific3Value, this.mCodecSpecific4Value);
    }
}
