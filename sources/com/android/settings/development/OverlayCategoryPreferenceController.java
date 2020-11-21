package com.android.settings.development;

import android.content.Context;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OverlayCategoryPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private static final Comparator<OverlayInfo> OVERLAY_INFO_COMPARATOR = Comparator.comparingInt($$Lambda$OverlayCategoryPreferenceController$RCMrfsrPVQZYqDXvYOMB7C2Md8.INSTANCE);
    static final String PACKAGE_DEVICE_DEFAULT = "package_device_default";
    private final boolean mAvailable;
    private final String mCategory;
    private final IOverlayManager mOverlayManager;
    private final PackageManager mPackageManager;
    private ListPreference mPreference;

    OverlayCategoryPreferenceController(Context context, PackageManager packageManager, IOverlayManager iOverlayManager, String str) {
        super(context);
        this.mOverlayManager = iOverlayManager;
        this.mPackageManager = packageManager;
        this.mCategory = str;
        this.mAvailable = iOverlayManager != null && !getOverlayInfos().isEmpty();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public boolean isAvailable() {
        return this.mAvailable;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mCategory;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        setPreference((ListPreference) preferenceScreen.findPreference(getPreferenceKey()));
    }

    /* access modifiers changed from: package-private */
    public void setPreference(ListPreference listPreference) {
        this.mPreference = listPreference;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return setOverlay((String) obj);
    }

    private boolean setOverlay(final String str) {
        final R orElse = getOverlayInfos().stream().filter($$Lambda$OverlayCategoryPreferenceController$ImUcEZADd2hoyz2UKfsCeptUZ30.INSTANCE).map($$Lambda$OverlayCategoryPreferenceController$gEnSn3XadWaJp8bOqxIifKyFdko.INSTANCE).findFirst().orElse(null);
        if ((PACKAGE_DEVICE_DEFAULT.equals(str) && TextUtils.isEmpty(orElse)) || TextUtils.equals(str, orElse)) {
            return true;
        }
        new AsyncTask<Void, Void, Boolean>() {
            /* class com.android.settings.development.OverlayCategoryPreferenceController.AnonymousClass1 */

            /* access modifiers changed from: protected */
            public Boolean doInBackground(Void... voidArr) {
                try {
                    if (OverlayCategoryPreferenceController.PACKAGE_DEVICE_DEFAULT.equals(str)) {
                        return Boolean.valueOf(OverlayCategoryPreferenceController.this.mOverlayManager.setEnabled(orElse, false, 0));
                    }
                    return Boolean.valueOf(OverlayCategoryPreferenceController.this.mOverlayManager.setEnabledExclusiveInCategory(str, 0));
                } catch (RemoteException | IllegalStateException | SecurityException e) {
                    Log.w("OverlayCategoryPC", "Error enabling overlay.", e);
                    return Boolean.FALSE;
                }
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Boolean bool) {
                OverlayCategoryPreferenceController overlayCategoryPreferenceController = OverlayCategoryPreferenceController.this;
                overlayCategoryPreferenceController.updateState(overlayCategoryPreferenceController.mPreference);
                if (!bool.booleanValue()) {
                    Toast.makeText(((AbstractPreferenceController) OverlayCategoryPreferenceController.this).mContext, C0017R$string.overlay_toast_failed_to_apply, 1).show();
                }
            }
        }.execute(new Void[0]);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        String string = this.mContext.getString(C0017R$string.overlay_option_device_default);
        String str = PACKAGE_DEVICE_DEFAULT;
        arrayList.add(str);
        arrayList2.add(string);
        for (OverlayInfo overlayInfo : getOverlayInfos()) {
            arrayList.add(overlayInfo.packageName);
            try {
                arrayList2.add(this.mPackageManager.getApplicationInfo(overlayInfo.packageName, 0).loadLabel(this.mPackageManager).toString());
            } catch (PackageManager.NameNotFoundException unused) {
                arrayList2.add(overlayInfo.packageName);
            }
            if (overlayInfo.isEnabled()) {
                String str2 = (String) arrayList2.get(arrayList2.size() - 1);
                str = (String) arrayList.get(arrayList.size() - 1);
                string = str2;
            }
        }
        this.mPreference.setEntries((CharSequence[]) arrayList2.toArray(new String[arrayList2.size()]));
        this.mPreference.setEntryValues((CharSequence[]) arrayList.toArray(new String[arrayList.size()]));
        this.mPreference.setValue(str);
        this.mPreference.setSummary(string);
    }

    private List<OverlayInfo> getOverlayInfos() {
        ArrayList arrayList = new ArrayList();
        try {
            for (OverlayInfo overlayInfo : this.mOverlayManager.getOverlayInfosForTarget("android", 0)) {
                if (this.mCategory.equals(overlayInfo.category)) {
                    arrayList.add(overlayInfo);
                }
            }
            arrayList.sort(OVERLAY_INFO_COMPARATOR);
            return arrayList;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        setOverlay(PACKAGE_DEVICE_DEFAULT);
        updateState(this.mPreference);
    }
}
