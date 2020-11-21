package com.android.settings.applications;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.os.UserManager;

public abstract class AppCounter extends AsyncTask<Void, Void, Integer> {
    protected final PackageManager mPm;
    protected final UserManager mUm;

    /* access modifiers changed from: protected */
    public abstract boolean includeInCount(ApplicationInfo applicationInfo);

    /* access modifiers changed from: protected */
    public abstract void onCountComplete(int i);

    public AppCounter(Context context, PackageManager packageManager) {
        this.mPm = packageManager;
        this.mUm = (UserManager) context.getSystemService("user");
    }

    /* access modifiers changed from: protected */
    public Integer doInBackground(Void... voidArr) {
        int i = 0;
        for (UserInfo userInfo : this.mUm.getProfiles(UserHandle.myUserId())) {
            for (ApplicationInfo applicationInfo : this.mPm.getInstalledApplicationsAsUser(33280 | (userInfo.isAdmin() ? 4194304 : 0), userInfo.id)) {
                if (includeInCount(applicationInfo)) {
                    i++;
                }
            }
        }
        return Integer.valueOf(i);
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(Integer num) {
        onCountComplete(num.intValue());
    }

    /* access modifiers changed from: package-private */
    public void executeInForeground() {
        onPostExecute(doInBackground(new Void[0]));
    }
}
