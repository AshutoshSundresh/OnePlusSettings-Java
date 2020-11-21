package com.android.settings.language;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.inputmethod.UserDictionaryList;
import com.android.settings.inputmethod.UserDictionaryListPreferenceController;
import com.android.settings.inputmethod.UserDictionarySettings;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.TreeSet;

public class UserDictionaryPreferenceController extends BasePreferenceController {
    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public UserDictionaryPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        Class cls;
        if (isAvailable() && preference != null) {
            TreeSet<String> dictionaryLocales = getDictionaryLocales();
            Bundle extras = preference.getExtras();
            if (dictionaryLocales.size() <= 1) {
                if (!dictionaryLocales.isEmpty()) {
                    extras.putString("locale", dictionaryLocales.first());
                }
                cls = UserDictionarySettings.class;
            } else {
                cls = UserDictionaryList.class;
            }
            preference.setFragment(cls.getCanonicalName());
        }
    }

    /* access modifiers changed from: protected */
    public TreeSet<String> getDictionaryLocales() {
        return UserDictionaryListPreferenceController.getUserDictionaryLocalesSet(this.mContext);
    }
}
