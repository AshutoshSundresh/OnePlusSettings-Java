package androidx.appcompat.widget;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.DataSetObservable;

class ActivityChooserModel extends DataSetObservable {
    public abstract Intent chooseActivity(int i);

    public abstract ResolveInfo getActivity(int i);

    public abstract int getActivityCount();

    public abstract int getActivityIndex(ResolveInfo resolveInfo);

    public abstract ResolveInfo getDefaultActivity();

    public abstract int getHistorySize();

    public abstract void setDefaultActivity(int i);
}
