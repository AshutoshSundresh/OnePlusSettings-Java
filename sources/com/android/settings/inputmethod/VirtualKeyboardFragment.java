package com.android.settings.inputmethod;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.internal.util.Preconditions;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.language.LanguageAndInputSettings;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.OPInputMethodPreference;
import com.android.settingslib.inputmethod.InputMethodAndSubtypeUtilCompat;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class VirtualKeyboardFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.inputmethod.VirtualKeyboardFragment.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.virtual_keyboard_settings;
            return Arrays.asList(searchIndexableResource);
        }
    };
    private Preference mAddVirtualKeyboardScreen;
    private PreferenceCategory mAvailableInputMethod;
    private Preference mCurrentInputMethod;
    private DevicePolicyManager mDpm;
    private WindowFocusChangeListener mFocusChangeListener;
    private InputMethodManager mImm;
    private final ArrayList<OPInputMethodPreference> mInputMethodPreferenceList = new ArrayList<>();

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "VirtualKeyboardFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 345;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        Activity activity = (Activity) Preconditions.checkNotNull(getActivity());
        this.mImm = (InputMethodManager) Preconditions.checkNotNull((InputMethodManager) activity.getSystemService(InputMethodManager.class));
        this.mDpm = (DevicePolicyManager) Preconditions.checkNotNull((DevicePolicyManager) activity.getSystemService(DevicePolicyManager.class));
        this.mAddVirtualKeyboardScreen = (Preference) Preconditions.checkNotNull(findPreference("add_virtual_keyboard_screen"));
        this.mCurrentInputMethod = (Preference) Preconditions.checkNotNull(findPreference("current_input_method"));
        this.mAvailableInputMethod = (PreferenceCategory) Preconditions.checkNotNull((PreferenceCategory) findPreference("available_input_method"));
        this.mCurrentInputMethod.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.inputmethod.$$Lambda$VirtualKeyboardFragment$H8P70pCV5BiAvO91Zn_0JTKg4Ns */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return VirtualKeyboardFragment.this.lambda$onCreatePreferences$0$VirtualKeyboardFragment(preference);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreatePreferences$0 */
    public /* synthetic */ boolean lambda$onCreatePreferences$0$VirtualKeyboardFragment(Preference preference) {
        this.mImm.showInputMethodPicker();
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateInputMethodPreferenceViews();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.virtual_keyboard_settings;
    }

    private void updateInputMethodPreferenceViews() {
        this.mInputMethodPreferenceList.clear();
        List permittedInputMethodsForCurrentUser = this.mDpm.getPermittedInputMethodsForCurrentUser();
        Context prefContext = getPrefContext();
        List<InputMethodInfo> enabledInputMethodList = this.mImm.getEnabledInputMethodList();
        int size = enabledInputMethodList == null ? 0 : enabledInputMethodList.size();
        for (int i = 0; i < size; i++) {
            InputMethodInfo inputMethodInfo = enabledInputMethodList.get(i);
            boolean z = permittedInputMethodsForCurrentUser == null || permittedInputMethodsForCurrentUser.contains(inputMethodInfo.getPackageName());
            Drawable loadIcon = inputMethodInfo.loadIcon(prefContext.getPackageManager());
            OPInputMethodPreference oPInputMethodPreference = new OPInputMethodPreference(prefContext, inputMethodInfo, false, z, (OPInputMethodPreference.OnSavePreferenceListener) null);
            oPInputMethodPreference.setIcon(loadIcon);
            this.mInputMethodPreferenceList.add(oPInputMethodPreference);
        }
        this.mInputMethodPreferenceList.sort(new Comparator(Collator.getInstance()) {
            /* class com.android.settings.inputmethod.$$Lambda$VirtualKeyboardFragment$SLLd3dtxj7HqQ80Fz3c43WyC6tQ */
            public final /* synthetic */ Collator f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return VirtualKeyboardFragment.lambda$updateInputMethodPreferenceViews$1(this.f$0, (OPInputMethodPreference) obj, (OPInputMethodPreference) obj2);
            }
        });
        this.mAvailableInputMethod.removeAll();
        for (int i2 = 0; i2 < size; i2++) {
            OPInputMethodPreference oPInputMethodPreference2 = this.mInputMethodPreferenceList.get(i2);
            oPInputMethodPreference2.setOrder(i2);
            this.mAvailableInputMethod.addPreference(oPInputMethodPreference2);
            InputMethodAndSubtypeUtilCompat.removeUnnecessaryNonPersistentPreference(oPInputMethodPreference2);
            oPInputMethodPreference2.updatePreferenceViews();
        }
        this.mAddVirtualKeyboardScreen.setIcon((Drawable) null);
        this.mAddVirtualKeyboardScreen.setOrder(size);
        this.mAvailableInputMethod.addPreference(this.mAddVirtualKeyboardScreen);
    }

    private class WindowFocusChangeListener implements ViewTreeObserver.OnWindowFocusChangeListener {
        private WindowFocusChangeListener() {
        }

        public void onWindowFocusChanged(boolean z) {
            Log.d("VirtualKeyboardFragment", "onWindowFocusChanged" + z);
            if (z) {
                VirtualKeyboardFragment.this.updateCurrentInput();
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateCurrentInput() {
        this.mCurrentInputMethod.setSummary(LanguageAndInputSettings.getCurrentInputMethod(getPrefContext()));
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mFocusChangeListener = new WindowFocusChangeListener();
        if (getView() != null && getView().getViewTreeObserver() != null) {
            getView().getViewTreeObserver().addOnWindowFocusChangeListener(this.mFocusChangeListener);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        if (getView() != null && getView().getViewTreeObserver() != null && this.mFocusChangeListener != null) {
            getView().getViewTreeObserver().removeOnWindowFocusChangeListener(this.mFocusChangeListener);
            this.mFocusChangeListener = null;
        }
    }
}
