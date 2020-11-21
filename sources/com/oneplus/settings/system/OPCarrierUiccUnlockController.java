package com.oneplus.settings.system;

import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.utils.OPUtils;
import java.lang.reflect.Method;

public class OPCarrierUiccUnlockController extends BasePreferenceController {
    private static final String KEY_UICC_UNLOCK = "uicc_unlock";
    private static final String TAG = "OPCarrierUiccUnlockCont";
    private Context mContext;
    private Preference unlockPreference;

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

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY_UICC_UNLOCK;
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

    public OPCarrierUiccUnlockController(Context context) {
        super(context, KEY_UICC_UNLOCK);
        this.mContext = context;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return OPUtils.isSupportUssOnly() ? 0 : 4;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (OPUtils.isSupportUss()) {
            Preference preference2 = this.unlockPreference;
            if (preference2 != null) {
                preference2.setSummary(getSummary());
            }
            Log.d(TAG, "updateState");
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.unlockPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getSummary() {
        if (getSimLockStatus() == 0) {
            return this.mContext.getResources().getString(C0017R$string.uicc_unlock_summary);
        }
        if (TextUtils.equals(getType(), "1")) {
            return this.mContext.getResources().getString(C0017R$string.uicc_all_lock_summary);
        }
        return this.mContext.getResources().getString(C0017R$string.uicc_lock_summary);
    }

    public String getType() {
        String queryParamstore = queryParamstore("value");
        return TextUtils.isEmpty(queryParamstore) ? queryParamstore("defaultvalue") : queryParamstore;
    }

    public String queryParamstore(String str) {
        String str2 = "";
        Uri parse = Uri.parse("content://com.redbend.app.provider");
        try {
            Cursor query = this.mContext.getContentResolver().query(parse, null, null, new String[]{"sim_rssb_indicator", str, "0"}, null);
            if (query != null) {
                if (query.getCount() == 1 && query.moveToFirst()) {
                    str2 = query.getString(0);
                }
                query.close();
            }
            return str2;
        } catch (Exception e) {
            e.printStackTrace();
            return str2;
        }
    }

    private int getSimLockStatus() {
        try {
            Class<?> cls = Class.forName("com.oneplus.android.telephony.OPSprintReqManager");
            Method declaredMethod = cls.getDeclaredMethod("getDefault", new Class[0]);
            declaredMethod.setAccessible(true);
            return ((Integer) cls.getDeclaredMethod("getSimLockStatus", new Class[0]).invoke(declaredMethod.invoke(cls.newInstance(), new Object[0]), new Object[0])).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
