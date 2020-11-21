package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.content.Context;
import android.provider.Settings;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.NotificationSettings;
import com.android.settingslib.RestrictedSwitchPreference;

public class BubblePreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    static final int SYSTEM_WIDE_OFF = 0;
    static final int SYSTEM_WIDE_ON = 1;
    private FragmentManager mFragmentManager;
    private boolean mHasSentInvalidMsg;
    private boolean mIsAppPage;
    private NotificationSettings.DependentFieldListener mListener;
    private int mNumConversations;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bubble_pref";
    }

    public BubblePreferenceController(Context context, FragmentManager fragmentManager, NotificationBackend notificationBackend, boolean z, NotificationSettings.DependentFieldListener dependentFieldListener) {
        super(context, notificationBackend);
        this.mFragmentManager = fragmentManager;
        this.mIsAppPage = z;
        this.mListener = dependentFieldListener;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        if (!super.isAvailable()) {
            return false;
        }
        if (!this.mIsAppPage && !isGloballyEnabled()) {
            return false;
        }
        if (this.mChannel == null || isDefaultChannel()) {
            return true;
        }
        if (this.mAppRow != null) {
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        NotificationBackend.AppRow appRow;
        boolean z = true;
        if (this.mIsAppPage && (appRow = this.mAppRow) != null) {
            this.mHasSentInvalidMsg = this.mBackend.isInInvalidMsgState(appRow.pkg, appRow.uid);
            NotificationBackend notificationBackend = this.mBackend;
            NotificationBackend.AppRow appRow2 = this.mAppRow;
            this.mNumConversations = notificationBackend.getConversations(appRow2.pkg, appRow2.uid).getList().size();
            int i = this.mAppRow.bubblePreference;
            BubblePreference bubblePreference = (BubblePreference) preference;
            bubblePreference.setDisabledByAdmin(this.mAdmin);
            if (this.mHasSentInvalidMsg && this.mNumConversations <= 0) {
                z = false;
            }
            bubblePreference.setSelectedVisibility(z);
            if (!isGloballyEnabled()) {
                bubblePreference.setSelectedPreference(0);
            } else {
                bubblePreference.setSelectedPreference(i);
            }
        } else if (this.mChannel != null) {
            RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
            restrictedSwitchPreference.setDisabledByAdmin(this.mAdmin);
            if (!this.mChannel.canBubble() || !isGloballyEnabled()) {
                z = false;
            }
            restrictedSwitchPreference.setChecked(z);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel != null) {
            notificationChannel.setAllowBubbles(((Boolean) obj).booleanValue());
            saveChannel();
            return true;
        } else if (!this.mIsAppPage) {
            return true;
        } else {
            BubblePreference bubblePreference = (BubblePreference) preference;
            if (!(this.mAppRow == null || this.mFragmentManager == null)) {
                int intValue = ((Integer) obj).intValue();
                if (isGloballyEnabled() || bubblePreference.getSelectedPreference() != 0) {
                    NotificationBackend.AppRow appRow = this.mAppRow;
                    appRow.bubblePreference = intValue;
                    this.mBackend.setAllowBubbles(appRow.pkg, appRow.uid, intValue);
                } else {
                    BubbleWarningDialogFragment bubbleWarningDialogFragment = new BubbleWarningDialogFragment();
                    NotificationBackend.AppRow appRow2 = this.mAppRow;
                    bubbleWarningDialogFragment.setPkgPrefInfo(appRow2.pkg, appRow2.uid, intValue);
                    bubbleWarningDialogFragment.show(this.mFragmentManager, "dialog");
                    return false;
                }
            }
            NotificationSettings.DependentFieldListener dependentFieldListener = this.mListener;
            if (dependentFieldListener == null) {
                return true;
            }
            dependentFieldListener.onFieldValueChanged();
            return true;
        }
    }

    private boolean isGloballyEnabled() {
        return Settings.Global.getInt(((NotificationPreferenceController) this).mContext.getContentResolver(), "notification_bubbles", 0) == 1;
    }

    public static void revertBubblesApproval(Context context, String str, int i) {
        new NotificationBackend().setAllowBubbles(str, i, 0);
        Settings.Global.putInt(context.getContentResolver(), "notification_bubbles", 0);
    }

    public static void applyBubblesApproval(Context context, String str, int i, int i2) {
        new NotificationBackend().setAllowBubbles(str, i, i2);
        Settings.Global.putInt(context.getContentResolver(), "notification_bubbles", 1);
    }
}
