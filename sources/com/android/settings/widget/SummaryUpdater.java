package com.android.settings.widget;

import android.content.Context;
import android.text.TextUtils;

public abstract class SummaryUpdater {
    protected final Context mContext;
    private final OnSummaryChangeListener mListener;
    private String mSummary;

    public interface OnSummaryChangeListener {
        void onSummaryChanged(String str);
    }

    /* access modifiers changed from: protected */
    public abstract String getSummary();

    public SummaryUpdater(Context context, OnSummaryChangeListener onSummaryChangeListener) {
        this.mContext = context;
        this.mListener = onSummaryChangeListener;
    }

    /* access modifiers changed from: protected */
    public void notifyChangeIfNeeded() {
        String summary = getSummary();
        if (!TextUtils.equals(this.mSummary, summary)) {
            this.mSummary = summary;
            OnSummaryChangeListener onSummaryChangeListener = this.mListener;
            if (onSummaryChangeListener != null) {
                onSummaryChangeListener.onSummaryChanged(summary);
            }
        }
    }
}
