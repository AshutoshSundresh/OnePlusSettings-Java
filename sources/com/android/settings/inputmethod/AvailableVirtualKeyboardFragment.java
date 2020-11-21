package com.android.settings.inputmethod;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceCategory;
import com.android.internal.util.Preconditions;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.OPInputMethodPreference;
import com.android.settingslib.inputmethod.InputMethodAndSubtypeUtilCompat;
import com.android.settingslib.inputmethod.InputMethodSettingValuesWrapper;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class AvailableVirtualKeyboardFragment extends SettingsPreferenceFragment implements OPInputMethodPreference.OnSavePreferenceListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.inputmethod.AvailableVirtualKeyboardFragment.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.available_virtual_keyboard;
            arrayList.add(searchIndexableResource);
            return arrayList;
        }
    };
    private PreferenceCategory mAvailableKeyboard;
    private DevicePolicyManager mDpm;
    private InputMethodManager mImm;
    private final ArrayList<OPInputMethodPreference> mInputMethodPreferenceList = new ArrayList<>();
    private InputMethodSettingValuesWrapper mInputMethodSettingValues;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 347;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment
    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(C0019R$xml.available_virtual_keyboard);
        FragmentActivity activity = getActivity();
        this.mInputMethodSettingValues = InputMethodSettingValuesWrapper.getInstance(activity);
        this.mImm = (InputMethodManager) activity.getSystemService(InputMethodManager.class);
        this.mDpm = (DevicePolicyManager) activity.getSystemService(DevicePolicyManager.class);
        this.mAvailableKeyboard = (PreferenceCategory) Preconditions.checkNotNull((PreferenceCategory) findPreference("available_keyboards"));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
        updateInputMethodPreferenceViews();
    }

    @Override // com.android.settingslib.OPInputMethodPreference.OnSavePreferenceListener
    public void onSaveInputMethodPreference(OPInputMethodPreference oPInputMethodPreference) {
        InputMethodAndSubtypeUtilCompat.saveInputMethodSubtypeList(this, getContentResolver(), this.mImm.getInputMethodList(), getResources().getConfiguration().keyboard == 2);
        this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
        Iterator<OPInputMethodPreference> it = this.mInputMethodPreferenceList.iterator();
        while (it.hasNext()) {
            it.next().updatePreferenceViews();
        }
    }

    private void updateInputMethodPreferenceViews() {
        this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
        this.mInputMethodPreferenceList.clear();
        List permittedInputMethodsForCurrentUser = this.mDpm.getPermittedInputMethodsForCurrentUser();
        Context prefContext = getPrefContext();
        List<InputMethodInfo> inputMethodList = this.mInputMethodSettingValues.getInputMethodList();
        int size = inputMethodList == null ? 0 : inputMethodList.size();
        for (int i = 0; i < size; i++) {
            InputMethodInfo inputMethodInfo = inputMethodList.get(i);
            OPInputMethodPreference oPInputMethodPreference = new OPInputMethodPreference(prefContext, inputMethodInfo, true, permittedInputMethodsForCurrentUser == null || permittedInputMethodsForCurrentUser.contains(inputMethodInfo.getPackageName()), (OPInputMethodPreference.OnSavePreferenceListener) this);
            oPInputMethodPreference.setIcon(inputMethodInfo.loadIcon(prefContext.getPackageManager()));
            this.mInputMethodPreferenceList.add(oPInputMethodPreference);
        }
        this.mInputMethodPreferenceList.sort(new Comparator(Collator.getInstance()) {
            /* class com.android.settings.inputmethod.$$Lambda$AvailableVirtualKeyboardFragment$vCdjUP8a540qakZeVUIXn3E4j9s */
            public final /* synthetic */ Collator f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return ((OPInputMethodPreference) obj).compareTo((OPInputMethodPreference) obj2, this.f$0);
            }
        });
        this.mAvailableKeyboard.removeAll();
        for (int i2 = 0; i2 < size; i2++) {
            OPInputMethodPreference oPInputMethodPreference2 = this.mInputMethodPreferenceList.get(i2);
            oPInputMethodPreference2.setOrder(i2);
            this.mAvailableKeyboard.addPreference(oPInputMethodPreference2);
            InputMethodAndSubtypeUtilCompat.removeUnnecessaryNonPersistentPreference(oPInputMethodPreference2);
            oPInputMethodPreference2.updatePreferenceViews();
        }
    }
}
