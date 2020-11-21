package com.android.settings.notification.app;

import android.app.NotificationChannelGroup;
import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.text.BidiFormatter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.widget.LayoutPreference;

public class ConversationHeaderPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, LifecycleObserver {
    private final DashboardFragment mFragment;
    private EntityHeaderController mHeaderController;
    private boolean mStarted = false;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "pref_app_header";
    }

    public ConversationHeaderPreferenceController(Context context, DashboardFragment dashboardFragment) {
        super(context, null);
        this.mFragment = dashboardFragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        return this.mAppRow != null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        DashboardFragment dashboardFragment;
        if (this.mAppRow != null && (dashboardFragment = this.mFragment) != null) {
            FragmentActivity fragmentActivity = null;
            if (this.mStarted) {
                fragmentActivity = dashboardFragment.getActivity();
            }
            if (fragmentActivity != null) {
                EntityHeaderController newInstance = EntityHeaderController.newInstance(fragmentActivity, this.mFragment, ((LayoutPreference) preference).findViewById(C0010R$id.entity_header));
                this.mHeaderController = newInstance;
                newInstance.setIcon(this.mConversationDrawable);
                newInstance.setLabel(getLabel());
                newInstance.setSummary(getSummary());
                newInstance.setPackageName(this.mAppRow.pkg);
                newInstance.setUid(this.mAppRow.uid);
                newInstance.setButtonActions(1, 0);
                newInstance.setHasAppInfoLink(true);
                newInstance.setRecyclerView(this.mFragment.getListView(), this.mFragment.getSettingsLifecycle());
                newInstance.done(fragmentActivity, ((NotificationPreferenceController) this).mContext).findViewById(C0010R$id.entity_header).setVisibility(0);
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (this.mChannel == null || isDefaultChannel()) {
            return this.mChannelGroup != null ? this.mAppRow.label.toString() : "";
        }
        NotificationChannelGroup notificationChannelGroup = this.mChannelGroup;
        if (notificationChannelGroup == null || TextUtils.isEmpty(notificationChannelGroup.getName())) {
            return this.mAppRow.label.toString();
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        BidiFormatter instance = BidiFormatter.getInstance();
        spannableStringBuilder.append((CharSequence) instance.unicodeWrap(this.mAppRow.label.toString()));
        spannableStringBuilder.append(instance.unicodeWrap(((NotificationPreferenceController) this).mContext.getText(C0017R$string.notification_header_divider_symbol_with_spaces)));
        spannableStringBuilder.append((CharSequence) instance.unicodeWrap(this.mChannelGroup.getName().toString()));
        return spannableStringBuilder.toString();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mStarted = true;
        EntityHeaderController entityHeaderController = this.mHeaderController;
        if (entityHeaderController != null) {
            entityHeaderController.styleActionBar(this.mFragment.getActivity());
        }
    }

    /* access modifiers changed from: package-private */
    public CharSequence getLabel() {
        ShortcutInfo shortcutInfo = this.mConversationInfo;
        if (shortcutInfo != null) {
            return shortcutInfo.getLabel();
        }
        return this.mChannel.getName();
    }
}
