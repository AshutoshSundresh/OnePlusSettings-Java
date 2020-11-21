package com.android.settings.wifi.dpp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.net.wifi.EasyConnectStatusCallback;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.wifi.dpp.WifiNetworkConfig;
import com.android.settings.wifi.qrcode.QrCamera;
import com.android.settings.wifi.qrcode.QrDecorateView;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.WifiTracker;
import com.android.settingslib.wifi.WifiTrackerFactory;
import java.util.List;

public class WifiDppQrCodeScannerFragment extends WifiDppQrCodeBaseFragment implements TextureView.SurfaceTextureListener, QrCamera.ScannerCallback, WifiManager.ActionListener, WifiTracker.WifiListener {
    private QrCamera mCamera;
    private QrDecorateView mDecorateView;
    private WifiConfiguration mEnrolleeWifiConfiguration;
    private TextView mErrorMessage;
    private final Handler mHandler = new Handler() {
        /* class com.android.settings.wifi.dpp.WifiDppQrCodeScannerFragment.AnonymousClass1 */

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                WifiDppQrCodeScannerFragment.this.mErrorMessage.setVisibility(8);
            } else if (i == 2) {
                WifiDppQrCodeScannerFragment.this.mErrorMessage.setVisibility(0);
                WifiDppQrCodeScannerFragment.this.mErrorMessage.setText((String) message.obj);
                WifiDppQrCodeScannerFragment.this.mErrorMessage.sendAccessibilityEvent(32);
                removeMessages(1);
                sendEmptyMessageDelayed(1, 10000);
                if (message.arg1 == 1) {
                    WifiDppQrCodeScannerFragment wifiDppQrCodeScannerFragment = WifiDppQrCodeScannerFragment.this;
                    if (wifiDppQrCodeScannerFragment.mIsInSetupWizard) {
                        wifiDppQrCodeScannerFragment.setProgressBarShown(false);
                    } else {
                        wifiDppQrCodeScannerFragment.mProgressBar.setVisibility(4);
                    }
                    WifiDppQrCodeScannerFragment.this.mDecorateView.setFocused(false);
                    WifiDppQrCodeScannerFragment.this.restartCamera();
                }
            } else if (i != 3) {
                if (i == 4) {
                    WifiManager wifiManager = (WifiManager) WifiDppQrCodeScannerFragment.this.getContext().getSystemService(WifiManager.class);
                    boolean z = false;
                    for (WifiConfiguration wifiConfiguration : ((WifiNetworkConfig) message.obj).getWifiConfigurations()) {
                        int addNetwork = wifiManager.addNetwork(wifiConfiguration);
                        if (addNetwork != -1) {
                            wifiManager.enableNetwork(addNetwork, false);
                            if (wifiConfiguration.hiddenSSID || WifiDppQrCodeScannerFragment.this.isReachableWifiNetwork(wifiConfiguration)) {
                                WifiDppQrCodeScannerFragment.this.mEnrolleeWifiConfiguration = wifiConfiguration;
                                wifiManager.connect(addNetwork, WifiDppQrCodeScannerFragment.this);
                                z = true;
                            }
                        }
                    }
                    if (!z) {
                        Log.d("WifiDppQrCodeScanner", "can't find network from scan ");
                        WifiDppQrCodeScannerFragment.this.showErrorMessageAndRestartCamera(C0017R$string.wifi_dpp_check_connection_try_again);
                        return;
                    }
                    ((InstrumentedFragment) WifiDppQrCodeScannerFragment.this).mMetricsFeatureProvider.action(((InstrumentedFragment) WifiDppQrCodeScannerFragment.this).mMetricsFeatureProvider.getAttribution(WifiDppQrCodeScannerFragment.this.getActivity()), 1711, 1596, null, Integer.MIN_VALUE);
                    WifiDppQrCodeScannerFragment.this.notifyUserForQrCodeRecognition();
                }
            } else if (WifiDppQrCodeScannerFragment.this.mScanWifiDppSuccessListener != null) {
                WifiDppQrCodeScannerFragment.this.mScanWifiDppSuccessListener.onScanWifiDppSuccess((WifiQrCode) message.obj);
                if (!WifiDppQrCodeScannerFragment.this.mIsConfiguratorMode) {
                    WifiDppQrCodeScannerFragment wifiDppQrCodeScannerFragment2 = WifiDppQrCodeScannerFragment.this;
                    if (wifiDppQrCodeScannerFragment2.mIsInSetupWizard) {
                        wifiDppQrCodeScannerFragment2.setProgressBarShown(true);
                    } else {
                        wifiDppQrCodeScannerFragment2.mProgressBar.setVisibility(0);
                    }
                    WifiDppQrCodeScannerFragment.this.startWifiDppEnrolleeInitiator((WifiQrCode) message.obj);
                    WifiDppQrCodeScannerFragment.this.updateEnrolleeSummary();
                    WifiDppQrCodeScannerFragment.this.mSummary.sendAccessibilityEvent(32);
                }
                WifiDppQrCodeScannerFragment.this.notifyUserForQrCodeRecognition();
            }
        }
    };
    private boolean mIsConfiguratorMode = true;
    private int mLatestStatusCode = 0;
    private ProgressBar mProgressBar;
    private OnScanWifiDppSuccessListener mScanWifiDppSuccessListener;
    private String mSsid;
    private TextureView mTextureView;
    private WifiQrCode mWifiQrCode;
    private WifiTracker mWifiTracker;

    public interface OnScanWifiDppSuccessListener {
        void onScanWifiDppSuccess(WifiQrCode wifiQrCode);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment
    public boolean isFooterAvailable() {
        return false;
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onAccessPointsChanged() {
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onConnectedChanged() {
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onWifiStateChanged(int i) {
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void notifyUserForQrCodeRecognition() {
        QrCamera qrCamera = this.mCamera;
        if (qrCamera != null) {
            qrCamera.stop();
        }
        this.mDecorateView.setFocused(true);
        this.mErrorMessage.setVisibility(8);
        WifiDppUtils.triggerVibrationForQrCodeRecognition(getContext());
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isReachableWifiNetwork(WifiConfiguration wifiConfiguration) {
        List<AccessPoint> accessPoints = this.mWifiTracker.getAccessPoints();
        if (wifiConfiguration.hiddenSSID) {
            return true;
        }
        for (AccessPoint accessPoint : accessPoints) {
            if (accessPoint.matches(wifiConfiguration) && accessPoint.isReachable()) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setRequestedOrientation(1);
        if (bundle != null) {
            this.mIsConfiguratorMode = bundle.getBoolean("key_is_configurator_mode");
            this.mLatestStatusCode = bundle.getInt("key_latest_error_code");
            this.mEnrolleeWifiConfiguration = (WifiConfiguration) bundle.getParcelable("key_wifi_configuration");
        }
        WifiDppInitiatorViewModel wifiDppInitiatorViewModel = (WifiDppInitiatorViewModel) ViewModelProviders.of(this).get(WifiDppInitiatorViewModel.class);
        wifiDppInitiatorViewModel.getEnrolleeSuccessNetworkId().observe(this, new Observer(wifiDppInitiatorViewModel) {
            /* class com.android.settings.wifi.dpp.$$Lambda$WifiDppQrCodeScannerFragment$pDAVP_ZnST79iW0FN3k5nED9adM */
            public final /* synthetic */ WifiDppInitiatorViewModel f$1;

            {
                this.f$1 = r2;
            }

            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                WifiDppQrCodeScannerFragment.this.lambda$onCreate$0$WifiDppQrCodeScannerFragment(this.f$1, (Integer) obj);
            }
        });
        wifiDppInitiatorViewModel.getStatusCode().observe(this, new Observer(wifiDppInitiatorViewModel) {
            /* class com.android.settings.wifi.dpp.$$Lambda$WifiDppQrCodeScannerFragment$MYF3pfAm1GnbeVdLhZBtGr1d_fM */
            public final /* synthetic */ WifiDppInitiatorViewModel f$1;

            {
                this.f$1 = r2;
            }

            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                WifiDppQrCodeScannerFragment.this.lambda$onCreate$1$WifiDppQrCodeScannerFragment(this.f$1, (Integer) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ void lambda$onCreate$0$WifiDppQrCodeScannerFragment(WifiDppInitiatorViewModel wifiDppInitiatorViewModel, Integer num) {
        if (!wifiDppInitiatorViewModel.isWifiDppHandshaking()) {
            new EasyConnectEnrolleeStatusCallback().onEnrolleeSuccess(num.intValue());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$1 */
    public /* synthetic */ void lambda$onCreate$1$WifiDppQrCodeScannerFragment(WifiDppInitiatorViewModel wifiDppInitiatorViewModel, Integer num) {
        if (!wifiDppInitiatorViewModel.isWifiDppHandshaking()) {
            int intValue = num.intValue();
            Log.d("WifiDppQrCodeScanner", "Easy connect enrollee callback onFailure " + intValue);
            new EasyConnectEnrolleeStatusCallback().onFailure(intValue);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onPause() {
        QrCamera qrCamera = this.mCamera;
        if (qrCamera != null) {
            qrCamera.stop();
        }
        super.onPause();
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (!isWifiDppHandshaking()) {
            restartCamera();
        }
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return this.mIsConfiguratorMode ? 1595 : 1596;
    }

    public WifiDppQrCodeScannerFragment() {
    }

    WifiDppQrCodeScannerFragment(String str) {
        this.mSsid = str;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mWifiTracker = WifiTrackerFactory.create(getActivity(), this, getSettingsLifecycle(), false, true);
        if (this.mIsConfiguratorMode) {
            getActivity().setTitle(C0017R$string.wifi_dpp_add_device_to_network);
        } else {
            getActivity().setTitle(C0017R$string.wifi_dpp_scan_qr_code);
        }
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mScanWifiDppSuccessListener = (OnScanWifiDppSuccessListener) context;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        this.mScanWifiDppSuccessListener = null;
        super.onDetach();
    }

    @Override // androidx.fragment.app.Fragment
    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (this.mIsInSetupWizard) {
            return layoutInflater.inflate(C0012R$layout.op_setup_wifi_dpp_qrcode_scanner_fragment, viewGroup, false);
        }
        return layoutInflater.inflate(C0012R$layout.op_wifi_dpp_qrcode_scanner_fragment, viewGroup, false);
    }

    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        TextureView textureView = (TextureView) view.findViewById(C0010R$id.preview_view);
        this.mTextureView = textureView;
        textureView.setSurfaceTextureListener(this);
        this.mDecorateView = (QrDecorateView) view.findViewById(C0010R$id.decorate_view);
        if (this.mIsInSetupWizard) {
            setProgressBarShown(isWifiDppHandshaking());
        } else {
            ProgressBar progressBar = (ProgressBar) view.findViewById(C0010R$id.indeterminate_bar);
            this.mProgressBar = progressBar;
            progressBar.setVisibility(isWifiDppHandshaking() ? 0 : 4);
        }
        if (this.mIsConfiguratorMode) {
            setHeaderTitle(C0017R$string.wifi_dpp_add_device_to_network, new Object[0]);
            WifiNetworkConfig wifiNetworkConfig = ((WifiNetworkConfig.Retriever) getActivity()).getWifiNetworkConfig();
            if (WifiNetworkConfig.isValidConfig(wifiNetworkConfig)) {
                this.mSummary.setText(getString(C0017R$string.wifi_dpp_center_qr_code, wifiNetworkConfig.getSsid()));
            } else {
                throw new IllegalStateException("Invalid Wi-Fi network for configuring");
            }
        } else {
            setHeaderTitle(C0017R$string.wifi_dpp_scan_qr_code, new Object[0]);
            updateEnrolleeSummary();
        }
        this.mErrorMessage = (TextView) view.findViewById(C0010R$id.error_message);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.removeItem(1);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        initCamera(surfaceTexture);
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        destroyCamera();
        return true;
    }

    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    public Size getViewSize() {
        return new Size(this.mTextureView.getWidth(), this.mTextureView.getHeight());
    }

    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    public Rect getFramePosition(Size size, int i) {
        return new Rect(0, 0, size.getWidth(), size.getHeight());
    }

    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    public void setTransform(Matrix matrix) {
        this.mTextureView.setTransform(matrix);
    }

    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    public boolean isValid(String str) {
        try {
            WifiQrCode wifiQrCode = new WifiQrCode(str);
            this.mWifiQrCode = wifiQrCode;
            String scheme = wifiQrCode.getScheme();
            if (!this.mIsConfiguratorMode || !"WIFI".equals(scheme)) {
                return true;
            }
            showErrorMessage(C0017R$string.wifi_dpp_qr_code_is_not_valid_format);
            return false;
        } catch (IllegalArgumentException unused) {
            showErrorMessage(C0017R$string.wifi_dpp_qr_code_is_not_valid_format);
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x002d  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0034  */
    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleSuccessfulResult(java.lang.String r4) {
        /*
            r3 = this;
            com.android.settings.wifi.dpp.WifiQrCode r4 = r3.mWifiQrCode
            java.lang.String r4 = r4.getScheme()
            int r0 = r4.hashCode()
            r1 = 67908(0x10944, float:9.516E-41)
            r2 = 1
            if (r0 == r1) goto L_0x0020
            r1 = 2664213(0x28a715, float:3.733358E-39)
            if (r0 == r1) goto L_0x0016
            goto L_0x002a
        L_0x0016:
            java.lang.String r0 = "WIFI"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x002a
            r4 = r2
            goto L_0x002b
        L_0x0020:
            java.lang.String r0 = "DPP"
            boolean r4 = r4.equals(r0)
            if (r4 == 0) goto L_0x002a
            r4 = 0
            goto L_0x002b
        L_0x002a:
            r4 = -1
        L_0x002b:
            if (r4 == 0) goto L_0x0034
            if (r4 == r2) goto L_0x0030
            goto L_0x0037
        L_0x0030:
            r3.handleZxingWifiFormat()
            goto L_0x0037
        L_0x0034:
            r3.handleWifiDpp()
        L_0x0037:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.dpp.WifiDppQrCodeScannerFragment.handleSuccessfulResult(java.lang.String):void");
    }

    private void handleWifiDpp() {
        Message obtainMessage = this.mHandler.obtainMessage(3);
        obtainMessage.obj = new WifiQrCode(this.mWifiQrCode.getQrCode());
        this.mHandler.sendMessageDelayed(obtainMessage, 1000);
    }

    private void handleZxingWifiFormat() {
        Message obtainMessage = this.mHandler.obtainMessage(4);
        obtainMessage.obj = new WifiQrCode(this.mWifiQrCode.getQrCode()).getWifiNetworkConfig();
        this.mHandler.sendMessageDelayed(obtainMessage, 1000);
    }

    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    public void handleCameraFailure() {
        destroyCamera();
    }

    private void initCamera(SurfaceTexture surfaceTexture) {
        if (this.mCamera == null) {
            this.mCamera = new QrCamera(getContext(), this);
            if (isWifiDppHandshaking()) {
                QrDecorateView qrDecorateView = this.mDecorateView;
                if (qrDecorateView != null) {
                    qrDecorateView.setFocused(true);
                    return;
                }
                return;
            }
            this.mCamera.start(surfaceTexture);
        }
    }

    private void destroyCamera() {
        QrCamera qrCamera = this.mCamera;
        if (qrCamera != null) {
            qrCamera.stop();
            this.mCamera = null;
        }
    }

    private void showErrorMessage(int i) {
        this.mHandler.obtainMessage(2, getString(i)).sendToTarget();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showErrorMessageAndRestartCamera(int i) {
        Message obtainMessage = this.mHandler.obtainMessage(2, getString(i));
        obtainMessage.arg1 = 1;
        obtainMessage.sendToTarget();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean("key_is_configurator_mode", this.mIsConfiguratorMode);
        bundle.putInt("key_latest_error_code", this.mLatestStatusCode);
        bundle.putParcelable("key_wifi_configuration", this.mEnrolleeWifiConfiguration);
        super.onSaveInstanceState(bundle);
    }

    /* access modifiers changed from: private */
    public class EasyConnectEnrolleeStatusCallback extends EasyConnectStatusCallback {
        public void onConfiguratorSuccess(int i) {
        }

        public void onProgress(int i) {
        }

        private EasyConnectEnrolleeStatusCallback() {
        }

        public void onEnrolleeSuccess(int i) {
            WifiManager wifiManager = (WifiManager) WifiDppQrCodeScannerFragment.this.getContext().getSystemService(WifiManager.class);
            for (WifiConfiguration wifiConfiguration : wifiManager.getPrivilegedConfiguredNetworks()) {
                if (wifiConfiguration.networkId == i) {
                    WifiDppQrCodeScannerFragment.this.mLatestStatusCode = 1;
                    WifiDppQrCodeScannerFragment.this.mEnrolleeWifiConfiguration = wifiConfiguration;
                    wifiManager.connect(wifiConfiguration, WifiDppQrCodeScannerFragment.this);
                    return;
                }
            }
            Log.e("WifiDppQrCodeScanner", "Invalid networkId " + i);
            WifiDppQrCodeScannerFragment.this.mLatestStatusCode = -7;
            WifiDppQrCodeScannerFragment.this.updateEnrolleeSummary();
            WifiDppQrCodeScannerFragment.this.showErrorMessageAndRestartCamera(C0017R$string.wifi_dpp_check_connection_try_again);
        }

        public void onFailure(int i) {
            int i2;
            Log.d("WifiDppQrCodeScanner", "EasyConnectEnrolleeStatusCallback.onFailure " + i);
            switch (i) {
                case -9:
                    throw new IllegalStateException("EASY_CONNECT_EVENT_FAILURE_INVALID_NETWORK should be a configurator only error");
                case -8:
                    throw new IllegalStateException("EASY_CONNECT_EVENT_FAILURE_NOT_SUPPORTED should be a configurator only error");
                case -7:
                    i2 = C0017R$string.wifi_dpp_failure_generic;
                    break;
                case -6:
                    i2 = C0017R$string.wifi_dpp_failure_timeout;
                    break;
                case -5:
                    if (i != WifiDppQrCodeScannerFragment.this.mLatestStatusCode) {
                        WifiDppQrCodeScannerFragment.this.mLatestStatusCode = i;
                        ((WifiManager) WifiDppQrCodeScannerFragment.this.getContext().getSystemService(WifiManager.class)).stopEasyConnectSession();
                        WifiDppQrCodeScannerFragment wifiDppQrCodeScannerFragment = WifiDppQrCodeScannerFragment.this;
                        wifiDppQrCodeScannerFragment.startWifiDppEnrolleeInitiator(wifiDppQrCodeScannerFragment.mWifiQrCode);
                        return;
                    }
                    throw new IllegalStateException("stopEasyConnectSession and try again forEASY_CONNECT_EVENT_FAILURE_BUSY but still failed");
                case -4:
                    i2 = C0017R$string.wifi_dpp_failure_authentication_or_configuration;
                    break;
                case -3:
                    i2 = C0017R$string.wifi_dpp_failure_not_compatible;
                    break;
                case -2:
                    i2 = C0017R$string.wifi_dpp_failure_authentication_or_configuration;
                    break;
                case -1:
                    i2 = C0017R$string.wifi_dpp_qr_code_is_not_valid_format;
                    break;
                default:
                    throw new IllegalStateException("Unexpected Wi-Fi DPP error");
            }
            WifiDppQrCodeScannerFragment.this.mLatestStatusCode = i;
            WifiDppQrCodeScannerFragment.this.updateEnrolleeSummary();
            WifiDppQrCodeScannerFragment.this.showErrorMessageAndRestartCamera(i2);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void startWifiDppEnrolleeInitiator(WifiQrCode wifiQrCode) {
        ((WifiDppInitiatorViewModel) ViewModelProviders.of(this).get(WifiDppInitiatorViewModel.class)).startEasyConnectAsEnrolleeInitiator(wifiQrCode.getQrCode());
    }

    public void onSuccess() {
        Intent intent = new Intent();
        intent.putExtra("wifi_configuration", this.mEnrolleeWifiConfiguration);
        FragmentActivity activity = getActivity();
        activity.setResult(-1, intent);
        activity.finish();
    }

    public void onFailure(int i) {
        Log.d("WifiDppQrCodeScanner", "Wi-Fi connect onFailure reason - " + i);
        showErrorMessageAndRestartCamera(C0017R$string.wifi_dpp_check_connection_try_again);
    }

    private boolean isWifiDppHandshaking() {
        return ((WifiDppInitiatorViewModel) ViewModelProviders.of(this).get(WifiDppInitiatorViewModel.class)).isWifiDppHandshaking();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void restartCamera() {
        QrCamera qrCamera = this.mCamera;
        if (qrCamera == null) {
            Log.d("WifiDppQrCodeScanner", "mCamera is not available for restarting camera");
            return;
        }
        if (qrCamera.isDecodeTaskAlive()) {
            this.mCamera.stop();
        }
        SurfaceTexture surfaceTexture = this.mTextureView.getSurfaceTexture();
        if (surfaceTexture != null) {
            this.mCamera.start(surfaceTexture);
            return;
        }
        throw new IllegalStateException("SurfaceTexture is not ready for restarting camera");
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateEnrolleeSummary() {
        String str;
        if (isWifiDppHandshaking()) {
            this.mSummary.setText(C0017R$string.wifi_dpp_connecting);
            return;
        }
        if (TextUtils.isEmpty(this.mSsid)) {
            str = getString(C0017R$string.wifi_dpp_scan_qr_code_join_unknown_network, this.mSsid);
        } else {
            str = getString(C0017R$string.wifi_dpp_scan_qr_code_join_network, this.mSsid);
        }
        this.mSummary.setText(str);
    }

    /* access modifiers changed from: protected */
    public boolean isDecodeTaskAlive() {
        QrCamera qrCamera = this.mCamera;
        return qrCamera != null && qrCamera.isDecodeTaskAlive();
    }
}
