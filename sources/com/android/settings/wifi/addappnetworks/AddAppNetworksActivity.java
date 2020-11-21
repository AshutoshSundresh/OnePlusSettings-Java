package com.android.settings.wifi.addappnetworks;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.oneplus.settings.BaseAppCompatActivity;

public class AddAppNetworksActivity extends BaseAppCompatActivity {
    @VisibleForTesting
    final Bundle mBundle = new Bundle();

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.settings_panel);
        showAddNetworksFragment();
        Window window = getWindow();
        window.setGravity(80);
        window.setLayout(-1, -2);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        showAddNetworksFragment();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void showAddNetworksFragment() {
        this.mBundle.putString("panel_calling_package_name", getCallingPackage());
        this.mBundle.putParcelableArrayList("android.provider.extra.WIFI_NETWORK_LIST", getIntent().getParcelableArrayListExtra("android.provider.extra.WIFI_NETWORK_LIST"));
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment findFragmentByTag = supportFragmentManager.findFragmentByTag("AddAppNetworksActivity");
        if (findFragmentByTag == null) {
            AddAppNetworksFragment addAppNetworksFragment = new AddAppNetworksFragment();
            addAppNetworksFragment.setArguments(this.mBundle);
            FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
            beginTransaction.add(C0010R$id.main_content, addAppNetworksFragment, "AddAppNetworksActivity");
            beginTransaction.commit();
            return;
        }
        ((AddAppNetworksFragment) findFragmentByTag).createContent(this.mBundle);
    }
}
