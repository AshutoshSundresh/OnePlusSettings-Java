package com.android.settings.notification.zen;

import android.app.NotificationManager;
import android.content.Context;
import android.provider.Settings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.DisabledCheckBoxPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenModeVisEffectPreferenceController extends AbstractZenModePreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    protected final int mEffect;
    protected final String mKey;
    protected final int mMetricsCategory;
    protected final int[] mParentSuppressedEffects;

    public ZenModeVisEffectPreferenceController(Context context, Lifecycle lifecycle, String str, int i, int i2, int[] iArr) {
        super(context, str, lifecycle);
        this.mKey = str;
        this.mEffect = i;
        this.mMetricsCategory = i2;
        this.mParentSuppressedEffects = iArr;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return this.mKey;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (this.mEffect == 8) {
            return this.mContext.getResources().getBoolean(17891478);
        }
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        boolean z;
        boolean z2;
        super.updateState(preference);
        if (areCustomOptionsSelected()) {
            boolean z3 = Settings.Secure.getInt(this.mContext.getContentResolver(), this.mKey, 0) != 0;
            int[] iArr = this.mParentSuppressedEffects;
            if (iArr == null || !z3) {
                z2 = false;
            } else {
                z2 = false;
                for (int i : iArr) {
                    z2 |= this.mBackend.isVisualEffectSuppressed(i);
                }
            }
            if (z2) {
                ((CheckBoxPreference) preference).setChecked(z2);
                onPreferenceChange(preference, Boolean.valueOf(z2));
                ((DisabledCheckBoxPreference) preference).enableCheckbox(false);
                return;
            }
            ((DisabledCheckBoxPreference) preference).enableCheckbox(true);
            ((CheckBoxPreference) preference).setChecked(z3);
            return;
        }
        boolean isVisualEffectSuppressed = this.mBackend.isVisualEffectSuppressed(this.mEffect);
        int[] iArr2 = this.mParentSuppressedEffects;
        if (iArr2 != null) {
            z = false;
            for (int i2 : iArr2) {
                z |= this.mBackend.isVisualEffectSuppressed(i2);
            }
        } else {
            z = false;
        }
        if (z) {
            ((CheckBoxPreference) preference).setChecked(z);
            onPreferenceChange(preference, Boolean.valueOf(z));
            ((DisabledCheckBoxPreference) preference).enableCheckbox(false);
            return;
        }
        ((DisabledCheckBoxPreference) preference).enableCheckbox(true);
        ((CheckBoxPreference) preference).setChecked(isVisualEffectSuppressed);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        this.mMetricsFeatureProvider.action(this.mContext, this.mMetricsCategory, booleanValue);
        this.mBackend.saveVisualEffectsPolicy(this.mEffect, booleanValue);
        Settings.Secure.putInt(this.mContext.getContentResolver(), this.mKey, booleanValue ? 1 : 0);
        if (areCustomOptionsSelected() && "zen_effect_list".equalsIgnoreCase(this.mKey) && booleanValue) {
            Settings.Secure.putInt(this.mContext.getContentResolver(), "zen_effect_status", 1);
            this.mBackend.saveVisualEffectsPolicy(32, booleanValue);
        }
        return true;
    }

    private boolean areCustomOptionsSelected() {
        return !NotificationManager.Policy.areAllVisualEffectsSuppressed(this.mBackend.mPolicy.suppressedVisualEffects) && !(this.mBackend.mPolicy.suppressedVisualEffects == 0);
    }
}
