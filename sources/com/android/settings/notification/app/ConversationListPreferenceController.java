package com.android.settings.notification.app;

import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.service.notification.ConversationChannelWrapper;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.core.AbstractPreferenceController;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;

public abstract class ConversationListPreferenceController extends AbstractPreferenceController {
    protected final NotificationBackend mBackend;
    protected Comparator<ConversationChannelWrapper> mConversationComparator = new Comparator<ConversationChannelWrapper>(this) {
        /* class com.android.settings.notification.app.ConversationListPreferenceController.AnonymousClass1 */
        private final Collator sCollator = Collator.getInstance();

        public int compare(ConversationChannelWrapper conversationChannelWrapper, ConversationChannelWrapper conversationChannelWrapper2) {
            if (conversationChannelWrapper.getShortcutInfo() != null && conversationChannelWrapper2.getShortcutInfo() == null) {
                return -1;
            }
            if (conversationChannelWrapper.getShortcutInfo() == null && conversationChannelWrapper2.getShortcutInfo() != null) {
                return 1;
            }
            if (conversationChannelWrapper.getShortcutInfo() == null && conversationChannelWrapper2.getShortcutInfo() == null) {
                return conversationChannelWrapper.getNotificationChannel().getId().compareTo(conversationChannelWrapper2.getNotificationChannel().getId());
            }
            return this.sCollator.compare(conversationChannelWrapper.getShortcutInfo().getLabel(), conversationChannelWrapper2.getShortcutInfo().getLabel());
        }
    };

    /* access modifiers changed from: package-private */
    public abstract Preference getSummaryPreference();

    /* access modifiers changed from: package-private */
    public abstract boolean matchesFilter(ConversationChannelWrapper conversationChannelWrapper);

    public ConversationListPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context);
        this.mBackend = notificationBackend;
    }

    /* access modifiers changed from: protected */
    public void populateList(List<ConversationChannelWrapper> list, PreferenceGroup preferenceGroup) {
        preferenceGroup.removeAll();
        if (list != null) {
            populateConversations(list, preferenceGroup);
        }
        if (preferenceGroup.getPreferenceCount() == 0) {
            preferenceGroup.setVisible(false);
            return;
        }
        preferenceGroup.setVisible(true);
        Preference summaryPreference = getSummaryPreference();
        if (summaryPreference != null) {
            preferenceGroup.addPreference(summaryPreference);
        }
    }

    /* access modifiers changed from: protected */
    public void populateConversations(List<ConversationChannelWrapper> list, PreferenceGroup preferenceGroup) {
        int i = 100;
        for (ConversationChannelWrapper conversationChannelWrapper : list) {
            if (!conversationChannelWrapper.getNotificationChannel().isDemoted() && matchesFilter(conversationChannelWrapper)) {
                preferenceGroup.addPreference(createConversationPref(conversationChannelWrapper, i));
                i++;
            }
        }
    }

    /* access modifiers changed from: protected */
    public Preference createConversationPref(ConversationChannelWrapper conversationChannelWrapper, int i) {
        Preference preference = new Preference(this.mContext);
        preference.setOrder(i);
        preference.setTitle(getTitle(conversationChannelWrapper));
        preference.setSummary(getSummary(conversationChannelWrapper));
        preference.setIcon(this.mBackend.getConversationDrawable(this.mContext, conversationChannelWrapper.getShortcutInfo(), conversationChannelWrapper.getPkg(), conversationChannelWrapper.getUid(), conversationChannelWrapper.getNotificationChannel().isImportantConversation()));
        preference.setKey(conversationChannelWrapper.getNotificationChannel().getId());
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(conversationChannelWrapper, preference) {
            /* class com.android.settings.notification.app.$$Lambda$ConversationListPreferenceController$HKXCMFFpA_u9ZZGfRz14CHviJtw */
            public final /* synthetic */ ConversationChannelWrapper f$1;
            public final /* synthetic */ Preference f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return ConversationListPreferenceController.this.lambda$createConversationPref$0$ConversationListPreferenceController(this.f$1, this.f$2, preference);
            }
        });
        return preference;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createConversationPref$0 */
    public /* synthetic */ boolean lambda$createConversationPref$0$ConversationListPreferenceController(ConversationChannelWrapper conversationChannelWrapper, Preference preference, Preference preference2) {
        getSubSettingLauncher(conversationChannelWrapper, preference.getTitle()).launch();
        return true;
    }

    /* access modifiers changed from: package-private */
    public CharSequence getSummary(ConversationChannelWrapper conversationChannelWrapper) {
        if (TextUtils.isEmpty(conversationChannelWrapper.getGroupLabel())) {
            return conversationChannelWrapper.getParentChannelLabel();
        }
        return this.mContext.getString(C0017R$string.notification_conversation_summary, conversationChannelWrapper.getParentChannelLabel(), conversationChannelWrapper.getGroupLabel());
    }

    /* access modifiers changed from: package-private */
    public CharSequence getTitle(ConversationChannelWrapper conversationChannelWrapper) {
        ShortcutInfo shortcutInfo = conversationChannelWrapper.getShortcutInfo();
        if (shortcutInfo != null) {
            return shortcutInfo.getShortLabel();
        }
        return conversationChannelWrapper.getNotificationChannel().getName();
    }

    /* access modifiers changed from: package-private */
    public SubSettingLauncher getSubSettingLauncher(ConversationChannelWrapper conversationChannelWrapper, CharSequence charSequence) {
        Bundle bundle = new Bundle();
        bundle.putInt("uid", conversationChannelWrapper.getUid());
        bundle.putString("package", conversationChannelWrapper.getPkg());
        bundle.putString("android.provider.extra.CHANNEL_ID", conversationChannelWrapper.getNotificationChannel().getId());
        bundle.putString("android.provider.extra.CONVERSATION_ID", conversationChannelWrapper.getNotificationChannel().getConversationId());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(ChannelNotificationSettings.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setExtras(bundle);
        subSettingLauncher.setUserHandle(UserHandle.getUserHandleForUid(conversationChannelWrapper.getUid()));
        subSettingLauncher.setTitleText(charSequence);
        subSettingLauncher.setSourceMetricsCategory(1834);
        return subSettingLauncher;
    }
}
