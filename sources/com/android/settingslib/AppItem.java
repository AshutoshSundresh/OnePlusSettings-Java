package com.android.settingslib;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

public class AppItem implements Comparable<AppItem>, Parcelable {
    public static final Parcelable.Creator<AppItem> CREATOR = new Parcelable.Creator<AppItem>() {
        /* class com.android.settingslib.AppItem.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public AppItem createFromParcel(Parcel parcel) {
            return new AppItem(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public AppItem[] newArray(int i) {
            return new AppItem[i];
        }
    };
    public int category;
    public final int key;
    public boolean restricted;
    public long total;
    public SparseBooleanArray uids;

    public int describeContents() {
        return 0;
    }

    public AppItem() {
        this.uids = new SparseBooleanArray();
        this.key = 0;
    }

    public AppItem(int i) {
        this.uids = new SparseBooleanArray();
        this.key = i;
    }

    public AppItem(Parcel parcel) {
        this.uids = new SparseBooleanArray();
        this.key = parcel.readInt();
        this.uids = parcel.readSparseBooleanArray();
        this.total = parcel.readLong();
    }

    public void addUid(int i) {
        this.uids.put(i, true);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.key);
        parcel.writeSparseBooleanArray(this.uids);
        parcel.writeLong(this.total);
    }

    public int compareTo(AppItem appItem) {
        int compare = Integer.compare(this.category, appItem.category);
        return compare == 0 ? Long.compare(appItem.total, this.total) : compare;
    }
}
