package com.google.zxing.common;

import java.util.List;

public final class DecoderResult {
    private final List<byte[]> byteSegments;
    private final String ecLevel;
    private Object other;
    private final byte[] rawBytes;
    private final String text;

    public void setErasures(Integer num) {
    }

    public void setErrorsCorrected(Integer num) {
    }

    public DecoderResult(byte[] bArr, String str, List<byte[]> list, String str2) {
        this.rawBytes = bArr;
        this.text = str;
        this.byteSegments = list;
        this.ecLevel = str2;
    }

    public byte[] getRawBytes() {
        return this.rawBytes;
    }

    public String getText() {
        return this.text;
    }

    public List<byte[]> getByteSegments() {
        return this.byteSegments;
    }

    public String getECLevel() {
        return this.ecLevel;
    }

    public Object getOther() {
        return this.other;
    }

    public void setOther(Object obj) {
        this.other = obj;
    }
}
