package com.android.settings.applications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.password.ChooseLockSettingsHelper;

public class ConvertToFbe extends InstrumentedFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 402;
    }

    private boolean runKeyguardConfirmation(int i) {
        return new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(i, getActivity().getResources().getText(C0017R$string.convert_to_file_encryption));
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(C0017R$string.convert_to_file_encryption);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0012R$layout.convert_fbe, (ViewGroup) null);
        ((Button) inflate.findViewById(C0010R$id.button_convert_fbe)).setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.applications.$$Lambda$ConvertToFbe$cKWuNkHedkbg8HKJCoDk07_9og */

            public final void onClick(View view) {
                ConvertToFbe.this.lambda$onCreateView$0$ConvertToFbe(view);
            }
        });
        return inflate;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateView$0 */
    public /* synthetic */ void lambda$onCreateView$0$ConvertToFbe(View view) {
        if (!runKeyguardConfirmation(55)) {
            convert();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 55 && i2 == -1) {
            convert();
        }
    }

    private void convert() {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setDestination(ConfirmConvertToFbe.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.convert_to_file_encryption);
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.launch();
    }
}
