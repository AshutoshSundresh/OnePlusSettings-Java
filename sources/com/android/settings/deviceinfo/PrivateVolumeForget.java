package com.android.settings.deviceinfo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeRecord;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.search.actionbar.SearchMenuController;

public class PrivateVolumeForget extends InstrumentedFragment {
    static final String TAG_FORGET_CONFIRM = "forget_confirm";
    private final View.OnClickListener mConfirmListener = new View.OnClickListener() {
        /* class com.android.settings.deviceinfo.PrivateVolumeForget.AnonymousClass1 */

        public void onClick(View view) {
            PrivateVolumeForget privateVolumeForget = PrivateVolumeForget.this;
            ForgetConfirmFragment.show(privateVolumeForget, privateVolumeForget.mRecord.getFsUuid());
        }
    };
    private VolumeRecord mRecord;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 42;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        SearchMenuController.init(this);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        StorageManager storageManager = (StorageManager) getActivity().getSystemService(StorageManager.class);
        String string = getArguments().getString("android.os.storage.extra.FS_UUID");
        if (string == null) {
            getActivity().finish();
            return null;
        }
        VolumeRecord findRecordByUuid = storageManager.findRecordByUuid(string);
        this.mRecord = findRecordByUuid;
        if (findRecordByUuid == null) {
            getActivity().finish();
            return null;
        }
        View inflate = layoutInflater.inflate(C0012R$layout.storage_internal_forget, viewGroup, false);
        ((TextView) inflate.findViewById(C0010R$id.body)).setText(TextUtils.expandTemplate(getText(C0017R$string.storage_internal_forget_details), this.mRecord.getNickname()));
        ((Button) inflate.findViewById(C0010R$id.confirm)).setOnClickListener(this.mConfirmListener);
        return inflate;
    }

    public static class ForgetConfirmFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 559;
        }

        public static void show(Fragment fragment, String str) {
            Bundle bundle = new Bundle();
            bundle.putString("android.os.storage.extra.FS_UUID", str);
            ForgetConfirmFragment forgetConfirmFragment = new ForgetConfirmFragment();
            forgetConfirmFragment.setArguments(bundle);
            forgetConfirmFragment.setTargetFragment(fragment, 0);
            forgetConfirmFragment.show(fragment.getFragmentManager(), PrivateVolumeForget.TAG_FORGET_CONFIRM);
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            FragmentActivity activity = getActivity();
            final StorageManager storageManager = (StorageManager) activity.getSystemService(StorageManager.class);
            final String string = getArguments().getString("android.os.storage.extra.FS_UUID");
            VolumeRecord findRecordByUuid = storageManager.findRecordByUuid(string);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(TextUtils.expandTemplate(getText(C0017R$string.storage_internal_forget_confirm_title), findRecordByUuid.getNickname()));
            builder.setMessage(TextUtils.expandTemplate(getText(C0017R$string.storage_internal_forget_confirm), findRecordByUuid.getNickname()));
            builder.setPositiveButton(C0017R$string.storage_menu_forget, new DialogInterface.OnClickListener() {
                /* class com.android.settings.deviceinfo.PrivateVolumeForget.ForgetConfirmFragment.AnonymousClass1 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    storageManager.forgetVolume(string);
                    ForgetConfirmFragment.this.getActivity().finish();
                }
            });
            builder.setNegativeButton(C0017R$string.cancel, (DialogInterface.OnClickListener) null);
            return builder.create();
        }
    }
}
