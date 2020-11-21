package com.android.settings.wifi.savedaccesspoints2;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0012R$layout;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.wifi.WifiEntryPreference;
import com.android.wifitrackerlib.WifiEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SavedAccessPointsPreferenceController2 extends BasePreferenceController implements Preference.OnPreferenceClickListener {
    private SavedAccessPointsWifiSettings2 mHost;
    private PreferenceGroup mPreferenceGroup;
    List<WifiEntry> mWifiEntries = new ArrayList();

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

    public SavedAccessPointsPreferenceController2(Context context, String str) {
        super(context, str);
    }

    public SavedAccessPointsPreferenceController2 setHost(SavedAccessPointsWifiSettings2 savedAccessPointsWifiSettings2) {
        this.mHost = savedAccessPointsWifiSettings2;
        return this;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mWifiEntries.size() > 0 ? 0 : 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
        updatePreference();
        super.displayPreference(preferenceScreen);
    }

    /* access modifiers changed from: package-private */
    public void displayPreference(PreferenceScreen preferenceScreen, List<WifiEntry> list) {
        if (list == null || list.isEmpty()) {
            this.mWifiEntries.clear();
        } else {
            this.mWifiEntries = list;
        }
        displayPreference(preferenceScreen);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        SavedAccessPointsWifiSettings2 savedAccessPointsWifiSettings2 = this.mHost;
        if (savedAccessPointsWifiSettings2 == null) {
            return false;
        }
        savedAccessPointsWifiSettings2.showWifiPage(preference.getKey(), preference.getTitle());
        return false;
    }

    private void updatePreference() {
        ArrayList<String> arrayList = new ArrayList();
        int preferenceCount = this.mPreferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            String key = this.mPreferenceGroup.getPreference(i).getKey();
            if (this.mWifiEntries.stream().filter(new Predicate(key) {
                /* class com.android.settings.wifi.savedaccesspoints2.$$Lambda$SavedAccessPointsPreferenceController2$2ZyDB4u15Hj2Ajk1kfPLKHP4cU */
                public final /* synthetic */ String f$0;

                {
                    this.f$0 = r1;
                }

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return SavedAccessPointsPreferenceController2.lambda$updatePreference$0(this.f$0, (WifiEntry) obj);
                }
            }).count() == 0) {
                arrayList.add(key);
            }
        }
        for (String str : arrayList) {
            PreferenceGroup preferenceGroup = this.mPreferenceGroup;
            preferenceGroup.removePreference(preferenceGroup.findPreference(str));
        }
        for (WifiEntry wifiEntry : this.mWifiEntries) {
            if (this.mPreferenceGroup.findPreference(wifiEntry.getKey()) == null) {
                WifiEntryPreference wifiEntryPreference = new WifiEntryPreference(this.mContext, wifiEntry);
                wifiEntryPreference.setLayoutResource(C0012R$layout.op_preference_access_point);
                wifiEntryPreference.setIcon((Drawable) null);
                wifiEntryPreference.setKey(wifiEntry.getKey());
                wifiEntryPreference.setOnPreferenceClickListener(this);
                this.mPreferenceGroup.addPreference(wifiEntryPreference);
            }
        }
    }
}
