package com.oneplus.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.google.android.material.indicator.PageIndicator;
import java.util.ArrayList;

public class OPScreenColorModeView extends FrameLayout {
    private static final int LAYOUT_RES_ID = C0012R$layout.op_screen_color_mode_preference;
    private static final int LAYOUT_RES_ID_2K = C0012R$layout.op_screen_color_mode_preference_2k;
    private int currIndex = 0;
    private Context mContext;
    private PageIndicator mPageIndicator;

    public OPScreenColorModeView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context);
    }

    public OPScreenColorModeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    public OPScreenColorModeView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        if (((WindowManager) context.getSystemService("window")).getDefaultDisplay().getWidth() == 1440) {
            setLayoutResource(LAYOUT_RES_ID_2K);
        } else {
            setLayoutResource(LAYOUT_RES_ID);
        }
        onBindViewHolder(this);
    }

    private void setLayoutResource(int i) {
        LayoutInflater.from(this.mContext).inflate(i, (ViewGroup) this, true);
    }

    public void onBindViewHolder(View view) {
        ViewPager viewPager = (ViewPager) view.findViewById(C0010R$id.whatsnew_viewpager);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        this.mPageIndicator = (PageIndicator) view.findViewById(C0010R$id.gesture_page_indicator);
        LayoutInflater from = LayoutInflater.from(this.mContext);
        View inflate = from.inflate(C0012R$layout.op_screen_image, (ViewGroup) null);
        ((ImageView) inflate.findViewById(C0010R$id.imageview)).setImageResource(C0008R$drawable.op_screen_show_1);
        View inflate2 = from.inflate(C0012R$layout.op_screen_image, (ViewGroup) null);
        ((ImageView) inflate2.findViewById(C0010R$id.imageview)).setImageResource(C0008R$drawable.op_screen_show_2);
        View inflate3 = from.inflate(C0012R$layout.op_screen_image, (ViewGroup) null);
        ((ImageView) inflate3.findViewById(C0010R$id.imageview)).setImageResource(C0008R$drawable.op_screen_show_3);
        final ArrayList arrayList = new ArrayList();
        arrayList.add(inflate);
        arrayList.add(inflate2);
        arrayList.add(inflate3);
        AnonymousClass1 r7 = new PagerAdapter(this) {
            /* class com.oneplus.settings.ui.OPScreenColorModeView.AnonymousClass1 */

            @Override // androidx.viewpager.widget.PagerAdapter
            public boolean isViewFromObject(View view, Object obj) {
                return view == obj;
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public int getCount() {
                return arrayList.size();
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public void destroyItem(View view, int i, Object obj) {
                ((ViewPager) view).removeView((View) arrayList.get(i));
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public Object instantiateItem(View view, int i) {
                ((ViewPager) view).addView((View) arrayList.get(i));
                return arrayList.get(i);
            }
        };
        viewPager.setAdapter(r7);
        viewPager.setCurrentItem(this.currIndex);
        this.mPageIndicator.setNumPages(r7.getCount());
        this.mPageIndicator.setLocation((float) viewPager.getCurrentItem());
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
        }

        public MyOnPageChangeListener() {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            OPScreenColorModeView.this.currIndex = i;
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
            OPScreenColorModeView.this.mPageIndicator.setLocation(((float) i) + f);
        }
    }
}
