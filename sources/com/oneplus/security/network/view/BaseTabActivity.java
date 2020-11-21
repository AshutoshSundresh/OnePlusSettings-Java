package com.oneplus.security.network.view;

import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.google.android.material.appbar.Appbar;
import com.google.android.material.tabs.TabLayout;
import com.oneplus.security.utils.TabUtils;
import com.oneplus.settings.BaseAppCompatActivity;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseTabActivity extends BaseAppCompatActivity {
    protected Fragment mCurrentFragment;
    protected List<Fragment> mFragmentList;
    protected TabLayout mTabLayout;
    protected TabsAdapter mTabsAdapter;
    protected ViewPager mViewPager;

    public abstract List<Fragment> getFragmentList();

    public abstract List<String> getTabTitle();

    public void onTabPageSelected(int i) {
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        ArrayList arrayList = new ArrayList();
        this.mFragmentList = arrayList;
        arrayList.addAll(getFragmentList());
        OPUtils.setLightNavigationBar(getWindow(), OPUtils.getThemeMode(getContentResolver()));
        super.onCreate(bundle);
        onSetContentView();
        this.mViewPager = (ViewPager) findViewById(C0010R$id.pager);
        this.mTabLayout = (TabLayout) findViewById(C0010R$id.tabs);
        Appbar appbar = (Appbar) findViewById(C0010R$id.action_bar);
        appbar.setTitle(getTitle());
        appbar.setDisplayHomeAsUpEnabled(true);
        appbar.setNavigationOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.security.network.view.BaseTabActivity.AnonymousClass1 */

            public void onClick(View view) {
                BaseTabActivity.this.onBackPressed();
            }
        });
        TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager());
        this.mTabsAdapter = tabsAdapter;
        this.mViewPager.setAdapter(tabsAdapter);
        TabUtils.setupWithViewPager(this.mTabLayout, this.mViewPager, new TabUtils.OnSetupTabListener() {
            /* class com.oneplus.security.network.view.BaseTabActivity.AnonymousClass2 */

            @Override // com.oneplus.security.utils.TabUtils.OnSetupTabListener
            public void onSetupTab(int i, TabLayout.Tab tab) {
                BaseTabActivity.this.setupTab(i, tab);
            }
        }, new TabUtils.ViewPagerOnTabSelectedListener(this.mViewPager) {
            /* class com.oneplus.security.network.view.BaseTabActivity.AnonymousClass3 */

            @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener, com.oneplus.security.utils.TabUtils.ViewPagerOnTabSelectedListener
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                BaseTabActivity baseTabActivity = BaseTabActivity.this;
                baseTabActivity.mCurrentFragment = baseTabActivity.mFragmentList.get(tab.getPosition());
                BaseTabActivity.this.onTabPageSelected(tab.getPosition());
            }
        });
    }

    public Fragment getCurrentFragment() {
        if (this.mCurrentFragment == null) {
            this.mCurrentFragment = this.mFragmentList.get(this.mViewPager.getCurrentItem());
        }
        return this.mCurrentFragment;
    }

    /* access modifiers changed from: protected */
    public void onSetContentView() {
        setContentView(C0012R$layout.activity_tab_default_fixed);
    }

    public void setSelectTab(int i) {
        this.mViewPager.setCurrentItem(i);
    }

    /* access modifiers changed from: protected */
    public void setupTab(int i, TabLayout.Tab tab) {
        tab.setText(this.mTabsAdapter.getPageTitle(i));
    }

    /* access modifiers changed from: package-private */
    public class TabsAdapter extends FragmentPagerAdapter {
        private List<String> titles;

        public TabsAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            List<String> tabTitle = BaseTabActivity.this.getTabTitle();
            this.titles = tabTitle;
            if (tabTitle == null) {
                this.titles = new ArrayList();
                int i = 0;
                while (i < BaseTabActivity.this.mFragmentList.size()) {
                    List<String> list = this.titles;
                    StringBuilder sb = new StringBuilder();
                    sb.append("tab");
                    i++;
                    sb.append(i);
                    list.add(sb.toString());
                }
            }
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public CharSequence getPageTitle(int i) {
            if (i >= this.titles.size()) {
                i = this.titles.size() - 1;
            }
            return this.titles.get(i);
        }

        @Override // androidx.fragment.app.FragmentPagerAdapter
        public Fragment getItem(int i) {
            if (i >= BaseTabActivity.this.mFragmentList.size()) {
                i = BaseTabActivity.this.mFragmentList.size() - 1;
            }
            return BaseTabActivity.this.mFragmentList.get(i);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return BaseTabActivity.this.mFragmentList.size();
        }
    }
}
