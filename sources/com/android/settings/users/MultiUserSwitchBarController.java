package com.android.settings.users;

import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class MultiUserSwitchBarController implements SwitchWidgetController.OnSwitchChangeListener, LifecycleObserver, OnStart, OnStop {
    private final Context mContext;
    private final OnMultiUserSwitchChangedListener mListener;
    final SwitchWidgetController mSwitchBar;
    private final UserCapabilities mUserCapabilities;

    interface OnMultiUserSwitchChangedListener {
        void onMultiUserSwitchChanged(boolean z);
    }

    MultiUserSwitchBarController(Context context, SwitchWidgetController switchWidgetController, OnMultiUserSwitchChangedListener onMultiUserSwitchChangedListener) {
        this.mContext = context;
        this.mSwitchBar = switchWidgetController;
        this.mListener = onMultiUserSwitchChangedListener;
        UserCapabilities create = UserCapabilities.create(context);
        this.mUserCapabilities = create;
        this.mSwitchBar.setChecked(create.mUserSwitcherEnabled);
        UserCapabilities userCapabilities = this.mUserCapabilities;
        boolean z = userCapabilities.mDisallowSwitchUser;
        if (z) {
            this.mSwitchBar.setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_user_switch", UserHandle.myUserId()));
        } else {
            this.mSwitchBar.setEnabled(!z && !userCapabilities.mIsGuest && userCapabilities.isAdmin());
        }
        this.mSwitchBar.setListener(this);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mSwitchBar.startListening();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mSwitchBar.stopListening();
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        OnMultiUserSwitchChangedListener onMultiUserSwitchChangedListener;
        Log.d("MultiUserSwitchBarCtrl", "Toggling multi-user feature enabled state to: " + z);
        boolean putInt = Settings.Global.putInt(this.mContext.getContentResolver(), "user_switcher_enabled", z ? 1 : 0);
        if (putInt && (onMultiUserSwitchChangedListener = this.mListener) != null) {
            onMultiUserSwitchChangedListener.onMultiUserSwitchChanged(z);
        }
        return putInt;
    }
}
