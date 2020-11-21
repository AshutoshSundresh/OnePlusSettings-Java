package com.android.settings.datausage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SpinnerAdapter;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.datausage.CycleAdapter;
import com.android.settingslib.widget.settingsspinner.SettingsSpinner;

public class SpinnerPreference extends Preference implements CycleAdapter.SpinnerInterface {
    private CycleAdapter mAdapter;
    private Object mCurrentObject;
    private AdapterView.OnItemSelectedListener mListener;
    private final AdapterView.OnItemSelectedListener mOnSelectedListener = new AdapterView.OnItemSelectedListener() {
        /* class com.android.settings.datausage.SpinnerPreference.AnonymousClass1 */

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            if (SpinnerPreference.this.mPosition != i) {
                SpinnerPreference.this.mPosition = i;
                SpinnerPreference spinnerPreference = SpinnerPreference.this;
                spinnerPreference.mCurrentObject = spinnerPreference.mAdapter.getItem(i);
                SpinnerPreference.this.mListener.onItemSelected(adapterView, view, i, j);
            }
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> adapterView) {
            SpinnerPreference.this.mListener.onNothingSelected(adapterView);
        }
    };
    private int mPosition;

    public SpinnerPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(C0012R$layout.data_usage_cycles);
    }

    @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
    public void setAdapter(CycleAdapter cycleAdapter) {
        this.mAdapter = cycleAdapter;
        notifyChanged();
    }

    @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.mListener = onItemSelectedListener;
    }

    @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
    public Object getSelectedItem() {
        return this.mCurrentObject;
    }

    @Override // com.android.settings.datausage.CycleAdapter.SpinnerInterface
    public void setSelection(int i) {
        this.mPosition = i;
        this.mCurrentObject = this.mAdapter.getItem(i);
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        SettingsSpinner settingsSpinner = (SettingsSpinner) preferenceViewHolder.findViewById(C0010R$id.cycles_spinner);
        settingsSpinner.setAdapter((SpinnerAdapter) this.mAdapter);
        settingsSpinner.setSelection(this.mPosition);
        settingsSpinner.setOnItemSelectedListener(this.mOnSelectedListener);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void performClick(View view) {
        view.findViewById(C0010R$id.cycles_spinner).performClick();
    }
}
