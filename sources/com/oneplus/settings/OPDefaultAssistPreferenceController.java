package com.oneplus.settings;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.voice.VoiceInteractionServiceInfo;
import android.text.TextUtils;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.app.AssistUtils;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.notification.zen.ZenCustomRadioButtonPreference;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPDefaultAssistPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause {
    private static final Intent ASSIST_ACTIVITY_PROBE = new Intent("android.intent.action.ASSIST");
    private static final Intent ASSIST_SERVICE_PROBE = new Intent("android.service.voice.VoiceInteractionService");
    private static final Uri QUICK_TURN_ON_VOICE_ASSISTANT_URI = Settings.System.getUriFor("quick_turn_on_voice_assistant");
    private AssistUtils mAssistUtils;
    private AlertDialog mAssistantAppDialog;
    private final List<Info> mAvailableAssistants = new ArrayList();
    final String mKEY;
    protected PackageManager mPm;
    private ZenCustomRadioButtonPreference mPreference;
    private AlertDialog mSelectAssistantAppDialog;
    private SettingObserver mSettingObserver;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public OPDefaultAssistPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str);
        this.mKEY = str;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.mPm = context.getPackageManager();
        this.mAssistUtils = new AssistUtils(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ZenCustomRadioButtonPreference zenCustomRadioButtonPreference = (ZenCustomRadioButtonPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = zenCustomRadioButtonPreference;
        zenCustomRadioButtonPreference.setOnGearClickListener(new ZenCustomRadioButtonPreference.OnGearClickListener() {
            /* class com.oneplus.settings.$$Lambda$OPDefaultAssistPreferenceController$r1cqR3PuUfUsiZG_rtejjs673M */

            @Override // com.android.settings.notification.zen.ZenCustomRadioButtonPreference.OnGearClickListener
            public final void onGearClick(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
                OPDefaultAssistPreferenceController.this.lambda$displayPreference$0$OPDefaultAssistPreferenceController(zenCustomRadioButtonPreference);
            }
        });
        this.mPreference.setOnRadioButtonClickListener(new ZenCustomRadioButtonPreference.OnRadioButtonClickListener() {
            /* class com.oneplus.settings.$$Lambda$OPDefaultAssistPreferenceController$cuUOQkPXJoNFauS9gjMOUskXozE */

            @Override // com.android.settings.notification.zen.ZenCustomRadioButtonPreference.OnRadioButtonClickListener
            public final void onRadioButtonClick(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
                OPDefaultAssistPreferenceController.this.lambda$displayPreference$1$OPDefaultAssistPreferenceController(zenCustomRadioButtonPreference);
            }
        });
        this.mSettingObserver = new SettingObserver();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ void lambda$displayPreference$0$OPDefaultAssistPreferenceController(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
        Intent intent = new Intent();
        intent.setAction("com.oneplus.intent.OPDefaultVoiceAssistPicker");
        intent.putExtra("assistntapp", "onClick");
        this.mContext.startActivity(intent);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$1 */
    public /* synthetic */ void lambda$displayPreference$1$OPDefaultAssistPreferenceController(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
        this.mPreference.setChecked(true);
        List<DefaultAppInfo> candidates = getCandidates();
        if (candidates.size() == 0) {
            noAssistantAppDialog();
        } else if (candidates.size() <= 0) {
        } else {
            if (getDefaultKey() == null || !OPUtils.isApplicationEnabled(this.mContext, getDefaultKey().split("/")[0])) {
                Settings.System.putInt(this.mContext.getContentResolver(), "quick_turn_on_voice_assistant", 1);
                toSelectAssistantAppDialog();
                return;
            }
            Settings.System.putInt(this.mContext.getContentResolver(), "quick_turn_on_voice_assistant", 1);
            OPUtils.sendAppTracker("quick_turn_on_voice_assistant", "on");
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return this.mKEY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ZenCustomRadioButtonPreference zenCustomRadioButtonPreference = this.mPreference;
        boolean z = false;
        if (Settings.System.getInt(this.mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0) == 1) {
            z = true;
        }
        zenCustomRadioButtonPreference.setChecked(z);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        List<DefaultAppInfo> candidates = getCandidates();
        if (candidates.size() > 0 && getDefaultKey() == null) {
            this.mPreference.setChecked(Settings.System.getInt(this.mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0) == 1);
        } else if (candidates.size() == 0 || getDefaultKey() == null) {
            this.mPreference.setChecked(false);
            Settings.System.putInt(this.mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0);
        } else if (!isEnabledApp(getDefaultKey().split("/")[0])) {
            this.mPreference.setChecked(false);
            Settings.System.putInt(this.mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0);
        } else {
            this.mPreference.setChecked(Settings.System.getInt(this.mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0) == 1);
        }
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.register(this.mContext.getContentResolver(), true);
        }
        String string = Settings.Secure.getString(this.mContext.getContentResolver(), "oneplus_default_voice_assist_picker_service");
        if (string == null || TextUtils.isEmpty(string)) {
            this.mPreference.setSummary(C0017R$string.no_notification_assistant);
            return;
        }
        String str = string.split("/")[0];
        if (TextUtils.isEmpty(str) || !OPUtils.isAppPakExist(this.mContext, string.split("/")[0]) || !OPUtils.isApplicationEnabled(this.mContext, string.split("/")[0])) {
            this.mPreference.setSummary(C0017R$string.no_notification_assistant);
        } else {
            this.mPreference.setSummary(OPUtils.getAppLabel(this.mContext, str));
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        SettingObserver settingObserver = this.mSettingObserver;
        if (settingObserver != null) {
            settingObserver.register(this.mContext.getContentResolver(), false);
        }
        AlertDialog alertDialog = this.mAssistantAppDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mAssistantAppDialog.dismiss();
            this.mAssistantAppDialog = null;
        }
        AlertDialog alertDialog2 = this.mSelectAssistantAppDialog;
        if (alertDialog2 != null && alertDialog2.isShowing()) {
            this.mSelectAssistantAppDialog.dismiss();
            this.mSelectAssistantAppDialog = null;
        }
    }

    class SettingObserver extends ContentObserver {
        public SettingObserver() {
            super(new Handler());
        }

        public void register(ContentResolver contentResolver, boolean z) {
            if (z) {
                contentResolver.registerContentObserver(OPDefaultAssistPreferenceController.QUICK_TURN_ON_VOICE_ASSISTANT_URI, false, this);
            } else {
                contentResolver.unregisterContentObserver(this);
            }
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (OPDefaultAssistPreferenceController.QUICK_TURN_ON_VOICE_ASSISTANT_URI.equals(uri)) {
                if (Settings.System.getInt(((AbstractPreferenceController) OPDefaultAssistPreferenceController.this).mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0) == 1) {
                    OPDefaultAssistPreferenceController.this.mPreference.setChecked(true);
                } else {
                    OPDefaultAssistPreferenceController.this.mPreference.setChecked(false);
                }
            }
        }
    }

    private List<DefaultAppInfo> getCandidates() {
        this.mAvailableAssistants.clear();
        addAssistServices();
        addAssistActivities();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (Info info : this.mAvailableAssistants) {
            String packageName = info.component.getPackageName();
            if (!arrayList.contains(packageName)) {
                arrayList.add(packageName);
                arrayList2.add(new DefaultAppInfo(this.mContext, this.mPm, UserHandle.myUserId(), info.component));
            }
        }
        return arrayList2;
    }

    public ComponentName getCurrentAssist() {
        return this.mAssistUtils.getAssistComponentForUser(UserHandle.myUserId());
    }

    private String getDefaultKey() {
        ComponentName currentAssist = getCurrentAssist();
        if (currentAssist == null || !OPUtils.isAppExist(this.mContext, currentAssist.getPackageName())) {
            return null;
        }
        return new DefaultAppInfo(this.mContext, this.mPm, UserHandle.myUserId(), currentAssist).getKey();
    }

    private boolean isEnabledApp(String str) {
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = SettingsBaseApplication.mApplication.getPackageManager().getApplicationInfoAsUser(str, 0, UserHandle.myUserId());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            applicationInfo = null;
        }
        if (applicationInfo != null) {
            return true;
        }
        return false;
    }

    private void addAssistServices() {
        for (ResolveInfo resolveInfo : this.mPm.queryIntentServices(ASSIST_SERVICE_PROBE, 128)) {
            VoiceInteractionServiceInfo voiceInteractionServiceInfo = new VoiceInteractionServiceInfo(this.mPm, resolveInfo.serviceInfo);
            if (voiceInteractionServiceInfo.getSupportsAssist()) {
                List<Info> list = this.mAvailableAssistants;
                ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                list.add(new Info(new ComponentName(serviceInfo.packageName, serviceInfo.name), voiceInteractionServiceInfo));
            }
        }
    }

    private void addAssistActivities() {
        for (ResolveInfo resolveInfo : this.mPm.queryIntentActivities(ASSIST_ACTIVITY_PROBE, 65536)) {
            List<Info> list = this.mAvailableAssistants;
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            list.add(new Info(new ComponentName(activityInfo.packageName, activityInfo.name)));
        }
    }

    private void noAssistantAppDialog() {
        AnonymousClass1 r0 = new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.OPDefaultAssistPreferenceController.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    Settings.System.putInt(((AbstractPreferenceController) OPDefaultAssistPreferenceController.this).mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0);
                    OPUtils.sendAppTracker("quick_turn_on_voice_assistant", "off");
                }
            }
        };
        AnonymousClass2 r1 = new DialogInterface.OnDismissListener() {
            /* class com.oneplus.settings.OPDefaultAssistPreferenceController.AnonymousClass2 */

            public void onDismiss(DialogInterface dialogInterface) {
                ZenCustomRadioButtonPreference zenCustomRadioButtonPreference = OPDefaultAssistPreferenceController.this.mPreference;
                boolean z = false;
                if (Settings.System.getInt(((AbstractPreferenceController) OPDefaultAssistPreferenceController.this).mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0) != 0) {
                    z = true;
                }
                zenCustomRadioButtonPreference.setChecked(z);
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setMessage(C0017R$string.oneplus_no_assistant_app_dialog);
        builder.setPositiveButton(17039370, r0);
        builder.setOnDismissListener(r1);
        AlertDialog create = builder.create();
        this.mAssistantAppDialog = create;
        create.show();
    }

    private void toSelectAssistantAppDialog() {
        AnonymousClass3 r0 = new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.OPDefaultAssistPreferenceController.AnonymousClass3 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    Intent intent = new Intent();
                    intent.setAction("com.oneplus.intent.DefaultAssistPicker");
                    intent.putExtra("assistntapp", "switch");
                    ((AbstractPreferenceController) OPDefaultAssistPreferenceController.this).mContext.startActivity(intent);
                } else if (i == -2) {
                    Settings.System.putInt(((AbstractPreferenceController) OPDefaultAssistPreferenceController.this).mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0);
                    OPUtils.sendAppTracker("quick_turn_on_voice_assistant", "off");
                }
            }
        };
        AnonymousClass4 r1 = new DialogInterface.OnDismissListener() {
            /* class com.oneplus.settings.OPDefaultAssistPreferenceController.AnonymousClass4 */

            public void onDismiss(DialogInterface dialogInterface) {
                ZenCustomRadioButtonPreference zenCustomRadioButtonPreference = OPDefaultAssistPreferenceController.this.mPreference;
                boolean z = false;
                if (Settings.System.getInt(((AbstractPreferenceController) OPDefaultAssistPreferenceController.this).mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0) != 0) {
                    z = true;
                }
                zenCustomRadioButtonPreference.setChecked(z);
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setMessage(C0017R$string.oneplus_to_select_assistant_app_dialog);
        builder.setPositiveButton(17039370, r0);
        builder.setNegativeButton(17039360, r0);
        builder.setOnDismissListener(r1);
        AlertDialog create = builder.create();
        this.mSelectAssistantAppDialog = create;
        create.show();
    }

    /* access modifiers changed from: package-private */
    public static class Info {
        public final ComponentName component;

        Info(ComponentName componentName) {
            this.component = componentName;
        }

        Info(ComponentName componentName, VoiceInteractionServiceInfo voiceInteractionServiceInfo) {
            this.component = componentName;
        }
    }
}
