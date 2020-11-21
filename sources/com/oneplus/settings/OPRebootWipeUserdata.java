package com.oneplus.settings;

import android.content.Context;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.IStorageManager;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OPRebootWipeUserdata {
    private static final File COMMAND_FILE_OP2 = new File(RECOVERY_DIR_OP2, "command");
    private static final File LOG_FILE_OP2 = new File(RECOVERY_DIR_OP2, "log");
    private static final File RECOVERY_DIR = new File("/cache/recovery");
    private static final File RECOVERY_DIR_OP2 = new File("/mnt/vendor/op2/recovery");

    static {
        new File(RECOVERY_DIR, "command");
        new File(RECOVERY_DIR, "log");
    }

    public static void rebootWipeUserData(Context context, boolean z, String str, String str2, String str3) throws IOException {
        String str4;
        new ConditionVariable();
        String str5 = z ? "--shutdown_after" : null;
        if (!TextUtils.isEmpty(str)) {
            str4 = "--reason=" + sanitizeArg(str);
        } else {
            str4 = null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        bootCommand(context, str5, str2, str4, "--locale=" + Locale.getDefault().toString(), "--password=" + str3, "--resetTime=" + simpleDateFormat.format(new Date(System.currentTimeMillis())), "--resetPackage=com.android.settings");
        throw null;
    }

    private static String sanitizeArg(String str) {
        return str.replace((char) 0, '?').replace('\n', '?');
    }

    /* JADX INFO: finally extract failed */
    private static void bootCommand(Context context, String... strArr) throws IOException {
        Log.d("OPRebootWipeUserdata", "bootCommand start");
        RECOVERY_DIR_OP2.mkdirs();
        COMMAND_FILE_OP2.delete();
        LOG_FILE_OP2.delete();
        FileWriter fileWriter = new FileWriter(COMMAND_FILE_OP2);
        try {
            for (String str : strArr) {
                if (!TextUtils.isEmpty(str)) {
                    fileWriter.write(str);
                    fileWriter.write("\n");
                }
            }
            fileWriter.close();
            try {
                IBinder service = ServiceManager.getService("mount");
                Log.d("OPRebootWipeUserdata", "bootCommand get mount Service");
                IStorageManager asInterface = IStorageManager.Stub.asInterface(service);
                asInterface.setField("SystemLocale", "");
                Log.d("OPRebootWipeUserdata", "bootCommand setField StorageManager.SYSTEM_LOCALE_KEY");
                asInterface.setField("PatternVisible", "");
                Log.d("OPRebootWipeUserdata", "bootCommand setField StorageManager.PATTERN_VISIBLE_KEY");
                asInterface.setField("PasswordVisible", "");
                Log.d("OPRebootWipeUserdata", "bootCommand setField StorageManager.PASSWORD_VISIBLE_KEY");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            ((PowerManager) context.getSystemService("power")).reboot("recovery");
            throw new IOException("Reboot failed (no permissions?)");
        } catch (Throwable th) {
            fileWriter.close();
            throw th;
        }
    }
}
