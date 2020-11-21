package com.android.settings.applications.assist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.app.AssistUtils;
import com.android.settings.applications.assist.DefaultVoiceInputPicker;
import com.android.settings.applications.assist.VoiceInputHelper;
import com.android.settings.applications.defaultapps.DefaultAppPreferenceController;
import com.android.settingslib.applications.DefaultAppInfo;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import java.util.Iterator;
import java.util.List;

public class DefaultVoiceInputPreferenceController extends DefaultAppPreferenceController implements LifecycleObserver, OnResume, OnPause {
    private AssistUtils mAssistUtils;
    private VoiceInputHelper mHelper;
    private Preference mPreference;
    private PreferenceScreen mScreen;
    private SettingObserver mSettingObserver = new SettingObserver();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "voice_input_settings";
    }

    public DefaultVoiceInputPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mAssistUtils = new AssistUtils(context);
        VoiceInputHelper voiceInputHelper = new VoiceInputHelper(context);
        this.mHelper = voiceInputHelper;
        voiceInputHelper.buildUi();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !DefaultVoiceInputPicker.isCurrentAssistVoiceService(this.mAssistUtils.getAssistComponentForUser(this.mUserId), DefaultVoiceInputPicker.getCurrentService(this.mHelper));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mScreen = preferenceScreen;
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mSettingObserver.register(this.mContext.getContentResolver(), true);
        updatePreference();
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(this.mPreference);
        updatePreference();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mSettingObserver.register(this.mContext.getContentResolver(), false);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public DefaultAppInfo getDefaultAppInfo() {
        String defaultAppKey = getDefaultAppKey();
        if (defaultAppKey == null) {
            return null;
        }
        Iterator<VoiceInputHelper.InteractionInfo> it = this.mHelper.mAvailableInteractionInfos.iterator();
        while (it.hasNext()) {
            VoiceInputHelper.InteractionInfo next = it.next();
            if (TextUtils.equals(defaultAppKey, next.key)) {
                return new DefaultVoiceInputPicker.VoiceInputDefaultAppInfo(this.mContext, this.mPackageManager, this.mUserId, next, true);
            }
        }
        Iterator<VoiceInputHelper.RecognizerInfo> it2 = this.mHelper.mAvailableRecognizerInfos.iterator();
        while (it2.hasNext()) {
            VoiceInputHelper.RecognizerInfo next2 = it2.next();
            if (TextUtils.equals(defaultAppKey, next2.key)) {
                return new DefaultVoiceInputPicker.VoiceInputDefaultAppInfo(this.mContext, this.mPackageManager, this.mUserId, next2, true);
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public Intent getSettingIntent(DefaultAppInfo defaultAppInfo) {
        DefaultAppInfo defaultAppInfo2 = getDefaultAppInfo();
        if (defaultAppInfo2 == null || !(defaultAppInfo2 instanceof DefaultVoiceInputPicker.VoiceInputDefaultAppInfo)) {
            return null;
        }
        return ((DefaultVoiceInputPicker.VoiceInputDefaultAppInfo) defaultAppInfo2).getSettingIntent();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updatePreference() {
        if (this.mPreference != null) {
            this.mHelper.buildUi();
            if (!isAvailable()) {
                this.mScreen.removePreference(this.mPreference);
            } else if (this.mScreen.findPreference(getPreferenceKey()) == null) {
                this.mScreen.addPreference(this.mPreference);
            }
        }
    }

    private String getDefaultAppKey() {
        ComponentName currentService = DefaultVoiceInputPicker.getCurrentService(this.mHelper);
        if (currentService == null) {
            return null;
        }
        return currentService.flattenToShortString();
    }

    class SettingObserver extends AssistSettingObserver {
        /* access modifiers changed from: protected */
        @Override // com.android.settings.applications.assist.AssistSettingObserver
        public List<Uri> getSettingUris() {
            return null;
        }

        SettingObserver() {
        }

        @Override // com.android.settings.applications.assist.AssistSettingObserver
        public void onSettingChange() {
            DefaultVoiceInputPreferenceController.this.updatePreference();
        }
    }
}
