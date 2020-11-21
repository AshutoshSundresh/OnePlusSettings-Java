package com.oneplus.settings.ui;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.C0010R$id;

public class OPCustomItemEntityViewHolder extends RecyclerView.ViewHolder {
    public RadiusImageView imageView;
    public RadiusImageView imageViewMask;
    public TextView textView;

    public OPCustomItemEntityViewHolder(View view) {
        super(view);
        this.imageView = (RadiusImageView) view.findViewById(C0010R$id.choose_image);
        this.imageViewMask = (RadiusImageView) view.findViewById(C0010R$id.choose_mask);
        this.textView = (TextView) view.findViewById(C0010R$id.choose_name);
    }
}
