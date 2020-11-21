package com.android.settings.notification;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.Utils;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.oneplus.settings.utils.OPUtils;

public class RedactNotificationPreferenceController extends TogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    static final String KEY_LOCKSCREEN_REDACT = "lock_screen_redact";
    static final String KEY_LOCKSCREEN_WORK_PROFILE_REDACT = "lock_screen_work_redact";
    private static final String TAG = "LockScreenNotifPref";
    private ContentObserver mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        /* class com.android.settings.notification.RedactNotificationPreferenceController.AnonymousClass1 */

        public void onChange(boolean z) {
            if (RedactNotificationPreferenceController.this.mPreference != null) {
                RedactNotificationPreferenceController.this.mPreference.setEnabled(RedactNotificationPreferenceController.this.getAvailabilityStatus() != 5);
            }
        }
    };
    private DevicePolicyManager mDpm;
    private KeyguardManager mKm;
    private Preference mPreference;
    private final int mProfileUserId;
    private UserManager mUm;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
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
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public RedactNotificationPreferenceController(Context context, String str) {
        super(context, str);
        this.mUm = (UserManager) context.getSystemService(UserManager.class);
        this.mDpm = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
        this.mKm = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        this.mProfileUserId = Utils.getManagedProfileId(this.mUm, UserHandle.myUserId());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return getAllowPrivateNotifications(KEY_LOCKSCREEN_REDACT.equals(getPreferenceKey()) ? UserHandle.myUserId() : this.mProfileUserId);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "lock_screen_allow_private_notifications", z ? 1 : 0, KEY_LOCKSCREEN_REDACT.equals(getPreferenceKey()) ? UserHandle.myUserId() : this.mProfileUserId);
        return true;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (KEY_LOCKSCREEN_WORK_PROFILE_REDACT.equals(getPreferenceKey()) && this.mProfileUserId == -10000) {
            return 2;
        }
        if (this.mUm.getUserProfiles().size() < 3 && OPUtils.hasMultiAppProfiles(this.mUm)) {
            return 2;
        }
        int myUserId = KEY_LOCKSCREEN_REDACT.equals(getPreferenceKey()) ? UserHandle.myUserId() : this.mProfileUserId;
        if (!FeatureFactory.getFactory(this.mContext).getSecurityFeatureProvider().getLockPatternUtils(this.mContext).isSecure(myUserId)) {
            return 2;
        }
        if (!getLockscreenNotificationsEnabled(myUserId) || !adminAllowsNotifications(myUserId) || !adminAllowsUnredactedNotifications(myUserId)) {
            return 5;
        }
        if (!KEY_LOCKSCREEN_WORK_PROFILE_REDACT.equals(getPreferenceKey()) || !this.mKm.isDeviceLocked(this.mProfileUserId)) {
            return 0;
        }
        return 5;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("lock_screen_show_notifications"), false, this.mContentObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
    }

    private boolean adminAllowsNotifications(int i) {
        return (this.mDpm.getKeyguardDisabledFeatures(null, i) & 4) == 0;
    }

    private boolean adminAllowsUnredactedNotifications(int i) {
        return (this.mDpm.getKeyguardDisabledFeatures(null, i) & 8) == 0;
    }

    private boolean getAllowPrivateNotifications(int i) {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_allow_private_notifications", 1, i) != 0;
    }

    private boolean getLockscreenNotificationsEnabled(int i) {
        return Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "lock_screen_show_notifications", 1, i) != 0;
    }
}
