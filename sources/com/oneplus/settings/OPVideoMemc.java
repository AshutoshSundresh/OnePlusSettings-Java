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
import com.android.settings.ui.RadioButtonPreference;
import com.android.settings.widget.SwitchBar;
import com.oneplus.android.context.IOneplusContext;
import com.oneplus.android.context.OneplusContext;
import com.oneplus.iris.IOneplusIrisManager;
import java.util.ArrayList;
import java.util.Map;

public class OPVideoMemc extends DashboardFragment implements OnChangeScreen, RadioButtonPreference.OnClickListener, Preference.OnPreferenceClickListener {
    private Context mContext;
    private RadioButtonPreference mSmoothMode;
    private PreferenceCategory mSupportVideo;
    private SwitchBar mSwitchBar;
    private RadioButtonPreference mTheAcmeMode;
    PreferenceScreen mVideoScreen;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPVideoMemc";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_video_memc;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        SwitchBar switchBar = settingsActivity.getSwitchBar();
        this.mSwitchBar = switchBar;
        this.mContext = settingsActivity;
        switchBar.setSwitchBarText(C0017R$string.oneplus_memc_open, C0017R$string.oneplus_memc_close);
        new OPMEMCSwitchBarController(settingsActivity, this.mSwitchBar, getSettingsLifecycle(), this, "video");
        if (Settings.System.getInt(getPrefContext().getContentResolver(), "op_iris_video_memc_status", 0) == 0) {
            this.mSwitchBar.setChecked(false);
        } else {
            this.mSwitchBar.setChecked(true);
        }
        this.mSwitchBar.show();
        this.mVideoScreen = (PreferenceScreen) findPreference("video_memc");
        if (this.mSwitchBar.isChecked()) {
            this.mVideoScreen.setEnabled(true);
        } else {
            this.mVideoScreen.setEnabled(false);
        }
        int i = Settings.System.getInt(getPrefContext().getContentResolver(), "op_iris_video_memc_mode", 0);
        RadioButtonPreference radioButtonPreference = (RadioButtonPreference) findPreference("video_smooth");
        this.mSmoothMode = radioButtonPreference;
        radioButtonPreference.setOnClickListener(this);
        RadioButtonPreference radioButtonPreference2 = (RadioButtonPreference) findPreference("video_acme");
        this.mTheAcmeMode = radioButtonPreference2;
        radioButtonPreference2.setOnClickListener(this);
        if (is1080pMode()) {
            this.mTheAcmeMode.setEnabled(true);
            this.mTheAcmeMode.setSummary("");
        } else {
            this.mTheAcmeMode.setEnabled(false);
            this.mTheAcmeMode.setSummary(this.mContext.getString(C0017R$string.oneplus_memc_video_nosupport_extreme_summary));
            if (i == 1) {
                Settings.System.putInt(getPrefContext().getContentResolver(), "op_iris_video_memc_mode", 0);
                i = 0;
            }
        }
        if (i == 0) {
            this.mSmoothMode.setChecked(true);
            this.mTheAcmeMode.setChecked(false);
        } else {
            this.mSmoothMode.setChecked(false);
            this.mTheAcmeMode.setChecked(true);
        }
        this.mSupportVideo = (PreferenceCategory) findPreference("support_video");
        findPreference("get_more_video").setOnPreferenceClickListener(this);
        addSupportAppPreference(getSupportVideo(i));
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if ("get_more_video".equals(key)) {
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
            this.mVideoScreen.setEnabled(true);
            Settings.System.putInt(getPrefContext().getContentResolver(), "op_iris_video_memc_status", 1);
            return;
        }
        this.mVideoScreen.setEnabled(false);
        Settings.System.putInt(getPrefContext().getContentResolver(), "op_iris_video_memc_status", 0);
    }

    @Override // com.android.settings.ui.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        RadioButtonPreference radioButtonPreference2 = this.mSmoothMode;
        if (radioButtonPreference == radioButtonPreference2) {
            radioButtonPreference2.setChecked(true);
            this.mTheAcmeMode.setChecked(false);
            Settings.System.putInt(getPrefContext().getContentResolver(), "op_iris_video_memc_mode", 0);
            addSupportAppPreference(getSupportVideo(0));
        } else if (radioButtonPreference == this.mTheAcmeMode) {
            radioButtonPreference2.setChecked(false);
            this.mTheAcmeMode.setChecked(true);
            Settings.System.putInt(getPrefContext().getContentResolver(), "op_iris_video_memc_mode", 1);
            addSupportAppPreference(getSupportVideo(1));
        }
    }

    private boolean is1080pMode() {
        return Settings.Global.getInt(getPrefContext().getContentResolver(), "oneplus_screen_resolution_adjust", 2) == 1;
    }

    private void addSupportAppPreference(ArrayList<String> arrayList) {
        ApplicationInfo applicationInfo;
        this.mSupportVideo.removeAll();
        if (arrayList == null || arrayList.size() == 0) {
            Preference preference = new Preference(getPrefContext());
            preference.setTitle(this.mContext.getString(C0017R$string.oneplus_memc_support_no_apps));
            this.mSupportVideo.addPreference(preference);
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
                this.mSupportVideo.addPreference(preference2);
            }
        }
        if (i == 0) {
            Preference preference3 = new Preference(getPrefContext());
            preference3.setTitle(this.mContext.getString(C0017R$string.oneplus_memc_support_no_apps));
            this.mSupportVideo.addPreference(preference3);
        }
    }

    private ArrayList<String> getSupportVideo(int i) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            Map memcRateMap = ((IOneplusIrisManager) OneplusContext.queryInterface(IOneplusContext.EType.ONEPLUS_IRIS_SERVICE)).getMemcRateMap();
            if (memcRateMap != null && memcRateMap.size() > 0) {
                for (String str : memcRateMap.keySet()) {
                    Log.d("OPVideoMemc", "getSupportVideo name = " + str);
                    String str2 = (String) memcRateMap.get(str);
                    if (i == 0) {
                        if (str2.equals("0")) {
                            arrayList.add(str);
                        }
                    } else if (str2.equals("1")) {
                        arrayList.add(str);
                    }
                }
            }
        } catch (Exception e) {
            Log.d("OPVideoMemc", "getSupportGame e = " + e);
        }
        return arrayList;
    }
}
