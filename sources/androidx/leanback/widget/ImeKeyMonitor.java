package androidx.leanback.widget;

import android.view.KeyEvent;
import android.widget.EditText;

public interface ImeKeyMonitor {

    public interface ImeKeyListener {
        boolean onKeyPreIme(EditText editText, int i, KeyEvent keyEvent);
    }

    void setImeKeyListener(ImeKeyListener imeKeyListener);
}
