package com.android.settings.core;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.R$id;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.survey.SurveyMixin;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.instrumentation.VisibilityLoggerMixin;
import com.android.settingslib.core.lifecycle.ObservablePreferenceFragment;
import com.oneplus.settings.edgeeffect.SpringRelativeLayout;

public abstract class InstrumentedPreferenceFragment extends ObservablePreferenceFragment implements Instrumentable {
    protected MetricsFeatureProvider mMetricsFeatureProvider;
    private VisibilityLoggerMixin mVisibilityLoggerMixin;

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return -1;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mVisibilityLoggerMixin = new VisibilityLoggerMixin(getMetricsCategory(), this.mMetricsFeatureProvider);
        getSettingsLifecycle().addObserver(this.mVisibilityLoggerMixin);
        getSettingsLifecycle().addObserver(new SurveyMixin(this, getClass().getSimpleName()));
        super.onAttach(context);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        this.mVisibilityLoggerMixin.setSourceMetricsCategory(getActivity());
        super.onResume();
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        int preferenceScreenResId = getPreferenceScreenResId();
        if (preferenceScreenResId > 0) {
            addPreferencesFromResource(preferenceScreenResId);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void addPreferencesFromResource(int i) {
        super.addPreferencesFromResource(i);
        updateActivityTitleWithScreenTitle(getPreferenceScreen());
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.preference.DialogPreference.TargetFragment
    public <T extends Preference> T findPreference(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        return (T) super.findPreference(charSequence);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        writePreferenceClickMetric(preference);
        return super.onPreferenceTreeClick(preference);
    }

    /* access modifiers changed from: protected */
    public final Context getPrefContext() {
        return getPreferenceManager().getContext();
    }

    /* access modifiers changed from: protected */
    public void writeElapsedTimeMetric(int i, String str) {
        this.mVisibilityLoggerMixin.writeElapsedTimeMetric(i, str);
    }

    /* access modifiers changed from: protected */
    public void writePreferenceClickMetric(Preference preference) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, getMetricsCategory());
    }

    private void updateActivityTitleWithScreenTitle(PreferenceScreen preferenceScreen) {
        if (preferenceScreen != null) {
            CharSequence title = preferenceScreen.getTitle();
            if (!TextUtils.isEmpty(title)) {
                getActivity().setTitle(title);
                return;
            }
            Log.w("InstrumentedPrefFrag", "Screen title missing for fragment " + getClass().getName());
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        enableSpringEdgeEffect();
        return onCreateView;
    }

    private void enableSpringEdgeEffect() {
        RecyclerView listView = getListView();
        if (listView == null) {
            Log.d("InstrumentedPrefFrag", "enableSpringEdgeEffect list == null");
            return;
        }
        ViewParent parent = listView.getParent();
        if (parent == null) {
            Log.d("InstrumentedPrefFrag", "enableSpringEdgeEffect parent == null");
            return;
        }
        Log.d("InstrumentedPrefFrag", "enableSpringEdgeEffect parent = " + parent);
        ViewGroup viewGroup = (ViewGroup) parent;
        viewGroup.removeView(listView);
        final SpringRelativeLayout springRelativeLayout = new SpringRelativeLayout(getContext());
        springRelativeLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        springRelativeLayout.setFocusable(true);
        springRelativeLayout.setFocusableInTouchMode(true);
        springRelativeLayout.setSaveEnabled(false);
        springRelativeLayout.addView(listView);
        springRelativeLayout.addSpringView(R$id.recycler_view);
        listView.setEdgeEffectFactory(springRelativeLayout.createEdgeEffectFactory());
        viewGroup.addView(springRelativeLayout);
        listView.addOnScrollListener(new RecyclerView.OnScrollListener(this) {
            /* class com.android.settings.core.InstrumentedPreferenceFragment.AnonymousClass1 */
            int state = 0;

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                this.state = i;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (this.state == 1 && i != 0) {
                    springRelativeLayout.onRecyclerViewScrolled();
                }
            }
        });
    }
}
