package com.android.settings.applications;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.usb.IUsbManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.R$attr;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;

public class ClearDefaultsPreference extends Preference {
    protected static final String TAG = ClearDefaultsPreference.class.getSimpleName();
    private Button mActivitiesButton;
    protected ApplicationsState.AppEntry mAppEntry;
    private AppWidgetManager mAppWidgetManager;
    private final RestrictedLockUtils.EnforcedAdmin mAppsControlDisallowedAdmin;
    private final boolean mAppsControlDisallowedBySystem;
    private String mPackageName;
    private PackageManager mPm;
    private IUsbManager mUsbManager;

    public ClearDefaultsPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setLayoutResource(C0012R$layout.app_preferred_settings);
        this.mAppWidgetManager = AppWidgetManager.getInstance(context);
        this.mPm = context.getPackageManager();
        this.mUsbManager = IUsbManager.Stub.asInterface(ServiceManager.getService("usb"));
        this.mAppsControlDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(getContext(), "no_control_apps", UserHandle.myUserId());
        this.mAppsControlDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getContext(), "no_control_apps", UserHandle.myUserId());
    }

    public ClearDefaultsPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public ClearDefaultsPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.preferenceStyle, 16842894));
    }

    public ClearDefaultsPreference(Context context) {
        this(context, null);
    }

    public void setPackageName(String str) {
        this.mPackageName = str;
    }

    public void setAppEntry(ApplicationsState.AppEntry appEntry) {
        this.mAppEntry = appEntry;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(final PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        Button button = (Button) preferenceViewHolder.findViewById(C0010R$id.clear_activities_button);
        this.mActivitiesButton = button;
        button.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.applications.ClearDefaultsPreference.AnonymousClass1 */

            public void onClick(View view) {
                if (ClearDefaultsPreference.this.mAppsControlDisallowedAdmin != null && !ClearDefaultsPreference.this.mAppsControlDisallowedBySystem) {
                    RestrictedLockUtils.sendShowAdminSupportDetailsIntent(ClearDefaultsPreference.this.getContext(), ClearDefaultsPreference.this.mAppsControlDisallowedAdmin);
                } else if (ClearDefaultsPreference.this.mUsbManager != null) {
                    int myUserId = UserHandle.myUserId();
                    ClearDefaultsPreference.this.mPm.clearPackagePreferredActivities(ClearDefaultsPreference.this.mPackageName);
                    ClearDefaultsPreference clearDefaultsPreference = ClearDefaultsPreference.this;
                    if (clearDefaultsPreference.isDefaultBrowser(clearDefaultsPreference.mPackageName)) {
                        ClearDefaultsPreference.this.mPm.setDefaultBrowserPackageNameAsUser(null, myUserId);
                    }
                    try {
                        ClearDefaultsPreference.this.mUsbManager.clearDefaults(ClearDefaultsPreference.this.mPackageName, myUserId);
                    } catch (RemoteException e) {
                        Log.e(ClearDefaultsPreference.TAG, "mUsbManager.clearDefaults", e);
                    }
                    ClearDefaultsPreference.this.mAppWidgetManager.setBindAppWidgetPermission(ClearDefaultsPreference.this.mPackageName, false);
                    ClearDefaultsPreference.this.resetLaunchDefaultsUi((TextView) preferenceViewHolder.findViewById(C0010R$id.auto_launch));
                }
            }
        });
        updateUI(preferenceViewHolder);
    }

    public boolean updateUI(PreferenceViewHolder preferenceViewHolder) {
        boolean hasBindAppWidgetPermission = this.mAppWidgetManager.hasBindAppWidgetPermission(this.mAppEntry.info.packageName);
        TextView textView = (TextView) preferenceViewHolder.findViewById(C0010R$id.auto_launch);
        boolean z = AppUtils.hasPreferredActivities(this.mPm, this.mPackageName) || isDefaultBrowser(this.mPackageName) || AppUtils.hasUsbDefaults(this.mUsbManager, this.mPackageName);
        if (z || hasBindAppWidgetPermission) {
            boolean z2 = hasBindAppWidgetPermission && z;
            if (hasBindAppWidgetPermission) {
                textView.setText(C0017R$string.auto_launch_label_generic);
            } else {
                textView.setText(C0017R$string.auto_launch_label);
            }
            Context context = getContext();
            CharSequence charSequence = null;
            int dimensionPixelSize = context.getResources().getDimensionPixelSize(C0007R$dimen.installed_app_details_bullet_offset);
            if (z) {
                CharSequence text = context.getText(C0017R$string.auto_launch_enable_text);
                SpannableString spannableString = new SpannableString(text);
                if (z2) {
                    spannableString.setSpan(new BulletSpan(dimensionPixelSize), 0, text.length(), 0);
                }
                charSequence = TextUtils.concat(spannableString, "\n");
            }
            if (hasBindAppWidgetPermission) {
                CharSequence text2 = context.getText(C0017R$string.always_allow_bind_appwidgets_text);
                SpannableString spannableString2 = new SpannableString(text2);
                if (z2) {
                    spannableString2.setSpan(new BulletSpan(dimensionPixelSize), 0, text2.length(), 0);
                }
                charSequence = charSequence == null ? TextUtils.concat(spannableString2, "\n") : TextUtils.concat(charSequence, "\n", spannableString2, "\n");
            }
            textView.setText(charSequence);
            this.mActivitiesButton.setEnabled(true);
        } else {
            resetLaunchDefaultsUi(textView);
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isDefaultBrowser(String str) {
        return str.equals(this.mPm.getDefaultBrowserPackageNameAsUser(UserHandle.myUserId()));
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void resetLaunchDefaultsUi(TextView textView) {
        textView.setText(C0017R$string.auto_launch_disable_text);
        this.mActivitiesButton.setEnabled(false);
    }
}
