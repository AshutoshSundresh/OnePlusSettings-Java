package com.android.settings.network;

import android.content.Context;
import android.net.NetworkScoreManager;
import android.net.NetworkScorerAppData;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.widget.RadioButtonPreference;
import java.util.List;

public class NetworkScorerPicker extends InstrumentedPreferenceFragment implements RadioButtonPreference.OnClickListener {
    private NetworkScoreManager mNetworkScoreManager;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 861;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        updateCandidates();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mNetworkScoreManager = createNetworkScorerManager(context);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        setHasOptionsMenu(true);
        return onCreateView;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.network_scorer_picker_prefs;
    }

    public void updateCandidates() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        List allValidScorers = this.mNetworkScoreManager.getAllValidScorers();
        String activeScorerPackage = getActiveScorerPackage();
        RadioButtonPreference radioButtonPreference = new RadioButtonPreference(getPrefContext());
        radioButtonPreference.setTitle(C0017R$string.network_scorer_picker_none_preference);
        if (allValidScorers.isEmpty()) {
            radioButtonPreference.setChecked(true);
        } else {
            radioButtonPreference.setKey(null);
            radioButtonPreference.setChecked(TextUtils.isEmpty(activeScorerPackage));
            radioButtonPreference.setOnClickListener(this);
        }
        preferenceScreen.addPreference(radioButtonPreference);
        int size = allValidScorers.size();
        for (int i = 0; i < size; i++) {
            RadioButtonPreference radioButtonPreference2 = new RadioButtonPreference(getPrefContext());
            NetworkScorerAppData networkScorerAppData = (NetworkScorerAppData) allValidScorers.get(i);
            String recommendationServicePackageName = networkScorerAppData.getRecommendationServicePackageName();
            radioButtonPreference2.setTitle(networkScorerAppData.getRecommendationServiceLabel());
            radioButtonPreference2.setKey(recommendationServicePackageName);
            radioButtonPreference2.setChecked(TextUtils.equals(activeScorerPackage, recommendationServicePackageName));
            radioButtonPreference2.setOnClickListener(this);
            preferenceScreen.addPreference(radioButtonPreference2);
        }
    }

    private String getActiveScorerPackage() {
        return this.mNetworkScoreManager.getActiveScorerPackage();
    }

    private boolean setActiveScorer(String str) {
        if (!TextUtils.equals(str, getActiveScorerPackage())) {
            return this.mNetworkScoreManager.setActiveScorer(str);
        }
        return false;
    }

    @Override // com.android.settings.widget.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        String key = radioButtonPreference.getKey();
        if (setActiveScorer(key)) {
            updateCheckedState(key);
        }
    }

    private void updateCheckedState(String str) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int preferenceCount = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if (preference instanceof RadioButtonPreference) {
                ((RadioButtonPreference) preference).setChecked(TextUtils.equals(preference.getKey(), str));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public NetworkScoreManager createNetworkScorerManager(Context context) {
        return (NetworkScoreManager) context.getSystemService("network_score");
    }
}
