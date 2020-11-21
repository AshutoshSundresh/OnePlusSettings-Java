package com.android.settings.display;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.IconDrawableFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import com.android.settings.C0012R$layout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppGridView extends GridView {
    public AppGridView(Context context) {
        super(context);
        init(context);
    }

    public AppGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public AppGridView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    public AppGridView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(context);
    }

    private void init(Context context) {
        setAdapter((ListAdapter) new AppsAdapter(context, C0012R$layout.screen_zoom_preview_app_icon, 16908308, 16908295));
    }

    public static class AppsAdapter extends ArrayAdapter<ActivityEntry> {
        private final int mIconResId;
        private final PackageManager mPackageManager;

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return true;
        }

        public boolean isEnabled(int i) {
            return false;
        }

        public AppsAdapter(Context context, int i, int i2, int i3) {
            super(context, i, i2);
            this.mIconResId = i3;
            this.mPackageManager = context.getPackageManager();
            loadAllApps();
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            ((ImageView) view2.findViewById(this.mIconResId)).setImageDrawable(((ActivityEntry) getItem(i)).getIcon());
            return view2;
        }

        private void loadAllApps() {
            Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
            intent.addCategory("android.intent.category.LAUNCHER");
            PackageManager packageManager = this.mPackageManager;
            ArrayList arrayList = new ArrayList();
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
            IconDrawableFactory newInstance = IconDrawableFactory.newInstance(getContext());
            for (ResolveInfo resolveInfo : queryIntentActivities) {
                CharSequence loadLabel = resolveInfo.loadLabel(packageManager);
                if (loadLabel != null) {
                    arrayList.add(new ActivityEntry(resolveInfo, loadLabel.toString(), newInstance));
                }
                if (arrayList.size() >= 6) {
                    break;
                }
            }
            Collections.sort(arrayList);
            addAll(arrayList);
        }
    }

    public static class ActivityEntry implements Comparable<ActivityEntry> {
        public final ResolveInfo info;
        public final String label;
        private final IconDrawableFactory mIconFactory;
        private final int mUserId = UserHandle.myUserId();

        public ActivityEntry(ResolveInfo resolveInfo, String str, IconDrawableFactory iconDrawableFactory) {
            this.info = resolveInfo;
            this.label = str;
            this.mIconFactory = iconDrawableFactory;
        }

        public int compareTo(ActivityEntry activityEntry) {
            return this.label.compareToIgnoreCase(activityEntry.label);
        }

        public String toString() {
            return this.label;
        }

        public Drawable getIcon() {
            IconDrawableFactory iconDrawableFactory = this.mIconFactory;
            ActivityInfo activityInfo = this.info.activityInfo;
            return iconDrawableFactory.getBadgedIcon(activityInfo, activityInfo.applicationInfo, this.mUserId);
        }
    }
}
