package com.google.android.setupcompat.internal;

import android.app.Activity;
import android.os.Bundle;

public final class LayoutBindBackHelper {
    public static final String getScreenName(Activity activity) {
        return activity.getComponentName().toString();
    }

    public static final Bundle getExtraBundle(Activity activity) {
        Bundle bundle = new Bundle();
        bundle.putString("screenName", getScreenName(activity));
        bundle.putString("intentAction", activity.getIntent().getAction());
        return bundle;
    }
}
