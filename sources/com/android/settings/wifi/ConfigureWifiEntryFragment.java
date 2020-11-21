package com.android.settings.wifi;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C0012R$layout;
import com.android.settings.core.InstrumentedFragment;
import com.android.wifitrackerlib.NetworkDetailsTracker;
import com.android.wifitrackerlib.WifiEntry;

public class ConfigureWifiEntryFragment extends InstrumentedFragment implements WifiConfigUiBase2 {
    private Button mCancelBtn;
    NetworkDetailsTracker mNetworkDetailsTracker;
    private Button mSubmitBtn;
    private WifiConfigController2 mUiController;
    private WifiEntry mWifiEntry;
    private HandlerThread mWorkerThread;

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public Button getForgetButton() {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1800;
    }

    public int getMode() {
        return 1;
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public void setForgetButton(CharSequence charSequence) {
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        setupNetworkDetailsTracker();
        this.mWifiEntry = this.mNetworkDetailsTracker.getWifiEntry();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        HandlerThread handlerThread = this.mWorkerThread;
        if (handlerThread != null) {
            handlerThread.quit();
        }
        super.onDestroy();
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0012R$layout.wifi_add_network_view, viewGroup, false);
        Button button = (Button) inflate.findViewById(16908315);
        if (button != null) {
            button.setVisibility(8);
        }
        this.mSubmitBtn = (Button) inflate.findViewById(16908313);
        this.mCancelBtn = (Button) inflate.findViewById(16908314);
        this.mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.$$Lambda$ConfigureWifiEntryFragment$99la8ni_DFcaWnm0d8dOwxhSVsU */

            public final void onClick(View view) {
                ConfigureWifiEntryFragment.this.lambda$onCreateView$0$ConfigureWifiEntryFragment(view);
            }
        });
        this.mCancelBtn.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.$$Lambda$ConfigureWifiEntryFragment$b6nv0bNYgGE0ctJxMVXzZ4xEcg */

            public final void onClick(View view) {
                ConfigureWifiEntryFragment.this.lambda$onCreateView$1$ConfigureWifiEntryFragment(view);
            }
        });
        this.mUiController = new WifiConfigController2(this, inflate, this.mWifiEntry, getMode());
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        return inflate;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateView$0 */
    public /* synthetic */ void lambda$onCreateView$0$ConfigureWifiEntryFragment(View view) {
        handleSubmitAction();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateView$1 */
    public /* synthetic */ void lambda$onCreateView$1$ConfigureWifiEntryFragment(View view) {
        handleCancelAction();
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewStateRestored(Bundle bundle) {
        super.onViewStateRestored(bundle);
        this.mUiController.updatePassword();
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public void dispatchSubmit() {
        handleSubmitAction();
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public void setTitle(int i) {
        getActivity().setTitle(i);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public void setTitle(CharSequence charSequence) {
        getActivity().setTitle(charSequence);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public void setSubmitButton(CharSequence charSequence) {
        this.mSubmitBtn.setText(charSequence);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public void setCancelButton(CharSequence charSequence) {
        this.mCancelBtn.setText(charSequence);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase2
    public Button getSubmitButton() {
        return this.mSubmitBtn;
    }

    /* access modifiers changed from: package-private */
    public void handleSubmitAction() {
        Intent intent = new Intent();
        FragmentActivity activity = getActivity();
        intent.putExtra("network_config_key", this.mUiController.getConfig());
        activity.setResult(-1, intent);
        activity.finish();
    }

    /* access modifiers changed from: package-private */
    public void handleCancelAction() {
        getActivity().finish();
    }

    /* JADX WARN: Type inference failed for: r8v0, types: [com.android.settings.wifi.ConfigureWifiEntryFragment$1, java.time.Clock] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setupNetworkDetailsTracker() {
        /*
        // Method dump skipped, instructions count: 126
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.ConfigureWifiEntryFragment.setupNetworkDetailsTracker():void");
    }
}
