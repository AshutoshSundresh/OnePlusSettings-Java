package com.android.settings.accounts;

import android.accounts.Account;
import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settingslib.widget.AnimatedImageView;

public class SyncStateSwitchPreference extends SwitchPreference {
    private Account mAccount;
    private String mAuthority;
    private boolean mFailed;
    private boolean mIsActive;
    private boolean mIsPending;
    private boolean mOneTimeSyncMode;
    private String mPackageName;
    private int mUid;

    public SyncStateSwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0, C0018R$style.SyncSwitchPreference);
        this.mIsActive = false;
        this.mIsPending = false;
        this.mFailed = false;
        this.mOneTimeSyncMode = false;
        this.mAccount = null;
        this.mAuthority = null;
        this.mPackageName = null;
        this.mUid = 0;
    }

    public SyncStateSwitchPreference(Context context, Account account, String str, String str2, int i) {
        super(context, null, 0, C0018R$style.SyncSwitchPreference);
        this.mIsActive = false;
        this.mIsPending = false;
        this.mFailed = false;
        this.mOneTimeSyncMode = false;
        setLayoutResource(C0012R$layout.op_sync_switch_preference);
        setup(account, str, str2, i);
    }

    public void setup(Account account, String str, String str2, int i) {
        this.mAccount = account;
        this.mAuthority = str;
        this.mPackageName = str2;
        this.mUid = i;
        setVisible(!TextUtils.isEmpty(str));
        notifyChanged();
    }

    @Override // androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        AnimatedImageView animatedImageView = (AnimatedImageView) preferenceViewHolder.findViewById(C0010R$id.sync_active);
        View findViewById = preferenceViewHolder.findViewById(C0010R$id.sync_failed);
        boolean z = this.mIsActive || this.mIsPending;
        animatedImageView.setVisibility(z ? 0 : 8);
        animatedImageView.setAnimating(this.mIsActive);
        findViewById.setVisibility(this.mFailed && !z ? 0 : 8);
        View findViewById2 = preferenceViewHolder.findViewById(16908352);
        if (this.mOneTimeSyncMode) {
            findViewById2.setVisibility(8);
            ((TextView) preferenceViewHolder.findViewById(16908304)).setText(getContext().getString(C0017R$string.sync_one_time_sync, getSummary()));
            return;
        }
        findViewById2.setVisibility(0);
    }

    public void setActive(boolean z) {
        this.mIsActive = z;
        notifyChanged();
    }

    public void setPending(boolean z) {
        this.mIsPending = z;
        notifyChanged();
    }

    public void setFailed(boolean z) {
        this.mFailed = z;
        notifyChanged();
    }

    public void setOneTimeSyncMode(boolean z) {
        this.mOneTimeSyncMode = z;
        notifyChanged();
    }

    public boolean isOneTimeSyncMode() {
        return this.mOneTimeSyncMode;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.SwitchPreference, androidx.preference.TwoStatePreference, androidx.preference.Preference
    public void onClick() {
        if (this.mOneTimeSyncMode) {
            return;
        }
        if (ActivityManager.isUserAMonkey()) {
            Log.d("SyncState", "ignoring monkey's attempt to flip sync state");
        } else {
            super.onClick();
        }
    }

    public Account getAccount() {
        return this.mAccount;
    }

    public String getAuthority() {
        return this.mAuthority;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public int getUid() {
        return this.mUid;
    }
}
