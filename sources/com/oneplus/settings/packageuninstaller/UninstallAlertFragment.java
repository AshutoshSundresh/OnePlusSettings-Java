package com.oneplus.settings.packageuninstaller;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import android.os.UserManager;
import androidx.leanback.app.GuidedStepFragment;
import androidx.leanback.widget.GuidanceStylist;
import androidx.leanback.widget.GuidedAction;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.oneplus.settings.packageuninstaller.UninstallerActivity;
import java.util.List;

public class UninstallAlertFragment extends GuidedStepFragment {
    @Override // androidx.leanback.app.GuidedStepFragment
    public int onProvideTheme() {
        return C0018R$style.Theme_Leanback_GuidedStep;
    }

    @Override // androidx.leanback.app.GuidedStepFragment
    public GuidanceStylist.Guidance onCreateGuidance(Bundle bundle) {
        PackageManager packageManager = getActivity().getPackageManager();
        UninstallerActivity.DialogInfo dialogInfo = ((UninstallerActivity) getActivity()).getDialogInfo();
        CharSequence loadSafeLabel = dialogInfo.appInfo.loadSafeLabel(packageManager);
        StringBuilder sb = new StringBuilder();
        ActivityInfo activityInfo = dialogInfo.activityInfo;
        if (activityInfo != null) {
            Object loadSafeLabel2 = activityInfo.loadSafeLabel(packageManager);
            if (!loadSafeLabel2.equals(loadSafeLabel)) {
                sb.append(getString(C0017R$string.uninstall_activity_text, loadSafeLabel2));
                sb.append(" ");
                sb.append(loadSafeLabel);
                sb.append(".\n\n");
            }
        }
        boolean z = (dialogInfo.appInfo.flags & 128) != 0;
        UserManager userManager = UserManager.get(getActivity());
        if (z) {
            if (isSingleUser(userManager)) {
                sb.append(getString(C0017R$string.uninstall_update_text));
            } else {
                sb.append(getString(C0017R$string.uninstall_update_text_multiuser));
            }
        } else if (dialogInfo.allUsers && !isSingleUser(userManager)) {
            sb.append(getString(C0017R$string.uninstall_application_text_all_users));
        } else if (!dialogInfo.user.equals(Process.myUserHandle())) {
            sb.append(getString(C0017R$string.uninstall_application_text_user, userManager.getUserInfo(dialogInfo.user.getIdentifier()).name));
        } else {
            sb.append(getString(C0017R$string.uninstall_application_text));
        }
        return new GuidanceStylist.Guidance(loadSafeLabel.toString(), sb.toString(), null, dialogInfo.appInfo.loadIcon(packageManager));
    }

    @Override // androidx.leanback.app.GuidedStepFragment
    public void onCreateActions(List<GuidedAction> list, Bundle bundle) {
        GuidedAction.Builder builder = new GuidedAction.Builder(getContext());
        builder.clickAction(-4);
        list.add(builder.build());
        GuidedAction.Builder builder2 = new GuidedAction.Builder(getContext());
        builder2.clickAction(-5);
        list.add(builder2.build());
    }

    @Override // androidx.leanback.app.GuidedStepFragment
    public void onGuidedActionClicked(GuidedAction guidedAction) {
        if (!isAdded()) {
            return;
        }
        if (guidedAction.getId() == -4) {
            ((UninstallerActivity) getActivity()).startUninstallProgress();
            getActivity().finish();
            return;
        }
        ((UninstallerActivity) getActivity()).dispatchAborted();
        getActivity().setResult(1);
        getActivity().finish();
    }

    private boolean isSingleUser(UserManager userManager) {
        int userCount = userManager.getUserCount();
        if (userCount == 1) {
            return true;
        }
        if (!UserManager.isSplitSystemUser() || userCount != 2) {
            return false;
        }
        return true;
    }
}
