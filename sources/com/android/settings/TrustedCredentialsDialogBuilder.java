package com.android.settings;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.pm.UserInfo;
import android.net.http.SslCertificate;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import androidx.appcompat.app.AlertDialog;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.TrustedCredentialsDialogBuilder;
import com.android.settings.TrustedCredentialsSettings;
import com.android.settingslib.RestrictedLockUtils;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/* access modifiers changed from: package-private */
public class TrustedCredentialsDialogBuilder extends AlertDialog.Builder {
    private final DialogEventHandler mDialogEventHandler;

    public interface DelegateInterface {
        List<X509Certificate> getX509CertsFromCertHolder(TrustedCredentialsSettings.CertHolder certHolder);

        void removeOrInstallCert(TrustedCredentialsSettings.CertHolder certHolder);

        boolean startConfirmCredentialIfNotConfirmed(int i, IntConsumer intConsumer);
    }

    public TrustedCredentialsDialogBuilder(Activity activity, DelegateInterface delegateInterface) {
        super(activity);
        this.mDialogEventHandler = new DialogEventHandler(activity, delegateInterface);
        initDefaultBuilderParams();
    }

    public TrustedCredentialsDialogBuilder setCertHolder(TrustedCredentialsSettings.CertHolder certHolder) {
        TrustedCredentialsSettings.CertHolder[] certHolderArr;
        if (certHolder == null) {
            certHolderArr = new TrustedCredentialsSettings.CertHolder[0];
        } else {
            certHolderArr = new TrustedCredentialsSettings.CertHolder[]{certHolder};
        }
        setCertHolders(certHolderArr);
        return this;
    }

    public TrustedCredentialsDialogBuilder setCertHolders(TrustedCredentialsSettings.CertHolder[] certHolderArr) {
        this.mDialogEventHandler.setCertHolders(certHolderArr);
        return this;
    }

    @Override // androidx.appcompat.app.AlertDialog.Builder
    public AlertDialog create() {
        AlertDialog create = super.create();
        create.setOnShowListener(this.mDialogEventHandler);
        this.mDialogEventHandler.setDialog(create);
        return create;
    }

    private void initDefaultBuilderParams() {
        setTitle(17041298);
        setView(this.mDialogEventHandler.mRootContainer);
        setPositiveButton(C0017R$string.trusted_credentials_trust_label, (DialogInterface.OnClickListener) null);
        setNegativeButton(17039370, (DialogInterface.OnClickListener) null);
    }

    /* access modifiers changed from: private */
    public static class DialogEventHandler implements DialogInterface.OnShowListener, View.OnClickListener {
        private final Activity mActivity;
        private TrustedCredentialsSettings.CertHolder[] mCertHolders = new TrustedCredentialsSettings.CertHolder[0];
        private int mCurrentCertIndex = -1;
        private View mCurrentCertLayout = null;
        private final DelegateInterface mDelegate;
        private AlertDialog mDialog;
        private final DevicePolicyManager mDpm;
        private boolean mNeedsApproval;
        private Button mNegativeButton;
        private Button mPositiveButton;
        private final LinearLayout mRootContainer;
        private final UserManager mUserManager;

        public DialogEventHandler(Activity activity, DelegateInterface delegateInterface) {
            this.mActivity = activity;
            this.mDpm = (DevicePolicyManager) activity.getSystemService(DevicePolicyManager.class);
            this.mUserManager = (UserManager) activity.getSystemService(UserManager.class);
            this.mDelegate = delegateInterface;
            LinearLayout linearLayout = new LinearLayout(this.mActivity);
            this.mRootContainer = linearLayout;
            linearLayout.setOrientation(1);
        }

        public void setDialog(AlertDialog alertDialog) {
            this.mDialog = alertDialog;
        }

        public void setCertHolders(TrustedCredentialsSettings.CertHolder[] certHolderArr) {
            this.mCertHolders = certHolderArr;
        }

        public void onShow(DialogInterface dialogInterface) {
            nextOrDismiss();
        }

        public void onClick(View view) {
            if (view == this.mPositiveButton) {
                if (this.mNeedsApproval) {
                    onClickTrust();
                } else {
                    onClickOk();
                }
            } else if (view == this.mNegativeButton) {
                onClickEnableOrDisable();
            }
        }

        private void onClickOk() {
            nextOrDismiss();
        }

        private void onClickTrust() {
            TrustedCredentialsSettings.CertHolder currentCertInfo = getCurrentCertInfo();
            if (!this.mDelegate.startConfirmCredentialIfNotConfirmed(currentCertInfo.getUserId(), new IntConsumer() {
                /* class com.android.settings.$$Lambda$TrustedCredentialsDialogBuilder$DialogEventHandler$lT7FQtmXq7wOC1gAhDRR6fZzQ */

                public final void accept(int i) {
                    TrustedCredentialsDialogBuilder.DialogEventHandler.m1lambda$lT7FQtmXq7wOC1gAhDRR6fZzQ(TrustedCredentialsDialogBuilder.DialogEventHandler.this, i);
                }
            })) {
                this.mDpm.approveCaCert(currentCertInfo.getAlias(), currentCertInfo.getUserId(), true);
                nextOrDismiss();
            }
        }

        private void onClickEnableOrDisable() {
            final TrustedCredentialsSettings.CertHolder currentCertInfo = getCurrentCertInfo();
            AnonymousClass1 r1 = new DialogInterface.OnClickListener() {
                /* class com.android.settings.TrustedCredentialsDialogBuilder.DialogEventHandler.AnonymousClass1 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    DialogEventHandler.this.mDelegate.removeOrInstallCert(currentCertInfo);
                    DialogEventHandler.this.nextOrDismiss();
                }
            };
            if (currentCertInfo.isSystemCert()) {
                r1.onClick(null, -1);
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mActivity);
            builder.setMessage(C0017R$string.trusted_credentials_remove_confirmation);
            builder.setPositiveButton(17039379, r1);
            builder.setNegativeButton(17039369, (DialogInterface.OnClickListener) null);
            builder.show();
        }

        /* access modifiers changed from: private */
        public void onCredentialConfirmed(int i) {
            if (this.mDialog.isShowing() && this.mNeedsApproval && getCurrentCertInfo() != null && getCurrentCertInfo().getUserId() == i) {
                onClickTrust();
            }
        }

        private TrustedCredentialsSettings.CertHolder getCurrentCertInfo() {
            int i = this.mCurrentCertIndex;
            TrustedCredentialsSettings.CertHolder[] certHolderArr = this.mCertHolders;
            if (i < certHolderArr.length) {
                return certHolderArr[i];
            }
            return null;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void nextOrDismiss() {
            this.mCurrentCertIndex++;
            while (this.mCurrentCertIndex < this.mCertHolders.length && getCurrentCertInfo() == null) {
                this.mCurrentCertIndex++;
            }
            if (this.mCurrentCertIndex >= this.mCertHolders.length) {
                this.mDialog.dismiss();
                return;
            }
            updateViewContainer();
            updatePositiveButton();
            updateNegativeButton();
        }

        private boolean isUserSecure(int i) {
            LockPatternUtils lockPatternUtils = new LockPatternUtils(this.mActivity);
            if (lockPatternUtils.isSecure(i)) {
                return true;
            }
            UserInfo profileParent = this.mUserManager.getProfileParent(i);
            if (profileParent == null) {
                return false;
            }
            return lockPatternUtils.isSecure(profileParent.id);
        }

        private void updatePositiveButton() {
            TrustedCredentialsSettings.CertHolder currentCertInfo = getCurrentCertInfo();
            boolean z = true;
            this.mNeedsApproval = !currentCertInfo.isSystemCert() && isUserSecure(currentCertInfo.getUserId()) && !this.mDpm.isCaCertApproved(currentCertInfo.getAlias(), currentCertInfo.getUserId());
            if (RestrictedLockUtils.getProfileOrDeviceOwner(this.mActivity, UserHandle.of(currentCertInfo.getUserId())) == null) {
                z = false;
            }
            this.mPositiveButton = updateButton(-1, this.mActivity.getText((z || !this.mNeedsApproval) ? 17039370 : C0017R$string.trusted_credentials_trust_label));
        }

        private void updateNegativeButton() {
            TrustedCredentialsSettings.CertHolder currentCertInfo = getCurrentCertInfo();
            boolean z = !this.mUserManager.hasUserRestriction("no_config_credentials", new UserHandle(currentCertInfo.getUserId()));
            Button updateButton = updateButton(-2, this.mActivity.getText(getButtonLabel(currentCertInfo)));
            this.mNegativeButton = updateButton;
            updateButton.setVisibility(z ? 0 : 8);
        }

        private Button updateButton(int i, CharSequence charSequence) {
            this.mDialog.setButton(i, charSequence, null);
            Button button = this.mDialog.getButton(i);
            button.setText(charSequence);
            button.setOnClickListener(this);
            return button;
        }

        private void updateViewContainer() {
            LinearLayout certLayout = getCertLayout(getCurrentCertInfo());
            if (this.mCurrentCertLayout == null) {
                this.mCurrentCertLayout = certLayout;
                this.mRootContainer.addView(certLayout);
                return;
            }
            animateViewTransition(certLayout);
        }

        private LinearLayout getCertLayout(TrustedCredentialsSettings.CertHolder certHolder) {
            final ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            List<X509Certificate> x509CertsFromCertHolder = this.mDelegate.getX509CertsFromCertHolder(certHolder);
            if (x509CertsFromCertHolder != null) {
                for (X509Certificate x509Certificate : x509CertsFromCertHolder) {
                    SslCertificate sslCertificate = new SslCertificate(x509Certificate);
                    arrayList.add(sslCertificate.inflateCertificateView(this.mActivity));
                    arrayList2.add(sslCertificate.getIssuedTo().getCName());
                }
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(this.mActivity, 17367048, arrayList2);
            arrayAdapter.setDropDownViewResource(17367049);
            Spinner spinner = new Spinner(this.mActivity);
            spinner.setAdapter((SpinnerAdapter) arrayAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(this) {
                /* class com.android.settings.TrustedCredentialsDialogBuilder.DialogEventHandler.AnonymousClass2 */

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                @Override // android.widget.AdapterView.OnItemSelectedListener
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                    int i2 = 0;
                    while (i2 < arrayList.size()) {
                        ((View) arrayList.get(i2)).setVisibility(i2 == i ? 0 : 8);
                        i2++;
                    }
                }
            });
            LinearLayout linearLayout = new LinearLayout(this.mActivity);
            linearLayout.setOrientation(1);
            linearLayout.addView(spinner);
            int i = 0;
            while (i < arrayList.size()) {
                View view = (View) arrayList.get(i);
                view.setVisibility(i == 0 ? 0 : 8);
                linearLayout.addView(view);
                i++;
            }
            return linearLayout;
        }

        private static int getButtonLabel(TrustedCredentialsSettings.CertHolder certHolder) {
            if (!certHolder.isSystemCert()) {
                return C0017R$string.trusted_credentials_remove_label;
            }
            if (certHolder.isDeleted()) {
                return C0017R$string.trusted_credentials_enable_label;
            }
            return C0017R$string.trusted_credentials_disable_label;
        }

        private void animateViewTransition(final View view) {
            animateOldContent(new Runnable() {
                /* class com.android.settings.TrustedCredentialsDialogBuilder.DialogEventHandler.AnonymousClass3 */

                public void run() {
                    DialogEventHandler.this.addAndAnimateNewContent(view);
                }
            });
        }

        private void animateOldContent(Runnable runnable) {
            this.mCurrentCertLayout.animate().alpha(0.0f).setDuration(300).setInterpolator(AnimationUtils.loadInterpolator(this.mActivity, 17563663)).withEndAction(runnable).start();
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void addAndAnimateNewContent(View view) {
            this.mCurrentCertLayout = view;
            this.mRootContainer.removeAllViews();
            this.mRootContainer.addView(view);
            this.mRootContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                /* class com.android.settings.TrustedCredentialsDialogBuilder.DialogEventHandler.AnonymousClass4 */

                public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    DialogEventHandler.this.mRootContainer.removeOnLayoutChangeListener(this);
                    DialogEventHandler.this.mCurrentCertLayout.setTranslationX((float) DialogEventHandler.this.mRootContainer.getWidth());
                    DialogEventHandler.this.mCurrentCertLayout.animate().translationX(0.0f).setInterpolator(AnimationUtils.loadInterpolator(DialogEventHandler.this.mActivity, 17563662)).setDuration(200).start();
                }
            });
        }
    }
}
