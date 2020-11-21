package com.oneplus.settings.quicklaunch;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.android.settings.C0003R$array;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OPQuickLaunchShortCutFragment extends Fragment implements AdapterView.OnItemClickListener {
    private List<OPAppModel> mAppList = new ArrayList();
    private AppOpsManager mAppOpsManager;
    private Context mContext;
    private List<OPAppModel> mDefaultpayAppList = new ArrayList();
    private View mEmptyView;
    private Handler mHandler;
    private ListView mListView;
    private View mLoadingContainer;
    private OPApplicationLoader mOPApplicationLoader;
    private OPShortcutListAdapter mOPShortcutListAdapter;
    private PackageManager mPackageManager;
    private String[] mPayWaysName;

    public OPQuickLaunchShortCutFragment() {
        new HashMap();
        this.mPayWaysName = SettingsBaseApplication.mApplication.getResources().getStringArray(C0003R$array.oneplus_quickpay_ways_name);
        this.mHandler = new Handler(Looper.getMainLooper()) {
            /* class com.oneplus.settings.quicklaunch.OPQuickLaunchShortCutFragment.AnonymousClass1 */

            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (OPQuickLaunchShortCutFragment.this.mOPShortcutListAdapter != null && OPQuickLaunchShortCutFragment.this.mOPApplicationLoader != null) {
                    OPQuickLaunchShortCutFragment.this.mAppList.clear();
                    OPQuickLaunchShortCutFragment.this.mAppList.addAll(OPQuickLaunchShortCutFragment.this.mDefaultpayAppList);
                    OPQuickLaunchShortCutFragment.this.mAppList.addAll(OPQuickLaunchShortCutFragment.this.mOPApplicationLoader.getAppListByType(message.what));
                    OPQuickLaunchShortCutFragment.this.mOPShortcutListAdapter.setData(OPQuickLaunchShortCutFragment.this.mAppList);
                    OPQuickLaunchShortCutFragment.this.mOPShortcutListAdapter.setAppType(5);
                    if (OPQuickLaunchShortCutFragment.this.mAppList.isEmpty()) {
                        OPQuickLaunchShortCutFragment.this.mListView.setVisibility(0);
                        OPQuickLaunchShortCutFragment.this.mListView.setEmptyView(OPQuickLaunchShortCutFragment.this.mEmptyView);
                    }
                }
            }
        };
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = this.mContext;
        if (context != null) {
            this.mPackageManager = context.getPackageManager();
            this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService("appops");
            this.mOPApplicationLoader = new OPApplicationLoader(this.mContext, this.mAppOpsManager, this.mPackageManager);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0012R$layout.op_app_list_activity, (ViewGroup) null);
        initViews(inflate);
        createDefaultAppList();
        return inflate;
    }

    private void initViews(View view) {
        this.mListView = (ListView) view.findViewById(C0010R$id.op_app_list);
        OPShortcutListAdapter oPShortcutListAdapter = new OPShortcutListAdapter(this.mContext, this.mAppList);
        this.mOPShortcutListAdapter = oPShortcutListAdapter;
        this.mListView.setAdapter((ListAdapter) oPShortcutListAdapter);
        this.mListView.setOnItemClickListener(this);
        this.mLoadingContainer = view.findViewById(C0010R$id.loading_container);
        this.mEmptyView = view.findViewById(C0010R$id.op_empty_list_tips_view);
        this.mOPApplicationLoader.setmLoadingContainer(this.mLoadingContainer);
        this.mOPApplicationLoader.setNeedLoadWorkProfileApps(false);
        this.mOPApplicationLoader.initData(5, this.mHandler);
    }

    private List<OPAppModel> createDefaultAppList() {
        this.mDefaultpayAppList.clear();
        if (OPUtils.isAppExist(this.mContext, "com.tencent.mm")) {
            OPAppModel oPAppModel = new OPAppModel("com.tencent.mm", this.mPayWaysName[0], String.valueOf(0), 0, false);
            oPAppModel.setType(2);
            oPAppModel.setAppIcon(OPUtils.getQuickPayIconByType(this.mContext, 0));
            oPAppModel.setSelected(OPUtils.isInQuickLaunchList(this.mContext, oPAppModel));
            OPAppModel oPAppModel2 = new OPAppModel("com.tencent.mm", this.mPayWaysName[1], String.valueOf(1), 0, false);
            oPAppModel2.setType(2);
            oPAppModel2.setAppIcon(OPUtils.getQuickPayIconByType(this.mContext, 1));
            oPAppModel2.setSelected(OPUtils.isInQuickLaunchList(this.mContext, oPAppModel2));
            OPAppModel oPAppModel3 = new OPAppModel("com.tencent.mm", this.mPayWaysName[2], String.valueOf(0), 0, false);
            oPAppModel3.setType(3);
            oPAppModel3.setAppIcon(OPUtils.getQuickMiniProgrameconByType(this.mContext, 0));
            oPAppModel3.setSelected(OPUtils.isInQuickLaunchList(this.mContext, oPAppModel3));
            this.mDefaultpayAppList.add(oPAppModel);
            this.mDefaultpayAppList.add(oPAppModel2);
            if (!OPUtils.isO2()) {
                this.mDefaultpayAppList.add(oPAppModel3);
            }
        }
        if (OPUtils.isAppExist(this.mContext, "com.eg.android.AlipayGphone")) {
            OPAppModel oPAppModel4 = new OPAppModel("com.eg.android.AlipayGphone", this.mPayWaysName[3], String.valueOf(2), 0, false);
            oPAppModel4.setType(2);
            oPAppModel4.setAppIcon(OPUtils.getQuickPayIconByType(this.mContext, 2));
            oPAppModel4.setSelected(OPUtils.isInQuickLaunchList(this.mContext, oPAppModel4));
            OPAppModel oPAppModel5 = new OPAppModel("com.eg.android.AlipayGphone", this.mPayWaysName[4], String.valueOf(3), 0, false);
            oPAppModel5.setType(2);
            oPAppModel5.setAppIcon(OPUtils.getQuickPayIconByType(this.mContext, 3));
            oPAppModel5.setSelected(OPUtils.isInQuickLaunchList(this.mContext, oPAppModel5));
            this.mDefaultpayAppList.add(oPAppModel4);
            this.mDefaultpayAppList.add(oPAppModel5);
        }
        if (OPUtils.isAppExist(this.mContext, "net.one97.paytm")) {
            OPAppModel oPAppModel6 = new OPAppModel("net.one97.paytm", this.mPayWaysName[5], String.valueOf(4), 0, false);
            oPAppModel6.setType(2);
            oPAppModel6.setAppIcon(OPUtils.getAppIcon(this.mContext, "net.one97.paytm"));
            oPAppModel6.setSelected(OPUtils.isInQuickLaunchList(this.mContext, oPAppModel6));
            this.mDefaultpayAppList.add(oPAppModel6);
        }
        return this.mDefaultpayAppList;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        boolean z = !this.mOPShortcutListAdapter.getSelected(i);
        if (!z || OPUtils.getQuickLaunchShortcutsAccount(this.mContext) < 6) {
            this.mOPShortcutListAdapter.setSelected(i, z);
            OPAppModel oPAppModel = (OPAppModel) this.mListView.getItemAtPosition(i);
            StringBuilder sb = new StringBuilder(OPUtils.getAllQuickLaunchStrings(this.mContext));
            String quickLaunchShortcutsString = OPUtils.getQuickLaunchShortcutsString(oPAppModel);
            if (OPUtils.isQuickPayModel(oPAppModel)) {
                quickLaunchShortcutsString = OPUtils.getQuickPayAppString(oPAppModel);
            } else if (OPUtils.isWeChatMiniProgrameModel(oPAppModel)) {
                quickLaunchShortcutsString = OPUtils.getQuickMiniProgrameString(oPAppModel);
            }
            if (z) {
                sb.append(quickLaunchShortcutsString);
            } else {
                int indexOf = sb.indexOf(quickLaunchShortcutsString);
                sb.delete(indexOf, quickLaunchShortcutsString.length() + indexOf);
            }
            OPUtils.saveQuickLaunchStrings(this.mContext, sb.toString());
            return;
        }
        Context context = this.mContext;
        Toast.makeText(context, context.getString(C0017R$string.oneplus_max_shortcuts_tips), 0).show();
    }
}
