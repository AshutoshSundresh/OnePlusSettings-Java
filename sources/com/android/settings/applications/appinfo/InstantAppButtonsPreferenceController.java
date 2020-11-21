package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.applications.AppStoreUtil;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreateOptionsMenu;
import com.android.settingslib.core.lifecycle.events.OnOptionsItemSelected;
import com.android.settingslib.core.lifecycle.events.OnPrepareOptionsMenu;
import com.android.settingslib.widget.LayoutPreference;

public class InstantAppButtonsPreferenceController extends BasePreferenceController implements LifecycleObserver, OnCreateOptionsMenu, OnPrepareOptionsMenu, OnOptionsItemSelected {
    private static final String KEY_INSTANT_APP_BUTTONS = "instant_app_buttons";
    private static final String META_DATA_DEFAULT_URI = "default-url";
    private MenuItem mInstallMenu;
    private String mLaunchUri = getDefaultLaunchUri();
    private final String mPackageName;
    private final AppInfoDashboardFragment mParent;
    private LayoutPreference mPreference;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
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

    public InstantAppButtonsPreferenceController(Context context, AppInfoDashboardFragment appInfoDashboardFragment, String str, Lifecycle lifecycle) {
        super(context, KEY_INSTANT_APP_BUTTONS);
        this.mParent = appInfoDashboardFragment;
        this.mPackageName = str;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return AppUtils.isInstant(this.mParent.getPackageInfo().applicationInfo) ? 0 : 4;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(KEY_INSTANT_APP_BUTTONS);
        this.mPreference = layoutPreference;
        initButtons(layoutPreference.findViewById(C0010R$id.instant_app_button_container));
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnCreateOptionsMenu
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (!TextUtils.isEmpty(this.mLaunchUri)) {
            menu.add(0, 3, 2, C0017R$string.install_text).setShowAsAction(0);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnOptionsItemSelected
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 3) {
            return false;
        }
        Intent appStoreLink = AppStoreUtil.getAppStoreLink(this.mContext, this.mPackageName);
        if (appStoreLink == null) {
            return true;
        }
        this.mParent.startActivity(appStoreLink);
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPrepareOptionsMenu
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem findItem = menu.findItem(3);
        this.mInstallMenu = findItem;
        if (findItem != null && AppStoreUtil.getAppStoreLink(this.mContext, this.mPackageName) == null) {
            this.mInstallMenu.setEnabled(false);
        }
    }

    private void initButtons(View view) {
        Button button = (Button) view.findViewById(C0010R$id.install);
        Button button2 = (Button) view.findViewById(C0010R$id.clear_data);
        Button button3 = (Button) view.findViewById(C0010R$id.launch);
        if (!TextUtils.isEmpty(this.mLaunchUri)) {
            button.setVisibility(8);
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.BROWSABLE");
            intent.setPackage(this.mPackageName);
            intent.setData(Uri.parse(this.mLaunchUri));
            intent.addFlags(268435456);
            button3.setOnClickListener(new View.OnClickListener(intent) {
                /* class com.android.settings.applications.appinfo.$$Lambda$InstantAppButtonsPreferenceController$2vM5nla3CEsaIUNVk7alr9UEbBA */
                public final /* synthetic */ Intent f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    InstantAppButtonsPreferenceController.this.lambda$initButtons$0$InstantAppButtonsPreferenceController(this.f$1, view);
                }
            });
        } else {
            button3.setVisibility(8);
            Intent appStoreLink = AppStoreUtil.getAppStoreLink(this.mContext, this.mPackageName);
            if (appStoreLink != null) {
                button.setOnClickListener(new View.OnClickListener(appStoreLink) {
                    /* class com.android.settings.applications.appinfo.$$Lambda$InstantAppButtonsPreferenceController$oBWjqqdf33bi3sDY5lE6TGLlFJM */
                    public final /* synthetic */ Intent f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        InstantAppButtonsPreferenceController.this.lambda$initButtons$1$InstantAppButtonsPreferenceController(this.f$1, view);
                    }
                });
            } else {
                button.setEnabled(false);
            }
        }
        button2.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.applications.appinfo.$$Lambda$InstantAppButtonsPreferenceController$f8slAx9lBDdGAmwfjMjp59JCarA */

            public final void onClick(View view) {
                InstantAppButtonsPreferenceController.this.lambda$initButtons$2$InstantAppButtonsPreferenceController(view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initButtons$0 */
    public /* synthetic */ void lambda$initButtons$0$InstantAppButtonsPreferenceController(Intent intent, View view) {
        this.mParent.startActivity(intent);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initButtons$1 */
    public /* synthetic */ void lambda$initButtons$1$InstantAppButtonsPreferenceController(Intent intent, View view) {
        this.mParent.startActivity(intent);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initButtons$2 */
    public /* synthetic */ void lambda$initButtons$2$InstantAppButtonsPreferenceController(View view) {
        showDialog();
    }

    private void showDialog() {
        InstantAppButtonDialogFragment newInstance = InstantAppButtonDialogFragment.newInstance(this.mPackageName);
        newInstance.setTargetFragment(this.mParent, 0);
        newInstance.show(this.mParent.getFragmentManager(), KEY_INSTANT_APP_BUTTONS);
    }

    private String getDefaultLaunchUri() {
        PackageManager packageManager = this.mContext.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(this.mPackageName);
        for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(intent, 8388736)) {
            Bundle bundle = resolveInfo.activityInfo.metaData;
            if (bundle != null) {
                String string = bundle.getString(META_DATA_DEFAULT_URI);
                if (!TextUtils.isEmpty(string)) {
                    return string;
                }
            }
        }
        return null;
    }
}
