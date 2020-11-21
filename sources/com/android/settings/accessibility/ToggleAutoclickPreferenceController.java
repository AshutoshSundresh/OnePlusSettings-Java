package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.provider.Settings;
import android.util.ArrayMap;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0003R$array;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.RadioButtonPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.LayoutPreference;
import java.util.Map;

public class ToggleAutoclickPreferenceController extends BasePreferenceController implements LifecycleObserver, RadioButtonPreference.OnClickListener, PreferenceControllerMixin {
    private static final int AUTOCLICK_CUSTOM_MODE = 2000;
    private static final int AUTOCLICK_OFF_MODE = 0;
    private static final String CONTROL_AUTOCLICK_DELAY_SECURE = "accessibility_autoclick_delay";
    private static final String KEY_AUTOCLICK_CUSTOM_SEEKBAR = "autoclick_custom_seekbar";
    static final String KEY_DELAY_MODE = "delay_mode";
    private final Map<String, Integer> mAccessibilityAutoclickKeyToValueMap = new ArrayMap();
    private final ContentResolver mContentResolver;
    private int mCurrentUiAutoClickMode;
    private RadioButtonPreference mDelayModePref;
    private OnChangeListener mOnChangeListener;
    private final Resources mResources;
    private LayoutPreference mSeekBerPreference;
    private SharedPreferences mSharedPreferences;

    public interface OnChangeListener {
        void onCheckedChanged(Preference preference);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public ToggleAutoclickPreferenceController(Context context, String str) {
        super(context, str);
        this.mSharedPreferences = context.getSharedPreferences(context.getPackageName(), 0);
        this.mContentResolver = context.getContentResolver();
        this.mResources = context.getResources();
        setAutoclickModeToKeyMap();
    }

    public ToggleAutoclickPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str);
        this.mSharedPreferences = context.getSharedPreferences(context.getPackageName(), 0);
        this.mContentResolver = context.getContentResolver();
        this.mResources = context.getResources();
        setAutoclickModeToKeyMap();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.mOnChangeListener = onChangeListener;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mDelayModePref = radioButtonPreference;
        radioButtonPreference.setOnClickListener(this);
        this.mSeekBerPreference = (LayoutPreference) preferenceScreen.findPreference(KEY_AUTOCLICK_CUSTOM_SEEKBAR);
        updateState(this.mDelayModePref);
    }

    @Override // com.android.settings.widget.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        handleRadioButtonPreferenceChange(this.mAccessibilityAutoclickKeyToValueMap.get(this.mPreferenceKey).intValue());
        OnChangeListener onChangeListener = this.mOnChangeListener;
        if (onChangeListener != null) {
            onChangeListener.onCheckedChanged(this.mDelayModePref);
        }
    }

    private void updatePreferenceCheckedState(int i) {
        if (this.mCurrentUiAutoClickMode == i) {
            this.mDelayModePref.setChecked(true);
        }
    }

    private void updatePreferenceVisibleState(int i) {
        this.mSeekBerPreference.setVisible(this.mCurrentUiAutoClickMode == i);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        boolean z = true;
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), "accessibility_autoclick_enabled", 0) != 1) {
            z = false;
        }
        this.mCurrentUiAutoClickMode = z ? getSharedPreferenceForAutoClickMode() : 0;
        this.mDelayModePref.setChecked(false);
        int intValue = this.mAccessibilityAutoclickKeyToValueMap.get(this.mDelayModePref.getKey()).intValue();
        updatePreferenceCheckedState(intValue);
        updatePreferenceVisibleState(intValue);
    }

    private void setAutoclickModeToKeyMap() {
        String[] stringArray = this.mResources.getStringArray(C0003R$array.accessibility_autoclick_control_selector_keys);
        int[] intArray = this.mResources.getIntArray(C0003R$array.accessibility_autoclick_selector_values);
        int length = intArray.length;
        for (int i = 0; i < length; i++) {
            this.mAccessibilityAutoclickKeyToValueMap.put(stringArray[i], Integer.valueOf(intArray[i]));
        }
    }

    private void handleRadioButtonPreferenceChange(int i) {
        putSecureInt("accessibility_autoclick_enabled", i != 0 ? 1 : 0);
        this.mSharedPreferences.edit().putInt(KEY_DELAY_MODE, i).apply();
        if (i != AUTOCLICK_CUSTOM_MODE) {
            putSecureInt(CONTROL_AUTOCLICK_DELAY_SECURE, i);
        }
    }

    private void putSecureInt(String str, int i) {
        Settings.Secure.putInt(this.mContentResolver, str, i);
    }

    private int getSharedPreferenceForAutoClickMode() {
        return this.mSharedPreferences.getInt(KEY_DELAY_MODE, AUTOCLICK_CUSTOM_MODE);
    }
}
