package com.android.settings.location;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.preference.Preference;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.RestrictedAppPreference;
import com.oneplus.settings.utils.OPUtils;
import java.util.List;
import java.util.Map;

public class LocationServiceForWorkPreferenceController extends LocationServicePreferenceController {
    private static final String TAG = "LocationWorkPrefCtrl";

    @Override // com.android.settings.location.LocationServicePreferenceController, com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.location.LocationServicePreferenceController, com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.location.LocationServicePreferenceController, com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.location.LocationServicePreferenceController, com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.location.LocationServicePreferenceController, com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.location.LocationServicePreferenceController, com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.location.LocationServicePreferenceController, com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.location.LocationServicePreferenceController, com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public LocationServiceForWorkPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.location.LocationServicePreferenceController
    public void updateState(Preference preference) {
        this.mCategoryLocationServices.removeAll();
        Map<Integer, List<Preference>> locationServices = getLocationServices();
        UserManager userManager = UserManager.get(this.mContext);
        List<UserHandle> userProfiles = userManager.getUserProfiles();
        boolean z = false;
        for (Map.Entry<Integer, List<Preference>> entry : locationServices.entrySet()) {
            for (Preference preference2 : entry.getValue()) {
                if (preference2 instanceof RestrictedAppPreference) {
                    ((RestrictedAppPreference) preference2).checkRestrictionAndSetDisabled();
                }
            }
            if (entry.getKey().intValue() == UserHandle.myUserId() || (userProfiles.size() < 3 && OPUtils.hasMultiAppProfiles(userManager))) {
                LocationSettings.addPreferencesSorted(entry.getValue(), this.mCategoryLocationServices);
                z = true;
            }
        }
        this.mCategoryLocationServices.setVisible(z);
    }
}
