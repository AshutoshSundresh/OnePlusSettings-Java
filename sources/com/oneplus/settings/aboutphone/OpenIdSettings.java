package com.oneplus.settings.aboutphone;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.widget.LayoutPreference;
import com.oneplus.android.openid.OpOpenIdManagerInjector;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.widget.OPFooterPreference;

public class OpenIdSettings extends DashboardFragment {
    private OPFooterPreference mFooterPreference;
    private AlertDialog mWarnDialog;

    static /* synthetic */ void lambda$showWarningDialog$3(DialogInterface dialogInterface, int i) {
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OpenIdSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LayoutPreference layoutPreference = (LayoutPreference) findPreference("reset_openid_button_container");
        SwitchPreference switchPreference = (SwitchPreference) findPreference("get_opendi_switch");
        OPFooterPreference createFooterPreference = this.mFooterPreferenceMixin.createFooterPreference();
        this.mFooterPreference = createFooterPreference;
        createFooterPreference.setOrder(20);
        this.mFooterPreference.setSelectable(false);
        try {
            this.mFooterPreference.setTitle(getString(C0017R$string.openid_prefix) + OpOpenIdManagerInjector.getOpenId((String) null, Process.myUid(), "OUID"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Button button = (Button) layoutPreference.findViewById(C0010R$id.reset_openid_button);
        boolean z = true;
        int i = Settings.System.getInt(getContentResolver(), "oneplus_openid_toggle", 1);
        if (i != 1) {
            z = false;
        }
        switchPreference.setChecked(z);
        button.setOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.aboutphone.$$Lambda$OpenIdSettings$AuO0aFRPrmiDP9aUKus1jWVCZ0 */

            public final void onClick(View view) {
                OpenIdSettings.this.lambda$onCreate$0$OpenIdSettings(view);
            }
        });
        if (i == 0) {
            button.setEnabled(false);
        }
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(button) {
            /* class com.oneplus.settings.aboutphone.$$Lambda$OpenIdSettings$LaYZZxMZM6DZERThyqEIte482Y */
            public final /* synthetic */ Button f$1;

            {
                this.f$1 = r2;
            }

            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return OpenIdSettings.this.lambda$onCreate$1$OpenIdSettings(this.f$1, preference, obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ void lambda$onCreate$0$OpenIdSettings(View view) {
        showWarningDialog();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$1 */
    public /* synthetic */ boolean lambda$onCreate$1$OpenIdSettings(Button button, Preference preference, Object obj) {
        Boolean bool = (Boolean) obj;
        Settings.System.putInt(getContentResolver(), "oneplus_openid_toggle", bool.booleanValue() ? 1 : 0);
        OPUtils.sendAnalytics("oaid", "status", bool.booleanValue() ? "on" : "off");
        button.setEnabled(bool.booleanValue());
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_openid_settings;
    }

    public void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0017R$string.openid_dialog_title);
        builder.setMessage(C0017R$string.openid_dialog_summary);
        builder.setPositiveButton(C0017R$string.openid_dialog_reset, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.aboutphone.$$Lambda$OpenIdSettings$gH9zyX_fqtkhjBj1gsjo4pbq_5Y */

            public final void onClick(DialogInterface dialogInterface, int i) {
                OpenIdSettings.this.lambda$showWarningDialog$2$OpenIdSettings(dialogInterface, i);
            }
        });
        builder.setNegativeButton(C0017R$string.cancel, $$Lambda$OpenIdSettings$zxn4_303Ql923k6ogE0guN_EXMA.INSTANCE);
        AlertDialog create = builder.create();
        this.mWarnDialog = create;
        create.show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showWarningDialog$2 */
    public /* synthetic */ void lambda$showWarningDialog$2$OpenIdSettings(DialogInterface dialogInterface, int i) {
        try {
            OpOpenIdManagerInjector.clearOpenId((String) null, Process.myUid(), "OUID");
            OPFooterPreference oPFooterPreference = this.mFooterPreference;
            oPFooterPreference.setTitle(getString(C0017R$string.openid_prefix) + OpOpenIdManagerInjector.getOpenId((String) null, Process.myUid(), "OUID"));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        AlertDialog alertDialog = this.mWarnDialog;
        if (alertDialog != null) {
            alertDialog.cancel();
        }
        super.onDestroyView();
    }
}
