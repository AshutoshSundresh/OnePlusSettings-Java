package com.android.settings.notification;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.text.TextUtils;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.internal.telephony.SmsApplication;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.RingtonePreference;
import com.android.settings.core.OnActivityResultListener;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.VolumeSeekBarPreference;
import com.android.settings.notification.zen.ZenModePreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.sound.AudioSwitchPreferenceController;
import com.android.settings.sound.HandsFreeProfileOutputPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.UpdatableListPreferenceDialogFragment;
import com.oneplus.settings.controllers.OPDolbyAtmosControlPreferenceController;
import com.oneplus.settings.controllers.OPEarphoneModeControlPreferenceController;
import com.oneplus.settings.controllers.OPSMSRingtonePreferenceController;
import com.oneplus.settings.controllers.OPSoundEffectPreferenceCategoryController;
import com.oneplus.settings.controllers.OPSoundOtherPreferenceCategoryController;
import com.oneplus.settings.controllers.OPSoundTunerControlPreferenceController;
import com.oneplus.settings.notification.OPSeekBarVolumizer;
import com.oneplus.settings.notification.OPSystemVibratePreferenceController;
import com.oneplus.settings.notification.OPSystemXVibratePreferenceController;
import com.oneplus.settings.notification.SoundVolumePreferenceCategoryController;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SoundSettings extends DashboardFragment implements OnActivityResultListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.sound_settings) {
        /* class com.android.settings.notification.SoundSettings.AnonymousClass2 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return SoundSettings.buildPreferenceControllers(context, null, null);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (OPUtils.isSupportUssOnly() || OPUtils.isSupportGoogleCommSuit()) {
                nonIndexableKeys.add("message_ringtone");
            }
            if (OPUtils.isSupportUstUnify() && !SoundSettings.isDefaultOPSms) {
                nonIndexableKeys.add("message_ringtone");
            }
            if (ProductUtils.isUsvMode()) {
                nonIndexableKeys.add("message_ringtone");
            }
            return nonIndexableKeys;
        }
    };
    static final int STOP_SAMPLE = 1;
    private static boolean isDefaultOPSms = false;
    private UpdatableListPreferenceDialogFragment mDialogFragment;
    final Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.android.settings.notification.SoundSettings.AnonymousClass1 */

        public void handleMessage(Message message) {
            if (message.what == 1) {
                SoundSettings.this.mVolumeCallback.stopSample();
            }
        }
    };
    private String mHfpOutputControllerKey;
    private RingtonePreference mRequestPreference;
    private Preference mSmsRingtonePreference;
    final VolumePreferenceCallback mVolumeCallback = new VolumePreferenceCallback();

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SoundSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 336;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            String string = bundle.getString("selected_preference", null);
            if (!TextUtils.isEmpty(string)) {
                this.mRequestPreference = (RingtonePreference) findPreference(string);
            }
            this.mDialogFragment = (UpdatableListPreferenceDialogFragment) getFragmentManager().findFragmentByTag("SoundSettings");
        }
        this.mSmsRingtonePreference = getPreferenceScreen().findPreference("message_ringtone");
        if (OPUtils.isSupportUssOnly() || OPUtils.isSupportGoogleCommSuit()) {
            removeSmsRingtone();
        }
        if (OPUtils.isSupportUstUnify()) {
            getDefaultSms();
            if (!isDefaultOPSms) {
                removeSmsRingtone();
            }
        }
        if (ProductUtils.isUsvMode()) {
            removeSmsRingtone();
        }
    }

    private void removeSmsRingtone() {
        Preference preference;
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("ringtone_and_vibrate");
        if (preferenceCategory != null && (preference = this.mSmsRingtonePreference) != null) {
            preferenceCategory.removePreference(preference);
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_sound;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        this.mVolumeCallback.stopSample();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener, com.android.settings.dashboard.DashboardFragment
    public boolean onPreferenceTreeClick(Preference preference) {
        if (!(preference instanceof RingtonePreference)) {
            return super.onPreferenceTreeClick(preference);
        }
        writePreferenceClickMetric(preference);
        RingtonePreference ringtonePreference = (RingtonePreference) preference;
        this.mRequestPreference = ringtonePreference;
        ringtonePreference.onPrepareRingtonePickerIntent(ringtonePreference.getIntent());
        if (this.mRequestPreference.getIntent() == null) {
            return true;
        }
        getActivity().startActivityForResultAsUser(this.mRequestPreference.getIntent(), 200, null, UserHandle.of(this.mRequestPreference.getUserId()));
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceManager.OnDisplayPreferenceDialogListener, androidx.preference.PreferenceFragmentCompat
    public void onDisplayPreferenceDialog(Preference preference) {
        UpdatableListPreferenceDialogFragment newInstance = UpdatableListPreferenceDialogFragment.newInstance(preference.getKey(), this.mHfpOutputControllerKey.equals(preference.getKey()) ? 1416 : 0);
        this.mDialogFragment = newInstance;
        newInstance.setTargetFragment(this, 0);
        this.mDialogFragment.show(getFragmentManager(), "SoundSettings");
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.sound_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this, getSettingsLifecycle());
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        RingtonePreference ringtonePreference = this.mRequestPreference;
        if (ringtonePreference != null) {
            ringtonePreference.onActivityResult(i, i2, intent);
            this.mRequestPreference = null;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        RingtonePreference ringtonePreference = this.mRequestPreference;
        if (ringtonePreference != null) {
            bundle.putString("selected_preference", ringtonePreference.getKey());
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ArrayList arrayList = new ArrayList();
        AlarmVolumePreferenceController alarmVolumePreferenceController = (AlarmVolumePreferenceController) use(AlarmVolumePreferenceController.class);
        arrayList.add(alarmVolumePreferenceController);
        MediaVolumePreferenceController mediaVolumePreferenceController = (MediaVolumePreferenceController) use(MediaVolumePreferenceController.class);
        arrayList.add(mediaVolumePreferenceController);
        RingVolumePreferenceController ringVolumePreferenceController = (RingVolumePreferenceController) use(RingVolumePreferenceController.class);
        arrayList.add(ringVolumePreferenceController);
        ((SoundVolumePreferenceCategoryController) use(SoundVolumePreferenceCategoryController.class)).setChildren(Arrays.asList(alarmVolumePreferenceController, mediaVolumePreferenceController, ringVolumePreferenceController));
        arrayList.add((VolumeSeekBarPreferenceController) use(NotificationVolumePreferenceController.class));
        if (ProductUtils.isUsvMode()) {
            arrayList.add((CallVolumePreferenceController) use(CallVolumePreferenceController.class));
        }
        ((HandsFreeProfileOutputPreferenceController) use(HandsFreeProfileOutputPreferenceController.class)).setCallback(new AudioSwitchPreferenceController.AudioSwitchCallback() {
            /* class com.android.settings.notification.$$Lambda$SoundSettings$N7fFCKwOwYJug19RG1Wew_H_2JM */

            @Override // com.android.settings.sound.AudioSwitchPreferenceController.AudioSwitchCallback
            public final void onPreferenceDataChanged(ListPreference listPreference) {
                SoundSettings.this.lambda$onAttach$0$SoundSettings(listPreference);
            }
        });
        this.mHfpOutputControllerKey = ((HandsFreeProfileOutputPreferenceController) use(HandsFreeProfileOutputPreferenceController.class)).getPreferenceKey();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            VolumeSeekBarPreferenceController volumeSeekBarPreferenceController = (VolumeSeekBarPreferenceController) it.next();
            volumeSeekBarPreferenceController.setCallback(this.mVolumeCallback);
            getSettingsLifecycle().addObserver(volumeSeekBarPreferenceController);
        }
    }

    final class VolumePreferenceCallback implements VolumeSeekBarPreference.Callback {
        private OPSeekBarVolumizer mCurrent;

        VolumePreferenceCallback() {
        }

        @Override // com.android.settings.notification.VolumeSeekBarPreference.Callback
        public void onSampleStarting(OPSeekBarVolumizer oPSeekBarVolumizer) {
            OPSeekBarVolumizer oPSeekBarVolumizer2 = this.mCurrent;
            if (!(oPSeekBarVolumizer2 == null || oPSeekBarVolumizer2 == oPSeekBarVolumizer)) {
                oPSeekBarVolumizer2.stopSample();
            }
            this.mCurrent = oPSeekBarVolumizer;
            if (oPSeekBarVolumizer != null) {
                SoundSettings.this.mHandler.removeMessages(1);
                SoundSettings.this.mHandler.sendEmptyMessageDelayed(1, 2000);
            }
        }

        @Override // com.android.settings.notification.VolumeSeekBarPreference.Callback
        public void onStreamValueChanged(int i, int i2) {
            if (this.mCurrent != null) {
                SoundSettings.this.mHandler.removeMessages(1);
                SoundSettings.this.mHandler.sendEmptyMessageDelayed(1, 2000);
            }
        }

        public void stopSample() {
            OPSeekBarVolumizer oPSeekBarVolumizer = this.mCurrent;
            if (oPSeekBarVolumizer != null) {
                oPSeekBarVolumizer.stopSample();
            }
        }
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, SoundSettings soundSettings, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PhoneRingtonePreferenceController(context));
        arrayList.add(new AlarmRingtonePreferenceController(context));
        arrayList.add(new NotificationRingtonePreferenceController(context));
        arrayList.add(new OPSMSRingtonePreferenceController(context));
        if (ProductUtils.isUsvMode()) {
            arrayList.add(new CallVolumePreferenceController(context, "call_volume"));
        }
        arrayList.add(new WorkSoundPreferenceController(context, soundSettings, lifecycle));
        arrayList.add(new OPSystemVibratePreferenceController(context, lifecycle));
        arrayList.add(new OPSystemXVibratePreferenceController(context, lifecycle));
        OPDolbyAtmosControlPreferenceController oPDolbyAtmosControlPreferenceController = new OPDolbyAtmosControlPreferenceController(context);
        ZenModePreferenceController zenModePreferenceController = new ZenModePreferenceController(context, "zen_mode");
        OPEarphoneModeControlPreferenceController oPEarphoneModeControlPreferenceController = new OPEarphoneModeControlPreferenceController(context);
        arrayList.add(new OPSoundEffectPreferenceCategoryController(context, "sound_effect").setChildren(Arrays.asList(oPDolbyAtmosControlPreferenceController, zenModePreferenceController, oPEarphoneModeControlPreferenceController)));
        arrayList.add(new OPSoundOtherPreferenceCategoryController(context, "do_not_disturb").setChildren(Arrays.asList(zenModePreferenceController)));
        OPSoundTunerControlPreferenceController oPSoundTunerControlPreferenceController = new OPSoundTunerControlPreferenceController(context);
        arrayList.add(new OPSoundOtherPreferenceCategoryController(context, "earphone").setChildren(Arrays.asList(oPEarphoneModeControlPreferenceController, oPSoundTunerControlPreferenceController)));
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public void enableWorkSync() {
        WorkSoundPreferenceController workSoundPreferenceController = (WorkSoundPreferenceController) use(WorkSoundPreferenceController.class);
        if (workSoundPreferenceController != null) {
            workSoundPreferenceController.enableWorkSync();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onPreferenceDataChanged */
    public void lambda$onAttach$0(ListPreference listPreference) {
        UpdatableListPreferenceDialogFragment updatableListPreferenceDialogFragment = this.mDialogFragment;
        if (updatableListPreferenceDialogFragment != null) {
            updatableListPreferenceDialogFragment.onListPreferenceUpdated(listPreference);
        }
    }

    private void getDefaultSms() {
        ComponentName defaultSmsApplication = SmsApplication.getDefaultSmsApplication(getPrefContext(), true);
        if (defaultSmsApplication == null || TextUtils.isEmpty(defaultSmsApplication.getPackageName()) || !defaultSmsApplication.getPackageName().equals("com.oneplus.mms")) {
            isDefaultOPSms = false;
        } else {
            isDefaultOPSms = true;
        }
    }
}
