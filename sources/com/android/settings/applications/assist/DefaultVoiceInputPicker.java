package com.android.settings.applications.assist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.internal.app.AssistUtils;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.assist.VoiceInputHelper;
import com.android.settings.applications.defaultapps.DefaultAppPickerFragment;
import com.android.settingslib.applications.DefaultAppInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultVoiceInputPicker extends DefaultAppPickerFragment {
    private String mAssistRestrict;
    private AssistUtils mAssistUtils;
    private VoiceInputHelper mHelper;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 844;
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mAssistUtils = new AssistUtils(context);
        VoiceInputHelper voiceInputHelper = new VoiceInputHelper(context);
        this.mHelper = voiceInputHelper;
        voiceInputHelper.buildUi();
        ComponentName currentAssist = getCurrentAssist();
        if (isCurrentAssistVoiceService(currentAssist, getCurrentService(this.mHelper))) {
            this.mAssistRestrict = currentAssist.flattenToShortString();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.default_voice_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<VoiceInputDefaultAppInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        Context context = getContext();
        Iterator<VoiceInputHelper.InteractionInfo> it = this.mHelper.mAvailableInteractionInfos.iterator();
        boolean z = true;
        while (it.hasNext()) {
            VoiceInputHelper.InteractionInfo next = it.next();
            boolean equals = TextUtils.equals(next.key, this.mAssistRestrict);
            arrayList.add(new VoiceInputDefaultAppInfo(context, this.mPm, this.mUserId, next, equals));
            z |= equals;
        }
        boolean z2 = !z;
        Iterator<VoiceInputHelper.RecognizerInfo> it2 = this.mHelper.mAvailableRecognizerInfos.iterator();
        while (it2.hasNext()) {
            arrayList.add(new VoiceInputDefaultAppInfo(context, this.mPm, this.mUserId, it2.next(), !z2));
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        ComponentName currentService = getCurrentService(this.mHelper);
        if (currentService == null) {
            return null;
        }
        return currentService.flattenToShortString();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        Iterator<VoiceInputHelper.InteractionInfo> it = this.mHelper.mAvailableInteractionInfos.iterator();
        while (it.hasNext()) {
            VoiceInputHelper.InteractionInfo next = it.next();
            if (TextUtils.equals(str, next.key)) {
                Settings.Secure.putString(getContext().getContentResolver(), "voice_interaction_service", str);
                Settings.Secure.putString(getContext().getContentResolver(), "voice_recognition_service", new ComponentName(next.service.packageName, next.serviceInfo.getRecognitionService()).flattenToShortString());
                return true;
            }
        }
        Iterator<VoiceInputHelper.RecognizerInfo> it2 = this.mHelper.mAvailableRecognizerInfos.iterator();
        while (true) {
            if (it2.hasNext()) {
                if (TextUtils.equals(str, it2.next().key)) {
                    Settings.Secure.putString(getContext().getContentResolver(), "voice_interaction_service", "");
                    Settings.Secure.putString(getContext().getContentResolver(), "voice_recognition_service", str);
                    break;
                }
            } else {
                break;
            }
        }
        return true;
    }

    public static ComponentName getCurrentService(VoiceInputHelper voiceInputHelper) {
        ComponentName componentName = voiceInputHelper.mCurrentVoiceInteraction;
        if (componentName != null) {
            return componentName;
        }
        ComponentName componentName2 = voiceInputHelper.mCurrentRecognizer;
        if (componentName2 != null) {
            return componentName2;
        }
        return null;
    }

    private ComponentName getCurrentAssist() {
        return this.mAssistUtils.getAssistComponentForUser(this.mUserId);
    }

    public static boolean isCurrentAssistVoiceService(ComponentName componentName, ComponentName componentName2) {
        return (componentName == null && componentName2 == null) || (componentName != null && componentName.equals(componentName2));
    }

    public static class VoiceInputDefaultAppInfo extends DefaultAppInfo {
        public VoiceInputHelper.BaseInfo mInfo;

        public VoiceInputDefaultAppInfo(Context context, PackageManager packageManager, int i, VoiceInputHelper.BaseInfo baseInfo, boolean z) {
            super(context, packageManager, i, baseInfo.componentName, (String) null, z);
            this.mInfo = baseInfo;
        }

        @Override // com.android.settingslib.widget.CandidateInfo, com.android.settingslib.applications.DefaultAppInfo
        public String getKey() {
            return this.mInfo.key;
        }

        @Override // com.android.settingslib.widget.CandidateInfo, com.android.settingslib.applications.DefaultAppInfo
        public CharSequence loadLabel() {
            VoiceInputHelper.BaseInfo baseInfo = this.mInfo;
            if (baseInfo instanceof VoiceInputHelper.InteractionInfo) {
                return baseInfo.appLabel;
            }
            return baseInfo.label;
        }

        public Intent getSettingIntent() {
            if (this.mInfo.settings == null) {
                return null;
            }
            return new Intent("android.intent.action.MAIN").setComponent(this.mInfo.settings);
        }
    }
}
