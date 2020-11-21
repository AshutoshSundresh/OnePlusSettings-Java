package com.android.settings.notification.zen;

import android.content.Context;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.LayoutPreference;

public class ZenModeSendersImagePreferenceController extends AbstractZenModePreferenceController {
    private ImageView mImageView;
    private final boolean mIsMessages;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return false;
    }

    public ZenModeSendersImagePreferenceController(Context context, String str, Lifecycle lifecycle, boolean z) {
        super(context, str, lifecycle);
        this.mIsMessages = z;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mImageView = (ImageView) ((LayoutPreference) preferenceScreen.findPreference(this.KEY)).findViewById(C0010R$id.zen_mode_settings_senders_image);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return this.KEY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int i;
        String str;
        int i2;
        int prioritySenders = getPrioritySenders();
        if (prioritySenders == 0) {
            if (this.mIsMessages) {
                i = C0008R$drawable.zen_messages_any;
            } else {
                i = C0008R$drawable.zen_calls_any;
            }
            str = this.mContext.getString(C0017R$string.zen_mode_from_anyone);
        } else if (1 == prioritySenders) {
            if (this.mIsMessages) {
                i = C0008R$drawable.zen_messages_contacts;
            } else {
                i = C0008R$drawable.zen_calls_contacts;
            }
            str = this.mContext.getString(C0017R$string.zen_mode_from_contacts);
        } else if (2 == prioritySenders) {
            if (this.mIsMessages) {
                i = C0008R$drawable.zen_messages_starred;
            } else {
                i = C0008R$drawable.zen_calls_starred;
            }
            str = this.mContext.getString(C0017R$string.zen_mode_from_starred);
        } else {
            if (this.mIsMessages) {
                i = C0008R$drawable.zen_messages_none;
            } else {
                i = C0008R$drawable.zen_calls_none;
            }
            Context context = this.mContext;
            if (this.mIsMessages) {
                i2 = C0017R$string.zen_mode_none_messages;
            } else {
                i2 = C0017R$string.zen_mode_none_calls;
            }
            str = context.getString(i2);
        }
        this.mImageView.setImageResource(i);
        this.mImageView.setContentDescription(str);
    }

    private int getPrioritySenders() {
        if (this.mIsMessages) {
            return this.mBackend.getPriorityMessageSenders();
        }
        return this.mBackend.getPriorityCallSenders();
    }
}
