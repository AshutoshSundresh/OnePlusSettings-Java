package com.android.settings.panel;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class SettingsPanelActivity extends FragmentActivity {
    @VisibleForTesting
    final Bundle mBundle = new Bundle();
    @VisibleForTesting
    boolean mForceCreation = false;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getApplicationContext().getTheme().rebase();
        createOrUpdatePanel(true);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        createOrUpdatePanel(this.mForceCreation);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        this.mForceCreation = false;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onStop() {
        super.onStop();
        this.mForceCreation = true;
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mForceCreation = true;
    }

    private void createOrUpdatePanel(boolean z) {
        Intent intent = getIntent();
        if (intent == null) {
            Log.e("panel_activity", "Null intent, closing Panel Activity");
            finish();
            return;
        }
        String stringExtra = intent.getStringExtra("com.android.settings.panel.extra.PACKAGE_NAME");
        this.mBundle.putString("PANEL_TYPE_ARGUMENT", intent.getAction());
        this.mBundle.putString("PANEL_CALLING_PACKAGE_NAME", getCallingPackage());
        this.mBundle.putString("PANEL_MEDIA_PACKAGE_NAME", stringExtra);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment findFragmentById = supportFragmentManager.findFragmentById(C0010R$id.main_content);
        if (z || findFragmentById == null || !(findFragmentById instanceof PanelFragment)) {
            setContentView(C0012R$layout.settings_panel);
            Window window = getWindow();
            window.setGravity(80);
            window.setLayout(-1, -2);
            PanelFragment panelFragment = new PanelFragment();
            panelFragment.setArguments(this.mBundle);
            FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
            beginTransaction.add(C0010R$id.main_content, panelFragment);
            beginTransaction.commit();
            return;
        }
        PanelFragment panelFragment2 = (PanelFragment) findFragmentById;
        panelFragment2.setArguments(this.mBundle);
        panelFragment2.updatePanelWithAnimation();
    }
}
