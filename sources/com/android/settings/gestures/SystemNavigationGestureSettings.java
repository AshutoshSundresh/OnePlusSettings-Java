package com.android.settings.gestures;

import android.content.Context;
import android.content.Intent;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsTutorialDialogWrapperActivity;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.actionbar.SearchMenuController;
import com.android.settings.support.actionbar.HelpMenuController;
import com.android.settings.support.actionbar.HelpResourceProvider;
import com.android.settings.utils.CandidateInfoExtra;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settings.widget.RadioButtonPreference;
import com.android.settings.widget.RadioButtonPreferenceWithExtraWidget;
import com.android.settings.widget.VideoPreference;
import com.android.settingslib.widget.CandidateInfo;
import java.util.ArrayList;
import java.util.List;

public class SystemNavigationGestureSettings extends RadioButtonPickerFragment implements HelpResourceProvider {
    static final String KEY_SYSTEM_NAV_2BUTTONS = "system_nav_2buttons";
    static final String KEY_SYSTEM_NAV_3BUTTONS = "system_nav_3buttons";
    static final String KEY_SYSTEM_NAV_GESTURAL = "system_nav_gestural";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.system_navigation_gesture_settings) {
        /* class com.android.settings.gestures.SystemNavigationGestureSettings.AnonymousClass1 */

        /* access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return SystemNavigationPreferenceController.isGestureAvailable(context);
        }
    };
    private IOverlayManager mOverlayManager;
    private VideoPreference mVideoPreference;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1374;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        SearchMenuController.init(this);
        HelpMenuController.init(this);
        FeatureFactory.getFactory(context).getSuggestionFeatureProvider(context).getSharedPrefs(context).edit().putBoolean("pref_system_navigation_suggestion_complete", true).apply();
        this.mOverlayManager = IOverlayManager.Stub.asInterface(ServiceManager.getService("overlay"));
        VideoPreference videoPreference = new VideoPreference(context);
        this.mVideoPreference = videoPreference;
        setIllustrationVideo(videoPreference, getDefaultKey());
        this.mVideoPreference.setHeight(getResources().getDimension(C0007R$dimen.system_navigation_illustration_height) / getResources().getDisplayMetrics().density);
        migrateOverlaySensitivityToSettings(context, this.mOverlayManager);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void updateCandidates() {
        String defaultKey = getDefaultKey();
        String systemDefaultKey = getSystemDefaultKey();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        preferenceScreen.addPreference(this.mVideoPreference);
        List<? extends CandidateInfo> candidates = getCandidates();
        if (candidates != null) {
            for (CandidateInfo candidateInfo : candidates) {
                RadioButtonPreferenceWithExtraWidget radioButtonPreferenceWithExtraWidget = new RadioButtonPreferenceWithExtraWidget(getPrefContext());
                bindPreference(radioButtonPreferenceWithExtraWidget, candidateInfo.getKey(), candidateInfo, defaultKey);
                bindPreferenceExtra(radioButtonPreferenceWithExtraWidget, candidateInfo.getKey(), candidateInfo, defaultKey, systemDefaultKey);
                preferenceScreen.addPreference(radioButtonPreferenceWithExtraWidget);
            }
            mayCheckOnlyRadioButton();
        }
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void bindPreferenceExtra(RadioButtonPreference radioButtonPreference, String str, CandidateInfo candidateInfo, String str2, String str3) {
        if ((candidateInfo instanceof CandidateInfoExtra) && (radioButtonPreference instanceof RadioButtonPreferenceWithExtraWidget)) {
            radioButtonPreference.setSummary(((CandidateInfoExtra) candidateInfo).loadSummary());
            RadioButtonPreferenceWithExtraWidget radioButtonPreferenceWithExtraWidget = (RadioButtonPreferenceWithExtraWidget) radioButtonPreference;
            if (candidateInfo.getKey() == KEY_SYSTEM_NAV_GESTURAL) {
                radioButtonPreferenceWithExtraWidget.setExtraWidgetVisibility(2);
                radioButtonPreferenceWithExtraWidget.setExtraWidgetOnClickListener(new View.OnClickListener() {
                    /* class com.android.settings.gestures.$$Lambda$SystemNavigationGestureSettings$JLPyJ0q716VHKo4MJYnw7DzMMKM */

                    public final void onClick(View view) {
                        SystemNavigationGestureSettings.this.lambda$bindPreferenceExtra$0$SystemNavigationGestureSettings(view);
                    }
                });
                return;
            }
            radioButtonPreferenceWithExtraWidget.setExtraWidgetVisibility(0);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$bindPreferenceExtra$0 */
    public /* synthetic */ void lambda$bindPreferenceExtra$0$SystemNavigationGestureSettings(View view) {
        startActivity(new Intent("com.android.settings.GESTURE_NAVIGATION_SETTINGS"));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.system_navigation_gesture_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public List<? extends CandidateInfo> getCandidates() {
        Context context = getContext();
        ArrayList arrayList = new ArrayList();
        if (SystemNavigationPreferenceController.isOverlayPackageAvailable(context, "com.android.internal.systemui.navbar.gestural")) {
            arrayList.add(new CandidateInfoExtra(context.getText(C0017R$string.edge_to_edge_navigation_title), context.getText(C0017R$string.edge_to_edge_navigation_summary), KEY_SYSTEM_NAV_GESTURAL, true));
        }
        if (SystemNavigationPreferenceController.isOverlayPackageAvailable(context, "com.android.internal.systemui.navbar.twobutton")) {
            arrayList.add(new CandidateInfoExtra(context.getText(C0017R$string.swipe_up_to_switch_apps_title), context.getText(C0017R$string.swipe_up_to_switch_apps_summary), KEY_SYSTEM_NAV_2BUTTONS, true));
        }
        if (SystemNavigationPreferenceController.isOverlayPackageAvailable(context, "com.android.internal.systemui.navbar.threebutton")) {
            arrayList.add(new CandidateInfoExtra(context.getText(C0017R$string.legacy_navigation_title), context.getText(C0017R$string.legacy_navigation_summary), KEY_SYSTEM_NAV_3BUTTONS, true));
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public String getDefaultKey() {
        return getCurrentSystemNavigationMode(getContext());
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public boolean setDefaultKey(String str) {
        setCurrentSystemNavigationMode(this.mOverlayManager, str);
        setIllustrationVideo(this.mVideoPreference, str);
        if (!TextUtils.equals(KEY_SYSTEM_NAV_GESTURAL, str)) {
            return true;
        }
        if (!isAnyServiceSupportAccessibilityButton() && !isNavBarMagnificationEnabled()) {
            return true;
        }
        Intent intent = new Intent(getActivity(), SettingsTutorialDialogWrapperActivity.class);
        intent.addFlags(268435456);
        startActivity(intent);
        return true;
    }

    static void migrateOverlaySensitivityToSettings(Context context, IOverlayManager iOverlayManager) {
        if (SystemNavigationPreferenceController.isGestureNavigationEnabled(context)) {
            OverlayInfo overlayInfo = null;
            try {
                overlayInfo = iOverlayManager.getOverlayInfo("com.android.internal.systemui.navbar.gestural", -2);
            } catch (RemoteException unused) {
            }
            if (overlayInfo != null && !overlayInfo.isEnabled()) {
                setCurrentSystemNavigationMode(iOverlayManager, KEY_SYSTEM_NAV_GESTURAL);
                Settings.Secure.putFloat(context.getContentResolver(), "back_gesture_inset_scale_left", 1.0f);
                Settings.Secure.putFloat(context.getContentResolver(), "back_gesture_inset_scale_right", 1.0f);
            }
        }
    }

    static String getCurrentSystemNavigationMode(Context context) {
        if (SystemNavigationPreferenceController.isGestureNavigationEnabled(context)) {
            return KEY_SYSTEM_NAV_GESTURAL;
        }
        return SystemNavigationPreferenceController.is2ButtonNavigationEnabled(context) ? KEY_SYSTEM_NAV_2BUTTONS : KEY_SYSTEM_NAV_3BUTTONS;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0039  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void setCurrentSystemNavigationMode(android.content.om.IOverlayManager r4, java.lang.String r5) {
        /*
            int r0 = r5.hashCode()
            r1 = -1860313413(0xffffffff911ddebb, float:-1.245375E-28)
            r2 = 2
            r3 = 1
            if (r0 == r1) goto L_0x002a
            r1 = -1375361165(0xffffffffae05a773, float:-3.0389424E-11)
            if (r0 == r1) goto L_0x0020
            r1 = -117503078(0xfffffffff8ff0b9a, float:-4.138347E34)
            if (r0 == r1) goto L_0x0016
            goto L_0x0034
        L_0x0016:
            java.lang.String r0 = "system_nav_3buttons"
            boolean r5 = r5.equals(r0)
            if (r5 == 0) goto L_0x0034
            r5 = r2
            goto L_0x0035
        L_0x0020:
            java.lang.String r0 = "system_nav_gestural"
            boolean r5 = r5.equals(r0)
            if (r5 == 0) goto L_0x0034
            r5 = 0
            goto L_0x0035
        L_0x002a:
            java.lang.String r0 = "system_nav_2buttons"
            boolean r5 = r5.equals(r0)
            if (r5 == 0) goto L_0x0034
            r5 = r3
            goto L_0x0035
        L_0x0034:
            r5 = -1
        L_0x0035:
            java.lang.String r0 = "com.android.internal.systemui.navbar.gestural"
            if (r5 == 0) goto L_0x0043
            if (r5 == r3) goto L_0x0041
            if (r5 == r2) goto L_0x003e
            goto L_0x0043
        L_0x003e:
            java.lang.String r0 = "com.android.internal.systemui.navbar.threebutton"
            goto L_0x0043
        L_0x0041:
            java.lang.String r0 = "com.android.internal.systemui.navbar.twobutton"
        L_0x0043:
            r5 = -2
            r4.setEnabledExclusiveInCategory(r0, r5)     // Catch:{ RemoteException -> 0x0048 }
            return
        L_0x0048:
            r4 = move-exception
            java.lang.RuntimeException r4 = r4.rethrowFromSystemServer()
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.gestures.SystemNavigationGestureSettings.setCurrentSystemNavigationMode(android.content.om.IOverlayManager, java.lang.String):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002a, code lost:
        if (r6.equals(com.android.settings.gestures.SystemNavigationGestureSettings.KEY_SYSTEM_NAV_GESTURAL) != false) goto L_0x0038;
     */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x004f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void setIllustrationVideo(com.android.settings.widget.VideoPreference r5, java.lang.String r6) {
        /*
            r0 = 0
            r5.setVideo(r0, r0)
            int r1 = r6.hashCode()
            r2 = -1860313413(0xffffffff911ddebb, float:-1.245375E-28)
            r3 = 2
            r4 = 1
            if (r1 == r2) goto L_0x002d
            r2 = -1375361165(0xffffffffae05a773, float:-3.0389424E-11)
            if (r1 == r2) goto L_0x0024
            r0 = -117503078(0xfffffffff8ff0b9a, float:-4.138347E34)
            if (r1 == r0) goto L_0x001a
            goto L_0x0037
        L_0x001a:
            java.lang.String r0 = "system_nav_3buttons"
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x0037
            r0 = r3
            goto L_0x0038
        L_0x0024:
            java.lang.String r1 = "system_nav_gestural"
            boolean r6 = r6.equals(r1)
            if (r6 == 0) goto L_0x0037
            goto L_0x0038
        L_0x002d:
            java.lang.String r0 = "system_nav_2buttons"
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x0037
            r0 = r4
            goto L_0x0038
        L_0x0037:
            r0 = -1
        L_0x0038:
            if (r0 == 0) goto L_0x004f
            if (r0 == r4) goto L_0x0047
            if (r0 == r3) goto L_0x003f
            goto L_0x0056
        L_0x003f:
            int r6 = com.android.settings.C0016R$raw.system_nav_3_button
            int r0 = com.android.settings.C0008R$drawable.system_nav_3_button
            r5.setVideo(r6, r0)
            goto L_0x0056
        L_0x0047:
            int r6 = com.android.settings.C0016R$raw.system_nav_2_button
            int r0 = com.android.settings.C0008R$drawable.system_nav_2_button
            r5.setVideo(r6, r0)
            goto L_0x0056
        L_0x004f:
            int r6 = com.android.settings.C0016R$raw.system_nav_fully_gestural
            int r0 = com.android.settings.C0008R$drawable.system_nav_fully_gestural
            r5.setVideo(r6, r0)
        L_0x0056:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.gestures.SystemNavigationGestureSettings.setIllustrationVideo(com.android.settings.widget.VideoPreference, java.lang.String):void");
    }

    private boolean isAnyServiceSupportAccessibilityButton() {
        return !((AccessibilityManager) getContext().getSystemService(AccessibilityManager.class)).getAccessibilityShortcutTargets(0).isEmpty();
    }

    private boolean isNavBarMagnificationEnabled() {
        return Settings.Secure.getInt(getContext().getContentResolver(), "accessibility_display_magnification_navbar_enabled", 0) == 1;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_uri_default;
    }
}
