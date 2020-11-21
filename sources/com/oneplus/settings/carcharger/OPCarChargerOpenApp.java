package com.oneplus.settings.carcharger;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.appcompat.app.ActionBar;
import com.android.settings.C0003R$array;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.BaseActivity;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPCarChargerOpenApp extends BaseActivity implements AdapterView.OnItemClickListener {
    private int hasRecommendedCount;
    private List<OPAppModel> mCarChargerAppsList = new ArrayList();
    private OPCarChargerOpenAppAdapter mCarChargerOpenAppAdapter;
    private ListView mCarChargerOpenAppListView;
    private List<OPAppModel> mCarChargerRecommendedAppsList = new ArrayList();
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.oneplus.settings.carcharger.OPCarChargerOpenApp.AnonymousClass1 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 0 && OPCarChargerOpenApp.this.mCarChargerOpenAppAdapter != null && OPCarChargerOpenApp.this.mOPApplicationLoader != null) {
                OPCarChargerOpenApp.this.mCarChargerAppsList.clear();
                OPCarChargerOpenApp.this.mCarChargerAppsList.addAll(OPCarChargerOpenApp.this.mCarChargerRecommendedAppsList);
                OPCarChargerOpenApp.this.mCarChargerAppsList.addAll(OPCarChargerOpenApp.this.mOPApplicationLoader.getAllAppList());
                OPCarChargerOpenApp.this.mCarChargerOpenAppAdapter.setData(OPCarChargerOpenApp.this.mCarChargerAppsList);
                OPCarChargerOpenApp.this.mCarChargerOpenAppAdapter.setHasRecommendedCount(OPCarChargerOpenApp.this.hasRecommendedCount);
                OPCarChargerOpenApp.this.mCarChargerOpenAppListView.setSelection(OPCarChargerOpenApp.this.getSelectionPosition());
            }
        }
    };
    private View mLoadingContainer;
    private OPApplicationLoader mOPApplicationLoader;
    private PackageManager mPackageManager;

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int getSelectionPosition() {
        String string = Settings.System.getString(getApplicationContext().getContentResolver(), "op_charger_mode_auto_open_app");
        for (int i = 0; i < this.mCarChargerAppsList.size(); i++) {
            if (string != null && string.equals(this.mCarChargerAppsList.get(i).getPkgName())) {
                return i;
            }
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.op_car_charger_open_app_list);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle(C0017R$string.oneplus_auto_open_specified_app);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeButtonEnabled(true);
        initView();
    }

    private void initView() {
        ListView listView = (ListView) findViewById(C0010R$id.op_car_charger_open_app_list);
        this.mCarChargerOpenAppListView = listView;
        listView.setOnItemClickListener(this);
        this.mPackageManager = getPackageManager();
        OPApplicationLoader oPApplicationLoader = new OPApplicationLoader(this, this.mPackageManager);
        this.mOPApplicationLoader = oPApplicationLoader;
        oPApplicationLoader.setAppType(80);
        View findViewById = findViewById(C0010R$id.loading_container);
        this.mLoadingContainer = findViewById;
        this.mOPApplicationLoader.setmLoadingContainer(findViewById);
        this.mOPApplicationLoader.setNeedLoadWorkProfileApps(false);
        createCarModeRecommendedAppsList();
        OPCarChargerOpenAppAdapter oPCarChargerOpenAppAdapter = new OPCarChargerOpenAppAdapter(this, this.mPackageManager);
        this.mCarChargerOpenAppAdapter = oPCarChargerOpenAppAdapter;
        this.mCarChargerOpenAppListView.setAdapter((ListAdapter) oPCarChargerOpenAppAdapter);
        initData();
    }

    private List<OPAppModel> createCarModeRecommendedAppsList() {
        this.mCarChargerRecommendedAppsList.clear();
        this.mCarChargerRecommendedAppsList.add(new OPAppModel("", getString(C0017R$string.oneplus_auto_open_app_none), "", 0, false));
        String[] stringArray = getResources().getStringArray(C0003R$array.op_car_mode_recommended_apps);
        for (int i = 0; i < stringArray.length; i++) {
            if (OPUtils.isAppExist(getApplicationContext(), stringArray[i])) {
                this.hasRecommendedCount++;
                OPAppModel oPAppModel = new OPAppModel(stringArray[i], OPUtils.getAppLabel(getApplicationContext(), stringArray[i]), "", 0, false);
                oPAppModel.setAppIcon(OPUtils.getAppIcon(getApplicationContext(), stringArray[i]));
                this.mCarChargerRecommendedAppsList.add(oPAppModel);
            }
        }
        return this.mCarChargerRecommendedAppsList;
    }

    private void initData() {
        this.mOPApplicationLoader.initData(0, this.mHandler);
    }

    private void refreshList() {
        this.mCarChargerOpenAppAdapter.setSelectedItem(Settings.System.getString(getApplicationContext().getContentResolver(), "op_care_charger_auto_open_app"));
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        refreshList();
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        OPAppModel oPAppModel = (OPAppModel) this.mCarChargerOpenAppListView.getItemAtPosition(i);
        Settings.System.putString(getApplicationContext().getContentResolver(), "op_care_charger_auto_open_app", oPAppModel.getPkgName());
        OPUtils.sendAppTracker("charge_app", oPAppModel.getPkgName());
        refreshList();
    }
}
