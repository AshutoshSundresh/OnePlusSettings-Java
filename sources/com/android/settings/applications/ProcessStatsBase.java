package com.android.settings.applications;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import com.android.internal.app.procstats.ProcessStats;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.ProcStatsData;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.widget.settingsspinner.SettingsSpinnerAdapter;

public abstract class ProcessStatsBase extends SettingsPreferenceFragment implements AdapterView.OnItemSelectedListener {
    private static final long DURATION_QUANTUM;
    protected static int[] sDurationLabels = {C0017R$string.menu_duration_3h, C0017R$string.menu_duration_6h, C0017R$string.menu_duration_12h, C0017R$string.menu_duration_1d};
    public static long[] sDurations;
    protected int mDurationIndex;
    private ArrayAdapter<String> mFilterAdapter;
    private Spinner mFilterSpinner;
    private ViewGroup mSpinnerHeader;
    protected ProcStatsData mStatsManager;

    public abstract void refreshUi();

    static {
        long j = ProcessStats.COMMIT_PERIOD;
        DURATION_QUANTUM = j;
        sDurations = new long[]{10800000 - (j / 2), 21600000 - (j / 2), 43200000 - (j / 2), 86400000 - (j / 2)};
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        this.mStatsManager = new ProcStatsData(getActivity(), bundle != null || (arguments != null && arguments.getBoolean("transfer_stats", false)));
        if (bundle != null) {
            i = bundle.getInt("duration_index");
        } else {
            i = arguments != null ? arguments.getInt("duration_index") : 0;
        }
        this.mDurationIndex = i;
        this.mStatsManager.setDuration(bundle != null ? bundle.getLong("duration", sDurations[0]) : sDurations[0]);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putLong("duration", this.mStatsManager.getDuration());
        bundle.putInt("duration_index", this.mDurationIndex);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        this.mStatsManager.refreshStats(false);
        refreshUi();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        if (getActivity().isChangingConfigurations()) {
            this.mStatsManager.xferStats();
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ViewGroup viewGroup = (ViewGroup) setPinnedHeaderView(C0012R$layout.apps_filter_spinner);
        this.mSpinnerHeader = viewGroup;
        this.mFilterSpinner = (Spinner) viewGroup.findViewById(C0010R$id.filter_spinner);
        this.mFilterAdapter = new SettingsSpinnerAdapter(this.mFilterSpinner.getContext());
        for (int i = 0; i < 4; i++) {
            this.mFilterAdapter.add(getString(sDurationLabels[i]));
        }
        this.mFilterSpinner.setAdapter((SpinnerAdapter) this.mFilterAdapter);
        this.mFilterSpinner.setSelection(this.mDurationIndex);
        this.mFilterSpinner.setOnItemSelectedListener(this);
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        this.mDurationIndex = i;
        this.mStatsManager.setDuration(sDurations[i]);
        refreshUi();
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
        this.mFilterSpinner.setSelection(0);
    }

    public static void launchMemoryDetail(SettingsActivity settingsActivity, ProcStatsData.MemInfo memInfo, ProcStatsPackageEntry procStatsPackageEntry, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("package_entry", procStatsPackageEntry);
        bundle.putDouble("weight_to_ram", memInfo.weightToRam);
        bundle.putLong("total_time", memInfo.memTotalTime);
        bundle.putDouble("max_memory_usage", memInfo.usedWeight * memInfo.weightToRam);
        bundle.putDouble("total_scale", memInfo.totalScale);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(settingsActivity);
        subSettingLauncher.setDestination(ProcessStatsDetail.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.memory_usage);
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setSourceMetricsCategory(0);
        subSettingLauncher.launch();
    }
}
