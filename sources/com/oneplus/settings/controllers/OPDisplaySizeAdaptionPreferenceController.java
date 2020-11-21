package com.oneplus.settings.controllers;

import android.content.Context;
import android.content.IntentFilter;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.os.RemoteException;
import android.os.ServiceManager;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OPDisplaySizeAdaptionPreferenceController extends BasePreferenceController {
    private static final Comparator<OverlayInfo> OVERLAY_INFO_COMPARATOR = Comparator.comparingInt($$Lambda$OPDisplaySizeAdaptionPreferenceController$lXl96kt8aptFcxY7kyqyVnJMfcc.INSTANCE);
    private static final String OVERLAY_TARGET_PACKAGE = "android";
    private static final String PACKAGE_DEVICE_DEFAULT = "package_device_default";

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

    public OPDisplaySizeAdaptionPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!OPUtils.isSupportScreenDisplayAdaption() || !OPUtils.isSupportAppsDisplayInFullscreen()) ? 3 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (OPUtils.isSupportScreenCutting()) {
            preference.setEnabled(PACKAGE_DEVICE_DEFAULT.equals(getCurrenMode()));
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (!OPUtils.isSupportScreenCutting()) {
            return this.mContext.getString(C0017R$string.oneplus_display_size_adaption_summary);
        }
        if (OPUtils.isSupportHolePunchFrontCam()) {
            return this.mContext.getString(C0017R$string.oneplus_front_camera_app_display_fullscreen_summary);
        }
        return this.mContext.getString(C0017R$string.oneplus_app_display_fullscreen_summary);
    }

    private String getCurrenMode() {
        ArrayList arrayList = new ArrayList();
        String str = PACKAGE_DEVICE_DEFAULT;
        arrayList.add(str);
        for (OverlayInfo overlayInfo : getOverlayInfos()) {
            arrayList.add(overlayInfo.packageName);
            if (overlayInfo.isEnabled()) {
                str = (String) arrayList.get(arrayList.size() - 1);
            }
        }
        return str;
    }

    private List<OverlayInfo> getOverlayInfos() {
        ArrayList arrayList = new ArrayList();
        try {
            for (OverlayInfo overlayInfo : IOverlayManager.Stub.asInterface(ServiceManager.getService("overlay")).getOverlayInfosForTarget(OVERLAY_TARGET_PACKAGE, 0)) {
                if ("com.android.internal.display_cutout_emulation".equals(overlayInfo.category)) {
                    arrayList.add(overlayInfo);
                }
            }
            arrayList.sort(OVERLAY_INFO_COMPARATOR);
            return arrayList;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
