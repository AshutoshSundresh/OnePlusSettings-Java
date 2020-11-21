package com.oneplus.settings.quicklaunch;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.google.android.material.tabs.TabLayout;
import com.oneplus.settings.BaseAppCompatActivity;
import com.oneplus.settings.quicklaunch.TabUtils;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPQuickLaunchCategorySettings extends BaseAppCompatActivity implements ViewPager.OnPageChangeListener {
    private OPQuickLaunchAppFragment mApplicationFragment;
    private int mCurrentIndex = 0;
    private final List<String> mFragmentTitles = new ArrayList();
    private final List<Fragment> mFragments = new ArrayList();
    private OPPagerAdapter mPagerAdapter;
    private OPQuickLaunchShortCutFragment mShortcutFragment;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageScrollStateChanged(int i) {
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageScrolled(int i, float f, int i2) {
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        OPUtils.setLightNavigationBar(getWindow(), OPUtils.getThemeMode(getContentResolver()));
        setContentView(C0012R$layout.op_quick_launcher_category_settings);
        Toolbar toolbar = (Toolbar) findViewById(C0010R$id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.mViewPager = (ViewPager) findViewById(C0010R$id.op_quick_launch_category_viewpager);
        this.mTabLayout = (TabLayout) findViewById(C0010R$id.tabs);
        initFragments();
        initViewPager();
        initTabLayout();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    private void initViewPager() {
        OPPagerAdapter oPPagerAdapter = new OPPagerAdapter(getSupportFragmentManager());
        this.mPagerAdapter = oPPagerAdapter;
        this.mViewPager.setAdapter(oPPagerAdapter);
        this.mPagerAdapter.updateData(this.mFragments);
        this.mViewPager.addOnPageChangeListener(this);
    }

    private void initFragments() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        this.mFragmentTitles.clear();
        this.mFragments.clear();
        OPQuickLaunchShortCutFragment oPQuickLaunchShortCutFragment = (OPQuickLaunchShortCutFragment) supportFragmentManager.findFragmentByTag("directory_fragment");
        this.mShortcutFragment = oPQuickLaunchShortCutFragment;
        if (oPQuickLaunchShortCutFragment == null) {
            OPQuickLaunchShortCutFragment oPQuickLaunchShortCutFragment2 = new OPQuickLaunchShortCutFragment();
            this.mShortcutFragment = oPQuickLaunchShortCutFragment2;
            beginTransaction.add(C0010R$id.op_quick_launch_category_viewpager, oPQuickLaunchShortCutFragment2, "directory_fragment");
        }
        this.mFragments.add(this.mShortcutFragment);
        this.mFragmentTitles.add(getString(C0017R$string.oneplus_shortcuts_title));
        beginTransaction.hide(this.mShortcutFragment);
        OPQuickLaunchAppFragment oPQuickLaunchAppFragment = (OPQuickLaunchAppFragment) supportFragmentManager.findFragmentByTag("browse_fragment");
        this.mApplicationFragment = oPQuickLaunchAppFragment;
        if (oPQuickLaunchAppFragment == null) {
            OPQuickLaunchAppFragment oPQuickLaunchAppFragment2 = new OPQuickLaunchAppFragment();
            this.mApplicationFragment = oPQuickLaunchAppFragment2;
            beginTransaction.add(C0010R$id.op_quick_launch_category_viewpager, oPQuickLaunchAppFragment2, "browse_fragment");
        }
        this.mFragments.add(this.mApplicationFragment);
        this.mFragmentTitles.add(getString(C0017R$string.oneplus_apps_title));
        beginTransaction.hide(this.mApplicationFragment);
        beginTransaction.commitAllowingStateLoss();
        supportFragmentManager.executePendingTransactions();
    }

    private void initTabLayout() {
        TabUtils.setupWithViewPager(this.mTabLayout, this.mViewPager, new TabUtils.OnSetupTabListener() {
            /* class com.oneplus.settings.quicklaunch.OPQuickLaunchCategorySettings.AnonymousClass1 */

            @Override // com.oneplus.settings.quicklaunch.TabUtils.OnSetupTabListener
            public void onSetupTab(int i, TabLayout.Tab tab) {
                tab.setText((CharSequence) OPQuickLaunchCategorySettings.this.mFragmentTitles.get(i));
            }
        });
        this.mViewPager.setCurrentItem(this.mCurrentIndex);
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageSelected(int i) {
        this.mTabLayout.getTabAt(i).select();
    }
}
