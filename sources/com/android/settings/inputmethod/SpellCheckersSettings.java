package com.android.settings.inputmethod;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.SpellCheckerSubtype;
import android.view.textservice.TextServicesManager;
import android.widget.Switch;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SwitchBar;

public class SpellCheckersSettings extends SettingsPreferenceFragment implements SwitchBar.OnSwitchChangeListener, Preference.OnPreferenceChangeListener {
    private SpellCheckerInfo mCurrentSci;
    private AlertDialog mDialog = null;
    private SpellCheckerInfo[] mEnabledScis;
    private Preference mSpellCheckerLanaguagePref;
    private SwitchBar mSwitchBar;
    private TextServicesManager mTsm;

    /* access modifiers changed from: private */
    public static int convertDialogItemIdToSubtypeIndex(int i) {
        return i - 1;
    }

    private static int convertSubtypeIndexToDialogItemId(int i) {
        return i + 1;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 59;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.spellchecker_prefs);
        this.mSpellCheckerLanaguagePref = findPreference("spellchecker_language");
        TextServicesManager textServicesManager = (TextServicesManager) getSystemService("textservices");
        this.mTsm = textServicesManager;
        this.mCurrentSci = textServicesManager.getCurrentSpellChecker();
        this.mEnabledScis = this.mTsm.getEnabledSpellCheckers();
        populatePreferenceScreen();
    }

    private void populatePreferenceScreen() {
        SpellCheckerPreference spellCheckerPreference = new SpellCheckerPreference(getPrefContext(), this.mEnabledScis);
        spellCheckerPreference.setTitle(C0017R$string.default_spell_checker);
        SpellCheckerInfo[] spellCheckerInfoArr = this.mEnabledScis;
        if ((spellCheckerInfoArr == null ? 0 : spellCheckerInfoArr.length) > 0) {
            spellCheckerPreference.setSummary("%s");
        } else {
            spellCheckerPreference.setSummary(C0017R$string.spell_checker_not_selected);
        }
        spellCheckerPreference.setKey("default_spellchecker");
        spellCheckerPreference.setOnPreferenceChangeListener(this);
        getPreferenceScreen().addPreference(spellCheckerPreference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        SwitchBar switchBar = ((SettingsActivity) getActivity()).getSwitchBar();
        this.mSwitchBar = switchBar;
        int i = C0017R$string.spell_checker_master_switch_title;
        switchBar.setSwitchBarText(i, i);
        this.mSwitchBar.show();
        this.mSwitchBar.addOnSwitchChangeListener(this);
        updatePreferenceScreen();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mSwitchBar.removeOnSwitchChangeListener(this);
    }

    @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
    public void onSwitchChanged(Switch r2, boolean z) {
        Settings.Secure.putInt(getContentResolver(), "spell_checker_enabled", z ? 1 : 0);
        updatePreferenceScreen();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updatePreferenceScreen() {
        this.mCurrentSci = this.mTsm.getCurrentSpellChecker();
        boolean isSpellCheckerEnabled = this.mTsm.isSpellCheckerEnabled();
        this.mSwitchBar.setChecked(isSpellCheckerEnabled);
        boolean z = false;
        this.mSpellCheckerLanaguagePref.setSummary(getSpellCheckerSubtypeLabel(this.mCurrentSci, this.mCurrentSci != null ? this.mTsm.getCurrentSpellCheckerSubtype(false) : null));
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int preferenceCount = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            preference.setEnabled(isSpellCheckerEnabled);
            if (preference instanceof SpellCheckerPreference) {
                ((SpellCheckerPreference) preference).setSelected(this.mCurrentSci);
            }
        }
        Preference preference2 = this.mSpellCheckerLanaguagePref;
        if (isSpellCheckerEnabled && this.mCurrentSci != null) {
            z = true;
        }
        preference2.setEnabled(z);
    }

    private CharSequence getSpellCheckerSubtypeLabel(SpellCheckerInfo spellCheckerInfo, SpellCheckerSubtype spellCheckerSubtype) {
        if (spellCheckerInfo == null) {
            return getString(C0017R$string.spell_checker_not_selected);
        }
        if (spellCheckerSubtype == null) {
            return getString(C0017R$string.use_system_language_to_select_input_method_subtypes);
        }
        return spellCheckerSubtype.getDisplayName(getActivity(), spellCheckerInfo.getPackageName(), spellCheckerInfo.getServiceInfo().applicationInfo);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (!"spellchecker_language".equals(preference.getKey())) {
            return super.onPreferenceTreeClick(preference);
        }
        writePreferenceClickMetric(preference);
        showChooseLanguageDialog();
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        SpellCheckerInfo spellCheckerInfo = (SpellCheckerInfo) obj;
        if ((spellCheckerInfo.getServiceInfo().applicationInfo.flags & 1) != 0) {
            changeCurrentSpellChecker(spellCheckerInfo);
            return true;
        }
        showSecurityWarnDialog(spellCheckerInfo);
        return false;
    }

    private void showChooseLanguageDialog() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        final SpellCheckerInfo currentSpellChecker = this.mTsm.getCurrentSpellChecker();
        if (currentSpellChecker != null) {
            SpellCheckerSubtype currentSpellCheckerSubtype = this.mTsm.getCurrentSpellCheckerSubtype(false);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(C0017R$string.phone_language);
            int subtypeCount = currentSpellChecker.getSubtypeCount();
            CharSequence[] charSequenceArr = new CharSequence[(subtypeCount + 1)];
            charSequenceArr[0] = getSpellCheckerSubtypeLabel(currentSpellChecker, null);
            int i = 0;
            for (int i2 = 0; i2 < subtypeCount; i2++) {
                SpellCheckerSubtype subtypeAt = currentSpellChecker.getSubtypeAt(i2);
                int convertSubtypeIndexToDialogItemId = convertSubtypeIndexToDialogItemId(i2);
                charSequenceArr[convertSubtypeIndexToDialogItemId] = getSpellCheckerSubtypeLabel(currentSpellChecker, subtypeAt);
                if (subtypeAt.equals(currentSpellCheckerSubtype)) {
                    i = convertSubtypeIndexToDialogItemId;
                }
            }
            builder.setSingleChoiceItems(charSequenceArr, i, new DialogInterface.OnClickListener() {
                /* class com.android.settings.inputmethod.SpellCheckersSettings.AnonymousClass1 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    int i2;
                    if (i == 0) {
                        i2 = 0;
                    } else {
                        i2 = currentSpellChecker.getSubtypeAt(SpellCheckersSettings.convertDialogItemIdToSubtypeIndex(i)).hashCode();
                    }
                    Settings.Secure.putInt(SpellCheckersSettings.this.getContentResolver(), "selected_spell_checker_subtype", i2);
                    dialogInterface.dismiss();
                    SpellCheckersSettings.this.updatePreferenceScreen();
                }
            });
            AlertDialog create = builder.create();
            this.mDialog = create;
            create.show();
        }
    }

    private void showSecurityWarnDialog(final SpellCheckerInfo spellCheckerInfo) {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(17039380);
        builder.setMessage(getString(C0017R$string.spellchecker_security_warning, spellCheckerInfo.loadLabel(getPackageManager())));
        builder.setCancelable(true);
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            /* class com.android.settings.inputmethod.SpellCheckersSettings.AnonymousClass2 */

            public void onClick(DialogInterface dialogInterface, int i) {
                SpellCheckersSettings.this.changeCurrentSpellChecker(spellCheckerInfo);
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener(this) {
            /* class com.android.settings.inputmethod.SpellCheckersSettings.AnonymousClass3 */

            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void changeCurrentSpellChecker(SpellCheckerInfo spellCheckerInfo) {
        Settings.Secure.putString(getContentResolver(), "selected_spell_checker", spellCheckerInfo.getId());
        Settings.Secure.putInt(getContentResolver(), "selected_spell_checker_subtype", 0);
        updatePreferenceScreen();
    }
}
