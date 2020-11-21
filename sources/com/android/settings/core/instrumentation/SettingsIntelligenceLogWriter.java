package com.android.settings.core.instrumentation;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.android.settings.C0017R$string;
import com.android.settings.intelligence.LogProto$SettingsLog;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.LogWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

public class SettingsIntelligenceLogWriter implements LogWriter {
    private SendLogHandler mLogHandler;
    private final Runnable mSendLogsRunnable = new Runnable() {
        /* class com.android.settings.core.instrumentation.$$Lambda$SettingsIntelligenceLogWriter$Hz0nZT4MTEYWxILUrdYtzbdCtA */

        public final void run() {
            SettingsIntelligenceLogWriter.this.lambda$new$1$SettingsIntelligenceLogWriter();
        }
    };
    private List<LogProto$SettingsLog> mSettingsLogList = new LinkedList();

    public SettingsIntelligenceLogWriter() {
        HandlerThread handlerThread = new HandlerThread("SettingsIntelligenceLogWriter", 10);
        handlerThread.start();
        this.mLogHandler = new SendLogHandler(handlerThread.getLooper());
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter
    public void visible(Context context, int i, int i2, int i3) {
        action(i, 1, i2, "", i3);
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter
    public void hidden(Context context, int i, int i2) {
        action(0, 2, i, "", i2);
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter
    public void action(Context context, int i, Pair<Integer, Object>... pairArr) {
        action(0, i, 0, "", 0);
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter
    public void action(Context context, int i, int i2) {
        action(0, i, 0, "", i2);
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter
    public void action(Context context, int i, boolean z) {
        action(0, i, 0, "", z ? 1 : 0);
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter
    public void action(Context context, int i, String str) {
        action(0, i, 0, str, 1);
    }

    @Override // com.android.settingslib.core.instrumentation.LogWriter
    public void action(int i, int i2, int i3, String str, int i4) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        LogProto$SettingsLog.Builder newBuilder = LogProto$SettingsLog.newBuilder();
        newBuilder.setAttribution(i);
        newBuilder.setAction(i2);
        newBuilder.setPageId(i3);
        if (str == null) {
            str = "";
        }
        newBuilder.setChangedPreferenceKey(str);
        newBuilder.setChangedPreferenceIntValue(i4);
        newBuilder.setTimestamp(now.toString());
        this.mLogHandler.post(new Runnable((LogProto$SettingsLog) newBuilder.build()) {
            /* class com.android.settings.core.instrumentation.$$Lambda$SettingsIntelligenceLogWriter$VFXexcil80tWUJXN7_8eUYD3K4 */
            public final /* synthetic */ LogProto$SettingsLog f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SettingsIntelligenceLogWriter.this.lambda$action$0$SettingsIntelligenceLogWriter(this.f$1);
            }
        });
        if (i2 == 1665) {
            this.mLogHandler.sendLog();
        } else {
            this.mLogHandler.scheduleSendLog();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$action$0 */
    public /* synthetic */ void lambda$action$0$SettingsIntelligenceLogWriter(LogProto$SettingsLog logProto$SettingsLog) {
        this.mSettingsLogList.add(logProto$SettingsLog);
    }

    static byte[] serialize(List<LogProto$SettingsLog> list) {
        int size = list.size();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeInt(size);
            for (LogProto$SettingsLog logProto$SettingsLog : list) {
                byte[] byteArray = logProto$SettingsLog.toByteArray();
                dataOutputStream.writeInt(byteArray.length);
                dataOutputStream.write(byteArray);
            }
            byte[] byteArray2 = byteArrayOutputStream.toByteArray();
            try {
                dataOutputStream.close();
            } catch (Exception e) {
                Log.e("IntelligenceLogWriter", "close error", e);
            }
            return byteArray2;
        } catch (Exception e2) {
            Log.e("IntelligenceLogWriter", "serialize error", e2);
            try {
                dataOutputStream.close();
            } catch (Exception e3) {
                Log.e("IntelligenceLogWriter", "close error", e3);
            }
            return null;
        } catch (Throwable th) {
            try {
                dataOutputStream.close();
            } catch (Exception e4) {
                Log.e("IntelligenceLogWriter", "close error", e4);
            }
            throw th;
        }
    }

    /* access modifiers changed from: private */
    public class SendLogHandler extends Handler {
        SendLogHandler(Looper looper) {
            super(looper);
        }

        /* access modifiers changed from: package-private */
        public void scheduleSendLog() {
            removeCallbacks(SettingsIntelligenceLogWriter.this.mSendLogsRunnable);
            postDelayed(SettingsIntelligenceLogWriter.this.mSendLogsRunnable, 60000);
        }

        /* access modifiers changed from: package-private */
        public void sendLog() {
            removeCallbacks(SettingsIntelligenceLogWriter.this.mSendLogsRunnable);
            post(SettingsIntelligenceLogWriter.this.mSendLogsRunnable);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$SettingsIntelligenceLogWriter() {
        Context appContext = FeatureFactory.getAppContext();
        if (appContext == null) {
            Log.e("IntelligenceLogWriter", "context is null");
            return;
        }
        String string = appContext.getString(C0017R$string.config_settingsintelligence_log_action);
        if (!TextUtils.isEmpty(string) && !this.mSettingsLogList.isEmpty()) {
            Intent intent = new Intent();
            intent.setPackage(appContext.getString(C0017R$string.config_settingsintelligence_package_name));
            intent.setAction(string);
            intent.putExtra("logs", serialize(this.mSettingsLogList));
            appContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
            this.mSettingsLogList.clear();
        }
    }
}
