package com.android.settings.fuelgauge.batterytip;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.IconDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.Utils;
import java.util.List;

public class HighUsageAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final Context mContext;
    private final List<AppInfo> mHighUsageAppList;
    private final IconDrawableFactory mIconDrawableFactory;
    private final PackageManager mPackageManager;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView appIcon;
        public TextView appName;

        public ViewHolder(View view) {
            super(view);
            this.appIcon = (ImageView) view.findViewById(C0010R$id.app_icon);
            this.appName = (TextView) view.findViewById(C0010R$id.app_name);
            TextView textView = (TextView) view.findViewById(C0010R$id.app_screen_time);
        }
    }

    public HighUsageAdapter(Context context, List<AppInfo> list) {
        this.mContext = context;
        this.mHighUsageAppList = list;
        this.mIconDrawableFactory = IconDrawableFactory.newInstance(context);
        this.mPackageManager = context.getPackageManager();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(C0012R$layout.app_high_usage_item, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        AppInfo appInfo = this.mHighUsageAppList.get(i);
        viewHolder.appIcon.setImageDrawable(Utils.getBadgedIcon(this.mIconDrawableFactory, this.mPackageManager, appInfo.packageName, UserHandle.getUserId(appInfo.uid)));
        CharSequence applicationLabel = Utils.getApplicationLabel(this.mContext, appInfo.packageName);
        if (applicationLabel == null) {
            applicationLabel = appInfo.packageName;
        }
        viewHolder.appName.setText(applicationLabel);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mHighUsageAppList.size();
    }
}
