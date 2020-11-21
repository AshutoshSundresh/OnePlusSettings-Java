package com.oneplus.settings.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;

public class OPListDialog {
    private Context mContext;
    private int mCurrentIndex = 0;
    private AlertDialog mDialog = null;
    private String[] mListEntries;
    private String[] mListEntriesValue;
    private OnDialogListItemClickListener mOnDialogListItemClickListener;
    private RadioGroup mRootContainer;

    public interface OnDialogListItemClickListener {
        void OnDialogListCancelClick();

        void OnDialogListConfirmClick(int i);

        void OnDialogListItemClick(int i);
    }

    public OPListDialog(Context context, CharSequence charSequence, String[] strArr, String[] strArr2) {
        this.mContext = context;
        this.mListEntriesValue = strArr;
        this.mListEntries = strArr2;
        View inflate = LayoutInflater.from(context).inflate(C0012R$layout.op_list_dialog_item_layout, (ViewGroup) null);
        this.mRootContainer = (RadioGroup) inflate.findViewById(C0010R$id.radioGroup);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setView(this.mRootContainer);
        builder.setTitle(charSequence);
        builder.setView(inflate);
        builder.setPositiveButton(C0017R$string.okay, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.ui.OPListDialog.AnonymousClass2 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (OPListDialog.this.mOnDialogListItemClickListener != null) {
                    OPListDialog.this.mOnDialogListItemClickListener.OnDialogListConfirmClick(OPListDialog.this.mCurrentIndex);
                }
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(C0017R$string.cancel, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.ui.OPListDialog.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (OPListDialog.this.mOnDialogListItemClickListener != null) {
                    OPListDialog.this.mOnDialogListItemClickListener.OnDialogListCancelClick();
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.setCanceledOnTouchOutside(true);
        this.mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            /* class com.oneplus.settings.ui.OPListDialog.AnonymousClass3 */

            public void onCancel(DialogInterface dialogInterface) {
                if (OPListDialog.this.mOnDialogListItemClickListener != null) {
                    OPListDialog.this.mOnDialogListItemClickListener.OnDialogListCancelClick();
                }
            }
        });
    }

    public void setOnDialogListItemClickListener(OnDialogListItemClickListener onDialogListItemClickListener) {
        this.mOnDialogListItemClickListener = onDialogListItemClickListener;
    }

    public void setVibrateLevelKey(String str) {
        this.mCurrentIndex = Settings.System.getInt(this.mContext.getContentResolver(), str, 0);
        int length = this.mListEntriesValue.length;
        for (int i = 0; i < length; i++) {
            RadioButton radioButton = (RadioButton) this.mRootContainer.getChildAt(i);
            radioButton.setVisibility(0);
            radioButton.setText(this.mListEntries[i]);
            if (this.mCurrentIndex == i) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
            radioButton.setOnClickListener(new View.OnClickListener() {
                /* class com.oneplus.settings.ui.OPListDialog.AnonymousClass5 */

                public void onClick(View view) {
                    int id = view.getId();
                    int i = 0;
                    if (C0010R$id.item_1 != id) {
                        if (C0010R$id.item_2 == id) {
                            i = 1;
                        } else if (C0010R$id.item_3 == id) {
                            i = 2;
                        } else if (C0010R$id.item_4 == id) {
                            i = 3;
                        } else if (C0010R$id.item_5 == id) {
                            i = 4;
                        } else if (C0010R$id.item_6 == id) {
                            i = 5;
                        }
                    }
                    OPListDialog.this.mCurrentIndex = i;
                    if (OPListDialog.this.mOnDialogListItemClickListener != null) {
                        OPListDialog.this.mOnDialogListItemClickListener.OnDialogListItemClick(i);
                    }
                }
            });
        }
    }

    public void show() {
        this.mDialog.show();
    }

    public View getRootContainer() {
        return this.mRootContainer;
    }
}
