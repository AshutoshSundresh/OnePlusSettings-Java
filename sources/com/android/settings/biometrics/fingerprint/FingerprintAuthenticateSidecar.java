package com.android.settings.biometrics.fingerprint;

import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import com.android.settings.core.InstrumentedFragment;

public class FingerprintAuthenticateSidecar extends InstrumentedFragment {
    private AuthenticationError mAuthenticationError;
    private FingerprintManager.AuthenticationResult mAuthenticationResult;
    private CancellationSignal mCancellationSignal;
    private Listener mListener;

    public interface Listener {
        void onAuthenticationError(int i, CharSequence charSequence);

        void onAuthenticationFailed();

        void onAuthenticationHelp(int i, CharSequence charSequence);

        void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult);
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1221;
    }

    public void setFingerprintManager(FingerprintManager fingerprintManager) {
    }

    public FingerprintAuthenticateSidecar() {
        new FingerprintManager.AuthenticationCallback() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintAuthenticateSidecar.AnonymousClass1 */

            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult) {
                FingerprintAuthenticateSidecar.this.mCancellationSignal = null;
                if (FingerprintAuthenticateSidecar.this.mListener != null) {
                    FingerprintAuthenticateSidecar.this.mListener.onAuthenticationSucceeded(authenticationResult);
                    return;
                }
                FingerprintAuthenticateSidecar.this.mAuthenticationResult = authenticationResult;
                FingerprintAuthenticateSidecar.this.mAuthenticationError = null;
            }

            public void onAuthenticationFailed() {
                if (FingerprintAuthenticateSidecar.this.mListener != null) {
                    FingerprintAuthenticateSidecar.this.mListener.onAuthenticationFailed();
                }
            }

            public void onAuthenticationError(int i, CharSequence charSequence) {
                FingerprintAuthenticateSidecar.this.mCancellationSignal = null;
                if (FingerprintAuthenticateSidecar.this.mListener != null) {
                    FingerprintAuthenticateSidecar.this.mListener.onAuthenticationError(i, charSequence);
                    return;
                }
                FingerprintAuthenticateSidecar fingerprintAuthenticateSidecar = FingerprintAuthenticateSidecar.this;
                fingerprintAuthenticateSidecar.mAuthenticationError = new AuthenticationError(fingerprintAuthenticateSidecar, i, charSequence);
                FingerprintAuthenticateSidecar.this.mAuthenticationResult = null;
            }

            public void onAuthenticationHelp(int i, CharSequence charSequence) {
                if (FingerprintAuthenticateSidecar.this.mListener != null) {
                    FingerprintAuthenticateSidecar.this.mListener.onAuthenticationHelp(i, charSequence);
                }
            }
        };
    }

    /* access modifiers changed from: private */
    public class AuthenticationError {
        public AuthenticationError(FingerprintAuthenticateSidecar fingerprintAuthenticateSidecar, int i, CharSequence charSequence) {
        }
    }
}
