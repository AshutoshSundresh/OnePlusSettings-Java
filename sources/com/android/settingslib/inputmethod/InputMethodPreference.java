package com.android.settingslib.inputmethod;

import android.app.AlertDialog;
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
import androidx.preference.Preference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.R$dimen;
import com.android.settingslib.R$string;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedSwitchPreference;
import java.text.Collator;

public class InputMethodPreference extends RestrictedSwitchPreference implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private static final String TAG = InputMethodPreference.class.getSimpleName();
    private AlertDialog mDialog;
    private final boolean mHasPriorityInSorting;
    private final InputMethodInfo mImi;
    private final InputMethodSettingValuesWrapper mInputMethodSettingValues;
    private final boolean mIsAllowedByOrganization;
    private final OnSavePreferenceListener mOnSaveListener;

    public interface OnSavePreferenceListener {
        void onSaveInputMethodPreference(InputMethodPreference inputMethodPreference);
    }

    public InputMethodPreference(Context context, InputMethodInfo inputMethodInfo, boolean z, boolean z2, OnSavePreferenceListener onSavePreferenceListener) {
        this(context, inputMethodInfo, inputMethodInfo.loadLabel(context.getPackageManager()), z2, onSavePreferenceListener);
        if (!z) {
            setWidgetLayoutResource(0);
        }
        setIconSize(context.getResources().getDimensionPixelSize(R$dimen.secondary_app_icon_size));
    }

    @VisibleForTesting
    InputMethodPreference(Context context, InputMethodInfo inputMethodInfo, CharSequence charSequence, boolean z, OnSavePreferenceListener onSavePreferenceListener) {
        super(context);
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
            Toast.makeText(context, context.getString(R$string.failed_to_open_app_settings_toast, this.mImi.loadLabel(context.getPackageManager())), 1).show();
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
        builder.setTitle(R$string.zzz_input_dialog_alert_title);
        CharSequence loadLabel = this.mImi.getServiceInfo().applicationInfo.loadLabel(context.getPackageManager());
        builder.setMessage(context.getString(R$string.zzz_op_ime_security_warning, loadLabel));
        builder.setPositiveButton(R$string.zzz_op_ime_warning_confirm, new DialogInterface.OnClickListener() {
            /* class com.android.settingslib.inputmethod.$$Lambda$InputMethodPreference$pHt46FWRQ9Ts6PuJy_AB14MhJc */

            public final void onClick(DialogInterface dialogInterface, int i) {
                InputMethodPreference.this.lambda$showSecurityWarnDialog$0$InputMethodPreference(dialogInterface, i);
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener() {
            /* class com.android.settingslib.inputmethod.$$Lambda$InputMethodPreference$HH5dtwzFZv06UNDXJAO6Cyx4kxo */

            public final void onClick(DialogInterface dialogInterface, int i) {
                InputMethodPreference.this.lambda$showSecurityWarnDialog$1$InputMethodPreference(dialogInterface, i);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            /* class com.android.settingslib.inputmethod.$$Lambda$InputMethodPreference$hpUUW_Jm1ATEk1GeQASyreqYZI */

            public final void onCancel(DialogInterface dialogInterface) {
                InputMethodPreference.this.lambda$showSecurityWarnDialog$2$InputMethodPreference(dialogInterface);
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showSecurityWarnDialog$0 */
    public /* synthetic */ void lambda$showSecurityWarnDialog$0$InputMethodPreference(DialogInterface dialogInterface, int i) {
        if (this.mImi.getServiceInfo().directBootAware || isTv()) {
            setCheckedInternal(true);
        } else {
            showDirectBootWarnDialog();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showSecurityWarnDialog$1 */
    public /* synthetic */ void lambda$showSecurityWarnDialog$1$InputMethodPreference(DialogInterface dialogInterface, int i) {
        setCheckedInternal(false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showSecurityWarnDialog$2 */
    public /* synthetic */ void lambda$showSecurityWarnDialog$2$InputMethodPreference(DialogInterface dialogInterface) {
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
        builder.setMessage(context.getText(R$string.zzz_op_direct_boot_inputmethod_unaware_dialog_message));
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            /* class com.android.settingslib.inputmethod.$$Lambda$InputMethodPreference$_R1WCgG1LabBNKieYWiJs9NnYv4 */

            public final void onClick(DialogInterface dialogInterface, int i) {
                InputMethodPreference.this.lambda$showDirectBootWarnDialog$3$InputMethodPreference(dialogInterface, i);
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener() {
            /* class com.android.settingslib.inputmethod.$$Lambda$InputMethodPreference$8Yu3IA81uQ9mforg_QOtWUG_Sj4 */

            public final void onClick(DialogInterface dialogInterface, int i) {
                InputMethodPreference.this.lambda$showDirectBootWarnDialog$4$InputMethodPreference(dialogInterface, i);
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showDirectBootWarnDialog$3 */
    public /* synthetic */ void lambda$showDirectBootWarnDialog$3$InputMethodPreference(DialogInterface dialogInterface, int i) {
        setCheckedInternal(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showDirectBootWarnDialog$4 */
    public /* synthetic */ void lambda$showDirectBootWarnDialog$4$InputMethodPreference(DialogInterface dialogInterface, int i) {
        setCheckedInternal(false);
    }

    public int compareTo(InputMethodPreference inputMethodPreference, Collator collator) {
        int i = 0;
        if (this == inputMethodPreference) {
            return 0;
        }
        boolean z = this.mHasPriorityInSorting;
        if (z != inputMethodPreference.mHasPriorityInSorting) {
            return z ? -1 : 1;
        }
        CharSequence title = getTitle();
        CharSequence title2 = inputMethodPreference.getTitle();
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
