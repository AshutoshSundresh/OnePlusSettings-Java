package com.android.settings.wifi;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C0012R$layout;
import com.android.settings.core.InstrumentedFragment;
import com.android.settingslib.wifi.AccessPoint;

public class ConfigureAccessPointFragment extends InstrumentedFragment implements WifiConfigUiBase {
    private AccessPoint mAccessPoint;
    private Button mCancelBtn;
    private Button mSubmitBtn;
    private WifiConfigController mUiController;

    @Override // com.android.settings.wifi.WifiConfigUiBase
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

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setForgetButton(CharSequence charSequence) {
    }

    @Override // com.android.settings.core.InstrumentedFragment, com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mAccessPoint = new AccessPoint(context, getArguments());
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
            /* class com.android.settings.wifi.$$Lambda$ConfigureAccessPointFragment$tVebcdGMZwnNzpTtH8NPbSC5ozM */

            public final void onClick(View view) {
                ConfigureAccessPointFragment.this.lambda$onCreateView$0$ConfigureAccessPointFragment(view);
            }
        });
        this.mCancelBtn.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.wifi.$$Lambda$ConfigureAccessPointFragment$ngdmwq8gfJ5i8l795Er7dD3IbVI */

            public final void onClick(View view) {
                ConfigureAccessPointFragment.this.lambda$onCreateView$1$ConfigureAccessPointFragment(view);
            }
        });
        this.mUiController = new WifiConfigController((WifiConfigUiBase) this, inflate, this.mAccessPoint, getMode(), false);
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
    public /* synthetic */ void lambda$onCreateView$0$ConfigureAccessPointFragment(View view) {
        handleSubmitAction();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateView$1 */
    public /* synthetic */ void lambda$onCreateView$1$ConfigureAccessPointFragment(View view) {
        handleCancelAction();
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewStateRestored(Bundle bundle) {
        super.onViewStateRestored(bundle);
        this.mUiController.updatePassword();
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void dispatchSubmit() {
        handleSubmitAction();
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setTitle(int i) {
        getActivity().setTitle(i);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setTitle(CharSequence charSequence) {
        getActivity().setTitle(charSequence);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setSubmitButton(CharSequence charSequence) {
        this.mSubmitBtn.setText(charSequence);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
    public void setCancelButton(CharSequence charSequence) {
        this.mCancelBtn.setText(charSequence);
    }

    @Override // com.android.settings.wifi.WifiConfigUiBase
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
        FragmentActivity activity = getActivity();
        activity.setResult(0);
        activity.finish();
    }
}
