package com.android.settings.development;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.view.IWindowManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class StrictModePreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final String STRICT_MODE_DISABLED = "";
    static final String STRICT_MODE_ENABLED = "1";
    private final IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "strict_mode";
    }

    public StrictModePreferenceController(Context context) {
        super(context);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        writeStrictModeVisualOptions(((Boolean) obj).booleanValue());
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(isStrictModeEnabled());
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        writeStrictModeVisualOptions(false);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }

    private boolean isStrictModeEnabled() {
        return SystemProperties.getBoolean("persist.sys.strictmode.visual", false);
    }

    private void writeStrictModeVisualOptions(boolean z) {
        try {
            this.mWindowManager.setStrictModeVisualIndicatorPreference(z ? STRICT_MODE_ENABLED : STRICT_MODE_DISABLED);
        } catch (RemoteException unused) {
        }
    }
}
