package com.oneplus.settings.aboutphone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0010R$id;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.oneplus.settings.aboutphone.SoftwareInfoAdapter;
import com.oneplus.settings.ui.OPLayoutPreference;
import com.oneplus.settings.utils.OPUtils;
import java.util.Arrays;
import java.util.List;

public class OPAboutPhone extends DashboardFragment implements Contract$View {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.aboutphone.OPAboutPhone.AnonymousClass3 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_about_phone;
            return Arrays.asList(searchIndexableResource);
        }
    };
    private SoftwareInfoAdapter mAdapter;
    private Context mContext;
    private View mCurrentClickView;
    private Toast mDevHitToast;
    private AboutPhonePresenter mPresenter;
    private RecyclerView mRecyclerView;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPAboutPhone";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_about_phone;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
        initView();
        this.mPresenter = new AboutPhonePresenter(getActivity(), this, this);
    }

    @Override // com.oneplus.settings.aboutphone.Contract$View
    public void displayHardWarePreference(int i, String str, String str2, String str3, String str4) {
        OPAboutPhoneHardWareController newInstance = OPAboutPhoneHardWareController.newInstance(getActivity(), this, ((OPLayoutPreference) getPreferenceScreen().findPreference("hardware_view")).findViewById(C0010R$id.phone_hardware_info));
        newInstance.setPhoneImage(this.mContext.getDrawable(i));
        newInstance.done();
        newInstance.setCameraMessage(str);
        newInstance.setCpuMessage(str2);
        newInstance.setScreenMessage(str3);
        newInstance.setStorageMessage(str4 + "GB RAM + " + OPUtils.showROMStorage(getActivity()) + " ROM");
        newInstance.done();
    }

    @Override // com.oneplus.settings.aboutphone.Contract$View
    public void displaySoftWarePreference(List<SoftwareInfoEntity> list) {
        SoftwareInfoAdapter softwareInfoAdapter = new SoftwareInfoAdapter(this.mContext, list);
        this.mAdapter = softwareInfoAdapter;
        this.mRecyclerView.setAdapter(softwareInfoAdapter);
        this.mAdapter.notifyDataSetChanged();
        this.mAdapter.setOnItemClickListener(new SoftwareInfoAdapter.OnItemClickListener() {
            /* class com.oneplus.settings.aboutphone.OPAboutPhone.AnonymousClass1 */

            @Override // com.oneplus.settings.aboutphone.SoftwareInfoAdapter.OnItemClickListener
            public void onItemClick(View view, int i) {
                OPAboutPhone.this.mCurrentClickView = view;
                OPAboutPhone.this.mPresenter.onItemClick(i);
            }
        });
    }

    private void initView() {
        this.mRecyclerView = (RecyclerView) ((OPLayoutPreference) getPreferenceScreen().findPreference("soft_view")).findViewById(C0010R$id.phone_software_info).findViewById(C0010R$id.recycler_view_software_info);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.mContext);
        linearLayoutManager.setOrientation(1);
        this.mRecyclerView.setLayoutManager(linearLayoutManager);
        this.mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            /* class com.oneplus.settings.aboutphone.OPAboutPhone.AnonymousClass2 */

            public boolean onTouch(View view, MotionEvent motionEvent) {
                OPAboutPhone.this.mRecyclerView.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
    }

    @Override // com.oneplus.settings.aboutphone.Contract$View
    public void performHapticFeedback() {
        View view = this.mCurrentClickView;
        if (view != null) {
            view.performHapticFeedback(1);
        }
    }

    @Override // com.oneplus.settings.aboutphone.Contract$View
    public void showLongToast(int i) {
        showLongToast(this.mContext.getString(i));
    }

    @Override // com.oneplus.settings.aboutphone.Contract$View
    public void showLongToast(String str) {
        Toast makeText = Toast.makeText(getActivity(), str, 1);
        this.mDevHitToast = makeText;
        makeText.show();
    }

    @Override // com.oneplus.settings.aboutphone.Contract$View
    public void cancelToast() {
        Toast toast = this.mDevHitToast;
        if (toast != null) {
            toast.cancel();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 100) {
            if (i2 == -1) {
                this.mPresenter.enableDevelopmentSettings();
            }
            this.mPresenter.mProcessingLastDevHit = false;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        this.mPresenter.onResume();
    }
}
