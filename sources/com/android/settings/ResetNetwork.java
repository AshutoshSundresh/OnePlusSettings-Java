package com.android.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.euicc.EuiccManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.enterprise.ActionDisabledByAdminDialogHelper;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import java.util.ArrayList;
import java.util.List;

public class ResetNetwork extends InstrumentedFragment {
    private View mContentView;
    CheckBox mEsimCheckbox;
    View mEsimContainer;
    private Button mInitiateButton;
    private final View.OnClickListener mInitiateListener = new View.OnClickListener() {
        /* class com.android.settings.ResetNetwork.AnonymousClass1 */

        public void onClick(View view) {
            if (!ResetNetwork.this.runKeyguardConfirmation(55)) {
                ResetNetwork.this.showFinalConfirmation();
            }
        }
    };
    private Spinner mSubscriptionSpinner;
    private List<SubscriptionInfo> mSubscriptions;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 83;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(C0017R$string.reset_network_title);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean runKeyguardConfirmation(int i) {
        return new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(i, getActivity().getResources().getText(C0017R$string.reset_network_title));
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 55) {
            if (i2 == -1) {
                showFinalConfirmation();
            } else {
                establishInitialState();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void showFinalConfirmation() {
        Bundle bundle = new Bundle();
        List<SubscriptionInfo> list = this.mSubscriptions;
        if (list != null && list.size() > 0) {
            bundle.putInt("android.telephony.extra.SUBSCRIPTION_INDEX", this.mSubscriptions.get(this.mSubscriptionSpinner.getSelectedItemPosition()).getSubscriptionId());
        }
        bundle.putBoolean("erase_esim", this.mEsimContainer.getVisibility() == 0 && this.mEsimCheckbox.isChecked());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setDestination(ResetNetworkConfirm.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setTitleRes(C0017R$string.reset_network_confirm_title);
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.launch();
    }

    private void establishInitialState() {
        this.mSubscriptionSpinner = (Spinner) this.mContentView.findViewById(C0010R$id.reset_network_subscription);
        this.mEsimContainer = this.mContentView.findViewById(C0010R$id.erase_esim_container);
        this.mEsimCheckbox = (CheckBox) this.mContentView.findViewById(C0010R$id.erase_esim);
        List<SubscriptionInfo> activeSubscriptionInfoList = SubscriptionManager.from(getActivity()).getActiveSubscriptionInfoList();
        this.mSubscriptions = activeSubscriptionInfoList;
        if (activeSubscriptionInfoList == null || activeSubscriptionInfoList.size() <= 0) {
            this.mSubscriptionSpinner.setVisibility(4);
        } else {
            int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
            if (!SubscriptionManager.isUsableSubscriptionId(defaultDataSubscriptionId)) {
                defaultDataSubscriptionId = SubscriptionManager.getDefaultVoiceSubscriptionId();
            }
            if (!SubscriptionManager.isUsableSubscriptionId(defaultDataSubscriptionId)) {
                defaultDataSubscriptionId = SubscriptionManager.getDefaultSmsSubscriptionId();
            }
            if (!SubscriptionManager.isUsableSubscriptionId(defaultDataSubscriptionId)) {
                defaultDataSubscriptionId = SubscriptionManager.getDefaultSubscriptionId();
            }
            this.mSubscriptions.size();
            ArrayList arrayList = new ArrayList();
            int i = 0;
            for (SubscriptionInfo subscriptionInfo : this.mSubscriptions) {
                if (subscriptionInfo.getSubscriptionId() == defaultDataSubscriptionId) {
                    i = arrayList.size();
                }
                String charSequence = subscriptionInfo.getDisplayName().toString();
                if (TextUtils.isEmpty(charSequence)) {
                    charSequence = subscriptionInfo.getNumber();
                }
                if (TextUtils.isEmpty(charSequence)) {
                    charSequence = subscriptionInfo.getCarrierName().toString();
                }
                if (TextUtils.isEmpty(charSequence)) {
                    charSequence = String.format("MCC:%s MNC:%s Slot:%s Id:%s", Integer.valueOf(subscriptionInfo.getMcc()), Integer.valueOf(subscriptionInfo.getMnc()), Integer.valueOf(subscriptionInfo.getSimSlotIndex()), Integer.valueOf(subscriptionInfo.getSubscriptionId()));
                }
                arrayList.add(charSequence);
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), 17367048, arrayList);
            arrayAdapter.setDropDownViewResource(17367049);
            this.mSubscriptionSpinner.setAdapter((SpinnerAdapter) arrayAdapter);
            this.mSubscriptionSpinner.setSelection(i);
            if (this.mSubscriptions.size() > 1) {
                this.mSubscriptionSpinner.setVisibility(0);
            } else {
                this.mSubscriptionSpinner.setVisibility(4);
            }
        }
        Button button = (Button) this.mContentView.findViewById(C0010R$id.initiate_reset_network);
        this.mInitiateButton = button;
        button.setOnClickListener(this.mInitiateListener);
        if (showEuiccSettings(getContext())) {
            this.mEsimContainer.setVisibility(0);
            this.mEsimContainer.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.ResetNetwork.AnonymousClass2 */

                public void onClick(View view) {
                    ResetNetwork.this.mEsimCheckbox.toggle();
                }
            });
            return;
        }
        this.mEsimCheckbox.setChecked(false);
    }

    private boolean showEuiccSettings(Context context) {
        if (!((EuiccManager) context.getSystemService("euicc")).isEnabled()) {
            return false;
        }
        if (Settings.Global.getInt(context.getContentResolver(), "euicc_provisioned", 0) != 0 || DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context)) {
            return true;
        }
        return false;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        UserManager userManager = UserManager.get(getActivity());
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getActivity(), "no_network_reset", UserHandle.myUserId());
        if (!userManager.isAdminUser() || RestrictedLockUtilsInternal.hasBaseUserRestriction(getActivity(), "no_network_reset", UserHandle.myUserId())) {
            return layoutInflater.inflate(C0012R$layout.network_reset_disallowed_screen, (ViewGroup) null);
        }
        if (checkIfRestrictionEnforced != null) {
            AlertDialog.Builder prepareDialogBuilder = new ActionDisabledByAdminDialogHelper(getActivity()).prepareDialogBuilder("no_network_reset", checkIfRestrictionEnforced);
            prepareDialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                /* class com.android.settings.$$Lambda$ResetNetwork$sNSFVrhYYO7NxbKY35cdb4I6sYI */

                public final void onDismiss(DialogInterface dialogInterface) {
                    ResetNetwork.this.lambda$onCreateView$0$ResetNetwork(dialogInterface);
                }
            });
            prepareDialogBuilder.show();
            return new View(getContext());
        }
        this.mContentView = layoutInflater.inflate(C0012R$layout.reset_network, (ViewGroup) null);
        establishInitialState();
        return this.mContentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateView$0 */
    public /* synthetic */ void lambda$onCreateView$0$ResetNetwork(DialogInterface dialogInterface) {
        getActivity().finish();
    }
}
