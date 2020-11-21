package com.android.settings.notification.history;

import android.view.View;
import android.widget.DateTimeView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0010R$id;

public class NotificationHistoryViewHolder extends RecyclerView.ViewHolder {
    private final TextView mSummary;
    private final DateTimeView mTime;
    private final TextView mTitle;

    NotificationHistoryViewHolder(View view) {
        super(view);
        DateTimeView findViewById = view.findViewById(C0010R$id.timestamp);
        this.mTime = findViewById;
        findViewById.setShowRelativeTime(true);
        this.mTitle = (TextView) view.findViewById(C0010R$id.title);
        this.mSummary = (TextView) view.findViewById(C0010R$id.text);
    }

    /* access modifiers changed from: package-private */
    public void setSummary(CharSequence charSequence) {
        this.mSummary.setText(charSequence);
        this.mSummary.setVisibility(charSequence != null ? 0 : 8);
    }

    /* access modifiers changed from: package-private */
    public void setTitle(CharSequence charSequence) {
        this.mTitle.setText(charSequence);
        this.mTitle.setVisibility(charSequence != null ? 0 : 4);
    }

    /* access modifiers changed from: package-private */
    public void setPostedTime(long j) {
        this.mTime.setTime(j);
    }
}
