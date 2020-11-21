package com.google.tagmanager;

import android.util.Log;
import com.google.tagmanager.Logger;

class DefaultLogger implements Logger {
    private Logger.LogLevel mLogLevel = Logger.LogLevel.WARNING;

    DefaultLogger() {
    }

    @Override // com.google.tagmanager.Logger
    public void e(String str) {
        if (this.mLogLevel.ordinal() <= Logger.LogLevel.ERROR.ordinal()) {
            Log.e("GoogleTagManager", str);
        }
    }

    @Override // com.google.tagmanager.Logger
    public void w(String str) {
        if (this.mLogLevel.ordinal() <= Logger.LogLevel.WARNING.ordinal()) {
            Log.w("GoogleTagManager", str);
        }
    }
}
