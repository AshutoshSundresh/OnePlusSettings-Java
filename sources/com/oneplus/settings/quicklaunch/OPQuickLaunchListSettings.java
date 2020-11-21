package com.oneplus.settings.quicklaunch;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.better.OPAppModel;
import com.oneplus.settings.quickpay.QuickPayLottieAnimPreference;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPQuickLaunchListSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener, QuickPayLottieAnimPreference.OnPreferenceViewClickListener {
    private OPAppDragAndDropAdapter mAdapter;
    private TextView mAddView;
    private List<OPAppModel> mAppList = new ArrayList();
    private AppOpsManager mAppOpsManager;
    private Context mContext;
    private String mLastListSettings;
    private RecyclerView mListView;
    private Menu mMenu;
    private OPApplicationLoader mOPApplicationLoader;
    private PackageManager mPackageManager;
    private boolean mRemoveMode;
    private boolean mShowingRemoveDialog;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override // com.oneplus.settings.quickpay.QuickPayLottieAnimPreference.OnPreferenceViewClickListener
    public void onPreferenceViewClick(View view) {
    }

    public OPQuickLaunchListSettings() {
        new Handler(Looper.getMainLooper()) {
            /* class com.oneplus.settings.quicklaunch.OPQuickLaunchListSettings.AnonymousClass1 */

            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (OPQuickLaunchListSettings.this.mAdapter != null && OPQuickLaunchListSettings.this.mOPApplicationLoader != null) {
                    OPQuickLaunchListSettings.this.mAppList.clear();
                    OPQuickLaunchListSettings.this.mAppList.addAll(OPQuickLaunchListSettings.this.mOPApplicationLoader.getAppListByType(message.what));
                    OPQuickLaunchListSettings.this.mAdapter.setAppList(OPQuickLaunchListSettings.this.mAppList);
                    OPAppLinearLayoutManager oPAppLinearLayoutManager = new OPAppLinearLayoutManager(OPQuickLaunchListSettings.this.mContext, OPQuickLaunchListSettings.this.mAdapter);
                    oPAppLinearLayoutManager.setAutoMeasureEnabled(true);
                    OPQuickLaunchListSettings.this.mListView.setLayoutManager(oPAppLinearLayoutManager);
                    OPQuickLaunchListSettings.this.mListView.setHasFixedSize(true);
                    OPQuickLaunchListSettings.this.mAdapter.setRecyclerView(OPQuickLaunchListSettings.this.mListView);
                    OPQuickLaunchListSettings.this.mListView.setAdapter(OPQuickLaunchListSettings.this.mAdapter);
                }
            }
        };
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(C0017R$string.oneplus_shortcuts_settings);
        }
        this.mAdapter = new OPAppDragAndDropAdapter(this.mContext, this.mAppList);
        Context context = this.mContext;
        if (context != null) {
            this.mPackageManager = context.getPackageManager();
            this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService("appops");
            this.mOPApplicationLoader = new OPApplicationLoader(this.mContext, this.mAppOpsManager, this.mPackageManager);
        }
        this.mLastListSettings = OPUtils.getAllQuickLaunchStrings(this.mContext);
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        if (!TextUtils.equals(this.mLastListSettings, OPUtils.getAllQuickLaunchStrings(this.mContext))) {
            OPUtils.sendAppTrackerForQuickLaunch();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        configureDragAndDrop(layoutInflater.inflate(C0012R$layout.op_drag_list, (ViewGroup) onCreateView));
        return onCreateView;
    }

    private void initData() {
        this.mAppList.clear();
        List<OPAppModel> parseAllQuickLaunchStrings = OPUtils.parseAllQuickLaunchStrings(this.mContext);
        this.mAppList = parseAllQuickLaunchStrings;
        if (parseAllQuickLaunchStrings.size() > 0) {
            Settings.System.putInt(getContentResolver(), "op_quick_launcher_edited", 1);
        }
        this.mAdapter.setAppList(this.mAppList);
        OPAppLinearLayoutManager oPAppLinearLayoutManager = new OPAppLinearLayoutManager(this.mContext, this.mAdapter);
        oPAppLinearLayoutManager.setAutoMeasureEnabled(true);
        this.mListView.setLayoutManager(oPAppLinearLayoutManager);
        this.mListView.setHasFixedSize(true);
        this.mAdapter.setRecyclerView(this.mListView);
        this.mListView.setAdapter(this.mAdapter);
    }

    private void configureDragAndDrop(View view) {
        this.mListView = (RecyclerView) view.findViewById(C0010R$id.dragList);
        TextView textView = (TextView) view.findViewById(C0010R$id.add_more);
        this.mAddView = textView;
        textView.setText(getActivity().getString(C0017R$string.oneplus_shortcuts_add));
        this.mAddView.setOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.quicklaunch.OPQuickLaunchListSettings.AnonymousClass2 */

            public void onClick(View view) {
                OPQuickLaunchListSettings.this.startActivityForResult(new Intent("com.oneplus.action.QUICKPAY_LAUNCH_CATEGORY_SETTINGS"), 0);
            }
        });
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewStateRestored(Bundle bundle) {
        super.onViewStateRestored(bundle);
        if (bundle != null) {
            this.mRemoveMode = bundle.getBoolean("appRemoveMode", false);
            this.mShowingRemoveDialog = bundle.getBoolean("showingAppRemoveDialog", false);
        }
        setRemoveMode(this.mRemoveMode);
        this.mAdapter.restoreState(bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("appRemoveMode", this.mRemoveMode);
        bundle.putBoolean("showingAppRemoveDialog", this.mShowingRemoveDialog);
        this.mAdapter.saveState(bundle);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 2) {
            if (this.mRemoveMode) {
                this.mRemoveMode = false;
                this.mAdapter.removeChecked();
                setRemoveMode(false);
            } else {
                setRemoveMode(true);
            }
            return true;
        } else if (itemId != 16908332 || !this.mRemoveMode) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            setRemoveMode(false);
            return true;
        }
    }

    private void setRemoveMode(boolean z) {
        this.mRemoveMode = z;
        this.mAdapter.setRemoveMode(z);
        this.mAddView.setVisibility(z ? 4 : 0);
        updateVisibilityOfRemoveMenu();
    }

    private void updateVisibilityOfRemoveMenu() {
        Menu menu = this.mMenu;
        if (menu != null) {
            int i = 2;
            MenuItem findItem = menu.findItem(2);
            if (findItem != null) {
                if (!this.mRemoveMode) {
                    i = 0;
                }
                findItem.setShowAsAction(i);
            }
        }
    }
}
