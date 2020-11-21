package com.oneplus.settings.better;

import android.app.ActionBar;
import android.app.AppOpsManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.BaseActivity;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPGameAndReadPickActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private List<OPAppModel> mAppList = new ArrayList();
    private ListView mAppListView;
    private AppOpsManager mAppOpsManager;
    private int mAppType;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.oneplus.settings.better.OPGameAndReadPickActivity.AnonymousClass1 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (OPGameAndReadPickActivity.this.mOPGameAndReadPickAdapter != null && OPGameAndReadPickActivity.this.mOPApplicationLoader != null) {
                OPGameAndReadPickActivity.this.mAppList.clear();
                OPGameAndReadPickActivity.this.mAppList.addAll(OPGameAndReadPickActivity.this.mOPApplicationLoader.getAppListByType(message.what));
                OPGameAndReadPickActivity.this.mOPGameAndReadPickAdapter.setData(OPGameAndReadPickActivity.this.mAppList);
                OPGameAndReadPickActivity.this.mOPGameAndReadPickAdapter.setAppType(OPGameAndReadPickActivity.this.mAppType);
                View findViewById = OPGameAndReadPickActivity.this.findViewById(C0010R$id.op_empty_list_tips_view);
                if (OPGameAndReadPickActivity.this.mAppList.isEmpty()) {
                    findViewById.setVisibility(0);
                    OPGameAndReadPickActivity.this.mAppListView.setEmptyView(findViewById);
                }
            }
        }
    };
    private View mLoadingContainer;
    private OPApplicationLoader mOPApplicationLoader;
    private OPGameAndReadPickAdapter mOPGameAndReadPickAdapter;
    private PackageManager mPackageManager;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.op_game_read_app_list_activity);
        this.mAppType = getIntent().getIntExtra("op_load_app_tyep", 0);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            int i = this.mAppType;
            if (i == 1004) {
                actionBar.setTitle(getString(C0017R$string.oneplus_game_mode_app_list));
            } else if (i == 1003) {
                actionBar.setTitle(getString(C0017R$string.oneplus_read_mode_app_list));
            } else if (i == 1001) {
                actionBar.setTitle(getString(C0017R$string.oneplus_app_locker_add_apps));
            }
        }
        this.mAppOpsManager = (AppOpsManager) getSystemService("appops");
        this.mPackageManager = getPackageManager();
        OPApplicationLoader oPApplicationLoader = new OPApplicationLoader(this, this.mAppOpsManager, this.mPackageManager);
        this.mOPApplicationLoader = oPApplicationLoader;
        oPApplicationLoader.setAppType(this.mAppType);
        initView();
    }

    private void initView() {
        this.mAppListView = (ListView) findViewById(C0010R$id.op_app_list);
        OPGameAndReadPickAdapter oPGameAndReadPickAdapter = new OPGameAndReadPickAdapter(this, this.mAppList);
        this.mOPGameAndReadPickAdapter = oPGameAndReadPickAdapter;
        this.mAppListView.setAdapter((ListAdapter) oPGameAndReadPickAdapter);
        this.mAppListView.setOnItemClickListener(this);
        View findViewById = findViewById(C0010R$id.loading_container);
        this.mLoadingContainer = findViewById;
        this.mOPApplicationLoader.setmLoadingContainer(findViewById);
        this.mOPApplicationLoader.loadSelectedGameOrReadAppMap(this.mAppType);
        this.mOPApplicationLoader.initData(2, this.mHandler);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        OPAppModel oPAppModel = (OPAppModel) this.mAppListView.getItemAtPosition(i);
        boolean z = !this.mOPGameAndReadPickAdapter.getSelected(i);
        this.mOPGameAndReadPickAdapter.setSelected(i, z);
        this.mAppOpsManager.setMode(this.mAppType, oPAppModel.getUid(), oPAppModel.getPkgName(), !z);
        StringBuilder sb = new StringBuilder(OPUtils.getGameModeAppListString(this));
        String gameModeAppString = OPUtils.getGameModeAppString(oPAppModel);
        if (OPUtils.isInRemovedGameAppListString(this, oPAppModel)) {
            oPAppModel.setEditMode(true);
        }
        if (oPAppModel.isEditMode()) {
            if (z != 0) {
                int indexOf = sb.indexOf(gameModeAppString);
                if (indexOf != -1) {
                    sb.delete(indexOf, gameModeAppString.length() + indexOf);
                }
            } else {
                sb.append(gameModeAppString);
            }
            OPUtils.saveGameModeRemovedAppLisStrings(this, sb.toString());
        }
    }
}
