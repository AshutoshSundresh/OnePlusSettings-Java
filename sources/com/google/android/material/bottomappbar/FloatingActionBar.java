package com.google.android.material.bottomappbar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.R$id;
import com.google.android.material.R$layout;
import com.google.android.material.R$styleable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.ThemeEnforcement;

public class FloatingActionBar extends FrameLayout implements View.OnClickListener {
    private FloatingActionButton mFloatingActionButton;
    private boolean mScrollHide;
    private Toolbar mToolbar;

    public FloatingActionBar(Context context) {
        super(context);
        this.mScrollHide = false;
    }

    public FloatingActionBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FloatingActionBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mScrollHide = false;
        init(context);
        initArray(context, attributeSet, i);
    }

    public FloatingActionBar(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mScrollHide = false;
        init(context);
        initArray(context, attributeSet, i);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R$layout.op_floating_action_bar, this);
        this.mToolbar = (Toolbar) findViewById(R$id.tool_bar);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R$id.floating_action_tool_button);
        this.mFloatingActionButton = floatingActionButton;
        floatingActionButton.setOnClickListener(this);
    }

    private void initArray(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = ThemeEnforcement.obtainStyledAttributes(context, attributeSet, R$styleable.FloatingActionBar, i, 0, new int[0]);
        setScrollHide(obtainStyledAttributes.getBoolean(R$styleable.FloatingActionBar_actionbarScrollHide, false));
        obtainStyledAttributes.recycle();
    }

    public Toolbar getToolBar() {
        return this.mToolbar;
    }

    public void setScrollHide(boolean z) {
        this.mScrollHide = z;
        FloatingActionButton floatingActionButton = this.mFloatingActionButton;
        if (floatingActionButton != null) {
            floatingActionButton.setScrollHide(z);
        }
    }

    public boolean getScrollHideBoolean() {
        return this.mScrollHide;
    }

    public void onClick(View view) {
        Menu menu = this.mToolbar.getMenu();
        Context context = getContext();
        if (context != null && (context instanceof Activity)) {
            ((Activity) context).onPrepareOptionsMenu(menu);
        }
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            menu.getItem(i).setShowAsAction(0);
        }
        this.mToolbar.showOverflowMenu();
    }

    public static class FloatingToolBar extends BottomActionbar {
        public FloatingToolBar(Context context) {
            super(context);
        }

        public FloatingToolBar(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public FloatingToolBar(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
        }
    }
}
