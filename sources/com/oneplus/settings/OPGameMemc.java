package com.oneplus.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.widget.SwitchBar;
import com.oneplus.android.context.IOneplusContext;
import com.oneplus.android.context.OneplusContext;
import com.oneplus.iris.IOneplusIrisManager;
import java.util.ArrayList;
import java.util.Map;

public class OPGameMemc extends DashboardFragment implements OnChangeScreen, Preference.OnPreferenceClickListener {
    private Context mContext;
    PreferenceScreen mGameScreen;
    private PreferenceCategory mSupportGame;
    private SwitchBar mSwitchBar;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPGameMemc";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_game_memc;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        this.mContext = settingsActivity;
        SwitchBar switchBar = settingsActivity.getSwitchBar();
        this.mSwitchBar = switchBar;
        switchBar.setSwitchBarText(C0017R$string.oneplus_memc_open, C0017R$string.oneplus_memc_close);
        new OPMEMCSwitchBarController(settingsActivity, this.mSwitchBar, getSettingsLifecycle(), this, "game");
        if (Settings.System.getInt(getPrefContext().getContentResolver(), "op_iris_game_memc_status", 0) == 0) {
            this.mSwitchBar.setChecked(false);
        } else {
            this.mSwitchBar.setChecked(true);
        }
        this.mSwitchBar.show();
        this.mGameScreen = (PreferenceScreen) findPreference("game_memc");
        if (this.mSwitchBar.isChecked()) {
            this.mGameScreen.setEnabled(true);
        } else {
            this.mGameScreen.setEnabled(false);
        }
        this.mSupportGame = (PreferenceCategory) findPreference("support_game");
        findPreference("get_more_game").setOnPreferenceClickListener(this);
        addSupportAppPreference(getSupportGame());
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if ("get_more_game".equals(key)) {
            Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage("com.heytap.market");
            if (launchIntentForPackage != null) {
                this.mContext.startActivity(launchIntentForPackage);
            } else {
                Intent launchIntentForPackage2 = getPackageManager().getLaunchIntentForPackage("com.android.vending");
                if (launchIntentForPackage2 != null) {
                    this.mContext.startActivity(launchIntentForPackage2);
                }
            }
            return true;
        } else if (key == null) {
            return false;
        } else {
            Intent launchIntentForPackage3 = getPackageManager().getLaunchIntentForPackage(key);
            if (launchIntentForPackage3 != null) {
                this.mContext.startActivity(launchIntentForPackage3);
            }
            return true;
        }
    }

    @Override // com.oneplus.settings.OnChangeScreen
    public void onChangeScreen(boolean z) {
        if (z) {
            this.mGameScreen.setEnabled(true);
            Settings.System.putInt(getPrefContext().getContentResolver(), "op_iris_game_memc_status", 1);
            return;
        }
        this.mGameScreen.setEnabled(false);
        Settings.System.putInt(getPrefContext().getContentResolver(), "op_iris_game_memc_status", 0);
    }

    private void addSupportAppPreference(ArrayList<String> arrayList) {
        ApplicationInfo applicationInfo;
        if (arrayList == null || arrayList.size() == 0) {
            Preference preference = new Preference(getPrefContext());
            preference.setTitle(this.mContext.getString(C0017R$string.oneplus_memc_support_no_apps));
            this.mSupportGame.addPreference(preference);
            return;
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        int i = 0;
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            String str = arrayList.get(i2);
            try {
                applicationInfo = packageManager.getApplicationInfo(str, 0);
            } catch (Exception unused) {
                applicationInfo = null;
            }
            if (applicationInfo != null) {
                i++;
                Preference preference2 = new Preference(this.mContext);
                preference2.setTitle(applicationInfo.loadLabel(packageManager));
                preference2.setKey(str);
                preference2.setIcon(applicationInfo.loadIcon(packageManager));
                preference2.setOnPreferenceClickListener(this);
                this.mSupportGame.addPreference(preference2);
            }
        }
        if (i == 0) {
            Preference preference3 = new Preference(getPrefContext());
            preference3.setTitle(this.mContext.getString(C0017R$string.oneplus_memc_support_no_apps));
            this.mSupportGame.addPreference(preference3);
        }
    }

    private ArrayList<String> getSupportGame() {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            Map memcAppTypeMap = ((IOneplusIrisManager) OneplusContext.queryInterface(IOneplusContext.EType.ONEPLUS_IRIS_SERVICE)).getMemcAppTypeMap();
            if (memcAppTypeMap != null && memcAppTypeMap.size() > 0) {
                for (String str : memcAppTypeMap.keySet()) {
                    Log.d("OPGameMemc", "getSupportGame name = " + str);
                    if (((String) memcAppTypeMap.get(str)).equals("0")) {
                        arrayList.add(str);
                    }
                }
            }
        } catch (Exception e) {
            Log.d("OPGameMemc", "getSupportGame e = " + e);
        }
        return arrayList;
    }
}
