package com.android.settings.notification;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RoutingSessionInfo;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.media.LocalMediaManager;
import com.android.settingslib.media.MediaDevice;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.List;

public class RemoteVolumeGroupController extends BasePreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver, OnDestroy, LocalMediaManager.DeviceCallback {
    private static final String KEY_REMOTE_VOLUME_GROUP = "remote_media_group";
    static final String SWITCHER_PREFIX = "OUTPUT_SWITCHER";
    private static final String TAG = "RemoteVolumePrefCtr";
    LocalMediaManager mLocalMediaManager;
    private PreferenceCategory mPreferenceCategory;
    private List<RoutingSessionInfo> mRoutingSessionInfos = new ArrayList();

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

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY_REMOTE_VOLUME_GROUP;
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

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public /* bridge */ /* synthetic */ void onDeviceAttributesChanged() {
        super.onDeviceAttributesChanged();
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public /* bridge */ /* synthetic */ void onRequestFailed(int i) {
        super.onRequestFailed(i);
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onSelectedDeviceStateChanged(MediaDevice mediaDevice, int i) {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public RemoteVolumeGroupController(Context context, String str) {
        super(context, str);
        if (this.mLocalMediaManager == null) {
            LocalMediaManager localMediaManager = new LocalMediaManager(this.mContext, null, null);
            this.mLocalMediaManager = localMediaManager;
            localMediaManager.registerCallback(this);
            this.mLocalMediaManager.startScan();
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mRoutingSessionInfos.isEmpty() ? 2 : 1;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        initRemoteMediaSession();
        refreshPreference();
    }

    private void initRemoteMediaSession() {
        this.mRoutingSessionInfos.clear();
        for (RoutingSessionInfo routingSessionInfo : this.mLocalMediaManager.getActiveMediaSession()) {
            if (!routingSessionInfo.isSystemSession()) {
                this.mRoutingSessionInfos.add(routingSessionInfo);
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        this.mLocalMediaManager.unregisterCallback(this);
        this.mLocalMediaManager.stopScan();
    }

    private void refreshPreference() {
        this.mPreferenceCategory.removeAll();
        if (!isAvailable()) {
            this.mPreferenceCategory.setVisible(false);
            return;
        }
        CharSequence text = this.mContext.getText(C0017R$string.remote_media_volume_option_title);
        this.mPreferenceCategory.setVisible(true);
        for (RoutingSessionInfo routingSessionInfo : this.mRoutingSessionInfos) {
            if (this.mPreferenceCategory.findPreference(routingSessionInfo.getId()) == null) {
                Context context = this.mContext;
                String string = context.getString(C0017R$string.media_output_label_title, Utils.getApplicationLabel(context, routingSessionInfo.getClientPackageName()));
                RemoteVolumeSeekBarPreference remoteVolumeSeekBarPreference = new RemoteVolumeSeekBarPreference(this.mContext);
                remoteVolumeSeekBarPreference.setKey(routingSessionInfo.getId());
                remoteVolumeSeekBarPreference.setTitle(text);
                remoteVolumeSeekBarPreference.setMax(routingSessionInfo.getVolumeMax());
                remoteVolumeSeekBarPreference.setProgress(routingSessionInfo.getVolume());
                remoteVolumeSeekBarPreference.setMin(0);
                remoteVolumeSeekBarPreference.setOnPreferenceChangeListener(this);
                remoteVolumeSeekBarPreference.setIcon(C0008R$drawable.ic_volume_remote);
                this.mPreferenceCategory.addPreference(remoteVolumeSeekBarPreference);
                Preference preference = new Preference(this.mContext);
                preference.setKey(SWITCHER_PREFIX + routingSessionInfo.getId());
                preference.setTitle(string);
                preference.setSummary(routingSessionInfo.getName());
                this.mPreferenceCategory.addPreference(preference);
            }
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        ThreadUtils.postOnBackgroundThread(new Runnable(preference, obj) {
            /* class com.android.settings.notification.$$Lambda$RemoteVolumeGroupController$_Wuw9wxpBAMoSWAFsevVleauuiA */
            public final /* synthetic */ Preference f$1;
            public final /* synthetic */ Object f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                RemoteVolumeGroupController.this.lambda$onPreferenceChange$0$RemoteVolumeGroupController(this.f$1, this.f$2);
            }
        });
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onPreferenceChange$0 */
    public /* synthetic */ void lambda$onPreferenceChange$0$RemoteVolumeGroupController(Preference preference, Object obj) {
        this.mLocalMediaManager.adjustSessionVolume(preference.getKey(), ((Integer) obj).intValue());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!preference.getKey().startsWith(SWITCHER_PREFIX)) {
            return false;
        }
        for (RoutingSessionInfo routingSessionInfo : this.mRoutingSessionInfos) {
            if (TextUtils.equals(routingSessionInfo.getId(), preference.getKey().substring(15))) {
                this.mContext.startActivity(new Intent().setAction("com.android.settings.panel.action.MEDIA_OUTPUT").setFlags(268435456).putExtra("com.android.settings.panel.extra.PACKAGE_NAME", routingSessionInfo.getClientPackageName()));
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settingslib.media.LocalMediaManager.DeviceCallback
    public void onDeviceListUpdate(List<MediaDevice> list) {
        if (this.mPreferenceCategory != null) {
            initRemoteMediaSession();
            refreshPreference();
        }
    }
}
