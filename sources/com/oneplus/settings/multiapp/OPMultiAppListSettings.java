package com.oneplus.settings.multiapp;

import android.accounts.AccountManager;
import android.app.ActivityManagerNative;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settingslib.utils.ThreadUtils;
import com.google.android.material.emptyview.EmptyPageView;
import com.oneplus.loading.DialogLoadingHelper;
import com.oneplus.loading.LoadingHelper;
import com.oneplus.settings.BaseActivity;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.multiapp.OPDeleteNonRequiredAppsTask;
import com.oneplus.settings.multiapp.OPMultiAppListSettings;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.VibratorSceneUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OPMultiAppListSettings extends BaseActivity implements AdapterView.OnItemClickListener {
    private List<OPAppModel> mAppList = new ArrayList();
    private ListView mAppListView;
    private AppOpsManager mAppOpsManager;
    private Context mContext;
    private AsyncTask<String, Void, Void> mCreateManagedProfileTask;
    private boolean mFirstLoad = true;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.oneplus.settings.multiapp.OPMultiAppListSettings.AnonymousClass1 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (OPMultiAppListSettings.this.mOPMultiAppAdapter != null && OPMultiAppListSettings.this.mOPApplicationLoader != null) {
                OPMultiAppListSettings.this.mAppList.clear();
                OPMultiAppListSettings.this.mAppList.addAll(OPMultiAppListSettings.this.mOPApplicationLoader.getAppListByType(message.what));
                OPMultiAppListSettings.this.mOPMultiAppAdapter.setData(OPMultiAppListSettings.this.mAppList);
                EmptyPageView emptyPageView = (EmptyPageView) OPMultiAppListSettings.this.findViewById(C0010R$id.op_empty_list_tips_view);
                emptyPageView.getEmptyTextView().setText(C0017R$string.oneplus_app_list_empty);
                emptyPageView.getEmptyImageView().setImageResource(C0008R$drawable.op_empty);
                if (OPMultiAppListSettings.this.mAppList.isEmpty()) {
                    emptyPageView.setVisibility(0);
                    OPMultiAppListSettings.this.mAppListView.setEmptyView(emptyPageView);
                }
            }
        }
    };
    private HandlerThread mHandlerThread;
    private int mInitPosition;
    private Handler mInstallMultiApphandler;
    private boolean mIsInCreating = false;
    private boolean mIsWarnDialogShowing = false;
    private View mLoadingContainer;
    private LoadingHelper mLoadingHelper;
    private UserInfo mManagedProfileOrUserInfo;
    private boolean mNeedReloadData = false;
    private OPApplicationLoader mOPApplicationLoader;
    private OPMultiAppAdapter mOPMultiAppAdapter;
    private final BroadcastReceiver mPackageBroadcastReceiver = new BroadcastReceiver() {
        /* class com.oneplus.settings.multiapp.OPMultiAppListSettings.AnonymousClass9 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (TextUtils.equals(action, "android.intent.action.PACKAGE_REMOVED") || TextUtils.equals(action, "android.intent.action.PACKAGE_ADDED")) {
                    String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
                    Log.d("OPMultiAppListSettings", schemeSpecificPart + "has changed");
                    OPMultiAppListSettings.this.mNeedReloadData = true;
                }
            }
        }
    };
    private PackageManager mPackageManager;
    private ProgressDialog mProgressDialog;
    private Handler mRefreshUIHandler = new Handler(Looper.getMainLooper()) {
        /* class com.oneplus.settings.multiapp.OPMultiAppListSettings.AnonymousClass2 */

        public void handleMessage(Message message) {
            if (message.what == 88) {
                int i = message.arg1;
                OPAppModel oPAppModel = (OPAppModel) OPMultiAppListSettings.this.mAppListView.getItemAtPosition(i);
                OPMultiAppListSettings.this.mOPMultiAppAdapter.setSelected(i, !OPMultiAppListSettings.this.mOPMultiAppAdapter.getSelected(i));
                OPMultiAppListSettings.this.mAppOpsManager.setMode(1005, oPAppModel.getUid(), oPAppModel.getPkgName(), 0);
                Toast.makeText(OPMultiAppListSettings.this.mContext, OPMultiAppListSettings.this.getEnabledString(oPAppModel), 0).show();
                OPUtils.notifyMultiPackageRemoved(OPMultiAppListSettings.this, oPAppModel.getPkgName(), oPAppModel.getUid(), false);
            }
        }
    };
    private UserManager mUserManager;
    private long[] mVibratePattern;
    private Vibrator mVibrator;
    private AlertDialog mWarnDialog;

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
        this.mUserManager = (UserManager) getSystemService("user");
        this.mAppOpsManager = (AppOpsManager) getSystemService("appops");
        this.mPackageManager = getPackageManager();
        this.mOPApplicationLoader = new OPApplicationLoader(this, this.mAppOpsManager, this.mPackageManager);
        HandlerThread handlerThread = new HandlerThread("install-multiapp-handler-thread");
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mInstallMultiApphandler = new Handler(this.mHandlerThread.getLooper()) {
            /* class com.oneplus.settings.multiapp.OPMultiAppListSettings.AnonymousClass3 */

            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (OPMultiAppListSettings.this.mAppListView != null && OPMultiAppListSettings.this.mOPMultiAppAdapter != null && OPMultiAppListSettings.this.mAppOpsManager != null) {
                    ProgressDialog progressDialog = (ProgressDialog) message.obj;
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage(OPMultiAppListSettings.this.mContext.getString(C0017R$string.oneplus_multi_app_init));
                    DialogLoadingHelper dialogLoadingHelper = new DialogLoadingHelper(progressDialog);
                    dialogLoadingHelper.beginShowProgress();
                    final int i = message.arg1;
                    OPAppModel oPAppModel = (OPAppModel) OPMultiAppListSettings.this.mAppListView.getItemAtPosition(i);
                    OPMultiAppListSettings.this.installMultiApp(oPAppModel.getPkgName(), oPAppModel);
                    dialogLoadingHelper.finishShowProgress(new LoadingHelper.FinishShowCallback() {
                        /* class com.oneplus.settings.multiapp.OPMultiAppListSettings.AnonymousClass3.AnonymousClass1 */

                        @Override // com.oneplus.loading.LoadingHelper.FinishShowCallback
                        public void finish(boolean z) {
                            Message message = new Message();
                            message.what = 88;
                            message.arg1 = i;
                            OPMultiAppListSettings.this.mRefreshUIHandler.sendMessage(message);
                            Settings.Secure.putIntForUser(OPMultiAppListSettings.this.mContext.getContentResolver(), "notification_badging", Settings.Secure.getInt(OPMultiAppListSettings.this.mContext.getContentResolver(), "notification_badging", 1), 999);
                        }
                    });
                }
            }
        };
        initView();
        this.mManagedProfileOrUserInfo = getCorpUserInfo(this.mContext);
        registerPackageReceiver();
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        if (this.mNeedReloadData && !this.mFirstLoad) {
            startLoadData();
            this.mNeedReloadData = false;
        }
        this.mFirstLoad = false;
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        super.onPause();
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
        this.mProgressDialog = new ProgressDialog(this);
        this.mAppListView = (ListView) findViewById(C0010R$id.op_app_list);
        OPMultiAppAdapter oPMultiAppAdapter = new OPMultiAppAdapter(this, this.mAppList);
        this.mOPMultiAppAdapter = oPMultiAppAdapter;
        this.mAppListView.setAdapter((ListAdapter) oPMultiAppAdapter);
        this.mAppListView.setOnItemClickListener(this);
        View findViewById = findViewById(C0010R$id.loading_container);
        this.mLoadingContainer = findViewById;
        TextView textView = (TextView) findViewById.findViewById(C0010R$id.loading_message);
        this.mLoadingHelper = new LoadingHelper() {
            /* class com.oneplus.settings.multiapp.OPMultiAppListSettings.AnonymousClass4 */

            /* access modifiers changed from: protected */
            @Override // com.oneplus.loading.LoadingHelper
            public Object showProgree() {
                if (OPMultiAppListSettings.this.isFinishing() || OPMultiAppListSettings.this.isDestroyed()) {
                    return OPMultiAppListSettings.this.mProgressDialog;
                }
                if (OPMultiAppListSettings.this.mProgressDialog != null && OPMultiAppListSettings.this.mProgressDialog.isShowing()) {
                    OPMultiAppListSettings.this.mProgressDialog.dismiss();
                }
                OPMultiAppListSettings.this.mProgressDialog.show();
                OPMultiAppListSettings.this.mProgressDialog.setCancelable(false);
                OPMultiAppListSettings.this.mProgressDialog.setCanceledOnTouchOutside(false);
                OPMultiAppListSettings.this.mProgressDialog.setMessage(OPMultiAppListSettings.this.getString(C0017R$string.oneplus_multi_app_init));
                return OPMultiAppListSettings.this.mProgressDialog;
            }

            /* access modifiers changed from: protected */
            @Override // com.oneplus.loading.LoadingHelper
            public void hideProgree(Object obj) {
                try {
                    if (OPMultiAppListSettings.this.isFinishing()) {
                        return;
                    }
                    if (!OPMultiAppListSettings.this.isDestroyed()) {
                        if (OPMultiAppListSettings.this.mProgressDialog != null && OPMultiAppListSettings.this.mProgressDialog.isShowing()) {
                            OPMultiAppListSettings.this.mProgressDialog.dismiss();
                        }
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        };
        this.mOPApplicationLoader.setmLoadingContainer(this.mLoadingContainer);
        startLoadData();
    }

    private void startLoadData() {
        this.mOPApplicationLoader.loadSelectedGameOrReadAppMap(1005);
        this.mOPApplicationLoader.initData(3, this.mHandler);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        Log.d("OPMultiAppListSettings", "Oneplus multi app list item click:" + this.mIsInCreating);
        if (VibratorSceneUtils.systemVibrateEnabled(this)) {
            long[] vibratorScenePattern = VibratorSceneUtils.getVibratorScenePattern(this, this.mVibrator, 1003);
            this.mVibratePattern = vibratorScenePattern;
            VibratorSceneUtils.vibrateIfNeeded(vibratorScenePattern, this.mVibrator);
        }
        if (!this.mIsInCreating) {
            if (this.mManagedProfileOrUserInfo == null) {
                this.mIsInCreating = true;
                this.mInitPosition = i;
                this.mCreateManagedProfileTask = new CreateManagedProfileTask();
                this.mLoadingHelper.beginShowProgress();
                this.mCreateManagedProfileTask.execute(getString(C0017R$string.oneplus_multi_app));
                return;
            }
            refreshList(i, getModelWithPosition(i));
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private OPAppModel getModelWithPosition(int i) {
        return (OPAppModel) this.mAppListView.getItemAtPosition(i);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshList(int i) {
        refreshList(i, getModelWithPosition(i));
    }

    private void refreshList(int i, OPAppModel oPAppModel) {
        if (!this.mOPMultiAppAdapter.getSelected(i)) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            Message message = new Message();
            message.what = i + 88;
            message.arg1 = i;
            message.obj = progressDialog;
            this.mInstallMultiApphandler.sendMessage(message);
            return;
        }
        showWarnigDialog(i);
    }

    public String getEnabledString(OPAppModel oPAppModel) {
        return String.format(getString(C0017R$string.oneplus_multi_app_init_succeeded), oPAppModel.getLabel());
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void removeMultiAppByPosition(int i) {
        OPAppModel modelWithPosition = getModelWithPosition(i);
        this.mInstallMultiApphandler.removeMessages(i + 88);
        removeMultiApp(modelWithPosition.getPkgName());
        this.mOPMultiAppAdapter.setSelected(i, false);
        this.mAppOpsManager.setMode(1005, modelWithPosition.getUid(), modelWithPosition.getPkgName(), 1);
        OPUtils.notifyMultiPackageRemoved(this, modelWithPosition.getPkgName(), modelWithPosition.getUid(), true);
    }

    private void showWarnigDialog(final int i) {
        if (!isFinishing() && !isDestroyed()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(C0017R$string.oneplus_multi_app_disable_tips);
            builder.setPositiveButton(C0017R$string.okay, new DialogInterface.OnClickListener() {
                /* class com.oneplus.settings.multiapp.OPMultiAppListSettings.AnonymousClass6 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    OPMultiAppListSettings.this.mIsWarnDialogShowing = false;
                    OPMultiAppListSettings.this.removeMultiAppByPosition(i);
                }
            });
            builder.setNegativeButton(C0017R$string.cancel, new DialogInterface.OnClickListener() {
                /* class com.oneplus.settings.multiapp.OPMultiAppListSettings.AnonymousClass5 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    OPMultiAppListSettings.this.mIsWarnDialogShowing = false;
                }
            });
            AlertDialog create = builder.create();
            this.mWarnDialog = create;
            create.setCanceledOnTouchOutside(false);
            this.mWarnDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                /* class com.oneplus.settings.multiapp.OPMultiAppListSettings.AnonymousClass7 */

                public void onCancel(DialogInterface dialogInterface) {
                    OPMultiAppListSettings.this.mIsWarnDialogShowing = false;
                }
            });
            if (!this.mIsWarnDialogShowing) {
                this.mWarnDialog.show();
                this.mIsWarnDialogShowing = true;
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void installMultiApp(String str, OPAppModel oPAppModel) {
        Log.e("OPMultiAppListSettings", "installMultiApp" + str);
        UserInfo userInfo = this.mManagedProfileOrUserInfo;
        if (userInfo != null) {
            try {
                int installExistingPackageAsUser = this.mPackageManager.installExistingPackageAsUser(str, userInfo.id);
                if (installExistingPackageAsUser == -111) {
                    Log.e("OPMultiAppListSettings", "Could not install mobile device management app on managed profile because the user is restricted");
                } else if (installExistingPackageAsUser == -3) {
                    Log.e("OPMultiAppListSettings", "Could not install mobile device management app on managed profile because the package could not be found");
                } else if (installExistingPackageAsUser != 1) {
                    Log.e("OPMultiAppListSettings", "Could not install mobile device management app on managed profile. Unknown status: " + installExistingPackageAsUser);
                } else {
                    Log.e("OPMultiAppListSettings", "installMultiApp" + str + "success");
                    this.mAppOpsManager.setMode(1005, oPAppModel.getUid(), oPAppModel.getPkgName(), 0);
                    OPUtils.notifyMultiPackageRemoved(this, oPAppModel.getPkgName(), oPAppModel.getUid(), false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("OPMultiAppListSettings", "This should not happen.", e);
            }
        }
    }

    private void removeMultiApp(String str) {
        Log.e("OPMultiAppListSettings", "removeMultiApp ," + str);
        if (this.mManagedProfileOrUserInfo != null) {
            try {
                IPackageManager.Stub.asInterface(ServiceManager.getService("package")).deletePackageAsUser(str, -1, new PackageDeleteObserver(), this.mManagedProfileOrUserInfo.id, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
        private PackageDeleteObserver() {
        }

        public void packageDeleted(String str, int i) {
            Log.i("OPMultiAppListSettings", "PackageDeleteObserver ," + i + " " + str);
            ThreadUtils.postOnBackgroundThread(new Runnable(str) {
                /* class com.oneplus.settings.multiapp.$$Lambda$OPMultiAppListSettings$PackageDeleteObserver$q6s5mafV66dmN20pl1Ds96zV5Io */
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    OPMultiAppListSettings.PackageDeleteObserver.this.lambda$packageDeleted$0$OPMultiAppListSettings$PackageDeleteObserver(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$packageDeleted$0 */
        public /* synthetic */ void lambda$packageDeleted$0$OPMultiAppListSettings$PackageDeleteObserver(String str) {
            OPAppModel oPAppModel;
            Iterator it = OPMultiAppListSettings.this.mAppList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    oPAppModel = null;
                    break;
                }
                oPAppModel = (OPAppModel) it.next();
                if (oPAppModel.getPkgName().equals(str)) {
                    break;
                }
            }
            if (oPAppModel != null) {
                ShortcutUtil.removeShortcut(OPMultiAppListSettings.this.mContext, OPMultiAppListSettings.this.mContext.getString(C0017R$string.multi_app_label_badge, oPAppModel.getLabel()), oPAppModel.getPkgName(), oPAppModel.getUid() + 99900000, true);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void initFailed() {
        LoadingHelper loadingHelper = this.mLoadingHelper;
        if (loadingHelper != null) {
            loadingHelper.finishShowProgress(new LoadingHelper.FinishShowCallback() {
                /* class com.oneplus.settings.multiapp.OPMultiAppListSettings.AnonymousClass8 */

                @Override // com.oneplus.loading.LoadingHelper.FinishShowCallback
                public void finish(boolean z) {
                    List users = OPMultiAppListSettings.this.mUserManager.getUsers();
                    if (users == null || users.size() < 4) {
                        Toast.makeText(OPMultiAppListSettings.this.mContext, C0017R$string.oneplus_multi_app_init_failed, 0).show();
                    } else {
                        Toast.makeText(OPMultiAppListSettings.this.mContext, C0017R$string.oneplus_multi_app_init_failed_for_more_users, 0).show();
                    }
                }
            });
        }
    }

    private class CreateManagedProfileTask extends AsyncTask<String, Void, Void> {
        private CreateManagedProfileTask() {
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(String... strArr) {
            try {
                if (OPMultiAppListSettings.this.mUserManager.hasUserRestriction("no_add_user", UserHandle.OWNER)) {
                    OPMultiAppListSettings.this.mUserManager.setUserRestriction("no_add_user", false, UserHandle.OWNER);
                }
                OPMultiAppListSettings.this.mManagedProfileOrUserInfo = OPMultiAppListSettings.this.mUserManager.createProfileForUser(strArr[0], 67108960, Process.myUserHandle().getIdentifier());
                Log.d("OPMultiAppListSettings", "Oneplus ManagedProfileOrUserInfo:" + OPMultiAppListSettings.this.mManagedProfileOrUserInfo);
                if (OPMultiAppListSettings.this.mManagedProfileOrUserInfo != null) {
                    new OPDeleteNonRequiredAppsTask(OPMultiAppListSettings.this.mContext, OPMemberController.PACKAGE_NAME, 1, true, OPMultiAppListSettings.this.mManagedProfileOrUserInfo.id, false, new OPDeleteNonRequiredAppsTask.Callback() {
                        /* class com.oneplus.settings.multiapp.OPMultiAppListSettings.CreateManagedProfileTask.AnonymousClass1 */

                        @Override // com.oneplus.settings.multiapp.OPDeleteNonRequiredAppsTask.Callback
                        public void onSuccess() {
                            try {
                                OPMultiAppListSettings.this.setUpUserOrProfile();
                                Settings.Secure.putIntForUser(OPMultiAppListSettings.this.mContext.getContentResolver(), "lock_screen_allow_private_notifications", 1, 999);
                                Settings.Secure.putIntForUser(OPMultiAppListSettings.this.mContext.getContentResolver(), "lock_screen_show_notifications", 1, 999);
                                if (OPMultiAppListSettings.this.mLoadingHelper != null) {
                                    OPMultiAppListSettings.this.mLoadingHelper.finishShowProgress(new LoadingHelper.FinishShowCallback() {
                                        /* class com.oneplus.settings.multiapp.OPMultiAppListSettings.CreateManagedProfileTask.AnonymousClass1.AnonymousClass1 */

                                        @Override // com.oneplus.loading.LoadingHelper.FinishShowCallback
                                        public void finish(boolean z) {
                                            OPMultiAppListSettings oPMultiAppListSettings = OPMultiAppListSettings.this;
                                            oPMultiAppListSettings.refreshList(oPMultiAppListSettings.mInitPosition);
                                            Context context = OPMultiAppListSettings.this.mContext;
                                            OPMultiAppListSettings oPMultiAppListSettings2 = OPMultiAppListSettings.this;
                                            Toast.makeText(context, oPMultiAppListSettings2.getEnabledString(oPMultiAppListSettings2.getModelWithPosition(oPMultiAppListSettings2.mInitPosition)), 0).show();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                Log.e("OPMultiAppListSettings", "Provisioning failed", e);
                            }
                            Settings.Secure.putIntForUser(OPMultiAppListSettings.this.mContext.getContentResolver(), "user_setup_complete", 1, OPMultiAppListSettings.this.mManagedProfileOrUserInfo.id);
                        }

                        @Override // com.oneplus.settings.multiapp.OPDeleteNonRequiredAppsTask.Callback
                        public void onError() {
                            Log.e("OPMultiAppListSettings", "Delete non required apps task failed.", new Exception());
                            Log.e("OPMultiAppListSettings", "onCreate----createProfileForUser--onError");
                            OPMultiAppListSettings.this.initFailed();
                        }
                    }).run();
                    Log.e("OPMultiAppListSettings", "onCreate----doInBackground-finish");
                    OPMultiAppListSettings.this.mIsInCreating = false;
                    return null;
                }
                OPMultiAppListSettings.this.initFailed();
                return null;
            } catch (Exception e) {
                Log.e("OPMultiAppListSettings", "Exception" + e);
                return null;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Void r1) {
            super.onPostExecute((Object) r1);
        }
    }

    private UserInfo getCorpUserInfo(Context context) {
        UserInfo profileParent;
        int userHandle = this.mUserManager.getUserHandle();
        for (UserInfo userInfo : this.mUserManager.getUsers()) {
            int i = userInfo.id;
            if (i == 999 && (profileParent = this.mUserManager.getProfileParent(i)) != null && profileParent.id == userHandle) {
                return userInfo;
            }
        }
        return null;
    }

    private void enableProfile() {
        int i = this.mManagedProfileOrUserInfo.id;
        this.mUserManager.setUserName(i, getString(C0017R$string.oneplus_multi_app));
        this.mUserManager.setUserEnabled(i);
        UserInfo profileParent = this.mUserManager.getProfileParent(i);
        Intent intent = new Intent("android.intent.action.MANAGED_PROFILE_ADDED");
        intent.putExtra("android.intent.extra.USER", new UserHandle(i));
        intent.addFlags(1342177280);
        this.mContext.sendBroadcastAsUser(intent, new UserHandle(profileParent.id));
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setUpUserOrProfile() {
        enableProfile();
        try {
            ActivityManagerNative.getDefault().startUserInBackground(this.mManagedProfileOrUserInfo.id);
        } catch (RemoteException unused) {
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mPackageBroadcastReceiver);
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread != null) {
            handlerThread.quit();
        }
    }
}
