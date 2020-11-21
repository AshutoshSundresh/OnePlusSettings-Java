package com.oneplus.security.utils;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import java.lang.ref.WeakReference;

public class TabUtils {

    public interface OnSetupTabListener {
        void onSetupTab(int i, TabLayout.Tab tab);
    }

    public static void setupWithViewPager(TabLayout tabLayout, ViewPager viewPager, OnSetupTabListener onSetupTabListener, ViewPagerOnTabSelectedListener viewPagerOnTabSelectedListener) {
        int currentItem;
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter != null) {
            setTabsFromPagerAdapter(tabLayout, adapter, onSetupTabListener);
            viewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener((TabLayout.OnTabSelectedListener) viewPagerOnTabSelectedListener);
            if (adapter.getCount() > 0 && tabLayout.getSelectedTabPosition() != (currentItem = viewPager.getCurrentItem())) {
                tabLayout.getTabAt(currentItem).select();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
    }

    public static void setTabsFromPagerAdapter(TabLayout tabLayout, PagerAdapter pagerAdapter, OnSetupTabListener onSetupTabListener) {
        tabLayout.removeAllTabs();
        int count = pagerAdapter.getCount();
        for (int i = 0; i < count; i++) {
            TabLayout.Tab newTab = tabLayout.newTab();
            onSetupTabListener.onSetupTab(i, newTab);
            tabLayout.addTab(newTab);
        }
    }

    public static class TabLayoutOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private int mPendingSelection = -1;
        private int mScrollState;
        private final WeakReference<TabLayout> mTabLayoutRef;

        public TabLayoutOnPageChangeListener(TabLayout tabLayout) {
            this.mTabLayoutRef = new WeakReference<>(tabLayout);
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
            this.mScrollState = i;
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
            int i3;
            TabLayout tabLayout = this.mTabLayoutRef.get();
            if (tabLayout != null) {
                tabLayout.setScrollPosition(i, f, true);
                int i4 = this.mScrollState;
                if ((i4 == 0 || i4 == 2) && (i3 = this.mPendingSelection) != -1) {
                    tabLayout.getTabAt(i3).select();
                    this.mPendingSelection = -1;
                }
            }
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            this.mPendingSelection = i;
        }
    }

    public static class ViewPagerOnTabSelectedListener implements TabLayout.OnTabSelectedListener {
        private final ViewPager mViewPager;

        @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
        public void onTabReselected(TabLayout.Tab tab) {
        }

        @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        public ViewPagerOnTabSelectedListener(ViewPager viewPager) {
            this.mViewPager = viewPager;
        }

        @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
        public void onTabSelected(TabLayout.Tab tab) {
            this.mViewPager.setCurrentItem(tab.getPosition());
        }
    }
}
