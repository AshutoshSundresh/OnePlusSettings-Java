package com.oneplus.settings.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.OPColorPickerActivity;
import com.oneplus.settings.OPFontStyleActivity;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.utils.OPUtils;

public class OPThemePreference extends Preference implements View.OnTouchListener, View.OnClickListener {
    private Context mContext;
    private ImageView mThemeIcon1;
    private ImageView mThemeIcon2;
    private ImageView mThemeIcon3;
    private ImageView mThemeIcon4;

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    public OPThemePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(C0012R$layout.op_theme_layout_main);
        this.mContext = context;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ViewGroup viewGroup = (ViewGroup) preferenceViewHolder.findViewById(C0010R$id.theme_accent_color);
        viewGroup.setOnClickListener(this);
        viewGroup.requestDisallowInterceptTouchEvent(true);
        RadiusImageView radiusImageView = (RadiusImageView) preferenceViewHolder.findViewById(C0010R$id.theme_theme);
        radiusImageView.setImageResource(getColorThmeImage());
        radiusImageView.setOnClickListener(this);
        preferenceViewHolder.findViewById(C0010R$id.theme_shape).setOnClickListener(this);
        View findViewById = preferenceViewHolder.findViewById(C0010R$id.theme_font_style);
        findViewById.setOnClickListener(this);
        if (!OPUtils.isSupportFontStyleSetting()) {
            findViewById.setVisibility(8);
            preferenceViewHolder.findViewById(C0010R$id.font_label).setVisibility(8);
            preferenceViewHolder.findViewById(C0010R$id.theme_font_container).setVisibility(8);
            preferenceViewHolder.findViewById(C0010R$id.theme_font_text_container).setVisibility(8);
        }
        initViewStatus(preferenceViewHolder);
        initAppIconView(preferenceViewHolder);
    }

    private int getColorThmeImage() {
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "oem_black_mode", 0);
        if (i == 0) {
            return C0008R$drawable.op_img_tone_light;
        }
        if (i == 1) {
            return C0008R$drawable.op_img_tone_dark;
        }
        if (i != 2) {
            return C0008R$drawable.op_img_tone_light;
        }
        return C0008R$drawable.op_img_tone_color;
    }

    public void initViewStatus(PreferenceViewHolder preferenceViewHolder) {
        View findViewById = preferenceViewHolder.findViewById(C0010R$id.theme_accentcolor_checkbox);
        findViewById.setOnTouchListener(this);
        findViewById.setFocusable(false);
        View findViewById2 = preferenceViewHolder.findViewById(C0010R$id.theme_accentcolor_radiobutton);
        findViewById2.setOnTouchListener(this);
        findViewById2.setFocusable(false);
        View findViewById3 = preferenceViewHolder.findViewById(C0010R$id.theme_accentcolor_switch);
        findViewById3.setOnTouchListener(this);
        findViewById3.setFocusable(false);
        View findViewById4 = preferenceViewHolder.findViewById(C0010R$id.theme_accentcolor_seekbar);
        findViewById4.setOnTouchListener(this);
        findViewById4.setFocusable(false);
        View findViewById5 = preferenceViewHolder.findViewById(C0010R$id.theme_accentcolor_button);
        findViewById5.setOnTouchListener(this);
        findViewById5.setFocusable(false);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == C0010R$id.theme_accent_color) {
            if (OPUtils.isAndroidModeOn(this.mContext.getContentResolver())) {
                Toast.makeText(this.mContext, C0017R$string.oneplus_colorful_mode_cannot_change_color_accent, 0).show();
                return;
            }
            Intent intent = new Intent();
            intent.setClass(this.mContext, OPColorPickerActivity.class);
            this.mContext.startActivity(intent);
        } else if (id == C0010R$id.theme_iconpack) {
            Intent intent2 = new Intent();
            intent2.setClassName("net.oneplus.launcher", "net.oneplus.launcher.IconPackSelectorActivity");
            this.mContext.startActivity(intent2);
        } else if (id == C0010R$id.theme_theme) {
            Intent intent3 = new Intent();
            intent3.setClassName(OPMemberController.PACKAGE_NAME, "com.android.settings.Settings$OPCustomToneSettingsActivity");
            intent3.addFlags(268435456);
            this.mContext.startActivity(intent3);
        } else if (id == C0010R$id.theme_shape) {
            Intent intent4 = new Intent();
            intent4.setClassName(OPMemberController.PACKAGE_NAME, "com.android.settings.Settings$OPCustomShapeSettingsActivity");
            intent4.addFlags(268435456);
            this.mContext.startActivity(intent4);
        } else if (id == C0010R$id.theme_font_style) {
            Intent intent5 = new Intent();
            intent5.setClass(this.mContext, OPFontStyleActivity.class);
            this.mContext.startActivity(intent5);
        }
    }

    private Drawable getAppIcon(String str) {
        try {
            return this.mContext.getPackageManager().getApplicationIcon(str);
        } catch (Exception e) {
            Log.d("OPThemePreference", "getAppIcon e = " + e + " packagename = " + str);
            return null;
        }
    }

    private void initAppIconView(PreferenceViewHolder preferenceViewHolder) {
        preferenceViewHolder.findViewById(C0010R$id.theme_iconpack).setOnClickListener(this);
        this.mThemeIcon1 = (ImageView) preferenceViewHolder.findViewById(C0010R$id.theme_icon_1);
        this.mThemeIcon2 = (ImageView) preferenceViewHolder.findViewById(C0010R$id.theme_icon_2);
        this.mThemeIcon3 = (ImageView) preferenceViewHolder.findViewById(C0010R$id.theme_icon_3);
        this.mThemeIcon4 = (ImageView) preferenceViewHolder.findViewById(C0010R$id.theme_icon_4);
        Drawable appIcon = getAppIcon("com.android.dialer");
        if (appIcon != null) {
            this.mThemeIcon1.setImageDrawable(appIcon);
        }
        Drawable appIcon2 = getAppIcon("com.oneplus.mms");
        if (appIcon2 != null) {
            this.mThemeIcon2.setImageDrawable(appIcon2);
        }
        Drawable appIcon3 = getAppIcon("com.oneplus.deskclock");
        if (appIcon3 != null) {
            this.mThemeIcon3.setImageDrawable(appIcon3);
        }
        Drawable appIcon4 = getAppIcon("com.oneplus.camera");
        if (appIcon4 != null) {
            this.mThemeIcon4.setImageDrawable(appIcon4);
        }
    }
}
