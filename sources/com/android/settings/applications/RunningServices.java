package com.android.settings.applications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.LoadingViewController;

public class RunningServices extends SettingsPreferenceFragment {
    private View mLoadingContainer;
    private LoadingViewController mLoadingViewController;
    private Menu mOptionsMenu;
    private final Runnable mRunningProcessesAvail = new Runnable() {
        /* class com.android.settings.applications.RunningServices.AnonymousClass1 */

        public void run() {
            RunningServices.this.mLoadingViewController.showContent(true);
        }
    };
    private RunningProcessesView mRunningProcessesView;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 404;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(C0017R$string.runningservices_settings_title);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0012R$layout.manage_applications_running, (ViewGroup) null);
        RunningProcessesView runningProcessesView = (RunningProcessesView) inflate.findViewById(C0010R$id.running_processes);
        this.mRunningProcessesView = runningProcessesView;
        runningProcessesView.doCreate();
        View findViewById = inflate.findViewById(C0010R$id.loading_container);
        this.mLoadingContainer = findViewById;
        this.mLoadingViewController = new LoadingViewController(findViewById, this.mRunningProcessesView);
        return inflate;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        this.mOptionsMenu = menu;
        menu.add(0, 1, 1, C0017R$string.show_running_services).setShowAsAction(0);
        menu.add(0, 2, 2, C0017R$string.show_background_processes).setShowAsAction(0);
        updateOptionsMenu();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        this.mLoadingViewController.handleLoadingContainer(this.mRunningProcessesView.doResume(this, this.mRunningProcessesAvail), false);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mRunningProcessesView.doPause();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            this.mRunningProcessesView.mAdapter.setShowBackground(false);
        } else if (itemId != 2) {
            return false;
        } else {
            this.mRunningProcessesView.mAdapter.setShowBackground(true);
        }
        updateOptionsMenu();
        return true;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPrepareOptionsMenu(Menu menu) {
        updateOptionsMenu();
    }

    private void updateOptionsMenu() {
        boolean showBackground = this.mRunningProcessesView.mAdapter.getShowBackground();
        this.mOptionsMenu.findItem(1).setVisible(showBackground);
        this.mOptionsMenu.findItem(2).setVisible(!showBackground);
    }
}
