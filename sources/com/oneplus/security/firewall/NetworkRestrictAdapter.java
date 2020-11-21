package com.oneplus.security.firewall;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.android.settings.C0003R$array;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.google.android.collect.Lists;
import com.oneplus.security.utils.CharUtil;
import com.oneplus.security.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NetworkRestrictAdapter extends BaseAdapter implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private List<AppUidItem> mAppUidItemList = Lists.newArrayList();
    private Context mContext;
    private LayoutInflater mInflater;
    ArrayAdapter<CharSequence> mSpinnerAdapter;

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public NetworkRestrictAdapter(Context context) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        ArrayAdapter<CharSequence> createFromResource = ArrayAdapter.createFromResource(this.mContext, C0003R$array.firewall_rule_items, C0012R$layout.filter_spinner_text_right);
        this.mSpinnerAdapter = createFromResource;
        createFromResource.setDropDownViewResource(C0012R$layout.spinnerlayout);
    }

    public List<AppUidItem> getmAppUidItemList() {
        return this.mAppUidItemList;
    }

    public int getCount() {
        return this.mAppUidItemList.size();
    }

    public AppUidItem getItem(int i) {
        return this.mAppUidItemList.get(i);
    }

    public long getItemId(int i) {
        return (long) this.mAppUidItemList.get(i).getAppUid();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        ViewHolder viewHolder;
        int i2 = 0;
        if (view == null) {
            viewHolder = new ViewHolder();
            view2 = this.mInflater.inflate(C0012R$layout.network_restrict_item_view, viewGroup, false);
            viewHolder.name = (TextView) view2.findViewById(C0010R$id.name);
            viewHolder.icon = (ImageView) view2.findViewById(C0010R$id.icon);
            Spinner spinner = (Spinner) view2.findViewById(C0010R$id.setting);
            viewHolder.setting = spinner;
            spinner.setAdapter((SpinnerAdapter) this.mSpinnerAdapter);
            view2.setTag(viewHolder);
        } else {
            view2 = view;
            viewHolder = (ViewHolder) view.getTag();
        }
        AppUidItem item = getItem(i);
        viewHolder.name.setText(item.getApps().get(0).getAppName());
        viewHolder.icon.setImageDrawable(item.getApps().get(0).getAppIcon());
        if (!item.isDataEnable() || !item.isWlanEnable()) {
            if (item.isDataEnable() || item.isWlanEnable()) {
                i2 = (item.isDataEnable() || !item.isWlanEnable()) ? 3 : 2;
            } else {
                i2 = 1;
            }
        }
        viewHolder.setting.setSelection(i2);
        viewHolder.setting.setTag(item);
        viewHolder.setting.setOnItemSelectedListener(this);
        return view2;
    }

    static final class ViewHolder {
        ImageView icon;
        TextView name;
        Spinner setting;

        ViewHolder() {
        }
    }

    private void updateRule(AppUidItem appUidItem, int i) {
        int i2 = 0;
        int i3 = 1;
        if (i == 0) {
            i3 = 0;
        } else if (i == 1) {
            i2 = 1;
        } else if (i == 2) {
            i3 = 0;
            i2 = 1;
        } else if (i != 3) {
            return;
        }
        NetworkRestrictManager.getInstance(this.mContext).updateRules(appUidItem, i2, i3);
    }

    public void updateData(List<AppUidItem> list) {
        if (list != null) {
            ArrayList newArrayList = Lists.newArrayList();
            newArrayList.addAll(list);
            Collections.sort(newArrayList, new Comparator<AppUidItem>(this) {
                /* class com.oneplus.security.firewall.NetworkRestrictAdapter.AnonymousClass1 */

                public int compare(AppUidItem appUidItem, AppUidItem appUidItem2) {
                    AppPkgItem appPkgItem = appUidItem.getApps().get(0);
                    AppPkgItem appPkgItem2 = appUidItem2.getApps().get(0);
                    boolean isSystemApp = appPkgItem.isSystemApp();
                    boolean isSystemApp2 = appPkgItem2.isSystemApp();
                    boolean isChinese = CharUtil.isChinese(appPkgItem.getAppName());
                    boolean isChinese2 = CharUtil.isChinese(appPkgItem2.getAppName());
                    if (!isSystemApp && !isSystemApp2) {
                        return appPkgItem.getAppSortKey().compareToIgnoreCase(appPkgItem2.getAppSortKey());
                    }
                    if (!isSystemApp || !isSystemApp2) {
                        return isSystemApp ? 1 : -1;
                    }
                    if (isChinese && !isChinese2) {
                        return -1;
                    }
                    if (isChinese || !isChinese2) {
                        return appPkgItem.getAppSortKey().compareToIgnoreCase(appPkgItem2.getAppSortKey());
                    }
                    return 1;
                }
            });
            this.mAppUidItemList = newArrayList;
        } else {
            this.mAppUidItemList.clear();
        }
        notifyDataSetChanged();
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        Object tag = adapterView.getTag();
        if (tag != null && (tag instanceof AppUidItem)) {
            updateRule((AppUidItem) tag, i);
        }
    }

    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag != null && (tag instanceof AppUidItem)) {
            Bundle bundle = new Bundle();
            bundle.putString("showAppImmediatePkg", ((AppUidItem) tag).getApps().get(0).getPkgName());
            bundle.putBoolean(":settings:need_back_to_ranklist", false);
            Context context = this.mContext;
            Utils.startSettingsAppFragment(context, "com.android.settings.DataUsageSummary", bundle, -1, context.getString(C0017R$string.app_data_usage), 1);
        }
    }
}
