package com.google.android.setupcompat.internal;

import com.google.android.setupcompat.partnerconfig.PartnerConfig;
import com.google.android.setupcompat.template.FooterButton;

public class FooterButtonPartnerConfig {
    private final PartnerConfig buttonBackgroundConfig;
    private final PartnerConfig buttonDisableAlphaConfig;
    private final PartnerConfig buttonDisableBackgroundConfig;
    private final PartnerConfig buttonIconConfig;
    private final PartnerConfig buttonRadiusConfig;
    private final PartnerConfig buttonRippleColorAlphaConfig;
    private final PartnerConfig buttonTextColorConfig;
    private final PartnerConfig buttonTextSizeConfig;
    private final PartnerConfig buttonTextTypeFaceConfig;
    private final int partnerTheme;

    private FooterButtonPartnerConfig(int i, PartnerConfig partnerConfig, PartnerConfig partnerConfig2, PartnerConfig partnerConfig3, PartnerConfig partnerConfig4, PartnerConfig partnerConfig5, PartnerConfig partnerConfig6, PartnerConfig partnerConfig7, PartnerConfig partnerConfig8, PartnerConfig partnerConfig9) {
        this.partnerTheme = i;
        this.buttonTextColorConfig = partnerConfig5;
        this.buttonTextSizeConfig = partnerConfig6;
        this.buttonTextTypeFaceConfig = partnerConfig7;
        this.buttonBackgroundConfig = partnerConfig;
        this.buttonDisableAlphaConfig = partnerConfig2;
        this.buttonDisableBackgroundConfig = partnerConfig3;
        this.buttonRadiusConfig = partnerConfig8;
        this.buttonIconConfig = partnerConfig4;
        this.buttonRippleColorAlphaConfig = partnerConfig9;
    }

    public int getPartnerTheme() {
        return this.partnerTheme;
    }

    public PartnerConfig getButtonBackgroundConfig() {
        return this.buttonBackgroundConfig;
    }

    public PartnerConfig getButtonDisableAlphaConfig() {
        return this.buttonDisableAlphaConfig;
    }

    public PartnerConfig getButtonDisableBackgroundConfig() {
        return this.buttonDisableBackgroundConfig;
    }

    public PartnerConfig getButtonIconConfig() {
        return this.buttonIconConfig;
    }

    public PartnerConfig getButtonTextColorConfig() {
        return this.buttonTextColorConfig;
    }

    public PartnerConfig getButtonTextSizeConfig() {
        return this.buttonTextSizeConfig;
    }

    public PartnerConfig getButtonTextTypeFaceConfig() {
        return this.buttonTextTypeFaceConfig;
    }

    public PartnerConfig getButtonRadiusConfig() {
        return this.buttonRadiusConfig;
    }

    public PartnerConfig getButtonRippleColorAlphaConfig() {
        return this.buttonRippleColorAlphaConfig;
    }

    public static class Builder {
        private PartnerConfig buttonBackgroundConfig = null;
        private PartnerConfig buttonDisableAlphaConfig = null;
        private PartnerConfig buttonDisableBackgroundConfig = null;
        private PartnerConfig buttonIconConfig = null;
        private PartnerConfig buttonRadiusConfig = null;
        private PartnerConfig buttonRippleColorAlphaConfig = null;
        private PartnerConfig buttonTextColorConfig = null;
        private PartnerConfig buttonTextSizeConfig = null;
        private PartnerConfig buttonTextTypeFaceConfig = null;
        private final FooterButton footerButton;
        private int partnerTheme;

        public Builder(FooterButton footerButton2) {
            this.footerButton = footerButton2;
            this.partnerTheme = footerButton2.getTheme();
        }

        public Builder setButtonBackgroundConfig(PartnerConfig partnerConfig) {
            this.buttonBackgroundConfig = partnerConfig;
            return this;
        }

        public Builder setButtonDisableAlphaConfig(PartnerConfig partnerConfig) {
            this.buttonDisableAlphaConfig = partnerConfig;
            return this;
        }

        public Builder setButtonDisableBackgroundConfig(PartnerConfig partnerConfig) {
            this.buttonDisableBackgroundConfig = partnerConfig;
            return this;
        }

        public Builder setButtonIconConfig(PartnerConfig partnerConfig) {
            this.buttonIconConfig = partnerConfig;
            return this;
        }

        public Builder setTextColorConfig(PartnerConfig partnerConfig) {
            this.buttonTextColorConfig = partnerConfig;
            return this;
        }

        public Builder setTextSizeConfig(PartnerConfig partnerConfig) {
            this.buttonTextSizeConfig = partnerConfig;
            return this;
        }

        public Builder setTextTypeFaceConfig(PartnerConfig partnerConfig) {
            this.buttonTextTypeFaceConfig = partnerConfig;
            return this;
        }

        public Builder setButtonRadiusConfig(PartnerConfig partnerConfig) {
            this.buttonRadiusConfig = partnerConfig;
            return this;
        }

        public Builder setButtonRippleColorAlphaConfig(PartnerConfig partnerConfig) {
            this.buttonRippleColorAlphaConfig = partnerConfig;
            return this;
        }

        public Builder setPartnerTheme(int i) {
            this.partnerTheme = i;
            return this;
        }

        public FooterButtonPartnerConfig build() {
            return new FooterButtonPartnerConfig(this.partnerTheme, this.buttonBackgroundConfig, this.buttonDisableAlphaConfig, this.buttonDisableBackgroundConfig, this.buttonIconConfig, this.buttonTextColorConfig, this.buttonTextSizeConfig, this.buttonTextTypeFaceConfig, this.buttonRadiusConfig, this.buttonRippleColorAlphaConfig);
        }
    }
}
