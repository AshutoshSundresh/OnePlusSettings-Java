package com.android.settings.notification.zen;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.overlay.FeatureFactory;

public class ZenOnboardingActivity extends Activity {
    static final long ALWAYS_SHOW_THRESHOLD = 1209600000;
    static final String PREF_KEY_SUGGESTION_FIRST_DISPLAY_TIME = "pref_zen_suggestion_first_display_time_ms";
    View mKeepCurrentSetting;
    RadioButton mKeepCurrentSettingButton;
    private MetricsLogger mMetrics;
    View mNewSetting;
    RadioButton mNewSettingButton;
    private NotificationManager mNm;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setNotificationManager((NotificationManager) getSystemService(NotificationManager.class));
        setMetricsLogger(new MetricsLogger());
        Settings.Secure.putInt(getApplicationContext().getContentResolver(), "zen_settings_suggestion_viewed", 1);
        setupUI();
    }

    /* access modifiers changed from: protected */
    public void setupUI() {
        setContentView(C0012R$layout.zen_onboarding);
        this.mNewSetting = findViewById(C0010R$id.zen_onboarding_new_setting);
        this.mKeepCurrentSetting = findViewById(C0010R$id.zen_onboarding_current_setting);
        this.mNewSettingButton = (RadioButton) findViewById(C0010R$id.zen_onboarding_new_setting_button);
        this.mKeepCurrentSettingButton = (RadioButton) findViewById(C0010R$id.zen_onboarding_current_setting_button);
        AnonymousClass1 r0 = new View.OnClickListener() {
            /* class com.android.settings.notification.zen.ZenOnboardingActivity.AnonymousClass1 */

            public void onClick(View view) {
                ZenOnboardingActivity.this.mKeepCurrentSettingButton.setChecked(false);
                ZenOnboardingActivity.this.mNewSettingButton.setChecked(true);
            }
        };
        AnonymousClass2 r1 = new View.OnClickListener() {
            /* class com.android.settings.notification.zen.ZenOnboardingActivity.AnonymousClass2 */

            public void onClick(View view) {
                ZenOnboardingActivity.this.mKeepCurrentSettingButton.setChecked(true);
                ZenOnboardingActivity.this.mNewSettingButton.setChecked(false);
            }
        };
        this.mNewSetting.setOnClickListener(r0);
        this.mNewSettingButton.setOnClickListener(r0);
        this.mKeepCurrentSetting.setOnClickListener(r1);
        this.mKeepCurrentSettingButton.setOnClickListener(r1);
        this.mKeepCurrentSettingButton.setChecked(true);
        this.mMetrics.visible(1380);
    }

    /* access modifiers changed from: protected */
    public void setNotificationManager(NotificationManager notificationManager) {
        this.mNm = notificationManager;
    }

    /* access modifiers changed from: protected */
    public void setMetricsLogger(MetricsLogger metricsLogger) {
        this.mMetrics = metricsLogger;
    }

    public void launchSettings(View view) {
        this.mMetrics.action(1379);
        Intent intent = new Intent("android.settings.ZEN_MODE_SETTINGS");
        intent.addFlags(268468224);
        startActivity(intent);
    }

    public void save(View view) {
        NotificationManager.Policy notificationPolicy = this.mNm.getNotificationPolicy();
        if (this.mNewSettingButton.isChecked()) {
            this.mNm.setNotificationPolicy(new NotificationManager.Policy(notificationPolicy.priorityCategories | 16, 2, notificationPolicy.priorityMessageSenders, NotificationManager.Policy.getAllSuppressedVisualEffects()));
            this.mMetrics.action(1378);
        } else {
            this.mMetrics.action(1406);
        }
        Settings.Secure.putInt(getApplicationContext().getContentResolver(), "zen_settings_updated", 1);
        finishAndRemoveTask();
    }

    public static boolean isSuggestionComplete(Context context) {
        if (wasZenUpdated(context)) {
            return true;
        }
        if (showSuggestion(context) || withinShowTimeThreshold(context)) {
            return false;
        }
        return true;
    }

    private static boolean wasZenUpdated(Context context) {
        if (NotificationManager.Policy.areAllVisualEffectsSuppressed(((NotificationManager) context.getSystemService(NotificationManager.class)).getNotificationPolicy().suppressedVisualEffects)) {
            Settings.Secure.putInt(context.getContentResolver(), "zen_settings_updated", 1);
        }
        if (Settings.Secure.getInt(context.getContentResolver(), "zen_settings_updated", 0) != 0) {
            return true;
        }
        return false;
    }

    private static boolean showSuggestion(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "show_zen_settings_suggestion", 0) != 0;
    }

    private static boolean withinShowTimeThreshold(Context context) {
        long j;
        SharedPreferences sharedPrefs = FeatureFactory.getFactory(context).getSuggestionFeatureProvider(context).getSharedPrefs(context);
        long currentTimeMillis = System.currentTimeMillis();
        if (!sharedPrefs.contains(PREF_KEY_SUGGESTION_FIRST_DISPLAY_TIME)) {
            sharedPrefs.edit().putLong(PREF_KEY_SUGGESTION_FIRST_DISPLAY_TIME, currentTimeMillis).commit();
            j = currentTimeMillis;
        } else {
            j = sharedPrefs.getLong(PREF_KEY_SUGGESTION_FIRST_DISPLAY_TIME, -1);
        }
        long j2 = j + ALWAYS_SHOW_THRESHOLD;
        boolean z = currentTimeMillis < j2;
        Log.d("ZenOnboardingActivity", "still show zen suggestion based on time: " + z + " showTimeMs=" + j2);
        return z;
    }
}
