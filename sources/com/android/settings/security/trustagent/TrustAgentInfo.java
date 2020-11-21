package com.android.settings.security.trustagent;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

public class TrustAgentInfo implements Comparable<TrustAgentInfo> {
    private final ComponentName mComponentName;
    private final Drawable mIcon;
    private final CharSequence mLabel;

    public TrustAgentInfo(CharSequence charSequence, ComponentName componentName, Drawable drawable) {
        this.mLabel = charSequence;
        this.mComponentName = componentName;
        this.mIcon = drawable;
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public boolean equals(Object obj) {
        if (obj instanceof TrustAgentInfo) {
            return this.mComponentName.equals(((TrustAgentInfo) obj).getComponentName());
        }
        return false;
    }

    public int compareTo(TrustAgentInfo trustAgentInfo) {
        return this.mComponentName.compareTo(trustAgentInfo.getComponentName());
    }
}
