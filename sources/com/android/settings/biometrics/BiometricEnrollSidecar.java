package com.android.settings.biometrics;

import android.app.Activity;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import com.android.settings.core.InstrumentedFragment;
import java.util.ArrayList;

public abstract class BiometricEnrollSidecar extends InstrumentedFragment {
    private boolean mEnrolling;
    protected CancellationSignal mEnrollmentCancel;
    private int mEnrollmentSteps = -1;
    private Handler mHandler = new Handler();
    private Listener mListener;
    private ArrayList<QueuedEvent> mQueuedEvents = new ArrayList<>();
    private final Runnable mTimeoutRunnable = new Runnable() {
        /* class com.android.settings.biometrics.BiometricEnrollSidecar.AnonymousClass1 */

        public void run() {
            BiometricEnrollSidecar.this.cancelEnrollment();
        }
    };
    protected byte[] mToken;
    protected int mUserId;

    public interface Listener {
        void onEnrollmentError(int i, CharSequence charSequence);

        void onEnrollmentHelp(int i, CharSequence charSequence);

        void onEnrollmentProgressChange(int i, int i2);
    }

    /* access modifiers changed from: private */
    public abstract class QueuedEvent {
        public abstract void send(Listener listener);

        private QueuedEvent(BiometricEnrollSidecar biometricEnrollSidecar) {
        }
    }

    private class QueuedEnrollmentProgress extends QueuedEvent {
        int enrollmentSteps;
        int remaining;

        public QueuedEnrollmentProgress(BiometricEnrollSidecar biometricEnrollSidecar, int i, int i2) {
            super();
            this.enrollmentSteps = i;
            this.remaining = i2;
        }

        @Override // com.android.settings.biometrics.BiometricEnrollSidecar.QueuedEvent
        public void send(Listener listener) {
            listener.onEnrollmentProgressChange(this.enrollmentSteps, this.remaining);
        }
    }

    private class QueuedEnrollmentHelp extends QueuedEvent {
        int helpMsgId;
        CharSequence helpString;

        public QueuedEnrollmentHelp(BiometricEnrollSidecar biometricEnrollSidecar, int i, CharSequence charSequence) {
            super();
            this.helpMsgId = i;
            this.helpString = charSequence;
        }

        @Override // com.android.settings.biometrics.BiometricEnrollSidecar.QueuedEvent
        public void send(Listener listener) {
            listener.onEnrollmentHelp(this.helpMsgId, this.helpString);
        }
    }

    private class QueuedEnrollmentError extends QueuedEvent {
        int errMsgId;
        CharSequence errString;

        public QueuedEnrollmentError(BiometricEnrollSidecar biometricEnrollSidecar, int i, CharSequence charSequence) {
            super();
            this.errMsgId = i;
            this.errString = charSequence;
        }

        @Override // com.android.settings.biometrics.BiometricEnrollSidecar.QueuedEvent
        public void send(Listener listener) {
            listener.onEnrollmentError(this.errMsgId, this.errString);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mToken = activity.getIntent().getByteArrayExtra("hw_auth_token");
        this.mUserId = activity.getIntent().getIntExtra("android.intent.extra.USER_ID", -10000);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (!this.mEnrolling) {
            startEnrollment();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (!getActivity().isChangingConfigurations()) {
            cancelEnrollment();
        }
    }

    /* access modifiers changed from: protected */
    public void startEnrollment() {
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
        this.mEnrollmentSteps = -1;
        this.mEnrollmentCancel = new CancellationSignal();
        this.mEnrolling = true;
    }

    public boolean cancelEnrollment() {
        this.mHandler.removeCallbacks(this.mTimeoutRunnable);
        if (!this.mEnrolling) {
            return false;
        }
        this.mEnrollmentCancel.cancel();
        this.mEnrolling = false;
        this.mEnrollmentSteps = -1;
        return true;
    }

    /* access modifiers changed from: protected */
    public void onEnrollmentProgress(int i) {
        if (this.mEnrollmentSteps == -1) {
            this.mEnrollmentSteps = i;
        }
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onEnrollmentProgressChange(this.mEnrollmentSteps, i);
        } else {
            this.mQueuedEvents.add(new QueuedEnrollmentProgress(this, this.mEnrollmentSteps, i));
        }
    }

    /* access modifiers changed from: protected */
    public void onEnrollmentHelp(int i, CharSequence charSequence) {
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onEnrollmentHelp(i, charSequence);
        } else {
            this.mQueuedEvents.add(new QueuedEnrollmentHelp(this, i, charSequence));
        }
    }

    /* access modifiers changed from: protected */
    public void onEnrollmentError(int i, CharSequence charSequence) {
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onEnrollmentError(i, charSequence);
        } else {
            this.mQueuedEvents.add(new QueuedEnrollmentError(this, i, charSequence));
        }
        this.mEnrolling = false;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
        if (listener != null) {
            for (int i = 0; i < this.mQueuedEvents.size(); i++) {
                this.mQueuedEvents.get(i).send(this.mListener);
            }
            this.mQueuedEvents.clear();
        }
    }
}
