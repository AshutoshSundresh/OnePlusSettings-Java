package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.zen.ZenRuleNameDialog;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.Map;

public abstract class AbstractZenModeAutomaticRulePreferenceController extends AbstractZenModePreferenceController implements PreferenceControllerMixin {
    protected ZenModeBackend mBackend;
    protected Fragment mParent;
    protected Map.Entry<String, AutomaticZenRule>[] mRules;

    public AbstractZenModeAutomaticRulePreferenceController(Context context, String str, Fragment fragment, Lifecycle lifecycle) {
        super(context, str, lifecycle);
        this.mBackend = ZenModeBackend.getInstance(context);
        this.mContext.getPackageManager();
        this.mParent = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mRules = this.mBackend.getAutomaticZenRules();
    }

    /* access modifiers changed from: protected */
    public Map.Entry<String, AutomaticZenRule>[] getRules() {
        if (this.mRules == null) {
            this.mRules = this.mBackend.getAutomaticZenRules();
        }
        return this.mRules;
    }

    /* access modifiers changed from: protected */
    public void showNameRuleDialog(ZenRuleInfo zenRuleInfo, Fragment fragment) {
        ZenRuleNameDialog.show(fragment, null, zenRuleInfo.defaultConditionId, new RuleNameChangeListener(zenRuleInfo));
    }

    protected static Intent getRuleIntent(String str, ComponentName componentName, String str2) {
        Intent putExtra = new Intent().addFlags(67108864).putExtra("android.service.notification.extra.RULE_ID", str2);
        if (componentName != null) {
            putExtra.setComponent(componentName);
        } else {
            putExtra.setAction(str);
        }
        return putExtra;
    }

    public static ZenRuleInfo getRuleInfo(PackageManager packageManager, ComponentInfo componentInfo) {
        Bundle bundle;
        String str;
        int i;
        ComponentName componentName = null;
        if (!(componentInfo == null || (bundle = componentInfo.metaData) == null)) {
            boolean z = componentInfo instanceof ServiceInfo;
            if (z) {
                str = bundle.getString("android.service.zen.automatic.ruleType");
            } else {
                str = bundle.getString("android.service.zen.automatic.ruleType");
            }
            ComponentName settingsActivity = getSettingsActivity(null, componentInfo);
            if (!(str == null || str.trim().isEmpty() || settingsActivity == null)) {
                ZenRuleInfo zenRuleInfo = new ZenRuleInfo();
                if (z) {
                    componentName = new ComponentName(componentInfo.packageName, componentInfo.name);
                }
                zenRuleInfo.serviceComponent = componentName;
                zenRuleInfo.settingsAction = "android.settings.ZEN_MODE_EXTERNAL_RULE_SETTINGS";
                zenRuleInfo.title = str;
                zenRuleInfo.packageName = componentInfo.packageName;
                zenRuleInfo.configurationActivity = settingsActivity;
                zenRuleInfo.packageLabel = componentInfo.applicationInfo.loadLabel(packageManager);
                if (z) {
                    i = componentInfo.metaData.getInt("android.service.zen.automatic.ruleInstanceLimit", -1);
                } else {
                    i = componentInfo.metaData.getInt("android.service.zen.automatic.ruleInstanceLimit", -1);
                }
                zenRuleInfo.ruleInstanceLimit = i;
                return zenRuleInfo;
            }
        }
        return null;
    }

    protected static ComponentName getSettingsActivity(AutomaticZenRule automaticZenRule, ComponentInfo componentInfo) {
        String string;
        if (automaticZenRule != null && automaticZenRule.getConfigurationActivity() != null) {
            return automaticZenRule.getConfigurationActivity();
        }
        if (componentInfo == null) {
            return null;
        }
        if (componentInfo instanceof ActivityInfo) {
            return new ComponentName(componentInfo.packageName, componentInfo.name);
        }
        Bundle bundle = componentInfo.metaData;
        if (bundle == null || (string = bundle.getString("android.service.zen.automatic.configurationActivity")) == null) {
            return null;
        }
        return ComponentName.unflattenFromString(string);
    }

    public class RuleNameChangeListener implements ZenRuleNameDialog.PositiveClickListener {
        ZenRuleInfo mRuleInfo;

        public RuleNameChangeListener(ZenRuleInfo zenRuleInfo) {
            this.mRuleInfo = zenRuleInfo;
        }

        @Override // com.android.settings.notification.zen.ZenRuleNameDialog.PositiveClickListener
        public void onOk(String str, Fragment fragment) {
            AbstractZenModeAutomaticRulePreferenceController abstractZenModeAutomaticRulePreferenceController = AbstractZenModeAutomaticRulePreferenceController.this;
            abstractZenModeAutomaticRulePreferenceController.mMetricsFeatureProvider.action(((AbstractPreferenceController) abstractZenModeAutomaticRulePreferenceController).mContext, 1267, new Pair[0]);
            ZenRuleInfo zenRuleInfo = this.mRuleInfo;
            String addZenRule = AbstractZenModeAutomaticRulePreferenceController.this.mBackend.addZenRule(new AutomaticZenRule(str, zenRuleInfo.serviceComponent, zenRuleInfo.configurationActivity, zenRuleInfo.defaultConditionId, null, 2, true));
            if (addZenRule != null) {
                fragment.startActivity(AbstractZenModeAutomaticRulePreferenceController.getRuleIntent(this.mRuleInfo.settingsAction, null, addZenRule));
            }
        }
    }
}
