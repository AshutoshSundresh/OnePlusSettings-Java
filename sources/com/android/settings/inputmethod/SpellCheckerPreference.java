package com.android.settings.inputmethod;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.textservice.SpellCheckerInfo;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.CustomListPreference;

/* access modifiers changed from: package-private */
public class SpellCheckerPreference extends CustomListPreference {
    private Intent mIntent;
    private final SpellCheckerInfo[] mScis;

    public SpellCheckerPreference(Context context, SpellCheckerInfo[] spellCheckerInfoArr) {
        super(context, null);
        this.mScis = spellCheckerInfoArr;
        setWidgetLayoutResource(C0012R$layout.preference_widget_gear);
        CharSequence[] charSequenceArr = new CharSequence[spellCheckerInfoArr.length];
        CharSequence[] charSequenceArr2 = new CharSequence[spellCheckerInfoArr.length];
        for (int i = 0; i < spellCheckerInfoArr.length; i++) {
            charSequenceArr[i] = spellCheckerInfoArr[i].loadLabel(context.getPackageManager());
            charSequenceArr2[i] = String.valueOf(i);
        }
        setEntries(charSequenceArr);
        setEntryValues(charSequenceArr2);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.CustomListPreference
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        builder.setTitle(C0017R$string.choose_spell_checker);
        builder.setSingleChoiceItems(getEntries(), findIndexOfValue(getValue()), onClickListener);
    }

    public void setSelected(SpellCheckerInfo spellCheckerInfo) {
        if (spellCheckerInfo == null) {
            setValue(null);
            return;
        }
        int i = 0;
        while (true) {
            SpellCheckerInfo[] spellCheckerInfoArr = this.mScis;
            if (i >= spellCheckerInfoArr.length) {
                return;
            }
            if (spellCheckerInfoArr[i].getId().equals(spellCheckerInfo.getId())) {
                setValueIndex(i);
                return;
            }
            i++;
        }
    }

    @Override // androidx.preference.ListPreference
    public void setValue(String str) {
        super.setValue(str);
        int parseInt = str != null ? Integer.parseInt(str) : -1;
        if (parseInt == -1) {
            this.mIntent = null;
            return;
        }
        SpellCheckerInfo spellCheckerInfo = this.mScis[parseInt];
        String settingsActivity = spellCheckerInfo.getSettingsActivity();
        if (TextUtils.isEmpty(settingsActivity)) {
            this.mIntent = null;
            return;
        }
        Intent intent = new Intent("android.intent.action.MAIN");
        this.mIntent = intent;
        intent.setClassName(spellCheckerInfo.getPackageName(), settingsActivity);
    }

    @Override // androidx.preference.Preference
    public boolean callChangeListener(Object obj) {
        return super.callChangeListener(obj != null ? this.mScis[Integer.parseInt((String) obj)] : null);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(C0010R$id.settings_button);
        findViewById.setVisibility(this.mIntent != null ? 0 : 4);
        findViewById.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.inputmethod.SpellCheckerPreference.AnonymousClass1 */

            public void onClick(View view) {
                SpellCheckerPreference.this.onSettingsButtonClicked();
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onSettingsButtonClicked() {
        Context context = getContext();
        try {
            Intent intent = this.mIntent;
            if (intent != null) {
                context.startActivity(intent);
            }
        } catch (ActivityNotFoundException unused) {
        }
    }
}
