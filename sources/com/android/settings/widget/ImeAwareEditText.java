package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ImeAwareEditText extends EditText {
    private boolean mHasPendingShowSoftInputRequest;
    final Runnable mRunShowSoftInputIfNecessary = new Runnable() {
        /* class com.android.settings.widget.$$Lambda$ImeAwareEditText$jSRw3KSZxc80AfkP8GTCtV5_bRY */

        public final void run() {
            ImeAwareEditText.this.lambda$new$0$ImeAwareEditText();
        }
    };

    public ImeAwareEditText(Context context) {
        super(context, null);
    }

    public ImeAwareEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ImeAwareEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public ImeAwareEditText(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
        if (this.mHasPendingShowSoftInputRequest) {
            removeCallbacks(this.mRunShowSoftInputIfNecessary);
            post(this.mRunShowSoftInputIfNecessary);
        }
        return onCreateInputConnection;
    }

    /* access modifiers changed from: private */
    /* renamed from: showSoftInputIfNecessary */
    public void lambda$new$0() {
        if (this.mHasPendingShowSoftInputRequest) {
            ((InputMethodManager) getContext().getSystemService(InputMethodManager.class)).showSoftInput(this, 0);
            this.mHasPendingShowSoftInputRequest = false;
        }
    }
}
