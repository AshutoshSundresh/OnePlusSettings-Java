package com.android.settings.notification;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.service.notification.ConversationChannelWrapper;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.notification.AppBubbleListPreferenceController;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.AppConversationListPreferenceController;
import com.android.settings.notification.app.NotificationPreferenceController;
import com.android.settingslib.RestrictedLockUtils;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AppBubbleListPreferenceController extends AppConversationListPreferenceController {
    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.AppConversationListPreferenceController
    public String getPreferenceKey() {
        return "bubble_conversations";
    }

    public AppBubbleListPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.AppConversationListPreferenceController
    public void updateState(Preference preference) {
        preference.setVisible(false);
        super.updateState(preference);
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    public void onResume(NotificationBackend.AppRow appRow, NotificationChannel notificationChannel, NotificationChannelGroup notificationChannelGroup, Drawable drawable, ShortcutInfo shortcutInfo, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        super.onResume(appRow, notificationChannel, notificationChannelGroup, drawable, shortcutInfo, enforcedAdmin);
        loadConversationsAndPopulate();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController, com.android.settings.notification.app.AppConversationListPreferenceController
    public boolean isAvailable() {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow == null || appRow.banned) {
            return false;
        }
        if ((this.mChannel == null || (!this.mBackend.onlyHasDefaultChannel(appRow.pkg, appRow.uid) && !"miscellaneous".equals(this.mChannel.getId()))) && this.mAppRow.bubblePreference != 0) {
            return true;
        }
        return false;
    }

    @Override // com.android.settings.notification.app.AppConversationListPreferenceController
    public List<ConversationChannelWrapper> filterAndSortConversations(List<ConversationChannelWrapper> list) {
        return (List) list.stream().sorted(this.mConversationComparator).filter(new Predicate() {
            /* class com.android.settings.notification.$$Lambda$AppBubbleListPreferenceController$tL6dTOqDtd6xd0Lo0vHgALY7dLs */

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return AppBubbleListPreferenceController.this.lambda$filterAndSortConversations$0$AppBubbleListPreferenceController((ConversationChannelWrapper) obj);
            }
        }).collect(Collectors.toList());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$filterAndSortConversations$0 */
    public /* synthetic */ boolean lambda$filterAndSortConversations$0$AppBubbleListPreferenceController(ConversationChannelWrapper conversationChannelWrapper) {
        int i = this.mAppRow.bubblePreference;
        if (i == 2) {
            return conversationChannelWrapper.getNotificationChannel().canBubble();
        }
        return i == 1 && conversationChannelWrapper.getNotificationChannel().getAllowBubbles() == 0;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.notification.app.AppConversationListPreferenceController
    public int getTitleResId() {
        if (this.mAppRow.bubblePreference == 2) {
            return C0017R$string.bubble_app_setting_selected_conversation_title;
        }
        return C0017R$string.bubble_app_setting_excluded_conversation_title;
    }

    @Override // com.android.settings.notification.app.AppConversationListPreferenceController
    public Preference createConversationPref(ConversationChannelWrapper conversationChannelWrapper) {
        ConversationPreference conversationPreference = new ConversationPreference(((NotificationPreferenceController) this).mContext);
        populateConversationPreference(conversationChannelWrapper, conversationPreference);
        boolean z = true;
        if (this.mAppRow.bubblePreference != 1) {
            z = false;
        }
        conversationPreference.setOnClickBubblesConversation(z);
        conversationPreference.setOnClickListener(new View.OnClickListener(conversationChannelWrapper, conversationPreference) {
            /* class com.android.settings.notification.$$Lambda$AppBubbleListPreferenceController$PGJvPLB4Zgk1aNx1U8XZK2SqPBM */
            public final /* synthetic */ ConversationChannelWrapper f$1;
            public final /* synthetic */ AppBubbleListPreferenceController.ConversationPreference f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onClick(View view) {
                AppBubbleListPreferenceController.this.lambda$createConversationPref$1$AppBubbleListPreferenceController(this.f$1, this.f$2, view);
            }
        });
        return conversationPreference;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createConversationPref$1 */
    public /* synthetic */ void lambda$createConversationPref$1$AppBubbleListPreferenceController(ConversationChannelWrapper conversationChannelWrapper, ConversationPreference conversationPreference, View view) {
        conversationChannelWrapper.getNotificationChannel().setAllowBubbles(-1);
        NotificationBackend notificationBackend = this.mBackend;
        NotificationBackend.AppRow appRow = this.mAppRow;
        notificationBackend.updateChannel(appRow.pkg, appRow.uid, conversationChannelWrapper.getNotificationChannel());
        this.mPreference.removePreference(conversationPreference);
        if (this.mPreference.getPreferenceCount() == 0) {
            this.mPreference.setVisible(false);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.notification.app.AppConversationListPreferenceController
    public void populateList() {
        super.populateList();
        PreferenceCategory preferenceCategory = this.mPreference;
        if (preferenceCategory != null) {
            preferenceCategory.setVisible(preferenceCategory.getPreferenceCount() > 0);
        }
    }

    public static class ConversationPreference extends Preference implements View.OnClickListener {
        boolean mOnClickBubbles;
        View.OnClickListener mOnClickListener;

        ConversationPreference(Context context) {
            super(context);
            setWidgetLayoutResource(C0012R$layout.bubble_conversation_remove_button);
        }

        @Override // androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            String str;
            super.onBindViewHolder(preferenceViewHolder);
            ImageView imageView = (ImageView) preferenceViewHolder.itemView.findViewById(C0010R$id.button);
            if (this.mOnClickBubbles) {
                str = getContext().getString(C0017R$string.bubble_app_setting_bubble_conversation);
            } else {
                str = getContext().getString(C0017R$string.bubble_app_setting_unbubble_conversation);
            }
            imageView.setContentDescription(str);
            imageView.setOnClickListener(this.mOnClickListener);
        }

        public void setOnClickBubblesConversation(boolean z) {
            this.mOnClickBubbles = z;
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.mOnClickListener = onClickListener;
        }

        public void onClick(View view) {
            View.OnClickListener onClickListener = this.mOnClickListener;
            if (onClickListener != null) {
                onClickListener.onClick(view);
            }
        }
    }
}
