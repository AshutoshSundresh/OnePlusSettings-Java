package com.android.settings.applications.appinfo;

import android.app.role.RoleControllerManager;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.util.CollectionUtils;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public abstract class DefaultAppShortcutPreferenceControllerBase extends BasePreferenceController {
    private boolean mAppVisible;
    protected final String mPackageName;
    private PreferenceScreen mPreferenceScreen;
    private final RoleManager mRoleManager;
    private final String mRoleName;
    private boolean mRoleVisible;

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

    public DefaultAppShortcutPreferenceControllerBase(Context context, String str, String str2, String str3) {
        super(context, str);
        this.mRoleName = str2;
        this.mPackageName = str3;
        this.mRoleManager = (RoleManager) context.getSystemService(RoleManager.class);
        RoleControllerManager roleControllerManager = (RoleControllerManager) this.mContext.getSystemService(RoleControllerManager.class);
        Executor mainExecutor = this.mContext.getMainExecutor();
        roleControllerManager.isRoleVisible(this.mRoleName, mainExecutor, new Consumer() {
            /* class com.android.settings.applications.appinfo.$$Lambda$DefaultAppShortcutPreferenceControllerBase$PAhuwVBE2P_xbMKEkn5AwPD1_DQ */

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DefaultAppShortcutPreferenceControllerBase.this.lambda$new$0$DefaultAppShortcutPreferenceControllerBase((Boolean) obj);
            }
        });
        roleControllerManager.isApplicationVisibleForRole(this.mRoleName, this.mPackageName, mainExecutor, new Consumer() {
            /* class com.android.settings.applications.appinfo.$$Lambda$DefaultAppShortcutPreferenceControllerBase$_GVkqHET8d1yF4IgDEBt_Ev5syM */

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DefaultAppShortcutPreferenceControllerBase.this.lambda$new$1$DefaultAppShortcutPreferenceControllerBase((Boolean) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$DefaultAppShortcutPreferenceControllerBase(Boolean bool) {
        this.mRoleVisible = bool.booleanValue();
        refreshAvailability();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$DefaultAppShortcutPreferenceControllerBase(Boolean bool) {
        this.mAppVisible = bool.booleanValue();
        refreshAvailability();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
    }

    private void refreshAvailability() {
        Preference findPreference;
        PreferenceScreen preferenceScreen = this.mPreferenceScreen;
        if (preferenceScreen != null && (findPreference = preferenceScreen.findPreference(getPreferenceKey())) != null) {
            findPreference.setVisible(isAvailable());
            updateState(findPreference);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (((UserManager) this.mContext.getSystemService(UserManager.class)).isManagedProfile()) {
            return 4;
        }
        return (!this.mRoleVisible || !this.mAppVisible) ? 3 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mContext.getText(isDefaultApp() ? C0017R$string.yes : C0017R$string.no);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(this.mPreferenceKey, preference.getKey()) || "default_home".equals(preference.getKey()) || "default_browser".equals(preference.getKey()) || "default_phone_app".equals(preference.getKey()) || "default_sms_app".equals(preference.getKey())) {
            return false;
        }
        this.mContext.startActivity(new Intent("android.intent.action.MANAGE_DEFAULT_APP").putExtra("android.intent.extra.ROLE_NAME", this.mRoleName));
        return true;
    }

    private boolean isDefaultApp() {
        return TextUtils.equals(this.mPackageName, (String) CollectionUtils.firstOrNull(this.mRoleManager.getRoleHolders(this.mRoleName)));
    }
}
