package com.oneplus.settings;

import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.preference.Preference;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.oneplus.settings.utils.OPUtils;

public class OnePlusSharePreferenceController extends TogglePreferenceController implements LifecycleObserver, OnCreate, OnDestroy {
    public static final Uri BASE_URI = Uri.parse("content://com.oneplus.share.provider/state");
    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        /* class com.oneplus.settings.OnePlusSharePreferenceController.AnonymousClass1 */

        public void onChange(boolean z, Uri uri) {
            OnePlusSharePreferenceController onePlusSharePreferenceController = OnePlusSharePreferenceController.this;
            onePlusSharePreferenceController.updateState(onePlusSharePreferenceController.mPreference);
        }
    };
    private Preference mPreference;

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

    public OnePlusSharePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.TogglePreferenceController
    public void updateState(Preference preference) {
        this.mPreference = preference;
        super.updateState(preference);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        Cursor query = this.mContext.getContentResolver().query(BASE_URI, null, null, null, null);
        boolean z = false;
        if (query == null) {
            return false;
        }
        while (query.moveToNext()) {
            z = "1".equals(query.getString(query.getColumnIndex("state")));
        }
        query.close();
        return z;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", Integer.valueOf(z ? 1 : 0));
        this.mContext.getContentResolver().update(BASE_URI, contentValues, null, null);
        return true;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return OPUtils.isAppExist(this.mContext, "com.oneplus.share") ? 0 : 3;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnCreate
    public void onCreate(Bundle bundle) {
        try {
            this.mContext.getContentResolver().registerContentObserver(BASE_URI, true, this.mContentObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        try {
            this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
