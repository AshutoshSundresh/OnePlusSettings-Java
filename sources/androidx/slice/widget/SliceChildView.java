package androidx.slice.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceAction;
import androidx.slice.widget.SliceActionView;
import androidx.slice.widget.SliceView;
import java.util.List;
import java.util.Set;

public abstract class SliceChildView extends FrameLayout {
    protected int mInsetBottom;
    protected int mInsetEnd;
    protected int mInsetStart;
    protected int mInsetTop;
    protected long mLastUpdated;
    protected SliceActionView.SliceActionLoadingListener mLoadingListener;
    protected SliceView.OnSliceActionListener mObserver;
    protected boolean mShowLastUpdated;
    protected SliceStyle mSliceStyle;
    protected int mTintColor;
    protected SliceViewPolicy mViewPolicy;

    public Set<SliceItem> getLoadingActions() {
        return null;
    }

    public abstract void resetView();

    public void setActionLoading(SliceItem sliceItem) {
    }

    public void setAllowTwoLines(boolean z) {
    }

    public void setLoadingActions(Set<SliceItem> set) {
    }

    public void setSliceActions(List<SliceAction> list) {
    }

    public void setSliceContent(ListContent listContent) {
    }

    public void setSliceItem(SliceContent sliceContent, boolean z, int i, int i2, SliceView.OnSliceActionListener onSliceActionListener) {
    }

    public SliceChildView(Context context) {
        super(context);
        this.mTintColor = -1;
        this.mLastUpdated = -1;
    }

    public SliceChildView(Context context, AttributeSet attributeSet) {
        this(context);
    }

    public void setInsets(int i, int i2, int i3, int i4) {
        this.mInsetStart = i;
        this.mInsetTop = i2;
        this.mInsetEnd = i3;
        this.mInsetBottom = i4;
    }

    public int getMode() {
        SliceViewPolicy sliceViewPolicy = this.mViewPolicy;
        if (sliceViewPolicy != null) {
            return sliceViewPolicy.getMode();
        }
        return 2;
    }

    public void setTint(int i) {
        this.mTintColor = i;
    }

    public void setShowLastUpdated(boolean z) {
        this.mShowLastUpdated = z;
    }

    public void setLastUpdated(long j) {
        this.mLastUpdated = j;
    }

    public void setSliceActionListener(SliceView.OnSliceActionListener onSliceActionListener) {
        this.mObserver = onSliceActionListener;
    }

    public void setSliceActionLoadingListener(SliceActionView.SliceActionLoadingListener sliceActionLoadingListener) {
        this.mLoadingListener = sliceActionLoadingListener;
    }

    public void setStyle(SliceStyle sliceStyle) {
        this.mSliceStyle = sliceStyle;
    }

    public void setPolicy(SliceViewPolicy sliceViewPolicy) {
        this.mViewPolicy = sliceViewPolicy;
    }
}
