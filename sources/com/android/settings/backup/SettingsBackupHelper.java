package com.android.settings.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInputStream;
import android.app.backup.BackupDataOutput;
import android.app.backup.BackupHelper;
import android.os.ParcelFileDescriptor;
import com.android.settings.shortcut.CreateShortcutPreferenceController;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsBackupHelper extends BackupAgentHelper {
    public void onCreate() {
        super.onCreate();
        addHelper("no-op", new NoOpHelper());
    }

    public void onRestoreFinished() {
        super.onRestoreFinished();
        CreateShortcutPreferenceController.updateRestoredShortcuts(this);
    }

    private static class NoOpHelper implements BackupHelper {
        public void restoreEntity(BackupDataInputStream backupDataInputStream) {
        }

        public void writeNewStateDescription(ParcelFileDescriptor parcelFileDescriptor) {
        }

        private NoOpHelper() {
        }

        public void performBackup(ParcelFileDescriptor parcelFileDescriptor, BackupDataOutput backupDataOutput, ParcelFileDescriptor parcelFileDescriptor2) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor2.getFileDescriptor());
                try {
                    if (getVersionCode(parcelFileDescriptor) != 1) {
                        backupDataOutput.writeEntityHeader("dummy", 1);
                        backupDataOutput.writeEntityData(new byte[1], 1);
                    }
                    fileOutputStream.write(1);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    return;
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
                throw th;
            } catch (IOException unused) {
            }
        }

        private int getVersionCode(ParcelFileDescriptor parcelFileDescriptor) {
            if (parcelFileDescriptor == null) {
                return 0;
            }
            try {
                FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                try {
                    int read = fileInputStream.read();
                    fileInputStream.close();
                    return read;
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
            } catch (IOException unused) {
                return 0;
            }
            throw th;
        }
    }
}
