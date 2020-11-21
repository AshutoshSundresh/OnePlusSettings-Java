package com.android.settings.deviceinfo;

import android.app.ActivityManager;
import android.os.Bundle;
import android.os.UserManager;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.internal.util.Preconditions;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.deviceinfo.StorageSettings;
import java.io.File;
import java.util.Objects;

public class PublicVolumeSettings extends SettingsPreferenceFragment {
    private DiskInfo mDisk;
    private Preference mFormatPrivate;
    private Preference mFormatPublic;
    private boolean mIsPermittedToAdopt;
    private Preference mMount;
    private final StorageEventListener mStorageListener = new StorageEventListener() {
        /* class com.android.settings.deviceinfo.PublicVolumeSettings.AnonymousClass2 */

        public void onVolumeStateChanged(VolumeInfo volumeInfo, int i, int i2) {
            if (Objects.equals(PublicVolumeSettings.this.mVolume.getId(), volumeInfo.getId())) {
                PublicVolumeSettings.this.mVolume = volumeInfo;
                PublicVolumeSettings.this.update();
            }
        }

        public void onVolumeRecordChanged(VolumeRecord volumeRecord) {
            if (Objects.equals(PublicVolumeSettings.this.mVolume.getFsUuid(), volumeRecord.getFsUuid())) {
                PublicVolumeSettings publicVolumeSettings = PublicVolumeSettings.this;
                publicVolumeSettings.mVolume = publicVolumeSettings.mStorageManager.findVolumeById(PublicVolumeSettings.this.mVolumeId);
                PublicVolumeSettings.this.update();
            }
        }
    };
    private StorageManager mStorageManager;
    private StorageSummaryPreference mSummary;
    private Button mUnmount;
    private final View.OnClickListener mUnmountListener = new View.OnClickListener() {
        /* class com.android.settings.deviceinfo.PublicVolumeSettings.AnonymousClass1 */

        public void onClick(View view) {
            new StorageSettings.UnmountTask(PublicVolumeSettings.this.getActivity(), PublicVolumeSettings.this.mVolume).execute(new Void[0]);
        }
    };
    private VolumeInfo mVolume;
    private String mVolumeId;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 42;
    }

    private boolean isVolumeValid() {
        VolumeInfo volumeInfo = this.mVolume;
        return volumeInfo != null && volumeInfo.getType() == 0 && this.mVolume.isMountedReadable();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mIsPermittedToAdopt = UserManager.get(activity).isAdminUser() && !ActivityManager.isUserAMonkey();
        this.mStorageManager = (StorageManager) activity.getSystemService(StorageManager.class);
        if ("android.provider.action.DOCUMENT_ROOT_SETTINGS".equals(getActivity().getIntent().getAction())) {
            this.mVolume = this.mStorageManager.findVolumeByUuid(DocumentsContract.getRootId(getActivity().getIntent().getData()));
        } else {
            String string = getArguments().getString("android.os.storage.extra.VOLUME_ID");
            if (string != null) {
                this.mVolume = this.mStorageManager.findVolumeById(string);
            }
        }
        if (!isVolumeValid()) {
            getActivity().finish();
            return;
        }
        DiskInfo findDiskById = this.mStorageManager.findDiskById(this.mVolume.getDiskId());
        this.mDisk = findDiskById;
        Preconditions.checkNotNull(findDiskById);
        this.mVolumeId = this.mVolume.getId();
        addPreferencesFromResource(C0019R$xml.device_info_storage_volume);
        getPreferenceScreen().setOrderingAsAdded(true);
        this.mSummary = new StorageSummaryPreference(getPrefContext());
        this.mMount = buildAction(C0017R$string.storage_menu_mount);
        Button button = new Button(getActivity());
        this.mUnmount = button;
        button.setText(C0017R$string.storage_menu_unmount);
        this.mUnmount.setOnClickListener(this.mUnmountListener);
        this.mFormatPublic = buildAction(C0017R$string.storage_menu_format);
        if (this.mIsPermittedToAdopt) {
            this.mFormatPrivate = buildAction(C0017R$string.storage_menu_format_private);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (isVolumeValid()) {
            ((ViewGroup) getActivity().findViewById(C0010R$id.container_material)).addView(this.mUnmount, new ViewGroup.LayoutParams(-1, -2));
        }
    }

    public void update() {
        if (!isVolumeValid()) {
            getActivity().finish();
            return;
        }
        getActivity().setTitle(this.mStorageManager.getBestVolumeDescription(this.mVolume));
        FragmentActivity activity = getActivity();
        getPreferenceScreen().removeAll();
        if (this.mVolume.isMountedReadable()) {
            addPreference(this.mSummary);
            File path = this.mVolume.getPath();
            long totalSpace = path.getTotalSpace();
            long freeSpace = totalSpace - path.getFreeSpace();
            Formatter.BytesResult formatBytes = Formatter.formatBytes(getResources(), freeSpace, 0);
            this.mSummary.setTitle(TextUtils.expandTemplate(getText(C0017R$string.storage_size_large), formatBytes.value, formatBytes.units));
            this.mSummary.setSummary(getString(C0017R$string.storage_volume_used, Formatter.formatFileSize(activity, totalSpace)));
            this.mSummary.setPercent(freeSpace, totalSpace);
        }
        if (this.mVolume.getState() == 0) {
            addPreference(this.mMount);
        }
        if (!this.mVolume.isMountedReadable()) {
            this.mUnmount.setVisibility(8);
        }
        addPreference(this.mFormatPublic);
        if (this.mDisk.isAdoptable() && this.mIsPermittedToAdopt) {
            addPreference(this.mFormatPrivate);
        }
    }

    private void addPreference(Preference preference) {
        preference.setOrder(Integer.MAX_VALUE);
        getPreferenceScreen().addPreference(preference);
    }

    private Preference buildAction(int i) {
        Preference preference = new Preference(getPrefContext());
        preference.setTitle(i);
        return preference;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        this.mVolume = this.mStorageManager.findVolumeById(this.mVolumeId);
        if (!isVolumeValid()) {
            getActivity().finish();
            return;
        }
        this.mStorageManager.registerListener(this.mStorageListener);
        update();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mStorageManager.unregisterListener(this.mStorageListener);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == this.mMount) {
            new StorageSettings.MountTask(getActivity(), this.mVolume).execute(new Void[0]);
        } else if (preference == this.mFormatPublic) {
            StorageWizardFormatConfirm.showPublic(getActivity(), this.mDisk.getId());
        } else if (preference == this.mFormatPrivate) {
            StorageWizardFormatConfirm.showPrivate(getActivity(), this.mDisk.getId());
        }
        return super.onPreferenceTreeClick(preference);
    }
}
