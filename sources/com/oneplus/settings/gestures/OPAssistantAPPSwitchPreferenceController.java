package com.oneplus.settings.gestures;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.voice.VoiceInteractionServiceInfo;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.app.AssistUtils;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.MasterSwitchController;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPAssistantAPPSwitchPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause, SwitchWidgetController.OnSwitchChangeListener {
    private static final Intent ASSIST_ACTIVITY_PROBE = new Intent("android.intent.action.ASSIST");
    private static final Intent ASSIST_SERVICE_PROBE = new Intent("android.service.voice.VoiceInteractionService");
    private static final String KEY_QUICK_TURN_ON_ASSISTANT_APP = "quick_turn_on_assistant_app";
    private AssistUtils mAssistUtils;
    private AlertDialog mAssistantAppDialog;
    private final List<Info> mAvailableAssistants = new ArrayList();
    protected PackageManager mPm;
    private AlertDialog mSelectAssistantAppDialog;
    private MasterSwitchPreference mSwitch;
    private MasterSwitchController mSwitchController;

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

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY_QUICK_TURN_ON_ASSISTANT_APP;
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

    public OPAssistantAPPSwitchPreferenceController(Context context) {
        super(context, KEY_QUICK_TURN_ON_ASSISTANT_APP);
        this.mPm = context.getPackageManager();
        this.mAssistUtils = new AssistUtils(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitch = (MasterSwitchPreference) preferenceScreen.findPreference(KEY_QUICK_TURN_ON_ASSISTANT_APP);
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

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable()) {
            List<DefaultAppInfo> candidates = getCandidates();
            boolean z = true;
            if (candidates.size() > 0 && getDefaultKey() == null) {
                MasterSwitchPreference masterSwitchPreference = this.mSwitch;
                if (Settings.System.getInt(this.mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0) != 1) {
                    z = false;
                }
                masterSwitchPreference.setChecked(z);
            } else if (candidates.size() == 0 || getDefaultKey() == null) {
                this.mSwitch.setChecked(false);
                Settings.System.putInt(this.mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0);
            } else {
                String defaultKey = getDefaultKey();
                if (defaultKey == null || isEnabledApp(defaultKey.split("/")[0])) {
                    MasterSwitchPreference masterSwitchPreference2 = this.mSwitch;
                    if (Settings.System.getInt(this.mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0) != 1) {
                        z = false;
                    }
                    masterSwitchPreference2.setChecked(z);
                } else {
                    this.mSwitch.setChecked(false);
                    Settings.System.putInt(this.mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0);
                }
            }
            MasterSwitchPreference masterSwitchPreference3 = this.mSwitch;
            if (masterSwitchPreference3 != null) {
                MasterSwitchController masterSwitchController = new MasterSwitchController(masterSwitchPreference3);
                this.mSwitchController = masterSwitchController;
                masterSwitchController.setListener(this);
                this.mSwitchController.startListening();
            }
        }
    }

    private void noAssistantAppDialog() {
        AnonymousClass1 r0 = new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.gestures.OPAssistantAPPSwitchPreferenceController.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    Settings.System.putInt(((AbstractPreferenceController) OPAssistantAPPSwitchPreferenceController.this).mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0);
                    OPUtils.sendAppTracker("quick_turn_on_voice_assistant", "off");
                }
            }
        };
        AlertDialog create = new AlertDialog.Builder(this.mContext).setMessage(C0017R$string.oneplus_no_assistant_app_dialog).setPositiveButton(17039370, r0).setOnDismissListener(new DialogInterface.OnDismissListener() {
            /* class com.oneplus.settings.gestures.OPAssistantAPPSwitchPreferenceController.AnonymousClass2 */

            public void onDismiss(DialogInterface dialogInterface) {
                MasterSwitchPreference masterSwitchPreference = OPAssistantAPPSwitchPreferenceController.this.mSwitch;
                boolean z = false;
                if (Settings.System.getInt(((AbstractPreferenceController) OPAssistantAPPSwitchPreferenceController.this).mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0) != 0) {
                    z = true;
                }
                masterSwitchPreference.setChecked(z);
            }
        }).create();
        this.mAssistantAppDialog = create;
        create.show();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
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

    private void toSelectAssistantAppDialog() {
        AnonymousClass3 r0 = new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.gestures.OPAssistantAPPSwitchPreferenceController.AnonymousClass3 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    Intent intent = new Intent();
                    intent.setAction("com.oneplus.intent.OPDefaultVoiceAssistPicker");
                    intent.putExtra("assistntapp", "switch");
                    ((AbstractPreferenceController) OPAssistantAPPSwitchPreferenceController.this).mContext.startActivity(intent);
                } else if (i == -2) {
                    Settings.System.putInt(((AbstractPreferenceController) OPAssistantAPPSwitchPreferenceController.this).mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0);
                    OPUtils.sendAppTracker("quick_turn_on_voice_assistant", "off");
                }
            }
        };
        AlertDialog create = new AlertDialog.Builder(this.mContext).setMessage(C0017R$string.oneplus_to_select_assistant_app_dialog).setPositiveButton(17039370, r0).setNegativeButton(17039360, r0).setOnDismissListener(new DialogInterface.OnDismissListener() {
            /* class com.oneplus.settings.gestures.OPAssistantAPPSwitchPreferenceController.AnonymousClass4 */

            public void onDismiss(DialogInterface dialogInterface) {
                MasterSwitchPreference masterSwitchPreference = OPAssistantAPPSwitchPreferenceController.this.mSwitch;
                boolean z = false;
                if (Settings.System.getInt(((AbstractPreferenceController) OPAssistantAPPSwitchPreferenceController.this).mContext.getContentResolver(), "quick_turn_on_voice_assistant", 0) != 0) {
                    z = true;
                }
                masterSwitchPreference.setChecked(z);
            }
        }).create();
        this.mSelectAssistantAppDialog = create;
        create.show();
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

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        List<DefaultAppInfo> candidates = getCandidates();
        if (candidates.size() == 0) {
            noAssistantAppDialog();
            return true;
        } else if (candidates.size() <= 0) {
            return true;
        } else {
            if (getDefaultKey() == null || !OPUtils.isApplicationEnabled(this.mContext, getDefaultKey().split("/")[0])) {
                toSelectAssistantAppDialog();
                return true;
            }
            Settings.System.putInt(this.mContext.getContentResolver(), "quick_turn_on_voice_assistant", z ? 1 : 0);
            OPUtils.sendAppTracker("quick_turn_on_voice_assistant", z ? "on" : "off");
            return true;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_QUICK_TURN_ON_ASSISTANT_APP.equals(preference.getKey())) {
            return false;
        }
        Intent intent = new Intent();
        intent.setAction("com.oneplus.intent.OPDefaultVoiceAssistPicker");
        intent.putExtra("assistntapp", "onClick");
        this.mContext.startActivity(intent);
        return true;
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
