package com.android.settings.slices;

import android.app.PendingIntent;
import android.app.slice.SliceManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.KeyValueListParser;
import android.util.Log;
import androidx.collection.ArraySet;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.Utils;
import com.android.settings.bluetooth.BluetoothSliceBuilder;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.notification.zen.ZenModeSliceBuilder;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.SliceBroadcastRelay;
import com.android.settingslib.utils.ThreadUtils;
import com.oneplus.settings.OPMemberController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SettingsSliceProvider extends SliceProvider {
    private static final KeyValueListParser KEY_VALUE_LIST_PARSER = new KeyValueListParser(',');
    private static final List<Uri> PUBLICLY_SUPPORTED_CUSTOM_SLICE_URIS = Arrays.asList(CustomSliceRegistry.BLUETOOTH_URI, CustomSliceRegistry.FLASHLIGHT_SLICE_URI, CustomSliceRegistry.LOCATION_SLICE_URI, CustomSliceRegistry.MOBILE_DATA_SLICE_URI, CustomSliceRegistry.WIFI_CALLING_URI, CustomSliceRegistry.WIFI_SLICE_URI, CustomSliceRegistry.ZEN_MODE_SLICE_URI);
    private Boolean mNightMode;
    final Map<Uri, SliceBackgroundWorker> mPinnedWorkers = new ArrayMap();
    Map<Uri, SliceData> mSliceWeakDataCache;
    SlicesDatabaseAccessor mSlicesDatabaseAccessor;

    public SettingsSliceProvider() {
        super("android.permission.READ_SEARCH_INDEXABLES");
    }

    @Override // androidx.slice.SliceProvider
    public boolean onCreateSliceProvider() {
        this.mSlicesDatabaseAccessor = new SlicesDatabaseAccessor(getContext());
        this.mSliceWeakDataCache = new WeakHashMap();
        return true;
    }

    @Override // androidx.slice.SliceProvider
    public void onSlicePinned(Uri uri) {
        if (CustomSliceRegistry.isValidUri(uri)) {
            Context context = getContext();
            CustomSliceable sliceableFromUri = FeatureFactory.getFactory(context).getSlicesFeatureProvider().getSliceableFromUri(context, uri);
            IntentFilter intentFilter = sliceableFromUri.getIntentFilter();
            if (intentFilter != null) {
                registerIntentToUri(intentFilter, uri);
            }
            ThreadUtils.postOnMainThread(new Runnable(sliceableFromUri, uri) {
                /* class com.android.settings.slices.$$Lambda$SettingsSliceProvider$OxnqCBSR19BnaiZI4ENq2nSvpwg */
                public final /* synthetic */ CustomSliceable f$1;
                public final /* synthetic */ Uri f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SettingsSliceProvider.this.lambda$onSlicePinned$0$SettingsSliceProvider(this.f$1, this.f$2);
                }
            });
        } else if (CustomSliceRegistry.ZEN_MODE_SLICE_URI.equals(uri)) {
            registerIntentToUri(ZenModeSliceBuilder.INTENT_FILTER, uri);
        } else if (CustomSliceRegistry.BLUETOOTH_URI.equals(uri)) {
            registerIntentToUri(BluetoothSliceBuilder.INTENT_FILTER, uri);
        } else {
            loadSliceInBackground(uri);
        }
    }

    @Override // androidx.slice.SliceProvider
    public void onSliceUnpinned(Uri uri) {
        SliceBroadcastRelay.unregisterReceivers(getContext(), uri);
        ThreadUtils.postOnMainThread(new Runnable(uri) {
            /* class com.android.settings.slices.$$Lambda$SettingsSliceProvider$nw1yuL5d3q2dSP1sXQw8rjg30U4 */
            public final /* synthetic */ Uri f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SettingsSliceProvider.this.lambda$onSliceUnpinned$1$SettingsSliceProvider(this.f$1);
            }
        });
    }

    @Override // androidx.slice.SliceProvider
    public Slice onBindSlice(Uri uri) {
        StrictMode.ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        try {
            if (!ThreadUtils.isMainThread()) {
                threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            }
            if (getBlockedKeys().contains(uri.getLastPathSegment())) {
                Log.e("SettingsSliceProvider", "Requested blocked slice with Uri: " + uri);
                StrictMode.setThreadPolicy(threadPolicy);
                return null;
            }
            boolean isNightMode = Utils.isNightMode(getContext());
            if (this.mNightMode == null) {
                this.mNightMode = Boolean.valueOf(isNightMode);
                getContext().setTheme(C0018R$style.Theme_SettingsBase);
            } else if (this.mNightMode.booleanValue() != isNightMode) {
                Log.d("SettingsSliceProvider", "Night mode changed, reload theme");
                this.mNightMode = Boolean.valueOf(isNightMode);
                getContext().getTheme().rebase();
            }
            if (CustomSliceRegistry.isValidUri(uri)) {
                Context context = getContext();
                Slice slice = FeatureFactory.getFactory(context).getSlicesFeatureProvider().getSliceableFromUri(context, uri).getSlice();
                StrictMode.setThreadPolicy(threadPolicy);
                return slice;
            } else if (CustomSliceRegistry.WIFI_CALLING_URI.equals(uri)) {
                Slice createWifiCallingSlice = FeatureFactory.getFactory(getContext()).getSlicesFeatureProvider().getNewWifiCallingSliceHelper(getContext()).createWifiCallingSlice(uri);
                StrictMode.setThreadPolicy(threadPolicy);
                return createWifiCallingSlice;
            } else if (CustomSliceRegistry.ZEN_MODE_SLICE_URI.equals(uri)) {
                Slice slice2 = ZenModeSliceBuilder.getSlice(getContext());
                StrictMode.setThreadPolicy(threadPolicy);
                return slice2;
            } else if (CustomSliceRegistry.BLUETOOTH_URI.equals(uri)) {
                Slice slice3 = BluetoothSliceBuilder.getSlice(getContext());
                StrictMode.setThreadPolicy(threadPolicy);
                return slice3;
            } else if (CustomSliceRegistry.ENHANCED_4G_SLICE_URI.equals(uri)) {
                Slice createEnhanced4gLteSlice = FeatureFactory.getFactory(getContext()).getSlicesFeatureProvider().getNewEnhanced4gLteSliceHelper(getContext()).createEnhanced4gLteSlice(uri);
                StrictMode.setThreadPolicy(threadPolicy);
                return createEnhanced4gLteSlice;
            } else if (CustomSliceRegistry.WIFI_CALLING_PREFERENCE_URI.equals(uri)) {
                Slice createWifiCallingPreferenceSlice = FeatureFactory.getFactory(getContext()).getSlicesFeatureProvider().getNewWifiCallingSliceHelper(getContext()).createWifiCallingPreferenceSlice(uri);
                StrictMode.setThreadPolicy(threadPolicy);
                return createWifiCallingPreferenceSlice;
            } else {
                SliceData sliceData = this.mSliceWeakDataCache.get(uri);
                if (sliceData == null) {
                    loadSliceInBackground(uri);
                    Slice sliceStub = getSliceStub(uri);
                    StrictMode.setThreadPolicy(threadPolicy);
                    return sliceStub;
                }
                if (!getPinnedSlices().contains(uri)) {
                    this.mSliceWeakDataCache.remove(uri);
                }
                Slice buildSlice = SliceBuilderUtils.buildSlice(getContext(), sliceData);
                StrictMode.setThreadPolicy(threadPolicy);
                return buildSlice;
            }
        } finally {
            StrictMode.setThreadPolicy(threadPolicy);
        }
    }

    @Override // androidx.slice.SliceProvider
    public Collection<Uri> onGetSliceDescendants(Uri uri) {
        ArrayList arrayList = new ArrayList();
        if (isPrivateSlicesNeeded(uri)) {
            arrayList.addAll(this.mSlicesDatabaseAccessor.getSliceUris(uri.getAuthority(), false));
            Log.d("SettingsSliceProvider", "provide " + arrayList.size() + " non-public slices");
            uri = new Uri.Builder().scheme("content").authority(uri.getAuthority()).build();
        }
        if (SliceBuilderUtils.getPathData(uri) != null) {
            arrayList.add(uri);
            return arrayList;
        }
        String authority = uri.getAuthority();
        String path = uri.getPath();
        boolean isEmpty = path.isEmpty();
        if (!isEmpty && !TextUtils.equals(path, "/action") && !TextUtils.equals(path, "/intent")) {
            return arrayList;
        }
        arrayList.addAll(this.mSlicesDatabaseAccessor.getSliceUris(authority, true));
        if (!isEmpty || !TextUtils.isEmpty(authority)) {
            arrayList.addAll((List) PUBLICLY_SUPPORTED_CUSTOM_SLICE_URIS.stream().filter(new Predicate(authority) {
                /* class com.android.settings.slices.$$Lambda$SettingsSliceProvider$VXnbwFbN5CCV0gRYaC_0GfYEq08 */
                public final /* synthetic */ String f$0;

                {
                    this.f$0 = r1;
                }

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return TextUtils.equals(this.f$0, ((Uri) obj).getAuthority());
                }
            }).collect(Collectors.toList()));
        } else {
            arrayList.addAll(PUBLICLY_SUPPORTED_CUSTOM_SLICE_URIS);
        }
        grantWhitelistedPackagePermissions(getContext(), arrayList);
        return arrayList;
    }

    @Override // androidx.slice.SliceProvider
    public PendingIntent onCreatePermissionRequest(Uri uri, String str) {
        return PendingIntent.getActivity(getContext(), 0, new Intent("android.settings.SETTINGS").setPackage(OPMemberController.PACKAGE_NAME), 0);
    }

    static void grantWhitelistedPackagePermissions(Context context, List<Uri> list) {
        if (list == null) {
            Log.d("SettingsSliceProvider", "No descendants to grant permission with, skipping.");
        }
        String[] stringArray = context.getResources().getStringArray(C0003R$array.slice_whitelist_package_names);
        if (stringArray == null || stringArray.length == 0) {
            Log.d("SettingsSliceProvider", "No packages to whitelist, skipping.");
            return;
        }
        Log.d("SettingsSliceProvider", String.format("Whitelisting %d uris to %d pkgs.", Integer.valueOf(list.size()), Integer.valueOf(stringArray.length)));
        SliceManager sliceManager = (SliceManager) context.getSystemService(SliceManager.class);
        for (Uri uri : list) {
            for (String str : stringArray) {
                sliceManager.grantSlicePermission(str, uri);
            }
        }
    }

    public void shutdown() {
        ThreadUtils.postOnMainThread($$Lambda$SettingsSliceProvider$ONIlOILb1GMrPvVsVULUAlE4IA.INSTANCE);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: loadSlice */
    public void lambda$loadSliceInBackground$5(Uri uri) {
        long currentTimeMillis = System.currentTimeMillis();
        try {
            SliceData sliceDataFromUri = this.mSlicesDatabaseAccessor.getSliceDataFromUri(uri);
            BasePreferenceController preferenceController = SliceBuilderUtils.getPreferenceController(getContext(), sliceDataFromUri);
            IntentFilter intentFilter = preferenceController.getIntentFilter();
            if (intentFilter != null) {
                registerIntentToUri(intentFilter, uri);
            }
            ThreadUtils.postOnMainThread(new Runnable(preferenceController, uri) {
                /* class com.android.settings.slices.$$Lambda$SettingsSliceProvider$gL7QSTHEV9ZwluRcXtR9gODoUWE */
                public final /* synthetic */ BasePreferenceController f$1;
                public final /* synthetic */ Uri f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SettingsSliceProvider.this.lambda$loadSlice$4$SettingsSliceProvider(this.f$1, this.f$2);
                }
            });
            this.mSliceWeakDataCache.put(uri, sliceDataFromUri);
            getContext().getContentResolver().notifyChange(uri, null);
            Log.d("SettingsSliceProvider", "Built slice (" + uri + ") in: " + (System.currentTimeMillis() - currentTimeMillis));
        } catch (IllegalStateException e) {
            Log.d("SettingsSliceProvider", "Could not create slicedata for uri: " + uri, e);
        }
    }

    /* access modifiers changed from: package-private */
    public void loadSliceInBackground(Uri uri) {
        ThreadUtils.postOnBackgroundThread(new Runnable(uri) {
            /* class com.android.settings.slices.$$Lambda$SettingsSliceProvider$CYGtB5I1fBKv8RTisOJzAVzWeLI */
            public final /* synthetic */ Uri f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SettingsSliceProvider.this.lambda$loadSliceInBackground$5$SettingsSliceProvider(this.f$1);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void registerIntentToUri(IntentFilter intentFilter, Uri uri) {
        SliceBroadcastRelay.registerReceiver(getContext(), uri, SliceRelayReceiver.class, intentFilter);
    }

    /* access modifiers changed from: package-private */
    public Set<String> getBlockedKeys() {
        String string = Settings.Global.getString(getContext().getContentResolver(), "blocked_slices");
        ArraySet arraySet = new ArraySet();
        try {
            KEY_VALUE_LIST_PARSER.setString(string);
            Collections.addAll(arraySet, parseStringArray(string));
            return arraySet;
        } catch (IllegalArgumentException e) {
            Log.e("SettingsSliceProvider", "Bad Settings Slices Whitelist flags", e);
            return arraySet;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isPrivateSlicesNeeded(Uri uri) {
        String string = getContext().getString(C0017R$string.config_non_public_slice_query_uri);
        if (TextUtils.isEmpty(string) || !TextUtils.equals(uri.toString(), string)) {
            return false;
        }
        int callingUid = Binder.getCallingUid();
        boolean z = getContext().checkPermission("android.permission.READ_SEARCH_INDEXABLES", Binder.getCallingPid(), callingUid) == 0;
        String str = getContext().getPackageManager().getPackagesForUid(callingUid)[0];
        if (!z || !TextUtils.equals(str, getContext().getString(C0017R$string.config_settingsintelligence_package_name))) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: startBackgroundWorker */
    public void lambda$onSlicePinned$0(Sliceable sliceable, Uri uri) {
        if (sliceable.getBackgroundWorkerClass() != null && !this.mPinnedWorkers.containsKey(uri)) {
            Log.d("SettingsSliceProvider", "Starting background worker for: " + uri);
            SliceBackgroundWorker instance = SliceBackgroundWorker.getInstance(getContext(), sliceable, uri);
            this.mPinnedWorkers.put(uri, instance);
            instance.pin();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: stopBackgroundWorker */
    public void lambda$onSliceUnpinned$1(Uri uri) {
        SliceBackgroundWorker sliceBackgroundWorker = this.mPinnedWorkers.get(uri);
        if (sliceBackgroundWorker != null) {
            Log.d("SettingsSliceProvider", "Stopping background worker for: " + uri);
            sliceBackgroundWorker.unpin();
            this.mPinnedWorkers.remove(uri);
        }
    }

    private static Slice getSliceStub(Uri uri) {
        return new Slice.Builder(uri).build();
    }

    private static String[] parseStringArray(String str) {
        if (str != null) {
            String[] split = str.split(":");
            if (split.length > 0) {
                return split;
            }
        }
        return new String[0];
    }
}
