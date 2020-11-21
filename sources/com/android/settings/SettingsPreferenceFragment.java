package com.android.settings;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.CustomListPreference;
import com.android.settings.RestrictedListPreference;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.search.actionbar.SearchMenuController;
import com.android.settings.support.actionbar.HelpMenuController;
import com.android.settings.support.actionbar.HelpResourceProvider;
import com.android.settings.widget.HighlightablePreferenceGroupAdapter;
import com.android.settings.widget.LoadingViewController;
import com.android.settingslib.CustomDialogPreferenceCompat;
import com.android.settingslib.CustomEditTextPreferenceCompat;
import com.android.settingslib.widget.LayoutPreference;
import com.oneplus.settings.widget.FooterPreferenceMixinCompat;
import java.util.UUID;

public abstract class SettingsPreferenceFragment extends InstrumentedPreferenceFragment implements DialogCreatable, HelpResourceProvider {
    public HighlightablePreferenceGroupAdapter mAdapter;
    private boolean mAnimationAllowed;
    private ContentResolver mContentResolver;
    private RecyclerView.Adapter mCurrentRootAdapter;
    private RecyclerView.AdapterDataObserver mDataSetObserver = new RecyclerView.AdapterDataObserver() {
        /* class com.android.settings.SettingsPreferenceFragment.AnonymousClass1 */

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onChanged() {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeChanged(int i, int i2) {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeChanged(int i, int i2, Object obj) {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeInserted(int i, int i2) {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeRemoved(int i, int i2) {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeMoved(int i, int i2, int i3) {
            SettingsPreferenceFragment.this.onDataSetChanged();
        }
    };
    private SettingsDialogFragment mDialogFragment;
    private View mEmptyView;
    protected final FooterPreferenceMixinCompat mFooterPreferenceMixin = new FooterPreferenceMixinCompat(this, getSettingsLifecycle());
    private LayoutPreference mHeader;
    private boolean mIsDataSetObserverRegistered = false;
    private LinearLayoutManager mLayoutManager;
    ViewGroup mPinnedHeaderFrameLayout;
    private ArrayMap<String, Preference> mPreferenceCache;
    public boolean mPreferenceHighlighted = false;

    @Override // com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return 0;
    }

    public int getInitialExpandedChildCount() {
        return 0;
    }

    @Override // com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        return null;
    }

    public void onDialogShowing() {
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SearchMenuController.init(this);
        HelpMenuController.init(this);
        if (bundle != null) {
            this.mPreferenceHighlighted = bundle.getBoolean("android:preference_highlighted");
        }
        HighlightablePreferenceGroupAdapter.adjustInitialExpandedChildCount(this);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        this.mPinnedHeaderFrameLayout = (ViewGroup) onCreateView.findViewById(C0010R$id.pinned_header);
        return onCreateView;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment
    public void addPreferencesFromResource(int i) {
        super.addPreferencesFromResource(i);
        checkAvailablePrefs(getPreferenceScreen());
    }

    /* access modifiers changed from: package-private */
    public void checkAvailablePrefs(PreferenceGroup preferenceGroup) {
        if (preferenceGroup != null) {
            for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
                Preference preference = preferenceGroup.getPreference(i);
                if ((preference instanceof SelfAvailablePreference) && !((SelfAvailablePreference) preference).isAvailable(getContext())) {
                    preference.setVisible(false);
                } else if (preference instanceof PreferenceGroup) {
                    checkAvailablePrefs((PreferenceGroup) preference);
                }
            }
        }
    }

    public View setPinnedHeaderView(int i) {
        View inflate = getActivity().getLayoutInflater().inflate(i, this.mPinnedHeaderFrameLayout, false);
        setPinnedHeaderView(inflate);
        return inflate;
    }

    public void setPinnedHeaderView(View view) {
        this.mPinnedHeaderFrameLayout.addView(view);
        this.mPinnedHeaderFrameLayout.setVisibility(0);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        HighlightablePreferenceGroupAdapter highlightablePreferenceGroupAdapter = this.mAdapter;
        if (highlightablePreferenceGroupAdapter != null) {
            bundle.putBoolean("android:preference_highlighted", highlightablePreferenceGroupAdapter.isHighlightRequested());
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setHasOptionsMenu(true);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        highlightPreferenceIfNeeded();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.PreferenceFragmentCompat
    public void onBindPreferences() {
        registerObserverIfNeeded();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.PreferenceFragmentCompat
    public void onUnbindPreferences() {
        unregisterObserverIfNeeded();
    }

    public void setLoading(boolean z, boolean z2) {
        LoadingViewController.handleLoadingContainer(getView().findViewById(C0010R$id.loading_container), getListView(), !z, z2);
    }

    public void registerObserverIfNeeded() {
        if (!this.mIsDataSetObserverRegistered) {
            RecyclerView.Adapter adapter = this.mCurrentRootAdapter;
            if (adapter != null) {
                adapter.unregisterAdapterDataObserver(this.mDataSetObserver);
            }
            RecyclerView.Adapter adapter2 = getListView().getAdapter();
            this.mCurrentRootAdapter = adapter2;
            adapter2.registerAdapterDataObserver(this.mDataSetObserver);
            this.mIsDataSetObserverRegistered = true;
            onDataSetChanged();
        }
    }

    public void unregisterObserverIfNeeded() {
        if (this.mIsDataSetObserverRegistered) {
            RecyclerView.Adapter adapter = this.mCurrentRootAdapter;
            if (adapter != null) {
                adapter.unregisterAdapterDataObserver(this.mDataSetObserver);
                this.mCurrentRootAdapter = null;
            }
            this.mIsDataSetObserverRegistered = false;
        }
    }

    public void highlightPreferenceIfNeeded() {
        HighlightablePreferenceGroupAdapter highlightablePreferenceGroupAdapter;
        if (isAdded() && (highlightablePreferenceGroupAdapter = this.mAdapter) != null) {
            highlightablePreferenceGroupAdapter.requestHighlight(getView(), getListView());
        }
    }

    /* access modifiers changed from: protected */
    public boolean isPreferenceExpanded(Preference preference) {
        HighlightablePreferenceGroupAdapter highlightablePreferenceGroupAdapter = this.mAdapter;
        return highlightablePreferenceGroupAdapter == null || highlightablePreferenceGroupAdapter.getPreferenceAdapterPosition(preference) != -1;
    }

    /* access modifiers changed from: protected */
    public void onDataSetChanged() {
        highlightPreferenceIfNeeded();
        updateEmptyView();
    }

    public LayoutPreference getHeaderView() {
        return this.mHeader;
    }

    /* access modifiers changed from: protected */
    public void setHeaderView(int i) {
        LayoutPreference layoutPreference = new LayoutPreference(getPrefContext(), i);
        this.mHeader = layoutPreference;
        layoutPreference.setSelectable(false);
        addPreferenceToTop(this.mHeader);
    }

    private void addPreferenceToTop(LayoutPreference layoutPreference) {
        layoutPreference.setOrder(-1);
        if (getPreferenceScreen() != null) {
            getPreferenceScreen().addPreference(layoutPreference);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        LayoutPreference layoutPreference;
        if (preferenceScreen != null && !preferenceScreen.isAttached()) {
            preferenceScreen.setShouldUseGeneratedIds(this.mAnimationAllowed);
        }
        super.setPreferenceScreen(preferenceScreen);
        if (preferenceScreen != null && (layoutPreference = this.mHeader) != null) {
            preferenceScreen.addPreference(layoutPreference);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateEmptyView() {
        if (this.mEmptyView != null) {
            int i = 0;
            if (getPreferenceScreen() != null) {
                View findViewById = getActivity().findViewById(16908351);
                boolean z = true;
                if ((getPreferenceScreen().getPreferenceCount() - (this.mHeader != null ? 1 : 0)) - (this.mFooterPreferenceMixin.hasFooter() ? 1 : 0) > 0 && (findViewById == null || findViewById.getVisibility() == 0)) {
                    z = false;
                }
                View view = this.mEmptyView;
                if (!z) {
                    i = 8;
                }
                view.setVisibility(i);
                return;
            }
            this.mEmptyView.setVisibility(0);
        }
    }

    public void setEmptyView(View view) {
        View view2 = this.mEmptyView;
        if (view2 != null) {
            view2.setVisibility(8);
        }
        this.mEmptyView = view;
        updateEmptyView();
    }

    public View getEmptyView() {
        return this.mEmptyView;
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public RecyclerView.LayoutManager onCreateLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        this.mLayoutManager = linearLayoutManager;
        return linearLayoutManager;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.preference.PreferenceFragmentCompat
    public RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
        String str;
        Bundle arguments = getArguments();
        if (arguments == null) {
            str = null;
        } else {
            str = arguments.getString(":settings:fragment_args_key");
        }
        HighlightablePreferenceGroupAdapter highlightablePreferenceGroupAdapter = new HighlightablePreferenceGroupAdapter(preferenceScreen, str, this.mPreferenceHighlighted);
        this.mAdapter = highlightablePreferenceGroupAdapter;
        return highlightablePreferenceGroupAdapter;
    }

    /* access modifiers changed from: protected */
    public void setAnimationAllowed(boolean z) {
        this.mAnimationAllowed = z;
    }

    /* access modifiers changed from: protected */
    public void cacheRemoveAllPrefs(PreferenceGroup preferenceGroup) {
        this.mPreferenceCache = new ArrayMap<>();
        int preferenceCount = preferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceGroup.getPreference(i);
            if (!TextUtils.isEmpty(preference.getKey())) {
                this.mPreferenceCache.put(preference.getKey(), preference);
            }
        }
    }

    /* access modifiers changed from: protected */
    public Preference getCachedPreference(String str) {
        ArrayMap<String, Preference> arrayMap = this.mPreferenceCache;
        if (arrayMap != null) {
            return arrayMap.remove(str);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void removeCachedPrefs(PreferenceGroup preferenceGroup) {
        for (Preference preference : this.mPreferenceCache.values()) {
            preferenceGroup.removePreference(preference);
        }
        this.mPreferenceCache = null;
    }

    public boolean removePreference(String str) {
        return removePreference(getPreferenceScreen(), str);
    }

    /* access modifiers changed from: package-private */
    public boolean removePreference(PreferenceGroup preferenceGroup, String str) {
        int preferenceCount = preferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceGroup.getPreference(i);
            if (TextUtils.equals(preference.getKey(), str)) {
                return preferenceGroup.removePreference(preference);
            }
            if ((preference instanceof PreferenceGroup) && removePreference((PreferenceGroup) preference, str)) {
                return true;
            }
        }
        return false;
    }

    public final void finishFragment() {
        getActivity().onBackPressed();
    }

    /* access modifiers changed from: protected */
    public ContentResolver getContentResolver() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            this.mContentResolver = activity.getContentResolver();
        }
        return this.mContentResolver;
    }

    /* access modifiers changed from: protected */
    public Object getSystemService(String str) {
        return getActivity().getSystemService(str);
    }

    /* access modifiers changed from: protected */
    public PackageManager getPackageManager() {
        return getActivity().getPackageManager();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        SettingsDialogFragment settingsDialogFragment;
        if (isRemoving() && (settingsDialogFragment = this.mDialogFragment) != null) {
            settingsDialogFragment.dismiss();
            this.mDialogFragment = null;
        }
        super.onDetach();
    }

    /* access modifiers changed from: protected */
    public void showDialog(int i) {
        if (this.mDialogFragment != null) {
            Log.e("SettingsPreference", "Old dialog fragment not null!");
        }
        SettingsDialogFragment newInstance = SettingsDialogFragment.newInstance(this, i);
        this.mDialogFragment = newInstance;
        newInstance.show(getChildFragmentManager(), Integer.toString(i));
    }

    /* access modifiers changed from: protected */
    public void removeDialog(int i) {
        SettingsDialogFragment settingsDialogFragment = this.mDialogFragment;
        if (settingsDialogFragment != null && settingsDialogFragment.getDialogId() == i) {
            this.mDialogFragment.dismissAllowingStateLoss();
        }
        this.mDialogFragment = null;
    }

    /* access modifiers changed from: protected */
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        SettingsDialogFragment settingsDialogFragment = this.mDialogFragment;
        if (settingsDialogFragment != null) {
            settingsDialogFragment.mOnDismissListener = onDismissListener;
        }
    }

    @Override // androidx.preference.PreferenceManager.OnDisplayPreferenceDialogListener, androidx.preference.PreferenceFragmentCompat
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment dialogFragment;
        if (preference.getKey() == null) {
            preference.setKey(UUID.randomUUID().toString());
        }
        if (preference instanceof RestrictedListPreference) {
            dialogFragment = RestrictedListPreference.RestrictedListPreferenceDialogFragment.newInstance(preference.getKey());
        } else if (preference instanceof CustomListPreference) {
            dialogFragment = CustomListPreference.CustomListPreferenceDialogFragment.newInstance(preference.getKey());
        } else if (preference instanceof CustomDialogPreferenceCompat) {
            dialogFragment = CustomDialogPreferenceCompat.CustomPreferenceDialogFragment.newInstance(preference.getKey());
        } else if (preference instanceof CustomEditTextPreferenceCompat) {
            dialogFragment = CustomEditTextPreferenceCompat.CustomPreferenceDialogFragment.newInstance(preference.getKey());
        } else {
            super.onDisplayPreferenceDialog(preference);
            return;
        }
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getFragmentManager(), "dialog_preference");
        onDialogShowing();
    }

    public static class SettingsDialogFragment extends InstrumentedDialogFragment {
        private DialogInterface.OnCancelListener mOnCancelListener;
        private DialogInterface.OnDismissListener mOnDismissListener;
        private Fragment mParentFragment;

        public static SettingsDialogFragment newInstance(DialogCreatable dialogCreatable, int i) {
            if (dialogCreatable instanceof Fragment) {
                SettingsDialogFragment settingsDialogFragment = new SettingsDialogFragment();
                settingsDialogFragment.setParentFragment(dialogCreatable);
                settingsDialogFragment.setDialogId(i);
                return settingsDialogFragment;
            }
            throw new IllegalArgumentException("fragment argument must be an instance of " + Fragment.class.getName());
        }

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            Fragment fragment = this.mParentFragment;
            if (fragment == null) {
                return 0;
            }
            int dialogMetricsCategory = ((DialogCreatable) fragment).getDialogMetricsCategory(this.mDialogId);
            if (dialogMetricsCategory > 0) {
                return dialogMetricsCategory;
            }
            throw new IllegalStateException("Dialog must provide a metrics category");
        }

        @Override // androidx.fragment.app.Fragment, androidx.fragment.app.DialogFragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            if (this.mParentFragment != null) {
                bundle.putInt("key_dialog_id", this.mDialogId);
                bundle.putInt("key_parent_fragment_id", this.mParentFragment.getId());
            }
        }

        @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment
        public void onStart() {
            super.onStart();
            Fragment fragment = this.mParentFragment;
            if (fragment != null && (fragment instanceof SettingsPreferenceFragment)) {
                ((SettingsPreferenceFragment) fragment).onDialogShowing();
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            Object obj;
            if (bundle != null) {
                this.mDialogId = bundle.getInt("key_dialog_id", 0);
                this.mParentFragment = getParentFragment();
                int i = bundle.getInt("key_parent_fragment_id", -1);
                if (this.mParentFragment == null) {
                    this.mParentFragment = getFragmentManager().findFragmentById(i);
                }
                Fragment fragment = this.mParentFragment;
                if (!(fragment instanceof DialogCreatable)) {
                    StringBuilder sb = new StringBuilder();
                    Fragment fragment2 = this.mParentFragment;
                    if (fragment2 != null) {
                        obj = fragment2.getClass().getName();
                    } else {
                        obj = Integer.valueOf(i);
                    }
                    sb.append(obj);
                    sb.append(" must implement ");
                    sb.append(DialogCreatable.class.getName());
                    throw new IllegalArgumentException(sb.toString());
                } else if (fragment instanceof SettingsPreferenceFragment) {
                    ((SettingsPreferenceFragment) fragment).mDialogFragment = this;
                }
            }
            return ((DialogCreatable) this.mParentFragment).onCreateDialog(this.mDialogId);
        }

        @Override // androidx.fragment.app.DialogFragment
        public void onCancel(DialogInterface dialogInterface) {
            super.onCancel(dialogInterface);
            DialogInterface.OnCancelListener onCancelListener = this.mOnCancelListener;
            if (onCancelListener != null) {
                onCancelListener.onCancel(dialogInterface);
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public void onDismiss(DialogInterface dialogInterface) {
            super.onDismiss(dialogInterface);
            DialogInterface.OnDismissListener onDismissListener = this.mOnDismissListener;
            if (onDismissListener != null) {
                onDismissListener.onDismiss(dialogInterface);
            }
        }

        public int getDialogId() {
            return this.mDialogId;
        }

        @Override // androidx.fragment.app.Fragment, androidx.fragment.app.DialogFragment
        public void onDetach() {
            super.onDetach();
            Fragment fragment = this.mParentFragment;
            if ((fragment instanceof SettingsPreferenceFragment) && ((SettingsPreferenceFragment) fragment).mDialogFragment == this) {
                ((SettingsPreferenceFragment) this.mParentFragment).mDialogFragment = null;
            }
        }

        private void setParentFragment(DialogCreatable dialogCreatable) {
            this.mParentFragment = (Fragment) dialogCreatable;
        }

        private void setDialogId(int i) {
            this.mDialogId = i;
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasNextButton() {
        return ((ButtonBarHandler) getActivity()).hasNextButton();
    }

    /* access modifiers changed from: protected */
    public Button getNextButton() {
        return ((ButtonBarHandler) getActivity()).getNextButton();
    }

    public void finish() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                activity.finish();
            }
        }
    }

    /* access modifiers changed from: protected */
    public Intent getIntent() {
        if (getActivity() == null) {
            return null;
        }
        return getActivity().getIntent();
    }

    /* access modifiers changed from: protected */
    public void setResult(int i, Intent intent) {
        if (getActivity() != null) {
            getActivity().setResult(i, intent);
        }
    }

    /* access modifiers changed from: protected */
    public void setResult(int i) {
        if (getActivity() != null) {
            getActivity().setResult(i);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isFinishingOrDestroyed() {
        FragmentActivity activity = getActivity();
        return activity == null || activity.isFinishing() || activity.isDestroyed();
    }
}
