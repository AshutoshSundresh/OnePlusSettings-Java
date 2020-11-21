package com.android.settings.bluetooth;

import android.content.Context;
import android.view.View;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.ActionButtonsPreference;

public class BluetoothDetailsButtonsController extends BluetoothDetailsController {
    private ActionButtonsPreference mActionButtons;
    private boolean mConnectButtonInitialized;
    private boolean mIsConnected;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "action_buttons";
    }

    public BluetoothDetailsButtonsController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, CachedBluetoothDevice cachedBluetoothDevice, Lifecycle lifecycle) {
        super(context, preferenceFragmentCompat, cachedBluetoothDevice, lifecycle);
        this.mIsConnected = cachedBluetoothDevice.isConnected();
    }

    private void onForgetButtonPressed() {
        ForgetDeviceDialogFragment.newInstance(this.mCachedDevice.getAddress()).show(this.mFragment.getFragmentManager(), "ForgetBluetoothDevice");
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    public void init(PreferenceScreen preferenceScreen) {
        ActionButtonsPreference actionButtonsPreference = (ActionButtonsPreference) preferenceScreen.findPreference(getPreferenceKey());
        actionButtonsPreference.setButton1Text(C0017R$string.forget);
        actionButtonsPreference.setButton1Icon(C0008R$drawable.ic_settings_delete);
        actionButtonsPreference.setButton1OnClickListener(new View.OnClickListener() {
            /* class com.android.settings.bluetooth.$$Lambda$BluetoothDetailsButtonsController$10mSfoM1rAEvasn6gco1iWQgIA */

            public final void onClick(View view) {
                BluetoothDetailsButtonsController.this.lambda$init$0$BluetoothDetailsButtonsController(view);
            }
        });
        actionButtonsPreference.setButton1Enabled(true);
        this.mActionButtons = actionButtonsPreference;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$init$0 */
    public /* synthetic */ void lambda$init$0$BluetoothDetailsButtonsController(View view) {
        onForgetButtonPressed();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    public void refresh() {
        this.mActionButtons.setButton2Enabled(!this.mCachedDevice.isBusy());
        boolean z = this.mIsConnected;
        boolean isConnected = this.mCachedDevice.isConnected();
        this.mIsConnected = isConnected;
        if (isConnected) {
            if (!this.mConnectButtonInitialized || !z) {
                ActionButtonsPreference actionButtonsPreference = this.mActionButtons;
                actionButtonsPreference.setButton2Text(C0017R$string.bluetooth_device_context_disconnect);
                actionButtonsPreference.setButton2Icon(C0008R$drawable.ic_settings_close);
                actionButtonsPreference.setButton2OnClickListener(new View.OnClickListener() {
                    /* class com.android.settings.bluetooth.$$Lambda$BluetoothDetailsButtonsController$AbsgPn9bfqFfvfi3BgeGPbSW3X0 */

                    public final void onClick(View view) {
                        BluetoothDetailsButtonsController.this.lambda$refresh$1$BluetoothDetailsButtonsController(view);
                    }
                });
                this.mConnectButtonInitialized = true;
            }
        } else if (!this.mConnectButtonInitialized || z) {
            ActionButtonsPreference actionButtonsPreference2 = this.mActionButtons;
            actionButtonsPreference2.setButton2Text(C0017R$string.bluetooth_device_context_connect);
            actionButtonsPreference2.setButton2Icon(C0008R$drawable.ic_add_24dp);
            actionButtonsPreference2.setButton2OnClickListener(new View.OnClickListener() {
                /* class com.android.settings.bluetooth.$$Lambda$BluetoothDetailsButtonsController$eZ36ezumIpXzpP7dOOnqngI5Uk */

                public final void onClick(View view) {
                    BluetoothDetailsButtonsController.this.lambda$refresh$2$BluetoothDetailsButtonsController(view);
                }
            });
            this.mConnectButtonInitialized = true;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$refresh$1 */
    public /* synthetic */ void lambda$refresh$1$BluetoothDetailsButtonsController(View view) {
        this.mCachedDevice.disconnect();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$refresh$2 */
    public /* synthetic */ void lambda$refresh$2$BluetoothDetailsButtonsController(View view) {
        this.mCachedDevice.connect();
    }
}
