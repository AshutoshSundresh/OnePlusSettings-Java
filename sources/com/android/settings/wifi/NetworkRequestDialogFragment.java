package com.android.settings.wifi;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.internal.PreferenceImageView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settingslib.R$id;
import com.android.settingslib.Utils;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.WifiTracker;
import com.android.settingslib.wifi.WifiTrackerFactory;
import java.util.ArrayList;
import java.util.List;

public class NetworkRequestDialogFragment extends NetworkRequestDialogBaseFragment implements DialogInterface.OnClickListener {
    private List<AccessPoint> mAccessPointList;
    private AccessPointAdapter mDialogAdapter;
    FilterWifiTracker mFilterWifiTracker;
    private boolean mShowLimitedItem = true;
    private WifiManager.NetworkRequestUserSelectionCallback mUserSelectionCallback;

    public static NetworkRequestDialogFragment newInstance() {
        return new NetworkRequestDialogFragment();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Context context = getContext();
        View inflate = LayoutInflater.from(context).inflate(C0012R$layout.network_request_dialog_title, (ViewGroup) null);
        ((TextView) inflate.findViewById(C0010R$id.network_request_title_text)).setText(getTitle());
        ((TextView) inflate.findViewById(C0010R$id.network_request_summary_text)).setText(getSummary());
        ((ProgressBar) inflate.findViewById(C0010R$id.network_request_title_progress)).setVisibility(0);
        this.mDialogAdapter = new AccessPointAdapter(this, context, C0012R$layout.preference_access_point, getAccessPointList());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCustomTitle(inflate);
        builder.setAdapter(this.mDialogAdapter, this);
        builder.setNegativeButton(C0017R$string.cancel, new DialogInterface.OnClickListener() {
            /* class com.android.settings.wifi.$$Lambda$NetworkRequestDialogFragment$WCubGJZUXXghSB4GzGNKVjh70wc */

            public final void onClick(DialogInterface dialogInterface, int i) {
                NetworkRequestDialogFragment.this.lambda$onCreateDialog$0$NetworkRequestDialogFragment(dialogInterface, i);
            }
        });
        builder.setNeutralButton(C0017R$string.network_connection_request_dialog_showall, (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        create.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener(create) {
            /* class com.android.settings.wifi.$$Lambda$NetworkRequestDialogFragment$kMx9q9fSHpeKkR6QNAIPpkj7yk */
            public final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                NetworkRequestDialogFragment.this.lambda$onCreateDialog$1$NetworkRequestDialogFragment(this.f$1, adapterView, view, i, j);
            }
        });
        setCancelable(false);
        create.setOnShowListener(new DialogInterface.OnShowListener(create) {
            /* class com.android.settings.wifi.$$Lambda$NetworkRequestDialogFragment$PTu_vRjTWuG_4vH1Q83vJ5FyGZs */
            public final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void onShow(DialogInterface dialogInterface) {
                NetworkRequestDialogFragment.this.lambda$onCreateDialog$3$NetworkRequestDialogFragment(this.f$1, dialogInterface);
            }
        });
        return create;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$0 */
    public /* synthetic */ void lambda$onCreateDialog$0$NetworkRequestDialogFragment(DialogInterface dialogInterface, int i) {
        onCancel(dialogInterface);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$1 */
    public /* synthetic */ void lambda$onCreateDialog$1$NetworkRequestDialogFragment(AlertDialog alertDialog, AdapterView adapterView, View view, int i, long j) {
        onClick(alertDialog, i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$3 */
    public /* synthetic */ void lambda$onCreateDialog$3$NetworkRequestDialogFragment(AlertDialog alertDialog, DialogInterface dialogInterface) {
        Button button = alertDialog.getButton(-3);
        button.setVisibility(8);
        button.setOnClickListener(new View.OnClickListener(button) {
            /* class com.android.settings.wifi.$$Lambda$NetworkRequestDialogFragment$lw_Wq0DVP57qlwDOANi5I6KnyZc */
            public final /* synthetic */ Button f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                NetworkRequestDialogFragment.this.lambda$onCreateDialog$2$NetworkRequestDialogFragment(this.f$1, view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$2 */
    public /* synthetic */ void lambda$onCreateDialog$2$NetworkRequestDialogFragment(Button button, View view) {
        this.mShowLimitedItem = false;
        renewAccessPointList(null);
        notifyAdapterRefresh();
        button.setVisibility(8);
    }

    /* access modifiers changed from: package-private */
    public List<AccessPoint> getAccessPointList() {
        if (this.mAccessPointList == null) {
            this.mAccessPointList = new ArrayList();
        }
        return this.mAccessPointList;
    }

    private BaseAdapter getDialogAdapter() {
        return this.mDialogAdapter;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        List<AccessPoint> accessPointList = getAccessPointList();
        if (accessPointList.size() != 0 && this.mUserSelectionCallback != null && i < accessPointList.size()) {
            AccessPoint accessPoint = accessPointList.get(i);
            WifiConfiguration config = accessPoint.getConfig();
            if (config == null) {
                config = WifiUtils.getWifiConfig(accessPoint, null, null);
            }
            if (config != null) {
                this.mUserSelectionCallback.select(config);
            }
        }
    }

    @Override // com.android.settings.wifi.NetworkRequestDialogBaseFragment, androidx.fragment.app.DialogFragment
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        WifiManager.NetworkRequestUserSelectionCallback networkRequestUserSelectionCallback = this.mUserSelectionCallback;
        if (networkRequestUserSelectionCallback != null) {
            networkRequestUserSelectionCallback.reject();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment
    public void onPause() {
        super.onPause();
        FilterWifiTracker filterWifiTracker = this.mFilterWifiTracker;
        if (filterWifiTracker != null) {
            filterWifiTracker.onPause();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment
    public void onDestroy() {
        super.onDestroy();
        FilterWifiTracker filterWifiTracker = this.mFilterWifiTracker;
        if (filterWifiTracker != null) {
            filterWifiTracker.onDestroy();
            this.mFilterWifiTracker = null;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showAllButton() {
        Button button;
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null && (button = alertDialog.getButton(-3)) != null) {
            button.setVisibility(0);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void hideProgressIcon() {
        View findViewById;
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog != null && (findViewById = alertDialog.findViewById(C0010R$id.network_request_title_progress)) != null) {
            findViewById.setVisibility(8);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment
    public void onResume() {
        super.onResume();
        if (this.mFilterWifiTracker == null) {
            this.mFilterWifiTracker = new FilterWifiTracker(getContext(), getSettingsLifecycle());
        }
        this.mFilterWifiTracker.onResume();
    }

    /* access modifiers changed from: private */
    public class AccessPointAdapter extends ArrayAdapter<AccessPoint> {
        private final LayoutInflater mInflater;
        private final int mResourceId;

        public AccessPointAdapter(NetworkRequestDialogFragment networkRequestDialogFragment, Context context, int i, List<AccessPoint> list) {
            super(context, i, list);
            this.mResourceId = i;
            this.mInflater = LayoutInflater.from(context);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z = false;
            if (view == null) {
                view = this.mInflater.inflate(this.mResourceId, viewGroup, false);
                view.findViewById(R$id.two_target_divider).setVisibility(8);
            }
            AccessPoint accessPoint = (AccessPoint) getItem(i);
            TextView textView = (TextView) view.findViewById(16908310);
            if (textView != null) {
                textView.setSingleLine(false);
                textView.setText(accessPoint.getTitle());
            }
            TextView textView2 = (TextView) view.findViewById(16908304);
            if (textView2 != null) {
                String settingsSummary = accessPoint.getSettingsSummary();
                if (TextUtils.isEmpty(settingsSummary)) {
                    textView2.setVisibility(8);
                } else {
                    textView2.setVisibility(0);
                    textView2.setText(settingsSummary);
                }
            }
            PreferenceImageView preferenceImageView = (PreferenceImageView) view.findViewById(16908294);
            int level = accessPoint.getLevel();
            int wifiStandard = accessPoint.getWifiStandard();
            if (accessPoint.isHe8ssCapableAp() && accessPoint.isVhtMax8SpatialStreamsSupported()) {
                z = true;
            }
            if (preferenceImageView != null) {
                Drawable drawable = getContext().getDrawable(Utils.getWifiIconResource(level, wifiStandard, z));
                drawable.setTintList(Utils.getColorAttr(getContext(), 16843817));
                preferenceImageView.setImageDrawable(drawable);
            }
            return view;
        }
    }

    @Override // com.android.settings.wifi.NetworkRequestDialogBaseFragment
    public void onUserSelectionCallbackRegistration(WifiManager.NetworkRequestUserSelectionCallback networkRequestUserSelectionCallback) {
        this.mUserSelectionCallback = networkRequestUserSelectionCallback;
    }

    @Override // com.android.settings.wifi.NetworkRequestDialogBaseFragment
    public void onMatch(List<ScanResult> list) {
        if (list != null && list.size() > 0) {
            renewAccessPointList(list);
            notifyAdapterRefresh();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void renewAccessPointList(List<ScanResult> list) {
        FilterWifiTracker filterWifiTracker = this.mFilterWifiTracker;
        if (filterWifiTracker != null) {
            if (list != null) {
                filterWifiTracker.updateKeys(list);
            }
            List<AccessPoint> accessPointList = getAccessPointList();
            accessPointList.clear();
            accessPointList.addAll(this.mFilterWifiTracker.getAccessPoints());
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyAdapterRefresh() {
        if (getDialogAdapter() != null) {
            getDialogAdapter().notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public final class FilterWifiTracker {
        private final List<String> mAccessPointKeys = new ArrayList();
        private final Context mContext;
        WifiTracker.WifiListener mWifiListener;
        private final WifiTracker mWifiTracker;

        public FilterWifiTracker(Context context, Lifecycle lifecycle) {
            AnonymousClass1 r2 = new WifiTracker.WifiListener() {
                /* class com.android.settings.wifi.NetworkRequestDialogFragment.FilterWifiTracker.AnonymousClass1 */

                @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
                public void onWifiStateChanged(int i) {
                    NetworkRequestDialogFragment.this.notifyAdapterRefresh();
                }

                @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
                public void onConnectedChanged() {
                    NetworkRequestDialogFragment.this.notifyAdapterRefresh();
                }

                @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
                public void onAccessPointsChanged() {
                    NetworkRequestDialogFragment.this.renewAccessPointList(null);
                    NetworkRequestDialogFragment.this.notifyAdapterRefresh();
                }
            };
            this.mWifiListener = r2;
            this.mWifiTracker = WifiTrackerFactory.create(context, r2, lifecycle, true, true);
            this.mContext = context;
        }

        public void updateKeys(List<ScanResult> list) {
            for (ScanResult scanResult : list) {
                String key = AccessPoint.getKey(this.mContext, scanResult);
                if (!this.mAccessPointKeys.contains(key)) {
                    this.mAccessPointKeys.add(key);
                }
            }
        }

        public List<AccessPoint> getAccessPoints() {
            List<AccessPoint> accessPoints = this.mWifiTracker.getAccessPoints();
            ArrayList arrayList = new ArrayList();
            int i = 0;
            for (AccessPoint accessPoint : accessPoints) {
                if (this.mAccessPointKeys.contains(accessPoint.getKey())) {
                    arrayList.add(accessPoint);
                    i++;
                    if (NetworkRequestDialogFragment.this.mShowLimitedItem && i >= 5) {
                        break;
                    }
                }
            }
            if (NetworkRequestDialogFragment.this.mShowLimitedItem && i >= 5) {
                NetworkRequestDialogFragment.this.showAllButton();
            }
            if (i > 0) {
                NetworkRequestDialogFragment.this.hideProgressIcon();
            }
            return arrayList;
        }

        public void onDestroy() {
            WifiTracker wifiTracker = this.mWifiTracker;
            if (wifiTracker != null) {
                wifiTracker.onDestroy();
            }
        }

        public void onResume() {
            WifiTracker wifiTracker = this.mWifiTracker;
            if (wifiTracker != null) {
                wifiTracker.onStart();
            }
        }

        public void onPause() {
            WifiTracker wifiTracker = this.mWifiTracker;
            if (wifiTracker != null) {
                wifiTracker.onStop();
            }
        }
    }
}
