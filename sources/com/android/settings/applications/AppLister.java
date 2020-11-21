package com.android.settings.applications;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.os.UserManager;
import java.util.ArrayList;
import java.util.List;

public abstract class AppLister extends AsyncTask<Void, Void, List<UserAppInfo>> {
    protected final PackageManager mPm;
    protected final UserManager mUm;

    /* access modifiers changed from: protected */
    public abstract boolean includeInCount(ApplicationInfo applicationInfo);

    /* access modifiers changed from: protected */
    public abstract void onAppListBuilt(List<UserAppInfo> list);

    public AppLister(PackageManager packageManager, UserManager userManager) {
        this.mPm = packageManager;
        this.mUm = userManager;
    }

    /* access modifiers changed from: protected */
    public List<UserAppInfo> doInBackground(Void... voidArr) {
        ArrayList arrayList = new ArrayList();
        for (UserInfo userInfo : this.mUm.getProfiles(UserHandle.myUserId())) {
            for (ApplicationInfo applicationInfo : this.mPm.getInstalledApplicationsAsUser(33280 | (userInfo.isAdmin() ? 4194304 : 0), userInfo.id)) {
                if (includeInCount(applicationInfo)) {
                    arrayList.add(new UserAppInfo(userInfo, applicationInfo));
                }
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(List<UserAppInfo> list) {
        onAppListBuilt(list);
    }
}
