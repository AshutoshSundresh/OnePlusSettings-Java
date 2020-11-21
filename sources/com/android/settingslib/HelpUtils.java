package com.android.settingslib;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.android.settingslib.widget.R$drawable;
import com.android.settingslib.widget.R$string;
import java.net.URISyntaxException;
import java.util.Locale;

public class HelpUtils {
    static final int MENU_HELP = 101;
    private static final String TAG = "HelpUtils";
    private static String sCachedVersionCode;

    public static boolean prepareHelpMenuItem(Activity activity, Menu menu, String str, String str2) {
        if (menu.findItem(101) != null) {
            return false;
        }
        MenuItem add = menu.add(0, 101, 0, R$string.help_feedback_label);
        add.setIcon(R$drawable.ic_help_actionbar);
        return prepareHelpMenuItem(activity, add, str, str2);
    }

    public static boolean prepareHelpMenuItem(Activity activity, Menu menu, int i, String str) {
        if (menu.findItem(101) != null) {
            return false;
        }
        MenuItem add = menu.add(0, 101, 0, R$string.help_feedback_label);
        add.setIcon(R$drawable.ic_help_actionbar);
        return prepareHelpMenuItem(activity, add, activity.getString(i), str);
    }

    static boolean prepareHelpMenuItem(final Activity activity, MenuItem menuItem, String str, String str2) {
        if (Settings.Global.getInt(activity.getContentResolver(), "device_provisioned", 0) == 0) {
            return false;
        }
        if (TextUtils.isEmpty(str)) {
            menuItem.setVisible(false);
            return false;
        }
        final Intent helpIntent = getHelpIntent(activity, str, str2);
        if (helpIntent != null) {
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                /* class com.android.settingslib.HelpUtils.AnonymousClass1 */

                public boolean onMenuItemClick(MenuItem menuItem) {
                    try {
                        activity.startActivityForResult(helpIntent, 0);
                        return true;
                    } catch (ActivityNotFoundException unused) {
                        String str = HelpUtils.TAG;
                        Log.e(str, "No activity found for intent: " + helpIntent);
                        return true;
                    }
                }
            });
            menuItem.setShowAsAction(2);
            menuItem.setVisible(true);
            return true;
        }
        menuItem.setVisible(false);
        return false;
    }

    public static Intent getHelpIntent(Context context, String str, String str2) {
        if (Settings.Global.getInt(context.getContentResolver(), "device_provisioned", 0) == 0) {
            return null;
        }
        try {
            Intent parseUri = Intent.parseUri(str, 3);
            addIntentParameters(context, parseUri, str2, true);
            if (parseUri.resolveActivity(context.getPackageManager()) != null) {
                return parseUri;
            }
            if (parseUri.hasExtra("EXTRA_BACKUP_URI")) {
                return getHelpIntent(context, parseUri.getStringExtra("EXTRA_BACKUP_URI"), str2);
            }
            return null;
        } catch (URISyntaxException unused) {
            Intent intent = new Intent("android.intent.action.VIEW", uriWithAddedParameters(context, Uri.parse(str)));
            intent.setFlags(276824064);
            return intent;
        }
    }

    public static void addIntentParameters(Context context, Intent intent, String str, boolean z) {
        if (!intent.hasExtra("EXTRA_CONTEXT")) {
            intent.putExtra("EXTRA_CONTEXT", str);
        }
        Resources resources = context.getResources();
        boolean z2 = resources.getBoolean(17891328);
        if (z && z2) {
            String[] strArr = {resources.getString(17039387)};
            String[] strArr2 = {resources.getString(17039388)};
            String string = resources.getString(17039389);
            String string2 = resources.getString(17039390);
            String string3 = resources.getString(17039391);
            String string4 = resources.getString(17039392);
            intent.putExtra(string, strArr);
            intent.putExtra(string2, strArr2);
            intent.putExtra(string3, strArr);
            intent.putExtra(string4, strArr2);
        }
        intent.putExtra("EXTRA_THEME", 3);
    }

    private static Uri uriWithAddedParameters(Context context, Uri uri) {
        Uri.Builder buildUpon = uri.buildUpon();
        buildUpon.appendQueryParameter("hl", Locale.getDefault().toString());
        String str = sCachedVersionCode;
        if (str == null) {
            try {
                String l = Long.toString(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).getLongVersionCode());
                sCachedVersionCode = l;
                buildUpon.appendQueryParameter("version", l);
            } catch (PackageManager.NameNotFoundException e) {
                Log.wtf(TAG, "Invalid package name for context", e);
            }
        } else {
            buildUpon.appendQueryParameter("version", str);
        }
        return buildUpon.build();
    }
}
