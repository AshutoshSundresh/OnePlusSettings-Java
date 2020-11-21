package com.android.settings.sim;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0006R$color;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.HelpTrampoline;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.telephony.MobileNetworkActivity;

public class SimSelectNotification extends BroadcastReceiver {
    @VisibleForTesting
    public static final String ENABLE_MMS_NOTIFICATION_CHANNEL = "enable_mms_notification_channel";
    @VisibleForTesting
    public static final int ENABLE_MMS_NOTIFICATION_ID = 2;
    @VisibleForTesting
    public static final String SIM_SELECT_NOTIFICATION_CHANNEL = "sim_select_notification_channel";
    @VisibleForTesting
    public static final int SIM_SELECT_NOTIFICATION_ID = 1;
    @VisibleForTesting
    public static final String SIM_WARNING_NOTIFICATION_CHANNEL = "sim_warning_notification_channel";
    @VisibleForTesting
    public static final int SIM_WARNING_NOTIFICATION_ID = 3;

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            Log.w("SimSelectNotification", "Received unexpected intent with null action.");
            return;
        }
        char c = 65535;
        int hashCode = action.hashCode();
        if (hashCode != -2059608843) {
            if (hashCode == -1734760714 && action.equals("android.settings.ENABLE_MMS_DATA_REQUEST")) {
                c = 1;
            }
        } else if (action.equals("android.telephony.action.PRIMARY_SUBSCRIPTION_LIST_CHANGED")) {
            c = 0;
        }
        if (c == 0) {
            onPrimarySubscriptionListChanged(context, intent);
        } else if (c != 1) {
            Log.w("SimSelectNotification", "Received unexpected intent " + intent.getAction());
        } else {
            onEnableMmsDataRequest(context, intent);
        }
    }

    private void onEnableMmsDataRequest(Context context, Intent intent) {
        CharSequence charSequence;
        int intExtra = intent.getIntExtra("android.provider.extra.SUB_ID", -1);
        if (intExtra == Integer.MAX_VALUE) {
            intExtra = SubscriptionManager.getDefaultSmsSubscriptionId();
        }
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService("telephony_subscription_service");
        if (!subscriptionManager.isActiveSubscriptionId(intExtra)) {
            Log.w("SimSelectNotification", "onEnableMmsDataRequest invalid sub ID " + intExtra);
            return;
        }
        SubscriptionInfo activeSubscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(intExtra);
        if (activeSubscriptionInfo == null) {
            Log.w("SimSelectNotification", "onEnableMmsDataRequest null SubscriptionInfo for sub ID " + intExtra);
            return;
        }
        int intExtra2 = intent.getIntExtra("android.settings.extra.ENABLE_MMS_DATA_REQUEST_REASON", -1);
        if (intExtra2 == 0) {
            charSequence = context.getResources().getText(C0017R$string.enable_receiving_mms_notification_title);
        } else if (intExtra2 == 1) {
            charSequence = context.getResources().getText(C0017R$string.enable_sending_mms_notification_title);
        } else {
            Log.w("SimSelectNotification", "onEnableMmsDataRequest invalid request reason " + intExtra2);
            return;
        }
        if (((TelephonyManager) context.getSystemService("phone")).createForSubscriptionId(intExtra).isDataEnabledForApn(2)) {
            Log.w("SimSelectNotification", "onEnableMmsDataRequest MMS data already enabled on sub ID " + intExtra);
            return;
        }
        String string = context.getResources().getString(C0017R$string.enable_mms_notification_summary, SubscriptionUtil.getDisplayName(activeSubscriptionInfo));
        cancelEnableMmsNotification(context);
        createEnableMmsNotification(context, charSequence, string, intExtra);
    }

    private void onPrimarySubscriptionListChanged(Context context, Intent intent) {
        startSimSelectDialogIfNeeded(context, intent);
        sendSimCombinationWarningIfNeeded(context, intent);
    }

    private void startSimSelectDialogIfNeeded(Context context, Intent intent) {
        int intExtra = intent.getIntExtra("android.telephony.extra.DEFAULT_SUBSCRIPTION_SELECT_TYPE", 0);
        if (intExtra != 0) {
            cancelSimSelectNotification(context);
            createSimSelectNotification(context);
            if (intExtra == 4) {
                int slotIndex = SubscriptionManager.getSlotIndex(intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_ID", Integer.MAX_VALUE));
                Intent intent2 = new Intent(context, SimDialogActivity.class);
                intent2.addFlags(268435456);
                intent2.putExtra(SimDialogActivity.DIALOG_TYPE_KEY, 3);
                intent2.putExtra(SimDialogActivity.PREFERRED_SIM, slotIndex);
                context.startActivity(intent2);
            } else if (intExtra == 1) {
                Intent intent3 = new Intent(context, SimDialogActivity.class);
                intent3.addFlags(268435456);
                intent3.putExtra(SimDialogActivity.DIALOG_TYPE_KEY, 0);
                context.startActivity(intent3);
            }
        }
    }

    private void sendSimCombinationWarningIfNeeded(Context context, Intent intent) {
        if (intent.getIntExtra("android.telephony.extra.SIM_COMBINATION_WARNING_TYPE", 0) == 1) {
            cancelSimCombinationWarningNotification(context);
            createSimCombinationWarningNotification(context, intent);
        }
    }

    private void createSimSelectNotification(Context context) {
        Resources resources = context.getResources();
        NotificationChannel notificationChannel = new NotificationChannel(SIM_SELECT_NOTIFICATION_CHANNEL, resources.getText(C0017R$string.sim_selection_channel_title), 2);
        Notification.Builder autoCancel = new Notification.Builder(context, SIM_SELECT_NOTIFICATION_CHANNEL).setSmallIcon(C0008R$drawable.ic_sim_alert).setColor(context.getColor(C0006R$color.sim_noitification)).setContentTitle(resources.getText(C0017R$string.sim_notification_title)).setContentText(resources.getText(C0017R$string.sim_notification_summary)).setAutoCancel(true);
        Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
        intent.addFlags(268435456);
        autoCancel.setContentIntent(PendingIntent.getActivity(context, 0, intent, 268435456));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1, autoCancel.build());
    }

    public static void cancelSimSelectNotification(Context context) {
        ((NotificationManager) context.getSystemService("notification")).cancel(1);
    }

    private void createEnableMmsNotification(Context context, CharSequence charSequence, CharSequence charSequence2, int i) {
        NotificationChannel notificationChannel = new NotificationChannel(ENABLE_MMS_NOTIFICATION_CHANNEL, context.getResources().getText(C0017R$string.enable_mms_notification_channel_title), 4);
        Notification.Builder autoCancel = new Notification.Builder(context, ENABLE_MMS_NOTIFICATION_CHANNEL).setSmallIcon(C0008R$drawable.ic_settings_24dp).setColor(context.getColor(C0006R$color.sim_noitification)).setContentTitle(charSequence).setContentText(charSequence2).setStyle(new Notification.BigTextStyle().bigText(charSequence2)).setAutoCancel(true);
        Intent intent = new Intent("android.settings.MMS_MESSAGE_SETTING");
        intent.setClass(context, MobileNetworkActivity.class);
        intent.putExtra("android.provider.extra.SUB_ID", i);
        intent.addFlags(268435456);
        autoCancel.setContentIntent(PendingIntent.getActivity(context, 0, intent, 268435456));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(2, autoCancel.build());
    }

    private void cancelEnableMmsNotification(Context context) {
        ((NotificationManager) context.getSystemService("notification")).cancel(2);
    }

    private void createSimCombinationWarningNotification(Context context, Intent intent) {
        Resources resources = context.getResources();
        String stringExtra = intent.getStringExtra("android.telephony.extra.SIM_COMBINATION_NAMES");
        if (stringExtra != null) {
            String string = resources.getString(C0017R$string.dual_cdma_sim_warning_notification_summary, stringExtra);
            NotificationChannel notificationChannel = new NotificationChannel(SIM_WARNING_NOTIFICATION_CHANNEL, resources.getText(C0017R$string.dual_cdma_sim_warning_notification_channel_title), 4);
            Notification.Builder autoCancel = new Notification.Builder(context, SIM_WARNING_NOTIFICATION_CHANNEL).setSmallIcon(C0008R$drawable.ic_sim_alert).setColor(context.getColor(C0006R$color.sim_noitification)).setContentTitle(resources.getText(C0017R$string.sim_combination_warning_notification_title)).setContentText(string).setStyle(new Notification.BigTextStyle().bigText(string)).setAutoCancel(true);
            Intent intent2 = new Intent(context, HelpTrampoline.class);
            intent2.putExtra("android.intent.extra.TEXT", "help_uri_sim_combination_warning");
            autoCancel.setContentIntent(PendingIntent.getActivity(context, 0, intent2, 268435456));
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(3, autoCancel.build());
        }
    }

    public static void cancelSimCombinationWarningNotification(Context context) {
        ((NotificationManager) context.getSystemService(NotificationManager.class)).cancel(3);
    }
}
