package com.oneplus.security.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastUtil {
    private static Handler mHandler = new Handler();
    private static Toast mToast;
    private static Runnable r = new Runnable() {
        /* class com.oneplus.security.utils.ToastUtil.AnonymousClass1 */

        public void run() {
            ToastUtil.mToast.cancel();
            Toast unused = ToastUtil.mToast = null;
        }
    };

    public static void showShortToast(Context context, String str) {
        showToast(context, str, 1000);
    }

    public static void showLongToast(Context context, String str) {
        showToast(context, str, 3000);
    }

    private static void showToast(Context context, String str, int i) {
        mHandler.removeCallbacks(r);
        Toast toast = mToast;
        if (toast == null) {
            mToast = Toast.makeText(context, str, 0);
        } else {
            toast.setText(str);
        }
        mHandler.postDelayed(r, (long) i);
        mToast.show();
    }
}
