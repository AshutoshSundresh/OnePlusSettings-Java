package com.google.android.setupdesign.items;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.setupdesign.R$id;
import com.google.android.setupdesign.R$layout;
import com.google.android.setupdesign.R$styleable;

public class SwitchItem extends Item implements CompoundButton.OnCheckedChangeListener {
    private boolean checked = false;
    private OnCheckedChangeListener listener;

    public interface OnCheckedChangeListener {
        void onCheckedChange(SwitchItem switchItem, boolean z);
    }

    public SwitchItem() {
    }

    public SwitchItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.SudSwitchItem);
        this.checked = obtainStyledAttributes.getBoolean(R$styleable.SudSwitchItem_android_checked, false);
        obtainStyledAttributes.recycle();
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.items.Item
    public int getDefaultLayoutResource() {
        return R$layout.sud_items_switch;
    }

    @Override // com.google.android.setupdesign.items.Item, com.google.android.setupdesign.items.IItem
    public void onBindView(View view) {
        super.onBindView(view);
        SwitchCompat switchCompat = (SwitchCompat) view.findViewById(R$id.sud_items_switch);
        switchCompat.setOnCheckedChangeListener(null);
        switchCompat.setChecked(this.checked);
        switchCompat.setOnCheckedChangeListener(this);
        switchCompat.setEnabled(isEnabled());
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        this.checked = z;
        OnCheckedChangeListener onCheckedChangeListener = this.listener;
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChange(this, z);
        }
    }
}
