package com.android.settings.notification.app;

import android.content.Context;
import android.os.AsyncTask;
import android.service.notification.ConversationChannelWrapper;
import androidx.preference.Preference;
import com.android.settings.C0010R$id;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;
import java.util.List;

public class NoConversationsPreferenceController extends ConversationListPreferenceController {
    private List<ConversationChannelWrapper> mConversations;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "no_conversations";
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    public Preference getSummaryPreference() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.notification.app.ConversationListPreferenceController
    public boolean matchesFilter(ConversationChannelWrapper conversationChannelWrapper) {
        return false;
    }

    public NoConversationsPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(final Preference preference) {
        final LayoutPreference layoutPreference = (LayoutPreference) preference;
        new AsyncTask<Void, Void, Void>() {
            /* class com.android.settings.notification.app.NoConversationsPreferenceController.AnonymousClass1 */

            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                NoConversationsPreferenceController noConversationsPreferenceController = NoConversationsPreferenceController.this;
                noConversationsPreferenceController.mConversations = noConversationsPreferenceController.mBackend.getConversations(false).getList();
                return null;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void r3) {
                if (((AbstractPreferenceController) NoConversationsPreferenceController.this).mContext != null) {
                    boolean z = false;
                    layoutPreference.findViewById(C0010R$id.onboarding).setVisibility(NoConversationsPreferenceController.this.mConversations.size() == 0 ? 0 : 8);
                    Preference preference = preference;
                    if (NoConversationsPreferenceController.this.mConversations.size() == 0) {
                        z = true;
                    }
                    preference.setVisible(z);
                }
            }
        }.execute(new Void[0]);
    }
}
