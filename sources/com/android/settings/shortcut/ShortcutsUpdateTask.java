package com.android.settings.shortcut;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.AsyncTask;
import java.util.ArrayList;

public class ShortcutsUpdateTask extends AsyncTask<Void, Void, Void> {
    private final Context mContext;

    public ShortcutsUpdateTask(Context context) {
        this.mContext = context;
    }

    public Void doInBackground(Void... voidArr) {
        ShortcutManager shortcutManager = (ShortcutManager) this.mContext.getSystemService(ShortcutManager.class);
        PackageManager packageManager = this.mContext.getPackageManager();
        ArrayList arrayList = new ArrayList();
        for (ShortcutInfo shortcutInfo : shortcutManager.getPinnedShortcuts()) {
            if (shortcutInfo.getId().startsWith("component-shortcut-")) {
                ResolveInfo resolveActivity = packageManager.resolveActivity(new Intent(CreateShortcutPreferenceController.SHORTCUT_PROBE).setComponent(ComponentName.unflattenFromString(shortcutInfo.getId().substring(19))), 0);
                if (resolveActivity != null) {
                    arrayList.add(new ShortcutInfo.Builder(this.mContext, shortcutInfo.getId()).setShortLabel(resolveActivity.loadLabel(packageManager)).build());
                }
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        shortcutManager.updateShortcuts(arrayList);
        return null;
    }
}
