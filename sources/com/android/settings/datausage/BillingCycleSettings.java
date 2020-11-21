package com.android.settings.datausage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.NetworkPolicy;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.text.method.NumberKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.NetworkPolicyEditor;
import com.android.settingslib.net.DataUsageController;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.TimeZone;

public class BillingCycleSettings extends DataUsageBaseFragment implements Preference.OnPreferenceChangeListener, DataUsageEditController {
    static final String KEY_SET_DATA_LIMIT = "set_data_limit";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.billing_cycle) {
        /* class com.android.settings.datausage.BillingCycleSettings.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return DataUsageUtils.hasMobileData(context);
        }
    };
    private Preference mBillingCycle;
    private Preference mDataLimit;
    private DataUsageController mDataUsageController;
    private Preference mDataWarning;
    private SwitchPreference mEnableDataLimit;
    private SwitchPreference mEnableDataWarning;
    NetworkTemplate mNetworkTemplate;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BillingCycleSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 342;
    }

    /* access modifiers changed from: package-private */
    public void setUpForTest(NetworkPolicyEditor networkPolicyEditor, Preference preference, Preference preference2, Preference preference3, SwitchPreference switchPreference, SwitchPreference switchPreference2) {
        this.services.mPolicyEditor = networkPolicyEditor;
        this.mBillingCycle = preference;
        this.mDataLimit = preference2;
        this.mDataWarning = preference3;
        this.mEnableDataLimit = switchPreference;
        this.mEnableDataWarning = switchPreference2;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.datausage.DataUsageBaseFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        this.mDataUsageController = new DataUsageController(context);
        NetworkTemplate parcelable = getArguments().getParcelable("network_template");
        this.mNetworkTemplate = parcelable;
        if (parcelable == null) {
            this.mNetworkTemplate = DataUsageUtils.getDefaultTemplate(context, DataUsageUtils.getDefaultSubscriptionId(context));
        }
        this.mBillingCycle = findPreference("billing_cycle");
        SwitchPreference switchPreference = (SwitchPreference) findPreference("set_data_warning");
        this.mEnableDataWarning = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        this.mDataWarning = findPreference("data_warning");
        SwitchPreference switchPreference2 = (SwitchPreference) findPreference(KEY_SET_DATA_LIMIT);
        this.mEnableDataLimit = switchPreference2;
        switchPreference2.setOnPreferenceChangeListener(this);
        this.mDataLimit = findPreference("data_limit");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.datausage.DataUsageBaseFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updatePrefs();
    }

    /* access modifiers changed from: package-private */
    public void updatePrefs() {
        this.mBillingCycle.setSummary((CharSequence) null);
        long policyWarningBytes = this.services.mPolicyEditor.getPolicyWarningBytes(this.mNetworkTemplate);
        if (policyWarningBytes != -1) {
            this.mDataWarning.setSummary(DataUsageUtils.formatDataUsage(getContext(), policyWarningBytes));
            this.mDataWarning.setEnabled(true);
            this.mEnableDataWarning.setChecked(true);
        } else {
            this.mDataWarning.setSummary((CharSequence) null);
            this.mDataWarning.setEnabled(false);
            this.mEnableDataWarning.setChecked(false);
        }
        long policyLimitBytes = this.services.mPolicyEditor.getPolicyLimitBytes(this.mNetworkTemplate);
        if (policyLimitBytes != -1) {
            this.mDataLimit.setSummary(DataUsageUtils.formatDataUsage(getContext(), policyLimitBytes));
            this.mDataLimit.setEnabled(true);
            this.mEnableDataLimit.setChecked(true);
            return;
        }
        this.mDataLimit.setSummary((CharSequence) null);
        this.mDataLimit.setEnabled(false);
        this.mEnableDataLimit.setChecked(false);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == this.mBillingCycle) {
            writePreferenceClickMetric(preference);
            CycleEditorFragment.show(this);
            return true;
        } else if (preference == this.mDataWarning) {
            writePreferenceClickMetric(preference);
            BytesEditorFragment.show((DataUsageEditController) this, false);
            return true;
        } else if (preference != this.mDataLimit) {
            return super.onPreferenceTreeClick(preference);
        } else {
            writePreferenceClickMetric(preference);
            BytesEditorFragment.show((DataUsageEditController) this, true);
            return true;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mEnableDataLimit == preference) {
            if (!((Boolean) obj).booleanValue()) {
                setPolicyLimitBytes(-1);
                return true;
            }
            ConfirmLimitFragment.show(this);
            return false;
        } else if (this.mEnableDataWarning != preference) {
            return false;
        } else {
            if (((Boolean) obj).booleanValue()) {
                setPolicyWarningBytes(this.mDataUsageController.getDefaultWarningLevel());
            } else {
                setPolicyWarningBytes(-1);
            }
            return true;
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.billing_cycle;
    }

    /* access modifiers changed from: package-private */
    public void setPolicyLimitBytes(long j) {
        this.services.mPolicyEditor.setPolicyLimitBytes(this.mNetworkTemplate, j);
        updatePrefs();
    }

    private void setPolicyWarningBytes(long j) {
        this.services.mPolicyEditor.setPolicyWarningBytes(this.mNetworkTemplate, j);
        updatePrefs();
    }

    @Override // com.android.settings.datausage.DataUsageEditController
    public NetworkPolicyEditor getNetworkPolicyEditor() {
        return this.services.mPolicyEditor;
    }

    @Override // com.android.settings.datausage.DataUsageEditController
    public NetworkTemplate getNetworkTemplate() {
        return this.mNetworkTemplate;
    }

    @Override // com.android.settings.datausage.DataUsageEditController
    public void updateDataUsage() {
        updatePrefs();
    }

    public static class BytesEditorFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
        private View mView;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 550;
        }

        public static void show(DataUsageEditController dataUsageEditController, boolean z) {
            if (dataUsageEditController instanceof Fragment) {
                Fragment fragment = (Fragment) dataUsageEditController;
                if (fragment.isAdded()) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("template", dataUsageEditController.getNetworkTemplate());
                    bundle.putBoolean("limit", z);
                    BytesEditorFragment bytesEditorFragment = new BytesEditorFragment();
                    bytesEditorFragment.setArguments(bundle);
                    bytesEditorFragment.setTargetFragment(fragment, 0);
                    bytesEditorFragment.show(fragment.getFragmentManager(), "warningEditor");
                }
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            int i;
            FragmentActivity activity = getActivity();
            LayoutInflater from = LayoutInflater.from(activity);
            boolean z = getArguments().getBoolean("limit");
            View inflate = from.inflate(C0012R$layout.data_usage_bytes_editor, (ViewGroup) null, false);
            this.mView = inflate;
            setupPicker((EditText) inflate.findViewById(C0010R$id.bytes), (Spinner) this.mView.findViewById(C0010R$id.size_spinner));
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            if (z) {
                i = C0017R$string.data_usage_limit_editor_title;
            } else {
                i = C0017R$string.data_usage_warning_editor_title;
            }
            builder.setTitle(i);
            builder.setView(this.mView);
            builder.setPositiveButton(C0017R$string.data_usage_cycle_editor_positive, this);
            return builder.create();
        }

        private void setupPicker(EditText editText, Spinner spinner) {
            long j;
            NetworkPolicyEditor networkPolicyEditor = ((DataUsageEditController) getTargetFragment()).getNetworkPolicyEditor();
            editText.setKeyListener(new NumberKeyListener(this) {
                /* class com.android.settings.datausage.BillingCycleSettings.BytesEditorFragment.AnonymousClass1 */

                public int getInputType() {
                    return 8194;
                }

                /* access modifiers changed from: protected */
                public char[] getAcceptedChars() {
                    return new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ',', '.'};
                }
            });
            NetworkTemplate networkTemplate = (NetworkTemplate) getArguments().getParcelable("template");
            if (getArguments().getBoolean("limit")) {
                j = networkPolicyEditor.getPolicyLimitBytes(networkTemplate);
            } else {
                j = networkPolicyEditor.getPolicyWarningBytes(networkTemplate);
            }
            int i = ((float) j) > 1.61061274E9f ? 1 : 0;
            String formatText = formatText((double) j, i != 0 ? 1.073741824E9d : 1048576.0d);
            editText.setText(formatText);
            editText.setSelection(0, formatText.length());
            spinner.setSelection(i);
        }

        private String formatText(double d, double d2) {
            NumberFormat numberInstance = NumberFormat.getNumberInstance();
            numberInstance.setMaximumFractionDigits(2);
            return numberInstance.format(d / d2);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                DataUsageEditController dataUsageEditController = (DataUsageEditController) getTargetFragment();
                NetworkPolicyEditor networkPolicyEditor = dataUsageEditController.getNetworkPolicyEditor();
                NetworkTemplate networkTemplate = (NetworkTemplate) getArguments().getParcelable("template");
                boolean z = getArguments().getBoolean("limit");
                Spinner spinner = (Spinner) this.mView.findViewById(C0010R$id.size_spinner);
                Number number = null;
                try {
                    number = NumberFormat.getNumberInstance().parse(((EditText) this.mView.findViewById(C0010R$id.bytes)).getText().toString());
                } catch (ParseException unused) {
                }
                long j = 0;
                if (number != null) {
                    j = (long) (number.floatValue() * ((float) (spinner.getSelectedItemPosition() == 0 ? 1048576 : 1073741824)));
                }
                long min = Math.min(53687091200000L, j);
                if (z) {
                    networkPolicyEditor.setPolicyLimitBytes(networkTemplate, min);
                } else {
                    networkPolicyEditor.setPolicyWarningBytes(networkTemplate, min);
                }
                dataUsageEditController.updateDataUsage();
            }
        }
    }

    public static class CycleEditorFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
        private NumberPicker mCycleDayPicker;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 549;
        }

        public static void show(BillingCycleSettings billingCycleSettings) {
            if (billingCycleSettings.isAdded()) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("template", billingCycleSettings.mNetworkTemplate);
                CycleEditorFragment cycleEditorFragment = new CycleEditorFragment();
                cycleEditorFragment.setArguments(bundle);
                cycleEditorFragment.setTargetFragment(billingCycleSettings, 0);
                cycleEditorFragment.show(billingCycleSettings.getFragmentManager(), "cycleEditor");
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            FragmentActivity activity = getActivity();
            NetworkPolicyEditor networkPolicyEditor = ((DataUsageEditController) getTargetFragment()).getNetworkPolicyEditor();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            View inflate = LayoutInflater.from(builder.getContext()).inflate(C0012R$layout.data_usage_cycle_editor, (ViewGroup) null, false);
            this.mCycleDayPicker = (NumberPicker) inflate.findViewById(C0010R$id.cycle_day);
            int policyCycleDay = networkPolicyEditor.getPolicyCycleDay((NetworkTemplate) getArguments().getParcelable("template"));
            this.mCycleDayPicker.setMinValue(1);
            this.mCycleDayPicker.setMaxValue(31);
            this.mCycleDayPicker.setValue(policyCycleDay);
            this.mCycleDayPicker.setWrapSelectorWheel(true);
            builder.setTitle(C0017R$string.data_usage_cycle_editor_title);
            builder.setView(inflate);
            builder.setPositiveButton(C0017R$string.data_usage_cycle_editor_positive, this);
            return builder.create();
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            DataUsageEditController dataUsageEditController = (DataUsageEditController) getTargetFragment();
            NetworkPolicyEditor networkPolicyEditor = dataUsageEditController.getNetworkPolicyEditor();
            this.mCycleDayPicker.clearFocus();
            networkPolicyEditor.setPolicyCycleDay(getArguments().getParcelable("template"), this.mCycleDayPicker.getValue(), TimeZone.getDefault().getID());
            dataUsageEditController.updateDataUsage();
        }
    }

    public static class ConfirmLimitFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
        static final String EXTRA_LIMIT_BYTES = "limitBytes";

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 551;
        }

        public static void show(BillingCycleSettings billingCycleSettings) {
            NetworkPolicy policy;
            if (billingCycleSettings.isAdded() && (policy = billingCycleSettings.services.mPolicyEditor.getPolicy(billingCycleSettings.mNetworkTemplate)) != null) {
                billingCycleSettings.getResources();
                long max = Math.max(5368709120L, (long) (((float) policy.warningBytes) * 1.2f));
                Bundle bundle = new Bundle();
                bundle.putLong(EXTRA_LIMIT_BYTES, max);
                ConfirmLimitFragment confirmLimitFragment = new ConfirmLimitFragment();
                confirmLimitFragment.setArguments(bundle);
                confirmLimitFragment.setTargetFragment(billingCycleSettings, 0);
                confirmLimitFragment.show(billingCycleSettings.getFragmentManager(), "confirmLimit");
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(C0017R$string.data_usage_limit_dialog_title);
            builder.setMessage(C0017R$string.data_usage_limit_dialog_mobile);
            builder.setPositiveButton(17039370, this);
            builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            return builder.create();
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            BillingCycleSettings billingCycleSettings = (BillingCycleSettings) getTargetFragment();
            if (i == -1) {
                long j = getArguments().getLong(EXTRA_LIMIT_BYTES);
                if (billingCycleSettings != null) {
                    billingCycleSettings.setPolicyLimitBytes(j);
                }
                billingCycleSettings.getPreferenceManager().getSharedPreferences().edit().putBoolean(BillingCycleSettings.KEY_SET_DATA_LIMIT, true).apply();
            }
        }
    }
}
