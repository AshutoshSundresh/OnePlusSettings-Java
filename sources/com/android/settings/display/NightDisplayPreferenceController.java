package com.android.settings.display;

import android.content.Context;
import android.hardware.display.ColorDisplayManager;
import com.android.settings.C0005R$bool;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public class NightDisplayPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    public static boolean isSuggestionComplete(Context context) {
        if (context.getResources().getBoolean(C0005R$bool.config_night_light_suggestion_enabled) && ((ColorDisplayManager) context.getSystemService(ColorDisplayManager.class)).getNightDisplayAutoMode() == 0) {
            return false;
        }
        return true;
    }
}
