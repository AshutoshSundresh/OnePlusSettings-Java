package com.android.settings.notification;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;

public class NotificationButtonRelativeLayout extends RelativeLayout {
    public NotificationButtonRelativeLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CharSequence getAccessibilityClassName() {
        return Button.class.getName();
    }
}
