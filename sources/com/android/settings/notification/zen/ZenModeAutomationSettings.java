package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.utils.ManagedServiceSettings;
import com.android.settings.utils.ZenServiceListing;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZenModeAutomationSettings extends ZenModeSettingsBase {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.zen_mode_automation_settings) {
        /* class com.android.settings.notification.zen.ZenModeAutomationSettings.AnonymousClass3 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            nonIndexableKeys.add("zen_mode_add_automatic_rule");
            nonIndexableKeys.add("zen_mode_automatic_rules");
            return nonIndexableKeys;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ZenModeAutomationSettings.buildPreferenceControllers(context, null, null, null);
        }
    };
    protected final ManagedServiceSettings.Config CONFIG = getConditionProviderConfig();
    private boolean[] mDeleteDialogChecked;
    private String[] mDeleteDialogRuleIds;
    private CharSequence[] mDeleteDialogRuleNames;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 142;
    }

    @Override // com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("DELETE_RULE")) {
            this.mBackend.removeZenRule(arguments.getString("DELETE_RULE"));
            arguments.remove("DELETE_RULE");
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ZenServiceListing zenServiceListing = new ZenServiceListing(getContext(), this.CONFIG);
        zenServiceListing.reloadApprovedServices();
        return buildPreferenceControllers(context, this, zenServiceListing, getSettingsLifecycle());
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Fragment fragment, ZenServiceListing zenServiceListing, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ZenModeAddAutomaticRulePreferenceController(context, fragment, zenServiceListing, lifecycle));
        arrayList.add(new ZenModeAutomaticRulesPreferenceController(context, fragment, lifecycle));
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.zen_mode_automation_settings;
    }

    protected static ManagedServiceSettings.Config getConditionProviderConfig() {
        ManagedServiceSettings.Config.Builder builder = new ManagedServiceSettings.Config.Builder();
        builder.setTag("ZenModeSettings");
        builder.setIntentAction("android.service.notification.ConditionProviderService");
        builder.setConfigurationIntentAction("android.app.action.AUTOMATIC_ZEN_RULE");
        builder.setPermission("android.permission.BIND_CONDITION_PROVIDER_SERVICE");
        builder.setNoun("condition provider");
        return builder.build();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.add(0, 1, 0, C0017R$string.zen_mode_delete_automatic_rules);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 1) {
            return super.onOptionsItemSelected(menuItem);
        }
        final Map.Entry<String, AutomaticZenRule>[] automaticZenRules = this.mBackend.getAutomaticZenRules();
        this.mDeleteDialogRuleNames = new CharSequence[automaticZenRules.length];
        this.mDeleteDialogRuleIds = new String[automaticZenRules.length];
        this.mDeleteDialogChecked = new boolean[automaticZenRules.length];
        for (int i = 0; i < automaticZenRules.length; i++) {
            this.mDeleteDialogRuleNames[i] = automaticZenRules[i].getValue().getName();
            this.mDeleteDialogRuleIds[i] = automaticZenRules[i].getKey();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle(C0017R$string.zen_mode_delete_automatic_rules);
        builder.setMultiChoiceItems(this.mDeleteDialogRuleNames, null, new DialogInterface.OnMultiChoiceClickListener() {
            /* class com.android.settings.notification.zen.ZenModeAutomationSettings.AnonymousClass2 */

            public void onClick(DialogInterface dialogInterface, int i, boolean z) {
                ZenModeAutomationSettings.this.mDeleteDialogChecked[i] = z;
            }
        });
        builder.setPositiveButton(C0017R$string.zen_mode_schedule_delete, new DialogInterface.OnClickListener() {
            /* class com.android.settings.notification.zen.ZenModeAutomationSettings.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                for (int i2 = 0; i2 < automaticZenRules.length; i2++) {
                    if (ZenModeAutomationSettings.this.mDeleteDialogChecked[i2]) {
                        ZenModeAutomationSettings zenModeAutomationSettings = ZenModeAutomationSettings.this;
                        zenModeAutomationSettings.mBackend.removeZenRule(zenModeAutomationSettings.mDeleteDialogRuleIds[i2]);
                    }
                }
            }
        });
        builder.show();
        return true;
    }
}
