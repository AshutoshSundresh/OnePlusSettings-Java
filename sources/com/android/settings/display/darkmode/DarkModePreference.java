package com.android.settings.display.darkmode;

import android.app.UiModeManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.AttributeSet;
import com.android.settings.C0017R$string;
import com.android.settings.widget.MasterSwitchPreference;
import java.time.LocalTime;

public class DarkModePreference extends MasterSwitchPreference {
    private Runnable mCallback;
    private DarkModeObserver mDarkModeObserver;
    private TimeFormatter mFormat;
    private PowerManager mPowerManager;
    private UiModeManager mUiModeManager;

    public DarkModePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDarkModeObserver = new DarkModeObserver(context);
        this.mUiModeManager = (UiModeManager) context.getSystemService(UiModeManager.class);
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mFormat = new TimeFormatter(context);
        $$Lambda$DarkModePreference$2Bk4dVlLCcWxOCDjzV5qjb0rWII r1 = new Runnable() {
            /* class com.android.settings.display.darkmode.$$Lambda$DarkModePreference$2Bk4dVlLCcWxOCDjzV5qjb0rWII */

            public final void run() {
                DarkModePreference.this.lambda$new$0$DarkModePreference();
            }
        };
        this.mCallback = r1;
        this.mDarkModeObserver.subscribe(r1);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$DarkModePreference() {
        boolean isPowerSaveMode = this.mPowerManager.isPowerSaveMode();
        boolean z = (getContext().getResources().getConfiguration().uiMode & 32) != 0;
        setSwitchEnabled(!isPowerSaveMode);
        updateSummary(isPowerSaveMode, z);
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mDarkModeObserver.subscribe(this.mCallback);
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        super.onDetached();
        this.mDarkModeObserver.unsubscribe();
    }

    private void updateSummary(boolean z, boolean z2) {
        String str;
        int i;
        int i2;
        LocalTime localTime;
        int i3;
        int i4;
        int i5;
        if (z) {
            if (z2) {
                i5 = C0017R$string.dark_ui_mode_disabled_summary_dark_theme_on;
            } else {
                i5 = C0017R$string.dark_ui_mode_disabled_summary_dark_theme_off;
            }
            setSummary(getContext().getString(i5));
            return;
        }
        int nightMode = this.mUiModeManager.getNightMode();
        if (nightMode == 0) {
            Context context = getContext();
            if (z2) {
                i4 = C0017R$string.dark_ui_summary_on_auto_mode_auto;
            } else {
                i4 = C0017R$string.dark_ui_summary_off_auto_mode_auto;
            }
            str = context.getString(i4);
        } else if (nightMode == 3) {
            if (z2) {
                localTime = this.mUiModeManager.getCustomNightModeEnd();
            } else {
                localTime = this.mUiModeManager.getCustomNightModeStart();
            }
            String of = this.mFormat.of(localTime);
            Context context2 = getContext();
            if (z2) {
                i3 = C0017R$string.dark_ui_summary_on_auto_mode_custom;
            } else {
                i3 = C0017R$string.dark_ui_summary_off_auto_mode_custom;
            }
            str = context2.getString(i3, of);
        } else {
            Context context3 = getContext();
            if (z2) {
                i2 = C0017R$string.dark_ui_summary_on_auto_mode_never;
            } else {
                i2 = C0017R$string.dark_ui_summary_off_auto_mode_never;
            }
            str = context3.getString(i2);
        }
        Context context4 = getContext();
        if (z2) {
            i = C0017R$string.dark_ui_summary_on;
        } else {
            i = C0017R$string.dark_ui_summary_off;
        }
        setSummary(context4.getString(i, str));
    }
}
