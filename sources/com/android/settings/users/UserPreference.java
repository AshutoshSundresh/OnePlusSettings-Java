package com.android.settings.users;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.AttributeSet;
import androidx.constraintlayout.widget.R$styleable;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.RestrictedPreference;
import java.util.Comparator;

public class UserPreference extends RestrictedPreference {
    public static final Comparator<UserPreference> SERIAL_NUMBER_COMPARATOR = $$Lambda$UserPreference$UpImioqp9l2DqerpjWaO9lbHRs.INSTANCE;
    private int mSerialNumber;
    private int mUserId;

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference
    public boolean shouldHideSecondTarget() {
        return true;
    }

    static /* synthetic */ int lambda$static$0(UserPreference userPreference, UserPreference userPreference2) {
        if (userPreference == null) {
            return -1;
        }
        if (userPreference2 == null) {
            return 1;
        }
        int serialNumber = userPreference.getSerialNumber();
        int serialNumber2 = userPreference2.getSerialNumber();
        if (serialNumber < serialNumber2) {
            return -1;
        }
        if (serialNumber > serialNumber2) {
            return 1;
        }
        return 0;
    }

    public UserPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, -10);
    }

    UserPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet);
        this.mSerialNumber = -1;
        this.mUserId = -10;
        this.mUserId = i;
        useAdminDisabledSummary(true);
    }

    private void dimIcon(boolean z) {
        Drawable icon = getIcon();
        if (icon != null) {
            icon.mutate().setAlpha(z ? R$styleable.Constraint_layout_goneMarginStart : 255);
            setIcon(icon);
        }
    }

    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        dimIcon(isDisabledByAdmin());
    }

    private int getSerialNumber() {
        if (this.mUserId == UserHandle.myUserId()) {
            return Integer.MIN_VALUE;
        }
        if (this.mSerialNumber < 0) {
            int i = this.mUserId;
            if (i == -10) {
                return Integer.MAX_VALUE;
            }
            if (i == -11) {
                return 2147483646;
            }
            int userSerialNumber = ((UserManager) getContext().getSystemService("user")).getUserSerialNumber(this.mUserId);
            this.mSerialNumber = userSerialNumber;
            if (userSerialNumber < 0) {
                return this.mUserId;
            }
        }
        return this.mSerialNumber;
    }

    public int getUserId() {
        return this.mUserId;
    }
}
