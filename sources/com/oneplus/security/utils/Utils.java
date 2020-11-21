package com.oneplus.security.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.format.DateUtils;
import android.widget.EditText;
import com.android.settings.C0017R$string;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPUtils;
import java.util.Collection;
import java.util.Formatter;
import java.util.Locale;

public class Utils {
    private static final StringBuilder sBuilder = new StringBuilder(50);
    private static final Formatter sFormatter = new Formatter(sBuilder, Locale.getDefault());

    public static String[] getFormattedFileSizeAndUnitForDisplay(Context context, long j, boolean z, boolean z2) {
        String str;
        String[] strArr = new String[2];
        String str2 = j < 0 ? "-" : "";
        float abs = (float) Math.abs(j);
        int i = C0017R$string.byteShort;
        if (abs > 900.0f) {
            i = C0017R$string.kilobyteShort;
            abs /= 1024.0f;
        }
        if (abs > 900.0f) {
            i = C0017R$string.megabyteShort;
            abs /= 1024.0f;
        }
        if (abs > 900.0f) {
            i = C0017R$string.gigabyteShort;
            abs /= 1024.0f;
        }
        if (abs > 900.0f) {
            i = C0017R$string.terabyteShort;
            abs /= 1024.0f;
        }
        if (abs > 900.0f) {
            i = C0017R$string.petabyteShort;
            abs /= 1024.0f;
        }
        if (abs < 1.0f) {
            if (i == C0017R$string.byteShort) {
                i = C0017R$string.megabyteShort;
            }
            str = String.format("%.2f", Float.valueOf(abs));
        } else if (abs < 10.0f) {
            if (z) {
                str = String.format("%.2f", Float.valueOf(abs));
            } else {
                str = String.format("%.2f", Float.valueOf(abs));
            }
        } else if (abs < 100.0f) {
            if ("-".equals(str2) && z) {
                str = String.format("%.1f", Float.valueOf(abs));
            } else if (z) {
                str = String.format("%.2f", Float.valueOf(abs));
            } else {
                str = String.format("%.2f", Float.valueOf(abs));
            }
        } else if (abs >= 10000.0f) {
            str = String.format("%.0f", Float.valueOf(abs));
        } else if (z) {
            str = String.format("%.0f", Float.valueOf(abs));
        } else {
            str = String.format("%.2f", Float.valueOf(abs));
        }
        strArr[0] = str2 + str;
        strArr[1] = SettingsBaseApplication.getContext().getString(i);
        return strArr;
    }

    public static boolean isCollectionEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    public static void setEditTextAtLastLocation(EditText editText) {
        Editable text = editText.getText();
        if (text instanceof Spannable) {
            Selection.setSelection(text, text.length());
        }
    }

    public static boolean currentUserIsOwner() {
        return UserHandle.myUserId() == 0;
    }

    private static Intent onBuildStartFragmentIntent(Context context, String str, Bundle bundle, String str2, int i, CharSequence charSequence, boolean z) {
        Intent intent = new Intent();
        intent.setClassName(OPMemberController.PACKAGE_NAME, "com.android.settings.SubSettings");
        intent.putExtra(":settings:show_fragment", str);
        intent.putExtra(":settings:show_fragment_args", bundle);
        intent.putExtra(":settings:show_fragment_title_res_package_name", str2);
        intent.putExtra(":settings:show_fragment_title_resid", i);
        intent.putExtra(":settings:show_fragment_title", charSequence);
        intent.putExtra(":settings:show_fragment_as_shortcut", z);
        return intent;
    }

    public static void startSettingsAppFragment(Context context, String str, Bundle bundle, int i, CharSequence charSequence, int i2) {
        context.startActivity(onBuildStartFragmentIntent(context, str, bundle, null, i, i < 0 ? charSequence != null ? charSequence.toString() : "" : null, false));
    }

    public static boolean isSystemApp(PackageInfo packageInfo) {
        return (packageInfo.applicationInfo.flags & 1) > 0;
    }

    public static boolean isSystemApp(ResolveInfo resolveInfo) {
        return (resolveInfo.activityInfo.applicationInfo.flags & 1) > 0;
    }

    public static void sendAppTracker(String str, int i) {
        OPUtils.sendAppTracker(str, i);
    }

    public static boolean hasSDK24() {
        return Build.VERSION.SDK_INT >= 24;
    }

    public static boolean hasSDK27() {
        return Build.VERSION.SDK_INT >= 27;
    }

    public static boolean hasSDK28() {
        return Build.VERSION.SDK_INT >= 28;
    }

    public static boolean issSDKAbove28() {
        return Build.VERSION.SDK_INT > 28;
    }

    public static String formatDateRange(Context context, long j, long j2) {
        String formatter;
        StringBuilder sb = sBuilder;
        synchronized (sb) {
            sb.setLength(0);
            formatter = DateUtils.formatDateRange(context, sFormatter, j, j2, 65552, null).toString();
        }
        return formatter;
    }

    public static boolean isIntentReceiverExists(Context context, Intent intent) {
        return context.getPackageManager().resolveActivity(intent, 65536) != null;
    }
}
