package com.android.settings.notification;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class NotificationPeopleStripPreferenceController extends TogglePreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener, LifecycleObserver, OnResume, OnPause {
    static final int OFF = 0;
    static final int ON = 1;
    private final Uri mPeopleStripUri = Settings.Secure.getUriFor("people_strip");
    private Preference mPreference;
    private Runnable mUnregisterOnPropertiesChangedListener;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public NotificationPeopleStripPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference("notification_people_strip");
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (this.mPreference != null) {
            AnonymousClass1 r0 = new ContentObserver(new Handler(Looper.getMainLooper())) {
                /* class com.android.settings.notification.NotificationPeopleStripPreferenceController.AnonymousClass1 */

                public void onChange(boolean z) {
                    super.onChange(z);
                    NotificationPeopleStripPreferenceController notificationPeopleStripPreferenceController = NotificationPeopleStripPreferenceController.this;
                    notificationPeopleStripPreferenceController.updateState(notificationPeopleStripPreferenceController.mPreference);
                }
            };
            ContentResolver contentResolver = this.mContext.getContentResolver();
            this.mUnregisterOnPropertiesChangedListener = new Runnable(contentResolver, r0) {
                /* class com.android.settings.notification.$$Lambda$NotificationPeopleStripPreferenceController$g55QSFR_o8TvqeEBFKoOHZl0v0 */
                public final /* synthetic */ ContentResolver f$0;
                public final /* synthetic */ ContentObserver f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationPeopleStripPreferenceController.lambda$onResume$0(this.f$0, this.f$1);
                }
            };
            contentResolver.registerContentObserver(this.mPeopleStripUri, false, r0);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        Runnable runnable = this.mUnregisterOnPropertiesChangedListener;
        if (runnable != null) {
            runnable.run();
            this.mUnregisterOnPropertiesChangedListener = null;
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), "people_strip", 0) != 0) {
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return Settings.Secure.putInt(this.mContext.getContentResolver(), "people_strip", z ? 1 : 0);
    }
}
