package com.oneplus.settings.better;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.ui.OPTextViewButtonPreference;
import com.oneplus.settings.widget.OPBorderlessButtonPreference;
import java.util.ArrayList;
import java.util.List;

public class OPAppLocker extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private List<OPAppModel> mAppList = new ArrayList();
    private OPBorderlessButtonPreference mAppLockerAddAppsPreference;
    private SwitchPreference mAppLockerSwitch;
    private AppOpsManager mAppOpsManager;
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.oneplus.settings.better.OPAppLocker.AnonymousClass1 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            OPAppLocker.this.mOpenAppsList.removeAll();
            OPAppLocker.this.mAppList.clear();
            OPAppLocker.this.mAppList.addAll(OPAppLocker.this.mOPApplicationLoader.getAppListByType(message.what));
            int size = OPAppLocker.this.mAppList.size();
            for (int i = 0; i < size; i++) {
                final OPAppModel oPAppModel = (OPAppModel) OPAppLocker.this.mAppList.get(i);
                final OPTextViewButtonPreference oPTextViewButtonPreference = new OPTextViewButtonPreference(OPAppLocker.this.mContext);
                oPTextViewButtonPreference.setIcon(oPAppModel.getAppIcon());
                oPTextViewButtonPreference.setTitle(oPAppModel.getLabel());
                oPTextViewButtonPreference.setButtonVisible(false);
                oPTextViewButtonPreference.setRightIconVisible(true);
                oPTextViewButtonPreference.setOnRightIconClickListener(new View.OnClickListener() {
                    /* class com.oneplus.settings.better.OPAppLocker.AnonymousClass1.AnonymousClass1 */

                    public void onClick(View view) {
                        oPTextViewButtonPreference.setButtonEnable(false);
                        OPAppLocker.this.mOpenAppsList.removePreference(oPTextViewButtonPreference);
                        OPAppLocker.this.mAppOpsManager.setMode(1001, oPAppModel.getUid(), oPAppModel.getPkgName(), 1);
                    }
                });
                OPAppLocker.this.mOpenAppsList.addPreference(oPTextViewButtonPreference);
            }
        }
    };
    private OPApplicationLoader mOPApplicationLoader;
    private PreferenceCategory mOpenAppsList;
    private PackageManager mPackageManager;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    private void updateListData() {
        if (!this.mOPApplicationLoader.isLoading()) {
            this.mOPApplicationLoader.loadSelectedGameOrReadAppMap(1001);
            this.mOPApplicationLoader.initData(1, this.mHandler);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_app_locker);
        this.mContext = getActivity();
        this.mAppOpsManager = (AppOpsManager) getSystemService("appops");
        this.mPackageManager = getPackageManager();
        OPApplicationLoader oPApplicationLoader = new OPApplicationLoader(this.mContext, this.mAppOpsManager, this.mPackageManager);
        this.mOPApplicationLoader = oPApplicationLoader;
        oPApplicationLoader.setAppType(1001);
        this.mOpenAppsList = (PreferenceCategory) findPreference("app_locker_open_apps");
        OPBorderlessButtonPreference oPBorderlessButtonPreference = (OPBorderlessButtonPreference) findPreference("app_locker_add_apps");
        this.mAppLockerAddAppsPreference = oPBorderlessButtonPreference;
        if (oPBorderlessButtonPreference != null) {
            oPBorderlessButtonPreference.setOnPreferenceClickListener(this);
        }
        SwitchPreference switchPreference = (SwitchPreference) findPreference("app_locker_switch");
        this.mAppLockerSwitch = switchPreference;
        if (switchPreference != null) {
            switchPreference.setOnPreferenceChangeListener(this);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateListData();
        if (this.mAppLockerSwitch != null) {
            boolean z = false;
            int intForUser = Settings.System.getIntForUser(getContentResolver(), "app_locker_switch", 0, -2);
            SwitchPreference switchPreference = this.mAppLockerSwitch;
            if (intForUser != 0) {
                z = true;
            }
            switchPreference.setChecked(z);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Boolean bool = (Boolean) obj;
        if (!"app_locker_switch".equals(preference.getKey())) {
            return true;
        }
        Log.d("OPAppLocker", "KEY_APP_LOCKER_SWITCH");
        Settings.System.putIntForUser(getContentResolver(), "app_locker_switch", bool.booleanValue() ? 1 : 0, -2);
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (!preference.getKey().equals("app_locker_add_apps")) {
            return false;
        }
        Log.d("OPAppLocker", "KEY_APP_LOCKER_ADD_APPS");
        Intent intent = new Intent("oneplus.intent.action.ONEPLUS_GAME_READ_APP_LIST_ACTION");
        intent.putExtra("op_load_app_tyep", 1001);
        this.mContext.startActivity(intent);
        return true;
    }
}
