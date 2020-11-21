package com.oneplus.settings.darkmode;

import android.accounts.AccountManager;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ServiceManager;
import android.os.UserManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.R$styleable;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.widget.SwitchBar;
import com.google.android.material.emptyview.EmptyPageView;
import com.oneplus.loading.LoadingHelper;
import com.oneplus.settings.BaseActivity;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.VibratorSceneUtils;
import java.util.ArrayList;
import java.util.List;

public class OPGloblaDarkModeSettings extends BaseActivity implements AdapterView.OnItemClickListener {
    private static boolean mItemClicked = false;
    private List<OPAppModel> mAppList = new ArrayList();
    private ListView mAppListView;
    AppOpsManager.OnOpChangedListener mAppOpsChangedListener = new AppOpsManager.OnOpChangedListener() {
        /* class com.oneplus.settings.darkmode.OPGloblaDarkModeSettings.AnonymousClass2 */

        public void onOpChanged(String str, String str2) {
            if (OPGloblaDarkModeSettings.mItemClicked) {
                boolean unused = OPGloblaDarkModeSettings.mItemClicked = false;
            } else {
                OPGloblaDarkModeSettings.this.startLoadData();
            }
        }
    };
    private AppOpsManager mAppOpsManager;
    private Context mContext;
    private boolean mFirstLoad = true;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.oneplus.settings.darkmode.OPGloblaDarkModeSettings.AnonymousClass1 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (OPGloblaDarkModeSettings.this.mOPGloblaDarkModeAdapter != null && OPGloblaDarkModeSettings.this.mOPGlobleDarkModeApplicationLoader != null) {
                OPGloblaDarkModeSettings.this.mAppList.clear();
                OPGloblaDarkModeSettings.this.mAppList.addAll(OPGloblaDarkModeSettings.this.mOPGlobleDarkModeApplicationLoader.getAppListByType(0));
                if (OPGloblaDarkModeSettings.this.mAppList.isEmpty()) {
                    OPGloblaDarkModeSettings.this.mAppList.add(new OPAppModel("", "", "", -1, false));
                }
                OPGloblaDarkModeSettings.this.mLoadingContainer.setVisibility(8);
                OPGloblaDarkModeSettings.this.mOPGloblaDarkModeAdapter.setData(OPGloblaDarkModeSettings.this.mAppList);
                EmptyPageView emptyPageView = (EmptyPageView) OPGloblaDarkModeSettings.this.findViewById(C0010R$id.op_empty_list_tips_view);
                emptyPageView.getEmptyTextView().setText(C0017R$string.oneplus_app_list_empty);
                emptyPageView.getEmptyImageView().setImageResource(C0008R$drawable.op_empty);
                if (OPGloblaDarkModeSettings.this.mAppList.isEmpty()) {
                    emptyPageView.setVisibility(0);
                    OPGloblaDarkModeSettings.this.mAppListView.setEmptyView(emptyPageView);
                }
            }
        }
    };
    private View mLoadingContainer;
    private boolean mNeedReloadData = false;
    private OPGloblaDarkModeAdapter mOPGloblaDarkModeAdapter;
    private OPGlobleDarkModeApplicationLoader mOPGlobleDarkModeApplicationLoader;
    private final BroadcastReceiver mPackageBroadcastReceiver = new BroadcastReceiver() {
        /* class com.oneplus.settings.darkmode.OPGloblaDarkModeSettings.AnonymousClass5 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (TextUtils.equals(action, "android.intent.action.PACKAGE_REMOVED") || TextUtils.equals(action, "android.intent.action.PACKAGE_ADDED")) {
                    String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
                    Log.d("OPGloblaDarkModeSettings", schemeSpecificPart + "has changed");
                    OPGloblaDarkModeSettings.this.mNeedReloadData = true;
                }
            }
        }
    };
    private PackageManager mPackageManager;
    private ProgressDialog mProgressDialog;
    private SwitchBar mSwitchBar;
    private Toast mToastTip;
    private long[] mVibratePattern;
    private Vibrator mVibrator;

    static {
        ComponentName.unflattenFromString("com.oneplus.settings.multiapp/com.oneplus.settings.multiapp.OPBasicDeviceAdminReceiver");
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.op_app_list_activity);
        this.mContext = this;
        if (OPUtils.isSupportXVibrate()) {
            this.mVibrator = (Vibrator) getSystemService("vibrator");
        }
        IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        AccountManager accountManager = (AccountManager) getSystemService("account");
        UserManager userManager = (UserManager) getSystemService("user");
        this.mAppOpsManager = (AppOpsManager) getSystemService("appops");
        this.mPackageManager = getPackageManager();
        this.mOPGlobleDarkModeApplicationLoader = new OPGlobleDarkModeApplicationLoader(this, this.mAppOpsManager, this.mPackageManager);
        initView();
        registerPackageReceiver();
        this.mAppOpsManager.startWatchingMode(1010, null, this.mAppOpsChangedListener);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        boolean z = true;
        boolean z2 = Settings.Secure.getInt(getContentResolver(), "op_force_dark_mode", 0) == 1;
        SwitchBar switchBar = this.mSwitchBar;
        if (!z2 || !OPUtils.isBlackModeOn(this.mContext.getContentResolver())) {
            z = false;
        }
        switchBar.setChecked(z);
        this.mSwitchBar.addOnSwitchChangeListener(new SwitchBar.OnSwitchChangeListener() {
            /* class com.oneplus.settings.darkmode.$$Lambda$OPGloblaDarkModeSettings$IDQfUg3wamKbZHcyGaART38ODL4 */

            @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
            public final void onSwitchChanged(Switch r1, boolean z) {
                OPGloblaDarkModeSettings.this.lambda$onResume$0$OPGloblaDarkModeSettings(r1, z);
            }
        });
        if (this.mNeedReloadData && !this.mFirstLoad) {
            startLoadData();
            this.mNeedReloadData = false;
        }
        this.mFirstLoad = false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onResume$0 */
    public /* synthetic */ void lambda$onResume$0$OPGloblaDarkModeSettings(Switch r4, final boolean z) {
        mItemClicked = true;
        OPUtils.sendAnalytics("app_dark_main", "main_switch", z ? "1" : "0");
        this.mOPGloblaDarkModeAdapter.enableList(z);
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler.postDelayed(new Runnable() {
            /* class com.oneplus.settings.darkmode.OPGloblaDarkModeSettings.AnonymousClass3 */

            public void run() {
                Settings.Secure.putInt(OPGloblaDarkModeSettings.this.getContentResolver(), "op_force_dark_mode", z ? 1 : 0);
            }
        }, 200);
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        super.onPause();
        mItemClicked = false;
        if (this.mProgressDialog != null && !isFinishing()) {
            this.mProgressDialog.dismiss();
        }
    }

    private void registerPackageReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        registerReceiver(this.mPackageBroadcastReceiver, intentFilter);
    }

    private void initView() {
        if (mItemClicked) {
            mItemClicked = false;
            return;
        }
        this.mProgressDialog = new ProgressDialog(this);
        this.mAppListView = (ListView) findViewById(C0010R$id.op_app_list);
        OPGloblaDarkModeAdapter oPGloblaDarkModeAdapter = new OPGloblaDarkModeAdapter(this, this.mAppList);
        this.mOPGloblaDarkModeAdapter = oPGloblaDarkModeAdapter;
        this.mAppListView.setAdapter((ListAdapter) oPGloblaDarkModeAdapter);
        this.mAppListView.setOnItemClickListener(this);
        View inflate = LayoutInflater.from(this).inflate(C0012R$layout.op_global_dark_mode_head_view, (ViewGroup) null);
        inflate.findViewById(C0010R$id.op_global_drakmode_toggle).setEnabled(OPUtils.isBlackModeOn(this.mContext.getContentResolver()));
        SwitchBar switchBar = (SwitchBar) inflate.findViewById(C0010R$id.switch_bar);
        this.mSwitchBar = switchBar;
        int i = C0017R$string.oneplus_global_dark_mode_title;
        switchBar.setSwitchBarText(i, i);
        if (!OPUtils.isBlackModeOn(this.mContext.getContentResolver())) {
            this.mSwitchBar.setClickable(false);
            inflate.setOnTouchListener(new View.OnTouchListener() {
                /* class com.oneplus.settings.darkmode.$$Lambda$OPGloblaDarkModeSettings$fsazuE0NxjdMcJTYWmUG8GjWbfs */

                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return OPGloblaDarkModeSettings.this.lambda$initView$1$OPGloblaDarkModeSettings(view, motionEvent);
                }
            });
        }
        this.mSwitchBar.setEnabled(OPUtils.isBlackModeOn(this.mContext.getContentResolver()));
        this.mAppListView.addHeaderView(inflate);
        View findViewById = findViewById(C0010R$id.loading_container);
        this.mLoadingContainer = findViewById;
        TextView textView = (TextView) findViewById.findViewById(C0010R$id.loading_message);
        this.mOPGlobleDarkModeApplicationLoader.setmLoadingContainer(this.mLoadingContainer);
        startLoadData();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initView$1 */
    public /* synthetic */ boolean lambda$initView$1$OPGloblaDarkModeSettings(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0) {
            return false;
        }
        showTips();
        return true;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.oneplus.settings.darkmode.OPGloblaDarkModeSettings$4  reason: invalid class name */
    public class AnonymousClass4 extends LoadingHelper {
        final /* synthetic */ OPGloblaDarkModeSettings this$0;

        /* access modifiers changed from: protected */
        @Override // com.oneplus.loading.LoadingHelper
        public Object showProgree() {
            if (this.this$0.isFinishing() || this.this$0.isDestroyed()) {
                return this.this$0.mProgressDialog;
            }
            if (this.this$0.mProgressDialog != null && this.this$0.mProgressDialog.isShowing()) {
                this.this$0.mProgressDialog.dismiss();
            }
            this.this$0.mProgressDialog.show();
            this.this$0.mProgressDialog.setCancelable(false);
            this.this$0.mProgressDialog.setCanceledOnTouchOutside(false);
            this.this$0.mProgressDialog.setMessage(this.this$0.getString(C0017R$string.oneplus_multi_app_init));
            return this.this$0.mProgressDialog;
        }

        /* access modifiers changed from: protected */
        @Override // com.oneplus.loading.LoadingHelper
        public void hideProgree(Object obj) {
            try {
                if (this.this$0.isFinishing()) {
                    return;
                }
                if (!this.this$0.isDestroyed()) {
                    if (this.this$0.mProgressDialog != null && this.this$0.mProgressDialog.isShowing()) {
                        this.this$0.mProgressDialog.dismiss();
                    }
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private void showTips() {
        Toast toast = this.mToastTip;
        if (toast != null) {
            toast.cancel();
        }
        Context context = this.mContext;
        Toast makeText = Toast.makeText(context, context.getString(C0017R$string.oneplus_global_dark_mode_only_valid_in_dark_mode), 0);
        this.mToastTip = makeText;
        makeText.show();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void startLoadData() {
        this.mOPGlobleDarkModeApplicationLoader.loadAppMode(1010);
        this.mOPGlobleDarkModeApplicationLoader.initData(0, this.mHandler);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        if (VibratorSceneUtils.systemVibrateEnabled(this)) {
            long[] vibratorScenePattern = VibratorSceneUtils.getVibratorScenePattern(this, this.mVibrator, 1003);
            this.mVibratePattern = vibratorScenePattern;
            VibratorSceneUtils.vibrateIfNeeded(vibratorScenePattern, this.mVibrator);
        }
        if (i > 0) {
            i--;
        }
        mItemClicked = true;
        boolean z = !this.mOPGloblaDarkModeAdapter.getSelected(i);
        OPAppModel oPAppModel = this.mAppList.get(i);
        Log.d("OPGloblaDarkModeSettings", "Oneplus global black  app list item click getAppopsMode:" + oPAppModel.getAppopsMode());
        if (oPAppModel.getAppopsMode() == 101) {
            oPAppModel.setDisable(true);
            oPAppModel.setAppopsMode(R$styleable.Constraint_motionStagger);
            this.mAppOpsManager.setMode(1010, oPAppModel.getUid(), oPAppModel.getPkgName(), R$styleable.Constraint_motionStagger);
        } else if (oPAppModel.getAppopsMode() == 104) {
            oPAppModel.setAppopsMode(R$styleable.Constraint_layout_goneMarginRight);
            this.mAppOpsManager.setMode(1010, oPAppModel.getUid(), oPAppModel.getPkgName(), R$styleable.Constraint_layout_goneMarginRight);
        } else if (oPAppModel.getAppopsMode() == 100) {
            oPAppModel.setAppopsMode(R$styleable.Constraint_layout_goneMarginTop);
            this.mAppOpsManager.setMode(1010, oPAppModel.getUid(), oPAppModel.getPkgName(), R$styleable.Constraint_layout_goneMarginTop);
        } else if (oPAppModel.getAppopsMode() == 103) {
            oPAppModel.setAppopsMode(100);
            this.mAppOpsManager.setMode(1010, oPAppModel.getUid(), oPAppModel.getPkgName(), 100);
        } else if (oPAppModel.getAppopsMode() == 102) {
            oPAppModel.setAppopsMode(R$styleable.Constraint_pathMotionArc);
            this.mAppOpsManager.setMode(1010, oPAppModel.getUid(), oPAppModel.getPkgName(), R$styleable.Constraint_pathMotionArc);
        } else if (oPAppModel.getAppopsMode() == 105) {
            oPAppModel.setAppopsMode(R$styleable.Constraint_layout_goneMarginStart);
            this.mAppOpsManager.setMode(1010, oPAppModel.getUid(), oPAppModel.getPkgName(), R$styleable.Constraint_layout_goneMarginStart);
        } else if (oPAppModel.getAppopsMode() == 107) {
            oPAppModel.setAppopsMode(R$styleable.Constraint_layout_goneMarginStart);
            this.mAppOpsManager.setMode(1010, oPAppModel.getUid(), oPAppModel.getPkgName(), R$styleable.Constraint_layout_goneMarginStart);
        } else if (oPAppModel.getAppopsMode() == 106) {
            oPAppModel.setAppopsMode(R$styleable.Constraint_layout_goneMarginTop);
            this.mAppOpsManager.setMode(1010, oPAppModel.getUid(), oPAppModel.getPkgName(), R$styleable.Constraint_layout_goneMarginTop);
        } else if (oPAppModel.getAppopsMode() == 108) {
            oPAppModel.setAppopsMode(R$styleable.Constraint_layout_goneMarginStart);
            this.mAppOpsManager.setMode(1010, oPAppModel.getUid(), oPAppModel.getPkgName(), R$styleable.Constraint_layout_goneMarginStart);
        } else {
            oPAppModel.setAppopsMode(R$styleable.Constraint_layout_goneMarginStart);
            this.mAppOpsManager.setMode(1010, oPAppModel.getUid(), oPAppModel.getPkgName(), R$styleable.Constraint_layout_goneMarginStart);
        }
        OPUtils.sendAnalytics("app_dark_app", "package_name", oPAppModel.getPkgName());
        int versionCode = oPAppModel.getVersionCode();
        String str = "0";
        OPUtils.sendAnalytics("app_dark_app", "version_code", versionCode > 0 ? String.valueOf(versionCode) : str);
        if (z) {
            str = "1";
        }
        OPUtils.sendAnalytics("app_dark_app", "app_switch", str);
        this.mOPGloblaDarkModeAdapter.setSelected(i, z);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
        mItemClicked = false;
        this.mHandler.removeCallbacksAndMessages(null);
        this.mAppOpsManager.stopWatchingMode(this.mAppOpsChangedListener);
        unregisterReceiver(this.mPackageBroadcastReceiver);
        this.mOPGlobleDarkModeApplicationLoader.releaseAppList();
    }
}
