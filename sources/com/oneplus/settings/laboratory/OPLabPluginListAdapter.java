package com.oneplus.settings.laboratory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import java.util.List;

public class OPLabPluginListAdapter extends BaseAdapter {
    private LayoutInflater mInflate;
    private List<OPLabPluginModel> mPluginData;

    public long getItemId(int i) {
        return (long) i;
    }

    public OPLabPluginListAdapter(Context context, List<OPLabPluginModel> list) {
        this.mPluginData = list;
        this.mInflate = LayoutInflater.from(context);
    }

    public void setData(List<OPLabPluginModel> list) {
        this.mPluginData = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mPluginData.size();
    }

    public OPLabPluginModel getItem(int i) {
        return this.mPluginData.get(i);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view2 = this.mInflate.inflate(C0012R$layout.op_lab_feature_plugin_item, (ViewGroup) null);
            viewHolder.featureImage = (ImageView) view2.findViewById(C0010R$id.feature_imageview);
            viewHolder.featureTitle = (TextView) view2.findViewById(C0010R$id.feature_title);
            view2.setTag(viewHolder);
        } else {
            view2 = view;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.featureTitle.setText(this.mPluginData.get(i).getFeatureTitle());
        viewHolder.featureImage.setImageResource(this.mPluginData.get(i).geFeatureIconId());
        return view2;
    }

    private class ViewHolder {
        ImageView featureImage;
        TextView featureTitle;

        private ViewHolder(OPLabPluginListAdapter oPLabPluginListAdapter) {
        }
    }
}
