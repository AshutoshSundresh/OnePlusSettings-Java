package com.oneplus.settings.gestures;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.BaseActivity;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPGestureAppListSettings extends BaseActivity implements AdapterView.OnItemClickListener {
    private List<OPAppModel> mDefaultGestureAppList = new ArrayList();
    private List<OPAppModel> mGestureAppList = new ArrayList();
    private ListView mGestureAppListView;
    private String mGestureKey;
    private String mGesturePackageName;
    private String mGestureSummary;
    private String mGestureTitle;
    private int mGestureUid;
    private int mGestureValueIndex;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.oneplus.settings.gestures.OPGestureAppListSettings.AnonymousClass1 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 0 && OPGestureAppListSettings.this.mOPGestureAppAdapter != null && OPGestureAppListSettings.this.mOPApplicationLoader != null) {
                OPGestureAppListSettings.this.mGestureAppList.clear();
                OPGestureAppListSettings.this.mGestureAppList.addAll(OPGestureAppListSettings.this.mDefaultGestureAppList);
                OPGestureAppListSettings.this.mGestureAppList.addAll(OPGestureAppListSettings.this.mOPApplicationLoader.getAllAppList());
                OPGestureAppListSettings.this.mOPGestureAppAdapter.setData(OPGestureAppListSettings.this.mGestureAppList);
                OPGestureAppListSettings.this.mGestureAppListView.setSelection(OPGestureAppListSettings.this.getSelectionPosition());
            }
        }
    };
    private View mLoadingContainer;
    private OPApplicationLoader mOPApplicationLoader;
    private OPGestureAppAdapter mOPGestureAppAdapter;
    private PackageManager mPackageManager;

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private int getSelectionPosition() {
        this.mGestureSummary = OPGestureUtils.getGestureSummarybyGestureKey(this, this.mGestureKey);
        this.mGesturePackageName = OPGestureUtils.getGesturePackageName(this, this.mGestureKey);
        for (int i = 0; i < this.mGestureAppList.size(); i++) {
            if (i < 6) {
                if (!this.mGestureSummary.equals(this.mGestureAppList.get(i).getLabel())) {
                }
            } else if (!this.mGesturePackageName.equals(this.mGestureAppList.get(i).getPkgName())) {
            }
            return i;
        }
        return 0;
    }

    private List<OPAppModel> createDefaultAppList() {
        this.mDefaultGestureAppList.clear();
        OPAppModel oPAppModel = new OPAppModel("", getString(C0017R$string.oneplus_draw_gesture_start_none), "", 0, false);
        OPAppModel oPAppModel2 = new OPAppModel("", getString(C0017R$string.oneplus_gestures_open_camera), "", 0, false);
        OPAppModel oPAppModel3 = new OPAppModel("", getString(C0017R$string.oneplus_gestures_open_front_camera), "", 0, false);
        OPAppModel oPAppModel4 = new OPAppModel("", getString(C0017R$string.oneplus_gestures_take_video), "", 0, false);
        OPAppModel oPAppModel5 = new OPAppModel("", getString(C0017R$string.oneplus_gestures_open_flashlight, new Object[]{Boolean.FALSE}), "", 0, false);
        OPAppModel oPAppModel6 = new OPAppModel("", getString(C0017R$string.hardware_keys_action_shelf), "", 0, false);
        this.mDefaultGestureAppList.add(oPAppModel);
        this.mDefaultGestureAppList.add(oPAppModel2);
        this.mDefaultGestureAppList.add(oPAppModel3);
        this.mDefaultGestureAppList.add(oPAppModel4);
        this.mDefaultGestureAppList.add(oPAppModel5);
        if (OPUtils.methodIsMigrated(SettingsBaseApplication.mApplication)) {
            this.mDefaultGestureAppList.add(oPAppModel6);
        }
        return this.mDefaultGestureAppList;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.op_gesture_app_list_activity);
        Intent intent = getIntent();
        this.mGestureKey = intent.getStringExtra("op_gesture_key");
        this.mGestureTitle = intent.getStringExtra("op_gesture_action");
        this.mGestureValueIndex = OPGestureUtils.getIndexByGestureValueKey(this.mGestureKey);
        this.mGestureSummary = OPGestureUtils.getGestureSummarybyGestureKey(this, this.mGestureKey);
        String gesturePacakgeUid = OPGestureUtils.getGesturePacakgeUid(this, this.mGestureKey);
        this.mGestureUid = TextUtils.isEmpty(gesturePacakgeUid) ? -1 : Integer.valueOf(gesturePacakgeUid).intValue();
        this.mGesturePackageName = OPGestureUtils.getGesturePackageName(this, this.mGestureKey);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(this.mGestureTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        initView();
    }

    private void initView() {
        ListView listView = (ListView) findViewById(C0010R$id.op_gesture_app_list);
        this.mGestureAppListView = listView;
        listView.setOnItemClickListener(this);
        this.mPackageManager = getPackageManager();
        this.mOPApplicationLoader = new OPApplicationLoader(this, this.mPackageManager);
        View findViewById = findViewById(C0010R$id.loading_container);
        this.mLoadingContainer = findViewById;
        this.mOPApplicationLoader.setmLoadingContainer(findViewById);
        this.mOPApplicationLoader.setNeedLoadWorkProfileApps(false);
        createDefaultAppList();
        OPGestureAppAdapter oPGestureAppAdapter = new OPGestureAppAdapter(this, this.mPackageManager, this.mGestureSummary);
        this.mOPGestureAppAdapter = oPGestureAppAdapter;
        this.mGestureAppListView.setAdapter((ListAdapter) oPGestureAppAdapter);
        this.mOPGestureAppAdapter.setDefaultNum(this.mDefaultGestureAppList.size());
        initData();
    }

    private void initData() {
        this.mOPApplicationLoader.initData(0, this.mHandler);
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0056  */
    /* JADX WARNING: Removed duplicated region for block: B:28:? A[RETURN, SYNTHETIC] */
    @Override // android.widget.AdapterView.OnItemClickListener
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onItemClick(android.widget.AdapterView<?> r1, android.view.View r2, int r3, long r4) {
        /*
            r0 = this;
            int r1 = r0.mGestureValueIndex
            com.oneplus.settings.gestures.OPGestureUtils.set1(r0, r1)
            r1 = 1
            if (r3 == 0) goto L_0x004d
            if (r3 == r1) goto L_0x0049
            r2 = 2
            if (r3 == r2) goto L_0x0045
            r2 = 3
            if (r3 == r2) goto L_0x0041
            r2 = 4
            if (r3 == r2) goto L_0x003d
            r2 = 5
            if (r3 == r2) goto L_0x0017
            goto L_0x0023
        L_0x0017:
            android.app.Application r2 = com.oneplus.settings.SettingsBaseApplication.mApplication
            boolean r2 = com.oneplus.settings.utils.OPUtils.methodIsMigrated(r2)
            if (r2 == 0) goto L_0x0023
            r0.openShelf()
            goto L_0x0050
        L_0x0023:
            android.widget.ListView r2 = r0.mGestureAppListView
            java.lang.Object r2 = r2.getItemAtPosition(r3)
            com.oneplus.settings.better.OPAppModel r2 = (com.oneplus.settings.better.OPAppModel) r2
            java.lang.String r3 = r2.getPkgName()
            boolean r3 = com.oneplus.settings.gestures.OPGestureUtils.hasShortCuts(r0, r3)
            if (r3 == 0) goto L_0x0039
            r0.gotoShortCutsPickPage(r2)
            goto L_0x0051
        L_0x0039:
            r0.openApps(r2)
            goto L_0x0050
        L_0x003d:
            r0.openFlashLight()
            goto L_0x0050
        L_0x0041:
            r0.openTakeVideo()
            goto L_0x0050
        L_0x0045:
            r0.openFrontCamera()
            goto L_0x0050
        L_0x0049:
            r0.openBackCamera()
            goto L_0x0050
        L_0x004d:
            r0.doNothing()
        L_0x0050:
            r1 = 0
        L_0x0051:
            r0.refreshList()
            if (r1 != 0) goto L_0x0059
            r0.finish()
        L_0x0059:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.gestures.OPGestureAppListSettings.onItemClick(android.widget.AdapterView, android.view.View, int, long):void");
    }

    private void refreshList() {
        this.mGestureSummary = OPGestureUtils.getGestureSummarybyGestureKey(this, this.mGestureKey);
        this.mGesturePackageName = OPGestureUtils.getGesturePackageName(this, this.mGestureKey);
        String shortCutIdByGestureKey = OPGestureUtils.getShortCutIdByGestureKey(this, this.mGestureKey);
        boolean z = OPGestureUtils.hasShortCutsGesture(this, this.mGestureKey) && OPGestureUtils.hasShortCutsId(this, this.mGesturePackageName, shortCutIdByGestureKey);
        OPGestureAppAdapter oPGestureAppAdapter = this.mOPGestureAppAdapter;
        String str = this.mGestureSummary;
        String str2 = this.mGesturePackageName;
        oPGestureAppAdapter.setSelectedItem(str, str2, this.mGestureUid, z, OPGestureUtils.getShortCutsNameByID(this, str2, shortCutIdByGestureKey));
    }

    private void doNothing() {
        OPGestureUtils.set0(this, this.mGestureValueIndex);
        Settings.System.putString(getContentResolver(), this.mGestureKey, "");
    }

    private void openBackCamera() {
        Settings.System.putString(getContentResolver(), this.mGestureKey, "OpenCamera");
    }

    private void openFrontCamera() {
        Settings.System.putString(getContentResolver(), this.mGestureKey, "FrontCamera");
    }

    private void openTakeVideo() {
        Settings.System.putString(getContentResolver(), this.mGestureKey, "TakeVideo");
    }

    private void openFlashLight() {
        Settings.System.putString(getContentResolver(), this.mGestureKey, "OpenTorch");
    }

    private void openShelf() {
        Settings.System.putString(getContentResolver(), this.mGestureKey, "OpenShelf");
    }

    private void openApps(OPAppModel oPAppModel) {
        ContentResolver contentResolver = getContentResolver();
        String str = this.mGestureKey;
        Settings.System.putString(contentResolver, str, "OpenApp:" + oPAppModel.getPkgName() + ";" + oPAppModel.getUid());
    }

    private void gotoShortCutsPickPage(OPAppModel oPAppModel) {
        Intent intent = new Intent("oneplus.intent.action.ONEPLUS_GESTURE_SHORTCUT_LIST_ACTION");
        intent.putExtra("op_gesture_key", this.mGestureKey);
        intent.putExtra("op_gesture_package", oPAppModel.getPkgName());
        intent.putExtra("op_gesture_package_uid", oPAppModel.getUid());
        intent.putExtra("op_gesture_package_app", oPAppModel.getLabel());
        startActivityForResult(intent, 1);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        refreshList();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        super.onPause();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1 && i2 == -1) {
            finish();
        }
    }
}
