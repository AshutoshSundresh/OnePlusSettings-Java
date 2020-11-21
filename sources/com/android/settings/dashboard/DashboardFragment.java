package com.android.settings.dashboard;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0003R$array;
import com.android.settings.C0012R$layout;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerListHelper;
import com.android.settings.core.SettingsBaseActivity;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.support.SupportPreferenceController;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.ProviderTile;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class DashboardFragment extends SettingsPreferenceFragment implements SettingsBaseActivity.CategoryListener, PreferenceGroup.OnExpandButtonClickListener, BasePreferenceController.UiBlockListener {
    UiBlockerController mBlockerController;
    private final List<AbstractPreferenceController> mControllers = new ArrayList();
    private DashboardFeatureProvider mDashboardFeatureProvider;
    final ArrayMap<String, List<DynamicDataObserver>> mDashboardTilePrefKeys = new ArrayMap<>();
    private boolean mListeningToCategoryChange;
    private DashboardTilePlaceholderPreferenceController mPlaceholderPreferenceController;
    private final Map<Class, List<AbstractPreferenceController>> mPreferenceControllers = new ArrayMap();
    private final List<DynamicDataObserver> mRegisteredObservers = new ArrayList();
    private List<String> mSuppressInjectedTileKeys;

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return null;
    }

    /* access modifiers changed from: protected */
    public abstract String getLogTag();

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public abstract int getPreferenceScreenResId();

    /* access modifiers changed from: protected */
    public boolean isParalleledControllers() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean shouldForceRoundedIcon() {
        return false;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mSuppressInjectedTileKeys = Arrays.asList(context.getResources().getStringArray(C0003R$array.config_suppress_injected_tile_keys));
        this.mDashboardFeatureProvider = FeatureFactory.getFactory(context).getDashboardFeatureProvider(context);
        List<AbstractPreferenceController> createPreferenceControllers = createPreferenceControllers(context);
        List<BasePreferenceController> filterControllers = PreferenceControllerListHelper.filterControllers(PreferenceControllerListHelper.getPreferenceControllersFromXml(context, getPreferenceScreenResId()), createPreferenceControllers);
        if (createPreferenceControllers != null) {
            this.mControllers.addAll(createPreferenceControllers);
        }
        this.mControllers.addAll(filterControllers);
        filterControllers.forEach(new Consumer() {
            /* class com.android.settings.dashboard.$$Lambda$DashboardFragment$zmtHbM63q2RY6Eq7lU0CIYkqoNE */

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.lambda$onAttach$0(Lifecycle.this, (BasePreferenceController) obj);
            }
        });
        this.mControllers.forEach(new Consumer(getMetricsCategory()) {
            /* class com.android.settings.dashboard.$$Lambda$DashboardFragment$HhsucvAKNfjcclJaEvRE6wEtAc */
            public final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.lambda$onAttach$1(this.f$0, (AbstractPreferenceController) obj);
            }
        });
        DashboardTilePlaceholderPreferenceController dashboardTilePlaceholderPreferenceController = new DashboardTilePlaceholderPreferenceController(context);
        this.mPlaceholderPreferenceController = dashboardTilePlaceholderPreferenceController;
        this.mControllers.add(dashboardTilePlaceholderPreferenceController);
        for (AbstractPreferenceController abstractPreferenceController : this.mControllers) {
            addPreferenceController(abstractPreferenceController);
        }
    }

    static /* synthetic */ void lambda$onAttach$0(Lifecycle lifecycle, BasePreferenceController basePreferenceController) {
        if (basePreferenceController instanceof LifecycleObserver) {
            lifecycle.addObserver((LifecycleObserver) basePreferenceController);
        }
    }

    static /* synthetic */ void lambda$onAttach$1(int i, AbstractPreferenceController abstractPreferenceController) {
        if (abstractPreferenceController instanceof BasePreferenceController) {
            ((BasePreferenceController) abstractPreferenceController).setMetricsCategory(i);
        }
    }

    /* access modifiers changed from: package-private */
    public void checkUiBlocker(List<AbstractPreferenceController> list) {
        ArrayList arrayList = new ArrayList();
        list.forEach(new Consumer(arrayList) {
            /* class com.android.settings.dashboard.$$Lambda$DashboardFragment$X8ujeNIfsIIfi9MgEB3su5GTRe0 */
            public final /* synthetic */ List f$1;

            {
                this.f$1 = r2;
            }

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.this.lambda$checkUiBlocker$2$DashboardFragment(this.f$1, (AbstractPreferenceController) obj);
            }
        });
        if (!arrayList.isEmpty()) {
            UiBlockerController uiBlockerController = new UiBlockerController(arrayList);
            this.mBlockerController = uiBlockerController;
            uiBlockerController.start(new Runnable() {
                /* class com.android.settings.dashboard.$$Lambda$DashboardFragment$cFFmLzl_a556_FbliTJesDjyJUw */

                public final void run() {
                    DashboardFragment.this.lambda$checkUiBlocker$3$DashboardFragment();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkUiBlocker$2 */
    public /* synthetic */ void lambda$checkUiBlocker$2$DashboardFragment(List list, AbstractPreferenceController abstractPreferenceController) {
        if ((abstractPreferenceController instanceof BasePreferenceController.UiBlocker) && abstractPreferenceController.isAvailable()) {
            ((BasePreferenceController) abstractPreferenceController).setUiBlockListener(this);
            list.add(abstractPreferenceController.getPreferenceKey());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkUiBlocker$3 */
    public /* synthetic */ void lambda$checkUiBlocker$3$DashboardFragment() {
        updatePreferenceVisibility(this.mPreferenceControllers);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            updatePreferenceStates();
        }
    }

    @Override // com.android.settings.core.SettingsBaseActivity.CategoryListener
    public void onCategoriesChanged() {
        if (this.mDashboardFeatureProvider.getTilesForCategory(getCategoryKey()) != null) {
            refreshDashboardTiles(getLogTag());
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment
    public void onCreatePreferences(Bundle bundle, String str) {
        checkUiBlocker(this.mControllers);
        refreshAllPreferences(getLogTag());
        this.mControllers.stream().map(new Function() {
            /* class com.android.settings.dashboard.$$Lambda$DashboardFragment$8zVWO_z0P7kuvXD1FDza9ZaZYKc */

            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return DashboardFragment.this.lambda$onCreatePreferences$4$DashboardFragment((AbstractPreferenceController) obj);
            }
        }).filter($$Lambda$_ih7F203tvzC4zolkEMwsKXIB9w.INSTANCE).forEach(new Consumer() {
            /* class com.android.settings.dashboard.$$Lambda$DashboardFragment$6kL4jxZe5kaB9hdUUgrFB6ZR9yU */

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DashboardFragment.this.lambda$onCreatePreferences$5$DashboardFragment((Preference) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreatePreferences$4 */
    public /* synthetic */ Preference lambda$onCreatePreferences$4$DashboardFragment(AbstractPreferenceController abstractPreferenceController) {
        return findPreference(abstractPreferenceController.getPreferenceKey());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreatePreferences$5 */
    public /* synthetic */ void lambda$onCreatePreferences$5$DashboardFragment(Preference preference) {
        preference.getExtras().putInt("category", getMetricsCategory());
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        if (this.mDashboardFeatureProvider.getTilesForCategory(getCategoryKey()) != null) {
            FragmentActivity activity = getActivity();
            if (activity instanceof SettingsBaseActivity) {
                this.mListeningToCategoryChange = true;
                ((SettingsBaseActivity) activity).addCategoryListener(this);
            }
            this.mDashboardTilePrefKeys.values().stream().filter($$Lambda$wXXh49TgHwGJpKCtXLpXf0v4kts.INSTANCE).flatMap($$Lambda$seyL25CSW2NInOydsTbSDrNW6pM.INSTANCE).forEach(new Consumer(getContentResolver()) {
                /* class com.android.settings.dashboard.$$Lambda$DashboardFragment$Nz4Fdb2TsGdCNeUAbeOcGCam25I */
                public final /* synthetic */ ContentResolver f$1;

                {
                    this.f$1 = r2;
                }

                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DashboardFragment.this.lambda$onStart$6$DashboardFragment(this.f$1, (DynamicDataObserver) obj);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onStart$6 */
    public /* synthetic */ void lambda$onStart$6$DashboardFragment(ContentResolver contentResolver, DynamicDataObserver dynamicDataObserver) {
        if (!this.mRegisteredObservers.contains(dynamicDataObserver)) {
            lambda$registerDynamicDataObservers$8(contentResolver, dynamicDataObserver);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updatePreferenceStates();
        writeElapsedTimeMetric(1729, "isParalleledControllers:" + isParalleledControllers());
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        for (List<AbstractPreferenceController> list : this.mPreferenceControllers.values()) {
            Iterator<AbstractPreferenceController> it = list.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().handlePreferenceTreeClick(preference)) {
                        writePreferenceClickMetric(preference);
                        return true;
                    }
                }
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        unregisterDynamicDataObservers(new ArrayList(this.mRegisteredObservers));
        if (this.mListeningToCategoryChange) {
            FragmentActivity activity = getActivity();
            if (activity instanceof SettingsBaseActivity) {
                ((SettingsBaseActivity) activity).remCategoryListener(this);
            }
            this.mListeningToCategoryChange = false;
        }
    }

    @Override // androidx.preference.PreferenceGroup.OnExpandButtonClickListener
    public void onExpandButtonClick() {
        this.mMetricsFeatureProvider.action(0, 834, getMetricsCategory(), null, 0);
    }

    /* access modifiers changed from: protected */
    public <T extends AbstractPreferenceController> T use(Class<T> cls) {
        List<AbstractPreferenceController> list = this.mPreferenceControllers.get(cls);
        if (list == null) {
            return null;
        }
        if (list.size() > 1) {
            Log.w("DashboardFragment", "Multiple controllers of Class " + cls.getSimpleName() + " found, returning first one.");
        }
        return (T) list.get(0);
    }

    /* access modifiers changed from: protected */
    public void addPreferenceController(AbstractPreferenceController abstractPreferenceController) {
        if (this.mPreferenceControllers.get(abstractPreferenceController.getClass()) == null) {
            this.mPreferenceControllers.put(abstractPreferenceController.getClass(), new ArrayList());
        }
        this.mPreferenceControllers.get(abstractPreferenceController.getClass()).add(abstractPreferenceController);
    }

    public String getCategoryKey() {
        return DashboardFragmentRegistry.PARENT_TO_CATEGORY_KEY_MAP.get(getClass().getName());
    }

    /* access modifiers changed from: protected */
    public boolean displayTile(Tile tile) {
        if (this.mSuppressInjectedTileKeys == null || !tile.hasKey()) {
            return true;
        }
        return !this.mSuppressInjectedTileKeys.contains(tile.getKey(getContext()));
    }

    private void displayResourceTiles() {
        int preferenceScreenResId = getPreferenceScreenResId();
        if (preferenceScreenResId > 0) {
            addPreferencesFromResource(preferenceScreenResId);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            preferenceScreen.setOnExpandButtonClickListener(this);
            displayResourceTilesToScreen(preferenceScreen);
        }
    }

    /* access modifiers changed from: protected */
    public void displayResourceTilesToScreen(PreferenceScreen preferenceScreen) {
        this.mPreferenceControllers.values().stream().flatMap($$Lambda$seyL25CSW2NInOydsTbSDrNW6pM.INSTANCE).forEach(new Consumer() {
            /* class com.android.settings.dashboard.$$Lambda$DashboardFragment$9yfkfZlETf1BygVuDmLc47Z008Y */

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((AbstractPreferenceController) obj).displayPreference(PreferenceScreen.this);
            }
        });
    }

    /* access modifiers changed from: protected */
    public Collection<List<AbstractPreferenceController>> getPreferenceControllers() {
        return this.mPreferenceControllers.values();
    }

    /* access modifiers changed from: protected */
    public void updatePreferenceStates() {
        Preference findPreference;
        if (!isParalleledControllers() || !FeatureFlagUtils.isEnabled(getContext(), "settings_controller_loading_enhancement")) {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            for (List<AbstractPreferenceController> list : this.mPreferenceControllers.values()) {
                for (AbstractPreferenceController abstractPreferenceController : list) {
                    if (abstractPreferenceController.isAvailable()) {
                        String preferenceKey = abstractPreferenceController.getPreferenceKey();
                        if (TextUtils.isEmpty(preferenceKey)) {
                            Log.d("DashboardFragment", String.format("Preference key is %s in Controller %s", preferenceKey, abstractPreferenceController.getClass().getSimpleName()));
                        } else {
                            Preference findPreference2 = preferenceScreen.findPreference(preferenceKey);
                            if (findPreference2 == null) {
                                Log.d("DashboardFragment", String.format("Cannot find preference with key %s in Controller %s", preferenceKey, abstractPreferenceController.getClass().getSimpleName()));
                            } else {
                                abstractPreferenceController.updateState(findPreference2);
                            }
                        }
                    } else if ((abstractPreferenceController instanceof SupportPreferenceController) && (findPreference = preferenceScreen.findPreference("top_level_about_device")) != null) {
                        findPreference.setLayoutResource(C0012R$layout.op_home_preference_card_bottom);
                    }
                }
            }
            return;
        }
        updatePreferenceStatesInParallel();
    }

    /* access modifiers changed from: package-private */
    public void updatePreferenceStatesInParallel() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Collection<List<AbstractPreferenceController>> values = this.mPreferenceControllers.values();
        ArrayList<ControllerFutureTask> arrayList = new ArrayList();
        for (List<AbstractPreferenceController> list : values) {
            for (AbstractPreferenceController abstractPreferenceController : list) {
                ControllerFutureTask controllerFutureTask = new ControllerFutureTask(new ControllerTask(abstractPreferenceController, preferenceScreen, this.mMetricsFeatureProvider, getMetricsCategory()), null);
                arrayList.add(controllerFutureTask);
                ThreadUtils.postOnBackgroundThread(controllerFutureTask);
            }
        }
        for (ControllerFutureTask controllerFutureTask2 : arrayList) {
            try {
                controllerFutureTask2.get();
            } catch (InterruptedException | ExecutionException e) {
                Log.w("DashboardFragment", controllerFutureTask2.getController().getPreferenceKey() + " " + e.getMessage());
            }
        }
    }

    private void refreshAllPreferences(String str) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.removeAll();
        }
        displayResourceTiles();
        refreshDashboardTiles(str);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Log.d(str, "All preferences added, reporting fully drawn");
            activity.reportFullyDrawn();
        }
        updatePreferenceVisibility(this.mPreferenceControllers);
    }

    /* access modifiers changed from: package-private */
    public void updatePreferenceVisibility(Map<Class, List<AbstractPreferenceController>> map) {
        UiBlockerController uiBlockerController;
        if (!(getPreferenceScreen() == null || map == null || (uiBlockerController = this.mBlockerController) == null)) {
            boolean isBlockerFinished = uiBlockerController.isBlockerFinished();
            for (List<AbstractPreferenceController> list : map.values()) {
                for (AbstractPreferenceController abstractPreferenceController : list) {
                    Preference findPreference = findPreference(abstractPreferenceController.getPreferenceKey());
                    if (findPreference != null) {
                        findPreference.setVisible(isBlockerFinished && abstractPreferenceController.isAvailable());
                    }
                }
            }
        }
    }

    private void refreshDashboardTiles(String str) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        DashboardCategory tilesForCategory = this.mDashboardFeatureProvider.getTilesForCategory(getCategoryKey());
        if (tilesForCategory == null) {
            Log.d(str, "NO dashboard tiles for " + str);
            return;
        }
        List<Tile> tiles = tilesForCategory.getTiles();
        if (tiles == null) {
            Log.d(str, "tile list is empty, skipping category " + tilesForCategory.key);
            return;
        }
        ArrayMap arrayMap = new ArrayMap(this.mDashboardTilePrefKeys);
        boolean shouldForceRoundedIcon = shouldForceRoundedIcon();
        for (Tile tile : tiles) {
            String dashboardKeyForTile = this.mDashboardFeatureProvider.getDashboardKeyForTile(tile);
            if (TextUtils.isEmpty(dashboardKeyForTile)) {
                Log.d(str, "tile does not contain a key, skipping " + tile);
            } else if (displayTile(tile)) {
                if (this.mDashboardTilePrefKeys.containsKey(dashboardKeyForTile)) {
                    this.mDashboardFeatureProvider.bindPreferenceToTileAndGetObservers(getActivity(), shouldForceRoundedIcon, getMetricsCategory(), preferenceScreen.findPreference(dashboardKeyForTile), tile, dashboardKeyForTile, this.mPlaceholderPreferenceController.getOrder());
                } else {
                    Preference createPreference = createPreference(tile);
                    List<DynamicDataObserver> bindPreferenceToTileAndGetObservers = this.mDashboardFeatureProvider.bindPreferenceToTileAndGetObservers(getActivity(), shouldForceRoundedIcon, getMetricsCategory(), createPreference, tile, dashboardKeyForTile, this.mPlaceholderPreferenceController.getOrder());
                    Log.d(str, "pref.getKey() =  " + createPreference.getKey());
                    if (createPreference.getKey().contains("com.google.android.apps.wellbeing.settings")) {
                        createPreference.setLayoutResource(C0012R$layout.op_home_preference_card_middle);
                        Preference findPreference = getPreferenceScreen().findPreference("top_level_accounts");
                        if (findPreference != null) {
                            Log.d("DashboardFragment", "account setLayoutResource");
                            findPreference.setLayoutResource(C0012R$layout.op_home_preference_card_middle);
                        }
                    }
                    if (createPreference.getKey().contains("com.google.android.gms.app.settings")) {
                        createPreference.setLayoutResource(C0012R$layout.op_home_preference_card_bottom);
                        Preference findPreference2 = getPreferenceScreen().findPreference("top_level_accounts");
                        if (findPreference2 != null) {
                            Log.d("DashboardFragment", "account setLayoutResource");
                            findPreference2.setLayoutResource(C0012R$layout.op_home_preference_card_middle);
                        }
                    }
                    preferenceScreen.addPreference(createPreference);
                    registerDynamicDataObservers(bindPreferenceToTileAndGetObservers);
                    this.mDashboardTilePrefKeys.put(dashboardKeyForTile, bindPreferenceToTileAndGetObservers);
                }
                arrayMap.remove(dashboardKeyForTile);
            }
        }
        for (Map.Entry entry : arrayMap.entrySet()) {
            String str2 = (String) entry.getKey();
            this.mDashboardTilePrefKeys.remove(str2);
            Preference findPreference3 = preferenceScreen.findPreference(str2);
            if (findPreference3 != null) {
                preferenceScreen.removePreference(findPreference3);
            }
            unregisterDynamicDataObservers((List) entry.getValue());
        }
    }

    @Override // com.android.settings.core.BasePreferenceController.UiBlockListener
    public void onBlockerWorkFinished(BasePreferenceController basePreferenceController) {
        this.mBlockerController.countDown(basePreferenceController.getPreferenceKey());
    }

    /* access modifiers changed from: package-private */
    public Preference createPreference(Tile tile) {
        if (tile instanceof ProviderTile) {
            return new SwitchPreference(getPrefContext());
        }
        if (tile.hasSwitch()) {
            return new MasterSwitchPreference(getPrefContext());
        }
        return new Preference(getPrefContext());
    }

    /* access modifiers changed from: package-private */
    public void registerDynamicDataObservers(List<DynamicDataObserver> list) {
        if (list != null && !list.isEmpty()) {
            list.forEach(new Consumer(getContentResolver()) {
                /* class com.android.settings.dashboard.$$Lambda$DashboardFragment$7XlKrr_sUhGxGieX_SrpuU26fx4 */
                public final /* synthetic */ ContentResolver f$1;

                {
                    this.f$1 = r2;
                }

                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DashboardFragment.this.lambda$registerDynamicDataObservers$8$DashboardFragment(this.f$1, (DynamicDataObserver) obj);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: registerDynamicDataObserver */
    public void lambda$registerDynamicDataObservers$8(ContentResolver contentResolver, DynamicDataObserver dynamicDataObserver) {
        Log.d("DashboardFragment", "register observer: @" + Integer.toHexString(dynamicDataObserver.hashCode()) + ", uri: " + dynamicDataObserver.getUri());
        contentResolver.registerContentObserver(dynamicDataObserver.getUri(), false, dynamicDataObserver);
        this.mRegisteredObservers.add(dynamicDataObserver);
    }

    private void unregisterDynamicDataObservers(List<DynamicDataObserver> list) {
        if (list != null && !list.isEmpty()) {
            list.forEach(new Consumer(getContentResolver()) {
                /* class com.android.settings.dashboard.$$Lambda$DashboardFragment$l2SMxKZAvNA7QD7Qfg6sLzaogw */
                public final /* synthetic */ ContentResolver f$1;

                {
                    this.f$1 = r2;
                }

                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DashboardFragment.this.lambda$unregisterDynamicDataObservers$9$DashboardFragment(this.f$1, (DynamicDataObserver) obj);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$unregisterDynamicDataObservers$9 */
    public /* synthetic */ void lambda$unregisterDynamicDataObservers$9$DashboardFragment(ContentResolver contentResolver, DynamicDataObserver dynamicDataObserver) {
        Log.d("DashboardFragment", "unregister observer: @" + Integer.toHexString(dynamicDataObserver.hashCode()) + ", uri: " + dynamicDataObserver.getUri());
        this.mRegisteredObservers.remove(dynamicDataObserver);
        contentResolver.unregisterContentObserver(dynamicDataObserver);
    }
}
