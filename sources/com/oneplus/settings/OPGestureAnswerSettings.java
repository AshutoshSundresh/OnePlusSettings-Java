package com.oneplus.settings;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MenuItem;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.utils.OPUtils;

public class OPGestureAnswerSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final Uri OPGUEST_ANSWER_CALLT_URI = Settings.Global.getUriFor("opguest_answer_call");
    private static final Uri OPGUEST_ROUTE_AUDIO_URI = Settings.Global.getUriFor("opguest_route_audio");
    private ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        /* class com.oneplus.settings.OPGestureAnswerSettings.AnonymousClass1 */

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (OPGestureAnswerSettings.OPGUEST_ANSWER_CALLT_URI.equals(uri) && OPGestureAnswerSettings.this.mGestureAnswerCall != null) {
                OPGestureAnswerSettings.this.mGestureAnswerCall.setChecked(OPGestureAnswerSettings.this.isGestureAnswerOn());
            }
            if (OPGestureAnswerSettings.OPGUEST_ROUTE_AUDIO_URI.equals(uri) && OPGestureAnswerSettings.this.mGestureRouteAudio != null) {
                OPGestureAnswerSettings.this.mGestureRouteAudio.setChecked(OPGestureAnswerSettings.this.isGestureRouteAudio());
            }
        }
    };
    private SwitchPreference mGestureAnswerCall;
    private SwitchPreference mGestureRouteAudio;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_gesture_answercall_settings);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("gesture_answer_call");
        this.mGestureAnswerCall = switchPreference;
        switchPreference.setChecked(isGestureAnswerOn());
        this.mGestureAnswerCall.setOnPreferenceChangeListener(this);
        if (OPUtils.supportGestureAudioRoute()) {
            SwitchPreference switchPreference2 = (SwitchPreference) findPreference("gesture_route_audio");
            this.mGestureRouteAudio = switchPreference2;
            switchPreference2.setChecked(isGestureRouteAudio());
            this.mGestureRouteAudio.setOnPreferenceChangeListener(this);
            return;
        }
        getActivity().setTitle(C0017R$string.oneplus_gesture_of_answer_call_title);
        this.mGestureAnswerCall.setTitle(C0017R$string.oneplus_gesture_of_answer_call_title);
        this.mGestureAnswerCall.setSummary("");
        removePreference("gesture_route_audio");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(Settings.Global.getUriFor("opguest_answer_call"), true, this.mContentObserver, -1);
        getContentResolver().registerContentObserver(Settings.Global.getUriFor("opguest_route_audio"), true, this.mContentObserver, -1);
        SwitchPreference switchPreference = this.mGestureAnswerCall;
        if (switchPreference != null) {
            switchPreference.setChecked(isGestureAnswerOn());
        }
        SwitchPreference switchPreference2 = this.mGestureRouteAudio;
        if (switchPreference2 != null) {
            switchPreference2.setChecked(isGestureRouteAudio());
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(this.mContentObserver);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mGestureAnswerCall) {
            setGestureAnswerOn(((Boolean) obj).booleanValue());
            return true;
        } else if (preference != this.mGestureRouteAudio) {
            return false;
        } else {
            setGestureAudioRoute(((Boolean) obj).booleanValue());
            return true;
        }
    }

    private void setGestureAnswerOn(boolean z) {
        OPUtils.sendAnalytics("YXKF6G2OQE", "phone.answer", "gestureon", z ? "1" : "0");
        Settings.Global.putInt(getContentResolver(), "opguest_answer_call", z ? 1 : 0);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isGestureAnswerOn() {
        return Settings.Global.getInt(getContentResolver(), "opguest_answer_call", 0) == 1;
    }

    private void setGestureAudioRoute(boolean z) {
        OPUtils.sendAnalytics("YXKF6G2OQE", "phone.answer", "gestureswitch", z ? "1" : "0");
        Settings.Global.putInt(getContentResolver(), "opguest_route_audio", z ? 1 : 0);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isGestureRouteAudio() {
        return Settings.Global.getInt(getContentResolver(), "opguest_route_audio", 1) == 1;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }
}
