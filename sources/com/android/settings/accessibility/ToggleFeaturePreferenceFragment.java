package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.accessibility.ShortcutPreference;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.settingslib.widget.FooterPreference;
import com.oneplus.settings.ui.OPPreferenceHeaderMargin;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class ToggleFeaturePreferenceFragment extends SettingsPreferenceFragment implements ShortcutPreference.OnClickCallback {
    protected ComponentName mComponentName;
    private CharSequence mDescription;
    private CheckBox mHardwareTypeCheckBox;
    protected CharSequence mHtmlDescription;
    private final Html.ImageGetter mImageGetter = new Html.ImageGetter() {
        /* class com.android.settings.accessibility.$$Lambda$ToggleFeaturePreferenceFragment$Jw70iwxBCvsyGxvG0caiaIjJWkY */

        public final Drawable getDrawable(String str) {
            return ToggleFeaturePreferenceFragment.this.lambda$new$0$ToggleFeaturePreferenceFragment(str);
        }
    };
    private ImageView mImageGetterCacheView;
    protected Uri mImageUri;
    protected CharSequence mPackageName;
    protected String mPreferenceKey;
    private SettingsContentObserver mSettingsContentObserver;
    protected Intent mSettingsIntent;
    protected Preference mSettingsPreference;
    protected CharSequence mSettingsTitle;
    protected ShortcutPreference mShortcutPreference;
    private CheckBox mSoftwareTypeCheckBox;
    protected DividerSwitchPreference mToggleServiceDividerSwitchPreference;
    private AccessibilityManager.TouchExplorationStateChangeListener mTouchExplorationStateChangeListener;
    private int mUserShortcutTypes = 0;
    protected int mUserShortcutTypesCache = 0;

    private String getShortcutPreferenceKey() {
        return "shortcut_preference";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i != 1) {
            return i != 1008 ? 0 : 1810;
        }
        return 1812;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 4;
    }

    /* access modifiers changed from: package-private */
    public abstract int getUserShortcutTypes();

    /* access modifiers changed from: protected */
    public void onInstallSwitchPreferenceToggleSwitch() {
    }

    /* access modifiers changed from: protected */
    public void onRemoveSwitchPreferenceToggleSwitch() {
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ Drawable lambda$new$0$ToggleFeaturePreferenceFragment(String str) {
        if (str == null || !str.startsWith("R.drawable.")) {
            return null;
        }
        String substring = str.substring(11);
        return getDrawableFromUri(Uri.parse("android.resource://" + this.mComponentName.getPackageName() + "/drawable/" + substring));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setupDefaultShortcutIfNecessary(getPrefContext());
        if (getPreferenceScreenResId() <= 0) {
            setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getPrefContext()));
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add("accessibility_button_targets");
        arrayList.add("accessibility_shortcut_target_service");
        this.mSettingsContentObserver = new SettingsContentObserver(new Handler(), arrayList) {
            /* class com.android.settings.accessibility.ToggleFeaturePreferenceFragment.AnonymousClass1 */

            public void onChange(boolean z, Uri uri) {
                ToggleFeaturePreferenceFragment.this.updateShortcutPreferenceData();
                ToggleFeaturePreferenceFragment.this.updateShortcutPreference();
            }
        };
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mTouchExplorationStateChangeListener = new AccessibilityManager.TouchExplorationStateChangeListener() {
            /* class com.android.settings.accessibility.$$Lambda$ToggleFeaturePreferenceFragment$uBGYLeeVR39WBTqL6H6Ihv2QGM */

            public final void onTouchExplorationStateChanged(boolean z) {
                ToggleFeaturePreferenceFragment.this.lambda$onCreateView$1$ToggleFeaturePreferenceFragment(z);
            }
        };
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateView$1 */
    public /* synthetic */ void lambda$onCreateView$1$ToggleFeaturePreferenceFragment(boolean z) {
        removeDialog(1);
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ((SettingsActivity) getActivity()).getSwitchBar().hide();
        onProcessArguments(getArguments());
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (this.mImageUri != null) {
            AnimatedImagePreference animatedImagePreference = new AnimatedImagePreference(getPrefContext());
            animatedImagePreference.setImageUri(this.mImageUri);
            animatedImagePreference.setSelectable(false);
            animatedImagePreference.setMaxHeight(AccessibilityUtil.getScreenHeightPixels(getPrefContext()) / 2);
            preferenceScreen.addPreference(animatedImagePreference);
        }
        DividerSwitchPreference dividerSwitchPreference = new DividerSwitchPreference(getPrefContext());
        this.mToggleServiceDividerSwitchPreference = dividerSwitchPreference;
        dividerSwitchPreference.setKey("use_service");
        if (getArguments().containsKey("checked")) {
            this.mToggleServiceDividerSwitchPreference.setChecked(getArguments().getBoolean("checked"));
        }
        preferenceScreen.addPreference(new OPPreferenceHeaderMargin(getPrefContext()));
        preferenceScreen.addPreference(this.mToggleServiceDividerSwitchPreference);
        updateToggleServiceTitle(this.mToggleServiceDividerSwitchPreference);
        PreferenceCategory preferenceCategory = new PreferenceCategory(getPrefContext());
        preferenceCategory.setKey("general_categories");
        preferenceCategory.setTitle(C0017R$string.accessibility_screen_option);
        preferenceScreen.addPreference(preferenceCategory);
        initShortcutPreference(bundle);
        preferenceCategory.addPreference(this.mShortcutPreference);
        if (!(this.mSettingsTitle == null || this.mSettingsIntent == null)) {
            Preference preference = new Preference(getPrefContext());
            this.mSettingsPreference = preference;
            preference.setTitle(this.mSettingsTitle);
            this.mSettingsPreference.setIconSpaceReserved(true);
            this.mSettingsPreference.setIntent(this.mSettingsIntent);
        }
        Preference preference2 = this.mSettingsPreference;
        if (preference2 != null) {
            preferenceCategory.addPreference(preference2);
        }
        if (!TextUtils.isEmpty(this.mHtmlDescription)) {
            PreferenceCategory preferenceCategory2 = new PreferenceCategory(getPrefContext());
            String string = getString(C0017R$string.accessibility_introduction_title, this.mPackageName);
            preferenceCategory2.setKey("introduction_categories");
            preferenceCategory2.setTitle(string);
            preferenceScreen.addPreference(preferenceCategory2);
            HtmlTextPreference htmlTextPreference = new HtmlTextPreference(getPrefContext());
            htmlTextPreference.setSummary(this.mHtmlDescription);
            htmlTextPreference.setImageGetter(this.mImageGetter);
            htmlTextPreference.setSelectable(false);
            preferenceCategory2.addPreference(htmlTextPreference);
        }
        if (!TextUtils.isEmpty(this.mDescription)) {
            createFooterPreference(this.mDescription);
        }
        if (TextUtils.isEmpty(this.mHtmlDescription) && TextUtils.isEmpty(this.mDescription)) {
            createFooterPreference(getText(C0017R$string.accessibility_service_default_description));
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        installActionBarToggleSwitch();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).addTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
        this.mSettingsContentObserver.register(getContentResolver());
        updateShortcutPreferenceData();
        updateShortcutPreference();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).removeTouchExplorationStateChangeListener(this.mTouchExplorationStateChangeListener);
        this.mSettingsContentObserver.unregister(getContentResolver());
        super.onPause();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("shortcut_type", this.mUserShortcutTypesCache);
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 1) {
            AlertDialog showEditShortcutDialog = AccessibilityEditDialogUtils.showEditShortcutDialog(getPrefContext(), getPrefContext().getString(C0017R$string.accessibility_shortcut_title, this.mPackageName), new DialogInterface.OnClickListener() {
                /* class com.android.settings.accessibility.$$Lambda$bFeaMzxQWXcddHdyUtAuKYB95AA */

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ToggleFeaturePreferenceFragment.this.callOnAlertDialogCheckboxClicked(dialogInterface, i);
                }
            });
            initializeDialogCheckBox(showEditShortcutDialog);
            return showEditShortcutDialog;
        } else if (i == 1008) {
            AlertDialog createAccessibilityTutorialDialog = AccessibilityGestureNavigationTutorial.createAccessibilityTutorialDialog(getPrefContext(), getUserShortcutTypes());
            createAccessibilityTutorialDialog.setCanceledOnTouchOutside(false);
            return createAccessibilityTutorialDialog;
        } else {
            throw new IllegalArgumentException("Unsupported dialogId " + i);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        removeActionBarToggleSwitch();
    }

    /* access modifiers changed from: protected */
    public void updateToggleServiceTitle(SwitchPreference switchPreference) {
        switchPreference.setTitle(C0017R$string.accessibility_service_master_switch_title);
    }

    private void installActionBarToggleSwitch() {
        onInstallSwitchPreferenceToggleSwitch();
    }

    private void removeActionBarToggleSwitch() {
        this.mToggleServiceDividerSwitchPreference.setOnPreferenceClickListener(null);
        onRemoveSwitchPreferenceToggleSwitch();
    }

    public void setTitle(String str) {
        getActivity().setTitle(str);
    }

    /* access modifiers changed from: protected */
    public void onProcessArguments(Bundle bundle) {
        this.mPreferenceKey = bundle.getString("preference_key");
        if (bundle.containsKey("resolve_info")) {
            getActivity().setTitle(((ResolveInfo) bundle.getParcelable("resolve_info")).loadLabel(getPackageManager()).toString());
        } else if (bundle.containsKey("title")) {
            setTitle(bundle.getString("title"));
        }
        if (bundle.containsKey("summary")) {
            this.mDescription = bundle.getCharSequence("summary");
        }
        if (bundle.containsKey("html_description")) {
            this.mHtmlDescription = bundle.getCharSequence("html_description");
        }
    }

    private Drawable getDrawableFromUri(Uri uri) {
        if (this.mImageGetterCacheView == null) {
            this.mImageGetterCacheView = new ImageView(getPrefContext());
        }
        this.mImageGetterCacheView.setAdjustViewBounds(true);
        this.mImageGetterCacheView.setImageURI(uri);
        if (this.mImageGetterCacheView.getDrawable() == null) {
            return null;
        }
        Drawable newDrawable = this.mImageGetterCacheView.getDrawable().mutate().getConstantState().newDrawable();
        this.mImageGetterCacheView.setImageURI(null);
        int intrinsicWidth = newDrawable.getIntrinsicWidth();
        int intrinsicHeight = newDrawable.getIntrinsicHeight();
        int screenHeightPixels = AccessibilityUtil.getScreenHeightPixels(getPrefContext()) / 2;
        if (intrinsicWidth > AccessibilityUtil.getScreenWidthPixels(getPrefContext()) || intrinsicHeight > screenHeightPixels) {
            return null;
        }
        newDrawable.setBounds(0, 0, newDrawable.getIntrinsicWidth(), newDrawable.getIntrinsicHeight());
        return newDrawable;
    }

    /* access modifiers changed from: package-private */
    public static final class AccessibilityUserShortcutType {
        private static final TextUtils.SimpleStringSplitter sStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        private String mComponentName;
        private int mType;

        AccessibilityUserShortcutType(String str, int i) {
            this.mComponentName = str;
            this.mType = i;
        }

        AccessibilityUserShortcutType(String str) {
            sStringColonSplitter.setString(str);
            if (sStringColonSplitter.hasNext()) {
                this.mComponentName = sStringColonSplitter.next();
                this.mType = Integer.parseInt(sStringColonSplitter.next());
            }
        }

        /* access modifiers changed from: package-private */
        public int getType() {
            return this.mType;
        }

        /* access modifiers changed from: package-private */
        public String flattenToString() {
            StringJoiner stringJoiner = new StringJoiner(String.valueOf(':'));
            stringJoiner.add(this.mComponentName);
            stringJoiner.add(String.valueOf(this.mType));
            return stringJoiner.toString();
        }
    }

    private void setDialogTextAreaClickListener(View view, CheckBox checkBox) {
        view.findViewById(C0010R$id.container).setOnClickListener(new View.OnClickListener(checkBox) {
            /* class com.android.settings.accessibility.$$Lambda$ToggleFeaturePreferenceFragment$qNByzc_V3jK9bmq_HKcCoSmgupU */
            public final /* synthetic */ CheckBox f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ToggleFeaturePreferenceFragment.this.lambda$setDialogTextAreaClickListener$2$ToggleFeaturePreferenceFragment(this.f$1, view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setDialogTextAreaClickListener$2 */
    public /* synthetic */ void lambda$setDialogTextAreaClickListener$2$ToggleFeaturePreferenceFragment(CheckBox checkBox, View view) {
        checkBox.toggle();
        updateUserShortcutType(false);
    }

    private void initializeDialogCheckBox(Dialog dialog) {
        View findViewById = dialog.findViewById(C0010R$id.software_shortcut);
        CheckBox checkBox = (CheckBox) findViewById.findViewById(C0010R$id.checkbox);
        this.mSoftwareTypeCheckBox = checkBox;
        setDialogTextAreaClickListener(findViewById, checkBox);
        View findViewById2 = dialog.findViewById(C0010R$id.hardware_shortcut);
        CheckBox checkBox2 = (CheckBox) findViewById2.findViewById(C0010R$id.checkbox);
        this.mHardwareTypeCheckBox = checkBox2;
        setDialogTextAreaClickListener(findViewById2, checkBox2);
        updateAlertDialogCheckState();
    }

    private void updateAlertDialogCheckState() {
        if (this.mUserShortcutTypesCache != 0) {
            updateCheckStatus(this.mSoftwareTypeCheckBox, 1);
            updateCheckStatus(this.mHardwareTypeCheckBox, 2);
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
        if (z) {
            if (this.mUserShortcutTypesCache != 0) {
                z2 = true;
            }
            if (z2) {
                setUserShortcutType(getPrefContext(), this.mUserShortcutTypesCache);
            }
            this.mUserShortcutTypes = this.mUserShortcutTypesCache;
        }
    }

    private void setUserShortcutType(Context context, int i) {
        if (this.mComponentName != null) {
            Set userShortcutTypes = SharedPreferenceUtils.getUserShortcutTypes(context);
            String flattenToString = this.mComponentName.flattenToString();
            if (userShortcutTypes.isEmpty()) {
                userShortcutTypes = new HashSet();
            } else {
                userShortcutTypes.removeAll((Set) userShortcutTypes.stream().filter(new Predicate(flattenToString) {
                    /* class com.android.settings.accessibility.$$Lambda$ToggleFeaturePreferenceFragment$ZyTMN_bWiZJbVxEZhfW9JlG6heA */
                    public final /* synthetic */ String f$0;

                    {
                        this.f$0 = r1;
                    }

                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return ((String) obj).contains(this.f$0);
                    }
                }).collect(Collectors.toSet()));
            }
            userShortcutTypes.add(new AccessibilityUserShortcutType(flattenToString, i).flattenToString());
            SharedPreferenceUtils.setUserShortcutType(context, userShortcutTypes);
        }
    }

    /* access modifiers changed from: protected */
    public CharSequence getShortcutTypeSummary(Context context) {
        if (!this.mShortcutPreference.isSettingsEditable()) {
            return context.getText(C0017R$string.accessibility_shortcut_edit_dialog_title_hardware);
        }
        if (!this.mShortcutPreference.isChecked()) {
            return context.getText(C0017R$string.switch_off_text);
        }
        int userShortcutTypes = getUserShortcutTypes(context, 1);
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
        if (arrayList.isEmpty()) {
            arrayList.add(text);
        }
        return CaseMap.toTitle().wholeString().noLowercase().apply(Locale.getDefault(), null, TextUtils.join(", ", arrayList));
    }

    /* access modifiers changed from: protected */
    public int getUserShortcutTypes(Context context, int i) {
        if (this.mComponentName == null) {
            return i;
        }
        Set set = (Set) SharedPreferenceUtils.getUserShortcutTypes(context).stream().filter(new Predicate(this.mComponentName.flattenToString()) {
            /* class com.android.settings.accessibility.$$Lambda$ToggleFeaturePreferenceFragment$SesE8GKaavFiGqrtP5dqnWz_Qg */
            public final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((String) obj).contains(this.f$0);
            }
        }).collect(Collectors.toSet());
        if (set.isEmpty()) {
            return i;
        }
        return new AccessibilityUserShortcutType((String) set.toArray()[0]).getType();
    }

    /* access modifiers changed from: protected */
    public void callOnAlertDialogCheckboxClicked(DialogInterface dialogInterface, int i) {
        if (this.mComponentName != null) {
            boolean z = true;
            updateUserShortcutType(true);
            AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), this.mUserShortcutTypes, this.mComponentName);
            AccessibilityUtil.optOutAllValuesFromSettings(getPrefContext(), ~this.mUserShortcutTypes, this.mComponentName);
            ShortcutPreference shortcutPreference = this.mShortcutPreference;
            if (this.mUserShortcutTypes == 0) {
                z = false;
            }
            shortcutPreference.setChecked(z);
            this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
        }
    }

    /* access modifiers changed from: protected */
    public void updateShortcutPreferenceData() {
        if (this.mComponentName != null) {
            int userShortcutTypesFromSettings = AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), this.mComponentName);
            this.mUserShortcutTypes = userShortcutTypesFromSettings;
            if (userShortcutTypesFromSettings != 0) {
                setUserShortcutType(getPrefContext(), this.mUserShortcutTypes);
            } else {
                this.mUserShortcutTypes = getUserShortcutTypes(getPrefContext(), 1);
            }
        }
    }

    private void initShortcutPreference(Bundle bundle) {
        if (bundle != null && bundle.containsKey("shortcut_type")) {
            this.mUserShortcutTypesCache = bundle.getInt("shortcut_type", 0);
        }
        ShortcutPreference shortcutPreference = new ShortcutPreference(getPrefContext(), null);
        this.mShortcutPreference = shortcutPreference;
        shortcutPreference.setPersistent(false);
        this.mShortcutPreference.setKey(getShortcutPreferenceKey());
        this.mShortcutPreference.setOnClickCallback(this);
        this.mShortcutPreference.setTitle(getString(C0017R$string.accessibility_shortcut_title, this.mPackageName));
    }

    /* access modifiers changed from: protected */
    public void updateShortcutPreference() {
        if (this.mComponentName != null) {
            this.mShortcutPreference.setChecked(AccessibilityUtil.hasValuesInSettings(getPrefContext(), getUserShortcutTypes(getPrefContext(), 1), this.mComponentName));
            this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
        }
    }

    @Override // com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onToggleClicked(ShortcutPreference shortcutPreference) {
        if (this.mComponentName != null) {
            int userShortcutTypes = getUserShortcutTypes(getPrefContext(), 1);
            if (shortcutPreference.isChecked()) {
                AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), userShortcutTypes, this.mComponentName);
                showDialog(1008);
            } else {
                AccessibilityUtil.optOutAllValuesFromSettings(getPrefContext(), userShortcutTypes, this.mComponentName);
            }
            this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
        }
    }

    @Override // com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onSettingsClicked(ShortcutPreference shortcutPreference) {
        this.mUserShortcutTypesCache = this.mShortcutPreference.isChecked() ? getUserShortcutTypes(getPrefContext(), 1) : 0;
    }

    private void createFooterPreference(CharSequence charSequence) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        FooterPreference.Builder builder = new FooterPreference.Builder(getActivity());
        builder.setTitle(charSequence);
        preferenceScreen.addPreference(builder.build());
    }

    private static void setupDefaultShortcutIfNecessary(Context context) {
        ComponentName unflattenFromString;
        if (TextUtils.isEmpty(Settings.Secure.getString(context.getContentResolver(), "accessibility_shortcut_target_service"))) {
            String shortcutTargetServiceComponentNameString = AccessibilityUtils.getShortcutTargetServiceComponentNameString(context, UserHandle.myUserId());
            if (!TextUtils.isEmpty(shortcutTargetServiceComponentNameString) && (unflattenFromString = ComponentName.unflattenFromString(shortcutTargetServiceComponentNameString)) != null) {
                Settings.Secure.putString(context.getContentResolver(), "accessibility_shortcut_target_service", unflattenFromString.flattenToString());
            }
        }
    }
}
