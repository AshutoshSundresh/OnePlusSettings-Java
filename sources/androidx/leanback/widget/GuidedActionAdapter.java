package androidx.leanback.widget;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.TextView;
import androidx.leanback.widget.GuidedActionAutofillSupport;
import androidx.leanback.widget.GuidedActionsStylist;
import androidx.leanback.widget.ImeKeyMonitor;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class GuidedActionAdapter extends RecyclerView.Adapter {
    private final ActionAutofillListener mActionAutofillListener;
    private final ActionEditListener mActionEditListener;
    private final ActionOnFocusListener mActionOnFocusListener;
    private final ActionOnKeyListener mActionOnKeyListener;
    final List<GuidedAction> mActions;
    private ClickListener mClickListener;
    DiffCallback<GuidedAction> mDiffCallback;
    GuidedActionAdapterGroup mGroup;
    private final boolean mIsSubAdapter;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        /* class androidx.leanback.widget.GuidedActionAdapter.AnonymousClass1 */

        public void onClick(View view) {
            if (view != null && view.getWindowToken() != null && GuidedActionAdapter.this.getRecyclerView() != null) {
                GuidedActionsStylist.ViewHolder viewHolder = (GuidedActionsStylist.ViewHolder) GuidedActionAdapter.this.getRecyclerView().getChildViewHolder(view);
                GuidedAction action = viewHolder.getAction();
                if (action.hasTextEditable()) {
                    GuidedActionAdapter guidedActionAdapter = GuidedActionAdapter.this;
                    guidedActionAdapter.mGroup.openIme(guidedActionAdapter, viewHolder);
                } else if (action.hasEditableActivatorView()) {
                    GuidedActionAdapter.this.performOnActionClick(viewHolder);
                } else {
                    GuidedActionAdapter.this.handleCheckedActions(viewHolder);
                    if (action.isEnabled() && !action.infoOnly()) {
                        GuidedActionAdapter.this.performOnActionClick(viewHolder);
                    }
                }
            }
        }
    };
    final GuidedActionsStylist mStylist;

    public interface ClickListener {
        void onGuidedActionClicked(GuidedAction guidedAction);
    }

    public interface EditListener {
        void onGuidedActionEditCanceled(GuidedAction guidedAction);

        long onGuidedActionEditedAndProceed(GuidedAction guidedAction);

        void onImeClose();

        void onImeOpen();
    }

    public interface FocusListener {
        void onGuidedActionFocused(GuidedAction guidedAction);
    }

    public GuidedActionAdapter(List<GuidedAction> list, ClickListener clickListener, FocusListener focusListener, GuidedActionsStylist guidedActionsStylist, boolean z) {
        ArrayList arrayList;
        if (list == null) {
            arrayList = new ArrayList();
        } else {
            arrayList = new ArrayList(list);
        }
        this.mActions = arrayList;
        this.mClickListener = clickListener;
        this.mStylist = guidedActionsStylist;
        this.mActionOnKeyListener = new ActionOnKeyListener();
        this.mActionOnFocusListener = new ActionOnFocusListener(focusListener);
        this.mActionEditListener = new ActionEditListener();
        this.mActionAutofillListener = new ActionAutofillListener();
        this.mIsSubAdapter = z;
        if (!z) {
            this.mDiffCallback = GuidedActionDiffCallback.getInstance();
        }
    }

    public void setActions(List<GuidedAction> list) {
        if (!this.mIsSubAdapter) {
            this.mStylist.collapseAction(false);
        }
        this.mActionOnFocusListener.unFocus();
        if (this.mDiffCallback != null) {
            final ArrayList arrayList = new ArrayList();
            arrayList.addAll(this.mActions);
            this.mActions.clear();
            this.mActions.addAll(list);
            DiffUtil.calculateDiff(new DiffUtil.Callback() {
                /* class androidx.leanback.widget.GuidedActionAdapter.AnonymousClass2 */

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public int getOldListSize() {
                    return arrayList.size();
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public int getNewListSize() {
                    return GuidedActionAdapter.this.mActions.size();
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public boolean areItemsTheSame(int i, int i2) {
                    return GuidedActionAdapter.this.mDiffCallback.areItemsTheSame((GuidedAction) arrayList.get(i), GuidedActionAdapter.this.mActions.get(i2));
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public boolean areContentsTheSame(int i, int i2) {
                    return GuidedActionAdapter.this.mDiffCallback.areContentsTheSame((GuidedAction) arrayList.get(i), GuidedActionAdapter.this.mActions.get(i2));
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public Object getChangePayload(int i, int i2) {
                    return GuidedActionAdapter.this.mDiffCallback.getChangePayload((GuidedAction) arrayList.get(i), GuidedActionAdapter.this.mActions.get(i2));
                }
            }).dispatchUpdatesTo(this);
            return;
        }
        this.mActions.clear();
        this.mActions.addAll(list);
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mActions.size();
    }

    public GuidedAction getItem(int i) {
        return this.mActions.get(i);
    }

    public int indexOf(GuidedAction guidedAction) {
        return this.mActions.indexOf(guidedAction);
    }

    public GuidedActionsStylist getGuidedActionsStylist() {
        return this.mStylist;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return this.mStylist.getItemViewType(this.mActions.get(i));
    }

    /* access modifiers changed from: package-private */
    public RecyclerView getRecyclerView() {
        boolean z = this.mIsSubAdapter;
        GuidedActionsStylist guidedActionsStylist = this.mStylist;
        return z ? guidedActionsStylist.getSubActionsGridView() : guidedActionsStylist.getActionsGridView();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        GuidedActionsStylist.ViewHolder onCreateViewHolder = this.mStylist.onCreateViewHolder(viewGroup, i);
        View view = onCreateViewHolder.itemView;
        view.setOnKeyListener(this.mActionOnKeyListener);
        view.setOnClickListener(this.mOnClickListener);
        view.setOnFocusChangeListener(this.mActionOnFocusListener);
        setupListeners(onCreateViewHolder.getEditableTitleView());
        setupListeners(onCreateViewHolder.getEditableDescriptionView());
        return onCreateViewHolder;
    }

    private void setupListeners(EditText editText) {
        if (editText != null) {
            editText.setPrivateImeOptions("escapeNorth");
            editText.setOnEditorActionListener(this.mActionEditListener);
            if (editText instanceof ImeKeyMonitor) {
                ((ImeKeyMonitor) editText).setImeKeyListener(this.mActionEditListener);
            }
            if (editText instanceof GuidedActionAutofillSupport) {
                ((GuidedActionAutofillSupport) editText).setOnAutofillListener(this.mActionAutofillListener);
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (i < this.mActions.size()) {
            this.mStylist.onBindViewHolder((GuidedActionsStylist.ViewHolder) viewHolder, this.mActions.get(i));
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mActions.size();
    }

    private class ActionOnFocusListener implements View.OnFocusChangeListener {
        private FocusListener mFocusListener;
        private View mSelectedView;

        ActionOnFocusListener(FocusListener focusListener) {
            this.mFocusListener = focusListener;
        }

        public void unFocus() {
            if (this.mSelectedView != null && GuidedActionAdapter.this.getRecyclerView() != null) {
                RecyclerView.ViewHolder childViewHolder = GuidedActionAdapter.this.getRecyclerView().getChildViewHolder(this.mSelectedView);
                if (childViewHolder != null) {
                    GuidedActionAdapter.this.mStylist.onAnimateItemFocused((GuidedActionsStylist.ViewHolder) childViewHolder, false);
                    return;
                }
                Log.w("GuidedActionAdapter", "RecyclerView returned null view holder", new Throwable());
            }
        }

        public void onFocusChange(View view, boolean z) {
            if (GuidedActionAdapter.this.getRecyclerView() != null) {
                GuidedActionsStylist.ViewHolder viewHolder = (GuidedActionsStylist.ViewHolder) GuidedActionAdapter.this.getRecyclerView().getChildViewHolder(view);
                if (z) {
                    this.mSelectedView = view;
                    FocusListener focusListener = this.mFocusListener;
                    if (focusListener != null) {
                        focusListener.onGuidedActionFocused(viewHolder.getAction());
                    }
                } else if (this.mSelectedView == view) {
                    GuidedActionAdapter.this.mStylist.onAnimateItemPressedCancelled(viewHolder);
                    this.mSelectedView = null;
                }
                GuidedActionAdapter.this.mStylist.onAnimateItemFocused(viewHolder, z);
            }
        }
    }

    public GuidedActionsStylist.ViewHolder findSubChildViewHolder(View view) {
        if (getRecyclerView() == null) {
            return null;
        }
        ViewParent parent = view.getParent();
        while (parent != getRecyclerView() && parent != null && view != null) {
            view = (View) parent;
            parent = parent.getParent();
        }
        if (parent == null || view == null) {
            return null;
        }
        return (GuidedActionsStylist.ViewHolder) getRecyclerView().getChildViewHolder(view);
    }

    public void handleCheckedActions(GuidedActionsStylist.ViewHolder viewHolder) {
        GuidedAction action = viewHolder.getAction();
        int checkSetId = action.getCheckSetId();
        if (getRecyclerView() != null && checkSetId != 0) {
            if (checkSetId != -1) {
                int size = this.mActions.size();
                for (int i = 0; i < size; i++) {
                    GuidedAction guidedAction = this.mActions.get(i);
                    if (guidedAction != action && guidedAction.getCheckSetId() == checkSetId && guidedAction.isChecked()) {
                        guidedAction.setChecked(false);
                        GuidedActionsStylist.ViewHolder viewHolder2 = (GuidedActionsStylist.ViewHolder) getRecyclerView().findViewHolderForPosition(i);
                        if (viewHolder2 != null) {
                            this.mStylist.onAnimateItemChecked(viewHolder2, false);
                        }
                    }
                }
            }
            if (!action.isChecked()) {
                action.setChecked(true);
                this.mStylist.onAnimateItemChecked(viewHolder, true);
            } else if (checkSetId == -1) {
                action.setChecked(false);
                this.mStylist.onAnimateItemChecked(viewHolder, false);
            }
        }
    }

    public void performOnActionClick(GuidedActionsStylist.ViewHolder viewHolder) {
        ClickListener clickListener = this.mClickListener;
        if (clickListener != null) {
            clickListener.onGuidedActionClicked(viewHolder.getAction());
        }
    }

    private class ActionOnKeyListener implements View.OnKeyListener {
        private boolean mKeyPressed = false;

        ActionOnKeyListener() {
        }

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (view == null || keyEvent == null || GuidedActionAdapter.this.getRecyclerView() == null) {
                return false;
            }
            if (i == 23 || i == 66 || i == 160 || i == 99 || i == 100) {
                GuidedActionsStylist.ViewHolder viewHolder = (GuidedActionsStylist.ViewHolder) GuidedActionAdapter.this.getRecyclerView().getChildViewHolder(view);
                GuidedAction action = viewHolder.getAction();
                if (!action.isEnabled() || action.infoOnly()) {
                    keyEvent.getAction();
                    return true;
                }
                int action2 = keyEvent.getAction();
                if (action2 != 0) {
                    if (action2 == 1 && this.mKeyPressed) {
                        this.mKeyPressed = false;
                        GuidedActionAdapter.this.mStylist.onAnimateItemPressed(viewHolder, false);
                    }
                } else if (!this.mKeyPressed) {
                    this.mKeyPressed = true;
                    GuidedActionAdapter.this.mStylist.onAnimateItemPressed(viewHolder, true);
                }
            }
            return false;
        }
    }

    /* access modifiers changed from: private */
    public class ActionEditListener implements TextView.OnEditorActionListener, ImeKeyMonitor.ImeKeyListener {
        ActionEditListener() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5 || i == 6) {
                GuidedActionAdapter guidedActionAdapter = GuidedActionAdapter.this;
                guidedActionAdapter.mGroup.fillAndGoNext(guidedActionAdapter, textView);
                return true;
            } else if (i != 1) {
                return false;
            } else {
                GuidedActionAdapter guidedActionAdapter2 = GuidedActionAdapter.this;
                guidedActionAdapter2.mGroup.fillAndStay(guidedActionAdapter2, textView);
                return true;
            }
        }

        @Override // androidx.leanback.widget.ImeKeyMonitor.ImeKeyListener
        public boolean onKeyPreIme(EditText editText, int i, KeyEvent keyEvent) {
            if (i == 4 && keyEvent.getAction() == 1) {
                GuidedActionAdapter guidedActionAdapter = GuidedActionAdapter.this;
                guidedActionAdapter.mGroup.fillAndStay(guidedActionAdapter, editText);
                return true;
            } else if (i != 66 || keyEvent.getAction() != 1) {
                return false;
            } else {
                GuidedActionAdapter guidedActionAdapter2 = GuidedActionAdapter.this;
                guidedActionAdapter2.mGroup.fillAndGoNext(guidedActionAdapter2, editText);
                return true;
            }
        }
    }

    /* access modifiers changed from: private */
    public class ActionAutofillListener implements GuidedActionAutofillSupport.OnAutofillListener {
        ActionAutofillListener() {
        }

        @Override // androidx.leanback.widget.GuidedActionAutofillSupport.OnAutofillListener
        public void onAutofill(View view) {
            GuidedActionAdapter guidedActionAdapter = GuidedActionAdapter.this;
            guidedActionAdapter.mGroup.fillAndGoNext(guidedActionAdapter, (EditText) view);
        }
    }
}
