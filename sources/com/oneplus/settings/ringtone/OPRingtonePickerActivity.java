package com.oneplus.settings.ringtone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.provider.DeviceConfig;
import android.provider.Settings;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.oneplus.settings.notification.OPRingtoneVibrateStrengthSettings;
import com.oneplus.settings.notification.OPSMSNotificationVibrateIntensitySettings;
import com.oneplus.settings.ringtone.OPRingtoneManager;
import com.oneplus.settings.utils.OPNotificationUtils;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.VibratorSceneUtils;
import java.util.ArrayList;
import java.util.List;

public class OPRingtonePickerActivity extends OPRingtoneBaseActivity implements Preference.OnPreferenceClickListener {
    private Cursor mCursor;
    private OPRadioButtonPreference mDefualtPreference;
    private OPRadioButtonPreference mLocalPreference;
    private PreferenceCategory mMainRoot;
    private OPRadioButtonPreference mNOPreference;
    private int mRequestCode = 100;
    private PreferenceCategory mRingtoneVibrateCategory;
    private Preference mSMSNotificationVibrateIntensityPreference;
    private Preference mSim1Layout;
    private Uri mSim1Uri;
    private Preference mSim2Layout;
    private Uri mSim2Uri;
    private final BroadcastReceiver mSimStateReceiver = new BroadcastReceiver() {
        /* class com.oneplus.settings.ringtone.OPRingtonePickerActivity.AnonymousClass2 */

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction())) {
                OPRingtonePickerActivity oPRingtonePickerActivity = OPRingtonePickerActivity.this;
                if (oPRingtonePickerActivity.mType == 1 && oPRingtonePickerActivity.isMultiSimEnabled()) {
                    if (OPRingtonePickerActivity.this.mSim1Layout != null) {
                        OPRingtonePickerActivity.this.mSim1Layout.setEnabled(OPRingtonePickerActivity.this.getSim1Enable());
                    }
                    if (OPRingtonePickerActivity.this.mSim2Layout != null) {
                        OPRingtonePickerActivity.this.mSim2Layout.setEnabled(OPRingtonePickerActivity.this.getSim2Enable());
                    }
                }
            }
        }
    };
    private SwitchPreference mSwitch;
    private List<OPRadioButtonPreference> mSystemRings = null;
    private Uri mUriForLocalItem;
    private PreferenceCategory mVibrateCategory;
    protected long[] mVibratePattern;
    private Preference mVibrateRingPreference;
    private ContentObserver mVibrateWhenRingObserver = new ContentObserver(new Handler()) {
        /* class com.oneplus.settings.ringtone.OPRingtonePickerActivity.AnonymousClass3 */

        public void onChange(boolean z, Uri uri) {
            boolean z2 = false;
            if (Settings.System.getInt(OPRingtonePickerActivity.this.getApplicationContext().getContentResolver(), "vibrate_when_ringing", 0) != 0) {
                z2 = true;
            }
            if (OPRingtonePickerActivity.this.mVibrateWhenRingPreference != null) {
                OPRingtonePickerActivity.this.mVibrateWhenRingPreference.setChecked(z2);
            }
        }
    };
    private SwitchPreference mVibrateWhenRingPreference;
    protected Vibrator mVibrator;

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.ringtone.OPRingtoneBaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (OPUtils.isSupportXVibrate()) {
            this.mVibrator = (Vibrator) getSystemService("vibrator");
        }
        updatePreference();
    }

    private void updatePreference() {
        Cursor cursor = this.mRingtoneManager.getCursor();
        this.mCursor = cursor;
        if (cursor != null && (cursor instanceof CursorWrapper) && ((CursorWrapper) cursor).getWrappedCursor() == null) {
            this.mCursor = null;
        }
        if (this.mType != 1 || !isMultiSimEnabled()) {
            addPreferencesFromResource(C0019R$xml.op_ring_system_fragment);
            this.mLocalPreference = (OPRadioButtonPreference) findPreference("local_select");
            this.mNOPreference = (OPRadioButtonPreference) findPreference("no_select");
            OPRadioButtonPreference oPRadioButtonPreference = (OPRadioButtonPreference) findPreference("defualt_select");
            this.mDefualtPreference = oPRadioButtonPreference;
            oPRadioButtonPreference.setOnPreferenceClickListener(this);
            Preference findPreference = findPreference("sms_notification_vibrate_intensity");
            this.mSMSNotificationVibrateIntensityPreference = findPreference;
            findPreference.setOnPreferenceClickListener(this);
            this.mMainRoot = (PreferenceCategory) findPreference("setting_title");
            this.mRingtoneVibrateCategory = (PreferenceCategory) findPreference("ringtone_vibrate");
            if (this.mType != 1 || isMultiSimEnabled()) {
                getPreferenceScreen().removePreference(this.mRingtoneVibrateCategory);
            } else {
                this.mVibrateCategory = (PreferenceCategory) findPreference("vibrate");
                getPreferenceScreen().removePreference(this.mVibrateCategory);
                Preference findPreference2 = findPreference("vibrate_strength");
                this.mVibrateRingPreference = findPreference2;
                findPreference2.setOnPreferenceClickListener(this);
                initVibrateWhenRingPreference();
            }
            this.mMainRoot.removePreference(this.mDefualtPreference);
            initPreference(true);
        } else {
            addPreferencesFromResource(C0019R$xml.op_ring_switch_fragment);
            this.mMainRoot = (PreferenceCategory) findPreference("setting_title");
            this.mVibrateCategory = (PreferenceCategory) findPreference("vibrate");
            this.mSwitch = (SwitchPreference) findPreference("setting_key");
            this.mSim1Layout = findPreference("sim1_select");
            this.mSim2Layout = findPreference("sim2_select");
            this.mLocalPreference = (OPRadioButtonPreference) findPreference("local_select");
            this.mNOPreference = (OPRadioButtonPreference) findPreference("no_select");
            this.mSwitch.setOnPreferenceClickListener(this);
            this.mSim1Layout.setOnPreferenceClickListener(this);
            this.mSim2Layout.setOnPreferenceClickListener(this);
            this.mSwitch.setChecked(OPRingtoneManager.isRingSimSwitchOn(getApplicationContext()));
            if (this.mContactsRingtone || isProfileId()) {
                this.mMainRoot.removePreference(this.mSwitch);
            }
            switchSimRingtone(false);
            Preference findPreference3 = findPreference("vibrate_strength");
            this.mVibrateRingPreference = findPreference3;
            findPreference3.setOnPreferenceClickListener(this);
            initVibrateWhenRingPreference();
            if (this.mContactsRingtone) {
                getPreferenceScreen().removePreference(this.mVibrateCategory);
            }
        }
        this.mLocalPreference.setOnPreferenceClickListener(this);
        this.mNOPreference.setOnPreferenceClickListener(this);
        updateSelected();
    }

    private void initVibrateWhenRingPreference() {
        SwitchPreference switchPreference = (SwitchPreference) findPreference("vibrate_when_ringing");
        this.mVibrateWhenRingPreference = switchPreference;
        switchPreference.setOnPreferenceClickListener(this);
        if (!Utils.isVoiceCapable(getApplicationContext()) || isRampingRingerEnabled()) {
            this.mVibrateCategory.removePreference(this.mVibrateWhenRingPreference);
        }
    }

    private void switchSimRingtone(boolean z) {
        if (!this.mSwitch.isChecked() || this.mContactsRingtone) {
            this.mMainRoot.removePreference(this.mSim1Layout);
            this.mMainRoot.removePreference(this.mSim2Layout);
            initPreference(true);
            return;
        }
        this.mMainRoot.removePreference(this.mLocalPreference);
        this.mMainRoot.removePreference(this.mNOPreference);
    }

    private void initPreference(boolean z) {
        if (this.mSystemRings == null) {
            this.mSystemRings = new ArrayList();
            Cursor cursor = this.mCursor;
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    OPRadioButtonPreference oPRadioButtonPreference = new OPRadioButtonPreference(this);
                    oPRadioButtonPreference.setTitle(OPNotificationUtils.replaceWith(this, this.mCursor.getString(1), OPRingtoneManager.getSettingForType(this.mType)));
                    Uri uriFromCursor = OPRingtoneManager.getUriFromCursor(this.mCursor);
                    oPRadioButtonPreference.setKey(uriFromCursor.toString());
                    oPRadioButtonPreference.setOnPreferenceClickListener(this);
                    this.mSystemRings.add(oPRadioButtonPreference);
                    getPreferenceScreen().addPreference(oPRadioButtonPreference);
                    Uri uri = this.mUriForDefaultItem;
                    if (uri == null || !uri.toString().startsWith(uriFromCursor.toString())) {
                        oPRadioButtonPreference.setChecked(false);
                    } else {
                        oPRadioButtonPreference.setChecked(true);
                    }
                } while (this.mCursor.moveToNext());
                return;
            }
            return;
        }
        for (int i = 0; i < this.mSystemRings.size(); i++) {
            if (z) {
                getPreferenceScreen().addPreference(this.mSystemRings.get(i));
            } else {
                getPreferenceScreen().removePreference(this.mSystemRings.get(i));
            }
        }
        if (z) {
            this.mHandler.post(new Runnable() {
                /* class com.oneplus.settings.ringtone.OPRingtonePickerActivity.AnonymousClass1 */

                public void run() {
                    OPRingtonePickerActivity.this.updateSelected();
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.ringtone.OPRingtoneBaseActivity
    public void updateSelected() {
        OPRadioButtonPreference oPRadioButtonPreference;
        OPRadioButtonPreference oPRadioButtonPreference2;
        OPRadioButtonPreference oPRadioButtonPreference3;
        if (this.mType == 1) {
            updateSimSwitch();
        }
        if (this.mUriForDefaultItem == null) {
            this.mLocalPreference.setChecked(false);
            this.mNOPreference.setChecked(true);
            updateChecks("-1");
            this.mLocalPreference.setSummary(C0017R$string.oneplus_no_choice);
            this.mUriForLocalItem = null;
            if (this.mHasDefaultItem && (oPRadioButtonPreference3 = this.mDefualtPreference) != null) {
                oPRadioButtonPreference3.setChecked(false);
                return;
            }
            return;
        }
        this.mNOPreference.setChecked(false);
        if (!OPRingtoneManager.isDefault(this.mUriForDefaultItem) || !this.mHasDefaultItem) {
            if (OPRingtoneManager.isDefault(this.mUriForDefaultItem)) {
                this.mUriForDefaultItem = OPRingtoneManager.ringtoneRestoreFromDefault(getApplicationContext(), this.mType, this.mUriForDefaultItem);
            }
            if (OPRingtoneManager.isSystemRingtone(getApplicationContext(), this.mUriForDefaultItem, this.mType)) {
                this.mLocalPreference.setChecked(false);
                updateChecks(this.mUriForDefaultItem.toString());
                this.mLocalPreference.setSummary(C0017R$string.oneplus_no_choice);
                this.mUriForLocalItem = null;
                if (this.mHasDefaultItem && (oPRadioButtonPreference2 = this.mDefualtPreference) != null) {
                    oPRadioButtonPreference2.setChecked(false);
                    return;
                }
                return;
            }
            this.mLocalPreference.setChecked(true);
            if (this.mHasDefaultItem && (oPRadioButtonPreference = this.mDefualtPreference) != null) {
                oPRadioButtonPreference.setChecked(false);
            }
            OPRingtoneManager.ResultRing locatRingtoneTitle = OPRingtoneManager.getLocatRingtoneTitle(getApplicationContext(), this.mUriForDefaultItem, this.mType, 0);
            OPRadioButtonPreference oPRadioButtonPreference4 = this.mLocalPreference;
            String str = locatRingtoneTitle.title;
            if (str == null) {
                str = getString(C0017R$string.oneplus_no_choice);
            }
            oPRadioButtonPreference4.setSummary(str);
            Uri uri = locatRingtoneTitle.ringUri;
            this.mUriForDefaultItem = uri;
            this.mUriForLocalItem = uri;
            updateChecks("-1");
            return;
        }
        OPRadioButtonPreference oPRadioButtonPreference5 = this.mDefualtPreference;
        if (oPRadioButtonPreference5 != null) {
            oPRadioButtonPreference5.setChecked(true);
        }
        this.mLocalPreference.setChecked(false);
        this.mLocalPreference.setSummary(C0017R$string.oneplus_no_choice);
        updateChecks("-1");
        this.mUriForLocalItem = null;
    }

    private void updateSimSwitch() {
        String str;
        String str2;
        if (isMultiSimEnabled()) {
            Preference preference = this.mSim1Layout;
            if (preference != null) {
                preference.setEnabled(getSim1Enable());
            }
            Preference preference2 = this.mSim2Layout;
            if (preference2 != null) {
                preference2.setEnabled(getSim2Enable());
            }
            this.mSim1Uri = OPRingtoneManager.getActualRingtoneUriBySubId(getApplicationContext(), 0);
            this.mSim2Uri = OPRingtoneManager.getActualRingtoneUriBySubId(getApplicationContext(), 1);
            OPRingtoneManager.ResultRing locatRingtoneTitle = OPRingtoneManager.getLocatRingtoneTitle(getApplicationContext(), this.mSim1Uri, this.mType, 1);
            OPRingtoneManager.ResultRing locatRingtoneTitle2 = OPRingtoneManager.getLocatRingtoneTitle(getApplicationContext(), this.mSim2Uri, this.mType, 2);
            this.mSim1Uri = locatRingtoneTitle.ringUri;
            this.mSim2Uri = locatRingtoneTitle2.ringUri;
            String settingForType = OPRingtoneManager.getSettingForType(this.mType);
            Preference preference3 = this.mSim1Layout;
            if (preference3 != null) {
                String str3 = locatRingtoneTitle.title;
                if (str3 != null) {
                    str2 = OPNotificationUtils.replaceWith(this, str3, settingForType);
                } else {
                    str2 = getString(C0017R$string.oneplus_no_ringtone);
                }
                preference3.setSummary(str2);
            }
            Preference preference4 = this.mSim2Layout;
            if (preference4 != null) {
                String str4 = locatRingtoneTitle2.title;
                if (str4 != null) {
                    str = OPNotificationUtils.replaceWith(this, str4, settingForType);
                } else {
                    str = getString(C0017R$string.oneplus_no_ringtone);
                }
                preference4.setSummary(str);
            }
        }
    }

    public void startVibrate() {
        if (VibratorSceneUtils.systemVibrateEnabled(getApplicationContext())) {
            long[] vibratorScenePattern = VibratorSceneUtils.getVibratorScenePattern(getApplicationContext(), this.mVibrator, 1003);
            this.mVibratePattern = vibratorScenePattern;
            VibratorSceneUtils.vibrateIfNeeded(vibratorScenePattern, this.mVibrator);
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        OPRadioButtonPreference oPRadioButtonPreference;
        OPRadioButtonPreference oPRadioButtonPreference2;
        this.isSelectedNone = false;
        String key = preference.getKey();
        if (key.equals("setting_key")) {
            stopAnyPlayingRingtone();
            OPRingtoneManager.setRingSimSwitch(getApplicationContext(), this.mSwitch.isChecked() ? 1 : 0);
            if (this.mSwitch.isChecked()) {
                OPRingtoneManager.updateActualRingtone2(getApplicationContext());
                this.mMainRoot.removePreference(this.mLocalPreference);
                this.mMainRoot.removePreference(this.mNOPreference);
                this.mMainRoot.addPreference(this.mSim1Layout);
                this.mMainRoot.addPreference(this.mSim2Layout);
                initPreference(false);
            } else {
                OPRingtoneManager.updateActualRingtone(getApplicationContext());
                this.mMainRoot.addPreference(this.mLocalPreference);
                this.mMainRoot.addPreference(this.mNOPreference);
                this.mMainRoot.removePreference(this.mSim1Layout);
                this.mMainRoot.removePreference(this.mSim2Layout);
                initPreference(true);
            }
            startVibrate();
        } else if (key.equals("local_select")) {
            stopAnyPlayingRingtone();
            Intent intent = new Intent(this, OPLocalRingtonePickerActivity.class);
            intent.putExtra("android.intent.extra.ringtone.TYPE", this.mType);
            intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", this.mHasDefaultItem);
            intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", this.mUriForLocalItem);
            intent.putExtra("ringtone_for_contacts", this.mContactsRingtone);
            intent.putExtra("needVibrate", this.mIsAlarmNeedVibrate);
            startActivityForResult(intent, this.mRequestCode);
        } else if (key.equals("no_select")) {
            stopAnyPlayingRingtone();
            updateChecks("-1");
            if (!isThreePart()) {
                OPRingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), this.mType, null);
            }
            if (this.mHasDefaultItem && (oPRadioButtonPreference2 = this.mDefualtPreference) != null) {
                oPRadioButtonPreference2.setChecked(false);
            }
            this.mLocalPreference.setChecked(false);
            this.mNOPreference.setChecked(true);
            this.isSelectedNone = true;
            this.mLocalPreference.setSummary(C0017R$string.oneplus_no_choice);
            this.mUriForLocalItem = null;
            this.mUriForDefaultItem = null;
        } else if (key.equals("sim1_select")) {
            Intent intent2 = new Intent(this, OPSystemRingtonePicker.class);
            intent2.putExtra("oneplus.intent.extra.ringtone.simid", 1);
            intent2.putExtra("android.intent.extra.ringtone.EXISTING_URI", this.mSim1Uri);
            intent2.putExtra("android.intent.extra.ringtone.TYPE", this.mType);
            startActivity(intent2);
        } else if (key.equals("sim2_select")) {
            Intent intent3 = new Intent(this, OPSystemRingtonePicker.class);
            intent3.putExtra("oneplus.intent.extra.ringtone.simid", 2);
            intent3.putExtra("android.intent.extra.ringtone.EXISTING_URI", this.mSim2Uri);
            intent3.putExtra("android.intent.extra.ringtone.TYPE", this.mType);
            startActivity(intent3);
        } else if (key.equals("defualt_select")) {
            Uri uri = this.mDefualtUri;
            this.mUriForDefaultItem = uri;
            playRingtone(300, uri);
            updateChecks("-1");
            OPMyLog.d("", "mUriForDefaultItem:" + this.mUriForDefaultItem + " key:" + key);
            this.mLocalPreference.setChecked(false);
            this.mNOPreference.setChecked(false);
            OPRadioButtonPreference oPRadioButtonPreference3 = this.mDefualtPreference;
            if (oPRadioButtonPreference3 != null) {
                oPRadioButtonPreference3.setChecked(true);
            }
            this.mLocalPreference.setSummary(C0017R$string.oneplus_no_choice);
            this.mUriForLocalItem = null;
        } else if ("vibrate_when_ringing".equals(key)) {
            startVibrate();
            Settings.System.putInt(getApplicationContext().getContentResolver(), "vibrate_when_ringing", this.mVibrateWhenRingPreference.isChecked() ? 1 : 0);
        } else if ("vibrate_strength".equals(key)) {
            OPUtils.startFragment(this, OPRingtoneVibrateStrengthSettings.class.getName(), 9999);
        } else if ("sms_notification_vibrate_intensity".equals(key)) {
            OPUtils.startFragment(this, OPSMSNotificationVibrateIntensitySettings.class.getName(), 9999);
        } else {
            Uri parse = Uri.parse(key);
            this.mUriForDefaultItem = parse;
            playRingtone(300, parse);
            if (!isThreePart()) {
                OPRingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), this.mType, this.mUriForDefaultItem);
            }
            updateChecks(key);
            if (this.mHasDefaultItem && (oPRadioButtonPreference = this.mDefualtPreference) != null) {
                oPRadioButtonPreference.setChecked(false);
            }
            this.mLocalPreference.setChecked(false);
            this.mNOPreference.setChecked(false);
            this.mLocalPreference.setSummary(C0017R$string.oneplus_no_choice);
            this.mUriForLocalItem = null;
        }
        return true;
    }

    private void updateChecks(String str) {
        List<OPRadioButtonPreference> list = this.mSystemRings;
        if (list != null) {
            for (OPRadioButtonPreference oPRadioButtonPreference : list) {
                oPRadioButtonPreference.setChecked(str.startsWith(oPRadioButtonPreference.getKey()));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        if (this.mType == 1 && isMultiSimEnabled()) {
            registerReceiver(this.mSimStateReceiver, new IntentFilter("android.intent.action.SIM_STATE_CHANGED"));
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        if (this.mType == 1 && isMultiSimEnabled()) {
            unregisterReceiver(this.mSimStateReceiver);
        }
        super.onStop();
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.ringtone.OPRingtoneBaseActivity
    public void onResume() {
        super.onResume();
        if (this.mVibrateWhenRingPreference != null) {
            boolean z = false;
            if (Settings.System.getInt(getApplicationContext().getContentResolver(), "vibrate_when_ringing", 0) != 0) {
                z = true;
            }
            this.mVibrateWhenRingPreference.setChecked(z);
        }
        getContentResolver().registerContentObserver(Settings.System.getUriFor("vibrate_when_ringing"), true, this.mVibrateWhenRingObserver, -1);
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.ringtone.OPRingtoneBaseActivity
    public void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(this.mVibrateWhenRingObserver);
    }

    private boolean isRampingRingerEnabled() {
        return DeviceConfig.getBoolean("telephony", "ramping_ringer_enabled", false);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        Cursor cursor = this.mCursor;
        if (cursor != null) {
            cursor.close();
            this.mCursor = null;
        }
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        Uri uri;
        if (i == this.mRequestCode && intent != null && (uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI")) != null && !uri.equals(this.mUriForDefaultItem)) {
            this.mUriForDefaultItem = uri;
            updateSelected();
        }
    }
}
