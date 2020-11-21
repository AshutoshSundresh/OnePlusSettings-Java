package com.android.settings.accounts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.util.Log;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import java.util.ArrayList;
import java.util.List;

public class ManagedProfileSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.accounts.ManagedProfileSettings.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return false;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.managed_profile_settings;
            arrayList.add(searchIndexableResource);
            return arrayList;
        }
    };
    private ManagedProfileBroadcastReceiver mManagedProfileBroadcastReceiver;
    private UserHandle mManagedUser;
    private UserManager mUserManager;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ManagedProfileSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 401;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.managed_profile_settings;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mUserManager = (UserManager) getSystemService("user");
        UserHandle managedUserFromArgument = getManagedUserFromArgument();
        this.mManagedUser = managedUserFromArgument;
        if (managedUserFromArgument == null) {
            getActivity().finish();
        }
        ((WorkModePreferenceController) use(WorkModePreferenceController.class)).setManagedUser(this.mManagedUser);
        ((ContactSearchPreferenceController) use(ContactSearchPreferenceController.class)).setManagedUser(this.mManagedUser);
        ((CrossProfileCalendarPreferenceController) use(CrossProfileCalendarPreferenceController.class)).setManagedUser(this.mManagedUser);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ManagedProfileBroadcastReceiver managedProfileBroadcastReceiver = new ManagedProfileBroadcastReceiver();
        this.mManagedProfileBroadcastReceiver = managedProfileBroadcastReceiver;
        managedProfileBroadcastReceiver.register(getActivity());
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        ManagedProfileBroadcastReceiver managedProfileBroadcastReceiver = this.mManagedProfileBroadcastReceiver;
        if (managedProfileBroadcastReceiver != null) {
            managedProfileBroadcastReceiver.unregister(getActivity());
        }
    }

    private UserHandle getManagedUserFromArgument() {
        UserHandle userHandle;
        Bundle arguments = getArguments();
        if (arguments == null || (userHandle = (UserHandle) arguments.getParcelable("android.intent.extra.USER")) == null || !this.mUserManager.isManagedProfile(userHandle.getIdentifier())) {
            return Utils.getManagedProfile(this.mUserManager);
        }
        return userHandle;
    }

    private class ManagedProfileBroadcastReceiver extends BroadcastReceiver {
        private ManagedProfileBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                Log.v("ManagedProfileSettings", "Received broadcast: " + action);
                if (!"android.intent.action.MANAGED_PROFILE_REMOVED".equals(action)) {
                    Log.w("ManagedProfileSettings", "Cannot handle received broadcast: " + intent.getAction());
                } else if (intent.getIntExtra("android.intent.extra.user_handle", -10000) == ManagedProfileSettings.this.mManagedUser.getIdentifier()) {
                    ManagedProfileSettings.this.getActivity().finish();
                }
            }
        }

        public void register(Context context) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
            context.registerReceiver(this, intentFilter);
        }

        public void unregister(Context context) {
            context.unregisterReceiver(this);
        }
    }
}
