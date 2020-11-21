package com.android.settings.deviceinfo.simstatus;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class SimStatusDialogFragment extends InstrumentedDialogFragment {
    private SimStatusDialogController mController;
    private View mRootView;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1246;
    }

    public static void show(Fragment fragment, int i, String str) {
        FragmentManager childFragmentManager = fragment.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("SimStatusDialog") == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("arg_key_sim_slot", i);
            bundle.putString("arg_key_dialog_title", str);
            SimStatusDialogFragment simStatusDialogFragment = new SimStatusDialogFragment();
            simStatusDialogFragment.setArguments(bundle);
            simStatusDialogFragment.show(childFragmentManager, "SimStatusDialog");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        int i = arguments.getInt("arg_key_sim_slot");
        String string = arguments.getString("arg_key_dialog_title");
        this.mController = new SimStatusDialogController(this, this.mLifecycle, i);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(string);
        builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
        this.mRootView = LayoutInflater.from(builder.getContext()).inflate(C0012R$layout.dialog_sim_status, (ViewGroup) null);
        this.mController.initialize();
        builder.setView(this.mRootView);
        return builder.create();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment
    public void onDestroy() {
        this.mController.deinitialize();
        super.onDestroy();
    }

    public void removeSettingFromScreen(int i) {
        View findViewById = this.mRootView.findViewById(i);
        if (findViewById != null) {
            findViewById.setVisibility(8);
        }
    }

    public void setText(int i, CharSequence charSequence) {
        TextView textView = (TextView) this.mRootView.findViewById(i);
        if (isAdded()) {
            if (TextUtils.isEmpty(charSequence)) {
                charSequence = getResources().getString(C0017R$string.device_info_default);
            }
            if (textView != null) {
                textView.setText(charSequence);
            }
        }
    }
}
