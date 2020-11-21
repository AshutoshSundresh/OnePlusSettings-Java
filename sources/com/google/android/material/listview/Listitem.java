package com.google.android.material.listview;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class Listitem extends ViewGroup {
    public abstract ImageView getActionButton();

    public abstract ImageView getIcon();

    public abstract TextView getPrimaryText();

    public abstract TextView getSecondaryText();

    public abstract TextView getStamp();

    public Listitem(Context context) {
        super(context);
    }
}
