package com.oneplus.security.widget;

import android.os.FileObserver;

public class FileSystemObserver extends FileObserver {
    private StorageListener storageListener;

    public interface StorageListener {
        void onFileChanged();
    }

    public FileSystemObserver(String str) {
        super(str);
    }

    public void setStorageListener(StorageListener storageListener2) {
        this.storageListener = storageListener2;
    }

    public void onEvent(int i, String str) {
        StorageListener storageListener2 = this.storageListener;
        if (storageListener2 != null) {
            storageListener2.onFileChanged();
        }
    }
}
