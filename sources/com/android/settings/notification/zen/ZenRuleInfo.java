package com.android.settings.notification.zen;

import android.content.ComponentName;
import android.net.Uri;

public class ZenRuleInfo {
    public ComponentName configurationActivity;
    public Uri defaultConditionId;
    public String id;
    public boolean isSystem;
    public CharSequence packageLabel;
    public String packageName;
    public int ruleInstanceLimit = -1;
    public ComponentName serviceComponent;
    public String settingsAction;
    public String title;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || ZenRuleInfo.class != obj.getClass()) {
            return false;
        }
        ZenRuleInfo zenRuleInfo = (ZenRuleInfo) obj;
        if (this.isSystem != zenRuleInfo.isSystem || this.ruleInstanceLimit != zenRuleInfo.ruleInstanceLimit) {
            return false;
        }
        String str = this.packageName;
        if (str == null ? zenRuleInfo.packageName != null : !str.equals(zenRuleInfo.packageName)) {
            return false;
        }
        String str2 = this.title;
        if (str2 == null ? zenRuleInfo.title != null : !str2.equals(zenRuleInfo.title)) {
            return false;
        }
        String str3 = this.settingsAction;
        if (str3 == null ? zenRuleInfo.settingsAction != null : !str3.equals(zenRuleInfo.settingsAction)) {
            return false;
        }
        ComponentName componentName = this.configurationActivity;
        if (componentName == null ? zenRuleInfo.configurationActivity != null : !componentName.equals(zenRuleInfo.configurationActivity)) {
            return false;
        }
        Uri uri = this.defaultConditionId;
        if (uri == null ? zenRuleInfo.defaultConditionId != null : !uri.equals(zenRuleInfo.defaultConditionId)) {
            return false;
        }
        ComponentName componentName2 = this.serviceComponent;
        if (componentName2 == null ? zenRuleInfo.serviceComponent != null : !componentName2.equals(zenRuleInfo.serviceComponent)) {
            return false;
        }
        String str4 = this.id;
        if (str4 == null ? zenRuleInfo.id != null : !str4.equals(zenRuleInfo.id)) {
            return false;
        }
        CharSequence charSequence = this.packageLabel;
        if (charSequence != null) {
            return charSequence.equals(zenRuleInfo.packageLabel);
        }
        return zenRuleInfo.packageLabel == null;
    }
}
