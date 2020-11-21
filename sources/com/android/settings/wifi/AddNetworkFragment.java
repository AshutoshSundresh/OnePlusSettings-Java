package com.android.settings.wifi;

import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.wifi.dpp.WifiDppUtils;

public class AddNetworkFragment extends InstrumentedFragment implements WifiConfigUiBase, View.OnClickListener {
    static final int CANCEL_BUTTON_ID = 16908314;
    static final int SSID_SCANNER_BUTTON_ID = C0010R$id.ssid_scanner_button;
    static final int SUBMIT_BUTTON_ID = 16908313;
    private Button mCancelBtn;
    private Button mSubmitBtn;
    private WifiConfigController mUIController;

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public Button getForgetButton() {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1556;
    }

    public int getMode() {
        return 1;
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setForgetButton(CharSequence charSequence) {
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0012R$layout.wifi_add_network_view, viewGroup, false);
        Button button = (Button) inflate.findViewById(16908315);
        if (button != null) {
            button.setVisibility(8);
        }
        this.mSubmitBtn = (Button) inflate.findViewById(SUBMIT_BUTTON_ID);
        this.mCancelBtn = (Button) inflate.findViewById(CANCEL_BUTTON_ID);
        this.mSubmitBtn.setOnClickListener(this);
        this.mCancelBtn.setOnClickListener(this);
        ((ImageButton) inflate.findViewById(SSID_SCANNER_BUTTON_ID)).setOnClickListener(this);
        this.mUIController = new WifiConfigController(this, inflate, null, getMode());
        return inflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewStateRestored(Bundle bundle) {
        super.onViewStateRestored(bundle);
        this.mUIController.updatePassword();
    }

    public void onClick(View view) {
        if (view.getId() == SUBMIT_BUTTON_ID) {
            handleSubmitAction();
        } else if (view.getId() == CANCEL_BUTTON_ID) {
            handleCancelAction();
        } else if (view.getId() == SSID_SCANNER_BUTTON_ID) {
            startActivityForResult(WifiDppUtils.getEnrolleeQrCodeScannerIntent(((TextView) getView().findViewById(C0010R$id.ssid)).getText().toString()), 0);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 0 && i2 == -1) {
            successfullyFinish((WifiConfiguration) intent.getParcelableExtra("wifi_configuration"));
        }
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void dispatchSubmit() {
        handleSubmitAction();
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setTitle(int i) {
        getActivity().setTitle(i);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setTitle(CharSequence charSequence) {
        getActivity().setTitle(charSequence);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setSubmitButton(CharSequence charSequence) {
        this.mSubmitBtn.setText(charSequence);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setCancelButton(CharSequence charSequence) {
        this.mCancelBtn.setText(charSequence);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public Button getSubmitButton() {
        return this.mSubmitBtn;
    }

    /* access modifiers changed from: package-private */
    public void handleSubmitAction() {
        successfullyFinish(this.mUIController.getConfig());
    }

    private void successfullyFinish(WifiConfiguration wifiConfiguration) {
        Intent intent = new Intent();
        FragmentActivity activity = getActivity();
        intent.putExtra("wifi_config_key", wifiConfiguration);
        activity.setResult(-1, intent);
        activity.finish();
    }

    /* access modifiers changed from: package-private */
    public void handleCancelAction() {
        FragmentActivity activity = getActivity();
        activity.setResult(0);
        activity.finish();
    }
}
