package com.android.settings;

import android.app.Activity;
import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import java.util.Objects;

public class FallbackHome extends Activity {
    private final WallpaperManager.OnColorsChangedListener mColorsChangedListener = new WallpaperManager.OnColorsChangedListener() {
        /* class com.android.settings.FallbackHome.AnonymousClass1 */

        public void onColorsChanged(WallpaperColors wallpaperColors, int i) {
            if (wallpaperColors != null) {
                View decorView = FallbackHome.this.getWindow().getDecorView();
                decorView.setSystemUiVisibility(FallbackHome.this.updateVisibilityFlagsFromColors(wallpaperColors, decorView.getSystemUiVisibility()));
                FallbackHome.this.mWallManager.removeOnColorsChangedListener(this);
            }
        }
    };
    private Handler mHandler = new Handler() {
        /* class com.android.settings.FallbackHome.AnonymousClass4 */

        public void handleMessage(Message message) {
            FallbackHome.this.maybeFinish();
        }
    };
    private final Runnable mProgressTimeoutRunnable = new Runnable() {
        /* class com.android.settings.$$Lambda$FallbackHome$t1fq3k7x_PYDiX5FzYbaIlCdg */

        public final void run() {
            FallbackHome.this.lambda$new$0$FallbackHome();
        }
    };
    private boolean mProvisioned;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.FallbackHome.AnonymousClass2 */

        public void onReceive(Context context, Intent intent) {
            FallbackHome.this.maybeFinish();
        }
    };
    private WallpaperManager mWallManager;

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$FallbackHome() {
        View inflate = getLayoutInflater().inflate(C0012R$layout.fallback_home_finishing_boot, (ViewGroup) null);
        setContentView(inflate);
        inflate.setAlpha(0.0f);
        inflate.animate().alpha(1.0f).setDuration(500).setInterpolator(AnimationUtils.loadInterpolator(this, 17563661)).start();
        getWindow().addFlags(128);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        boolean z = false;
        if (Settings.Global.getInt(getContentResolver(), "device_provisioned", 0) != 0) {
            z = true;
        }
        this.mProvisioned = z;
        if (!z) {
            setTheme(C0018R$style.FallbackHome_SetupWizard);
            i = 4102;
        } else {
            i = 1536;
        }
        WallpaperManager wallpaperManager = (WallpaperManager) getSystemService(WallpaperManager.class);
        this.mWallManager = wallpaperManager;
        if (wallpaperManager == null) {
            Log.w("FallbackHome", "Wallpaper manager isn't ready, can't listen to color changes!");
        } else {
            loadWallpaperColors(i);
        }
        getWindow().getDecorView().setSystemUiVisibility(i);
        registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.USER_UNLOCKED"));
        maybeFinish();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.mProvisioned) {
            this.mHandler.postDelayed(this.mProgressTimeoutRunnable, 2000);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.mHandler.removeCallbacks(this.mProgressTimeoutRunnable);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mReceiver);
        WallpaperManager wallpaperManager = this.mWallManager;
        if (wallpaperManager != null) {
            wallpaperManager.removeOnColorsChangedListener(this.mColorsChangedListener);
        }
    }

    private void loadWallpaperColors(final int i) {
        new AsyncTask<Object, Void, Integer>() {
            /* class com.android.settings.FallbackHome.AnonymousClass3 */

            /* access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Integer doInBackground(Object... objArr) {
                WallpaperColors wallpaperColors = FallbackHome.this.mWallManager.getWallpaperColors(1);
                if (wallpaperColors != null) {
                    return Integer.valueOf(FallbackHome.this.updateVisibilityFlagsFromColors(wallpaperColors, i));
                }
                FallbackHome.this.mWallManager.addOnColorsChangedListener(FallbackHome.this.mColorsChangedListener, null);
                return null;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Integer num) {
                if (num != null) {
                    FallbackHome.this.getWindow().getDecorView().setSystemUiVisibility(num.intValue());
                }
            }
        }.execute(new Object[0]);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void maybeFinish() {
        if (((UserManager) getSystemService(UserManager.class)).isUserUnlocked()) {
            if (!Objects.equals(getPackageName(), getPackageManager().resolveActivity(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME"), 0).activityInfo.packageName)) {
                Log.d("FallbackHome", "User unlocked and real home found; let's go!");
                ((PowerManager) getSystemService(PowerManager.class)).userActivity(SystemClock.uptimeMillis(), false);
                finish();
            } else if (!UserManager.isSplitSystemUser() || UserHandle.myUserId() != 0) {
                Log.d("FallbackHome", "User unlocked but no home; let's hope someone enables one soon?");
                this.mHandler.sendEmptyMessageDelayed(0, 500);
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int updateVisibilityFlagsFromColors(WallpaperColors wallpaperColors, int i) {
        return (wallpaperColors.getColorHints() & 1) != 0 ? i | 8192 | 16 : i & -8193 & -17;
    }
}
