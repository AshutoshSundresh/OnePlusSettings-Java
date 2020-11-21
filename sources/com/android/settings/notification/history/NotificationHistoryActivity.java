package com.android.settings.notification.history;

import android.app.ActivityManager;
import android.app.INotificationManager;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.util.Slog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.history.HistoryLoader;
import com.android.settings.notification.history.NotificationHistoryAdapter;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.utils.ThreadUtils;
import com.oneplus.settings.BaseActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class NotificationHistoryActivity extends BaseActivity {
    private static String TAG = "NotifHistory";
    private Future mCountdownFuture;
    private CountDownLatch mCountdownLatch;
    private ViewGroup mDismissView;
    private ViewGroup mHistoryEmpty;
    private HistoryLoader mHistoryLoader;
    private ViewGroup mHistoryOff;
    private ViewGroup mHistoryOn;
    private final NotificationListenerService mListener = new NotificationListenerService() {
        /* class com.android.settings.notification.history.NotificationHistoryActivity.AnonymousClass2 */
        private RecyclerView mDismissedRv;
        private RecyclerView mSnoozedRv;

        public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        }

        public void onListenerConnected() {
            StatusBarNotification[] statusBarNotificationArr;
            StatusBarNotification[] statusBarNotificationArr2 = null;
            try {
                statusBarNotificationArr = getSnoozedNotifications();
                try {
                    statusBarNotificationArr2 = NotificationHistoryActivity.this.mNm.getHistoricalNotificationsWithAttribution(NotificationHistoryActivity.this.getPackageName(), NotificationHistoryActivity.this.getAttributionTag(), 6, false);
                } catch (RemoteException | SecurityException unused) {
                }
            } catch (RemoteException | SecurityException unused2) {
                statusBarNotificationArr = null;
                Log.d(NotificationHistoryActivity.TAG, "OnPaused called while trying to retrieve notifications");
                this.mSnoozedRv = (RecyclerView) NotificationHistoryActivity.this.mSnoozeView.findViewById(C0010R$id.notification_list);
                this.mSnoozedRv.setLayoutManager(new LinearLayoutManager(NotificationHistoryActivity.this));
                RecyclerView recyclerView = this.mSnoozedRv;
                NotificationHistoryActivity notificationHistoryActivity = NotificationHistoryActivity.this;
                recyclerView.setAdapter(new NotificationSbnAdapter(notificationHistoryActivity, notificationHistoryActivity.mPm, NotificationHistoryActivity.this.mUm, true, NotificationHistoryActivity.this.mUiEventLogger));
                this.mSnoozedRv.setNestedScrollingEnabled(false);
                if (statusBarNotificationArr != null) {
                }
                NotificationHistoryActivity.this.mSnoozeView.setVisibility(8);
                this.mDismissedRv = (RecyclerView) NotificationHistoryActivity.this.mDismissView.findViewById(C0010R$id.notification_list);
                this.mDismissedRv.setLayoutManager(new LinearLayoutManager(NotificationHistoryActivity.this));
                RecyclerView recyclerView2 = this.mDismissedRv;
                NotificationHistoryActivity notificationHistoryActivity2 = NotificationHistoryActivity.this;
                recyclerView2.setAdapter(new NotificationSbnAdapter(notificationHistoryActivity2, notificationHistoryActivity2.mPm, NotificationHistoryActivity.this.mUm, false, NotificationHistoryActivity.this.mUiEventLogger));
                this.mDismissedRv.setNestedScrollingEnabled(false);
                if (statusBarNotificationArr2 != null) {
                }
                NotificationHistoryActivity.this.mDismissView.setVisibility(8);
                NotificationHistoryActivity.this.mCountdownLatch.countDown();
            }
            this.mSnoozedRv = (RecyclerView) NotificationHistoryActivity.this.mSnoozeView.findViewById(C0010R$id.notification_list);
            this.mSnoozedRv.setLayoutManager(new LinearLayoutManager(NotificationHistoryActivity.this));
            RecyclerView recyclerView3 = this.mSnoozedRv;
            NotificationHistoryActivity notificationHistoryActivity3 = NotificationHistoryActivity.this;
            recyclerView3.setAdapter(new NotificationSbnAdapter(notificationHistoryActivity3, notificationHistoryActivity3.mPm, NotificationHistoryActivity.this.mUm, true, NotificationHistoryActivity.this.mUiEventLogger));
            this.mSnoozedRv.setNestedScrollingEnabled(false);
            if (statusBarNotificationArr != null || statusBarNotificationArr.length == 0) {
                NotificationHistoryActivity.this.mSnoozeView.setVisibility(8);
            } else {
                ((NotificationSbnAdapter) this.mSnoozedRv.getAdapter()).onRebuildComplete(new ArrayList(Arrays.asList(statusBarNotificationArr)));
            }
            this.mDismissedRv = (RecyclerView) NotificationHistoryActivity.this.mDismissView.findViewById(C0010R$id.notification_list);
            this.mDismissedRv.setLayoutManager(new LinearLayoutManager(NotificationHistoryActivity.this));
            RecyclerView recyclerView22 = this.mDismissedRv;
            NotificationHistoryActivity notificationHistoryActivity22 = NotificationHistoryActivity.this;
            recyclerView22.setAdapter(new NotificationSbnAdapter(notificationHistoryActivity22, notificationHistoryActivity22.mPm, NotificationHistoryActivity.this.mUm, false, NotificationHistoryActivity.this.mUiEventLogger));
            this.mDismissedRv.setNestedScrollingEnabled(false);
            if (statusBarNotificationArr2 != null || statusBarNotificationArr2.length == 0) {
                NotificationHistoryActivity.this.mDismissView.setVisibility(8);
            } else {
                NotificationHistoryActivity.this.mDismissView.setVisibility(0);
                ((NotificationSbnAdapter) this.mDismissedRv.getAdapter()).onRebuildComplete(new ArrayList(Arrays.asList(statusBarNotificationArr2)));
            }
            NotificationHistoryActivity.this.mCountdownLatch.countDown();
        }

        public void onNotificationRemoved(StatusBarNotification statusBarNotification, NotificationListenerService.RankingMap rankingMap, int i) {
            if (i == 18) {
                ((NotificationSbnAdapter) this.mSnoozedRv.getAdapter()).addSbn(statusBarNotification);
                NotificationHistoryActivity.this.mSnoozeView.setVisibility(0);
                return;
            }
            ((NotificationSbnAdapter) this.mDismissedRv.getAdapter()).addSbn(statusBarNotification);
            NotificationHistoryActivity.this.mDismissView.setVisibility(0);
        }
    };
    private INotificationManager mNm;
    private HistoryLoader.OnHistoryLoaderListener mOnHistoryLoaderListener = new HistoryLoader.OnHistoryLoaderListener() {
        /* class com.android.settings.notification.history.$$Lambda$NotificationHistoryActivity$ZXlabOReguBaB6QHmXbJ7sY3WTQ */

        @Override // com.android.settings.notification.history.HistoryLoader.OnHistoryLoaderListener
        public final void onHistoryLoaded(List list) {
            NotificationHistoryActivity.this.lambda$new$2$NotificationHistoryActivity(list);
        }
    };
    private final SwitchBar.OnSwitchChangeListener mOnSwitchClickListener = new SwitchBar.OnSwitchChangeListener() {
        /* class com.android.settings.notification.history.$$Lambda$NotificationHistoryActivity$c6cjNq83Slaql3Z4wWBJ_3YoxJI */

        @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
        public final void onSwitchChanged(Switch r1, boolean z) {
            NotificationHistoryActivity.this.lambda$new$5$NotificationHistoryActivity(r1, z);
        }
    };
    private PackageManager mPm;
    private ViewGroup mSnoozeView;
    private SwitchBar mSwitchBar;
    private ViewGroup mTodayView;
    private UiEventLogger mUiEventLogger = new UiEventLoggerImpl();
    private UserManager mUm;

    /* access modifiers changed from: package-private */
    public enum NotificationHistoryEvent implements UiEventLogger.UiEventEnum {
        NOTIFICATION_HISTORY_ON(504),
        NOTIFICATION_HISTORY_OFF(505),
        NOTIFICATION_HISTORY_OPEN(506),
        NOTIFICATION_HISTORY_CLOSE(507),
        NOTIFICATION_HISTORY_RECENT_ITEM_CLICK(508),
        NOTIFICATION_HISTORY_SNOOZED_ITEM_CLICK(509),
        NOTIFICATION_HISTORY_PACKAGE_HISTORY_OPEN(510),
        NOTIFICATION_HISTORY_PACKAGE_HISTORY_CLOSE(511),
        NOTIFICATION_HISTORY_OLDER_ITEM_CLICK(512),
        NOTIFICATION_HISTORY_OLDER_ITEM_DELETE(513);
        
        private int mId;

        private NotificationHistoryEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ void lambda$new$2$NotificationHistoryActivity(List list) {
        String str;
        int i = 8;
        findViewById(C0010R$id.today_list).setVisibility(list.isEmpty() ? 8 : 0);
        this.mCountdownLatch.countDown();
        this.mTodayView.setClipToOutline(true);
        this.mTodayView.setOutlineProvider(new ViewOutlineProvider() {
            /* class com.android.settings.notification.history.NotificationHistoryActivity.AnonymousClass1 */

            public void getOutline(View view, Outline outline) {
                TypedArray obtainStyledAttributes = NotificationHistoryActivity.this.obtainStyledAttributes(new int[]{16844145});
                float dimension = obtainStyledAttributes.getDimension(0, 0.0f);
                obtainStyledAttributes.recycle();
                TypedValue typedValue = new TypedValue();
                NotificationHistoryActivity.this.getTheme().resolveAttribute(16843284, typedValue, true);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight() - NotificationHistoryActivity.this.getDrawable(typedValue.resourceId).getIntrinsicHeight(), dimension);
            }
        });
        int size = list.size();
        int i2 = 0;
        while (i2 < size) {
            NotificationHistoryPackage notificationHistoryPackage = (NotificationHistoryPackage) list.get(i2);
            View inflate = LayoutInflater.from(this).inflate(C0012R$layout.notification_history_app_layout, (ViewGroup) null);
            View findViewById = inflate.findViewById(C0010R$id.notification_list);
            findViewById.setVisibility(i);
            ImageButton imageButton = (ImageButton) inflate.findViewById(C0010R$id.expand);
            if (findViewById.getVisibility() == 0) {
                str = getString(C0017R$string.condition_expand_hide);
            } else {
                str = getString(C0017R$string.condition_expand_show);
            }
            imageButton.setContentDescription(str);
            imageButton.setOnClickListener(new View.OnClickListener(findViewById, imageButton, notificationHistoryPackage, i2) {
                /* class com.android.settings.notification.history.$$Lambda$NotificationHistoryActivity$kB_2sxVU4kpyzKgXNnSz3s36YQ */
                public final /* synthetic */ View f$1;
                public final /* synthetic */ ImageButton f$2;
                public final /* synthetic */ NotificationHistoryPackage f$3;
                public final /* synthetic */ int f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void onClick(View view) {
                    NotificationHistoryActivity.this.lambda$new$0$NotificationHistoryActivity(this.f$1, this.f$2, this.f$3, this.f$4, view);
                }
            });
            TextView textView = (TextView) inflate.findViewById(C0010R$id.label);
            CharSequence charSequence = notificationHistoryPackage.label;
            if (charSequence == null) {
                charSequence = notificationHistoryPackage.pkgName;
            }
            textView.setText(charSequence);
            textView.setContentDescription(this.mUm.getBadgedLabelForUser(textView.getText(), UserHandle.getUserHandleForUid(notificationHistoryPackage.uid)));
            ((ImageView) inflate.findViewById(C0010R$id.icon)).setImageDrawable(notificationHistoryPackage.icon);
            TextView textView2 = (TextView) inflate.findViewById(C0010R$id.count);
            textView2.setText(getResources().getQuantityString(C0015R$plurals.notification_history_count, notificationHistoryPackage.notifications.size(), Integer.valueOf(notificationHistoryPackage.notifications.size())));
            NotificationHistoryRecyclerView notificationHistoryRecyclerView = (NotificationHistoryRecyclerView) inflate.findViewById(C0010R$id.notification_list);
            notificationHistoryRecyclerView.setAdapter(new NotificationHistoryAdapter(this.mNm, notificationHistoryRecyclerView, new NotificationHistoryAdapter.OnItemDeletedListener(textView2, inflate) {
                /* class com.android.settings.notification.history.$$Lambda$NotificationHistoryActivity$Q0zSdWxJN000ofO6qpsdybFXYkY */
                public final /* synthetic */ TextView f$1;
                public final /* synthetic */ View f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                @Override // com.android.settings.notification.history.NotificationHistoryAdapter.OnItemDeletedListener
                public final void onItemDeleted(int i) {
                    NotificationHistoryActivity.this.lambda$new$1$NotificationHistoryActivity(this.f$1, this.f$2, i);
                }
            }, this.mUiEventLogger));
            ((NotificationHistoryAdapter) notificationHistoryRecyclerView.getAdapter()).onRebuildComplete(new ArrayList(notificationHistoryPackage.notifications));
            this.mTodayView.addView(inflate);
            i2++;
            i = 8;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$NotificationHistoryActivity(View view, ImageButton imageButton, NotificationHistoryPackage notificationHistoryPackage, int i, View view2) {
        String str;
        NotificationHistoryEvent notificationHistoryEvent;
        view.setVisibility(view.getVisibility() == 0 ? 8 : 0);
        imageButton.setImageResource(view.getVisibility() == 0 ? C0008R$drawable.ic_expand_less : 17302425);
        if (view.getVisibility() == 0) {
            str = getString(C0017R$string.condition_expand_hide);
        } else {
            str = getString(C0017R$string.condition_expand_show);
        }
        imageButton.setContentDescription(str);
        imageButton.sendAccessibilityEvent(32768);
        UiEventLogger uiEventLogger = this.mUiEventLogger;
        if (view.getVisibility() == 0) {
            notificationHistoryEvent = NotificationHistoryEvent.NOTIFICATION_HISTORY_PACKAGE_HISTORY_OPEN;
        } else {
            notificationHistoryEvent = NotificationHistoryEvent.NOTIFICATION_HISTORY_PACKAGE_HISTORY_CLOSE;
        }
        uiEventLogger.logWithPosition(notificationHistoryEvent, notificationHistoryPackage.uid, notificationHistoryPackage.pkgName, i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$NotificationHistoryActivity(TextView textView, View view, int i) {
        textView.setText(getResources().getQuantityString(C0015R$plurals.notification_history_count, i, Integer.valueOf(i)));
        if (i == 0) {
            view.setVisibility(8);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(C0017R$string.notification_history);
        setContentView(C0012R$layout.notification_history);
        this.mTodayView = (ViewGroup) findViewById(C0010R$id.apps);
        this.mSnoozeView = (ViewGroup) findViewById(C0010R$id.snoozed_list);
        this.mDismissView = (ViewGroup) findViewById(C0010R$id.recently_dismissed_list);
        this.mHistoryOff = (ViewGroup) findViewById(C0010R$id.history_off);
        this.mHistoryOn = (ViewGroup) findViewById(C0010R$id.history_on);
        this.mHistoryEmpty = (ViewGroup) findViewById(C0010R$id.history_on_empty);
        this.mSwitchBar = (SwitchBar) findViewById(C0010R$id.switch_bar);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        this.mPm = getPackageManager();
        this.mUm = (UserManager) getSystemService(UserManager.class);
        this.mCountdownLatch = new CountDownLatch(2);
        this.mTodayView.removeAllViews();
        HistoryLoader historyLoader = new HistoryLoader(this, new NotificationBackend(), this.mPm);
        this.mHistoryLoader = historyLoader;
        historyLoader.load(this.mOnHistoryLoaderListener);
        this.mNm = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
        try {
            this.mListener.registerAsSystemService(this, new ComponentName(getPackageName(), getClass().getCanonicalName()), ActivityManager.getCurrentUser());
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot register listener", e);
        }
        bindSwitch();
        this.mCountdownFuture = ThreadUtils.postOnBackgroundThread(new Runnable() {
            /* class com.android.settings.notification.history.$$Lambda$NotificationHistoryActivity$OcfJZpEwLx_PEWMYo3Pqjnixrl8 */

            public final void run() {
                NotificationHistoryActivity.this.lambda$onResume$4$NotificationHistoryActivity();
            }
        });
        this.mUiEventLogger.log(NotificationHistoryEvent.NOTIFICATION_HISTORY_OPEN);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onResume$4 */
    public /* synthetic */ void lambda$onResume$4$NotificationHistoryActivity() {
        try {
            this.mCountdownLatch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Slog.e(TAG, "timed out waiting for loading", e);
        }
        ThreadUtils.postOnMainThread(new Runnable() {
            /* class com.android.settings.notification.history.$$Lambda$NotificationHistoryActivity$xN8GKzUWpK36hayQgSsBHMotFyE */

            public final void run() {
                NotificationHistoryActivity.this.lambda$onResume$3$NotificationHistoryActivity();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onResume$3 */
    public /* synthetic */ void lambda$onResume$3$NotificationHistoryActivity() {
        if (this.mSwitchBar.isChecked() && findViewById(C0010R$id.today_list).getVisibility() == 8 && this.mSnoozeView.getVisibility() == 8 && this.mDismissView.getVisibility() == 8) {
            this.mHistoryOn.setVisibility(8);
            this.mHistoryEmpty.setVisibility(0);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        try {
            this.mListener.unregisterAsSystemService();
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot unregister listener", e);
        }
        this.mUiEventLogger.log(NotificationHistoryEvent.NOTIFICATION_HISTORY_CLOSE);
        super.onPause();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        Future future = this.mCountdownFuture;
        if (future != null) {
            future.cancel(true);
        }
        super.onDestroy();
    }

    public boolean onNavigateUp() {
        finish();
        return true;
    }

    private void bindSwitch() {
        SwitchBar switchBar = this.mSwitchBar;
        if (switchBar != null) {
            int i = C0017R$string.notification_history_toggle;
            switchBar.setSwitchBarText(i, i);
            this.mSwitchBar.show();
            try {
                this.mSwitchBar.addOnSwitchChangeListener(this.mOnSwitchClickListener);
            } catch (IllegalStateException unused) {
            }
            SwitchBar switchBar2 = this.mSwitchBar;
            boolean z = false;
            if (Settings.Secure.getInt(getContentResolver(), "notification_history_enabled", 0) == 1) {
                z = true;
            }
            switchBar2.setChecked(z);
            toggleViews(this.mSwitchBar.isChecked());
        }
    }

    private void toggleViews(boolean z) {
        if (z) {
            this.mHistoryOff.setVisibility(8);
            this.mHistoryOn.setVisibility(0);
        } else {
            this.mHistoryOn.setVisibility(8);
            this.mHistoryOff.setVisibility(0);
            this.mTodayView.removeAllViews();
        }
        this.mHistoryEmpty.setVisibility(8);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$5 */
    public /* synthetic */ void lambda$new$5$NotificationHistoryActivity(Switch r4, boolean z) {
        int i;
        NotificationHistoryEvent notificationHistoryEvent;
        try {
            i = Settings.Secure.getInt(getContentResolver(), "notification_history_enabled");
        } catch (Settings.SettingNotFoundException unused) {
            i = 0;
        }
        if (i != z) {
            Settings.Secure.putInt(getContentResolver(), "notification_history_enabled", z ? 1 : 0);
            UiEventLogger uiEventLogger = this.mUiEventLogger;
            if (z) {
                notificationHistoryEvent = NotificationHistoryEvent.NOTIFICATION_HISTORY_ON;
            } else {
                notificationHistoryEvent = NotificationHistoryEvent.NOTIFICATION_HISTORY_OFF;
            }
            uiEventLogger.log(notificationHistoryEvent);
            Log.d(TAG, "onSwitchChange history to " + z);
        }
        this.mHistoryOn.setVisibility(8);
        if (z) {
            this.mHistoryEmpty.setVisibility(0);
            this.mHistoryOff.setVisibility(8);
        } else {
            this.mHistoryOff.setVisibility(0);
            this.mHistoryEmpty.setVisibility(8);
        }
        this.mTodayView.removeAllViews();
    }
}
