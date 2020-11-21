package com.android.settings.sim;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.C0017R$string;
import com.oneplus.settings.BaseAppCompatActivity;
import java.util.List;

public class SimDialogActivity extends BaseAppCompatActivity {
    public static String DIALOG_TYPE_KEY = "dialog_type";
    public static String PREFERRED_SIM = "preferred_sim";
    public static String RESULT_SUB_ID = "result_sub_id";
    private static String TAG = "SimDialogActivity";

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        showOrUpdateDialog();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        showOrUpdateDialog();
    }

    private void showOrUpdateDialog() {
        int intExtra = getIntent().getIntExtra(DIALOG_TYPE_KEY, -1);
        String num = Integer.toString(intExtra);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        SimDialogFragment simDialogFragment = (SimDialogFragment) supportFragmentManager.findFragmentByTag(num);
        if (simDialogFragment == null) {
            createFragment(intExtra).show(supportFragmentManager, num);
        } else {
            simDialogFragment.updateDialog();
        }
    }

    private SimDialogFragment createFragment(int i) {
        if (i == 0) {
            return SimListDialogFragment.newInstance(i, C0017R$string.select_sim_for_data, false);
        }
        if (i == 1) {
            return SimListDialogFragment.newInstance(i, C0017R$string.select_sim_for_calls, true);
        }
        if (i == 2) {
            return SimListDialogFragment.newInstance(i, C0017R$string.select_sim_for_sms, true);
        }
        if (i != 3) {
            if (i == 4) {
                return SimListDialogFragment.newInstance(i, C0017R$string.select_sim_for_sms, false);
            }
            throw new IllegalArgumentException("Invalid dialog type " + i + " sent.");
        } else if (getIntent().hasExtra(PREFERRED_SIM)) {
            return PreferredSimDialogFragment.newInstance();
        } else {
            throw new IllegalArgumentException("Missing required extra " + PREFERRED_SIM);
        }
    }

    public void onSubscriptionSelected(int i, int i2) {
        if (getSupportFragmentManager().findFragmentByTag(Integer.toString(i)) == null) {
            Log.w(TAG, "onSubscriptionSelected ignored because stored fragment was null");
        } else if (i == 0) {
            setDefaultDataSubId(i2);
        } else if (i == 1) {
            setDefaultCallsSubId(i2);
        } else if (i == 2) {
            setDefaultSmsSubId(i2);
        } else if (i == 3) {
            setPreferredSim(i2);
        } else if (i == 4) {
            Intent intent = new Intent();
            intent.putExtra(RESULT_SUB_ID, i2);
            setResult(-1, intent);
        } else {
            throw new IllegalArgumentException("Invalid dialog type " + i + " sent.");
        }
    }

    public void onFragmentDismissed(SimDialogFragment simDialogFragment) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() == 1 && fragments.get(0) == simDialogFragment) {
            finishAndRemoveTask();
        }
    }

    private void setDefaultDataSubId(int i) {
        TelephonyManager createForSubscriptionId = ((TelephonyManager) getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        ((SubscriptionManager) getSystemService(SubscriptionManager.class)).setDefaultDataSubId(i);
        createForSubscriptionId.setDataEnabled(true);
        Toast.makeText(this, C0017R$string.data_switch_started, 1).show();
    }

    private void setDefaultCallsSubId(int i) {
        ((TelecomManager) getSystemService(TelecomManager.class)).setUserSelectedOutgoingPhoneAccount(subscriptionIdToPhoneAccountHandle(i));
    }

    private void setDefaultSmsSubId(int i) {
        ((SubscriptionManager) getSystemService(SubscriptionManager.class)).setDefaultSmsSubId(i);
    }

    private void setPreferredSim(int i) {
        setDefaultDataSubId(i);
        setDefaultSmsSubId(i);
        setDefaultCallsSubId(i);
    }

    private PhoneAccountHandle subscriptionIdToPhoneAccountHandle(int i) {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TelephonyManager.class);
        for (PhoneAccountHandle phoneAccountHandle : ((TelecomManager) getSystemService(TelecomManager.class)).getCallCapablePhoneAccounts()) {
            if (i == telephonyManager.getSubscriptionId(phoneAccountHandle)) {
                return phoneAccountHandle;
            }
        }
        return null;
    }
}
