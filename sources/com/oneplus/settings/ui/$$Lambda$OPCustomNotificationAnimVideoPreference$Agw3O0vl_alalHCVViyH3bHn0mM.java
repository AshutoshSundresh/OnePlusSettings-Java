package com.oneplus.settings.ui;

import android.media.MediaPlayer;

/* renamed from: com.oneplus.settings.ui.-$$Lambda$OPCustomNotificationAnimVideoPreference$Agw3O0vl_alalHCVViyH3bHn0mM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$OPCustomNotificationAnimVideoPreference$Agw3O0vl_alalHCVViyH3bHn0mM implements MediaPlayer.OnPreparedListener {
    public static final /* synthetic */ $$Lambda$OPCustomNotificationAnimVideoPreference$Agw3O0vl_alalHCVViyH3bHn0mM INSTANCE = new $$Lambda$OPCustomNotificationAnimVideoPreference$Agw3O0vl_alalHCVViyH3bHn0mM();

    private /* synthetic */ $$Lambda$OPCustomNotificationAnimVideoPreference$Agw3O0vl_alalHCVViyH3bHn0mM() {
    }

    public final void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setLooping(true);
    }
}
