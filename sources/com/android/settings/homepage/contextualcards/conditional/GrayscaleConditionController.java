package com.android.settings.homepage.contextualcards.conditional;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.ColorDisplayManager;
import android.os.UserHandle;
import android.util.Log;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import java.net.URISyntaxException;
import java.util.Objects;

public class GrayscaleConditionController implements ConditionalCardController {
    private static final IntentFilter GRAYSCALE_CHANGED_FILTER = new IntentFilter("android.settings.action.GRAYSCALE_CHANGED");
    static final int ID = Objects.hash("GrayscaleConditionController");
    private final Context mAppContext;
    private final ColorDisplayManager mColorDisplayManager;
    private final ConditionManager mConditionManager;
    private Intent mIntent;
    private final Receiver mReceiver = new Receiver();

    public GrayscaleConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
        this.mColorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return (long) ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        try {
            this.mIntent = Intent.parseUri(this.mAppContext.getString(C0017R$string.config_grayscale_settings_intent), 1);
            return this.mColorDisplayManager.isSaturationActivated();
        } catch (URISyntaxException e) {
            Log.w("GrayscaleCondition", "Failure parsing grayscale settings intent, skipping", e);
            return false;
        }
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        this.mIntent.setFlags(268435456);
        try {
            this.mAppContext.startActivity(this.mIntent);
        } catch (Exception e) {
            Log.w("GrayscaleCondition", "onPrimaryClick", e);
        }
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
        this.mColorDisplayManager.setSaturationLevel(100);
        sendBroadcast();
        this.mConditionManager.onConditionChanged();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder builder = new ConditionalContextualCard.Builder();
        builder.setConditionId((long) ID);
        builder.setMetricsConstant(1683);
        builder.setActionText(this.mAppContext.getText(C0017R$string.condition_turn_off));
        builder.setName(this.mAppContext.getPackageName() + "/" + ((Object) this.mAppContext.getText(C0017R$string.condition_grayscale_title)));
        builder.setTitleText(this.mAppContext.getText(C0017R$string.condition_grayscale_title).toString());
        builder.setSummaryText(this.mAppContext.getText(C0017R$string.condition_grayscale_summary).toString());
        builder.setIconDrawable(this.mAppContext.getDrawable(C0008R$drawable.ic_gray_scale_24dp));
        builder.setViewType(ConditionContextualCardRenderer.VIEW_TYPE_FULL_WIDTH);
        return builder.build();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
        this.mAppContext.registerReceiver(this.mReceiver, GRAYSCALE_CHANGED_FILTER, "android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS", null);
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
        this.mAppContext.unregisterReceiver(this.mReceiver);
    }

    private void sendBroadcast() {
        Intent intent = new Intent("android.settings.action.GRAYSCALE_CHANGED");
        intent.addFlags(16777216);
        this.mAppContext.sendBroadcastAsUser(intent, UserHandle.CURRENT, "android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS");
    }

    public class Receiver extends BroadcastReceiver {
        public Receiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.settings.action.GRAYSCALE_CHANGED".equals(intent.getAction())) {
                GrayscaleConditionController.this.mConditionManager.onConditionChanged();
            }
        }
    }
}
