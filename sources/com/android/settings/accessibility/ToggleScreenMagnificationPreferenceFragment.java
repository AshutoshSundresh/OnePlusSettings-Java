package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.CheckBox;
import androidx.appcompat.app.AlertDialog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.accessibility.ToggleFeaturePreferenceFragment;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class ToggleScreenMagnificationPreferenceFragment extends ToggleFeaturePreferenceFragment {
    private static final TextUtils.SimpleStringSplitter sStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
    private CheckBox mHardwareTypeCheckBox;
    private CheckBox mSoftwareTypeCheckBox;
    private AccessibilityManager.TouchExplorationStateChangeListener mTouchExplorationStateChangeListener;
    private CheckBox mTripleTapTypeCheckBox;
    private int mUserShortcutType = 0;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable, com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public int getMetricsCategory() {
        return 7;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getActivity().setTitle(C0017R$string.accessibility_screen_magnification_title);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mPackageName = getString(C0017R$string.accessibility_screen_magnification_title);
        this.mImageUri = new Uri.Builder().scheme("android.resource").authority(getPrefContext().getPackageName()).appendPath(String.valueOf(C0008R$drawable.accessibility_magnification_banner)).build();
        this.mTouchExplorationStateChangeListener = new AccessibilityManager.TouchExplorationStateChangeListener() {
            /* class com.android.settings.accessibility.$$Lambda$ToggleScreenMagnificationPreferenceFragment$pHpHStHkaUYYL1IxGd42qua0j8 */

            public final void onTouchExplorationStateChanged(boolean z) {
                ToggleScreenMagnificationPreferenceFragment.this.lambda$onCreateView$0$ToggleScreenMagnificationPreferenceFragment(z);
            }
        };
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateView$0 */
    public /* synthetic */ void lambda$onCreateView$0$ToggleScreenMagnificationPreferenceFragment(boolean z) {
        removeDialog(1);
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        initShortcutPreference();
        super.onViewCreated(view, bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("shortcut_type", this.mUserShortcutTypesCache);
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).addTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
        updateShortcutPreferenceData();
        updateShortcutPreference();
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).removeTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
        super.onPause();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 1001) {
            AlertDialog showMagnificationEditShortcutDialog = AccessibilityEditDialogUtils.showMagnificationEditShortcutDialog(getPrefContext(), getPrefContext().getString(C0017R$string.accessibility_shortcut_title, this.mPackageName), new DialogInterface.OnClickListener() {
                /* class com.android.settings.accessibility.$$Lambda$0zHTfWw0JY9APo2WKNhZM4FVDU */

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ToggleScreenMagnificationPreferenceFragment.this.callOnAlertDialogCheckboxClicked(dialogInterface, i);
                }
            });
            initializeDialogCheckBox(showMagnificationEditShortcutDialog);
            return showMagnificationEditShortcutDialog;
        } else if (i != 1007) {
            return super.onCreateDialog(i);
        } else {
            return AccessibilityGestureNavigationTutorial.showGestureNavigationTutorialDialog(getPrefContext());
        }
    }

    private void setDialogTextAreaClickListener(View view, CheckBox checkBox) {
        view.findViewById(C0010R$id.container).setOnClickListener(new View.OnClickListener(checkBox) {
            /* class com.android.settings.accessibility.$$Lambda$ToggleScreenMagnificationPreferenceFragment$CWkmiTS_kuVvoBiSVALJFtQ9Ng */
            public final /* synthetic */ CheckBox f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ToggleScreenMagnificationPreferenceFragment.this.lambda$setDialogTextAreaClickListener$1$ToggleScreenMagnificationPreferenceFragment(this.f$1, view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setDialogTextAreaClickListener$1 */
    public /* synthetic */ void lambda$setDialogTextAreaClickListener$1$ToggleScreenMagnificationPreferenceFragment(CheckBox checkBox, View view) {
        checkBox.toggle();
        updateUserShortcutType(false);
    }

    private void initializeDialogCheckBox(AlertDialog alertDialog) {
        View findViewById = alertDialog.findViewById(C0010R$id.software_shortcut);
        CheckBox checkBox = (CheckBox) findViewById.findViewById(C0010R$id.checkbox);
        this.mSoftwareTypeCheckBox = checkBox;
        setDialogTextAreaClickListener(findViewById, checkBox);
        View findViewById2 = alertDialog.findViewById(C0010R$id.hardware_shortcut);
        CheckBox checkBox2 = (CheckBox) findViewById2.findViewById(C0010R$id.checkbox);
        this.mHardwareTypeCheckBox = checkBox2;
        setDialogTextAreaClickListener(findViewById2, checkBox2);
        View findViewById3 = alertDialog.findViewById(C0010R$id.triple_tap_shortcut);
        CheckBox checkBox3 = (CheckBox) findViewById3.findViewById(C0010R$id.checkbox);
        this.mTripleTapTypeCheckBox = checkBox3;
        setDialogTextAreaClickListener(findViewById3, checkBox3);
        View findViewById4 = alertDialog.findViewById(C0010R$id.advanced_shortcut);
        updateAlertDialogCheckState();
        if (isWindowMagnification(getPrefContext())) {
            findViewById4.setVisibility(8);
        } else if (this.mTripleTapTypeCheckBox.isChecked()) {
            findViewById4.setVisibility(8);
            findViewById3.setVisibility(0);
        }
    }

    private void updateAlertDialogCheckState() {
        if (this.mUserShortcutTypesCache != 0) {
            updateCheckStatus(this.mSoftwareTypeCheckBox, 1);
            updateCheckStatus(this.mHardwareTypeCheckBox, 2);
            updateCheckStatus(this.mTripleTapTypeCheckBox, 4);
        }
    }

    private void updateCheckStatus(CheckBox checkBox, int i) {
        checkBox.setChecked((this.mUserShortcutTypesCache & i) == i);
    }

    private void updateUserShortcutType(boolean z) {
        boolean z2 = false;
        this.mUserShortcutTypesCache = 0;
        if (this.mSoftwareTypeCheckBox.isChecked()) {
            this.mUserShortcutTypesCache |= 1;
        }
        if (this.mHardwareTypeCheckBox.isChecked()) {
            this.mUserShortcutTypesCache |= 2;
        }
        if (this.mTripleTapTypeCheckBox.isChecked()) {
            this.mUserShortcutTypesCache |= 4;
        }
        if (z) {
            if (this.mUserShortcutTypesCache != 0) {
                z2 = true;
            }
            if (z2) {
                setUserShortcutType(getPrefContext(), this.mUserShortcutTypesCache);
            }
            this.mUserShortcutType = this.mUserShortcutTypesCache;
        }
    }

    private void setUserShortcutType(Context context, int i) {
        Set userShortcutTypes = SharedPreferenceUtils.getUserShortcutTypes(context);
        if (userShortcutTypes.isEmpty()) {
            userShortcutTypes = new HashSet();
        } else {
            userShortcutTypes.removeAll((Set) userShortcutTypes.stream().filter($$Lambda$ToggleScreenMagnificationPreferenceFragment$NMbh9Wncrv2kTRebrthNrEGXDE.INSTANCE).collect(Collectors.toSet()));
        }
        userShortcutTypes.add(new ToggleFeaturePreferenceFragment.AccessibilityUserShortcutType("com.android.server.accessibility.MagnificationController", i).flattenToString());
        SharedPreferenceUtils.setUserShortcutType(context, userShortcutTypes);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public CharSequence getShortcutTypeSummary(Context context) {
        if (!this.mShortcutPreference.isChecked()) {
            return context.getText(C0017R$string.switch_off_text);
        }
        int userShortcutTypes = getUserShortcutTypes(context, 0);
        int i = C0017R$string.accessibility_shortcut_edit_summary_software;
        if (AccessibilityUtil.isGestureNavigateEnabled(context)) {
            if (AccessibilityUtil.isTouchExploreEnabled(context)) {
                i = C0017R$string.accessibility_shortcut_edit_dialog_title_software_gesture_talkback;
            } else {
                i = C0017R$string.accessibility_shortcut_edit_dialog_title_software_gesture;
            }
        }
        CharSequence text = context.getText(i);
        ArrayList arrayList = new ArrayList();
        if ((userShortcutTypes & 1) == 1) {
            arrayList.add(text);
        }
        if ((userShortcutTypes & 2) == 2) {
            arrayList.add(context.getText(C0017R$string.accessibility_shortcut_hardware_keyword));
        }
        if ((userShortcutTypes & 4) == 4) {
            arrayList.add(context.getText(C0017R$string.accessibility_shortcut_triple_tap_keyword));
        }
        if (arrayList.isEmpty()) {
            arrayList.add(text);
        }
        return CaseMap.toTitle().wholeString().noLowercase().apply(Locale.getDefault(), null, TextUtils.join(", ", arrayList));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public int getUserShortcutTypes(Context context, int i) {
        Set set = (Set) SharedPreferenceUtils.getUserShortcutTypes(context).stream().filter($$Lambda$ToggleScreenMagnificationPreferenceFragment$RgCY6S9jRtyNwEr_aWUBasoExh8.INSTANCE).collect(Collectors.toSet());
        if (set.isEmpty()) {
            return i;
        }
        return new ToggleFeaturePreferenceFragment.AccessibilityUserShortcutType((String) set.toArray()[0]).getType();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void callOnAlertDialogCheckboxClicked(DialogInterface dialogInterface, int i) {
        boolean z = true;
        updateUserShortcutType(true);
        optInAllMagnificationValuesToSettings(getPrefContext(), this.mUserShortcutType);
        optOutAllMagnificationValuesFromSettings(getPrefContext(), ~this.mUserShortcutType);
        ShortcutPreference shortcutPreference = this.mShortcutPreference;
        if (this.mUserShortcutType == 0) {
            z = false;
        }
        shortcutPreference.setChecked(z);
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i == 1001) {
            return 1813;
        }
        if (i == 1006) {
            return 1801;
        }
        if (i != 1007) {
            return super.getDialogMetricsCategory(i);
        }
        return 1802;
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public int getUserShortcutTypes() {
        return getUserShortcutTypeFromSettings(getPrefContext());
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void onInstallSwitchPreferenceToggleSwitch() {
        super.onInstallSwitchPreferenceToggleSwitch();
        this.mToggleServiceDividerSwitchPreference.setVisible(false);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onToggleClicked(ShortcutPreference shortcutPreference) {
        int userShortcutTypes = getUserShortcutTypes(getPrefContext(), 1);
        if (shortcutPreference.isChecked()) {
            optInAllMagnificationValuesToSettings(getPrefContext(), userShortcutTypes);
        } else {
            optOutAllMagnificationValuesFromSettings(getPrefContext(), userShortcutTypes);
        }
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onSettingsClicked(ShortcutPreference shortcutPreference) {
        this.mUserShortcutTypesCache = this.mShortcutPreference.isChecked() ? getUserShortcutTypes(getPrefContext(), 1) : 0;
        showDialog(1001);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void updateShortcutPreferenceData() {
        int userShortcutTypeFromSettings = getUserShortcutTypeFromSettings(getPrefContext());
        this.mUserShortcutType = userShortcutTypeFromSettings;
        if (userShortcutTypeFromSettings != 0) {
            setUserShortcutType(getPrefContext(), this.mUserShortcutType);
        } else {
            this.mUserShortcutType = getUserShortcutTypes(getPrefContext(), 1);
        }
    }

    private void initShortcutPreference() {
        ShortcutPreference shortcutPreference = new ShortcutPreference(getPrefContext(), null);
        this.mShortcutPreference = shortcutPreference;
        shortcutPreference.setPersistent(false);
        this.mShortcutPreference.setKey("shortcut_preference");
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
        this.mShortcutPreference.setOnClickCallback(this);
        this.mShortcutPreference.setTitle(getString(C0017R$string.accessibility_shortcut_title, this.mPackageName));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void updateShortcutPreference() {
        this.mShortcutPreference.setChecked(hasMagnificationValuesInSettings(getPrefContext(), getUserShortcutTypes(getPrefContext(), 1)));
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    @VisibleForTesting
    static void optInAllMagnificationValuesToSettings(Context context, int i) {
        if ((i & 1) == 1) {
            optInMagnificationValueToSettings(context, 1);
        }
        if ((i & 2) == 2) {
            optInMagnificationValueToSettings(context, 2);
        }
        if ((i & 4) == 4) {
            optInMagnificationValueToSettings(context, 4);
        }
    }

    private static void optInMagnificationValueToSettings(Context context, int i) {
        if (i == 4) {
            Settings.Secure.putInt(context.getContentResolver(), "accessibility_display_magnification_enabled", 1);
        } else if (!hasMagnificationValueInSettings(context, i)) {
            String convertKeyFromSettings = AccessibilityUtil.convertKeyFromSettings(i);
            String string = Settings.Secure.getString(context.getContentResolver(), convertKeyFromSettings);
            StringJoiner stringJoiner = new StringJoiner(String.valueOf(':'));
            if (!TextUtils.isEmpty(string)) {
                stringJoiner.add(string);
            }
            stringJoiner.add("com.android.server.accessibility.MagnificationController");
            Settings.Secure.putString(context.getContentResolver(), convertKeyFromSettings, stringJoiner.toString());
        }
    }

    @VisibleForTesting
    static void optOutAllMagnificationValuesFromSettings(Context context, int i) {
        if ((i & 1) == 1) {
            optOutMagnificationValueFromSettings(context, 1);
        }
        if ((i & 2) == 2) {
            optOutMagnificationValueFromSettings(context, 2);
        }
        if ((i & 4) == 4) {
            optOutMagnificationValueFromSettings(context, 4);
        }
    }

    private static void optOutMagnificationValueFromSettings(Context context, int i) {
        if (i == 4) {
            Settings.Secure.putInt(context.getContentResolver(), "accessibility_display_magnification_enabled", 0);
            return;
        }
        String convertKeyFromSettings = AccessibilityUtil.convertKeyFromSettings(i);
        String string = Settings.Secure.getString(context.getContentResolver(), convertKeyFromSettings);
        if (!TextUtils.isEmpty(string)) {
            StringJoiner stringJoiner = new StringJoiner(String.valueOf(':'));
            sStringColonSplitter.setString(string);
            while (sStringColonSplitter.hasNext()) {
                String next = sStringColonSplitter.next();
                if (!TextUtils.isEmpty(next) && !"com.android.server.accessibility.MagnificationController".equals(next)) {
                    stringJoiner.add(next);
                }
            }
            Settings.Secure.putString(context.getContentResolver(), convertKeyFromSettings, stringJoiner.toString());
        }
    }

    @VisibleForTesting
    static boolean hasMagnificationValuesInSettings(Context context, int i) {
        boolean hasMagnificationValueInSettings = (i & 1) == 1 ? hasMagnificationValueInSettings(context, 1) : false;
        if ((i & 2) == 2) {
            hasMagnificationValueInSettings |= hasMagnificationValueInSettings(context, 2);
        }
        return (i & 4) == 4 ? hasMagnificationValueInSettings | hasMagnificationValueInSettings(context, 4) : hasMagnificationValueInSettings;
    }

    private static boolean hasMagnificationValueInSettings(Context context, int i) {
        if (i == 4) {
            return Settings.Secure.getInt(context.getContentResolver(), "accessibility_display_magnification_enabled", 0) == 1;
        }
        String string = Settings.Secure.getString(context.getContentResolver(), AccessibilityUtil.convertKeyFromSettings(i));
        if (TextUtils.isEmpty(string)) {
            return false;
        }
        sStringColonSplitter.setString(string);
        while (sStringColonSplitter.hasNext()) {
            if ("com.android.server.accessibility.MagnificationController".equals(sStringColonSplitter.next())) {
                return true;
            }
        }
        return false;
    }

    private boolean isWindowMagnification(Context context) {
        if (Settings.Secure.getIntForUser(context.getContentResolver(), "accessibility_magnification_mode", 1, context.getContentResolver().getUserId()) == 2) {
            return true;
        }
        return false;
    }

    private static int getUserShortcutTypeFromSettings(Context context) {
        int i = 1;
        if (!hasMagnificationValuesInSettings(context, 1)) {
            i = 0;
        }
        if (hasMagnificationValuesInSettings(context, 2)) {
            i |= 2;
        }
        return hasMagnificationValuesInSettings(context, 4) ? i | 4 : i;
    }

    public static CharSequence getServiceSummary(Context context) {
        if (getUserShortcutTypeFromSettings(context) != 0) {
            return context.getText(C0017R$string.accessibility_summary_shortcut_enabled);
        }
        return context.getText(C0017R$string.accessibility_summary_shortcut_disabled);
    }
}
