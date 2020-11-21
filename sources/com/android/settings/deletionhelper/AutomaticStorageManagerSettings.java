package com.android.settings.deletionhelper;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class AutomaticStorageManagerSettings extends DashboardFragment implements Preference.OnPreferenceChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.deletionhelper.AutomaticStorageManagerSettings.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return false;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return AutomaticStorageManagerSettings.buildPreferenceControllers(context);
        }
    };
    private DropDownPreference mDaysToRetain;
    private SwitchBar mSwitchBar;
    private AutomaticStorageManagerSwitchBarController mSwitchController;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 458;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        initializeDaysToRetainPreference();
        initializeSwitchBar();
        return onCreateView;
    }

    private void initializeDaysToRetainPreference() {
        DropDownPreference dropDownPreference = (DropDownPreference) findPreference("days");
        this.mDaysToRetain = dropDownPreference;
        dropDownPreference.setOnPreferenceChangeListener(this);
        int i = Settings.Secure.getInt(getContentResolver(), "automatic_storage_manager_days_to_retain", Utils.getDefaultStorageManagerDaysToRetain(getResources()));
        String[] stringArray = getResources().getStringArray(C0003R$array.automatic_storage_management_days_values);
        this.mDaysToRetain.setValue(stringArray[daysValueToIndex(i, stringArray)]);
    }

    private void initializeSwitchBar() {
        SwitchBar switchBar = ((SettingsActivity) getActivity()).getSwitchBar();
        this.mSwitchBar = switchBar;
        int i = C0017R$string.automatic_storage_manager_master_switch_title;
        switchBar.setSwitchBarText(i, i);
        this.mSwitchBar.show();
        this.mSwitchController = new AutomaticStorageManagerSwitchBarController(getContext(), this.mSwitchBar, this.mMetricsFeatureProvider, this.mDaysToRetain, getFragmentManager());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        this.mDaysToRetain.setEnabled(Utils.isStorageManagerEnabled(getContext()));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.automatic_storage_management_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mSwitchBar.hide();
        this.mSwitchController.tearDown();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!"days".equals(preference.getKey())) {
            return true;
        }
        Settings.Secure.putInt(getContentResolver(), "automatic_storage_manager_days_to_retain", Integer.parseInt((String) obj));
        return true;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_uri_storage;
    }

    private static int daysValueToIndex(int i, String[] strArr) {
        for (int i2 = 0; i2 < strArr.length; i2++) {
            if (i == Integer.parseInt(strArr[i2])) {
                return i2;
            }
        }
        return strArr.length - 1;
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new AutomaticStorageManagerDescriptionPreferenceController(context));
        return arrayList;
    }
}
