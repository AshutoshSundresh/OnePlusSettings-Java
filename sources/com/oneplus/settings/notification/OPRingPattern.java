package com.oneplus.settings.notification;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.search.BaseSearchIndexProvider;
import java.util.Arrays;
import java.util.List;

public class OPRingPattern extends SettingsPreferenceFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.notification.OPRingPattern.AnonymousClass2 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_ring_pattern;
            return Arrays.asList(searchIndexableResource);
        }
    };
    private final SettingsObserver mSettingsObserver = new SettingsObserver();
    private TwoStatePreference mVibrateWhenRinging;
    private boolean mVoiceCapable;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 76;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_ring_pattern);
        this.mVoiceCapable = Utils.isVoiceCapable(getActivity());
        initVibrateWhenRinging();
        this.mSettingsObserver.register(true);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        TwoStatePreference twoStatePreference = this.mVibrateWhenRinging;
        if (twoStatePreference != null) {
            boolean z = false;
            if (Settings.System.getInt(getContentResolver(), "vibrate_when_ringing", 0) != 0) {
                z = true;
            }
            twoStatePreference.setChecked(z);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        this.mSettingsObserver.register(false);
    }

    private void initVibrateWhenRinging() {
        TwoStatePreference twoStatePreference = (TwoStatePreference) findPreference("vibrate_when_ringing");
        this.mVibrateWhenRinging = twoStatePreference;
        if (twoStatePreference == null) {
            Log.i("OPRingPattern", "Preference not found: vibrate_when_ringing");
        } else if (!this.mVoiceCapable) {
            getPreferenceScreen().removePreference(this.mVibrateWhenRinging);
            this.mVibrateWhenRinging = null;
        } else {
            twoStatePreference.setPersistent(false);
            updateVibrateWhenRinging();
            this.mVibrateWhenRinging.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                /* class com.oneplus.settings.notification.OPRingPattern.AnonymousClass1 */

                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    return Settings.System.putInt(OPRingPattern.this.getContentResolver(), "vibrate_when_ringing", ((Boolean) obj).booleanValue() ? 1 : 0);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateVibrateWhenRinging() {
        TwoStatePreference twoStatePreference = this.mVibrateWhenRinging;
        if (twoStatePreference != null) {
            boolean z = false;
            if (Settings.System.getInt(getContentResolver(), "vibrate_when_ringing", 0) != 0) {
                z = true;
            }
            twoStatePreference.setChecked(z);
        }
    }

    private final class SettingsObserver extends ContentObserver {
        private final Uri VIBRATE_WHEN_RINGING_URI = Settings.System.getUriFor("vibrate_when_ringing");

        public SettingsObserver() {
            super(new Handler());
        }

        public void register(boolean z) {
            ContentResolver contentResolver = OPRingPattern.this.getContentResolver();
            if (z) {
                contentResolver.registerContentObserver(this.VIBRATE_WHEN_RINGING_URI, false, this);
            } else {
                contentResolver.unregisterContentObserver(this);
            }
        }

        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.VIBRATE_WHEN_RINGING_URI.equals(uri)) {
                OPRingPattern.this.updateVibrateWhenRinging();
            }
        }
    }
}
