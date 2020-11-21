package com.android.settingslib.core.instrumentation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Pair;
import androidx.preference.Preference;
import java.util.ArrayList;
import java.util.List;

public class MetricsFeatureProvider {
    protected List<LogWriter> mLoggerWriters = new ArrayList();

    public MetricsFeatureProvider() {
        installLogWriters();
    }

    /* access modifiers changed from: protected */
    public void installLogWriters() {
        this.mLoggerWriters.add(new EventLogWriter());
    }

    public int getAttribution(Activity activity) {
        Intent intent;
        if (activity == null || (intent = activity.getIntent()) == null) {
            return 0;
        }
        return intent.getIntExtra(":settings:source_metrics", 0);
    }

    public void visible(Context context, int i, int i2, int i3) {
        for (LogWriter logWriter : this.mLoggerWriters) {
            logWriter.visible(context, i, i2, i3);
        }
    }

    public void hidden(Context context, int i, int i2) {
        for (LogWriter logWriter : this.mLoggerWriters) {
            logWriter.hidden(context, i, i2);
        }
    }

    public void action(Context context, int i, Pair<Integer, Object>... pairArr) {
        for (LogWriter logWriter : this.mLoggerWriters) {
            logWriter.action(context, i, pairArr);
        }
    }

    public void action(Context context, int i, String str) {
        for (LogWriter logWriter : this.mLoggerWriters) {
            logWriter.action(context, i, str);
        }
    }

    public void action(int i, int i2, int i3, String str, int i4) {
        for (LogWriter logWriter : this.mLoggerWriters) {
            logWriter.action(i, i2, i3, str, i4);
        }
    }

    public void action(Context context, int i, int i2) {
        for (LogWriter logWriter : this.mLoggerWriters) {
            logWriter.action(context, i, i2);
        }
    }

    public void action(Context context, int i, boolean z) {
        for (LogWriter logWriter : this.mLoggerWriters) {
            logWriter.action(context, i, z);
        }
    }

    public int getMetricsCategory(Object obj) {
        if (obj == null || !(obj instanceof Instrumentable)) {
            return 0;
        }
        return ((Instrumentable) obj).getMetricsCategory();
    }

    public boolean logClickedPreference(Preference preference, int i) {
        if (preference == null) {
            return false;
        }
        if (logSettingsTileClick(preference.getKey(), i) || logStartedIntent(preference.getIntent(), i) || logSettingsTileClick(preference.getFragment(), i)) {
            return true;
        }
        return false;
    }

    public boolean logStartedIntent(Intent intent, int i) {
        if (intent == null) {
            return false;
        }
        ComponentName component = intent.getComponent();
        return logSettingsTileClick(component != null ? component.flattenToString() : intent.getAction(), i);
    }

    public boolean logStartedIntentWithProfile(Intent intent, int i, boolean z) {
        if (intent == null) {
            return false;
        }
        ComponentName component = intent.getComponent();
        String flattenToString = component != null ? component.flattenToString() : intent.getAction();
        StringBuilder sb = new StringBuilder();
        sb.append(flattenToString);
        sb.append(z ? "/work" : "/personal");
        return logSettingsTileClick(sb.toString(), i);
    }

    public boolean logSettingsTileClick(String str, int i) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        action(i, 830, 0, str, 0);
        return true;
    }
}
