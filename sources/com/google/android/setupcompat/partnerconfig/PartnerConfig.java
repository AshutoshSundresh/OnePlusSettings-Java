package com.google.android.setupcompat.partnerconfig;

public enum PartnerConfig {
    CONFIG_STATUS_BAR_BACKGROUND("setup_compat_status_bar_background", ResourceType.DRAWABLE),
    CONFIG_LIGHT_STATUS_BAR("setup_compat_light_status_bar", ResourceType.BOOL),
    CONFIG_NAVIGATION_BAR_BG_COLOR("setup_compat_navigation_bar_bg_color", ResourceType.COLOR),
    CONFIG_FOOTER_BAR_BG_COLOR("setup_compat_footer_bar_bg_color", ResourceType.COLOR),
    CONFIG_LIGHT_NAVIGATION_BAR("setup_compat_light_navigation_bar", ResourceType.BOOL),
    CONFIG_FOOTER_BUTTON_FONT_FAMILY("setup_compat_footer_button_font_family", ResourceType.STRING),
    CONFIG_FOOTER_BUTTON_ICON_ADD_ANOTHER("setup_compat_footer_button_icon_add_another", ResourceType.DRAWABLE),
    CONFIG_FOOTER_BUTTON_ICON_CANCEL("setup_compat_footer_button_icon_cancel", ResourceType.DRAWABLE),
    CONFIG_FOOTER_BUTTON_ICON_CLEAR("setup_compat_footer_button_icon_clear", ResourceType.DRAWABLE),
    CONFIG_FOOTER_BUTTON_ICON_DONE("setup_compat_footer_button_icon_done", ResourceType.DRAWABLE),
    CONFIG_FOOTER_BUTTON_ICON_NEXT("setup_compat_footer_button_icon_next", ResourceType.DRAWABLE),
    CONFIG_FOOTER_BUTTON_ICON_OPT_IN("setup_compat_footer_button_icon_opt_in", ResourceType.DRAWABLE),
    CONFIG_FOOTER_BUTTON_ICON_SKIP("setup_compat_footer_button_icon_skip", ResourceType.DRAWABLE),
    CONFIG_FOOTER_BUTTON_ICON_STOP("setup_compat_footer_button_icon_stop", ResourceType.DRAWABLE),
    CONFIG_FOOTER_BUTTON_PADDING_TOP("setup_compat_footer_button_padding_top", ResourceType.DIMENSION),
    CONFIG_FOOTER_BUTTON_PADDING_BOTTOM("setup_compat_footer_button_padding_bottom", ResourceType.DIMENSION),
    CONFIG_FOOTER_BUTTON_RADIUS("setup_compat_footer_button_radius", ResourceType.DIMENSION),
    CONFIG_FOOTER_BUTTON_RIPPLE_COLOR_ALPHA("setup_compat_footer_button_ripple_alpha", ResourceType.FRACTION),
    CONFIG_FOOTER_BUTTON_TEXT_SIZE("setup_compat_footer_button_text_size", ResourceType.DIMENSION),
    CONFIG_FOOTER_BUTTON_DISABLED_ALPHA("setup_compat_footer_button_disabled_alpha", ResourceType.FRACTION),
    CONFIG_FOOTER_BUTTON_DISABLED_BG_COLOR("setup_compat_footer_button_disabled_bg_color", ResourceType.COLOR),
    CONFIG_FOOTER_PRIMARY_BUTTON_BG_COLOR("setup_compat_footer_primary_button_bg_color", ResourceType.COLOR),
    CONFIG_FOOTER_PRIMARY_BUTTON_TEXT_COLOR("setup_compat_footer_primary_button_text_color", ResourceType.COLOR),
    CONFIG_FOOTER_SECONDARY_BUTTON_BG_COLOR("setup_compat_footer_secondary_button_bg_color", ResourceType.COLOR),
    CONFIG_FOOTER_SECONDARY_BUTTON_TEXT_COLOR("setup_compat_footer_secondary_button_text_color", ResourceType.COLOR),
    CONFIG_LAYOUT_BACKGROUND_COLOR("setup_design_layout_bg_color", ResourceType.COLOR),
    CONFIG_HEADER_TEXT_COLOR("setup_design_header_text_color", ResourceType.COLOR),
    CONFIG_HEADER_TEXT_SIZE("setup_design_header_text_size", ResourceType.DIMENSION),
    CONFIG_HEADER_FONT_FAMILY("setup_design_header_font_family", ResourceType.STRING),
    CONFIG_LAYOUT_GRAVITY("setup_design_layout_gravity", ResourceType.STRING),
    CONFIG_HEADER_AREA_BACKGROUND_COLOR("setup_design_header_area_background_color", ResourceType.COLOR),
    CONFIG_DESCRIPTION_TEXT_SIZE("setup_design_description_text_size", ResourceType.DIMENSION),
    CONFIG_DESCRIPTION_TEXT_COLOR("setup_design_description_text_color", ResourceType.COLOR),
    CONFIG_DESCRIPTION_LINK_TEXT_COLOR("setup_design_description_link_text_color", ResourceType.COLOR),
    CONFIG_DESCRIPTION_FONT_FAMILY("setup_design_description_font_family", ResourceType.STRING),
    CONFIG_CONTENT_TEXT_SIZE("setup_design_content_text_size", ResourceType.DIMENSION),
    CONFIG_CONTENT_TEXT_COLOR("setup_design_content_text_color", ResourceType.COLOR),
    CONFIG_CONTENT_LINK_TEXT_COLOR("setup_design_content_link_text_color", ResourceType.COLOR),
    CONFIG_CONTENT_FONT_FAMILY("setup_design_content_font_family", ResourceType.STRING),
    CONFIG_CONTENT_LAYOUT_GRAVITY("setup_design_content_layout_gravity", ResourceType.STRING),
    CONFIG_PROGRESS_ILLUSTRATION_DEFAULT("progress_illustration_custom_default", ResourceType.ILLUSTRATION),
    CONFIG_PROGRESS_ILLUSTRATION_ACCOUNT("progress_illustration_custom_account", ResourceType.ILLUSTRATION),
    CONFIG_PROGRESS_ILLUSTRATION_CONNECTION("progress_illustration_custom_connection", ResourceType.ILLUSTRATION),
    CONFIG_PROGRESS_ILLUSTRATION_UPDATE("progress_illustration_custom_update", ResourceType.ILLUSTRATION),
    CONFIG_PROGRESS_ILLUSTRATION_DISPLAY_MINIMUM_MS("progress_illustration_display_minimum_ms", ResourceType.INTEGER);
    
    private final String resourceName;
    private final ResourceType resourceType;

    public enum ResourceType {
        INTEGER,
        BOOL,
        COLOR,
        DRAWABLE,
        STRING,
        DIMENSION,
        FRACTION,
        ILLUSTRATION
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    private PartnerConfig(String str, ResourceType resourceType2) {
        this.resourceName = str;
        this.resourceType = resourceType2;
    }
}
