package com.oneplus.settings.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.google.android.material.indicator.PageIndicator;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;

public class OPViewPagerGuideCategory extends Preference {
    private String[] mAnimationDarkId;
    private String[] mAnimationWhiteId;
    private ContentResolver mContentResolver;
    private Context mContext;
    private int mCurrIndex = 0;
    private int[] mDescriptionId;
    private PageIndicator mDotContainer;
    private View mDotContainerArea;
    private ArrayList<View> mGuideViews = new ArrayList<>();
    private boolean mHasInited = false;
    private int mLayoutItemID = C0012R$layout.op_viewpager_guide_item_vertical_layout;
    private int mLayoutResId = C0012R$layout.op_viewpager_guide_category;
    private int[] mTitleId;
    private ViewPager mViewPager;

    public OPViewPagerGuideCategory(Context context) {
        super(context);
        initViews(context);
    }

    public OPViewPagerGuideCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPViewPagerGuideCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        setLayoutResource(this.mLayoutResId);
    }

    public void setType(int i) {
        if (i == 1) {
            this.mLayoutItemID = C0012R$layout.op_viewpager_guide_item_vertical_layout;
        } else if (i == 2) {
            this.mLayoutItemID = C0012R$layout.op_viewpager_guide_item_landscape_layout;
        }
    }

    public void setAnimationWhiteResources(String[] strArr) {
        this.mAnimationWhiteId = strArr;
    }

    public void setAnimationDarkResources(String[] strArr) {
        this.mAnimationDarkId = strArr;
    }

    public void setTitleResources(int[] iArr) {
        this.mTitleId = iArr;
    }

    public void setDescriptionIdResources(int[] iArr) {
        this.mDescriptionId = iArr;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mDotContainerArea = preferenceViewHolder.findViewById(C0010R$id.op_viewpager_guide_dot_area);
        this.mDotContainer = (PageIndicator) preferenceViewHolder.findViewById(C0010R$id.op_viewpager_guide_dot_container);
        preferenceViewHolder.findViewById(C0010R$id.op_viewpager_guide_container);
        ViewPager viewPager = (ViewPager) preferenceViewHolder.findViewById(C0010R$id.op_viewpager_guide_viewpager);
        this.mViewPager = viewPager;
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        initViewPage();
        preferenceViewHolder.setDividerAllowedBelow(false);
    }

    public void showDotView(boolean z) {
        View view = this.mDotContainerArea;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
        }
    }

    public void initViewPage() {
        int[] iArr;
        LayoutInflater from = LayoutInflater.from(this.mContext);
        this.mGuideViews.clear();
        int i = 0;
        while (true) {
            iArr = this.mTitleId;
            if (i >= iArr.length) {
                break;
            }
            View inflate = from.inflate(this.mLayoutItemID, (ViewGroup) null);
            ((TextView) inflate.findViewById(C0010R$id.guide_title)).setText(this.mTitleId[i]);
            ((TextView) inflate.findViewById(C0010R$id.guide_summary)).setText(this.mDescriptionId[i]);
            LottieAnimationView lottieAnimationView = (LottieAnimationView) inflate.findViewById(C0010R$id.guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                lottieAnimationView.setAnimation(this.mAnimationDarkId[i]);
            } else {
                lottieAnimationView.setAnimation(this.mAnimationWhiteId[i]);
            }
            this.mGuideViews.add(inflate);
            i++;
        }
        if (iArr.length == 1) {
            showDotView(false);
        }
        AnonymousClass1 r0 = new PagerAdapter() {
            /* class com.oneplus.settings.ui.OPViewPagerGuideCategory.AnonymousClass1 */

            @Override // androidx.viewpager.widget.PagerAdapter
            public boolean isViewFromObject(View view, Object obj) {
                return view == obj;
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public int getCount() {
                return OPViewPagerGuideCategory.this.mGuideViews.size();
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public void destroyItem(View view, int i, Object obj) {
                ((ViewPager) view).removeView((View) OPViewPagerGuideCategory.this.mGuideViews.get(i));
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public Object instantiateItem(View view, int i) {
                ((ViewPager) view).addView((View) OPViewPagerGuideCategory.this.mGuideViews.get(i));
                return OPViewPagerGuideCategory.this.mGuideViews.get(i);
            }
        };
        this.mViewPager.setAdapter(r0);
        this.mViewPager.setCurrentItem(this.mCurrIndex);
        this.mDotContainer.setNumPages(r0.getCount());
        this.mDotContainer.setLocation((float) this.mViewPager.getCurrentItem());
        this.mHasInited = true;
        startAnim();
    }

    public void startAnim() {
        if (this.mHasInited) {
            playCurrentPageAnim(this.mCurrIndex);
        }
    }

    public void playCurrentPageAnim(int i) {
        for (int i2 = 0; i2 < this.mGuideViews.size(); i2++) {
            LottieAnimationView lottieAnimationView = (LottieAnimationView) this.mGuideViews.get(i2).findViewById(C0010R$id.guide_anim);
            if (i == i2) {
                lottieAnimationView.playAnimation();
            } else {
                lottieAnimationView.cancelAnimation();
            }
        }
    }

    public void stopAnim() {
        if (this.mHasInited) {
            playCurrentPageAnim(-1);
        }
    }

    public void releaseAnim() {
        stopAnim();
        ArrayList<View> arrayList = this.mGuideViews;
        if (arrayList != null) {
            arrayList.clear();
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
        }

        public MyOnPageChangeListener() {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            OPViewPagerGuideCategory.this.playCurrentPageAnim(i);
            OPViewPagerGuideCategory.this.mCurrIndex = i;
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
            OPViewPagerGuideCategory.this.mDotContainer.setLocation(((float) i) + f);
        }
    }
}
