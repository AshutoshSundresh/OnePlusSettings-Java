package com.oneplus.settings.aboutphone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import java.util.List;

public class SoftwareInfoAdapter extends RecyclerView.Adapter<ViewHolder> {
    private Context mContext;
    private List<SoftwareInfoEntity> mList;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        return (long) i;
    }

    public SoftwareInfoAdapter(Context context, List<SoftwareInfoEntity> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(this, LayoutInflater.from(this.mContext).inflate(C0012R$layout.op_aboute_phone_software_item, (ViewGroup) null));
    }

    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        if (i >= 0 && i < this.mList.size()) {
            SoftwareInfoEntity softwareInfoEntity = this.mList.get(i);
            viewHolder.tvTitle.setText(softwareInfoEntity.getTitle());
            viewHolder.tvSummary.setText(softwareInfoEntity.getSummary());
            viewHolder.imageView.setImageDrawable(this.mContext.getDrawable(softwareInfoEntity.getResIcon()));
        }
        if (this.mOnItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                /* class com.oneplus.settings.aboutphone.SoftwareInfoAdapter.AnonymousClass1 */

                public void onClick(View view) {
                    SoftwareInfoAdapter.this.mOnItemClickListener.onItemClick(viewHolder.itemView, i);
                }
            });
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mList.size();
    }

    /* access modifiers changed from: package-private */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvSummary;
        TextView tvTitle;

        public ViewHolder(SoftwareInfoAdapter softwareInfoAdapter, View view) {
            super(view);
            this.tvTitle = (TextView) view.findViewById(C0010R$id.tv_title_software_info);
            this.tvSummary = (TextView) view.findViewById(C0010R$id.tv_summary_software_info);
            this.imageView = (ImageView) view.findViewById(C0010R$id.img_software_info);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
