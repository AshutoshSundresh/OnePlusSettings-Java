package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceScreen;
import com.android.settings.network.PreferredNetworkModeContentObserver;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.PreferenceCategoryController;

public class NetworkPreferenceCategoryController extends PreferenceCategoryController implements LifecycleObserver {
    private PreferenceScreen mPreferenceScreen;
    private PreferredNetworkModeContentObserver mPreferredNetworkModeObserver;
    protected int mSubId = -1;

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public NetworkPreferenceCategoryController(Context context, String str) {
        super(context, str);
        PreferredNetworkModeContentObserver preferredNetworkModeContentObserver = new PreferredNetworkModeContentObserver(new Handler(Looper.getMainLooper()));
        this.mPreferredNetworkModeObserver = preferredNetworkModeContentObserver;
        preferredNetworkModeContentObserver.setPreferredNetworkModeChangedListener(new PreferredNetworkModeContentObserver.OnPreferredNetworkModeChangedListener() {
            /* class com.android.settings.network.telephony.$$Lambda$NetworkPreferenceCategoryController$R8C3sZaGpLWj6AavQAX4ucgV0I */

            @Override // com.android.settings.network.PreferredNetworkModeContentObserver.OnPreferredNetworkModeChangedListener
            public final void onPreferredNetworkModeChanged() {
                NetworkPreferenceCategoryController.this.lambda$new$0$NetworkPreferenceCategoryController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: updatePreference */
    public void lambda$new$0() {
        displayPreference(this.mPreferenceScreen);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mPreferredNetworkModeObserver.register(this.mContext, this.mSubId);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mPreferredNetworkModeObserver.unregister(this.mContext);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
    }

    public NetworkPreferenceCategoryController init(Lifecycle lifecycle, int i) {
        this.mSubId = i;
        lifecycle.addObserver(this);
        return this;
    }
}
