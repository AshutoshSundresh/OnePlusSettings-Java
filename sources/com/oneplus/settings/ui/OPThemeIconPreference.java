package com.oneplus.settings.ui;

import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.OpFeatures;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.google.android.material.snackbar.Snackbar;
import com.oneplus.settings.OPCustomClockSettings;
import com.oneplus.settings.OPCustomFingerprintAnimSettings;
import com.oneplus.settings.OPCustomNotificationAnimSettings;
import com.oneplus.settings.utils.OPThemeUtils;
import com.oneplus.settings.utils.OPUtils;

public class OPThemeIconPreference extends Preference implements View.OnClickListener {
    private static final boolean IS_SUPPORT_AOD_ALWAYS_ON = OpFeatures.isSupport(new int[]{307});
    private RadiusImageView mClockStyle;
    private Context mContext;
    private RadiusImageView mFingerprintAnimation;
    private RadiusImageView mNotificationAnimation;
    private RadiusImageView mWallpaper;

    public OPThemeIconPreference(Context context) {
        this(context, null);
    }

    public OPThemeIconPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public OPThemeIconPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public OPThemeIconPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setLayoutResource(C0012R$layout.op_theme_icon_layout);
        this.mContext = context;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        View findViewById;
        View findViewById2;
        super.onBindViewHolder(preferenceViewHolder);
        RadiusImageView radiusImageView = (RadiusImageView) preferenceViewHolder.findViewById(C0010R$id.theme_wallpaper);
        this.mWallpaper = radiusImageView;
        if (radiusImageView != null) {
            radiusImageView.setOnClickListener(this);
        }
        RadiusImageView radiusImageView2 = (RadiusImageView) preferenceViewHolder.findViewById(C0010R$id.theme_clock_style);
        this.mClockStyle = radiusImageView2;
        if (radiusImageView2 != null) {
            radiusImageView2.setOnClickListener(this);
        }
        RadiusImageView radiusImageView3 = (RadiusImageView) preferenceViewHolder.findViewById(C0010R$id.theme_fingerprint);
        this.mFingerprintAnimation = radiusImageView3;
        if (radiusImageView3 != null) {
            radiusImageView3.setOnClickListener(this);
        }
        if (!OPUtils.isSupportCustomFingerprint() && (findViewById2 = preferenceViewHolder.findViewById(C0010R$id.theme_fingerprint_container)) != null) {
            findViewById2.setVisibility(8);
        }
        RadiusImageView radiusImageView4 = (RadiusImageView) preferenceViewHolder.findViewById(C0010R$id.theme_notification);
        this.mNotificationAnimation = radiusImageView4;
        if (radiusImageView4 != null) {
            radiusImageView4.setOnClickListener(this);
        }
        if (!OPUtils.isSupportNotificationLight() && (findViewById = preferenceViewHolder.findViewById(C0010R$id.op_custom_horizon_light)) != null) {
            findViewById.setVisibility(8);
        }
        refreshUI();
    }

    public void refreshUI() {
        RadiusImageView radiusImageView = this.mWallpaper;
        if (radiusImageView != null) {
            Context context = this.mContext;
            radiusImageView.setImageBitmap(OPWallPaperUtils.loadHomeWallpaper(context, WallpaperManager.getInstance(context).getWallpaperInfo()));
            Log.d("OPThemeIconPreference", "OPSettings-wallpaper-preview--widht:" + this.mWallpaper.getWidth() + " height:" + this.mWallpaper.getHeight());
        }
        RadiusImageView radiusImageView2 = this.mClockStyle;
        if (radiusImageView2 != null) {
            radiusImageView2.setImageResource(getClockStyleImage());
        }
        RadiusImageView radiusImageView3 = this.mFingerprintAnimation;
        if (radiusImageView3 != null) {
            radiusImageView3.setImageResource(getFodAnimStyleImage());
        }
        RadiusImageView radiusImageView4 = this.mNotificationAnimation;
        if (radiusImageView4 != null) {
            radiusImageView4.setImageResource(getHorizonLightStyleImage());
        }
    }

    private int getClockStyleImage() {
        int intForUser = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "aod_clock_style", 100, ActivityManager.getCurrentUser());
        if (intForUser != 40) {
            switch (intForUser) {
                case 0:
                    return C0008R$drawable.op_custom_aodpreview_smart_space_default;
                case 1:
                    return C0008R$drawable.op_custom_aodpreview_none;
                case 2:
                    return C0008R$drawable.op_custom_aodpreview_digital_1;
                case 3:
                    return C0008R$drawable.op_custom_aodpreview_digital_2;
                case 4:
                    return C0008R$drawable.op_custom_aodpreview_text_clock;
                case 5:
                    return C0008R$drawable.op_custom_aodpreview_bold;
                case 6:
                    return C0008R$drawable.op_custom_aodpreview_analog_1;
                case 7:
                    return C0008R$drawable.op_custom_aodpreview_analog_2;
                case 8:
                    return C0008R$drawable.op_custom_aodpreview_analog_3;
                case 9:
                    return C0008R$drawable.op_custom_aodpreview_minimalism_1;
                case 10:
                    return C0008R$drawable.op_custom_aodpreview_minimalism_2;
                case 11:
                    return C0008R$drawable.op_custom_aodpreview_parsons;
                default:
                    return C0008R$drawable.op_custom_aodpreview_default;
            }
        } else if (!OPThemeUtils.isSupportMclTheme() || TextUtils.equals("18801", SystemProperties.get("ro.boot.project_name"))) {
            return C0008R$drawable.op_custom_aodpreview_analog_1;
        } else {
            return C0008R$drawable.op_custom_aodpreview_mcl;
        }
    }

    private int getFodAnimStyleImage() {
        int intForUser = Settings.System.getIntForUser(this.mContext.getContentResolver(), "op_custom_unlock_animation_style", 0, -2);
        if (intForUser == 0) {
            return C0008R$drawable.op_custom_fingeprint_preview_cosmos;
        }
        if (intForUser == 1) {
            return C0008R$drawable.op_custom_fingeprint_preview_ripple;
        }
        if (intForUser == 2) {
            return C0008R$drawable.op_custom_fingeprint_preview_stripe;
        }
        if (intForUser == 3) {
            return C0008R$drawable.op_custom_fingeprint_preview_00;
        }
        if (intForUser == 4) {
            return C0008R$drawable.op_custom_fingeprint_preview_energy;
        }
        if (intForUser != 9) {
            return C0008R$drawable.op_custom_fingeprint_preview_cosmos;
        }
        return C0008R$drawable.op_custom_fingeprint_preview_04;
    }

    private int getHorizonLightStyleImage() {
        int intForUser = Settings.System.getIntForUser(this.mContext.getContentResolver(), "op_custom_horizon_light_animation_style", 0, -2);
        if (intForUser == 0) {
            return C0008R$drawable.op_custom_horizon_light_preview_blue;
        }
        if (intForUser == 1) {
            return C0008R$drawable.op_custom_horizon_light_preview_red;
        }
        if (intForUser == 2) {
            return C0008R$drawable.op_custom_horizon_light_preview_gold;
        }
        if (intForUser == 3) {
            return C0008R$drawable.op_custom_horizon_light_preview_purple;
        }
        if (intForUser != 10) {
            return C0008R$drawable.op_custom_horizon_light_preview_blue;
        }
        return C0008R$drawable.op_custom_horizon_light_preview_mcl;
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == C0010R$id.theme_wallpaper) {
            Intent intent = new Intent();
            intent.setClassName("net.oneplus.launcher", "net.oneplus.launcher.wallpaper.picker.WallpaperPickerActivity");
            intent.addFlags(268435456);
            getContext().startActivity(intent);
            return;
        }
        boolean z = true;
        if (id == C0010R$id.theme_clock_style) {
            boolean z2 = 1 == Settings.System.getIntForUser(getContext().getContentResolver(), "single_tap_show_aod_enabled", 1, ActivityManager.getCurrentUser());
            boolean z3 = 1 == Settings.System.getIntForUser(getContext().getContentResolver(), "pickup_show_aod_enabled", 1, ActivityManager.getCurrentUser());
            boolean z4 = IS_SUPPORT_AOD_ALWAYS_ON && Settings.Secure.getIntForUser(getContext().getContentResolver(), "always_on_state", 0, ActivityManager.getCurrentUser()) != 0;
            if (z2 || z3 || z4) {
                z = false;
            }
            if (z) {
                Snackbar make = Snackbar.make(view, getContext().getString(C0017R$string.oneplus_theme_clock_snake_tip), 0);
                make.setAction(getContext().getString(C0017R$string.oneplus_theme_clock_snake_action), new View.OnClickListener() {
                    /* class com.oneplus.settings.ui.$$Lambda$OPThemeIconPreference$1gv9y775WOwpXD6wQNZOPFoL1jI */

                    public final void onClick(View view) {
                        OPThemeIconPreference.this.lambda$onClick$0$OPThemeIconPreference(view);
                    }
                });
                make.show();
                return;
            }
            SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
            subSettingLauncher.setDestination(OPCustomClockSettings.class.getName());
            subSettingLauncher.setSourceMetricsCategory(9999);
            subSettingLauncher.setTitleRes(C0017R$string.oneplus_theme_clock);
            subSettingLauncher.launch();
        } else if (id == C0010R$id.theme_fingerprint) {
            if (!Utils.getFingerprintManagerOrNull(this.mContext).hasEnrolledFingerprints()) {
                Snackbar make2 = Snackbar.make(view, getContext().getString(C0017R$string.oneplus_theme_fingerprint_snake_tip), 0);
                make2.setAction(getContext().getString(C0017R$string.oneplus_theme_fingerprint_snake_action), new View.OnClickListener() {
                    /* class com.oneplus.settings.ui.$$Lambda$OPThemeIconPreference$2hUhkUR3NvOGZPRMONCnL7jutRw */

                    public final void onClick(View view) {
                        OPThemeIconPreference.this.lambda$onClick$1$OPThemeIconPreference(view);
                    }
                });
                make2.show();
                return;
            }
            SubSettingLauncher subSettingLauncher2 = new SubSettingLauncher(this.mContext);
            subSettingLauncher2.setDestination(OPCustomFingerprintAnimSettings.class.getName());
            subSettingLauncher2.setSourceMetricsCategory(9999);
            subSettingLauncher2.setTitleRes(C0017R$string.oneplus_fingerprint_animation_effect_title);
            subSettingLauncher2.launch();
        } else if (id == C0010R$id.theme_notification) {
            if (Settings.Secure.getIntForUser(getContext().getContentResolver(), "notification_wake_enabled", 1, ActivityManager.getCurrentUser()) != 1) {
                z = false;
            }
            if (!z) {
                Snackbar make3 = Snackbar.make(view, getContext().getString(C0017R$string.aod_horizon_light_snake_tip), 0);
                make3.setAction(getContext().getString(C0017R$string.aod_horizon_light_snake_action), new View.OnClickListener() {
                    /* class com.oneplus.settings.ui.$$Lambda$OPThemeIconPreference$32z1EioBQt9cLeskTiEIfOEOrw0 */

                    public final void onClick(View view) {
                        OPThemeIconPreference.this.lambda$onClick$2$OPThemeIconPreference(view);
                    }
                });
                make3.show();
                return;
            }
            SubSettingLauncher subSettingLauncher3 = new SubSettingLauncher(this.mContext);
            subSettingLauncher3.setDestination(OPCustomNotificationAnimSettings.class.getName());
            subSettingLauncher3.setSourceMetricsCategory(9999);
            subSettingLauncher3.setTitleRes(C0017R$string.aod_horizon_light);
            subSettingLauncher3.launch();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onClick$0 */
    public /* synthetic */ void lambda$onClick$0$OPThemeIconPreference(View view) {
        Intent intent = new Intent("com.oneplus.action.INTENT_TRANSIT_ACTIVITY");
        intent.putExtra("intent_action", "com.android.settings.SEARCH_RESULT_TRAMPOLINE");
        intent.putExtra(":settings:show_fragment", "com.android.settings.DisplaySettings");
        if (OPUtils.isSupportCustomFingerprint()) {
            intent.putExtra(":settings:fragment_args_key", "doze_801");
        } else {
            intent.putExtra(":settings:fragment_args_key", "doze");
        }
        getContext().startActivity(intent);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onClick$1 */
    public /* synthetic */ void lambda$onClick$1$OPThemeIconPreference(View view) {
        Intent intent = new Intent("com.oneplus.action.INTENT_TRANSIT_ACTIVITY");
        intent.putExtra("intent_action", "com.android.settings.SEARCH_RESULT_TRAMPOLINE");
        intent.putExtra(":settings:show_fragment", "com.android.settings.security.SecuritySettings");
        intent.putExtra(":settings:fragment_args_key", "fingerprint_settings");
        getContext().startActivity(intent);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onClick$2 */
    public /* synthetic */ void lambda$onClick$2$OPThemeIconPreference(View view) {
        Intent intent = new Intent("com.oneplus.action.INTENT_TRANSIT_ACTIVITY");
        intent.putExtra("intent_action", "com.android.settings.SEARCH_RESULT_TRAMPOLINE");
        intent.putExtra(":settings:show_fragment", "com.android.settings.DisplaySettings");
        if (OPUtils.isSupportCustomFingerprint()) {
            intent.putExtra(":settings:fragment_args_key", "doze_801");
        } else {
            intent.putExtra(":settings:fragment_args_key", "doze");
        }
        getContext().startActivity(intent);
    }
}
