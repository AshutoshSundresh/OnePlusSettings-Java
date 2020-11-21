package com.oneplus.settings.quicklaunch;

import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class OPPagerAdapter extends PagerAdapter {
    private Fragment mCurrentItem;
    private final FragmentManager mFragmentManager;
    private List<Fragment> mFragments = new ArrayList();
    private FragmentTransaction mTransaction = null;

    public OPPagerAdapter(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }

    public void updateData(List<Fragment> list) {
        if (list != null) {
            this.mFragments = list;
        } else {
            this.mFragments.clear();
        }
        notifyDataSetChanged();
    }

    private Fragment getFragment(int i) {
        return this.mFragments.get(i);
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        return this.mFragments.size();
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getItemPosition(Object obj) {
        for (int i = 0; i < this.mFragments.size(); i++) {
            if (this.mFragments.get(i) == obj) {
                return i;
            }
        }
        return -2;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public Object instantiateItem(ViewGroup viewGroup, int i) {
        if (this.mTransaction == null) {
            this.mTransaction = this.mFragmentManager.beginTransaction();
        }
        Fragment fragment = getFragment(i);
        this.mTransaction.show(fragment);
        fragment.setUserVisibleHint(fragment == this.mCurrentItem);
        return fragment;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        if (this.mTransaction == null) {
            this.mTransaction = this.mFragmentManager.beginTransaction();
        }
        this.mTransaction.hide((Fragment) obj);
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void finishUpdate(ViewGroup viewGroup) {
        FragmentTransaction fragmentTransaction = this.mTransaction;
        if (fragmentTransaction != null) {
            fragmentTransaction.commitAllowingStateLoss();
            this.mTransaction = null;
            this.mFragmentManager.executePendingTransactions();
        }
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public boolean isViewFromObject(View view, Object obj) {
        return ((Fragment) obj).getView() == view;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
        if (this.mFragments.get(i) != this.mCurrentItem) {
            this.mCurrentItem = this.mFragments.get(i);
        }
    }
}
