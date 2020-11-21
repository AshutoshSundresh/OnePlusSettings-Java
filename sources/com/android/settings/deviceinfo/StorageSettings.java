package com.android.settings.deviceinfo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.provider.SearchIndexableData;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.deviceinfo.PrivateStorageInfo;
import com.android.settingslib.deviceinfo.StorageManagerVolumeProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StorageSettings extends SettingsPreferenceFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.deviceinfo.StorageSettings.AnonymousClass2 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
            searchIndexableRaw.title = context.getString(C0017R$string.storage_settings);
            ((SearchIndexableData) searchIndexableRaw).key = "storage_settings";
            searchIndexableRaw.screenTitle = context.getString(C0017R$string.storage_settings);
            searchIndexableRaw.keywords = context.getString(C0017R$string.keywords_storage_settings);
            arrayList.add(searchIndexableRaw);
            SearchIndexableRaw searchIndexableRaw2 = new SearchIndexableRaw(context);
            searchIndexableRaw2.title = context.getString(C0017R$string.internal_storage);
            ((SearchIndexableData) searchIndexableRaw2).key = "storage_settings_internal_storage";
            searchIndexableRaw2.screenTitle = context.getString(C0017R$string.storage_settings);
            arrayList.add(searchIndexableRaw2);
            SearchIndexableRaw searchIndexableRaw3 = new SearchIndexableRaw(context);
            StorageManager storageManager = (StorageManager) context.getSystemService(StorageManager.class);
            for (VolumeInfo volumeInfo : storageManager.getVolumes()) {
                if (StorageSettings.isInteresting(volumeInfo)) {
                    searchIndexableRaw3.title = storageManager.getBestVolumeDescription(volumeInfo);
                    ((SearchIndexableData) searchIndexableRaw3).key = "storage_settings_volume_" + volumeInfo.id;
                    searchIndexableRaw3.screenTitle = context.getString(C0017R$string.storage_settings);
                    arrayList.add(searchIndexableRaw3);
                }
            }
            SearchIndexableRaw searchIndexableRaw4 = new SearchIndexableRaw(context);
            searchIndexableRaw4.title = context.getString(C0017R$string.memory_size);
            ((SearchIndexableData) searchIndexableRaw4).key = "storage_settings_memory_size";
            searchIndexableRaw4.screenTitle = context.getString(C0017R$string.storage_settings);
            arrayList.add(searchIndexableRaw4);
            SearchIndexableRaw searchIndexableRaw5 = new SearchIndexableRaw(context);
            searchIndexableRaw5.title = context.getString(C0017R$string.memory_available);
            ((SearchIndexableData) searchIndexableRaw5).key = "storage_settings_memory_available";
            searchIndexableRaw5.screenTitle = context.getString(C0017R$string.storage_settings);
            arrayList.add(searchIndexableRaw5);
            SearchIndexableRaw searchIndexableRaw6 = new SearchIndexableRaw(context);
            searchIndexableRaw6.title = context.getString(C0017R$string.memory_dcim_usage);
            ((SearchIndexableData) searchIndexableRaw6).key = "storage_settings_dcim_space";
            searchIndexableRaw6.screenTitle = context.getString(C0017R$string.storage_settings);
            arrayList.add(searchIndexableRaw6);
            SearchIndexableRaw searchIndexableRaw7 = new SearchIndexableRaw(context);
            searchIndexableRaw7.title = context.getString(C0017R$string.memory_music_usage);
            ((SearchIndexableData) searchIndexableRaw7).key = "storage_settings_music_space";
            searchIndexableRaw7.screenTitle = context.getString(C0017R$string.storage_settings);
            arrayList.add(searchIndexableRaw7);
            SearchIndexableRaw searchIndexableRaw8 = new SearchIndexableRaw(context);
            searchIndexableRaw8.title = context.getString(C0017R$string.memory_media_misc_usage);
            ((SearchIndexableData) searchIndexableRaw8).key = "storage_settings_misc_space";
            searchIndexableRaw8.screenTitle = context.getString(C0017R$string.storage_settings);
            arrayList.add(searchIndexableRaw8);
            SearchIndexableRaw searchIndexableRaw9 = new SearchIndexableRaw(context);
            searchIndexableRaw9.title = context.getString(C0017R$string.storage_menu_free);
            ((SearchIndexableData) searchIndexableRaw9).key = "storage_settings_free_space";
            searchIndexableRaw9.screenTitle = context.getString(C0017R$string.storage_menu_free);
            ((SearchIndexableData) searchIndexableRaw9).intentAction = "android.os.storage.action.MANAGE_STORAGE";
            searchIndexableRaw9.keywords = context.getString(C0017R$string.keywords_storage_menu_free);
            arrayList.add(searchIndexableRaw9);
            return arrayList;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (isExternalExist(context)) {
                nonIndexableKeys.add("storage_settings");
                nonIndexableKeys.add("storage_settings_internal_storage");
                nonIndexableKeys.add("storage_settings_memory_size");
                nonIndexableKeys.add("storage_settings_memory_available");
                nonIndexableKeys.add("storage_settings_dcim_space");
                nonIndexableKeys.add("storage_settings_music_space");
                nonIndexableKeys.add("storage_settings_misc_space");
                nonIndexableKeys.add("storage_settings_free_space");
                for (VolumeInfo volumeInfo : ((StorageManager) context.getSystemService(StorageManager.class)).getVolumes()) {
                    if (StorageSettings.isInteresting(volumeInfo)) {
                        nonIndexableKeys.add("storage_settings_volume_" + volumeInfo.id);
                    }
                }
            }
            return nonIndexableKeys;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return !isExternalExist(context);
        }

        private boolean isExternalExist(Context context) {
            StorageManager storageManager = (StorageManager) context.getSystemService(StorageManager.class);
            int i = 0;
            for (VolumeInfo volumeInfo : storageManager.getVolumes()) {
                if (volumeInfo.getType() == 0 || volumeInfo.getType() == 5) {
                    return true;
                }
                if (volumeInfo.getType() == 1) {
                    i++;
                }
            }
            for (DiskInfo diskInfo : storageManager.getDisks()) {
                if (diskInfo.volumeCount == 0 && diskInfo.size > 0) {
                    return true;
                }
            }
            for (VolumeRecord volumeRecord : storageManager.getVolumeRecords()) {
                if (volumeRecord.getType() == 1 && storageManager.findVolumeByUuid(volumeRecord.getFsUuid()) == null) {
                    i++;
                }
            }
            return i != 1;
        }
    };
    private static long sTotalInternalStorage;
    private PreferenceCategory mExternalCategory;
    private boolean mHasLaunchedPrivateVolumeSettings = false;
    private PreferenceCategory mInternalCategory;
    private StorageSummaryPreference mInternalSummary;
    private final StorageEventListener mStorageListener = new StorageEventListener() {
        /* class com.android.settings.deviceinfo.StorageSettings.AnonymousClass1 */

        public void onVolumeStateChanged(VolumeInfo volumeInfo, int i, int i2) {
            if (StorageSettings.isInteresting(volumeInfo)) {
                StorageSettings.this.refresh();
            }
        }

        public void onDiskDestroyed(DiskInfo diskInfo) {
            StorageSettings.this.refresh();
        }
    };
    private StorageManager mStorageManager;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 42;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_uri_storage;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        StorageManager storageManager = (StorageManager) getActivity().getSystemService(StorageManager.class);
        this.mStorageManager = storageManager;
        if (sTotalInternalStorage <= 0) {
            sTotalInternalStorage = storageManager.getPrimaryStorageSize();
        }
        addPreferencesFromResource(C0019R$xml.device_info_storage);
        this.mInternalCategory = (PreferenceCategory) findPreference("storage_internal");
        this.mExternalCategory = (PreferenceCategory) findPreference("storage_external");
        this.mInternalSummary = new StorageSummaryPreference(getPrefContext());
        setHasOptionsMenu(true);
    }

    /* access modifiers changed from: private */
    public static boolean isInteresting(VolumeInfo volumeInfo) {
        int type = volumeInfo.getType();
        return type == 0 || type == 1 || type == 5;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private synchronized void refresh() {
        Context prefContext = getPrefContext();
        getPreferenceScreen().removeAll();
        this.mInternalCategory.removeAll();
        this.mExternalCategory.removeAll();
        this.mInternalCategory.addPreference(this.mInternalSummary);
        PrivateStorageInfo privateStorageInfo = PrivateStorageInfo.getPrivateStorageInfo(new StorageManagerVolumeProvider(this.mStorageManager));
        long j = privateStorageInfo.totalBytes;
        long j2 = privateStorageInfo.totalBytes - privateStorageInfo.freeBytes;
        List<VolumeInfo> volumes = this.mStorageManager.getVolumes();
        Collections.sort(volumes, VolumeInfo.getDescriptionComparator());
        for (VolumeInfo volumeInfo : volumes) {
            if (volumeInfo.getType() == 1) {
                if (volumeInfo.getState() == 6) {
                    this.mInternalCategory.addPreference(new StorageVolumePreference(prefContext, volumeInfo, 0));
                } else {
                    this.mInternalCategory.addPreference(new StorageVolumePreference(prefContext, volumeInfo, PrivateStorageInfo.getTotalSize(volumeInfo, sTotalInternalStorage)));
                }
            } else if (volumeInfo.getType() == 0 || volumeInfo.getType() == 5) {
                this.mExternalCategory.addPreference(new StorageVolumePreference(prefContext, volumeInfo, 0));
            }
        }
        for (VolumeRecord volumeRecord : this.mStorageManager.getVolumeRecords()) {
            if (volumeRecord.getType() == 1 && this.mStorageManager.findVolumeByUuid(volumeRecord.getFsUuid()) == null) {
                Preference preference = new Preference(prefContext);
                preference.setKey(volumeRecord.getFsUuid());
                preference.setTitle(volumeRecord.getNickname());
                preference.setSummary(17040141);
                preference.setIcon(C0008R$drawable.ic_sim_sd);
                this.mInternalCategory.addPreference(preference);
            }
        }
        for (DiskInfo diskInfo : this.mStorageManager.getDisks()) {
            if (diskInfo.volumeCount == 0 && diskInfo.size > 0) {
                Preference preference2 = new Preference(prefContext);
                preference2.setKey(diskInfo.getId());
                preference2.setTitle(diskInfo.getDescription());
                preference2.setSummary(17040147);
                preference2.setIcon(C0008R$drawable.ic_sim_sd);
                this.mExternalCategory.addPreference(preference2);
            }
        }
        Formatter.BytesResult formatBytes = Formatter.formatBytes(getResources(), j2, 0);
        this.mInternalSummary.setTitle(TextUtils.expandTemplate(getText(C0017R$string.storage_size_large), formatBytes.value, formatBytes.units));
        this.mInternalSummary.setSummary(getString(C0017R$string.storage_volume_used_total, Formatter.formatFileSize(prefContext, j)));
        if (this.mInternalCategory.getPreferenceCount() > 0) {
            getPreferenceScreen().addPreference(this.mInternalCategory);
        }
        if (this.mExternalCategory.getPreferenceCount() > 0) {
            getPreferenceScreen().addPreference(this.mExternalCategory);
        }
        if (this.mInternalCategory.getPreferenceCount() == 2 && this.mExternalCategory.getPreferenceCount() == 0 && !this.mHasLaunchedPrivateVolumeSettings) {
            this.mHasLaunchedPrivateVolumeSettings = true;
            Bundle bundle = new Bundle();
            bundle.putString("android.os.storage.extra.VOLUME_ID", "private");
            SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getActivity());
            subSettingLauncher.setDestination(StorageDashboardFragment.class.getName());
            subSettingLauncher.setArguments(bundle);
            subSettingLauncher.setTitleRes(C0017R$string.storage_settings);
            subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
            subSettingLauncher.launch();
            finish();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        this.mStorageManager.registerListener(this.mStorageListener);
        refresh();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mStorageManager.unregisterListener(this.mStorageListener);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if (preference instanceof StorageVolumePreference) {
            VolumeInfo findVolumeById = this.mStorageManager.findVolumeById(key);
            if (findVolumeById == null) {
                return false;
            }
            if (findVolumeById.getState() == 0) {
                VolumeUnmountedFragment.show(this, findVolumeById.getId());
                return true;
            } else if (findVolumeById.getState() == 6) {
                DiskInitFragment.show(this, C0017R$string.storage_dialog_unmountable, findVolumeById.getDiskId());
                return true;
            } else if (findVolumeById.getType() == 1) {
                Bundle bundle = new Bundle();
                bundle.putString("android.os.storage.extra.VOLUME_ID", findVolumeById.getId());
                if ("private".equals(findVolumeById.getId())) {
                    SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
                    subSettingLauncher.setDestination(StorageDashboardFragment.class.getCanonicalName());
                    subSettingLauncher.setTitleRes(C0017R$string.storage_settings);
                    subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
                    subSettingLauncher.setArguments(bundle);
                    subSettingLauncher.launch();
                } else {
                    PrivateVolumeSettings.setVolumeSize(bundle, PrivateStorageInfo.getTotalSize(findVolumeById, sTotalInternalStorage));
                    SubSettingLauncher subSettingLauncher2 = new SubSettingLauncher(getContext());
                    subSettingLauncher2.setDestination(PrivateVolumeSettings.class.getCanonicalName());
                    subSettingLauncher2.setTitleRes(-1);
                    subSettingLauncher2.setSourceMetricsCategory(getMetricsCategory());
                    subSettingLauncher2.setArguments(bundle);
                    subSettingLauncher2.launch();
                }
                return true;
            } else if (findVolumeById.getType() == 0) {
                return handlePublicVolumeClick(getContext(), findVolumeById);
            } else {
                if (findVolumeById.getType() == 5) {
                    return handleStubVolumeClick(getContext(), findVolumeById);
                }
                return false;
            }
        } else if (key.startsWith("disk:")) {
            DiskInitFragment.show(this, C0017R$string.storage_dialog_unsupported, key);
            return true;
        } else {
            Bundle bundle2 = new Bundle();
            bundle2.putString("android.os.storage.extra.FS_UUID", key);
            SubSettingLauncher subSettingLauncher3 = new SubSettingLauncher(getContext());
            subSettingLauncher3.setDestination(PrivateVolumeForget.class.getCanonicalName());
            subSettingLauncher3.setTitleRes(C0017R$string.storage_menu_forget);
            subSettingLauncher3.setSourceMetricsCategory(getMetricsCategory());
            subSettingLauncher3.setArguments(bundle2);
            subSettingLauncher3.launch();
            return true;
        }
    }

    static boolean handleStubVolumeClick(Context context, VolumeInfo volumeInfo) {
        Intent buildBrowseIntent = volumeInfo.buildBrowseIntent();
        if (!volumeInfo.isMountedReadable() || buildBrowseIntent == null) {
            return false;
        }
        context.startActivity(buildBrowseIntent);
        return true;
    }

    static boolean handlePublicVolumeClick(Context context, VolumeInfo volumeInfo) {
        Intent buildBrowseIntent = volumeInfo.buildBrowseIntent();
        if (!volumeInfo.isMountedReadable() || buildBrowseIntent == null) {
            Bundle bundle = new Bundle();
            bundle.putString("android.os.storage.extra.VOLUME_ID", volumeInfo.getId());
            SubSettingLauncher subSettingLauncher = new SubSettingLauncher(context);
            subSettingLauncher.setDestination(PublicVolumeSettings.class.getCanonicalName());
            subSettingLauncher.setTitleRes(-1);
            subSettingLauncher.setSourceMetricsCategory(42);
            subSettingLauncher.setArguments(bundle);
            subSettingLauncher.launch();
            return true;
        }
        context.startActivity(buildBrowseIntent);
        return true;
    }

    public static class MountTask extends AsyncTask<Void, Void, Exception> {
        private final Context mContext;
        private final String mDescription;
        private final StorageManager mStorageManager;
        private final String mVolumeId;

        public MountTask(Context context, VolumeInfo volumeInfo) {
            Context applicationContext = context.getApplicationContext();
            this.mContext = applicationContext;
            this.mStorageManager = (StorageManager) applicationContext.getSystemService(StorageManager.class);
            this.mVolumeId = volumeInfo.getId();
            this.mDescription = this.mStorageManager.getBestVolumeDescription(volumeInfo);
        }

        /* access modifiers changed from: protected */
        public Exception doInBackground(Void... voidArr) {
            try {
                this.mStorageManager.mount(this.mVolumeId);
                return null;
            } catch (Exception e) {
                return e;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Exception exc) {
            if (exc == null) {
                Context context = this.mContext;
                Toast.makeText(context, context.getString(C0017R$string.storage_mount_success, this.mDescription), 0).show();
                return;
            }
            Log.e("StorageSettings", "Failed to mount " + this.mVolumeId, exc);
            Context context2 = this.mContext;
            Toast.makeText(context2, context2.getString(C0017R$string.storage_mount_failure, this.mDescription), 0).show();
        }
    }

    public static class UnmountTask extends AsyncTask<Void, Void, Exception> {
        private final Context mContext;
        private final String mDescription;
        private final StorageManager mStorageManager;
        private final String mVolumeId;

        public UnmountTask(Context context, VolumeInfo volumeInfo) {
            Context applicationContext = context.getApplicationContext();
            this.mContext = applicationContext;
            this.mStorageManager = (StorageManager) applicationContext.getSystemService(StorageManager.class);
            this.mVolumeId = volumeInfo.getId();
            this.mDescription = this.mStorageManager.getBestVolumeDescription(volumeInfo);
        }

        /* access modifiers changed from: protected */
        public Exception doInBackground(Void... voidArr) {
            try {
                this.mStorageManager.unmount(this.mVolumeId);
                return null;
            } catch (Exception e) {
                return e;
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Exception exc) {
            if (exc == null) {
                Context context = this.mContext;
                Toast.makeText(context, context.getString(C0017R$string.storage_unmount_success, this.mDescription), 0).show();
                return;
            }
            Log.e("StorageSettings", "Failed to unmount " + this.mVolumeId, exc);
            Context context2 = this.mContext;
            Toast.makeText(context2, context2.getString(C0017R$string.storage_unmount_failure, this.mDescription), 0).show();
        }
    }

    public static class VolumeUnmountedFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 562;
        }

        public static void show(Fragment fragment, String str) {
            Bundle bundle = new Bundle();
            bundle.putString("android.os.storage.extra.VOLUME_ID", str);
            VolumeUnmountedFragment volumeUnmountedFragment = new VolumeUnmountedFragment();
            volumeUnmountedFragment.setArguments(bundle);
            volumeUnmountedFragment.setTargetFragment(fragment, 0);
            volumeUnmountedFragment.show(fragment.getFragmentManager(), "volume_unmounted");
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            final FragmentActivity activity = getActivity();
            final VolumeInfo findVolumeById = ((StorageManager) activity.getSystemService(StorageManager.class)).findVolumeById(getArguments().getString("android.os.storage.extra.VOLUME_ID"));
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(TextUtils.expandTemplate(getText(C0017R$string.storage_dialog_unmounted), findVolumeById.getDisk().getDescription()));
            builder.setPositiveButton(C0017R$string.storage_menu_mount, new DialogInterface.OnClickListener() {
                /* class com.android.settings.deviceinfo.StorageSettings.VolumeUnmountedFragment.AnonymousClass1 */

                private boolean wasAdminSupportIntentShown(String str) {
                    RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(VolumeUnmountedFragment.this.getActivity(), str, UserHandle.myUserId());
                    boolean hasBaseUserRestriction = RestrictedLockUtilsInternal.hasBaseUserRestriction(VolumeUnmountedFragment.this.getActivity(), str, UserHandle.myUserId());
                    if (checkIfRestrictionEnforced == null || hasBaseUserRestriction) {
                        return false;
                    }
                    RestrictedLockUtils.sendShowAdminSupportDetailsIntent(VolumeUnmountedFragment.this.getActivity(), checkIfRestrictionEnforced);
                    return true;
                }

                public void onClick(DialogInterface dialogInterface, int i) {
                    if (!wasAdminSupportIntentShown("no_physical_media")) {
                        DiskInfo diskInfo = findVolumeById.disk;
                        if (diskInfo == null || !diskInfo.isUsb() || !wasAdminSupportIntentShown("no_usb_file_transfer")) {
                            new MountTask(activity, findVolumeById).execute(new Void[0]);
                        }
                    }
                }
            });
            builder.setNegativeButton(C0017R$string.cancel, (DialogInterface.OnClickListener) null);
            return builder.create();
        }
    }

    public static class DiskInitFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 561;
        }

        public static void show(Fragment fragment, int i, String str) {
            Bundle bundle = new Bundle();
            bundle.putInt("android.intent.extra.TEXT", i);
            bundle.putString("android.os.storage.extra.DISK_ID", str);
            DiskInitFragment diskInitFragment = new DiskInitFragment();
            diskInitFragment.setArguments(bundle);
            diskInitFragment.setTargetFragment(fragment, 0);
            diskInitFragment.show(fragment.getFragmentManager(), "disk_init");
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            final FragmentActivity activity = getActivity();
            int i = getArguments().getInt("android.intent.extra.TEXT");
            final String string = getArguments().getString("android.os.storage.extra.DISK_ID");
            DiskInfo findDiskById = ((StorageManager) activity.getSystemService(StorageManager.class)).findDiskById(string);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(TextUtils.expandTemplate(getText(i), findDiskById.getDescription()));
            builder.setPositiveButton(C0017R$string.storage_menu_set_up, new DialogInterface.OnClickListener() {
                /* class com.android.settings.deviceinfo.StorageSettings.DiskInitFragment.AnonymousClass1 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(activity, StorageWizardInit.class);
                    intent.putExtra("android.os.storage.extra.DISK_ID", string);
                    DiskInitFragment.this.startActivity(intent);
                }
            });
            builder.setNegativeButton(C0017R$string.cancel, (DialogInterface.OnClickListener) null);
            return builder.create();
        }
    }
}
