package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.os.Handler;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public abstract class UsbDetailsController extends AbstractPreferenceController implements PreferenceControllerMixin {
    protected final UsbDetailsFragment mFragment;
    Handler mHandler;
    protected final UsbBackend mUsbBackend;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    /* access modifiers changed from: protected */
    public abstract void refresh(boolean z, long j, int i, int i2);

    public UsbDetailsController(Context context, UsbDetailsFragment usbDetailsFragment, UsbBackend usbBackend) {
        super(context);
        this.mFragment = usbDetailsFragment;
        this.mUsbBackend = usbBackend;
        this.mHandler = new Handler(context.getMainLooper());
    }
}
