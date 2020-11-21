package com.oneplus.settings.opfinger;

import android.content.Context;
import android.util.AttributeSet;
import com.airbnb.lottie.LottieAnimationView;

public class OPLottieAnimationView extends LottieAnimationView {
    private int mCurrenProgress;
    private boolean mFillCompleted;
    private int mSplitSteps;

    public OPLottieAnimationView(Context context) {
        super(context);
    }

    public OPLottieAnimationView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public OPLottieAnimationView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public int getSplitSteps() {
        return this.mSplitSteps;
    }

    public void setSplitSteps(int i) {
        this.mSplitSteps = i;
    }

    public int getCurrenProgress() {
        return this.mCurrenProgress;
    }

    public void setCurrenProgress(int i) {
        this.mCurrenProgress = i;
    }

    public boolean isFillCompleted() {
        return this.mFillCompleted;
    }

    public void setFillCompleted(boolean z) {
        this.mFillCompleted = z;
    }
}
