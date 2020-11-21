package com.android.settings.applications;

import android.os.Bundle;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C0012R$layout;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.applications.AppUtils;

public abstract class AppInfoWithHeader extends AppInfoBase {
    private boolean mCreated;

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (this.mCreated) {
            Log.w("AppInfoWithHeader", "onActivityCreated: ignoring duplicate call");
            return;
        }
        this.mCreated = true;
        if (this.mPackageInfo != null) {
            FragmentActivity activity = getActivity();
            EntityHeaderController newInstance = EntityHeaderController.newInstance(activity, this, LayoutInflater.from(activity).inflate(C0012R$layout.op_settings_entity_header, (ViewGroup) null));
            newInstance.setRecyclerView(getListView(), getSettingsLifecycle());
            newInstance.setIcon(IconDrawableFactory.newInstance(getContext()).getBadgedIcon(this.mPackageInfo.applicationInfo));
            newInstance.setLabel(this.mPackageInfo.applicationInfo.loadLabel(this.mPm));
            newInstance.setSummary(this.mPackageInfo);
            newInstance.setIsInstantApp(AppUtils.isInstant(this.mPackageInfo.applicationInfo));
            newInstance.setPackageName(this.mPackageName);
            newInstance.setUid(this.mPackageInfo.applicationInfo.uid);
            newInstance.setHasAppInfoLink(true);
            newInstance.setButtonActions(0, 0);
            getPreferenceScreen().addPreference(newInstance.done(activity, getPrefContext()));
        }
    }
}
