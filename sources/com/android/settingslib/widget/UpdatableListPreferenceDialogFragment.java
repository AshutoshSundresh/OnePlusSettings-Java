package com.android.settingslib.widget;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceDialogFragmentCompat;
import com.android.internal.R;
import com.android.settingslib.core.instrumentation.Instrumentable;
import java.util.ArrayList;

public class UpdatableListPreferenceDialogFragment extends PreferenceDialogFragmentCompat implements Instrumentable {
    private ArrayAdapter mAdapter;
    private int mClickedDialogEntryIndex;
    private ArrayList<CharSequence> mEntries;
    private CharSequence[] mEntryValues;
    private int mMetricsCategory = 0;

    public static UpdatableListPreferenceDialogFragment newInstance(String str, int i) {
        UpdatableListPreferenceDialogFragment updatableListPreferenceDialogFragment = new UpdatableListPreferenceDialogFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", str);
        bundle.putInt("metrics_category_key", i);
        updatableListPreferenceDialogFragment.setArguments(bundle);
        return updatableListPreferenceDialogFragment;
    }

    @Override // androidx.preference.PreferenceDialogFragmentCompat, androidx.fragment.app.Fragment, androidx.fragment.app.DialogFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mMetricsCategory = getArguments().getInt("metrics_category_key", 0);
        if (bundle == null) {
            this.mEntries = new ArrayList<>();
            setPreferenceData(getListPreference());
            return;
        }
        this.mClickedDialogEntryIndex = bundle.getInt("UpdatableListPreferenceDialogFragment.index", 0);
        this.mEntries = bundle.getCharSequenceArrayList("UpdatableListPreferenceDialogFragment.entries");
        this.mEntryValues = bundle.getCharSequenceArray("UpdatableListPreferenceDialogFragment.entryValues");
    }

    @Override // androidx.preference.PreferenceDialogFragmentCompat, androidx.fragment.app.Fragment, androidx.fragment.app.DialogFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("UpdatableListPreferenceDialogFragment.index", this.mClickedDialogEntryIndex);
        bundle.putCharSequenceArrayList("UpdatableListPreferenceDialogFragment.entries", this.mEntries);
        bundle.putCharSequenceArray("UpdatableListPreferenceDialogFragment.entryValues", this.mEntryValues);
    }

    @Override // androidx.preference.PreferenceDialogFragmentCompat
    public void onDialogClosed(boolean z) {
        if (z && this.mClickedDialogEntryIndex >= 0) {
            ListPreference listPreference = getListPreference();
            String charSequence = this.mEntryValues[this.mClickedDialogEntryIndex].toString();
            if (listPreference.callChangeListener(charSequence)) {
                listPreference.setValue(charSequence);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setAdapter(ArrayAdapter arrayAdapter) {
        this.mAdapter = arrayAdapter;
    }

    /* access modifiers changed from: package-private */
    public void setEntries(ArrayList<CharSequence> arrayList) {
        this.mEntries = arrayList;
    }

    /* access modifiers changed from: package-private */
    public ArrayAdapter getAdapter() {
        return this.mAdapter;
    }

    /* access modifiers changed from: package-private */
    public void setMetricsCategory(Bundle bundle) {
        this.mMetricsCategory = bundle.getInt("metrics_category_key", 0);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.PreferenceDialogFragmentCompat
    public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(null, R.styleable.AlertDialog, 16842845, 0);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), obtainStyledAttributes.getResourceId(21, 17367058), this.mEntries);
        this.mAdapter = arrayAdapter;
        builder.setSingleChoiceItems(arrayAdapter, this.mClickedDialogEntryIndex, new DialogInterface.OnClickListener() {
            /* class com.android.settingslib.widget.$$Lambda$UpdatableListPreferenceDialogFragment$AOz45Qsiv_Tk7VzDyhpcuZlE0g */

            public final void onClick(DialogInterface dialogInterface, int i) {
                UpdatableListPreferenceDialogFragment.this.lambda$onPrepareDialogBuilder$0$UpdatableListPreferenceDialogFragment(dialogInterface, i);
            }
        });
        builder.setPositiveButton((CharSequence) null, (DialogInterface.OnClickListener) null);
        obtainStyledAttributes.recycle();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onPrepareDialogBuilder$0 */
    public /* synthetic */ void lambda$onPrepareDialogBuilder$0$UpdatableListPreferenceDialogFragment(DialogInterface dialogInterface, int i) {
        this.mClickedDialogEntryIndex = i;
        onClick(dialogInterface, -1);
        dialogInterface.dismiss();
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return this.mMetricsCategory;
    }

    /* access modifiers changed from: package-private */
    public ListPreference getListPreference() {
        return (ListPreference) getPreference();
    }

    private void setPreferenceData(ListPreference listPreference) {
        this.mEntries.clear();
        this.mClickedDialogEntryIndex = listPreference.findIndexOfValue(listPreference.getValue());
        for (CharSequence charSequence : listPreference.getEntries()) {
            this.mEntries.add(charSequence);
        }
        this.mEntryValues = listPreference.getEntryValues();
    }

    public void onListPreferenceUpdated(ListPreference listPreference) {
        if (this.mAdapter != null) {
            setPreferenceData(listPreference);
            this.mAdapter.notifyDataSetChanged();
        }
    }
}
