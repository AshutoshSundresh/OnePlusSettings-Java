package com.android.settings.notification.zen;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import androidx.preference.Preference;
import com.android.settings.C0019R$xml;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.zen.ZenModeSettings;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ZenCustomRuleConfigSettings extends ZenCustomRuleSettingsBase {
    private Preference mCallsPreference;
    private Preference mMessagesPreference;
    private Preference mNotificationsPreference;
    private ZenModeSettings.SummaryBuilder mSummaryBuilder;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1605;
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase
    public String getPreferenceCategoryKey() {
        return "zen_custom_rule_configuration_category";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.notification.zen.ZenModeSettingsBase, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSummaryBuilder = new ZenModeSettings.SummaryBuilder(this.mContext);
        Preference findPreference = getPreferenceScreen().findPreference("zen_rule_calls_settings");
        this.mCallsPreference = findPreference;
        findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.notification.zen.ZenCustomRuleConfigSettings.AnonymousClass1 */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                SubSettingLauncher subSettingLauncher = new SubSettingLauncher(ZenCustomRuleConfigSettings.this.mContext);
                subSettingLauncher.setDestination(ZenCustomRuleCallsSettings.class.getName());
                subSettingLauncher.setArguments(ZenCustomRuleConfigSettings.this.createZenRuleBundle());
                subSettingLauncher.setSourceMetricsCategory(1611);
                subSettingLauncher.launch();
                return true;
            }
        });
        Preference findPreference2 = getPreferenceScreen().findPreference("zen_rule_messages_settings");
        this.mMessagesPreference = findPreference2;
        findPreference2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.notification.zen.ZenCustomRuleConfigSettings.AnonymousClass2 */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                SubSettingLauncher subSettingLauncher = new SubSettingLauncher(ZenCustomRuleConfigSettings.this.mContext);
                subSettingLauncher.setDestination(ZenCustomRuleMessagesSettings.class.getName());
                subSettingLauncher.setArguments(ZenCustomRuleConfigSettings.this.createZenRuleBundle());
                subSettingLauncher.setSourceMetricsCategory(1610);
                subSettingLauncher.launch();
                return true;
            }
        });
        Preference findPreference3 = getPreferenceScreen().findPreference("zen_rule_notifications");
        this.mNotificationsPreference = findPreference3;
        findPreference3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.notification.zen.ZenCustomRuleConfigSettings.AnonymousClass3 */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                SubSettingLauncher subSettingLauncher = new SubSettingLauncher(ZenCustomRuleConfigSettings.this.mContext);
                subSettingLauncher.setDestination(ZenCustomRuleNotificationsSettings.class.getName());
                subSettingLauncher.setArguments(ZenCustomRuleConfigSettings.this.createZenRuleBundle());
                subSettingLauncher.setSourceMetricsCategory(1608);
                subSettingLauncher.launch();
                return true;
            }
        });
        updateSummaries();
    }

    @Override // com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.notification.zen.ZenCustomRuleSettingsBase
    public void onZenModeConfigChanged() {
        super.onZenModeConfigChanged();
        updateSummaries();
    }

    private void updateSummaries() {
        NotificationManager.Policy notificationPolicy = this.mBackend.toNotificationPolicy(this.mRule.getZenPolicy());
        this.mCallsPreference.setSummary(this.mSummaryBuilder.getCallsSettingSummary(notificationPolicy));
        this.mMessagesPreference.setSummary(this.mSummaryBuilder.getMessagesSettingSummary(notificationPolicy));
        this.mNotificationsPreference.setSummary(this.mSummaryBuilder.getBlockedEffectsSummary(notificationPolicy));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.zen_mode_custom_rule_configuration;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((ZenCustomRuleSettingsBase) this).mControllers = arrayList;
        arrayList.add(new ZenRuleCustomSwitchPreferenceController(context, getSettingsLifecycle(), "zen_rule_alarms", 5, 1226));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleCustomSwitchPreferenceController(context, getSettingsLifecycle(), "zen_rule_media", 6, 1227));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleCustomSwitchPreferenceController(context, getSettingsLifecycle(), "zen_rule_system", 7, 1340));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleCustomSwitchPreferenceController(context, getSettingsLifecycle(), "zen_rule_reminders", 0, 167));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleCustomSwitchPreferenceController(context, getSettingsLifecycle(), "zen_rule_events", 1, 168));
        return ((ZenCustomRuleSettingsBase) this).mControllers;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.notification.zen.ZenCustomRuleSettingsBase, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateSummaries();
    }
}
