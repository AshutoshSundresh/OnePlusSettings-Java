package com.android.settings.applications.specialaccess.premiumsms;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.applications.AppStateSmsPremBridge;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.EmptyTextSettings;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.widget.FooterPreference;
import java.util.ArrayList;

public class PremiumSmsAccess extends EmptyTextSettings implements AppStateBaseBridge.Callback, ApplicationsState.Callbacks, Preference.OnPreferenceChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.premium_sms_settings);
    private ApplicationsState mApplicationsState;
    private ApplicationsState.Session mSession;
    private AppStateSmsPremBridge mSmsBackend;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 388;
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onAllSizesComputed() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLauncherInfoChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLoadEntriesCompleted() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageIconChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageListChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageSizeChanged(String str) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRunningStateChanged(boolean z) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ApplicationsState instance = ApplicationsState.getInstance((Application) getContext().getApplicationContext());
        this.mApplicationsState = instance;
        this.mSession = instance.newSession(this, getSettingsLifecycle());
        this.mSmsBackend = new AppStateSmsPremBridge(getContext(), this.mApplicationsState, this);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settings.widget.EmptyTextSettings
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        setLoading(true, false);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        this.mSmsBackend.resume();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        this.mSmsBackend.pause();
        super.onPause();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        this.mSmsBackend.release();
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.premium_sms_settings;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        PremiumSmsPreference premiumSmsPreference = (PremiumSmsPreference) preference;
        int parseInt = Integer.parseInt((String) obj);
        logSpecialPermissionChange(parseInt, premiumSmsPreference.mAppEntry.info.packageName);
        this.mSmsBackend.setSmsState(premiumSmsPreference.mAppEntry.info.packageName, parseInt);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void logSpecialPermissionChange(int i, String str) {
        int i2 = i != 1 ? i != 2 ? i != 3 ? 0 : 780 : 779 : 778;
        if (i2 != 0) {
            MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
            metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(getActivity()), i2, getMetricsCategory(), str, i);
        }
    }

    private void updatePrefs(ArrayList<ApplicationsState.AppEntry> arrayList) {
        if (arrayList != null) {
            setEmptyText(C0017R$string.premium_sms_none);
            setLoading(false, true);
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            preferenceScreen.removeAll();
            preferenceScreen.setOrderingAsAdded(true);
            for (int i = 0; i < arrayList.size(); i++) {
                PremiumSmsPreference premiumSmsPreference = new PremiumSmsPreference(arrayList.get(i), getPrefContext());
                premiumSmsPreference.setOnPreferenceChangeListener(this);
                preferenceScreen.addPreference(premiumSmsPreference);
            }
            if (arrayList.size() != 0) {
                FooterPreference footerPreference = new FooterPreference(getPrefContext());
                footerPreference.setTitle(C0017R$string.premium_sms_warning);
                preferenceScreen.addPreference(footerPreference);
            }
        }
    }

    private void update() {
        updatePrefs(this.mSession.rebuild(AppStateSmsPremBridge.FILTER_APP_PREMIUM_SMS, ApplicationsState.ALPHA_COMPARATOR));
    }

    @Override // com.android.settings.applications.AppStateBaseBridge.Callback
    public void onExtraInfoUpdated() {
        update();
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
        updatePrefs(arrayList);
    }

    /* access modifiers changed from: private */
    public class PremiumSmsPreference extends DropDownPreference {
        private final ApplicationsState.AppEntry mAppEntry;

        public PremiumSmsPreference(ApplicationsState.AppEntry appEntry, Context context) {
            super(context);
            this.mAppEntry = appEntry;
            appEntry.ensureLabel(context);
            setTitle(this.mAppEntry.label);
            Drawable drawable = this.mAppEntry.icon;
            if (drawable != null) {
                setIcon(drawable);
            }
            setEntries(C0003R$array.security_settings_premium_sms_values);
            setEntryValues(new CharSequence[]{String.valueOf(1), String.valueOf(2), String.valueOf(3)});
            setValue(String.valueOf(getCurrentValue()));
            setSummary("%s");
        }

        private int getCurrentValue() {
            Object obj = this.mAppEntry.extraInfo;
            if (obj instanceof AppStateSmsPremBridge.SmsState) {
                return ((AppStateSmsPremBridge.SmsState) obj).smsState;
            }
            return 0;
        }

        @Override // androidx.preference.DropDownPreference, androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            if (getIcon() == null) {
                preferenceViewHolder.itemView.post(new Runnable() {
                    /* class com.android.settings.applications.specialaccess.premiumsms.PremiumSmsAccess.PremiumSmsPreference.AnonymousClass1 */

                    public void run() {
                        PremiumSmsAccess.this.mApplicationsState.ensureIcon(PremiumSmsPreference.this.mAppEntry);
                        PremiumSmsPreference premiumSmsPreference = PremiumSmsPreference.this;
                        premiumSmsPreference.setIcon(premiumSmsPreference.mAppEntry.icon);
                    }
                });
            }
            super.onBindViewHolder(preferenceViewHolder);
        }
    }
}
