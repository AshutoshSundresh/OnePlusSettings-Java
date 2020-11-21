package com.android.settings.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.RestrictionsManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.enterprise.ActionDisabledByAdminDialogHelper;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.material.emptyview.EmptyPageView;

public abstract class RestrictedDashboardFragment extends DashboardFragment {
    private AlertDialog mActionDisabledDialog;
    private boolean mChallengeRequested;
    private boolean mChallengeSucceeded;
    private EmptyPageView mEmptyTextView;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    private boolean mIsAdminUser;
    private boolean mOnlyAvailableForAdmins = false;
    private final String mRestrictionKey;
    private RestrictionsManager mRestrictionsManager;
    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        /* class com.android.settings.dashboard.RestrictedDashboardFragment.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            if (!RestrictedDashboardFragment.this.mChallengeRequested) {
                RestrictedDashboardFragment.this.mChallengeSucceeded = false;
                RestrictedDashboardFragment.this.mChallengeRequested = false;
            }
        }
    };
    private UserManager mUserManager;

    public RestrictedDashboardFragment(String str) {
        this.mRestrictionKey = str;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mRestrictionsManager = (RestrictionsManager) getSystemService("restrictions");
        UserManager userManager = (UserManager) getSystemService("user");
        this.mUserManager = userManager;
        this.mIsAdminUser = userManager.isAdminUser();
        if (bundle != null) {
            this.mChallengeSucceeded = bundle.getBoolean("chsc", false);
            this.mChallengeRequested = bundle.getBoolean("chrq", false);
        }
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        getActivity().registerReceiver(this.mScreenOffReceiver, intentFilter);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mEmptyTextView = initEmptyTextView();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (getActivity().isChangingConfigurations()) {
            bundle.putBoolean("chrq", this.mChallengeRequested);
            bundle.putBoolean("chsc", this.mChallengeSucceeded);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        if (shouldBeProviderProtected(this.mRestrictionKey)) {
            ensurePin();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        getActivity().unregisterReceiver(this.mScreenOffReceiver);
        super.onDestroy();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i != 12309) {
            super.onActivityResult(i, i2, intent);
        } else if (i2 == -1) {
            this.mChallengeSucceeded = true;
            this.mChallengeRequested = false;
        } else {
            this.mChallengeSucceeded = false;
        }
    }

    private void ensurePin() {
        Intent createLocalApprovalIntent;
        if (!this.mChallengeSucceeded && !this.mChallengeRequested && this.mRestrictionsManager.hasRestrictionsProvider() && (createLocalApprovalIntent = this.mRestrictionsManager.createLocalApprovalIntent()) != null) {
            this.mChallengeRequested = true;
            this.mChallengeSucceeded = false;
            PersistableBundle persistableBundle = new PersistableBundle();
            persistableBundle.putString("android.request.mesg", getResources().getString(C0017R$string.restr_pin_enter_admin_pin));
            createLocalApprovalIntent.putExtra("android.content.extra.REQUEST_BUNDLE", persistableBundle);
            startActivityForResult(createLocalApprovalIntent, 12309);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isRestrictedAndNotProviderProtected() {
        String str = this.mRestrictionKey;
        if (str == null || "restrict_if_overridable".equals(str) || !this.mUserManager.hasUserRestriction(this.mRestrictionKey) || this.mRestrictionsManager.hasRestrictionsProvider()) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean hasChallengeSucceeded() {
        return (this.mChallengeRequested && this.mChallengeSucceeded) || !this.mChallengeRequested;
    }

    /* access modifiers changed from: protected */
    public boolean shouldBeProviderProtected(String str) {
        if (str == null) {
            return false;
        }
        return ("restrict_if_overridable".equals(str) || this.mUserManager.hasUserRestriction(this.mRestrictionKey)) && this.mRestrictionsManager.hasRestrictionsProvider();
    }

    /* access modifiers changed from: protected */
    public EmptyPageView initEmptyTextView() {
        EmptyPageView emptyPageView = (EmptyPageView) getActivity().findViewById(16908292);
        emptyPageView.getEmptyImageView().setImageResource(C0008R$drawable.op_empty);
        return emptyPageView;
    }

    public RestrictedLockUtils.EnforcedAdmin getRestrictionEnforcedAdmin() {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getActivity(), this.mRestrictionKey, UserHandle.myUserId());
        this.mEnforcedAdmin = checkIfRestrictionEnforced;
        if (checkIfRestrictionEnforced != null && checkIfRestrictionEnforced.user == null) {
            checkIfRestrictionEnforced.user = UserHandle.of(UserHandle.myUserId());
        }
        return this.mEnforcedAdmin;
    }

    public TextView getEmptyTextView() {
        return this.mEmptyTextView.getEmptyTextView();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.SettingsPreferenceFragment
    public void onDataSetChanged() {
        AlertDialog alertDialog;
        highlightPreferenceIfNeeded();
        if (!isUiRestrictedByOnlyAdmin() || ((alertDialog = this.mActionDisabledDialog) != null && alertDialog.isShowing())) {
            EmptyPageView emptyPageView = this.mEmptyTextView;
            if (emptyPageView != null) {
                setEmptyView(emptyPageView);
            }
        } else {
            AlertDialog.Builder prepareDialogBuilder = new ActionDisabledByAdminDialogHelper(getActivity()).prepareDialogBuilder(this.mRestrictionKey, getRestrictionEnforcedAdmin());
            prepareDialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                /* class com.android.settings.dashboard.$$Lambda$RestrictedDashboardFragment$xeipMNP1JUFbhaWoStBNgM1y67g */

                public final void onDismiss(DialogInterface dialogInterface) {
                    RestrictedDashboardFragment.this.lambda$onDataSetChanged$0$RestrictedDashboardFragment(dialogInterface);
                }
            });
            this.mActionDisabledDialog = prepareDialogBuilder.show();
            setEmptyView(new View(getContext()));
        }
        super.onDataSetChanged();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onDataSetChanged$0 */
    public /* synthetic */ void lambda$onDataSetChanged$0$RestrictedDashboardFragment(DialogInterface dialogInterface) {
        getActivity().finish();
    }

    public void setIfOnlyAvailableForAdmins(boolean z) {
        this.mOnlyAvailableForAdmins = z;
    }

    /* access modifiers changed from: protected */
    public boolean isUiRestricted() {
        return isRestrictedAndNotProviderProtected() || !hasChallengeSucceeded() || (!this.mIsAdminUser && this.mOnlyAvailableForAdmins);
    }

    /* access modifiers changed from: protected */
    public boolean isUiRestrictedByOnlyAdmin() {
        return isUiRestricted() && !this.mUserManager.hasBaseUserRestriction(this.mRestrictionKey, UserHandle.of(UserHandle.myUserId())) && (this.mIsAdminUser || !this.mOnlyAvailableForAdmins);
    }
}
