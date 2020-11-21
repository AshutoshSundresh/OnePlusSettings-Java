package com.oneplus.settings.packageuninstaller;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.oneplus.settings.packageuninstaller.UninstallerActivity;

public class UninstallAlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    public Dialog onCreateDialog(Bundle bundle) {
        PackageManager packageManager = getActivity().getPackageManager();
        UninstallerActivity.DialogInfo dialogInfo = ((UninstallerActivity) getActivity()).getDialogInfo();
        CharSequence loadSafeLabel = dialogInfo.appInfo.loadSafeLabel(packageManager);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
            sb.append(getString(C0017R$string.oneplus_uninstatll_multi_app_msg));
        } else if (!dialogInfo.user.equals(Process.myUserHandle())) {
            UserInfo userInfo = userManager.getUserInfo(dialogInfo.user.getIdentifier());
            UserHandle userHandle = dialogInfo.user;
            if (UserHandle.getUserId(dialogInfo.appInfo.uid) == 999) {
                sb.append(getString(C0017R$string.oneplus_uninstatll_multi_app_msg));
                loadSafeLabel = getString(C0017R$string.oneplus_uninstatll_multi_app_title, loadSafeLabel);
            } else {
                sb.append(getString(C0017R$string.uninstall_application_text_user, userInfo.name));
            }
        } else {
            UserHandle userHandle2 = dialogInfo.user;
            if (UserHandle.getUserId(dialogInfo.appInfo.uid) == 999) {
                sb.append(getString(C0017R$string.oneplus_uninstatll_multi_app_msg));
                loadSafeLabel = getString(C0017R$string.oneplus_uninstatll_multi_app_title, loadSafeLabel);
            } else {
                sb.append(getString(C0017R$string.oneplus_uninstatll_multi_main_app_msg));
            }
        }
        builder.setTitle(loadSafeLabel);
        builder.setPositiveButton(17039370, this);
        builder.setNegativeButton(17039360, this);
        builder.setMessage(sb.toString());
        return builder.create();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            ((UninstallerActivity) getActivity()).startUninstallProgress();
        } else {
            ((UninstallerActivity) getActivity()).dispatchAborted();
        }
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        if (isAdded()) {
            getActivity().finish();
        }
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
