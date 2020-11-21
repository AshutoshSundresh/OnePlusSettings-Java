package com.android.settings.applications.manageapplications;

import com.android.settingslib.applications.ApplicationsState;
import java.util.Objects;

public class AppFilterItem implements Comparable<AppFilterItem> {
    private final ApplicationsState.AppFilter mFilter;
    private final int mFilterType;
    private final int mTitle;

    public AppFilterItem(ApplicationsState.AppFilter appFilter, int i, int i2) {
        this.mTitle = i2;
        this.mFilterType = i;
        this.mFilter = appFilter;
    }

    public int getTitle() {
        return this.mTitle;
    }

    public ApplicationsState.AppFilter getFilter() {
        return this.mFilter;
    }

    public int getFilterType() {
        return this.mFilterType;
    }

    public int compareTo(AppFilterItem appFilterItem) {
        if (appFilterItem == null) {
            return 1;
        }
        if (this == appFilterItem) {
            return 0;
        }
        return this.mFilterType - appFilterItem.mFilterType;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AppFilterItem)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        AppFilterItem appFilterItem = (AppFilterItem) obj;
        return this.mTitle == appFilterItem.mTitle && this.mFilterType == appFilterItem.mFilterType && this.mFilter == appFilterItem.mFilter;
    }

    public int hashCode() {
        return Objects.hash(this.mFilter, Integer.valueOf(this.mTitle), Integer.valueOf(this.mFilterType));
    }
}
