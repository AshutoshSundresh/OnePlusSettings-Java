package com.android.settings.backup;

import android.content.Intent;

public class PrivacySettingsConfigData {
    private static PrivacySettingsConfigData sInstance;
    private boolean mBackupEnabled = false;
    private boolean mBackupGray = false;
    private Intent mConfigIntent = null;
    private String mConfigSummary = null;
    private Intent mManageIntent = null;
    private CharSequence mManageLabel = null;

    private PrivacySettingsConfigData() {
    }

    public static PrivacySettingsConfigData getInstance() {
        if (sInstance == null) {
            sInstance = new PrivacySettingsConfigData();
        }
        return sInstance;
    }

    public boolean isBackupEnabled() {
        return this.mBackupEnabled;
    }

    public void setBackupEnabled(boolean z) {
        this.mBackupEnabled = z;
    }

    public boolean isBackupGray() {
        return this.mBackupGray;
    }

    public void setBackupGray(boolean z) {
        this.mBackupGray = z;
    }

    public Intent getConfigIntent() {
        return this.mConfigIntent;
    }

    public void setConfigIntent(Intent intent) {
        this.mConfigIntent = intent;
    }

    public String getConfigSummary() {
        return this.mConfigSummary;
    }

    public void setConfigSummary(String str) {
        this.mConfigSummary = str;
    }

    public Intent getManageIntent() {
        return this.mManageIntent;
    }

    public void setManageIntent(Intent intent) {
        this.mManageIntent = intent;
    }

    public CharSequence getManageLabel() {
        return this.mManageLabel;
    }

    public void setManageLabel(CharSequence charSequence) {
        this.mManageLabel = charSequence;
    }
}
