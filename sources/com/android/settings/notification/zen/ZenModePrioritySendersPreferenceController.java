package com.android.settings.notification.zen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.widget.RadioButtonPreference;
import com.android.settings.widget.RadioButtonPreferenceWithExtraWidget;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;

public class ZenModePrioritySendersPreferenceController extends AbstractZenModePreferenceController {
    private static final Intent ALL_CONTACTS_INTENT = new Intent("com.android.contacts.action.LIST_DEFAULT");
    private static final Intent FALLBACK_INTENT = new Intent("android.intent.action.MAIN");
    static final String KEY_ANY = "senders_anyone";
    static final String KEY_CONTACTS = "senders_contacts";
    static final String KEY_NONE = "senders_none";
    static final String KEY_STARRED = "senders_starred_contacts";
    private static final Intent STARRED_CONTACTS_INTENT = new Intent("com.android.contacts.action.LIST_STARRED");
    private final boolean mIsMessages;
    private final PackageManager mPackageManager;
    private PreferenceCategory mPreferenceCategory;
    private RadioButtonPreference.OnClickListener mRadioButtonClickListener = new RadioButtonPreference.OnClickListener() {
        /* class com.android.settings.notification.zen.ZenModePrioritySendersPreferenceController.AnonymousClass1 */

        @Override // com.android.settings.widget.RadioButtonPreference.OnClickListener
        public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
            int keyToSetting = ZenModePrioritySendersPreferenceController.keyToSetting(radioButtonPreference.getKey());
            if (keyToSetting != ZenModePrioritySendersPreferenceController.this.getPrioritySenders()) {
                ZenModePrioritySendersPreferenceController zenModePrioritySendersPreferenceController = ZenModePrioritySendersPreferenceController.this;
                zenModePrioritySendersPreferenceController.mBackend.saveSenders(zenModePrioritySendersPreferenceController.mIsMessages ? 4 : 8, keyToSetting);
            }
        }
    };
    private List<RadioButtonPreferenceWithExtraWidget> mRadioButtonPreferences = new ArrayList();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModePrioritySendersPreferenceController(Context context, String str, Lifecycle lifecycle, boolean z) {
        super(context, str, lifecycle);
        this.mIsMessages = z;
        this.mPackageManager = this.mContext.getPackageManager();
        if (!FALLBACK_INTENT.hasCategory("android.intent.category.APP_CONTACTS")) {
            FALLBACK_INTENT.addCategory("android.intent.category.APP_CONTACTS");
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        int i;
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreferenceCategory = preferenceCategory;
        if (preferenceCategory.findPreference(KEY_ANY) == null) {
            makeRadioPreference(KEY_STARRED, C0017R$string.zen_mode_from_starred);
            makeRadioPreference(KEY_CONTACTS, C0017R$string.zen_mode_from_contacts);
            makeRadioPreference(KEY_ANY, C0017R$string.zen_mode_from_anyone);
            if (this.mIsMessages) {
                i = C0017R$string.zen_mode_none_messages;
            } else {
                i = C0017R$string.zen_mode_none_calls;
            }
            makeRadioPreference(KEY_NONE, i);
            updateSummaries();
        }
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return this.KEY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int prioritySenders = getPrioritySenders();
        for (RadioButtonPreferenceWithExtraWidget radioButtonPreferenceWithExtraWidget : this.mRadioButtonPreferences) {
            radioButtonPreferenceWithExtraWidget.setChecked(keyToSetting(radioButtonPreferenceWithExtraWidget.getKey()) == prioritySenders);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void onResume() {
        super.onResume();
        updateSummaries();
    }

    private void updateSummaries() {
        for (RadioButtonPreferenceWithExtraWidget radioButtonPreferenceWithExtraWidget : this.mRadioButtonPreferences) {
            radioButtonPreferenceWithExtraWidget.setSummary(getSummary(radioButtonPreferenceWithExtraWidget.getKey()));
        }
    }

    /* access modifiers changed from: private */
    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    public static int keyToSetting(String str) {
        char c;
        switch (str.hashCode()) {
            case -1145842476:
                if (str.equals(KEY_STARRED)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -133103980:
                if (str.equals(KEY_CONTACTS)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1725241211:
                if (str.equals(KEY_ANY)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1767544313:
                if (str.equals(KEY_NONE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            return 2;
        }
        if (c != 1) {
            return c != 2 ? -1 : 0;
        }
        return 1;
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    private String getSummary(String str) {
        char c;
        int i;
        switch (str.hashCode()) {
            case -1145842476:
                if (str.equals(KEY_STARRED)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -133103980:
                if (str.equals(KEY_CONTACTS)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1725241211:
                if (str.equals(KEY_ANY)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1767544313:
                if (str.equals(KEY_NONE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            return this.mBackend.getStarredContactsSummary(this.mContext);
        }
        if (c == 1) {
            return this.mBackend.getContactsNumberSummary(this.mContext);
        }
        if (c != 2) {
            return null;
        }
        Resources resources = this.mContext.getResources();
        if (this.mIsMessages) {
            i = C0017R$string.zen_mode_all_messages_summary;
        } else {
            i = C0017R$string.zen_mode_all_calls_summary;
        }
        return resources.getString(i);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int getPrioritySenders() {
        if (this.mIsMessages) {
            return this.mBackend.getPriorityMessageSenders();
        }
        return this.mBackend.getPriorityCallSenders();
    }

    private RadioButtonPreferenceWithExtraWidget makeRadioPreference(String str, int i) {
        RadioButtonPreferenceWithExtraWidget radioButtonPreferenceWithExtraWidget = new RadioButtonPreferenceWithExtraWidget(this.mPreferenceCategory.getContext());
        radioButtonPreferenceWithExtraWidget.setKey(str);
        radioButtonPreferenceWithExtraWidget.setTitle(i);
        radioButtonPreferenceWithExtraWidget.setOnClickListener(this.mRadioButtonClickListener);
        View.OnClickListener widgetClickListener = getWidgetClickListener(str);
        if (widgetClickListener != null) {
            radioButtonPreferenceWithExtraWidget.setExtraWidgetOnClickListener(widgetClickListener);
            radioButtonPreferenceWithExtraWidget.setExtraWidgetVisibility(2);
        } else {
            radioButtonPreferenceWithExtraWidget.setExtraWidgetVisibility(0);
        }
        this.mPreferenceCategory.addPreference(radioButtonPreferenceWithExtraWidget);
        this.mRadioButtonPreferences.add(radioButtonPreferenceWithExtraWidget);
        return radioButtonPreferenceWithExtraWidget;
    }

    private View.OnClickListener getWidgetClickListener(final String str) {
        if (!KEY_CONTACTS.equals(str) && !KEY_STARRED.equals(str)) {
            return null;
        }
        if (KEY_STARRED.equals(str) && !isStarredIntentValid()) {
            return null;
        }
        if (!KEY_CONTACTS.equals(str) || isContactsIntentValid()) {
            return new View.OnClickListener() {
                /* class com.android.settings.notification.zen.ZenModePrioritySendersPreferenceController.AnonymousClass2 */

                public void onClick(View view) {
                    if (ZenModePrioritySendersPreferenceController.KEY_STARRED.equals(str) && ZenModePrioritySendersPreferenceController.STARRED_CONTACTS_INTENT.resolveActivity(ZenModePrioritySendersPreferenceController.this.mPackageManager) != null) {
                        ((AbstractPreferenceController) ZenModePrioritySendersPreferenceController.this).mContext.startActivity(ZenModePrioritySendersPreferenceController.STARRED_CONTACTS_INTENT);
                    } else if (!ZenModePrioritySendersPreferenceController.KEY_CONTACTS.equals(str) || ZenModePrioritySendersPreferenceController.ALL_CONTACTS_INTENT.resolveActivity(ZenModePrioritySendersPreferenceController.this.mPackageManager) == null) {
                        ((AbstractPreferenceController) ZenModePrioritySendersPreferenceController.this).mContext.startActivity(ZenModePrioritySendersPreferenceController.FALLBACK_INTENT);
                    } else {
                        ((AbstractPreferenceController) ZenModePrioritySendersPreferenceController.this).mContext.startActivity(ZenModePrioritySendersPreferenceController.ALL_CONTACTS_INTENT);
                    }
                }
            };
        }
        return null;
    }

    private boolean isStarredIntentValid() {
        return (STARRED_CONTACTS_INTENT.resolveActivity(this.mPackageManager) == null && FALLBACK_INTENT.resolveActivity(this.mPackageManager) == null) ? false : true;
    }

    private boolean isContactsIntentValid() {
        return (ALL_CONTACTS_INTENT.resolveActivity(this.mPackageManager) == null && FALLBACK_INTENT.resolveActivity(this.mPackageManager) == null) ? false : true;
    }
}
