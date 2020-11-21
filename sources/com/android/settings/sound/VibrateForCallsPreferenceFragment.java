package com.android.settings.sound;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.widget.CandidateInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VibrateForCallsPreferenceFragment extends RadioButtonPickerFragment {
    static final String KEY_ALWAYS_VIBRATE = "always_vibrate";
    static final String KEY_NEVER_VIBRATE = "never_vibrate";
    static final String KEY_RAMPING_RINGER = "ramping_ringer";
    private final Map<String, VibrateForCallsCandidateInfo> mCandidates = new ArrayMap();

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1827;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        loadCandidates(context);
    }

    private void loadCandidates(Context context) {
        this.mCandidates.put(KEY_NEVER_VIBRATE, new VibrateForCallsCandidateInfo(KEY_NEVER_VIBRATE, C0017R$string.vibrate_when_ringing_option_never_vibrate));
        this.mCandidates.put(KEY_ALWAYS_VIBRATE, new VibrateForCallsCandidateInfo(KEY_ALWAYS_VIBRATE, C0017R$string.vibrate_when_ringing_option_always_vibrate));
        this.mCandidates.put(KEY_RAMPING_RINGER, new VibrateForCallsCandidateInfo(KEY_RAMPING_RINGER, C0017R$string.vibrate_when_ringing_option_ramping_ringer));
    }

    private void updateSettings(VibrateForCallsCandidateInfo vibrateForCallsCandidateInfo) {
        String key = vibrateForCallsCandidateInfo.getKey();
        if (TextUtils.equals(key, KEY_ALWAYS_VIBRATE)) {
            Settings.System.putInt(getContext().getContentResolver(), "vibrate_when_ringing", 1);
            Settings.Global.putInt(getContext().getContentResolver(), "apply_ramping_ringer", 0);
        } else if (TextUtils.equals(key, KEY_RAMPING_RINGER)) {
            Settings.System.putInt(getContext().getContentResolver(), "vibrate_when_ringing", 0);
            Settings.Global.putInt(getContext().getContentResolver(), "apply_ramping_ringer", 1);
        } else {
            Settings.System.putInt(getContext().getContentResolver(), "vibrate_when_ringing", 0);
            Settings.Global.putInt(getContext().getContentResolver(), "apply_ramping_ringer", 0);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<? extends CandidateInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.mCandidates.get(KEY_NEVER_VIBRATE));
        arrayList.add(this.mCandidates.get(KEY_ALWAYS_VIBRATE));
        arrayList.add(this.mCandidates.get(KEY_RAMPING_RINGER));
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        if (Settings.Global.getInt(getContext().getContentResolver(), "apply_ramping_ringer", 0) == 1) {
            return KEY_RAMPING_RINGER;
        }
        return Settings.System.getInt(getContext().getContentResolver(), "vibrate_when_ringing", 0) == 1 ? KEY_ALWAYS_VIBRATE : KEY_NEVER_VIBRATE;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        VibrateForCallsCandidateInfo vibrateForCallsCandidateInfo = this.mCandidates.get(str);
        if (vibrateForCallsCandidateInfo == null) {
            Log.e("VibrateForCallsPreferenceFragment", "Unknown vibrate for calls candidate (key = " + str + ")!");
            return false;
        }
        updateSettings(vibrateForCallsCandidateInfo);
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.vibrate_for_calls_settings;
    }

    /* access modifiers changed from: package-private */
    public class VibrateForCallsCandidateInfo extends CandidateInfo {
        private final String mKey;
        private final int mLabelId;

        @Override // com.android.settingslib.widget.CandidateInfo
        public Drawable loadIcon() {
            return null;
        }

        VibrateForCallsCandidateInfo(String str, int i) {
            super(true);
            this.mKey = str;
            this.mLabelId = i;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public CharSequence loadLabel() {
            return VibrateForCallsPreferenceFragment.this.getContext().getString(this.mLabelId);
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public String getKey() {
            return this.mKey;
        }
    }
}
