package com.android.settings.deviceinfo.storage;

import android.content.Context;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.UserManager;
import android.util.SparseArray;
import com.android.internal.util.Preconditions;
import com.android.settingslib.Utils;
import com.android.settingslib.utils.AsyncLoaderCompat;

public class UserIconLoader extends AsyncLoaderCompat<SparseArray<Drawable>> {
    private FetchUserIconTask mTask;

    public interface FetchUserIconTask {
        SparseArray<Drawable> getUserIcons();
    }

    public interface UserIconHandler {
        void handleUserIcons(SparseArray<Drawable> sparseArray);
    }

    /* access modifiers changed from: protected */
    public void onDiscardResult(SparseArray<Drawable> sparseArray) {
    }

    public UserIconLoader(Context context, FetchUserIconTask fetchUserIconTask) {
        super(context);
        this.mTask = (FetchUserIconTask) Preconditions.checkNotNull(fetchUserIconTask);
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public SparseArray<Drawable> loadInBackground() {
        return this.mTask.getUserIcons();
    }

    public static SparseArray<Drawable> loadUserIconsWithContext(Context context) {
        SparseArray<Drawable> sparseArray = new SparseArray<>();
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        for (UserInfo userInfo : userManager.getUsers()) {
            sparseArray.put(userInfo.id, Utils.getUserIcon(context, userManager, userInfo));
        }
        return sparseArray;
    }
}
