package com.android.settings.notification.zen;

import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.service.notification.ConversationChannelWrapper;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.LayoutPreference;
import java.util.ArrayList;
import java.util.List;

public class ZenModeConversationsImagePreferenceController extends AbstractZenModePreferenceController {
    private final ArrayList<Drawable> mConversationDrawables = new ArrayList<>();
    private final int mIconOffsetPx;
    private final int mIconSizePx;
    private final NotificationBackend mNotificationBackend;
    private LayoutPreference mPreference;
    private ViewGroup mViewGroup;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeConversationsImagePreferenceController(Context context, String str, Lifecycle lifecycle, NotificationBackend notificationBackend) {
        super(context, str, lifecycle);
        this.mNotificationBackend = notificationBackend;
        this.mIconSizePx = this.mContext.getResources().getDimensionPixelSize(C0007R$dimen.zen_conversations_icon_size);
        this.mIconOffsetPx = this.mContext.getResources().getDimensionPixelSize(C0007R$dimen.zen_conversations_icon_offset);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(this.KEY);
        this.mPreference = layoutPreference;
        this.mViewGroup = (ViewGroup) layoutPreference.findViewById(C0010R$id.zen_mode_settings_senders_overlay_view);
        loadConversations();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return this.KEY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        loadConversations();
        this.mViewGroup.removeAllViews();
        int priorityConversationSenders = this.mBackend.getPriorityConversationSenders();
        int i = 8;
        if (priorityConversationSenders == 1) {
            this.mViewGroup.setContentDescription(this.mContext.getResources().getString(C0017R$string.zen_mode_from_all_conversations));
        } else if (priorityConversationSenders == 2) {
            this.mViewGroup.setContentDescription(this.mContext.getResources().getString(C0017R$string.zen_mode_from_important_conversations));
        } else {
            this.mViewGroup.setContentDescription(null);
            this.mViewGroup.setVisibility(8);
            return;
        }
        int min = Math.min(5, this.mConversationDrawables.size());
        for (int i2 = 0; i2 < min; i2++) {
            ImageView imageView = new ImageView(this.mContext);
            imageView.setImageDrawable(this.mConversationDrawables.get(i2));
            int i3 = this.mIconSizePx;
            imageView.setLayoutParams(new ViewGroup.LayoutParams(i3, i3));
            FrameLayout frameLayout = new FrameLayout(this.mContext);
            frameLayout.addView(imageView);
            frameLayout.setPadding(((min - i2) - 1) * this.mIconOffsetPx, 0, 0, 0);
            this.mViewGroup.addView(frameLayout);
        }
        ViewGroup viewGroup = this.mViewGroup;
        if (min > 0) {
            i = 0;
        }
        viewGroup.setVisibility(i);
    }

    private void loadConversations() {
        new AsyncTask<Void, Void, Void>() {
            /* class com.android.settings.notification.zen.ZenModeConversationsImagePreferenceController.AnonymousClass1 */
            private List<Drawable> mDrawables = new ArrayList();

            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                Drawable conversationDrawable;
                this.mDrawables.clear();
                int priorityConversationSenders = ZenModeConversationsImagePreferenceController.this.mBackend.getPriorityConversationSenders();
                if (priorityConversationSenders == 3) {
                    return null;
                }
                ParceledListSlice<ConversationChannelWrapper> conversations = ZenModeConversationsImagePreferenceController.this.mNotificationBackend.getConversations(priorityConversationSenders == 2);
                if (conversations != null) {
                    for (ConversationChannelWrapper conversationChannelWrapper : conversations.getList()) {
                        if (!conversationChannelWrapper.getNotificationChannel().isDemoted() && (conversationDrawable = ZenModeConversationsImagePreferenceController.this.mNotificationBackend.getConversationDrawable(((AbstractPreferenceController) ZenModeConversationsImagePreferenceController.this).mContext, conversationChannelWrapper.getShortcutInfo(), conversationChannelWrapper.getPkg(), conversationChannelWrapper.getUid(), conversationChannelWrapper.getNotificationChannel().isImportantConversation())) != null) {
                            this.mDrawables.add(conversationDrawable);
                        }
                    }
                }
                return null;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void r2) {
                if (((AbstractPreferenceController) ZenModeConversationsImagePreferenceController.this).mContext != null) {
                    ZenModeConversationsImagePreferenceController.this.mConversationDrawables.clear();
                    ZenModeConversationsImagePreferenceController.this.mConversationDrawables.addAll(this.mDrawables);
                    ZenModeConversationsImagePreferenceController zenModeConversationsImagePreferenceController = ZenModeConversationsImagePreferenceController.this;
                    zenModeConversationsImagePreferenceController.updateState(zenModeConversationsImagePreferenceController.mPreference);
                }
            }
        }.execute(new Void[0]);
    }
}
