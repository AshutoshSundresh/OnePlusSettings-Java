package com.android.settings.deviceinfo.storage;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.VolumeInfo;
import android.util.Pair;
import android.util.SparseArray;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.Settings;
import com.android.settings.Utils;
import com.android.settings.applications.manageapplications.ManageApplications;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.deviceinfo.PrivateVolumeSettings;
import com.android.settings.deviceinfo.StorageItemPreference;
import com.android.settings.deviceinfo.storage.StorageAsyncLoader;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.applications.StorageStatsSource;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.deviceinfo.StorageVolumeProvider;

public class StorageItemPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    static final String AUDIO_KEY = "pref_music_audio";
    static final String FILES_KEY = "pref_files";
    static final String GAME_KEY = "pref_games";
    static final String MOVIES_KEY = "pref_movies";
    static final String OTHER_APPS_KEY = "pref_other_apps";
    static final String PHOTO_KEY = "pref_photos_videos";
    static final String SYSTEM_KEY = "pref_system";
    private StorageItemPreference mAppPreference;
    private StorageItemPreference mAudioPreference;
    private StorageItemPreference mFilePreference;
    private final Fragment mFragment;
    private StorageItemPreference mGamePreference;
    private boolean mIsWorkProfile;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private StorageItemPreference mMoviesPreference;
    private StorageItemPreference mPhotoPreference;
    private PreferenceScreen mScreen;
    private final StorageVolumeProvider mSvp;
    private StorageItemPreference mSystemPreference;
    private long mTotalSize;
    private long mUsedBytes;
    private int mUserId;
    private VolumeInfo mVolume;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public StorageItemPreferenceController(Context context, Fragment fragment, VolumeInfo volumeInfo, StorageVolumeProvider storageVolumeProvider) {
        super(context);
        this.mFragment = fragment;
        this.mVolume = volumeInfo;
        this.mSvp = storageVolumeProvider;
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mUserId = UserHandle.myUserId();
    }

    public StorageItemPreferenceController(Context context, Fragment fragment, VolumeInfo volumeInfo, StorageVolumeProvider storageVolumeProvider, boolean z) {
        this(context, fragment, volumeInfo, storageVolumeProvider);
        this.mIsWorkProfile = z;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (preference == null) {
            return false;
        }
        Intent intent = null;
        if (preference.getKey() == null) {
            return false;
        }
        String key = preference.getKey();
        char c = 65535;
        switch (key.hashCode()) {
            case -1642571429:
                if (key.equals(FILES_KEY)) {
                    c = 5;
                    break;
                }
                break;
            case -1641885275:
                if (key.equals(GAME_KEY)) {
                    c = 2;
                    break;
                }
                break;
            case -1488779334:
                if (key.equals(PHOTO_KEY)) {
                    c = 0;
                    break;
                }
                break;
            case 283435296:
                if (key.equals(AUDIO_KEY)) {
                    c = 1;
                    break;
                }
                break;
            case 826139871:
                if (key.equals(MOVIES_KEY)) {
                    c = 3;
                    break;
                }
                break;
            case 1007071179:
                if (key.equals(SYSTEM_KEY)) {
                    c = 6;
                    break;
                }
                break;
            case 1161100765:
                if (key.equals(OTHER_APPS_KEY)) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                intent = getPhotosIntent();
                break;
            case 1:
                intent = getAudioIntent();
                break;
            case 2:
                intent = getGamesIntent();
                break;
            case 3:
                intent = getMoviesIntent();
                break;
            case 4:
                if (this.mVolume != null) {
                    intent = getAppsIntent();
                    break;
                }
                break;
            case 5:
                intent = getFilesIntent();
                FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider().action(this.mContext, 841, new Pair[0]);
                break;
            case 6:
                PrivateVolumeSettings.SystemInfoFragment systemInfoFragment = new PrivateVolumeSettings.SystemInfoFragment();
                systemInfoFragment.setTargetFragment(this.mFragment, 0);
                systemInfoFragment.show(this.mFragment.getFragmentManager(), "SystemInfo");
                return true;
        }
        if (intent == null) {
            return super.handlePreferenceTreeClick(preference);
        }
        intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
        Utils.launchIntent(this.mFragment, intent);
        return true;
    }

    public void setVolume(VolumeInfo volumeInfo) {
        this.mVolume = volumeInfo;
        setFilesPreferenceVisibility();
    }

    private void setFilesPreferenceVisibility() {
        if (this.mScreen != null) {
            VolumeInfo findEmulatedForPrivate = this.mSvp.findEmulatedForPrivate(this.mVolume);
            if (findEmulatedForPrivate == null || !findEmulatedForPrivate.isMountedReadable()) {
                this.mScreen.removePreference(this.mFilePreference);
            } else {
                this.mScreen.addPreference(this.mFilePreference);
            }
        }
    }

    public void setUserId(UserHandle userHandle) {
        this.mUserId = userHandle.getIdentifier();
        tintPreference(this.mPhotoPreference);
        tintPreference(this.mMoviesPreference);
        tintPreference(this.mAudioPreference);
        tintPreference(this.mGamePreference);
        tintPreference(this.mAppPreference);
        tintPreference(this.mSystemPreference);
        tintPreference(this.mFilePreference);
    }

    private void tintPreference(Preference preference) {
        if (preference != null) {
            preference.setIcon(applyTint(this.mContext, preference.getIcon()));
        }
    }

    private static Drawable applyTint(Context context, Drawable drawable) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{16843817});
        Drawable mutate = drawable.mutate();
        mutate.setTint(obtainStyledAttributes.getColor(0, 0));
        obtainStyledAttributes.recycle();
        return mutate;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mScreen = preferenceScreen;
        this.mPhotoPreference = (StorageItemPreference) preferenceScreen.findPreference(PHOTO_KEY);
        this.mAudioPreference = (StorageItemPreference) preferenceScreen.findPreference(AUDIO_KEY);
        this.mGamePreference = (StorageItemPreference) preferenceScreen.findPreference(GAME_KEY);
        this.mMoviesPreference = (StorageItemPreference) preferenceScreen.findPreference(MOVIES_KEY);
        this.mAppPreference = (StorageItemPreference) preferenceScreen.findPreference(OTHER_APPS_KEY);
        this.mSystemPreference = (StorageItemPreference) preferenceScreen.findPreference(SYSTEM_KEY);
        this.mFilePreference = (StorageItemPreference) preferenceScreen.findPreference(FILES_KEY);
        setFilesPreferenceVisibility();
    }

    public void onLoadFinished(SparseArray<StorageAsyncLoader.AppsStorageResult> sparseArray, int i) {
        StorageAsyncLoader.AppsStorageResult appsStorageResult = sparseArray.get(i);
        StorageAsyncLoader.AppsStorageResult appsStorageResult2 = sparseArray.get(Utils.getManagedProfileId((UserManager) this.mContext.getSystemService(UserManager.class), i));
        this.mPhotoPreference.setStorageSize(getPhotosSize(appsStorageResult, appsStorageResult2), this.mTotalSize);
        this.mAudioPreference.setStorageSize(getAudioSize(appsStorageResult, appsStorageResult2), this.mTotalSize);
        this.mGamePreference.setStorageSize(getGamesSize(appsStorageResult, appsStorageResult2), this.mTotalSize);
        this.mMoviesPreference.setStorageSize(getMoviesSize(appsStorageResult, appsStorageResult2), this.mTotalSize);
        this.mAppPreference.setStorageSize(getAppsSize(appsStorageResult, appsStorageResult2), this.mTotalSize);
        this.mFilePreference.setStorageSize(getFilesSize(appsStorageResult, appsStorageResult2), this.mTotalSize);
        if (this.mSystemPreference != null) {
            long j = 0;
            for (int i2 = 0; i2 < sparseArray.size(); i2++) {
                StorageAsyncLoader.AppsStorageResult valueAt = sparseArray.valueAt(i2);
                StorageStatsSource.ExternalStorageStats externalStorageStats = valueAt.externalStats;
                j = j + valueAt.gamesSize + valueAt.musicAppsSize + valueAt.videoAppsSize + valueAt.photosAppsSize + valueAt.otherAppsSize + (externalStorageStats.totalBytes - externalStorageStats.appBytes);
            }
            this.mSystemPreference.setStorageSize(Math.max(1073741824L, this.mUsedBytes - j), this.mTotalSize);
        }
    }

    public void setUsedSize(long j) {
        this.mUsedBytes = j;
    }

    public void setTotalSize(long j) {
        this.mTotalSize = j;
    }

    private Intent getPhotosIntent() {
        Bundle workAnnotatedBundle = getWorkAnnotatedBundle(2);
        workAnnotatedBundle.putString("classname", Settings.PhotosStorageActivity.class.getName());
        workAnnotatedBundle.putInt("storageType", 3);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(ManageApplications.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.storage_photos_videos);
        subSettingLauncher.setArguments(workAnnotatedBundle);
        subSettingLauncher.setSourceMetricsCategory(this.mMetricsFeatureProvider.getMetricsCategory(this.mFragment));
        return subSettingLauncher.toIntent();
    }

    private long getPhotosSize(StorageAsyncLoader.AppsStorageResult appsStorageResult, StorageAsyncLoader.AppsStorageResult appsStorageResult2) {
        long j;
        long j2;
        if (appsStorageResult2 != null) {
            long j3 = appsStorageResult.photosAppsSize;
            StorageStatsSource.ExternalStorageStats externalStorageStats = appsStorageResult.externalStats;
            long j4 = j3 + externalStorageStats.imageBytes + externalStorageStats.videoBytes + appsStorageResult2.photosAppsSize;
            StorageStatsSource.ExternalStorageStats externalStorageStats2 = appsStorageResult2.externalStats;
            j = j4 + externalStorageStats2.imageBytes;
            j2 = externalStorageStats2.videoBytes;
        } else {
            long j5 = appsStorageResult.photosAppsSize;
            StorageStatsSource.ExternalStorageStats externalStorageStats3 = appsStorageResult.externalStats;
            j = j5 + externalStorageStats3.imageBytes;
            j2 = externalStorageStats3.videoBytes;
        }
        return j + j2;
    }

    private Intent getAudioIntent() {
        if (this.mVolume == null) {
            return null;
        }
        Bundle workAnnotatedBundle = getWorkAnnotatedBundle(4);
        workAnnotatedBundle.putString("classname", Settings.StorageUseActivity.class.getName());
        workAnnotatedBundle.putString("volumeUuid", this.mVolume.getFsUuid());
        workAnnotatedBundle.putString("volumeName", this.mVolume.getDescription());
        workAnnotatedBundle.putInt("storageType", 1);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(ManageApplications.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.storage_music_audio);
        subSettingLauncher.setArguments(workAnnotatedBundle);
        subSettingLauncher.setSourceMetricsCategory(this.mMetricsFeatureProvider.getMetricsCategory(this.mFragment));
        return subSettingLauncher.toIntent();
    }

    private long getAudioSize(StorageAsyncLoader.AppsStorageResult appsStorageResult, StorageAsyncLoader.AppsStorageResult appsStorageResult2) {
        long j;
        long j2;
        if (appsStorageResult2 != null) {
            j = appsStorageResult.musicAppsSize + appsStorageResult.externalStats.audioBytes + appsStorageResult2.musicAppsSize;
            j2 = appsStorageResult2.externalStats.audioBytes;
        } else {
            j = appsStorageResult.musicAppsSize;
            j2 = appsStorageResult.externalStats.audioBytes;
        }
        return j + j2;
    }

    private Intent getAppsIntent() {
        if (this.mVolume == null) {
            return null;
        }
        Bundle workAnnotatedBundle = getWorkAnnotatedBundle(3);
        workAnnotatedBundle.putString("classname", Settings.StorageUseActivity.class.getName());
        workAnnotatedBundle.putString("volumeUuid", this.mVolume.getFsUuid());
        workAnnotatedBundle.putString("volumeName", this.mVolume.getDescription());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(ManageApplications.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.apps_storage);
        subSettingLauncher.setArguments(workAnnotatedBundle);
        subSettingLauncher.setSourceMetricsCategory(this.mMetricsFeatureProvider.getMetricsCategory(this.mFragment));
        return subSettingLauncher.toIntent();
    }

    private long getAppsSize(StorageAsyncLoader.AppsStorageResult appsStorageResult, StorageAsyncLoader.AppsStorageResult appsStorageResult2) {
        if (appsStorageResult2 != null) {
            return appsStorageResult.otherAppsSize + appsStorageResult2.otherAppsSize;
        }
        return appsStorageResult.otherAppsSize;
    }

    private Intent getGamesIntent() {
        Bundle workAnnotatedBundle = getWorkAnnotatedBundle(1);
        workAnnotatedBundle.putString("classname", Settings.GamesStorageActivity.class.getName());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(ManageApplications.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.game_storage_settings);
        subSettingLauncher.setArguments(workAnnotatedBundle);
        subSettingLauncher.setSourceMetricsCategory(this.mMetricsFeatureProvider.getMetricsCategory(this.mFragment));
        return subSettingLauncher.toIntent();
    }

    private long getGamesSize(StorageAsyncLoader.AppsStorageResult appsStorageResult, StorageAsyncLoader.AppsStorageResult appsStorageResult2) {
        if (appsStorageResult2 != null) {
            return appsStorageResult.gamesSize + appsStorageResult2.gamesSize;
        }
        return appsStorageResult.gamesSize;
    }

    private Intent getMoviesIntent() {
        Bundle workAnnotatedBundle = getWorkAnnotatedBundle(1);
        workAnnotatedBundle.putString("classname", Settings.MoviesStorageActivity.class.getName());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(ManageApplications.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.storage_movies_tv);
        subSettingLauncher.setArguments(workAnnotatedBundle);
        subSettingLauncher.setSourceMetricsCategory(this.mMetricsFeatureProvider.getMetricsCategory(this.mFragment));
        return subSettingLauncher.toIntent();
    }

    private long getMoviesSize(StorageAsyncLoader.AppsStorageResult appsStorageResult, StorageAsyncLoader.AppsStorageResult appsStorageResult2) {
        if (appsStorageResult2 != null) {
            return appsStorageResult.videoAppsSize + appsStorageResult2.videoAppsSize;
        }
        return appsStorageResult.videoAppsSize;
    }

    private Bundle getWorkAnnotatedBundle(int i) {
        Bundle bundle = new Bundle(i + 1);
        bundle.putInt(":settings:show_fragment_tab", this.mIsWorkProfile ? 1 : 0);
        return bundle;
    }

    private Intent getFilesIntent() {
        return this.mSvp.findEmulatedForPrivate(this.mVolume).buildBrowseIntent();
    }

    private long getFilesSize(StorageAsyncLoader.AppsStorageResult appsStorageResult, StorageAsyncLoader.AppsStorageResult appsStorageResult2) {
        if (appsStorageResult2 != null) {
            StorageStatsSource.ExternalStorageStats externalStorageStats = appsStorageResult.externalStats;
            long j = (((externalStorageStats.totalBytes - externalStorageStats.audioBytes) - externalStorageStats.videoBytes) - externalStorageStats.imageBytes) - externalStorageStats.appBytes;
            StorageStatsSource.ExternalStorageStats externalStorageStats2 = appsStorageResult2.externalStats;
            return ((((j + externalStorageStats2.totalBytes) - externalStorageStats2.audioBytes) - externalStorageStats2.videoBytes) - externalStorageStats2.imageBytes) - externalStorageStats2.appBytes;
        }
        StorageStatsSource.ExternalStorageStats externalStorageStats3 = appsStorageResult.externalStats;
        return (((externalStorageStats3.totalBytes - externalStorageStats3.audioBytes) - externalStorageStats3.videoBytes) - externalStorageStats3.imageBytes) - externalStorageStats3.appBytes;
    }
}
