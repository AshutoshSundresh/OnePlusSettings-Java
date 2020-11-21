package com.android.settings.wifi.tether.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.android.settings.wifi.tether.utils.TetherUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

public class TetherUtils {

    public interface OnDialogConfirmCallback {
        void onConfirm();
    }

    public static int getTetherData(Context context) {
        int i = Settings.Global.getInt(context.getContentResolver(), "TetheredData", 3);
        if (i > 3 || i < 1) {
            return 3;
        }
        return i;
    }

    public static boolean isNoSimCard(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService("phone");
        if (telephonyManager == null) {
            return false;
        }
        int simState = telephonyManager.getSimState();
        return simState == 1 || simState == 0 || simState == 8 || simState == 6;
    }

    public static boolean isHaveProfile(Context context) {
        if (!isSprintMccMnc(context)) {
            return true;
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager == null) {
            return false;
        }
        String simOperator = telephonyManager.getSimOperator();
        if (TextUtils.isEmpty(simOperator)) {
            return false;
        }
        Cursor query = context.getContentResolver().query(Telephony.Carriers.CONTENT_URI, new String[]{"apn"}, "type = ? and numeric = ? and user_visible != ? and name != ?", new String[]{"dun", simOperator, "0", "3G_HOT"}, null);
        if (query == null || query.getCount() <= 0 || !query.moveToFirst()) {
            closeCursor(query);
            return false;
        }
        closeCursor(query);
        return true;
    }

    public static void showTertheringErrorDialog(Context context, String str, String str2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(str);
        builder.setMessage(str2);
        builder.setPositiveButton(C0017R$string.okay, new DialogInterface.OnClickListener() {
            /* class com.android.settings.wifi.tether.utils.TetherUtils.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public static boolean isSimStatusChange(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getSimState() == 5;
    }

    public static boolean isSprintMccMnc(Context context) {
        String mccMnc = getMccMnc(context);
        if (TextUtils.isEmpty(mccMnc)) {
            return false;
        }
        if (mccMnc.equals("310120") || mccMnc.equals("311870") || mccMnc.equals("311490") || mccMnc.equals("312530") || mccMnc.equals("310000")) {
            return true;
        }
        return false;
    }

    public static String getMccMnc(Context context) {
        TelephonyManager telephonyManager;
        if (context == null || (telephonyManager = (TelephonyManager) context.getSystemService("phone")) == null) {
            return null;
        }
        String subscriberId = telephonyManager.getSubscriberId();
        if (TextUtils.isEmpty(subscriberId) || subscriberId.length() <= 6) {
            return null;
        }
        return subscriberId.substring(0, 6);
    }

    private static void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    public static void startUstTethering(Context context, OnDialogConfirmCallback onDialogConfirmCallback) {
        Log.d("TetherUtils", "startUstTethering");
        setUstWifiTetheringStatus(context, 1);
        if (isNeedShowDialog(context)) {
            showUstAlertDialog(context, onDialogConfirmCallback, true);
            return;
        }
        setTetherState(context, true);
        onDialogConfirmCallback.onConfirm();
    }

    public static void openUstWifi(Context context, OnDialogConfirmCallback onDialogConfirmCallback) {
        Log.d("TetherUtils", "openUstWifi");
        if (isNeedShowDialog(context)) {
            showUstAlertDialog(context, onDialogConfirmCallback, false);
            return;
        }
        stopUstTethering(context);
        onDialogConfirmCallback.onConfirm();
    }

    private static void showUstAlertDialog(Context context, OnDialogConfirmCallback onDialogConfirmCallback, boolean z) {
        if (context != null && (context instanceof Activity)) {
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(84869377);
            builder.setPositiveButton(17039370, new DialogInterface.OnClickListener(z, context, atomicBoolean) {
                /* class com.android.settings.wifi.tether.utils.$$Lambda$TetherUtils$IKOKaRA4bryDn22PlVB4mW7qVS4 */
                public final /* synthetic */ boolean f$1;
                public final /* synthetic */ Context f$2;
                public final /* synthetic */ AtomicBoolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    TetherUtils.lambda$showUstAlertDialog$0(TetherUtils.OnDialogConfirmCallback.this, this.f$1, this.f$2, this.f$3, dialogInterface, i);
                }
            });
            builder.setCancelable(false);
            AlertDialog create = builder.create();
            View inflate = LayoutInflater.from(create.getContext()).inflate(84606979, (ViewGroup) null);
            ViewGroup viewGroup = (ViewGroup) inflate.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(inflate);
            }
            ((TextView) inflate.findViewById(84410420)).setText(context.getResources().getString(84869376));
            CheckBox checkBox = (CheckBox) inflate.findViewById(84410658);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(atomicBoolean) {
                /* class com.android.settings.wifi.tether.utils.$$Lambda$TetherUtils$utGM3o4y0PBGDd4W70zOcMfdXfc */
                public final /* synthetic */ AtomicBoolean f$0;

                {
                    this.f$0 = r1;
                }

                public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    TetherUtils.lambda$showUstAlertDialog$1(this.f$0, compoundButton, z);
                }
            });
            checkBox.setChecked(false);
            checkBox.setText(context.getResources().getString(84869375));
            create.setView(inflate);
            create.setCanceledOnTouchOutside(false);
            create.show();
        }
    }

    static /* synthetic */ void lambda$showUstAlertDialog$0(OnDialogConfirmCallback onDialogConfirmCallback, boolean z, Context context, AtomicBoolean atomicBoolean, DialogInterface dialogInterface, int i) {
        if (onDialogConfirmCallback != null) {
            Log.d("TetherUtils", "showUstAlertDialog isTethering = " + z);
            onDialogConfirmCallback.onConfirm();
            if (z) {
                setTetherState(context, true);
            } else {
                stopUstTethering(context);
            }
            setDialogNotShowAgain(context, atomicBoolean.get());
        }
    }

    static /* synthetic */ void lambda$showUstAlertDialog$1(AtomicBoolean atomicBoolean, CompoundButton compoundButton, boolean z) {
        atomicBoolean.set(z);
        Log.d("TetherUtils", "onCheckedChanged isChecked = " + z);
    }

    private static void stopUstTethering(Context context) {
        setTetherEnabled(context);
        stopUsbTethering(context);
        setUstWifiTetheringStatus(context, 0);
    }

    public static boolean isWifiEnable(Context context) {
        if (context == null) {
            return false;
        }
        return ((WifiManager) context.getApplicationContext().getSystemService("wifi")).isWifiEnabled();
    }

    public static void setTetherState(Context context, boolean z) {
        WifiManager wifiManager;
        if (context != null && (wifiManager = (WifiManager) context.getApplicationContext().getSystemService("wifi")) != null) {
            try {
                Method declaredMethod = wifiManager.getClass().getDeclaredMethod("setTetherState", Integer.TYPE, Boolean.TYPE);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(wifiManager, 0, Boolean.valueOf(z));
                Log.d("TetherUtils", "setTetherState state = " + z);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (InvocationTargetException e3) {
                e3.printStackTrace();
            } catch (RuntimeException e4) {
                e4.printStackTrace();
            }
        }
    }

    public static boolean isTetheringOpen(Context context) {
        if (context == null) {
            return false;
        }
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService("wifi");
        try {
            Method declaredMethod = wifiManager.getClass().getDeclaredMethod("isWifiApEnabled", new Class[0]);
            declaredMethod.setAccessible(true);
            boolean booleanValue = ((Boolean) declaredMethod.invoke(wifiManager, new Object[0])).booleanValue();
            Method declaredMethod2 = wifiManager.getClass().getDeclaredMethod("getUsbTetherEnabled", new Class[0]);
            declaredMethod2.setAccessible(true);
            boolean booleanValue2 = ((Boolean) declaredMethod2.invoke(wifiManager, new Object[0])).booleanValue();
            Log.d("TetherUtils", "isTetheringOpen isWifiApEnabled = " + booleanValue + ", isUsbTetherEnabled = " + booleanValue2);
            if (booleanValue || booleanValue2) {
                return true;
            }
            return false;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            return false;
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
            return false;
        }
    }

    private static void setTetherEnabled(Context context) {
        WifiManager wifiManager;
        if (context != null && (wifiManager = (WifiManager) context.getApplicationContext().getSystemService("wifi")) != null) {
            try {
                wifiManager.getClass().getDeclaredMethod("setTetherEnabled", Boolean.TYPE).invoke(wifiManager, Boolean.FALSE);
                Log.d("TetherUtils", "setTetherEnabled false");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (InvocationTargetException e3) {
                e3.printStackTrace();
            }
        }
    }

    private static void setDialogNotShowAgain(Context context, boolean z) {
        if (context != null) {
            Settings.System.putInt(context.getApplicationContext().getContentResolver(), "tether_checkbox_not_show_again", z ? 1 : 0);
            Log.d("TetherUtils", "setDialogNotShowAgain notShowAgain =" + z);
        }
    }

    public static boolean isNeedShowDialog(Context context) {
        boolean z = true;
        if (context != null) {
            if (Settings.System.getInt(context.getApplicationContext().getContentResolver(), "tether_checkbox_not_show_again", 0) != 0) {
                z = false;
            }
            Log.d("TetherUtils", "isNeedShowDialog = " + z);
        }
        return z;
    }

    public static boolean getUstWifiTetheringStatus(Context context) {
        boolean z = false;
        if (context != null) {
            if (Settings.Global.getInt(context.getApplicationContext().getContentResolver(), "start_ust_tethering_wifi", 0) == 1) {
                z = true;
            }
            Log.d("TetherUtils", "getUstWifiTetheringStatus: status = " + z);
        }
        return z;
    }

    public static void setUstWifiTetheringStatus(Context context, int i) {
        if (context != null) {
            Settings.Global.putInt(context.getApplicationContext().getContentResolver(), "start_ust_tethering_wifi", i);
        }
    }

    private static void stopUsbTethering(Context context) {
        ConnectivityManager connectivityManager;
        Log.d("TetherUtils", "stopUsbTethering");
        if (context != null && (connectivityManager = (ConnectivityManager) context.getSystemService("connectivity")) != null) {
            connectivityManager.stopTethering(1);
        }
    }
}
