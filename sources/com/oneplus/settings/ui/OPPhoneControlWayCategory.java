package com.oneplus.settings.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.google.android.material.indicator.PageIndicator;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;

public class OPPhoneControlWayCategory extends Preference {
    private ImageView mAlwaysShowNavigationBarImageView;
    private LottieAnimationView mBackAnim;
    private LottieAnimationView mBackAnimDeprecated;
    private LottieAnimationView mBackAnimNoBar;
    private ContentResolver mContentResolver;
    private Context mContext;
    private int mCurrIndex = 0;
    private boolean mHasInited = false;
    private LottieAnimationView mHomeAnim;
    private LottieAnimationView mHomeAnimDeprecated;
    private LottieAnimationView mHomeAnimNoBar;
    private LottieAnimationView mLandBackAnim;
    private LottieAnimationView mLandBackAnimDeprecated;
    private LottieAnimationView mLandBackAnimNoBar;
    private int mLayoutResId = C0012R$layout.op_phone_control_way_instructions_category;
    private PageIndicator mPageIndicator;
    private PagerAdapter mPagerAdapter;
    private LottieAnimationView mPreviousAppAnim;
    private LottieAnimationView mPreviousAppAnimDeprecated;
    private LottieAnimationView mPreviousAppAnimNoBar;
    private LottieAnimationView mRecentAnim;
    private LottieAnimationView mRecentAnimDeprecated;
    private LottieAnimationView mRecentAnimNoBar;
    private int mTempType = 0;
    private ViewPager mViewPager;
    private View mViewPagerContainer;
    private final ArrayList<View> mViews = new ArrayList<>();
    private final ArrayList<View> mViewsDeprecated = new ArrayList<>();
    private final ArrayList<View> mViewsNobar = new ArrayList<>();

    public OPPhoneControlWayCategory(Context context) {
        super(context);
        initViews(context);
    }

    public OPPhoneControlWayCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPPhoneControlWayCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        setLayoutResource(this.mLayoutResId);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mAlwaysShowNavigationBarImageView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.always_show_nb_anim);
        if (OPUtils.isBlackModeOn(this.mContentResolver)) {
            this.mAlwaysShowNavigationBarImageView.setBackgroundResource(C0008R$drawable.op_always_show_navigation_bar_dark);
        } else {
            this.mAlwaysShowNavigationBarImageView.setBackgroundResource(C0008R$drawable.op_always_show_navigation_bar_light);
        }
        this.mPageIndicator = (PageIndicator) preferenceViewHolder.findViewById(C0010R$id.gesture_page_indicator);
        this.mViewPagerContainer = preferenceViewHolder.findViewById(C0010R$id.gesture_anim_viewpager_container);
        ViewPager viewPager = (ViewPager) preferenceViewHolder.findViewById(C0010R$id.gesture_anim_viewpager);
        this.mViewPager = viewPager;
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        initViewPage();
        AnonymousClass1 r0 = new PagerAdapter() {
            /* class com.oneplus.settings.ui.OPPhoneControlWayCategory.AnonymousClass1 */

            @Override // androidx.viewpager.widget.PagerAdapter
            public int getItemPosition(Object obj) {
                return -2;
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public boolean isViewFromObject(View view, Object obj) {
                return view == obj;
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public int getCount() {
                if (!OPPhoneControlWayCategory.isSideEnabled(OPPhoneControlWayCategory.this.getContext())) {
                    return OPPhoneControlWayCategory.this.mViewsDeprecated.size();
                }
                if (OPPhoneControlWayCategory.isHideBarEnabled(OPPhoneControlWayCategory.this.getContext())) {
                    return OPPhoneControlWayCategory.this.mViewsNobar.size();
                }
                return OPPhoneControlWayCategory.this.mViews.size();
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
                if (!OPPhoneControlWayCategory.isSideEnabled(OPPhoneControlWayCategory.this.getContext())) {
                    if (OPPhoneControlWayCategory.this.mViewsDeprecated.size() > i) {
                        viewGroup.removeView((View) OPPhoneControlWayCategory.this.mViewsDeprecated.get(i));
                    } else {
                        viewGroup.removeAllViews();
                    }
                    if (OPPhoneControlWayCategory.this.mViews.size() > i) {
                        viewGroup.removeView((View) OPPhoneControlWayCategory.this.mViews.get(i));
                    }
                    if (OPPhoneControlWayCategory.this.mViewsNobar.size() > i) {
                        viewGroup.removeView((View) OPPhoneControlWayCategory.this.mViewsNobar.get(i));
                    }
                } else if (OPPhoneControlWayCategory.isHideBarEnabled(OPPhoneControlWayCategory.this.getContext())) {
                    if (OPPhoneControlWayCategory.this.mViewsNobar.size() > i) {
                        viewGroup.removeView((View) OPPhoneControlWayCategory.this.mViewsNobar.get(i));
                    } else {
                        viewGroup.removeAllViews();
                    }
                    if (OPPhoneControlWayCategory.this.mViews.size() > i) {
                        viewGroup.removeView((View) OPPhoneControlWayCategory.this.mViews.get(i));
                    }
                    if (OPPhoneControlWayCategory.this.mViewsDeprecated.size() > i) {
                        viewGroup.removeView((View) OPPhoneControlWayCategory.this.mViewsDeprecated.get(i));
                    }
                } else {
                    if (OPPhoneControlWayCategory.this.mViews.size() > i) {
                        viewGroup.removeView((View) OPPhoneControlWayCategory.this.mViews.get(i));
                    } else {
                        viewGroup.removeAllViews();
                    }
                    if (OPPhoneControlWayCategory.this.mViewsNobar.size() > i) {
                        viewGroup.removeView((View) OPPhoneControlWayCategory.this.mViewsNobar.get(i));
                    }
                    if (OPPhoneControlWayCategory.this.mViewsDeprecated.size() > i) {
                        viewGroup.removeView((View) OPPhoneControlWayCategory.this.mViewsDeprecated.get(i));
                    }
                }
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public Object instantiateItem(ViewGroup viewGroup, int i) {
                if (!OPPhoneControlWayCategory.isSideEnabled(OPPhoneControlWayCategory.this.getContext())) {
                    viewGroup.addView((View) OPPhoneControlWayCategory.this.mViewsDeprecated.get(i));
                    return OPPhoneControlWayCategory.this.mViewsDeprecated.get(i);
                } else if (OPPhoneControlWayCategory.isHideBarEnabled(OPPhoneControlWayCategory.this.getContext())) {
                    viewGroup.addView((View) OPPhoneControlWayCategory.this.mViewsNobar.get(i));
                    return OPPhoneControlWayCategory.this.mViewsNobar.get(i);
                } else {
                    viewGroup.addView((View) OPPhoneControlWayCategory.this.mViews.get(i));
                    return OPPhoneControlWayCategory.this.mViews.get(i);
                }
            }
        };
        this.mPagerAdapter = r0;
        this.mViewPager.setAdapter(r0);
        this.mViewPager.setCurrentItem(this.mCurrIndex);
        this.mPageIndicator.setNumPages(this.mPagerAdapter.getCount());
        this.mPageIndicator.setLocation((float) this.mViewPager.getCurrentItem());
        this.mHasInited = true;
        startAnim();
        preferenceViewHolder.setDividerAllowedBelow(false);
    }

    private void initViewPage() {
        if (this.mViews.size() <= 5 || this.mViewsNobar.size() <= 5 || this.mViewsDeprecated.size() <= 5) {
            this.mViews.clear();
            this.mViewsNobar.clear();
            this.mViewsDeprecated.clear();
            LayoutInflater from = LayoutInflater.from(this.mContext);
            View inflate = from.inflate(C0012R$layout.op_fullscreen_gesture_guide_layout_land, (ViewGroup) null);
            ((TextView) inflate.findViewById(C0010R$id.fullscreen_guide_title)).setText(C0017R$string.oneplus_fullscreen_back_guide_title);
            ((TextView) inflate.findViewById(C0010R$id.fullscreen_guide_summary)).setText(C0017R$string.oneplus_gesture_navigation_bar_summary_for_deprecated);
            this.mBackAnim = (LottieAnimationView) inflate.findViewById(C0010R$id.fullscreen_guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mBackAnim.setAnimation("op_back_gesture_dark_anim.json");
            } else {
                this.mBackAnim.setAnimation("op_back_gesture_light_anim.json");
            }
            this.mBackAnim.loop(true);
            this.mBackAnim.playAnimation();
            View inflate2 = from.inflate(C0012R$layout.op_fullscreen_gesture_guide_layout_land, (ViewGroup) null);
            ((TextView) inflate2.findViewById(C0010R$id.fullscreen_guide_title)).setText(C0017R$string.oneplus_fullscreen_back_guide_title);
            ((TextView) inflate2.findViewById(C0010R$id.fullscreen_guide_summary)).setText(C0017R$string.oneplus_gesture_navigation_bar_summary_for_deprecated);
            this.mBackAnimNoBar = (LottieAnimationView) inflate2.findViewById(C0010R$id.fullscreen_guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mBackAnimNoBar.setAnimation("op_back_gesture_dark_anim_no_bar.json");
            } else {
                this.mBackAnimNoBar.setAnimation("op_back_gesture_light_anim_no_bar.json");
            }
            this.mBackAnimNoBar.loop(true);
            this.mBackAnimNoBar.playAnimation();
            View inflate3 = from.inflate(C0012R$layout.op_fullscreen_gesture_guide_layout_land, (ViewGroup) null);
            ((TextView) inflate3.findViewById(C0010R$id.fullscreen_guide_title)).setText(C0017R$string.oneplus_fullscreen_back_guide_title);
            ((TextView) inflate3.findViewById(C0010R$id.fullscreen_guide_summary)).setText(C0017R$string.oneplus_fullscreen_back_guide_summary_deprecated);
            this.mBackAnimDeprecated = (LottieAnimationView) inflate3.findViewById(C0010R$id.fullscreen_guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mBackAnimDeprecated.setAnimation("op_back_gesture_dark_anim_deprecated.json");
            } else {
                this.mBackAnimDeprecated.setAnimation("op_back_gesture_light_anim_deprecated.json");
            }
            this.mBackAnimDeprecated.loop(true);
            this.mBackAnimDeprecated.playAnimation();
            View inflate4 = from.inflate(C0012R$layout.op_fullscreen_gesture_guide_layout_land, (ViewGroup) null);
            ((TextView) inflate4.findViewById(C0010R$id.fullscreen_guide_title)).setText(C0017R$string.oneplus_fullscreen_home_guide_title);
            ((TextView) inflate4.findViewById(C0010R$id.fullscreen_guide_summary)).setText(C0017R$string.oneplus_fullscreen_home_guide_summary);
            this.mHomeAnim = (LottieAnimationView) inflate4.findViewById(C0010R$id.fullscreen_guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mHomeAnim.setAnimation("op_home_gesture_dark_anim.json");
            } else {
                this.mHomeAnim.setAnimation("op_home_gesture_light_anim.json");
            }
            this.mHomeAnim.loop(true);
            this.mHomeAnim.playAnimation();
            View inflate5 = from.inflate(C0012R$layout.op_fullscreen_gesture_guide_layout_land, (ViewGroup) null);
            ((TextView) inflate5.findViewById(C0010R$id.fullscreen_guide_title)).setText(C0017R$string.oneplus_fullscreen_home_guide_title);
            ((TextView) inflate5.findViewById(C0010R$id.fullscreen_guide_summary)).setText(C0017R$string.oneplus_fullscreen_home_guide_summary);
            this.mHomeAnimNoBar = (LottieAnimationView) inflate5.findViewById(C0010R$id.fullscreen_guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mHomeAnimNoBar.setAnimation("op_home_gesture_dark_anim_no_bar.json");
            } else {
                this.mHomeAnimNoBar.setAnimation("op_home_gesture_light_anim_no_bar.json");
            }
            this.mHomeAnimNoBar.loop(true);
            this.mHomeAnimNoBar.playAnimation();
            View inflate6 = from.inflate(C0012R$layout.op_fullscreen_gesture_guide_layout_land, (ViewGroup) null);
            ((TextView) inflate6.findViewById(C0010R$id.fullscreen_guide_title)).setText(C0017R$string.oneplus_fullscreen_home_guide_title);
            ((TextView) inflate6.findViewById(C0010R$id.fullscreen_guide_summary)).setText(C0017R$string.oneplus_fullscreen_home_guide_summary_deprecated);
            this.mHomeAnimDeprecated = (LottieAnimationView) inflate6.findViewById(C0010R$id.fullscreen_guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mHomeAnimDeprecated.setAnimation("op_home_gesture_dark_anim_deprecated.json");
            } else {
                this.mHomeAnimDeprecated.setAnimation("op_home_gesture_light_anim_deprecated.json");
            }
            this.mHomeAnimDeprecated.loop(true);
            this.mHomeAnimDeprecated.playAnimation();
            View inflate7 = from.inflate(C0012R$layout.op_fullscreen_gesture_guide_layout_land, (ViewGroup) null);
            ((TextView) inflate7.findViewById(C0010R$id.fullscreen_guide_title)).setText(C0017R$string.oneplus_fullscreen_recent_guide_title);
            ((TextView) inflate7.findViewById(C0010R$id.fullscreen_guide_summary)).setText(C0017R$string.oneplus_fullscreen_recent_guide_summary);
            this.mRecentAnim = (LottieAnimationView) inflate7.findViewById(C0010R$id.fullscreen_guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mRecentAnim.setAnimation("op_recent_gesture_dark_anim.json");
            } else {
                this.mRecentAnim.setAnimation("op_recent_gesture_light_anim.json");
            }
            this.mRecentAnim.loop(true);
            this.mRecentAnim.playAnimation();
            View inflate8 = from.inflate(C0012R$layout.op_fullscreen_gesture_guide_layout_land, (ViewGroup) null);
            ((TextView) inflate8.findViewById(C0010R$id.fullscreen_guide_title)).setText(C0017R$string.oneplus_fullscreen_recent_guide_title);
            ((TextView) inflate8.findViewById(C0010R$id.fullscreen_guide_summary)).setText(C0017R$string.oneplus_fullscreen_recent_guide_summary);
            this.mRecentAnimNoBar = (LottieAnimationView) inflate8.findViewById(C0010R$id.fullscreen_guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mRecentAnimNoBar.setAnimation("op_recent_gesture_dark_anim_no_bar.json");
            } else {
                this.mRecentAnimNoBar.setAnimation("op_recent_gesture_light_anim_no_bar.json");
            }
            this.mRecentAnimNoBar.loop(true);
            this.mRecentAnimNoBar.playAnimation();
            View inflate9 = from.inflate(C0012R$layout.op_fullscreen_gesture_guide_layout_land, (ViewGroup) null);
            ((TextView) inflate9.findViewById(C0010R$id.fullscreen_guide_title)).setText(C0017R$string.oneplus_fullscreen_recent_guide_title);
            ((TextView) inflate9.findViewById(C0010R$id.fullscreen_guide_summary)).setText(C0017R$string.oneplus_fullscreen_recent_guide_summary_deprecated);
            this.mRecentAnimDeprecated = (LottieAnimationView) inflate9.findViewById(C0010R$id.fullscreen_guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mRecentAnimDeprecated.setAnimation("op_recent_gesture_dark_anim_deprecated.json");
            } else {
                this.mRecentAnimDeprecated.setAnimation("op_recent_gesture_light_anim_deprecated.json");
            }
            this.mRecentAnimDeprecated.loop(true);
            this.mRecentAnimDeprecated.playAnimation();
            View inflate10 = from.inflate(C0012R$layout.op_fullscreen_gesture_guide_layout_land, (ViewGroup) null);
            ((TextView) inflate10.findViewById(C0010R$id.fullscreen_guide_title)).setText(C0017R$string.oneplus_fullscreen_previous_app_guide_title);
            ((TextView) inflate10.findViewById(C0010R$id.fullscreen_guide_summary)).setText(C0017R$string.oneplus_fullscreen_previous_app_guide_summary);
            this.mPreviousAppAnim = (LottieAnimationView) inflate10.findViewById(C0010R$id.fullscreen_guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mPreviousAppAnim.setAnimation("op_previous_app_gesture_dark_anim.json");
            } else {
                this.mPreviousAppAnim.setAnimation("op_previous_app_gesture_light_anim.json");
            }
            this.mPreviousAppAnim.loop(true);
            this.mPreviousAppAnim.playAnimation();
            View inflate11 = from.inflate(C0012R$layout.op_fullscreen_gesture_guide_layout_land, (ViewGroup) null);
            ((TextView) inflate11.findViewById(C0010R$id.fullscreen_guide_title)).setText(C0017R$string.oneplus_fullscreen_previous_app_no_bar_guide_title);
            ((TextView) inflate11.findViewById(C0010R$id.fullscreen_guide_summary)).setText(C0017R$string.oneplus_fullscreen_previous_app_no_bar_guide_summary);
            this.mPreviousAppAnimNoBar = (LottieAnimationView) inflate11.findViewById(C0010R$id.fullscreen_guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mPreviousAppAnimNoBar.setAnimation("op_previous_app_gesture_dark_anim_no_bar.json");
            } else {
                this.mPreviousAppAnimNoBar.setAnimation("op_previous_app_gesture_light_anim_no_bar.json");
            }
            this.mPreviousAppAnimNoBar.loop(true);
            this.mPreviousAppAnimNoBar.playAnimation();
            View inflate12 = from.inflate(C0012R$layout.op_fullscreen_gesture_guide_layout_land, (ViewGroup) null);
            ((TextView) inflate12.findViewById(C0010R$id.fullscreen_guide_title)).setText(C0017R$string.oneplus_fullscreen_previous_app_no_bar_guide_title);
            ((TextView) inflate12.findViewById(C0010R$id.fullscreen_guide_summary)).setText(C0017R$string.oneplus_fullscreen_previous_app_guide_summary_deprecated);
            this.mPreviousAppAnimDeprecated = (LottieAnimationView) inflate12.findViewById(C0010R$id.fullscreen_guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mPreviousAppAnimDeprecated.setAnimation("op_previous_app_gesture_dark_anim_deprecated.json");
            } else {
                this.mPreviousAppAnimDeprecated.setAnimation("op_previous_app_gesture_light_anim_deprecated.json");
            }
            this.mPreviousAppAnimDeprecated.loop(true);
            this.mPreviousAppAnimDeprecated.playAnimation();
            View inflate13 = from.inflate(C0012R$layout.op_fullscreen_gesture_guide_layout_land, (ViewGroup) null);
            ((TextView) inflate13.findViewById(C0010R$id.fullscreen_guide_title)).setText(C0017R$string.oneplus_fullscreen_landscape_guide_title);
            ((TextView) inflate13.findViewById(C0010R$id.fullscreen_guide_summary)).setText(C0017R$string.oneplus_fullscreen_landscape_guide_summary);
            this.mLandBackAnim = (LottieAnimationView) inflate13.findViewById(C0010R$id.fullscreen_guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mLandBackAnim.setAnimation("op_landscape_dark_anim.json");
            } else {
                this.mLandBackAnim.setAnimation("op_landscape_light_anim.json");
            }
            this.mLandBackAnim.loop(true);
            this.mLandBackAnim.playAnimation();
            View inflate14 = from.inflate(C0012R$layout.op_fullscreen_gesture_guide_layout_land, (ViewGroup) null);
            ((TextView) inflate14.findViewById(C0010R$id.fullscreen_guide_title)).setText(C0017R$string.oneplus_fullscreen_landscape_guide_title);
            ((TextView) inflate14.findViewById(C0010R$id.fullscreen_guide_summary)).setText(C0017R$string.oneplus_fullscreen_landscape_guide_summary);
            this.mLandBackAnimNoBar = (LottieAnimationView) inflate14.findViewById(C0010R$id.fullscreen_guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mLandBackAnimNoBar.setAnimation("op_landscape_dark_anim_no_bar.json");
            } else {
                this.mLandBackAnimNoBar.setAnimation("op_landscape_light_anim_no_bar.json");
            }
            this.mLandBackAnimNoBar.loop(true);
            this.mLandBackAnimNoBar.playAnimation();
            View inflate15 = from.inflate(C0012R$layout.op_fullscreen_gesture_guide_layout_land, (ViewGroup) null);
            ((TextView) inflate15.findViewById(C0010R$id.fullscreen_guide_title)).setText(C0017R$string.oneplus_fullscreen_landscape_guide_title);
            ((TextView) inflate15.findViewById(C0010R$id.fullscreen_guide_summary)).setText(C0017R$string.oneplus_fullscreen_landscape_guide_summary_deprecated);
            this.mLandBackAnimDeprecated = (LottieAnimationView) inflate15.findViewById(C0010R$id.fullscreen_guide_anim);
            if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mLandBackAnimDeprecated.setAnimation("op_landscape_dark_anim_deprecated.json");
            } else {
                this.mLandBackAnimDeprecated.setAnimation("op_landscape_light_anim_deprecated.json");
            }
            this.mLandBackAnimDeprecated.loop(true);
            this.mLandBackAnimDeprecated.playAnimation();
            this.mViews.add(inflate);
            this.mViewsNobar.add(inflate2);
            this.mViewsDeprecated.add(inflate3);
            this.mViews.add(inflate4);
            this.mViewsNobar.add(inflate5);
            this.mViewsDeprecated.add(inflate6);
            this.mViews.add(inflate7);
            this.mViewsNobar.add(inflate8);
            this.mViewsDeprecated.add(inflate9);
            if (OPUtils.isSupportNewGesture()) {
                this.mViews.add(inflate10);
                this.mViewsNobar.add(inflate11);
                this.mViewsDeprecated.add(inflate12);
            }
            this.mViews.add(inflate13);
            this.mViewsNobar.add(inflate14);
            this.mViewsDeprecated.add(inflate15);
        }
    }

    public void startAnim() {
        if (this.mHasInited) {
            int i = this.mTempType;
            if (i == 0) {
                int i2 = 1;
                if (isGESTURALEnabled(this.mContext) || is2ButtonEnabled(this.mContext)) {
                    if (isGESTURALEnabled(this.mContext) && !isSideEnabled(this.mContext)) {
                        i2 = 3;
                    } else if (isSideEnabled(this.mContext) && isHideBarEnabled(this.mContext)) {
                        i2 = 4;
                    } else if (isSideEnabled(this.mContext)) {
                        i2 = 2;
                    }
                }
                setViewType(i2);
                return;
            }
            setViewType(i);
        }
    }

    static boolean is2ButtonEnabled(Context context) {
        return 1 == context.getResources().getInteger(17694854);
    }

    static boolean isGESTURALEnabled(Context context) {
        return 2 == context.getResources().getInteger(17694854);
    }

    static boolean isHideBarEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "oneplus_fullscreen_gesture_type", 0) == 1;
    }

    static boolean isSideEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "op_gesture_button_side_enabled", 0) == 1;
    }

    public void setViewType(int i) {
        if (this.mHasInited) {
            this.mTempType = i;
            stopAnim();
            if (i == 1) {
                this.mAlwaysShowNavigationBarImageView.setVisibility(0);
                this.mViewPagerContainer.setVisibility(8);
            } else if (i == 2 || i == 3 || i == 4) {
                this.mAlwaysShowNavigationBarImageView.setVisibility(8);
                this.mViewPagerContainer.setVisibility(0);
                this.mPagerAdapter.notifyDataSetChanged();
                playCurrentPageAnim(this.mCurrIndex);
            }
        }
    }

    public void setViewType(int i, int i2) {
        if (this.mHasInited) {
            this.mTempType = i;
            stopAnim();
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        this.mAlwaysShowNavigationBarImageView.setVisibility(8);
                        this.mViewPagerContainer.setVisibility(0);
                        this.mPagerAdapter.notifyDataSetChanged();
                        playCurrentPageAnim(this.mCurrIndex);
                        return;
                    } else if (i != 4) {
                        return;
                    }
                }
                this.mAlwaysShowNavigationBarImageView.setVisibility(8);
                this.mViewPagerContainer.setVisibility(0);
                this.mPagerAdapter.notifyDataSetChanged();
                playCurrentPageAnim(i2);
                this.mViewPager.setCurrentItem(i2, false);
                return;
            }
            this.mAlwaysShowNavigationBarImageView.setVisibility(0);
            this.mViewPagerContainer.setVisibility(8);
        }
    }

    public void stopAnim() {
        if (this.mHasInited) {
            this.mBackAnim.cancelAnimation();
            this.mBackAnimNoBar.cancelAnimation();
            this.mHomeAnim.cancelAnimation();
            this.mHomeAnimNoBar.cancelAnimation();
            this.mRecentAnim.cancelAnimation();
            this.mRecentAnimNoBar.cancelAnimation();
            this.mPreviousAppAnim.cancelAnimation();
            this.mPreviousAppAnimNoBar.cancelAnimation();
            this.mLandBackAnim.cancelAnimation();
            this.mLandBackAnimNoBar.cancelAnimation();
            this.mBackAnimDeprecated.cancelAnimation();
            this.mHomeAnimDeprecated.cancelAnimation();
            this.mRecentAnimDeprecated.cancelAnimation();
            this.mPreviousAppAnimDeprecated.cancelAnimation();
            this.mLandBackAnimDeprecated.cancelAnimation();
        }
    }

    public void releaseAnim() {
        if (this.mHasInited) {
            this.mTempType = 0;
            this.mBackAnim.cancelAnimation();
            this.mBackAnimNoBar.cancelAnimation();
            this.mHomeAnim.cancelAnimation();
            this.mHomeAnimNoBar.cancelAnimation();
            this.mRecentAnim.cancelAnimation();
            this.mRecentAnimNoBar.cancelAnimation();
            this.mPreviousAppAnim.cancelAnimation();
            this.mPreviousAppAnimNoBar.cancelAnimation();
            this.mLandBackAnim.cancelAnimation();
            this.mLandBackAnimNoBar.cancelAnimation();
            this.mBackAnimDeprecated.cancelAnimation();
            this.mHomeAnimDeprecated.cancelAnimation();
            this.mRecentAnimDeprecated.cancelAnimation();
            this.mPreviousAppAnimDeprecated.cancelAnimation();
            this.mLandBackAnimDeprecated.cancelAnimation();
            this.mBackAnim = null;
            this.mBackAnimNoBar = null;
            this.mHomeAnim = null;
            this.mHomeAnimNoBar = null;
            this.mRecentAnim = null;
            this.mRecentAnimNoBar = null;
            this.mPreviousAppAnim = null;
            this.mPreviousAppAnimNoBar = null;
            this.mLandBackAnim = null;
            this.mLandBackAnimNoBar = null;
            this.mBackAnimDeprecated = null;
            this.mHomeAnimDeprecated = null;
            this.mRecentAnimDeprecated = null;
            this.mPreviousAppAnimDeprecated = null;
            this.mLandBackAnimDeprecated = null;
        }
    }

    public void playCurrentPageAnim(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i == 4) {
                            if (!isSideEnabled(getContext())) {
                                this.mBackAnim.cancelAnimation();
                                this.mHomeAnim.cancelAnimation();
                                this.mRecentAnim.cancelAnimation();
                                this.mPreviousAppAnim.cancelAnimation();
                                this.mLandBackAnim.cancelAnimation();
                                this.mBackAnimDeprecated.cancelAnimation();
                                this.mHomeAnimDeprecated.cancelAnimation();
                                this.mRecentAnimDeprecated.cancelAnimation();
                                this.mPreviousAppAnimDeprecated.cancelAnimation();
                                this.mLandBackAnimDeprecated.playAnimation();
                                this.mBackAnimNoBar.cancelAnimation();
                                this.mHomeAnimNoBar.cancelAnimation();
                                this.mRecentAnimNoBar.cancelAnimation();
                                this.mPreviousAppAnimNoBar.cancelAnimation();
                                this.mLandBackAnimNoBar.cancelAnimation();
                            } else if (isHideBarEnabled(getContext())) {
                                this.mBackAnim.cancelAnimation();
                                this.mHomeAnim.cancelAnimation();
                                this.mRecentAnim.playAnimation();
                                this.mPreviousAppAnim.cancelAnimation();
                                this.mLandBackAnim.cancelAnimation();
                                this.mBackAnimDeprecated.cancelAnimation();
                                this.mHomeAnimDeprecated.cancelAnimation();
                                this.mRecentAnimDeprecated.cancelAnimation();
                                this.mPreviousAppAnimDeprecated.cancelAnimation();
                                this.mLandBackAnimDeprecated.cancelAnimation();
                                this.mBackAnimNoBar.cancelAnimation();
                                this.mHomeAnimNoBar.cancelAnimation();
                                this.mRecentAnimNoBar.cancelAnimation();
                                this.mPreviousAppAnimNoBar.cancelAnimation();
                                this.mLandBackAnimNoBar.playAnimation();
                            } else {
                                this.mBackAnim.cancelAnimation();
                                this.mHomeAnim.cancelAnimation();
                                this.mRecentAnim.cancelAnimation();
                                this.mPreviousAppAnim.cancelAnimation();
                                this.mLandBackAnim.playAnimation();
                                this.mBackAnimDeprecated.cancelAnimation();
                                this.mHomeAnimDeprecated.cancelAnimation();
                                this.mRecentAnimDeprecated.cancelAnimation();
                                this.mPreviousAppAnimDeprecated.cancelAnimation();
                                this.mLandBackAnimDeprecated.cancelAnimation();
                                this.mBackAnimNoBar.cancelAnimation();
                                this.mHomeAnimNoBar.cancelAnimation();
                                this.mRecentAnimNoBar.cancelAnimation();
                                this.mPreviousAppAnimNoBar.cancelAnimation();
                                this.mLandBackAnimNoBar.cancelAnimation();
                            }
                        }
                    } else if (!isSideEnabled(getContext())) {
                        this.mBackAnim.cancelAnimation();
                        this.mHomeAnim.cancelAnimation();
                        this.mRecentAnim.cancelAnimation();
                        this.mPreviousAppAnim.cancelAnimation();
                        this.mLandBackAnim.cancelAnimation();
                        this.mBackAnimDeprecated.cancelAnimation();
                        this.mHomeAnimDeprecated.cancelAnimation();
                        this.mRecentAnimDeprecated.cancelAnimation();
                        this.mPreviousAppAnimDeprecated.playAnimation();
                        this.mLandBackAnimDeprecated.cancelAnimation();
                        this.mBackAnimNoBar.cancelAnimation();
                        this.mHomeAnimNoBar.cancelAnimation();
                        this.mRecentAnimNoBar.cancelAnimation();
                        this.mPreviousAppAnimNoBar.cancelAnimation();
                        this.mLandBackAnimNoBar.cancelAnimation();
                    } else if (isHideBarEnabled(getContext())) {
                        this.mBackAnim.cancelAnimation();
                        this.mHomeAnim.cancelAnimation();
                        this.mRecentAnim.playAnimation();
                        this.mPreviousAppAnim.cancelAnimation();
                        this.mLandBackAnim.cancelAnimation();
                        this.mBackAnimDeprecated.cancelAnimation();
                        this.mHomeAnimDeprecated.cancelAnimation();
                        this.mRecentAnimDeprecated.cancelAnimation();
                        this.mPreviousAppAnimDeprecated.cancelAnimation();
                        this.mLandBackAnimDeprecated.cancelAnimation();
                        this.mBackAnimNoBar.cancelAnimation();
                        this.mHomeAnimNoBar.cancelAnimation();
                        this.mRecentAnimNoBar.cancelAnimation();
                        this.mPreviousAppAnimNoBar.playAnimation();
                        this.mLandBackAnimNoBar.cancelAnimation();
                    } else {
                        this.mBackAnim.cancelAnimation();
                        this.mHomeAnim.cancelAnimation();
                        this.mRecentAnim.cancelAnimation();
                        this.mPreviousAppAnim.playAnimation();
                        this.mLandBackAnim.cancelAnimation();
                        this.mBackAnimDeprecated.cancelAnimation();
                        this.mHomeAnimDeprecated.cancelAnimation();
                        this.mRecentAnimDeprecated.cancelAnimation();
                        this.mPreviousAppAnimDeprecated.cancelAnimation();
                        this.mLandBackAnimDeprecated.cancelAnimation();
                        this.mBackAnimNoBar.cancelAnimation();
                        this.mHomeAnimNoBar.cancelAnimation();
                        this.mRecentAnimNoBar.cancelAnimation();
                        this.mPreviousAppAnimNoBar.cancelAnimation();
                        this.mLandBackAnimNoBar.cancelAnimation();
                    }
                } else if (!isSideEnabled(getContext())) {
                    this.mBackAnim.cancelAnimation();
                    this.mHomeAnim.cancelAnimation();
                    this.mRecentAnim.cancelAnimation();
                    this.mPreviousAppAnim.cancelAnimation();
                    this.mLandBackAnim.cancelAnimation();
                    this.mBackAnimDeprecated.cancelAnimation();
                    this.mHomeAnimDeprecated.cancelAnimation();
                    this.mRecentAnimDeprecated.playAnimation();
                    this.mPreviousAppAnimDeprecated.cancelAnimation();
                    this.mLandBackAnimDeprecated.cancelAnimation();
                    this.mBackAnimNoBar.cancelAnimation();
                    this.mHomeAnimNoBar.cancelAnimation();
                    this.mRecentAnimNoBar.cancelAnimation();
                    this.mPreviousAppAnimNoBar.cancelAnimation();
                    this.mLandBackAnimNoBar.cancelAnimation();
                } else if (isHideBarEnabled(getContext())) {
                    this.mBackAnim.cancelAnimation();
                    this.mHomeAnim.cancelAnimation();
                    this.mRecentAnim.cancelAnimation();
                    this.mPreviousAppAnim.cancelAnimation();
                    this.mLandBackAnim.cancelAnimation();
                    this.mBackAnimDeprecated.cancelAnimation();
                    this.mHomeAnimDeprecated.cancelAnimation();
                    this.mRecentAnimDeprecated.cancelAnimation();
                    this.mPreviousAppAnimDeprecated.cancelAnimation();
                    this.mLandBackAnimDeprecated.cancelAnimation();
                    this.mBackAnimNoBar.cancelAnimation();
                    this.mHomeAnimNoBar.cancelAnimation();
                    this.mRecentAnimNoBar.playAnimation();
                    this.mPreviousAppAnimNoBar.cancelAnimation();
                    this.mLandBackAnimNoBar.cancelAnimation();
                } else {
                    this.mBackAnim.cancelAnimation();
                    this.mHomeAnim.cancelAnimation();
                    this.mRecentAnim.playAnimation();
                    this.mPreviousAppAnim.cancelAnimation();
                    this.mLandBackAnim.cancelAnimation();
                    this.mBackAnimDeprecated.cancelAnimation();
                    this.mHomeAnimDeprecated.cancelAnimation();
                    this.mRecentAnimDeprecated.cancelAnimation();
                    this.mPreviousAppAnimDeprecated.cancelAnimation();
                    this.mLandBackAnimDeprecated.cancelAnimation();
                    this.mBackAnimNoBar.cancelAnimation();
                    this.mHomeAnimNoBar.cancelAnimation();
                    this.mRecentAnimNoBar.cancelAnimation();
                    this.mPreviousAppAnimNoBar.cancelAnimation();
                    this.mLandBackAnimNoBar.cancelAnimation();
                }
            } else if (!isSideEnabled(getContext())) {
                this.mBackAnim.cancelAnimation();
                this.mHomeAnim.cancelAnimation();
                this.mRecentAnim.cancelAnimation();
                this.mPreviousAppAnim.cancelAnimation();
                this.mLandBackAnim.cancelAnimation();
                this.mBackAnimDeprecated.cancelAnimation();
                this.mHomeAnimDeprecated.playAnimation();
                this.mRecentAnimDeprecated.cancelAnimation();
                this.mPreviousAppAnimDeprecated.cancelAnimation();
                this.mLandBackAnimDeprecated.cancelAnimation();
                this.mBackAnimNoBar.cancelAnimation();
                this.mHomeAnimNoBar.cancelAnimation();
                this.mRecentAnimNoBar.cancelAnimation();
                this.mPreviousAppAnimNoBar.cancelAnimation();
                this.mLandBackAnimNoBar.cancelAnimation();
            } else if (isHideBarEnabled(getContext())) {
                this.mBackAnim.cancelAnimation();
                this.mHomeAnim.cancelAnimation();
                this.mRecentAnim.cancelAnimation();
                this.mPreviousAppAnim.cancelAnimation();
                this.mLandBackAnim.cancelAnimation();
                this.mBackAnimDeprecated.cancelAnimation();
                this.mHomeAnimDeprecated.cancelAnimation();
                this.mRecentAnimDeprecated.cancelAnimation();
                this.mPreviousAppAnimDeprecated.cancelAnimation();
                this.mLandBackAnimDeprecated.cancelAnimation();
                this.mBackAnimNoBar.cancelAnimation();
                this.mHomeAnimNoBar.playAnimation();
                this.mRecentAnimNoBar.cancelAnimation();
                this.mPreviousAppAnimNoBar.cancelAnimation();
                this.mLandBackAnimNoBar.cancelAnimation();
            } else {
                this.mBackAnim.cancelAnimation();
                this.mHomeAnim.playAnimation();
                this.mRecentAnim.cancelAnimation();
                this.mPreviousAppAnim.cancelAnimation();
                this.mLandBackAnim.cancelAnimation();
                this.mBackAnimDeprecated.cancelAnimation();
                this.mHomeAnimDeprecated.cancelAnimation();
                this.mRecentAnimDeprecated.cancelAnimation();
                this.mPreviousAppAnimDeprecated.cancelAnimation();
                this.mLandBackAnimDeprecated.cancelAnimation();
                this.mBackAnimNoBar.cancelAnimation();
                this.mHomeAnimNoBar.cancelAnimation();
                this.mRecentAnimNoBar.cancelAnimation();
                this.mPreviousAppAnimNoBar.cancelAnimation();
                this.mLandBackAnimNoBar.cancelAnimation();
            }
        } else if (!isSideEnabled(getContext())) {
            this.mBackAnim.cancelAnimation();
            this.mHomeAnim.cancelAnimation();
            this.mRecentAnim.cancelAnimation();
            this.mPreviousAppAnim.cancelAnimation();
            this.mLandBackAnim.cancelAnimation();
            this.mBackAnimDeprecated.playAnimation();
            this.mHomeAnimDeprecated.cancelAnimation();
            this.mRecentAnimDeprecated.cancelAnimation();
            this.mPreviousAppAnimDeprecated.cancelAnimation();
            this.mLandBackAnimDeprecated.cancelAnimation();
            this.mBackAnimNoBar.cancelAnimation();
            this.mHomeAnimNoBar.cancelAnimation();
            this.mRecentAnimNoBar.cancelAnimation();
            this.mPreviousAppAnimNoBar.cancelAnimation();
            this.mLandBackAnimNoBar.cancelAnimation();
        } else if (isHideBarEnabled(getContext())) {
            this.mBackAnim.cancelAnimation();
            this.mHomeAnim.cancelAnimation();
            this.mRecentAnim.cancelAnimation();
            this.mPreviousAppAnim.cancelAnimation();
            this.mLandBackAnim.cancelAnimation();
            this.mBackAnimDeprecated.cancelAnimation();
            this.mHomeAnimDeprecated.cancelAnimation();
            this.mRecentAnimDeprecated.cancelAnimation();
            this.mPreviousAppAnimDeprecated.cancelAnimation();
            this.mLandBackAnimDeprecated.cancelAnimation();
            this.mBackAnimNoBar.playAnimation();
            this.mHomeAnimNoBar.cancelAnimation();
            this.mRecentAnimNoBar.cancelAnimation();
            this.mPreviousAppAnimNoBar.cancelAnimation();
            this.mLandBackAnimNoBar.cancelAnimation();
        } else {
            this.mBackAnim.playAnimation();
            this.mHomeAnim.cancelAnimation();
            this.mRecentAnim.cancelAnimation();
            this.mPreviousAppAnim.cancelAnimation();
            this.mLandBackAnim.cancelAnimation();
            this.mBackAnimDeprecated.cancelAnimation();
            this.mHomeAnimDeprecated.cancelAnimation();
            this.mRecentAnimDeprecated.cancelAnimation();
            this.mPreviousAppAnimDeprecated.cancelAnimation();
            this.mLandBackAnimDeprecated.cancelAnimation();
            this.mBackAnimNoBar.cancelAnimation();
            this.mHomeAnimNoBar.cancelAnimation();
            this.mRecentAnimNoBar.cancelAnimation();
            this.mPreviousAppAnimNoBar.cancelAnimation();
            this.mLandBackAnimNoBar.cancelAnimation();
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
            OPPhoneControlWayCategory.this.playCurrentPageAnim(i);
            OPPhoneControlWayCategory.this.mCurrIndex = i;
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
            OPPhoneControlWayCategory.this.mPageIndicator.setLocation(((float) i) + f);
        }
    }
}
