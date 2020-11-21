package com.android.settings.display;

import android.app.Dialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class DarkUIInfoDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1740;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View inflate = LayoutInflater.from(builder.getContext()).inflate(C0012R$layout.settings_dialog_title, (ViewGroup) null);
        ((ImageView) inflate.findViewById(C0010R$id.settings_icon)).setImageDrawable(context.getDrawable(C0008R$drawable.dark_theme));
        ((TextView) inflate.findViewById(C0010R$id.settings_title)).setText(C0017R$string.dark_ui_mode);
        builder.setCustomTitle(inflate);
        builder.setMessage(C0017R$string.dark_ui_settings_dark_summary);
        builder.setPositiveButton(C0017R$string.dark_ui_settings_dialog_acknowledge, this);
        return builder.create();
    }

    @Override // androidx.fragment.app.DialogFragment
    public void onDismiss(DialogInterface dialogInterface) {
        enableDarkTheme();
        super.onDismiss(dialogInterface);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        enableDarkTheme();
    }

    private void enableDarkTheme() {
        Context context = getContext();
        if (context != null) {
            Settings.Secure.putInt(context.getContentResolver(), DarkUIPreferenceController.PREF_DARK_MODE_DIALOG_SEEN, 1);
            ((UiModeManager) context.getSystemService(UiModeManager.class)).setNightMode(2);
        }
    }
}
