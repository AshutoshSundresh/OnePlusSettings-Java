package com.oneplus.settings.network;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.better.OPAppModel;
import java.util.ArrayList;
import java.util.List;

public class OPDualChannelDownloadAccelerationAdapter extends BaseAdapter {
    private List<OPAppModel> mAppList = new ArrayList();
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Boolean> mSelectedList = new ArrayList();

    public long getItemId(int i) {
        return (long) i;
    }

    public OPDualChannelDownloadAccelerationAdapter(Context context, List<OPAppModel> list) {
        this.mAppList = list;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setData(List<OPAppModel> list) {
        this.mAppList = list;
        this.mSelectedList.clear();
        for (int i = 0; i < this.mAppList.size(); i++) {
            this.mSelectedList.add(Boolean.valueOf(this.mAppList.get(i).isSelected()));
        }
        notifyDataSetChanged();
    }

    public void setSelected(int i, boolean z) {
        this.mSelectedList.set(i, Boolean.valueOf(z));
        notifyDataSetChanged();
    }

    public boolean getSelected(int i) {
        return this.mSelectedList.get(i).booleanValue();
    }

    public int getCount() {
        return this.mAppList.size();
    }

    public OPAppModel getItem(int i) {
        return this.mAppList.get(i);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemViewHolder itemViewHolder;
        OPAppModel oPAppModel = this.mAppList.get(i);
        if (view == null) {
            view = this.mInflater.inflate(C0012R$layout.op_dual_channel_download_acceleration_item, (ViewGroup) null);
            itemViewHolder = new ItemViewHolder(this);
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(C0010R$id.parent);
            itemViewHolder.titleTv = (TextView) view.findViewById(C0010R$id.tv);
            itemViewHolder.appIconIv = (ImageView) view.findViewById(C0010R$id.icon);
            itemViewHolder.appNameTv = (TextView) view.findViewById(C0010R$id.name);
            TextView textView = (TextView) view.findViewById(C0010R$id.summary);
            itemViewHolder.groupDivider = view.findViewById(C0010R$id.group_divider_area);
            itemViewHolder.switchBt = (Switch) view.findViewById(C0010R$id.switch_button);
            view.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) view.getTag();
        }
        itemViewHolder.groupDivider.setVisibility(8);
        if (i == 0) {
            itemViewHolder.titleTv.setVisibility(0);
            itemViewHolder.titleTv.setText(C0017R$string.oneplus_support_apps);
        } else {
            itemViewHolder.titleTv.setVisibility(8);
        }
        itemViewHolder.appIconIv.setImageDrawable(oPAppModel.getAppIcon());
        itemViewHolder.appNameTv.setText(oPAppModel.getLabel());
        itemViewHolder.switchBt.setClickable(false);
        itemViewHolder.switchBt.setBackground(null);
        if (getSelected(i)) {
            itemViewHolder.switchBt.setChecked(true);
        } else {
            itemViewHolder.switchBt.setChecked(false);
        }
        return view;
    }

    class ItemViewHolder {
        ImageView appIconIv;
        TextView appNameTv;
        View groupDivider;
        Switch switchBt;
        TextView titleTv;

        ItemViewHolder(OPDualChannelDownloadAccelerationAdapter oPDualChannelDownloadAccelerationAdapter) {
        }
    }
}
