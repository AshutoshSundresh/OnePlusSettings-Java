package com.android.settings.datausage;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.datausage.DataSaverBackend;

public class DataSaverPreference extends Preference implements DataSaverBackend.Listener {
    private final DataSaverBackend mDataSaverBackend;

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onBlacklistStatusChanged(int i, boolean z) {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onWhitelistStatusChanged(int i, boolean z) {
    }

    public DataSaverPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDataSaverBackend = new DataSaverBackend(context);
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mDataSaverBackend.addListener(this);
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        super.onDetached();
        this.mDataSaverBackend.remListener(this);
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        setSummary(z ? C0017R$string.data_saver_on : C0017R$string.data_saver_off);
    }
}
