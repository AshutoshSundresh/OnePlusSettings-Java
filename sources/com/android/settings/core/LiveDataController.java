package com.android.settings.core;

import android.content.Context;
import android.content.IntentFilter;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.utils.ThreadUtils;

public abstract class LiveDataController extends BasePreferenceController {
    private MutableLiveData<CharSequence> mData = new MutableLiveData<>();
    private Preference mPreference;
    protected CharSequence mSummary;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    /* access modifiers changed from: protected */
    public abstract CharSequence getSummaryTextInBackground();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public LiveDataController(Context context, String str) {
        super(context, str);
        this.mSummary = context.getText(C0017R$string.summary_placeholder);
    }

    public void initLifeCycleOwner(Fragment fragment) {
        this.mData.observe(fragment, new Observer() {
            /* class com.android.settings.core.$$Lambda$LiveDataController$e3zriOUtyliwAiGcaFttoGEvO9k */

            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                LiveDataController.this.lambda$initLifeCycleOwner$0$LiveDataController((CharSequence) obj);
            }
        });
        ThreadUtils.postOnBackgroundThread(new Runnable() {
            /* class com.android.settings.core.$$Lambda$LiveDataController$a7UiE6cDWRF1rLzi6ELqIjCHNo */

            public final void run() {
                LiveDataController.this.lambda$initLifeCycleOwner$1$LiveDataController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initLifeCycleOwner$0 */
    public /* synthetic */ void lambda$initLifeCycleOwner$0$LiveDataController(CharSequence charSequence) {
        this.mSummary = charSequence;
        refreshSummary(this.mPreference);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initLifeCycleOwner$1 */
    public /* synthetic */ void lambda$initLifeCycleOwner$1$LiveDataController() {
        this.mData.postValue(getSummaryTextInBackground());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mSummary;
    }
}
