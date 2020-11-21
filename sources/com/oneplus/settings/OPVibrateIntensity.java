package com.oneplus.settings;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import androidx.preference.Preference;
import com.android.settings.C0003R$array;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.ui.OPListDialog;
import com.oneplus.settings.utils.OPUtils;

public class OPVibrateIntensity extends SettingsPreferenceFragment implements OPListDialog.OnDialogListItemClickListener, Preference.OnPreferenceChangeListener {
    private Context mContext;
    private String mCunrrentType = "incoming_call_vibrate_intensity";
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.oneplus.settings.OPVibrateIntensity.AnonymousClass1 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 10) {
                View view = null;
                if (OPVibrateIntensity.this.mOPListDialog != null) {
                    view = OPVibrateIntensity.this.mOPListDialog.getRootContainer();
                }
                if (view == null) {
                    view = new View(OPVibrateIntensity.this.mContext);
                }
                view.performHapticFeedback(1);
                Settings.System.putInt(OPVibrateIntensity.this.getActivity().getContentResolver(), "vibrate_on_touch_intensity", OPVibrateIntensity.this.mTempTouchTapIntensity);
            }
        }
    };
    private OPListDialog mOPListDialog;
    private int mTempTouchTapIntensity;
    private Preference mVibrateOnTouchIntensityPreference;
    private Vibrator mVibrator;
    private long[][] sNoticeVibrateIntensity = {new long[]{-1, 0, 100, 150, 100, 1000, 100, 150, 100}, new long[]{-2, 0, 100, 150, 100, 1000, 100, 150, 100}, new long[]{-3, 0, 100, 150, 100, 1000, 100, 150, 100}};
    private long[][] sTouchVibrateIntensity = {new long[]{-1, 0, 10, 1000, 10}, new long[]{-2, 0, 10, 1000, 10}, new long[]{-3, 0, 10, 1000, 10}};
    private long[][] sVibratePatternrhythm = {new long[]{-2, 0, 1000, 1000, 1000}, new long[]{-2, 0, 500, 250, 10, 1000, 500, 250, 10}, new long[]{-2, 0, 300, 400, 300, 400, 300, 1000, 300, 400, 300, 400, 300}, new long[]{-2, 0, 30, 80, 30, 80, 50, 180, 600, 1000, 30, 80, 30, 80, 50, 180, 600}, new long[]{-2, 0, 80, 200, 600, 150, 10, 1000, 80, 200, 600, 150, 10}};

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_vibrate_intensity);
        this.mContext = getActivity();
        Vibrator vibrator = (Vibrator) getActivity().getSystemService("vibrator");
        this.mVibrator = vibrator;
        if (vibrator != null && !vibrator.hasVibrator()) {
            this.mVibrator = null;
        }
        findPreference("incoming_call_vibrate_intensity");
        findPreference("notice_vibrate_intensity");
        this.mVibrateOnTouchIntensityPreference = findPreference("vibrate_on_touch_intensity");
        if (OPUtils.isSupportXVibrate()) {
            this.mVibrateOnTouchIntensityPreference.setVisible(false);
        }
        updateVibratePreferenceDescription("incoming_call_vibrate_intensity", Settings.System.getInt(getActivity().getContentResolver(), "incoming_call_vibrate_intensity", 0));
        updateVibratePreferenceDescription("notice_vibrate_intensity", Settings.System.getInt(getActivity().getContentResolver(), "notice_vibrate_intensity", 0));
        int i = Settings.System.getInt(getActivity().getContentResolver(), "vibrate_on_touch_intensity", 0);
        this.mTempTouchTapIntensity = i;
        updateVibratePreferenceDescription("vibrate_on_touch_intensity", i);
    }

    private void updateVibratePreferenceDescription(String str, int i) {
        Preference findPreference = findPreference(str);
        if (findPreference != null) {
            findPreference.setSummary(this.mContext.getResources().getStringArray(C0003R$array.vibrate_intensity)[i]);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        OPListDialog oPListDialog = new OPListDialog(this.mContext, preference.getTitle(), this.mContext.getResources().getStringArray(C0003R$array.vibrate_intensity_values), this.mContext.getResources().getStringArray(C0003R$array.vibrate_intensity));
        this.mOPListDialog = oPListDialog;
        oPListDialog.setOnDialogListItemClickListener(this);
        if ("incoming_call_vibrate_intensity".equals(key)) {
            this.mOPListDialog.setVibrateLevelKey("incoming_call_vibrate_intensity");
            this.mCunrrentType = "incoming_call_vibrate_intensity";
        } else if ("notice_vibrate_intensity".equals(key)) {
            this.mOPListDialog.setVibrateLevelKey("notice_vibrate_intensity");
            this.mCunrrentType = "notice_vibrate_intensity";
        } else if ("vibrate_on_touch_intensity".equals(key)) {
            this.mOPListDialog.setVibrateLevelKey("vibrate_on_touch_intensity");
            this.mCunrrentType = "vibrate_on_touch_intensity";
        }
        this.mOPListDialog.show();
        return true;
    }

    @Override // com.oneplus.settings.ui.OPListDialog.OnDialogListItemClickListener
    public void OnDialogListItemClick(int i) {
        Vibrator vibrator;
        Log.d("OPVibrateIntensity", "OnDialogListItemClick--index:" + i);
        if ("incoming_call_vibrate_intensity".equals(this.mCunrrentType) && this.mVibrator != null) {
            int i2 = Settings.System.getInt(getActivity().getContentResolver(), "incoming_call_vibrate_mode", i);
            int i3 = 0;
            if (i2 > this.sVibratePatternrhythm.length - 1) {
                i2 = 0;
            }
            this.mVibrator.cancel();
            if (i == 0) {
                this.sVibratePatternrhythm[i2][0] = -1;
            } else if (i == 1) {
                this.sVibratePatternrhythm[i2][0] = -2;
            } else if (i == 2) {
                this.sVibratePatternrhythm[i2][0] = -3;
            }
            while (true) {
                long[][] jArr = this.sVibratePatternrhythm;
                if (i3 < jArr[i2].length) {
                    Log.d("OPVibrateIntensity", "sVibratePatternrhythm [" + i2 + "][" + i3 + "] = " + this.sVibratePatternrhythm[i2][i3]);
                    i3++;
                } else {
                    this.mVibrator.vibrate(jArr[i2], -1);
                    return;
                }
            }
        } else if ("notice_vibrate_intensity".equals(this.mCunrrentType) && (vibrator = this.mVibrator) != null) {
            vibrator.cancel();
            this.mVibrator.vibrate(this.sNoticeVibrateIntensity[i], -1);
        } else if ("vibrate_on_touch_intensity".equals(this.mCunrrentType) && this.mVibrator != null) {
            if (OPUtils.isSupportZVibrationMotor()) {
                Settings.System.putInt(getActivity().getContentResolver(), "vibrate_on_touch_intensity", i);
                this.mHandler.removeMessages(10);
                this.mHandler.sendEmptyMessageDelayed(10, 100);
                return;
            }
            this.mVibrator.cancel();
            this.mVibrator.vibrate(this.sTouchVibrateIntensity[i], -1);
        }
    }

    @Override // com.oneplus.settings.ui.OPListDialog.OnDialogListItemClickListener
    public void OnDialogListConfirmClick(int i) {
        if ("incoming_call_vibrate_intensity".equals(this.mCunrrentType)) {
            Settings.System.putInt(getActivity().getContentResolver(), "incoming_call_vibrate_intensity", i);
            updateVibratePreferenceDescription("incoming_call_vibrate_intensity", i);
        } else if ("notice_vibrate_intensity".equals(this.mCunrrentType) && this.mVibrator != null) {
            Settings.System.putInt(getActivity().getContentResolver(), "notice_vibrate_intensity", i);
            updateVibratePreferenceDescription("notice_vibrate_intensity", i);
        } else if ("vibrate_on_touch_intensity".equals(this.mCunrrentType) && this.mVibrator != null) {
            Settings.System.putInt(getActivity().getContentResolver(), "vibrate_on_touch_intensity", i);
            updateVibratePreferenceDescription("vibrate_on_touch_intensity", i);
        }
        Vibrator vibrator = this.mVibrator;
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    @Override // com.oneplus.settings.ui.OPListDialog.OnDialogListItemClickListener
    public void OnDialogListCancelClick() {
        Vibrator vibrator = this.mVibrator;
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}
