package com.android.settings.network.telephony;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.dashboard.RestrictedDashboardFragment;
import com.android.settings.network.telephony.TelephonyStatusControlSession;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

abstract class AbstractMobileNetworkSettings extends RestrictedDashboardFragment {
    private List<AbstractPreferenceController> mHiddenControllerList = new ArrayList();
    private boolean mIsRedrawRequired;

    AbstractMobileNetworkSettings(String str) {
        super(str);
    }

    /* access modifiers changed from: package-private */
    public List<AbstractPreferenceController> getPreferenceControllersAsList() {
        ArrayList arrayList = new ArrayList();
        getPreferenceControllers().forEach(new Consumer(arrayList) {
            /* class com.android.settings.network.telephony.$$Lambda$AbstractMobileNetworkSettings$Pq1caXnhffD6nVOEzsL1irxJxg */
            public final /* synthetic */ List f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.addAll((List) obj);
            }
        });
        return arrayList;
    }

    /* access modifiers changed from: package-private */
    public Preference searchForPreference(PreferenceScreen preferenceScreen, AbstractPreferenceController abstractPreferenceController) {
        String preferenceKey = abstractPreferenceController.getPreferenceKey();
        if (TextUtils.isEmpty(preferenceKey)) {
            return null;
        }
        return preferenceScreen.findPreference(preferenceKey);
    }

    /* access modifiers changed from: package-private */
    public TelephonyStatusControlSession setTelephonyAvailabilityStatus(Collection<AbstractPreferenceController> collection) {
        return new TelephonyStatusControlSession.Builder(collection).build();
    }

    @Override // androidx.preference.PreferenceGroup.OnExpandButtonClickListener, com.android.settings.dashboard.DashboardFragment
    public void onExpandButtonClick() {
        this.mHiddenControllerList.stream().filter($$Lambda$AbstractMobileNetworkSettings$bo6YOG7aYe8ttG4vhoq1luq3stY.INSTANCE).forEach(new Consumer() {
            /* class com.android.settings.network.telephony.$$Lambda$AbstractMobileNetworkSettings$OSqqtYmfC1bCckWQ6N0SQ1RcLlo */

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AbstractPreferenceController abstractPreferenceController;
                abstractPreferenceController.updateState(PreferenceScreen.this.findPreference(((AbstractPreferenceController) obj).getPreferenceKey()));
            }
        });
        super.onExpandButtonClick();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public void updatePreferenceStates() {
        this.mHiddenControllerList.clear();
        if (this.mIsRedrawRequired) {
            redrawPreferenceControllers();
            return;
        }
        getPreferenceControllersAsList().forEach(new Consumer(getPreferenceScreen()) {
            /* class com.android.settings.network.telephony.$$Lambda$AbstractMobileNetworkSettings$m5ehCoz_h9df7IWByDb_ijDU4wU */
            public final /* synthetic */ PreferenceScreen f$1;

            {
                this.f$1 = r2;
            }

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AbstractMobileNetworkSettings.this.lambda$updatePreferenceStates$3$AbstractMobileNetworkSettings(this.f$1, (AbstractPreferenceController) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: updateVisiblePreferenceControllers */
    public void lambda$updatePreferenceStates$3(PreferenceScreen preferenceScreen, AbstractPreferenceController abstractPreferenceController) {
        Preference searchForPreference = searchForPreference(preferenceScreen, abstractPreferenceController);
        if (searchForPreference != null) {
            if (!isPreferenceExpanded(searchForPreference)) {
                this.mHiddenControllerList.add(abstractPreferenceController);
            } else if (abstractPreferenceController.isAvailable()) {
                abstractPreferenceController.updateState(searchForPreference);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void redrawPreferenceControllers() {
        this.mHiddenControllerList.clear();
        if (!isResumed()) {
            this.mIsRedrawRequired = true;
            return;
        }
        this.mIsRedrawRequired = false;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        List<AbstractPreferenceController> preferenceControllersAsList = getPreferenceControllersAsList();
        TelephonyStatusControlSession telephonyAvailabilityStatus = setTelephonyAvailabilityStatus(preferenceControllersAsList);
        preferenceControllersAsList.forEach(new Consumer(getPreferenceScreen()) {
            /* class com.android.settings.network.telephony.$$Lambda$AbstractMobileNetworkSettings$asImtwOFBrFw_KxXxr9JqQzNra8 */
            public final /* synthetic */ PreferenceScreen f$1;

            {
                this.f$1 = r2;
            }

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AbstractMobileNetworkSettings.this.lambda$redrawPreferenceControllers$4$AbstractMobileNetworkSettings(this.f$1, (AbstractPreferenceController) obj);
            }
        });
        long elapsedRealtime2 = SystemClock.elapsedRealtime();
        Log.d("AbsNetworkSettings", "redraw fragment: +" + (elapsedRealtime2 - elapsedRealtime) + "ms");
        telephonyAvailabilityStatus.close();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$redrawPreferenceControllers$4 */
    public /* synthetic */ void lambda$redrawPreferenceControllers$4$AbstractMobileNetworkSettings(PreferenceScreen preferenceScreen, AbstractPreferenceController abstractPreferenceController) {
        abstractPreferenceController.displayPreference(preferenceScreen);
        lambda$updatePreferenceStates$3(preferenceScreen, abstractPreferenceController);
    }
}
