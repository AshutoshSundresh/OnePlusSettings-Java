package com.oneplus.settings.gestures;

import android.content.Context;
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
import java.util.ArrayList;
import java.util.List;

public class OPGestureShortcutsAdapter extends BaseAdapter {
    private Context mContext;
    private List<OPGestureAppModel> mGestureAppList = new ArrayList();
    private String mGestureSummary;
    private LayoutInflater mInflater;

    public long getItemId(int i) {
        return (long) i;
    }

    public OPGestureShortcutsAdapter(Context context, List<OPGestureAppModel> list, String str) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mGestureAppList = list;
        this.mGestureSummary = str;
    }

    public int getCount() {
        return this.mGestureAppList.size();
    }

    public OPGestureAppModel getItem(int i) {
        return this.mGestureAppList.get(i);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ItemViewHolder itemViewHolder;
        OPGestureAppModel oPGestureAppModel = this.mGestureAppList.get(i);
        if (view == null) {
            view = this.mInflater.inflate(C0012R$layout.op_gesture_app_item, (ViewGroup) null);
            itemViewHolder = new ItemViewHolder(this);
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(C0010R$id.parent);
            itemViewHolder.titleTv = (TextView) view.findViewById(C0010R$id.tv);
            itemViewHolder.tvmargin = view.findViewById(C0010R$id.tv_margin);
            itemViewHolder.appIconIv = (ImageView) view.findViewById(C0010R$id.icon);
            itemViewHolder.appNameTv = (TextView) view.findViewById(C0010R$id.name);
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
        } else {
            itemViewHolder.titleTv.setVisibility(8);
            itemViewHolder.tvmargin.setVisibility(8);
        }
        itemViewHolder.appIconIv.setVisibility(0);
        itemViewHolder.appIconIv.setImageDrawable(oPGestureAppModel.getAppIcon());
        itemViewHolder.appNameTv.setText(oPGestureAppModel.getTitle());
        if (i == 0) {
            itemViewHolder.headermargin.setVisibility(0);
        } else {
            itemViewHolder.headermargin.setVisibility(8);
        }
        if (this.mGestureSummary.equals(oPGestureAppModel.getTitle())) {
            itemViewHolder.radioButton.setChecked(true);
        } else {
            itemViewHolder.radioButton.setChecked(false);
        }
        return view;
    }

    class ItemViewHolder {
        ImageView appIconIv;
        TextView appNameTv;
        View headermargin;
        RadioButton radioButton;
        TextView titleTv;
        View tvmargin;

        ItemViewHolder(OPGestureShortcutsAdapter oPGestureShortcutsAdapter) {
        }
    }
}
