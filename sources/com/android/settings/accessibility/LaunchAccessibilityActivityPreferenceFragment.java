package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityShortcutInfo;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import java.util.List;

public class LaunchAccessibilityActivityPreferenceFragment extends ToggleFeaturePreferenceFragment {
    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mToggleServiceDividerSwitchPreference.setSwitchVisibility(8);
    }

    /* access modifiers changed from: protected */
    public void onPreferenceToggled(String str, boolean z) {
        AccessibilityStatsLogUtils.logAccessibilityServiceEnabled(this.mComponentName, z);
        launchShortcutTargetActivity(getPrefContext().getDisplayId(), this.mComponentName);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void onInstallSwitchPreferenceToggleSwitch() {
        super.onInstallSwitchPreferenceToggleSwitch();
        this.mToggleServiceDividerSwitchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.accessibility.$$Lambda$LaunchAccessibilityActivityPreferenceFragment$a3zPr9ZHUleAF30EQcwhRd1NWLc */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return LaunchAccessibilityActivityPreferenceFragment.this.lambda$onInstallSwitchPreferenceToggleSwitch$0$LaunchAccessibilityActivityPreferenceFragment(preference);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onInstallSwitchPreferenceToggleSwitch$0 */
    public /* synthetic */ boolean lambda$onInstallSwitchPreferenceToggleSwitch$0$LaunchAccessibilityActivityPreferenceFragment(Preference preference) {
        onPreferenceToggled(this.mPreferenceKey, ((DividerSwitchPreference) preference).isChecked());
        return false;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void onProcessArguments(Bundle bundle) {
        super.onProcessArguments(bundle);
        this.mComponentName = (ComponentName) bundle.getParcelable("component_name");
        this.mPackageName = getAccessibilityShortcutInfo().getActivityInfo().loadLabel(getPackageManager()).toString();
        this.mImageUri = new Uri.Builder().scheme("android.resource").authority(this.mComponentName.getPackageName()).appendPath(String.valueOf(bundle.getInt("animated_image_res"))).build();
        this.mHtmlDescription = bundle.getCharSequence("html_description");
        String string = bundle.getString("settings_title");
        Intent settingsIntent = TextUtils.isEmpty(string) ? null : getSettingsIntent(bundle);
        this.mSettingsIntent = settingsIntent;
        if (settingsIntent == null) {
            string = null;
        }
        this.mSettingsTitle = string;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onSettingsClicked(ShortcutPreference shortcutPreference) {
        super.onSettingsClicked(shortcutPreference);
        showDialog(1);
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public int getUserShortcutTypes() {
        return AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), this.mComponentName);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void updateToggleServiceTitle(SwitchPreference switchPreference) {
        String str;
        AccessibilityShortcutInfo accessibilityShortcutInfo = getAccessibilityShortcutInfo();
        if (accessibilityShortcutInfo == null) {
            str = "";
        } else {
            str = getString(C0017R$string.accessibility_service_master_open_title, accessibilityShortcutInfo.getActivityInfo().loadLabel(getPackageManager()));
        }
        switchPreference.setTitle(str);
    }

    private AccessibilityShortcutInfo getAccessibilityShortcutInfo() {
        List installedAccessibilityShortcutListAsUser = AccessibilityManager.getInstance(getPrefContext()).getInstalledAccessibilityShortcutListAsUser(getPrefContext(), UserHandle.myUserId());
        int size = installedAccessibilityShortcutListAsUser.size();
        for (int i = 0; i < size; i++) {
            AccessibilityShortcutInfo accessibilityShortcutInfo = (AccessibilityShortcutInfo) installedAccessibilityShortcutListAsUser.get(i);
            ActivityInfo activityInfo = accessibilityShortcutInfo.getActivityInfo();
            if (this.mComponentName.getPackageName().equals(activityInfo.packageName) && this.mComponentName.getClassName().equals(activityInfo.name)) {
                return accessibilityShortcutInfo;
            }
        }
        return null;
    }

    private void launchShortcutTargetActivity(int i, ComponentName componentName) {
        Intent intent = new Intent();
        Bundle bundle = ActivityOptions.makeBasic().setLaunchDisplayId(i).toBundle();
        intent.setComponent(componentName);
        intent.addFlags(268435456);
        try {
            getPrefContext().startActivityAsUser(intent, bundle, UserHandle.of(UserHandle.myUserId()));
        } catch (ActivityNotFoundException unused) {
            Log.w("LaunchA11yActivity", "Target activity not found.");
        }
    }

    private Intent getSettingsIntent(Bundle bundle) {
        String string = bundle.getString("settings_component_name");
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        Intent component = new Intent("android.intent.action.MAIN").setComponent(ComponentName.unflattenFromString(string));
        if (getPackageManager().queryIntentActivities(component, 0).isEmpty()) {
            return null;
        }
        return component;
    }
}
