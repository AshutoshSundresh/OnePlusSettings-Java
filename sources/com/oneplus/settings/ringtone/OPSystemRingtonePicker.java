package com.oneplus.settings.ringtone;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.oneplus.settings.ringtone.OPRingtoneManager;
import com.oneplus.settings.utils.OPNotificationUtils;
import java.util.ArrayList;
import java.util.List;

public class OPSystemRingtonePicker extends OPRingtoneBaseActivity implements Preference.OnPreferenceClickListener {
    private Cursor mCursor;
    private OPRadioButtonPreference mDefualtPreference;
    private OPRadioButtonPreference mLocalPreference;
    private OPRadioButtonPreference mNOPreference;
    private PreferenceCategory mSettingTitleCategory;
    private List<OPRadioButtonPreference> mSystemRings = null;
    private Uri mUriForLocalItem;

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.ringtone.OPRingtoneBaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_ring_system_fragment);
        this.mCursor = this.mRingtoneManager.getCursor();
        getPreferenceScreen().removePreference(findPreference("ringtone_vibrate"));
        getPreferenceScreen().removePreference(findPreference("vibrate"));
        this.mSettingTitleCategory = (PreferenceCategory) findPreference("setting_title");
        this.mLocalPreference = (OPRadioButtonPreference) findPreference("local_select");
        this.mNOPreference = (OPRadioButtonPreference) findPreference("no_select");
        OPRadioButtonPreference oPRadioButtonPreference = (OPRadioButtonPreference) findPreference("defualt_select");
        this.mDefualtPreference = oPRadioButtonPreference;
        this.mSettingTitleCategory.removePreference(oPRadioButtonPreference);
        this.mLocalPreference.setOnPreferenceClickListener(this);
        this.mNOPreference.setOnPreferenceClickListener(this);
        initPreference();
        updateSelected();
    }

    private void initPreference() {
        if (this.mSystemRings == null) {
            this.mSystemRings = new ArrayList();
            Cursor cursor = this.mCursor;
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    OPRadioButtonPreference oPRadioButtonPreference = new OPRadioButtonPreference(this);
                    oPRadioButtonPreference.setTitle(OPNotificationUtils.replaceWith(this, this.mCursor.getString(1), OPRingtoneManager.getSettingForType(this.mType)));
                    oPRadioButtonPreference.setKey(OPRingtoneManager.getUriFromCursor(this.mCursor).toString());
                    oPRadioButtonPreference.setOnPreferenceClickListener(this);
                    this.mSystemRings.add(oPRadioButtonPreference);
                    getPreferenceScreen().addPreference(oPRadioButtonPreference);
                    oPRadioButtonPreference.setChecked(false);
                } while (this.mCursor.moveToNext());
            }
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.oneplus.settings.ringtone.OPRingtoneBaseActivity
    public void updateSelected() {
        if (this.mUriForDefaultItem == null) {
            this.mLocalPreference.setChecked(false);
            this.mNOPreference.setChecked(true);
            updateChecks("-1");
            this.mLocalPreference.setSummary(C0017R$string.oneplus_no_choice);
            this.mUriForLocalItem = null;
            return;
        }
        this.mNOPreference.setChecked(false);
        if (OPRingtoneManager.isSystemRingtone(getApplicationContext(), this.mUriForDefaultItem, this.mType)) {
            this.mLocalPreference.setChecked(false);
            updateChecks(this.mUriForDefaultItem.toString());
            this.mLocalPreference.setSummary(C0017R$string.oneplus_no_choice);
            this.mUriForLocalItem = null;
            return;
        }
        this.mLocalPreference.setChecked(true);
        OPRingtoneManager.ResultRing locatRingtoneTitle = OPRingtoneManager.getLocatRingtoneTitle(getApplicationContext(), this.mUriForDefaultItem, this.mType, getSimId());
        OPRadioButtonPreference oPRadioButtonPreference = this.mLocalPreference;
        String str = locatRingtoneTitle.title;
        if (str == null) {
            str = getString(C0017R$string.oneplus_no_choice);
        }
        oPRadioButtonPreference.setSummary(str);
        Uri uri = locatRingtoneTitle.ringUri;
        this.mUriForDefaultItem = uri;
        this.mUriForLocalItem = uri;
        updateChecks("-1");
    }

    private void updateChecks(String str) {
        List<OPRadioButtonPreference> list = this.mSystemRings;
        if (list != null) {
            for (OPRadioButtonPreference oPRadioButtonPreference : list) {
                oPRadioButtonPreference.setChecked(oPRadioButtonPreference.getKey().equals(str));
            }
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals("local_select")) {
            stopAnyPlayingRingtone();
            Intent intent = new Intent(this, OPLocalRingtonePickerActivity.class);
            intent.putExtra("android.intent.extra.ringtone.TYPE", this.mType);
            intent.putExtra("oneplus.intent.extra.ringtone.simid", getSimId());
            intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
            intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", this.mUriForLocalItem);
            startActivity(intent);
        } else if (key.equals("no_select")) {
            stopAnyPlayingRingtone();
            updateChecks("-1");
            if (getSimId() == 2) {
                OPRingtoneManager.setActualRingtoneUriBySubId(getApplicationContext(), 1, null);
            } else {
                OPRingtoneManager.setActualRingtoneUriBySubId(getApplicationContext(), 0, null);
            }
            this.mLocalPreference.setChecked(false);
            this.mNOPreference.setChecked(true);
            this.mLocalPreference.setSummary(C0017R$string.oneplus_no_choice);
            this.mUriForLocalItem = null;
            this.mUriForDefaultItem = null;
        } else {
            Uri parse = Uri.parse(key);
            this.mUriForDefaultItem = parse;
            playRingtone(300, parse);
            if (getSimId() == 2) {
                OPRingtoneManager.setActualRingtoneUriBySubId(getApplicationContext(), 1, this.mUriForDefaultItem);
            } else {
                OPRingtoneManager.setActualRingtoneUriBySubId(getApplicationContext(), 0, this.mUriForDefaultItem);
            }
            updateChecks(key);
            this.mLocalPreference.setChecked(false);
            this.mNOPreference.setChecked(false);
            this.mLocalPreference.setSummary(C0017R$string.oneplus_no_choice);
            this.mUriForLocalItem = null;
        }
        return true;
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
}
