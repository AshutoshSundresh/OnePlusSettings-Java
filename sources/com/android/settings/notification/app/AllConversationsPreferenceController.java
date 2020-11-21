package com.android.settings.notification.app;

import android.content.Context;
import android.os.AsyncTask;
import android.service.notification.ConversationChannelWrapper;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.C0017R$string;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Collections;
import java.util.List;

public class AllConversationsPreferenceController extends ConversationListPreferenceController {
    private List<ConversationChannelWrapper> mConversations;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "other_conversations";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public AllConversationsPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    public Preference getSummaryPreference() {
        Preference preference = new Preference(this.mContext);
        preference.setOrder(1);
        preference.setSummary(C0017R$string.other_conversations_summary);
        return preference;
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    public boolean matchesFilter(ConversationChannelWrapper conversationChannelWrapper) {
        return !conversationChannelWrapper.getNotificationChannel().isImportantConversation();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        final PreferenceCategory preferenceCategory = (PreferenceCategory) preference;
        new AsyncTask<Void, Void, Void>() {
            /* class com.android.settings.notification.app.AllConversationsPreferenceController.AnonymousClass1 */

            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                AllConversationsPreferenceController allConversationsPreferenceController = AllConversationsPreferenceController.this;
                allConversationsPreferenceController.mConversations = allConversationsPreferenceController.mBackend.getConversations(false).getList();
                Collections.sort(AllConversationsPreferenceController.this.mConversations, AllConversationsPreferenceController.this.mConversationComparator);
                return null;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void r2) {
                if (((AbstractPreferenceController) AllConversationsPreferenceController.this).mContext != null) {
                    AllConversationsPreferenceController allConversationsPreferenceController = AllConversationsPreferenceController.this;
                    allConversationsPreferenceController.populateList(allConversationsPreferenceController.mConversations, preferenceCategory);
                }
            }
        }.execute(new Void[0]);
    }
}
