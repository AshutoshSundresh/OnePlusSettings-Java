package com.android.settings.privacy;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.dashboard.profileselector.UserAdapter;
import com.android.settings.privacy.EnableContentCaptureWithServiceSettingsPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.ContentCaptureUtils;
import com.android.settingslib.R$string;
import java.util.ArrayList;
import java.util.List;

public final class EnableContentCaptureWithServiceSettingsPreferenceController extends TogglePreferenceController {
    private static final String TAG = "ContentCaptureController";
    private final UserManager mUserManager;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public EnableContentCaptureWithServiceSettingsPreferenceController(Context context, String str) {
        super(context, str);
        this.mUserManager = UserManager.get(context);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return ContentCaptureUtils.isEnabledForUser(this.mContext);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        ContentCaptureUtils.setEnabledForUser(this.mContext, z);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.TogglePreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ComponentName serviceSettingsComponentName = ContentCaptureUtils.getServiceSettingsComponentName();
        if (serviceSettingsComponentName != null) {
            preference.setIntent(new Intent("android.intent.action.MAIN").setComponent(serviceSettingsComponentName));
        } else {
            Log.w(TAG, "No component name for custom service settings");
            preference.setSelectable(false);
        }
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.privacy.$$Lambda$EnableContentCaptureWithServiceSettingsPreferenceController$wvbA3waPG91zIQ9YKuVJlMjUL8Q */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return EnableContentCaptureWithServiceSettingsPreferenceController.this.lambda$updateState$0$EnableContentCaptureWithServiceSettingsPreferenceController(preference);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateState$0 */
    public /* synthetic */ boolean lambda$updateState$0$EnableContentCaptureWithServiceSettingsPreferenceController(Preference preference) {
        ProfileSelectDialog.show(this.mContext, preference);
        return true;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (ContentCaptureUtils.isFeatureAvailable() && ContentCaptureUtils.getServiceSettingsComponentName() != null) {
            return 0;
        }
        return 3;
    }

    /* access modifiers changed from: private */
    public static final class ProfileSelectDialog {
        public static void show(Context context, Preference preference) {
            UserManager userManager = UserManager.get(context);
            List<UserInfo> users = userManager.getUsers();
            ArrayList arrayList = new ArrayList(users.size());
            for (UserInfo userInfo : users) {
                arrayList.add(userInfo.getUserHandle());
            }
            if (arrayList.size() == 1) {
                context.startActivityAsUser(preference.getIntent().addFlags(32768), (UserHandle) arrayList.get(0));
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            UserAdapter createUserAdapter = UserAdapter.createUserAdapter(userManager, context, arrayList);
            builder.setTitle(R$string.choose_profile);
            builder.setAdapter(createUserAdapter, new DialogInterface.OnClickListener(arrayList, preference, context) {
                /* class com.android.settings.privacy.$$Lambda$EnableContentCaptureWithServiceSettingsPreferenceController$ProfileSelectDialog$OMo4n7mn0aHIsZvpNoi9lROMhsw */
                public final /* synthetic */ ArrayList f$0;
                public final /* synthetic */ Preference f$1;
                public final /* synthetic */ Context f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    EnableContentCaptureWithServiceSettingsPreferenceController.ProfileSelectDialog.lambda$show$0(this.f$0, this.f$1, this.f$2, dialogInterface, i);
                }
            });
            builder.show();
        }
    }
}
