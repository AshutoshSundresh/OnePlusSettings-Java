package com.android.settings.notification.zen;

import android.app.Activity;
import android.app.AutomaticZenRule;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.service.notification.ZenModeConfig;
import android.util.Slog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.LayoutPreference;

public class ZenAutomaticRuleHeaderPreferenceController extends AbstractZenModePreferenceController implements PreferenceControllerMixin {
    private EntityHeaderController mController;
    private final PreferenceFragmentCompat mFragment;
    private AutomaticZenRule mRule;

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return "pref_app_header";
    }

    public ZenAutomaticRuleHeaderPreferenceController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, Lifecycle lifecycle) {
        super(context, "pref_app_header", lifecycle);
        this.mFragment = preferenceFragmentCompat;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mRule != null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        PreferenceFragmentCompat preferenceFragmentCompat;
        if (this.mRule != null && (preferenceFragmentCompat = this.mFragment) != null) {
            if (this.mController == null) {
                this.mController = EntityHeaderController.newInstance(preferenceFragmentCompat.getActivity(), this.mFragment, ((LayoutPreference) preference).findViewById(C0010R$id.entity_header));
            }
            EntityHeaderController entityHeaderController = this.mController;
            entityHeaderController.setIcon(getIcon());
            entityHeaderController.setLabel(this.mRule.getName());
            entityHeaderController.done((Activity) this.mFragment.getActivity(), false);
        }
    }

    private Drawable getIcon() {
        try {
            PackageManager packageManager = this.mContext.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.mRule.getOwner().getPackageName(), 0);
            if (applicationInfo.isSystemApp()) {
                if (ZenModeConfig.isValidScheduleConditionId(this.mRule.getConditionId())) {
                    return this.mContext.getDrawable(C0008R$drawable.ic_timelapse);
                }
                if (ZenModeConfig.isValidEventConditionId(this.mRule.getConditionId())) {
                    return this.mContext.getDrawable(C0008R$drawable.ic_event);
                }
            }
            return applicationInfo.loadIcon(packageManager);
        } catch (PackageManager.NameNotFoundException unused) {
            Slog.w("PrefControllerMixin", "Unable to load icon - PackageManager.NameNotFoundException");
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public void onResume(AutomaticZenRule automaticZenRule, String str) {
        this.mRule = automaticZenRule;
    }
}
