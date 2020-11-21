package com.android.settings.print;

import android.content.Context;
import android.os.Bundle;
import android.print.PrintJob;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;

public class PrintJobSettingsFragment extends DashboardFragment {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "PrintJobSettingsFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 78;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.print_job_settings;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((PrintJobPreferenceController) use(PrintJobPreferenceController.class)).init(this);
        ((PrintJobMessagePreferenceController) use(PrintJobMessagePreferenceController.class)).init(this);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        getListView().setEnabled(false);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        PrintJob printJob = ((PrintJobPreferenceController) use(PrintJobPreferenceController.class)).getPrintJob();
        if (printJob != null) {
            if (!printJob.getInfo().isCancelling()) {
                menu.add(0, 1, 0, getString(C0017R$string.print_cancel)).setShowAsAction(1);
            }
            if (printJob.isFailed()) {
                menu.add(0, 2, 0, getString(C0017R$string.print_restart)).setShowAsAction(1);
            }
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        PrintJob printJob = ((PrintJobPreferenceController) use(PrintJobPreferenceController.class)).getPrintJob();
        if (printJob != null) {
            int itemId = menuItem.getItemId();
            if (itemId == 1) {
                printJob.cancel();
                finish();
                return true;
            } else if (itemId == 2) {
                printJob.restart();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
