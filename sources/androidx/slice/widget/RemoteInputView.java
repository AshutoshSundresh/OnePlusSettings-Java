package androidx.slice.widget;

import android.animation.Animator;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.CompletionInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.widget.TextViewCompat;
import androidx.slice.SliceItem;
import androidx.slice.view.R$id;
import androidx.slice.view.R$layout;

public class RemoteInputView extends LinearLayout implements View.OnClickListener, TextWatcher {
    public static final Object VIEW_TAG = new Object();
    private SliceItem mAction;
    RemoteEditText mEditText;
    private ProgressBar mProgressBar;
    private RemoteInput mRemoteInput;
    private RemoteInput[] mRemoteInputs;
    private boolean mResetting;
    private int mRevealCx;
    private int mRevealCy;
    private int mRevealR;
    private ImageButton mSendButton;

    public static final boolean isConfirmKey(int i) {
        return i == 23 || i == 62 || i == 66 || i == 160;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public RemoteInputView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mProgressBar = (ProgressBar) findViewById(R$id.remote_input_progress);
        ImageButton imageButton = (ImageButton) findViewById(R$id.remote_input_send);
        this.mSendButton = imageButton;
        imageButton.setOnClickListener(this);
        RemoteEditText remoteEditText = (RemoteEditText) getChildAt(0);
        this.mEditText = remoteEditText;
        remoteEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            /* class androidx.slice.widget.RemoteInputView.AnonymousClass1 */

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean z = keyEvent == null && (i == 6 || i == 5 || i == 4);
                boolean z2 = keyEvent != null && RemoteInputView.isConfirmKey(keyEvent.getKeyCode()) && keyEvent.getAction() == 0;
                if (!z && !z2) {
                    return false;
                }
                if (RemoteInputView.this.mEditText.length() > 0) {
                    RemoteInputView.this.sendRemoteInput();
                }
                return true;
            }
        });
        this.mEditText.addTextChangedListener(this);
        this.mEditText.setInnerFocusable(false);
        this.mEditText.mRemoteInputView = this;
    }

    /* access modifiers changed from: package-private */
    public void sendRemoteInput() {
        Bundle bundle = new Bundle();
        bundle.putString(this.mRemoteInput.getResultKey(), this.mEditText.getText().toString());
        Intent addFlags = new Intent().addFlags(268435456);
        RemoteInput.addResultsToIntent(this.mRemoteInputs, addFlags, bundle);
        this.mEditText.setEnabled(false);
        this.mSendButton.setVisibility(4);
        this.mProgressBar.setVisibility(0);
        this.mEditText.mShowImeOnInputConnection = false;
        try {
            this.mAction.fireAction(getContext(), addFlags);
            reset();
        } catch (PendingIntent.CanceledException e) {
            Log.i("RemoteInput", "Unable to send remote input result", e);
            Toast.makeText(getContext(), "Failure sending pending intent for inline reply :(", 0).show();
            reset();
        }
    }

    public static RemoteInputView inflate(Context context, ViewGroup viewGroup) {
        RemoteInputView remoteInputView = (RemoteInputView) LayoutInflater.from(context).inflate(R$layout.abc_slice_remote_input, viewGroup, false);
        remoteInputView.setTag(VIEW_TAG);
        return remoteInputView;
    }

    public void onClick(View view) {
        if (view == this.mSendButton) {
            sendRemoteInput();
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void onDefocus() {
        setVisibility(4);
    }

    public void setAction(SliceItem sliceItem) {
        this.mAction = sliceItem;
    }

    public void setRemoteInput(RemoteInput[] remoteInputArr, RemoteInput remoteInput) {
        this.mRemoteInputs = remoteInputArr;
        this.mRemoteInput = remoteInput;
        this.mEditText.setHint(remoteInput.getLabel());
    }

    public void focusAnimated() {
        if (getVisibility() != 0) {
            Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(this, this.mRevealCx, this.mRevealCy, 0.0f, (float) this.mRevealR);
            createCircularReveal.setDuration(200);
            createCircularReveal.start();
        }
        focus();
    }

    private void focus() {
        setVisibility(0);
        this.mEditText.setInnerFocusable(true);
        RemoteEditText remoteEditText = this.mEditText;
        remoteEditText.mShowImeOnInputConnection = true;
        remoteEditText.setSelection(remoteEditText.getText().length());
        this.mEditText.requestFocus();
        updateSendButton();
    }

    private void reset() {
        this.mResetting = true;
        this.mEditText.getText().clear();
        this.mEditText.setEnabled(true);
        this.mSendButton.setVisibility(0);
        this.mProgressBar.setVisibility(4);
        updateSendButton();
        onDefocus();
        this.mResetting = false;
    }

    public boolean onRequestSendAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        if (!this.mResetting || view != this.mEditText) {
            return super.onRequestSendAccessibilityEvent(view, accessibilityEvent);
        }
        return false;
    }

    private void updateSendButton() {
        this.mSendButton.setEnabled(this.mEditText.getText().length() != 0);
    }

    public void afterTextChanged(Editable editable) {
        updateSendButton();
    }

    public void setRevealParameters(int i, int i2, int i3) {
        this.mRevealCx = i;
        this.mRevealCy = i2;
        this.mRevealR = i3;
    }

    public void dispatchStartTemporaryDetach() {
        super.dispatchStartTemporaryDetach();
        detachViewFromParent(this.mEditText);
    }

    public void dispatchFinishTemporaryDetach() {
        if (isAttachedToWindow()) {
            RemoteEditText remoteEditText = this.mEditText;
            attachViewToParent(remoteEditText, 0, remoteEditText.getLayoutParams());
        } else {
            removeDetachedView(this.mEditText, false);
        }
        super.dispatchFinishTemporaryDetach();
    }

    public static class RemoteEditText extends EditText {
        private final Drawable mBackground = getBackground();
        RemoteInputView mRemoteInputView;
        boolean mShowImeOnInputConnection;

        public RemoteEditText(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        private void defocusIfNeeded() {
            if (this.mRemoteInputView != null || isTemporarilyDetachedCompat()) {
                isTemporarilyDetachedCompat();
            } else if (isFocusable() && isEnabled()) {
                setInnerFocusable(false);
                RemoteInputView remoteInputView = this.mRemoteInputView;
                if (remoteInputView != null) {
                    remoteInputView.onDefocus();
                }
                this.mShowImeOnInputConnection = false;
            }
        }

        private boolean isTemporarilyDetachedCompat() {
            if (Build.VERSION.SDK_INT >= 24) {
                return isTemporarilyDetached();
            }
            return false;
        }

        /* access modifiers changed from: protected */
        public void onVisibilityChanged(View view, int i) {
            super.onVisibilityChanged(view, i);
            if (!isShown()) {
                defocusIfNeeded();
            }
        }

        /* access modifiers changed from: protected */
        public void onFocusChanged(boolean z, int i, Rect rect) {
            super.onFocusChanged(z, i, rect);
            if (!z) {
                defocusIfNeeded();
            }
        }

        public void getFocusedRect(Rect rect) {
            super.getFocusedRect(rect);
            rect.top = getScrollY();
            rect.bottom = getScrollY() + (getBottom() - getTop());
        }

        public boolean onKeyDown(int i, KeyEvent keyEvent) {
            if (i == 4) {
                return true;
            }
            return super.onKeyDown(i, keyEvent);
        }

        public boolean onKeyUp(int i, KeyEvent keyEvent) {
            if (i != 4) {
                return super.onKeyUp(i, keyEvent);
            }
            defocusIfNeeded();
            return true;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:3:0x000a, code lost:
            r0 = (android.view.inputmethod.InputMethodManager) androidx.core.content.ContextCompat.getSystemService(getContext(), android.view.inputmethod.InputMethodManager.class);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public android.view.inputmethod.InputConnection onCreateInputConnection(android.view.inputmethod.EditorInfo r3) {
            /*
                r2 = this;
                android.view.inputmethod.InputConnection r3 = super.onCreateInputConnection(r3)
                boolean r0 = r2.mShowImeOnInputConnection
                if (r0 == 0) goto L_0x0020
                if (r3 == 0) goto L_0x0020
                android.content.Context r0 = r2.getContext()
                java.lang.Class<android.view.inputmethod.InputMethodManager> r1 = android.view.inputmethod.InputMethodManager.class
                java.lang.Object r0 = androidx.core.content.ContextCompat.getSystemService(r0, r1)
                android.view.inputmethod.InputMethodManager r0 = (android.view.inputmethod.InputMethodManager) r0
                if (r0 == 0) goto L_0x0020
                androidx.slice.widget.RemoteInputView$RemoteEditText$1 r1 = new androidx.slice.widget.RemoteInputView$RemoteEditText$1
                r1.<init>(r0)
                r2.post(r1)
            L_0x0020:
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.RemoteInputView.RemoteEditText.onCreateInputConnection(android.view.inputmethod.EditorInfo):android.view.inputmethod.InputConnection");
        }

        public void onCommitCompletion(CompletionInfo completionInfo) {
            clearComposingText();
            setText(completionInfo.getText());
            setSelection(getText().length());
        }

        /* access modifiers changed from: package-private */
        public void setInnerFocusable(boolean z) {
            setFocusableInTouchMode(z);
            setFocusable(z);
            setCursorVisible(z);
            if (z) {
                requestFocus();
                setBackground(this.mBackground);
                return;
            }
            setBackground(null);
        }

        public void setCustomSelectionActionModeCallback(ActionMode.Callback callback) {
            super.setCustomSelectionActionModeCallback(TextViewCompat.wrapCustomSelectionActionModeCallback(this, callback));
        }
    }
}
