package androidx.leanback.widget;

import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import androidx.leanback.widget.GuidedActionAdapter;
import androidx.leanback.widget.GuidedActionsStylist;
import java.util.ArrayList;

public class GuidedActionAdapterGroup {
    ArrayList<Pair<GuidedActionAdapter, GuidedActionAdapter>> mAdapters = new ArrayList<>();
    private GuidedActionAdapter.EditListener mEditListener;
    private boolean mImeOpened;

    public void addAdpter(GuidedActionAdapter guidedActionAdapter, GuidedActionAdapter guidedActionAdapter2) {
        this.mAdapters.add(new Pair<>(guidedActionAdapter, guidedActionAdapter2));
        if (guidedActionAdapter != null) {
            guidedActionAdapter.mGroup = this;
        }
        if (guidedActionAdapter2 != null) {
            guidedActionAdapter2.mGroup = this;
        }
    }

    public GuidedActionAdapter getNextAdapter(GuidedActionAdapter guidedActionAdapter) {
        for (int i = 0; i < this.mAdapters.size(); i++) {
            Pair<GuidedActionAdapter, GuidedActionAdapter> pair = this.mAdapters.get(i);
            if (pair.first == guidedActionAdapter) {
                return (GuidedActionAdapter) pair.second;
            }
        }
        return null;
    }

    public void setEditListener(GuidedActionAdapter.EditListener editListener) {
        this.mEditListener = editListener;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0027 A[LOOP:1: B:13:0x0027->B:16:0x0035, LOOP_START, PHI: r8 
      PHI: (r8v5 int) = (r8v1 int), (r8v6 int) binds: [B:8:0x0016, B:16:0x0035] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0063 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0064  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0018 A[LOOP:0: B:9:0x0018->B:12:0x0024, LOOP_START, PHI: r8 
      PHI: (r8v7 int) = (r8v1 int), (r8v8 int) binds: [B:8:0x0016, B:12:0x0024] A[DONT_GENERATE, DONT_INLINE]] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean focusToNextAction(androidx.leanback.widget.GuidedActionAdapter r7, androidx.leanback.widget.GuidedAction r8, long r9) {
        /*
        // Method dump skipped, instructions count: 107
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GuidedActionAdapterGroup.focusToNextAction(androidx.leanback.widget.GuidedActionAdapter, androidx.leanback.widget.GuidedAction, long):boolean");
    }

    public void openIme(GuidedActionAdapter guidedActionAdapter, GuidedActionsStylist.ViewHolder viewHolder) {
        guidedActionAdapter.getGuidedActionsStylist().setEditingMode(viewHolder, true);
        View editingView = viewHolder.getEditingView();
        if (editingView != null && viewHolder.isInEditingText()) {
            editingView.setFocusable(true);
            editingView.requestFocus();
            ((InputMethodManager) editingView.getContext().getSystemService("input_method")).showSoftInput(editingView, 0);
            if (!this.mImeOpened) {
                this.mImeOpened = true;
                this.mEditListener.onImeOpen();
            }
        }
    }

    public void closeIme(View view) {
        if (this.mImeOpened) {
            this.mImeOpened = false;
            ((InputMethodManager) view.getContext().getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
            this.mEditListener.onImeClose();
        }
    }

    public void fillAndStay(GuidedActionAdapter guidedActionAdapter, TextView textView) {
        GuidedActionsStylist.ViewHolder findSubChildViewHolder = guidedActionAdapter.findSubChildViewHolder(textView);
        updateTextIntoAction(findSubChildViewHolder, textView);
        this.mEditListener.onGuidedActionEditCanceled(findSubChildViewHolder.getAction());
        guidedActionAdapter.getGuidedActionsStylist().setEditingMode(findSubChildViewHolder, false);
        closeIme(textView);
        findSubChildViewHolder.itemView.requestFocus();
    }

    public void fillAndGoNext(GuidedActionAdapter guidedActionAdapter, TextView textView) {
        GuidedActionsStylist.ViewHolder findSubChildViewHolder = guidedActionAdapter.findSubChildViewHolder(textView);
        updateTextIntoAction(findSubChildViewHolder, textView);
        guidedActionAdapter.performOnActionClick(findSubChildViewHolder);
        long onGuidedActionEditedAndProceed = this.mEditListener.onGuidedActionEditedAndProceed(findSubChildViewHolder.getAction());
        boolean z = false;
        guidedActionAdapter.getGuidedActionsStylist().setEditingMode(findSubChildViewHolder, false);
        if (!(onGuidedActionEditedAndProceed == -3 || onGuidedActionEditedAndProceed == findSubChildViewHolder.getAction().getId())) {
            z = focusToNextAction(guidedActionAdapter, findSubChildViewHolder.getAction(), onGuidedActionEditedAndProceed);
        }
        if (!z) {
            closeIme(textView);
            findSubChildViewHolder.itemView.requestFocus();
        }
    }

    private void updateTextIntoAction(GuidedActionsStylist.ViewHolder viewHolder, TextView textView) {
        GuidedAction action = viewHolder.getAction();
        if (textView == viewHolder.getDescriptionView()) {
            if (action.getEditDescription() != null) {
                action.setEditDescription(textView.getText());
            } else {
                action.setDescription(textView.getText());
            }
        } else if (textView != viewHolder.getTitleView()) {
        } else {
            if (action.getEditTitle() != null) {
                action.setEditTitle(textView.getText());
            } else {
                action.setTitle(textView.getText());
            }
        }
    }
}
