package com.android.settings.system;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemUpdateManager;
import android.os.UserManager;
import android.telephony.CarrierConfigManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class SystemUpdatePreferenceController extends BasePreferenceController {
    private static final String KEY_SYSTEM_UPDATE_SETTINGS = "system_update_settings";
    private static final String TAG = "SysUpdatePrefContr";
    private final UserManager mUm;
    private final SystemUpdateManager mUpdateManager;

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

    public SystemUpdatePreferenceController(Context context) {
        super(context, KEY_SYSTEM_UPDATE_SETTINGS);
        this.mUm = UserManager.get(context);
        this.mUpdateManager = (SystemUpdateManager) context.getSystemService("system_update");
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!this.mContext.getResources().getBoolean(C0005R$bool.config_show_system_update_settings) || !this.mContext.getResources().getBoolean(C0005R$bool.config_use_gota) || !this.mUm.isAdminUser()) ? 3 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            Utils.updatePreferenceToSpecificActivityOrRemove(this.mContext, preferenceScreen, getPreferenceKey(), 1);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        PersistableBundle config;
        if (!TextUtils.equals(getPreferenceKey(), preference.getKey()) || (config = ((CarrierConfigManager) this.mContext.getSystemService("carrier_config")).getConfig()) == null || !config.getBoolean("ci_action_on_sys_update_bool")) {
            return false;
        }
        try {
            ciActionOnSysUpdate(config);
            return false;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        String string = this.mContext.getString(C0017R$string.android_version_summary, Build.VERSION.RELEASE_OR_CODENAME);
        FutureTask futureTask = new FutureTask(new Callable() {
            /* class com.android.settings.system.$$Lambda$SystemUpdatePreferenceController$XHnSEfghEOzLX1wZid9rCEinHuU */

            @Override // java.util.concurrent.Callable
            public final Object call() {
                return SystemUpdatePreferenceController.this.lambda$getSummary$0$SystemUpdatePreferenceController();
            }
        });
        try {
            futureTask.run();
            Bundle bundle = (Bundle) futureTask.get();
            int i = bundle.getInt("status");
            if (i == 0) {
                Log.d(TAG, "Update statue unknown");
            } else if (i != 1) {
                if (i == 2 || i == 3 || i == 4 || i == 5) {
                    return this.mContext.getText(C0017R$string.android_version_pending_update_summary);
                }
                return string;
            }
            String string2 = bundle.getString("title");
            if (TextUtils.isEmpty(string2)) {
                return string;
            }
            return this.mContext.getString(C0017R$string.android_version_summary, string2);
        } catch (InterruptedException | ExecutionException unused) {
            Log.w(TAG, "Error getting system update info.");
            return string;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getSummary$0 */
    public /* synthetic */ Bundle lambda$getSummary$0$SystemUpdatePreferenceController() throws Exception {
        return this.mUpdateManager.retrieveSystemUpdateInfo();
    }

    private void ciActionOnSysUpdate(PersistableBundle persistableBundle) {
        String string = persistableBundle.getString("ci_action_on_sys_update_intent_string");
        if (!TextUtils.isEmpty(string)) {
            String string2 = persistableBundle.getString("ci_action_on_sys_update_extra_string");
            String string3 = persistableBundle.getString("ci_action_on_sys_update_extra_val_string");
            Intent intent = new Intent(string);
            if (!TextUtils.isEmpty(string2)) {
                intent.putExtra(string2, string3);
            }
            Log.d(TAG, "ciActionOnSysUpdate: broadcasting intent " + string + " with extra " + string2 + ", " + string3);
            intent.addFlags(16777216);
            this.mContext.getApplicationContext().sendBroadcast(intent);
        }
    }
}
