package androidx.leanback.app;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.Bundle;
import android.util.Property;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.leanback.R$animator;
import androidx.leanback.R$attr;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;
import androidx.leanback.widget.PagingIndicator;
import java.util.ArrayList;

public abstract class OnboardingSupportFragment extends Fragment {
    private static final TimeInterpolator HEADER_APPEAR_INTERPOLATOR = new DecelerateInterpolator();
    private static final TimeInterpolator HEADER_DISAPPEAR_INTERPOLATOR = new AccelerateInterpolator();
    private static int sSlideDistance;
    private AnimatorSet mAnimator;
    private int mArrowBackgroundColor = 0;
    private boolean mArrowBackgroundColorSet;
    private int mArrowColor = 0;
    private boolean mArrowColorSet;
    int mCurrentPageIndex;
    TextView mDescriptionView;
    private int mDescriptionViewTextColor = 0;
    private boolean mDescriptionViewTextColorSet;
    private int mDotBackgroundColor = 0;
    private boolean mDotBackgroundColorSet;
    boolean mEnterAnimationFinished;
    private int mIconResourceId;
    boolean mIsLtr;
    boolean mLogoAnimationFinished;
    private int mLogoResourceId;
    private ImageView mLogoView;
    private ImageView mMainIconView;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        /* class androidx.leanback.app.OnboardingSupportFragment.AnonymousClass1 */

        public void onClick(View view) {
            OnboardingSupportFragment onboardingSupportFragment = OnboardingSupportFragment.this;
            if (onboardingSupportFragment.mLogoAnimationFinished) {
                if (onboardingSupportFragment.mCurrentPageIndex == onboardingSupportFragment.getPageCount() - 1) {
                    OnboardingSupportFragment.this.onFinishFragment();
                } else {
                    OnboardingSupportFragment.this.moveToNextPage();
                }
            }
        }
    };
    private final View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {
        /* class androidx.leanback.app.OnboardingSupportFragment.AnonymousClass2 */

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (!OnboardingSupportFragment.this.mLogoAnimationFinished) {
                return i != 4;
            }
            if (keyEvent.getAction() == 0) {
                return false;
            }
            if (i == 4) {
                OnboardingSupportFragment onboardingSupportFragment = OnboardingSupportFragment.this;
                if (onboardingSupportFragment.mCurrentPageIndex == 0) {
                    return false;
                }
                onboardingSupportFragment.moveToPreviousPage();
                return true;
            } else if (i == 21) {
                OnboardingSupportFragment onboardingSupportFragment2 = OnboardingSupportFragment.this;
                if (onboardingSupportFragment2.mIsLtr) {
                    onboardingSupportFragment2.moveToPreviousPage();
                } else {
                    onboardingSupportFragment2.moveToNextPage();
                }
                return true;
            } else if (i != 22) {
                return false;
            } else {
                OnboardingSupportFragment onboardingSupportFragment3 = OnboardingSupportFragment.this;
                if (onboardingSupportFragment3.mIsLtr) {
                    onboardingSupportFragment3.moveToNextPage();
                } else {
                    onboardingSupportFragment3.moveToPreviousPage();
                }
                return true;
            }
        }
    };
    PagingIndicator mPageIndicator;
    View mStartButton;
    private CharSequence mStartButtonText;
    private boolean mStartButtonTextSet;
    private ContextThemeWrapper mThemeWrapper;
    TextView mTitleView;
    private int mTitleViewTextColor = 0;
    private boolean mTitleViewTextColorSet;

    /* access modifiers changed from: protected */
    public abstract int getPageCount();

    /* access modifiers changed from: protected */
    public abstract CharSequence getPageDescription(int i);

    /* access modifiers changed from: protected */
    public abstract CharSequence getPageTitle(int i);

    /* access modifiers changed from: protected */
    public abstract View onCreateBackgroundView(LayoutInflater layoutInflater, ViewGroup viewGroup);

    /* access modifiers changed from: protected */
    public abstract View onCreateContentView(LayoutInflater layoutInflater, ViewGroup viewGroup);

    /* access modifiers changed from: protected */
    public Animator onCreateEnterAnimation() {
        return null;
    }

    /* access modifiers changed from: protected */
    public abstract View onCreateForegroundView(LayoutInflater layoutInflater, ViewGroup viewGroup);

    /* access modifiers changed from: protected */
    public Animator onCreateLogoAnimation() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void onFinishFragment() {
    }

    /* access modifiers changed from: protected */
    public void onPageChanged(int i, int i2) {
    }

    public int onProvideTheme() {
        return -1;
    }

    /* access modifiers changed from: protected */
    public void moveToPreviousPage() {
        int i;
        if (this.mLogoAnimationFinished && (i = this.mCurrentPageIndex) > 0) {
            int i2 = i - 1;
            this.mCurrentPageIndex = i2;
            onPageChangedInternal(i2 + 1);
        }
    }

    /* access modifiers changed from: protected */
    public void moveToNextPage() {
        if (this.mLogoAnimationFinished && this.mCurrentPageIndex < getPageCount() - 1) {
            int i = this.mCurrentPageIndex + 1;
            this.mCurrentPageIndex = i;
            onPageChangedInternal(i - 1);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        resolveTheme();
        boolean z = false;
        ViewGroup viewGroup2 = (ViewGroup) getThemeInflater(layoutInflater).inflate(R$layout.lb_onboarding_fragment, viewGroup, false);
        if (getResources().getConfiguration().getLayoutDirection() == 0) {
            z = true;
        }
        this.mIsLtr = z;
        PagingIndicator pagingIndicator = (PagingIndicator) viewGroup2.findViewById(R$id.page_indicator);
        this.mPageIndicator = pagingIndicator;
        pagingIndicator.setOnClickListener(this.mOnClickListener);
        this.mPageIndicator.setOnKeyListener(this.mOnKeyListener);
        View findViewById = viewGroup2.findViewById(R$id.button_start);
        this.mStartButton = findViewById;
        findViewById.setOnClickListener(this.mOnClickListener);
        this.mStartButton.setOnKeyListener(this.mOnKeyListener);
        this.mMainIconView = (ImageView) viewGroup2.findViewById(R$id.main_icon);
        this.mLogoView = (ImageView) viewGroup2.findViewById(R$id.logo);
        this.mTitleView = (TextView) viewGroup2.findViewById(R$id.title);
        this.mDescriptionView = (TextView) viewGroup2.findViewById(R$id.description);
        if (this.mTitleViewTextColorSet) {
            this.mTitleView.setTextColor(this.mTitleViewTextColor);
        }
        if (this.mDescriptionViewTextColorSet) {
            this.mDescriptionView.setTextColor(this.mDescriptionViewTextColor);
        }
        if (this.mDotBackgroundColorSet) {
            this.mPageIndicator.setDotBackgroundColor(this.mDotBackgroundColor);
        }
        if (this.mArrowColorSet) {
            this.mPageIndicator.setArrowColor(this.mArrowColor);
        }
        if (this.mArrowBackgroundColorSet) {
            this.mPageIndicator.setDotBackgroundColor(this.mArrowBackgroundColor);
        }
        if (this.mStartButtonTextSet) {
            ((Button) this.mStartButton).setText(this.mStartButtonText);
        }
        Context context = getContext();
        if (sSlideDistance == 0) {
            sSlideDistance = (int) (context.getResources().getDisplayMetrics().scaledDensity * 60.0f);
        }
        viewGroup2.requestFocus();
        return viewGroup2;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (bundle == null) {
            this.mCurrentPageIndex = 0;
            this.mLogoAnimationFinished = false;
            this.mEnterAnimationFinished = false;
            this.mPageIndicator.onPageSelected(0, false);
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                /* class androidx.leanback.app.OnboardingSupportFragment.AnonymousClass3 */

                public boolean onPreDraw() {
                    OnboardingSupportFragment.this.getView().getViewTreeObserver().removeOnPreDrawListener(this);
                    if (!OnboardingSupportFragment.this.startLogoAnimation()) {
                        OnboardingSupportFragment onboardingSupportFragment = OnboardingSupportFragment.this;
                        onboardingSupportFragment.mLogoAnimationFinished = true;
                        onboardingSupportFragment.onLogoAnimationFinished();
                    }
                    return true;
                }
            });
            return;
        }
        this.mCurrentPageIndex = bundle.getInt("leanback.onboarding.current_page_index");
        this.mLogoAnimationFinished = bundle.getBoolean("leanback.onboarding.logo_animation_finished");
        this.mEnterAnimationFinished = bundle.getBoolean("leanback.onboarding.enter_animation_finished");
        if (this.mLogoAnimationFinished) {
            onLogoAnimationFinished();
        } else if (!startLogoAnimation()) {
            this.mLogoAnimationFinished = true;
            onLogoAnimationFinished();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("leanback.onboarding.current_page_index", this.mCurrentPageIndex);
        bundle.putBoolean("leanback.onboarding.logo_animation_finished", this.mLogoAnimationFinished);
        bundle.putBoolean("leanback.onboarding.enter_animation_finished", this.mEnterAnimationFinished);
    }

    private void resolveTheme() {
        Context context = getContext();
        int onProvideTheme = onProvideTheme();
        if (onProvideTheme == -1) {
            int i = R$attr.onboardingTheme;
            TypedValue typedValue = new TypedValue();
            if (context.getTheme().resolveAttribute(i, typedValue, true)) {
                this.mThemeWrapper = new ContextThemeWrapper(context, typedValue.resourceId);
                return;
            }
            return;
        }
        this.mThemeWrapper = new ContextThemeWrapper(context, onProvideTheme);
    }

    private LayoutInflater getThemeInflater(LayoutInflater layoutInflater) {
        ContextThemeWrapper contextThemeWrapper = this.mThemeWrapper;
        return contextThemeWrapper == null ? layoutInflater : layoutInflater.cloneInContext(contextThemeWrapper);
    }

    /* access modifiers changed from: package-private */
    public boolean startLogoAnimation() {
        AnimatorSet animatorSet;
        final Context context = getContext();
        if (context == null) {
            return false;
        }
        if (this.mLogoResourceId != 0) {
            this.mLogoView.setVisibility(0);
            this.mLogoView.setImageResource(this.mLogoResourceId);
            Animator loadAnimator = AnimatorInflater.loadAnimator(context, R$animator.lb_onboarding_logo_enter);
            Animator loadAnimator2 = AnimatorInflater.loadAnimator(context, R$animator.lb_onboarding_logo_exit);
            loadAnimator2.setStartDelay(1333);
            AnimatorSet animatorSet2 = new AnimatorSet();
            animatorSet2.playSequentially(loadAnimator, loadAnimator2);
            animatorSet2.setTarget(this.mLogoView);
            animatorSet = animatorSet2;
        } else {
            animatorSet = onCreateLogoAnimation();
        }
        if (animatorSet == null) {
            return false;
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            /* class androidx.leanback.app.OnboardingSupportFragment.AnonymousClass4 */

            public void onAnimationEnd(Animator animator) {
                if (context != null) {
                    OnboardingSupportFragment onboardingSupportFragment = OnboardingSupportFragment.this;
                    onboardingSupportFragment.mLogoAnimationFinished = true;
                    onboardingSupportFragment.onLogoAnimationFinished();
                }
            }
        });
        animatorSet.start();
        return true;
    }

    /* access modifiers changed from: package-private */
    public void hideLogoView() {
        this.mLogoView.setVisibility(8);
        int i = this.mIconResourceId;
        if (i != 0) {
            this.mMainIconView.setImageResource(i);
            this.mMainIconView.setVisibility(0);
        }
        View view = getView();
        LayoutInflater themeInflater = getThemeInflater(LayoutInflater.from(getContext()));
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R$id.background_container);
        View onCreateBackgroundView = onCreateBackgroundView(themeInflater, viewGroup);
        if (onCreateBackgroundView != null) {
            viewGroup.setVisibility(0);
            viewGroup.addView(onCreateBackgroundView);
        }
        ViewGroup viewGroup2 = (ViewGroup) view.findViewById(R$id.content_container);
        View onCreateContentView = onCreateContentView(themeInflater, viewGroup2);
        if (onCreateContentView != null) {
            viewGroup2.setVisibility(0);
            viewGroup2.addView(onCreateContentView);
        }
        ViewGroup viewGroup3 = (ViewGroup) view.findViewById(R$id.foreground_container);
        View onCreateForegroundView = onCreateForegroundView(themeInflater, viewGroup3);
        if (onCreateForegroundView != null) {
            viewGroup3.setVisibility(0);
            viewGroup3.addView(onCreateForegroundView);
        }
        view.findViewById(R$id.page_container).setVisibility(0);
        view.findViewById(R$id.content_container).setVisibility(0);
        if (getPageCount() > 1) {
            this.mPageIndicator.setPageCount(getPageCount());
            this.mPageIndicator.onPageSelected(this.mCurrentPageIndex, false);
        }
        if (this.mCurrentPageIndex == getPageCount() - 1) {
            this.mStartButton.setVisibility(0);
        } else {
            this.mPageIndicator.setVisibility(0);
        }
        this.mTitleView.setText(getPageTitle(this.mCurrentPageIndex));
        this.mDescriptionView.setText(getPageDescription(this.mCurrentPageIndex));
    }

    /* access modifiers changed from: protected */
    public void onLogoAnimationFinished() {
        startEnterAnimation(false);
    }

    /* access modifiers changed from: protected */
    public final void startEnterAnimation(boolean z) {
        Context context = getContext();
        if (context != null) {
            hideLogoView();
            if (!this.mEnterAnimationFinished || z) {
                ArrayList arrayList = new ArrayList();
                Animator loadAnimator = AnimatorInflater.loadAnimator(context, R$animator.lb_onboarding_page_indicator_enter);
                loadAnimator.setTarget(getPageCount() <= 1 ? this.mStartButton : this.mPageIndicator);
                arrayList.add(loadAnimator);
                Animator onCreateTitleAnimator = onCreateTitleAnimator();
                if (onCreateTitleAnimator != null) {
                    onCreateTitleAnimator.setTarget(this.mTitleView);
                    arrayList.add(onCreateTitleAnimator);
                }
                Animator onCreateDescriptionAnimator = onCreateDescriptionAnimator();
                if (onCreateDescriptionAnimator != null) {
                    onCreateDescriptionAnimator.setTarget(this.mDescriptionView);
                    arrayList.add(onCreateDescriptionAnimator);
                }
                Animator onCreateEnterAnimation = onCreateEnterAnimation();
                if (onCreateEnterAnimation != null) {
                    arrayList.add(onCreateEnterAnimation);
                }
                if (!arrayList.isEmpty()) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    this.mAnimator = animatorSet;
                    animatorSet.playTogether(arrayList);
                    this.mAnimator.start();
                    this.mAnimator.addListener(new AnimatorListenerAdapter() {
                        /* class androidx.leanback.app.OnboardingSupportFragment.AnonymousClass5 */

                        public void onAnimationEnd(Animator animator) {
                            OnboardingSupportFragment.this.mEnterAnimationFinished = true;
                        }
                    });
                    getView().requestFocus();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public Animator onCreateDescriptionAnimator() {
        return AnimatorInflater.loadAnimator(getContext(), R$animator.lb_onboarding_description_enter);
    }

    /* access modifiers changed from: protected */
    public Animator onCreateTitleAnimator() {
        return AnimatorInflater.loadAnimator(getContext(), R$animator.lb_onboarding_title_enter);
    }

    /* access modifiers changed from: protected */
    public final int getCurrentPageIndex() {
        return this.mCurrentPageIndex;
    }

    private void onPageChangedInternal(int i) {
        Animator animator;
        AnimatorSet animatorSet = this.mAnimator;
        if (animatorSet != null) {
            animatorSet.end();
        }
        this.mPageIndicator.onPageSelected(this.mCurrentPageIndex, true);
        ArrayList arrayList = new ArrayList();
        if (i < getCurrentPageIndex()) {
            arrayList.add(createAnimator(this.mTitleView, false, 8388611, 0));
            animator = createAnimator(this.mDescriptionView, false, 8388611, 33);
            arrayList.add(animator);
            arrayList.add(createAnimator(this.mTitleView, true, 8388613, 500));
            arrayList.add(createAnimator(this.mDescriptionView, true, 8388613, 533));
        } else {
            arrayList.add(createAnimator(this.mTitleView, false, 8388613, 0));
            animator = createAnimator(this.mDescriptionView, false, 8388613, 33);
            arrayList.add(animator);
            arrayList.add(createAnimator(this.mTitleView, true, 8388611, 500));
            arrayList.add(createAnimator(this.mDescriptionView, true, 8388611, 533));
        }
        final int currentPageIndex = getCurrentPageIndex();
        animator.addListener(new AnimatorListenerAdapter() {
            /* class androidx.leanback.app.OnboardingSupportFragment.AnonymousClass6 */

            public void onAnimationEnd(Animator animator) {
                OnboardingSupportFragment onboardingSupportFragment = OnboardingSupportFragment.this;
                onboardingSupportFragment.mTitleView.setText(onboardingSupportFragment.getPageTitle(currentPageIndex));
                OnboardingSupportFragment onboardingSupportFragment2 = OnboardingSupportFragment.this;
                onboardingSupportFragment2.mDescriptionView.setText(onboardingSupportFragment2.getPageDescription(currentPageIndex));
            }
        });
        Context context = getContext();
        if (getCurrentPageIndex() == getPageCount() - 1) {
            this.mStartButton.setVisibility(0);
            Animator loadAnimator = AnimatorInflater.loadAnimator(context, R$animator.lb_onboarding_page_indicator_fade_out);
            loadAnimator.setTarget(this.mPageIndicator);
            loadAnimator.addListener(new AnimatorListenerAdapter() {
                /* class androidx.leanback.app.OnboardingSupportFragment.AnonymousClass7 */

                public void onAnimationEnd(Animator animator) {
                    OnboardingSupportFragment.this.mPageIndicator.setVisibility(8);
                }
            });
            arrayList.add(loadAnimator);
            Animator loadAnimator2 = AnimatorInflater.loadAnimator(context, R$animator.lb_onboarding_start_button_fade_in);
            loadAnimator2.setTarget(this.mStartButton);
            arrayList.add(loadAnimator2);
        } else if (i == getPageCount() - 1) {
            this.mPageIndicator.setVisibility(0);
            Animator loadAnimator3 = AnimatorInflater.loadAnimator(context, R$animator.lb_onboarding_page_indicator_fade_in);
            loadAnimator3.setTarget(this.mPageIndicator);
            arrayList.add(loadAnimator3);
            Animator loadAnimator4 = AnimatorInflater.loadAnimator(context, R$animator.lb_onboarding_start_button_fade_out);
            loadAnimator4.setTarget(this.mStartButton);
            loadAnimator4.addListener(new AnimatorListenerAdapter() {
                /* class androidx.leanback.app.OnboardingSupportFragment.AnonymousClass8 */

                public void onAnimationEnd(Animator animator) {
                    OnboardingSupportFragment.this.mStartButton.setVisibility(8);
                }
            });
            arrayList.add(loadAnimator4);
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.mAnimator = animatorSet2;
        animatorSet2.playTogether(arrayList);
        this.mAnimator.start();
        onPageChanged(this.mCurrentPageIndex, i);
    }

    private Animator createAnimator(View view, boolean z, int i, long j) {
        ObjectAnimator objectAnimator;
        ObjectAnimator objectAnimator2;
        boolean z2 = getView().getLayoutDirection() == 0;
        boolean z3 = (z2 && i == 8388613) || (!z2 && i == 8388611) || i == 5;
        if (z) {
            objectAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f, 1.0f);
            Property property = View.TRANSLATION_X;
            float[] fArr = new float[2];
            fArr[0] = (float) (z3 ? sSlideDistance : -sSlideDistance);
            fArr[1] = 0.0f;
            objectAnimator2 = ObjectAnimator.ofFloat(view, property, fArr);
            objectAnimator.setInterpolator(HEADER_APPEAR_INTERPOLATOR);
            objectAnimator2.setInterpolator(HEADER_APPEAR_INTERPOLATOR);
        } else {
            objectAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f, 0.0f);
            Property property2 = View.TRANSLATION_X;
            float[] fArr2 = new float[2];
            fArr2[0] = 0.0f;
            fArr2[1] = (float) (z3 ? sSlideDistance : -sSlideDistance);
            objectAnimator2 = ObjectAnimator.ofFloat(view, property2, fArr2);
            objectAnimator.setInterpolator(HEADER_DISAPPEAR_INTERPOLATOR);
            objectAnimator2.setInterpolator(HEADER_DISAPPEAR_INTERPOLATOR);
        }
        objectAnimator.setDuration(417L);
        objectAnimator.setTarget(view);
        objectAnimator2.setDuration(417L);
        objectAnimator2.setTarget(view);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator, objectAnimator2);
        if (j > 0) {
            animatorSet.setStartDelay(j);
        }
        return animatorSet;
    }
}
