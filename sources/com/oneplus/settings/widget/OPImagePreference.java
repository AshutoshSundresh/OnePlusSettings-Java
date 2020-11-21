package com.oneplus.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class OPImagePreference extends Preference {
    public OPImagePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context);
    }

    public OPImagePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    private void initView(Context context) {
        setLayoutResource(C0012R$layout.op_image_preferece);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ImageView imageView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.imageview);
        Log.d("OPImagePreference", "onBindViewHolder imageView = " + imageView);
        if (imageView != null) {
            imageView.setImageResource(C0008R$drawable.op_screen_show_1);
        }
    }
}
