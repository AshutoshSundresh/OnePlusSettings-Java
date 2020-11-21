package com.android.settings.display;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.android.settings.Settings;

public class TwilightLocationDialog {
    public static String TAG = "TwilightLocationDialog";

    public static void show(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(C0017R$string.twilight_mode_launch_location, new DialogInterface.OnClickListener(context) {
            /* class com.android.settings.display.$$Lambda$TwilightLocationDialog$smfHub8jJ_Oc8G55KRbswCoIHfk */
            public final /* synthetic */ Context f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                TwilightLocationDialog.lambda$show$0(this.f$0, dialogInterface, i);
            }
        });
        builder.setNegativeButton(C0017R$string.cancel, (DialogInterface.OnClickListener) null);
        builder.setMessage(C0017R$string.twilight_mode_location_off_dialog_message);
        builder.create().show();
    }

    static /* synthetic */ void lambda$show$0(Context context, DialogInterface dialogInterface, int i) {
        Log.d(TAG, "clicked forget");
        Intent intent = new Intent();
        intent.setClass(context, Settings.LocationSettingsActivity.class);
        context.startActivity(intent);
    }
}
