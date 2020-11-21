package com.oneplus.settings.product;

import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.List;

public class NovicePagerAdapter extends PagerAdapter {
    public List<View> mListViews = null;

    @Override // androidx.viewpager.widget.PagerAdapter
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public NovicePagerAdapter(List<View> list) {
        this.mListViews = list;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        try {
            ((ViewPager) viewGroup).removeView(this.mListViews.get(i));
        } catch (Exception unused) {
        }
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public Object instantiateItem(ViewGroup viewGroup, int i) {
        ((ViewPager) viewGroup).addView(this.mListViews.get(i), 0);
        return this.mListViews.get(i);
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        List<View> list = this.mListViews;
        if (list != null) {
            return list.size();
        }
        return 0;
    }
}
