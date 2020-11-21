package com.android.settings.wifi.dpp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.wifi.dpp.WifiNetworkConfig;
import com.android.settings.wifi.qrcode.QrCodeGenerator;
import com.google.zxing.WriterException;

public class WifiDppQrCodeGeneratorFragment extends WifiDppQrCodeBaseFragment {
    private String mQrCode;
    private ImageView mQrCodeView;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1595;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment
    public boolean isFooterAvailable() {
        return false;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (getWifiNetworkConfigFromHostActivity().isHotspot()) {
            getActivity().setTitle(C0017R$string.wifi_dpp_share_hotspot);
        } else {
            getActivity().setTitle(C0017R$string.wifi_dpp_share_wifi);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem findItem = menu.findItem(1);
        if (findItem != null) {
            findItem.setShowAsAction(0);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // androidx.fragment.app.Fragment
    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(C0012R$layout.wifi_dpp_qrcode_generator_fragment, viewGroup, false);
    }

    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mQrCodeView = (ImageView) view.findViewById(C0010R$id.qrcode_view);
        WifiNetworkConfig wifiNetworkConfigFromHostActivity = getWifiNetworkConfigFromHostActivity();
        if (wifiNetworkConfigFromHostActivity.isHotspot()) {
            setHeaderTitle(C0017R$string.wifi_dpp_share_hotspot, new Object[0]);
        } else {
            setHeaderTitle(C0017R$string.wifi_dpp_share_wifi, new Object[0]);
        }
        String preSharedKey = wifiNetworkConfigFromHostActivity.getPreSharedKey();
        TextView textView = (TextView) view.findViewById(C0010R$id.password);
        if (TextUtils.isEmpty(preSharedKey)) {
            this.mSummary.setText(getString(C0017R$string.wifi_dpp_scan_open_network_qr_code_with_another_device, wifiNetworkConfigFromHostActivity.getSsid()));
            textView.setVisibility(8);
        } else {
            this.mSummary.setText(getString(C0017R$string.wifi_dpp_scan_qr_code_with_another_device, wifiNetworkConfigFromHostActivity.getSsid()));
            if (wifiNetworkConfigFromHostActivity.isHotspot()) {
                textView.setText(getString(C0017R$string.wifi_dpp_hotspot_password, preSharedKey));
            } else {
                textView.setText(getString(C0017R$string.wifi_dpp_wifi_password, preSharedKey));
            }
        }
        this.mQrCode = wifiNetworkConfigFromHostActivity.getQrCode();
        setQrCode();
    }

    private void setQrCode() {
        try {
            this.mQrCodeView.setImageBitmap(QrCodeGenerator.encodeQrCode(this.mQrCode, getContext().getResources().getDimensionPixelSize(C0007R$dimen.qrcode_size)));
        } catch (WriterException e) {
            Log.e("WifiDppQrCodeGeneratorFragment", "Error generating QR code bitmap " + e);
        }
    }

    private WifiNetworkConfig getWifiNetworkConfigFromHostActivity() {
        WifiNetworkConfig wifiNetworkConfig = ((WifiNetworkConfig.Retriever) getActivity()).getWifiNetworkConfig();
        if (WifiNetworkConfig.isValidConfig(wifiNetworkConfig)) {
            return wifiNetworkConfig;
        }
        throw new IllegalStateException("Invalid Wi-Fi network for configuring");
    }
}
