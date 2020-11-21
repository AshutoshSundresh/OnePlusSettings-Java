package com.android.settings.homepage.contextualcards.slices;

import android.app.PendingIntent;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.CustomSliceable;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.Utils;

public class DarkThemeSlice implements CustomSliceable {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    static long sActiveUiSession = -1000;
    static boolean sKeepSliceShow;
    static boolean sPreChecked = false;
    static boolean sSliceClicked = false;
    private final Context mContext;
    private final PowerManager mPowerManager;
    private final UiModeManager mUiModeManager;

    @Override // com.android.settings.slices.CustomSliceable
    public Intent getIntent() {
        return null;
    }

    public DarkThemeSlice(Context context) {
        this.mContext = context;
        this.mUiModeManager = (UiModeManager) context.getSystemService(UiModeManager.class);
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Slice getSlice() {
        long uiSessionToken = FeatureFactory.getFactory(this.mContext).getSlicesFeatureProvider().getUiSessionToken();
        if (uiSessionToken != sActiveUiSession) {
            sActiveUiSession = uiSessionToken;
            sKeepSliceShow = false;
        }
        if (DEBUG) {
            Log.d("DarkThemeSlice", "sKeepSliceShow = " + sKeepSliceShow + ", sSliceClicked = " + sSliceClicked + ", isAvailable = " + isAvailable(this.mContext));
        }
        if (this.mPowerManager.isPowerSaveMode() || ((!sKeepSliceShow || !sSliceClicked) && !isAvailable(this.mContext))) {
            ListBuilder listBuilder = new ListBuilder(this.mContext, CustomSliceRegistry.DARK_THEME_SLICE_URI, -1);
            listBuilder.setIsError(true);
            return listBuilder.build();
        }
        sKeepSliceShow = true;
        PendingIntent broadcastIntent = getBroadcastIntent(this.mContext);
        int colorAccentDefaultColor = Utils.getColorAccentDefaultColor(this.mContext);
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C0008R$drawable.dark_theme);
        boolean isNightMode = com.android.settings.Utils.isNightMode(this.mContext);
        if (sPreChecked != isNightMode) {
            resetValue(isNightMode, false);
        }
        ListBuilder listBuilder2 = new ListBuilder(this.mContext, CustomSliceRegistry.DARK_THEME_SLICE_URI, -1);
        listBuilder2.setAccentColor(colorAccentDefaultColor);
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        rowBuilder.setTitle(this.mContext.getText(C0017R$string.dark_theme_slice_title));
        rowBuilder.setTitleItem(createWithResource, 0);
        rowBuilder.setSubtitle(this.mContext.getText(C0017R$string.dark_theme_slice_subtitle));
        rowBuilder.setPrimaryAction(SliceAction.createToggle(broadcastIntent, null, isNightMode));
        listBuilder2.addRow(rowBuilder);
        return listBuilder2.build();
    }

    @Override // com.android.settings.slices.CustomSliceable
    public Uri getUri() {
        return CustomSliceRegistry.DARK_THEME_SLICE_URI;
    }

    @Override // com.android.settings.slices.CustomSliceable
    public void onNotifyChange(Intent intent) {
        boolean booleanExtra = intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", false);
        if (booleanExtra) {
            resetValue(booleanExtra, true);
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable(booleanExtra) {
            /* class com.android.settings.homepage.contextualcards.slices.$$Lambda$DarkThemeSlice$_s2iKR_lEhdSCVKkB_3a97GGi_k */
            public final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                DarkThemeSlice.this.lambda$onNotifyChange$0$DarkThemeSlice(this.f$1);
            }
        }, 200);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onNotifyChange$0 */
    public /* synthetic */ void lambda$onNotifyChange$0$DarkThemeSlice(boolean z) {
        this.mUiModeManager.setNightModeActivated(z);
    }

    @Override // com.android.settings.slices.Sliceable
    public Class getBackgroundWorkerClass() {
        return DarkThemeWorker.class;
    }

    /* access modifiers changed from: package-private */
    public boolean isAvailable(Context context) {
        if (com.android.settings.Utils.isNightMode(context) || isNightModeScheduled()) {
            return false;
        }
        int intProperty = ((BatteryManager) context.getSystemService(BatteryManager.class)).getIntProperty(4);
        Log.d("DarkThemeSlice", "battery level = " + intProperty);
        if (intProperty <= 50) {
            return true;
        }
        return false;
    }

    private void resetValue(boolean z, boolean z2) {
        sPreChecked = z;
        sSliceClicked = z2;
    }

    private boolean isNightModeScheduled() {
        int nightMode = this.mUiModeManager.getNightMode();
        if (DEBUG) {
            Log.d("DarkThemeSlice", "night mode = " + nightMode);
        }
        return nightMode == 0 || nightMode == 3;
    }

    public static class DarkThemeWorker extends SliceBackgroundWorker<Void> {
        private final ContentObserver mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            /* class com.android.settings.homepage.contextualcards.slices.DarkThemeSlice.DarkThemeWorker.AnonymousClass1 */

            public void onChange(boolean z) {
                if (((PowerManager) DarkThemeWorker.this.mContext.getSystemService(PowerManager.class)).isPowerSaveMode()) {
                    DarkThemeWorker.this.notifySliceChange();
                }
            }
        };
        private final Context mContext;

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
        }

        public DarkThemeWorker(Context context, Uri uri) {
            super(context, uri);
            this.mContext = context;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.slices.SliceBackgroundWorker
        public void onSlicePinned() {
            this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("low_power"), false, this.mContentObserver);
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.slices.SliceBackgroundWorker
        public void onSliceUnpinned() {
            this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
        }
    }
}
