package com.oneplus.settings.highpowerapp;

import android.os.Parcel;
import android.os.Parcelable;

public class HighPowerApp implements Parcelable {
    public static final Parcelable.Creator<HighPowerApp> CREATOR = new Parcelable.Creator<HighPowerApp>() {
        /* class com.oneplus.settings.highpowerapp.HighPowerApp.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public HighPowerApp createFromParcel(Parcel parcel) {
            return new HighPowerApp(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public HighPowerApp[] newArray(int i) {
            return new HighPowerApp[i];
        }
    };
    public boolean isLocked;
    public boolean isStopped;
    public String pkgName;
    public int powerLevel;
    public long timeStamp;
    public int uid;

    public int describeContents() {
        return 0;
    }

    public HighPowerApp(Parcel parcel) {
        readFromParcel(parcel);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.pkgName);
        parcel.writeInt(this.powerLevel);
        parcel.writeInt(this.isLocked ? 1 : 0);
        parcel.writeInt(this.isStopped ? 1 : 0);
        parcel.writeLong(this.timeStamp);
        parcel.writeInt(this.uid);
    }

    public void readFromParcel(Parcel parcel) {
        this.pkgName = parcel.readString();
        this.powerLevel = parcel.readInt();
        boolean z = false;
        this.isLocked = parcel.readInt() == 1;
        if (parcel.readInt() == 1) {
            z = true;
        }
        this.isStopped = z;
        this.timeStamp = parcel.readLong();
        this.uid = parcel.readInt();
    }
}
