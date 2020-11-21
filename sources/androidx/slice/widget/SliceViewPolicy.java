package androidx.slice.widget;

public class SliceViewPolicy {
    private PolicyChangeListener mListener;
    private int mMaxHeight = 0;
    private int mMaxSmallHeight = 0;
    private int mMode = 2;
    private boolean mScrollable = true;

    public interface PolicyChangeListener {
        void onMaxHeightChanged(int i);

        void onMaxSmallChanged(int i);

        void onModeChanged(int i);

        void onScrollingChanged(boolean z);
    }

    public void setListener(PolicyChangeListener policyChangeListener) {
        this.mListener = policyChangeListener;
    }

    public int getMaxHeight() {
        return this.mMaxHeight;
    }

    public int getMaxSmallHeight() {
        return this.mMaxSmallHeight;
    }

    public boolean isScrollable() {
        return this.mScrollable;
    }

    public int getMode() {
        return this.mMode;
    }

    public void setMaxHeight(int i) {
        if (i != this.mMaxHeight) {
            this.mMaxHeight = i;
            PolicyChangeListener policyChangeListener = this.mListener;
            if (policyChangeListener != null) {
                policyChangeListener.onMaxHeightChanged(i);
            }
        }
    }

    public void setMaxSmallHeight(int i) {
        if (this.mMaxSmallHeight != i) {
            this.mMaxSmallHeight = i;
            PolicyChangeListener policyChangeListener = this.mListener;
            if (policyChangeListener != null) {
                policyChangeListener.onMaxSmallChanged(i);
            }
        }
    }

    public void setScrollable(boolean z) {
        if (z != this.mScrollable) {
            this.mScrollable = z;
            PolicyChangeListener policyChangeListener = this.mListener;
            if (policyChangeListener != null) {
                policyChangeListener.onScrollingChanged(z);
            }
        }
    }

    public void setMode(int i) {
        if (this.mMode != i) {
            this.mMode = i;
            PolicyChangeListener policyChangeListener = this.mListener;
            if (policyChangeListener != null) {
                policyChangeListener.onModeChanged(i);
            }
        }
    }
}
