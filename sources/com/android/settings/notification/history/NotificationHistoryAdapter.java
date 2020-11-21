package com.android.settings.notification.history;

import android.app.INotificationManager;
import android.app.NotificationHistory;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.logging.UiEventLogger;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.notification.history.NotificationHistoryActivity;
import com.android.settings.notification.history.NotificationHistoryRecyclerView;
import java.util.ArrayList;
import java.util.List;

public class NotificationHistoryAdapter extends RecyclerView.Adapter<NotificationHistoryViewHolder> implements NotificationHistoryRecyclerView.OnItemSwipeDeleteListener {
    private static String TAG = "NotiHistoryAdapter";
    private OnItemDeletedListener mListener;
    private INotificationManager mNm;
    private UiEventLogger mUiEventLogger;
    private List<NotificationHistory.HistoricalNotification> mValues = new ArrayList();

    /* access modifiers changed from: package-private */
    public interface OnItemDeletedListener {
        void onItemDeleted(int i);
    }

    public NotificationHistoryAdapter(INotificationManager iNotificationManager, NotificationHistoryRecyclerView notificationHistoryRecyclerView, OnItemDeletedListener onItemDeletedListener, UiEventLogger uiEventLogger) {
        setHasStableIds(true);
        notificationHistoryRecyclerView.setOnItemSwipeDeleteListener(this);
        this.mNm = iNotificationManager;
        this.mListener = onItemDeletedListener;
        this.mUiEventLogger = uiEventLogger;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public NotificationHistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new NotificationHistoryViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(C0012R$layout.notification_history_log_row, viewGroup, false));
    }

    public void onBindViewHolder(NotificationHistoryViewHolder notificationHistoryViewHolder, int i) {
        final NotificationHistory.HistoricalNotification historicalNotification = this.mValues.get(i);
        notificationHistoryViewHolder.setTitle(historicalNotification.getTitle());
        notificationHistoryViewHolder.setSummary(historicalNotification.getText());
        notificationHistoryViewHolder.setPostedTime(historicalNotification.getPostedTimeMs());
        notificationHistoryViewHolder.itemView.setOnClickListener(new View.OnClickListener(historicalNotification, i, notificationHistoryViewHolder) {
            /* class com.android.settings.notification.history.$$Lambda$NotificationHistoryAdapter$IaG6kTT8Cqg_nryleB50gWW7c */
            public final /* synthetic */ NotificationHistory.HistoricalNotification f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ NotificationHistoryViewHolder f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onClick(View view) {
                NotificationHistoryAdapter.this.lambda$onBindViewHolder$0$NotificationHistoryAdapter(this.f$1, this.f$2, this.f$3, view);
            }
        });
        notificationHistoryViewHolder.itemView.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            /* class com.android.settings.notification.history.NotificationHistoryAdapter.AnonymousClass1 */

            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, view.getResources().getText(C0017R$string.notification_history_view_settings)));
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_DISMISS);
            }

            public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
                super.performAccessibilityAction(view, i, bundle);
                if (i != AccessibilityNodeInfo.AccessibilityAction.ACTION_DISMISS.getId()) {
                    return false;
                }
                NotificationHistoryAdapter.this.onItemSwipeDeleted(NotificationHistoryAdapter.this.mValues.indexOf(historicalNotification));
                return true;
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onBindViewHolder$0 */
    public /* synthetic */ void lambda$onBindViewHolder$0$NotificationHistoryAdapter(NotificationHistory.HistoricalNotification historicalNotification, int i, NotificationHistoryViewHolder notificationHistoryViewHolder, View view) {
        this.mUiEventLogger.logWithPosition(NotificationHistoryActivity.NotificationHistoryEvent.NOTIFICATION_HISTORY_OLDER_ITEM_CLICK, historicalNotification.getUid(), historicalNotification.getPackage(), i);
        Intent putExtra = new Intent("android.settings.CHANNEL_NOTIFICATION_SETTINGS").putExtra("android.provider.extra.APP_PACKAGE", historicalNotification.getPackage()).putExtra("android.provider.extra.CHANNEL_ID", historicalNotification.getChannelId()).putExtra("android.provider.extra.CONVERSATION_ID", historicalNotification.getConversationId());
        putExtra.addFlags(268435456);
        notificationHistoryViewHolder.itemView.getContext().startActivityAsUser(putExtra, UserHandle.of(historicalNotification.getUserId()));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mValues.size();
    }

    public void onRebuildComplete(List<NotificationHistory.HistoricalNotification> list) {
        this.mValues = list;
        list.sort($$Lambda$NotificationHistoryAdapter$hS13J5m3lQ9o8T4ity6Z5iDBQVE.INSTANCE);
        notifyDataSetChanged();
    }

    @Override // com.android.settings.notification.history.NotificationHistoryRecyclerView.OnItemSwipeDeleteListener
    public void onItemSwipeDeleted(int i) {
        if (i > this.mValues.size() - 1) {
            String str = TAG;
            Slog.d(str, "Tried to swipe element out of list: position: " + i + " size? " + this.mValues.size());
            return;
        }
        NotificationHistory.HistoricalNotification remove = this.mValues.remove(i);
        if (remove != null) {
            try {
                this.mNm.deleteNotificationHistoryItem(remove.getPackage(), remove.getUid(), remove.getPostedTimeMs());
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to delete item", e);
            }
            this.mUiEventLogger.logWithPosition(NotificationHistoryActivity.NotificationHistoryEvent.NOTIFICATION_HISTORY_OLDER_ITEM_DELETE, remove.getUid(), remove.getPackage(), i);
        }
        this.mListener.onItemDeleted(this.mValues.size());
        notifyItemRemoved(i);
    }
}
