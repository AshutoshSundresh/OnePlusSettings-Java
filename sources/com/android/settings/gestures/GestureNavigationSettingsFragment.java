package com.android.settings.gestures;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.LabeledSeekBarPreference;

public class GestureNavigationSettingsFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.gesture_navigation_settings) {
        /* class com.android.settings.gestures.GestureNavigationSettingsFragment.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return SystemNavigationPreferenceController.isGestureAvailable(context);
        }
    };
    private float[] mBackGestureInsetScales;
    private float mDefaultBackGestureInset;
    private BackGestureIndicatorView mIndicatorView;
    private WindowManager mWindowManager;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "GestureNavigationSettingsFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1748;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mIndicatorView = new BackGestureIndicatorView(getActivity());
        this.mWindowManager = (WindowManager) getActivity().getSystemService("window");
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        Resources resources = getActivity().getResources();
        this.mDefaultBackGestureInset = (float) resources.getDimensionPixelSize(17105052);
        this.mBackGestureInsetScales = getFloatArray(resources.obtainTypedArray(17235993));
        initSeekBarPreference("gesture_left_back_sensitivity");
        initSeekBarPreference("gesture_right_back_sensitivity");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        WindowManager windowManager = this.mWindowManager;
        BackGestureIndicatorView backGestureIndicatorView = this.mIndicatorView;
        windowManager.addView(backGestureIndicatorView, backGestureIndicatorView.getLayoutParams(getActivity().getWindow().getAttributes()));
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mWindowManager.removeView(this.mIndicatorView);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.gesture_navigation_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_uri_default;
    }

    private void initSeekBarPreference(String str) {
        LabeledSeekBarPreference labeledSeekBarPreference = (LabeledSeekBarPreference) getPreferenceScreen().findPreference(str);
        labeledSeekBarPreference.setContinuousUpdates(true);
        String str2 = str == "gesture_left_back_sensitivity" ? "back_gesture_inset_scale_left" : "back_gesture_inset_scale_right";
        float f = Settings.Secure.getFloat(getContext().getContentResolver(), str2, 1.0f);
        float f2 = Float.MAX_VALUE;
        int i = -1;
        int i2 = 0;
        while (true) {
            float[] fArr = this.mBackGestureInsetScales;
            if (i2 < fArr.length) {
                float abs = Math.abs(fArr[i2] - f);
                if (abs < f2) {
                    i = i2;
                    f2 = abs;
                }
                i2++;
            } else {
                labeledSeekBarPreference.setProgress(i);
                labeledSeekBarPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(str) {
                    /* class com.android.settings.gestures.$$Lambda$GestureNavigationSettingsFragment$6L3GECHBiNe72OpqyJO365318 */
                    public final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        return GestureNavigationSettingsFragment.this.lambda$initSeekBarPreference$0$GestureNavigationSettingsFragment(this.f$1, preference, obj);
                    }
                });
                labeledSeekBarPreference.setOnPreferenceChangeStopListener(new Preference.OnPreferenceChangeListener(str, str2) {
                    /* class com.android.settings.gestures.$$Lambda$GestureNavigationSettingsFragment$aaXw_5rVyEDe531BlqIx8COMB_o */
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        return GestureNavigationSettingsFragment.this.lambda$initSeekBarPreference$1$GestureNavigationSettingsFragment(this.f$1, this.f$2, preference, obj);
                    }
                });
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initSeekBarPreference$0 */
    public /* synthetic */ boolean lambda$initSeekBarPreference$0$GestureNavigationSettingsFragment(String str, Preference preference, Object obj) {
        this.mIndicatorView.setIndicatorWidth((int) (this.mDefaultBackGestureInset * this.mBackGestureInsetScales[((Integer) obj).intValue()]), str == "gesture_left_back_sensitivity");
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initSeekBarPreference$1 */
    public /* synthetic */ boolean lambda$initSeekBarPreference$1$GestureNavigationSettingsFragment(String str, String str2, Preference preference, Object obj) {
        this.mIndicatorView.setIndicatorWidth(0, str == "gesture_left_back_sensitivity");
        Settings.Secure.putFloat(getContext().getContentResolver(), str2, this.mBackGestureInsetScales[((Integer) obj).intValue()]);
        return true;
    }

    private static float[] getFloatArray(TypedArray typedArray) {
        int length = typedArray.length();
        float[] fArr = new float[length];
        for (int i = 0; i < length; i++) {
            fArr[i] = typedArray.getFloat(i, 1.0f);
        }
        typedArray.recycle();
        return fArr;
    }
}
