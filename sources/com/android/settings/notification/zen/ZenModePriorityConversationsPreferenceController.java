package com.android.settings.notification.zen;

import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.os.AsyncTask;
import android.service.notification.ConversationChannelWrapper;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.ConversationListSettings;
import com.android.settings.widget.RadioButtonPreference;
import com.android.settings.widget.RadioButtonPreferenceWithExtraWidget;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;

public class ZenModePriorityConversationsPreferenceController extends AbstractZenModePreferenceController {
    static final String KEY_ALL = "conversations_all";
    static final String KEY_IMPORTANT = "conversations_important";
    static final String KEY_NONE = "conversations_none";
    private View.OnClickListener mConversationSettingsWidgetClickListener = new View.OnClickListener() {
        /* class com.android.settings.notification.zen.ZenModePriorityConversationsPreferenceController.AnonymousClass2 */

        public void onClick(View view) {
            SubSettingLauncher subSettingLauncher = new SubSettingLauncher(ZenModePriorityConversationsPreferenceController.this.mPreferenceScreenContext);
            subSettingLauncher.setDestination(ConversationListSettings.class.getName());
            subSettingLauncher.setSourceMetricsCategory(1837);
            subSettingLauncher.launch();
        }
    };
    private final NotificationBackend mNotificationBackend;
    private int mNumConversations = -1;
    private int mNumImportantConversations = -1;
    private PreferenceCategory mPreferenceCategory;
    private Context mPreferenceScreenContext;
    private RadioButtonPreference.OnClickListener mRadioButtonClickListener = new RadioButtonPreference.OnClickListener() {
        /* class com.android.settings.notification.zen.ZenModePriorityConversationsPreferenceController.AnonymousClass3 */

        @Override // com.android.settings.widget.RadioButtonPreference.OnClickListener
        public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
            int keyToSetting = ZenModePriorityConversationsPreferenceController.keyToSetting(radioButtonPreference.getKey());
            if (keyToSetting != ZenModePriorityConversationsPreferenceController.this.mBackend.getPriorityConversationSenders()) {
                ZenModePriorityConversationsPreferenceController.this.mBackend.saveConversationSenders(keyToSetting);
            }
        }
    };
    private List<RadioButtonPreference> mRadioButtonPreferences = new ArrayList();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModePriorityConversationsPreferenceController(Context context, String str, Lifecycle lifecycle, NotificationBackend notificationBackend) {
        super(context, str, lifecycle);
        this.mNotificationBackend = notificationBackend;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceScreenContext = preferenceScreen.getContext();
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceCategory = preferenceCategory;
        if (preferenceCategory.findPreference(KEY_ALL) == null) {
            makeRadioPreference(KEY_ALL, C0017R$string.zen_mode_from_all_conversations);
            makeRadioPreference(KEY_IMPORTANT, C0017R$string.zen_mode_from_important_conversations);
            makeRadioPreference(KEY_NONE, C0017R$string.zen_mode_from_no_conversations);
            updateChannelCounts();
        }
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void onResume() {
        super.onResume();
        updateChannelCounts();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return this.KEY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int priorityConversationSenders = this.mBackend.getPriorityConversationSenders();
        for (RadioButtonPreference radioButtonPreference : this.mRadioButtonPreferences) {
            radioButtonPreference.setChecked(keyToSetting(radioButtonPreference.getKey()) == priorityConversationSenders);
            radioButtonPreference.setSummary(getSummary(radioButtonPreference.getKey()));
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0027  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x002d A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int keyToSetting(java.lang.String r3) {
        /*
            int r0 = r3.hashCode()
            r1 = 660058867(0x2757b2f3, float:2.9934252E-15)
            r2 = 1
            if (r0 == r1) goto L_0x001a
            r1 = 775402802(0x2e37b532, float:4.1770316E-11)
            if (r0 == r1) goto L_0x0010
            goto L_0x0024
        L_0x0010:
            java.lang.String r0 = "conversations_all"
            boolean r3 = r3.equals(r0)
            if (r3 == 0) goto L_0x0024
            r3 = 0
            goto L_0x0025
        L_0x001a:
            java.lang.String r0 = "conversations_important"
            boolean r3 = r3.equals(r0)
            if (r3 == 0) goto L_0x0024
            r3 = r2
            goto L_0x0025
        L_0x0024:
            r3 = -1
        L_0x0025:
            if (r3 == 0) goto L_0x002d
            if (r3 == r2) goto L_0x002b
            r3 = 3
            return r3
        L_0x002b:
            r3 = 2
            return r3
        L_0x002d:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.notification.zen.ZenModePriorityConversationsPreferenceController.keyToSetting(java.lang.String):int");
    }

    private String getSummary(String str) {
        int i;
        if (KEY_ALL.equals(str)) {
            i = this.mNumConversations;
        } else if (!KEY_IMPORTANT.equals(str)) {
            return null;
        } else {
            i = this.mNumImportantConversations;
        }
        if (i == -1) {
            return null;
        }
        if (i == 0) {
            return this.mContext.getResources().getString(C0017R$string.zen_mode_conversations_count_none);
        }
        return this.mContext.getResources().getQuantityString(C0015R$plurals.zen_mode_conversations_count, i, Integer.valueOf(i));
    }

    private void updateChannelCounts() {
        new AsyncTask<Void, Void, Void>() {
            /* class com.android.settings.notification.zen.ZenModePriorityConversationsPreferenceController.AnonymousClass1 */

            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                int i;
                int i2 = 0;
                ParceledListSlice<ConversationChannelWrapper> conversations = ZenModePriorityConversationsPreferenceController.this.mNotificationBackend.getConversations(false);
                if (conversations != null) {
                    i = 0;
                    for (ConversationChannelWrapper conversationChannelWrapper : conversations.getList()) {
                        if (!conversationChannelWrapper.getNotificationChannel().isDemoted()) {
                            i++;
                        }
                    }
                } else {
                    i = 0;
                }
                ZenModePriorityConversationsPreferenceController.this.mNumConversations = i;
                ParceledListSlice<ConversationChannelWrapper> conversations2 = ZenModePriorityConversationsPreferenceController.this.mNotificationBackend.getConversations(true);
                if (conversations2 != null) {
                    for (ConversationChannelWrapper conversationChannelWrapper2 : conversations2.getList()) {
                        if (!conversationChannelWrapper2.getNotificationChannel().isDemoted()) {
                            i2++;
                        }
                    }
                }
                ZenModePriorityConversationsPreferenceController.this.mNumImportantConversations = i2;
                return null;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void r1) {
                if (((AbstractPreferenceController) ZenModePriorityConversationsPreferenceController.this).mContext != null) {
                    ZenModePriorityConversationsPreferenceController zenModePriorityConversationsPreferenceController = ZenModePriorityConversationsPreferenceController.this;
                    zenModePriorityConversationsPreferenceController.updateState(zenModePriorityConversationsPreferenceController.mPreferenceCategory);
                }
            }
        }.execute(new Void[0]);
    }

    private RadioButtonPreference makeRadioPreference(String str, int i) {
        RadioButtonPreferenceWithExtraWidget radioButtonPreferenceWithExtraWidget = new RadioButtonPreferenceWithExtraWidget(this.mPreferenceCategory.getContext());
        if (KEY_ALL.equals(str) || KEY_IMPORTANT.equals(str)) {
            radioButtonPreferenceWithExtraWidget.setExtraWidgetOnClickListener(this.mConversationSettingsWidgetClickListener);
            radioButtonPreferenceWithExtraWidget.setExtraWidgetVisibility(2);
        } else {
            radioButtonPreferenceWithExtraWidget.setExtraWidgetVisibility(0);
        }
        radioButtonPreferenceWithExtraWidget.setKey(str);
        radioButtonPreferenceWithExtraWidget.setTitle(i);
        radioButtonPreferenceWithExtraWidget.setOnClickListener(this.mRadioButtonClickListener);
        this.mPreferenceCategory.addPreference(radioButtonPreferenceWithExtraWidget);
        this.mRadioButtonPreferences.add(radioButtonPreferenceWithExtraWidget);
        return radioButtonPreferenceWithExtraWidget;
    }
}
