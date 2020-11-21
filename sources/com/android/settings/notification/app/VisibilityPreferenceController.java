package com.android.settings.notification.app;

import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0017R$string;
import com.android.settings.RestrictedListPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import java.util.ArrayList;

public class VisibilityPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private LockPatternUtils mLockPatternUtils;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "visibility_override";
    }

    public VisibilityPreferenceController(Context context, LockPatternUtils lockPatternUtils, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
        this.mLockPatternUtils = lockPatternUtils;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        if (super.isAvailable() && this.mChannel != null && !this.mAppRow.banned && checkCanBeVisible(2) && isLockScreenSecure()) {
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mChannel != null && this.mAppRow != null) {
            RestrictedListPreference restrictedListPreference = (RestrictedListPreference) preference;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            restrictedListPreference.clearRestrictedItems();
            if (getLockscreenNotificationsEnabled() && getLockscreenAllowPrivateNotifications()) {
                String string = ((NotificationPreferenceController) this).mContext.getString(C0017R$string.lock_screen_notifications_summary_show);
                String num = Integer.toString(-1000);
                arrayList.add(string);
                arrayList2.add(num);
                setRestrictedIfNotificationFeaturesDisabled(restrictedListPreference, string, num, 12);
            }
            if (getLockscreenNotificationsEnabled()) {
                String string2 = ((NotificationPreferenceController) this).mContext.getString(C0017R$string.lock_screen_notifications_summary_hide);
                String num2 = Integer.toString(0);
                arrayList.add(string2);
                arrayList2.add(num2);
                setRestrictedIfNotificationFeaturesDisabled(restrictedListPreference, string2, num2, 4);
            }
            arrayList.add(((NotificationPreferenceController) this).mContext.getString(C0017R$string.lock_screen_notifications_summary_disable));
            arrayList2.add(Integer.toString(-1));
            restrictedListPreference.setEntries((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]));
            restrictedListPreference.setEntryValues((CharSequence[]) arrayList2.toArray(new CharSequence[arrayList2.size()]));
            if (this.mChannel.getLockscreenVisibility() == -1000) {
                restrictedListPreference.setValue(Integer.toString(getGlobalVisibility()));
            } else {
                restrictedListPreference.setValue(Integer.toString(this.mChannel.getLockscreenVisibility()));
            }
            restrictedListPreference.setSummary("%s");
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mChannel == null) {
            return true;
        }
        int parseInt = Integer.parseInt((String) obj);
        if (parseInt == getGlobalVisibility()) {
            parseInt = -1000;
        }
        this.mChannel.setLockscreenVisibility(parseInt);
        this.mChannel.lockFields(2);
        saveChannel();
        return true;
    }

    private void setRestrictedIfNotificationFeaturesDisabled(RestrictedListPreference restrictedListPreference, CharSequence charSequence, CharSequence charSequence2, int i) {
        RestrictedLockUtils.EnforcedAdmin checkIfKeyguardFeaturesDisabled = RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(((NotificationPreferenceController) this).mContext, i, this.mAppRow.userId);
        if (checkIfKeyguardFeaturesDisabled != null) {
            restrictedListPreference.addRestrictedItem(new RestrictedListPreference.RestrictedItem(charSequence, charSequence2, checkIfKeyguardFeaturesDisabled));
        }
    }

    private int getGlobalVisibility() {
        if (!getLockscreenNotificationsEnabled()) {
            return -1;
        }
        return !getLockscreenAllowPrivateNotifications() ? 0 : -1000;
    }

    private boolean getLockscreenNotificationsEnabled() {
        UserInfo profileParent = this.mUm.getProfileParent(UserHandle.myUserId());
        return Settings.Secure.getIntForUser(((NotificationPreferenceController) this).mContext.getContentResolver(), "lock_screen_show_notifications", 0, profileParent != null ? profileParent.id : UserHandle.myUserId()) != 0;
    }

    private boolean getLockscreenAllowPrivateNotifications() {
        return Settings.Secure.getInt(((NotificationPreferenceController) this).mContext.getContentResolver(), "lock_screen_allow_private_notifications", 0) != 0;
    }

    /* access modifiers changed from: protected */
    public boolean isLockScreenSecure() {
        boolean isSecure = this.mLockPatternUtils.isSecure(UserHandle.myUserId());
        UserInfo profileParent = this.mUm.getProfileParent(UserHandle.myUserId());
        return profileParent != null ? isSecure | this.mLockPatternUtils.isSecure(profileParent.id) : isSecure;
    }
}
