package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.SwitchPreference;
import com.android.settings.C0006R$color;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settingslib.RestrictedSwitchPreference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChannelListPreferenceController extends NotificationPreferenceController {
    private static String KEY_GENERAL_CATEGORY = "categories";
    private List<NotificationChannelGroup> mChannelGroupList;
    private PreferenceCategory mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "channels";
    }

    public ChannelListPreferenceController(Context context, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.app.NotificationPreferenceController
    public boolean isAvailable() {
        NotificationBackend.AppRow appRow = this.mAppRow;
        if (appRow == null || appRow.banned) {
            return false;
        }
        if (this.mChannel == null) {
            return true;
        }
        if (this.mBackend.onlyHasDefaultChannel(appRow.pkg, appRow.uid) || "miscellaneous".equals(this.mChannel.getId())) {
            return false;
        }
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mPreference = (PreferenceCategory) preference;
        new AsyncTask<Void, Void, Void>() {
            /* class com.android.settings.notification.app.ChannelListPreferenceController.AnonymousClass1 */

            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                ChannelListPreferenceController channelListPreferenceController = ChannelListPreferenceController.this;
                NotificationBackend notificationBackend = channelListPreferenceController.mBackend;
                NotificationBackend.AppRow appRow = channelListPreferenceController.mAppRow;
                channelListPreferenceController.mChannelGroupList = notificationBackend.getGroups(appRow.pkg, appRow.uid).getList();
                Collections.sort(ChannelListPreferenceController.this.mChannelGroupList, NotificationPreferenceController.CHANNEL_GROUP_COMPARATOR);
                return null;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void r1) {
                ChannelListPreferenceController channelListPreferenceController = ChannelListPreferenceController.this;
                if (((NotificationPreferenceController) channelListPreferenceController).mContext != null) {
                    channelListPreferenceController.populateList();
                }
            }
        }.execute(new Void[0]);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void populateList() {
        this.mPreference.removeAll();
        if (TextUtils.isEmpty(this.mBackend.mInstantAppPKG)) {
            if (this.mChannelGroupList.isEmpty()) {
                PreferenceCategory preferenceCategory = new PreferenceCategory(((NotificationPreferenceController) this).mContext);
                preferenceCategory.setTitle(C0017R$string.notification_channels);
                preferenceCategory.setKey(KEY_GENERAL_CATEGORY);
                this.mPreference.addPreference(preferenceCategory);
                Preference preference = new Preference(((NotificationPreferenceController) this).mContext);
                preference.setTitle(C0017R$string.no_channels);
                preference.setEnabled(false);
                preferenceCategory.addPreference(preference);
                return;
            }
            populateGroupList();
        }
    }

    private void populateGroupList() {
        for (NotificationChannelGroup notificationChannelGroup : this.mChannelGroupList) {
            PreferenceCategory preferenceCategory = new PreferenceCategory(((NotificationPreferenceController) this).mContext);
            preferenceCategory.setOrderingAsAdded(true);
            this.mPreference.addPreference(preferenceCategory);
            if (notificationChannelGroup.getId() == null) {
                preferenceCategory.setTitle(C0017R$string.notification_channels_other);
                preferenceCategory.setKey(KEY_GENERAL_CATEGORY);
            } else {
                preferenceCategory.setTitle(notificationChannelGroup.getName());
                preferenceCategory.setKey(notificationChannelGroup.getId());
                populateGroupToggle(preferenceCategory, notificationChannelGroup);
            }
            if (!notificationChannelGroup.isBlocked()) {
                List<NotificationChannel> channels = notificationChannelGroup.getChannels();
                Collections.sort(channels, NotificationPreferenceController.CHANNEL_COMPARATOR);
                int size = channels.size();
                for (int i = 0; i < size; i++) {
                    NotificationChannel notificationChannel = channels.get(i);
                    if (TextUtils.isEmpty(notificationChannel.getConversationId()) || notificationChannel.isDemoted()) {
                        populateSingleChannelPrefs(preferenceCategory, notificationChannel, notificationChannelGroup.isBlocked());
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void populateGroupToggle(PreferenceGroup preferenceGroup, NotificationChannelGroup notificationChannelGroup) {
        RestrictedSwitchPreference restrictedSwitchPreference = new RestrictedSwitchPreference(((NotificationPreferenceController) this).mContext);
        boolean z = false;
        restrictedSwitchPreference.setTitle(((NotificationPreferenceController) this).mContext.getString(C0017R$string.notification_switch_label, notificationChannelGroup.getName()));
        if (this.mAdmin == null && isChannelGroupBlockable(notificationChannelGroup)) {
            z = true;
        }
        restrictedSwitchPreference.setEnabled(z);
        restrictedSwitchPreference.setChecked(!notificationChannelGroup.isBlocked());
        restrictedSwitchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(notificationChannelGroup) {
            /* class com.android.settings.notification.app.$$Lambda$ChannelListPreferenceController$AR2CQeChZZ0ExptRl8UMLLpOCio */
            public final /* synthetic */ NotificationChannelGroup f$1;

            {
                this.f$1 = r2;
            }

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return ChannelListPreferenceController.this.lambda$populateGroupToggle$0$ChannelListPreferenceController(this.f$1, preference);
            }
        });
        preferenceGroup.addPreference(restrictedSwitchPreference);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$populateGroupToggle$0 */
    public /* synthetic */ boolean lambda$populateGroupToggle$0$ChannelListPreferenceController(NotificationChannelGroup notificationChannelGroup, Preference preference) {
        notificationChannelGroup.setBlocked(!((SwitchPreference) preference).isChecked());
        NotificationBackend notificationBackend = this.mBackend;
        NotificationBackend.AppRow appRow = this.mAppRow;
        notificationBackend.updateChannelGroup(appRow.pkg, appRow.uid, notificationChannelGroup);
        onGroupBlockStateChanged(notificationChannelGroup);
        return true;
    }

    /* access modifiers changed from: protected */
    public Preference populateSingleChannelPrefs(PreferenceGroup preferenceGroup, NotificationChannel notificationChannel, boolean z) {
        MasterSwitchPreference masterSwitchPreference = new MasterSwitchPreference(((NotificationPreferenceController) this).mContext);
        boolean z2 = false;
        masterSwitchPreference.setSwitchEnabled(this.mAdmin == null && isChannelBlockable(notificationChannel) && isChannelConfigurable(notificationChannel) && !z);
        masterSwitchPreference.setIcon((Drawable) null);
        notificationChannel.getImportance();
        masterSwitchPreference.setIconSize(2);
        masterSwitchPreference.setKey(notificationChannel.getId());
        masterSwitchPreference.setTitle(notificationChannel.getName());
        masterSwitchPreference.setSummary(NotificationBackend.getSentSummary(((NotificationPreferenceController) this).mContext, this.mAppRow.sentByChannel.get(notificationChannel.getId()), false));
        if (notificationChannel.getImportance() != 0) {
            z2 = true;
        }
        masterSwitchPreference.setChecked(z2);
        Bundle bundle = new Bundle();
        bundle.putInt("uid", this.mAppRow.uid);
        bundle.putString("package", this.mAppRow.pkg);
        bundle.putString("android.provider.extra.CHANNEL_ID", notificationChannel.getId());
        bundle.putBoolean("fromSettings", true);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(((NotificationPreferenceController) this).mContext);
        subSettingLauncher.setDestination(ChannelNotificationSettings.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setTitleRes(C0017R$string.notification_channel_title);
        subSettingLauncher.setSourceMetricsCategory(72);
        masterSwitchPreference.setIntent(subSettingLauncher.toIntent());
        masterSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(notificationChannel) {
            /* class com.android.settings.notification.app.$$Lambda$ChannelListPreferenceController$PLvfA3g4OWDxkdAt44EkWT5JVd4 */
            public final /* synthetic */ NotificationChannel f$1;

            {
                this.f$1 = r2;
            }

            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return ChannelListPreferenceController.this.lambda$populateSingleChannelPrefs$1$ChannelListPreferenceController(this.f$1, preference, obj);
            }
        });
        if (preferenceGroup.findPreference(masterSwitchPreference.getKey()) == null) {
            preferenceGroup.addPreference(masterSwitchPreference);
        }
        return masterSwitchPreference;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$populateSingleChannelPrefs$1 */
    public /* synthetic */ boolean lambda$populateSingleChannelPrefs$1$ChannelListPreferenceController(NotificationChannel notificationChannel, Preference preference, Object obj) {
        boolean z = false;
        int i = ((Boolean) obj).booleanValue() ? 2 : 0;
        notificationChannel.setImportance(i);
        notificationChannel.lockFields(4);
        MasterSwitchPreference masterSwitchPreference = (MasterSwitchPreference) preference;
        masterSwitchPreference.setIcon((Drawable) null);
        notificationChannel.getImportance();
        Drawable icon = masterSwitchPreference.getIcon();
        if (i != 0) {
            z = true;
        }
        toggleBehaviorIconState(icon, z);
        NotificationBackend notificationBackend = this.mBackend;
        NotificationBackend.AppRow appRow = this.mAppRow;
        notificationBackend.updateChannel(appRow.pkg, appRow.uid, notificationChannel);
        return true;
    }

    private void toggleBehaviorIconState(Drawable drawable, boolean z) {
        GradientDrawable gradientDrawable;
        if (drawable != null && (gradientDrawable = (GradientDrawable) ((LayerDrawable) drawable).findDrawableByLayerId(C0010R$id.back)) != null) {
            if (z) {
                gradientDrawable.clearColorFilter();
            } else {
                gradientDrawable.setColorFilter(new BlendModeColorFilter(((NotificationPreferenceController) this).mContext.getColor(C0006R$color.material_grey_300), BlendMode.SRC_IN));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onGroupBlockStateChanged(NotificationChannelGroup notificationChannelGroup) {
        PreferenceGroup preferenceGroup;
        if (!(notificationChannelGroup == null || (preferenceGroup = (PreferenceGroup) this.mPreference.findPreference(notificationChannelGroup.getId())) == null)) {
            int i = 0;
            if (notificationChannelGroup.isBlocked()) {
                ArrayList<Preference> arrayList = new ArrayList();
                int preferenceCount = preferenceGroup.getPreferenceCount();
                while (i < preferenceCount) {
                    Preference preference = preferenceGroup.getPreference(i);
                    if (preference instanceof MasterSwitchPreference) {
                        arrayList.add(preference);
                    }
                    i++;
                }
                for (Preference preference2 : arrayList) {
                    preferenceGroup.removePreference(preference2);
                }
                return;
            }
            List<NotificationChannel> channels = notificationChannelGroup.getChannels();
            Collections.sort(channels, NotificationPreferenceController.CHANNEL_COMPARATOR);
            int size = channels.size();
            while (i < size) {
                populateSingleChannelPrefs(preferenceGroup, channels.get(i), notificationChannelGroup.isBlocked());
                i++;
            }
        }
    }
}
