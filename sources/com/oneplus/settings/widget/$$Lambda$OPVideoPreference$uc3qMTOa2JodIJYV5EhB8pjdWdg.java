package com.oneplus.settings.widget;

import android.media.MediaPlayer;

/* renamed from: com.oneplus.settings.widget.-$$Lambda$OPVideoPreference$uc3qMTOa2JodIJYV5EhB8pjdWdg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$OPVideoPreference$uc3qMTOa2JodIJYV5EhB8pjdWdg implements MediaPlayer.OnPreparedListener {
    public static final /* synthetic */ $$Lambda$OPVideoPreference$uc3qMTOa2JodIJYV5EhB8pjdWdg INSTANCE = new $$Lambda$OPVideoPreference$uc3qMTOa2JodIJYV5EhB8pjdWdg();

    private /* synthetic */ $$Lambda$OPVideoPreference$uc3qMTOa2JodIJYV5EhB8pjdWdg() {
    }

    public final void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setLooping(true);
    }
}
