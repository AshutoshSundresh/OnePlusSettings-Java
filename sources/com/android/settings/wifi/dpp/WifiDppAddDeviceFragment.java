package com.android.settings.wifi.dpp;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.EasyConnectStatusCallback;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.google.android.setupcompat.template.FooterButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WifiDppAddDeviceFragment extends WifiDppQrCodeBaseFragment {
    private Button mChooseDifferentNetwork;
    private OnClickChooseDifferentNetworkListener mClickChooseDifferentNetworkListener;
    private int mLatestStatusCode = 0;
    private ImageView mWifiApPictureView;

    public interface OnClickChooseDifferentNetworkListener {
        void onClickChooseDifferentNetwork();
    }

    private boolean hasRetryButton(int i) {
        return (i == -3 || i == -1) ? false : true;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1595;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment
    public boolean isFooterAvailable() {
        return true;
    }

    /* access modifiers changed from: private */
    public class EasyConnectConfiguratorStatusCallback extends EasyConnectStatusCallback {
        public void onEnrolleeSuccess(int i) {
        }

        public void onProgress(int i) {
        }

        private EasyConnectConfiguratorStatusCallback() {
        }

        public void onConfiguratorSuccess(int i) {
            WifiDppAddDeviceFragment.this.showSuccessUi(false);
        }

        public void onFailure(int i, String str, SparseArray<int[]> sparseArray, int[] iArr) {
            Log.d("WifiDppAddDeviceFragment", "EasyConnectConfiguratorStatusCallback.onFailure: " + i);
            if (!TextUtils.isEmpty(str)) {
                Log.d("WifiDppAddDeviceFragment", "Tried SSID: " + str);
            }
            if (sparseArray.size() != 0) {
                Log.d("WifiDppAddDeviceFragment", "Tried channels: " + sparseArray);
            }
            if (iArr != null && iArr.length > 0) {
                StringBuilder sb = new StringBuilder("Supported bands: ");
                for (int i2 = 0; i2 < iArr.length; i2++) {
                    sb.append(iArr[i2] + " ");
                }
                Log.d("WifiDppAddDeviceFragment", sb.toString());
            }
            WifiDppAddDeviceFragment wifiDppAddDeviceFragment = WifiDppAddDeviceFragment.this;
            wifiDppAddDeviceFragment.showErrorUi(i, wifiDppAddDeviceFragment.getResultIntent(i, str, sparseArray, iArr), false);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showSuccessUi(boolean z) {
        setHeaderIconImageResource(C0008R$drawable.ic_devices_check_circle_green_32dp);
        setHeaderTitle(C0017R$string.wifi_dpp_wifi_shared_with_device, new Object[0]);
        setProgressBarShown(isEasyConnectHandshaking());
        this.mSummary.setVisibility(4);
        this.mWifiApPictureView.setImageResource(C0008R$drawable.wifi_dpp_success);
        this.mChooseDifferentNetwork.setVisibility(4);
        this.mLeftButton.setText(getContext(), C0017R$string.wifi_dpp_add_another_device);
        this.mLeftButton.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.dpp.$$Lambda$WifiDppAddDeviceFragment$dntOA5CSz7vdUN8fKOi6A_4uTjw */

            public final void onClick(View view) {
                WifiDppAddDeviceFragment.this.lambda$showSuccessUi$0$WifiDppAddDeviceFragment(view);
            }
        });
        this.mRightButton.setText(getContext(), C0017R$string.done);
        this.mRightButton.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.dpp.$$Lambda$WifiDppAddDeviceFragment$0F_t6DDfzPLjkfZOw4l4LdPjU */

            public final void onClick(View view) {
                WifiDppAddDeviceFragment.this.lambda$showSuccessUi$1$WifiDppAddDeviceFragment(view);
            }
        });
        this.mRightButton.setVisibility(0);
        if (!z) {
            this.mLatestStatusCode = 1;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showSuccessUi$0 */
    public /* synthetic */ void lambda$showSuccessUi$0$WifiDppAddDeviceFragment(View view) {
        getFragmentManager().popBackStack();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showSuccessUi$1 */
    public /* synthetic */ void lambda$showSuccessUi$1$WifiDppAddDeviceFragment(View view) {
        FragmentActivity activity = getActivity();
        activity.setResult(-1);
        activity.finish();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private Intent getResultIntent(int i, String str, SparseArray<int[]> sparseArray, int[] iArr) {
        int[] iArr2;
        Intent intent = new Intent();
        intent.putExtra("android.provider.extra.EASY_CONNECT_ERROR_CODE", i);
        if (!TextUtils.isEmpty(str)) {
            intent.putExtra("android.provider.extra.EASY_CONNECT_ATTEMPTED_SSID", str);
        }
        if (!(sparseArray == null || sparseArray.size() == 0)) {
            JSONObject jSONObject = new JSONObject();
            int i2 = 0;
            while (true) {
                try {
                    int keyAt = sparseArray.keyAt(i2);
                    JSONArray jSONArray = new JSONArray();
                    for (int i3 : sparseArray.get(keyAt)) {
                        jSONArray.put(i3);
                    }
                    try {
                        jSONObject.put(Integer.toString(keyAt), jSONArray);
                        i2++;
                    } catch (JSONException unused) {
                        jSONObject = new JSONObject();
                        intent.putExtra("android.provider.extra.EASY_CONNECT_CHANNEL_LIST", jSONObject.toString());
                        intent.putExtra("android.provider.extra.EASY_CONNECT_BAND_LIST", iArr);
                        return intent;
                    }
                } catch (ArrayIndexOutOfBoundsException unused2) {
                    intent.putExtra("android.provider.extra.EASY_CONNECT_CHANNEL_LIST", jSONObject.toString());
                    intent.putExtra("android.provider.extra.EASY_CONNECT_BAND_LIST", iArr);
                    return intent;
                }
            }
        }
        if (!(iArr == null || iArr.length == 0)) {
            intent.putExtra("android.provider.extra.EASY_CONNECT_BAND_LIST", iArr);
        }
        return intent;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showErrorUi(int i, Intent intent, boolean z) {
        CharSequence charSequence;
        int i2 = 0;
        switch (i) {
            case -12:
                charSequence = getText(C0017R$string.wifi_dpp_failure_enrollee_rejected_configuration);
                break;
            case -11:
                charSequence = getText(C0017R$string.wifi_dpp_failure_enrollee_authentication);
                break;
            case -10:
                charSequence = getText(C0017R$string.wifi_dpp_failure_cannot_find_network);
                break;
            case -9:
                throw new IllegalStateException("Wi-Fi DPP configurator used a non-PSK/non-SAEnetwork to handshake");
            case -8:
                charSequence = getString(C0017R$string.wifi_dpp_failure_not_supported, getSsid());
                break;
            case -7:
                charSequence = getText(C0017R$string.wifi_dpp_failure_generic);
                break;
            case -6:
                charSequence = getText(C0017R$string.wifi_dpp_failure_timeout);
                break;
            case -5:
                if (!z) {
                    if (i != this.mLatestStatusCode) {
                        this.mLatestStatusCode = i;
                        ((WifiManager) getContext().getSystemService(WifiManager.class)).stopEasyConnectSession();
                        startWifiDppConfiguratorInitiator();
                        return;
                    }
                    throw new IllegalStateException("Tried restarting EasyConnectSession but stillreceiving EASY_CONNECT_EVENT_FAILURE_BUSY");
                }
                return;
            case -4:
                charSequence = getText(C0017R$string.wifi_dpp_failure_authentication_or_configuration);
                break;
            case -3:
                charSequence = getText(C0017R$string.wifi_dpp_failure_not_compatible);
                break;
            case -2:
                charSequence = getText(C0017R$string.wifi_dpp_failure_authentication_or_configuration);
                break;
            case -1:
                charSequence = getText(C0017R$string.wifi_dpp_qr_code_is_not_valid_format);
                break;
            default:
                throw new IllegalStateException("Unexpected Wi-Fi DPP error");
        }
        setHeaderTitle(C0017R$string.wifi_dpp_could_not_add_device, new Object[0]);
        this.mSummary.setText(charSequence);
        this.mWifiApPictureView.setImageResource(C0008R$drawable.wifi_dpp_error);
        this.mChooseDifferentNetwork.setVisibility(4);
        FooterButton footerButton = this.mLeftButton;
        if (hasRetryButton(i)) {
            this.mRightButton.setText(getContext(), C0017R$string.retry);
        } else {
            this.mRightButton.setText(getContext(), C0017R$string.done);
            footerButton = this.mRightButton;
            this.mLeftButton.setVisibility(4);
        }
        footerButton.setOnClickListener(new View.OnClickListener(intent) {
            /* class com.android.settings.wifi.dpp.$$Lambda$WifiDppAddDeviceFragment$AqS1Hg8cpMDq_7d0SHE1DCf6rk */
            public final /* synthetic */ Intent f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                WifiDppAddDeviceFragment.this.lambda$showErrorUi$2$WifiDppAddDeviceFragment(this.f$1, view);
            }
        });
        if (isEasyConnectHandshaking()) {
            this.mSummary.setText(C0017R$string.wifi_dpp_sharing_wifi_with_this_device);
        }
        setProgressBarShown(isEasyConnectHandshaking());
        FooterButton footerButton2 = this.mRightButton;
        if (isEasyConnectHandshaking()) {
            i2 = 4;
        }
        footerButton2.setVisibility(i2);
        if (!z) {
            this.mLatestStatusCode = i;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showErrorUi$2 */
    public /* synthetic */ void lambda$showErrorUi$2$WifiDppAddDeviceFragment(Intent intent, View view) {
        getActivity().setResult(0, intent);
        getActivity().finish();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mLatestStatusCode = bundle.getInt("key_latest_status_code");
        }
        WifiDppInitiatorViewModel wifiDppInitiatorViewModel = (WifiDppInitiatorViewModel) ViewModelProviders.of(this).get(WifiDppInitiatorViewModel.class);
        wifiDppInitiatorViewModel.getStatusCode().observe(this, new Observer(wifiDppInitiatorViewModel) {
            /* class com.android.settings.wifi.dpp.$$Lambda$WifiDppAddDeviceFragment$Xi7v8lbNG6AHnwMwvxrIKuLQHYA */
            public final /* synthetic */ WifiDppInitiatorViewModel f$1;

            {
                this.f$1 = r2;
            }

            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                WifiDppAddDeviceFragment.this.lambda$onCreate$3$WifiDppAddDeviceFragment(this.f$1, (Integer) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$3 */
    public /* synthetic */ void lambda$onCreate$3$WifiDppAddDeviceFragment(WifiDppInitiatorViewModel wifiDppInitiatorViewModel, Integer num) {
        if (!wifiDppInitiatorViewModel.isWifiDppHandshaking()) {
            int intValue = num.intValue();
            if (intValue == 1) {
                new EasyConnectConfiguratorStatusCallback().onConfiguratorSuccess(intValue);
            } else {
                new EasyConnectConfiguratorStatusCallback().onFailure(intValue, wifiDppInitiatorViewModel.getTriedSsid(), wifiDppInitiatorViewModel.getTriedChannels(), wifiDppInitiatorViewModel.getBandArray());
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(C0012R$layout.wifi_dpp_add_device_fragment, viewGroup, false);
    }

    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        setHeaderIconImageResource(C0008R$drawable.ic_devices_other_32dp);
        String information = ((WifiDppConfiguratorActivity) getActivity()).getWifiDppQrCode().getInformation();
        int i = 0;
        if (TextUtils.isEmpty(information)) {
            setHeaderTitle(C0017R$string.wifi_dpp_device_found, new Object[0]);
        } else {
            setHeaderTitle(information);
        }
        updateSummary();
        this.mWifiApPictureView = (ImageView) view.findViewById(C0010R$id.wifi_ap_picture_view);
        Button button = (Button) view.findViewById(C0010R$id.choose_different_network);
        this.mChooseDifferentNetwork = button;
        button.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.dpp.$$Lambda$WifiDppAddDeviceFragment$gzpbkmWTjT17nBVoNoD9odnQ1ks */

            public final void onClick(View view) {
                WifiDppAddDeviceFragment.this.lambda$onViewCreated$4$WifiDppAddDeviceFragment(view);
            }
        });
        this.mLeftButton.setText(getContext(), C0017R$string.cancel);
        this.mLeftButton.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.dpp.$$Lambda$WifiDppAddDeviceFragment$Pdookd_X5dhJOm7Vf3Fqshw60Q */

            public final void onClick(View view) {
                WifiDppAddDeviceFragment.this.lambda$onViewCreated$5$WifiDppAddDeviceFragment(view);
            }
        });
        this.mRightButton.setText(getContext(), C0017R$string.wifi_dpp_share_wifi);
        this.mRightButton.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.dpp.$$Lambda$WifiDppAddDeviceFragment$co6MlaQuNMuge8n_bCbWqRO3VEQ */

            public final void onClick(View view) {
                WifiDppAddDeviceFragment.this.lambda$onViewCreated$6$WifiDppAddDeviceFragment(view);
            }
        });
        if (bundle != null) {
            int i2 = this.mLatestStatusCode;
            if (i2 == 1) {
                showSuccessUi(true);
            } else if (i2 == 0) {
                setProgressBarShown(isEasyConnectHandshaking());
                FooterButton footerButton = this.mRightButton;
                if (isEasyConnectHandshaking()) {
                    i = 4;
                }
                footerButton.setVisibility(i);
            } else {
                showErrorUi(i2, null, true);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onViewCreated$4 */
    public /* synthetic */ void lambda$onViewCreated$4$WifiDppAddDeviceFragment(View view) {
        this.mClickChooseDifferentNetworkListener.onClickChooseDifferentNetwork();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onViewCreated$5 */
    public /* synthetic */ void lambda$onViewCreated$5$WifiDppAddDeviceFragment(View view) {
        getActivity().finish();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onViewCreated$6 */
    public /* synthetic */ void lambda$onViewCreated$6$WifiDppAddDeviceFragment(View view) {
        setProgressBarShown(true);
        this.mRightButton.setVisibility(4);
        startWifiDppConfiguratorInitiator();
        updateSummary();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("key_latest_status_code", this.mLatestStatusCode);
        super.onSaveInstanceState(bundle);
    }

    private String getSsid() {
        WifiNetworkConfig wifiNetworkConfig = ((WifiDppConfiguratorActivity) getActivity()).getWifiNetworkConfig();
        if (WifiNetworkConfig.isValidConfig(wifiNetworkConfig)) {
            return wifiNetworkConfig.getSsid();
        }
        throw new IllegalStateException("Invalid Wi-Fi network for configuring");
    }

    private void startWifiDppConfiguratorInitiator() {
        ((WifiDppInitiatorViewModel) ViewModelProviders.of(this).get(WifiDppInitiatorViewModel.class)).startEasyConnectAsConfiguratorInitiator(((WifiDppConfiguratorActivity) getActivity()).getWifiDppQrCode().getQrCode(), ((WifiDppConfiguratorActivity) getActivity()).getWifiNetworkConfig().getNetworkId());
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mClickChooseDifferentNetworkListener = (OnClickChooseDifferentNetworkListener) context;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        this.mClickChooseDifferentNetworkListener = null;
        super.onDetach();
    }

    private boolean isEasyConnectHandshaking() {
        return ((WifiDppInitiatorViewModel) ViewModelProviders.of(this).get(WifiDppInitiatorViewModel.class)).isWifiDppHandshaking();
    }

    private void updateSummary() {
        if (isEasyConnectHandshaking()) {
            this.mSummary.setText(C0017R$string.wifi_dpp_sharing_wifi_with_this_device);
            return;
        }
        this.mSummary.setText(getString(C0017R$string.wifi_dpp_add_device_to_wifi, getSsid()));
    }
}
