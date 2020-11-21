package com.android.settings.applications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.SettingsPreferenceFragment;

public class ConfirmConvertToFbe extends SettingsPreferenceFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 403;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0012R$layout.confirm_convert_fbe, (ViewGroup) null);
        ((Button) inflate.findViewById(C0010R$id.button_confirm_convert_fbe)).setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.applications.ConfirmConvertToFbe.AnonymousClass1 */

            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.FACTORY_RESET");
                intent.addFlags(268435456);
                intent.setPackage("android");
                intent.putExtra("android.intent.extra.REASON", "convert_fbe");
                ConfirmConvertToFbe.this.getActivity().sendBroadcast(intent);
            }
        });
        return inflate;
    }
}
