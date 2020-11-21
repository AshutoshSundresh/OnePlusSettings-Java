package com.oneplus.settings.defaultapp.view;

import com.oneplus.settings.defaultapp.DefaultAppUtils;

public class DefaultMusicPicker extends DefaultBasePicker {
    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.defaultapp.view.DefaultBasePicker
    public String getType() {
        return DefaultAppUtils.getKeyTypeString(2);
    }
}
