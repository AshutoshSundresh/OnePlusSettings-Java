package com.android.settingslib.inputmethod;

import android.content.Context;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import com.android.settingslib.R$string;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class InputMethodAndSubtypeEnablerManagerCompat implements Preference.OnPreferenceChangeListener {
    private final HashMap<String, TwoStatePreference> mAutoSelectionPrefsMap = new HashMap<>();
    private final Collator mCollator = Collator.getInstance();
    private final PreferenceFragmentCompat mFragment;
    private boolean mHaveHardKeyboard;
    private InputMethodManager mImm;
    private final HashMap<String, List<Preference>> mInputMethodAndSubtypePrefsMap = new HashMap<>();
    private List<InputMethodInfo> mInputMethodInfoList;

    public InputMethodAndSubtypeEnablerManagerCompat(PreferenceFragmentCompat preferenceFragmentCompat) {
        this.mFragment = preferenceFragmentCompat;
        InputMethodManager inputMethodManager = (InputMethodManager) preferenceFragmentCompat.getContext().getSystemService(InputMethodManager.class);
        this.mImm = inputMethodManager;
        this.mInputMethodInfoList = inputMethodManager.getInputMethodList();
    }

    public void init(PreferenceFragmentCompat preferenceFragmentCompat, String str, PreferenceScreen preferenceScreen) {
        this.mHaveHardKeyboard = preferenceFragmentCompat.getResources().getConfiguration().keyboard == 2;
        for (InputMethodInfo inputMethodInfo : this.mInputMethodInfoList) {
            if (inputMethodInfo.getId().equals(str) || TextUtils.isEmpty(str)) {
                addInputMethodSubtypePreferences(preferenceFragmentCompat, inputMethodInfo, preferenceScreen);
            }
        }
    }

    public void refresh(Context context, PreferenceFragmentCompat preferenceFragmentCompat) {
        InputMethodSettingValuesWrapper.getInstance(context).refreshAllInputMethodAndSubtypes();
        InputMethodAndSubtypeUtilCompat.loadInputMethodSubtypeList(preferenceFragmentCompat, context.getContentResolver(), this.mInputMethodInfoList, this.mInputMethodAndSubtypePrefsMap);
        updateAutoSelectionPreferences();
    }

    public void save(Context context, PreferenceFragmentCompat preferenceFragmentCompat) {
        InputMethodAndSubtypeUtilCompat.saveInputMethodSubtypeList(preferenceFragmentCompat, context.getContentResolver(), this.mInputMethodInfoList, this.mHaveHardKeyboard);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!(obj instanceof Boolean)) {
            return true;
        }
        boolean booleanValue = ((Boolean) obj).booleanValue();
        for (String str : this.mAutoSelectionPrefsMap.keySet()) {
            if (this.mAutoSelectionPrefsMap.get(str) == preference) {
                TwoStatePreference twoStatePreference = (TwoStatePreference) preference;
                twoStatePreference.setChecked(booleanValue);
                setAutoSelectionSubtypesEnabled(str, twoStatePreference.isChecked());
                return false;
            }
        }
        if (!(preference instanceof InputMethodSubtypePreference)) {
            return true;
        }
        InputMethodSubtypePreference inputMethodSubtypePreference = (InputMethodSubtypePreference) preference;
        inputMethodSubtypePreference.setChecked(booleanValue);
        if (!inputMethodSubtypePreference.isChecked()) {
            updateAutoSelectionPreferences();
        }
        return false;
    }

    private void addInputMethodSubtypePreferences(PreferenceFragmentCompat preferenceFragmentCompat, InputMethodInfo inputMethodInfo, PreferenceScreen preferenceScreen) {
        Context context = preferenceFragmentCompat.getPreferenceManager().getContext();
        int subtypeCount = inputMethodInfo.getSubtypeCount();
        if (subtypeCount > 1) {
            String id = inputMethodInfo.getId();
            PreferenceCategory preferenceCategory = new PreferenceCategory(context);
            preferenceScreen.addPreference(preferenceCategory);
            preferenceCategory.setTitle(inputMethodInfo.loadLabel(context.getPackageManager()));
            preferenceCategory.setKey(id);
            SwitchWithNoTextPreference switchWithNoTextPreference = new SwitchWithNoTextPreference(context);
            this.mAutoSelectionPrefsMap.put(id, switchWithNoTextPreference);
            preferenceCategory.addPreference(switchWithNoTextPreference);
            switchWithNoTextPreference.setOnPreferenceChangeListener(this);
            PreferenceCategory preferenceCategory2 = new PreferenceCategory(context);
            preferenceCategory2.setTitle(R$string.active_input_method_subtypes);
            preferenceScreen.addPreference(preferenceCategory2);
            String str = null;
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < subtypeCount; i++) {
                InputMethodSubtype subtypeAt = inputMethodInfo.getSubtypeAt(i);
                if (!subtypeAt.overridesImplicitlyEnabledSubtype()) {
                    arrayList.add(new InputMethodSubtypePreference(context, subtypeAt, inputMethodInfo));
                } else if (str == null) {
                    str = InputMethodAndSubtypeUtil.getSubtypeLocaleNameAsSentence(subtypeAt, context, inputMethodInfo);
                }
            }
            arrayList.sort(new Comparator() {
                /* class com.android.settingslib.inputmethod.$$Lambda$InputMethodAndSubtypeEnablerManagerCompat$LAzLoY5JDn4eaG08pP_eZZ7DnU */

                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return InputMethodAndSubtypeEnablerManagerCompat.this.lambda$addInputMethodSubtypePreferences$0$InputMethodAndSubtypeEnablerManagerCompat((Preference) obj, (Preference) obj2);
                }
            });
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                Preference preference = (Preference) it.next();
                preferenceCategory2.addPreference(preference);
                preference.setOnPreferenceChangeListener(this);
                InputMethodAndSubtypeUtil.removeUnnecessaryNonPersistentPreference(preference);
            }
            this.mInputMethodAndSubtypePrefsMap.put(id, arrayList);
            if (TextUtils.isEmpty(str)) {
                switchWithNoTextPreference.setTitle(R$string.use_system_language_to_select_input_method_subtypes);
            } else {
                switchWithNoTextPreference.setTitle(str);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$addInputMethodSubtypePreferences$0 */
    public /* synthetic */ int lambda$addInputMethodSubtypePreferences$0$InputMethodAndSubtypeEnablerManagerCompat(Preference preference, Preference preference2) {
        if (preference instanceof InputMethodSubtypePreference) {
            return ((InputMethodSubtypePreference) preference).compareTo(preference2, this.mCollator);
        }
        return preference.compareTo(preference2);
    }

    private boolean isNoSubtypesExplicitlySelected(String str) {
        for (Preference preference : this.mInputMethodAndSubtypePrefsMap.get(str)) {
            if ((preference instanceof TwoStatePreference) && ((TwoStatePreference) preference).isChecked()) {
                return false;
            }
        }
        return true;
    }

    private void setAutoSelectionSubtypesEnabled(String str, boolean z) {
        TwoStatePreference twoStatePreference = this.mAutoSelectionPrefsMap.get(str);
        if (twoStatePreference != null) {
            twoStatePreference.setChecked(z);
            for (Preference preference : this.mInputMethodAndSubtypePrefsMap.get(str)) {
                if (preference instanceof TwoStatePreference) {
                    preference.setEnabled(!z);
                    if (z) {
                        ((TwoStatePreference) preference).setChecked(false);
                    }
                }
            }
            if (z) {
                PreferenceFragmentCompat preferenceFragmentCompat = this.mFragment;
                InputMethodAndSubtypeUtilCompat.saveInputMethodSubtypeList(preferenceFragmentCompat, preferenceFragmentCompat.getContext().getContentResolver(), this.mInputMethodInfoList, this.mHaveHardKeyboard);
                updateImplicitlyEnabledSubtypes(str);
            }
        }
    }

    private void updateImplicitlyEnabledSubtypes(String str) {
        for (InputMethodInfo inputMethodInfo : this.mInputMethodInfoList) {
            String id = inputMethodInfo.getId();
            TwoStatePreference twoStatePreference = this.mAutoSelectionPrefsMap.get(id);
            if (twoStatePreference != null && twoStatePreference.isChecked()) {
                if (id.equals(str) || str == null) {
                    updateImplicitlyEnabledSubtypesOf(inputMethodInfo);
                }
            }
        }
    }

    private void updateImplicitlyEnabledSubtypesOf(InputMethodInfo inputMethodInfo) {
        String id = inputMethodInfo.getId();
        List<Preference> list = this.mInputMethodAndSubtypePrefsMap.get(id);
        List<InputMethodSubtype> enabledInputMethodSubtypeList = this.mImm.getEnabledInputMethodSubtypeList(inputMethodInfo, true);
        if (!(list == null || enabledInputMethodSubtypeList == null)) {
            for (Preference preference : list) {
                if (preference instanceof TwoStatePreference) {
                    TwoStatePreference twoStatePreference = (TwoStatePreference) preference;
                    twoStatePreference.setChecked(false);
                    Iterator<InputMethodSubtype> it = enabledInputMethodSubtypeList.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        if (twoStatePreference.getKey().equals(id + it.next().hashCode())) {
                            twoStatePreference.setChecked(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void updateAutoSelectionPreferences() {
        for (String str : this.mInputMethodAndSubtypePrefsMap.keySet()) {
            setAutoSelectionSubtypesEnabled(str, isNoSubtypesExplicitlySelected(str));
        }
        updateImplicitlyEnabledSubtypes(null);
    }
}
