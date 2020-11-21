package com.android.settings.homepage.contextualcards.conditional;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.os.UserHandle;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.TetherSettings;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import java.util.Objects;

public class HotspotConditionController implements ConditionalCardController {
    static final int ID = Objects.hash("HotspotConditionController");
    private static final IntentFilter WIFI_AP_STATE_FILTER = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
    private final Context mAppContext;
    private final ConditionManager mConditionManager;
    private final Receiver mReceiver = new Receiver();
    private final WifiManager mWifiManager;

    public HotspotConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return (long) ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        return this.mWifiManager.isWifiApEnabled();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(context);
        subSettingLauncher.setDestination(TetherSettings.class.getName());
        subSettingLauncher.setSourceMetricsCategory(35);
        subSettingLauncher.setTitleRes(C0017R$string.tether_settings_title_all);
        subSettingLauncher.launch();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mAppContext, "no_config_tethering", UserHandle.myUserId());
        if (checkIfRestrictionEnforced != null) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mAppContext, checkIfRestrictionEnforced);
        } else {
            ((ConnectivityManager) this.mAppContext.getSystemService("connectivity")).stopTethering(0);
        }
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder builder = new ConditionalContextualCard.Builder();
        builder.setConditionId((long) ID);
        builder.setMetricsConstant(382);
        builder.setActionText(this.mAppContext.getText(C0017R$string.oneplus_condition_hotspot_turn_off));
        builder.setName(this.mAppContext.getPackageName() + "/" + ((Object) this.mAppContext.getText(C0017R$string.condition_hotspot_title)));
        builder.setTitleText(this.mAppContext.getText(C0017R$string.condition_hotspot_title).toString());
        builder.setSummaryText(getSsid().toString());
        builder.setIconDrawable(this.mAppContext.getDrawable(C0008R$drawable.ic_hotspot));
        builder.setViewType(ConditionContextualCardRenderer.VIEW_TYPE_FULL_WIDTH);
        return builder.build();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
        this.mAppContext.registerReceiver(this.mReceiver, WIFI_AP_STATE_FILTER);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
        this.mAppContext.unregisterReceiver(this.mReceiver);
    }

    private CharSequence getSsid() {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        if (softApConfiguration == null) {
            return "";
        }
        return softApConfiguration.getSsid();
    }

    public class Receiver extends BroadcastReceiver {
        public Receiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(intent.getAction())) {
                HotspotConditionController.this.mConditionManager.onConditionChanged();
            }
        }
    }
}
