package com.oneplus.security.firewall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0013R$menu;
import com.android.settings.C0017R$string;
import com.google.android.material.edgeeffect.SpringListView;
import com.google.android.material.edgeeffect.SpringRelativeLayout;
import com.oneplus.security.database.Const;
import com.oneplus.security.firewall.NetworkRestrictManager;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.security.utils.ToastUtil;
import com.oneplus.settings.BaseAppCompatActivity;
import com.oneplus.settings.SettingsBaseApplication;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkRestrictActivity extends BaseAppCompatActivity implements NetworkRestrictManager.IAppsNetworkRestrictTaskCallBack {
    private NetworkRestrictAdapter mAdapter;
    private AppAddOrRemovedReceiver mAddOrRemovedReceiver;
    private ExecutorService mExecutor = Executors.newFixedThreadPool(5);
    private Handler mHandler;
    private boolean mIsTaskDoing = false;
    private ListView mListView;
    protected Menu mMenu;
    private ContentObserver mNetworkRestrictObserver = new ContentObserver(new Handler()) {
        /* class com.oneplus.security.firewall.NetworkRestrictActivity.AnonymousClass5 */

        public void onChange(boolean z, Uri uri) {
            if (uri != null && !NetworkRestrictActivity.this.mIsTaskDoing) {
                LogUtils.d("NetworkRestrictActivity", "mNetworkRestrictObserver uri=" + uri);
                NetworkRestrictActivity.this.sendHandlerMessage(1, 1000);
            }
        }
    };
    protected PopupMenu mPopupMenu;
    private OPProgressDialog mProgressDialog = null;
    private TextView mSettingAll;
    private boolean mShowSystemApp = false;

    static class NetworkRestrictHandler extends Handler {
        private final WeakReference<NetworkRestrictActivity> activityReference;

        public NetworkRestrictHandler(NetworkRestrictActivity networkRestrictActivity) {
            this.activityReference = new WeakReference<>(networkRestrictActivity);
        }

        public void handleMessage(Message message) {
            NetworkRestrictActivity networkRestrictActivity = this.activityReference.get();
            if (networkRestrictActivity != null && message.what == 1) {
                NetworkRestrictManager.getInstance(SettingsBaseApplication.getContext()).refreshAppsNetworkRestrict(networkRestrictActivity, networkRestrictActivity.mShowSystemApp, networkRestrictActivity.mExecutor);
            }
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.activity_network_restrict_applist);
        handleSpringListView(getWindow().getDecorView(), C0010R$id.app_list);
        Toolbar toolbar = (Toolbar) findViewById(C0010R$id.action_bar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.security.firewall.NetworkRestrictActivity.AnonymousClass1 */

            public void onClick(View view) {
                NetworkRestrictActivity.this.onBackPressed();
            }
        });
        this.mSettingAll = (TextView) findViewById(C0010R$id.setting_all);
        this.mListView = (ListView) findViewById(C0010R$id.app_list);
        TextView textView = new TextView(this);
        textView.setLayoutParams(new AbsListView.LayoutParams(-1, getResources().getDimensionPixelSize(C0007R$dimen.oneplus_security_layout_margin_top1)));
        this.mListView.addHeaderView(textView);
        this.mProgressDialog = new OPProgressDialog(this);
        this.mHandler = new NetworkRestrictHandler(this);
        NetworkRestrictAdapter networkRestrictAdapter = new NetworkRestrictAdapter(this);
        this.mAdapter = networkRestrictAdapter;
        this.mListView.setAdapter((ListAdapter) networkRestrictAdapter);
        this.mAdapter.updateData(NetworkRestrictManager.getInstance(getApplicationContext()).getAppList());
        PopupMenu popupMenu = new PopupMenu(this, this.mSettingAll);
        this.mPopupMenu = popupMenu;
        this.mMenu = popupMenu.getMenu();
        getMenuInflater().inflate(C0013R$menu.menu_firewall_batch, this.mMenu);
        this.mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            /* class com.oneplus.security.firewall.NetworkRestrictActivity.AnonymousClass2 */

            /* JADX WARNING: Removed duplicated region for block: B:15:0x0025 A[SYNTHETIC] */
            @Override // androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onMenuItemClick(android.view.MenuItem r10) {
                /*
                    r9 = this;
                    int r10 = r10.getItemId()
                    int r0 = com.android.settings.C0010R$id.firewall_rule_allow_batch
                    r1 = 1
                    r2 = 0
                    if (r10 != r0) goto L_0x000d
                    r5 = r2
                L_0x000b:
                    r6 = r5
                    goto L_0x0020
                L_0x000d:
                    int r0 = com.android.settings.C0010R$id.firewall_rule_forbid_batch
                    if (r10 != r0) goto L_0x0013
                    r5 = r1
                    goto L_0x000b
                L_0x0013:
                    int r0 = com.android.settings.C0010R$id.firewall_rule_wlan_only_batch
                    if (r10 != r0) goto L_0x001a
                    r5 = r1
                    r6 = r2
                    goto L_0x0020
                L_0x001a:
                    int r0 = com.android.settings.C0010R$id.firewall_rule_data_only_batch
                    if (r10 != r0) goto L_0x004a
                    r6 = r1
                    r5 = r2
                L_0x0020:
                    java.util.ArrayList r4 = com.google.android.collect.Lists.newArrayList()
                    monitor-enter(r9)
                    com.oneplus.security.firewall.NetworkRestrictActivity r10 = com.oneplus.security.firewall.NetworkRestrictActivity.this     // Catch:{ all -> 0x0047 }
                    com.oneplus.security.firewall.NetworkRestrictAdapter r10 = com.oneplus.security.firewall.NetworkRestrictActivity.access$200(r10)     // Catch:{ all -> 0x0047 }
                    java.util.List r10 = r10.getmAppUidItemList()     // Catch:{ all -> 0x0047 }
                    r4.addAll(r10)     // Catch:{ all -> 0x0047 }
                    monitor-exit(r9)     // Catch:{ all -> 0x0047 }
                    com.oneplus.security.firewall.NetworkRestrictActivity r10 = com.oneplus.security.firewall.NetworkRestrictActivity.this
                    android.content.Context r10 = r10.getApplicationContext()
                    com.oneplus.security.firewall.NetworkRestrictManager r3 = com.oneplus.security.firewall.NetworkRestrictManager.getInstance(r10)
                    com.oneplus.security.firewall.NetworkRestrictActivity r7 = com.oneplus.security.firewall.NetworkRestrictActivity.this
                    java.util.concurrent.ExecutorService r8 = com.oneplus.security.firewall.NetworkRestrictActivity.access$100(r7)
                    r3.batchUpdateRules(r4, r5, r6, r7, r8)
                    return r2
                L_0x0047:
                    r10 = move-exception
                    monitor-exit(r9)
                    throw r10
                L_0x004a:
                    return r2
                */
                throw new UnsupportedOperationException("Method not decompiled: com.oneplus.security.firewall.NetworkRestrictActivity.AnonymousClass2.onMenuItemClick(android.view.MenuItem):boolean");
            }
        });
        this.mPopupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            /* class com.oneplus.security.firewall.NetworkRestrictActivity.AnonymousClass3 */

            @Override // androidx.appcompat.widget.PopupMenu.OnDismissListener
            public void onDismiss(PopupMenu popupMenu) {
                NetworkRestrictActivity.this.mSettingAll.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, NetworkRestrictActivity.this.getDrawable(C0008R$drawable.ic_down), (Drawable) null);
            }
        });
        this.mSettingAll.setOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.security.firewall.NetworkRestrictActivity.AnonymousClass4 */

            public void onClick(View view) {
                NetworkRestrictActivity.this.mSettingAll.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, NetworkRestrictActivity.this.getDrawable(C0008R$drawable.ic_up), (Drawable) null);
                NetworkRestrictActivity.this.mPopupMenu.show();
            }
        });
        NetworkRestrictManager.getInstance(getApplicationContext()).refreshAppsNetworkRestrict(this, this.mShowSystemApp, this.mExecutor);
        getApplicationContext().getContentResolver().registerContentObserver(Const.URI_NETWORK_RESTRICT, false, this.mNetworkRestrictObserver);
        IntentFilter intentFilter = new IntentFilter("com.oneplus.security.ACTION_REFRESH_APP_LIST");
        this.mAddOrRemovedReceiver = new AppAddOrRemovedReceiver();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(this.mAddOrRemovedReceiver, intentFilter);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        getApplicationContext().getContentResolver().unregisterContentObserver(this.mNetworkRestrictObserver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(this.mAddOrRemovedReceiver);
        this.mExecutor.shutdownNow();
        this.mExecutor = null;
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        try {
            this.mPopupMenu.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, getString(C0017R$string.menu_show_system_app)).setShowAsActionFlags(0);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 1) {
            return super.onOptionsItemSelected(menuItem);
        }
        if (!this.mShowSystemApp) {
            this.mShowSystemApp = true;
            menuItem.setTitle(getString(C0017R$string.menu_hide_system_app));
        } else {
            this.mShowSystemApp = false;
            menuItem.setTitle(getString(C0017R$string.menu_show_system_app));
        }
        NetworkRestrictManager.getInstance(getApplicationContext()).refreshAppsNetworkRestrict(this, this.mShowSystemApp, this.mExecutor);
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void sendHandlerMessage(int i, long j) {
        this.mHandler.removeMessages(i);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(i), j);
    }

    class AppAddOrRemovedReceiver extends BroadcastReceiver {
        AppAddOrRemovedReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.d("NetworkRestrictActivity", "AppAddOrRemovedReceiver action=" + action);
            if ("com.oneplus.security.ACTION_REFRESH_APP_LIST".equals(action)) {
                NetworkRestrictActivity.this.sendHandlerMessage(1, 1000);
            }
        }
    }

    @Override // com.oneplus.security.firewall.NetworkRestrictManager.IAppsNetworkRestrictTaskCallBack
    public void onTaskStart(int i, boolean z) {
        LogUtils.d("NetworkRestrictActivity", "onLoadAppStart type=" + i);
        this.mIsTaskDoing = true;
        if (this.mProgressDialog == null) {
            return;
        }
        if (z || (i == 1 && this.mAdapter.getmAppUidItemList().isEmpty())) {
            this.mProgressDialog.setMessage(getString(C0017R$string.text_waiting));
            this.mProgressDialog.show();
        } else if (i == 2) {
            this.mProgressDialog.setMessage(getString(C0017R$string.firewall_rule_batch_operating));
            this.mProgressDialog.show();
        }
    }

    @Override // com.oneplus.security.firewall.NetworkRestrictManager.IAppsNetworkRestrictTaskCallBack
    public void onTaskFinished(int i, Object obj) {
        LogUtils.d("NetworkRestrictActivity", "onLoadAppFinished type=" + i);
        if (i == 1) {
            this.mAdapter.updateData((List) obj);
            OPProgressDialog oPProgressDialog = this.mProgressDialog;
            if (oPProgressDialog != null && oPProgressDialog.isShowing()) {
                try {
                    this.mProgressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (i == 2) {
            NetworkRestrictManager.getInstance(getApplicationContext()).refreshAppsNetworkRestrict(this, this.mShowSystemApp, this.mExecutor);
        }
        this.mIsTaskDoing = false;
    }

    @Override // com.oneplus.security.firewall.NetworkRestrictManager.IAppsNetworkRestrictTaskCallBack
    public void onTaskError(int i, String str) {
        LogUtils.d("NetworkRestrictActivity", "onLoadAppError type=" + i + " msg=" + str);
        OPProgressDialog oPProgressDialog = this.mProgressDialog;
        if (oPProgressDialog != null) {
            try {
                oPProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.mIsTaskDoing = false;
        if (i == 1) {
            ToastUtil.showShortToast(this, getString(C0017R$string.load_error));
        } else if (i == 2) {
            ToastUtil.showShortToast(this, getString(C0017R$string.set_error));
        }
    }

    private void handleSpringListView(View view, int i) {
        SpringRelativeLayout springRelativeLayout = (SpringRelativeLayout) view.findViewById(C0010R$id.spring_layout);
        springRelativeLayout.addSpringView(i);
        ((SpringListView) view.findViewById(i)).setEdgeEffectFactory(springRelativeLayout.createViewEdgeEffectFactory());
    }
}
