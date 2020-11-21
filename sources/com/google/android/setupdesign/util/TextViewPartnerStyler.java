package com.google.android.setupdesign.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;
import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.partnerconfig.PartnerConfigHelper;

/* access modifiers changed from: package-private */
public final class TextViewPartnerStyler {
    public static void applyPartnerCustomizationStyle(TextView textView, TextPartnerConfigs textPartnerConfigs) {
        Typeface create;
        int color;
        int color2;
        if (textView != null && textPartnerConfigs != null) {
            Context context = textView.getContext();
            if (!(textPartnerConfigs.getTextColorConfig() == null || (color2 = PartnerConfigHelper.get(context).getColor(context, textPartnerConfigs.getTextColorConfig())) == 0)) {
                textView.setTextColor(color2);
            }
            if (!(textPartnerConfigs.getTextLinkedColorConfig() == null || (color = PartnerConfigHelper.get(context).getColor(context, textPartnerConfigs.getTextLinkedColorConfig())) == 0)) {
                textView.setLinkTextColor(color);
            }
            if (textPartnerConfigs.getTextSizeConfig() != null) {
                float dimension = PartnerConfigHelper.get(context).getDimension(context, textPartnerConfigs.getTextSizeConfig(), 0.0f);
                if (dimension > 0.0f) {
                    textView.setTextSize(0, dimension);
                }
            }
            if (!(textPartnerConfigs.getTextFontFamilyConfig() == null || (create = Typeface.create(PartnerConfigHelper.get(context).getString(context, textPartnerConfigs.getTextFontFamilyConfig()), 0)) == null)) {
                textView.setTypeface(create);
            }
            textView.setGravity(textPartnerConfigs.getTextGravity());
        }
    }

    public static class TextPartnerConfigs {
        private final PartnerConfig textColorConfig;
        private final PartnerConfig textFontFamilyConfig;
        private final int textGravity;
        private final PartnerConfig textLinkedColorConfig;
        private final PartnerConfig textSizeConfig;

        public TextPartnerConfigs(PartnerConfig partnerConfig, PartnerConfig partnerConfig2, PartnerConfig partnerConfig3, PartnerConfig partnerConfig4, int i) {
            this.textColorConfig = partnerConfig;
            this.textLinkedColorConfig = partnerConfig2;
            this.textSizeConfig = partnerConfig3;
            this.textFontFamilyConfig = partnerConfig4;
            this.textGravity = i;
        }

        public PartnerConfig getTextColorConfig() {
            return this.textColorConfig;
        }

        public PartnerConfig getTextLinkedColorConfig() {
            return this.textLinkedColorConfig;
        }

        public PartnerConfig getTextSizeConfig() {
            return this.textSizeConfig;
        }

        public PartnerConfig getTextFontFamilyConfig() {
            return this.textFontFamilyConfig;
        }

        public int getTextGravity() {
            return this.textGravity;
        }
    }
}
