package com.android.settings.notification.history;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.util.ContrastColorUtil;
import com.android.settings.C0012R$layout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationSbnAdapter extends RecyclerView.Adapter<NotificationSbnViewHolder> {
    private int mBackgroundColor;
    private final Context mContext;
    private int mCurrentUser;
    private List<Integer> mEnabledProfiles = new ArrayList();
    private boolean mInNightMode;
    private boolean mIsSnoozed;
    private PackageManager mPm;
    private UiEventLogger mUiEventLogger;
    private Map<Integer, Drawable> mUserBadgeCache;
    private List<StatusBarNotification> mValues;

    public NotificationSbnAdapter(Context context, PackageManager packageManager, UserManager userManager, boolean z, UiEventLogger uiEventLogger) {
        this.mContext = context;
        this.mPm = packageManager;
        this.mUserBadgeCache = new HashMap();
        this.mValues = new ArrayList();
        this.mBackgroundColor = this.mContext.getColor(17170888);
        this.mInNightMode = (this.mContext.getResources().getConfiguration().uiMode & 48) == 32;
        int currentUser = ActivityManager.getCurrentUser();
        this.mCurrentUser = currentUser;
        int[] enabledProfileIds = userManager.getEnabledProfileIds(currentUser);
        for (int i : enabledProfileIds) {
            if (!userManager.isQuietModeEnabled(UserHandle.of(i))) {
                this.mEnabledProfiles.add(Integer.valueOf(i));
            }
        }
        setHasStableIds(true);
        this.mIsSnoozed = z;
        this.mUiEventLogger = uiEventLogger;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public NotificationSbnViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new NotificationSbnViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(C0012R$layout.notification_sbn_log_row, viewGroup, false));
    }

    public void onBindViewHolder(NotificationSbnViewHolder notificationSbnViewHolder, int i) {
        StatusBarNotification statusBarNotification = this.mValues.get(i);
        if (statusBarNotification != null) {
            notificationSbnViewHolder.setIcon(loadIcon(statusBarNotification));
            notificationSbnViewHolder.setPackageLabel(loadPackageLabel(statusBarNotification.getPackageName()).toString());
            notificationSbnViewHolder.setTitle(getTitleString(statusBarNotification.getNotification()));
            notificationSbnViewHolder.setSummary(getTextString(this.mContext, statusBarNotification.getNotification()));
            notificationSbnViewHolder.setPostedTime(statusBarNotification.getPostTime());
            boolean z = true;
            if (i >= this.mValues.size() - 1) {
                z = false;
            }
            notificationSbnViewHolder.setDividerVisible(z);
            int normalizeUserId = normalizeUserId(statusBarNotification);
            if (!this.mUserBadgeCache.containsKey(Integer.valueOf(normalizeUserId))) {
                this.mUserBadgeCache.put(Integer.valueOf(normalizeUserId), this.mContext.getPackageManager().getUserBadgeForDensity(UserHandle.of(normalizeUserId), -1));
            }
            notificationSbnViewHolder.setProfileBadge(this.mUserBadgeCache.get(Integer.valueOf(normalizeUserId)));
            notificationSbnViewHolder.addOnClick(i, statusBarNotification.getPackageName(), statusBarNotification.getUid(), statusBarNotification.getUserId(), statusBarNotification.getNotification().contentIntent, statusBarNotification.getInstanceId(), this.mIsSnoozed, this.mUiEventLogger);
            notificationSbnViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener(statusBarNotification, notificationSbnViewHolder, normalizeUserId) {
                /* class com.android.settings.notification.history.$$Lambda$NotificationSbnAdapter$yceDzCEKiG9xAIkldcKOrTG6LUw */
                public final /* synthetic */ StatusBarNotification f$0;
                public final /* synthetic */ NotificationSbnViewHolder f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final boolean onLongClick(View view) {
                    return NotificationSbnAdapter.lambda$onBindViewHolder$0(this.f$0, this.f$1, this.f$2, view);
                }
            });
            return;
        }
        Slog.w("SbnAdapter", "null entry in list at position " + i);
    }

    static /* synthetic */ boolean lambda$onBindViewHolder$0(StatusBarNotification statusBarNotification, NotificationSbnViewHolder notificationSbnViewHolder, int i, View view) {
        Intent putExtra = new Intent("android.settings.CHANNEL_NOTIFICATION_SETTINGS").putExtra("android.provider.extra.APP_PACKAGE", statusBarNotification.getPackageName()).putExtra("android.provider.extra.CHANNEL_ID", statusBarNotification.getNotification().getChannelId()).putExtra("android.provider.extra.CONVERSATION_ID", statusBarNotification.getNotification().getShortcutId());
        putExtra.addFlags(268435456);
        notificationSbnViewHolder.itemView.getContext().startActivityAsUser(putExtra, UserHandle.of(i));
        return true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mValues.size();
    }

    public void onRebuildComplete(List<StatusBarNotification> list) {
        for (int size = list.size() - 1; size >= 0; size--) {
            if (!shouldShowSbn(list.get(size))) {
                list.remove(size);
            }
        }
        this.mValues = list;
        notifyDataSetChanged();
    }

    public void addSbn(StatusBarNotification statusBarNotification) {
        if (shouldShowSbn(statusBarNotification)) {
            this.mValues.add(0, statusBarNotification);
            notifyDataSetChanged();
        }
    }

    private boolean shouldShowSbn(StatusBarNotification statusBarNotification) {
        if ((!statusBarNotification.isGroup() || !statusBarNotification.getNotification().isGroupSummary()) && this.mEnabledProfiles.contains(Integer.valueOf(normalizeUserId(statusBarNotification)))) {
            return true;
        }
        return false;
    }

    private CharSequence loadPackageLabel(String str) {
        try {
            ApplicationInfo applicationInfo = this.mPm.getApplicationInfo(str, 4194304);
            if (applicationInfo != null) {
                return this.mPm.getApplicationLabel(applicationInfo);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("SbnAdapter", "Cannot load package name", e);
        }
        return str;
    }

    private static String getTitleString(Notification notification) {
        Bundle bundle = notification.extras;
        CharSequence charSequence = bundle != null ? bundle.getCharSequence("android.title") : null;
        if (charSequence == null) {
            return null;
        }
        return String.valueOf(charSequence);
    }

    private static String getTextString(Context context, Notification notification) {
        CharSequence charSequence;
        List<Notification.MessagingStyle.Message> messages;
        Bundle bundle = notification.extras;
        if (bundle != null) {
            charSequence = bundle.getCharSequence("android.text");
            Notification.Builder recoverBuilder = Notification.Builder.recoverBuilder(context, notification);
            if (recoverBuilder.getStyle() instanceof Notification.BigTextStyle) {
                charSequence = ((Notification.BigTextStyle) recoverBuilder.getStyle()).getBigText();
            } else if ((recoverBuilder.getStyle() instanceof Notification.MessagingStyle) && (messages = ((Notification.MessagingStyle) recoverBuilder.getStyle()).getMessages()) != null && messages.size() > 0) {
                charSequence = messages.get(messages.size() - 1).getText();
            }
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = notification.extras.getCharSequence("android.text");
            }
        } else {
            charSequence = null;
        }
        if (charSequence == null) {
            return null;
        }
        return String.valueOf(charSequence);
    }

    private Drawable loadIcon(StatusBarNotification statusBarNotification) {
        Drawable loadDrawableAsUser = statusBarNotification.getNotification().getSmallIcon().loadDrawableAsUser(statusBarNotification.getPackageContext(this.mContext), normalizeUserId(statusBarNotification));
        if (loadDrawableAsUser == null) {
            return null;
        }
        loadDrawableAsUser.mutate();
        loadDrawableAsUser.setColorFilter(getContrastedColor(statusBarNotification.getNotification()), PorterDuff.Mode.SRC_ATOP);
        return loadDrawableAsUser;
    }

    private int normalizeUserId(StatusBarNotification statusBarNotification) {
        int userId = statusBarNotification.getUserId();
        return userId == -1 ? this.mCurrentUser : userId;
    }

    private int getContrastedColor(Notification notification) {
        int i = notification.color;
        if (i != 0) {
            i |= -16777216;
        }
        return ContrastColorUtil.resolveContrastColor(this.mContext, i, this.mBackgroundColor, this.mInNightMode);
    }
}
