package com.android.settings;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.android.internal.util.ArrayUtils;
import com.android.settings.Settings;
import com.android.settings.applications.appinfo.AppButtonsPreferenceController;
import com.android.settings.applications.manageapplications.ManageApplications;
import com.android.settings.core.OnActivityResultListener;
import com.android.settings.core.SettingsBaseActivity;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.gateway.SettingsGateway;
import com.android.settings.dashboard.DashboardFeatureProvider;
import com.android.settings.homepage.TopLevelSettings;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.wfd.WifiDisplaySettings;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.android.settingslib.core.instrumentation.SharedPreferencesLogger;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.drawer.DashboardCategory;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends SettingsBaseActivity implements PreferenceManager.OnPreferenceTreeClickListener, PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, ButtonBarHandler, FragmentManager.OnBackStackChangedListener {
    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
        /* class com.android.settings.SettingsActivity.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            boolean isBatteryPresent;
            if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction()) && SettingsActivity.this.mBatteryPresent != (isBatteryPresent = Utils.isBatteryPresent(intent))) {
                SettingsActivity.this.mBatteryPresent = isBatteryPresent;
                SettingsActivity.this.updateTilesList();
            }
        }
    };
    private boolean mBatteryPresent = true;
    private ArrayList<DashboardCategory> mCategories = new ArrayList<>();
    private DashboardFeatureProvider mDashboardFeatureProvider;
    private BroadcastReceiver mDevelopmentSettingsListener;
    private String mFragmentClass;
    private CharSequence mInitialTitle;
    private int mInitialTitleResId;
    private Button mNextButton;
    private SmqSettings mSMQ;
    private SwitchBar mSwitchBar;

    @Override // androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        return false;
    }

    public SwitchBar getSwitchBar() {
        return this.mSwitchBar;
    }

    @Override // androidx.preference.PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat preferenceFragmentCompat, Preference preference) {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this);
        subSettingLauncher.setDestination(preference.getFragment());
        subSettingLauncher.setArguments(preference.getExtras());
        subSettingLauncher.setSourceMetricsCategory(preferenceFragmentCompat instanceof Instrumentable ? ((Instrumentable) preferenceFragmentCompat).getMetricsCategory() : 0);
        subSettingLauncher.setTitleRes(-1);
        subSettingLauncher.launch();
        return true;
    }

    public SharedPreferences getSharedPreferences(String str, int i) {
        if (str.equals(getPackageName() + "_preferences")) {
            return new SharedPreferencesLogger(this, getMetricsTag(), FeatureFactory.getFactory(this).getMetricsFeatureProvider());
        }
        return super.getSharedPreferences(str, i);
    }

    private String getMetricsTag() {
        String stringExtra = (getIntent() == null || !getIntent().hasExtra(":settings:show_fragment")) ? null : getIntent().getStringExtra(":settings:show_fragment");
        if (TextUtils.isEmpty(stringExtra)) {
            Log.w("SettingsActivity", "MetricsTag is invalid " + stringExtra);
            stringExtra = getClass().getName();
        }
        return stringExtra.startsWith("com.android.settings.") ? stringExtra.replace("com.android.settings.", "") : stringExtra;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.android.settings.core.SettingsBaseActivity
    public void onCreate(Bundle bundle) {
        View findViewById;
        super.onCreate(bundle);
        Log.d("SettingsActivity", "Starting onCreate");
        System.currentTimeMillis();
        this.mDashboardFeatureProvider = FeatureFactory.getFactory(this).getDashboardFeatureProvider(this);
        OPUtils.setLightNavigationBar(getWindow(), OPUtils.getThemeMode(getContentResolver()));
        getMetaData();
        Intent intent = getIntent();
        if (intent.hasExtra("settings:ui_options")) {
            getWindow().setUiOptions(intent.getIntExtra("settings:ui_options", 0));
        }
        this.mSMQ = new SmqSettings(getApplicationContext());
        String stringExtra = intent.getStringExtra(":settings:show_fragment");
        if (((this instanceof SubSettings) || intent.getBooleanExtra(":settings:show_fragment_as_subsetting", false)) && !WizardManagerHelper.isAnySetupWizard(getIntent())) {
            setTheme(C0018R$style.Theme_SubSettings);
        }
        setContentView(C0012R$layout.settings_main_prefs);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (bundle != null) {
            setTitleFromIntent(intent);
            ArrayList parcelableArrayList = bundle.getParcelableArrayList(":settings:categories");
            if (parcelableArrayList != null) {
                this.mCategories.clear();
                this.mCategories.addAll(parcelableArrayList);
                setTitleFromBackStack();
            }
        } else {
            launchSettingFragment(stringExtra, intent);
        }
        boolean isAnySetupWizard = WizardManagerHelper.isAnySetupWizard(getIntent());
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(!isAnySetupWizard);
            actionBar.setHomeButtonEnabled(!isAnySetupWizard);
            actionBar.setDisplayShowTitleEnabled(true);
        }
        SwitchBar switchBar = (SwitchBar) findViewById(C0010R$id.switch_bar);
        this.mSwitchBar = switchBar;
        if (switchBar != null) {
            switchBar.setMetricsTag(getMetricsTag());
        }
        if (intent.getBooleanExtra("extra_prefs_show_button_bar", false) && (findViewById = findViewById(C0010R$id.button_bar)) != null) {
            findViewById.setVisibility(0);
            Button button = (Button) findViewById(C0010R$id.back_button);
            button.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.$$Lambda$SettingsActivity$xj33YuwAAWABRerpKy5E8fkjk */

                public final void onClick(View view) {
                    SettingsActivity.this.lambda$onCreate$0$SettingsActivity(view);
                }
            });
            Button button2 = (Button) findViewById(C0010R$id.skip_button);
            button2.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.$$Lambda$SettingsActivity$DprhoLIEb9VcKgAzJHK8h9J6b8M */

                public final void onClick(View view) {
                    SettingsActivity.this.lambda$onCreate$1$SettingsActivity(view);
                }
            });
            Button button3 = (Button) findViewById(C0010R$id.next_button);
            this.mNextButton = button3;
            button3.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.$$Lambda$SettingsActivity$1W97zC8WEIU323IiggDWF3SpYA */

                public final void onClick(View view) {
                    SettingsActivity.this.lambda$onCreate$2$SettingsActivity(view);
                }
            });
            if (intent.hasExtra("extra_prefs_set_next_text")) {
                String stringExtra2 = intent.getStringExtra("extra_prefs_set_next_text");
                if (TextUtils.isEmpty(stringExtra2)) {
                    this.mNextButton.setVisibility(8);
                } else {
                    this.mNextButton.setText(stringExtra2);
                }
            }
            if (intent.hasExtra("extra_prefs_set_back_text")) {
                String stringExtra3 = intent.getStringExtra("extra_prefs_set_back_text");
                if (TextUtils.isEmpty(stringExtra3)) {
                    button.setVisibility(8);
                } else {
                    button.setText(stringExtra3);
                }
            }
            if (intent.getBooleanExtra("extra_prefs_show_skip", false)) {
                button2.setVisibility(0);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ void lambda$onCreate$0$SettingsActivity(View view) {
        setResult(0, null);
        finish();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$1 */
    public /* synthetic */ void lambda$onCreate$1$SettingsActivity(View view) {
        setResult(-1, null);
        finish();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$2 */
    public /* synthetic */ void lambda$onCreate$2$SettingsActivity(View view) {
        setResult(-1, null);
        finish();
    }

    /* access modifiers changed from: protected */
    public void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        theme.applyStyle(C0018R$style.SetupWizardPartnerResource, true);
        super.onApplyThemeResource(theme, i, z);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof OnActivityResultListener) {
                    fragment.onActivityResult(i, i2, intent);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void launchSettingFragment(String str, Intent intent) {
        if (str != null) {
            setTitleFromIntent(intent);
            switchToFragment(str, intent.getBundleExtra(":settings:show_fragment_args"), true, this.mInitialTitleResId, this.mInitialTitle);
            return;
        }
        this.mInitialTitleResId = C0017R$string.dashboard_title;
        switchToFragment(TopLevelSettings.class.getName(), null, false, this.mInitialTitleResId, this.mInitialTitle);
    }

    private void setTitleFromIntent(Intent intent) {
        Log.d("SettingsActivity", "Starting to set activity title");
        int intExtra = intent.getIntExtra(":settings:show_fragment_title_resid", -1);
        if (intExtra > 0) {
            this.mInitialTitle = null;
            this.mInitialTitleResId = intExtra;
            String stringExtra = intent.getStringExtra(":settings:show_fragment_title_res_package_name");
            if (stringExtra != null) {
                try {
                    CharSequence text = createPackageContextAsUser(stringExtra, 0, new UserHandle(UserHandle.myUserId())).getResources().getText(this.mInitialTitleResId);
                    this.mInitialTitle = text;
                    setTitle(text);
                    this.mInitialTitleResId = -1;
                    return;
                } catch (PackageManager.NameNotFoundException unused) {
                    Log.w("SettingsActivity", "Could not find package" + stringExtra);
                }
            } else {
                setTitle(this.mInitialTitleResId);
            }
        } else {
            this.mInitialTitleResId = -1;
            CharSequence stringExtra2 = intent.getStringExtra(":settings:show_fragment_title");
            if (stringExtra2 == null) {
                stringExtra2 = getTitle();
            }
            this.mInitialTitle = stringExtra2;
            setTitle(stringExtra2);
        }
        Log.d("SettingsActivity", "Done setting title");
    }

    @Override // androidx.fragment.app.FragmentManager.OnBackStackChangedListener
    public void onBackStackChanged() {
        setTitleFromBackStack();
    }

    private void setTitleFromBackStack() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 0) {
            int i = this.mInitialTitleResId;
            if (i > 0) {
                setTitle(i);
            } else {
                setTitle(this.mInitialTitle);
            }
        } else {
            setTitleFromBackStackEntry(getSupportFragmentManager().getBackStackEntryAt(backStackEntryCount - 1));
        }
    }

    private void setTitleFromBackStackEntry(FragmentManager.BackStackEntry backStackEntry) {
        CharSequence charSequence;
        int breadCrumbTitleRes = backStackEntry.getBreadCrumbTitleRes();
        if (breadCrumbTitleRes > 0) {
            charSequence = getText(breadCrumbTitleRes);
        } else {
            charSequence = backStackEntry.getBreadCrumbTitle();
        }
        if (charSequence != null) {
            setTitle(charSequence);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        saveState(bundle);
    }

    /* access modifiers changed from: package-private */
    public void saveState(Bundle bundle) {
        if (this.mCategories.size() > 0) {
            bundle.putParcelableArrayList(":settings:categories", this.mCategories);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, com.android.settings.core.SettingsBaseActivity
    public void onResume() {
        super.onResume();
        this.mDevelopmentSettingsListener = new BroadcastReceiver() {
            /* class com.android.settings.SettingsActivity.AnonymousClass2 */

            public void onReceive(Context context, Intent intent) {
                SettingsActivity.this.updateTilesList();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mDevelopmentSettingsListener, new IntentFilter("com.android.settingslib.development.DevelopmentSettingsEnabler.SETTINGS_CHANGED"));
        registerReceiver(this.mBatteryInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        updateTilesList();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, com.android.settings.core.SettingsBaseActivity
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mDevelopmentSettingsListener);
        this.mDevelopmentSettingsListener = null;
        unregisterReceiver(this.mBatteryInfoReceiver);
    }

    public void setTaskDescription(ActivityManager.TaskDescription taskDescription) {
        taskDescription.setIcon(Icon.createWithResource(this, C0008R$drawable.ic_launcher_settings));
        super.setTaskDescription(taskDescription);
    }

    /* access modifiers changed from: protected */
    public boolean isValidFragment(String str) {
        int i = 0;
        while (true) {
            String[] strArr = SettingsGateway.ENTRY_FRAGMENTS;
            if (i >= strArr.length) {
                return false;
            }
            if (strArr[i].equals(str)) {
                return true;
            }
            i++;
        }
    }

    public Intent getIntent() {
        Bundle bundle;
        Intent intent = super.getIntent();
        String startingFragmentClass = getStartingFragmentClass(intent);
        if (startingFragmentClass == null) {
            return intent;
        }
        Intent intent2 = new Intent(intent);
        intent2.putExtra(":settings:show_fragment", startingFragmentClass);
        Bundle bundleExtra = intent.getBundleExtra(":settings:show_fragment_args");
        if (bundleExtra != null) {
            bundle = new Bundle(bundleExtra);
        } else {
            bundle = new Bundle();
        }
        bundle.putParcelable("intent", intent);
        intent2.putExtra(":settings:show_fragment_args", bundle);
        return intent2;
    }

    private String getStartingFragmentClass(Intent intent) {
        String str = this.mFragmentClass;
        if (str != null) {
            return str;
        }
        String className = intent.getComponent().getClassName();
        if (className.equals(getClass().getName())) {
            return null;
        }
        return ("com.android.settings.RunningServices".equals(className) || "com.android.settings.applications.StorageUse".equals(className)) ? ManageApplications.class.getName() : className;
    }

    public void finishPreferencePanel(int i, Intent intent) {
        setResult(i, intent);
        if (intent == null || !intent.getBooleanExtra(AppButtonsPreferenceController.KEY_REMOVE_TASK_WHEN_FINISHING, false)) {
            finish();
        } else {
            finishAndRemoveTask();
        }
    }

    private Fragment switchToFragment(String str, Bundle bundle, boolean z, int i, CharSequence charSequence) {
        if (str.equals(getString(C0017R$string.qtifeedback_intent_action))) {
            Intent intent = new Intent(getString(C0017R$string.qtifeedback_intent_action));
            intent.addCategory("android.intent.category.DEFAULT");
            startActivity(intent);
            finish();
            return null;
        }
        Log.d("SettingsActivity", "Switching to fragment " + str);
        if (str.equals("com.android.settings.sim.SimSettings")) {
            if (Utils.isSimSettingsApkAvailable()) {
                Log.i("SettingsActivity", "switchToFragment, launch simSettings");
                Intent intent2 = new Intent("com.android.settings.sim.SIM_SUB_INFO_SETTINGS");
                if (!getPackageManager().queryIntentActivities(intent2, 0).isEmpty()) {
                    startActivity(intent2);
                }
            }
            finish();
            return null;
        } else if (!z || isValidFragment(str)) {
            Fragment targetFragment = Utils.getTargetFragment(this, str, bundle);
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.replace(C0010R$id.main_content, targetFragment);
            if (i > 0) {
                beginTransaction.setBreadCrumbTitle(i);
            } else if (charSequence != null) {
                beginTransaction.setBreadCrumbTitle(charSequence);
            }
            beginTransaction.commitAllowingStateLoss();
            getSupportFragmentManager().executePendingTransactions();
            Log.d("SettingsActivity", "Executed frag manager pendingTransactions");
            return targetFragment;
        } else {
            throw new IllegalArgumentException("Invalid fragment for this activity: " + str);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateTilesList() {
        AsyncTask.execute(new Runnable() {
            /* class com.android.settings.$$Lambda$SettingsActivity$8Oa7mI89HcrjAAmE1FOVHs_ZdXU */

            public final void run() {
                SettingsActivity.this.lambda$updateTilesList$3$SettingsActivity();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: doUpdateTilesList */
    public void lambda$updateTilesList$3() {
        PackageManager packageManager = getPackageManager();
        boolean isAdminUser = UserManager.get(this).isAdminUser();
        String packageName = getPackageName();
        StringBuilder sb = new StringBuilder();
        boolean z = setTileEnabled(sb, new ComponentName(packageName, Settings.BluetoothSettingsActivity.class.getName()), packageManager.hasSystemFeature("android.hardware.bluetooth"), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.WifiSettingsActivity.class.getName()), packageManager.hasSystemFeature("android.hardware.wifi"), isAdminUser));
        if (this.mSMQ.isShowSmqSettings()) {
            z = setTileEnabled(sb, new ComponentName(packageName, Settings.SMQQtiFeedbackActivity.class.getName()), this.mSMQ.isShowSmqSettings(), isAdminUser) || z;
        }
        boolean z2 = setTileEnabled(sb, new ComponentName(packageName, Settings.WifiDisplaySettingsActivity.class.getName()), WifiDisplaySettings.isAvailable(this), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.DevelopmentSettingsDashboardActivity.class.getName()), DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(this) && !Utils.isMonkeyRunning(), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.UserSettingsActivity.class.getName()), UserManager.supportsMultipleUsers() && !Utils.isMonkeyRunning(), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.DataUsageSummaryActivity.class.getName()), Utils.isBandwidthControlEnabled(), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.PowerUsageSummaryActivity.class.getName()), this.mBatteryPresent, isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.SimSettingsActivity.class.getName()), Utils.showSimCardTile(this), isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.ConnectedDeviceDashboardActivity.class.getName()), UserManager.isDeviceInDemoMode(this) ^ true, isAdminUser) || (setTileEnabled(sb, new ComponentName(packageName, Settings.DataUsageSummaryActivity.class.getName()), Utils.isBandwidthControlEnabled(), isAdminUser) || z)))))));
        if (!isAdminUser) {
            List<DashboardCategory> allCategories = this.mDashboardFeatureProvider.getAllCategories();
            synchronized (allCategories) {
                for (DashboardCategory dashboardCategory : allCategories) {
                    int tilesCount = dashboardCategory.getTilesCount();
                    for (int i = 0; i < tilesCount; i++) {
                        ComponentName component = dashboardCategory.getTile(i).getIntent().getComponent();
                        boolean contains = ArrayUtils.contains(SettingsGateway.SETTINGS_FOR_RESTRICTED, component.getClassName());
                        if (packageName.equals(component.getPackageName()) && !contains) {
                            if (!setTileEnabled(sb, component, false, isAdminUser)) {
                                if (!z2) {
                                    z2 = false;
                                }
                            }
                            z2 = true;
                        }
                    }
                }
            }
        }
        if (z2) {
            Log.d("SettingsActivity", "Enabled state changed for some tiles, reloading all categories " + sb.toString());
            updateCategories();
            return;
        }
        Log.d("SettingsActivity", "No enabled state changed, skipping updateCategory call");
    }

    private boolean setTileEnabled(StringBuilder sb, ComponentName componentName, boolean z, boolean z2) {
        if (!z2 && getPackageName().equals(componentName.getPackageName()) && !ArrayUtils.contains(SettingsGateway.SETTINGS_FOR_RESTRICTED, componentName.getClassName())) {
            z = false;
        }
        boolean tileEnabled = setTileEnabled(componentName, z);
        if (tileEnabled) {
            sb.append(componentName.toShortString());
            sb.append(",");
        }
        return tileEnabled;
    }

    private void getMetaData() {
        try {
            ActivityInfo activityInfo = getPackageManager().getActivityInfo(getComponentName(), 128);
            if (activityInfo == null) {
                return;
            }
            if (activityInfo.metaData != null) {
                this.mFragmentClass = activityInfo.metaData.getString("com.android.settings.FRAGMENT_CLASS");
            }
        } catch (PackageManager.NameNotFoundException unused) {
            Log.d("SettingsActivity", "Cannot get Metadata for: " + getComponentName().toString());
        }
    }

    @Override // com.android.settings.ButtonBarHandler
    public boolean hasNextButton() {
        return this.mNextButton != null;
    }

    @Override // com.android.settings.ButtonBarHandler
    public Button getNextButton() {
        return this.mNextButton;
    }
}
