package com.oneplus.settings.application.assist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.provider.Settings;
import android.service.voice.VoiceInteractionServiceInfo;
import android.text.TextUtils;
import com.android.internal.app.AssistUtils;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.defaultapps.DefaultAppPickerFragment;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.widget.CandidateInfo;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPDefaultVoiceAssistPicker extends DefaultAppPickerFragment {
    private static final Intent ASSIST_ACTIVITY_PROBE = new Intent("android.intent.action.ASSIST");
    private static final Intent ASSIST_SERVICE_PROBE = new Intent("android.service.voice.VoiceInteractionService");
    private final List<Info> mAvailableAssistants = new ArrayList();

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment
    public String getConfirmationMessage(CandidateInfo candidateInfo) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 843;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean shouldShowItemNone() {
        return true;
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        new AssistUtils(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_default_voice_assist_settings;
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

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        String stringForUser = Settings.Secure.getStringForUser(getContext().getContentResolver(), "oneplus_default_voice_assist_picker_service", this.mUserId);
        if (TextUtils.isEmpty(stringForUser)) {
            return;
        }
        if (!OPUtils.isAppPakExist(getContext(), stringForUser.split("/")[0]) || !OPUtils.isApplicationEnabled(getContext(), stringForUser.split("/")[0])) {
            updateCandidates();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        String string = getActivity().getIntent().getExtras().getString("assistntapp");
        if (TextUtils.isEmpty(str)) {
            setAssistNone();
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
        String stringForUser = Settings.Secure.getStringForUser(getContext().getContentResolver(), "oneplus_default_voice_assist_picker_service", this.mUserId);
        if ((TextUtils.isEmpty(stringForUser) || (OPUtils.isAppPakExist(getContext(), stringForUser.split("/")[0]) && OPUtils.isApplicationEnabled(getContext(), stringForUser.split("/")[0]))) && stringForUser != null) {
            return ComponentName.unflattenFromString(stringForUser);
        }
        return null;
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
        Settings.Secure.putString(getContext().getContentResolver(), "oneplus_default_voice_assist_picker_service", "");
    }

    private void setAssistService(Info info) {
        String flattenToShortString = info.component.flattenToShortString();
        new ComponentName(info.component.getPackageName(), info.voiceInteractionServiceInfo.getRecognitionService()).flattenToShortString();
        Settings.Secure.putString(getContext().getContentResolver(), "oneplus_default_voice_assist_picker_service", flattenToShortString);
    }

    private void setAssistActivity(Info info) {
        Settings.Secure.putString(getContext().getContentResolver(), "oneplus_default_voice_assist_picker_service", info.component.flattenToShortString());
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
