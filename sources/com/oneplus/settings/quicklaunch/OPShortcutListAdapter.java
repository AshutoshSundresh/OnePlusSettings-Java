package com.oneplus.settings.quicklaunch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.better.OPAppModel;
import java.util.ArrayList;
import java.util.List;

public class OPShortcutListAdapter extends BaseAdapter {
    private List<OPAppModel> mAppList = new ArrayList();
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Boolean> mSelectedList = new ArrayList();

    public long getItemId(int i) {
        return (long) i;
    }

    public void setAppType(int i) {
    }

    public OPShortcutListAdapter(Context context, List<OPAppModel> list) {
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
            view = this.mInflater.inflate(C0012R$layout.op_app_list_item, (ViewGroup) null);
            itemViewHolder = new ItemViewHolder(this);
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(C0010R$id.parent);
            itemViewHolder.titleTv = (TextView) view.findViewById(C0010R$id.tv);
            itemViewHolder.appIconIv = (ImageView) view.findViewById(C0010R$id.big_icon);
            itemViewHolder.smallAppIconIv = (ImageView) view.findViewById(C0010R$id.small_icon);
            itemViewHolder.appNameTv = (TextView) view.findViewById(C0010R$id.name);
            TextView textView = (TextView) view.findViewById(C0010R$id.summary);
            view.findViewById(C0010R$id.bottom_line);
            itemViewHolder.groupDivider = view.findViewById(C0010R$id.group_divider_area);
            itemViewHolder.checkBox = (CheckBox) view.findViewById(C0010R$id.check_box);
            view.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) view.getTag();
        }
        if (i == 0) {
            itemViewHolder.titleTv.setVisibility(0);
            if (oPAppModel.getType() == 1) {
                itemViewHolder.titleTv.setText(oPAppModel.getAppLabel());
            } else {
                itemViewHolder.titleTv.setText(C0017R$string.oneplus_quickpay);
            }
        } else if (oPAppModel.getType() == 2 || oPAppModel.getType() == 3) {
            itemViewHolder.titleTv.setVisibility(8);
        } else {
            int i2 = i - 1;
            if (!oPAppModel.getPkgName().equals(this.mAppList.get(i2).getPkgName()) || oPAppModel.getType() != this.mAppList.get(i2).getType()) {
                itemViewHolder.titleTv.setVisibility(0);
                itemViewHolder.titleTv.setText(oPAppModel.getAppLabel());
            } else {
                itemViewHolder.titleTv.setVisibility(8);
            }
        }
        if (oPAppModel.getType() == 1) {
            itemViewHolder.appIconIv.setImageDrawable(oPAppModel.getShortCutIcon());
            itemViewHolder.smallAppIconIv.setVisibility(0);
            itemViewHolder.smallAppIconIv.setImageDrawable(oPAppModel.getAppIcon());
        } else {
            itemViewHolder.appIconIv.setImageDrawable(oPAppModel.getAppIcon());
            itemViewHolder.smallAppIconIv.setVisibility(8);
        }
        itemViewHolder.appNameTv.setText(oPAppModel.getLabel());
        itemViewHolder.groupDivider.setVisibility(8);
        if (getSelected(i)) {
            itemViewHolder.checkBox.setChecked(true);
        } else {
            itemViewHolder.checkBox.setChecked(false);
        }
        return view;
    }

    class ItemViewHolder {
        ImageView appIconIv;
        TextView appNameTv;
        CheckBox checkBox;
        View groupDivider;
        ImageView smallAppIconIv;
        TextView titleTv;

        ItemViewHolder(OPShortcutListAdapter oPShortcutListAdapter) {
        }
    }
}
