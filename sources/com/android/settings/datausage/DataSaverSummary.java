package com.android.settings.datausage;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import androidx.preference.Preference;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.datausage.AppStateDataUsageBridge;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.applications.ApplicationsState;
import java.util.ArrayList;

public class DataSaverSummary extends SettingsPreferenceFragment implements SwitchBar.OnSwitchChangeListener, DataSaverBackend.Listener, AppStateBaseBridge.Callback, ApplicationsState.Callbacks {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.data_saver) {
        /* class com.android.settings.datausage.DataSaverSummary.AnonymousClass2 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return true;
        }
    };
    private ApplicationsState mApplicationsState;
    private DataSaverBackend mDataSaverBackend;
    private AppStateDataUsageBridge mDataUsageBridge;
    private ApplicationsState.Session mSession;
    private SwitchBar mSwitchBar;
    private boolean mSwitching;
    private Preference mUnrestrictedAccess;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 348;
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onAllSizesComputed() {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onBlacklistStatusChanged(int i, boolean z) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLauncherInfoChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageIconChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageListChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageSizeChanged(String str) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRunningStateChanged(boolean z) {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onWhitelistStatusChanged(int i, boolean z) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.data_saver);
        this.mUnrestrictedAccess = findPreference("unrestricted_access");
        this.mApplicationsState = ApplicationsState.getInstance((Application) getContext().getApplicationContext());
        DataSaverBackend dataSaverBackend = new DataSaverBackend(getContext());
        this.mDataSaverBackend = dataSaverBackend;
        this.mDataUsageBridge = new AppStateDataUsageBridge(this.mApplicationsState, this, dataSaverBackend);
        this.mSession = this.mApplicationsState.newSession(this, getSettingsLifecycle());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SwitchBar switchBar = ((SettingsActivity) getActivity()).getSwitchBar();
        this.mSwitchBar = switchBar;
        int i = C0017R$string.data_saver_switch_title;
        switchBar.setSwitchBarText(i, i);
        this.mSwitchBar.show();
        this.mSwitchBar.addOnSwitchChangeListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        DataSaverBackend dataSaverBackend = new DataSaverBackend(getContext());
        this.mDataSaverBackend = dataSaverBackend;
        this.mDataUsageBridge = new AppStateDataUsageBridge(this.mApplicationsState, this, dataSaverBackend);
        this.mSession = this.mApplicationsState.newSession(this);
        this.mDataSaverBackend.refreshWhitelist();
        this.mDataSaverBackend.refreshBlacklist();
        this.mDataSaverBackend.addListener(this);
        this.mDataUsageBridge.resume();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mDataSaverBackend.remListener(this);
        this.mDataUsageBridge.pause();
    }

    @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        synchronized (this) {
            if (!this.mSwitching) {
                this.mSwitching = true;
                this.mDataSaverBackend.setDataSaverEnabled(z);
            }
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_data_saver;
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        synchronized (this) {
            this.mSwitchBar.setChecked(z);
            this.mSwitching = false;
        }
    }

    @Override // com.android.settings.applications.AppStateBaseBridge.Callback
    public void onExtraInfoUpdated() {
        Object obj;
        if (isAdded()) {
            ArrayList<ApplicationsState.AppEntry> allApps = this.mSession.getAllApps();
            int size = allApps.size();
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                ApplicationsState.AppEntry appEntry = allApps.get(i2);
                if (ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER.filterApp(appEntry) && (obj = appEntry.extraInfo) != null && ((AppStateDataUsageBridge.DataUsageState) obj).isDataSaverWhitelisted) {
                    i++;
                }
            }
            this.mUnrestrictedAccess.setSummary(getResources().getQuantityString(C0015R$plurals.data_saver_unrestricted_summary, i, Integer.valueOf(i)));
        }
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLoadEntriesCompleted() {
        this.mSwitchBar.postDelayed(new Runnable() {
            /* class com.android.settings.datausage.DataSaverSummary.AnonymousClass1 */

            public void run() {
                Log.d("DataSaverSummary", "onLoadEntriesCompleted............");
                DataSaverSummary.this.mDataUsageBridge.resume();
            }
        }, 300);
    }
}
