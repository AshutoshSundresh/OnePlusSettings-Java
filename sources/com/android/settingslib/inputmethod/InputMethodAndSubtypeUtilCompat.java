package com.android.settingslib.inputmethod;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InputMethodAndSubtypeUtilCompat {
    private static final TextUtils.SimpleStringSplitter sStringInputMethodSplitter = new TextUtils.SimpleStringSplitter(':');
    private static final TextUtils.SimpleStringSplitter sStringInputMethodSubtypeSplitter = new TextUtils.SimpleStringSplitter(';');

    public static String buildInputMethodsAndSubtypesString(HashMap<String, HashSet<String>> hashMap) {
        StringBuilder sb = new StringBuilder();
        for (String str : hashMap.keySet()) {
            if (sb.length() > 0) {
                sb.append(':');
            }
            sb.append(str);
            Iterator<String> it = hashMap.get(str).iterator();
            while (it.hasNext()) {
                sb.append(';');
                sb.append(it.next());
            }
        }
        return sb.toString();
    }

    private static String buildInputMethodsString(HashSet<String> hashSet) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = hashSet.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (sb.length() > 0) {
                sb.append(':');
            }
            sb.append(next);
        }
        return sb.toString();
    }

    private static int getInputMethodSubtypeSelected(ContentResolver contentResolver) {
        try {
            return Settings.Secure.getInt(contentResolver, "selected_input_method_subtype");
        } catch (Settings.SettingNotFoundException unused) {
            return -1;
        }
    }

    private static boolean isInputMethodSubtypeSelected(ContentResolver contentResolver) {
        return getInputMethodSubtypeSelected(contentResolver) != -1;
    }

    private static void putSelectedInputMethodSubtype(ContentResolver contentResolver, int i) {
        Settings.Secure.putInt(contentResolver, "selected_input_method_subtype", i);
    }

    static HashMap<String, HashSet<String>> getEnabledInputMethodsAndSubtypeList(ContentResolver contentResolver) {
        return parseInputMethodsAndSubtypesString(Settings.Secure.getString(contentResolver, "enabled_input_methods"));
    }

    public static HashMap<String, HashSet<String>> parseInputMethodsAndSubtypesString(String str) {
        HashMap<String, HashSet<String>> hashMap = new HashMap<>();
        if (TextUtils.isEmpty(str)) {
            return hashMap;
        }
        sStringInputMethodSplitter.setString(str);
        while (sStringInputMethodSplitter.hasNext()) {
            sStringInputMethodSubtypeSplitter.setString(sStringInputMethodSplitter.next());
            if (sStringInputMethodSubtypeSplitter.hasNext()) {
                HashSet<String> hashSet = new HashSet<>();
                String next = sStringInputMethodSubtypeSplitter.next();
                while (sStringInputMethodSubtypeSplitter.hasNext()) {
                    hashSet.add(sStringInputMethodSubtypeSplitter.next());
                }
                hashMap.put(next, hashSet);
            }
        }
        return hashMap;
    }

    private static HashSet<String> getDisabledSystemIMEs(ContentResolver contentResolver) {
        HashSet<String> hashSet = new HashSet<>();
        String string = Settings.Secure.getString(contentResolver, "disabled_system_input_methods");
        if (TextUtils.isEmpty(string)) {
            return hashSet;
        }
        sStringInputMethodSplitter.setString(string);
        while (sStringInputMethodSplitter.hasNext()) {
            hashSet.add(sStringInputMethodSplitter.next());
        }
        return hashSet;
    }

    public static void saveInputMethodSubtypeList(PreferenceFragmentCompat preferenceFragmentCompat, ContentResolver contentResolver, List<InputMethodInfo> list, boolean z) {
        boolean z2;
        Iterator<InputMethodInfo> it;
        String string = Settings.Secure.getString(contentResolver, "default_input_method");
        int inputMethodSubtypeSelected = getInputMethodSubtypeSelected(contentResolver);
        HashMap<String, HashSet<String>> enabledInputMethodsAndSubtypeList = getEnabledInputMethodsAndSubtypeList(contentResolver);
        HashSet<String> disabledSystemIMEs = getDisabledSystemIMEs(contentResolver);
        Iterator<InputMethodInfo> it2 = list.iterator();
        boolean z3 = false;
        while (it2.hasNext()) {
            InputMethodInfo next = it2.next();
            String id = next.getId();
            Preference findPreference = preferenceFragmentCompat.findPreference(id);
            if (findPreference != null) {
                if (findPreference instanceof TwoStatePreference) {
                    z2 = ((TwoStatePreference) findPreference).isChecked();
                } else {
                    z2 = enabledInputMethodsAndSubtypeList.containsKey(id);
                }
                boolean equals = id.equals(string);
                boolean isSystem = next.isSystem();
                if ((z || !InputMethodSettingValuesWrapper.getInstance(preferenceFragmentCompat.getActivity()).isAlwaysCheckedIme(next)) && !z2) {
                    it = it2;
                    enabledInputMethodsAndSubtypeList.remove(id);
                    if (equals) {
                        string = null;
                    }
                } else {
                    if (!enabledInputMethodsAndSubtypeList.containsKey(id)) {
                        enabledInputMethodsAndSubtypeList.put(id, new HashSet<>());
                    }
                    HashSet<String> hashSet = enabledInputMethodsAndSubtypeList.get(id);
                    it = it2;
                    int i = 0;
                    boolean z4 = false;
                    for (int subtypeCount = next.getSubtypeCount(); i < subtypeCount; subtypeCount = subtypeCount) {
                        InputMethodSubtype subtypeAt = next.getSubtypeAt(i);
                        String valueOf = String.valueOf(subtypeAt.hashCode());
                        boolean z5 = z3;
                        TwoStatePreference twoStatePreference = (TwoStatePreference) preferenceFragmentCompat.findPreference(id + valueOf);
                        if (twoStatePreference != null) {
                            if (!z4) {
                                hashSet.clear();
                                z4 = true;
                                z5 = true;
                            }
                            if (!twoStatePreference.isEnabled() || !twoStatePreference.isChecked()) {
                                hashSet.remove(valueOf);
                            } else {
                                hashSet.add(valueOf);
                                if (equals && inputMethodSubtypeSelected == subtypeAt.hashCode()) {
                                    z3 = false;
                                    i++;
                                }
                            }
                        }
                        z3 = z5;
                        i++;
                    }
                }
                if (isSystem && z) {
                    if (disabledSystemIMEs.contains(id)) {
                        if (z2) {
                            disabledSystemIMEs.remove(id);
                        }
                    } else if (!z2) {
                        disabledSystemIMEs.add(id);
                    }
                }
                it2 = it;
            }
        }
        String buildInputMethodsAndSubtypesString = buildInputMethodsAndSubtypesString(enabledInputMethodsAndSubtypeList);
        String buildInputMethodsString = buildInputMethodsString(disabledSystemIMEs);
        if (z3 || !isInputMethodSubtypeSelected(contentResolver)) {
            putSelectedInputMethodSubtype(contentResolver, -1);
        }
        Settings.Secure.putString(contentResolver, "enabled_input_methods", buildInputMethodsAndSubtypesString);
        if (buildInputMethodsString.length() > 0) {
            Settings.Secure.putString(contentResolver, "disabled_system_input_methods", buildInputMethodsString);
        }
        if (string == null) {
            string = "";
        }
        Settings.Secure.putString(contentResolver, "default_input_method", string);
    }

    public static void loadInputMethodSubtypeList(PreferenceFragmentCompat preferenceFragmentCompat, ContentResolver contentResolver, List<InputMethodInfo> list, Map<String, List<Preference>> map) {
        HashMap<String, HashSet<String>> enabledInputMethodsAndSubtypeList = getEnabledInputMethodsAndSubtypeList(contentResolver);
        for (InputMethodInfo inputMethodInfo : list) {
            String id = inputMethodInfo.getId();
            Preference findPreference = preferenceFragmentCompat.findPreference(id);
            if (findPreference instanceof TwoStatePreference) {
                boolean containsKey = enabledInputMethodsAndSubtypeList.containsKey(id);
                ((TwoStatePreference) findPreference).setChecked(containsKey);
                if (map != null) {
                    for (Preference preference : map.get(id)) {
                        preference.setEnabled(containsKey);
                    }
                }
                setSubtypesPreferenceEnabled(preferenceFragmentCompat, list, id, containsKey);
            }
        }
        updateSubtypesPreferenceChecked(preferenceFragmentCompat, list, enabledInputMethodsAndSubtypeList);
    }

    private static void setSubtypesPreferenceEnabled(PreferenceFragmentCompat preferenceFragmentCompat, List<InputMethodInfo> list, String str, boolean z) {
        PreferenceScreen preferenceScreen = preferenceFragmentCompat.getPreferenceScreen();
        for (InputMethodInfo inputMethodInfo : list) {
            if (str.equals(inputMethodInfo.getId())) {
                int subtypeCount = inputMethodInfo.getSubtypeCount();
                for (int i = 0; i < subtypeCount; i++) {
                    InputMethodSubtype subtypeAt = inputMethodInfo.getSubtypeAt(i);
                    TwoStatePreference twoStatePreference = (TwoStatePreference) preferenceScreen.findPreference(str + subtypeAt.hashCode());
                    if (twoStatePreference != null) {
                        twoStatePreference.setEnabled(z);
                    }
                }
            }
        }
    }

    private static void updateSubtypesPreferenceChecked(PreferenceFragmentCompat preferenceFragmentCompat, List<InputMethodInfo> list, HashMap<String, HashSet<String>> hashMap) {
        PreferenceScreen preferenceScreen = preferenceFragmentCompat.getPreferenceScreen();
        for (InputMethodInfo inputMethodInfo : list) {
            String id = inputMethodInfo.getId();
            if (hashMap.containsKey(id)) {
                HashSet<String> hashSet = hashMap.get(id);
                int subtypeCount = inputMethodInfo.getSubtypeCount();
                for (int i = 0; i < subtypeCount; i++) {
                    String valueOf = String.valueOf(inputMethodInfo.getSubtypeAt(i).hashCode());
                    TwoStatePreference twoStatePreference = (TwoStatePreference) preferenceScreen.findPreference(id + valueOf);
                    if (twoStatePreference != null) {
                        twoStatePreference.setChecked(hashSet.contains(valueOf));
                    }
                }
            }
        }
    }

    public static void removeUnnecessaryNonPersistentPreference(Preference preference) {
        SharedPreferences sharedPreferences;
        String key = preference.getKey();
        if (!preference.isPersistent() && key != null && (sharedPreferences = preference.getSharedPreferences()) != null && sharedPreferences.contains(key)) {
            sharedPreferences.edit().remove(key).apply();
        }
    }
}
