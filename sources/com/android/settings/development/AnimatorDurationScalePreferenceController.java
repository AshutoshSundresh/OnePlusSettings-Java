package com.android.settings.development;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.IWindowManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.C0003R$array;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class AnimatorDurationScalePreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final int ANIMATOR_DURATION_SCALE_SELECTOR = 2;
    static final float DEFAULT_VALUE = 1.0f;
    private final String[] mListSummaries;
    private final String[] mListValues;
    private final IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "animator_duration_scale";
    }

    public AnimatorDurationScalePreferenceController(Context context) {
        super(context);
        this.mListValues = context.getResources().getStringArray(C0003R$array.animator_duration_scale_values);
        this.mListSummaries = context.getResources().getStringArray(C0003R$array.animator_duration_scale_entries);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        writeAnimationScaleOption(obj);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateAnimationScaleValue();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        writeAnimationScaleOption(null);
    }

    private void writeAnimationScaleOption(Object obj) {
        float f;
        if (obj != null) {
            try {
                f = Float.parseFloat(obj.toString());
            } catch (RemoteException unused) {
                return;
            }
        } else {
            f = DEFAULT_VALUE;
        }
        this.mWindowManager.setAnimationScale(2, f);
        updateAnimationScaleValue();
    }

    private void updateAnimationScaleValue() {
        try {
            float animationScale = this.mWindowManager.getAnimationScale(2);
            int i = 0;
            int i2 = 0;
            while (true) {
                if (i2 >= this.mListValues.length) {
                    break;
                } else if (animationScale <= Float.parseFloat(this.mListValues[i2])) {
                    i = i2;
                    break;
                } else {
                    i2++;
                }
            }
            ListPreference listPreference = (ListPreference) this.mPreference;
            listPreference.setValue(this.mListValues[i]);
            listPreference.setSummary(this.mListSummaries[i]);
        } catch (RemoteException unused) {
        }
    }
}
