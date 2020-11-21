package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.accessibility.AccessibilityUtils;

public class AccessibilitySlicePreferenceController extends TogglePreferenceController {
    private static final String EMPTY_STRING = "";
    private final ComponentName mComponentName;

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
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AccessibilitySlicePreferenceController(Context context, String str) {
        super(context, str);
        ComponentName unflattenFromString = ComponentName.unflattenFromString(getPreferenceKey());
        this.mComponentName = unflattenFromString;
        if (unflattenFromString == null) {
            throw new IllegalArgumentException("Illegal Component Name from: " + str);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        AccessibilityServiceInfo accessibilityServiceInfo = getAccessibilityServiceInfo();
        if (accessibilityServiceInfo == null) {
            return EMPTY_STRING;
        }
        return AccessibilitySettings.getServiceSummary(this.mContext, accessibilityServiceInfo, isChecked());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        boolean z = true;
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), "accessibility_enabled", 0) != 1) {
            z = false;
        }
        if (!z) {
            return false;
        }
        return AccessibilityUtils.getEnabledServicesFromSettings(this.mContext).contains(this.mComponentName);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (getAccessibilityServiceInfo() == null) {
            return false;
        }
        AccessibilityUtils.setAccessibilityServiceState(this.mContext, this.mComponentName, z);
        if (z == isChecked()) {
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return getAccessibilityServiceInfo() == null ? 3 : 0;
    }

    private AccessibilityServiceInfo getAccessibilityServiceInfo() {
        for (AccessibilityServiceInfo accessibilityServiceInfo : ((AccessibilityManager) this.mContext.getSystemService(AccessibilityManager.class)).getInstalledAccessibilityServiceList()) {
            if (this.mComponentName.equals(accessibilityServiceInfo.getComponentName())) {
                return accessibilityServiceInfo;
            }
        }
        return null;
    }
}
