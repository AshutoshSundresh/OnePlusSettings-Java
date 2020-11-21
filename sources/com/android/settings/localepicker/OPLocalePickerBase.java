package com.android.settings.localepicker;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.OpFeatures;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import androidx.fragment.app.ListFragment;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.oneplus.settings.utils.OPUtils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class OPLocalePickerBase extends ListFragment {
    private static String[] mChinaDefaultLocale = {"en-US", "zh-CN", "zh-TW"};
    private static ArrayList<String> mChinaDefaultLocaleList;
    LocaleSelectionListener mListener;

    public interface LocaleSelectionListener {
        void onLocaleSelected(Locale locale);
    }

    static {
        mChinaDefaultLocaleList = null;
        mChinaDefaultLocaleList = new ArrayList<>(Arrays.asList(mChinaDefaultLocale));
    }

    public static class LocaleInfo implements Comparable<LocaleInfo> {
        static final Collator sCollator = Collator.getInstance();
        String label;
        final Locale locale;

        public LocaleInfo(String str, Locale locale2) {
            this.label = str;
            this.locale = locale2;
        }

        public Locale getLocale() {
            return this.locale;
        }

        public String toString() {
            return this.label;
        }

        public int compareTo(LocaleInfo localeInfo) {
            return sCollator.compare(this.label, localeInfo.label);
        }
    }

    public static String[] getSystemAssetLocales() {
        return Resources.getSystem().getAssets().getLocales();
    }

    public static List<LocaleInfo> getAllAssetLocales(Context context, boolean z) {
        Resources resources = context.getResources();
        String[] systemAssetLocales = getSystemAssetLocales();
        ArrayList<String> arrayList = new ArrayList(systemAssetLocales.length);
        Collections.addAll(arrayList, systemAssetLocales);
        Collections.sort(arrayList);
        String[] stringArray = resources.getStringArray(17236133);
        String[] stringArray2 = resources.getStringArray(17236134);
        if (OpFeatures.isSupport(new int[]{0})) {
            stringArray = (String[]) Arrays.copyOf(stringArray, stringArray.length + 1);
            stringArray2 = (String[]) Arrays.copyOf(stringArray2, stringArray2.length + 1);
            stringArray[stringArray.length - 1] = "en_US";
            stringArray2[stringArray2.length - 1] = "English (United States)";
        }
        ArrayList arrayList2 = new ArrayList(arrayList.size());
        for (String str : arrayList) {
            Locale forLanguageTag = Locale.forLanguageTag(str.replace('_', '-'));
            if (forLanguageTag != null && !"und".equals(forLanguageTag.getLanguage()) && !forLanguageTag.getLanguage().isEmpty() && !forLanguageTag.getCountry().isEmpty()) {
                if (z || !LocaleList.isPseudoLocale(forLanguageTag)) {
                    if (!OpFeatures.isSupport(new int[]{0}) || (!LocaleList.isPseudoLocale(forLanguageTag) && mChinaDefaultLocaleList.contains(str))) {
                        if (arrayList2.isEmpty()) {
                            if (OpFeatures.isSupport(new int[]{0})) {
                                arrayList2.add(new LocaleInfo(toTitleCase(getDisplayName(forLanguageTag, stringArray, stringArray2)), forLanguageTag));
                            } else {
                                arrayList2.add(new LocaleInfo(toTitleCase(forLanguageTag.getDisplayLanguage(forLanguageTag)), forLanguageTag));
                            }
                        } else {
                            LocaleInfo localeInfo = (LocaleInfo) arrayList2.get(arrayList2.size() - 1);
                            if (!localeInfo.locale.getLanguage().equals(forLanguageTag.getLanguage()) || localeInfo.locale.getLanguage().equals("zz")) {
                                arrayList2.add(new LocaleInfo(toTitleCase(forLanguageTag.getDisplayLanguage(forLanguageTag)), forLanguageTag));
                            } else {
                                localeInfo.label = toTitleCase(getDisplayName(localeInfo.locale, stringArray, stringArray2));
                                arrayList2.add(new LocaleInfo(toTitleCase(getDisplayName(forLanguageTag, stringArray, stringArray2)), forLanguageTag));
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(arrayList2);
        return arrayList2;
    }

    public static ArrayAdapter<LocaleInfo> constructAdapter(Context context) {
        return constructAdapter(context, C0012R$layout.op_locale_picker_item, C0010R$id.locale);
    }

    public static ArrayAdapter<LocaleInfo> constructAdapter(Context context, final int i, final int i2) {
        final List<LocaleInfo> allAssetLocales = getAllAssetLocales(context, Settings.Global.getInt(context.getContentResolver(), "development_settings_enabled", 0) != 0);
        final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        final Locale locale = LocaleList.getDefault().get(0);
        return new ArrayAdapter<LocaleInfo>(context, i, i2, allAssetLocales) {
            /* class com.android.settings.localepicker.OPLocalePickerBase.AnonymousClass1 */

            public View getView(int i, View view, ViewGroup viewGroup) {
                CheckedTextView checkedTextView;
                if (view == null) {
                    view = layoutInflater.inflate(i, viewGroup, false);
                    checkedTextView = (CheckedTextView) view.findViewById(i2);
                    view.setTag(checkedTextView);
                } else {
                    checkedTextView = (CheckedTextView) view.getTag();
                }
                LocaleInfo localeInfo = (LocaleInfo) getItem(i);
                checkedTextView.setText(localeInfo.toString());
                checkedTextView.setTextLocale(localeInfo.getLocale());
                if (((LocaleInfo) allAssetLocales.get(i)).locale.equals(locale)) {
                    checkedTextView.setChecked(true);
                } else {
                    checkedTextView.setChecked(false);
                }
                return view;
            }
        };
    }

    private static String toTitleCase(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private static String getDisplayName(Locale locale, String[] strArr, String[] strArr2) {
        String locale2 = locale.toString();
        for (int i = 0; i < strArr.length; i++) {
            if (strArr[i].equals(locale2)) {
                return strArr2[i];
            }
        }
        return locale.getDisplayName(locale);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        OPUtils.replaceListViewForListFragment(this);
        setListAdapter(constructAdapter(getActivity()));
        getListView().setDivider(null);
    }

    public void setLocaleSelectionListener(LocaleSelectionListener localeSelectionListener) {
        this.mListener = localeSelectionListener;
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        getListView().requestFocus();
    }

    @Override // androidx.fragment.app.ListFragment
    public void onListItemClick(ListView listView, View view, int i, long j) {
        if (this.mListener != null) {
            this.mListener.onLocaleSelected(((LocaleInfo) getListAdapter().getItem(i)).locale);
        }
    }

    public static void updateLocale(Locale locale) {
        updateLocales(new LocaleList(locale));
    }

    public static void updateLocales(LocaleList localeList) {
        try {
            IActivityManager service = ActivityManager.getService();
            Configuration configuration = service.getConfiguration();
            configuration.setLocales(localeList);
            configuration.userSetLocale = true;
            service.updatePersistentConfiguration(configuration);
            BackupManager.dataChanged("com.android.providers.settings");
        } catch (RemoteException unused) {
        }
    }
}
