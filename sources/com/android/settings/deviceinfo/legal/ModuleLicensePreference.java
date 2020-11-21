package com.android.settings.deviceinfo.legal;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ModuleInfo;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;

public class ModuleLicensePreference extends Preference {
    private final ModuleInfo mModule;

    public ModuleLicensePreference(Context context, ModuleInfo moduleInfo) {
        super(context);
        this.mModule = moduleInfo;
        setKey(moduleInfo.getPackageName());
        setTitle(moduleInfo.getName());
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onClick() {
        try {
            getContext().startActivity(new Intent("android.intent.action.VIEW").setDataAndType(ModuleLicenseProvider.getUriForPackage(this.mModule.getPackageName()), "text/html").putExtra("android.intent.extra.TITLE", this.mModule.getName()).addFlags(1).addCategory("android.intent.category.DEFAULT").setPackage("com.android.htmlviewer"));
        } catch (ActivityNotFoundException e) {
            Log.e("ModuleLicensePreference", "Failed to find viewer", e);
            showError();
        }
    }

    private void showError() {
        Toast.makeText(getContext(), C0017R$string.settings_license_activity_unavailable, 1).show();
    }
}
