package com.android.settings.wifi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.wifi.AccessPoint;

public class WifiDialog extends AlertDialog implements WifiConfigUiBase, DialogInterface.OnClickListener {
    private final AccessPoint mAccessPoint;
    private WifiConfigController mController;
    private boolean mHideSubmitButton;
    private final WifiDialogListener mListener;
    private final int mMode;
    private View mView;

    public interface WifiDialogListener {
        default void onForget(WifiDialog wifiDialog) {
        }

        default void onScan(WifiDialog wifiDialog, String str) {
        }

        default void onSubmit(WifiDialog wifiDialog) {
        }
    }

    public static WifiDialog createModal(Context context, WifiDialogListener wifiDialogListener, AccessPoint accessPoint, int i) {
        return new WifiDialog(context, wifiDialogListener, accessPoint, i, 0, i == 0);
    }

    public static WifiDialog createModal(Context context, WifiDialogListener wifiDialogListener, AccessPoint accessPoint, int i, int i2) {
        return new WifiDialog(context, wifiDialogListener, accessPoint, i, i2, i == 0);
    }

    WifiDialog(Context context, WifiDialogListener wifiDialogListener, AccessPoint accessPoint, int i, int i2, boolean z) {
        super(context, i2);
        this.mMode = i;
        this.mListener = wifiDialogListener;
        this.mAccessPoint = accessPoint;
        this.mHideSubmitButton = z;
    }

    public WifiConfigController getController() {
        return this.mController;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        View inflate = getLayoutInflater().inflate(C0012R$layout.wifi_dialog, (ViewGroup) null);
        this.mView = inflate;
        setView(inflate);
        this.mController = new WifiConfigController(this, this.mView, this.mAccessPoint, this.mMode);
        super.onCreate(bundle);
        if (this.mHideSubmitButton) {
            this.mController.hideSubmitButton();
        } else {
            this.mController.enableSubmitIfAppropriate();
        }
        if (this.mAccessPoint == null) {
            this.mController.hideForgetButton();
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        View view = this.mView;
        if (view != null) {
            ImageButton imageButton = (ImageButton) view.findViewById(C0010R$id.ssid_scanner_button);
            if (!this.mHideSubmitButton || imageButton == null) {
                $$Lambda$WifiDialog$A0XFUDDETwsfRxrVaOXME4wrgzI r1 = new View.OnClickListener() {
                    /* class com.android.settings.wifi.$$Lambda$WifiDialog$A0XFUDDETwsfRxrVaOXME4wrgzI */

                    public final void onClick(View view) {
                        WifiDialog.this.lambda$onStart$0$WifiDialog(view);
                    }
                };
                if (imageButton != null) {
                    imageButton.setOnClickListener(r1);
                    return;
                }
                return;
            }
            imageButton.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onStart$0 */
    public /* synthetic */ void lambda$onStart$0$WifiDialog(View view) {
        TextView textView;
        if (this.mListener != null && (textView = (TextView) this.mView.findViewById(C0010R$id.ssid)) != null) {
            this.mListener.onScan(this, textView.getText().toString());
        }
    }

    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.mController.updatePassword();
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void dispatchSubmit() {
        WifiDialogListener wifiDialogListener = this.mListener;
        if (wifiDialogListener != null) {
            wifiDialogListener.onSubmit(this);
        }
        dismiss();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        WifiDialogListener wifiDialogListener = this.mListener;
        if (wifiDialogListener == null) {
            return;
        }
        if (i != -3) {
            if (i == -1) {
                wifiDialogListener.onSubmit(this);
            }
        } else if (WifiUtils.isNetworkLockedDown(getContext(), this.mAccessPoint.getConfig())) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getContext(), RestrictedLockUtilsInternal.getDeviceOwner(getContext()));
        } else {
            this.mListener.onForget(this);
        }
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public Button getSubmitButton() {
        return getButton(-1);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public Button getForgetButton() {
        return getButton(-3);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setSubmitButton(CharSequence charSequence) {
        setButton(-1, charSequence, this);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setForgetButton(CharSequence charSequence) {
        setButton(-3, charSequence, this);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setCancelButton(CharSequence charSequence) {
        setButton(-2, charSequence, this);
    }
}
