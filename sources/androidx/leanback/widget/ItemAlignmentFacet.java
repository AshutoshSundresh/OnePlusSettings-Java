package androidx.leanback.widget;

public final class ItemAlignmentFacet {
    private ItemAlignmentDef[] mAlignmentDefs = {new ItemAlignmentDef()};

    public static class ItemAlignmentDef {
        private boolean mAlignToBaseline;
        int mFocusViewId = -1;
        int mOffset = 0;
        float mOffsetPercent = 50.0f;
        boolean mOffsetWithPadding = false;
        int mViewId = -1;

        public final void setItemAlignmentOffset(int i) {
            this.mOffset = i;
        }

        public final int getItemAlignmentOffset() {
            return this.mOffset;
        }

        public final void setItemAlignmentOffsetWithPadding(boolean z) {
            this.mOffsetWithPadding = z;
        }

        public final void setItemAlignmentOffsetPercent(float f) {
            if ((f < 0.0f || f > 100.0f) && f != -1.0f) {
                throw new IllegalArgumentException();
            }
            this.mOffsetPercent = f;
        }

        public final float getItemAlignmentOffsetPercent() {
            return this.mOffsetPercent;
        }

        public final void setItemAlignmentViewId(int i) {
            this.mViewId = i;
        }

        public final int getItemAlignmentViewId() {
            return this.mViewId;
        }

        public final int getItemAlignmentFocusViewId() {
            int i = this.mFocusViewId;
            return i != -1 ? i : this.mViewId;
        }

        public final void setAlignedToTextViewBaseline(boolean z) {
            this.mAlignToBaseline = z;
        }

        public boolean isAlignedToTextViewBaseLine() {
            return this.mAlignToBaseline;
        }
    }

    public void setAlignmentDefs(ItemAlignmentDef[] itemAlignmentDefArr) {
        if (itemAlignmentDefArr == null || itemAlignmentDefArr.length < 1) {
            throw new IllegalArgumentException();
        }
        this.mAlignmentDefs = itemAlignmentDefArr;
    }

    public ItemAlignmentDef[] getAlignmentDefs() {
        return this.mAlignmentDefs;
    }
}
