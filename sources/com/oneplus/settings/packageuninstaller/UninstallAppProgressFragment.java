package com.oneplus.settings.packageuninstaller;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.packageuninstaller.UninstallAppProgress;

public class UninstallAppProgressFragment extends Fragment implements View.OnClickListener, UninstallAppProgress.ProgressFragment {
    private Button mDeviceManagerButton;
    private Button mOkButton;
    private Button mUsersButton;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0012R$layout.op_uninstall_progress, viewGroup, false);
        PackageUtil.initSnippetForInstalledApp(getContext(), ((UninstallAppProgress) getActivity()).getAppInfo(), inflate.findViewById(C0010R$id.app_snippet));
        this.mDeviceManagerButton = (Button) inflate.findViewById(C0010R$id.device_manager_button);
        this.mUsersButton = (Button) inflate.findViewById(C0010R$id.users_button);
        this.mDeviceManagerButton.setVisibility(8);
        this.mDeviceManagerButton.setOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.packageuninstaller.UninstallAppProgressFragment.AnonymousClass1 */

            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName(OPMemberController.PACKAGE_NAME, "com.android.settings.Settings$DeviceAdminSettingsActivity");
                intent.setFlags(1342177280);
                UninstallAppProgressFragment.this.startActivity(intent);
                UninstallAppProgressFragment.this.getActivity().finish();
            }
        });
        this.mUsersButton.setVisibility(8);
        this.mUsersButton.setOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.packageuninstaller.UninstallAppProgressFragment.AnonymousClass2 */

            public void onClick(View view) {
                Intent intent = new Intent("android.settings.USER_SETTINGS");
                intent.setFlags(1342177280);
                UninstallAppProgressFragment.this.startActivity(intent);
                UninstallAppProgressFragment.this.getActivity().finish();
            }
        });
        Button button = (Button) inflate.findViewById(C0010R$id.ok_button);
        this.mOkButton = button;
        button.setOnClickListener(this);
        return inflate;
    }

    public void onClick(View view) {
        UninstallAppProgress uninstallAppProgress = (UninstallAppProgress) getActivity();
        if (view == this.mOkButton && uninstallAppProgress != null) {
            Log.i("UninstallAppProgressF", "Finished uninstalling pkg: " + uninstallAppProgress.getAppInfo().packageName);
            uninstallAppProgress.setResultAndFinish();
        }
    }

    @Override // com.oneplus.settings.packageuninstaller.UninstallAppProgress.ProgressFragment
    public void setUsersButtonVisible(boolean z) {
        this.mUsersButton.setVisibility(z ? 0 : 8);
    }

    @Override // com.oneplus.settings.packageuninstaller.UninstallAppProgress.ProgressFragment
    public void setDeviceManagerButtonVisible(boolean z) {
        this.mDeviceManagerButton.setVisibility(z ? 0 : 8);
    }

    @Override // com.oneplus.settings.packageuninstaller.UninstallAppProgress.ProgressFragment
    public void showCompletion(CharSequence charSequence) {
        View view = getView();
        view.findViewById(C0010R$id.progress_view).setVisibility(8);
        view.findViewById(C0010R$id.status_view).setVisibility(0);
        ((TextView) view.findViewById(C0010R$id.status_text)).setText(charSequence);
        view.findViewById(C0010R$id.ok_panel).setVisibility(0);
    }
}
