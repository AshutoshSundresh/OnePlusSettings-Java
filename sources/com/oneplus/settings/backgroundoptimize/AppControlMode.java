package com.oneplus.settings.backgroundoptimize;

import android.os.Parcel;
import android.os.Parcelable;

public class AppControlMode implements Parcelable {
    public static final Parcelable.Creator<AppControlMode> CREATOR = new Parcelable.Creator<AppControlMode>() {
        /* class com.oneplus.settings.backgroundoptimize.AppControlMode.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public AppControlMode createFromParcel(Parcel parcel) {
            return new AppControlMode(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public AppControlMode[] newArray(int i) {
            return new AppControlMode[i];
        }
    };
    public int mode;
    public String packageName;
    public int value;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.packageName);
        parcel.writeInt(this.mode);
        parcel.writeInt(this.value);
    }

    public void readFromParcel(Parcel parcel) {
        this.packageName = parcel.readString();
        this.mode = parcel.readInt();
        this.value = parcel.readInt();
    }

    public AppControlMode(String str, int i, int i2) {
        this.packageName = str;
        this.mode = i;
        this.value = i2;
    }

    private AppControlMode(Parcel parcel) {
        readFromParcel(parcel);
    }

    public String toString(String str) {
        return str + " packageName=" + this.packageName + " mode=" + this.mode + " value=" + this.value;
    }

    public String toString() {
        return toString("");
    }
}
