package com.android.settings.widget;

import com.android.settingslib.RestrictedLockUtils;

public abstract class SwitchWidgetController {
    protected OnSwitchChangeListener mListener;

    public interface OnSwitchChangeListener {
        boolean onSwitchToggled(boolean z);
    }

    public abstract boolean isChecked();

    public abstract void setChecked(boolean z);

    public abstract void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin);

    public abstract void setEnabled(boolean z);

    public void setupView() {
    }

    public abstract void startListening();

    public abstract void stopListening();

    public void teardownView() {
    }

    public abstract void updateTitle(boolean z);

    public void setListener(OnSwitchChangeListener onSwitchChangeListener) {
        this.mListener = onSwitchChangeListener;
    }
}
