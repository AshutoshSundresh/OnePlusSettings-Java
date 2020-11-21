package com.oneplus.settings.carcharger;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.better.OPAppModel;
import java.util.ArrayList;
import java.util.List;

public class OPCarChargerOpenAppAdapter extends BaseAdapter {
    private int hasRecommendedCount;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<OPAppModel> mOpenAppList = new ArrayList();
    private String mPackageName;

    public long getItemId(int i) {
        return (long) i;
    }

    public OPCarChargerOpenAppAdapter(Context context, PackageManager packageManager) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setData(List<OPAppModel> list) {
        this.mOpenAppList = list;
        notifyDataSetChanged();
    }

    public void setHasRecommendedCount(int i) {
        this.hasRecommendedCount = i;
    }

    public void setSelectedItem(String str) {
        if (str != null) {
            this.mPackageName = str;
        } else {
            this.mPackageName = "";
        }
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mOpenAppList.size();
    }

    public OPAppModel getItem(int i) {
        return this.mOpenAppList.get(i);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemViewHolder itemViewHolder;
        OPAppModel oPAppModel = this.mOpenAppList.get(i);
        if (view == null) {
            view = this.mInflater.inflate(C0012R$layout.op_car_charger_open_app_item, (ViewGroup) null);
            itemViewHolder = new ItemViewHolder(this);
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(C0010R$id.parent);
            itemViewHolder.titleTv = (TextView) view.findViewById(C0010R$id.tv);
            itemViewHolder.appIconIv = (ImageView) view.findViewById(C0010R$id.icon);
            itemViewHolder.appNameTv = (TextView) view.findViewById(C0010R$id.name);
            itemViewHolder.bottomLine = view.findViewById(C0010R$id.bottom_line);
            itemViewHolder.groupDivider = view.findViewById(C0010R$id.group_divider_area);
            itemViewHolder.radioButton = (RadioButton) view.findViewById(C0010R$id.radio_button);
            view.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) view.getTag();
        }
        itemViewHolder.titleTv.setVisibility(0);
        if (i == 1) {
            itemViewHolder.titleTv.setText(C0017R$string.oneplus_auto_open_app_recommended);
        } else if (i == this.hasRecommendedCount + 1) {
            itemViewHolder.titleTv.setText(C0017R$string.oneplus_auto_open_app_other_applications);
        } else {
            itemViewHolder.titleTv.setVisibility(8);
        }
        if (i < 1) {
            itemViewHolder.appIconIv.setVisibility(4);
        } else {
            itemViewHolder.appIconIv.setVisibility(0);
            itemViewHolder.appIconIv.setImageDrawable(oPAppModel.getAppIcon());
        }
        itemViewHolder.appNameTv.setText(oPAppModel.getLabel());
        if (i == 0 || i == this.hasRecommendedCount) {
            itemViewHolder.bottomLine.setVisibility(0);
        } else {
            itemViewHolder.bottomLine.setVisibility(8);
        }
        itemViewHolder.groupDivider.setVisibility(8);
        if (this.mPackageName.equals(oPAppModel.getPkgName())) {
            itemViewHolder.radioButton.setChecked(true);
        } else {
            itemViewHolder.radioButton.setChecked(false);
        }
        return view;
    }

    class ItemViewHolder {
        ImageView appIconIv;
        TextView appNameTv;
        View bottomLine;
        View groupDivider;
        RadioButton radioButton;
        TextView titleTv;

        ItemViewHolder(OPCarChargerOpenAppAdapter oPCarChargerOpenAppAdapter) {
        }
    }
}
