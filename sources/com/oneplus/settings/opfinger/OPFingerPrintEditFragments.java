package com.oneplus.settings.opfinger;

import android.app.Activity;
import android.content.DialogInterface;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.OPProgressDialog;

public class OPFingerPrintEditFragments extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener {
    private boolean isDeleteDialogShow;
    private boolean isRenameDialogShow;
    private boolean isWarnDialogShow;
    private AlertDialog mDeleteDialog;
    private Fingerprint mFingerprint;
    private FingerprintManager mFingerprintManager;
    private CharSequence mFingerprintName;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.oneplus.settings.opfinger.OPFingerPrintEditFragments.AnonymousClass1 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            int i = message.what;
            if (i == 1) {
                FragmentActivity activity = OPFingerPrintEditFragments.this.getActivity();
                if (activity != null && !activity.isDestroyed()) {
                    OPFingerPrintEditFragments.this.mProgressDialog.setMessage(OPFingerPrintEditFragments.this.getResources().getString(C0017R$string.oneplus_deleteing_fingerprint_list));
                    OPFingerPrintEditFragments.this.mProgressDialog.show();
                }
            } else if (i == 3) {
                OPFingerPrintEditFragments.this.mProgressDialog.dismiss();
                Toast.makeText(SettingsBaseApplication.mApplication, C0017R$string.oneplus_deleted_fingerprint_list_failed, 0).show();
            } else if (i == 4) {
                OPFingerPrintEditFragments.this.showRenameDialog();
            } else if (i == 5) {
                Toast.makeText(SettingsBaseApplication.mApplication, C0017R$string.oneplus_deleted_fingerprint_list, 0).show();
                if (OPFingerPrintEditFragments.this.mProgressDialog != null) {
                    OPFingerPrintEditFragments.this.mProgressDialog.dismiss();
                }
                OPFingerPrintEditFragments.this.finish();
            } else if (i == 6) {
                OPFingerPrintEditFragments.this.showWarnigDialog((Fingerprint) message.obj);
            } else if (i == 7) {
                OPFingerPrintEditFragments.this.showDeleteDialog((Fingerprint) message.obj);
            }
        }
    };
    private OPFingerPrintEditCategory mOPFingerPrintEditViewCategory;
    private OPProgressDialog mProgressDialog;
    private FingerprintManager.RemovalCallback mRemoveCallback = new FingerprintManager.RemovalCallback() {
        /* class com.oneplus.settings.opfinger.OPFingerPrintEditFragments.AnonymousClass2 */

        public void onRemovalSucceeded(Fingerprint fingerprint, int i) {
            OPFingerPrintEditFragments.this.mHandler.obtainMessage(5, fingerprint.getBiometricId(), 0).sendToTarget();
        }

        public void onRemovalError(Fingerprint fingerprint, int i, CharSequence charSequence) {
            FragmentActivity activity = OPFingerPrintEditFragments.this.getActivity();
            if (activity != null) {
                Toast.makeText(activity, charSequence, 0);
            }
        }
    };
    private AlertDialog mRenameDialog;
    private int mUserId;
    private AlertDialog mWarnDialog;
    private String renameData;
    private EditText renameEdit;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        if (bundle != null) {
            this.isRenameDialogShow = bundle.getBoolean("renamedialog");
            this.isDeleteDialogShow = bundle.getBoolean("deletedialog");
            this.isWarnDialogShow = bundle.getBoolean("warndialog");
            this.renameData = bundle.getString("renamedata");
        }
        super.onCreate(bundle);
        this.mUserId = getActivity().getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
        Fingerprint parcelable = getArguments().getParcelable("fingerprint_parcelable");
        this.mFingerprint = parcelable;
        if (bundle != null) {
            this.mFingerprintName = bundle.getCharSequence("fingerprint_name");
        } else if (parcelable != null) {
            this.mFingerprintName = parcelable.getName();
        }
        this.mProgressDialog = new OPProgressDialog(getActivity());
        this.mFingerprintManager = (FingerprintManager) getActivity().getSystemService("fingerprint");
        addPreferencesFromResource(C0019R$xml.op_fingerprint_edit);
        initViews();
        if (this.isRenameDialogShow) {
            showRenameDialog();
        } else if (this.isWarnDialogShow) {
            showWarnigDialog(this.mFingerprint);
        } else if (this.isDeleteDialogShow) {
            showDeleteDialog(this.mFingerprint);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        AlertDialog alertDialog = this.mRenameDialog;
        if (alertDialog != null) {
            bundle.putBoolean("renamedialog", alertDialog.isShowing());
            bundle.putString("renamedata", this.renameEdit.getText().toString());
        }
        AlertDialog alertDialog2 = this.mWarnDialog;
        if (alertDialog2 != null) {
            bundle.putBoolean("warndialog", alertDialog2.isShowing());
        }
        AlertDialog alertDialog3 = this.mDeleteDialog;
        if (alertDialog3 != null) {
            bundle.putBoolean("deletedialog", alertDialog3.isShowing());
        }
        bundle.putCharSequence("fingerprint_name", this.mFingerprintName);
    }

    private void initViews() {
        this.mOPFingerPrintEditViewCategory = (OPFingerPrintEditCategory) findPreference("key_opfinger_edit");
        findPreference("opfingerprint_rename");
        findPreference("opfingerprint_delete");
        this.mOPFingerPrintEditViewCategory.setFingerprintName(this.mFingerprintName);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void renameFingerPrint(int i, String str) {
        this.mFingerprintManager.rename(i, this.mUserId, str);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void deleteFingerPrint(Fingerprint fingerprint) {
        this.mFingerprintManager.remove(fingerprint, this.mUserId, this.mRemoveCallback);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if ("opfingerprint_rename".equals(key)) {
            this.mHandler.sendEmptyMessage(4);
            return true;
        } else if (!"opfingerprint_delete".equals(key)) {
            return true;
        } else {
            new Thread(new Runnable() {
                /* class com.oneplus.settings.opfinger.OPFingerPrintEditFragments.AnonymousClass3 */

                public void run() {
                    if (OPFingerPrintEditFragments.this.mFingerprintManager != null) {
                        Message obtainMessage = OPFingerPrintEditFragments.this.mHandler.obtainMessage();
                        if (OPFingerPrintEditFragments.this.mFingerprintManager.getEnrolledFingerprints().size() == 1) {
                            obtainMessage.what = 6;
                        } else {
                            obtainMessage.what = 7;
                        }
                        obtainMessage.obj = OPFingerPrintEditFragments.this.mFingerprint;
                        OPFingerPrintEditFragments.this.mHandler.sendMessage(obtainMessage);
                        return;
                    }
                    OPFingerPrintEditFragments.this.mHandler.sendEmptyMessage(3);
                }
            }).start();
            return true;
        }
    }

    public void showWarnigDialog(final Fingerprint fingerprint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0017R$string.fingerprint_last_delete_title);
        builder.setMessage(C0017R$string.fingerprint_last_delete_message);
        builder.setPositiveButton(C0017R$string.okay, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.opfinger.OPFingerPrintEditFragments.AnonymousClass5 */

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                OPFingerPrintEditFragments.this.mHandler.sendEmptyMessage(1);
                OPFingerPrintEditFragments.this.deleteFingerPrint(fingerprint);
                OPFingerPrintEditFragments.this.mHandler.sendEmptyMessageDelayed(2, 100);
            }
        });
        builder.setNegativeButton(C0017R$string.cancel, new DialogInterface.OnClickListener(this) {
            /* class com.oneplus.settings.opfinger.OPFingerPrintEditFragments.AnonymousClass4 */

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog create = builder.create();
        this.mWarnDialog = create;
        create.show();
    }

    public void showDeleteDialog(final Fingerprint fingerprint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0017R$string.security_settings_fingerprint_enroll_dialog_delete);
        builder.setMessage(C0017R$string.oneplus_fingerprint_delete_confirm_message);
        builder.setPositiveButton(C0017R$string.okay, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.opfinger.OPFingerPrintEditFragments.AnonymousClass7 */

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                OPFingerPrintEditFragments.this.mHandler.sendEmptyMessage(1);
                OPFingerPrintEditFragments.this.deleteFingerPrint(fingerprint);
                OPFingerPrintEditFragments.this.mHandler.sendEmptyMessageDelayed(2, 100);
            }
        });
        builder.setNegativeButton(C0017R$string.cancel, new DialogInterface.OnClickListener(this) {
            /* class com.oneplus.settings.opfinger.OPFingerPrintEditFragments.AnonymousClass6 */

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog create = builder.create();
        this.mDeleteDialog = create;
        create.show();
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
    }

    public void showRenameDialog() {
        View inflate = LayoutInflater.from(getActivity()).inflate(C0012R$layout.op_fingerprint_rename_dialog, (ViewGroup) null);
        EditText editText = (EditText) inflate.findViewById(C0010R$id.opfinger_rename_ed);
        this.renameEdit = editText;
        editText.setHint(this.mFingerprintName);
        this.renameEdit.requestFocus();
        this.renameEdit.setText(this.renameData);
        this.renameEdit.addTextChangedListener(new TextWatcher(this) {
            /* class com.oneplus.settings.opfinger.OPFingerPrintEditFragments.AnonymousClass8 */

            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0017R$string.user_rename);
        builder.setView(inflate);
        builder.setCancelable(true);
        builder.setPositiveButton(C0017R$string.okay, (DialogInterface.OnClickListener) null);
        builder.setNegativeButton(C0017R$string.alert_dialog_cancel, new DialogInterface.OnClickListener(this) {
            /* class com.oneplus.settings.opfinger.OPFingerPrintEditFragments.AnonymousClass9 */

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog create = builder.create();
        this.mRenameDialog = create;
        create.show();
        this.mRenameDialog.getButton(-1).setOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.opfinger.OPFingerPrintEditFragments.AnonymousClass10 */

            public void onClick(View view) {
                String trim = OPFingerPrintEditFragments.this.renameEdit.getText().toString().trim();
                if ("".equals(trim)) {
                    String str = (String) OPFingerPrintEditFragments.this.mFingerprintName;
                    Toast.makeText(OPFingerPrintEditFragments.this.getActivity(), C0017R$string.oneplus_opfinger_input_only_space, 0).show();
                    return;
                }
                OPFingerPrintEditFragments.this.mFingerprintName = trim;
                OPFingerPrintEditFragments oPFingerPrintEditFragments = OPFingerPrintEditFragments.this;
                oPFingerPrintEditFragments.renameFingerPrint(oPFingerPrintEditFragments.mFingerprint.getBiometricId(), trim);
                OPFingerPrintEditFragments.this.mFingerprintName = trim;
                OPFingerPrintEditFragments.this.mOPFingerPrintEditViewCategory.setFingerprintName(trim);
                OPFingerPrintEditFragments.this.mRenameDialog.dismiss();
            }
        });
    }
}
