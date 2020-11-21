package com.oneplus.settings.quicklaunch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.recyclerview.widget.RecyclerView;

class OPAppRecyclerView extends RecyclerView {
    public OPAppRecyclerView(Context context) {
        super(context);
    }

    public OPAppRecyclerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public OPAppRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public boolean onTouchEvent(MotionEvent motionEvent) {
        OPAppDragAndDropAdapter oPAppDragAndDropAdapter;
        if (motionEvent.getAction() == 1 && (oPAppDragAndDropAdapter = (OPAppDragAndDropAdapter) getAdapter()) != null) {
            oPAppDragAndDropAdapter.doTheUpdate();
        }
        return super.onTouchEvent(motionEvent);
    }
}
