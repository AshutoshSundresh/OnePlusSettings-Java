package com.android.settings.biometrics.face;

import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0010R$id;
import com.android.settings.biometrics.BiometricEnrollSidecar;
import com.android.settings.biometrics.face.ParticleCollection;
import com.android.settings.core.InstrumentedPreferenceFragment;
import java.util.Arrays;

public class FaceEnrollPreviewFragment extends InstrumentedPreferenceFragment implements BiometricEnrollSidecar.Listener {
    private FaceEnrollAnimationDrawable mAnimationDrawable;
    private final ParticleCollection.Listener mAnimationListener = new ParticleCollection.Listener() {
        /* class com.android.settings.biometrics.face.FaceEnrollPreviewFragment.AnonymousClass1 */

        @Override // com.android.settings.biometrics.face.ParticleCollection.Listener
        public void onEnrolled() {
            FaceEnrollPreviewFragment.this.mListener.onEnrolled();
        }
    };
    private CameraDevice mCameraDevice;
    private String mCameraId;
    private CameraManager mCameraManager;
    private final CameraDevice.StateCallback mCameraStateCallback = new CameraDevice.StateCallback() {
        /* class com.android.settings.biometrics.face.FaceEnrollPreviewFragment.AnonymousClass3 */

        public void onOpened(CameraDevice cameraDevice) {
            FaceEnrollPreviewFragment.this.mCameraDevice = cameraDevice;
            try {
                SurfaceTexture surfaceTexture = FaceEnrollPreviewFragment.this.mTextureView.getSurfaceTexture();
                surfaceTexture.setDefaultBufferSize(FaceEnrollPreviewFragment.this.mPreviewSize.getWidth(), FaceEnrollPreviewFragment.this.mPreviewSize.getHeight());
                Surface surface = new Surface(surfaceTexture);
                FaceEnrollPreviewFragment.this.mPreviewRequestBuilder = FaceEnrollPreviewFragment.this.mCameraDevice.createCaptureRequest(1);
                FaceEnrollPreviewFragment.this.mPreviewRequestBuilder.addTarget(surface);
                FaceEnrollPreviewFragment.this.mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                    /* class com.android.settings.biometrics.face.FaceEnrollPreviewFragment.AnonymousClass3.AnonymousClass1 */

                    public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                        if (FaceEnrollPreviewFragment.this.mCameraDevice != null) {
                            FaceEnrollPreviewFragment.this.mCaptureSession = cameraCaptureSession;
                            try {
                                FaceEnrollPreviewFragment.this.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, 4);
                                FaceEnrollPreviewFragment.this.mPreviewRequest = FaceEnrollPreviewFragment.this.mPreviewRequestBuilder.build();
                                FaceEnrollPreviewFragment.this.mCaptureSession.setRepeatingRequest(FaceEnrollPreviewFragment.this.mPreviewRequest, null, FaceEnrollPreviewFragment.this.mHandler);
                            } catch (CameraAccessException e) {
                                Log.e("FaceEnrollPreviewFragment", "Unable to access camera", e);
                            }
                        }
                    }

                    public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                        Log.e("FaceEnrollPreviewFragment", "Unable to configure camera");
                    }
                }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            FaceEnrollPreviewFragment.this.mCameraDevice = null;
        }

        public void onError(CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            FaceEnrollPreviewFragment.this.mCameraDevice = null;
        }
    };
    private CameraCaptureSession mCaptureSession;
    private ImageView mCircleView;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ParticleCollection.Listener mListener;
    private CaptureRequest mPreviewRequest;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private Size mPreviewSize;
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        /* class com.android.settings.biometrics.face.FaceEnrollPreviewFragment.AnonymousClass2 */

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
            FaceEnrollPreviewFragment.this.openCamera(i, i2);
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
            FaceEnrollPreviewFragment.this.configureTransform(i, i2);
        }
    };
    private FaceSquareTextureView mTextureView;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1554;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mTextureView = (FaceSquareTextureView) getActivity().findViewById(C0010R$id.texture_view);
        ImageView imageView = (ImageView) getActivity().findViewById(C0010R$id.circle_view);
        this.mCircleView = imageView;
        imageView.setLayerType(1, null);
        FaceEnrollAnimationDrawable faceEnrollAnimationDrawable = new FaceEnrollAnimationDrawable(getContext(), this.mAnimationListener);
        this.mAnimationDrawable = faceEnrollAnimationDrawable;
        this.mCircleView.setImageDrawable(faceEnrollAnimationDrawable);
        this.mCameraManager = (CameraManager) getContext().getSystemService("camera");
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        if (this.mTextureView.isAvailable()) {
            openCamera(this.mTextureView.getWidth(), this.mTextureView.getHeight());
        } else {
            this.mTextureView.setSurfaceTextureListener(this.mSurfaceTextureListener);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        closeCamera();
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentError(int i, CharSequence charSequence) {
        this.mAnimationDrawable.onEnrollmentError(i, charSequence);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentHelp(int i, CharSequence charSequence) {
        this.mAnimationDrawable.onEnrollmentHelp(i, charSequence);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentProgressChange(int i, int i2) {
        this.mAnimationDrawable.onEnrollmentProgressChange(i, i2);
    }

    public void setListener(ParticleCollection.Listener listener) {
        this.mListener = listener;
    }

    private void setUpCameraOutputs() {
        try {
            String[] cameraIdList = this.mCameraManager.getCameraIdList();
            for (String str : cameraIdList) {
                CameraCharacteristics cameraCharacteristics = this.mCameraManager.getCameraCharacteristics(str);
                Integer num = (Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (num != null) {
                    if (num.intValue() == 0) {
                        this.mCameraId = str;
                        this.mPreviewSize = chooseOptimalSize(((StreamConfigurationMap) cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)).getOutputSizes(SurfaceTexture.class));
                        return;
                    }
                }
            }
        } catch (CameraAccessException e) {
            Log.e("FaceEnrollPreviewFragment", "Unable to access camera", e);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void openCamera(int i, int i2) {
        try {
            setUpCameraOutputs();
            this.mCameraManager.openCamera(this.mCameraId, this.mCameraStateCallback, this.mHandler);
            configureTransform(i, i2);
        } catch (CameraAccessException e) {
            Log.e("FaceEnrollPreviewFragment", "Unable to open camera", e);
        }
    }

    private Size chooseOptimalSize(Size[] sizeArr) {
        for (int i = 0; i < sizeArr.length; i++) {
            if (sizeArr[i].getHeight() == 1080 && sizeArr[i].getWidth() == 1920) {
                return sizeArr[i];
            }
        }
        Log.w("FaceEnrollPreviewFragment", "Unable to find a good resolution");
        return sizeArr[0];
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void configureTransform(int i, int i2) {
        if (this.mTextureView != null) {
            float width = ((float) i) / ((float) this.mPreviewSize.getWidth());
            float height = ((float) i2) / ((float) this.mPreviewSize.getHeight());
            float min = Math.min(width, height);
            float f = width / min;
            float f2 = height / min;
            TypedValue typedValue = new TypedValue();
            TypedValue typedValue2 = new TypedValue();
            TypedValue typedValue3 = new TypedValue();
            getResources().getValue(C0007R$dimen.face_preview_translate_x, typedValue, true);
            getResources().getValue(C0007R$dimen.face_preview_translate_y, typedValue2, true);
            getResources().getValue(C0007R$dimen.face_preview_scale, typedValue3, true);
            Matrix matrix = new Matrix();
            this.mTextureView.getTransform(matrix);
            matrix.setScale(f * typedValue3.getFloat(), f2 * typedValue3.getFloat());
            matrix.postTranslate(typedValue.getFloat(), typedValue2.getFloat());
            this.mTextureView.setTransform(matrix);
        }
    }

    private void closeCamera() {
        CameraCaptureSession cameraCaptureSession = this.mCaptureSession;
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            this.mCaptureSession = null;
        }
        CameraDevice cameraDevice = this.mCameraDevice;
        if (cameraDevice != null) {
            cameraDevice.close();
            this.mCameraDevice = null;
        }
    }
}
