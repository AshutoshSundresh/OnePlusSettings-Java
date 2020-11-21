package com.android.settings.gestures;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Switch;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0010R$id;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;

public class PreventRingingSwitchPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, SwitchBar.OnSwitchChangeListener {
    private final Context mContext;
    SwitchBar mSwitch;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "gesture_prevent_ringing_switch";
    }

    public PreventRingingSwitchPreferenceController(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        LayoutPreference layoutPreference;
        super.displayPreference(preferenceScreen);
        if (isAvailable() && (layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey())) != null) {
            new SettingObserver(layoutPreference);
            layoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                /* class com.android.settings.gestures.$$Lambda$PreventRingingSwitchPreferenceController$tkwvFAD7BhbhXsBPnVpa8l9DK84 */

                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    return PreventRingingSwitchPreferenceController.this.lambda$displayPreference$0$PreventRingingSwitchPreferenceController(preference);
                }
            });
            SwitchBar switchBar = (SwitchBar) layoutPreference.findViewById(C0010R$id.switch_bar);
            this.mSwitch = switchBar;
            if (switchBar != null) {
                switchBar.addOnSwitchChangeListener(this);
                this.mSwitch.show();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ boolean lambda$displayPreference$0$PreventRingingSwitchPreferenceController(Preference preference) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "volume_hush_gesture", (Settings.Secure.getInt(this.mContext.getContentResolver(), "volume_hush_gesture", 1) != 0 ? 1 : 0) ^ 1);
        return true;
    }

    public void setChecked(boolean z) {
        SwitchBar switchBar = this.mSwitch;
        if (switchBar != null) {
            switchBar.setChecked(z);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        boolean z = true;
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), "volume_hush_gesture", 1) == 0) {
            z = false;
        }
        setChecked(z);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(17891591);
    }

    @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
    public void onSwitchChanged(Switch r3, boolean z) {
        int i = 1;
        int i2 = Settings.Secure.getInt(this.mContext.getContentResolver(), "volume_hush_gesture", 1);
        if (i2 != 0) {
            i = i2;
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        if (!z) {
            i = 0;
        }
        Settings.Secure.putInt(contentResolver, "volume_hush_gesture", i);
    }

    private class SettingObserver extends ContentObserver {
        private final Uri VOLUME_HUSH_GESTURE = Settings.Secure.getUriFor("volume_hush_gesture");
        private final Preference mPreference;

        public SettingObserver(Preference preference) {
            super(new Handler());
            this.mPreference = preference;
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (uri == null || this.VOLUME_HUSH_GESTURE.equals(uri)) {
                PreventRingingSwitchPreferenceController.this.updateState(this.mPreference);
            }
        }
    }
}
