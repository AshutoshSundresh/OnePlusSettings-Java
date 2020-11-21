package com.google.android.setupdesign.util;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;
import com.google.android.setupdesign.util.TextViewPartnerStyler;

public final class HeaderAreaStyler {
    public static void applyPartnerCustomizationHeaderStyle(TextView textView) {
        if (textView != null) {
            TextViewPartnerStyler.applyPartnerCustomizationStyle(textView, new TextViewPartnerStyler.TextPartnerConfigs(PartnerConfig.CONFIG_HEADER_TEXT_COLOR, null, PartnerConfig.CONFIG_HEADER_TEXT_SIZE, PartnerConfig.CONFIG_HEADER_FONT_FAMILY, PartnerStyleHelper.getLayoutGravity(textView.getContext())));
        }
    }

    public static void applyPartnerCustomizationHeaderAreaStyle(ViewGroup viewGroup) {
        if (viewGroup != null) {
            Context context = viewGroup.getContext();
            viewGroup.setBackgroundColor(PartnerConfigHelper.get(context).getColor(context, PartnerConfig.CONFIG_HEADER_AREA_BACKGROUND_COLOR));
        }
    }

    public static void applyPartnerCustomizationIconStyle(ImageView imageView) {
        int layoutGravity;
        if (imageView != null && (layoutGravity = PartnerStyleHelper.getLayoutGravity(imageView.getContext())) != 0) {
            setGravity(imageView, layoutGravity);
        }
    }

    private static void setGravity(ImageView imageView, int i) {
        if (imageView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.gravity = i;
            imageView.setLayoutParams(layoutParams);
        }
    }
}
