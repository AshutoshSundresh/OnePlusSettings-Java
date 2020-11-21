package com.oneplus.settings.application.assist;

import android.app.role.RoleManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Process;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.voice.VoiceInteractionServiceInfo;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.app.AssistUtils;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.defaultapps.DefaultAppPickerFragment;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.widget.CandidateInfo;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class DefaultAssistPicker extends DefaultAppPickerFragment {
    private static final Intent ASSIST_ACTIVITY_PROBE = new Intent("android.intent.action.ASSIST");
    private static final Intent ASSIST_SERVICE_PROBE = new Intent("android.service.voice.VoiceInteractionService");
    private AssistUtils mAssistUtils;
    private final List<Info> mAvailableAssistants = new ArrayList();

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 843;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean shouldShowItemNone() {
        return false;
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mAssistUtils = new AssistUtils(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.default_assist_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<DefaultAppInfo> getCandidates() {
        this.mAvailableAssistants.clear();
        addAssistServices();
        addAssistActivities();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (Info info : this.mAvailableAssistants) {
            String packageName = info.component.getPackageName();
            if (!arrayList.contains(packageName)) {
                arrayList.add(packageName);
                arrayList2.add(new DefaultAppInfo(getContext(), this.mPm, this.mUserId, info.component));
            }
        }
        return arrayList2;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        ComponentName currentAssist = getCurrentAssist();
        if (currentAssist != null) {
            return new DefaultAppInfo(getContext(), this.mPm, this.mUserId, currentAssist).getKey();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment
    public String getConfirmationMessage(CandidateInfo candidateInfo) {
        if (candidateInfo == null) {
            return null;
        }
        return getContext().getString(C0017R$string.assistant_security_warning, candidateInfo.loadLabel());
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        String string = getActivity().getIntent().getExtras().getString("assistntapp");
        if (TextUtils.isEmpty(str)) {
            setAssistNone();
            if ("switch".equals(string) || "onClick".equals(string)) {
                Settings.System.putInt(getContext().getContentResolver(), "quick_turn_on_voice_assistant", 0);
                OPUtils.sendAppTracker("quick_turn_on_voice_assistant", "off");
            }
            return true;
        }
        Info findAssistantByPackageName = findAssistantByPackageName(ComponentName.unflattenFromString(str).getPackageName());
        if (findAssistantByPackageName == null) {
            setAssistNone();
            if ("switch".equals(string) || "onClick".equals(string)) {
                Settings.System.putInt(getContext().getContentResolver(), "quick_turn_on_voice_assistant", 0);
                OPUtils.sendAppTracker("quick_turn_on_voice_assistant", "off");
            }
            return true;
        }
        if (findAssistantByPackageName.isVoiceInteractionService()) {
            setAssistService(findAssistantByPackageName);
        } else {
            setAssistActivity(findAssistantByPackageName);
        }
        if ("switch".equals(string)) {
            Settings.System.putInt(getContext().getContentResolver(), "quick_turn_on_voice_assistant", 1);
            OPUtils.sendAppTracker("quick_turn_on_voice_assistant", "on");
        }
        return true;
    }

    public ComponentName getCurrentAssist() {
        return this.mAssistUtils.getAssistComponentForUser(this.mUserId);
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

    private Info findAssistantByPackageName(String str) {
        for (Info info : this.mAvailableAssistants) {
            if (TextUtils.equals(info.component.getPackageName(), str)) {
                return info;
            }
        }
        return null;
    }

    private void setAssistNone() {
        String str = Settings.Secure.getString(getContext().getContentResolver(), "assistant").split("/")[0];
        if (!TextUtils.isEmpty(str)) {
            setRoleHolderAsUser("android.app.role.ASSISTANT", str, false, 0, Process.myUserHandle(), getContext());
        }
        clearRoleHoldersAsUser("android.app.role.ASSISTANT", 0, Process.myUserHandle(), getContext());
    }

    private void setAssistService(Info info) {
        String flattenToShortString = info.component.flattenToShortString();
        String flattenToShortString2 = new ComponentName(info.component.getPackageName(), info.voiceInteractionServiceInfo.getRecognitionService()).flattenToShortString();
        setRoleHolderAsUser("android.app.role.ASSISTANT", info.component.getPackageName(), true, 0, Process.myUserHandle(), getContext());
        Settings.Secure.putString(getContext().getContentResolver(), "assistant", flattenToShortString);
        Settings.Secure.putString(getContext().getContentResolver(), "voice_interaction_service", flattenToShortString);
        Settings.Secure.putString(getContext().getContentResolver(), "voice_recognition_service", flattenToShortString2);
    }

    private void clearRoleHoldersAsUser(String str, int i, UserHandle userHandle, Context context) {
        ((RoleManager) context.getSystemService(RoleManager.class)).clearRoleHoldersAsUser(str, i, userHandle, context.getMainExecutor(), new Consumer(str) {
            /* class com.oneplus.settings.application.assist.$$Lambda$DefaultAssistPicker$XiYEMVQdqwpkl7wz6AEaiweN2A */
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DefaultAssistPicker.this.lambda$clearRoleHoldersAsUser$0$DefaultAssistPicker(this.f$1, (Boolean) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$clearRoleHoldersAsUser$0 */
    public /* synthetic */ void lambda$clearRoleHoldersAsUser$0$DefaultAssistPicker(String str, Boolean bool) {
        if (bool.booleanValue()) {
            Log.i("DefaultAssistPicker", "clearRoleHoldersAsUser Cleared role holders, role: " + str);
            if (getContext() != null) {
                Settings.Secure.putString(getContext().getContentResolver(), "assistant", "");
                Settings.Secure.putString(getContext().getContentResolver(), "voice_interaction_service", "");
                Settings.Secure.putString(getContext().getContentResolver(), "voice_recognition_service", getDefaultRecognizer());
                return;
            }
            return;
        }
        Log.i("DefaultAssistPicker", "clearRoleHoldersAsUser Failed to clear role holders, role: " + str);
    }

    private void setRoleHolderAsUser(String str, String str2, boolean z, int i, UserHandle userHandle, Context context) {
        RoleManager roleManager = (RoleManager) context.getSystemService(RoleManager.class);
        Executor mainExecutor = context.getMainExecutor();
        $$Lambda$DefaultAssistPicker$4bytTVT0TnekX9LCVR6gTfHLR70 r6 = new Consumer(str) {
            /* class com.oneplus.settings.application.assist.$$Lambda$DefaultAssistPicker$4bytTVT0TnekX9LCVR6gTfHLR70 */
            public final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DefaultAssistPicker.lambda$setRoleHolderAsUser$1(this.f$0, (Boolean) obj);
            }
        };
        if (z) {
            roleManager.addRoleHolderAsUser(str, str2, i, userHandle, mainExecutor, r6);
        } else {
            roleManager.removeRoleHolderAsUser(str, str2, i, userHandle, mainExecutor, r6);
        }
    }

    static /* synthetic */ void lambda$setRoleHolderAsUser$1(String str, Boolean bool) {
        if (bool.booleanValue()) {
            Log.i("DefaultAssistPicker", "setRoleHolderAsUser Cleared role holders, role: " + str);
            return;
        }
        Log.i("DefaultAssistPicker", "setRoleHolderAsUser Failed to clear role holders, role: " + str);
    }

    private void setAssistActivity(Info info) {
        setRoleHolderAsUser("android.app.role.ASSISTANT", info.component.getPackageName(), true, 0, Process.myUserHandle(), getContext());
        Settings.Secure.putString(getContext().getContentResolver(), "assistant", info.component.flattenToShortString());
        Settings.Secure.putString(getContext().getContentResolver(), "voice_interaction_service", "");
        Settings.Secure.putString(getContext().getContentResolver(), "voice_recognition_service", getDefaultRecognizer());
    }

    private String getDefaultRecognizer() {
        ResolveInfo resolveService = this.mPm.resolveService(new Intent("android.speech.RecognitionService"), 128);
        if (resolveService == null || resolveService.serviceInfo == null) {
            Log.w("DefaultAssistPicker", "Unable to resolve default voice recognition service.");
            return "";
        }
        ServiceInfo serviceInfo = resolveService.serviceInfo;
        return new ComponentName(serviceInfo.packageName, serviceInfo.name).flattenToShortString();
    }

    /* access modifiers changed from: package-private */
    public static class Info {
        public final ComponentName component;
        public final VoiceInteractionServiceInfo voiceInteractionServiceInfo;

        Info(ComponentName componentName) {
            this.component = componentName;
            this.voiceInteractionServiceInfo = null;
        }

        Info(ComponentName componentName, VoiceInteractionServiceInfo voiceInteractionServiceInfo2) {
            this.component = componentName;
            this.voiceInteractionServiceInfo = voiceInteractionServiceInfo2;
        }

        public boolean isVoiceInteractionService() {
            return this.voiceInteractionServiceInfo != null;
        }
    }
}
