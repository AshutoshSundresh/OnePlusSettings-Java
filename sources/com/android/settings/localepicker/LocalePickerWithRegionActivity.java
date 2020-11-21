package com.android.settings.localepicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import com.android.internal.app.LocalePickerWithRegion;
import com.android.internal.app.LocaleStore;
import com.android.settings.C0007R$dimen;
import java.io.Serializable;

public class LocalePickerWithRegionActivity extends Activity implements LocalePickerWithRegion.LocaleSelectedListener {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTheme(16974371);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        final LocalePickerWithRegion createLanguagePicker = LocalePickerWithRegion.createLanguagePicker(this, this, false);
        getFragmentManager().beginTransaction().setTransition(4097).replace(16908290, createLanguagePicker).addToBackStack("localeListEditor").commit();
        new Handler().post(new Runnable() {
            /* class com.android.settings.localepicker.LocalePickerWithRegionActivity.AnonymousClass1 */

            public void run() {
                LocalePickerWithRegionActivity.this.findViewById(16908290).setPadding(0, LocalePickerWithRegionActivity.this.getResources().getDimensionPixelSize(C0007R$dimen.op_control_margin_space4), 0, 0);
                createLanguagePicker.getListView().setDivider(null);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        handleBackPressed();
        return true;
    }

    public void onLocaleSelected(LocaleStore.LocaleInfo localeInfo) {
        Intent intent = new Intent();
        intent.putExtra("localeInfo", (Serializable) localeInfo);
        setResult(-1, intent);
        finish();
    }

    public void onBackPressed() {
        handleBackPressed();
    }

    private void handleBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
            return;
        }
        setResult(0);
        finish();
    }
}
