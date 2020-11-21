package com.oneplus.settings.aod;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.widget.SwitchBar;
import com.oneplus.settings.OPOnBackPressedListener;

public class AodSmartDisplaySettingsActivity extends SettingsActivity {
    private SwitchBar mSwitchBar;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.android.settings.core.SettingsBaseActivity, com.android.settings.SettingsActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Toolbar toolbar = (Toolbar) findViewById(C0010R$id.action_bar);
        toolbar.setTitle(getApplicationContext().getResources().getString(C0017R$string.oneplus_aod_smart_display_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.aod.AodSmartDisplaySettingsActivity.AnonymousClass1 */

            public void onClick(View view) {
                AodSmartDisplaySettingsActivity.this.onBackPressed();
            }
        });
        setContentView(C0012R$layout.settings_main_prefs);
        AodSmartDisplaySettingsFragment newInstance = AodSmartDisplaySettingsFragment.newInstance();
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(C0010R$id.main_content, newInstance);
        beginTransaction.commit();
        SwitchBar switchBar = (SwitchBar) findViewById(C0010R$id.switch_bar);
        this.mSwitchBar = switchBar;
        switchBar.show();
    }

    @Override // com.android.settings.SettingsActivity
    public SwitchBar getSwitchBar() {
        return this.mSwitchBar;
    }

    @Override // androidx.activity.ComponentActivity, com.android.settings.core.SettingsBaseActivity
    public void onBackPressed() {
        Fragment findFragmentById = getSupportFragmentManager().findFragmentById(C0010R$id.main_content);
        if (findFragmentById instanceof OPOnBackPressedListener) {
            ((OPOnBackPressedListener) findFragmentById).doBack();
        } else {
            super.onBackPressed();
        }
    }
}
