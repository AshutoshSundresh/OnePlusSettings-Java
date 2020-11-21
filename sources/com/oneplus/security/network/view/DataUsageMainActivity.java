package com.oneplus.security.network.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.google.android.collect.Lists;
import com.oneplus.security.SecureService;
import com.oneplus.security.network.OperatorInfoUtils;
import com.oneplus.security.network.operator.InvalidOperatorDataModel;
import com.oneplus.security.network.operator.OperatorDataModelFactory;
import com.oneplus.security.network.operator.OperatorModelInterface;
import com.oneplus.security.network.operator.OperatorPackageUsageUpdater;
import com.oneplus.security.network.simcard.SimStateListener;
import com.oneplus.security.network.simcard.SimcardDataModel;
import com.oneplus.security.network.simcard.SimcardDataModelInterface;
import com.oneplus.security.network.simcard.SimcardStateManager;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.security.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class DataUsageMainActivity extends BaseTabActivity implements SimStateListener, OperatorPackageUsageUpdater {
    private boolean isActivityShowing = false;
    private int mCurrentDataSlotId = -1;
    private OperatorModelInterface mInvalidDataModel;
    private OperatorModelInterface mOperatorDataModel;
    private OperatorModelInterface mOperatorDataModelSimOne;
    private OperatorModelInterface mOperatorDataModelSimTwo;
    private SimcardDataModelInterface mSimcardDataModel;

    @Override // com.oneplus.security.network.simcard.SimStateListener
    public void onSimStateChanged(String str) {
    }

    @Override // com.oneplus.security.network.view.BaseTabActivity
    public void onTabPageSelected(int i) {
        super.onTabPageSelected(i);
        LogUtils.d("DataUsageMainActivity", "onTabPageSelected");
        Fragment currentFragment = getCurrentFragment();
        if ((currentFragment instanceof DataUsagePrefFragment) && currentFragment.isAdded() && ((DataUsagePrefFragment) currentFragment).ismNeedHeadView()) {
            int currentSlotId = ((DataUsagePrefFragment) getCurrentFragment()).getCurrentSlotId();
            this.mCurrentDataSlotId = currentSlotId;
            OperatorInfoUtils.setCurrentDisplayingSlotId(this, currentSlotId);
            refreshDataUsageUI(currentFragment);
        }
    }

    public void refreshDataUsageUI(Fragment fragment) {
        LogUtils.d("DataUsageMainActivity", "refreshDataUsageUI fragment = " + fragment);
        if (fragment == null) {
            fragment = getCurrentFragment();
        }
        if (fragment != getCurrentFragment()) {
            LogUtils.d("DataUsageMainActivity", "The fragment is not current selected fragment,no need to do any thing");
        } else if (fragment instanceof DataUsagePrefFragment) {
            DataUsagePrefFragment dataUsagePrefFragment = (DataUsagePrefFragment) fragment;
            if (dataUsagePrefFragment.ismNeedHeadView()) {
                int currentSlotId = ((DataUsagePrefFragment) getCurrentFragment()).getCurrentSlotId();
                LogUtils.d("DataUsageMainActivity", "dataUsagePrefFragment = " + dataUsagePrefFragment.isAdded());
                requestDataUsageUpdate(currentSlotId);
            }
        }
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.security.network.view.BaseTabActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        initData();
        super.onCreate(bundle);
        SecureService.startService(getApplicationContext());
        if (!(this.mOperatorDataModelSimOne == null || this.mOperatorDataModelSimTwo == null)) {
            int i = 0;
            try {
                i = getIntent().getIntExtra("select_tab", 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.mCurrentDataSlotId = i;
            OperatorInfoUtils.setCurrentDisplayingSlotId(this, i);
            setSelectTab(i);
        }
        int i2 = -1;
        try {
            i2 = getIntent().getIntExtra("tracker_event", -1);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        LogUtils.i("DataUsageMainActivity", "call this activity from trackerEvent:" + i2);
        if (i2 == 0) {
            Utils.sendAppTracker("settings_data", 1);
        } else if (i2 == 1) {
            Utils.sendAppTracker("widget_data", 1);
        } else if (i2 == 2) {
            Utils.sendAppTracker("systemui_data", 1);
        } else if (i2 == 3) {
            Utils.sendAppTracker("settings_shortcut_data", 1);
        }
    }

    public void initData() {
        this.mSimcardDataModel = SimcardDataModel.getInstance(getApplicationContext());
        this.mInvalidDataModel = InvalidOperatorDataModel.getInstance();
        configOperatorDataModels();
        findOutCurrentUsingSimSlot();
    }

    private boolean isSlotEffective() {
        return this.mSimcardDataModel.isSlotSimInserted(0) || this.mSimcardDataModel.isSlotSimInserted(1);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        registerListener();
        refreshDataUsageUI(null);
    }

    private void registerListener() {
        this.isActivityShowing = true;
        if (this.mOperatorDataModel == this.mInvalidDataModel && isSlotEffective()) {
            LogUtils.d("DataUsageMainActivity", "Slot status change , no sim card -> sim card, need refresh");
            restartActivity();
        } else if (this.mOperatorDataModel == this.mInvalidDataModel || isSlotEffective()) {
            registerOperatorValueListeners();
            this.mSimcardDataModel.registerSimStateListener(this);
        } else {
            LogUtils.d("DataUsageMainActivity", "Slot status change 2, sim card -> no sim card, need refresh");
            restartActivity();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        super.onPause();
        this.isActivityShowing = false;
        this.mSimcardDataModel.removeSimStateListener(this);
        unregisterOperatorValueListeners();
        if (SimcardStateManager.getShouldAlertSimcardHasPopedOut(this, this.mCurrentDataSlotId)) {
            SimcardStateManager.setShouldAlertSimcardHasPopedOut(this, false, this.mCurrentDataSlotId);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        clearExistingOperatorDataModels();
        OperatorModelInterface operatorModelInterface = this.mInvalidDataModel;
        if (operatorModelInterface != null) {
            operatorModelInterface.clearData();
            this.mInvalidDataModel = null;
        }
        super.onDestroy();
    }

    @Override // com.oneplus.security.network.view.BaseTabActivity
    public List<Fragment> getFragmentList() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.isEmpty()) {
            fragments = Lists.newArrayList();
            if (this.mOperatorDataModelSimOne != null) {
                DataUsagePrefFragment dataUsagePrefFragment = new DataUsagePrefFragment(this, C0019R$xml.data_usage_simcard_prefs, true, 0);
                dataUsagePrefFragment.setSupportSdk(this.mSimcardDataModel.isSlotOperatorSupportedBySdk(0));
                fragments.add(dataUsagePrefFragment);
            }
            if (this.mOperatorDataModelSimTwo != null) {
                DataUsagePrefFragment dataUsagePrefFragment2 = new DataUsagePrefFragment(this, C0019R$xml.data_usage_simcard_prefs, true, 1);
                dataUsagePrefFragment2.setSupportSdk(this.mSimcardDataModel.isSlotOperatorSupportedBySdk(1));
                fragments.add(dataUsagePrefFragment2);
            }
            if (this.mOperatorDataModelSimTwo == null && this.mOperatorDataModelSimOne == null) {
                fragments.add(new DataUsagePrefFragment(this, C0019R$xml.data_usage_simcard_prefs, true, -1));
            }
            fragments.add(new DataUsagePrefFragment(this, C0019R$xml.data_usage_wlan_prefs));
        }
        return fragments;
    }

    @Override // com.oneplus.security.network.view.BaseTabActivity
    public List<String> getTabTitle() {
        ArrayList arrayList = new ArrayList();
        if (this.mOperatorDataModelSimOne != null) {
            arrayList.add(this.mSimcardDataModel.getSlotOperatorName(0).toUpperCase());
        }
        if (this.mOperatorDataModelSimTwo != null) {
            arrayList.add(this.mSimcardDataModel.getSlotOperatorName(1).toUpperCase());
        }
        if (this.mOperatorDataModelSimTwo == null && this.mOperatorDataModelSimOne == null) {
            arrayList.add(getString(C0017R$string.data_usage_no_simcard).toUpperCase());
        }
        arrayList.add(getString(C0017R$string.data_usage_tab_wifi));
        return arrayList;
    }

    private void requestDataUsageUpdate(int i) {
        LogUtils.d("DataUsageMainActivity", "requestDataUsageUpdate slotId:" + i);
        OperatorModelInterface operatorModelInterface = this.mOperatorDataModel;
        if (operatorModelInterface != null) {
            operatorModelInterface.requesetPkgMonthlyUsageAndTotalInByte(i);
        }
    }

    private void registerOperatorValueListeners() {
        OperatorModelInterface operatorModelInterface = this.mOperatorDataModelSimOne;
        if (operatorModelInterface != null) {
            operatorModelInterface.addTrafficUsageUpdater(this);
            if (this.mSimcardDataModel.isSlotSimInserted(0)) {
                this.mOperatorDataModelSimOne.addQueryResultListener(0);
            }
        }
        OperatorModelInterface operatorModelInterface2 = this.mOperatorDataModelSimTwo;
        if (operatorModelInterface2 != null) {
            operatorModelInterface2.addTrafficUsageUpdater(this);
            if (this.mSimcardDataModel.isSlotSimInserted(1)) {
                this.mOperatorDataModelSimTwo.addQueryResultListener(1);
            }
        }
        OperatorModelInterface operatorModelInterface3 = this.mInvalidDataModel;
        if (operatorModelInterface3 != null) {
            operatorModelInterface3.addTrafficUsageUpdater(this);
        }
    }

    private void configOperatorDataModels() {
        boolean z;
        boolean z2 = false;
        if (this.mSimcardDataModel.isSlotSimInserted(0)) {
            this.mOperatorDataModelSimOne = OperatorDataModelFactory.getOperatorDataModel(getApplicationContext());
            z = true;
        } else {
            LogUtils.d("DataUsageMainActivity", "no sim card is inserted in slot 1.");
            z = false;
        }
        if (this.mSimcardDataModel.isSlotSimInserted(1)) {
            this.mOperatorDataModelSimTwo = OperatorDataModelFactory.getOperatorDataModel(getApplicationContext());
            z2 = true;
        } else {
            LogUtils.d("DataUsageMainActivity", "no sim card is inserted in slot 2.");
        }
        if (!z && !z2) {
            LogUtils.d("DataUsageMainActivity", "none effective sim card inserted, use null model");
            this.mOperatorDataModel = this.mInvalidDataModel;
        }
    }

    private void clearExistingOperatorDataModels() {
        OperatorModelInterface operatorModelInterface = this.mOperatorDataModelSimOne;
        if (operatorModelInterface != null) {
            operatorModelInterface.clearData();
            this.mOperatorDataModelSimOne = null;
        }
        OperatorModelInterface operatorModelInterface2 = this.mOperatorDataModelSimTwo;
        if (operatorModelInterface2 != null) {
            operatorModelInterface2.clearData();
            this.mOperatorDataModelSimTwo = null;
        }
    }

    private void findOutCurrentUsingSimSlot() {
        int currentDisplayingSlotId = OperatorInfoUtils.getCurrentDisplayingSlotId(this);
        LogUtils.d("DataUsageMainActivity", "findOutCurrentUsingSimSlot cachedSlotId = " + currentDisplayingSlotId);
        if (-1 == currentDisplayingSlotId || !this.mSimcardDataModel.isSlotSimInserted(currentDisplayingSlotId)) {
            LogUtils.d("DataUsageMainActivity", "current slot id is " + this.mSimcardDataModel.getCurrentTrafficRunningSlotId());
            if (this.mSimcardDataModel.getCurrentTrafficRunningSlotId() == 0 && this.mSimcardDataModel.isSlotSimInserted(0)) {
                this.mCurrentDataSlotId = 0;
                this.mOperatorDataModel = this.mOperatorDataModelSimOne;
                LogUtils.d("DataUsageMainActivity", "assign model one");
            } else if (1 != this.mSimcardDataModel.getCurrentTrafficRunningSlotId() || !this.mSimcardDataModel.isSlotSimInserted(1)) {
                this.mCurrentDataSlotId = -1;
                this.mOperatorDataModel = this.mInvalidDataModel;
                LogUtils.d("DataUsageMainActivity", "assign nothing");
            } else {
                this.mCurrentDataSlotId = 1;
                this.mOperatorDataModel = this.mOperatorDataModelSimTwo;
                LogUtils.d("DataUsageMainActivity", "assign model two");
            }
            OperatorInfoUtils.setCurrentDisplayingSlotId(this, this.mCurrentDataSlotId);
            return;
        }
        this.mCurrentDataSlotId = currentDisplayingSlotId;
        if (currentDisplayingSlotId == 0 && this.mOperatorDataModelSimOne != null) {
            LogUtils.d("DataUsageMainActivity", "use cached sim card one.");
            this.mOperatorDataModel = this.mOperatorDataModelSimOne;
        } else if (1 != currentDisplayingSlotId || this.mOperatorDataModelSimTwo == null) {
            LogUtils.e("DataUsageMainActivity", "this thing should never happen since we only save effective sim id");
        } else {
            LogUtils.d("DataUsageMainActivity", "use cached sim card two.");
            this.mOperatorDataModel = this.mOperatorDataModelSimTwo;
        }
    }

    private void unregisterOperatorValueListeners() {
        LogUtils.d("DataUsageMainActivity", "unregister listeners.");
        OperatorModelInterface operatorModelInterface = this.mOperatorDataModelSimOne;
        if (operatorModelInterface != null) {
            operatorModelInterface.removeTrafficUsageUpdater(this);
            if (this.mSimcardDataModel.isSlotSimInserted(0)) {
                this.mOperatorDataModelSimOne.removeQueryResultListener(0);
            }
        }
        OperatorModelInterface operatorModelInterface2 = this.mOperatorDataModelSimTwo;
        if (operatorModelInterface2 != null) {
            operatorModelInterface2.removeTrafficUsageUpdater(this);
            if (this.mSimcardDataModel.isSlotSimInserted(1)) {
                this.mOperatorDataModelSimTwo.removeQueryResultListener(1);
            }
        }
        OperatorModelInterface operatorModelInterface3 = this.mInvalidDataModel;
        if (operatorModelInterface3 != null) {
            operatorModelInterface3.removeTrafficUsageUpdater(this);
        }
    }

    @Override // com.oneplus.security.network.operator.OperatorPackageUsageUpdater
    public void onTrafficTotalAndUsedUpdate(long j, long j2, int i) {
        LogUtils.i("DataUsageMainActivity", "onTrafficTotalAndUsedUpdate totalByte:" + j + ",usedByte:" + j2 + ",slotId:" + i + ",mCurrentDataSlotId:" + this.mCurrentDataSlotId);
        if (isFinishing() || !this.isActivityShowing) {
            LogUtils.i("DataUsageMainActivity", "skip onTrafficTotalAndUsedUpdate");
        } else if (i == this.mCurrentDataSlotId) {
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment instanceof DataUsagePrefFragment) {
                DataUsagePrefFragment dataUsagePrefFragment = (DataUsagePrefFragment) currentFragment;
                LogUtils.d("DataUsageMainActivity", "onTrafficTotalAndUsedUpdate dataUsagePrefFragment = " + dataUsagePrefFragment.isAdded());
                if (dataUsagePrefFragment.ismNeedHeadView()) {
                    dataUsagePrefFragment.animateUpdateMonthlyRemainingData(j, j2);
                }
            }
        }
    }

    @Override // com.oneplus.security.network.simcard.SimStateListener
    public void onSimOperatorCodeChanged(int i, String str) {
        LogUtils.d("DataUsageMainActivity", "effective operator code loaded " + i + " value " + str);
        restartActivity();
    }

    /* access modifiers changed from: protected */
    public void restartActivity() {
        if (!this.isActivityShowing) {
            LogUtils.d("DataUsageMainActivity", "restartActivity isActivityShowing is false.");
            return;
        }
        Intent intent = getIntent();
        intent.setFlags(67108864);
        overridePendingTransition(0, 0);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
