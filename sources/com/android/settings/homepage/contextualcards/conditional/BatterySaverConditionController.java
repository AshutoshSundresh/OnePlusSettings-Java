package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import android.os.PowerManager;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.fuelgauge.BatterySaverReceiver;
import com.android.settings.fuelgauge.batterysaver.BatterySaverSettings;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import com.android.settingslib.fuelgauge.BatterySaverUtils;
import java.util.Objects;

public class BatterySaverConditionController implements ConditionalCardController, BatterySaverReceiver.BatterySaverListener {
    static final int ID = Objects.hash("BatterySaverConditionController");
    private final Context mAppContext;
    private final ConditionManager mConditionManager;
    private final PowerManager mPowerManager;
    private final BatterySaverReceiver mReceiver;

    @Override // com.android.settings.fuelgauge.BatterySaverReceiver.BatterySaverListener
    public void onBatteryChanged(boolean z) {
    }

    public BatterySaverConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        BatterySaverReceiver batterySaverReceiver = new BatterySaverReceiver(context);
        this.mReceiver = batterySaverReceiver;
        batterySaverReceiver.setBatterySaverListener(this);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return (long) ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        return this.mPowerManager.isPowerSaveMode();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(context);
        subSettingLauncher.setDestination(BatterySaverSettings.class.getName());
        subSettingLauncher.setSourceMetricsCategory(35);
        subSettingLauncher.setTitleRes(C0017R$string.battery_saver);
        subSettingLauncher.launch();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
        BatterySaverUtils.setPowerSaveMode(this.mAppContext, false, false);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder builder = new ConditionalContextualCard.Builder();
        builder.setConditionId((long) ID);
        builder.setMetricsConstant(379);
        builder.setActionText(this.mAppContext.getText(C0017R$string.oneplus_condition_battery_turn_off));
        builder.setName(this.mAppContext.getPackageName() + "/" + ((Object) this.mAppContext.getText(C0017R$string.condition_battery_title)));
        builder.setTitleText(this.mAppContext.getText(C0017R$string.condition_battery_title).toString());
        builder.setSummaryText(this.mAppContext.getText(C0017R$string.oneplus_condition_battery_summary).toString());
        builder.setIconDrawable(this.mAppContext.getDrawable(C0008R$drawable.ic_battery_saver_accent_24dp));
        builder.setViewType(ConditionContextualCardRenderer.VIEW_TYPE_FULL_WIDTH);
        return builder.build();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
        this.mReceiver.setListening(true);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
        this.mReceiver.setListening(false);
    }

    @Override // com.android.settings.fuelgauge.BatterySaverReceiver.BatterySaverListener
    public void onPowerSaveModeChanged() {
        this.mConditionManager.onConditionChanged();
    }
}
