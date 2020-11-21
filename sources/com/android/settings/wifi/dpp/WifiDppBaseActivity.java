package com.android.settings.wifi.dpp;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0018R$style;
import com.android.settings.SetupWizardUtils;
import com.android.settings.core.InstrumentedActivity;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.oneplus.settings.utils.OPThemeUtils;
import com.oneplus.settings.utils.OPUtils;

/* access modifiers changed from: package-private */
public abstract class WifiDppBaseActivity extends InstrumentedActivity {
    protected FragmentManager mFragmentManager;

    /* access modifiers changed from: protected */
    public abstract void handleIntent(Intent intent);

    WifiDppBaseActivity() {
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.core.InstrumentedActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.wifi_dpp_activity);
        boolean isAnySetupWizard = WizardManagerHelper.isAnySetupWizard(getIntent());
        Toolbar toolbar = (Toolbar) findViewById(C0010R$id.action_bar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(C0008R$drawable.op_ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.dpp.WifiDppBaseActivity.AnonymousClass1 */

            public void onClick(View view) {
                WifiDppBaseActivity.this.onBackPressed();
            }
        });
        if (isAnySetupWizard) {
            toolbar.setVisibility(8);
        } else {
            LinearLayout linearLayout = (LinearLayout) findViewById(C0010R$id.root);
            linearLayout.setClipToPadding(true);
            linearLayout.setFitsSystemWindows(true);
            OPUtils.setLightNavigationBar(getWindow(), OPThemeUtils.getCurrentBasicColorMode(this));
        }
        this.mFragmentManager = getSupportFragmentManager();
        if (bundle == null) {
            handleIntent(getIntent());
        }
    }

    /* access modifiers changed from: protected */
    public void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        int glifTheme = SetupWizardUtils.getGlifTheme(getIntent());
        theme.applyStyle(C0018R$style.SetupWizardPartnerResource, true);
        super.onApplyThemeResource(theme, glifTheme, z);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity
    public void onTitleChanged(CharSequence charSequence, int i) {
        super.onTitleChanged(charSequence, i);
        Toolbar toolbar = (Toolbar) findViewById(C0010R$id.action_bar);
        if (toolbar != null) {
            toolbar.setTitle(getTitle());
        }
    }
}
