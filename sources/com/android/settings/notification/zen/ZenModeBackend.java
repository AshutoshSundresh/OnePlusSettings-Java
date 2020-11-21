package com.android.settings.notification.zen;

import android.app.ActivityManager;
import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.icu.text.ListFormatter;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenPolicy;
import android.util.Log;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ZenModeBackend {
    public static final Comparator<Map.Entry<String, AutomaticZenRule>> RULE_COMPARATOR = new Comparator<Map.Entry<String, AutomaticZenRule>>() {
        /* class com.android.settings.notification.zen.ZenModeBackend.AnonymousClass1 */

        public int compare(Map.Entry<String, AutomaticZenRule> entry, Map.Entry<String, AutomaticZenRule> entry2) {
            boolean contains = ZenModeBackend.getDefaultRuleIds().contains(entry.getKey());
            if (contains != ZenModeBackend.getDefaultRuleIds().contains(entry2.getKey())) {
                return contains ? -1 : 1;
            }
            int compare = Long.compare(entry.getValue().getCreationTime(), entry2.getValue().getCreationTime());
            if (compare != 0) {
                return compare;
            }
            return key(entry.getValue()).compareTo(key(entry2.getValue()));
        }

        private String key(AutomaticZenRule automaticZenRule) {
            int i;
            if (ZenModeConfig.isValidScheduleConditionId(automaticZenRule.getConditionId())) {
                i = 1;
            } else {
                i = ZenModeConfig.isValidEventConditionId(automaticZenRule.getConditionId()) ? 2 : 3;
            }
            return i + automaticZenRule.getName().toString();
        }
    };
    protected static final String ZEN_MODE_FROM_ANYONE = "zen_mode_from_anyone";
    protected static final String ZEN_MODE_FROM_CONTACTS = "zen_mode_from_contacts";
    protected static final String ZEN_MODE_FROM_NONE = "zen_mode_from_none";
    protected static final String ZEN_MODE_FROM_STARRED = "zen_mode_from_starred";
    private static List<String> mDefaultRuleIds;
    private static ZenModeBackend sInstance;
    private String TAG = "ZenModeSettingsBackend";
    private final Context mContext;
    private final NotificationManager mNotificationManager;
    protected NotificationManager.Policy mPolicy;
    protected int mZenMode;

    private int clearDeprecatedEffects(int i) {
        return i & -4;
    }

    protected static String getKeyFromZenPolicySetting(int i) {
        return i != 1 ? i != 2 ? i != 3 ? ZEN_MODE_FROM_NONE : ZEN_MODE_FROM_STARRED : ZEN_MODE_FROM_CONTACTS : ZEN_MODE_FROM_ANYONE;
    }

    public static ZenModeBackend getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ZenModeBackend(context);
        }
        return sInstance;
    }

    public ZenModeBackend(Context context) {
        this.mContext = context;
        this.mNotificationManager = (NotificationManager) context.getSystemService("notification");
        updateZenMode();
        updatePolicy();
    }

    /* access modifiers changed from: protected */
    public void updatePolicy() {
        NotificationManager notificationManager = this.mNotificationManager;
        if (notificationManager != null) {
            this.mPolicy = notificationManager.getNotificationPolicy();
        }
    }

    /* access modifiers changed from: protected */
    public void updateZenMode() {
        this.mZenMode = Settings.Global.getInt(this.mContext.getContentResolver(), "zen_mode", this.mZenMode);
    }

    /* access modifiers changed from: protected */
    public boolean updateZenRule(String str, AutomaticZenRule automaticZenRule) {
        return NotificationManager.from(this.mContext).updateAutomaticZenRule(str, automaticZenRule);
    }

    /* access modifiers changed from: protected */
    public void setZenMode(int i) {
        NotificationManager.from(this.mContext).setZenMode(i, null, this.TAG);
        this.mZenMode = getZenMode();
    }

    /* access modifiers changed from: protected */
    public void setZenModeForDuration(int i) {
        this.mNotificationManager.setZenMode(1, ZenModeConfig.toTimeCondition(this.mContext, i, ActivityManager.getCurrentUser(), true).id, this.TAG);
        this.mZenMode = getZenMode();
    }

    /* access modifiers changed from: protected */
    public int getZenMode() {
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "zen_mode", this.mZenMode);
        this.mZenMode = i;
        return i;
    }

    /* access modifiers changed from: protected */
    public boolean isVisualEffectSuppressed(int i) {
        return (this.mPolicy.suppressedVisualEffects & i) != 0;
    }

    /* access modifiers changed from: protected */
    public boolean isPriorityCategoryEnabled(int i) {
        return (this.mPolicy.priorityCategories & i) != 0;
    }

    /* access modifiers changed from: protected */
    public int getNewDefaultPriorityCategories(boolean z, int i) {
        int i2 = this.mPolicy.priorityCategories;
        return z ? i2 | i : i2 & (~i);
    }

    /* access modifiers changed from: protected */
    public int getPriorityCallSenders() {
        if (isPriorityCategoryEnabled(8)) {
            return this.mPolicy.priorityCallSenders;
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public int getPriorityMessageSenders() {
        if (isPriorityCategoryEnabled(4)) {
            return this.mPolicy.priorityMessageSenders;
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public int getPriorityConversationSenders() {
        if (isPriorityCategoryEnabled(256)) {
            return this.mPolicy.priorityConversationSenders;
        }
        return 3;
    }

    /* access modifiers changed from: protected */
    public void saveVisualEffectsPolicy(int i, boolean z) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "zen_settings_updated", 1);
        int newSuppressedEffects = getNewSuppressedEffects(z, i);
        NotificationManager.Policy policy = this.mPolicy;
        savePolicy(policy.priorityCategories, policy.priorityCallSenders, policy.priorityMessageSenders, newSuppressedEffects, policy.priorityConversationSenders);
    }

    /* access modifiers changed from: protected */
    public void saveSoundPolicy(int i, boolean z) {
        int newDefaultPriorityCategories = getNewDefaultPriorityCategories(z, i);
        NotificationManager.Policy policy = this.mPolicy;
        savePolicy(newDefaultPriorityCategories, policy.priorityCallSenders, policy.priorityMessageSenders, policy.suppressedVisualEffects, policy.priorityConversationSenders);
    }

    /* access modifiers changed from: protected */
    public void savePolicy(int i, int i2, int i3, int i4, int i5) {
        NotificationManager.Policy policy = new NotificationManager.Policy(i, i2, i3, i4, i5);
        this.mPolicy = policy;
        this.mNotificationManager.setNotificationPolicy(policy);
    }

    private int getNewSuppressedEffects(boolean z, int i) {
        int i2 = this.mPolicy.suppressedVisualEffects;
        return clearDeprecatedEffects(z ? i2 | i : (~i) & i2);
    }

    /* access modifiers changed from: protected */
    public void saveSenders(int i, int i2) {
        int i3;
        String str;
        int i4;
        int priorityCallSenders = getPriorityCallSenders();
        int priorityMessageSenders = getPriorityMessageSenders();
        int prioritySenders = getPrioritySenders(i);
        boolean z = i2 != -1;
        if (i2 == -1) {
            i2 = prioritySenders;
        }
        if (i == 8) {
            str = "Calls";
            i3 = i2;
        } else {
            i3 = priorityCallSenders;
            str = "";
        }
        if (i == 4) {
            str = "Messages";
            i4 = i2;
        } else {
            i4 = priorityMessageSenders;
        }
        int newDefaultPriorityCategories = getNewDefaultPriorityCategories(z, i);
        NotificationManager.Policy policy = this.mPolicy;
        savePolicy(newDefaultPriorityCategories, i3, i4, policy.suppressedVisualEffects, policy.priorityConversationSenders);
        if (ZenModeSettingsBase.DEBUG) {
            String str2 = this.TAG;
            Log.d(str2, "onPrefChange allow" + str + "=" + z + " allow" + str + "From=" + ZenModeConfig.sourceToString(i2));
        }
    }

    /* access modifiers changed from: protected */
    public void saveConversationSenders(int i) {
        int newDefaultPriorityCategories = getNewDefaultPriorityCategories(i != 3, 256);
        NotificationManager.Policy policy = this.mPolicy;
        savePolicy(newDefaultPriorityCategories, policy.priorityCallSenders, policy.priorityMessageSenders, policy.suppressedVisualEffects, i);
    }

    private int getPrioritySenders(int i) {
        if (i == 8) {
            return getPriorityCallSenders();
        }
        if (i == 4) {
            return getPriorityMessageSenders();
        }
        if (i == 256) {
            return getPriorityConversationSenders();
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public int getAlarmsTotalSilencePeopleSummary(int i) {
        if (i == 4) {
            return C0017R$string.zen_mode_none_messages;
        }
        if (i == 8) {
            return C0017R$string.zen_mode_none_calls;
        }
        if (i == 256) {
            return C0017R$string.zen_mode_from_no_conversations;
        }
        return C0017R$string.zen_mode_from_no_conversations;
    }

    /* access modifiers changed from: protected */
    public int getConversationSummary() {
        int priorityConversationSenders = getPriorityConversationSenders();
        if (priorityConversationSenders == 1) {
            return C0017R$string.zen_mode_from_all_conversations;
        }
        if (priorityConversationSenders == 2) {
            return C0017R$string.zen_mode_from_important_conversations;
        }
        if (priorityConversationSenders != 3) {
            return C0017R$string.zen_mode_from_no_conversations;
        }
        return C0017R$string.zen_mode_from_no_conversations;
    }

    /* access modifiers changed from: protected */
    public int getContactsCallsSummary(ZenPolicy zenPolicy) {
        int priorityCallSenders = zenPolicy.getPriorityCallSenders();
        if (priorityCallSenders == 1) {
            return C0017R$string.zen_mode_from_anyone;
        }
        if (priorityCallSenders == 2) {
            return C0017R$string.zen_mode_from_contacts;
        }
        if (priorityCallSenders != 3) {
            return C0017R$string.zen_mode_none_calls;
        }
        return C0017R$string.zen_mode_from_starred;
    }

    /* access modifiers changed from: protected */
    public int getContactsMessagesSummary(ZenPolicy zenPolicy) {
        int priorityMessageSenders = zenPolicy.getPriorityMessageSenders();
        if (priorityMessageSenders == 1) {
            return C0017R$string.zen_mode_from_anyone;
        }
        if (priorityMessageSenders == 2) {
            return C0017R$string.zen_mode_from_contacts;
        }
        if (priorityMessageSenders != 3) {
            return C0017R$string.zen_mode_none_messages;
        }
        return C0017R$string.zen_mode_from_starred;
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    protected static int getZenPolicySettingFromPrefKey(String str) {
        char c;
        switch (str.hashCode()) {
            case -946901971:
                if (str.equals(ZEN_MODE_FROM_NONE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -423126328:
                if (str.equals(ZEN_MODE_FROM_CONTACTS)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 187510959:
                if (str.equals(ZEN_MODE_FROM_ANYONE)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 462773226:
                if (str.equals(ZEN_MODE_FROM_STARRED)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            return 1;
        }
        if (c != 1) {
            return c != 2 ? 4 : 3;
        }
        return 2;
    }

    public boolean removeZenRule(String str) {
        return NotificationManager.from(this.mContext).removeAutomaticZenRule(str);
    }

    public NotificationManager.Policy getConsolidatedPolicy() {
        return NotificationManager.from(this.mContext).getConsolidatedNotificationPolicy();
    }

    /* access modifiers changed from: protected */
    public String addZenRule(AutomaticZenRule automaticZenRule) {
        try {
            return NotificationManager.from(this.mContext).addAutomaticZenRule(automaticZenRule);
        } catch (Exception unused) {
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public ZenPolicy setDefaultZenPolicy(ZenPolicy zenPolicy) {
        int i = 4;
        int zenPolicySenders = this.mPolicy.allowCalls() ? ZenModeConfig.getZenPolicySenders(this.mPolicy.allowCallsFrom()) : 4;
        if (this.mPolicy.allowMessages()) {
            i = ZenModeConfig.getZenPolicySenders(this.mPolicy.allowMessagesFrom());
        }
        return new ZenPolicy.Builder(zenPolicy).allowAlarms(this.mPolicy.allowAlarms()).allowCalls(zenPolicySenders).allowEvents(this.mPolicy.allowEvents()).allowMedia(this.mPolicy.allowMedia()).allowMessages(i).allowConversations(this.mPolicy.allowConversations() ? this.mPolicy.allowConversationsFrom() : 3).allowReminders(this.mPolicy.allowReminders()).allowRepeatCallers(this.mPolicy.allowRepeatCallers()).allowSystem(this.mPolicy.allowSystem()).showFullScreenIntent(this.mPolicy.showFullScreenIntents()).showLights(this.mPolicy.showLights()).showInAmbientDisplay(this.mPolicy.showAmbient()).showInNotificationList(this.mPolicy.showInNotificationList()).showBadges(this.mPolicy.showBadges()).showPeeking(this.mPolicy.showPeeking()).showStatusBarIcons(this.mPolicy.showStatusBarIcons()).build();
    }

    /* access modifiers changed from: protected */
    public Map.Entry<String, AutomaticZenRule>[] getAutomaticZenRules() {
        Map<String, AutomaticZenRule> automaticZenRules = NotificationManager.from(this.mContext).getAutomaticZenRules();
        Map.Entry<String, AutomaticZenRule>[] entryArr = (Map.Entry[]) automaticZenRules.entrySet().toArray(new Map.Entry[automaticZenRules.size()]);
        Arrays.sort(entryArr, RULE_COMPARATOR);
        return entryArr;
    }

    /* access modifiers changed from: protected */
    public AutomaticZenRule getAutomaticZenRule(String str) {
        return NotificationManager.from(this.mContext).getAutomaticZenRule(str);
    }

    /* access modifiers changed from: private */
    public static List<String> getDefaultRuleIds() {
        if (mDefaultRuleIds == null) {
            mDefaultRuleIds = ZenModeConfig.DEFAULT_RULE_IDS;
        }
        return mDefaultRuleIds;
    }

    /* access modifiers changed from: package-private */
    public NotificationManager.Policy toNotificationPolicy(ZenPolicy zenPolicy) {
        return new ZenModeConfig().toNotificationPolicy(zenPolicy);
    }

    /* access modifiers changed from: package-private */
    public List<String> getStarredContacts(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if (cursor == null || !cursor.moveToFirst()) {
            return arrayList;
        }
        do {
            String string = cursor.getString(0);
            if (string != null) {
                arrayList.add(string);
            }
        } while (cursor.moveToNext());
        return arrayList;
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0014  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.List<java.lang.String> getStarredContacts() {
        /*
            r1 = this;
            android.database.Cursor r0 = r1.queryStarredContactsData()     // Catch:{ all -> 0x0010 }
            java.util.List r1 = r1.getStarredContacts(r0)     // Catch:{ all -> 0x000e }
            if (r0 == 0) goto L_0x000d
            r0.close()
        L_0x000d:
            return r1
        L_0x000e:
            r1 = move-exception
            goto L_0x0012
        L_0x0010:
            r1 = move-exception
            r0 = 0
        L_0x0012:
            if (r0 == 0) goto L_0x0017
            r0.close()
        L_0x0017:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.notification.zen.ZenModeBackend.getStarredContacts():java.util.List");
    }

    /* access modifiers changed from: package-private */
    public String getStarredContactsSummary(Context context) {
        List<String> starredContacts = getStarredContacts();
        int size = starredContacts.size();
        ArrayList arrayList = new ArrayList();
        if (size == 0) {
            arrayList.add(context.getString(C0017R$string.zen_mode_starred_contacts_summary_none));
        } else {
            int i = 0;
            while (i < 2 && i < size) {
                arrayList.add(starredContacts.get(i));
                i++;
            }
            if (size == 3) {
                arrayList.add(starredContacts.get(2));
            } else if (size > 2) {
                int i2 = size - 2;
                arrayList.add(context.getResources().getQuantityString(C0015R$plurals.zen_mode_starred_contacts_summary_additional_contacts, i2, Integer.valueOf(i2)));
            }
        }
        return ListFormatter.getInstance().format(arrayList);
    }

    /* access modifiers changed from: package-private */
    public String getContactsNumberSummary(Context context) {
        int count = queryAllContactsData().getCount();
        if (count == 0) {
            return context.getResources().getString(C0017R$string.zen_mode_contacts_count_none);
        }
        return context.getResources().getQuantityString(C0015R$plurals.zen_mode_contacts_count, count, Integer.valueOf(count));
    }

    private Cursor queryStarredContactsData() {
        return this.mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{"display_name"}, "starred=1", null, "times_contacted");
    }

    private Cursor queryAllContactsData() {
        return this.mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{"display_name"}, null, null, null);
    }
}
