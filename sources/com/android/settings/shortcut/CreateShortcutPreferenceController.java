package com.android.settings.shortcut;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0012R$layout;
import com.android.settings.C0014R$mipmap;
import com.android.settings.Settings;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CreateShortcutPreferenceController extends BasePreferenceController {
    private static final Comparator<ResolveInfo> SHORTCUT_COMPARATOR = $$Lambda$CreateShortcutPreferenceController$m1jNu5xouyh4ECkFYf1QbPGluU.INSTANCE;
    static final String SHORTCUT_ID_PREFIX = "component-shortcut-";
    static final Intent SHORTCUT_PROBE = new Intent("android.intent.action.MAIN").addCategory("com.android.settings.SHORTCUT").addFlags(268435456);
    private static final String TAG = "CreateShortcutPrefCtrl";
    private final ConnectivityManager mConnectivityManager;
    private Activity mHost;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final PackageManager mPackageManager;
    private final ShortcutManager mShortcutManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public CreateShortcutPreferenceController(Context context, String str) {
        super(context, str);
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mShortcutManager = (ShortcutManager) context.getSystemService(ShortcutManager.class);
        this.mPackageManager = context.getPackageManager();
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    public void setActivity(Activity activity) {
        this.mHost = activity;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof PreferenceGroup) {
            PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
            preferenceGroup.removeAll();
            List<ResolveInfo> queryShortcuts = queryShortcuts();
            Context context = preference.getContext();
            if (!queryShortcuts.isEmpty()) {
                PreferenceCategory preferenceCategory = new PreferenceCategory(context);
                preferenceGroup.addPreference(preferenceCategory);
                int i = 0;
                for (ResolveInfo resolveInfo : queryShortcuts) {
                    int i2 = resolveInfo.priority / 10;
                    if (i2 != i) {
                        preferenceCategory = new PreferenceCategory(context);
                        preferenceGroup.addPreference(preferenceCategory);
                    }
                    Preference preference2 = new Preference(context);
                    preference2.setTitle(resolveInfo.loadLabel(this.mPackageManager));
                    preference2.setKey(resolveInfo.activityInfo.getComponentName().flattenToString());
                    preference2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(resolveInfo) {
                        /* class com.android.settings.shortcut.$$Lambda$CreateShortcutPreferenceController$m5coWjMe0E4dVYsG_EHyeWibFdM */
                        public final /* synthetic */ ResolveInfo f$1;

                        {
                            this.f$1 = r2;
                        }

                        @Override // androidx.preference.Preference.OnPreferenceClickListener
                        public final boolean onPreferenceClick(Preference preference) {
                            return CreateShortcutPreferenceController.this.lambda$updateState$0$CreateShortcutPreferenceController(this.f$1, preference);
                        }
                    });
                    preferenceCategory.addPreference(preference2);
                    i = i2;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateState$0 */
    public /* synthetic */ boolean lambda$updateState$0$CreateShortcutPreferenceController(ResolveInfo resolveInfo, Preference preference) {
        if (this.mHost == null) {
            return false;
        }
        this.mHost.setResult(-1, createResultIntent(buildShortcutIntent(resolveInfo), resolveInfo, preference.getTitle()));
        logCreateShortcut(resolveInfo);
        this.mHost.finish();
        return true;
    }

    /* access modifiers changed from: package-private */
    public Intent createResultIntent(Intent intent, ResolveInfo resolveInfo, CharSequence charSequence) {
        Intent createShortcutResultIntent = this.mShortcutManager.createShortcutResultIntent(createShortcutInfo(this.mContext, intent, resolveInfo, charSequence));
        if (createShortcutResultIntent == null) {
            createShortcutResultIntent = new Intent();
        }
        createShortcutResultIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(this.mContext, C0014R$mipmap.ic_launcher_settings)).putExtra("android.intent.extra.shortcut.INTENT", intent).putExtra("android.intent.extra.shortcut.NAME", charSequence);
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        int i = activityInfo.icon;
        if (i != 0) {
            Context context = this.mContext;
            createShortcutResultIntent.putExtra("android.intent.extra.shortcut.ICON", createIcon(context, activityInfo.applicationInfo, i, C0012R$layout.shortcut_badge, context.getResources().getDimensionPixelSize(C0007R$dimen.shortcut_size)));
        }
        return createShortcutResultIntent;
    }

    /* access modifiers changed from: package-private */
    public List<ResolveInfo> queryShortcuts() {
        ArrayList arrayList = new ArrayList();
        List<ResolveInfo> queryIntentActivities = this.mPackageManager.queryIntentActivities(SHORTCUT_PROBE, 128);
        if (queryIntentActivities == null) {
            return null;
        }
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            if (!resolveInfo.activityInfo.name.endsWith(Settings.TetherSettingsActivity.class.getSimpleName()) || this.mConnectivityManager.isTetheringSupported()) {
                if (!resolveInfo.activityInfo.applicationInfo.isSystemApp()) {
                    Log.d(TAG, "Skipping non-system app: " + resolveInfo.activityInfo);
                } else {
                    if (FeatureFlagUtils.isEnabled(this.mContext, "settings_wifitracker2")) {
                        if (resolveInfo.activityInfo.name.endsWith(Settings.WifiSettingsActivity.class.getSimpleName())) {
                        }
                    } else if (resolveInfo.activityInfo.name.endsWith(Settings.WifiSettings2Activity.class.getSimpleName())) {
                    }
                    arrayList.add(resolveInfo);
                }
            }
        }
        Collections.sort(arrayList, SHORTCUT_COMPARATOR);
        return arrayList;
    }

    private void logCreateShortcut(ResolveInfo resolveInfo) {
        ActivityInfo activityInfo;
        if (resolveInfo != null && (activityInfo = resolveInfo.activityInfo) != null) {
            this.mMetricsFeatureProvider.action(this.mContext, 829, activityInfo.name);
        }
    }

    private static Intent buildShortcutIntent(ResolveInfo resolveInfo) {
        Intent flags = new Intent(SHORTCUT_PROBE).setFlags(335544320);
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        return flags.setClassName(activityInfo.packageName, activityInfo.name);
    }

    private static ShortcutInfo createShortcutInfo(Context context, Intent intent, ResolveInfo resolveInfo, CharSequence charSequence) {
        Icon icon;
        ApplicationInfo applicationInfo;
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        int i = activityInfo.icon;
        if (i == 0 || (applicationInfo = activityInfo.applicationInfo) == null) {
            icon = Icon.createWithResource(context, C0008R$drawable.ic_launcher_settings);
        } else {
            icon = Icon.createWithAdaptiveBitmap(createIcon(context, applicationInfo, i, C0012R$layout.shortcut_badge_maskable, context.getResources().getDimensionPixelSize(C0007R$dimen.shortcut_size_maskable)));
        }
        return new ShortcutInfo.Builder(context, SHORTCUT_ID_PREFIX + intent.getComponent().flattenToShortString()).setShortLabel(charSequence).setIntent(intent).setIcon(icon).build();
    }

    private static Bitmap createIcon(Context context, ApplicationInfo applicationInfo, int i, int i2, int i3) {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, 16974372);
        View inflate = LayoutInflater.from(contextThemeWrapper).inflate(i2, (ViewGroup) null);
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
        inflate.measure(makeMeasureSpec, makeMeasureSpec);
        Bitmap createBitmap = Bitmap.createBitmap(inflate.getMeasuredWidth(), inflate.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        try {
            Drawable drawable = context.getPackageManager().getResourcesForApplication(applicationInfo).getDrawable(i, contextThemeWrapper.getTheme());
            if (drawable instanceof LayerDrawable) {
                drawable = ((LayerDrawable) drawable).getDrawable(1);
            }
            ((ImageView) inflate.findViewById(16908294)).setImageDrawable(drawable);
        } catch (PackageManager.NameNotFoundException unused) {
            Log.w(TAG, "Cannot load icon from app " + applicationInfo + ", returning a default icon");
            ((ImageView) inflate.findViewById(16908294)).setImageIcon(Icon.createWithResource(context, C0008R$drawable.ic_launcher_settings));
        }
        inflate.layout(0, 0, inflate.getMeasuredWidth(), inflate.getMeasuredHeight());
        inflate.draw(canvas);
        return createBitmap;
    }

    public static void updateRestoredShortcuts(Context context) {
        ResolveInfo resolveActivity;
        ShortcutManager shortcutManager = (ShortcutManager) context.getSystemService(ShortcutManager.class);
        ArrayList arrayList = new ArrayList();
        for (ShortcutInfo shortcutInfo : shortcutManager.getPinnedShortcuts()) {
            if (shortcutInfo.getId().startsWith(SHORTCUT_ID_PREFIX) && (resolveActivity = context.getPackageManager().resolveActivity(shortcutInfo.getIntent(), 0)) != null) {
                arrayList.add(createShortcutInfo(context, buildShortcutIntent(resolveActivity), resolveActivity, shortcutInfo.getShortLabel()));
            }
        }
        if (!arrayList.isEmpty()) {
            shortcutManager.updateShortcuts(arrayList);
        }
    }

    static /* synthetic */ int lambda$static$1(ResolveInfo resolveInfo, ResolveInfo resolveInfo2) {
        return resolveInfo.priority - resolveInfo2.priority;
    }
}
