package com.oneplus.settings.laboratory;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.SearchIndexableData;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import com.google.android.material.listview.OPListView;
import com.oneplus.settings.BaseActivity;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OPLabFeatureActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.laboratory.OPLabFeatureActivity.AnonymousClass3 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            if (OPUtils.isGuestMode()) {
                return arrayList;
            }
            Resources resources = context.getResources();
            List<OPLabPluginModel> fetchLockedAppListByPackageInfo = OPLabFeatureActivity.fetchLockedAppListByPackageInfo(context);
            ComponentName componentName = new ComponentName(context, OPLabFeatureActivity.class);
            String string = resources.getString(C0017R$string.oneplus_laboratory);
            for (OPLabPluginModel oPLabPluginModel : fetchLockedAppListByPackageInfo) {
                SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
                ((SearchIndexableData) searchIndexableRaw).key = oPLabPluginModel.getFeatureKey();
                searchIndexableRaw.title = oPLabPluginModel.getFeatureTitle();
                ((SearchIndexableData) searchIndexableRaw).iconResId = oPLabPluginModel.geFeatureIconId();
                searchIndexableRaw.screenTitle = string;
                ((SearchIndexableData) searchIndexableRaw).intentTargetPackage = componentName.getPackageName();
                ((SearchIndexableData) searchIndexableRaw).intentTargetClass = componentName.getClassName();
                ((SearchIndexableData) searchIndexableRaw).intentAction = "oneplus.intent.action.ONEPLUS_LAB_FEATURE";
                arrayList.add(searchIndexableRaw);
            }
            return arrayList;
        }
    };
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        /* class com.oneplus.settings.laboratory.OPLabFeatureActivity.AnonymousClass1 */

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 0 && OPLabFeatureActivity.this.mPluginListAdapter != null) {
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(OPLabFeatureActivity.this.mPluginData);
                OPLabFeatureActivity.this.mPluginListAdapter.setData(arrayList);
            }
        }
    };
    private List<OPLabPluginModel> mPluginData = new ArrayList();
    private ImageView mPluginHeadImageView;
    private OPListView mPluginList;
    private OPLabPluginListAdapter mPluginListAdapter;
    private ExecutorService mThreadPool = Executors.newCachedThreadPool();

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.oneplus.settings.BaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = this;
        setContentView(C0012R$layout.op_lab_feature_list_activity);
        this.mPluginHeadImageView = (ImageView) findViewById(C0010R$id.op_lab_feature_plugin_head);
        this.mPluginList = (OPListView) findViewById(C0010R$id.op_lab_feature_plugin_list);
        if (OPUtils.isBlackModeOn(getContentResolver())) {
            this.mPluginHeadImageView.setImageResource(C0008R$drawable.oneplus_lab_head_bg_dark);
        } else {
            this.mPluginHeadImageView.setImageResource(C0008R$drawable.oneplus_lab_head_bg_light);
        }
        OPLabPluginListAdapter oPLabPluginListAdapter = new OPLabPluginListAdapter(this, this.mPluginData);
        this.mPluginListAdapter = oPLabPluginListAdapter;
        this.mPluginList.setAdapter((ListAdapter) oPLabPluginListAdapter);
        this.mPluginList.setOnItemClickListener(this);
        initData(this.mHandler);
    }

    @Override // com.oneplus.settings.BaseActivity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    private void initData(final Handler handler) {
        this.mThreadPool.execute(new Runnable() {
            /* class com.oneplus.settings.laboratory.OPLabFeatureActivity.AnonymousClass2 */

            public void run() {
                OPLabFeatureActivity oPLabFeatureActivity = OPLabFeatureActivity.this;
                oPLabFeatureActivity.mPluginData = OPLabFeatureActivity.fetchLockedAppListByPackageInfo(oPLabFeatureActivity.mContext);
                handler.sendEmptyMessage(0);
            }
        });
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        gotoDetailPage(this.mPluginListAdapter.getItem(i));
    }

    private void gotoDetailPage(OPLabPluginModel oPLabPluginModel) {
        if (!TextUtils.isEmpty(oPLabPluginModel.getAction())) {
            startActivity(new Intent(oPLabPluginModel.getAction()));
            return;
        }
        Intent intent = new Intent("oneplus.intent.action.ONEPLUS_LAB_FEATURE_DETAILS");
        intent.putExtra("oneplus_lab_feature_toggle_count", oPLabPluginModel.getToggleCount());
        intent.putExtra("oneplus_lab_feature_toggle_names", oPLabPluginModel.getMultiToggleName());
        intent.putExtra("oneplus_lab_feature_title", oPLabPluginModel.getFeatureTitle());
        intent.putExtra("oneplus_lab_feature_Summary", oPLabPluginModel.getFeatureSummary());
        intent.putExtra("oneplus_lab_feature_key", oPLabPluginModel.getFeatureKey());
        intent.putExtra("oneplus_lab_feature_icon_id", oPLabPluginModel.geFeatureIconId());
        startActivity(intent);
    }

    /* access modifiers changed from: private */
    public static List<OPLabPluginModel> fetchLockedAppListByPackageInfo(Context context) {
        String[] strArr;
        Iterator<PackageInfo> it;
        String str;
        int i;
        String str2;
        String str3;
        int i2;
        String str4;
        String str5;
        String str6 = "oneplus_lab_feature";
        try {
            System.currentTimeMillis();
            List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(128);
            if (installedPackages.isEmpty()) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            Iterator<PackageInfo> it2 = installedPackages.iterator();
            while (it2.hasNext()) {
                PackageInfo next = it2.next();
                Bundle bundle = next.applicationInfo.metaData;
                if (bundle != null && bundle.containsKey(str6)) {
                    char c = 0;
                    Context createPackageContext = context.createPackageContext(next.packageName, 0);
                    String[] split = bundle.getString(str6).split(";");
                    int i3 = 0;
                    while (i3 < split.length) {
                        OPLabPluginModel oPLabPluginModel = new OPLabPluginModel();
                        String[] split2 = split[i3].split(",");
                        int length = split2.length;
                        if (length > 4) {
                            String str7 = split2[c];
                            str = str6;
                            int identifier = createPackageContext.getResources().getIdentifier(str7, "string", next.packageName);
                            String str8 = split2[1];
                            it = it2;
                            strArr = split;
                            int identifier2 = createPackageContext.getResources().getIdentifier(str8, "string", next.packageName);
                            int identifier3 = createPackageContext.getResources().getIdentifier(split2[2], "drawable", next.packageName);
                            int identifier4 = createPackageContext.getResources().getIdentifier(split2[3], "string", next.packageName);
                            str5 = identifier4 != 0 ? createPackageContext.getResources().getString(identifier4) : split2[3];
                            if (TextUtils.isEmpty(str5)) {
                                i = i3;
                                i3 = i + 1;
                                str6 = str;
                                it2 = it;
                                split = strArr;
                                c = 0;
                            } else {
                                int parseInt = Integer.parseInt(split2[4]);
                                String[] strArr2 = (String[]) Arrays.copyOfRange(split2, 5, length);
                                String[] strArr3 = new String[strArr2.length];
                                i2 = parseInt;
                                int i4 = 0;
                                while (i4 < strArr2.length) {
                                    int identifier5 = createPackageContext.getResources().getIdentifier(strArr2[i4], "string", next.packageName);
                                    strArr3[i4] = identifier5 != 0 ? createPackageContext.getResources().getString(identifier5) : strArr2[i4];
                                    i4++;
                                    str7 = str7;
                                    i3 = i3;
                                }
                                i = i3;
                                oPLabPluginModel.setFeatureIconId(identifier3);
                                oPLabPluginModel.setFeatureTitle(identifier != 0 ? createPackageContext.getResources().getString(identifier) : str7);
                                oPLabPluginModel.setFeatureSummary(identifier2 != 0 ? createPackageContext.getResources().getString(identifier2) : str8);
                                oPLabPluginModel.setMultiToggleName(strArr3);
                                oPLabPluginModel.setFeatureKey(str5);
                            }
                        } else {
                            str = str6;
                            it = it2;
                            strArr = split;
                            i = i3;
                            if (length > 1) {
                                str2 = split2[0];
                            } else {
                                str2 = "";
                            }
                            int identifier6 = createPackageContext.getResources().getIdentifier(str2, "string", next.packageName);
                            if (length > 2) {
                                str3 = split2[1];
                            } else {
                                str3 = "";
                            }
                            int identifier7 = createPackageContext.getResources().getIdentifier(str3, "string", next.packageName);
                            i2 = 2;
                            int identifier8 = createPackageContext.getResources().getIdentifier(split2[2], "drawable", next.packageName);
                            if (length > 3) {
                                str4 = split2[3];
                            } else {
                                str4 = "";
                            }
                            System.out.println("zhuyang--featureAction:" + str4);
                            int identifier9 = createPackageContext.getResources().getIdentifier(split2[3], "string", next.packageName);
                            String string = identifier9 != 0 ? createPackageContext.getResources().getString(identifier9) : split2[3];
                            if (!TextUtils.isEmpty(str4) && str4.startsWith("action:")) {
                                oPLabPluginModel.setAction(str4.replace("action:", ""));
                            } else if (TextUtils.isEmpty(string)) {
                                i3 = i + 1;
                                str6 = str;
                                it2 = it;
                                split = strArr;
                                c = 0;
                            }
                            oPLabPluginModel.setFeatureIconId(identifier8);
                            oPLabPluginModel.setFeatureTitle(identifier6 != 0 ? createPackageContext.getResources().getString(identifier6) : str2);
                            if (identifier7 != 0) {
                                str3 = createPackageContext.getResources().getString(identifier7);
                            }
                            oPLabPluginModel.setFeatureSummary(str3);
                            oPLabPluginModel.setFeatureKey(string);
                            str5 = string;
                        }
                        if (OPUtils.isSurportSimNfc(createPackageContext) || !"oneplus_nfc_security_module_key".equals(str5)) {
                            oPLabPluginModel.setToggleCount(i2);
                            System.out.println("zhuyang--add:");
                            if (OPUtils.isSupportMotionGraphicsCompensation() || !"op_iris_video_memc_extreme_status".equals(str5)) {
                                arrayList.add(oPLabPluginModel);
                                i3 = i + 1;
                                str6 = str;
                                it2 = it;
                                split = strArr;
                                c = 0;
                            } else {
                                i3 = i + 1;
                                str6 = str;
                                it2 = it;
                                split = strArr;
                                c = 0;
                            }
                        } else {
                            i3 = i + 1;
                            str6 = str;
                            it2 = it;
                            split = strArr;
                            c = 0;
                        }
                    }
                }
                str6 = str6;
                it2 = it2;
            }
            return arrayList;
        } catch (Exception e) {
            Log.e("PluginDemo", "some unknown error happened.");
            e.printStackTrace();
            return null;
        }
    }
}
