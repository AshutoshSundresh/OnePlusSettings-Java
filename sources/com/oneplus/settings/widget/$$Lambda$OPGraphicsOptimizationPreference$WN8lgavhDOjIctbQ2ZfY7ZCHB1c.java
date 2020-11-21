package com.oneplus.settings.widget;

import android.media.MediaPlayer;

/* renamed from: com.oneplus.settings.widget.-$$Lambda$OPGraphicsOptimizationPreference$WN8lgavhDOjIctbQ2ZfY7ZCHB1c  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$OPGraphicsOptimizationPreference$WN8lgavhDOjIctbQ2ZfY7ZCHB1c implements MediaPlayer.OnPreparedListener {
    public static final /* synthetic */ $$Lambda$OPGraphicsOptimizationPreference$WN8lgavhDOjIctbQ2ZfY7ZCHB1c INSTANCE = new $$Lambda$OPGraphicsOptimizationPreference$WN8lgavhDOjIctbQ2ZfY7ZCHB1c();

    private /* synthetic */ $$Lambda$OPGraphicsOptimizationPreference$WN8lgavhDOjIctbQ2ZfY7ZCHB1c() {
    }

    public final void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setLooping(true);
    }
}
