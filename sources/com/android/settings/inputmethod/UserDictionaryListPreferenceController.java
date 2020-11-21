package com.android.settings.inputmethod;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.provider.UserDictionary;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

public class UserDictionaryListPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart {
    public static final String USER_DICTIONARY_SETTINGS_INTENT_ACTION = "android.settings.USER_DICTIONARY_SETTINGS";
    private final String KEY_ALL_LANGUAGE = "all_languages";
    private String mLocale;
    private PreferenceScreen mScreen;

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

    public UserDictionaryListPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void setLocale(String str) {
        this.mLocale = str;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        preferenceScreen.setOrderingAsAdded(false);
        this.mScreen = preferenceScreen;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        createUserDictSettings();
    }

    /* JADX INFO: finally extract failed */
    public static TreeSet<String> getUserDictionaryLocalesSet(Context context) {
        Cursor query = context.getContentResolver().query(UserDictionary.Words.CONTENT_URI, new String[]{"locale"}, null, null, null);
        TreeSet<String> treeSet = new TreeSet<>();
        if (query == null) {
            return treeSet;
        }
        try {
            if (query.moveToFirst()) {
                int columnIndex = query.getColumnIndex("locale");
                do {
                    String string = query.getString(columnIndex);
                    if (string == null) {
                        string = "";
                    }
                    treeSet.add(string);
                } while (query.moveToNext());
            }
            query.close();
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService("input_method");
            for (InputMethodInfo inputMethodInfo : inputMethodManager.getEnabledInputMethodList()) {
                for (InputMethodSubtype inputMethodSubtype : inputMethodManager.getEnabledInputMethodSubtypeList(inputMethodInfo, true)) {
                    String locale = inputMethodSubtype.getLocale();
                    if (!TextUtils.isEmpty(locale)) {
                        treeSet.add(locale);
                    }
                }
            }
            if (!treeSet.contains(Locale.getDefault().getLanguage())) {
                treeSet.add(Locale.getDefault().toString());
            }
            return treeSet;
        } catch (Throwable th) {
            query.close();
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public TreeSet<String> getUserDictLocalesSet(Context context) {
        return getUserDictionaryLocalesSet(context);
    }

    private void createUserDictSettings() {
        TreeSet<String> userDictLocalesSet = getUserDictLocalesSet(this.mContext);
        int preferenceCount = this.mScreen.getPreferenceCount();
        String str = this.mLocale;
        if (str != null) {
            userDictLocalesSet.add(str);
        }
        if (userDictLocalesSet.size() > 1) {
            userDictLocalesSet.add("");
        }
        if (preferenceCount > 0) {
            for (int i = preferenceCount - 1; i >= 0; i--) {
                String key = this.mScreen.getPreference(i).getKey();
                if (!TextUtils.isEmpty(key) && !TextUtils.equals("all_languages", key)) {
                    if (userDictLocalesSet.isEmpty() || !userDictLocalesSet.contains(key)) {
                        PreferenceScreen preferenceScreen = this.mScreen;
                        preferenceScreen.removePreference(preferenceScreen.findPreference(key));
                    } else {
                        userDictLocalesSet.remove(key);
                    }
                }
            }
        }
        if (!userDictLocalesSet.isEmpty() || preferenceCount != 0) {
            Iterator<String> it = userDictLocalesSet.iterator();
            while (it.hasNext()) {
                Preference createUserDictionaryPreference = createUserDictionaryPreference(it.next());
                if (this.mScreen.findPreference(createUserDictionaryPreference.getKey()) == null) {
                    this.mScreen.addPreference(createUserDictionaryPreference);
                }
            }
            return;
        }
        this.mScreen.addPreference(createUserDictionaryPreference(null));
    }

    private Preference createUserDictionaryPreference(String str) {
        Preference preference = new Preference(this.mScreen.getContext());
        Intent intent = new Intent(USER_DICTIONARY_SETTINGS_INTENT_ACTION);
        if (str == null) {
            preference.setTitle(Locale.getDefault().getDisplayName());
            preference.setKey(Locale.getDefault().toString());
        } else {
            if (TextUtils.isEmpty(str)) {
                preference.setTitle(this.mContext.getString(C0017R$string.user_dict_settings_all_languages));
                preference.setKey("all_languages");
                preference.setOrder(0);
            } else {
                preference.setTitle(Utils.createLocaleFromString(str).getDisplayName());
                preference.setKey(str);
            }
            intent.putExtra("locale", str);
            preference.getExtras().putString("locale", str);
        }
        preference.setIntent(intent);
        preference.setFragment(UserDictionarySettings.class.getName());
        return preference;
    }
}
