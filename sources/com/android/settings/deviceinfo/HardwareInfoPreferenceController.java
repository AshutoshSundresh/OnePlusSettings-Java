package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class HardwareInfoPreferenceController extends BasePreferenceController {
    private static final String TAG = "DeviceModelPrefCtrl";

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 3;
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

    public HardwareInfoPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (Utils.isSupportCTPA(this.mContext)) {
            String string = Utils.getString(this.mContext, "ext_model_name_from_meta");
            if (string == null || string.isEmpty()) {
                string = getDeviceModel();
            }
            String string2 = Utils.getString(this.mContext, "ext_hardware_version");
            if (string2 == null || string2.isEmpty()) {
                string2 = this.mContext.getResources().getString(C0017R$string.device_info_default);
            }
            return this.mContext.getResources().getString(C0017R$string.model_hardware_summary, string, string2);
        }
        return this.mContext.getResources().getString(C0017R$string.model_summary, getDeviceModel());
    }

    public static String getDeviceModel() {
        FutureTask futureTask = new FutureTask($$Lambda$HardwareInfoPreferenceController$2jUJufqqG3kEFPreI5guPIXwUZA.INSTANCE);
        futureTask.run();
        try {
            return Build.MODEL + ((String) futureTask.get());
        } catch (ExecutionException unused) {
            Log.e(TAG, "Execution error, so we only show model name");
            return Build.MODEL;
        } catch (InterruptedException unused2) {
            Log.e(TAG, "Interruption error, so we only show model name");
            return Build.MODEL;
        }
    }
}
