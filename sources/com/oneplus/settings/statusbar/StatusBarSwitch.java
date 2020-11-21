package com.oneplus.settings.statusbar;

import android.app.ActivityManager;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import androidx.preference.SwitchPreference;
import java.util.Set;

public class StatusBarSwitch extends SwitchPreference {
    private Set<String> mBlacklist;
    private Utils mUtils;

    public StatusBarSwitch(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mUtils = new Utils(context);
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        updateUI();
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        super.onDetached();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public boolean persistBoolean(boolean z) {
        updateList();
        Log.i("StatusBarSwitch", "set key:" + getKey() + " value:" + z);
        if (!z) {
            if (this.mBlacklist.contains(getKey())) {
                return true;
            }
            this.mBlacklist.add(getKey());
            setList(this.mBlacklist);
            return true;
        } else if (!this.mBlacklist.remove(getKey())) {
            return true;
        } else {
            setList(this.mBlacklist);
            return true;
        }
    }

    private void setList(Set<String> set) {
        Settings.Secure.putStringForUser(getContext().getContentResolver(), "icon_blacklist", TextUtils.join(",", set), ActivityManager.getCurrentUser());
        Log.i("StatusBarSwitch", " setList blacklist:" + set);
    }

    private void updateUI() {
        updateList();
        setChecked(!this.mBlacklist.contains(getKey()));
        Log.i("StatusBarSwitch", " updateUI blacklist:" + this.mBlacklist);
    }

    private void updateList() {
        this.mBlacklist = Utils.getIconBlacklist(this.mUtils.getValue("icon_blacklist"));
    }
}
