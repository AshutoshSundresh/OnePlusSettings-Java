package com.android.settings.development;

import android.content.Context;
import android.content.om.IOverlayManager;
import android.content.pm.PackageManager;
import android.os.ServiceManager;

public class EmulateDisplayCutoutPreferenceController extends OverlayCategoryPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.development.OverlayCategoryPreferenceController
    public String getPreferenceKey() {
        return "display_cutout_emulation";
    }

    EmulateDisplayCutoutPreferenceController(Context context, PackageManager packageManager, IOverlayManager iOverlayManager) {
        super(context, packageManager, iOverlayManager, "com.android.internal.display_cutout_emulation");
    }

    public EmulateDisplayCutoutPreferenceController(Context context) {
        this(context, context.getPackageManager(), IOverlayManager.Stub.asInterface(ServiceManager.getService("overlay")));
    }
}
