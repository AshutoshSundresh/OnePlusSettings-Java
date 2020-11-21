package com.oneplus.settings.statusbar;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import com.android.settings.RestrictedListPreference;

public class ClockPreference extends RestrictedListPreference {
    private ArraySet<String> mBlacklist;
    private final String mClock;
    private boolean mClockEnabled;
    private boolean mHasSeconds;
    private boolean mHasSetValue;
    private Utils mUtils;

    public ClockPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mUtils = new Utils(context);
        this.mClock = context.getString(17041307);
        setEntryValues(new CharSequence[]{"seconds", "default", "disabled"});
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

    private void updateUI() {
        updateStatus();
        if (!this.mHasSetValue) {
            this.mHasSetValue = true;
            if (this.mClockEnabled && this.mHasSeconds) {
                setValue("seconds");
            } else if (this.mClockEnabled) {
                setValue("default");
            } else {
                setValue("disabled");
            }
        }
    }

    private void updateStatus() {
        ArraySet<String> iconBlacklist = Utils.getIconBlacklist(this.mUtils.getValue("icon_blacklist"));
        this.mBlacklist = iconBlacklist;
        boolean z = true;
        this.mClockEnabled = !iconBlacklist.contains(this.mClock);
        if (this.mUtils.getValue("clock_seconds", 0) == 0) {
            z = false;
        }
        this.mHasSeconds = z;
        Log.i("ClockPreference", "updateStatus mBlacklist:" + this.mBlacklist + " TextUtils.join:" + TextUtils.join(",", this.mBlacklist));
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public boolean persistString(String str) {
        if (this.mUtils == null) {
            return true;
        }
        updateStatus();
        this.mUtils.setValue("clock_seconds", "seconds".equals(str) ? 1 : 0);
        if ("disabled".equals(str)) {
            this.mBlacklist.add(this.mClock);
        } else {
            this.mBlacklist.remove(this.mClock);
        }
        Log.i("ClockPreference", "update value:" + str + " mBlacklist:" + TextUtils.join(",", this.mBlacklist));
        this.mUtils.setValue("icon_blacklist", TextUtils.join(",", this.mBlacklist));
        return true;
    }
}
