package com.android.settings.homepage.contextualcards.conditional;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.service.notification.ScheduleCalendar;
import android.service.notification.ZenModeConfig;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import com.android.settings.notification.zen.ZenModeSettings;
import java.util.Objects;

public class DndConditionCardController implements ConditionalCardController {
    static final IntentFilter DND_FILTER = new IntentFilter("android.app.action.INTERRUPTION_FILTER_CHANGED_INTERNAL");
    static final int ID = Objects.hash("DndConditionCardController");
    private static ZenModeConfigWrapper mZenModeConfigWrapper;
    private final Context mAppContext;
    private final ConditionManager mConditionManager;
    private final NotificationManager mNotificationManager;
    private final Receiver mReceiver = new Receiver();

    public DndConditionCardController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        this.mNotificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
        mZenModeConfigWrapper = new ZenModeConfigWrapper(this.mAppContext);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return (long) ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        return this.mNotificationManager.getZenMode() != 0;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
        this.mAppContext.registerReceiver(this.mReceiver, DND_FILTER);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
        this.mAppContext.unregisterReceiver(this.mReceiver);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(context);
        subSettingLauncher.setDestination(ZenModeSettings.class.getName());
        subSettingLauncher.setSourceMetricsCategory(1502);
        subSettingLauncher.setTitleRes(C0017R$string.zen_mode_settings_title);
        subSettingLauncher.launch();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
        this.mNotificationManager.setZenMode(0, null, "DndCondition");
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder builder = new ConditionalContextualCard.Builder();
        builder.setConditionId((long) ID);
        builder.setMetricsConstant(381);
        builder.setActionText(this.mAppContext.getText(C0017R$string.oneplus_dnd_condition_turn_off));
        builder.setName(this.mAppContext.getPackageName() + "/" + ((Object) this.mAppContext.getText(C0017R$string.condition_zen_title)));
        builder.setTitleText(this.mAppContext.getText(C0017R$string.condition_zen_title).toString());
        builder.setSummaryText(getSummary());
        builder.setIconDrawable(this.mAppContext.getDrawable(C0008R$drawable.ic_do_not_disturb_on_24dp));
        builder.setViewType(ConditionContextualCardRenderer.VIEW_TYPE_FULL_WIDTH);
        return builder.build();
    }

    public class Receiver extends BroadcastReceiver {
        public Receiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.app.action.INTERRUPTION_FILTER_CHANGED_INTERNAL".equals(intent.getAction())) {
                DndConditionCardController.this.mConditionManager.onConditionChanged();
            }
        }
    }

    private String getSummary() {
        return getDefaultStrings(this.mNotificationManager.getZenModeConfig());
    }

    private String getDefaultStrings(ZenModeConfig zenModeConfig) {
        ZenModeConfig.ZenRule zenRule = zenModeConfig.manualRule;
        String str = "";
        long j = -1;
        if (zenRule != null) {
            Uri uri = zenRule.conditionId;
            String str2 = zenRule.enabler;
            if (str2 != null) {
                String ownerCaption = mZenModeConfigWrapper.getOwnerCaption(str2);
                if (!ownerCaption.isEmpty()) {
                    str = this.mAppContext.getString(C0017R$string.zen_mode_settings_dnd_automatic_rule_app, ownerCaption);
                }
            } else if (uri == null) {
                return this.mAppContext.getString(C0017R$string.zen_mode_duration_summary_forever);
            } else {
                j = mZenModeConfigWrapper.parseManualRuleTime(uri);
                if (j > 0) {
                    CharSequence formattedTime = mZenModeConfigWrapper.getFormattedTime(j, this.mAppContext.getUserId());
                    str = this.mAppContext.getString(17041533, formattedTime);
                }
            }
        }
        for (ZenModeConfig.ZenRule zenRule2 : zenModeConfig.automaticRules.values()) {
            if (zenRule2.isAutomaticActive()) {
                if (!mZenModeConfigWrapper.isTimeRule(zenRule2.conditionId)) {
                    return this.mAppContext.getString(C0017R$string.zen_mode_settings_dnd_automatic_rule, zenRule2.name);
                }
                long parseAutomaticRuleEndTime = mZenModeConfigWrapper.parseAutomaticRuleEndTime(zenRule2.conditionId);
                if (parseAutomaticRuleEndTime > j) {
                    str = this.mAppContext.getString(C0017R$string.zen_mode_settings_dnd_automatic_rule, zenRule2.name);
                    j = parseAutomaticRuleEndTime;
                }
            }
        }
        return str;
    }

    /* access modifiers changed from: package-private */
    public static class ZenModeConfigWrapper {
        private final Context mContext;

        public ZenModeConfigWrapper(Context context) {
            this.mContext = context;
        }

        /* access modifiers changed from: protected */
        public String getOwnerCaption(String str) {
            return ZenModeConfig.getOwnerCaption(this.mContext, str);
        }

        /* access modifiers changed from: protected */
        public boolean isTimeRule(Uri uri) {
            return ZenModeConfig.isValidEventConditionId(uri) || ZenModeConfig.isValidScheduleConditionId(uri);
        }

        /* access modifiers changed from: protected */
        public CharSequence getFormattedTime(long j, int i) {
            return ZenModeConfig.getFormattedTime(this.mContext, j, isToday(j), i);
        }

        private boolean isToday(long j) {
            return ZenModeConfig.isToday(j);
        }

        /* access modifiers changed from: protected */
        public long parseManualRuleTime(Uri uri) {
            return ZenModeConfig.tryParseCountdownConditionId(uri);
        }

        /* access modifiers changed from: protected */
        public long parseAutomaticRuleEndTime(Uri uri) {
            if (ZenModeConfig.isValidEventConditionId(uri)) {
                return Long.MAX_VALUE;
            }
            if (!ZenModeConfig.isValidScheduleConditionId(uri)) {
                return -1;
            }
            ScheduleCalendar scheduleCalendar = ZenModeConfig.toScheduleCalendar(uri);
            long nextChangeTime = scheduleCalendar.getNextChangeTime(System.currentTimeMillis());
            if (scheduleCalendar.exitAtAlarm()) {
                long nextAlarm = DndConditionCardController.getNextAlarm(this.mContext);
                scheduleCalendar.maybeSetNextAlarm(System.currentTimeMillis(), nextAlarm);
                if (scheduleCalendar.shouldExitForAlarm(nextChangeTime)) {
                    return nextAlarm;
                }
            }
            return nextChangeTime;
        }
    }

    /* access modifiers changed from: private */
    public static long getNextAlarm(Context context) {
        AlarmManager.AlarmClockInfo nextAlarmClock = ((AlarmManager) context.getSystemService("alarm")).getNextAlarmClock(ActivityManager.getCurrentUser());
        if (nextAlarmClock != null) {
            return nextAlarmClock.getTriggerTime();
        }
        return 0;
    }
}
