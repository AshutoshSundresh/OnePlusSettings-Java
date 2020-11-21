package com.android.settings.development;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.debug.IAdbManager;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.wifi.dpp.AdbQrCode;
import com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment;
import com.android.settings.wifi.dpp.WifiNetworkConfig;
import com.android.settings.wifi.qrcode.QrCamera;
import com.android.settings.wifi.qrcode.QrDecorateView;

public class AdbQrcodeScannerFragment extends WifiDppQrCodeBaseFragment implements TextureView.SurfaceTextureListener, QrCamera.ScannerCallback {
    private WifiNetworkConfig mAdbConfig;
    private IAdbManager mAdbManager;
    private AdbQrCode mAdbQrCode;
    private QrCamera mCamera;
    private QrDecorateView mDecorateView;
    private TextView mErrorMessage;
    private final Handler mHandler = new Handler() {
        /* class com.android.settings.development.AdbQrcodeScannerFragment.AnonymousClass2 */

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                AdbQrcodeScannerFragment.this.mErrorMessage.setVisibility(4);
            } else if (i == 2) {
                AdbQrcodeScannerFragment.this.mErrorMessage.setVisibility(0);
                AdbQrcodeScannerFragment.this.mErrorMessage.setText((String) message.obj);
                AdbQrcodeScannerFragment.this.mErrorMessage.sendAccessibilityEvent(32);
                removeMessages(1);
                sendEmptyMessageDelayed(1, 10000);
            }
        }
    };
    private IntentFilter mIntentFilter;
    private View mQrCameraView;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.development.AdbQrcodeScannerFragment.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            if ("com.android.server.adb.WIRELESS_DEBUG_PAIRING_RESULT".equals(intent.getAction())) {
                Integer valueOf = Integer.valueOf(intent.getIntExtra("status", 0));
                if (valueOf.equals(1)) {
                    Intent intent2 = new Intent();
                    intent2.putExtra("request_type_pairing", 0);
                    AdbQrcodeScannerFragment.this.getActivity().setResult(-1, intent2);
                    AdbQrcodeScannerFragment.this.getActivity().finish();
                } else if (valueOf.equals(0)) {
                    Intent intent3 = new Intent();
                    intent3.putExtra("request_type_pairing", 1);
                    AdbQrcodeScannerFragment.this.getActivity().setResult(-1, intent3);
                    AdbQrcodeScannerFragment.this.getActivity().finish();
                } else if (valueOf.equals(4)) {
                    int intExtra = intent.getIntExtra("adb_port", 0);
                    Log.i("AdbQrcodeScannerFrag", "Got Qr pairing code port=" + intExtra);
                }
            }
        }
    };
    private TextureView mTextureView;
    private TextView mVerifyingTextView;
    private View mVerifyingView;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment
    public boolean isFooterAvailable() {
        return false;
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mIntentFilter = new IntentFilter("com.android.server.adb.WIRELESS_DEBUG_PAIRING_RESULT");
    }

    @Override // androidx.fragment.app.Fragment
    public final View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(C0012R$layout.adb_qrcode_scanner_fragment, viewGroup, false);
    }

    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeBaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        TextureView textureView = (TextureView) view.findViewById(C0010R$id.preview_view);
        this.mTextureView = textureView;
        textureView.setSurfaceTextureListener(this);
        this.mDecorateView = (QrDecorateView) view.findViewById(C0010R$id.decorate_view);
        setProgressBarShown(false);
        setHeaderIconImageResource(C0008R$drawable.ic_scan_24dp);
        this.mQrCameraView = view.findViewById(C0010R$id.camera_layout);
        this.mVerifyingView = view.findViewById(C0010R$id.verifying_layout);
        this.mVerifyingTextView = (TextView) view.findViewById(C0010R$id.verifying_textview);
        setHeaderTitle(C0017R$string.wifi_dpp_scan_qr_code, new Object[0]);
        this.mSummary.setText(C0017R$string.adb_wireless_qrcode_pairing_description);
        this.mErrorMessage = (TextView) view.findViewById(C0010R$id.error_message);
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mAdbManager = IAdbManager.Stub.asInterface(ServiceManager.getService("adb"));
        getActivity().registerReceiver(this.mReceiver, this.mIntentFilter);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.mReceiver);
        try {
            this.mAdbManager.disablePairing();
        } catch (RemoteException unused) {
            Log.e("AdbQrcodeScannerFrag", "Unable to cancel pairing");
        }
        getActivity().setResult(0);
        getActivity().finish();
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getActivity().getActionBar().hide();
        getActivity().setTitle(C0017R$string.wifi_dpp_scan_qr_code);
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
    public void setTransform(Matrix matrix) {
        this.mTextureView.setTransform(matrix);
    }

    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    public Rect getFramePosition(Size size, int i) {
        return new Rect(0, 0, size.getHeight(), size.getHeight());
    }

    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    public boolean isValid(String str) {
        try {
            AdbQrCode adbQrCode = new AdbQrCode(str);
            this.mAdbQrCode = adbQrCode;
            this.mAdbConfig = adbQrCode.getAdbNetworkConfig();
            return true;
        } catch (IllegalArgumentException unused) {
            showErrorMessage(C0017R$string.wifi_dpp_qr_code_is_not_valid_format);
            return false;
        }
    }

    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    public void handleSuccessfulResult(String str) {
        destroyCamera();
        this.mDecorateView.setFocused(true);
        this.mQrCameraView.setVisibility(8);
        this.mVerifyingView.setVisibility(0);
        AdbQrCode.triggerVibrationForQrCodeRecognition(getContext());
        this.mVerifyingTextView.sendAccessibilityEvent(8);
        try {
            this.mAdbManager.enablePairingByQrCode(this.mAdbConfig.getSsid(), this.mAdbConfig.getPreSharedKey());
        } catch (RemoteException unused) {
            Log.e("AdbQrcodeScannerFrag", "Unable to enable QR code pairing");
            getActivity().setResult(0);
            getActivity().finish();
        }
    }

    @Override // com.android.settings.wifi.qrcode.QrCamera.ScannerCallback
    public void handleCameraFailure() {
        destroyCamera();
    }

    private void initCamera(SurfaceTexture surfaceTexture) {
        if (this.mCamera == null) {
            QrCamera qrCamera = new QrCamera(getContext(), this);
            this.mCamera = qrCamera;
            qrCamera.start(surfaceTexture);
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
}
