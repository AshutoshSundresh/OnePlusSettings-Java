package com.android.settings.notification.zen;

import android.app.NotificationManager;
import android.content.Context;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.zen.ZenCustomRadioButtonPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenModeVisEffectsCustomPreferenceController extends AbstractZenModePreferenceController {
    private static final String[] ZENMODEVISEFFECT = {"zen_effect_intent", "zen_effect_light", "zen_effect_peek", "zen_effect_status", "zen_effect_badge", "zen_effect_ambient", "zen_effect_list"};
    private static final int[] ZENMODEVISEFFECT_VALUE = {4, 8, 16, 32, 64, 128, 256};
    private ZenCustomRadioButtonPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeVisEffectsCustomPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ZenCustomRadioButtonPreference zenCustomRadioButtonPreference = (ZenCustomRadioButtonPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = zenCustomRadioButtonPreference;
        zenCustomRadioButtonPreference.setOnGearClickListener(new ZenCustomRadioButtonPreference.OnGearClickListener() {
            /* class com.android.settings.notification.zen.$$Lambda$ZenModeVisEffectsCustomPreferenceController$CCFn6jYXEaUv6rkqVzz4PUil0o */

            @Override // com.android.settings.notification.zen.ZenCustomRadioButtonPreference.OnGearClickListener
            public final void onGearClick(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
                ZenModeVisEffectsCustomPreferenceController.this.lambda$displayPreference$0$ZenModeVisEffectsCustomPreferenceController(zenCustomRadioButtonPreference);
            }
        });
        this.mPreference.setOnRadioButtonClickListener(new ZenCustomRadioButtonPreference.OnRadioButtonClickListener() {
            /* class com.android.settings.notification.zen.$$Lambda$ZenModeVisEffectsCustomPreferenceController$ecCIqwNkcKumrKjb4Fy1MVxkY */

            @Override // com.android.settings.notification.zen.ZenCustomRadioButtonPreference.OnRadioButtonClickListener
            public final void onRadioButtonClick(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
                ZenModeVisEffectsCustomPreferenceController.this.lambda$displayPreference$1$ZenModeVisEffectsCustomPreferenceController(zenCustomRadioButtonPreference);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ void lambda$displayPreference$0$ZenModeVisEffectsCustomPreferenceController(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
        launchCustomSettings();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$1 */
    public /* synthetic */ void lambda$displayPreference$1$ZenModeVisEffectsCustomPreferenceController(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
        selectCustomOptions();
    }

    private void selectCustomOptions() {
        String[] strArr = ZENMODEVISEFFECT;
        boolean z = false;
        for (int i = 0; i < strArr.length; i++) {
            z = Settings.Secure.getInt(this.mContext.getContentResolver(), strArr[i], 0) != 0;
            if (z) {
                break;
            }
        }
        if (z) {
            for (int i2 = 0; i2 < strArr.length; i2++) {
                this.mBackend.saveVisualEffectsPolicy(ZENMODEVISEFFECT_VALUE[i2], Settings.Secure.getInt(this.mContext.getContentResolver(), strArr[i2], 0) != 0);
            }
            return;
        }
        launchCustomSettings();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mPreference.setChecked(areCustomOptionsSelected());
    }

    /* access modifiers changed from: protected */
    public boolean areCustomOptionsSelected() {
        return !NotificationManager.Policy.areAllVisualEffectsSuppressed(this.mBackend.mPolicy.suppressedVisualEffects) && !(this.mBackend.mPolicy.suppressedVisualEffects == 0);
    }

    /* access modifiers changed from: protected */
    public void select() {
        this.mMetricsFeatureProvider.action(this.mContext, 1399, true);
    }

    private void launchCustomSettings() {
        select();
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(ZenModeBlockedEffectsSettings.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.zen_mode_what_to_block_title);
        subSettingLauncher.setSourceMetricsCategory(1400);
        subSettingLauncher.launch();
    }
}
