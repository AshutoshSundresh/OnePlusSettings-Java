package androidx.leanback.widget;

/* access modifiers changed from: package-private */
public class WindowAlignment {
    public final Axis horizontal;
    private Axis mMainAxis;
    private int mOrientation = 0;
    private Axis mSecondAxis;
    public final Axis vertical = new Axis("vertical");

    WindowAlignment() {
        Axis axis = new Axis("horizontal");
        this.horizontal = axis;
        this.mMainAxis = axis;
        this.mSecondAxis = this.vertical;
    }

    public static class Axis {
        private int mMaxEdge;
        private int mMaxScroll;
        private int mMinEdge;
        private int mMinScroll;
        private int mPaddingMax;
        private int mPaddingMin;
        private int mPreferredKeyLine = 2;
        private boolean mReversedFlow;
        private int mSize;
        private int mWindowAlignment = 3;
        private int mWindowAlignmentOffset = 0;
        private float mWindowAlignmentOffsetPercent = 50.0f;

        /* access modifiers changed from: package-private */
        public final int calculateScrollToKeyLine(int i, int i2) {
            return i - i2;
        }

        public Axis(String str) {
            reset();
        }

        public final int getWindowAlignment() {
            return this.mWindowAlignment;
        }

        public final void setWindowAlignment(int i) {
            this.mWindowAlignment = i;
        }

        /* access modifiers changed from: package-private */
        public final void setPreferKeylineOverLowEdge(boolean z) {
            int i;
            if (z) {
                i = this.mPreferredKeyLine | 1;
            } else {
                i = this.mPreferredKeyLine & -2;
            }
            this.mPreferredKeyLine = i;
        }

        /* access modifiers changed from: package-private */
        public final void setPreferKeylineOverHighEdge(boolean z) {
            int i;
            if (z) {
                i = this.mPreferredKeyLine | 2;
            } else {
                i = this.mPreferredKeyLine & -3;
            }
            this.mPreferredKeyLine = i;
        }

        /* access modifiers changed from: package-private */
        public final boolean isPreferKeylineOverHighEdge() {
            return (this.mPreferredKeyLine & 2) != 0;
        }

        /* access modifiers changed from: package-private */
        public final boolean isPreferKeylineOverLowEdge() {
            return (this.mPreferredKeyLine & 1) != 0;
        }

        public final int getWindowAlignmentOffset() {
            return this.mWindowAlignmentOffset;
        }

        public final void setWindowAlignmentOffset(int i) {
            this.mWindowAlignmentOffset = i;
        }

        public final void setWindowAlignmentOffsetPercent(float f) {
            if ((f < 0.0f || f > 100.0f) && f != -1.0f) {
                throw new IllegalArgumentException();
            }
            this.mWindowAlignmentOffsetPercent = f;
        }

        public final float getWindowAlignmentOffsetPercent() {
            return this.mWindowAlignmentOffsetPercent;
        }

        public final int getMinScroll() {
            return this.mMinScroll;
        }

        public final void invalidateScrollMin() {
            this.mMinEdge = Integer.MIN_VALUE;
            this.mMinScroll = Integer.MIN_VALUE;
        }

        public final int getMaxScroll() {
            return this.mMaxScroll;
        }

        public final void invalidateScrollMax() {
            this.mMaxEdge = Integer.MAX_VALUE;
            this.mMaxScroll = Integer.MAX_VALUE;
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.mMinEdge = Integer.MIN_VALUE;
            this.mMaxEdge = Integer.MAX_VALUE;
        }

        public final boolean isMinUnknown() {
            return this.mMinEdge == Integer.MIN_VALUE;
        }

        public final boolean isMaxUnknown() {
            return this.mMaxEdge == Integer.MAX_VALUE;
        }

        public final void setSize(int i) {
            this.mSize = i;
        }

        public final int getSize() {
            return this.mSize;
        }

        public final void setPadding(int i, int i2) {
            this.mPaddingMin = i;
            this.mPaddingMax = i2;
        }

        public final int getPaddingMin() {
            return this.mPaddingMin;
        }

        public final int getPaddingMax() {
            return this.mPaddingMax;
        }

        public final int getClientSize() {
            return (this.mSize - this.mPaddingMin) - this.mPaddingMax;
        }

        /* access modifiers changed from: package-private */
        public final int calculateKeyline() {
            if (!this.mReversedFlow) {
                int i = this.mWindowAlignmentOffset;
                if (i < 0) {
                    i += this.mSize;
                }
                float f = this.mWindowAlignmentOffsetPercent;
                if (f != -1.0f) {
                    return i + ((int) ((((float) this.mSize) * f) / 100.0f));
                }
                return i;
            }
            int i2 = this.mWindowAlignmentOffset;
            int i3 = i2 >= 0 ? this.mSize - i2 : -i2;
            float f2 = this.mWindowAlignmentOffsetPercent;
            return f2 != -1.0f ? i3 - ((int) ((((float) this.mSize) * f2) / 100.0f)) : i3;
        }

        public final void updateMinMax(int i, int i2, int i3, int i4) {
            this.mMinEdge = i;
            this.mMaxEdge = i2;
            int clientSize = getClientSize();
            int calculateKeyline = calculateKeyline();
            boolean isMinUnknown = isMinUnknown();
            boolean isMaxUnknown = isMaxUnknown();
            if (!isMinUnknown) {
                if (!this.mReversedFlow) {
                    this.mMinScroll = calculateScrollToKeyLine(i3, calculateKeyline);
                } else {
                    this.mMinScroll = calculateScrollToKeyLine(i3, calculateKeyline);
                }
                this.mMinScroll = this.mMinEdge - this.mPaddingMin;
            }
            if (!isMaxUnknown) {
                if (!this.mReversedFlow) {
                    this.mMaxScroll = calculateScrollToKeyLine(i4, calculateKeyline);
                } else {
                    this.mMaxScroll = calculateScrollToKeyLine(i4, calculateKeyline);
                }
                this.mMaxScroll = (this.mMaxEdge - this.mPaddingMin) - clientSize;
            }
            if (!isMaxUnknown && !isMinUnknown) {
                if (!this.mReversedFlow) {
                    int i5 = this.mWindowAlignment;
                    if ((i5 & 1) != 0) {
                        if (isPreferKeylineOverLowEdge()) {
                            this.mMinScroll = Math.min(this.mMinScroll, calculateScrollToKeyLine(i4, calculateKeyline));
                        }
                        this.mMaxScroll = Math.max(this.mMinScroll, this.mMaxScroll);
                    } else if ((i5 & 2) != 0) {
                        if (isPreferKeylineOverHighEdge()) {
                            this.mMaxScroll = Math.max(this.mMaxScroll, calculateScrollToKeyLine(i3, calculateKeyline));
                        }
                        this.mMinScroll = Math.min(this.mMinScroll, this.mMaxScroll);
                    }
                } else {
                    int i6 = this.mWindowAlignment;
                    if ((i6 & 1) != 0) {
                        if (isPreferKeylineOverLowEdge()) {
                            this.mMaxScroll = Math.max(this.mMaxScroll, calculateScrollToKeyLine(i3, calculateKeyline));
                        }
                        this.mMinScroll = Math.min(this.mMinScroll, this.mMaxScroll);
                    } else if ((i6 & 2) != 0) {
                        if (isPreferKeylineOverHighEdge()) {
                            this.mMinScroll = Math.min(this.mMinScroll, calculateScrollToKeyLine(i4, calculateKeyline));
                        }
                        this.mMaxScroll = Math.max(this.mMinScroll, this.mMaxScroll);
                    }
                }
            }
        }

        public final int getScroll(int i) {
            int i2;
            int i3;
            int size = getSize();
            int calculateKeyline = calculateKeyline();
            boolean isMinUnknown = isMinUnknown();
            boolean isMaxUnknown = isMaxUnknown();
            if (!isMinUnknown) {
                int i4 = calculateKeyline - this.mPaddingMin;
                if (this.mReversedFlow ? (this.mWindowAlignment & 2) != 0 : (this.mWindowAlignment & 1) != 0) {
                    int i5 = this.mMinEdge;
                    if (i - i5 <= i4) {
                        int i6 = i5 - this.mPaddingMin;
                        return (isMaxUnknown || i6 <= (i3 = this.mMaxScroll)) ? i6 : i3;
                    }
                }
            }
            if (!isMaxUnknown) {
                int i7 = (size - calculateKeyline) - this.mPaddingMax;
                if (this.mReversedFlow ? (this.mWindowAlignment & 1) != 0 : (this.mWindowAlignment & 2) != 0) {
                    int i8 = this.mMaxEdge;
                    if (i8 - i <= i7) {
                        int i9 = i8 - (size - this.mPaddingMax);
                        return (isMinUnknown || i9 >= (i2 = this.mMinScroll)) ? i9 : i2;
                    }
                }
            }
            return calculateScrollToKeyLine(i, calculateKeyline);
        }

        public final void setReversedFlow(boolean z) {
            this.mReversedFlow = z;
        }

        public String toString() {
            return " min:" + this.mMinEdge + " " + this.mMinScroll + " max:" + this.mMaxEdge + " " + this.mMaxScroll;
        }
    }

    public final Axis mainAxis() {
        return this.mMainAxis;
    }

    public final Axis secondAxis() {
        return this.mSecondAxis;
    }

    public final void setOrientation(int i) {
        this.mOrientation = i;
        if (i == 0) {
            this.mMainAxis = this.horizontal;
            this.mSecondAxis = this.vertical;
            return;
        }
        this.mMainAxis = this.vertical;
        this.mSecondAxis = this.horizontal;
    }

    public final void reset() {
        mainAxis().reset();
    }

    public String toString() {
        return "horizontal=" + this.horizontal + "; vertical=" + this.vertical;
    }
}
