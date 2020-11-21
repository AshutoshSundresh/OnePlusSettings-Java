package com.google.android.setupcompat.logging.internal;

import android.annotation.TargetApi;
import android.os.PersistableBundle;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FooterBarMixinMetrics {
    public static final String EXTRA_PRIMARY_BUTTON_VISIBILITY = "PrimaryButtonVisibility";
    public static final String EXTRA_SECONDARY_BUTTON_VISIBILITY = "SecondaryButtonVisibility";
    String primaryButtonVisibility = "Unknown";
    String secondaryButtonVisibility = "Unknown";

    @Retention(RetentionPolicy.SOURCE)
    public @interface FooterButtonVisibility {
    }

    public String getInitialStateVisibility(boolean z, boolean z2) {
        return z ? z2 ? "VisibleUsingXml" : "Visible" : "Invisible";
    }

    public void logPrimaryButtonInitialStateVisibility(boolean z, boolean z2) {
        String str;
        if (this.primaryButtonVisibility.equals("Unknown")) {
            str = getInitialStateVisibility(z, z2);
        } else {
            str = this.primaryButtonVisibility;
        }
        this.primaryButtonVisibility = str;
    }

    public void logSecondaryButtonInitialStateVisibility(boolean z, boolean z2) {
        String str;
        if (this.secondaryButtonVisibility.equals("Unknown")) {
            str = getInitialStateVisibility(z, z2);
        } else {
            str = this.secondaryButtonVisibility;
        }
        this.secondaryButtonVisibility = str;
    }

    public void updateButtonVisibility(boolean z, boolean z2) {
        this.primaryButtonVisibility = updateButtonVisibilityState(this.primaryButtonVisibility, z);
        this.secondaryButtonVisibility = updateButtonVisibilityState(this.secondaryButtonVisibility, z2);
    }

    static String updateButtonVisibilityState(String str, boolean z) {
        if (!"VisibleUsingXml".equals(str) && !"Visible".equals(str) && !"Invisible".equals(str)) {
            throw new IllegalStateException("Illegal visibility state: " + str);
        } else if (z && "Invisible".equals(str)) {
            return "Invisible_to_Visible";
        } else {
            if (z) {
                return str;
            }
            if ("VisibleUsingXml".equals(str)) {
                return "VisibleUsingXml_to_Invisible";
            }
            return "Visible".equals(str) ? "Visible_to_Invisible" : str;
        }
    }

    @TargetApi(29)
    public PersistableBundle getMetrics() {
        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putString(EXTRA_PRIMARY_BUTTON_VISIBILITY, this.primaryButtonVisibility);
        persistableBundle.putString(EXTRA_SECONDARY_BUTTON_VISIBILITY, this.secondaryButtonVisibility);
        return persistableBundle;
    }
}
