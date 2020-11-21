package com.android.settings.network.telephony;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import com.android.settings.C0005R$bool;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.security.ConfirmSimDeletionPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.dpp.WifiDppUtils;
import java.util.Iterator;

public class DeleteSimProfilePreferenceController extends BasePreferenceController {
    private boolean mConfirmationDefaultOn;
    private Fragment mParentFragment;
    private int mRequestCode;
    private SubscriptionInfo mSubscriptionInfo;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DeleteSimProfilePreferenceController(Context context, String str) {
        super(context, str);
        this.mConfirmationDefaultOn = context.getResources().getBoolean(C0005R$bool.config_sim_deletion_confirmation_default_on);
    }

    public void init(int i, Fragment fragment, int i2) {
        this.mParentFragment = fragment;
        Iterator<SubscriptionInfo> it = SubscriptionUtil.getAvailableSubscriptions(this.mContext).iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            SubscriptionInfo next = it.next();
            if (next.getSubscriptionId() == i && next.isEmbedded()) {
                this.mSubscriptionInfo = next;
                break;
            }
        }
        this.mRequestCode = i2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        boolean z = false;
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }
        if (Settings.Global.getInt(this.mContext.getContentResolver(), ConfirmSimDeletionPreferenceController.KEY_CONFIRM_SIM_DELETION, this.mConfirmationDefaultOn ? 1 : 0) == 1) {
            z = true;
        }
        if (z) {
            WifiDppUtils.showLockScreen(this.mContext, new Runnable() {
                /* class com.android.settings.network.telephony.$$Lambda$DeleteSimProfilePreferenceController$8UWme3ssAKfh3UYXlgZtSK20hjk */

                public final void run() {
                    DeleteSimProfilePreferenceController.this.lambda$handlePreferenceTreeClick$0$DeleteSimProfilePreferenceController();
                }
            });
        } else {
            lambda$handlePreferenceTreeClick$0();
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: deleteSim */
    public void lambda$handlePreferenceTreeClick$0() {
        Intent intent = new Intent("android.telephony.euicc.action.DELETE_SUBSCRIPTION_PRIVILEGED");
        intent.putExtra("android.telephony.euicc.extra.SUBSCRIPTION_ID", this.mSubscriptionInfo.getSubscriptionId());
        this.mParentFragment.startActivityForResult(intent, this.mRequestCode);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mSubscriptionInfo != null ? 0 : 2;
    }
}
