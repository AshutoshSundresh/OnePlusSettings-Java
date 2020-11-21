package com.android.settings.accounts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class WorkModePreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver, OnStart, OnStop {
    private static final String TAG = "WorkModeController";
    private IntentFilter mIntentFilter;
    private UserHandle mManagedUser;
    private Preference mPreference;
    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.accounts.WorkModePreferenceController.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                Log.v(WorkModePreferenceController.TAG, "Received broadcast: " + action);
                if (!"android.intent.action.MANAGED_PROFILE_AVAILABLE".equals(action) && !"android.intent.action.MANAGED_PROFILE_UNAVAILABLE".equals(action)) {
                    Log.w(WorkModePreferenceController.TAG, "Cannot handle received broadcast: " + intent.getAction());
                } else if (intent.getIntExtra("android.intent.extra.user_handle", -10000) == WorkModePreferenceController.this.mManagedUser.getIdentifier()) {
                    WorkModePreferenceController workModePreferenceController = WorkModePreferenceController.this;
                    workModePreferenceController.updateState(workModePreferenceController.mPreference);
                }
            }
        }
    };
    private UserManager mUserManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getSliceType() {
        return 1;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public WorkModePreferenceController(Context context, String str) {
        super(context, str);
        this.mUserManager = (UserManager) context.getSystemService("user");
        IntentFilter intentFilter = new IntentFilter();
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
        this.mIntentFilter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
    }

    public void setManagedUser(UserHandle userHandle) {
        this.mManagedUser = userHandle;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mManagedUser != null ? 0 : 4;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i;
        Context context = this.mContext;
        if (isChecked()) {
            i = C0017R$string.work_mode_on_summary;
        } else {
            i = C0017R$string.work_mode_off_summary;
        }
        return context.getText(i);
    }

    private boolean isChecked() {
        UserHandle userHandle;
        UserManager userManager = this.mUserManager;
        if (userManager == null || (userHandle = this.mManagedUser) == null) {
            return false;
        }
        return !userManager.isQuietModeEnabled(userHandle);
    }

    private boolean setChecked(boolean z) {
        UserHandle userHandle;
        UserManager userManager = this.mUserManager;
        if (!(userManager == null || (userHandle = this.mManagedUser) == null)) {
            userManager.requestQuietModeEnabled(!z, userHandle);
        }
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference instanceof TwoStatePreference) {
            ((TwoStatePreference) preference).setChecked(isChecked());
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public final boolean onPreferenceChange(Preference preference, Object obj) {
        return setChecked(((Boolean) obj).booleanValue());
    }
}
