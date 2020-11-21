package com.android.settings.deviceinfo;

import android.os.Bundle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.deviceinfo.StorageSettings;
import com.android.settings.search.actionbar.SearchMenuController;

public class PrivateVolumeUnmount extends InstrumentedFragment {
    private final View.OnClickListener mConfirmListener = new View.OnClickListener() {
        /* class com.android.settings.deviceinfo.PrivateVolumeUnmount.AnonymousClass1 */

        public void onClick(View view) {
            new StorageSettings.UnmountTask(PrivateVolumeUnmount.this.getActivity(), PrivateVolumeUnmount.this.mVolume).execute(new Void[0]);
            PrivateVolumeUnmount.this.getActivity().finish();
        }
    };
    private DiskInfo mDisk;
    private VolumeInfo mVolume;

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
        VolumeInfo findVolumeById = storageManager.findVolumeById(getArguments().getString("android.os.storage.extra.VOLUME_ID"));
        this.mVolume = findVolumeById;
        this.mDisk = storageManager.findDiskById(findVolumeById.getDiskId());
        View inflate = layoutInflater.inflate(C0012R$layout.storage_internal_unmount, viewGroup, false);
        ((TextView) inflate.findViewById(C0010R$id.body)).setText(TextUtils.expandTemplate(getText(C0017R$string.storage_internal_unmount_details), this.mDisk.getDescription()));
        ((Button) inflate.findViewById(C0010R$id.confirm)).setOnClickListener(this.mConfirmListener);
        return inflate;
    }
}
