package com.android.settingslib;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settingslib.inputmethod.InputMethodAndSubtypeUtil;
import com.android.settingslib.inputmethod.InputMethodPreference;
import com.android.settingslib.inputmethod.InputMethodSettingValuesWrapper;
import com.oneplus.settings.ui.OPRestrictedSwitchPreference;
import java.text.Collator;

public class OPInputMethodPreference extends OPRestrictedSwitchPreference implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private static final String TAG = InputMethodPreference.class.getSimpleName();
    private AlertDialog mDialog;
    private final boolean mHasPriorityInSorting;
    private final InputMethodInfo mImi;
    private final InputMethodSettingValuesWrapper mInputMethodSettingValues;
    private final boolean mIsAllowedByOrganization;
    private final OnSavePreferenceListener mOnSaveListener;

    public interface OnSavePreferenceListener {
        void onSaveInputMethodPreference(OPInputMethodPreference oPInputMethodPreference);
    }

    public OPInputMethodPreference(Context context, InputMethodInfo inputMethodInfo, boolean z, boolean z2, OnSavePreferenceListener onSavePreferenceListener) {
        this(context, inputMethodInfo, inputMethodInfo.loadLabel(context.getPackageManager()), z2, onSavePreferenceListener);
        if (!z) {
            setWidgetLayoutResource(0);
        }
    }

    @VisibleForTesting
    OPInputMethodPreference(Context context, InputMethodInfo inputMethodInfo, CharSequence charSequence, boolean z, OnSavePreferenceListener onSavePreferenceListener) {
        super(context);
        setLayoutResource(C0012R$layout.op_preference_material_input);
        this.mDialog = null;
        boolean z2 = false;
        setPersistent(false);
        this.mImi = inputMethodInfo;
        this.mIsAllowedByOrganization = z;
        this.mOnSaveListener = onSavePreferenceListener;
        setSwitchTextOn("");
        setSwitchTextOff("");
        setKey(inputMethodInfo.getId());
        setTitle(charSequence);
        String settingsActivity = inputMethodInfo.getSettingsActivity();
        if (TextUtils.isEmpty(settingsActivity)) {
            setIntent(null);
        } else {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setClassName(inputMethodInfo.getPackageName(), settingsActivity);
            setIntent(intent);
        }
        this.mInputMethodSettingValues = InputMethodSettingValuesWrapper.getInstance(context);
        if (inputMethodInfo.isSystem() && InputMethodAndSubtypeUtil.isValidNonAuxAsciiCapableIme(inputMethodInfo)) {
            z2 = true;
        }
        this.mHasPriorityInSorting = z2;
        setOnPreferenceClickListener(this);
        setOnPreferenceChangeListener(this);
    }

    private boolean isImeEnabler() {
        return getWidgetLayoutResource() != 0;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!isImeEnabler()) {
            return false;
        }
        if (isChecked()) {
            setCheckedInternal(false);
            return false;
        }
        if (!this.mImi.isSystem()) {
            showSecurityWarnDialog();
        } else if (this.mImi.getServiceInfo().directBootAware || isTv()) {
            setCheckedInternal(true);
        } else if (!isTv()) {
            showDirectBootWarnDialog();
        }
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (isImeEnabler()) {
            return true;
        }
        Context context = getContext();
        try {
            Intent intent = getIntent();
            if (intent != null) {
                context.startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            Log.d(TAG, "IME's Settings Activity Not Found", e);
            Toast.makeText(context, context.getString(C0017R$string.failed_to_open_app_settings_toast, this.mImi.loadLabel(context.getPackageManager())), 1).show();
        }
        return true;
    }

    public void updatePreferenceViews() {
        if (this.mInputMethodSettingValues.isAlwaysCheckedIme(this.mImi) && isImeEnabler()) {
            setDisabledByAdmin(null);
            setEnabled(false);
        } else if (!this.mIsAllowedByOrganization) {
            setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfInputMethodDisallowed(getContext(), this.mImi.getPackageName(), UserHandle.myUserId()));
        } else {
            setEnabled(true);
        }
        setChecked(this.mInputMethodSettingValues.isEnabledImi(this.mImi));
        if (!isDisabledByAdmin()) {
            setSummary(getSummaryString());
        }
    }

    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getContext().getSystemService("input_method");
    }

    private String getSummaryString() {
        return InputMethodAndSubtypeUtil.getSubtypeLocaleNameListAsSentence(getInputMethodManager().getEnabledInputMethodSubtypeList(this.mImi, true), getContext(), this.mImi);
    }

    private void setCheckedInternal(boolean z) {
        super.setChecked(z);
        this.mOnSaveListener.onSaveInputMethodPreference(this);
        notifyChanged();
    }

    private void showSecurityWarnDialog() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(C0017R$string.oneplus_input_dialog_alert_title);
        CharSequence loadLabel = this.mImi.getServiceInfo().applicationInfo.loadLabel(context.getPackageManager());
        builder.setMessage(context.getString(C0017R$string.oneplus_ime_security_warning, loadLabel));
        builder.setPositiveButton(C0017R$string.oneplus_ime_warning_confirm, new DialogInterface.OnClickListener() {
            /* class com.android.settingslib.$$Lambda$OPInputMethodPreference$NtGeHnMX1DAJE2ckpe_WH3DV4 */

            public final void onClick(DialogInterface dialogInterface, int i) {
                OPInputMethodPreference.this.lambda$showSecurityWarnDialog$0$OPInputMethodPreference(dialogInterface, i);
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener() {
            /* class com.android.settingslib.$$Lambda$OPInputMethodPreference$w8BKl7yJpqRQX2CpoF6jWCdUeAk */

            public final void onClick(DialogInterface dialogInterface, int i) {
                OPInputMethodPreference.this.lambda$showSecurityWarnDialog$1$OPInputMethodPreference(dialogInterface, i);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            /* class com.android.settingslib.$$Lambda$OPInputMethodPreference$USke2EHba4uewPWvqFx6i4X0 */

            public final void onCancel(DialogInterface dialogInterface) {
                OPInputMethodPreference.this.lambda$showSecurityWarnDialog$2$OPInputMethodPreference(dialogInterface);
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showSecurityWarnDialog$0 */
    public /* synthetic */ void lambda$showSecurityWarnDialog$0$OPInputMethodPreference(DialogInterface dialogInterface, int i) {
        if (this.mImi.getServiceInfo().directBootAware || isTv()) {
            setCheckedInternal(true);
        } else {
            showDirectBootWarnDialog();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showSecurityWarnDialog$1 */
    public /* synthetic */ void lambda$showSecurityWarnDialog$1$OPInputMethodPreference(DialogInterface dialogInterface, int i) {
        setCheckedInternal(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showSecurityWarnDialog$2 */
    public /* synthetic */ void lambda$showSecurityWarnDialog$2$OPInputMethodPreference(DialogInterface dialogInterface) {
        setCheckedInternal(false);
    }

    private boolean isTv() {
        return (getContext().getResources().getConfiguration().uiMode & 15) == 4;
    }

    private void showDirectBootWarnDialog() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setMessage(context.getText(C0017R$string.oneplus_direct_boot_inputmethod_unaware_dialog_message));
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            /* class com.android.settingslib.$$Lambda$OPInputMethodPreference$yMlXX3I3tK5RiM6_O1OFwaI */

            public final void onClick(DialogInterface dialogInterface, int i) {
                OPInputMethodPreference.this.lambda$showDirectBootWarnDialog$3$OPInputMethodPreference(dialogInterface, i);
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener() {
            /* class com.android.settingslib.$$Lambda$OPInputMethodPreference$yH6VYPB5Z40X5PvlMG_brlVWQN0 */

            public final void onClick(DialogInterface dialogInterface, int i) {
                OPInputMethodPreference.this.lambda$showDirectBootWarnDialog$4$OPInputMethodPreference(dialogInterface, i);
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showDirectBootWarnDialog$3 */
    public /* synthetic */ void lambda$showDirectBootWarnDialog$3$OPInputMethodPreference(DialogInterface dialogInterface, int i) {
        setCheckedInternal(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showDirectBootWarnDialog$4 */
    public /* synthetic */ void lambda$showDirectBootWarnDialog$4$OPInputMethodPreference(DialogInterface dialogInterface, int i) {
        setCheckedInternal(false);
    }

    public int compareTo(OPInputMethodPreference oPInputMethodPreference, Collator collator) {
        int i = 0;
        if (this == oPInputMethodPreference) {
            return 0;
        }
        boolean z = this.mHasPriorityInSorting;
        if (z != oPInputMethodPreference.mHasPriorityInSorting) {
            return z ? -1 : 1;
        }
        CharSequence title = getTitle();
        CharSequence title2 = oPInputMethodPreference.getTitle();
        boolean isEmpty = TextUtils.isEmpty(title);
        boolean isEmpty2 = TextUtils.isEmpty(title2);
        if (!isEmpty && !isEmpty2) {
            return collator.compare(title.toString(), title2.toString());
        }
        int i2 = isEmpty ? -1 : 0;
        if (isEmpty2) {
            i = -1;
        }
        return i2 - i;
    }
}
