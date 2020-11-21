package com.android.settingslib.core.instrumentation;

import android.content.Context;
import android.util.Pair;

public interface LogWriter {
    void action(int i, int i2, int i3, String str, int i4);

    void action(Context context, int i, int i2);

    void action(Context context, int i, String str);

    void action(Context context, int i, boolean z);

    void action(Context context, int i, Pair<Integer, Object>... pairArr);

    void hidden(Context context, int i, int i2);

    void visible(Context context, int i, int i2, int i3);
}
