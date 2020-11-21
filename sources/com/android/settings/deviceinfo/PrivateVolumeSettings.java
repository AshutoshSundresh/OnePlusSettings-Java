package com.android.settings.deviceinfo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.os.storage.VolumeRecord;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0005R$bool;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0013R$menu;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Settings;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.applications.manageapplications.ManageApplications;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.deviceinfo.StorageSettings;
import com.android.settingslib.deviceinfo.StorageMeasurement;
import com.google.android.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PrivateVolumeSettings extends SettingsPreferenceFragment {
    private static final int[] ITEMS_NO_SHOW_SHARED = {C0017R$string.storage_detail_apps, C0017R$string.storage_detail_system};
    private static final int[] ITEMS_SHOW_SHARED = {C0017R$string.storage_detail_apps, C0017R$string.storage_detail_images, C0017R$string.storage_detail_videos, C0017R$string.storage_detail_audio, C0017R$string.storage_detail_system, C0017R$string.storage_detail_other};
    private UserInfo mCurrentUser;
    private Preference mExplore;
    private int mHeaderPoolIndex;
    private List<PreferenceCategory> mHeaderPreferencePool = Lists.newArrayList();
    private int mItemPoolIndex;
    private List<StorageItemPreference> mItemPreferencePool = Lists.newArrayList();
    private StorageMeasurement mMeasure;
    private boolean mNeedsUpdate;
    private final StorageMeasurement.MeasurementReceiver mReceiver = new StorageMeasurement.MeasurementReceiver() {
        /* class com.android.settings.deviceinfo.PrivateVolumeSettings.AnonymousClass1 */

        @Override // com.android.settingslib.deviceinfo.StorageMeasurement.MeasurementReceiver
        public void onDetailsChanged(StorageMeasurement.MeasurementDetails measurementDetails) {
            PrivateVolumeSettings.this.updateDetails(measurementDetails);
        }
    };
    private VolumeInfo mSharedVolume;
    private final StorageEventListener mStorageListener = new StorageEventListener() {
        /* class com.android.settings.deviceinfo.PrivateVolumeSettings.AnonymousClass2 */

        public void onVolumeStateChanged(VolumeInfo volumeInfo, int i, int i2) {
            if (Objects.equals(PrivateVolumeSettings.this.mVolume.getId(), volumeInfo.getId())) {
                PrivateVolumeSettings.this.mVolume = volumeInfo;
                PrivateVolumeSettings.this.update();
            }
        }

        public void onVolumeRecordChanged(VolumeRecord volumeRecord) {
            if (Objects.equals(PrivateVolumeSettings.this.mVolume.getFsUuid(), volumeRecord.getFsUuid())) {
                PrivateVolumeSettings privateVolumeSettings = PrivateVolumeSettings.this;
                privateVolumeSettings.mVolume = privateVolumeSettings.mStorageManager.findVolumeById(PrivateVolumeSettings.this.mVolumeId);
                PrivateVolumeSettings.this.update();
            }
        }
    };
    private StorageManager mStorageManager;
    private StorageSummaryPreference mSummary;
    private long mSystemSize;
    private long mTotalSize;
    private UserManager mUserManager;
    private VolumeInfo mVolume;
    private String mVolumeId;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 42;
    }

    private boolean isVolumeValid() {
        VolumeInfo volumeInfo = this.mVolume;
        if (volumeInfo == null || volumeInfo.getType() != 1 || !this.mVolume.isMountedReadable()) {
            return false;
        }
        return true;
    }

    public PrivateVolumeSettings() {
        setRetainInstance(true);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mUserManager = (UserManager) activity.getSystemService(UserManager.class);
        this.mStorageManager = (StorageManager) activity.getSystemService(StorageManager.class);
        String string = getArguments().getString("android.os.storage.extra.VOLUME_ID");
        this.mVolumeId = string;
        VolumeInfo findVolumeById = this.mStorageManager.findVolumeById(string);
        this.mVolume = findVolumeById;
        long totalSpace = findVolumeById.getPath().getTotalSpace();
        long j = getArguments().getLong("volume_size", 0);
        this.mTotalSize = j;
        this.mSystemSize = j - totalSpace;
        if (j <= 0) {
            this.mTotalSize = totalSpace;
            this.mSystemSize = 0;
        }
        VolumeInfo findEmulatedForPrivate = this.mStorageManager.findEmulatedForPrivate(this.mVolume);
        this.mSharedVolume = findEmulatedForPrivate;
        StorageMeasurement storageMeasurement = new StorageMeasurement(activity, this.mVolume, findEmulatedForPrivate);
        this.mMeasure = storageMeasurement;
        storageMeasurement.setReceiver(this.mReceiver);
        if (!isVolumeValid()) {
            getActivity().finish();
            return;
        }
        addPreferencesFromResource(C0019R$xml.device_info_storage_volume);
        getPreferenceScreen().setOrderingAsAdded(true);
        this.mSummary = new StorageSummaryPreference(getPrefContext());
        this.mCurrentUser = this.mUserManager.getUserInfo(UserHandle.myUserId());
        this.mExplore = buildAction(C0017R$string.storage_menu_explore);
        this.mNeedsUpdate = true;
        setHasOptionsMenu(true);
    }

    private void setTitle() {
        getActivity().setTitle(this.mStorageManager.getBestVolumeDescription(this.mVolume));
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void update() {
        if (!isVolumeValid()) {
            getActivity().finish();
            return;
        }
        setTitle();
        getActivity().invalidateOptionsMenu();
        FragmentActivity activity = getActivity();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        addPreference(preferenceScreen, this.mSummary);
        List users = this.mUserManager.getUsers();
        int size = users.size();
        boolean z = size > 1;
        VolumeInfo volumeInfo = this.mSharedVolume;
        boolean z2 = volumeInfo != null && volumeInfo.isMountedReadable();
        this.mItemPoolIndex = 0;
        this.mHeaderPoolIndex = 0;
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            UserInfo userInfo = (UserInfo) users.get(i2);
            if (Utils.isProfileOf(this.mCurrentUser, userInfo)) {
                addDetailItems(z ? addCategory(preferenceScreen, userInfo.name) : preferenceScreen, z2, userInfo.id);
                i++;
            }
        }
        if (size - i > 0) {
            PreferenceCategory addCategory = addCategory(preferenceScreen, getText(C0017R$string.storage_other_users));
            for (int i3 = 0; i3 < size; i3++) {
                UserInfo userInfo2 = (UserInfo) users.get(i3);
                if (!Utils.isProfileOf(this.mCurrentUser, userInfo2)) {
                    addItem(addCategory, 0, userInfo2.name, userInfo2.id);
                }
            }
        }
        addItem(preferenceScreen, C0017R$string.storage_detail_cached, null, -10000);
        if (z2) {
            addPreference(preferenceScreen, this.mExplore);
        }
        long freeSpace = this.mTotalSize - this.mVolume.getPath().getFreeSpace();
        Formatter.BytesResult formatBytes = Formatter.formatBytes(getResources(), freeSpace, 0);
        this.mSummary.setTitle(TextUtils.expandTemplate(getText(C0017R$string.storage_size_large), formatBytes.value, formatBytes.units));
        this.mSummary.setSummary(getString(C0017R$string.storage_volume_used, Formatter.formatFileSize(activity, this.mTotalSize)));
        this.mSummary.setPercent(freeSpace, this.mTotalSize);
        this.mMeasure.forceMeasure();
        this.mNeedsUpdate = false;
    }

    private void addPreference(PreferenceGroup preferenceGroup, Preference preference) {
        preference.setOrder(Integer.MAX_VALUE);
        preferenceGroup.addPreference(preference);
    }

    private PreferenceCategory addCategory(PreferenceGroup preferenceGroup, CharSequence charSequence) {
        PreferenceCategory preferenceCategory;
        if (this.mHeaderPoolIndex < this.mHeaderPreferencePool.size()) {
            preferenceCategory = this.mHeaderPreferencePool.get(this.mHeaderPoolIndex);
        } else {
            preferenceCategory = new PreferenceCategory(getPrefContext());
            this.mHeaderPreferencePool.add(preferenceCategory);
        }
        preferenceCategory.setTitle(charSequence);
        preferenceCategory.removeAll();
        addPreference(preferenceGroup, preferenceCategory);
        this.mHeaderPoolIndex++;
        return preferenceCategory;
    }

    private void addDetailItems(PreferenceGroup preferenceGroup, boolean z, int i) {
        int[] iArr;
        for (int i2 : z ? ITEMS_SHOW_SHARED : ITEMS_NO_SHOW_SHARED) {
            addItem(preferenceGroup, i2, null, i);
        }
    }

    private void addItem(PreferenceGroup preferenceGroup, int i, CharSequence charSequence, int i2) {
        StorageItemPreference storageItemPreference;
        if (i == C0017R$string.storage_detail_system) {
            if (this.mSystemSize <= 0) {
                Log.w("PrivateVolumeSettings", "Skipping System storage because its size is " + this.mSystemSize);
                return;
            } else if (i2 != UserHandle.myUserId()) {
                return;
            }
        }
        if (this.mItemPoolIndex < this.mItemPreferencePool.size()) {
            storageItemPreference = this.mItemPreferencePool.get(this.mItemPoolIndex);
        } else {
            storageItemPreference = buildItem();
            this.mItemPreferencePool.add(storageItemPreference);
        }
        if (charSequence != null) {
            storageItemPreference.setTitle(charSequence);
            storageItemPreference.setKey(charSequence.toString());
        } else {
            storageItemPreference.setTitle(i);
            storageItemPreference.setKey(Integer.toString(i));
        }
        storageItemPreference.setSummary(C0017R$string.memory_calculating_size);
        storageItemPreference.userHandle = i2;
        addPreference(preferenceGroup, storageItemPreference);
        this.mItemPoolIndex++;
    }

    private StorageItemPreference buildItem() {
        StorageItemPreference storageItemPreference = new StorageItemPreference(getPrefContext());
        storageItemPreference.setIcon(C0008R$drawable.empty_icon);
        return storageItemPreference;
    }

    private Preference buildAction(int i) {
        Preference preference = new Preference(getPrefContext());
        preference.setTitle(i);
        preference.setKey(Integer.toString(i));
        return preference;
    }

    static void setVolumeSize(Bundle bundle, long j) {
        bundle.putLong("volume_size", j);
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
        if (this.mNeedsUpdate) {
            update();
        } else {
            setTitle();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mStorageManager.unregisterListener(this.mStorageListener);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        StorageMeasurement storageMeasurement = this.mMeasure;
        if (storageMeasurement != null) {
            storageMeasurement.onDestroy();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(C0013R$menu.storage_volume, menu);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPrepareOptionsMenu(Menu menu) {
        if (isVolumeValid()) {
            MenuItem findItem = menu.findItem(C0010R$id.storage_rename);
            MenuItem findItem2 = menu.findItem(C0010R$id.storage_mount);
            MenuItem findItem3 = menu.findItem(C0010R$id.storage_unmount);
            MenuItem findItem4 = menu.findItem(C0010R$id.storage_format);
            MenuItem findItem5 = menu.findItem(C0010R$id.storage_migrate);
            MenuItem findItem6 = menu.findItem(C0010R$id.storage_free);
            boolean z = true;
            if ("private".equals(this.mVolume.getId())) {
                findItem.setVisible(false);
                findItem2.setVisible(false);
                findItem3.setVisible(false);
                findItem4.setVisible(false);
                findItem6.setVisible(getResources().getBoolean(C0005R$bool.config_storage_manager_settings_enabled));
            } else {
                findItem.setVisible(this.mVolume.getType() == 1);
                findItem2.setVisible(this.mVolume.getState() == 0);
                findItem3.setVisible(this.mVolume.isMountedReadable());
                findItem4.setVisible(true);
                findItem6.setVisible(false);
            }
            findItem4.setTitle(C0017R$string.storage_menu_format_public);
            VolumeInfo primaryStorageCurrentVolume = getActivity().getPackageManager().getPrimaryStorageCurrentVolume();
            if (primaryStorageCurrentVolume == null || primaryStorageCurrentVolume.getType() != 1 || Objects.equals(this.mVolume, primaryStorageCurrentVolume) || !primaryStorageCurrentVolume.isMountedWritable()) {
                z = false;
            }
            findItem5.setVisible(z);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        FragmentActivity activity = getActivity();
        Bundle bundle = new Bundle();
        int itemId = menuItem.getItemId();
        if (itemId == C0010R$id.storage_rename) {
            RenameFragment.show(this, this.mVolume);
            return true;
        } else if (itemId == C0010R$id.storage_mount) {
            new StorageSettings.MountTask(activity, this.mVolume).execute(new Void[0]);
            return true;
        } else if (itemId == C0010R$id.storage_unmount) {
            bundle.putString("android.os.storage.extra.VOLUME_ID", this.mVolume.getId());
            SubSettingLauncher subSettingLauncher = new SubSettingLauncher(activity);
            subSettingLauncher.setDestination(PrivateVolumeUnmount.class.getCanonicalName());
            subSettingLauncher.setTitleRes(C0017R$string.storage_menu_unmount);
            subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
            subSettingLauncher.setArguments(bundle);
            subSettingLauncher.launch();
            return true;
        } else if (itemId == C0010R$id.storage_format) {
            bundle.putString("android.os.storage.extra.VOLUME_ID", this.mVolume.getId());
            SubSettingLauncher subSettingLauncher2 = new SubSettingLauncher(activity);
            subSettingLauncher2.setDestination(PrivateVolumeFormat.class.getCanonicalName());
            subSettingLauncher2.setTitleRes(C0017R$string.storage_menu_format);
            subSettingLauncher2.setSourceMetricsCategory(getMetricsCategory());
            subSettingLauncher2.setArguments(bundle);
            subSettingLauncher2.launch();
            return true;
        } else if (itemId == C0010R$id.storage_migrate) {
            Intent intent = new Intent(activity, StorageWizardMigrateConfirm.class);
            intent.putExtra("android.os.storage.extra.VOLUME_ID", this.mVolume.getId());
            startActivity(intent);
            return true;
        } else if (itemId != C0010R$id.storage_free) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            startActivity(new Intent("android.os.storage.action.MANAGE_STORAGE"));
            return true;
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        int i;
        int i2 = preference instanceof StorageItemPreference ? ((StorageItemPreference) preference).userHandle : -1;
        try {
            i = Integer.parseInt(preference.getKey());
        } catch (NumberFormatException unused) {
            i = 0;
        }
        Intent intent = null;
        if (i == C0017R$string.storage_detail_apps) {
            Bundle bundle = new Bundle();
            bundle.putString("classname", Settings.StorageUseActivity.class.getName());
            bundle.putString("volumeUuid", this.mVolume.getFsUuid());
            bundle.putString("volumeName", this.mVolume.getDescription());
            bundle.putInt("storageType", 2);
            SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getActivity());
            subSettingLauncher.setDestination(ManageApplications.class.getName());
            subSettingLauncher.setArguments(bundle);
            subSettingLauncher.setTitleRes(C0017R$string.apps_storage);
            subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
            intent = subSettingLauncher.toIntent();
        } else if (i == C0017R$string.storage_detail_images) {
            intent = getIntentForStorage("com.android.providers.media.documents", "images_root");
        } else if (i == C0017R$string.storage_detail_videos) {
            intent = getIntentForStorage("com.android.providers.media.documents", "videos_root");
        } else if (i == C0017R$string.storage_detail_audio) {
            intent = getIntentForStorage("com.android.providers.media.documents", "audio_root");
        } else if (i == C0017R$string.storage_detail_system) {
            SystemInfoFragment.show(this);
            return true;
        } else if (i == C0017R$string.storage_detail_other) {
            OtherInfoFragment.show(this, this.mStorageManager.getBestVolumeDescription(this.mVolume), this.mSharedVolume, i2);
            return true;
        } else if (i == C0017R$string.storage_detail_cached) {
            ConfirmClearCacheFragment.show(this);
            return true;
        } else if (i == C0017R$string.storage_menu_explore) {
            intent = this.mSharedVolume.buildBrowseIntent();
        } else if (i == 0) {
            UserInfoFragment.show(this, preference.getTitle(), preference.getSummary());
            return true;
        }
        if (intent == null) {
            return super.onPreferenceTreeClick(preference);
        }
        intent.putExtra("android.intent.extra.USER_ID", i2);
        Utils.launchIntent(this, intent);
        return true;
    }

    private Intent getIntentForStorage(String str, String str2) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(DocumentsContract.buildRootUri(str, str2), "vnd.android.document/root");
        intent.addCategory("android.intent.category.DEFAULT");
        return intent;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateDetails(StorageMeasurement.MeasurementDetails measurementDetails) {
        int i;
        boolean z;
        long j;
        long j2;
        long j3 = 0;
        long j4 = 0;
        StorageItemPreference storageItemPreference = null;
        long j5 = 0;
        for (int i2 = 0; i2 < this.mItemPoolIndex; i2++) {
            StorageItemPreference storageItemPreference2 = this.mItemPreferencePool.get(i2);
            int i3 = storageItemPreference2.userHandle;
            try {
                i = Integer.parseInt(storageItemPreference2.getKey());
            } catch (NumberFormatException unused) {
                i = 0;
            }
            if (i == C0017R$string.storage_detail_system) {
                updatePreference(storageItemPreference2, this.mSystemSize);
                j2 = this.mSystemSize;
            } else if (i == C0017R$string.storage_detail_apps) {
                updatePreference(storageItemPreference2, measurementDetails.appsSize.get(i3));
                j2 = measurementDetails.appsSize.get(i3);
            } else {
                if (i == C0017R$string.storage_detail_images) {
                    z = false;
                    j = totalValues(measurementDetails, i3, Environment.DIRECTORY_DCIM, Environment.DIRECTORY_PICTURES);
                    updatePreference(storageItemPreference2, j);
                } else {
                    z = false;
                    if (i == C0017R$string.storage_detail_videos) {
                        j = totalValues(measurementDetails, i3, Environment.DIRECTORY_MOVIES);
                        updatePreference(storageItemPreference2, j);
                    } else if (i == C0017R$string.storage_detail_audio) {
                        long j6 = totalValues(measurementDetails, i3, Environment.DIRECTORY_MUSIC, Environment.DIRECTORY_ALARMS, Environment.DIRECTORY_NOTIFICATIONS, Environment.DIRECTORY_RINGTONES, Environment.DIRECTORY_PODCASTS);
                        updatePreference(storageItemPreference2, j6);
                        j3 += j6;
                    } else {
                        if (i == C0017R$string.storage_detail_other) {
                            long j7 = totalValues(measurementDetails, i3, Environment.DIRECTORY_DOWNLOADS);
                            long j8 = measurementDetails.miscSize.get(i3);
                            j4 += j7;
                            j5 += j8;
                            j3 += j8 + j7;
                            storageItemPreference = storageItemPreference2;
                        } else if (i == C0017R$string.storage_detail_cached) {
                            updatePreference(storageItemPreference2, measurementDetails.cacheSize);
                            j3 += measurementDetails.cacheSize;
                        } else if (i == 0) {
                            long j9 = measurementDetails.usersSize.get(i3);
                            updatePreference(storageItemPreference2, j9);
                            j3 += j9;
                        }
                    }
                }
                j3 += j;
            }
            j3 += j2;
        }
        if (storageItemPreference != null) {
            long j10 = this.mTotalSize - measurementDetails.availSize;
            long j11 = j10 - j3;
            Log.v("PrivateVolumeSettings", "Other items: \n\tmTotalSize: " + this.mTotalSize + " availSize: " + measurementDetails.availSize + " usedSize: " + j10 + "\n\taccountedSize: " + j3 + " unaccountedSize size: " + j11 + "\n\ttotalMiscSize: " + j5 + " totalDownloadsSize: " + j4 + "\n\tdetails: " + measurementDetails);
            updatePreference(storageItemPreference, j5 + j4 + j11);
        }
    }

    private void updatePreference(StorageItemPreference storageItemPreference, long j) {
        storageItemPreference.setStorageSize(j, this.mTotalSize);
    }

    private static long totalValues(StorageMeasurement.MeasurementDetails measurementDetails, int i, String... strArr) {
        HashMap<String, Long> hashMap = measurementDetails.mediaSize.get(i);
        long j = 0;
        if (hashMap != null) {
            for (String str : strArr) {
                if (hashMap.containsKey(str)) {
                    j += hashMap.get(str).longValue();
                }
            }
        } else {
            Log.w("PrivateVolumeSettings", "MeasurementDetails mediaSize array does not have key for user " + i);
        }
        return j;
    }

    public static class RenameFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 563;
        }

        public static void show(PrivateVolumeSettings privateVolumeSettings, VolumeInfo volumeInfo) {
            if (privateVolumeSettings.isAdded()) {
                RenameFragment renameFragment = new RenameFragment();
                renameFragment.setTargetFragment(privateVolumeSettings, 0);
                Bundle bundle = new Bundle();
                bundle.putString("android.os.storage.extra.FS_UUID", volumeInfo.getFsUuid());
                renameFragment.setArguments(bundle);
                renameFragment.show(privateVolumeSettings.getFragmentManager(), "rename");
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            FragmentActivity activity = getActivity();
            final StorageManager storageManager = (StorageManager) activity.getSystemService(StorageManager.class);
            final String string = getArguments().getString("android.os.storage.extra.FS_UUID");
            storageManager.findVolumeByUuid(string);
            VolumeRecord findRecordByUuid = storageManager.findRecordByUuid(string);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            View inflate = LayoutInflater.from(builder.getContext()).inflate(C0012R$layout.dialog_edittext, (ViewGroup) null, false);
            final EditText editText = (EditText) inflate.findViewById(C0010R$id.edittext);
            editText.setText(findRecordByUuid.getNickname());
            builder.setTitle(C0017R$string.storage_rename_title);
            builder.setView(inflate);
            builder.setPositiveButton(C0017R$string.save, new DialogInterface.OnClickListener(this) {
                /* class com.android.settings.deviceinfo.PrivateVolumeSettings.RenameFragment.AnonymousClass1 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    storageManager.setVolumeNickname(string, editText.getText().toString());
                }
            });
            builder.setNegativeButton(C0017R$string.cancel, (DialogInterface.OnClickListener) null);
            return builder.create();
        }
    }

    public static class SystemInfoFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 565;
        }

        public static void show(Fragment fragment) {
            if (fragment.isAdded()) {
                SystemInfoFragment systemInfoFragment = new SystemInfoFragment();
                systemInfoFragment.setTargetFragment(fragment, 0);
                systemInfoFragment.show(fragment.getFragmentManager(), "systemInfo");
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getContext().getString(C0017R$string.storage_detail_dialog_system, Build.VERSION.RELEASE_OR_CODENAME));
            builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            return builder.create();
        }
    }

    public static class OtherInfoFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 566;
        }

        public static void show(Fragment fragment, String str, VolumeInfo volumeInfo, int i) {
            if (fragment.isAdded()) {
                OtherInfoFragment otherInfoFragment = new OtherInfoFragment();
                otherInfoFragment.setTargetFragment(fragment, 0);
                Bundle bundle = new Bundle();
                bundle.putString("android.intent.extra.TITLE", str);
                Intent buildBrowseIntent = volumeInfo.buildBrowseIntent();
                buildBrowseIntent.putExtra("android.intent.extra.USER_ID", i);
                bundle.putParcelable("android.intent.extra.INTENT", buildBrowseIntent);
                otherInfoFragment.setArguments(bundle);
                otherInfoFragment.show(fragment.getFragmentManager(), "otherInfo");
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            FragmentActivity activity = getActivity();
            String string = getArguments().getString("android.intent.extra.TITLE");
            final Intent intent = (Intent) getArguments().getParcelable("android.intent.extra.INTENT");
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(TextUtils.expandTemplate(getText(C0017R$string.storage_detail_dialog_other), string));
            builder.setPositiveButton(C0017R$string.storage_menu_explore, new DialogInterface.OnClickListener() {
                /* class com.android.settings.deviceinfo.PrivateVolumeSettings.OtherInfoFragment.AnonymousClass1 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    Utils.launchIntent(OtherInfoFragment.this, intent);
                }
            });
            builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            return builder.create();
        }
    }

    public static class UserInfoFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 567;
        }

        public static void show(Fragment fragment, CharSequence charSequence, CharSequence charSequence2) {
            if (fragment.isAdded()) {
                UserInfoFragment userInfoFragment = new UserInfoFragment();
                userInfoFragment.setTargetFragment(fragment, 0);
                Bundle bundle = new Bundle();
                bundle.putCharSequence("android.intent.extra.TITLE", charSequence);
                bundle.putCharSequence("android.intent.extra.SUBJECT", charSequence2);
                userInfoFragment.setArguments(bundle);
                userInfoFragment.show(fragment.getFragmentManager(), "userInfo");
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            FragmentActivity activity = getActivity();
            CharSequence charSequence = getArguments().getCharSequence("android.intent.extra.TITLE");
            CharSequence charSequence2 = getArguments().getCharSequence("android.intent.extra.SUBJECT");
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(TextUtils.expandTemplate(getText(C0017R$string.storage_detail_dialog_user), charSequence, charSequence2));
            builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            return builder.create();
        }
    }

    public static class ConfirmClearCacheFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 564;
        }

        public static void show(Fragment fragment) {
            if (fragment.isAdded()) {
                ConfirmClearCacheFragment confirmClearCacheFragment = new ConfirmClearCacheFragment();
                confirmClearCacheFragment.setTargetFragment(fragment, 0);
                confirmClearCacheFragment.show(fragment.getFragmentManager(), "confirmClearCache");
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            final FragmentActivity activity = getActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(C0017R$string.memory_clear_cache_title);
            builder.setMessage(getString(C0017R$string.memory_clear_cache_message));
            builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
                /* class com.android.settings.deviceinfo.PrivateVolumeSettings.ConfirmClearCacheFragment.AnonymousClass1 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    PrivateVolumeSettings privateVolumeSettings = (PrivateVolumeSettings) ConfirmClearCacheFragment.this.getTargetFragment();
                    PackageManager packageManager = activity.getPackageManager();
                    int[] profileIdsWithDisabled = ((UserManager) activity.getSystemService(UserManager.class)).getProfileIdsWithDisabled(activity.getUserId());
                    for (int i2 : profileIdsWithDisabled) {
                        List<PackageInfo> installedPackagesAsUser = packageManager.getInstalledPackagesAsUser(0, i2);
                        ClearCacheObserver clearCacheObserver = new ClearCacheObserver(privateVolumeSettings, installedPackagesAsUser.size());
                        for (PackageInfo packageInfo : installedPackagesAsUser) {
                            packageManager.deleteApplicationCacheFilesAsUser(packageInfo.packageName, i2, clearCacheObserver);
                        }
                    }
                }
            });
            builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            return builder.create();
        }
    }

    private static class ClearCacheObserver extends IPackageDataObserver.Stub {
        private int mRemaining;
        private final PrivateVolumeSettings mTarget;

        public ClearCacheObserver(PrivateVolumeSettings privateVolumeSettings, int i) {
            this.mTarget = privateVolumeSettings;
            this.mRemaining = i;
        }

        public void onRemoveCompleted(String str, boolean z) {
            synchronized (this) {
                int i = this.mRemaining - 1;
                this.mRemaining = i;
                if (i == 0) {
                    this.mTarget.getActivity().runOnUiThread(new Runnable() {
                        /* class com.android.settings.deviceinfo.PrivateVolumeSettings.ClearCacheObserver.AnonymousClass1 */

                        public void run() {
                            ClearCacheObserver.this.mTarget.update();
                        }
                    });
                }
            }
        }
    }
}
