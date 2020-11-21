package com.android.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.SubscriptionsPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.WifiConnectionPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class MultiNetworkHeaderController extends BasePreferenceController implements WifiConnectionPreferenceController.UpdateListener, SubscriptionsPreferenceController.UpdateListener {
    public static final String TAG = "MultiNetworkHdrCtrl";
    private int mOriginalExpandedChildrenCount;
    private PreferenceCategory mPreferenceCategory;
    private PreferenceScreen mPreferenceScreen;
    private SubscriptionsPreferenceController mSubscriptionsController;
    private WifiConnectionPreferenceController mWifiController;

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

    public MultiNetworkHeaderController(Context context, String str) {
        super(context, str);
    }

    public void init(Lifecycle lifecycle) {
        this.mWifiController = createWifiController(lifecycle);
        this.mSubscriptionsController = createSubscriptionsController(lifecycle);
    }

    /* access modifiers changed from: package-private */
    public WifiConnectionPreferenceController createWifiController(Lifecycle lifecycle) {
        return new WifiConnectionPreferenceController(this.mContext, lifecycle, this, this.mPreferenceKey, 0, 746);
    }

    /* access modifiers changed from: package-private */
    public SubscriptionsPreferenceController createSubscriptionsController(Lifecycle lifecycle) {
        return new SubscriptionsPreferenceController(this.mContext, lifecycle, this, this.mPreferenceKey, 10);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        this.mOriginalExpandedChildrenCount = preferenceScreen.getInitialExpandedChildrenCount();
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(this.mPreferenceKey);
        this.mPreferenceCategory = preferenceCategory;
        preferenceCategory.setVisible(isAvailable());
        this.mWifiController.displayPreference(preferenceScreen);
        this.mSubscriptionsController.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        SubscriptionsPreferenceController subscriptionsPreferenceController = this.mSubscriptionsController;
        return (subscriptionsPreferenceController == null || !subscriptionsPreferenceController.isAvailable()) ? 2 : 0;
    }

    @Override // com.android.settings.wifi.WifiConnectionPreferenceController.UpdateListener, com.android.settings.network.SubscriptionsPreferenceController.UpdateListener
    public void onChildrenUpdated() {
        boolean isAvailable = isAvailable();
        int i = this.mOriginalExpandedChildrenCount;
        if (i != Integer.MAX_VALUE) {
            if (isAvailable) {
                this.mPreferenceScreen.setInitialExpandedChildrenCount(i + this.mPreferenceCategory.getPreferenceCount());
            } else {
                this.mPreferenceScreen.setInitialExpandedChildrenCount(i);
            }
        }
        this.mPreferenceCategory.setVisible(isAvailable);
    }
}
