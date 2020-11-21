package com.oneplus.security.network.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import com.android.settings.C0010R$id;
import com.android.settings.C0011R$integer;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.google.android.material.appbar.Appbar;
import com.google.android.material.picker.NumberPicker;
import com.oneplus.security.SecureService;
import com.oneplus.security.network.NetworkPolicyEditor;
import com.oneplus.security.network.calibrate.AutoCalibrateUtil;
import com.oneplus.security.network.operator.OperatorAccountDayUpdater;
import com.oneplus.security.network.operator.OperatorDataModelFactory;
import com.oneplus.security.network.operator.OperatorModelInterface;
import com.oneplus.security.network.operator.OperatorPackageUsageUpdater;
import com.oneplus.security.network.simcard.SimStateListener;
import com.oneplus.security.network.simcard.SimcardDataModel;
import com.oneplus.security.network.simcard.SimcardDataModelInterface;
import com.oneplus.security.network.trafficalarm.TrafficUsageAlarmIntentService;
import com.oneplus.security.network.trafficalarm.TrafficUsageAlarmUtils;
import com.oneplus.security.network.trafficinfo.NativeTrafficDataModel;
import com.oneplus.security.utils.FunctionUtils;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.security.utils.OPSNSUtils;
import com.oneplus.security.utils.ToastUtil;
import com.oneplus.security.utils.Utils;
import com.oneplus.settings.BaseAppCompatActivity;
import com.oneplus.settings.SettingsBaseApplication;

public class TrafficUsageSettingsActivity extends BaseAppCompatActivity {
    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.traffic_usage_settings_layout);
        Appbar appbar = (Appbar) findViewById(C0010R$id.action_bar);
        appbar.setTitle(getTitle());
        appbar.setDisplayHomeAsUpEnabled(true);
        appbar.setNavigationOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.security.network.view.TrafficUsageSettingsActivity.AnonymousClass1 */

            public void onClick(View view) {
                TrafficUsageSettingsActivity.this.onBackPressed();
            }
        });
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity
    public void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
        setupSimplePreferencesScreen();
    }

    private void setupSimplePreferencesScreen() {
        Intent intent = getIntent();
        if (intent != null) {
            int intExtra = intent.getIntExtra("sim_card_slot", -1);
            Bundle bundle = new Bundle();
            bundle.putInt("sim_card_slot", intExtra);
            TrafficUsageSettingsFragment trafficUsageSettingsFragment = new TrafficUsageSettingsFragment();
            trafficUsageSettingsFragment.setArguments(bundle);
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.replace(C0010R$id.content, trafficUsageSettingsFragment);
            beginTransaction.commit();
        }
    }

    @TargetApi(11)
    public static class TrafficUsageSettingsFragment extends PreferenceFragmentCompat implements OperatorPackageUsageUpdater, OperatorAccountDayUpdater, SimStateListener, Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private Activity mActivity;
        private Context mContext;
        private int mCurrentSlotId = 0;
        private int mCurrentSubId;
        private boolean mDialogShowing = false;
        private NetworkTemplate mNetworkTemplate;
        private OperatorModelInterface mOperatorDataModel;
        NetworkPolicyEditor mPolicyEditor;
        NetworkPolicyManager mPolicyManager;
        private SimcardDataModelInterface mSimcardDataModel;
        private long mUsedByte;
        private Preference traffic_monthly_usage;
        private Preference traffic_reset_day;
        private Preference traffic_total_settings;
        private SwitchPreference traffic_total_switch;
        private Preference traffic_warn_settings;
        private SwitchPreference traffic_warn_switch;

        @Override // androidx.preference.PreferenceFragmentCompat
        public void onCreatePreferences(Bundle bundle, String str) {
        }

        @Override // com.oneplus.security.network.simcard.SimStateListener
        public void onSimOperatorCodeChanged(int i, String str) {
        }

        private void initPreference() {
            getPreferenceScreen();
            this.traffic_reset_day = findPreference("key_traffic_reset_day");
            this.traffic_monthly_usage = findPreference("key_traffic_monthly_usage");
            this.traffic_total_switch = (SwitchPreference) findPreference("key_traffic_total_switch");
            this.traffic_total_settings = findPreference("key_traffic_total_settings");
            this.traffic_total_switch.setChecked(TrafficUsageAlarmUtils.getDataTotalState(this.mContext, this.mCurrentSlotId));
            this.traffic_warn_switch = (SwitchPreference) findPreference("key_traffic_warn_switch");
            this.traffic_warn_settings = findPreference("key_traffic_warn_settings");
            this.traffic_warn_switch.setChecked(TrafficUsageAlarmUtils.getDataWarnState(this.mContext, false));
            this.traffic_reset_day.setOnPreferenceClickListener(this);
            this.traffic_monthly_usage.setOnPreferenceClickListener(this);
            this.traffic_total_settings.setOnPreferenceClickListener(this);
            this.traffic_warn_settings.setOnPreferenceClickListener(this);
            this.traffic_total_switch.setOnPreferenceChangeListener(this);
            this.traffic_warn_switch.setOnPreferenceChangeListener(this);
        }

        @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mContext = getContext();
            this.mActivity = getActivity();
            if (getArguments() != null) {
                this.mCurrentSlotId = getArguments().getInt("sim_card_slot");
            } else {
                LogUtils.e("TrafficUsageSettingsFragment", "error retrieve current using slot id.");
            }
            this.mCurrentSubId = OPSNSUtils.findSubIdBySlotId(this.mCurrentSlotId);
            this.mOperatorDataModel = OperatorDataModelFactory.getOperatorDataModel(SettingsBaseApplication.getContext());
            NetworkPolicyManager from = NetworkPolicyManager.from(this.mContext.getApplicationContext());
            this.mPolicyManager = from;
            this.mPolicyEditor = new NetworkPolicyEditor(from);
            this.mNetworkTemplate = NativeTrafficDataModel.getNetworkTemplate(this.mCurrentSubId);
            addPreferencesFromResource(C0019R$xml.pref_traffic_usage);
            initPreference();
        }

        @Override // androidx.fragment.app.Fragment
        public void onDetach() {
            super.onDetach();
            this.mSimcardDataModel.removeSimStateListener(this);
        }

        @Override // androidx.fragment.app.Fragment
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            SimcardDataModel instance = SimcardDataModel.getInstance(SettingsBaseApplication.getContext());
            this.mSimcardDataModel = instance;
            instance.registerSimStateListener(this);
        }

        private void finishActivity() {
            Activity activity = this.mActivity;
            if (activity != null) {
                activity.finish();
            }
        }

        @Override // androidx.fragment.app.Fragment
        public void onPause() {
            super.onPause();
            this.mOperatorDataModel.removeTrafficUsageUpdater(this);
            this.mOperatorDataModel.removeOperatorAccountDayUpdater(this);
        }

        @Override // androidx.fragment.app.Fragment
        public void onDestroy() {
            super.onDestroy();
        }

        @Override // androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            this.mOperatorDataModel.addTrafficUsageUpdater(this);
            this.mOperatorDataModel.registerOperatorAccountDayUpdater(this);
            this.mOperatorDataModel.requesetPkgMonthlyUsageAndTotalInByte(this.mCurrentSlotId);
            this.mOperatorDataModel.requestOperatorAccountDay(this.mCurrentSlotId);
            refreshDataTotalPre(this.mOperatorDataModel.getPkgTotalInByte(this.mCurrentSlotId));
            refreshDataWarnPre();
            this.mPolicyEditor.read();
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            if (preference == this.traffic_reset_day) {
                showResetDayDialog();
                return true;
            }
            if (preference == this.traffic_total_settings) {
                showBytesEditorDialog(this.mNetworkTemplate, 0);
            } else if (preference == this.traffic_warn_settings) {
                showBytesEditorDialog(this.mNetworkTemplate, 1);
            } else if (preference == this.traffic_monthly_usage) {
                showBytesEditorDialog(this.mNetworkTemplate, 2);
            }
            return false;
        }

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            if (preference == this.traffic_total_switch) {
                boolean booleanValue = ((Boolean) obj).booleanValue();
                long pkgTotalInByte = this.mOperatorDataModel.getPkgTotalInByte(this.mCurrentSlotId);
                if (booleanValue) {
                    showConfirmSetDataLimitDialog(pkgTotalInByte);
                    return false;
                }
                Utils.sendAppTracker("data_limit", 0);
                TrafficUsageAlarmUtils.setDataTotalState(this.mContext, booleanValue, this.mCurrentSlotId);
                this.mPolicyEditor.setPolicyLimitBytes(this.mNetworkTemplate, -1);
                Log.d("setPolicyLimitBytes ", "setPolicyLimitBytes:2 ");
                refreshDataTotalPre(pkgTotalInByte);
                return true;
            } else if (preference != this.traffic_warn_switch) {
                return false;
            } else {
                boolean booleanValue2 = ((Boolean) obj).booleanValue();
                Utils.sendAppTracker("data_alert", booleanValue2 ? 1 : 0);
                TrafficUsageAlarmUtils.setDataWarnState(this.mContext, booleanValue2, this.mCurrentSlotId);
                if (booleanValue2) {
                    this.mOperatorDataModel.setWarnByteValue(this.mCurrentSlotId, checkDataWarnValue(TrafficUsageAlarmUtils.getDataWarnValue(this.mContext, this.mCurrentSlotId, -1)));
                    SecureService.startServiceForDataUsage(this.mContext, this.mCurrentSlotId);
                    TrafficUsageAlarmUtils.resetTrafficDialogAlertedState(this.mContext, this.mCurrentSlotId);
                } else {
                    this.mPolicyEditor.setPolicyWarningBytes(this.mNetworkTemplate, -1);
                }
                return true;
            }
        }

        @Override // com.oneplus.security.network.simcard.SimStateListener
        public void onSimStateChanged(String str) {
            finishActivity();
        }

        @Override // com.oneplus.security.network.operator.OperatorPackageUsageUpdater
        public void onTrafficTotalAndUsedUpdate(long j, long j2, int i) {
            if (getActivity() != null && isAdded()) {
                if (i != this.mCurrentSlotId) {
                    LogUtils.e("TrafficUsageSettingsFragment", "onTrafficTotalAndUsedUpdate currentSlotId is not eq with simIndex, simIndex:" + i + ", mCurrentSlotId:" + this.mCurrentSlotId);
                    return;
                }
                refreshDataTotalPre(j);
                refreshDataUsedPre(j2);
            }
        }

        @Override // com.oneplus.security.network.operator.OperatorAccountDayUpdater
        public void onAccountDayUpdate(int i, int i2) {
            if (getActivity() != null && isAdded()) {
                if (i != this.mCurrentSlotId) {
                    LogUtils.e("TrafficUsageSettingsFragment", "onAccountDayUpdate currentSlotId is not eq with simIndex, simIndex:" + i + ", mCurrentSlotId:" + this.mCurrentSlotId);
                    return;
                }
                refreshAccountDayPre(i2);
            }
        }

        public void refreshAccountDayPre(final int i) {
            if (this.mActivity != null && isAdded()) {
                this.mActivity.runOnUiThread(new Runnable() {
                    /* class com.oneplus.security.network.view.TrafficUsageSettingsActivity.TrafficUsageSettingsFragment.AnonymousClass1 */

                    public void run() {
                        Context context = TrafficUsageSettingsFragment.this.mContext;
                        int i = C0017R$string.traffic_reset_day_summary;
                        TrafficUsageSettingsFragment.this.traffic_reset_day.setSummary(context.getString(i, i + TrafficUsageSettingsFragment.this.getString(C0017R$string.traffic_day_dimension)));
                    }
                });
            }
        }

        public void refreshDataTotalPre(final long j) {
            if (this.mActivity != null && isAdded()) {
                this.mActivity.runOnUiThread(new Runnable() {
                    /* class com.oneplus.security.network.view.TrafficUsageSettingsActivity.TrafficUsageSettingsFragment.AnonymousClass2 */

                    public void run() {
                        String[] formattedFileSizeAndUnitForDisplay = Utils.getFormattedFileSizeAndUnitForDisplay(null, TrafficUsageSettingsFragment.this.checkDataTotalValue(j), false, true);
                        TrafficUsageSettingsFragment.this.traffic_total_settings.setSummary(formattedFileSizeAndUnitForDisplay[0] + " " + formattedFileSizeAndUnitForDisplay[1]);
                    }
                });
            }
        }

        public void refreshDataUsedPre(final long j) {
            if (this.mActivity == null || !isAdded()) {
                Log.d("TrafficUsageSettingsFragment", "mActivity: " + this.mActivity);
                Log.d("TrafficUsageSettingsFragment", "isAdded: " + isAdded());
                return;
            }
            Log.d("TrafficUsageSettingsFragment", "refreshDataUsedPre: " + j);
            this.mUsedByte = j;
            this.mActivity.runOnUiThread(new Runnable() {
                /* class com.oneplus.security.network.view.TrafficUsageSettingsActivity.TrafficUsageSettingsFragment.AnonymousClass3 */

                public void run() {
                    String[] formattedFileSizeAndUnitForDisplay = Utils.getFormattedFileSizeAndUnitForDisplay(null, j, false, true);
                    TrafficUsageSettingsFragment.this.traffic_monthly_usage.setSummary(TrafficUsageSettingsFragment.this.mContext.getString(C0017R$string.traffic_used_summary, formattedFileSizeAndUnitForDisplay[0] + " " + formattedFileSizeAndUnitForDisplay[1]));
                }
            });
        }

        public void refreshDataWarnPre() {
            if (this.mActivity != null && isAdded()) {
                final boolean dataWarnState = TrafficUsageAlarmUtils.getDataWarnState(this.mContext, false, this.mCurrentSlotId);
                this.mActivity.runOnUiThread(new Runnable() {
                    /* class com.oneplus.security.network.view.TrafficUsageSettingsActivity.TrafficUsageSettingsFragment.AnonymousClass4 */

                    public void run() {
                        TrafficUsageSettingsFragment.this.traffic_warn_switch.setChecked(dataWarnState);
                        String[] formattedFileSizeAndUnitForDisplay = Utils.getFormattedFileSizeAndUnitForDisplay(null, TrafficUsageSettingsFragment.this.checkDataWarnValue(TrafficUsageAlarmUtils.getDataWarnValue(TrafficUsageSettingsFragment.this.mContext, TrafficUsageSettingsFragment.this.mCurrentSlotId, -1)), false, false);
                        TrafficUsageSettingsFragment.this.traffic_warn_settings.setSummary(formattedFileSizeAndUnitForDisplay[0] + " " + formattedFileSizeAndUnitForDisplay[1]);
                    }
                });
            }
        }

        public long checkDataTotalValue(long j) {
            if (j != -1) {
                return j;
            }
            int integer = this.mContext.getResources().getInteger(C0011R$integer.datausage_defaul_limit_value);
            if (FunctionUtils.isSupportUstMode()) {
                integer = this.mContext.getResources().getInteger(C0011R$integer.datausage_ust_limit_value);
            }
            return Math.max(((long) integer) * 1073741824, -1L);
        }

        public long checkDataWarnValue(long j) {
            long j2 = -1;
            if (j != -1) {
                return j;
            }
            NetworkPolicy policy = this.mPolicyEditor.getPolicy(this.mNetworkTemplate);
            if (policy != null) {
                j2 = (long) (((float) policy.warningBytes) * 1.2f);
            }
            return Math.max(2147483648L, j2);
        }

        private void showResetDayDialog() {
            int i;
            if (!this.mDialogShowing) {
                this.mDialogShowing = true;
                try {
                    i = this.mOperatorDataModel.getAccountDay(this.mCurrentSlotId);
                } catch (NumberFormatException e) {
                    LogUtils.e("TrafficUsageSettingsFragment", e.getLocalizedMessage());
                    i = 1;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
                View inflate = LayoutInflater.from(builder.getContext()).inflate(C0012R$layout.dialog_account_day_setting, (ViewGroup) null, false);
                final NumberPicker numberPicker = (NumberPicker) inflate.findViewById(C0010R$id.np_account_day);
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(31);
                numberPicker.setValue(i);
                numberPicker.setWrapSelectorWheel(false);
                builder.setTitle(C0017R$string.traffic_reset_day_title);
                builder.setView(inflate);
                builder.setPositiveButton(C0017R$string.yes, new DialogInterface.OnClickListener() {
                    /* class com.oneplus.security.network.view.TrafficUsageSettingsActivity.TrafficUsageSettingsFragment.AnonymousClass5 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        numberPicker.clearFocus();
                        int value = numberPicker.getValue();
                        TrafficUsageSettingsFragment.this.mOperatorDataModel.setOperatorAccountDay(TrafficUsageSettingsFragment.this.mCurrentSlotId, value);
                        String str = new Time().timezone;
                        TrafficUsageSettingsFragment trafficUsageSettingsFragment = TrafficUsageSettingsFragment.this;
                        trafficUsageSettingsFragment.mPolicyEditor.setPolicyCycleDay(trafficUsageSettingsFragment.mNetworkTemplate, value, str);
                    }
                });
                builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    /* class com.oneplus.security.network.view.TrafficUsageSettingsActivity.TrafficUsageSettingsFragment.AnonymousClass6 */

                    public void onDismiss(DialogInterface dialogInterface) {
                        TrafficUsageSettingsFragment.this.mDialogShowing = false;
                    }
                });
                builder.show();
                numberPicker.requestFocus();
            }
        }

        private void showConfirmSetDataLimitDialog(final long j) {
            if (!this.mDialogShowing) {
                this.mDialogShowing = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
                if (FunctionUtils.isUsvMode()) {
                    builder.setTitle(C0017R$string.traffic_usage_limit_dialog_title_vzw);
                    builder.setMessage(C0017R$string.traffic_usage_limit_dialog_message_vzw);
                } else {
                    builder.setTitle(C0017R$string.traffic_usage_limit_dialog_title);
                    builder.setMessage(C0017R$string.traffic_usage_limit_dialog_message);
                }
                builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
                    /* class com.oneplus.security.network.view.TrafficUsageSettingsActivity.TrafficUsageSettingsFragment.AnonymousClass7 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.sendAppTracker("data_limit", 1);
                        long checkDataTotalValue = TrafficUsageSettingsFragment.this.checkDataTotalValue(j);
                        TrafficUsageAlarmUtils.setDataLimitValue(TrafficUsageSettingsFragment.this.mContext, (checkDataTotalValue * 1024) / 1024, TrafficUsageSettingsFragment.this.mCurrentSlotId);
                        TrafficUsageAlarmUtils.setDataTotalState(TrafficUsageSettingsFragment.this.mContext, true, TrafficUsageSettingsFragment.this.mCurrentSlotId);
                        SecureService.startServiceForDataUsage(TrafficUsageSettingsFragment.this.mContext, TrafficUsageSettingsFragment.this.mCurrentSlotId);
                        TrafficUsageSettingsFragment.this.mOperatorDataModel.setPackageTotalUsage(TrafficUsageSettingsFragment.this.mCurrentSlotId, (long) ((int) (checkDataTotalValue / 1024)));
                        TrafficUsageSettingsFragment.this.refreshDataTotalPre(checkDataTotalValue);
                        TrafficUsageSettingsFragment.this.traffic_total_switch.setChecked(true);
                        TrafficUsageAlarmIntentService.startService(TrafficUsageSettingsFragment.this.mContext);
                        TrafficUsageAlarmUtils.resetTrafficDialogAlertedState(TrafficUsageSettingsFragment.this.mContext, TrafficUsageSettingsFragment.this.mCurrentSlotId);
                    }
                });
                builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    /* class com.oneplus.security.network.view.TrafficUsageSettingsActivity.TrafficUsageSettingsFragment.AnonymousClass8 */

                    public void onDismiss(DialogInterface dialogInterface) {
                        TrafficUsageSettingsFragment.this.mDialogShowing = false;
                    }
                });
                builder.show();
            }
        }

        private void showBytesEditorDialog(NetworkTemplate networkTemplate, final int i) {
            int i2;
            if (!this.mDialogShowing) {
                this.mDialogShowing = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
                int i3 = 0;
                final View inflate = LayoutInflater.from(builder.getContext()).inflate(C0012R$layout.dialog_data_usage_editor, (ViewGroup) null, false);
                Spinner spinner = (Spinner) inflate.findViewById(C0010R$id.size_spinner);
                CharSequence[] charSequenceArr = new CharSequence[2];
                if (Utils.hasSDK27()) {
                    charSequenceArr[0] = getResources().getString(C0017R$string.megabyteShort);
                    charSequenceArr[1] = getResources().getString(C0017R$string.gigabyteShort);
                } else {
                    int identifier = getResources().getIdentifier("megabyteShort", "string", "android");
                    int identifier2 = getResources().getIdentifier("gigabyteShort", "string", "android");
                    charSequenceArr[0] = getResources().getString(identifier);
                    charSequenceArr[1] = getResources().getString(identifier2);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this.mContext, 17367048, charSequenceArr);
                arrayAdapter.setDropDownViewResource(17367049);
                spinner.setAdapter((SpinnerAdapter) arrayAdapter);
                setupPicker((EditText) inflate.findViewById(C0010R$id.bytes), spinner, i);
                if (i != 0) {
                    if (i != 1) {
                        if (i != 2) {
                            i2 = C0017R$string.traffic_used_title;
                        } else {
                            i2 = C0017R$string.traffic_used_title;
                        }
                    } else if (FunctionUtils.isUsvMode()) {
                        i2 = C0017R$string.traffic_set_warn_title_vzw;
                        i3 = C0017R$string.traffic_set_warn_description_vzw;
                    } else {
                        i2 = C0017R$string.traffic_set_warn_title;
                    }
                } else if (FunctionUtils.isUsvMode()) {
                    i2 = C0017R$string.traffic_set_limit_title_vzw;
                    i3 = C0017R$string.traffic_set_limit_description_vzw;
                } else {
                    i2 = C0017R$string.traffic_set_limit_title;
                }
                builder.setTitle(i2);
                if (FunctionUtils.isUsvMode() && i3 != 0) {
                    builder.setMessage(i3);
                }
                builder.setView(inflate);
                builder.setPositiveButton(C0017R$string.yes, new DialogInterface.OnClickListener() {
                    /* class com.oneplus.security.network.view.TrafficUsageSettingsActivity.TrafficUsageSettingsFragment.AnonymousClass9 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        long j;
                        Spinner spinner = (Spinner) inflate.findViewById(C0010R$id.size_spinner);
                        String obj = ((EditText) inflate.findViewById(C0010R$id.bytes)).getText().toString();
                        if (obj.isEmpty()) {
                            obj = "0";
                        }
                        try {
                            long floatValue = (long) (Float.valueOf(obj).floatValue() * ((float) (spinner.getSelectedItemPosition() == 0 ? 1048576 : 1073741824)));
                            if (floatValue > 2199023255552L) {
                                ToastUtil.showLongToast(TrafficUsageSettingsFragment.this.mContext, TrafficUsageSettingsFragment.this.getString(C0017R$string.datausage_max_number_tips, "2048 GB"));
                                return;
                            }
                            long j2 = 0;
                            int i2 = (floatValue > 0 ? 1 : (floatValue == 0 ? 0 : -1));
                            if (i2 > 0 || i == 2) {
                                if (i2 < 0) {
                                    floatValue = 0;
                                }
                                if (floatValue != 0) {
                                    j = floatValue / 1024;
                                    j2 = 1024 * j;
                                } else {
                                    j = 0;
                                }
                                Log.d("TrafficUsageSettingsFragment", "onClick: kbToByte" + j2);
                                Log.d("TrafficUsageSettingsFragment", "onClick: kb" + j);
                                int i3 = i;
                                if (i3 == 0) {
                                    try {
                                        TrafficUsageSettingsFragment.this.mOperatorDataModel.setPackageTotalUsage(TrafficUsageSettingsFragment.this.mCurrentSlotId, j);
                                    } catch (Exception e) {
                                        LogUtils.e("TrafficUsageSettingsFragment", "mOperatorPrefModel.setTotalPkgSizeInKb error." + e.getMessage());
                                    }
                                    TrafficUsageSettingsFragment.this.refreshDataTotalPre(j2);
                                    TrafficUsageAlarmUtils.setDataLimitValue(TrafficUsageSettingsFragment.this.mContext, floatValue, TrafficUsageSettingsFragment.this.mCurrentSlotId);
                                    SecureService.startServiceForDataUsage(TrafficUsageSettingsFragment.this.mContext, TrafficUsageSettingsFragment.this.mCurrentSlotId);
                                } else if (i3 == 1) {
                                    TrafficUsageSettingsFragment.this.mOperatorDataModel.setWarnByteValue(TrafficUsageSettingsFragment.this.mCurrentSlotId, j2);
                                    TrafficUsageSettingsFragment.this.refreshDataWarnPre();
                                    SecureService.startServiceForDataUsage(TrafficUsageSettingsFragment.this.mContext, TrafficUsageSettingsFragment.this.mCurrentSlotId);
                                } else {
                                    AutoCalibrateUtil.setLastCalibrateTime(TrafficUsageSettingsFragment.this.mContext, System.currentTimeMillis(), TrafficUsageSettingsFragment.this.mCurrentSlotId, true);
                                    TrafficUsageSettingsFragment.this.mOperatorDataModel.setPackageMonthlyUsage(TrafficUsageSettingsFragment.this.mCurrentSlotId, j);
                                    SecureService.startServiceForDataUsage(TrafficUsageSettingsFragment.this.mContext, TrafficUsageSettingsFragment.this.mCurrentSlotId);
                                    Log.d("TrafficUsageSettingsFragment", "onClick: kbToByte" + j2);
                                    TrafficUsageSettingsFragment.this.refreshDataUsedPre(j2);
                                }
                                TrafficUsageAlarmUtils.resetTrafficDialogAlertedState(TrafficUsageSettingsFragment.this.mContext, TrafficUsageSettingsFragment.this.mCurrentSlotId);
                                TrafficUsageAlarmIntentService.startService(TrafficUsageSettingsFragment.this.mContext);
                            }
                        } catch (NumberFormatException unused) {
                            LogUtils.e("TrafficUsageSettingsFragment", "cast number error . bytesString=" + obj);
                        }
                    }
                });
                builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    /* class com.oneplus.security.network.view.TrafficUsageSettingsActivity.TrafficUsageSettingsFragment.AnonymousClass10 */

                    public void onDismiss(DialogInterface dialogInterface) {
                        TrafficUsageSettingsFragment.this.mDialogShowing = false;
                    }
                });
                builder.show();
            }
        }

        private void setupPicker(EditText editText, Spinner spinner, int i) {
            long j;
            if (i == 0) {
                j = this.mOperatorDataModel.getPkgTotalInByte(this.mCurrentSlotId);
            } else if (i != 1) {
                j = i != 2 ? 0 : this.mUsedByte;
            } else {
                j = TrafficUsageAlarmUtils.getDataWarnValue(this.mContext, this.mCurrentSlotId, -1);
            }
            float f = (float) j;
            if (f > 1.61061274E9f) {
                editText.setText(formatText(f / 1.07374182E9f));
                spinner.setSelection(1);
            } else {
                editText.setText(formatText(f / 1048576.0f));
                spinner.setSelection(0);
            }
            Utils.setEditTextAtLastLocation(editText);
        }

        private String formatText(float f) {
            return String.valueOf(((float) Math.round(f * 100.0f)) / 100.0f);
        }
    }
}
