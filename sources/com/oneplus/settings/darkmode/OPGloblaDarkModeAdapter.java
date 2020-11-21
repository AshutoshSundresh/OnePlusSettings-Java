package com.oneplus.settings.darkmode;

import android.content.Context;
import android.text.TextUtils;
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

public class OPGloblaDarkModeAdapter extends BaseAdapter {
    private List<OPAppModel> mAppList = new ArrayList();
    private Context mContext;
    private boolean mEnableList;
    private int mFirstUnFullSupportPosition = -1;
    private LayoutInflater mInflater;
    private List<Boolean> mSelectedList = new ArrayList();

    public boolean areAllItemsEnabled() {
        return false;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public OPGloblaDarkModeAdapter(Context context, List<OPAppModel> list) {
        this.mAppList = list;
        this.mContext = context;
        this.mEnableList = needEnableList();
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    public void setData(List<OPAppModel> list) {
        this.mAppList = list;
        this.mSelectedList.clear();
        for (int i = 0; i < this.mAppList.size(); i++) {
            this.mSelectedList.add(Boolean.valueOf(this.mAppList.get(i).isSelected()));
        }
        this.mEnableList = needEnableList();
        notifyDataSetChanged();
    }

    public void setSelected(int i, boolean z) {
        this.mSelectedList.set(i, Boolean.valueOf(z));
        notifyDataSetChanged();
    }

    public void enableList(boolean z) {
        this.mEnableList = needEnableList() || z;
        notifyDataSetChanged();
    }

    public boolean needEnableList() {
        return (this.mContext.getResources().getConfiguration().uiMode & 32) != 0;
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
            view = this.mInflater.inflate(C0012R$layout.op_multi_app_item, (ViewGroup) null);
            itemViewHolder = new ItemViewHolder(this);
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(C0010R$id.parent);
            itemViewHolder.titleTv = (TextView) view.findViewById(C0010R$id.tv);
            itemViewHolder.appIconIv = (ImageView) view.findViewById(C0010R$id.icon);
            itemViewHolder.appNameTv = (TextView) view.findViewById(C0010R$id.name);
            TextView textView = (TextView) view.findViewById(C0010R$id.summary);
            itemViewHolder.bottomLine = view.findViewById(C0010R$id.bottom_line);
            itemViewHolder.groupDivider = view.findViewById(C0010R$id.group_divider_area);
            itemViewHolder.switchBt = (Switch) view.findViewById(C0010R$id.switch_button);
            view.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) view.getTag();
        }
        itemViewHolder.bottomLine.setVisibility(0);
        itemViewHolder.groupDivider.setVisibility(8);
        if (i == 0) {
            itemViewHolder.titleTv.setVisibility(0);
            itemViewHolder.titleTv.setText(C0017R$string.op_global_drak_mode_full_support_list);
        } else if (oPAppModel.getAppopsMode() == 105 || oPAppModel.getAppopsMode() == 108 || oPAppModel.getAppopsMode() == 102) {
            if (this.mFirstUnFullSupportPosition == -1) {
                this.mFirstUnFullSupportPosition = i;
            }
            if (this.mFirstUnFullSupportPosition == i) {
                itemViewHolder.titleTv.setVisibility(0);
                itemViewHolder.titleTv.setText(C0017R$string.op_global_drak_mode_not_full_support_list);
            } else {
                itemViewHolder.titleTv.setVisibility(8);
            }
        } else {
            itemViewHolder.titleTv.setVisibility(8);
        }
        if (this.mAppList.size() != 1 || !TextUtils.isEmpty(oPAppModel.getPkgName()) || !TextUtils.isEmpty(oPAppModel.getLabel())) {
            view.setClickable(false);
            view.setEnabled(true);
            itemViewHolder.appNameTv.setEnabled(true);
            itemViewHolder.appIconIv.setVisibility(0);
            itemViewHolder.switchBt.setVisibility(0);
            itemViewHolder.appIconIv.setImageDrawable(oPAppModel.getAppIcon());
            itemViewHolder.appNameTv.setText(oPAppModel.getLabel());
        } else {
            view.setClickable(true);
            view.setEnabled(false);
            itemViewHolder.appNameTv.setEnabled(false);
            itemViewHolder.appIconIv.setVisibility(4);
            itemViewHolder.switchBt.setVisibility(8);
            itemViewHolder.appNameTv.setText(C0017R$string.oneplus_memc_support_no_apps);
        }
        itemViewHolder.switchBt.setClickable(false);
        itemViewHolder.switchBt.setBackground(null);
        if (oPAppModel.isDisable() || !this.mEnableList) {
            itemViewHolder.switchBt.setEnabled(false);
        } else {
            itemViewHolder.switchBt.setEnabled(true);
        }
        if (getSelected(i)) {
            itemViewHolder.switchBt.setChecked(true);
        } else {
            itemViewHolder.switchBt.setChecked(false);
        }
        return view;
    }

    public boolean isEnabled(int i) {
        if (this.mEnableList && !this.mAppList.get(i).isDisable()) {
            return true;
        }
        return false;
    }

    class ItemViewHolder {
        ImageView appIconIv;
        TextView appNameTv;
        View bottomLine;
        View groupDivider;
        Switch switchBt;
        TextView titleTv;

        ItemViewHolder(OPGloblaDarkModeAdapter oPGloblaDarkModeAdapter) {
        }
    }
}
