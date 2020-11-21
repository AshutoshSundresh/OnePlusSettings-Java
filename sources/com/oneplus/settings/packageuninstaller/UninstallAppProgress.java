package com.oneplus.settings.packageuninstaller;

import android.app.Activity;
import android.app.admin.IDevicePolicyManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageDeleteObserver2;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.widget.Toast;
import com.android.settings.C0017R$string;
import com.oneplus.settings.OPMemberController;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

public class UninstallAppProgress extends Activity {
    private boolean mAllUsers;
    private ApplicationInfo mAppInfo;
    private IBinder mCallback;
    private Handler mHandler = new MessageHandler(this);
    private boolean mIsViewInitialized;
    private volatile int mResultCode = -1;

    public interface ProgressFragment {
        void setDeviceManagerButtonVisible(boolean z);

        void setUsersButtonVisible(boolean z);

        void showCompletion(CharSequence charSequence);
    }

    private static class MessageHandler extends Handler {
        private final WeakReference<UninstallAppProgress> mActivity;

        public MessageHandler(UninstallAppProgress uninstallAppProgress) {
            this.mActivity = new WeakReference<>(uninstallAppProgress);
        }

        public void handleMessage(Message message) {
            UninstallAppProgress uninstallAppProgress = this.mActivity.get();
            if (uninstallAppProgress != null) {
                uninstallAppProgress.handleMessage(message);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleMessage(Message message) {
        String str;
        int i;
        if (!isFinishing() && !isDestroyed()) {
            int i2 = message.what;
            int i3 = 1;
            if (i2 == 1) {
                this.mHandler.removeMessages(2);
                if (message.arg1 != 1) {
                    initView();
                }
                this.mResultCode = message.arg1;
                String str2 = (String) message.obj;
                IBinder iBinder = this.mCallback;
                if (iBinder != null) {
                    try {
                        IPackageDeleteObserver2.Stub.asInterface(iBinder).onPackageDeleted(this.mAppInfo.packageName, this.mResultCode, str2);
                    } catch (RemoteException unused) {
                    }
                    finish();
                } else if (getIntent().getBooleanExtra("android.intent.extra.RETURN_RESULT", false)) {
                    Intent intent = new Intent();
                    intent.putExtra("android.intent.extra.INSTALL_RESULT", this.mResultCode);
                    if (this.mResultCode == 1) {
                        i3 = -1;
                    }
                    setResult(i3, intent);
                    finish();
                } else {
                    int i4 = message.arg1;
                    if (i4 == -4) {
                        UserManager userManager = (UserManager) getSystemService("user");
                        IPackageManager asInterface = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
                        List users = userManager.getUsers();
                        int i5 = 0;
                        while (true) {
                            if (i5 >= users.size()) {
                                i = -10000;
                                break;
                            }
                            UserInfo userInfo = (UserInfo) users.get(i5);
                            try {
                                if (asInterface.getBlockUninstallForUser(str2, userInfo.id)) {
                                    i = userInfo.id;
                                    break;
                                }
                                i5++;
                            } catch (RemoteException e) {
                                Log.e("UninstallAppProgress", "Failed to talk to package manager", e);
                            }
                        }
                        if (isProfileOfOrSame(userManager, UserHandle.myUserId(), i)) {
                            getProgressFragment().setDeviceManagerButtonVisible(true);
                        } else {
                            getProgressFragment().setDeviceManagerButtonVisible(false);
                            getProgressFragment().setUsersButtonVisible(true);
                        }
                        if (i == 0) {
                            str = getString(C0017R$string.uninstall_blocked_device_owner);
                        } else if (i == -10000) {
                            Log.d("UninstallAppProgress", "Uninstall failed for " + str2 + " with code " + message.arg1 + " no blocking user");
                            str = getString(C0017R$string.uninstall_failed);
                        } else if (this.mAllUsers) {
                            str = getString(C0017R$string.uninstall_all_blocked_profile_owner);
                        } else {
                            str = getString(C0017R$string.uninstall_blocked_profile_owner);
                        }
                    } else if (i4 == -2) {
                        UserManager userManager2 = (UserManager) getSystemService("user");
                        IDevicePolicyManager asInterface2 = IDevicePolicyManager.Stub.asInterface(ServiceManager.getService("device_policy"));
                        int myUserId = UserHandle.myUserId();
                        UserInfo userInfo2 = null;
                        Iterator it = userManager2.getUsers().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            UserInfo userInfo3 = (UserInfo) it.next();
                            if (!isProfileOfOrSame(userManager2, myUserId, userInfo3.id)) {
                                try {
                                    if (asInterface2.packageHasActiveAdmins(str2, userInfo3.id)) {
                                        userInfo2 = userInfo3;
                                        break;
                                    }
                                } catch (RemoteException e2) {
                                    Log.e("UninstallAppProgress", "Failed to talk to package manager", e2);
                                }
                            }
                        }
                        if (userInfo2 == null) {
                            Log.d("UninstallAppProgress", "Uninstall failed because " + str2 + " is a device admin");
                            getProgressFragment().setDeviceManagerButtonVisible(true);
                            str = getString(C0017R$string.uninstall_failed_device_policy_manager);
                        } else {
                            Log.d("UninstallAppProgress", "Uninstall failed because " + str2 + " is a device admin of user " + userInfo2);
                            getProgressFragment().setDeviceManagerButtonVisible(false);
                            str = String.format(getString(C0017R$string.uninstall_failed_device_policy_manager_of_user), userInfo2.name);
                        }
                    } else if (i4 != 1) {
                        Log.d("UninstallAppProgress", "Uninstall failed for " + str2 + " with code " + message.arg1);
                        str = getString(C0017R$string.uninstall_failed);
                    } else {
                        String string = getString(C0017R$string.uninstall_done);
                        notifyPackageRemoved();
                        Toast.makeText(getBaseContext(), string, 1).show();
                        setResultAndFinish();
                        return;
                    }
                    getProgressFragment().showCompletion(str);
                }
            } else if (i2 == 2) {
                initView();
            }
        }
    }

    private boolean isProfileOfOrSame(UserManager userManager, int i, int i2) {
        if (i == i2) {
            return true;
        }
        UserInfo profileParent = userManager.getProfileParent(i2);
        if (profileParent == null || profileParent.id != i) {
            return false;
        }
        return true;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        this.mAppInfo = (ApplicationInfo) intent.getParcelableExtra("com.android.packageinstaller.applicationInfo");
        this.mCallback = intent.getIBinderExtra("android.content.pm.extra.CALLBACK");
        if (bundle != null) {
            this.mResultCode = -1;
            IBinder iBinder = this.mCallback;
            if (iBinder != null) {
                try {
                    IPackageDeleteObserver2.Stub.asInterface(iBinder).onPackageDeleted(this.mAppInfo.packageName, this.mResultCode, (String) null);
                } catch (RemoteException unused) {
                }
                finish();
                return;
            }
            setResultAndFinish();
            return;
        }
        int i = 0;
        this.mAllUsers = intent.getBooleanExtra("android.intent.extra.UNINSTALL_ALL_USERS", false);
        UserHandle userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
        if (userHandle == null) {
            userHandle = Process.myUserHandle();
        }
        PackageDeleteObserver packageDeleteObserver = new PackageDeleteObserver();
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getWindow().setStatusBarColor(0);
        getWindow().setNavigationBarColor(0);
        try {
            PackageManager packageManager = getPackageManager();
            String str = this.mAppInfo.packageName;
            if (this.mAllUsers) {
                i = 2;
            }
            packageManager.deletePackageAsUser(str, packageDeleteObserver, i, userHandle.getIdentifier());
        } catch (IllegalArgumentException e) {
            Log.w("UninstallAppProgress", "Could not find package, not deleting " + this.mAppInfo.packageName, e);
        }
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(2), 500);
    }

    public ApplicationInfo getAppInfo() {
        return this.mAppInfo;
    }

    private class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
        private PackageDeleteObserver() {
        }

        public void packageDeleted(String str, int i) {
            Message obtainMessage = UninstallAppProgress.this.mHandler.obtainMessage(1);
            obtainMessage.arg1 = i;
            obtainMessage.obj = str;
            UninstallAppProgress.this.mHandler.sendMessage(obtainMessage);
        }
    }

    public void setResultAndFinish() {
        setResult(this.mResultCode);
        finish();
    }

    private void initView() {
        if (!this.mIsViewInitialized) {
            boolean z = true;
            this.mIsViewInitialized = true;
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(16842836, typedValue, true);
            int i = typedValue.type;
            if (i < 28 || i > 31) {
                getWindow().setBackgroundDrawable(getResources().getDrawable(typedValue.resourceId, getTheme()));
            } else {
                getWindow().setBackgroundDrawable(new ColorDrawable(typedValue.data));
            }
            getTheme().resolveAttribute(16843858, typedValue, true);
            getWindow().setNavigationBarColor(typedValue.data);
            getTheme().resolveAttribute(16843857, typedValue, true);
            getWindow().setStatusBarColor(typedValue.data);
            if ((this.mAppInfo.flags & 128) == 0) {
                z = false;
            }
            setTitle(z ? C0017R$string.uninstall_update_title : C0017R$string.uninstall_application_title);
            getFragmentManager().beginTransaction().add(16908290, new UninstallAppProgressFragment(), "progress_fragment").commitNowAllowingStateLoss();
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4) {
            if (this.mResultCode == -1) {
                return true;
            }
            setResult(this.mResultCode);
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    private void notifyPackageRemoved() {
        Intent intent = new Intent("oneplus.settings.intent.action.PACKAGE_REMOVED");
        intent.setFlags(285212672);
        intent.setPackage(OPMemberController.PACKAGE_NAME);
        sendBroadcast(intent);
    }

    private ProgressFragment getProgressFragment() {
        return (ProgressFragment) getFragmentManager().findFragmentByTag("progress_fragment");
    }
}
