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
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OPQuickLaunchAppFragment extends Fragment implements AdapterView.OnItemClickListener {
    private List<OPAppModel> mAppList = new ArrayList();
    private AppOpsManager mAppOpsManager;
    private Context mContext;
    private View mEmptyView;
    private Handler mHandler;
    private ListView mListView;
    private View mLoadingContainer;
    private OPApplicationListAdapter mOPApplicationListAdapter;
    private OPApplicationLoader mOPApplicationLoader;
    private PackageManager mPackageManager;

    public OPQuickLaunchAppFragment() {
        new HashMap();
        this.mHandler = new Handler(Looper.getMainLooper()) {
            /* class com.oneplus.settings.quicklaunch.OPQuickLaunchAppFragment.AnonymousClass1 */

            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (OPQuickLaunchAppFragment.this.mOPApplicationListAdapter != null && OPQuickLaunchAppFragment.this.mOPApplicationLoader != null) {
                    OPQuickLaunchAppFragment.this.mAppList.clear();
                    OPQuickLaunchAppFragment.this.mAppList.addAll(OPQuickLaunchAppFragment.this.mOPApplicationLoader.getAppListByType(message.what));
                    OPQuickLaunchAppFragment.this.mOPApplicationListAdapter.setData(OPQuickLaunchAppFragment.this.mAppList);
                    OPQuickLaunchAppFragment.this.mOPApplicationListAdapter.setAppType(4);
                    if (OPQuickLaunchAppFragment.this.mAppList.isEmpty()) {
                        OPQuickLaunchAppFragment.this.mListView.setVisibility(0);
                        OPQuickLaunchAppFragment.this.mListView.setEmptyView(OPQuickLaunchAppFragment.this.mEmptyView);
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
        return inflate;
    }

    private void initViews(View view) {
        this.mListView = (ListView) view.findViewById(C0010R$id.op_app_list);
        OPApplicationListAdapter oPApplicationListAdapter = new OPApplicationListAdapter(this.mContext, this.mAppList);
        this.mOPApplicationListAdapter = oPApplicationListAdapter;
        this.mListView.setAdapter((ListAdapter) oPApplicationListAdapter);
        this.mListView.setOnItemClickListener(this);
        this.mLoadingContainer = view.findViewById(C0010R$id.loading_container);
        this.mEmptyView = view.findViewById(C0010R$id.op_empty_list_tips_view);
        this.mOPApplicationLoader.setmLoadingContainer(this.mLoadingContainer);
        this.mOPApplicationLoader.setNeedLoadWorkProfileApps(false);
        this.mOPApplicationLoader.initData(4, this.mHandler);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        boolean z = !this.mOPApplicationListAdapter.getSelected(i);
        if (!z || OPUtils.getQuickLaunchShortcutsAccount(this.mContext) < 6) {
            this.mOPApplicationListAdapter.setSelected(i, z);
            StringBuilder sb = new StringBuilder(OPUtils.getAllQuickLaunchStrings(this.mContext));
            String quickLaunchAppString = OPUtils.getQuickLaunchAppString((OPAppModel) this.mListView.getItemAtPosition(i));
            if (z) {
                sb.append(quickLaunchAppString);
            } else {
                int indexOf = sb.indexOf(quickLaunchAppString);
                sb.delete(indexOf, quickLaunchAppString.length() + indexOf);
            }
            OPUtils.saveQuickLaunchStrings(this.mContext, sb.toString());
            return;
        }
        Context context = this.mContext;
        Toast.makeText(context, context.getString(C0017R$string.oneplus_max_shortcuts_tips), 0).show();
    }
}
