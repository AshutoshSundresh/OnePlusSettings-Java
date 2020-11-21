package com.android.settings.deviceinfo;

import android.app.Activity;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.provider.SearchIndexableResource;
import android.util.SparseArray;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.deviceinfo.StorageDashboardFragment;
import com.android.settings.deviceinfo.storage.AutomaticStorageManagementSwitchPreferenceController;
import com.android.settings.deviceinfo.storage.CachedStorageValuesHelper;
import com.android.settings.deviceinfo.storage.SecondaryUserController;
import com.android.settings.deviceinfo.storage.StorageAsyncLoader;
import com.android.settings.deviceinfo.storage.StorageItemPreferenceController;
import com.android.settings.deviceinfo.storage.UserIconLoader;
import com.android.settings.deviceinfo.storage.VolumeSizesLoader;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.applications.StorageStatsSource;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.deviceinfo.PrivateStorageInfo;
import com.android.settingslib.deviceinfo.StorageManagerVolumeProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class StorageDashboardFragment extends DashboardFragment implements LoaderManager.LoaderCallbacks<SparseArray<StorageAsyncLoader.AppsStorageResult>> {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.deviceinfo.StorageDashboardFragment.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.storage_dashboard_fragment;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new StorageItemPreferenceController(context, null, null, new StorageManagerVolumeProvider((StorageManager) context.getSystemService(StorageManager.class))));
            arrayList.addAll(SecondaryUserController.getSecondaryUserControllers(context, (UserManager) context.getSystemService(UserManager.class)));
            return arrayList;
        }
    };
    private SparseArray<StorageAsyncLoader.AppsStorageResult> mAppsResult;
    private CachedStorageValuesHelper mCachedStorageValuesHelper;
    private PrivateVolumeOptionMenuController mOptionMenuController;
    private boolean mPersonalOnly;
    private StorageItemPreferenceController mPreferenceController;
    private List<AbstractPreferenceController> mSecondaryUsers;
    private PrivateStorageInfo mStorageInfo;
    private VolumeInfo mVolume;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "StorageDashboardFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 745;
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<SparseArray<StorageAsyncLoader.AppsStorageResult>> loader) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        Preference findPreference;
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mVolume = Utils.maybeInitializeVolume((StorageManager) activity.getSystemService(StorageManager.class), getArguments());
        boolean z = true;
        if (getArguments().getInt("profile") != 1) {
            z = false;
        }
        this.mPersonalOnly = z;
        if (this.mVolume == null) {
            activity.finish();
            return;
        }
        initializeOptionsMenu(activity);
        if (this.mPersonalOnly && (findPreference = getPreferenceScreen().findPreference("storage_summary")) != null) {
            findPreference.setVisible(false);
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((AutomaticStorageManagementSwitchPreferenceController) use(AutomaticStorageManagementSwitchPreferenceController.class)).setFragmentManager(getFragmentManager());
    }

    /* access modifiers changed from: package-private */
    public void initializeOptionsMenu(Activity activity) {
        this.mOptionMenuController = new PrivateVolumeOptionMenuController(activity, this.mVolume, activity.getPackageManager());
        getSettingsLifecycle().addObserver(this.mOptionMenuController);
        setHasOptionsMenu(true);
        activity.invalidateOptionsMenu();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        initializeCacheProvider();
        maybeSetLoading(isQuotaSupported());
        FragmentActivity activity = getActivity();
        EntityHeaderController newInstance = EntityHeaderController.newInstance(activity, this, null);
        newInstance.setRecyclerView(getListView(), getSettingsLifecycle());
        newInstance.styleActionBar(activity);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, Bundle.EMPTY, this);
        getLoaderManager().restartLoader(2, Bundle.EMPTY, new VolumeSizeCallbacks());
        getLoaderManager().restartLoader(1, Bundle.EMPTY, new IconLoaderCallbacks());
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_storage_dashboard;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onReceivedSizes() {
        boolean z;
        PrivateStorageInfo privateStorageInfo = this.mStorageInfo;
        if (privateStorageInfo != null) {
            long j = privateStorageInfo.totalBytes - privateStorageInfo.freeBytes;
            this.mPreferenceController.setVolume(this.mVolume);
            this.mPreferenceController.setUsedSize(j);
            this.mPreferenceController.setTotalSize(this.mStorageInfo.totalBytes);
            int size = this.mSecondaryUsers.size();
            for (int i = 0; i < size; i++) {
                AbstractPreferenceController abstractPreferenceController = this.mSecondaryUsers.get(i);
                if (abstractPreferenceController instanceof SecondaryUserController) {
                    ((SecondaryUserController) abstractPreferenceController).setTotalSize(this.mStorageInfo.totalBytes);
                }
            }
            z = true;
        } else {
            z = false;
        }
        SparseArray<StorageAsyncLoader.AppsStorageResult> sparseArray = this.mAppsResult;
        if (sparseArray != null) {
            this.mPreferenceController.onLoadFinished(sparseArray, UserHandle.myUserId());
            updateSecondaryUserControllers(this.mSecondaryUsers, this.mAppsResult);
            z = true;
        }
        if (z && getView().findViewById(C0010R$id.loading_container).getVisibility() == 0) {
            setLoading(false, true);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.storage_dashboard_fragment;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        StorageItemPreferenceController storageItemPreferenceController = new StorageItemPreferenceController(context, this, this.mVolume, new StorageManagerVolumeProvider((StorageManager) context.getSystemService(StorageManager.class)));
        this.mPreferenceController = storageItemPreferenceController;
        arrayList.add(storageItemPreferenceController);
        List<AbstractPreferenceController> secondaryUserControllers = SecondaryUserController.getSecondaryUserControllers(context, (UserManager) context.getSystemService(UserManager.class));
        this.mSecondaryUsers = secondaryUserControllers;
        arrayList.addAll(secondaryUserControllers);
        return arrayList;
    }

    /* access modifiers changed from: protected */
    public void setVolume(VolumeInfo volumeInfo) {
        this.mVolume = volumeInfo;
    }

    private void updateSecondaryUserControllers(List<AbstractPreferenceController> list, SparseArray<StorageAsyncLoader.AppsStorageResult> sparseArray) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            AbstractPreferenceController abstractPreferenceController = list.get(i);
            if (abstractPreferenceController instanceof StorageAsyncLoader.ResultHandler) {
                ((StorageAsyncLoader.ResultHandler) abstractPreferenceController).handleResult(sparseArray);
            }
        }
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<SparseArray<StorageAsyncLoader.AppsStorageResult>> onCreateLoader(int i, Bundle bundle) {
        Context context = getContext();
        return new StorageAsyncLoader(context, (UserManager) context.getSystemService(UserManager.class), this.mVolume.fsUuid, new StorageStatsSource(context), context.getPackageManager());
    }

    public void onLoadFinished(Loader<SparseArray<StorageAsyncLoader.AppsStorageResult>> loader, SparseArray<StorageAsyncLoader.AppsStorageResult> sparseArray) {
        this.mAppsResult = sparseArray;
        maybeCacheFreshValues();
        onReceivedSizes();
    }

    public void setCachedStorageValuesHelper(CachedStorageValuesHelper cachedStorageValuesHelper) {
        this.mCachedStorageValuesHelper = cachedStorageValuesHelper;
    }

    public PrivateStorageInfo getPrivateStorageInfo() {
        return this.mStorageInfo;
    }

    public void setPrivateStorageInfo(PrivateStorageInfo privateStorageInfo) {
        this.mStorageInfo = privateStorageInfo;
    }

    public SparseArray<StorageAsyncLoader.AppsStorageResult> getAppsStorageResult() {
        return this.mAppsResult;
    }

    public void setAppsStorageResult(SparseArray<StorageAsyncLoader.AppsStorageResult> sparseArray) {
        this.mAppsResult = sparseArray;
    }

    public void initializeCachedValues() {
        PrivateStorageInfo cachedPrivateStorageInfo = this.mCachedStorageValuesHelper.getCachedPrivateStorageInfo();
        SparseArray<StorageAsyncLoader.AppsStorageResult> cachedAppsStorageResult = this.mCachedStorageValuesHelper.getCachedAppsStorageResult();
        if (cachedPrivateStorageInfo != null && cachedAppsStorageResult != null) {
            this.mStorageInfo = cachedPrivateStorageInfo;
            this.mAppsResult = cachedAppsStorageResult;
        }
    }

    public void maybeSetLoading(boolean z) {
        if ((z && (this.mStorageInfo == null || this.mAppsResult == null)) || (!z && this.mStorageInfo == null)) {
            setLoading(true, false);
        }
    }

    private void initializeCacheProvider() {
        this.mCachedStorageValuesHelper = new CachedStorageValuesHelper(getContext(), UserHandle.myUserId());
        initializeCachedValues();
        onReceivedSizes();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void maybeCacheFreshValues() {
        SparseArray<StorageAsyncLoader.AppsStorageResult> sparseArray;
        PrivateStorageInfo privateStorageInfo = this.mStorageInfo;
        if (privateStorageInfo != null && (sparseArray = this.mAppsResult) != null) {
            this.mCachedStorageValuesHelper.cacheResult(privateStorageInfo, sparseArray.get(UserHandle.myUserId()));
        }
    }

    private boolean isQuotaSupported() {
        try {
            return ((StorageStatsManager) getActivity().getSystemService(StorageStatsManager.class)).isQuotaSupported(this.mVolume.fsUuid);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public final class IconLoaderCallbacks implements LoaderManager.LoaderCallbacks<SparseArray<Drawable>> {
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<SparseArray<Drawable>> loader) {
        }

        public IconLoaderCallbacks() {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<SparseArray<Drawable>> onCreateLoader(int i, Bundle bundle) {
            return new UserIconLoader(StorageDashboardFragment.this.getContext(), new UserIconLoader.FetchUserIconTask() {
                /* class com.android.settings.deviceinfo.$$Lambda$StorageDashboardFragment$IconLoaderCallbacks$yGwysNy4Bq4_2nwwvU2QePhZgvU */

                @Override // com.android.settings.deviceinfo.storage.UserIconLoader.FetchUserIconTask
                public final SparseArray getUserIcons() {
                    return StorageDashboardFragment.IconLoaderCallbacks.this.lambda$onCreateLoader$0$StorageDashboardFragment$IconLoaderCallbacks();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCreateLoader$0 */
        public /* synthetic */ SparseArray lambda$onCreateLoader$0$StorageDashboardFragment$IconLoaderCallbacks() {
            return UserIconLoader.loadUserIconsWithContext(StorageDashboardFragment.this.getContext());
        }

        public void onLoadFinished(Loader<SparseArray<Drawable>> loader, SparseArray<Drawable> sparseArray) {
            StorageDashboardFragment.this.mSecondaryUsers.stream().filter($$Lambda$StorageDashboardFragment$IconLoaderCallbacks$7UIHa462aQ5cO1d2zsPI99b5Y1Y.INSTANCE).forEach(new Consumer(sparseArray) {
                /* class com.android.settings.deviceinfo.$$Lambda$StorageDashboardFragment$IconLoaderCallbacks$Jn0eBlqBHbuO2COJ4jEmaXSJJc */
                public final /* synthetic */ SparseArray f$0;

                {
                    this.f$0 = r1;
                }

                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((UserIconLoader.UserIconHandler) ((AbstractPreferenceController) obj)).handleUserIcons(this.f$0);
                }
            });
        }

        static /* synthetic */ boolean lambda$onLoadFinished$1(AbstractPreferenceController abstractPreferenceController) {
            return abstractPreferenceController instanceof UserIconLoader.UserIconHandler;
        }
    }

    public final class VolumeSizeCallbacks implements LoaderManager.LoaderCallbacks<PrivateStorageInfo> {
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<PrivateStorageInfo> loader) {
        }

        public VolumeSizeCallbacks() {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<PrivateStorageInfo> onCreateLoader(int i, Bundle bundle) {
            Context context = StorageDashboardFragment.this.getContext();
            return new VolumeSizesLoader(context, new StorageManagerVolumeProvider((StorageManager) context.getSystemService(StorageManager.class)), (StorageStatsManager) context.getSystemService(StorageStatsManager.class), StorageDashboardFragment.this.mVolume);
        }

        public void onLoadFinished(Loader<PrivateStorageInfo> loader, PrivateStorageInfo privateStorageInfo) {
            if (privateStorageInfo == null) {
                StorageDashboardFragment.this.getActivity().finish();
                return;
            }
            StorageDashboardFragment.this.mStorageInfo = privateStorageInfo;
            StorageDashboardFragment.this.maybeCacheFreshValues();
            StorageDashboardFragment.this.onReceivedSizes();
        }
    }
}
