package com.oneplus.settings;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.RadioButtonPreference;
import com.android.settingslib.display.DisplayDensityUtils;
import com.oneplus.settings.utils.OPApplicationUtils;
import com.oneplus.settings.utils.OPDisplayDensityUtils;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPScreenResolutionAdjust extends SettingsPreferenceFragment implements RadioButtonPreference.OnClickListener, Preference.OnPreferenceChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.OPScreenResolutionAdjust.AnonymousClass4 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            if (OPUtils.isSupportMultiScreenResolution(context) && !OPUtils.isGuestMode()) {
                searchIndexableResource.xmlResId = C0019R$xml.op_screen_resolution_adjust_select;
            }
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            ArrayList arrayList = new ArrayList();
            if (TextUtils.equals("19811", SystemProperties.get("ro.boot.project_name"))) {
                arrayList.add("op_other_resolution_mode");
                arrayList.add("op_1080p_mode");
            } else {
                arrayList.add("op_other_resolution_mode_19811");
                arrayList.add("op_1080p_mode_19811");
            }
            return arrayList;
        }
    };
    private RadioButtonPreference m1080PMode;
    private RadioButtonPreference m1080PMode19811;
    private ActivityManager mAm;
    private Context mContext;
    private int[] mDpiValues1080P = {420, 450, 480, 510, 540};
    private int[] mDpiValuesOther;
    private int mEnterValue;
    private SwitchPreference mIntelligentSwitchResolutionMode;
    private RadioButtonPreference mOtherResolutionMode;
    private RadioButtonPreference mOtherResolutionMode19811;
    private PreferenceCategory mQHDSettingsCatagory;
    private AlertDialog mWarnDialog;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    public OPScreenResolutionAdjust() {
        new Handler();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_screen_resolution_adjust_select);
        Application application = SettingsBaseApplication.mApplication;
        this.mContext = application;
        this.mDpiValues1080P = OPDisplayDensityUtils.get1080Dpi(application);
        this.mAm = (ActivityManager) getSystemService("activity");
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("op_qhd_settings");
        this.mQHDSettingsCatagory = preferenceCategory;
        preferenceCategory.setTitle(this.mContext.getResources().getString(C0017R$string.oneplus_screen_resolution_adjust_other) + " " + this.mContext.getResources().getString(C0017R$string.settings_label));
        this.mIntelligentSwitchResolutionMode = (SwitchPreference) findPreference("op_intelligent_switch_resolution_mode");
        this.mOtherResolutionMode = (RadioButtonPreference) findPreference("op_other_resolution_mode");
        this.m1080PMode = (RadioButtonPreference) findPreference("op_1080p_mode");
        this.mOtherResolutionMode19811 = (RadioButtonPreference) findPreference("op_other_resolution_mode_19811");
        this.m1080PMode19811 = (RadioButtonPreference) findPreference("op_1080p_mode_19811");
        this.mIntelligentSwitchResolutionMode.setOnPreferenceChangeListener(this);
        this.mOtherResolutionMode.setOnClickListener(this);
        this.m1080PMode.setOnClickListener(this);
        this.m1080PMode19811.setOnClickListener(this);
        this.mOtherResolutionMode19811.setOnClickListener(this);
        getQHDScreenSummary();
        getFHDScreenSummary();
        this.mDpiValuesOther = this.mContext.getResources().getIntArray(C0003R$array.oneplus_screen_dpi_values);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        boolean z = false;
        if (getActivity().isInMultiWindowMode()) {
            this.mIntelligentSwitchResolutionMode.setEnabled(false);
            this.mOtherResolutionMode.setEnabled(false);
            this.mOtherResolutionMode19811.setEnabled(false);
            this.m1080PMode.setEnabled(false);
            this.m1080PMode19811.setEnabled(false);
        }
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "oneplus_screen_resolution_adjust", 2);
        int i2 = Settings.Global.getInt(this.mContext.getContentResolver(), "oneplus_screen_resolution_auto_adjust", 0);
        this.mOtherResolutionMode.setChecked(i == 0 || i == 2);
        this.mIntelligentSwitchResolutionMode.setChecked(i2 == 1 || i == 2);
        this.mOtherResolutionMode19811.setChecked(i == 0 || i == 2);
        this.m1080PMode.setChecked(i == 1);
        RadioButtonPreference radioButtonPreference = this.m1080PMode19811;
        if (i == 1) {
            z = true;
        }
        radioButtonPreference.setChecked(z);
        this.mEnterValue = Settings.Global.getInt(this.mContext.getContentResolver(), "oneplus_screen_resolution_adjust", 2);
        if (i == 1) {
            getPreferenceScreen().removePreference(this.mQHDSettingsCatagory);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onMultiWindowModeChanged(boolean z) {
        super.onMultiWindowModeChanged(z);
        if (!z) {
            this.mIntelligentSwitchResolutionMode.setEnabled(true);
            this.mOtherResolutionMode.setEnabled(true);
            this.mOtherResolutionMode19811.setEnabled(true);
            this.m1080PMode.setEnabled(true);
            this.m1080PMode19811.setEnabled(true);
        }
    }

    @Override // com.android.settings.widget.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        if (radioButtonPreference == this.mOtherResolutionMode || radioButtonPreference == this.mOtherResolutionMode19811) {
            if (!isOtherMode()) {
                showWarnigDialog(0);
            }
        } else if ((radioButtonPreference == this.m1080PMode || radioButtonPreference == this.m1080PMode19811) && !is1080pMode()) {
            showWarnigDialog(1);
        }
    }

    private void openSurfaceComposerInterface() {
        IBinder service = ServiceManager.getService("SurfaceFlinger");
        if (service != null) {
            try {
                Parcel obtain = Parcel.obtain();
                obtain.writeInterfaceToken("android.ui.ISurfaceComposer");
                obtain.writeInt(1);
                service.transact(1008, obtain, null, 0);
                obtain.recycle();
            } catch (RemoteException unused) {
            }
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!"op_intelligent_switch_resolution_mode".equals(preference.getKey())) {
            return true;
        }
        Boolean bool = (Boolean) obj;
        Settings.Global.putInt(this.mContext.getContentResolver(), "oneplus_screen_resolution_auto_adjust", bool.booleanValue() ? 1 : 0);
        changeScreenResolution(bool.booleanValue() ? 2 : 0);
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void changeScreenResolution(int i) {
        int i2 = 2;
        int i3 = Settings.Global.getInt(this.mContext.getContentResolver(), "oneplus_screen_resolution_adjust", 2);
        if (i == 2) {
            this.mIntelligentSwitchResolutionMode.setChecked(true);
            this.mOtherResolutionMode.setChecked(true);
            this.mOtherResolutionMode19811.setChecked(true);
            this.m1080PMode.setChecked(false);
            this.m1080PMode19811.setChecked(false);
            if (i3 == 1) {
                openSurfaceComposerInterface();
            }
            if (is1080pMode()) {
                DisplayDensityUtils.setForcedDisplayDensity(0, this.mDpiValuesOther[getCurrent1080pDpiIndex()]);
            }
            Settings.Global.putInt(this.mContext.getContentResolver(), "oneplus_screen_resolution_adjust", 2);
            if (i3 == 1) {
                OPApplicationUtils.forceStopPackage(this.mAm);
            }
        } else if (i == 0) {
            this.mOtherResolutionMode.setChecked(true);
            this.mOtherResolutionMode19811.setChecked(true);
            this.m1080PMode.setChecked(false);
            this.m1080PMode19811.setChecked(false);
            if (i3 == 1) {
                openSurfaceComposerInterface();
            }
            if (is1080pMode()) {
                DisplayDensityUtils.setForcedDisplayDensity(0, this.mDpiValuesOther[getCurrent1080pDpiIndex()]);
            }
            getPreferenceScreen().addPreference(this.mQHDSettingsCatagory);
            int i4 = Settings.Global.getInt(this.mContext.getContentResolver(), "oneplus_screen_resolution_auto_adjust", 0);
            ContentResolver contentResolver = this.mContext.getContentResolver();
            if (i4 != 1) {
                i2 = 0;
            }
            Settings.Global.putInt(contentResolver, "oneplus_screen_resolution_adjust", i2);
            if (i3 == 1) {
                OPApplicationUtils.forceStopPackage(this.mAm);
            }
        } else if (i == 1) {
            this.mOtherResolutionMode.setChecked(false);
            this.mOtherResolutionMode19811.setChecked(false);
            this.m1080PMode.setChecked(true);
            this.m1080PMode19811.setChecked(true);
            openSurfaceComposerInterface();
            if (!is1080pMode()) {
                DisplayDensityUtils.setForcedDisplayDensity(0, this.mDpiValues1080P[getCurrentOtherDpiIndex()]);
            }
            getPreferenceScreen().removePreference(this.mQHDSettingsCatagory);
            Settings.Global.putInt(this.mContext.getContentResolver(), "oneplus_screen_resolution_adjust", 1);
            OPApplicationUtils.forceStopPackage(this.mAm);
        }
        OPApplicationUtils.killProcess(this.mAm, false);
    }

    public void showWarnigDialog(final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0017R$string.oneplus_switch_resolution_kill_process_tips);
        builder.setPositiveButton(C0017R$string.oneplus_switch_resolution_kill_process_confirm, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.OPScreenResolutionAdjust.AnonymousClass2 */

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                OPScreenResolutionAdjust.this.changeScreenResolution(i);
            }
        });
        builder.setNegativeButton(C0017R$string.cancel, new DialogInterface.OnClickListener(this) {
            /* class com.oneplus.settings.OPScreenResolutionAdjust.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog create = builder.create();
        this.mWarnDialog = create;
        create.show();
    }

    private boolean isOtherMode() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "oneplus_screen_resolution_adjust", 2) == 0;
    }

    private boolean is1080pMode() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "oneplus_screen_resolution_adjust", 2) == 1;
    }

    private int getCurrent1080pDpiIndex() {
        String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "display_density_forced", -2);
        if (TextUtils.isEmpty(stringForUser)) {
            return 1;
        }
        int i = 0;
        while (true) {
            int[] iArr = this.mDpiValues1080P;
            if (i >= iArr.length) {
                return 0;
            }
            if (stringForUser.equals(String.valueOf(iArr[i]))) {
                return i;
            }
            i++;
        }
    }

    private int getCurrentOtherDpiIndex() {
        String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "display_density_forced", -2);
        if (TextUtils.isEmpty(stringForUser)) {
            return 1;
        }
        int i = 0;
        while (true) {
            int[] iArr = this.mDpiValuesOther;
            if (i >= iArr.length) {
                return 0;
            }
            if (stringForUser.equals(String.valueOf(iArr[i]))) {
                return i;
            }
            i++;
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        super.onStop();
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "oneplus_screen_resolution_adjust", 2);
        if (this.mEnterValue == i) {
            return;
        }
        if (i == 2) {
            OPUtils.sendAnalytics("resolution", "status", "1");
        } else if (i == 0) {
            OPUtils.sendAnalytics("resolution", "status", "2");
        } else if (i == 1) {
            OPUtils.sendAnalytics("resolution", "status", OPMemberController.CLIENT_TYPE);
        }
    }

    private void getQHDScreenSummary() {
        if (TextUtils.equals("19811", SystemProperties.get("ro.boot.project_name"))) {
            this.mOtherResolutionMode.setVisible(false);
            this.mOtherResolutionMode19811.setVisible(true);
            return;
        }
        this.mOtherResolutionMode.setVisible(true);
        this.mOtherResolutionMode19811.setVisible(false);
    }

    private void getFHDScreenSummary() {
        if (TextUtils.equals("19811", SystemProperties.get("ro.boot.project_name"))) {
            this.m1080PMode.setVisible(false);
            this.m1080PMode19811.setVisible(true);
            return;
        }
        this.m1080PMode.setVisible(true);
        this.m1080PMode19811.setVisible(false);
    }
}
