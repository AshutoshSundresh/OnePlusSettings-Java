package com.android.settings.widget;

import android.media.MediaPlayer;

/* renamed from: com.android.settings.widget.-$$Lambda$MediaAnimationController$60QwoqjGmisNwYSwY_DAxBhw-I8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaAnimationController$60QwoqjGmisNwYSwY_DAxBhwI8 implements MediaPlayer.OnPreparedListener {
    public static final /* synthetic */ $$Lambda$MediaAnimationController$60QwoqjGmisNwYSwY_DAxBhwI8 INSTANCE = new $$Lambda$MediaAnimationController$60QwoqjGmisNwYSwY_DAxBhwI8();

    private /* synthetic */ $$Lambda$MediaAnimationController$60QwoqjGmisNwYSwY_DAxBhwI8() {
    }

    public final void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setLooping(true);
    }
}
