package com.android.settings;

import android.content.Intent;
import android.os.Bundle;
import android.sysprop.SetupWizardProperties;
import android.util.Log;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.oneplus.settings.utils.OPUtils;
import java.util.Arrays;

public class SetupWizardUtils {
    public static String getThemeString(Intent intent) {
        String stringExtra = intent.getStringExtra("theme");
        return stringExtra == null ? (String) SetupWizardProperties.theme().orElse("") : stringExtra;
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004b, code lost:
        if (r0.equals("glif_v3_light") != false) goto L_0x0057;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00a3, code lost:
        if (r0.equals("glif_v3_light") != false) goto L_0x00af;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getGlifTheme(android.content.Intent r14) {
        /*
        // Method dump skipped, instructions count: 252
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.SetupWizardUtils.getGlifTheme(android.content.Intent):int");
    }

    public static int getTheme(Intent intent) {
        if (OPUtils.isO2()) {
            Log.i("SetupWizardUtils", "Theme_Oneplus_SetupWizardTheme_Oxygen");
            return C0018R$style.Theme_Oneplus_SetupWizardTheme_Oxygen;
        }
        Log.i("SetupWizardUtils", "Theme_Oneplus_SetupWizardTheme_Hydrogen");
        return C0018R$style.Theme_Oneplus_SetupWizardTheme_Hydrogen;
    }

    public static int getTransparentTheme(Intent intent) {
        int theme = getTheme(intent);
        int i = C0018R$style.GlifV2Theme_Light_Transparent;
        if (theme == C0018R$style.GlifV3Theme) {
            return C0018R$style.GlifV3Theme_Transparent;
        }
        if (theme == C0018R$style.GlifV3Theme_Light) {
            return C0018R$style.GlifV3Theme_Light_Transparent;
        }
        if (theme == C0018R$style.GlifV2Theme) {
            return C0018R$style.GlifV2Theme_Transparent;
        }
        if (theme == C0018R$style.GlifTheme_Light) {
            return C0018R$style.SetupWizardTheme_Light_Transparent;
        }
        return theme == C0018R$style.GlifTheme ? C0018R$style.SetupWizardTheme_Transparent : i;
    }

    public static void copySetupExtras(Intent intent, Intent intent2) {
        WizardManagerHelper.copyWizardManagerExtras(intent, intent2);
    }

    public static Bundle copyLifecycleExtra(Bundle bundle, Bundle bundle2) {
        for (String str : Arrays.asList(WizardManagerHelper.EXTRA_IS_FIRST_RUN, WizardManagerHelper.EXTRA_IS_SETUP_FLOW)) {
            bundle2.putBoolean(str, bundle.getBoolean(str, false));
        }
        return bundle2;
    }
}
