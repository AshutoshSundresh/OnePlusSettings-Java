package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.zen.ZenDeleteRuleDialog;
import com.android.settings.notification.zen.ZenRuleNameDialog;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.ActionButtonsPreference;

public class ZenRuleButtonsPreferenceController extends AbstractZenModePreferenceController implements PreferenceControllerMixin {
    private PreferenceFragmentCompat mFragment;
    private String mId;
    private AutomaticZenRule mRule;

    public ZenRuleButtonsPreferenceController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, Lifecycle lifecycle) {
        super(context, "zen_action_buttons", lifecycle);
        this.mFragment = preferenceFragmentCompat;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mRule != null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        if (isAvailable()) {
            ActionButtonsPreference actionButtonsPreference = (ActionButtonsPreference) preferenceScreen.findPreference("zen_action_buttons");
            actionButtonsPreference.setButton1Text(C0017R$string.zen_mode_rule_name_edit);
            actionButtonsPreference.setButton1Icon(17302751);
            actionButtonsPreference.setButton1OnClickListener(new EditRuleNameClickListener());
            actionButtonsPreference.setButton2Text(C0017R$string.zen_mode_delete_rule_button);
            actionButtonsPreference.setButton2Icon(C0008R$drawable.ic_settings_delete);
            actionButtonsPreference.setButton2OnClickListener(new DeleteRuleClickListener());
        }
    }

    public class EditRuleNameClickListener implements View.OnClickListener {
        public EditRuleNameClickListener() {
        }

        public void onClick(View view) {
            ZenRuleNameDialog.show(ZenRuleButtonsPreferenceController.this.mFragment, ZenRuleButtonsPreferenceController.this.mRule.getName(), null, new ZenRuleNameDialog.PositiveClickListener() {
                /* class com.android.settings.notification.zen.ZenRuleButtonsPreferenceController.EditRuleNameClickListener.AnonymousClass1 */

                @Override // com.android.settings.notification.zen.ZenRuleNameDialog.PositiveClickListener
                public void onOk(String str, Fragment fragment) {
                    if (!TextUtils.equals(str, ZenRuleButtonsPreferenceController.this.mRule.getName())) {
                        ZenRuleButtonsPreferenceController zenRuleButtonsPreferenceController = ZenRuleButtonsPreferenceController.this;
                        zenRuleButtonsPreferenceController.mMetricsFeatureProvider.action(((AbstractPreferenceController) zenRuleButtonsPreferenceController).mContext, 1267, new Pair[0]);
                        ZenRuleButtonsPreferenceController.this.mRule.setName(str);
                        ZenRuleButtonsPreferenceController.this.mRule.setModified(true);
                        ZenRuleButtonsPreferenceController zenRuleButtonsPreferenceController2 = ZenRuleButtonsPreferenceController.this;
                        zenRuleButtonsPreferenceController2.mBackend.updateZenRule(zenRuleButtonsPreferenceController2.mId, ZenRuleButtonsPreferenceController.this.mRule);
                    }
                }
            });
        }
    }

    public class DeleteRuleClickListener implements View.OnClickListener {
        public DeleteRuleClickListener() {
        }

        public void onClick(View view) {
            ZenDeleteRuleDialog.show(ZenRuleButtonsPreferenceController.this.mFragment, ZenRuleButtonsPreferenceController.this.mRule.getName(), ZenRuleButtonsPreferenceController.this.mId, new ZenDeleteRuleDialog.PositiveClickListener() {
                /* class com.android.settings.notification.zen.ZenRuleButtonsPreferenceController.DeleteRuleClickListener.AnonymousClass1 */

                @Override // com.android.settings.notification.zen.ZenDeleteRuleDialog.PositiveClickListener
                public void onOk(String str) {
                    Bundle bundle = new Bundle();
                    bundle.putString("DELETE_RULE", str);
                    ZenRuleButtonsPreferenceController zenRuleButtonsPreferenceController = ZenRuleButtonsPreferenceController.this;
                    zenRuleButtonsPreferenceController.mMetricsFeatureProvider.action(((AbstractPreferenceController) zenRuleButtonsPreferenceController).mContext, 175, new Pair[0]);
                    SubSettingLauncher subSettingLauncher = new SubSettingLauncher(((AbstractPreferenceController) ZenRuleButtonsPreferenceController.this).mContext);
                    subSettingLauncher.addFlags(67108864);
                    subSettingLauncher.setDestination(ZenModeAutomationSettings.class.getName());
                    subSettingLauncher.setSourceMetricsCategory(142);
                    subSettingLauncher.setArguments(bundle);
                    subSettingLauncher.launch();
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void onResume(AutomaticZenRule automaticZenRule, String str) {
        this.mRule = automaticZenRule;
        this.mId = str;
    }
}
