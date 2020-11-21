package com.oneplus.settings.network;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.oneplus.settings.BaseActivity;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPDualChannelDownloadAccelerationSettings extends BaseActivity implements AdapterView.OnItemClickListener {
    private List<OPAppModel> mAppList = new ArrayList();
    private ListView mAppListView;
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.oneplus.settings.network.OPDualChannelDownloadAccelerationSettings.AnonymousClass1 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (OPDualChannelDownloadAccelerationSettings.this.mOPDualChannelDownloadAccelerationAdapter != null && OPDualChannelDownloadAccelerationSettings.this.mOPApplicationLoader != null) {
                OPDualChannelDownloadAccelerationSettings.this.mAppList.clear();
                OPDualChannelDownloadAccelerationSettings.this.mAppList.addAll(OPDualChannelDownloadAccelerationSettings.this.mOPApplicationLoader.getAppListByType(message.what));
                OPDualChannelDownloadAccelerationSettings.this.mOPDualChannelDownloadAccelerationAdapter.setData(OPDualChannelDownloadAccelerationSettings.this.mAppList);
                View findViewById = OPDualChannelDownloadAccelerationSettings.this.findViewById(C0010R$id.op_empty_list_tips_view);
                if (OPDualChannelDownloadAccelerationSettings.this.mAppList.isEmpty()) {
                    findViewById.setVisibility(0);
                    OPDualChannelDownloadAccelerationSettings.this.mAppListView.setEmptyView(findViewById);
                }
            }
        }
    };
    private OPApplicationLoader mOPApplicationLoader;
    private OPDualChannelDownloadAccelerationAdapter mOPDualChannelDownloadAccelerationAdapter;
    private PackageManager mPackageManager;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.op_sla_down_load_app_list_activity);
        this.mContext = getApplicationContext();
        this.mPackageManager = getPackageManager();
        OPApplicationLoader oPApplicationLoader = new OPApplicationLoader(this, this.mPackageManager);
        this.mOPApplicationLoader = oPApplicationLoader;
        oPApplicationLoader.setAppType(100);
        initView();
    }

    private void initView() {
        this.mAppListView = (ListView) findViewById(C0010R$id.op_app_list);
        this.mAppListView.addHeaderView(LayoutInflater.from(this).inflate(C0012R$layout.op_sla_down_load_head_view, (ViewGroup) null));
        OPDualChannelDownloadAccelerationAdapter oPDualChannelDownloadAccelerationAdapter = new OPDualChannelDownloadAccelerationAdapter(this, this.mAppList);
        this.mOPDualChannelDownloadAccelerationAdapter = oPDualChannelDownloadAccelerationAdapter;
        this.mAppListView.setAdapter((ListAdapter) oPDualChannelDownloadAccelerationAdapter);
        this.mAppListView.setOnItemClickListener(this);
        startLoadData();
    }

    private void startLoadData() {
        this.mOPApplicationLoader.initData(0, this.mHandler);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        if (i > 0) {
            i--;
        }
        refreshList(i, getModelWithPosition(i + 1));
    }

    private OPAppModel getModelWithPosition(int i) {
        return (OPAppModel) this.mAppListView.getItemAtPosition(i);
    }

    private String getSlaDownLoadOpenAppsListString() {
        String string = Settings.System.getString(getContentResolver(), "sla_download_open_apps_list");
        return TextUtils.isEmpty(string) ? "" : string;
    }

    private String getSlaDownLoadOpenAppsString(OPAppModel oPAppModel) {
        if (oPAppModel == null) {
            return "";
        }
        return oPAppModel.getPkgName() + ";";
    }

    public void saveSlaDownLoadOpenAppsListStrings(String str) {
        Settings.System.putString(getContentResolver(), "sla_download_open_apps_list", str);
    }

    public void deleteSlaDownLoadOpenAppString(OPAppModel oPAppModel) {
        StringBuilder sb = new StringBuilder(getSlaDownLoadOpenAppsListString());
        String slaDownLoadOpenAppsString = getSlaDownLoadOpenAppsString(oPAppModel);
        int indexOf = sb.indexOf(slaDownLoadOpenAppsString);
        sb.delete(indexOf, slaDownLoadOpenAppsString.length() + indexOf);
        saveSlaDownLoadOpenAppsListStrings(sb.toString());
    }

    private void refreshList(int i, OPAppModel oPAppModel) {
        this.mOPDualChannelDownloadAccelerationAdapter.setSelected(i, !this.mOPDualChannelDownloadAccelerationAdapter.getSelected(i));
        StringBuilder sb = new StringBuilder(getSlaDownLoadOpenAppsListString());
        if (!OPUtils.isInSlaDownLoadOpenAppsListString(this.mContext, oPAppModel)) {
            sb.append(getSlaDownLoadOpenAppsString(oPAppModel));
            saveSlaDownLoadOpenAppsListStrings(sb.toString());
            return;
        }
        deleteSlaDownLoadOpenAppString(oPAppModel);
    }
}
