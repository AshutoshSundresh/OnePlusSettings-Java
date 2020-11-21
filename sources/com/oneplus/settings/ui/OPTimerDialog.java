package com.oneplus.settings.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;

public class OPTimerDialog {
    private Context mContext;
    private AlertDialog mDialog = null;
    private Handler mHandler = new Handler() {
        /* class com.oneplus.settings.ui.OPTimerDialog.AnonymousClass2 */

        public void handleMessage(Message message) {
            int i = message.what;
            if (i != 1) {
                if (i == 2) {
                    if (OPTimerDialog.this.mNegativeCount > 0) {
                        OPTimerDialog.access$110(OPTimerDialog.this);
                        if (OPTimerDialog.this.n != null) {
                            Button button = OPTimerDialog.this.n;
                            OPTimerDialog oPTimerDialog = OPTimerDialog.this;
                            button.setText(oPTimerDialog.getTimeText((String) OPTimerDialog.this.n.getText(), oPTimerDialog.mNegativeCount));
                        }
                        OPTimerDialog.this.mHandler.sendEmptyMessageDelayed(2, 1000);
                    } else if (OPTimerDialog.this.n == null) {
                    } else {
                        if (OPTimerDialog.this.n.isEnabled()) {
                            OPTimerDialog.this.n.performClick();
                        } else {
                            OPTimerDialog.this.n.setEnabled(true);
                        }
                    }
                }
            } else if (OPTimerDialog.this.mPositiveCount > 0) {
                OPTimerDialog.access$410(OPTimerDialog.this);
                if (OPTimerDialog.this.p != null) {
                    String str = (String) OPTimerDialog.this.p.getText();
                    OPTimerDialog oPTimerDialog2 = OPTimerDialog.this;
                    oPTimerDialog2.setMessage("已经达到定时关机时间," + String.valueOf(OPTimerDialog.this.mPositiveCount) + "s后确认关机?");
                }
                if (OPTimerDialog.this.mHandler != null) {
                    OPTimerDialog.this.mHandler.sendEmptyMessageDelayed(1, 1000);
                }
            } else if (OPTimerDialog.this.p != null && OPTimerDialog.this.status) {
                if (OPTimerDialog.this.p.isEnabled()) {
                    OPTimerDialog.this.p.performClick();
                } else {
                    OPTimerDialog.this.p.setEnabled(true);
                }
            }
        }
    };
    private int mNegativeCount = 0;
    private int mPositiveCount = 0;
    private Button n = null;
    private Button p = null;
    private boolean status = true;

    static /* synthetic */ int access$110(OPTimerDialog oPTimerDialog) {
        int i = oPTimerDialog.mNegativeCount;
        oPTimerDialog.mNegativeCount = i - 1;
        return i;
    }

    static /* synthetic */ int access$410(OPTimerDialog oPTimerDialog) {
        int i = oPTimerDialog.mPositiveCount;
        oPTimerDialog.mPositiveCount = i - 1;
        return i;
    }

    public OPTimerDialog(Context context) {
        this.mContext = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.getWindow().setType(2003);
        this.mDialog.getWindow().setType(2009);
        this.mDialog.setCanceledOnTouchOutside(false);
    }

    public void setMessage(String str) {
        this.mDialog.setMessage(str);
    }

    public void setTitle(String str) {
        this.mDialog.setTitle(str);
    }

    public void show() {
        this.mDialog.show();
    }

    public void setStatus(boolean z) {
        this.status = z;
    }

    public void setPositiveButton(String str, DialogInterface.OnClickListener onClickListener, int i) {
        this.mDialog.setButton(-1, str, onClickListener);
    }

    public void setNegativeButton(String str, final DialogInterface.OnClickListener onClickListener, int i) {
        this.mDialog.setButton(-2, str, onClickListener);
        this.mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            /* class com.oneplus.settings.ui.OPTimerDialog.AnonymousClass1 */

            public void onCancel(DialogInterface dialogInterface) {
                onClickListener.onClick(OPTimerDialog.this.mDialog, 2);
            }
        });
    }

    public void dismiss() {
        setStatus(false);
        if (this.mHandler != null) {
            this.mHandler = null;
        }
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setButtonType(int i, int i2, boolean z) {
        if (i2 > 0) {
            if (i == -1) {
                Button button = this.mDialog.getButton(-1);
                this.p = button;
                button.setEnabled(z);
                this.mPositiveCount = i2;
            } else if (i == -2) {
                Button button2 = this.mDialog.getButton(-2);
                this.n = button2;
                button2.setEnabled(z);
                this.mNegativeCount = i2;
            }
        }
    }

    public Button getPButton() {
        Button button = this.p;
        if (button != null) {
            return button;
        }
        return null;
    }

    public Button getNButton() {
        Button button = this.n;
        if (button != null) {
            return button;
        }
        return null;
    }

    public String getTimeText(String str, int i) {
        if (str == null || str.length() <= 0 || i <= 0) {
            return str;
        }
        int indexOf = str.indexOf("(");
        if (indexOf > 0) {
            String substring = str.substring(0, indexOf);
            return substring + "(" + i + "s)";
        }
        return str + "(" + i + "s)";
    }

    public boolean isShowing() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            return alertDialog.isShowing();
        }
        return false;
    }
}
