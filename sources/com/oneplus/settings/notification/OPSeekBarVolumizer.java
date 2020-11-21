package com.oneplus.settings.notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.audiopolicy.AudioProductStrategy;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.SeekBar;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.SomeArgs;
import com.oneplus.settings.utils.OPUtils;

@Deprecated
public class OPSeekBarVolumizer implements SeekBar.OnSeekBarChangeListener, Handler.Callback {
    private boolean mAffectedByRingerMode;
    private boolean mAllowAlarms;
    private boolean mAllowMedia;
    private boolean mAllowRinger;
    private final AudioManager mAudioManager;
    private final Callback mCallback;
    private final Context mContext;
    private final Uri mDefaultUri;
    private Handler mHandler;
    private int mLastAudibleStreamVolume;
    private int mLastProgress;
    private final int mMaxStreamVolume;
    private boolean mMuted;
    private final NotificationManager mNotificationManager;
    private boolean mNotificationOrRing;
    private NotificationManager.Policy mNotificationPolicy;
    private int mOriginalStreamVolume;
    private boolean mPlaySample;
    private final Receiver mReceiver;
    private int mRingerMode;
    @GuardedBy({"this"})
    private Ringtone mRingtone;
    private SeekBar mSeekBar;
    private final int mStreamType;
    private final H mUiHandler;
    private final AudioManager.VolumeGroupCallback mVolumeGroupCallback;
    private int mVolumeGroupId;
    private final Handler mVolumeHandler;
    private Observer mVolumeObserver;
    private int mZenMode;

    public interface Callback {
        void onMuted(boolean z, boolean z2);

        void onProgressChanged(SeekBar seekBar, int i, boolean z);

        void onSampleStarting(OPSeekBarVolumizer oPSeekBarVolumizer);
    }

    private static boolean isAlarmsStream(int i) {
        return i == 4;
    }

    private static boolean isMediaStream(int i) {
        return i == 3;
    }

    /* access modifiers changed from: private */
    public static boolean isNotificationOrRing(int i) {
        return i == 2 || i == 5;
    }

    public boolean isZenModeEnabled(int i) {
        return i == 1 || i == 2 || i == 3;
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public OPSeekBarVolumizer(Context context, int i, Uri uri, Callback callback) {
        this(context, i, uri, callback, true);
    }

    public OPSeekBarVolumizer(Context context, int i, Uri uri, Callback callback, boolean z) {
        this.mVolumeHandler = new VolumeHandler();
        this.mVolumeGroupCallback = new AudioManager.VolumeGroupCallback() {
            /* class com.oneplus.settings.notification.OPSeekBarVolumizer.AnonymousClass1 */

            public void onAudioVolumeGroupChanged(int i, int i2) {
                if (OPSeekBarVolumizer.this.mHandler != null) {
                    SomeArgs obtain = SomeArgs.obtain();
                    obtain.arg1 = Integer.valueOf(i);
                    obtain.arg2 = Integer.valueOf(i2);
                    OPSeekBarVolumizer.this.mVolumeHandler.sendMessage(OPSeekBarVolumizer.this.mHandler.obtainMessage(1, obtain));
                }
            }
        };
        this.mUiHandler = new H();
        this.mReceiver = new Receiver();
        this.mLastProgress = -1;
        this.mContext = context;
        this.mAudioManager = (AudioManager) context.getSystemService(AudioManager.class);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
        this.mNotificationManager = notificationManager;
        NotificationManager.Policy consolidatedNotificationPolicy = notificationManager.getConsolidatedNotificationPolicy();
        this.mNotificationPolicy = consolidatedNotificationPolicy;
        boolean z2 = true;
        this.mAllowAlarms = (consolidatedNotificationPolicy.priorityCategories & 32) != 0;
        this.mAllowMedia = (this.mNotificationPolicy.priorityCategories & 64) == 0 ? false : z2;
        this.mStreamType = i;
        this.mAffectedByRingerMode = this.mAudioManager.isStreamAffectedByRingerMode(i);
        boolean isNotificationOrRing = isNotificationOrRing(this.mStreamType);
        this.mNotificationOrRing = isNotificationOrRing;
        if (isNotificationOrRing) {
            this.mRingerMode = this.mAudioManager.getRingerModeInternal();
        }
        this.mZenMode = this.mNotificationManager.getZenMode();
        if (hasAudioProductStrategies()) {
            this.mVolumeGroupId = getVolumeGroupIdForLegacyStreamType(this.mStreamType);
            getAudioAttributesForLegacyStreamType(this.mStreamType);
        }
        this.mMaxStreamVolume = this.mAudioManager.getStreamMaxVolume(this.mStreamType);
        this.mCallback = callback;
        this.mOriginalStreamVolume = this.mAudioManager.getStreamVolume(this.mStreamType);
        this.mLastAudibleStreamVolume = this.mAudioManager.getLastAudibleStreamVolume(this.mStreamType);
        boolean isStreamMute = this.mAudioManager.isStreamMute(this.mStreamType);
        this.mMuted = isStreamMute;
        this.mPlaySample = z;
        Callback callback2 = this.mCallback;
        if (callback2 != null) {
            callback2.onMuted(isStreamMute, isZenMuted());
        }
        if (uri == null) {
            int i2 = this.mStreamType;
            if (i2 == 2) {
                uri = Settings.System.DEFAULT_RINGTONE_URI;
            } else if (i2 == 5) {
                uri = Settings.System.DEFAULT_NOTIFICATION_URI;
            } else {
                uri = Settings.System.DEFAULT_ALARM_ALERT_URI;
            }
        }
        this.mDefaultUri = uri;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean hasAudioProductStrategies() {
        return AudioManager.getAudioProductStrategies().size() > 0;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int getVolumeGroupIdForLegacyStreamType(int i) {
        for (AudioProductStrategy audioProductStrategy : AudioManager.getAudioProductStrategies()) {
            int volumeGroupIdForLegacyStreamType = audioProductStrategy.getVolumeGroupIdForLegacyStreamType(i);
            if (volumeGroupIdForLegacyStreamType != -1) {
                return volumeGroupIdForLegacyStreamType;
            }
        }
        return ((Integer) AudioManager.getAudioProductStrategies().stream().map($$Lambda$OPSeekBarVolumizer$BaHlvY9YWvugHrdWUFgxL5eDgo.INSTANCE).filter($$Lambda$OPSeekBarVolumizer$uYYnp9GRgGkdFxwtIEYpjFWJJ3U.INSTANCE).findFirst().orElse(-1)).intValue();
    }

    static /* synthetic */ boolean lambda$getVolumeGroupIdForLegacyStreamType$1(Integer num) {
        return num.intValue() != -1;
    }

    private AudioAttributes getAudioAttributesForLegacyStreamType(int i) {
        for (AudioProductStrategy audioProductStrategy : AudioManager.getAudioProductStrategies()) {
            AudioAttributes audioAttributesForLegacyStreamType = audioProductStrategy.getAudioAttributesForLegacyStreamType(i);
            if (audioAttributesForLegacyStreamType != null) {
                return audioAttributesForLegacyStreamType;
            }
        }
        return new AudioAttributes.Builder().setContentType(0).setUsage(0).build();
    }

    public void setSeekBar(SeekBar seekBar) {
        SeekBar seekBar2 = this.mSeekBar;
        if (seekBar2 != null) {
            seekBar2.setOnSeekBarChangeListener(null);
        }
        this.mSeekBar = seekBar;
        seekBar.setOnSeekBarChangeListener(null);
        this.mSeekBar.setMax(this.mMaxStreamVolume);
        updateSeekBar();
        this.mSeekBar.setOnSeekBarChangeListener(this);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isZenMuted() {
        return !OPUtils.isSupportSocTriState() ? getThreeKeyStatus(this.mContext) == 1 : getThreeKeyStatus(this.mContext) == 1 || getThreeKeyStatus(this.mContext) == 2;
    }

    public int getThreeKeyStatus(Context context) {
        if (context == null) {
            Log.e("OPSeekBarVolumizer", "getThreeKeyStatus error, context is null");
            return 0;
        }
        try {
            return Settings.Global.getInt(context.getContentResolver(), "three_Key_mode");
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /* access modifiers changed from: protected */
    public void updateSeekBar() {
        boolean isZenMuted = isZenMuted();
        if (this.mNotificationOrRing) {
            this.mSeekBar.setEnabled(!isZenMuted);
        }
        if (isAlarmsStream(this.mStreamType)) {
            if (!isZenModeEnabled(this.mZenMode) || this.mAllowAlarms) {
                this.mSeekBar.setEnabled(true);
            } else {
                this.mSeekBar.setEnabled(false);
            }
        }
        if (isMediaStream(this.mStreamType)) {
            if (!isZenModeEnabled(this.mZenMode) || this.mAllowMedia) {
                this.mSeekBar.setEnabled(true);
            } else {
                this.mSeekBar.setEnabled(false);
            }
        }
        if (this.mNotificationOrRing && this.mRingerMode == 1) {
            this.mSeekBar.setProgress(0, true);
        } else if (this.mMuted) {
            this.mSeekBar.setProgress(0, true);
        } else {
            SeekBar seekBar = this.mSeekBar;
            int i = this.mLastProgress;
            if (i <= -1) {
                i = this.mOriginalStreamVolume;
            }
            seekBar.setProgress(i, true);
        }
    }

    public boolean handleMessage(Message message) {
        int i = message.what;
        if (i == 0) {
            if (this.mMuted && this.mLastProgress > 0) {
                this.mAudioManager.adjustStreamVolume(this.mStreamType, 100, 0);
            } else if (!this.mMuted && this.mLastProgress == 0) {
                this.mAudioManager.adjustStreamVolume(this.mStreamType, -100, 0);
            }
            Log.d("OPSeekBarVolumizer", "MSG_SET_STREAM_VOLUME setStreamVolume mStreamType : " + this.mStreamType + " mLastProgress : " + this.mLastProgress);
            this.mAudioManager.setStreamVolume(this.mStreamType, this.mLastProgress, 1024);
        } else if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    Log.e("OPSeekBarVolumizer", "invalid SeekBarVolumizer message: " + message.what);
                } else if (this.mPlaySample) {
                    onInitSample();
                }
            } else if (this.mPlaySample) {
                onStopSample();
            }
        } else if (this.mPlaySample) {
            onStartSample();
        }
        return true;
    }

    private void onInitSample() {
        synchronized (this) {
            Ringtone ringtone = RingtoneManager.getRingtone(this.mContext, this.mDefaultUri);
            this.mRingtone = ringtone;
            if (ringtone != null) {
                ringtone.setStreamType(this.mStreamType);
            }
        }
    }

    private void postStartSample() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeMessages(1);
            Handler handler2 = this.mHandler;
            handler2.sendMessageDelayed(handler2.obtainMessage(1), isSamplePlaying() ? 1000 : 0);
        }
    }

    private void onStartSample() {
        if (!isSamplePlaying()) {
            Callback callback = this.mCallback;
            if (callback != null) {
                callback.onSampleStarting(this);
            }
            synchronized (this) {
                if (this.mRingtone != null) {
                    try {
                        this.mRingtone.setAudioAttributes(new AudioAttributes.Builder(this.mRingtone.getAudioAttributes()).setFlags(128).build());
                        this.mRingtone.play();
                    } catch (Throwable th) {
                        Log.w("OPSeekBarVolumizer", "Error playing ringtone, stream " + this.mStreamType, th);
                    }
                }
            }
        }
    }

    private void postStopSample() {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeMessages(1);
            this.mHandler.removeMessages(2);
            Handler handler2 = this.mHandler;
            handler2.sendMessage(handler2.obtainMessage(2));
        }
    }

    private void onStopSample() {
        synchronized (this) {
            if (this.mRingtone != null) {
                this.mRingtone.stop();
            }
        }
    }

    public void stop() {
        if (this.mHandler != null) {
            postStopSample();
            this.mContext.getContentResolver().unregisterContentObserver(this.mVolumeObserver);
            this.mReceiver.setListening(false);
            if (hasAudioProductStrategies()) {
                unregisterVolumeGroupCb();
            }
            this.mSeekBar.setOnSeekBarChangeListener(null);
            this.mHandler.getLooper().quitSafely();
            this.mHandler = null;
            this.mVolumeObserver = null;
        }
    }

    public void start() {
        if (this.mHandler == null) {
            HandlerThread handlerThread = new HandlerThread("OPSeekBarVolumizer.CallbackHandler");
            handlerThread.start();
            Handler handler = new Handler(handlerThread.getLooper(), this);
            this.mHandler = handler;
            handler.sendEmptyMessage(3);
            this.mVolumeObserver = new Observer(this.mHandler);
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.VOLUME_SETTINGS_INT[this.mStreamType]), false, this.mVolumeObserver);
            this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("three_Key_mode"), false, this.mVolumeObserver);
            this.mReceiver.setListening(true);
            if (hasAudioProductStrategies()) {
                registerVolumeGroupCb();
            }
        }
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (!this.mMuted && this.mNotificationOrRing && i < 1) {
            seekBar.setProgress(1);
            i = 1;
        }
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onProgressChanged(seekBar, i, z);
        }
    }

    private void postSetVolume(int i) {
        Handler handler = this.mHandler;
        if (handler != null) {
            this.mLastProgress = i;
            handler.removeMessages(0);
            Handler handler2 = this.mHandler;
            handler2.sendMessage(handler2.obtainMessage(0));
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        postSetVolume(seekBar.getProgress());
        postStartSample();
    }

    public boolean isSamplePlaying() {
        boolean z;
        synchronized (this) {
            z = this.mRingtone != null && this.mRingtone.isPlaying();
        }
        return z;
    }

    public void stopSample() {
        postStopSample();
    }

    /* access modifiers changed from: private */
    public final class H extends Handler {
        private H() {
        }

        public void handleMessage(Message message) {
            if (message.what == 1 && OPSeekBarVolumizer.this.mSeekBar != null) {
                OPSeekBarVolumizer.this.mLastProgress = message.arg1;
                OPSeekBarVolumizer.this.mLastAudibleStreamVolume = message.arg2;
                boolean booleanValue = ((Boolean) message.obj).booleanValue();
                if (booleanValue != OPSeekBarVolumizer.this.mMuted) {
                    OPSeekBarVolumizer.this.mMuted = booleanValue;
                    if (OPSeekBarVolumizer.this.mCallback != null) {
                        OPSeekBarVolumizer.this.mCallback.onMuted(OPSeekBarVolumizer.this.mMuted, OPSeekBarVolumizer.this.isZenMuted());
                    }
                }
                OPSeekBarVolumizer.this.updateSeekBar();
            }
        }

        public void postUpdateSlider(int i, int i2, boolean z) {
            obtainMessage(1, i, i2, new Boolean(z)).sendToTarget();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateSlider() {
        AudioManager audioManager;
        if (this.mSeekBar != null && (audioManager = this.mAudioManager) != null) {
            this.mUiHandler.postUpdateSlider(audioManager.getStreamVolume(this.mStreamType), this.mAudioManager.getLastAudibleStreamVolume(this.mStreamType), this.mAudioManager.isStreamMute(this.mStreamType));
        }
    }

    /* access modifiers changed from: private */
    public final class Observer extends ContentObserver {
        public Observer(Handler handler) {
            super(handler);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            OPSeekBarVolumizer.this.updateSlider();
        }
    }

    /* access modifiers changed from: private */
    public final class Receiver extends BroadcastReceiver {
        private boolean mListening;

        private Receiver() {
        }

        public void setListening(boolean z) {
            if (this.mListening != z) {
                this.mListening = z;
                if (z) {
                    IntentFilter intentFilter = new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
                    intentFilter.addAction("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
                    intentFilter.addAction("android.intent.action.HEADSET_PLUG");
                    intentFilter.addAction("android.app.action.INTERRUPTION_FILTER_CHANGED");
                    intentFilter.addAction("android.app.action.NOTIFICATION_POLICY_CHANGED");
                    intentFilter.addAction("android.media.STREAM_DEVICES_CHANGED_ACTION");
                    OPSeekBarVolumizer.this.mContext.registerReceiver(this, intentFilter);
                    return;
                }
                OPSeekBarVolumizer.this.mContext.unregisterReceiver(this);
            }
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.media.VOLUME_CHANGED_ACTION".equals(action)) {
                int intExtra = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
                int intExtra2 = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1);
                if ((OPSeekBarVolumizer.this.mStreamType != 3 || !OPSeekBarVolumizer.this.mAudioManager.isWiredHeadsetOn() || OPSeekBarVolumizer.this.mAudioManager.isMusicActive()) && OPSeekBarVolumizer.this.hasAudioProductStrategies()) {
                    updateVolumeSlider(intExtra, intExtra2);
                }
            } else if ("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION".equals(action)) {
                if (OPSeekBarVolumizer.this.mNotificationOrRing) {
                    OPSeekBarVolumizer oPSeekBarVolumizer = OPSeekBarVolumizer.this;
                    oPSeekBarVolumizer.mRingerMode = oPSeekBarVolumizer.mAudioManager.getRingerModeInternal();
                }
                if (OPSeekBarVolumizer.this.mAffectedByRingerMode && !OPSeekBarVolumizer.this.mAudioManager.isWiredHeadsetOn() && !OPSeekBarVolumizer.this.mAudioManager.isMusicActive()) {
                    OPSeekBarVolumizer.this.updateSlider();
                }
            } else if ("android.media.STREAM_DEVICES_CHANGED_ACTION".equals(action)) {
                int intExtra3 = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
                if (((OPSeekBarVolumizer.this.mStreamType != 4 && OPSeekBarVolumizer.this.mStreamType != 3) || !OPSeekBarVolumizer.this.mAudioManager.isWiredHeadsetOn() || OPSeekBarVolumizer.this.mAudioManager.isMusicActive()) && !OPSeekBarVolumizer.isNotificationOrRing(intExtra3)) {
                    if (OPSeekBarVolumizer.this.hasAudioProductStrategies()) {
                        updateVolumeSlider(intExtra3, OPSeekBarVolumizer.this.mAudioManager.getStreamVolume(intExtra3));
                        return;
                    }
                    int volumeGroupIdForLegacyStreamType = OPSeekBarVolumizer.this.getVolumeGroupIdForLegacyStreamType(intExtra3);
                    if (volumeGroupIdForLegacyStreamType != -1 && volumeGroupIdForLegacyStreamType == OPSeekBarVolumizer.this.mVolumeGroupId) {
                        updateVolumeSlider(intExtra3, OPSeekBarVolumizer.this.mAudioManager.getStreamVolume(intExtra3));
                    }
                }
            } else if ("android.app.action.INTERRUPTION_FILTER_CHANGED".equals(action)) {
                OPSeekBarVolumizer oPSeekBarVolumizer2 = OPSeekBarVolumizer.this;
                oPSeekBarVolumizer2.mZenMode = oPSeekBarVolumizer2.mNotificationManager.getZenMode();
                OPSeekBarVolumizer.this.updateSlider();
            } else if ("android.intent.action.HEADSET_PLUG".equals(action)) {
                if (OPSeekBarVolumizer.this.mSeekBar != null && OPSeekBarVolumizer.this.mAudioManager != null) {
                    Log.w("OPSeekBarVolumizer", "VOLUME_CHANGED_ACTION mStreamType : " + OPSeekBarVolumizer.this.mStreamType);
                    if (OPSeekBarVolumizer.this.mStreamType == 3) {
                        int streamVolume = OPSeekBarVolumizer.this.mAudioManager.getStreamVolume(OPSeekBarVolumizer.this.mStreamType);
                        OPSeekBarVolumizer.this.mAudioManager.isStreamMute(OPSeekBarVolumizer.this.mStreamType);
                        Log.w("OPSeekBarVolumizer", "volume = " + streamVolume);
                        OPSeekBarVolumizer.this.mUiHandler.postUpdateSlider(streamVolume, OPSeekBarVolumizer.this.mLastAudibleStreamVolume, false);
                    }
                }
            } else if ("android.app.action.NOTIFICATION_POLICY_CHANGED".equals(action)) {
                OPSeekBarVolumizer oPSeekBarVolumizer3 = OPSeekBarVolumizer.this;
                oPSeekBarVolumizer3.mNotificationPolicy = oPSeekBarVolumizer3.mNotificationManager.getConsolidatedNotificationPolicy();
                OPSeekBarVolumizer oPSeekBarVolumizer4 = OPSeekBarVolumizer.this;
                boolean z = true;
                oPSeekBarVolumizer4.mAllowAlarms = (oPSeekBarVolumizer4.mNotificationPolicy.priorityCategories & 32) != 0;
                OPSeekBarVolumizer oPSeekBarVolumizer5 = OPSeekBarVolumizer.this;
                if ((oPSeekBarVolumizer5.mNotificationPolicy.priorityCategories & 64) == 0) {
                    z = false;
                }
                oPSeekBarVolumizer5.mAllowMedia = z;
                OPSeekBarVolumizer.this.mAllowRinger = false;
                OPSeekBarVolumizer.this.updateSlider();
            }
        }

        private void updateVolumeSlider(int i, int i2) {
            boolean z;
            boolean z2 = true;
            if (OPSeekBarVolumizer.this.mNotificationOrRing) {
                z = OPSeekBarVolumizer.isNotificationOrRing(i);
            } else {
                z = i == OPSeekBarVolumizer.this.mStreamType;
            }
            if (OPSeekBarVolumizer.this.mSeekBar != null && z && i2 != -1) {
                if (!OPSeekBarVolumizer.this.mAudioManager.isStreamMute(OPSeekBarVolumizer.this.mStreamType) && i2 != 0) {
                    z2 = false;
                }
                OPSeekBarVolumizer.this.mUiHandler.postUpdateSlider(i2, OPSeekBarVolumizer.this.mLastAudibleStreamVolume, z2);
            }
        }
    }

    private void registerVolumeGroupCb() {
        if (this.mVolumeGroupId != -1) {
            this.mAudioManager.registerVolumeGroupCallback($$Lambda$_14QHG018Z6p13d3hzJuGTWnNeo.INSTANCE, this.mVolumeGroupCallback);
            this.mLastProgress = this.mAudioManager.getStreamVolume(this.mStreamType);
        }
    }

    private void unregisterVolumeGroupCb() {
        if (this.mVolumeGroupId != -1) {
            this.mAudioManager.unregisterVolumeGroupCallback(this.mVolumeGroupCallback);
        }
    }

    private class VolumeHandler extends Handler {
        private VolumeHandler() {
        }

        public void handleMessage(Message message) {
            SomeArgs someArgs = (SomeArgs) message.obj;
            if (message.what == 1 && OPSeekBarVolumizer.this.mVolumeGroupId == ((Integer) someArgs.arg1).intValue() && OPSeekBarVolumizer.this.mVolumeGroupId != -1) {
                OPSeekBarVolumizer.this.updateSlider();
            }
        }
    }
}
