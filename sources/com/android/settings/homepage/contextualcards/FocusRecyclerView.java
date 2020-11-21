package com.android.settings.homepage.contextualcards;

import android.content.Context;
import android.util.AttributeSet;
import androidx.recyclerview.widget.RecyclerView;

public class FocusRecyclerView extends RecyclerView {
    private FocusListener mListener;

    public interface FocusListener {
        void onWindowFocusChanged(boolean z);
    }

    public FocusRecyclerView(Context context) {
        super(context);
    }

    public FocusRecyclerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        FocusListener focusListener = this.mListener;
        if (focusListener != null) {
            focusListener.onWindowFocusChanged(z);
        }
    }

    public void setListener(FocusListener focusListener) {
        this.mListener = focusListener;
    }
}
