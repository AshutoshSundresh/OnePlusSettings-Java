package com.oneplus.settings.gestures;

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

public class OPGestureAppAdapter extends BaseAdapter {
    private Context mContext;
    private int mDefaultNum;
    private List<OPAppModel> mGestureAppList = new ArrayList();
    private String mGesturePackageName;
    private String mGestureSummary;
    private boolean mHasShortCut;
    private LayoutInflater mInflater;
    private String mShortcutName;

    public long getItemId(int i) {
        return (long) i;
    }

    public OPGestureAppAdapter(Context context, PackageManager packageManager, String str) {
        this.mContext = context;
        this.mGestureSummary = str;
        this.mInflater = LayoutInflater.from(context);
        this.mDefaultNum = 0;
    }

    public void setData(List<OPAppModel> list) {
        this.mGestureAppList = list;
        notifyDataSetChanged();
    }

    public void setSelectedItem(String str, String str2, int i, boolean z, String str3) {
        this.mGestureSummary = str;
        this.mGesturePackageName = str2;
        this.mHasShortCut = z;
        this.mShortcutName = str3;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mGestureAppList.size();
    }

    public OPAppModel getItem(int i) {
        return this.mGestureAppList.get(i);
    }

    public void setDefaultNum(int i) {
        this.mDefaultNum = i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemViewHolder itemViewHolder;
        OPAppModel oPAppModel = this.mGestureAppList.get(i);
        if (view == null) {
            view = this.mInflater.inflate(C0012R$layout.op_gesture_app_item, (ViewGroup) null);
            itemViewHolder = new ItemViewHolder(this);
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(C0010R$id.parent);
            itemViewHolder.titleTv = (TextView) view.findViewById(C0010R$id.tv);
            itemViewHolder.tvmargin = view.findViewById(C0010R$id.tv_margin);
            itemViewHolder.appIconIv = (ImageView) view.findViewById(C0010R$id.icon);
            itemViewHolder.appNameTv = (TextView) view.findViewById(C0010R$id.name);
            itemViewHolder.summaryTv = (TextView) view.findViewById(C0010R$id.summary);
            itemViewHolder.headermargin = view.findViewById(C0010R$id.header_margin);
            itemViewHolder.radioButton = (RadioButton) view.findViewById(C0010R$id.radio_button);
            view.setTag(itemViewHolder);
        } else {
            itemViewHolder = (ItemViewHolder) view.getTag();
        }
        itemViewHolder.titleTv.setVisibility(0);
        itemViewHolder.tvmargin.setVisibility(0);
        if (i == 1) {
            itemViewHolder.titleTv.setText(C0017R$string.oneplus_gestures_fast_entrance);
        } else if (i == this.mDefaultNum) {
            itemViewHolder.titleTv.setText(C0017R$string.oneplus_gestures_open_apps);
        } else {
            itemViewHolder.titleTv.setVisibility(8);
            itemViewHolder.tvmargin.setVisibility(8);
        }
        if (i < this.mDefaultNum) {
            itemViewHolder.appIconIv.setVisibility(8);
        } else {
            itemViewHolder.appIconIv.setVisibility(0);
            itemViewHolder.appIconIv.setImageDrawable(oPAppModel.getAppIcon());
        }
        itemViewHolder.appNameTv.setText(oPAppModel.getLabel());
        if (i == 0) {
            itemViewHolder.headermargin.setVisibility(0);
        } else {
            itemViewHolder.headermargin.setVisibility(8);
        }
        if ((i >= this.mDefaultNum || !this.mGestureSummary.equals(oPAppModel.getLabel())) && (i < this.mDefaultNum || !this.mGesturePackageName.equals(oPAppModel.getPkgName()))) {
            itemViewHolder.radioButton.setChecked(false);
            itemViewHolder.summaryTv.setVisibility(8);
        } else {
            itemViewHolder.radioButton.setChecked(true);
            if (this.mHasShortCut) {
                itemViewHolder.summaryTv.setText(this.mShortcutName);
                itemViewHolder.summaryTv.setVisibility(0);
            } else {
                itemViewHolder.summaryTv.setText("");
                itemViewHolder.summaryTv.setVisibility(8);
            }
        }
        return view;
    }

    class ItemViewHolder {
        ImageView appIconIv;
        TextView appNameTv;
        View headermargin;
        RadioButton radioButton;
        TextView summaryTv;
        TextView titleTv;
        View tvmargin;

        ItemViewHolder(OPGestureAppAdapter oPGestureAppAdapter) {
        }
    }
}
