package com.oneplus.settings.notification;

import android.content.Context;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.BootSoundPreferenceController;
import com.android.settings.notification.ChargingSoundPreferenceController;
import com.android.settings.notification.DialPadTonePreferenceController;
import com.android.settings.notification.DockAudioMediaPreferenceController;
import com.android.settings.notification.DockingSoundPreferenceController;
import com.android.settings.notification.ScreenLockSoundPreferenceController;
import com.android.settings.notification.TouchSoundPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;

public class OPSystemRingtoneSettings extends DashboardFragment {
    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPSystemRingtoneSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this, getSettingsLifecycle());
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context, OPSystemRingtoneSettings oPSystemRingtoneSettings, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        DialPadTonePreferenceController dialPadTonePreferenceController = new DialPadTonePreferenceController(context, oPSystemRingtoneSettings, lifecycle);
        ScreenLockSoundPreferenceController screenLockSoundPreferenceController = new ScreenLockSoundPreferenceController(context, oPSystemRingtoneSettings, lifecycle);
        ChargingSoundPreferenceController chargingSoundPreferenceController = new ChargingSoundPreferenceController(context, oPSystemRingtoneSettings, lifecycle);
        DockingSoundPreferenceController dockingSoundPreferenceController = new DockingSoundPreferenceController(context, oPSystemRingtoneSettings, lifecycle);
        TouchSoundPreferenceController touchSoundPreferenceController = new TouchSoundPreferenceController(context, oPSystemRingtoneSettings, lifecycle);
        DockAudioMediaPreferenceController dockAudioMediaPreferenceController = new DockAudioMediaPreferenceController(context, oPSystemRingtoneSettings, lifecycle);
        BootSoundPreferenceController bootSoundPreferenceController = new BootSoundPreferenceController(context);
        OPScreenShotSoundPreferenceController oPScreenShotSoundPreferenceController = new OPScreenShotSoundPreferenceController(context, oPSystemRingtoneSettings, lifecycle);
        arrayList.add(dialPadTonePreferenceController);
        arrayList.add(screenLockSoundPreferenceController);
        arrayList.add(chargingSoundPreferenceController);
        arrayList.add(dockingSoundPreferenceController);
        arrayList.add(touchSoundPreferenceController);
        arrayList.add(dockAudioMediaPreferenceController);
        arrayList.add(bootSoundPreferenceController);
        arrayList.add(oPScreenShotSoundPreferenceController);
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_system_ringtone_settings;
    }
}
