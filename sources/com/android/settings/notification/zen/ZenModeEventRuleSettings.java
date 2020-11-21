package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.CalendarContract;
import android.service.notification.ZenModeConfig;
import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ZenModeEventRuleSettings extends ZenModeRuleSettingsBase {
    private static final Comparator<CalendarInfo> CALENDAR_NAME = new Comparator<CalendarInfo>() {
        /* class com.android.settings.notification.zen.ZenModeEventRuleSettings.AnonymousClass3 */

        public int compare(CalendarInfo calendarInfo, CalendarInfo calendarInfo2) {
            return calendarInfo.name.compareTo(calendarInfo2.name);
        }
    };
    private DropDownPreference mCalendar;
    private boolean mCreate;
    private ZenModeConfig.EventInfo mEvent;
    private DropDownPreference mReply;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 146;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.notification.zen.ZenModeRuleSettingsBase
    public boolean setRule(AutomaticZenRule automaticZenRule) {
        ZenModeConfig.EventInfo tryParseEventConditionId = automaticZenRule != null ? ZenModeConfig.tryParseEventConditionId(automaticZenRule.getConditionId()) : null;
        this.mEvent = tryParseEventConditionId;
        return tryParseEventConditionId != null;
    }

    @Override // com.android.settings.notification.zen.ZenModeRuleSettingsBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.notification.zen.ZenModeSettingsBase, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        if (!isUiRestricted()) {
            if (!this.mCreate) {
                reloadCalendar();
            }
            this.mCreate = false;
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.zen_mode_event_rule_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((ZenModeRuleSettingsBase) this).mHeader = new ZenAutomaticRuleHeaderPreferenceController(context, this, getSettingsLifecycle());
        this.mActionButtons = new ZenRuleButtonsPreferenceController(context, this, getSettingsLifecycle());
        this.mSwitch = new ZenAutomaticRuleSwitchPreferenceController(context, this, getSettingsLifecycle());
        arrayList.add(((ZenModeRuleSettingsBase) this).mHeader);
        arrayList.add(this.mActionButtons);
        arrayList.add(this.mSwitch);
        return arrayList;
    }

    private void reloadCalendar() {
        List<CalendarInfo> calendars = getCalendars(((ZenModeRuleSettingsBase) this).mContext);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList.add(getString(C0017R$string.zen_mode_event_rule_calendar_any));
        String str = null;
        arrayList2.add(key(0, null, ""));
        ZenModeConfig.EventInfo eventInfo = this.mEvent;
        if (eventInfo != null) {
            str = eventInfo.calName;
        }
        for (CalendarInfo calendarInfo : calendars) {
            arrayList.add(calendarInfo.name);
            arrayList2.add(key(calendarInfo));
            if (str != null && this.mEvent.calendarId == null && str.equals(calendarInfo.name)) {
                this.mEvent.calendarId = calendarInfo.calendarId;
            }
        }
        CharSequence[] charSequenceArr = (CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]);
        CharSequence[] charSequenceArr2 = (CharSequence[]) arrayList2.toArray(new CharSequence[arrayList2.size()]);
        if (!Objects.equals(this.mCalendar.getEntries(), charSequenceArr)) {
            this.mCalendar.setEntries(charSequenceArr);
        }
        if (!Objects.equals(this.mCalendar.getEntryValues(), charSequenceArr2)) {
            this.mCalendar.setEntryValues(charSequenceArr2);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.notification.zen.ZenModeRuleSettingsBase
    public void onCreateInternal() {
        this.mCreate = true;
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        DropDownPreference dropDownPreference = (DropDownPreference) preferenceScreen.findPreference("calendar");
        this.mCalendar = dropDownPreference;
        dropDownPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            /* class com.android.settings.notification.zen.ZenModeEventRuleSettings.AnonymousClass1 */

            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                String str = (String) obj;
                if (str.equals(ZenModeEventRuleSettings.key(ZenModeEventRuleSettings.this.mEvent))) {
                    return false;
                }
                String[] split = str.split(":", 3);
                ZenModeEventRuleSettings.this.mEvent.userId = Integer.parseInt(split[0]);
                String str2 = null;
                ZenModeEventRuleSettings.this.mEvent.calendarId = split[1].equals("") ? null : Long.valueOf(Long.parseLong(split[1]));
                ZenModeConfig.EventInfo eventInfo = ZenModeEventRuleSettings.this.mEvent;
                if (!split[2].equals("")) {
                    str2 = split[2];
                }
                eventInfo.calName = str2;
                ZenModeEventRuleSettings zenModeEventRuleSettings = ZenModeEventRuleSettings.this;
                zenModeEventRuleSettings.updateRule(ZenModeConfig.toEventConditionId(zenModeEventRuleSettings.mEvent));
                return true;
            }
        });
        DropDownPreference dropDownPreference2 = (DropDownPreference) preferenceScreen.findPreference("reply");
        this.mReply = dropDownPreference2;
        dropDownPreference2.setEntries(new CharSequence[]{getString(C0017R$string.zen_mode_event_rule_reply_any_except_no), getString(C0017R$string.zen_mode_event_rule_reply_yes_or_maybe), getString(C0017R$string.zen_mode_event_rule_reply_yes)});
        this.mReply.setEntryValues(new CharSequence[]{Integer.toString(0), Integer.toString(1), Integer.toString(2)});
        this.mReply.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            /* class com.android.settings.notification.zen.ZenModeEventRuleSettings.AnonymousClass2 */

            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                int parseInt = Integer.parseInt((String) obj);
                if (parseInt == ZenModeEventRuleSettings.this.mEvent.reply) {
                    return false;
                }
                ZenModeEventRuleSettings.this.mEvent.reply = parseInt;
                ZenModeEventRuleSettings zenModeEventRuleSettings = ZenModeEventRuleSettings.this;
                zenModeEventRuleSettings.updateRule(ZenModeConfig.toEventConditionId(zenModeEventRuleSettings.mEvent));
                return true;
            }
        });
        reloadCalendar();
        updateControlsInternal();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.notification.zen.ZenModeRuleSettingsBase
    public void updateControlsInternal() {
        if (!Objects.equals(this.mCalendar.getValue(), key(this.mEvent))) {
            this.mCalendar.setValue(key(this.mEvent));
        }
        if (!Objects.equals(this.mReply.getValue(), Integer.toString(this.mEvent.reply))) {
            this.mReply.setValue(Integer.toString(this.mEvent.reply));
        }
    }

    private List<CalendarInfo> getCalendars(Context context) {
        ArrayList arrayList = new ArrayList();
        for (UserHandle userHandle : UserManager.get(context).getUserProfiles()) {
            Context contextForUser = getContextForUser(context, userHandle);
            if (contextForUser != null) {
                addCalendars(contextForUser, arrayList);
            }
        }
        Collections.sort(arrayList, CALENDAR_NAME);
        return arrayList;
    }

    private static Context getContextForUser(Context context, UserHandle userHandle) {
        try {
            return context.createPackageContextAsUser(context.getPackageName(), 0, userHandle);
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    private void addCalendars(Context context, List<CalendarInfo> list) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, new String[]{"_id", "calendar_displayName"}, "calendar_access_level >= 500 AND sync_events = 1", null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    addCalendar(cursor.getLong(0), cursor.getString(1), context.getUserId(), list);
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void addCalendar(long j, String str, int i, List<CalendarInfo> list) {
        CalendarInfo calendarInfo = new CalendarInfo();
        calendarInfo.calendarId = Long.valueOf(j);
        calendarInfo.name = str;
        calendarInfo.userId = i;
        if (!list.contains(calendarInfo)) {
            list.add(calendarInfo);
        }
    }

    private static String key(CalendarInfo calendarInfo) {
        return key(calendarInfo.userId, calendarInfo.calendarId, calendarInfo.name);
    }

    /* access modifiers changed from: private */
    public static String key(ZenModeConfig.EventInfo eventInfo) {
        return key(eventInfo.userId, eventInfo.calendarId, eventInfo.calName);
    }

    private static String key(int i, Long l, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(ZenModeConfig.EventInfo.resolveUserId(i));
        sb.append(":");
        if (l == null) {
            l = "";
        }
        sb.append(l);
        sb.append(":");
        if (str == null) {
            str = "";
        }
        sb.append(str);
        return sb.toString();
    }

    public static class CalendarInfo {
        public Long calendarId;
        public String name;
        public int userId;

        public boolean equals(Object obj) {
            if (!(obj instanceof CalendarInfo)) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            CalendarInfo calendarInfo = (CalendarInfo) obj;
            return Objects.equals(calendarInfo.name, this.name) && Objects.equals(calendarInfo.calendarId, this.calendarId);
        }

        public int hashCode() {
            return Objects.hash(this.name, this.calendarId);
        }
    }
}
