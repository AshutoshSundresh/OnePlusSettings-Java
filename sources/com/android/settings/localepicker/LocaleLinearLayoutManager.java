package com.android.settings.localepicker;

import android.content.Context;
import android.view.View;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;

public class LocaleLinearLayoutManager extends LinearLayoutManager {
    private final AccessibilityNodeInfoCompat.AccessibilityActionCompat mActionMoveBottom = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(C0010R$id.action_drag_move_bottom, this.mContext.getString(C0017R$string.action_drag_label_move_bottom));
    private final AccessibilityNodeInfoCompat.AccessibilityActionCompat mActionMoveDown = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(C0010R$id.action_drag_move_down, this.mContext.getString(C0017R$string.action_drag_label_move_down));
    private final AccessibilityNodeInfoCompat.AccessibilityActionCompat mActionMoveTop = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(C0010R$id.action_drag_move_top, this.mContext.getString(C0017R$string.action_drag_label_move_top));
    private final AccessibilityNodeInfoCompat.AccessibilityActionCompat mActionMoveUp = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(C0010R$id.action_drag_move_up, this.mContext.getString(C0017R$string.action_drag_label_move_up));
    private final AccessibilityNodeInfoCompat.AccessibilityActionCompat mActionRemove = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(C0010R$id.action_drag_remove, this.mContext.getString(C0017R$string.action_drag_label_remove));
    private final LocaleDragAndDropAdapter mAdapter;
    private final Context mContext;

    public LocaleLinearLayoutManager(Context context, LocaleDragAndDropAdapter localeDragAndDropAdapter) {
        super(context);
        this.mContext = context;
        this.mAdapter = localeDragAndDropAdapter;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        super.onInitializeAccessibilityNodeInfoForItem(recycler, state, view, accessibilityNodeInfoCompat);
        int itemCount = getItemCount();
        int position = getPosition(view);
        StringBuilder sb = new StringBuilder();
        int i = position + 1;
        sb.append(i);
        sb.append(", ");
        sb.append((Object) ((LocaleDragCell) view).getCheckbox().getContentDescription());
        accessibilityNodeInfoCompat.setContentDescription(sb.toString());
        if (!this.mAdapter.isRemoveMode()) {
            if (position > 0) {
                accessibilityNodeInfoCompat.addAction(this.mActionMoveUp);
                accessibilityNodeInfoCompat.addAction(this.mActionMoveTop);
            }
            if (i < itemCount) {
                accessibilityNodeInfoCompat.addAction(this.mActionMoveDown);
                accessibilityNodeInfoCompat.addAction(this.mActionMoveBottom);
            }
            if (itemCount > 1) {
                accessibilityNodeInfoCompat.addAction(this.mActionRemove);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x004e  */
    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean performAccessibilityActionForItem(androidx.recyclerview.widget.RecyclerView.Recycler r6, androidx.recyclerview.widget.RecyclerView.State r7, android.view.View r8, int r9, android.os.Bundle r10) {
        /*
            r5 = this;
            int r0 = r5.getItemCount()
            int r1 = r5.getPosition(r8)
            int r2 = com.android.settings.C0010R$id.action_drag_move_up
            r3 = 0
            r4 = 1
            if (r9 != r2) goto L_0x0019
            if (r1 <= 0) goto L_0x004c
            com.android.settings.localepicker.LocaleDragAndDropAdapter r6 = r5.mAdapter
            int r7 = r1 + -1
            r6.onItemMove(r1, r7)
        L_0x0017:
            r3 = r4
            goto L_0x004c
        L_0x0019:
            int r2 = com.android.settings.C0010R$id.action_drag_move_down
            if (r9 != r2) goto L_0x0027
            int r6 = r1 + 1
            if (r6 >= r0) goto L_0x004c
            com.android.settings.localepicker.LocaleDragAndDropAdapter r7 = r5.mAdapter
            r7.onItemMove(r1, r6)
            goto L_0x0017
        L_0x0027:
            int r2 = com.android.settings.C0010R$id.action_drag_move_top
            if (r9 != r2) goto L_0x0033
            if (r1 == 0) goto L_0x004c
            com.android.settings.localepicker.LocaleDragAndDropAdapter r6 = r5.mAdapter
            r6.onItemMove(r1, r3)
            goto L_0x0017
        L_0x0033:
            int r2 = com.android.settings.C0010R$id.action_drag_move_bottom
            if (r9 != r2) goto L_0x0040
            int r0 = r0 - r4
            if (r1 == r0) goto L_0x004c
            com.android.settings.localepicker.LocaleDragAndDropAdapter r6 = r5.mAdapter
            r6.onItemMove(r1, r0)
            goto L_0x0017
        L_0x0040:
            int r2 = com.android.settings.C0010R$id.action_drag_remove
            if (r9 != r2) goto L_0x0054
            if (r0 <= r4) goto L_0x004c
            com.android.settings.localepicker.LocaleDragAndDropAdapter r6 = r5.mAdapter
            r6.removeItem(r1)
            goto L_0x0017
        L_0x004c:
            if (r3 == 0) goto L_0x0053
            com.android.settings.localepicker.LocaleDragAndDropAdapter r5 = r5.mAdapter
            r5.doTheUpdate()
        L_0x0053:
            return r3
        L_0x0054:
            boolean r5 = super.performAccessibilityActionForItem(r6, r7, r8, r9, r10)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.localepicker.LocaleLinearLayoutManager.performAccessibilityActionForItem(androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State, android.view.View, int, android.os.Bundle):boolean");
    }
}
