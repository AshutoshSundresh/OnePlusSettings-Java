package androidx.appcompat.app;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.appcompat.R$id;
import androidx.appcompat.R$layout;

public class EditTextDialog extends AlertDialog {
    private EditText mEditText;

    public EditTextDialog(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog
    public void onCreate(Bundle bundle) {
        Log.i("OPEditTextDialog", "onCreate");
        View inflate = LayoutInflater.from(getContext()).inflate(R$layout.oneplus_control_alert_dialog_with_edittext, (ViewGroup) null);
        initView(inflate);
        setView(inflate);
        super.onCreate(bundle);
    }

    /* access modifiers changed from: package-private */
    public void initView(View view) {
        this.mEditText = (EditText) view.findViewById(R$id.edit_text);
    }

    public EditText getEditText() {
        if (this.mEditText == null) {
            this.mEditText = (EditText) findViewById(R$id.edit_text);
        }
        return this.mEditText;
    }
}
