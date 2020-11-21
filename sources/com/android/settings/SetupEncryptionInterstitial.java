package com.android.settings;

import android.content.Context;
import android.content.Intent;
import com.android.settings.EncryptionInterstitial;

public class SetupEncryptionInterstitial extends EncryptionInterstitial {

    public static class SetupEncryptionInterstitialFragment extends EncryptionInterstitial.EncryptionInterstitialFragment {
    }

    public static Intent createStartIntent(Context context, int i, boolean z, Intent intent) {
        Intent createStartIntent = EncryptionInterstitial.createStartIntent(context, i, z, intent);
        createStartIntent.setClass(context, SetupEncryptionInterstitial.class);
        createStartIntent.putExtra("extra_prefs_show_button_bar", false).putExtra(":settings:show_fragment_title_resid", -1);
        return createStartIntent;
    }

    @Override // com.android.settings.EncryptionInterstitial, com.android.settings.SettingsActivity
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", SetupEncryptionInterstitialFragment.class.getName());
        return intent;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.EncryptionInterstitial, com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return SetupEncryptionInterstitialFragment.class.getName().equals(str);
    }
}
