package com.android.settings.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.dashboard.profileselector.ProfileSelectDialog;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.drawer.ActivityTile;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.drawer.TileUtils;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.widget.AdaptiveIcon;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class DashboardFeatureProviderImpl implements DashboardFeatureProvider {
    private final CategoryManager mCategoryManager;
    protected final Context mContext;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final PackageManager mPackageManager;

    public DashboardFeatureProviderImpl(Context context) {
        this.mContext = context.getApplicationContext();
        this.mCategoryManager = CategoryManager.get(context);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mPackageManager = context.getPackageManager();
    }

    @Override // com.android.settings.dashboard.DashboardFeatureProvider
    public DashboardCategory getTilesForCategory(String str) {
        return this.mCategoryManager.getTilesByCategory(this.mContext, str);
    }

    @Override // com.android.settings.dashboard.DashboardFeatureProvider
    public List<DashboardCategory> getAllCategories() {
        return this.mCategoryManager.getCategories(this.mContext);
    }

    @Override // com.android.settings.dashboard.DashboardFeatureProvider
    public String getDashboardKeyForTile(Tile tile) {
        if (tile == null) {
            return null;
        }
        if (tile.hasKey()) {
            return tile.getKey(this.mContext);
        }
        return "dashboard_tile_pref_" + tile.getIntent().getComponent().getClassName();
    }

    @Override // com.android.settings.dashboard.DashboardFeatureProvider
    public List<DynamicDataObserver> bindPreferenceToTileAndGetObservers(FragmentActivity fragmentActivity, boolean z, int i, Preference preference, Tile tile, String str, int i2) {
        String str2;
        String str3;
        if (preference == null) {
            return null;
        }
        if (!TextUtils.isEmpty(str)) {
            preference.setKey(str);
        } else {
            preference.setKey(getDashboardKeyForTile(tile));
        }
        ArrayList arrayList = new ArrayList();
        DynamicDataObserver bindTitleAndGetObserver = bindTitleAndGetObserver(preference, tile);
        if (bindTitleAndGetObserver != null) {
            arrayList.add(bindTitleAndGetObserver);
        }
        DynamicDataObserver bindSummaryAndGetObserver = bindSummaryAndGetObserver(preference, tile);
        if (bindSummaryAndGetObserver != null) {
            arrayList.add(bindSummaryAndGetObserver);
        }
        DynamicDataObserver bindSwitchAndGetObserver = bindSwitchAndGetObserver(preference, tile);
        if (bindSwitchAndGetObserver != null) {
            arrayList.add(bindSwitchAndGetObserver);
        }
        bindIcon(preference, tile, z);
        if (tile instanceof ActivityTile) {
            Bundle metaData = tile.getMetaData();
            if (metaData != null) {
                str2 = metaData.getString("com.android.settings.FRAGMENT_CLASS");
                str3 = metaData.getString("com.android.settings.intent.action");
            } else {
                str3 = null;
                str2 = null;
            }
            if (!TextUtils.isEmpty(str2)) {
                preference.setFragment(str2);
            } else {
                Intent intent = new Intent(tile.getIntent());
                intent.putExtra(":settings:source_metrics", i);
                if (str3 != null) {
                    intent.setAction(str3);
                }
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(fragmentActivity, tile, intent, i) {
                    /* class com.android.settings.dashboard.$$Lambda$DashboardFeatureProviderImpl$1Nxb7k08OqHJp5_9MH1fBnUT3q4 */
                    public final /* synthetic */ FragmentActivity f$1;
                    public final /* synthetic */ Tile f$2;
                    public final /* synthetic */ Intent f$3;
                    public final /* synthetic */ int f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        return DashboardFeatureProviderImpl.this.lambda$bindPreferenceToTileAndGetObservers$0$DashboardFeatureProviderImpl(this.f$1, this.f$2, this.f$3, this.f$4, preference);
                    }
                });
            }
        }
        if (tile.hasOrder()) {
            String packageName = fragmentActivity.getPackageName();
            int order = tile.getOrder();
            if (TextUtils.equals(packageName, tile.getIntent().getComponent().getPackageName()) || i2 == Integer.MAX_VALUE) {
                preference.setOrder(order);
            } else {
                preference.setOrder(order + i2);
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$bindPreferenceToTileAndGetObservers$0 */
    public /* synthetic */ boolean lambda$bindPreferenceToTileAndGetObservers$0$DashboardFeatureProviderImpl(FragmentActivity fragmentActivity, Tile tile, Intent intent, int i, Preference preference) {
        launchIntentOrSelectProfile(fragmentActivity, tile, intent, i);
        return true;
    }

    private DynamicDataObserver createDynamicDataObserver(final String str, final Uri uri, final Preference preference) {
        return new DynamicDataObserver() {
            /* class com.android.settings.dashboard.DashboardFeatureProviderImpl.AnonymousClass1 */

            @Override // com.android.settings.dashboard.DynamicDataObserver
            public Uri getUri() {
                return uri;
            }

            /* JADX WARNING: Removed duplicated region for block: B:17:0x0039  */
            /* JADX WARNING: Removed duplicated region for block: B:21:0x0052  */
            @Override // com.android.settings.dashboard.DynamicDataObserver
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onDataChanged() {
                /*
                    r5 = this;
                    java.lang.String r0 = r2
                    int r1 = r0.hashCode()
                    r2 = -2097433649(0xffffffff82fbb3cf, float:-3.698434E-37)
                    r3 = 2
                    r4 = 1
                    if (r1 == r2) goto L_0x002c
                    r2 = -1844463779(0xffffffff920fb75d, float:-4.5348856E-28)
                    if (r1 == r2) goto L_0x0022
                    r2 = 162535197(0x9b0171d, float:4.2392194E-33)
                    if (r1 == r2) goto L_0x0018
                    goto L_0x0036
                L_0x0018:
                    java.lang.String r1 = "isChecked"
                    boolean r0 = r0.equals(r1)
                    if (r0 == 0) goto L_0x0036
                    r0 = r3
                    goto L_0x0037
                L_0x0022:
                    java.lang.String r1 = "getDynamicSummary"
                    boolean r0 = r0.equals(r1)
                    if (r0 == 0) goto L_0x0036
                    r0 = r4
                    goto L_0x0037
                L_0x002c:
                    java.lang.String r1 = "getDynamicTitle"
                    boolean r0 = r0.equals(r1)
                    if (r0 == 0) goto L_0x0036
                    r0 = 0
                    goto L_0x0037
                L_0x0036:
                    r0 = -1
                L_0x0037:
                    if (r0 == 0) goto L_0x0052
                    if (r0 == r4) goto L_0x0048
                    if (r0 == r3) goto L_0x003e
                    goto L_0x005b
                L_0x003e:
                    com.android.settings.dashboard.DashboardFeatureProviderImpl r0 = com.android.settings.dashboard.DashboardFeatureProviderImpl.this
                    android.net.Uri r1 = r3
                    androidx.preference.Preference r5 = r4
                    com.android.settings.dashboard.DashboardFeatureProviderImpl.access$200(r0, r1, r5)
                    goto L_0x005b
                L_0x0048:
                    com.android.settings.dashboard.DashboardFeatureProviderImpl r0 = com.android.settings.dashboard.DashboardFeatureProviderImpl.this
                    android.net.Uri r1 = r3
                    androidx.preference.Preference r5 = r4
                    com.android.settings.dashboard.DashboardFeatureProviderImpl.access$100(r0, r1, r5)
                    goto L_0x005b
                L_0x0052:
                    com.android.settings.dashboard.DashboardFeatureProviderImpl r0 = com.android.settings.dashboard.DashboardFeatureProviderImpl.this
                    android.net.Uri r1 = r3
                    androidx.preference.Preference r5 = r4
                    com.android.settings.dashboard.DashboardFeatureProviderImpl.access$000(r0, r1, r5)
                L_0x005b:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.settings.dashboard.DashboardFeatureProviderImpl.AnonymousClass1.onDataChanged():void");
            }
        };
    }

    private DynamicDataObserver bindTitleAndGetObserver(Preference preference, Tile tile) {
        CharSequence title = tile.getTitle(this.mContext.getApplicationContext());
        if (title != null) {
            preference.setTitle(title);
            return null;
        } else if (tile.getMetaData() == null || !tile.getMetaData().containsKey("com.android.settings.title_uri")) {
            return null;
        } else {
            preference.setTitle(C0017R$string.summary_placeholder);
            Uri completeUri = TileUtils.getCompleteUri(tile, "com.android.settings.title_uri", "getDynamicTitle");
            refreshTitle(completeUri, preference);
            return createDynamicDataObserver("getDynamicTitle", completeUri, preference);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshTitle(Uri uri, Preference preference) {
        ThreadUtils.postOnBackgroundThread(new Runnable(uri, preference) {
            /* class com.android.settings.dashboard.$$Lambda$DashboardFeatureProviderImpl$imqWZaCM37FKwI__orPNm28CBFI */
            public final /* synthetic */ Uri f$1;
            public final /* synthetic */ Preference f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                DashboardFeatureProviderImpl.this.lambda$refreshTitle$2$DashboardFeatureProviderImpl(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$refreshTitle$2 */
    public /* synthetic */ void lambda$refreshTitle$2$DashboardFeatureProviderImpl(Uri uri, Preference preference) {
        ThreadUtils.postOnMainThread(new Runnable(TileUtils.getTextFromUri(this.mContext, uri, new ArrayMap(), "com.android.settings.title")) {
            /* class com.android.settings.dashboard.$$Lambda$DashboardFeatureProviderImpl$uoxHW1pur1i7_qPxjv6ZsKqOnco */
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                DashboardFeatureProviderImpl.lambda$refreshTitle$1(Preference.this, this.f$1);
            }
        });
    }

    private DynamicDataObserver bindSummaryAndGetObserver(Preference preference, Tile tile) {
        CharSequence summary = tile.getSummary(this.mContext);
        if ("com.android.settings/com.android.settings.Settings$LanguageAndInputSettingsActivity".equals(tile.getDescription()) || "com.android.settings/com.android.settings.Settings$DateTimeSettingsActivity".equals(tile.getDescription())) {
            preference.setSummary(summary);
            return null;
        }
        if (summary != null) {
            preference.setSummary(summary);
        } else if (tile.getMetaData() == null || !tile.getMetaData().containsKey("com.android.settings.summary_uri")) {
            preference.setSummary(C0017R$string.summary_placeholder);
        } else {
            preference.setSummary(C0017R$string.summary_placeholder);
            Uri completeUri = TileUtils.getCompleteUri(tile, "com.android.settings.summary_uri", "getDynamicSummary");
            refreshSummary(completeUri, preference);
            return createDynamicDataObserver("getDynamicSummary", completeUri, preference);
        }
        return null;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshSummary(Uri uri, Preference preference) {
        ThreadUtils.postOnBackgroundThread(new Runnable(uri, preference) {
            /* class com.android.settings.dashboard.$$Lambda$DashboardFeatureProviderImpl$wBX69wLZIusZKJ_JD6TMy0hbnRA */
            public final /* synthetic */ Uri f$1;
            public final /* synthetic */ Preference f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                DashboardFeatureProviderImpl.this.lambda$refreshSummary$4$DashboardFeatureProviderImpl(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$refreshSummary$4 */
    public /* synthetic */ void lambda$refreshSummary$4$DashboardFeatureProviderImpl(Uri uri, Preference preference) {
        ThreadUtils.postOnMainThread(new Runnable(TileUtils.getTextFromUri(this.mContext, uri, new ArrayMap(), "com.android.settings.summary")) {
            /* class com.android.settings.dashboard.$$Lambda$DashboardFeatureProviderImpl$G8CK765cvmpsNyKsWWsh2utg1k */
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                DashboardFeatureProviderImpl.lambda$refreshSummary$3(Preference.this, this.f$1);
            }
        });
    }

    private DynamicDataObserver bindSwitchAndGetObserver(Preference preference, Tile tile) {
        if (!tile.hasSwitch()) {
            return null;
        }
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(TileUtils.getCompleteUri(tile, "com.android.settings.switch_uri", "onCheckedChanged")) {
            /* class com.android.settings.dashboard.$$Lambda$DashboardFeatureProviderImpl$d37Xecq3_Yhxov2MB6k5KUpk14Q */
            public final /* synthetic */ Uri f$1;

            {
                this.f$1 = r2;
            }

            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return DashboardFeatureProviderImpl.this.lambda$bindSwitchAndGetObserver$5$DashboardFeatureProviderImpl(this.f$1, preference, obj);
            }
        });
        Uri completeUri = TileUtils.getCompleteUri(tile, "com.android.settings.switch_uri", "isChecked");
        setSwitchEnabled(preference, false);
        refreshSwitch(completeUri, preference);
        return createDynamicDataObserver("isChecked", completeUri, preference);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$bindSwitchAndGetObserver$5 */
    public /* synthetic */ boolean lambda$bindSwitchAndGetObserver$5$DashboardFeatureProviderImpl(Uri uri, Preference preference, Object obj) {
        onCheckedChanged(uri, preference, ((Boolean) obj).booleanValue());
        return true;
    }

    private void onCheckedChanged(Uri uri, Preference preference, boolean z) {
        setSwitchEnabled(preference, false);
        ThreadUtils.postOnBackgroundThread(new Runnable(uri, z, preference) {
            /* class com.android.settings.dashboard.$$Lambda$DashboardFeatureProviderImpl$wKqnYfOZzMoUQIRuR54oqo7Pj8 */
            public final /* synthetic */ Uri f$1;
            public final /* synthetic */ boolean f$2;
            public final /* synthetic */ Preference f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                DashboardFeatureProviderImpl.this.lambda$onCheckedChanged$7$DashboardFeatureProviderImpl(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCheckedChanged$7 */
    public /* synthetic */ void lambda$onCheckedChanged$7$DashboardFeatureProviderImpl(Uri uri, boolean z, Preference preference) {
        ThreadUtils.postOnMainThread(new Runnable(preference, TileUtils.putBooleanToUriAndGetResult(this.mContext, uri, new ArrayMap(), "checked_state", z), z) {
            /* class com.android.settings.dashboard.$$Lambda$DashboardFeatureProviderImpl$uO0ZUwA7CpqwRsFA5w7cFCsAqY */
            public final /* synthetic */ Preference f$1;
            public final /* synthetic */ Bundle f$2;
            public final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                DashboardFeatureProviderImpl.this.lambda$onCheckedChanged$6$DashboardFeatureProviderImpl(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCheckedChanged$6 */
    public /* synthetic */ void lambda$onCheckedChanged$6$DashboardFeatureProviderImpl(Preference preference, Bundle bundle, boolean z) {
        setSwitchEnabled(preference, true);
        if (bundle.getBoolean("set_checked_error")) {
            setSwitchChecked(preference, !z);
            String string = bundle.getString("set_checked_error_message");
            if (!TextUtils.isEmpty(string)) {
                Toast.makeText(this.mContext, string, 0).show();
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void refreshSwitch(Uri uri, Preference preference) {
        ThreadUtils.postOnBackgroundThread(new Runnable(uri, preference) {
            /* class com.android.settings.dashboard.$$Lambda$DashboardFeatureProviderImpl$b8Kg9TcNFcMO8gdU9F2ttKW62zI */
            public final /* synthetic */ Uri f$1;
            public final /* synthetic */ Preference f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                DashboardFeatureProviderImpl.this.lambda$refreshSwitch$9$DashboardFeatureProviderImpl(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$refreshSwitch$9 */
    public /* synthetic */ void lambda$refreshSwitch$9$DashboardFeatureProviderImpl(Uri uri, Preference preference) {
        ThreadUtils.postOnMainThread(new Runnable(preference, TileUtils.getBooleanFromUri(this.mContext, uri, new ArrayMap(), "checked_state")) {
            /* class com.android.settings.dashboard.$$Lambda$DashboardFeatureProviderImpl$JKORuxEpBEQen40LPrvmvn2tI */
            public final /* synthetic */ Preference f$1;
            public final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                DashboardFeatureProviderImpl.this.lambda$refreshSwitch$8$DashboardFeatureProviderImpl(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$refreshSwitch$8 */
    public /* synthetic */ void lambda$refreshSwitch$8$DashboardFeatureProviderImpl(Preference preference, boolean z) {
        setSwitchChecked(preference, z);
        setSwitchEnabled(preference, true);
    }

    private void setSwitchChecked(Preference preference, boolean z) {
        if (preference instanceof MasterSwitchPreference) {
            ((MasterSwitchPreference) preference).setChecked(z);
        } else if (preference instanceof SwitchPreference) {
            ((SwitchPreference) preference).setChecked(z);
        }
    }

    private void setSwitchEnabled(Preference preference, boolean z) {
        if (preference instanceof MasterSwitchPreference) {
            ((MasterSwitchPreference) preference).setSwitchEnabled(z);
        } else {
            preference.setEnabled(z);
        }
    }

    /* access modifiers changed from: package-private */
    public void bindIcon(Preference preference, Tile tile, boolean z) {
        Icon icon = tile.getIcon(preference.getContext());
        if (icon != null) {
            Drawable loadDrawable = icon.loadDrawable(preference.getContext());
            if ("com.google.android.gms".equals(tile.getPackageName()) && "Google".equalsIgnoreCase(tile.getTitle(preference.getContext()).toString())) {
                loadDrawable = preference.getContext().getDrawable(C0008R$drawable.op_ic_homepage_google_settings);
            } else if ("com.google.android.apps.wellbeing".equals(tile.getPackageName())) {
                loadDrawable = preference.getContext().getDrawable(C0008R$drawable.op_ic_homepage_wellbeing_settings);
            } else if ("Chromebook".equalsIgnoreCase(tile.getTitle(preference.getContext()).toString())) {
                loadDrawable = preference.getContext().getDrawable(C0008R$drawable.op_chromebook);
            } else if (z && !TextUtils.equals(this.mContext.getPackageName(), tile.getPackageName())) {
                AdaptiveIcon adaptiveIcon = new AdaptiveIcon(this.mContext, loadDrawable);
                adaptiveIcon.setBackgroundColor(this.mContext, tile);
                loadDrawable = adaptiveIcon;
            }
            if ("com.android.settings.category.ia.homepage".equals(tile.getCategory())) {
                preference.setIcon(loadDrawable);
            }
        } else if (tile.getMetaData() != null && tile.getMetaData().containsKey("com.android.settings.icon_uri")) {
            ThreadUtils.postOnBackgroundThread(new Runnable(tile, preference) {
                /* class com.android.settings.dashboard.$$Lambda$DashboardFeatureProviderImpl$PNXMLBdTvUQFvU7S_OzYZOXKOLQ */
                public final /* synthetic */ Tile f$1;
                public final /* synthetic */ Preference f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    DashboardFeatureProviderImpl.this.lambda$bindIcon$11$DashboardFeatureProviderImpl(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$bindIcon$11 */
    public /* synthetic */ void lambda$bindIcon$11$DashboardFeatureProviderImpl(Tile tile, Preference preference) {
        String str;
        Intent intent = tile.getIntent();
        if (!TextUtils.isEmpty(intent.getPackage())) {
            str = intent.getPackage();
        } else {
            str = intent.getComponent() != null ? intent.getComponent().getPackageName() : null;
        }
        ArrayMap arrayMap = new ArrayMap();
        Uri completeUri = TileUtils.getCompleteUri(tile, "com.android.settings.icon_uri", "getProviderIcon");
        Pair<String, Integer> iconFromUri = TileUtils.getIconFromUri(this.mContext, str, completeUri, arrayMap);
        if (iconFromUri == null) {
            Log.w("DashboardFeatureImpl", "Failed to get icon from uri " + completeUri);
            return;
        }
        Icon createWithResource = Icon.createWithResource((String) iconFromUri.first, ((Integer) iconFromUri.second).intValue());
        if ("com.android.settings.category.ia.homepage".equals(tile.getCategory())) {
            ThreadUtils.postOnMainThread(new Runnable(createWithResource) {
                /* class com.android.settings.dashboard.$$Lambda$DashboardFeatureProviderImpl$651jct42Po2mFv12KjSJ5qWrxCo */
                public final /* synthetic */ Icon f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    DashboardFeatureProviderImpl.lambda$bindIcon$10(Preference.this, this.f$1);
                }
            });
        }
    }

    private void launchIntentOrSelectProfile(FragmentActivity fragmentActivity, Tile tile, Intent intent, int i) {
        if (!isIntentResolvable(intent)) {
            Log.w("DashboardFeatureImpl", "Cannot resolve intent, skipping. " + intent);
            return;
        }
        ProfileSelectDialog.updateUserHandlesIfNeeded(this.mContext, tile);
        UserManager userManager = UserManager.get(this.mContext);
        if (tile.userHandle == null || tile.isPrimaryProfileOnly()) {
            this.mMetricsFeatureProvider.logStartedIntent(intent, i);
            fragmentActivity.startActivityForResult(intent, 0);
        } else if (tile.userHandle.size() == 1 || (tile.userHandle.size() == 2 && OPUtils.hasMultiAppProfiles(userManager))) {
            this.mMetricsFeatureProvider.logStartedIntent(intent, i);
            fragmentActivity.startActivityForResultAsUser(intent, 0, tile.userHandle.get(0));
        } else {
            this.mMetricsFeatureProvider.logStartedIntent(intent, i);
            UserHandle userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
            if (userHandle == null || !tile.userHandle.contains(userHandle)) {
                ProfileSelectDialog.show(fragmentActivity.getSupportFragmentManager(), tile, i);
            } else {
                fragmentActivity.startActivityForResultAsUser(intent, 0, userHandle);
            }
        }
    }

    private boolean isIntentResolvable(Intent intent) {
        return this.mPackageManager.resolveActivity(intent, 0) != null;
    }
}
