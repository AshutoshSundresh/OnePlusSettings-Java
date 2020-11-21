package com.android.settings.homepage.contextualcards.conditional;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.PreferenceManager;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.homepage.contextualcards.ContextualCard;
import com.android.settings.homepage.contextualcards.conditional.ConditionalContextualCard;
import com.oneplus.settings.utils.OPUtils;
import java.util.Objects;

public class CloudConditionController implements ConditionalCardController {
    static final int ID = Objects.hash("CloudConditionController");
    private final Context mAppContext;
    private final ConditionManager mConditionManager;

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void startMonitoringStateChange() {
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void stopMonitoringStateChange() {
    }

    public CloudConditionController(Context context, ConditionManager conditionManager) {
        this.mAppContext = context;
        this.mConditionManager = conditionManager;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public long getId() {
        return (long) ID;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public boolean isDisplayable() {
        if (OPUtils.isAppExist(this.mAppContext, "com.oneplus.cloud") && !OPUtils.isGuestMode()) {
            return getCloudTipsNeed(this.mAppContext) && TextUtils.isEmpty(getOnePlusTokenForProvider(this.mAppContext));
        }
        if (OPUtils.isAppExist(this.mAppContext, "com.heytap.cloud") && !OPUtils.isGuestMode()) {
            return getCloudTipsNeed(this.mAppContext) && TextUtils.isEmpty(getOPlusTokenForProvider(this.mAppContext));
        }
        Log.d("CloudConditionController", "isDisplayable false");
        return false;
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onPrimaryClick(Context context) {
        OPUtils.sendAnalytics("guide_click", "click", "0");
        if (OPUtils.isAppExist(context, "com.oneplus.cloud")) {
            Intent intent = new Intent("android.intent.action.ONEPLUSCLOUD");
            intent.setClassName("com.oneplus.cloud", "com.oneplus.cloud.activity.OPMainActivity");
            context.startActivity(intent);
        } else if (OPUtils.isAppExist(context, "com.heytap.cloud")) {
            Intent intent2 = new Intent("intent.action.ocloud.MAIN");
            intent2.setPackage("com.heytap.cloud");
            context.startActivity(intent2);
        }
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public void onActionClick() {
        OPUtils.sendAnalytics("guide_click", "click", "1");
        Log.d("CloudConditionController", "onActionClick dimiss clould condition");
        applyCloudTipsNeed(this.mAppContext, false);
        this.mConditionManager.onConditionChanged();
    }

    @Override // com.android.settings.homepage.contextualcards.conditional.ConditionalCardController
    public ContextualCard buildContextualCard() {
        ConditionalContextualCard.Builder builder = new ConditionalContextualCard.Builder();
        builder.setConditionId((long) ID);
        builder.setMetricsConstant(9999);
        builder.setActionText(this.mAppContext.getText(C0017R$string.condition_cloud_turn_off));
        builder.setName(this.mAppContext.getPackageName() + "/" + ((Object) this.mAppContext.getText(C0017R$string.condition_cloud_title)));
        builder.setTitleText(this.mAppContext.getText(C0017R$string.condition_cloud_title).toString());
        builder.setSummaryText(this.mAppContext.getText(C0017R$string.condition_cloud_summary).toString());
        builder.setIconDrawable(this.mAppContext.getDrawable(C0008R$drawable.ic_settings_cloud));
        builder.setViewType(ConditionContextualCardRenderer.VIEW_TYPE_FULL_WIDTH);
        return builder.build();
    }

    private String getOPlusTokenForProvider(Context context) {
        Bundle bundle;
        Bundle bundle2 = new Bundle();
        bundle2.putString("extra_package_name", context.getPackageName());
        try {
            bundle = context.getContentResolver().call(Uri.parse("content://com.oneplus.account.provider.open"), "get_account_o_token", "", bundle2);
        } catch (Exception e) {
            e.printStackTrace();
            bundle = null;
        }
        if (bundle == null) {
            return null;
        }
        String string = bundle.getString("oplustoken", null);
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        if (!TextUtils.isEmpty(string)) {
            applyCloudTipsNeed(context, false);
        }
        return string;
    }

    private String getOnePlusTokenForProvider(Context context) {
        Bundle bundle;
        Bundle bundle2 = new Bundle();
        bundle2.putString("extra_package_name", context.getPackageName());
        try {
            bundle = context.getContentResolver().call(Uri.parse("content://com.oneplus.account.provider.open"), "get_account_oneplus_token", "", bundle2);
        } catch (Exception e) {
            e.printStackTrace();
            bundle = null;
        }
        if (bundle == null) {
            return null;
        }
        String string = bundle.getString("token", null);
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        if (!TextUtils.isEmpty(string)) {
            applyCloudTipsNeed(context, false);
        }
        Log.d("CloudConditionController", "getOnePlusTokenForProvider token : " + string);
        return string;
    }

    private void applyCloudTipsNeed(Context context, boolean z) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean("cloud_tips_need", z);
        edit.apply();
    }

    private boolean getCloudTipsNeed(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d("CloudConditionController", "getCloudTipsNeed cloud_tips_need : " + defaultSharedPreferences.getBoolean("cloud_tips_need", true));
        return defaultSharedPreferences.getBoolean("cloud_tips_need", true);
    }
}
