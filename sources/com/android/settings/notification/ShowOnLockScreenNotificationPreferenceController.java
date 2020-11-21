package com.android.settings.notification;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.RestrictedListPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;

public class ShowOnLockScreenNotificationPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private DevicePolicyManager mDpm;
    private final String mSettingKey;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ShowOnLockScreenNotificationPreferenceController(Context context, String str) {
        super(context);
        this.mSettingKey = str;
        this.mDpm = (DevicePolicyManager) context.getSystemService(DevicePolicyManager.class);
    }

    /* access modifiers changed from: package-private */
    public void setDpm(DevicePolicyManager devicePolicyManager) {
        this.mDpm = devicePolicyManager;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mSettingKey;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        RestrictedListPreference restrictedListPreference = (RestrictedListPreference) preference;
        restrictedListPreference.clearRestrictedItems();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        String string = this.mContext.getString(C0017R$string.lock_screen_notifs_show_all);
        String num = Integer.toString(C0017R$string.lock_screen_notifs_show_all);
        arrayList.add(string);
        arrayList2.add(num);
        setRestrictedIfNotificationFeaturesDisabled(restrictedListPreference, string, num, 4);
        String string2 = this.mContext.getString(C0017R$string.lock_screen_notifs_show_alerting);
        String num2 = Integer.toString(C0017R$string.lock_screen_notifs_show_alerting);
        arrayList.add(string2);
        arrayList2.add(num2);
        setRestrictedIfNotificationFeaturesDisabled(restrictedListPreference, string2, num2, 4);
        arrayList.add(this.mContext.getString(C0017R$string.lock_screen_notifs_show_none));
        arrayList2.add(Integer.toString(C0017R$string.lock_screen_notifs_show_none));
        restrictedListPreference.setEntries((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]));
        restrictedListPreference.setEntryValues((CharSequence[]) arrayList2.toArray(new CharSequence[arrayList2.size()]));
        if (!adminAllowsNotifications() || !getLockscreenNotificationsEnabled()) {
            restrictedListPreference.setValue(Integer.toString(C0017R$string.lock_screen_notifs_show_none));
        } else if (!getLockscreenSilentNotificationsEnabled()) {
            restrictedListPreference.setValue(Integer.toString(C0017R$string.lock_screen_notifs_show_alerting));
        } else {
            restrictedListPreference.setValue(Integer.toString(C0017R$string.lock_screen_notifs_show_all));
        }
        restrictedListPreference.setOnPreferenceChangeListener(this);
        refreshSummary(preference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (!adminAllowsNotifications() || !getLockscreenNotificationsEnabled()) {
            return this.mContext.getString(C0017R$string.lock_screen_notifs_show_none);
        }
        if (!getLockscreenSilentNotificationsEnabled()) {
            return this.mContext.getString(C0017R$string.lock_screen_notifs_show_alerting);
        }
        return this.mContext.getString(C0017R$string.lock_screen_notifs_show_all);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = Integer.parseInt((String) obj);
        int i = 0;
        int i2 = parseInt != C0017R$string.lock_screen_notifs_show_none ? 1 : 0;
        if (parseInt == C0017R$string.lock_screen_notifs_show_all) {
            i = 1;
        }
        Settings.Secure.putInt(this.mContext.getContentResolver(), "lock_screen_show_silent_notifications", i);
        Settings.Secure.putInt(this.mContext.getContentResolver(), "lock_screen_show_notifications", i2);
        refreshSummary(preference);
        return true;
    }

    private void setRestrictedIfNotificationFeaturesDisabled(RestrictedListPreference restrictedListPreference, CharSequence charSequence, CharSequence charSequence2, int i) {
        RestrictedLockUtils.EnforcedAdmin checkIfKeyguardFeaturesDisabled = RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(this.mContext, i, UserHandle.myUserId());
        if (checkIfKeyguardFeaturesDisabled != null && restrictedListPreference != null) {
            restrictedListPreference.addRestrictedItem(new RestrictedListPreference.RestrictedItem(charSequence, charSequence2, checkIfKeyguardFeaturesDisabled));
        }
    }

    private boolean adminAllowsNotifications() {
        return (this.mDpm.getKeyguardDisabledFeatures(null) & 4) == 0;
    }

    private boolean getLockscreenNotificationsEnabled() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "lock_screen_show_notifications", 1) != 0;
    }

    private boolean getLockscreenSilentNotificationsEnabled() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "lock_screen_show_silent_notifications", 1) != 0;
    }
}
