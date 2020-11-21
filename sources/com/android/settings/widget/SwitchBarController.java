package com.android.settings.widget;

import android.widget.Switch;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.RestrictedLockUtils;

public class SwitchBarController extends SwitchWidgetController implements SwitchBar.OnSwitchChangeListener {
    private final SwitchBar mSwitchBar;

    public SwitchBarController(SwitchBar switchBar) {
        this.mSwitchBar = switchBar;
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setupView() {
        this.mSwitchBar.show();
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void teardownView() {
        this.mSwitchBar.hide();
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void updateTitle(boolean z) {
        this.mSwitchBar.setTextViewLabelAndBackground(z);
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void startListening() {
        this.mSwitchBar.addOnSwitchChangeListener(this);
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void stopListening() {
        this.mSwitchBar.removeOnSwitchChangeListener(this);
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setChecked(boolean z) {
        this.mSwitchBar.setChecked(z);
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public boolean isChecked() {
        return this.mSwitchBar.isChecked();
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setEnabled(boolean z) {
        this.mSwitchBar.setEnabled(z);
    }

    @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        SwitchWidgetController.OnSwitchChangeListener onSwitchChangeListener = this.mListener;
        if (onSwitchChangeListener != null) {
            onSwitchChangeListener.onSwitchToggled(z);
        }
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mSwitchBar.setDisabledByAdmin(enforcedAdmin);
    }
}
