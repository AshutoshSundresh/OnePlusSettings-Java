package com.android.settings.localepicker;

import android.app.Dialog;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.FragmentActivity;
import com.android.internal.app.LocaleStore;
import com.android.settings.C0017R$string;
import com.android.settings.DialogCreatable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.localepicker.OPLocalePickerBase;
import com.oneplus.settings.utils.OPUtils;
import java.util.Locale;

public class OPLocalePicker extends OPLocalePickerBase implements OPLocalePickerBase.LocaleSelectionListener, DialogCreatable {
    private SettingsPreferenceFragment.SettingsDialogFragment mDialogFragment;
    private Locale mTargetLocale;

    @Override // com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return 0;
    }

    public OPLocalePicker() {
        setLocaleSelectionListener(this);
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null && bundle.containsKey("locale")) {
            this.mTargetLocale = new Locale(bundle.getString("locale"));
        }
        setHasOptionsMenu(true);
    }

    @Override // androidx.fragment.app.Fragment, androidx.fragment.app.ListFragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        Utils.forcePrepareCustomPreferencesList(viewGroup, onCreateView, (ListView) onCreateView.findViewById(16908298), false);
        return onCreateView;
    }

    @Override // com.android.settings.localepicker.OPLocalePickerBase.LocaleSelectionListener
    public void onLocaleSelected(Locale locale) {
        getActivity().onBackPressed();
        LocaleList localeList = new LocaleList("en-US".equals(LocaleStore.getLocaleInfo(locale).getId()) ? new Locale[]{locale} : new Locale[]{locale, Locale.forLanguageTag("en-US")});
        LocaleList.setDefault(localeList);
        OPLocalePickerBase.updateLocales(localeList);
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Locale locale = this.mTargetLocale;
        if (locale != null) {
            bundle.putString("locale", locale.toString());
        }
    }

    @Override // com.android.settings.DialogCreatable
    public Dialog onCreateDialog(final int i) {
        int i2;
        FragmentActivity activity = getActivity();
        if (OPUtils.isO2()) {
            i2 = C0017R$string.language_picker_title_o2;
        } else {
            i2 = C0017R$string.language_picker_title;
        }
        return Utils.buildGlobalChangeWarningDialog(activity, i2, new Runnable() {
            /* class com.android.settings.localepicker.OPLocalePicker.AnonymousClass1 */

            public void run() {
                OPLocalePicker.this.removeDialog(i);
                OPLocalePicker.this.getActivity().onBackPressed();
                OPLocalePickerBase.updateLocale(OPLocalePicker.this.mTargetLocale);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void removeDialog(int i) {
        SettingsPreferenceFragment.SettingsDialogFragment settingsDialogFragment = this.mDialogFragment;
        if (settingsDialogFragment != null && settingsDialogFragment.getDialogId() == i) {
            this.mDialogFragment.dismiss();
        }
        this.mDialogFragment = null;
    }
}
