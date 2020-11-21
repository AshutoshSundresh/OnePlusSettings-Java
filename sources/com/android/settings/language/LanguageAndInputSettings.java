package com.android.settings.language;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.inputmethod.PhysicalKeyboardPreferenceController;
import com.android.settings.inputmethod.SpellCheckerPreferenceController;
import com.android.settings.inputmethod.VirtualKeyboardPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.PreferenceCategoryController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LanguageAndInputSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.language_and_input) {
        /* class com.android.settings.language.LanguageAndInputSettings.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return LanguageAndInputSettings.buildPreferenceControllers(context, null);
        }
    };
    private Preference currentInput;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "LangAndInputSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 750;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getPreferenceScreen().setInitialExpandedChildrenCount(Integer.MAX_VALUE);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.setTitle(C0017R$string.language_settings);
            if (this.currentInput == null) {
                this.currentInput = getPreferenceScreen().findPreference("virtual_keyboard_pref");
            }
            if (this.currentInput != null && getContext() != null) {
                this.currentInput.setSummary(getCurrentInputMethod(getContext()));
            }
        }
    }

    public static String getCurrentInputMethod(Context context) {
        Log.d("LangAndInputSettings", "getCurrentInputMethod ");
        Context applicationContext = context.getApplicationContext();
        String string = Settings.Secure.getString(applicationContext.getContentResolver(), "default_input_method");
        String str = "";
        if (!TextUtils.isEmpty(string)) {
            String packageName = ComponentName.unflattenFromString(string).getPackageName();
            PackageManager packageManager = applicationContext.getPackageManager();
            for (InputMethodInfo inputMethodInfo : ((InputMethodManager) applicationContext.getSystemService("input_method")).getInputMethodList()) {
                Log.d("LangAndInputSettings", "imi.getPackageName = " + inputMethodInfo.getPackageName());
                if (TextUtils.equals(inputMethodInfo.getPackageName(), packageName)) {
                    str = inputMethodInfo.loadLabel(packageManager).toString();
                    Log.d("LangAndInputSettings", "current input = " + str);
                }
            }
        }
        return str;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.language_and_input;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PhoneLanguagePreferenceController(context));
        arrayList.add(new OPPhoneLanguageH2PreferenceController(context));
        VirtualKeyboardPreferenceController virtualKeyboardPreferenceController = new VirtualKeyboardPreferenceController(context);
        PhysicalKeyboardPreferenceController physicalKeyboardPreferenceController = new PhysicalKeyboardPreferenceController(context, lifecycle);
        arrayList.add(virtualKeyboardPreferenceController);
        arrayList.add(physicalKeyboardPreferenceController);
        arrayList.add(new PreferenceCategoryController(context, "keyboards_category").setChildren(Arrays.asList(virtualKeyboardPreferenceController, physicalKeyboardPreferenceController)));
        TtsPreferenceController ttsPreferenceController = new TtsPreferenceController(context, "tts_settings_summary");
        arrayList.add(ttsPreferenceController);
        PointerSpeedController pointerSpeedController = new PointerSpeedController(context);
        arrayList.add(pointerSpeedController);
        arrayList.add(new PreferenceCategoryController(context, "pointer_and_tts_category").setChildren(Arrays.asList(pointerSpeedController, ttsPreferenceController)));
        arrayList.add(new SpellCheckerPreferenceController(context));
        return arrayList;
    }
}
