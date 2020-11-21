package com.android.settings.biometrics.fingerprint;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.StatusBarManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.IFingerprintService;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.C0002R$anim;
import com.android.settings.C0006R$color;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.android.settings.biometrics.BiometricEnrollSidecar;
import com.android.settings.biometrics.BiometricErrorDialog;
import com.android.settings.biometrics.BiometricsEnrollEnrolling;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.util.DescriptionStyler;
import com.oneplus.settings.opfinger.OPFingerPrintDynamicEnrollView;
import com.oneplus.settings.opfinger.OPFingerPrintEnrollView;
import com.oneplus.settings.opfinger.OnOPFingerComfirmListener;
import com.oneplus.settings.utils.OPThemeUtils;
import com.oneplus.settings.utils.OPUtils;

public class FingerprintEnrollEnrolling extends BiometricsEnrollEnrolling implements OnOPFingerComfirmListener, View.OnClickListener {
    Runnable callFingerprintServiceRunnable;
    protected boolean isSetupPage;
    private boolean mAnimationCancelled;
    private boolean mConfirmCompleted = false;
    private int mCurrentProgress = 0;
    private final Runnable mDelayedFinishRunnable = new Runnable() {
        /* class com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.AnonymousClass4 */

        public void run() {
            FingerprintEnrollEnrolling fingerprintEnrollEnrolling = FingerprintEnrollEnrolling.this;
            fingerprintEnrollEnrolling.launchFinish(((BiometricEnrollBase) fingerprintEnrollEnrolling).mToken);
        }
    };
    private final Runnable mDelayedShowLottieRunnable;
    private AlertDialog mDialog;
    private LottieAnimationView mEdgeEnrollAnimView;
    private int mEnrollState = -1;
    private int mEnrollSuccessCount = 0;
    private Animator mErrorAnimator;
    private TextView mErrorText;
    private Interpolator mFastOutLinearInInterpolator;
    private Handler mHandler;
    private boolean mHasInputCompleted = false;
    private AnimatedVectorDrawable mIconAnimationDrawable;
    private boolean mIsEnrollPaused = false;
    private boolean mLaunchingFinish;
    private Interpolator mLinearOutSlowInInterpolator;
    protected boolean mNeedHideNavBar = true;
    private boolean mNeedJumpToFingerprintSettings = false;
    private Button mNextButton;
    protected OPFingerPrintDynamicEnrollView mOPFingerPrintDynamicEnrollView;
    protected OPFingerPrintEnrollView mOPFingerPrintEnrollView;
    private boolean mOnBackPress = false;
    private ProgressBar mProgressBar;
    protected TextView mRepeatMessage;
    private boolean mScreenNavBarEnabled = false;
    protected TextView mStartMessage;
    StatusBarManager mStatusBarManager;
    private final Runnable mTouchAgainRunnable;
    private boolean mValidEnroll = true;
    private PowerManager.WakeLock mWakeLock;
    private Runnable mWakeLockUseRunnable;
    int overlayLayoutId = -1;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 240;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling
    public boolean shouldStartAutomatically() {
        return false;
    }

    public FingerprintEnrollEnrolling() {
        new Animatable2.AnimationCallback() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.AnonymousClass5 */

            public void onAnimationEnd(Drawable drawable) {
                if (!FingerprintEnrollEnrolling.this.mAnimationCancelled) {
                    FingerprintEnrollEnrolling.this.mProgressBar.post(new Runnable() {
                        /* class com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.AnonymousClass5.AnonymousClass1 */

                        public void run() {
                            FingerprintEnrollEnrolling.this.startIconAnimation();
                        }
                    });
                }
            }
        };
        this.mTouchAgainRunnable = new Runnable() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.AnonymousClass7 */

            public void run() {
                FingerprintEnrollEnrolling.this.clearError();
                FingerprintEnrollEnrolling fingerprintEnrollEnrolling = FingerprintEnrollEnrolling.this;
                fingerprintEnrollEnrolling.showError(fingerprintEnrollEnrolling.getString(C0017R$string.security_settings_fingerprint_enroll_lift_touch_again));
            }
        };
        this.mHandler = new Handler(this, Looper.getMainLooper()) {
            /* class com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.AnonymousClass8 */

            public void handleMessage(Message message) {
                super.handleMessage(message);
            }
        };
        IFingerprintService.Stub.asInterface(ServiceManager.getService("fingerprint"));
        this.mWakeLockUseRunnable = new Runnable() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.AnonymousClass12 */

            public void run() {
                if (FingerprintEnrollEnrolling.this.mWakeLock != null) {
                    FingerprintEnrollEnrolling.this.releaseWakeLock();
                }
            }
        };
        this.mDelayedShowLottieRunnable = new Runnable() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.AnonymousClass13 */

            public void run() {
                if (OPUtils.isSupportDynamicEnrollAnimation()) {
                    FingerprintEnrollEnrolling.this.mOPFingerPrintDynamicEnrollView.playEnrollCompletedAnim();
                } else {
                    FingerprintEnrollEnrolling.this.mOPFingerPrintEnrollView.playEnrollCompletedAnim();
                }
            }
        };
        this.callFingerprintServiceRunnable = new Runnable() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.AnonymousClass14 */

            public void run() {
                if (((BiometricsEnrollEnrolling) FingerprintEnrollEnrolling.this).mSidecar == null) {
                    ((BiometricsEnrollEnrolling) FingerprintEnrollEnrolling.this).mSidecar = new FingerprintEnrollSidecar();
                    FragmentTransaction beginTransaction = FingerprintEnrollEnrolling.this.getSupportFragmentManager().beginTransaction();
                    beginTransaction.add(((BiometricsEnrollEnrolling) FingerprintEnrollEnrolling.this).mSidecar, "sidecar");
                    beginTransaction.commitAllowingStateLoss();
                }
                ((BiometricsEnrollEnrolling) FingerprintEnrollEnrolling.this).mSidecar.setListener(FingerprintEnrollEnrolling.this);
            }
        };
    }

    static {
        VibrationEffect.createWaveform(new long[]{0, 5, 55, 60}, -1);
        new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    }

    public static class FingerprintErrorDialog extends BiometricErrorDialog {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 569;
        }

        static FingerprintErrorDialog newInstance(CharSequence charSequence, int i) {
            FingerprintErrorDialog fingerprintErrorDialog = new FingerprintErrorDialog();
            Bundle bundle = new Bundle();
            bundle.putCharSequence("error_msg", charSequence);
            bundle.putInt("error_id", i);
            fingerprintErrorDialog.setArguments(bundle);
            return fingerprintErrorDialog;
        }

        static FingerprintErrorDialog newInstance(CharSequence charSequence, int i, boolean z) {
            FingerprintErrorDialog fingerprintErrorDialog = new FingerprintErrorDialog();
            Bundle bundle = new Bundle();
            bundle.putCharSequence("error_msg", charSequence);
            bundle.putInt("error_id", i);
            bundle.putBoolean("setup_for_back_fingerprint", z);
            fingerprintErrorDialog.setArguments(bundle);
            return fingerprintErrorDialog;
        }

        @Override // com.android.settings.biometrics.BiometricErrorDialog
        public int getTitleResId() {
            return C0017R$string.security_settings_fingerprint_enroll_error_dialog_title;
        }

        @Override // com.android.settings.biometrics.BiometricErrorDialog
        public int getOkButtonTextResId() {
            return C0017R$string.security_settings_fingerprint_enroll_dialog_ok;
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, com.android.settings.biometrics.BiometricEnrollBase, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.core.InstrumentedActivity
    public void onCreate(Bundle bundle) {
        int i;
        if (OPUtils.isSupportCustomFingerprint()) {
            setTheme(C0018R$style.OnePlusFingerprintEnrolling);
        }
        super.onCreate(bundle);
        boolean z = false;
        if (OPUtils.isSupportCustomFingerprint()) {
            getWindow().getDecorView().setSystemUiVisibility(0);
        }
        if (isInMultiWindowMode()) {
            Toast.makeText(this, C0017R$string.oneplus_cannot_enroll_fingerprint_in_splitting_screen, 0).show();
            finish();
        }
        this.mNeedJumpToFingerprintSettings = getIntent().getBooleanExtra("needJumpToFingerprintSettings", false);
        Utils.getFingerprintManagerOrNull(this);
        if (OPUtils.isSupportCustomFingerprint()) {
            getWindow().getDecorView().setSystemUiVisibility(0);
        }
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            getActionBar().setElevation(0.0f);
        }
        if (OPUtils.isSupportDynamicEnrollAnimation()) {
            i = this.overlayLayoutId;
            if (i == -1) {
                i = C0012R$layout.op_fod_fingerprint_dynamic_enroll_enrolling_base;
            }
        } else if (OPUtils.isSupportCustomFingerprint()) {
            i = this.overlayLayoutId;
            if (i == -1) {
                if (this.isSetupPage) {
                    i = C0012R$layout.op_fod_setup_fingerprint_enroll_enrolling_base;
                } else {
                    i = C0012R$layout.op_fod_fingerprint_enroll_enrolling_base;
                }
            }
        } else if (OPUtils.isSurportBackFingerprint(this)) {
            i = this.overlayLayoutId;
            if (i == -1) {
                i = C0012R$layout.op_back_fingerprint_enroll_enrolling_base;
            }
        } else {
            i = C0012R$layout.fingerprint_enroll_enrolling_base;
        }
        setContentView(i);
        setHeaderText(C0017R$string.security_settings_fingerprint_enroll_repeat_title);
        ((GlifLayout) findViewById(C0010R$id.setup_wizard_layout)).getHeaderTextView().setTextColor(getResources().getColor(C0006R$color.op_control_text_color_primary_dark));
        this.mStartMessage = (TextView) findViewById(C0010R$id.start_message);
        this.mRepeatMessage = (TextView) findViewById(C0010R$id.repeat_message);
        this.mErrorText = (TextView) findViewById(C0010R$id.error_text);
        this.mProgressBar = (ProgressBar) findViewById(C0010R$id.fingerprint_progress_bar);
        Vibrator vibrator = (Vibrator) getSystemService(Vibrator.class);
        if (getLayout().shouldApplyPartnerHeavyThemeResource()) {
            DescriptionStyler.applyPartnerCustomizationStyle(this.mRepeatMessage);
        }
        if (OPUtils.isSupportCustomFingerprint()) {
            Button button = (Button) findViewById(C0010R$id.continue_enroll_button);
            this.mNextButton = button;
            button.setOnClickListener(this);
            this.mNextButton.setVisibility(4);
            LottieAnimationView lottieAnimationView = (LottieAnimationView) findViewById(C0010R$id.op_finger_edge_enroll_view);
            this.mEdgeEnrollAnimView = lottieAnimationView;
            lottieAnimationView.loop(true);
        }
        initFingerPrintEnrollView();
        this.mStatusBarManager = (StatusBarManager) getSystemService("statusbar");
        if (Settings.System.getInt(getContentResolver(), "buttons_show_on_screen_navkeys", 0) == 1) {
            z = true;
        }
        this.mScreenNavBarEnabled = z;
        this.mErrorAnimator = AnimatorInflater.loadAnimator(this, C0002R$anim.shake_anim);
        adjustTitleSize();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling
    public BiometricEnrollSidecar getSidecar() {
        return new FingerprintEnrollSidecar();
    }

    /* access modifiers changed from: protected */
    public void showEnrollNoteDialog() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && !alertDialog.isShowing()) {
            this.mDialog.show();
        }
        View inflate = LayoutInflater.from(this).inflate(C0012R$layout.op_fingerprint_note_dialog, (ViewGroup) null);
        int dimensionPixelSize = getResources().getDimensionPixelSize(C0007R$dimen.oneplus_contorl_avatar_standard);
        CheckBox checkBox = (CheckBox) inflate.findViewById(C0010R$id.checkbox);
        boolean z = true;
        if (Settings.System.getIntForUser(getContentResolver(), "op_do_not_show_fingerprint_enroll_note", 0, -2) != 1) {
            z = false;
        }
        checkBox.setChecked(z);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /* class com.android.settings.biometrics.fingerprint.$$Lambda$FingerprintEnrollEnrolling$rb_Rq7CTysqI3SAxCDpkfbZtt88 */

            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                FingerprintEnrollEnrolling.this.lambda$showEnrollNoteDialog$0$FingerprintEnrollEnrolling(compoundButton, z);
            }
        });
        expandViewTouchDelegate(checkBox, dimensionPixelSize);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, C0018R$style.OPDarkDialogAlert);
        builder.setTitle(C0017R$string.op_fingerprint_enroll_note);
        builder.setMessage(C0017R$string.op_fingerprint_enroll_note_dialog_msg);
        builder.setView(inflate);
        builder.setCancelable(false);
        builder.setPositiveButton(C0017R$string.oneplus_device_name_ok, new DialogInterface.OnClickListener() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                FingerprintEnrollEnrolling.this.mDialog.dismiss();
                FingerprintEnrollEnrolling.this.delayCallFingerprintService();
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
        OPThemeUtils.setDialogTextColor(this.mDialog);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showEnrollNoteDialog$0 */
    public /* synthetic */ void lambda$showEnrollNoteDialog$0$FingerprintEnrollEnrolling(CompoundButton compoundButton, boolean z) {
        Settings.System.putIntForUser(getContentResolver(), "op_do_not_show_fingerprint_enroll_note", z ? 1 : 0, -2);
        Log.d("FingerprintNoteDialog", "Don't show again:" + z);
    }

    private void expandViewTouchDelegate(final View view, final int i) {
        ((View) view.getParent()).post(new Runnable(this) {
            /* class com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.AnonymousClass2 */

            public void run() {
                Rect rect = new Rect();
                view.setEnabled(true);
                view.getHitRect(rect);
                int i = (i - (rect.bottom - rect.top)) / 2;
                Log.i("CheckBoxDemo", "bounds.top = " + rect.top + " ,bounds.bottom = " + rect.bottom + " ,bounds.left = " + rect.left + " ,bounds.right = " + rect.right + " ,padding = " + i);
                rect.top = rect.top - i;
                rect.bottom = rect.bottom + i;
                rect.left = rect.left - i;
                rect.right = rect.right + i;
                TouchDelegate touchDelegate = new TouchDelegate(rect, view);
                if (View.class.isInstance(view.getParent())) {
                    ((View) view.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.biometrics.BiometricsEnrollEnrolling
    public void onStart() {
        super.onStart();
        if (Settings.System.getIntForUser(getContentResolver(), "op_do_not_show_fingerprint_enroll_note", 0, -2) == 1) {
            delayCallFingerprintService();
        } else {
            showEnrollNoteDialog();
        }
    }

    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        this.mAnimationCancelled = false;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void startIconAnimation() {
        this.mIconAnimationDrawable.start();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.biometrics.BiometricsEnrollEnrolling
    public void onStop() {
        super.onStop();
        this.mHandler.removeCallbacks(this.callFingerprintServiceRunnable);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling
    public Intent getFinishIntent() {
        return new Intent(this, FingerprintEnrollFinish.class);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentHelp(int i, CharSequence charSequence) {
        Log.d("FingerprintEnrollEnrolling", "onEnrollmentHelp:" + ((Object) charSequence) + " helpMsgId:" + i);
        this.mValidEnroll = false;
        if (!OPUtils.isSupportDynamicEnrollAnimation()) {
            this.mOPFingerPrintEnrollView.setEnrollAnimVisibility(true);
        }
        if (!OPUtils.isSupportCustomFingerprint()) {
            if (i == 1002) {
                CharSequence text = getText(C0017R$string.oneplus_fingerprint_acquired_too_similar);
                showError(text);
                Log.d("FingerprintEnrollEnrolling", "FINGERPRINT_ACQUIRED_TOO_SIMILAR:" + ((Object) text));
            } else if (i != 1100) {
                showError(charSequence);
            } else {
                showError(getText(C0017R$string.oneplus_security_settings_fingerprint_exists_dialog_message));
            }
        } else if (i == 1) {
            showError(getText(C0017R$string.oneplus_fingerprint_acquired_partial));
        } else if (i == 3) {
            showError(getText(C0017R$string.oneplus_fingerprint_acquired_imager_dirty));
        } else if (i == 5) {
            showError(getText(C0017R$string.oneplus_fingerprint_acquired_too_fast));
        } else if (i == 1000) {
            OPFingerPrintEnrollView oPFingerPrintEnrollView = this.mOPFingerPrintEnrollView;
            if (oPFingerPrintEnrollView != null) {
                oPFingerPrintEnrollView.startTouchDownAnim();
            }
        } else if (i == 1002) {
            showError(getText(C0017R$string.oneplus_fingerprint_acquired_too_similar));
        } else if (i != 1100) {
            showError(charSequence);
        } else {
            showError(getText(C0017R$string.oneplus_security_settings_fingerprint_exists_dialog_message));
        }
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentError(int i, CharSequence charSequence) {
        int i2;
        Log.d("FingerprintEnrollEnrolling", "onEnrollmentError:" + ((Object) charSequence) + " errMsgId:" + i);
        this.mValidEnroll = false;
        if (OPUtils.isSupportDynamicEnrollAnimation()) {
            this.mOPFingerPrintDynamicEnrollView.setEnrollAnimVisibility(true);
        } else {
            this.mOPFingerPrintEnrollView.setEnrollAnimVisibility(true);
        }
        if (i != 3) {
            i2 = C0017R$string.oneplus_finger_input_error_tips;
        } else {
            i2 = C0017R$string.security_settings_fingerprint_enroll_error_timeout_dialog_message;
        }
        showErrorDialog(getText(i2), i);
        this.mErrorText.removeCallbacks(this.mTouchAgainRunnable);
    }

    @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
    public void onEnrollmentProgressChange(int i, int i2) {
        Log.d("FingerprintEnrollEnrolling", "onEnrollmentProgressChange--mValidEnroll:" + this.mValidEnroll + " steps:" + i + " remaining:" + i2);
        updateProgress(true, i, i2);
    }

    private void showErrorDialog(CharSequence charSequence, int i) {
        FingerprintErrorDialog fingerprintErrorDialog;
        if (OPUtils.isSupportCustomFingerprint()) {
            fingerprintErrorDialog = FingerprintErrorDialog.newInstance(charSequence, i);
        } else if (this.isSetupPage) {
            fingerprintErrorDialog = FingerprintErrorDialog.newInstance(charSequence, i, true);
        } else {
            fingerprintErrorDialog = FingerprintErrorDialog.newInstance(charSequence, i);
        }
        fingerprintErrorDialog.show(getSupportFragmentManager(), FingerprintErrorDialog.class.getName());
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showError(CharSequence charSequence) {
        if (OPUtils.isSupportCustomFingerprint()) {
            this.mRepeatMessage.setText(charSequence);
            animateErrorText(this.mRepeatMessage);
            return;
        }
        Log.e("FingerprintEnrollEnrolling", "showError error:" + ((Object) charSequence));
        this.mErrorText.setText(charSequence);
        if (this.mErrorText.getVisibility() == 4) {
            this.mErrorText.setVisibility(0);
            this.mErrorText.setTranslationY((float) getResources().getDimensionPixelSize(C0007R$dimen.fingerprint_error_text_appear_distance));
            this.mErrorText.setAlpha(0.0f);
            this.mErrorText.animate().alpha(1.0f).translationY(0.0f).setDuration(200).setInterpolator(this.mLinearOutSlowInInterpolator).start();
            return;
        }
        this.mErrorText.animate().cancel();
        this.mErrorText.setAlpha(1.0f);
        this.mErrorText.setTranslationY(0.0f);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void clearError() {
        if (this.mErrorText.getVisibility() == 0) {
            this.mErrorText.animate().alpha(0.0f).translationY((float) getResources().getDimensionPixelSize(C0007R$dimen.fingerprint_error_text_disappear_distance)).setDuration(100).setInterpolator(this.mFastOutLinearInInterpolator).withEndAction(new Runnable() {
                /* class com.android.settings.biometrics.fingerprint.$$Lambda$FingerprintEnrollEnrolling$eo3zX11cVbdZD3KKpJKd94ja24w */

                public final void run() {
                    FingerprintEnrollEnrolling.this.lambda$clearError$1$FingerprintEnrollEnrolling();
                }
            }).start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$clearError$1 */
    public /* synthetic */ void lambda$clearError$1$FingerprintEnrollEnrolling() {
        this.mErrorText.setVisibility(4);
    }

    public static class IconTouchDialog extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 568;
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(C0017R$string.security_settings_fingerprint_enroll_touch_dialog_title);
            builder.setMessage(C0017R$string.security_settings_fingerprint_enroll_touch_dialog_message);
            builder.setPositiveButton(C0017R$string.security_settings_fingerprint_enroll_dialog_ok, new DialogInterface.OnClickListener(this) {
                /* class com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.IconTouchDialog.AnonymousClass1 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            return builder.create();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        if (OPUtils.isSupportCustomFingerprint()) {
            disableRecentAndHomeKey();
        } else if (OPUtils.isSurportBackFingerprint(this)) {
            disableRecentKey();
        } else {
            disableAllKey();
        }
        if (!this.mConfirmCompleted && (!this.mOnBackPress || !this.mHasInputCompleted)) {
            if (OPUtils.isSupportDynamicEnrollAnimation()) {
                this.mOPFingerPrintDynamicEnrollView.resetWithoutAnimation();
            } else {
                this.mOPFingerPrintEnrollView.resetWithoutAnimation();
            }
        }
        this.mHasInputCompleted = false;
        this.mConfirmCompleted = false;
        this.mOnBackPress = false;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity
    public void onPause() {
        super.onPause();
        if (OPUtils.isSupportCustomFingerprint()) {
            enableRecentAndHomeKey();
        } else if (OPUtils.isSurportBackFingerprint(this)) {
            enableRecentKey();
        } else {
            enableAllKey();
        }
        if (!isChangingConfigurations()) {
            this.mCurrentProgress = 0;
            this.mEnrollSuccessCount = 0;
            BiometricEnrollSidecar biometricEnrollSidecar = this.mSidecar;
            if (biometricEnrollSidecar != null) {
                biometricEnrollSidecar.cancelEnrollment();
            }
            releaseWakeLock();
            if (!this.mConfirmCompleted) {
                boolean z = this.mOnBackPress;
            }
            if (OPUtils.isSupportDynamicEnrollAnimation()) {
                this.mOPFingerPrintDynamicEnrollView.hideWarningTips();
            } else {
                this.mOPFingerPrintEnrollView.hideWarningTips();
            }
            this.mHandler.removeCallbacks(this.mTouchAgainRunnable);
            this.mConfirmCompleted = false;
            this.mOnBackPress = false;
            this.mHasInputCompleted = false;
            resumeEnroll(false, 10);
            setResult(1);
            finish();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        BiometricEnrollSidecar biometricEnrollSidecar = this.mSidecar;
        if (biometricEnrollSidecar != null) {
            biometricEnrollSidecar.cancelEnrollment();
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.remove(this.mSidecar);
            beginTransaction.commitAllowingStateLoss();
        }
        if (OPUtils.isSupportCustomFingerprint()) {
            if (this.mNeedHideNavBar) {
                enableAllKey();
            } else {
                enableRecentAndHomeKey();
            }
        } else if (OPUtils.isSurportBackFingerprint(this)) {
            enableRecentKey();
        } else {
            enableAllKey();
        }
        LottieAnimationView lottieAnimationView = this.mEdgeEnrollAnimView;
        if (lottieAnimationView != null) {
            lottieAnimationView.cancelAnimation();
            this.mEdgeEnrollAnimView = null;
        }
        if (OPUtils.isSupportDynamicEnrollAnimation()) {
            this.mOPFingerPrintDynamicEnrollView.releaseEnrollCompletedAnim();
        } else {
            this.mOPFingerPrintEnrollView.releaseEnrollCompletedAnim();
        }
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismiss();
            this.mDialog = null;
        }
        super.onDestroy();
    }

    @Override // androidx.activity.ComponentActivity, com.android.settings.biometrics.BiometricsEnrollEnrolling
    public void onBackPressed() {
        Log.d("FingerprintEnrollEnrolling", "onBackPressed");
        changeEnrollStateByFocusChanged(false);
        super.onBackPressed();
    }

    private void initFingerPrintEnrollView() {
        if (OPUtils.isSupportDynamicEnrollAnimation()) {
            OPFingerPrintDynamicEnrollView oPFingerPrintDynamicEnrollView = (OPFingerPrintDynamicEnrollView) findViewById(C0010R$id.op_finger_enroll_view);
            this.mOPFingerPrintDynamicEnrollView = oPFingerPrintDynamicEnrollView;
            oPFingerPrintDynamicEnrollView.setTitleView(this.mStartMessage);
            this.mRepeatMessage.setVisibility(0);
            this.mOPFingerPrintDynamicEnrollView.setSubTitleView(this.mRepeatMessage);
            this.mOPFingerPrintDynamicEnrollView.hideHeaderView();
            this.mOPFingerPrintDynamicEnrollView.setOnOPFingerComfirmListener(this);
            this.mOPFingerPrintDynamicEnrollView.setEnrollAnimVisibility(true);
        } else {
            this.mOPFingerPrintEnrollView = (OPFingerPrintEnrollView) findViewById(C0010R$id.op_finger_enroll_view);
            if (OPUtils.isSupportCustomFingerprint()) {
                this.mOPFingerPrintEnrollView.setTitleView(this.mStartMessage);
                this.mRepeatMessage.setVisibility(0);
                this.mOPFingerPrintEnrollView.setSubTitleView(this.mRepeatMessage);
            } else {
                this.mOPFingerPrintEnrollView.setTitleView(getHeadView());
                this.mOPFingerPrintEnrollView.setSubTitleView(this.mStartMessage);
            }
            this.mOPFingerPrintEnrollView.hideHeaderView();
            this.mOPFingerPrintEnrollView.setOnOPFingerComfirmListener(this);
            if (OPUtils.isSupportCustomFingerprint()) {
                this.mOPFingerPrintEnrollView.setEnrollAnimVisibility(false);
            }
        }
        this.mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(this, 17563663);
        this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(this, 17563662);
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (!this.mIsEnrollPaused && !isFinishing()) {
            changeEnrollStateByFocusChanged(z);
        }
    }

    private void changeEnrollStateByFocusChanged(boolean z) {
        IFingerprintService asInterface = IFingerprintService.Stub.asInterface(ServiceManager.getService("fingerprint"));
        if (asInterface != null) {
            try {
                asInterface.updateStatus(z ? 8 : 9);
                Log.w("FingerprintEnrollEnrolling", "changeEnrollStateByFocusChanged ");
            } catch (RemoteException e) {
                Log.w("FingerprintEnrollEnrolling", "updateStatus , " + e);
            }
        }
    }

    private void showScreenNavBar(boolean z) {
        Settings.System.putInt(getContentResolver(), "buttons_show_on_screen_navkeys", !z ? 1 : 0);
    }

    public void enableAllKey() {
        if (this.mLaunchingFinish) {
            setFingerprintEnrolling(true);
        } else {
            setFingerprintEnrolling(false);
        }
        if (this.mScreenNavBarEnabled) {
            showScreenNavBar(false);
        }
    }

    public void disableAllKey() {
        this.mLaunchingFinish = false;
        setFingerprintEnrolling(true);
        if (this.mScreenNavBarEnabled) {
            showScreenNavBar(true);
        }
    }

    private void disableRecentAndHomeKey() {
        if (this.mStatusBarManager != null) {
            Log.d("FingerprintEnrollEnrolling", "disableRecentAndHomeKey:");
            this.mStatusBarManager.disable(18874368);
        }
    }

    private void enableRecentAndHomeKey() {
        Log.d("FingerprintEnrollEnrolling", "enableRecentAndHomeKey:");
        StatusBarManager statusBarManager = this.mStatusBarManager;
        if (statusBarManager != null) {
            statusBarManager.disable(0);
        }
    }

    private void disableRecentKey() {
        StatusBarManager statusBarManager = this.mStatusBarManager;
        if (statusBarManager != null) {
            statusBarManager.disable(16777216);
        }
    }

    private void enableRecentKey() {
        StatusBarManager statusBarManager = this.mStatusBarManager;
        if (statusBarManager != null) {
            statusBarManager.disable(0);
        }
    }

    private void setFingerprintEnrolling(boolean z) {
        if (!OPUtils.isSurportBackFingerprint(this)) {
            boolean z2 = false;
            if (Settings.System.getInt(getApplicationContext().getContentResolver(), "oem_acc_fingerprint_enrolling", 0) != 0) {
                z2 = true;
            }
            if (z != z2) {
                Settings.System.putInt(getApplicationContext().getContentResolver(), "oem_acc_fingerprint_enrolling", z ? 1 : 0);
            }
        }
    }

    @Override // com.oneplus.settings.opfinger.OnOPFingerComfirmListener
    public void onOPFingerComfirmClick() {
        this.mConfirmCompleted = true;
        setResult(1);
        finish();
    }

    private void acquireWakeLock() {
        if (this.mWakeLock == null) {
            PowerManager.WakeLock newWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(26, "FingerprintEnrollEnrolling");
            this.mWakeLock = newWakeLock;
            newWakeLock.acquire();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void releaseWakeLock() {
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null && wakeLock.isHeld()) {
            this.mWakeLock.release();
            this.mWakeLock = null;
        }
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onMultiWindowModeChanged(boolean z) {
        super.onMultiWindowModeChanged(z);
        if (z) {
            this.mHandler.removeCallbacks(this.callFingerprintServiceRunnable);
            BiometricEnrollSidecar biometricEnrollSidecar = this.mSidecar;
            if (biometricEnrollSidecar != null) {
                biometricEnrollSidecar.cancelEnrollment();
                FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
                beginTransaction.remove(this.mSidecar);
                beginTransaction.commitAllowingStateLoss();
            }
            enableRecentAndHomeKey();
            finish();
        }
    }

    private void fadeIn(final View view) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);
        ofFloat.setDuration(300L);
        ofFloat.addListener(new Animator.AnimatorListener() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.AnonymousClass9 */

            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
                if (FingerprintEnrollEnrolling.this.mEdgeEnrollAnimView != null && FingerprintEnrollEnrolling.this.mEdgeEnrollAnimView.equals(view)) {
                    FingerprintEnrollEnrolling.this.mEdgeEnrollAnimView.setVisibility(0);
                }
            }
        });
        ofFloat.start();
    }

    private void fadeOut(final View view) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f);
        ofFloat.setDuration(300L);
        ofFloat.addListener(new Animator.AnimatorListener() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.AnonymousClass10 */

            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                if (FingerprintEnrollEnrolling.this.mEdgeEnrollAnimView != null && FingerprintEnrollEnrolling.this.mEdgeEnrollAnimView.equals(view)) {
                    FingerprintEnrollEnrolling.this.mEdgeEnrollAnimView.setVisibility(4);
                }
            }
        });
        ofFloat.start();
    }

    private void pauseEnroll() {
        if (OPUtils.isSupportDynamicEnrollAnimation()) {
            this.mNextButton.setVisibility(0);
            fadeOut(this.mOPFingerPrintDynamicEnrollView);
            fadeIn(this.mEdgeEnrollAnimView);
            this.mEdgeEnrollAnimView.playAnimation();
        } else if (OPUtils.isSupportCustomFingerprint()) {
            this.mNextButton.setVisibility(0);
            fadeOut(this.mOPFingerPrintEnrollView);
            fadeIn(this.mEdgeEnrollAnimView);
            this.mEdgeEnrollAnimView.playAnimation();
        }
        this.mIsEnrollPaused = true;
        IFingerprintService asInterface = IFingerprintService.Stub.asInterface(ServiceManager.getService("fingerprint"));
        if (asInterface != null) {
            try {
                asInterface.updateStatus(9);
                this.mEnrollState = 9;
                Log.w("FingerprintEnrollEnrolling", "pauseEnroll ");
            } catch (RemoteException e) {
                Log.w("FingerprintEnrollEnrolling", "updateStatus , " + e);
            }
        }
    }

    private void showContinueView() {
        this.mHandler.postDelayed(new Runnable() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling.AnonymousClass11 */

            public void run() {
                if (OPUtils.isSupportDynamicEnrollAnimation()) {
                    FingerprintEnrollEnrolling.this.mOPFingerPrintDynamicEnrollView.showContinueView();
                } else {
                    FingerprintEnrollEnrolling.this.mOPFingerPrintEnrollView.showContinueView();
                }
            }
        }, 300);
    }

    private void resumeEnroll(boolean z, int i) {
        if (OPUtils.isSupportDynamicEnrollAnimation() && z) {
            this.mNextButton.setVisibility(4);
            fadeIn(this.mOPFingerPrintDynamicEnrollView);
            fadeOut(this.mEdgeEnrollAnimView);
            showContinueView();
            this.mOPFingerPrintDynamicEnrollView.setEdgeVisible(true);
            this.mEdgeEnrollAnimView.pauseAnimation();
        } else if (OPUtils.isSupportCustomFingerprint() && z) {
            this.mNextButton.setVisibility(4);
            fadeIn(this.mOPFingerPrintEnrollView);
            fadeOut(this.mEdgeEnrollAnimView);
            showContinueView();
            this.mOPFingerPrintEnrollView.setEdgeVisible(true);
            this.mEdgeEnrollAnimView.pauseAnimation();
        }
        this.mIsEnrollPaused = false;
        IFingerprintService asInterface = IFingerprintService.Stub.asInterface(ServiceManager.getService("fingerprint"));
        if (asInterface != null && this.mEnrollState == 9) {
            try {
                asInterface.updateStatus(i);
                this.mEnrollState = i;
                Log.w("FingerprintEnrollEnrolling", "resumeEnroll ");
            } catch (RemoteException e) {
                Log.w("FingerprintEnrollEnrolling", "updateStatus , " + e);
            }
        }
    }

    private void updateProgress(boolean z, int i, int i2) {
        System.out.println("oneplus--updateProgress-enrollSteps:" + i + " enrollStepsRemaining:" + i2);
        if (i != -1) {
            int i3 = i + 1;
            int i4 = ((i3 - i2) * 100) / i3;
            if (i4 <= this.mCurrentProgress || i2 > i) {
                this.mCurrentProgress = i4;
                if (OPUtils.isSupportDynamicEnrollAnimation()) {
                    this.mOPFingerPrintDynamicEnrollView.doRecognition(this.mEnrollSuccessCount + 1, i2, i4, false);
                } else {
                    this.mOPFingerPrintEnrollView.doRecognition(this.mEnrollSuccessCount + 1, i4, false);
                }
            } else {
                clearError();
                this.mCurrentProgress = i4;
                this.mEnrollSuccessCount++;
                if (!OPUtils.isSupportDynamicEnrollAnimation()) {
                    this.mOPFingerPrintEnrollView.setEnrollAnimVisibility(true);
                } else if (this.mEnrollSuccessCount <= OPUtils.getFingerprintScaleAnimStep(this)) {
                    this.mOPFingerPrintDynamicEnrollView.setEnrollAnimVisibility(true);
                } else {
                    this.mOPFingerPrintDynamicEnrollView.setEnrollAnimVisibility(false);
                }
                if (OPUtils.isSupportDynamicEnrollAnimation()) {
                    this.mOPFingerPrintDynamicEnrollView.doRecognition(this.mEnrollSuccessCount, i2, i4, true);
                } else {
                    this.mOPFingerPrintEnrollView.doRecognition(this.mEnrollSuccessCount, i4, true);
                }
            }
            if (this.mEnrollSuccessCount == OPUtils.getFingerprintScaleAnimStep(this)) {
                pauseEnroll();
                if (OPUtils.isSupportDynamicEnrollAnimation()) {
                    this.mOPFingerPrintDynamicEnrollView.setTipsContinueContent();
                } else {
                    this.mOPFingerPrintEnrollView.setTipsContinueContent();
                }
                if (!OPUtils.isSupportCustomFingerprint()) {
                    showContinueView();
                }
            }
            this.mHandler.removeCallbacks(this.mTouchAgainRunnable);
            if (i4 >= 100) {
                this.mHasInputCompleted = true;
                this.mHandler.removeCallbacks(this.mTouchAgainRunnable);
                this.mHandler.postDelayed(this.mDelayedShowLottieRunnable, 300);
                this.mHandler.postDelayed(this.mDelayedFinishRunnable, 750);
            }
            PowerManager.WakeLock wakeLock = this.mWakeLock;
            if (wakeLock == null || (wakeLock != null && !wakeLock.isHeld())) {
                acquireWakeLock();
            }
            this.mHandler.removeCallbacks(this.mWakeLockUseRunnable);
            this.mHandler.postDelayed(this.mWakeLockUseRunnable, (long) Settings.System.getInt(getContentResolver(), "screen_off_timeout", 0));
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling
    public void launchFinish(byte[] bArr) {
        this.mLaunchingFinish = true;
        Intent finishIntent = getFinishIntent();
        finishIntent.addFlags(637534208);
        finishIntent.putExtra("hw_auth_token", bArr);
        int i = this.mUserId;
        if (i != -10000) {
            finishIntent.putExtra("android.intent.extra.USER_ID", i);
        }
        finishIntent.putExtra("needJumpToFingerprintSettings", this.mNeedJumpToFingerprintSettings);
        startActivity(finishIntent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void animateErrorText(TextView textView) {
        if (this.mErrorAnimator == null) {
            this.mErrorAnimator = AnimatorInflater.loadAnimator(this, C0002R$anim.shake_anim);
        }
        this.mErrorAnimator.cancel();
        this.mErrorAnimator.setTarget(textView);
        this.mErrorAnimator.start();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void delayCallFingerprintService() {
        this.mHandler.removeCallbacks(this.callFingerprintServiceRunnable);
        this.mHandler.postDelayed(this.callFingerprintServiceRunnable, 150);
    }

    private TextView getHeadView() {
        if (OPUtils.isSurportBackFingerprint(this)) {
            return (TextView) findViewById(C0010R$id.suc_layout_title);
        }
        TextView headerTextView = getLayout().getHeaderTextView();
        if (OPUtils.isSupportCustomFingerprint()) {
            headerTextView.setTextColor(getResources().getColor(C0006R$color.oneplus_contorl_text_color_primary_dark));
        }
        return headerTextView;
    }

    public void onClick(View view) {
        if (view.getId() == C0010R$id.skip_button) {
            onSkipButtonClick(view);
        } else if (view.getId() == C0010R$id.continue_enroll_button) {
            resumeEnroll(true, 8);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public void setHeaderText(int i) {
        if (OPUtils.isSurportBackFingerprint(this)) {
            ((TextView) findViewById(C0010R$id.suc_layout_title)).setText(i);
        } else {
            super.setHeaderText(i);
        }
    }

    private void adjustTitleSize() {
        if (OPUtils.isLargerFontSize(this) && OPUtils.isLargerScreenZoom(this)) {
            if (OPUtils.isSurportBackFingerprint(this)) {
                ((TextView) findViewById(C0010R$id.suc_layout_title)).setTextSize(18.0f);
                ((TextView) findViewById(C0010R$id.start_message)).setTextSize(14.0f);
                ((TextView) findViewById(C0010R$id.repeat_message)).setTextSize(14.0f);
                View findViewById = findViewById(C0010R$id.sud_layout_icon);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) findViewById.getLayoutParams();
                layoutParams.topMargin /= 2;
                findViewById.setLayoutParams(layoutParams);
                View findViewById2 = findViewById(C0010R$id.fingerprint_enroll_enrolling_content);
                if (findViewById2 != null) {
                    LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) findViewById2.getLayoutParams();
                    layoutParams2.topMargin /= 2;
                    findViewById2.setLayoutParams(layoutParams2);
                }
            } else {
                getLayout().getHeaderTextView().setTextSize(18.0f);
                this.mStartMessage.setTextSize(18.0f);
                this.mRepeatMessage.setTextSize(14.0f);
            }
            if (OPUtils.isSupportCustomFingerprint()) {
                RelativeLayout.LayoutParams layoutParams3 = (RelativeLayout.LayoutParams) this.mStartMessage.getLayoutParams();
                layoutParams3.topMargin /= 2;
                this.mStartMessage.setLayoutParams(layoutParams3);
            }
        }
    }
}
