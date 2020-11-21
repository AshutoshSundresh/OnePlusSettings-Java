package com.android.settings.fuelgauge.batterytip;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.ArraySet;
import java.util.Objects;

public class AppInfo implements Comparable<AppInfo>, Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        /* class com.android.settings.fuelgauge.batterytip.AppInfo.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public AppInfo createFromParcel(Parcel parcel) {
            return new AppInfo(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public AppInfo[] newArray(int i) {
            return new AppInfo[i];
        }
    };
    public final ArraySet<Integer> anomalyTypes;
    public final String packageName;
    public final long screenOnTimeMs;
    public final int uid;

    public int describeContents() {
        return 0;
    }

    private AppInfo(Builder builder) {
        this.packageName = builder.mPackageName;
        this.anomalyTypes = builder.mAnomalyTypes;
        this.screenOnTimeMs = builder.mScreenOnTimeMs;
        this.uid = builder.mUid;
    }

    AppInfo(Parcel parcel) {
        this.packageName = parcel.readString();
        this.anomalyTypes = parcel.readArraySet(null);
        this.screenOnTimeMs = parcel.readLong();
        this.uid = parcel.readInt();
    }

    public int compareTo(AppInfo appInfo) {
        return Long.compare(this.screenOnTimeMs, appInfo.screenOnTimeMs);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.packageName);
        parcel.writeArraySet(this.anomalyTypes);
        parcel.writeLong(this.screenOnTimeMs);
        parcel.writeInt(this.uid);
    }

    public String toString() {
        return "packageName=" + this.packageName + ",anomalyTypes=" + this.anomalyTypes + ",screenTime=" + this.screenOnTimeMs;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AppInfo)) {
            return false;
        }
        AppInfo appInfo = (AppInfo) obj;
        return Objects.equals(this.anomalyTypes, appInfo.anomalyTypes) && this.uid == appInfo.uid && this.screenOnTimeMs == appInfo.screenOnTimeMs && TextUtils.equals(this.packageName, appInfo.packageName);
    }

    public static final class Builder {
        private ArraySet<Integer> mAnomalyTypes = new ArraySet<>();
        private String mPackageName;
        private long mScreenOnTimeMs;
        private int mUid;

        public Builder setPackageName(String str) {
            this.mPackageName = str;
            return this;
        }

        public Builder setScreenOnTimeMs(long j) {
            this.mScreenOnTimeMs = j;
            return this;
        }

        public Builder setUid(int i) {
            this.mUid = i;
            return this;
        }

        public AppInfo build() {
            return new AppInfo(this);
        }
    }
}
