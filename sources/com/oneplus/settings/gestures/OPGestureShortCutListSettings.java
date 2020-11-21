package com.oneplus.settings.gestures;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.oneplus.settings.BaseActivity;
import java.util.ArrayList;
import java.util.List;

public class OPGestureShortCutListSettings extends BaseActivity implements AdapterView.OnItemClickListener {
    private Drawable mAppDrawable;
    private ApplicationInfo mApplicationInfo;
    private List<OPGestureAppModel> mGestureAppList = new ArrayList();
    private String mGestureKey;
    private String mGesturePackage;
    private ListView mGestureShortcutListView;
    private String mGestureSummary;
    private int mGestureUid;
    private OPGestureShortcutsAdapter mOPGestureShortcutsAdapter;
    private PackageManager mPackageManager;
    private List<ShortcutInfo> mShortcutInfo;
    private String mTitle;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.op_gesture_app_list_activity);
        Intent intent = getIntent();
        this.mGestureKey = intent.getStringExtra("op_gesture_key");
        this.mGesturePackage = intent.getStringExtra("op_gesture_package");
        this.mGestureUid = intent.getIntExtra("op_gesture_package_uid", -1);
        this.mTitle = intent.getStringExtra("op_gesture_package_app");
        PackageManager packageManager = getPackageManager();
        this.mPackageManager = packageManager;
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.mGesturePackage, 0);
            this.mApplicationInfo = applicationInfo;
            this.mAppDrawable = applicationInfo.loadIcon(this.mPackageManager);
        } catch (PackageManager.NameNotFoundException unused) {
        }
        initView();
    }

    private void initView() {
        ListView listView = (ListView) findViewById(C0010R$id.op_gesture_app_list);
        this.mGestureShortcutListView = listView;
        listView.setOnItemClickListener(this);
    }

    private void initData() {
        LauncherApps launcherApps = (LauncherApps) getSystemService("launcherapps");
        List<ShortcutInfo> loadShortCuts = OPGestureUtils.loadShortCuts(this, this.mGesturePackage);
        this.mShortcutInfo = loadShortCuts;
        if (loadShortCuts != null) {
            this.mGestureAppList.clear();
            OPGestureAppModel oPGestureAppModel = new OPGestureAppModel(this.mGesturePackage, this.mTitle, "", 0);
            oPGestureAppModel.setAppIcon(this.mAppDrawable);
            this.mGestureAppList.add(oPGestureAppModel);
            int size = this.mShortcutInfo.size();
            for (int i = 0; i < size; i++) {
                ShortcutInfo shortcutInfo = this.mShortcutInfo.get(i);
                CharSequence longLabel = shortcutInfo.getLongLabel();
                if (TextUtils.isEmpty(longLabel)) {
                    longLabel = shortcutInfo.getShortLabel();
                }
                if (TextUtils.isEmpty(longLabel)) {
                    longLabel = shortcutInfo.getId();
                }
                OPGestureAppModel oPGestureAppModel2 = new OPGestureAppModel(shortcutInfo.getPackage(), longLabel.toString(), shortcutInfo.getId(), 0);
                try {
                    oPGestureAppModel2.setAppIcon(createPackageContext(this.mGesturePackage, 0).getResources().getDrawable(shortcutInfo.getIconResourceId()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.mGestureAppList.add(oPGestureAppModel2);
            }
        }
    }

    private void refreshList() {
        initData();
        if (!this.mGesturePackage.equals(OPGestureUtils.getGesturePackageName(this, this.mGestureKey))) {
            ContentResolver contentResolver = getContentResolver();
            String str = this.mGestureKey;
            Settings.System.putString(contentResolver, str, "OpenApp:" + this.mGesturePackage);
        }
        this.mGestureSummary = OPGestureUtils.getShortCutsNameByID(this, this.mGesturePackage, OPGestureUtils.getShortCutIdByGestureKey(this, this.mGestureKey));
        OPGestureShortcutsAdapter oPGestureShortcutsAdapter = new OPGestureShortcutsAdapter(this, this.mGestureAppList, TextUtils.isEmpty(this.mGestureSummary) ? this.mTitle : this.mGestureSummary);
        this.mOPGestureShortcutsAdapter = oPGestureShortcutsAdapter;
        this.mGestureShortcutListView.setAdapter((ListAdapter) oPGestureShortcutsAdapter);
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

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        OPGestureAppModel oPGestureAppModel = (OPGestureAppModel) this.mGestureShortcutListView.getItemAtPosition(i);
        if (i != 0) {
            openShortCuts(oPGestureAppModel);
        } else {
            openApps(oPGestureAppModel);
        }
        setResult(-1);
        finish();
    }

    private void openApps(OPGestureAppModel oPGestureAppModel) {
        ContentResolver contentResolver = getContentResolver();
        String str = this.mGestureKey;
        Settings.System.putString(contentResolver, str, "OpenApp:" + oPGestureAppModel.getPkgName() + ";" + this.mGestureUid);
    }

    private void openShortCuts(OPGestureAppModel oPGestureAppModel) {
        ContentResolver contentResolver = getContentResolver();
        String str = this.mGestureKey;
        Settings.System.putString(contentResolver, str, "OpenShortcut:" + oPGestureAppModel.getPkgName() + ";" + oPGestureAppModel.getShortCutId() + ";" + this.mGestureUid);
    }
}
