package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.NightDisplayListener;
import android.provider.Settings;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import com.oneplus.settings.better.OPNightMode;
import java.util.Objects;

public class NightDisplayConditionController implements ConditionalCardController, NightDisplayListener.Callback {
    static final int ID = Objects.hash("NightDisplayConditionController");
    private final Context mAppContext;
    private final ColorDisplayManager mColorDisplayManager;
    private final ConditionManager mConditionManager;
    private final NightDisplayListener mNightDisplayListener;

    public NightDisplayConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        this.mColorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
        this.mNightDisplayListener = new NightDisplayListener(context);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return (long) ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        return this.mColorDisplayManager.isNightDisplayActivated() && !(Settings.Secure.getInt(this.mAppContext.getContentResolver(), "accessibility_display_daltonizer_enabled", 12) == 1) && !(Settings.Secure.getInt(this.mAppContext.getContentResolver(), "accessibility_display_inversion_enabled", 0) == 1);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(context);
        subSettingLauncher.setDestination(OPNightMode.class.getName());
        subSettingLauncher.setSourceMetricsCategory(1502);
        subSettingLauncher.setTitleRes(C0017R$string.night_display_title);
        subSettingLauncher.launch();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
        this.mColorDisplayManager.setNightDisplayActivated(false);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder builder = new ConditionalContextualCard.Builder();
        builder.setConditionId((long) ID);
        builder.setMetricsConstant(492);
        builder.setActionText(this.mAppContext.getText(C0017R$string.oneplus_condition_night_mode_turn_off));
        builder.setName(this.mAppContext.getPackageName() + "/" + ((Object) this.mAppContext.getText(C0017R$string.condition_night_display_title)));
        builder.setTitleText(this.mAppContext.getText(C0017R$string.condition_night_display_title).toString());
        builder.setSummaryText(this.mAppContext.getText(C0017R$string.condition_night_display_summary).toString());
        builder.setIconDrawable(this.mAppContext.getDrawable(C0008R$drawable.ic_settings_night_display));
        builder.setViewType(ConditionContextualCardRenderer.VIEW_TYPE_FULL_WIDTH);
        return builder.build();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
        this.mNightDisplayListener.setCallback(this);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
        this.mNightDisplayListener.setCallback((NightDisplayListener.Callback) null);
    }

    public void onActivated(boolean z) {
        this.mConditionManager.onConditionChanged();
    }
}
