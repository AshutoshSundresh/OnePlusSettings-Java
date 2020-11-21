package com.oneplus.settings.ringtone;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.os.Vibrator;
import android.preference.PreferenceActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.appcompat.widget.Toolbar;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.oneplus.settings.edgeeffect.SpringListView;
import com.oneplus.settings.edgeeffect.SpringRelativeLayout;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.OPVibrateUtils;

public abstract class OPRingtoneBaseActivity extends PreferenceActivity implements Runnable {
    private boolean isFirst = true;
    private boolean isPlaying = false;
    protected boolean isSelectedNone = false;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        /* class com.oneplus.settings.ringtone.OPRingtoneBaseActivity.AnonymousClass3 */

        public void onAudioFocusChange(int i) {
            if (i == -2 && OPRingtoneBaseActivity.this.isPlaying) {
                OPRingtoneBaseActivity.this.stopAnyPlayingRingtone();
            }
        }
    };
    private AudioManager mAudioManager;
    public boolean mContactsRingtone = false;
    private Ringtone mDefaultRingtone;
    public Uri mDefualtUri;
    public Handler mHandler = new Handler() {
        /* class com.oneplus.settings.ringtone.OPRingtoneBaseActivity.AnonymousClass1 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            int i = message.what;
            if (i != 1) {
                if (i == 2) {
                    Log.d("RingtoneBaseActivity", "OPRingtoneBaseActivity play ringtone delay");
                    if (OPRingtoneBaseActivity.this.mDefaultRingtone != null) {
                        OPRingtoneBaseActivity.this.mDefaultRingtone.stop();
                        OPRingtoneBaseActivity.this.mDefaultRingtone.play();
                    }
                }
            } else if (OPRingtoneBaseActivity.this.mDefaultRingtone == null) {
            } else {
                if (OPRingtoneBaseActivity.this.mDefaultRingtone.isPlaying()) {
                    sendEmptyMessageDelayed(1, 1000);
                    return;
                }
                OPRingtoneBaseActivity.this.stopVibrate();
                Log.d("RingtoneBaseActivity", "Ringtone play stoped, stop vibrate");
            }
        }
    };
    public boolean mHasDefaultItem;
    public boolean mIsAlarmNeedVibrate = false;
    private final Runnable mLookupRingtoneNames = new Runnable() {
        /* class com.oneplus.settings.ringtone.OPRingtoneBaseActivity.AnonymousClass4 */

        public void run() {
            if (OPRingtoneBaseActivity.this.mSimid == 1) {
                OPRingtoneBaseActivity oPRingtoneBaseActivity = OPRingtoneBaseActivity.this;
                oPRingtoneBaseActivity.mUriForDefaultItem = OPRingtoneManager.getActualRingtoneUriBySubId(oPRingtoneBaseActivity.getApplicationContext(), 0);
            } else if (OPRingtoneBaseActivity.this.mSimid == 2) {
                OPRingtoneBaseActivity oPRingtoneBaseActivity2 = OPRingtoneBaseActivity.this;
                oPRingtoneBaseActivity2.mUriForDefaultItem = OPRingtoneManager.getActualRingtoneUriBySubId(oPRingtoneBaseActivity2.getApplicationContext(), 1);
            } else {
                OPRingtoneBaseActivity oPRingtoneBaseActivity3 = OPRingtoneBaseActivity.this;
                oPRingtoneBaseActivity3.mUriForDefaultItem = OPRingtoneManager.getActualDefaultRingtoneUri(oPRingtoneBaseActivity3.getUserContext(), OPRingtoneBaseActivity.this.mType);
            }
            OPRingtoneBaseActivity.this.mHandler.post(new Runnable() {
                /* class com.oneplus.settings.ringtone.OPRingtoneBaseActivity.AnonymousClass4.AnonymousClass1 */

                public void run() {
                    OPRingtoneBaseActivity.this.updateSelected();
                }
            });
        }
    };
    private PhoneCallListener mPhoneCallListener;
    public OPRingtoneManager mRingtoneManager;
    private int mSimid = 0;
    private TelephonyManager mTelephonyManager;
    public int mType = 1;
    public Uri mUriForDefaultItem;
    private Context mUserContext;
    private Vibrator mVibrator;

    /* access modifiers changed from: protected */
    public abstract void updateSelected();

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        Uri uri = this.mUriForDefaultItem;
        if (uri != null) {
            bundle.putString("key_selected_item_uri", uri.toString());
        }
        super.onSaveInstanceState(bundle);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle bundle) {
        String string = bundle.getString("key_selected_item_uri");
        if (string != null) {
            this.mUriForDefaultItem = Uri.parse(string);
        }
        super.onRestoreInstanceState(bundle);
    }

    public Context getUserContext() {
        return this.mUserContext;
    }

    public boolean isProfileId() {
        try {
            return UserManager.get(getApplicationContext()).isManagedProfile(this.mUserContext.getUserId());
        } catch (Exception e) {
            Log.e("RingtoneBaseActivity", "isManagedProfile :" + e.getMessage());
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        String string;
        super.onCreate(bundle);
        OPUtils.setLightNavigationBar(getWindow(), OPUtils.getThemeMode(getContentResolver()));
        setContentView(C0012R$layout.op_ringtone_activity_parent);
        this.mVibrator = (Vibrator) getSystemService("vibrator");
        Intent intent = getIntent();
        Context createPackageContextAsUser = Utils.createPackageContextAsUser(getApplicationContext(), intent.getIntExtra("CURRENT_USER_ID", 0));
        this.mUserContext = createPackageContextAsUser;
        if (createPackageContextAsUser == null) {
            Log.w("RingtoneBaseActivity", "use application context instead");
            this.mUserContext = getApplicationContext();
        }
        this.mType = intent.getIntExtra("android.intent.extra.ringtone.TYPE", 1);
        this.mIsAlarmNeedVibrate = intent.getBooleanExtra("needVibrate", false);
        this.mHasDefaultItem = intent.getBooleanExtra("android.intent.extra.ringtone.SHOW_DEFAULT", false);
        this.mContactsRingtone = intent.getBooleanExtra("ringtone_for_contacts", false);
        this.mDefualtUri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.DEFAULT_URI");
        if (this.mUriForDefaultItem == null) {
            if (!(bundle == null || (string = bundle.getString("key_selected_item_uri")) == null)) {
                this.mUriForDefaultItem = Uri.parse(string);
            }
            if (this.mUriForDefaultItem == null && !this.isSelectedNone) {
                this.mUriForDefaultItem = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.EXISTING_URI");
            }
        }
        Log.d("RingtoneBaseActivity", "mDefualtUri:" + this.mDefualtUri);
        Log.d("RingtoneBaseActivity", "mUriForDefaultItem:" + this.mUriForDefaultItem);
        Log.d("RingtoneBaseActivity", "mHasDefaultItem:" + this.mHasDefaultItem);
        CharSequence charSequenceExtra = intent.getCharSequenceExtra("android.intent.extra.ringtone.TITLE");
        int intExtra = intent.getIntExtra("oneplus.intent.extra.ringtone.simid", 0);
        this.mSimid = intExtra;
        if (charSequenceExtra == null) {
            if (intExtra == 1) {
                charSequenceExtra = getString(C0017R$string.oneplus_sim1_ringtone_switch);
            } else if (intExtra == 2) {
                charSequenceExtra = getString(C0017R$string.oneplus_sim2_ringtone_switch);
            } else {
                charSequenceExtra = getString(17041175);
            }
        }
        OPRingtoneManager oPRingtoneManager = new OPRingtoneManager(this);
        this.mRingtoneManager = oPRingtoneManager;
        oPRingtoneManager.setType(this.mType);
        setVolumeControlStream(this.mRingtoneManager.inferStreamType());
        Toolbar toolbar = (Toolbar) findViewById(C0010R$id.action_bar);
        toolbar.setTitle(charSequenceExtra);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.ringtone.OPRingtoneBaseActivity.AnonymousClass2 */

            public void onClick(View view) {
                OPRingtoneBaseActivity.this.onBackPressed();
            }
        });
        this.mTelephonyManager = (TelephonyManager) getSystemService("phone");
        this.mAudioManager = (AudioManager) getSystemService("audio");
        this.mPhoneCallListener = new PhoneCallListener();
        enableSpringEdgeEffect();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        onBackPressed();
        return true;
    }

    /* access modifiers changed from: protected */
    public void playRingtone(int i, Uri uri) {
        this.mHandler.removeCallbacks(this);
        this.mUriForDefaultItem = uri;
        this.mHandler.postDelayed(this, (long) i);
    }

    public void run() {
        stopAnyPlayingRingtone2();
        OPMyLog.d("RingtoneBaseActivity", "mUriForDefaultItem:" + this.mUriForDefaultItem);
        Uri uri = this.mUriForDefaultItem;
        if (uri != null) {
            try {
                Ringtone ringtone = OPRingtoneManager.getRingtone(this, uri);
                this.mDefaultRingtone = ringtone;
                ringtone.setStreamType(this.mRingtoneManager.inferStreamType());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (this.mDefaultRingtone != null) {
                if (!this.isPlaying) {
                    this.isPlaying = true;
                    this.mAudioManager.requestAudioFocus(this.mAudioFocusChangeListener, this.mRingtoneManager.inferStreamType(), 2);
                }
                startPreview();
            }
        }
    }

    private void startPreview() {
        startVibrate(this.mUriForDefaultItem);
        this.mHandler.removeMessages(2);
        this.mHandler.sendEmptyMessageDelayed(2, 200);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.mHandler.removeCallbacks(this);
        this.mHandler.removeMessages(2);
        stopAnyPlayingRingtone();
        this.mTelephonyManager.listen(this.mPhoneCallListener, 0);
        this.mAudioManager.abandonAudioFocus(this.mAudioFocusChangeListener);
    }

    /* access modifiers changed from: protected */
    public void startVibrate(Uri uri) {
        Vibrator vibrator = this.mVibrator;
        if (vibrator != null) {
            vibrator.cancel();
        }
        int i = this.mType;
        if (i == 1) {
            OPVibrateUtils.startVibrateForRingtone(this.mUserContext, uri, this.mVibrator);
        } else if (i == 2) {
            OPVibrateUtils.startVibrateForNotification(this.mUserContext, uri, this.mVibrator);
        } else if (i != 4) {
            if (i == 8) {
                OPVibrateUtils.startVibrateForSms(this.mUserContext, uri, this.mVibrator);
            }
        } else if (this.mIsAlarmNeedVibrate) {
            OPVibrateUtils.startVibrateForAlarm(this.mUserContext, uri, this.mVibrator);
        }
        if (OPVibrateUtils.isThreeKeyRingMode(getApplicationContext())) {
            this.mHandler.removeMessages(1);
            this.mHandler.sendEmptyMessageDelayed(1, 500);
        }
    }

    /* access modifiers changed from: protected */
    public void stopVibrate() {
        Vibrator vibrator;
        if (OPUtils.isSupportXVibrate() && (vibrator = this.mVibrator) != null) {
            vibrator.cancel();
        }
    }

    /* access modifiers changed from: protected */
    public void stopAnyPlayingRingtone() {
        stopAnyPlayingRingtone2();
        stopVibrate();
        this.isPlaying = false;
        this.mAudioManager.abandonAudioFocus(null);
    }

    private void stopAnyPlayingRingtone2() {
        String name = OPRingtoneBaseActivity.class.getName();
        Log.v(name, "stopAnyPlayingRingtone2 mDefaultRingtone = " + this.mDefaultRingtone);
        Ringtone ringtone = this.mDefaultRingtone;
        if (ringtone != null) {
            ringtone.stop();
            this.mDefaultRingtone = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (!this.isFirst) {
            lookupRingtoneNames();
        }
        this.isFirst = false;
        this.mTelephonyManager.listen(this.mPhoneCallListener, 32);
        getListView().setDivider(null);
    }

    private void lookupRingtoneNames() {
        if (!isThreePart() && !this.mContactsRingtone) {
            AsyncTask.execute(this.mLookupRingtoneNames);
        }
    }

    /* access modifiers changed from: package-private */
    public class PhoneCallListener extends PhoneStateListener {
        PhoneCallListener() {
        }

        public void onCallStateChanged(int i, String str) {
            super.onCallStateChanged(i, str);
            if (i == 1) {
                Log.d("RingtoneBaseActivity", "PhoneCallListener-CALL_STATE_RINGING--stopAnyPlayingRingtone");
                OPRingtoneBaseActivity.this.stopAnyPlayingRingtone();
            }
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("android.intent.extra.ringtone.PICKED_URI", this.mUriForDefaultItem);
        setResult(-1, intent);
        super.onBackPressed();
    }

    public boolean isThreePart() {
        OPMyLog.d("", "mHasDefaultItem:" + this.mHasDefaultItem + " mType:" + this.mType);
        return this.mType == 4 || this.mHasDefaultItem;
    }

    public boolean isMultiSimEnabled() {
        return this.mTelephonyManager.isMultiSimEnabled();
    }

    public int getSimId() {
        return this.mSimid;
    }

    public boolean getSim1Enable() {
        return this.mTelephonyManager.hasIccCard(0);
    }

    public boolean getSim2Enable() {
        return this.mTelephonyManager.hasIccCard(1);
    }

    /* access modifiers changed from: package-private */
    public void enableSpringEdgeEffect() {
        ViewParent parent;
        View listView = getListView();
        if (listView != null && (parent = listView.getParent()) != null) {
            ViewGroup viewGroup = (ViewGroup) parent;
            viewGroup.removeView(listView);
            SpringRelativeLayout springRelativeLayout = new SpringRelativeLayout(this);
            springRelativeLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            springRelativeLayout.setFocusable(true);
            springRelativeLayout.setFocusableInTouchMode(true);
            springRelativeLayout.setSaveEnabled(false);
            SpringListView springListView = (SpringListView) LayoutInflater.from(this).inflate(C0012R$layout.spring_preference_listview, (ViewGroup) null, false);
            springRelativeLayout.addView(springListView);
            springRelativeLayout.addSpringView(16908298);
            springListView.setEdgeEffectFactory(springRelativeLayout.createViewEdgeEffectFactory());
            viewGroup.addView(springRelativeLayout);
            ((PreferenceActivity) this).mList = springListView;
        }
    }
}
