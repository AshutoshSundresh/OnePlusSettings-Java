package com.android.settings.applications;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.ArrayMap;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.CancellablePreference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SummaryPreference;
import com.android.settings.widget.EntityHeaderController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ProcessStatsDetail extends SettingsPreferenceFragment {
    static final Comparator<ProcStatsEntry> sEntryCompare = new Comparator<ProcStatsEntry>() {
        /* class com.android.settings.applications.ProcessStatsDetail.AnonymousClass2 */

        public int compare(ProcStatsEntry procStatsEntry, ProcStatsEntry procStatsEntry2) {
            double d = procStatsEntry.mRunWeight;
            double d2 = procStatsEntry2.mRunWeight;
            if (d < d2) {
                return 1;
            }
            return d > d2 ? -1 : 0;
        }
    };
    private ProcStatsPackageEntry mApp;
    private DevicePolicyManager mDpm;
    private MenuItem mForceStop;
    private double mMaxMemoryUsage;
    private PackageManager mPm;
    private PreferenceCategory mProcGroup;
    private final ArrayMap<ComponentName, CancellablePreference> mServiceMap = new ArrayMap<>();
    private double mTotalScale;
    private long mTotalTime;
    private double mWeightToRam;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 21;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mPm = getActivity().getPackageManager();
        this.mDpm = (DevicePolicyManager) getActivity().getSystemService("device_policy");
        Bundle arguments = getArguments();
        ProcStatsPackageEntry procStatsPackageEntry = (ProcStatsPackageEntry) arguments.getParcelable("package_entry");
        this.mApp = procStatsPackageEntry;
        procStatsPackageEntry.retrieveUiData(getActivity(), this.mPm);
        this.mWeightToRam = arguments.getDouble("weight_to_ram");
        this.mTotalTime = arguments.getLong("total_time");
        this.mMaxMemoryUsage = arguments.getDouble("max_memory_usage");
        this.mTotalScale = arguments.getDouble("total_scale");
        long j = this.mTotalTime / 100;
        this.mServiceMap.clear();
        createDetails();
        setHasOptionsMenu(true);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        Drawable drawable;
        super.onViewCreated(view, bundle);
        if (this.mApp.mUiTargetApp == null) {
            finish();
            return;
        }
        FragmentActivity activity = getActivity();
        EntityHeaderController newInstance = EntityHeaderController.newInstance(activity, this, null);
        newInstance.setRecyclerView(getListView(), getSettingsLifecycle());
        if (this.mApp.mUiTargetApp != null) {
            drawable = IconDrawableFactory.newInstance(activity).getBadgedIcon(this.mApp.mUiTargetApp);
        } else {
            drawable = new ColorDrawable(0);
        }
        newInstance.setIcon(drawable);
        newInstance.setLabel(this.mApp.mUiLabel);
        newInstance.setPackageName(this.mApp.mPackage);
        ApplicationInfo applicationInfo = this.mApp.mUiTargetApp;
        newInstance.setUid(applicationInfo != null ? applicationInfo.uid : -10000);
        newInstance.setHasAppInfoLink(true);
        newInstance.setButtonActions(0, 0);
        getPreferenceScreen().addPreference(newInstance.done(activity, getPrefContext()));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        checkForceStop();
        updateRunningServices();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0047, code lost:
        r3 = r3.service;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateRunningServices() {
        /*
            r6 = this;
            androidx.fragment.app.FragmentActivity r0 = r6.getActivity()
            java.lang.String r1 = "activity"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0
            r1 = 2147483647(0x7fffffff, float:NaN)
            java.util.List r0 = r0.getRunningServices(r1)
            android.util.ArrayMap<android.content.ComponentName, com.android.settings.CancellablePreference> r1 = r6.mServiceMap
            int r1 = r1.size()
            r2 = 0
            r3 = r2
        L_0x001b:
            if (r3 >= r1) goto L_0x002b
            android.util.ArrayMap<android.content.ComponentName, com.android.settings.CancellablePreference> r4 = r6.mServiceMap
            java.lang.Object r4 = r4.valueAt(r3)
            com.android.settings.CancellablePreference r4 = (com.android.settings.CancellablePreference) r4
            r4.setCancellable(r2)
            int r3 = r3 + 1
            goto L_0x001b
        L_0x002b:
            int r1 = r0.size()
        L_0x002f:
            if (r2 >= r1) goto L_0x0062
            java.lang.Object r3 = r0.get(r2)
            android.app.ActivityManager$RunningServiceInfo r3 = (android.app.ActivityManager.RunningServiceInfo) r3
            boolean r4 = r3.started
            if (r4 != 0) goto L_0x0040
            int r4 = r3.clientLabel
            if (r4 != 0) goto L_0x0040
            goto L_0x005f
        L_0x0040:
            int r4 = r3.flags
            r4 = r4 & 8
            if (r4 == 0) goto L_0x0047
            goto L_0x005f
        L_0x0047:
            android.content.ComponentName r3 = r3.service
            android.util.ArrayMap<android.content.ComponentName, com.android.settings.CancellablePreference> r4 = r6.mServiceMap
            java.lang.Object r4 = r4.get(r3)
            com.android.settings.CancellablePreference r4 = (com.android.settings.CancellablePreference) r4
            if (r4 == 0) goto L_0x005f
            com.android.settings.applications.ProcessStatsDetail$1 r5 = new com.android.settings.applications.ProcessStatsDetail$1
            r5.<init>(r3)
            r4.setOnCancelListener(r5)
            r3 = 1
            r4.setCancellable(r3)
        L_0x005f:
            int r2 = r2 + 1
            goto L_0x002f
        L_0x0062:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.ProcessStatsDetail.updateRunningServices():void");
    }

    private void createDetails() {
        addPreferencesFromResource(C0019R$xml.app_memory_settings);
        this.mProcGroup = (PreferenceCategory) findPreference("processes");
        fillProcessesSection();
        SummaryPreference summaryPreference = (SummaryPreference) findPreference("status_header");
        ProcStatsPackageEntry procStatsPackageEntry = this.mApp;
        double d = ((procStatsPackageEntry.mRunWeight > procStatsPackageEntry.mBgWeight ? 1 : (procStatsPackageEntry.mRunWeight == procStatsPackageEntry.mBgWeight ? 0 : -1)) > 0 ? this.mApp.mRunWeight : this.mApp.mBgWeight) * this.mWeightToRam;
        float f = (float) (d / this.mMaxMemoryUsage);
        FragmentActivity activity = getActivity();
        summaryPreference.setRatios(f, 0.0f, 1.0f - f);
        Formatter.BytesResult formatBytes = Formatter.formatBytes(activity.getResources(), (long) d, 1);
        summaryPreference.setAmount(formatBytes.value);
        summaryPreference.setUnits(formatBytes.units);
        ProcStatsPackageEntry procStatsPackageEntry2 = this.mApp;
        findPreference("frequency").setSummary(ProcStatsPackageEntry.getFrequency(((float) Math.max(procStatsPackageEntry2.mRunDuration, procStatsPackageEntry2.mBgDuration)) / ((float) this.mTotalTime), getActivity()));
        ProcStatsPackageEntry procStatsPackageEntry3 = this.mApp;
        findPreference("max_usage").setSummary(Formatter.formatShortFileSize(getContext(), (long) (((double) Math.max(procStatsPackageEntry3.mMaxBgMem, procStatsPackageEntry3.mMaxRunMem)) * this.mTotalScale * 1024.0d)));
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        this.mForceStop = menu.add(0, 1, 0, C0017R$string.force_stop);
        checkForceStop();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 1) {
            return false;
        }
        killProcesses();
        return true;
    }

    private void fillProcessesSection() {
        this.mProcGroup.removeAll();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mApp.mEntries.size(); i++) {
            ProcStatsEntry procStatsEntry = this.mApp.mEntries.get(i);
            if (procStatsEntry.mPackage.equals("os")) {
                procStatsEntry.mLabel = procStatsEntry.mName;
            } else {
                procStatsEntry.mLabel = getProcessName(this.mApp.mUiLabel, procStatsEntry);
            }
            arrayList.add(procStatsEntry);
        }
        Collections.sort(arrayList, sEntryCompare);
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            ProcStatsEntry procStatsEntry2 = (ProcStatsEntry) arrayList.get(i2);
            Preference preference = new Preference(getPrefContext());
            preference.setTitle(procStatsEntry2.mLabel);
            preference.setSelectable(false);
            long max = Math.max(procStatsEntry2.mRunDuration, procStatsEntry2.mBgDuration);
            double d = procStatsEntry2.mRunWeight;
            double d2 = this.mWeightToRam;
            preference.setSummary(getString(C0017R$string.memory_use_running_format, Formatter.formatShortFileSize(getActivity(), Math.max((long) (d * d2), (long) (procStatsEntry2.mBgWeight * d2))), ProcStatsPackageEntry.getFrequency(((float) max) / ((float) this.mTotalTime), getActivity())));
            this.mProcGroup.addPreference(preference);
        }
        if (this.mProcGroup.getPreferenceCount() < 2) {
            getPreferenceScreen().removePreference(this.mProcGroup);
        }
    }

    private static String capitalize(String str) {
        char charAt = str.charAt(0);
        if (!Character.isLowerCase(charAt)) {
            return str;
        }
        return Character.toUpperCase(charAt) + str.substring(1);
    }

    private static String getProcessName(String str, ProcStatsEntry procStatsEntry) {
        String str2 = procStatsEntry.mName;
        if (str2.contains(":")) {
            return capitalize(str2.substring(str2.lastIndexOf(58) + 1));
        }
        if (!str2.startsWith(procStatsEntry.mPackage)) {
            return str2;
        }
        if (str2.length() == procStatsEntry.mPackage.length()) {
            return str;
        }
        int length = procStatsEntry.mPackage.length();
        if (str2.charAt(length) == '.') {
            length++;
        }
        return capitalize(str2.substring(length));
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void stopService(String str, String str2) {
        try {
            if ((getActivity().getPackageManager().getApplicationInfo(str, 0).flags & 1) != 0) {
                showStopServiceDialog(str, str2);
            } else {
                doStopService(str, str2);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("ProcessStatsDetail", "Can't find app " + str, e);
        }
    }

    private void showStopServiceDialog(final String str, final String str2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0017R$string.runningservicedetails_stop_dlg_title);
        builder.setMessage(C0017R$string.runningservicedetails_stop_dlg_text);
        builder.setPositiveButton(C0017R$string.dlg_ok, new DialogInterface.OnClickListener() {
            /* class com.android.settings.applications.ProcessStatsDetail.AnonymousClass5 */

            public void onClick(DialogInterface dialogInterface, int i) {
                ProcessStatsDetail.this.doStopService(str, str2);
            }
        });
        builder.setNegativeButton(C0017R$string.dlg_cancel, (DialogInterface.OnClickListener) null);
        builder.show();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void doStopService(String str, String str2) {
        getActivity().stopService(new Intent().setClassName(str, str2));
        updateRunningServices();
    }

    private void killProcesses() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService("activity");
        for (int i = 0; i < this.mApp.mEntries.size(); i++) {
            ProcStatsEntry procStatsEntry = this.mApp.mEntries.get(i);
            for (int i2 = 0; i2 < procStatsEntry.mPackages.size(); i2++) {
                activityManager.forceStopPackage(procStatsEntry.mPackages.get(i2));
            }
        }
    }

    private void checkForceStop() {
        if (this.mForceStop != null) {
            if (this.mApp.mEntries.get(0).mUid < 10000) {
                this.mForceStop.setVisible(false);
                return;
            }
            boolean z = false;
            for (int i = 0; i < this.mApp.mEntries.size(); i++) {
                ProcStatsEntry procStatsEntry = this.mApp.mEntries.get(i);
                for (int i2 = 0; i2 < procStatsEntry.mPackages.size(); i2++) {
                    String str = procStatsEntry.mPackages.get(i2);
                    if (this.mDpm.packageHasActiveAdmins(str)) {
                        this.mForceStop.setEnabled(false);
                        return;
                    }
                    try {
                        if ((this.mPm.getApplicationInfo(str, 0).flags & 2097152) == 0) {
                            z = true;
                        }
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                }
            }
            if (z) {
                this.mForceStop.setVisible(true);
            }
        }
    }
}
