package com.android.settings.password;

import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.os.CancellationSignal;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.password.BiometricFragment;
import java.util.concurrent.Executor;

public class BiometricFragment extends InstrumentedFragment {
    private boolean mAuthenticating;
    private BiometricPrompt.AuthenticationCallback mAuthenticationCallback = new BiometricPrompt.AuthenticationCallback() {
        /* class com.android.settings.password.BiometricFragment.AnonymousClass1 */

        public void onAuthenticationError(int i, CharSequence charSequence) {
            BiometricFragment.this.mAuthenticating = false;
            BiometricFragment.this.mClientExecutor.execute(new Runnable(i, charSequence) {
                /* class com.android.settings.password.$$Lambda$BiometricFragment$1$8MFWuri3Rm7ZsrcLMkq8aGNRNY */
                public final /* synthetic */ int f$1;
                public final /* synthetic */ CharSequence f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    BiometricFragment.AnonymousClass1.this.lambda$onAuthenticationError$0$BiometricFragment$1(this.f$1, this.f$2);
                }
            });
            BiometricFragment.this.cleanup();
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onAuthenticationError$0 */
        public /* synthetic */ void lambda$onAuthenticationError$0$BiometricFragment$1(int i, CharSequence charSequence) {
            BiometricFragment.this.mClientCallback.onAuthenticationError(i, charSequence);
        }

        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult authenticationResult) {
            BiometricFragment.this.mAuthenticating = false;
            BiometricFragment.this.mClientExecutor.execute(new Runnable(authenticationResult) {
                /* class com.android.settings.password.$$Lambda$BiometricFragment$1$VRGQlQZZYr0QoD3OQpS9MEP5Z08 */
                public final /* synthetic */ BiometricPrompt.AuthenticationResult f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    BiometricFragment.AnonymousClass1.this.lambda$onAuthenticationSucceeded$1$BiometricFragment$1(this.f$1);
                }
            });
            BiometricFragment.this.cleanup();
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onAuthenticationSucceeded$1 */
        public /* synthetic */ void lambda$onAuthenticationSucceeded$1$BiometricFragment$1(BiometricPrompt.AuthenticationResult authenticationResult) {
            BiometricFragment.this.mClientCallback.onAuthenticationSucceeded(authenticationResult);
        }

        public void onAuthenticationFailed() {
            BiometricFragment.this.mClientExecutor.execute(new Runnable() {
                /* class com.android.settings.password.$$Lambda$BiometricFragment$1$di5vVbjcZSKYBrdj3zA0to77iaU */

                public final void run() {
                    BiometricFragment.AnonymousClass1.this.lambda$onAuthenticationFailed$2$BiometricFragment$1();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onAuthenticationFailed$2 */
        public /* synthetic */ void lambda$onAuthenticationFailed$2$BiometricFragment$1() {
            BiometricFragment.this.mClientCallback.onAuthenticationFailed();
        }

        public void onSystemEvent(int i) {
            BiometricFragment.this.mClientExecutor.execute(new Runnable(i) {
                /* class com.android.settings.password.$$Lambda$BiometricFragment$1$CofdZU9upqDBesLU_tCxlebk */
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    BiometricFragment.AnonymousClass1.this.lambda$onSystemEvent$3$BiometricFragment$1(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onSystemEvent$3 */
        public /* synthetic */ void lambda$onSystemEvent$3$BiometricFragment$1(int i) {
            BiometricFragment.this.mClientCallback.onSystemEvent(i);
        }
    };
    private BiometricPrompt mBiometricPrompt;
    private Bundle mBundle;
    private CancellationSignal mCancellationSignal;
    private BiometricPrompt.AuthenticationCallback mClientCallback;
    private Executor mClientExecutor;
    private int mUserId;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1585;
    }

    public static BiometricFragment newInstance(Bundle bundle) {
        BiometricFragment biometricFragment = new BiometricFragment();
        biometricFragment.setArguments(bundle);
        return biometricFragment;
    }

    public void setCallbacks(Executor executor, BiometricPrompt.AuthenticationCallback authenticationCallback) {
        this.mClientExecutor = executor;
        this.mClientCallback = authenticationCallback;
    }

    public void setUser(int i) {
        this.mUserId = i;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void cleanup() {
        if (getActivity() != null) {
            FragmentTransaction beginTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            beginTransaction.remove(this);
            beginTransaction.commitAllowingStateLoss();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
        this.mBundle = getArguments();
        this.mBiometricPrompt = new BiometricPrompt.Builder(getContext()).setTitle(this.mBundle.getString("title")).setUseDefaultTitle().setDeviceCredentialAllowed(true).setSubtitle(this.mBundle.getString("subtitle")).setDescription(this.mBundle.getString("description")).setTextForDeviceCredential(this.mBundle.getCharSequence("device_credential_title"), this.mBundle.getCharSequence("device_credential_subtitle"), this.mBundle.getCharSequence("device_credential_description")).setConfirmationRequired(this.mBundle.getBoolean("require_confirmation", true)).setDisallowBiometricsIfPolicyExists(this.mBundle.getBoolean("check_dpm", false)).setReceiveSystemEvents(true).build();
        CancellationSignal cancellationSignal = new CancellationSignal();
        this.mCancellationSignal = cancellationSignal;
        this.mBiometricPrompt.authenticateUser(cancellationSignal, this.mClientExecutor, this.mAuthenticationCallback, this.mUserId);
    }
}
