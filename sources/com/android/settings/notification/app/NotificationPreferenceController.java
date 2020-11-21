package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.os.UserManager;
import android.util.Log;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Comparator;
import java.util.Objects;

public abstract class NotificationPreferenceController extends AbstractPreferenceController {
    public static final Comparator<NotificationChannel> CHANNEL_COMPARATOR = $$Lambda$NotificationPreferenceController$6voFphxabHhSQuewcIliIYLDKQs.INSTANCE;
    public static final Comparator<NotificationChannelGroup> CHANNEL_GROUP_COMPARATOR = new Comparator<NotificationChannelGroup>() {
        /* class com.android.settings.notification.app.NotificationPreferenceController.AnonymousClass1 */

        public int compare(NotificationChannelGroup notificationChannelGroup, NotificationChannelGroup notificationChannelGroup2) {
            if (notificationChannelGroup.getId() == null && notificationChannelGroup2.getId() != null) {
                return 1;
            }
            if (notificationChannelGroup2.getId() != null || notificationChannelGroup.getId() == null) {
                return notificationChannelGroup.getId().compareTo(notificationChannelGroup2.getId());
            }
            return -1;
        }
    };
    protected RestrictedLockUtils.EnforcedAdmin mAdmin;
    protected NotificationBackend.AppRow mAppRow;
    protected final NotificationBackend mBackend;
    protected NotificationChannel mChannel;
    protected NotificationChannelGroup mChannelGroup;
    protected final Context mContext;
    protected Drawable mConversationDrawable;
    protected ShortcutInfo mConversationInfo;
    protected final UserManager mUm = ((UserManager) this.mContext.getSystemService("user"));

    public NotificationPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context);
        this.mContext = context;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        this.mBackend = notificationBackend;
        this.mContext.getPackageManager();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow == null || appRow.banned) {
            return false;
        }
        NotificationChannelGroup notificationChannelGroup = this.mChannelGroup;
        if (notificationChannelGroup != null && notificationChannelGroup.isBlocked()) {
            return false;
        }
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel == null) {
            return true;
        }
        if (notificationChannel.getImportance() != 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onResume(NotificationBackend.AppRow appRow, NotificationChannel notificationChannel, NotificationChannelGroup notificationChannelGroup, Drawable drawable, ShortcutInfo shortcutInfo, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mAppRow = appRow;
        this.mChannel = notificationChannel;
        this.mChannelGroup = notificationChannelGroup;
        this.mAdmin = enforcedAdmin;
        this.mConversationDrawable = drawable;
        this.mConversationInfo = shortcutInfo;
    }

    /* access modifiers changed from: protected */
    public void onResume(NotificationBackend.AppRow appRow, NotificationChannel notificationChannel, NotificationChannelGroup notificationChannelGroup, Drawable drawable, ShortcutInfo shortcutInfo, RestrictedLockUtils.EnforcedAdmin enforcedAdmin, String str) {
        this.mAppRow = appRow;
        this.mChannel = notificationChannel;
        this.mChannelGroup = notificationChannelGroup;
        this.mAdmin = enforcedAdmin;
        this.mConversationDrawable = drawable;
        this.mConversationInfo = shortcutInfo;
        NotificationBackend notificationBackend = this.mBackend;
        if (notificationBackend != null) {
            notificationBackend.mInstantAppPKG = str;
        }
    }

    /* access modifiers changed from: protected */
    public boolean checkCanBeVisible(int i) {
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel == null) {
            Log.w("ChannelPrefContr", "No channel");
            return false;
        }
        int importance = notificationChannel.getImportance();
        if (importance == -1000) {
            return true;
        }
        if (importance >= i) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void saveChannel() {
        NotificationBackend.AppRow appRow;
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel != null && (appRow = this.mAppRow) != null) {
            this.mBackend.updateChannel(appRow.pkg, appRow.uid, notificationChannel);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isChannelBlockable() {
        return isChannelBlockable(this.mChannel);
    }

    /* access modifiers changed from: protected */
    public boolean isChannelBlockable(NotificationChannel notificationChannel) {
        if (notificationChannel == null || this.mAppRow == null) {
            return false;
        }
        if (notificationChannel.isImportanceLockedByCriticalDeviceFunction() || notificationChannel.isImportanceLockedByOEM()) {
            if (notificationChannel.getImportance() == 0) {
                return true;
            }
            return false;
        } else if (notificationChannel.isBlockable() || !this.mAppRow.systemApp || notificationChannel.getImportance() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public boolean isChannelConfigurable(NotificationChannel notificationChannel) {
        if (notificationChannel == null || this.mAppRow == null) {
            return false;
        }
        return !notificationChannel.isImportanceLockedByOEM();
    }

    /* access modifiers changed from: protected */
    public boolean isChannelGroupBlockable() {
        return isChannelGroupBlockable(this.mChannelGroup);
    }

    /* access modifiers changed from: protected */
    public boolean isChannelGroupBlockable(NotificationChannelGroup notificationChannelGroup) {
        NotificationBackend.AppRow appRow;
        if (notificationChannelGroup == null || (appRow = this.mAppRow) == null) {
            return false;
        }
        if (!appRow.systemApp) {
            return true;
        }
        return notificationChannelGroup.isBlocked();
    }

    /* access modifiers changed from: protected */
    public boolean hasValidGroup() {
        return this.mChannelGroup != null;
    }

    /* access modifiers changed from: protected */
    public final boolean isDefaultChannel() {
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel == null) {
            return false;
        }
        return Objects.equals("miscellaneous", notificationChannel.getId());
    }

    static /* synthetic */ int lambda$static$0(NotificationChannel notificationChannel, NotificationChannel notificationChannel2) {
        if (notificationChannel.isDeleted() != notificationChannel2.isDeleted()) {
            return Boolean.compare(notificationChannel.isDeleted(), notificationChannel2.isDeleted());
        }
        if (notificationChannel.getId().equals("miscellaneous")) {
            return 1;
        }
        if (notificationChannel2.getId().equals("miscellaneous")) {
            return -1;
        }
        return notificationChannel.getId().compareTo(notificationChannel2.getId());
    }
}
