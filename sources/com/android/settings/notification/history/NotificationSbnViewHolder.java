package com.android.settings.notification.history;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Slog;
import android.view.View;
import android.widget.DateTimeView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.logging.InstanceId;
import com.android.internal.logging.UiEventLogger;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.notification.history.NotificationHistoryActivity;

public class NotificationSbnViewHolder extends RecyclerView.ViewHolder {
    private final View mDivider;
    private final ImageView mIcon;
    private final TextView mPkgName;
    private final ImageView mProfileBadge;
    private final TextView mSummary;
    private final DateTimeView mTime;
    private final TextView mTitle;

    NotificationSbnViewHolder(View view) {
        super(view);
        this.mPkgName = (TextView) view.findViewById(C0010R$id.pkgname);
        this.mIcon = (ImageView) view.findViewById(C0010R$id.icon);
        this.mTime = view.findViewById(C0010R$id.timestamp);
        this.mTitle = (TextView) view.findViewById(C0010R$id.title);
        this.mSummary = (TextView) view.findViewById(C0010R$id.text);
        this.mProfileBadge = (ImageView) view.findViewById(C0010R$id.profile_badge);
        this.mDivider = view.findViewById(C0010R$id.divider);
    }

    /* access modifiers changed from: package-private */
    public void setSummary(CharSequence charSequence) {
        this.mSummary.setVisibility(TextUtils.isEmpty(charSequence) ? 8 : 0);
        this.mSummary.setText(charSequence);
    }

    /* access modifiers changed from: package-private */
    public void setTitle(CharSequence charSequence) {
        if (charSequence != null) {
            this.mTitle.setText(charSequence);
        }
    }

    /* access modifiers changed from: package-private */
    public void setIcon(Drawable drawable) {
        this.mIcon.setImageDrawable(drawable);
    }

    /* access modifiers changed from: package-private */
    public void setPackageLabel(String str) {
        this.mPkgName.setText(str);
    }

    /* access modifiers changed from: package-private */
    public void setPostedTime(long j) {
        this.mTime.setTime(j);
    }

    /* access modifiers changed from: package-private */
    public void setProfileBadge(Drawable drawable) {
        this.mProfileBadge.setImageDrawable(drawable);
        this.mProfileBadge.setVisibility(drawable != null ? 0 : 8);
    }

    /* access modifiers changed from: package-private */
    public void setDividerVisible(boolean z) {
        this.mDivider.setVisibility(z ? 0 : 8);
    }

    /* access modifiers changed from: package-private */
    public void addOnClick(int i, String str, int i2, int i3, PendingIntent pendingIntent, InstanceId instanceId, boolean z, UiEventLogger uiEventLogger) {
        Intent launchIntentForPackage = this.itemView.getContext().getPackageManager().getLaunchIntentForPackage(str);
        boolean z2 = false;
        if (!(pendingIntent == null || PendingIntent.getActivity(this.itemView.getContext(), 0, pendingIntent.getIntent(), 536870912) == null)) {
            z2 = true;
        }
        if (z2 || launchIntentForPackage != null) {
            this.itemView.setOnClickListener(new View.OnClickListener(uiEventLogger, z, i2, str, instanceId, i, pendingIntent, launchIntentForPackage, i3) {
                /* class com.android.settings.notification.history.$$Lambda$NotificationSbnViewHolder$0Yckye1GJOLj3tsMhI01g9RBaM */
                public final /* synthetic */ UiEventLogger f$1;
                public final /* synthetic */ boolean f$2;
                public final /* synthetic */ int f$3;
                public final /* synthetic */ String f$4;
                public final /* synthetic */ InstanceId f$5;
                public final /* synthetic */ int f$6;
                public final /* synthetic */ PendingIntent f$7;
                public final /* synthetic */ Intent f$8;
                public final /* synthetic */ int f$9;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                    this.f$8 = r9;
                    this.f$9 = r10;
                }

                public final void onClick(View view) {
                    NotificationSbnViewHolder.this.lambda$addOnClick$0$NotificationSbnViewHolder(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, view);
                }
            });
            ViewCompat.setAccessibilityDelegate(this.itemView, new AccessibilityDelegateCompat(this) {
                /* class com.android.settings.notification.history.NotificationSbnViewHolder.AnonymousClass1 */

                @Override // androidx.core.view.AccessibilityDelegateCompat
                public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                    accessibilityNodeInfoCompat.addAction(new AccessibilityNodeInfoCompat.AccessibilityActionCompat(16, view.getResources().getText(C0017R$string.notification_history_open_notification)));
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$addOnClick$0 */
    public /* synthetic */ void lambda$addOnClick$0$NotificationSbnViewHolder(UiEventLogger uiEventLogger, boolean z, int i, String str, InstanceId instanceId, int i2, PendingIntent pendingIntent, Intent intent, int i3, View view) {
        NotificationHistoryActivity.NotificationHistoryEvent notificationHistoryEvent;
        if (z) {
            notificationHistoryEvent = NotificationHistoryActivity.NotificationHistoryEvent.NOTIFICATION_HISTORY_SNOOZED_ITEM_CLICK;
        } else {
            notificationHistoryEvent = NotificationHistoryActivity.NotificationHistoryEvent.NOTIFICATION_HISTORY_RECENT_ITEM_CLICK;
        }
        uiEventLogger.logWithInstanceIdAndPosition(notificationHistoryEvent, i, str, instanceId, i2);
        if (pendingIntent != null) {
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                Slog.e("SbnViewHolder", "Could not launch", e);
            }
        } else if (intent != null) {
            intent.addFlags(268435456);
            try {
                this.itemView.getContext().startActivityAsUser(intent, UserHandle.of(i3));
            } catch (ActivityNotFoundException e2) {
                Slog.e("SbnViewHolder", "no launch activity", e2);
            }
        }
    }
}
